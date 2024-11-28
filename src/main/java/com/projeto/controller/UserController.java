package com.projeto.controller;

import com.projeto.model.User;
import com.projeto.service.UserService;

import java.util.List;

public class UserController {
    private UserService userService;

    public UserController() {
        this.userService = new UserService();
    }

    // Adicionar um novo usuário
    public void createUser(int id, String username, String password) {
        User newUser = new User(id, username, password);
        userService.addUser(newUser);
        System.out.println("Usuário criado com sucesso!");
    }

    // Listar todos os usuários
    public void listUsers() {
        System.out.println("Usuários cadastrados:");
        List<User> users = userService.getAllUsers();
        for (User user : users) {
            System.out.println(user);
        }
    }

    // Buscar usuário pelo ID
    public void getUserById(int id) {
        User user = userService.getUserById(id);
        if (user != null) {
            System.out.println("Usuário encontrado: " + user);
        } else {
            System.out.println("Usuário com ID " + id + " não encontrado.");
        }
    }

    // Remover usuário pelo ID
    public void deleteUserById(int id) {
        boolean removed = userService.removeUserById(id);
        if (removed) {
            System.out.println("Usuário com ID " + id + " removido com sucesso!");
        } else {
            System.out.println("Usuário com ID " + id + " não encontrado.");
        }
    }
}
