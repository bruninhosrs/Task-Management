package com.projeto.controller;

import com.projeto.model.Task;
import com.projeto.service.TaskService;
import java.time.LocalDate;
import java.util.List;

public class TaskController {
    private final TaskService taskService;

    public TaskController() {
        this.taskService = new TaskService();
    }

    
    public void addTask(String title, String description, LocalDate dueDate, String priority) {
        Task newTask = new Task(0, title, description, dueDate, priority);
        taskService.addTask(newTask);
        System.out.println("Tarefa criada com sucesso!");
    }

    
    public void listTasks() {
        List<Task> tasks = taskService.getAllTasks();

        if (tasks.isEmpty()) {
            System.out.println("Nenhuma tarefa encontrada.");
        } else {
            System.out.println("--- Tarefas Cadastradas ---");
            tasks.forEach(System.out::println);
        }
    }

    
    public void getTaskById(int id) {
        Task task = taskService.getTaskById(id);

        if (task != null) {
            System.out.println("Tarefa encontrada: " + task);
        } else {
            System.out.println("Tarefa com ID " + id + " não encontrada.");
        }
    }

    public void showOverdueTasks() {
        List<Task> overdueTasks = taskService.getOverdueTasks();

        if (overdueTasks.isEmpty()) {
            System.out.println("Nenhuma tarefa atrasada encontrada.");
        } else {
            System.out.println("--- Tarefas Atrasadas ---");
            overdueTasks.forEach(System.out::println);
        }
    }

    public void showTasksByPriority(String priority) {
        List<Task> tasksByPriority = taskService.getTasksByPriority(priority);

        if (tasksByPriority.isEmpty()) {
            System.out.println("Nenhuma tarefa encontrada com prioridade: " + priority);
        } else {
            System.out.println("--- Tarefas com Prioridade " + priority + " ---");
            tasksByPriority.forEach(System.out::println);
        }
    }

    public void showTotalTasks() {
        int totalTasks = taskService.getTotalTasks();
        System.out.println("Número total de tarefas no sistema: " + totalTasks);
    }

    
    public void removeTaskById(int id) {
        boolean removed = taskService.removeTaskById(id);

        if (removed) {
            System.out.println("Tarefa com ID " + id + " removida com sucesso!");
        } else {
            System.out.println("Tarefa com ID " + id + " não encontrada.");
        }
    }
}
