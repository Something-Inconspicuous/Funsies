package unit6;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

public class TreePriorityQueue<E> implements Queue<E> {
    private class Node implements Comparable<Node>, Serializable {
        Node left;
        Node right;
        transient Node parent;

        E value;


        @SuppressWarnings("unchecked")
        @Override
        public int compareTo(TreePriorityQueue<E>.Node o) {
            if(comparator == null) {
                return ((Comparable<E>) value).compareTo(o.value);
            }
            return comparator.compare(value, o.value);
        }

        @SuppressWarnings("unchecked")
        public int compareWith(E e) {
            if(comparator == null) {
                return ((Comparable<E>) value).compareTo(e);
            }
            return comparator.compare(value, e);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getEnclosingInstance().hashCode();
            result = prime * result + ((left == null) ? 0 : left.hashCode());
            result = prime * result + ((right == null) ? 0 : right.hashCode());
            result = prime * result + ((value == null) ? 0 : value.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            @SuppressWarnings("unchecked")
            Node other = (Node) obj;
            if (!getEnclosingInstance().equals(other.getEnclosingInstance()))
                return false;
            if (left == null) {
                if (other.left != null)
                    return false;
            } else if (!left.equals(other.left))
                return false;
            if (right == null) {
                if (other.right != null)
                    return false;
            } else if (!right.equals(other.right))
                return false;
            if (value == null) {
                if (other.value != null)
                    return false;
            } else if (!value.equals(other.value))
                return false;
            return true;
        }

        @SuppressWarnings("rawtypes")
        private TreePriorityQueue getEnclosingInstance() {
            return TreePriorityQueue.this;
        }

        @Override
        public String toString() {
            if(left == null) {
                if(right == null) {
                    return "{" + value + "}";
                } else {
                    return "{" + value + " > " + right + "}";
                }
            } else {
                if(left == null) {
                    return "{" + left + " < " + value + "}";
                } else {
                    return "{" + left + " < " + value + " > " + right + "}";
                }
            }
        }

        
    }

    private Comparator<? super E> comparator;

    private transient int size;

    private Node root;
    
    // The tail of the queue, tracked to allow O(1) poll and peek operation
    // rather than O(log(n))
    private transient Node least;

    protected transient int modCount;

    public TreePriorityQueue() {
        this(null);
    }

    public TreePriorityQueue(Comparator<? super E> comparator) {
        this.comparator = comparator;
        this.root = new Node();
        least = root;
    }

    @SuppressWarnings("unchecked")
    private int compare(E e, Node node) {
        if(comparator == null) {
            return ((Comparable<E>) e).compareTo(node.value);
        }
        return comparator.compare(e, node.value);
    }
    
    @SuppressWarnings("unchecked")
    private int compare(Node node1, Node node2) {
        if(comparator == null) {
            return ((Comparable<E>) node1.value).compareTo(node2.value);
        }
        return comparator.compare(node1.value, node2.value);
    }
    
    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        if(o == null) return false;
        return search(o) != null;
    }
    
    @SuppressWarnings("unchecked")
    private Node search(Object o) {
        if(root.value == null) return null;

        if(comparator == null) {
            return searchComparable(root, (E) o);
        } else {
            return searchComparator(root, (E) o);
        }
    }

    @SuppressWarnings("unchecked")
    private Node searchM1(Object o) {
        if(root.value == null) return root;

        if(comparator == null) {
            return searchComparableM1(root, (E) o);
        } else {
            return searchComparatorM1(root, (E) o);
        }
    }

    private Node searchComparable(Node node, E find) {
        if(node == null) return null; // not found

        int cmp = node.compareWith(find);
        if(cmp == 0) return node;

        if(cmp < 0) return searchComparable(node.left, find);
        else        return searchComparable(node.right, find);
    }

    private Node searchComparator(Node node, E find) {
        if(node == null) return null; // not found

        int cmp = comparator.compare(node.value, find);
        if(cmp == 0) return node;

        // node.value > find, find must be left
        if(cmp > 0) return searchComparator(node.left, find);
        else        return searchComparator(node.right, find);
    }
    
    // search minus 1, gets a leaf
    private Node searchComparableM1(Node node, E find) {
        int cmp = node.compareWith(find);
        if(cmp == 0) return node;

        // node.value > find, find must be left
        if(cmp > 0){
            if(node.left == null) return node;
            else                  return searchComparableM1(node.left, find);
        } else {
            if(node.right == null) return node;
            else                   return searchComparableM1(node.right, find);
        }
    }

    private Node searchComparatorM1(Node node, E find) {
        int cmp = comparator.compare(node.value, find);
        if(cmp == 0) return node;

        // node.value > find, find must be left
        if(cmp > 0){
            if(node.left == null) return node;
            else                  return searchComparatorM1(node.left, find);
        } else {
            if(node.right == null) return node;
            else                   return searchComparatorM1(node.right, find);
        }
    }

    private class QIterator implements Iterator<E> {
        private int expectedModCount;

        private Node next;
        private Node old;

