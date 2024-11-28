package com.projeto.controller;

import com.projeto.model.User;
import com.projeto.service.UserService;

import java.util.List;

public class UserController {
    private UserService userService;

    public UserController() {
        this.userService = new UserService();
    }

    
    public void createUser(int id, String username, String password) {
        User newUser = new User(id, username, password);
        userService.addUser(newUser);
        System.out.println("Usuário criado com sucesso!");
    }

    
    public void listUsers() {
        System.out.println("Usuários cadastrados:");
        List<User> users = userService.getAllUsers();
        for (User user : users) {
            System.out.println(user);
        }
    }

    
    public void getUserById(int id) {
        User user = userService.getUserById(id);
        if (user != null) {
            System.out.println("Usuário encontrado: " + user);
        } else {
            System.out.println("Usuário com ID " + id + " não encontrado.");
        }
    }

    
    public void deleteUserById(int id) {
        boolean removed = userService.removeUserById(id);
        if (removed) {
            System.out.println("Usuário com ID " + id + " removido com sucesso!");
        } else {
            System.out.println("Usuário com ID " + id + " não encontrado.");
        }
    }
}
