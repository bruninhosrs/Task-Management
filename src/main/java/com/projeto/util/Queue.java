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

    private Node<T> front; 
    private Node<T> rear;  
    private int size;      

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


    public T peek() {
        if (front == null) {
            throw new IllegalStateException("Fila está vazia");
        }
        return front.data;
    }

    
    public boolean isEmpty() {
        return size == 0;
    }


    public int size() {
        return size;
    }
}

