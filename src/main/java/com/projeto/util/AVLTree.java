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

    public void inOrderTraversal(Consumer<T> action) {
        inOrderTraversal(root, action);
    }

    private void inOrderTraversal(Node node, Consumer<T> action) {
        if (node != null) {
            inOrderTraversal(node.left, action);
            action.accept(node.data); 
            inOrderTraversal(node.right, action);
        }
    }

    private int height(Node node) {
        return (node == null) ? 0 : node.height;
    }


    private int getBalanceFactor(Node node) {
        return (node == null) ? 0 : height(node.left) - height(node.right);
    }

    
    private Node rotateRight(Node y) {
        Node x = y.left;
        Node T = x.right;

        x.right = y;
        y.left = T;

        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;

        return x;
    }

    
    private Node rotateLeft(Node x) {
        Node y = x.right;
        Node T = y.left;

        y.left = x;
        x.right = T;

        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;

        return y;
    }

    
    public void insert(T data) {
        root = insert(root, data);
    }

    
public void delete(T data) {
    root = delete(root, data);
}

private Node delete(Node node, T data) {
    if (node == null) {
        return node;
    }

    
    if (data.compareTo(node.data) < 0) {
        node.left = delete(node.left, data);
    } else if (data.compareTo(node.data) > 0) {
        node.right = delete(node.right, data);
    } else {
       
        if ((node.left == null) || (node.right == null)) {
            Node temp = (node.left != null) ? node.left : node.right;

            
            if (temp == null) {
                node = null;
            } else {
                node = temp;
            }
        } else {
            
            Node temp = getMinValueNode(node.right);
            node.data = temp.data;
            node.right = delete(node.right, temp.data);
        }
    }

   
    if (node == null) {
        return node;
    }
    node.height = Math.max(height(node.left), height(node.right)) + 1;

    
    int balanceFactor = getBalanceFactor(node);

   
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
            return node; 
        }

        node.height = Math.max(height(node.left), height(node.right)) + 1;

        int balanceFactor = getBalanceFactor(node);

       
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
