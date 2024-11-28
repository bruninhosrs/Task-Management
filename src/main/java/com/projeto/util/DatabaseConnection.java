package com.projeto.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/task_management"; 
    private static final String USER = "root"; 
    private static final String PASSWORD = "admin"; 

    // Método para obter uma conexão com o banco de dados
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Carregar o driver MySQL
        } catch (ClassNotFoundException e) {
            System.err.println("Driver MySQL não encontrado: " + e.getMessage());
            throw new SQLException(e);
        }

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

