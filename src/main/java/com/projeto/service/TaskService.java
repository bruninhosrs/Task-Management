package com.projeto.service;

import com.projeto.util.DatabaseConnection;
import com.projeto.model.Task;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskService {

    
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
}
