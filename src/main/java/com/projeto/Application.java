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

        // Define rotas
        server.createContext("/login", new LoginHandler());
        server.createContext("/tasks", new TaskHandler());
        server.createContext("/reports", new ReportHandler());

        // Inicia o servidor
        server.setExecutor(null);
        server.start();
        System.out.println("Servidor rodando na porta 8080");
    }

    // Handler para Login
    static class LoginHandler implements HttpHandler {
        private final UserService userService = new UserService();

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                handleCors(exchange);
                return;
            }

            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                String[] credentials = new String(exchange.getRequestBody().readAllBytes()).split("&");
                String username = credentials[0].split("=")[1];
                String password = credentials[1].split("=")[1];

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

    // Handler para Tasks
    static class TaskHandler implements HttpHandler {
        private final TaskService taskService = new TaskService();

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                handleCors(exchange);
                return;
            }

            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                String body = new String(exchange.getRequestBody().readAllBytes());
                String[] params = body.split("&");
                String title = params[0].split("=")[1];
                String description = params[1].split("=")[1];
                String dueDate = params[2].split("=")[1];
                String priority = params[3].split("=")[1];

                taskService.addTask(new Task(0, title, description, LocalDate.parse(dueDate), priority));

                String response = "{\"message\":\"Tarefa adicionada com sucesso!\"}";
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(201, response.getBytes().length);

                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                List<Task> tasks = taskService.getAllTasks();
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < tasks.size(); i++) {
                    Task task = tasks.get(i);
                    json.append("{")
                            .append("\"id\":").append(task.getId()).append(",")
                            .append("\"title\":\"").append(task.getTitle()).append("\",")
                            .append("\"description\":\"").append(task.getDescription()).append("\",")
                            .append("\"dueDate\":\"").append(task.getDueDate()).append("\",")
                            .append("\"priority\":\"").append(task.getPriority()).append("\"")
                            .append("}");
                    if (i < tasks.size() - 1) {
                        json.append(",");
                    }
                }
                json.append("]");

                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, json.toString().getBytes().length);

                OutputStream os = exchange.getResponseBody();
                os.write(json.toString().getBytes());
                os.close();
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }
    }

    // Handler para Relatórios
    static class ReportHandler implements HttpHandler {
        private final TaskService taskService = new TaskService();

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                handleCors(exchange);
                return;
            }

            if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                String query = exchange.getRequestURI().getQuery();
                String type = query != null && query.contains("type") ? query.split("=")[1] : "general";

                String response = switch (type) {
                    case "overdue" -> taskService.getOverdueTasks().toString();
                    case "completed" -> taskService.getCompletedTasksAsJson().toString();
                    case "by-priority" -> taskService.getTasksByPriorityReportAsJson();
                    case "by-period" -> taskService.getTasksByPeriodReportAsJson("month");
                    default -> taskService.getGeneralStatsAsJson();
                };

                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);

                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }
    }

    // Método para adicionar cabeçalhos CORS em respostas OPTIONS (Preflight)
    private static void handleCors(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        exchange.sendResponseHeaders(204, -1);
    }
}
