package com.projeto.util;

import java.util.function.Consumer;

public class AVLTree<T extends Comparable<T>> {
    private class Node {
        private T data;
        private Node left;
        private Node right;
        private int height;

        public Node(T data) {
            this.data = data;
            this.left = null;
            this.right = null;
            this.height = 1;
        }
    }

    private Node root;

    public AVLTree() {
        this.root = null;
    }

    // Método para fazer o percurso in-order e aplicar um Consumer
    public void inOrderTraversal(Consumer<T> action) {
        inOrderTraversal(root, action);
    }

    private void inOrderTraversal(Node node, Consumer<T> action) {
        if (node != null) {
            inOrderTraversal(node.left, action);
            action.accept(node.data); // Aplica a ação ao nó atual
            inOrderTraversal(node.right, action);
        }
    }

    // Obter a altura de um nó
    private int height(Node node) {
        return (node == null) ? 0 : node.height;
    }

    // Obter o fator de balanceamento de um nó
    private int getBalanceFactor(Node node) {
        return (node == null) ? 0 : height(node.left) - height(node.right);
    }

    // Rotação à direita
    private Node rotateRight(Node y) {
        Node x = y.left;
        Node T = x.right;

        x.right = y;
        y.left = T;

        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;

        return x;
    }

    // Rotação à esquerda
    private Node rotateLeft(Node x) {
        Node y = x.right;
        Node T = y.left;

        y.left = x;
        x.right = T;

        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;

        return y;
    }

    // Inserir um elemento na árvore
    public void insert(T data) {
        root = insert(root, data);
    }

    // Remover um elemento da árvore
public void delete(T data) {
    root = delete(root, data);
}

private Node delete(Node node, T data) {
    if (node == null) {
        return node;
    }

    // Navegar para o lado correto da árvore
    if (data.compareTo(node.data) < 0) {
        node.left = delete(node.left, data);
    } else if (data.compareTo(node.data) > 0) {
        node.right = delete(node.right, data);
    } else {
        // Caso: Nó com apenas um filho ou nenhum filho
        if ((node.left == null) || (node.right == null)) {
            Node temp = (node.left != null) ? node.left : node.right;

            // Caso: Nenhum filho
            if (temp == null) {
                node = null;
            } else {
                node = temp;
            }
        } else {
            // Caso: Nó com dois filhos
            Node temp = getMinValueNode(node.right);
            node.data = temp.data;
            node.right = delete(node.right, temp.data);
        }
    }

    // Atualizar a altura do nó
    if (node == null) {
        return node;
    }
    node.height = Math.max(height(node.left), height(node.right)) + 1;

    // Balancear a árvore
    int balanceFactor = getBalanceFactor(node);

    // Caso de rotação
    if (balanceFactor > 1 && getBalanceFactor(node.left) >= 0) {
        return rotateRight(node);
    }
    if (balanceFactor > 1 && getBalanceFactor(node.left) < 0) {
        node.left = rotateLeft(node.left);
        return rotateRight(node);
    }
    if (balanceFactor < -1 && getBalanceFactor(node.right) <= 0) {
        return rotateLeft(node);
    }
    if (balanceFactor < -1 && getBalanceFactor(node.right) > 0) {
        node.right = rotateRight(node.right);
        return rotateLeft(node);
    }

    return node;
}

// Método auxiliar para encontrar o menor valor na subárvore
private Node getMinValueNode(Node node) {
    Node current = node;
    while (current.left != null) {
        current = current.left;
    }
    return current;
}


    private Node insert(Node node, T data) {
        if (node == null) {
            return new Node(data);
        }

        if (data.compareTo(node.data) < 0) {
            node.left = insert(node.left, data);
        } else if (data.compareTo(node.data) > 0) {
            node.right = insert(node.right, data);
        } else {
            return node; // Elemento já existe, sem duplicatas
        }

        node.height = Math.max(height(node.left), height(node.right)) + 1;

        int balanceFactor = getBalanceFactor(node);

        // Rotação para balancear a árvore
        if (balanceFactor > 1 && data.compareTo(node.left.data) < 0) {
            return rotateRight(node);
        }
        if (balanceFactor < -1 && data.compareTo(node.right.data) > 0) {
            return rotateLeft(node);
        }
        if (balanceFactor > 1 && data.compareTo(node.left.data) > 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }
        if (balanceFactor < -1 && data.compareTo(node.right.data) < 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    // Buscar um elemento na árvore
    public boolean contains(T data) {
        return contains(root, data);
    }

    private boolean contains(Node node, T data) {
        if (node == null) {
            return false;
        }

        if (data.compareTo(node.data) == 0) {
            return true;
        }

        return data.compareTo(node.data) < 0 ? contains(node.left, data) : contains(node.right, data);
    }

    // Exibir a árvore em ordem
    public void inOrderTraversal() {
        inOrderTraversal(root);
    }

    private void inOrderTraversal(Node node) {
        if (node != null) {
            inOrderTraversal(node.left);
            System.out.println(node.data);
            inOrderTraversal(node.right);
        }
    }
}
