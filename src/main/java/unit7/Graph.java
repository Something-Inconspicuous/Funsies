package unit7;

import java.awt.Dimension;
import java.util.AbstractCollection;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.SequencedMap;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Graph<E> extends AbstractCollection<E> {
    public static void main(String[] args) {
        Graph<String> graph = new Graph<>();

        graph.add("Bob", "Maria");
        graph.add("Bob", "Mark");
        graph.add("Maria", "Alice");
        graph.add("Mark", "Alice");
        graph.add("Maria", "Rob");
        graph.add("Mark", "Rob");

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
            JPanel pane = new JPanel();
            GraphGUI ggui = new GraphGUI(graph);
            ggui.setPreferredSize(new Dimension(400, 400));
            pane.add(ggui);
            frame.setContentPane(pane);
            frame.setSize(400, 400);
        });

        // graph.depthFirst(System.out::println);
        // System.out.println();
        // graph.breadthFirst(System.out::println);
        System.out.println(graph);
    }

    // extension that caches an immutable veiw into a list
    private static final class LinkedListWithView<F> extends LinkedList<F> {
        private transient List<F> immutVeiw = null;

        public List<F> getVeiw() {
            if(immutVeiw == null) {
                immutVeiw = Collections.unmodifiableList(this);
            }
            return immutVeiw;
        }
    }

    // private transient E arbRoot;

    private SequencedMap<E, LinkedListWithView<E>> data = new LinkedHashMap<>();

    private transient int edgeCount = 0;

    @Override
    public boolean add(E vertex) {
        // if(arbRoot == null) arbRoot = vertex;
        return data.putIfAbsent(vertex, new LinkedListWithView<>()) == null;
    }

    @Override
    public boolean remove(Object vertex) {
        // if(Objects.equals(vertex, arbRoot)) {
        //     arbRoot = null;
        // }
        boolean ret = false;
        Collection<? extends List<E>> values = data.values();
        for(List<E> list : values) {
            if(list.remove(vertex)) {
                edgeCount--;
                ret = true;
            }
        }
        return data.remove(vertex) == null || ret;
    }

    public boolean remove(E src, E dst) {
        List<E> eV1 = data.get(src);
        List<E> eV2 = data.get(dst);
        boolean ret = false;
        if (eV1 != null)
            ret |= eV1.remove(src);
        if (eV2 != null)
            ret |= eV2.remove(dst);
        if(ret) edgeCount--;
        return ret;
    }

    public boolean add(E src, E dst, boolean bi) {
        if (!data.containsKey(src))   
            add(src);   
        if (!data.containsKey(dst))   
            add(dst);   
        boolean ret = data.get(src).add(dst);
        if(ret) edgeCount++; 
        if (bi) {
            ret |= data.get(dst).add(src);
        }
        return ret;
    }

    public boolean add(E src, E dst) {
        return add(src, dst, false);
    }

    public int size() {
        return data.size();
    }

    public int edgeCount() {
        return edgeCount;
    }

    @Override
    public boolean contains(Object vertex) {
        return data.containsKey(vertex);
    }

    public boolean contains(E src, E dst) {
        return data.get(src).contains(dst);
    }

    @Override  
    public String toString() {   
        StringBuilder sb = new StringBuilder("{\n");
        for (E v : data.keySet()) {
            sb.append("\t")
                .append(v)
                .append(": ")
                .append(data.get(v))
                .append("\n");
        }
        return sb.append("}").toString();   
    }

    public void depthFirst(Consumer<? super E> action, E root) {
        Set<E> visited = new HashSet<>();
        Deque<E> stack = new ArrayDeque<>();
        stack.push(root);
        while(!stack.isEmpty()) {
            E vertex = stack.pop();
            if(!visited.contains(vertex)) {
                action.accept(vertex);
                visited.add(vertex);
                List<E> neighbors = data.get(vertex);
                for(E e : neighbors) {
                    stack.push(e);
                }
            }
        }
    }

    public List<E> getNeighbors(E vertex) {
        LinkedListWithView<E> llwv = data.get(vertex);
        if(llwv == null) throw new NoSuchElementException("No vertex " + vertex + " found.");
        else return llwv.getVeiw();
    }

    public List<E> get(Object vertex) {
        LinkedListWithView<E> llwv = data.get(vertex);
        if(llwv == null) return null;
        else return llwv.getVeiw();
    }

    public void depthFirst(Consumer<? super E> action) {
        depthFirst(action, data.firstEntry().getKey());
    }

    public void breadthFirst(Consumer<? super E> action, E root) {
        Set<E> visited = new LinkedHashSet<>();
        Deque<E> queue = new ArrayDeque<>();
        queue.push(root);
        while(!queue.isEmpty()) {
            E vertex = queue.pollLast();
            action.accept(vertex);
            visited.add(vertex);
            List<E> neighbors = data.get(vertex);
            for(E e : neighbors) {
                if(!visited.contains(e)) {
                    visited.add(e);
                    queue.push(e);
                }
            }
        }
    }

    public void breadthFirst(Consumer<? super E> action) {
        breadthFirst(action, data.firstEntry().getKey());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(data);
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
        Graph other = (Graph) obj;
        if (data == null) {
            if (other.data != null)
                return false;
        } else if (!data.equals(other.data))
            return false;
        return true;
    }

    @Override
    public Iterator<E> iterator() {
        return data.keySet().iterator();
    }

    @Override
    public Spliterator<E> spliterator() {
        return data.keySet().spliterator();
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        breadthFirst(action);
    }
}
