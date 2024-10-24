package unit3;

public class BinarySearchTree<E extends Comparable<E>> {
    private static class Node<F extends Comparable<F>> implements Comparable<Node<F>> {
        F value;
        Node<F> left;
        Node<F> right;

        public Node(F value) {
            super();
            this.value = value;
        }

        public Node(F value, Node<F> left, Node<F> right) {
            super();
            this.value = value;
            this.left = left;
            this.right = right;
        }

        public boolean isLeaf() {
            return left == null && right == null;
        }

        @Override
        public int compareTo(Node<F> o) {
            return value.compareTo(o.value);
        }
    }

    private int size;

    private Node<E> root;

    public BinarySearchTree(E rootVal) {
        super();
        this.root = new Node<>(rootVal);
        size = 1;
    }

    public int size() {
        return size;
    }

    public void insert(E val) {
        insert(val, root);
    }
    
    private void insert(E val, Node<E> node) {
        int comp = node.value.compareTo(val);
    
        if(comp == 0) return;
        if(comp < 0) {
            if(node.left == null) {
                node.left = new Node<E>(val);
                size++;
            } else {
                insert(val, node.left);
            }
        } else {
            if(node.right == null) {
                node.right = new Node<E>(val);
                size++;
            } else {
                insert(val, node.right);
            }
        }
        
    }

    public boolean contains(E val) {
        return contains(val, root);
    }

    private static <G extends Comparable<G>> boolean contains(G val, Node<G> node) {
        int comp = node.value.compareTo(val);
    
        if(comp == 0) return true;
        if(comp < 0) {
            if(node.left == null) {
                return false;
            } else {
                return contains(val, node.left);
            }
        } else {
            if(node.right == null) {
                return false;
            } else {
                return contains(val, node.right);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        append(root, sb);

        return sb.toString();
    }

    private static <G extends Comparable<G>> void append(Node<G> node, StringBuilder sb) {
        if(node.right != null)
            append(node.right, sb);
        sb.append(node.value).append(" ");
        if(node.left != null)
            append(node.left, sb);
    }

    // public String viualize() {
    //     StringBuilder sb = new StringBuilder();
    // }

    // private static <G extends Comparable<G>> void appendVisual(Node<G> node, StringBuilder sb) {
    //     if(node.right != null){
    //         append(node.right, sb);
    //     }
    //     if(node.left != null){
    //         append(node.left, sb);
    //     }
    //     sb.append(node.value).append(" ");
    // }

}
