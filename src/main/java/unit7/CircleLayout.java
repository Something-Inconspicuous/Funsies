package unit7;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

public class CircleLayout implements LayoutManager {
    @Override
    public void addLayoutComponent(String name, Component comp) {
    }

    @Override
    public void removeLayoutComponent(Component comp) {
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        if(parent.isPreferredSizeSet()) return parent.getPreferredSize();
        else return calcPreferedLayoutSize(parent);
    }

    private Dimension calcPreferedLayoutSize(Container parent) {
        Dimension dim = new Dimension();
        synchronized (parent.getTreeLock()) {
            int n = parent.getComponentCount();
            for(int i = 0; i < n; i++) {
                Component comp = parent.getComponent(i);
                Dimension d = comp.getPreferredSize();
                dim.height = Math.max(dim.height, d.height);
                dim.width = Math.max(dim.width, d.width);
            }
        }
        return dim;
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        if(parent.isMinimumSizeSet()) return parent.getMinimumSize();
        else return calcMinimumLayoutSize(parent);
    }

    private Dimension calcMinimumLayoutSize(Container parent) {
        Dimension dim = new Dimension();
        synchronized (parent.getTreeLock()) {
            int n = parent.getComponentCount();
            for(int i = 0; i < n; i++) {
                Component comp = parent.getComponent(i);
                Dimension d = comp.getMinimumSize();
                dim.height = Math.max(dim.height, d.height);
                dim.width = Math.max(dim.width, d.width);
            }
        }
        return dim;
    }
    
    @Override
    public void layoutContainer(Container parent) {
        synchronized (parent.getTreeLock()) {
            double theta = -Math.PI * 0.5; // start at north
            final int n = parent.getComponentCount();
            final int h = parent.getHeight();
            final int w = parent.getWidth();

            final double r = 0.33 * Math.min(h, w);
            final double deltaTheta = Math.PI * 2.0 / n;

            final int cx = w / 2;
            final int cy = h / 2;
            for(int i = 0; i < n; i++) {
                final Component comp = parent.getComponent(i);
                final double x = Math.cos(theta);
                final double y = Math.sin(theta);

                final int xpx = (int) (x * r) + cx; 
                final int ypx = (int) (y * r) + cy;

                System.out.printf("[%d, %d]\n", xpx, ypx);
                final Dimension dim = comp.getPreferredSize();
                comp.setBounds(xpx, ypx, dim.width, dim.height);
                theta += deltaTheta;
            }
        }
        
    }
    
}
