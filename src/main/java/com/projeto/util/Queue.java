package com.projeto.util;

public class Queue<T> {
    private static class Node<T> {
        private T data;
        private Node<T> next;

        public Node(T data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node<T> front; // Início da fila
    private Node<T> rear;  // Fim da fila
    private int size;      // Tamanho da fila

    public Queue() {
        this.front = null;
        this.rear = null;
        this.size = 0;
    }

    // Adicionar elemento na fila
    public void enqueue(T data) {
        Node<T> newNode = new Node<>(data);
        if (rear != null) {
            rear.next = newNode;
        }
        rear = newNode;
        if (front == null) {
            front = rear;
        }
        size++;
    }

    // Remover elemento da fila
    public T dequeue() {
        if (front == null) {
            throw new IllegalStateException("Fila está vazia");
        }
        T data = front.data;
        front = front.next;
        if (front == null) {
            rear = null;
        }
        size--;
        return data;
    }

    // Ver o elemento da frente da fila
    public T peek() {
        if (front == null) {
            throw new IllegalStateException("Fila está vazia");
        }
        return front.data;
    }

    // Verificar se a fila está vazia
    public boolean isEmpty() {
        return size == 0;
    }

    // Obter o tamanho da fila
    public int size() {
        return size;
    }
}

