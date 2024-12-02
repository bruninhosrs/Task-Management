package com.projeto;

import com.projeto.service.UserService;
import com.projeto.model.Task;
import com.projeto.service.TaskService;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.LocalDate;
import java.util.List;

public class Application {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/login", new LoginHandler());
        server.createContext("/tasks", new TaskHandler());
        server.createContext("/reports", new ReportHandler());

        server.setExecutor(null);
        server.start();
        System.out.println("Servidor rodando na porta 8080");
    }

    static class LoginHandler implements HttpHandler {
        private final UserService userService = new UserService();

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                handleCors(exchange);
                return;
            }

            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String body = new String(exchange.getRequestBody().readAllBytes());
                String[] params = body.split("&");
                String username = params[0].split("=")[1];
                String password = params[1].split("=")[1];

                boolean isAuthenticated = userService.authenticate(username, password) != null;
                String response = isAuthenticated ? "{\"message\":\"Login realizado com sucesso!\"}"
                        : "{\"message\":\"Credenciais inválidas!\"}";
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(isAuthenticated ? 200 : 401, response.getBytes().length);

                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }
    }

    static class TaskHandler implements HttpHandler {
        private final TaskService taskService = new TaskService();

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                handleCors(exchange);
                return;
            }

            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String body = new String(exchange.getRequestBody().readAllBytes());
                String[] params = body.split("&");
                String title = params[0].split("=")[1];
                String description = params[1].split("=")[1];
                String dueDateParam = params[2].split("=")[1];
                String priority = params[3].split("=")[1];

                LocalDate dueDate = LocalDate.parse(dueDateParam);

                taskService.addTask(new Task(0, title, description, dueDate, priority));

                String response = "{\"message\":\"Tarefa adicionada com sucesso!\"}";
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(201, response.getBytes().length);

                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                String response = taskService.getAllTasksAsJson();

                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);

                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                exchange.sendResponseHeaders(405, -1);
            }

            if ("PUT".equalsIgnoreCase(exchange.getRequestMethod())) {
                String query = exchange.getRequestURI().getQuery();
                int taskId = Integer.parseInt(query.split("=")[1]);

                String body = new String(exchange.getRequestBody().readAllBytes());
                String[] params = body.split("&");
                String title = params[0].split("=")[1];
                String description = params[1].split("=")[1];
                String dueDate = params[2].split("=")[1];
                String priority = params[3].split("=")[1];

                boolean updated = taskService
                        .updateTask(new Task(taskId, title, description, LocalDate.parse(dueDate), priority));
                String response = updated ? "{\"message\":\"Tarefa atualizada com sucesso!\"}"
                        : "{\"message\":\"Erro ao atualizar tarefa.\"}";

                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(updated ? 200 : 400, response.getBytes().length);

                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
            if ("DELETE".equalsIgnoreCase(exchange.getRequestMethod())) {
                String query = exchange.getRequestURI().getQuery();
                int taskId;
            
                try {
                    taskId = Integer.parseInt(query.split("=")[1]);
                } catch (Exception e) {
                    exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                    exchange.sendResponseHeaders(400, -1); // Bad Request
                    return;
                }
            
                boolean deleted = taskService.removeTaskById(taskId);
                String response = deleted ? "{\"message\":\"Tarefa excluída com sucesso!\"}" : "{\"message\":\"Erro ao excluir tarefa.\"}";
            
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(deleted ? 200 : 400, response.getBytes().length);
            
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }                   
        }
    }

    static class ReportHandler implements HttpHandler {
        private final TaskService taskService = new TaskService();

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                handleCors(exchange);
                return;
            }

            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                String query = exchange.getRequestURI().getQuery();
                String type = query != null && query.contains("type") ? query.split("=")[1] : "general";

                String response;
                switch (type) {
                    case "overdue":
                        response = taskService.getOverdueTasksAsJson();
                        break;
                    case "completed":
                        response = taskService.getCompletedTasksAsJson();
                        break;
                    case "by-priority":
                        response = taskService.getTasksByPriorityReportAsJson();
                        break;
                    case "by-period":
                        response = taskService.getTasksByPeriodReportAsJson("month");
                        break;
                    default:
                        response = taskService.getGeneralStatsAsJson();
                }

                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);

                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }
    }

    private static void handleCors(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        exchange.sendResponseHeaders(204, -1); // No Content
    }
}
