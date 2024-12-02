package com.projeto.service;

import com.projeto.util.DatabaseConnection;
import com.projeto.model.Task;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskService {

    // Adicionar nova tarefa
    public void addTask(Task task) {
        String sql = "INSERT INTO tasks (title, description, due_date, priority) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, task.getTitle());
            stmt.setString(2, task.getDescription());
            stmt.setDate(3, Date.valueOf(task.getDueDate()));
            stmt.setString(4, task.getPriority());
            stmt.executeUpdate();

            System.out.println("Tarefa adicionada com sucesso: " + task);

        } catch (SQLException e) {
            System.err.println("Erro ao adicionar tarefa: " + e.getMessage());
        }
    }

    // Obter todas as tarefas
    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks ORDER BY due_date";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                tasks.add(new Task(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getDate("due_date").toLocalDate(),
                        rs.getString("priority")));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar tarefas: " + e.getMessage());
        }

        return tasks;
    }

    public boolean updateTask(Task task) {
        String sql = "UPDATE tasks SET title = ?, description = ?, due_date = ?, priority = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, task.getTitle()); 
            stmt.setString(2, task.getDescription());
            stmt.setDate(3, java.sql.Date.valueOf(task.getDueDate())); 
            stmt.setString(4, task.getPriority());
            stmt.setInt(5, task.getId());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Tarefa atualizada com sucesso: " + task);
                return true;
            } else {
                System.err.println("Nenhuma tarefa encontrada com o ID: " + task.getId());
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar tarefa: " + e.getMessage());
            return false;
        }
    }

    // Obter tarefa por ID
    public Task getTaskById(int id) {
        String sql = "SELECT * FROM tasks WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Task(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getDate("due_date").toLocalDate(),
                        rs.getString("priority"));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar tarefa: " + e.getMessage());
        }

        return null;
    }

    // Obter tarefas atrasadas como JSON
    public String getOverdueTasksAsJson() {
        String sql = "SELECT * FROM tasks WHERE due_date < CURDATE()";
        List<Task> overdueTasks = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                overdueTasks.add(new Task(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getDate("due_date").toLocalDate(),
                        rs.getString("priority")));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar tarefas atrasadas: " + e.getMessage());
        }

        return convertTasksToJson(overdueTasks);
    }

    // Obter tarefas atrasadas
    public List<Task> getOverdueTasks() {
        List<Task> overdueTasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE due_date < CURDATE()";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                overdueTasks.add(new Task(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getDate("due_date").toLocalDate(),
                        rs.getString("priority")));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar tarefas atrasadas: " + e.getMessage());
        }

        return overdueTasks;
    }

    // Obter tarefas por prioridade
    public List<Task> getTasksByPriority(String priority) {
        List<Task> tasksByPriority = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE priority = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, priority);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                tasksByPriority.add(new Task(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getDate("due_date").toLocalDate(),
                        rs.getString("priority")));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar tarefas por prioridade: " + e.getMessage());
        }

        return tasksByPriority;
    }

    // Contar todas as tarefas
    public int getTotalTasks() {
        String sql = "SELECT COUNT(*) AS total FROM tasks";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.err.println("Erro ao contar tarefas: " + e.getMessage());
        }

        return 0;
    }

    // Remover tarefa por ID
    public boolean removeTaskById(int id) {
        String sql = "DELETE FROM tasks WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Tarefa removida com sucesso!");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Erro ao remover tarefa: " + e.getMessage());
        }

        return false;
    }

    public String getAllTasksAsJson() {
        List<Task> tasks = getAllTasks(); 
        return convertTasksToJson(tasks); 
    }

    // Retorna tarefas concluídas como JSON
    public String getCompletedTasksAsJson() {
        String sql = "SELECT * FROM tasks WHERE status = 'completed'";
        List<Task> completedTasks = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                completedTasks.add(new Task(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getDate("due_date").toLocalDate(),
                        rs.getString("priority")));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar tarefas concluídas: " + e.getMessage());
        }

        return convertTasksToJson(completedTasks);
    }

    // Retorna relatório de tarefas por prioridade
    public String getTasksByPriorityReportAsJson() {
        String sql = "SELECT priority, COUNT(*) as total FROM tasks GROUP BY priority";
        StringBuilder json = new StringBuilder("[");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                json.append("{")
                        .append("\"priority\":\"").append(rs.getString("priority")).append("\",")
                        .append("\"total\":").append(rs.getInt("total"))
                        .append("},");
            }

            if (json.charAt(json.length() - 1) == ',') {
                json.deleteCharAt(json.length() - 1);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar tarefas por prioridade: " + e.getMessage());
        }

        json.append("]");
        return json.toString();
    }

    // Retorna relatório de tarefas do mês atual
    public String getTasksByPeriodReportAsJson(String period) {
        String sql = "SELECT * FROM tasks WHERE MONTH(due_date) = MONTH(CURRENT_DATE()) AND YEAR(due_date) = YEAR(CURRENT_DATE())";
        List<Task> tasksInPeriod = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                tasksInPeriod.add(new Task(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getDate("due_date").toLocalDate(),
                        rs.getString("priority")));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar tarefas por período: " + e.getMessage());
        }

        return convertTasksToJson(tasksInPeriod);
    }

    // Retorna estatísticas gerais
    public String getGeneralStatsAsJson() {
        String sql = "SELECT COUNT(*) as totalTasks, " +
                "SUM(CASE WHEN status = 'completed' THEN 1 ELSE 0 END) as completedTasks, " +
                "SUM(CASE WHEN due_date < CURDATE() THEN 1 ELSE 0 END) as overdueTasks " +
                "FROM tasks";
        StringBuilder json = new StringBuilder("{");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                json.append("\"totalTasks\":").append(rs.getInt("totalTasks")).append(",")
                        .append("\"completedTasks\":").append(rs.getInt("completedTasks")).append(",")
                        .append("\"overdueTasks\":").append(rs.getInt("overdueTasks"));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar estatísticas gerais: " + e.getMessage());
        }

        json.append("}");
        return json.toString();
    }

    // Converte lista de tarefas para JSON
    private String convertTasksToJson(List<Task> tasks) {
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
        return json.toString();
    }
}
