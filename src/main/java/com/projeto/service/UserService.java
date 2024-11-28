package com.projeto.service;

import com.projeto.util.DatabaseConnection;
import com.projeto.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService {

    // Adicionar um novo usuário
    public void addUser(User user) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.executeUpdate();
            System.out.println("Usuário adicionado com sucesso: " + user);

        } catch (SQLException e) {
            System.err.println("Erro ao adicionar usuário: " + e.getMessage());
        }
    }

    // Método para autenticar um usuário pelo username e password
    public User authenticate(String username, String password) {
    String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, username);
        stmt.setString(2, password);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return new User(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("password")
            );
        }

    } catch (SQLException e) {
        System.err.println("Erro ao autenticar usuário: " + e.getMessage());
    }

    return null; // Retorna null se a autenticação falhar
    }
    
    // Obter todos os usuários
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar usuários: " + e.getMessage());
        }

        return users;
    }

    // Obter um usuário pelo ID
    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password")
                );
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar usuário: " + e.getMessage());
        }

        return null;
    }

    // Remover um usuário pelo ID
    public boolean removeUserById(int id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao remover usuário: " + e.getMessage());
        }

        return false;
    }
}