        public QIterator() {
            super();
            next = root;

            while(next.left != null) {
                next = next.left;
            }

            this.expectedModCount = modCount;
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public E next() {
            checkModCount();
            old = next;
            next = successor(next);
            return old.value;
        }

        @Override
        public void remove() {
            checkModCount();
            modCount++;
            expectedModCount++;
            
            removeNode(old);
        }

        private void checkModCount() {
            if(modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }
    }

    private static class EmptyIterator<E> implements Iterator<E> {
        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public E next() {
            throwForEmptyQueue();
            return null;
        }

        @Override
        public void remove() {
            throwForEmptyQueue();
        }
    }

    private Node successor(Node node) {
        if (node == null)
            return null;
        else if (node.right != null) {
            Node parent = node.right;
            while (parent.left != null)
                parent = parent.left;
            return parent;
        } else {
            Node parent = node.parent;
            Node ch = node;
            while (parent != null && ch == parent.right) {
                ch = parent;
                parent = parent.parent;
            }
            return parent;
        }
    }

    @Override
    public Iterator<E> iterator() {
        if(isEmpty()) return new EmptyIterator<E>();
        else          return new QIterator();
    }

    @Override
    public Object[] toArray() {
        Object[] a = allocateArray();
        fillArray(a);
        return a;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        if(a == null || a.length < size()) {
            a = allocateArray();
        }
        fillArray(a);
        return a;
    }

    @SuppressWarnings("unchecked")
    private <T> T[] allocateArray() {
        return (T[]) new Object[size()];
    }

    private <T> void fillArray(T[] a) {
        Arrays.fill(a, null);
        addToArray(a, 0, root);
    }

    @SuppressWarnings("unchecked")
    private <T> int addToArray(T[] a, int i, Node node) {
        if(node == null) return i;
        if(node.left != null) i = addToArray(a, i, node.left);
        if(node.right != null) i = addToArray(a, i, node.right);
        a[i] = (T) node.value;
        return i + 1;
    }

    @Override
    public boolean remove(Object o) {
        Node node = search(o);
        if(node == null) {
            return false;
        }

        removeNode(node);

        return true;
    }

    private void removeNode(Node node) {
        --size;
        ++modCount;
        
        if(node.right == null) {
            if(node.left != null) {
                node.left.parent = node.parent;
            }

            if(node.parent.left == node) {
                node.parent.left = node.left;
            } else {
                node.parent.right = node.left;
            }
        } else {
            Node floor = node.right;
    
            while(floor.left != null) {
                floor = floor.left;
            }
    
            // cut floor out of its parent's life
            if(floor.parent.left == floor) {
                floor.parent.left = null;
            } else {
                floor.parent.right = null;
            }
            floor.parent = node.parent;
    
            if(floor.parent.left == node) {
                floor.parent.left = floor;
            } else {
                floor.parent.right = floor;
            }
        }
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for(Object o : c) {
            if(!contains(o)) return false;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean ret = false;
        for(E e : c) {
            ret |= offer(e);
        }
        return ret;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean ret = false;
        for(Object o : c) {
            ret |= remove(o);
        }
        return ret;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean ret = false;
        
        for(Node node = root; node != null; node = successor(node)) {
            if(!c.contains(node.value)) {
                ret = true;
                removeNode(node);
            }
        }

        return ret;
    }

    @Override
    public void clear() {
        this.root = new Node();
        this.least = root;
        size = 0;
    }

    @Override
    public boolean add(E e) {
        return offer(e);
    }

    @Override
    public boolean offer(E e) {
        if(root.value == null) {
            root.value = e;
            ++size;
            ++modCount;
            return true;
        } else {
            // System.out.println("emplacing " + e + " with parent " + parent);
            Node parent = searchM1(e);
            return emplaceNode(e, parent);
        }
    }

    private boolean emplaceNode(E e, Node parent) {
        int cmp = compare(e, parent);
        if(cmp == 0) return false;

        Node node = new Node();
        node.parent = parent;
        node.value = e;

        if(compare(least, node) > 0) {
            least = node;
        }

        if(cmp < 0) parent.left = node;
        else        parent.right = node;
        ++size;
        ++modCount;
        return true;
    }

    @Override
    public E remove() {
        E e = poll();
        if(e == null)
            throwForEmptyQueue();

        return e;
    }

    private static void throwForEmptyQueue() {
        throw new NoSuchElementException("Queue is empty!");
    }

    @Override
    public E poll() {
        if(isEmpty() || least == null) return null;

        --size;
        ++modCount;
        E e = least.value;

        // reconfigure least node
        least.parent.left = least.right;
        if(least.right != null) {
            least.right.parent = least.parent;
            least = least.right;
        } else {
            least = least.parent;
        }

        return e;
    }

    @Override
    public E element() {
        E peek = peek();
        if(peek == null)
            throwForEmptyQueue();

        return peek;
    }

    @Override
    public E peek() {
        if(least == null) return null;
        return least.value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((comparator == null) ? 0 : comparator.hashCode());
        result = prime * result + ((root == null) ? 0 : root.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        @SuppressWarnings("rawtypes")
        TreePriorityQueue other = (TreePriorityQueue) obj;
        if (comparator == null) {
            if (other.comparator != null)
                return false;
        } else if (!comparator.equals(other.comparator))
            return false;
        if (root == null) {
            if (other.root != null)
                return false;
        } else if (!root.equals(other.root))
            return false;
        return true;
    }

    @Override
    public String toString() {
        if(size() == 0) return "[]";
        if(size() == 1) return "[" + root.value + "]";

        Iterator<E> it = iterator();
        StringBuilder sb = new StringBuilder("[");

        while(true) {
            sb.append(it.next());
            if(it.hasNext()) {
                sb.append(", ");
            } else {
                break;
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
