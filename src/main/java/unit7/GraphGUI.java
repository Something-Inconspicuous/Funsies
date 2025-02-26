package unit7;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.util.List;
import java.util.Objects;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class GraphGUI extends JPanel {
    private Graph<?> graph;

    private static final class ObjLabel extends JLabel {
        Object obj;
        public ObjLabel(Object o) {
            super(String.valueOf(o));
            this.obj = o;
            setBackground(Color.BLACK);
            setForeground(Color.WHITE);
            setOpaque(true);
        }
    }

    public GraphGUI(Graph<?> graph) {
        super(new CircleLayout());
        this.graph = graph;
        graph.breadthFirst(v -> {
            add(new ObjLabel(v));
        });
    }

    private static final Color[] order = {
        Color.BLACK, Color.BLUE, Color.GREEN, Color.RED, Color.PINK
    };

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        validate();
        Graphics gg = g.create();

        graph.breadthFirst(v -> drawEdgesOf(v, gg));

        gg.dispose();
    }

    private void drawEdgesOf(Object vertex, Graphics g) {
        List<?> neighbors = graph.get(vertex);

        synchronized(getTreeLock()) {
            for(Object neighbor : neighbors) {
                int x1 = -1, y1 = -1, x2 = -1, y2 = -1;

                final int n = getComponentCount();
                for(int i = 0; i < n; i++) {
                    Component comp = getComponent(i);
                    if(comp instanceof ObjLabel) {
                        ObjLabel label = (ObjLabel) comp;
                        if(x2 == -1 && Objects.equals(neighbor, label.obj)) {
                            x2 = label.getX() + label.getWidth() / 2;
                            y2 = label.getY() + label.getHeight() / 2;
                        } else if (x1 == -1 && Objects.equals(vertex, label.obj)){
                            x1 = label.getX() + label.getWidth() / 2;
                            y1 = label.getY() + label.getHeight() / 2;
                        }

                        if(x1 != -1 && x2 != -1) {
                            break;
                        }
                    }
                }
                g.drawLine(x1, y1, x2, y2);
            }
        }
    }
}
