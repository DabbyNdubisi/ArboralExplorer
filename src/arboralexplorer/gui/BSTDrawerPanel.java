/*
 * Copyright 2016 dabeluchindubisi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package arboralexplorer.gui;

import arboralexplorer.BTNode;
import javax.swing.JPanel;
import arboralexplorer.BinarySearchTree;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;

/**
 *
 * @author dabeluchindubisi
 */
public class BSTDrawerPanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {
    BinarySearchTree tree;
    ArrayList<BTNode> accessedElems;
    
    private double zoomFactor = 1;
    private int panX = 0;
    private int panY = 0;
    private int mouseX = 0;
    private int mouseY = 0;
    
    public BSTDrawerPanel() {
        tree = null;
        initialize();
    }
    
    public void initialize() {
        setFocusable(true);
        setBackground(Color.white);
        setForeground(Color.black);
        
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
    }
    
    public void setTree(BinarySearchTree t, int access) {
        tree = t;
        if(access != -1)
            accessedElems = t.findPath(access);
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        if(tree == null)
            return;
        
        g.setColor(getBackground()); //colors the window
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(getForeground()); //set color and fonts
        drawTree(g);
        revalidate(); //update the component panel
    }
    
    public void drawTree(Graphics g) {
        int XSCALE, YSCALE;
        XSCALE = getWidth() / tree.numNodes; //scale x by total nodes in tree
        YSCALE = getHeight() / (tree.maxHeight + 1); //scale y by tree height
        final int r = (int)(Math.min(XSCALE, YSCALE) * zoomFactor),
                  m = (int)(Math.max(YSCALE, 10) * zoomFactor);
        
        Font MyFont = new Font("SansSerif", Font.BOLD, r/2); //bigger font for tree
        g.setFont(MyFont);
        this.drawTree(g, tree.root, r, m); // draw the tree
    }
    
    public void drawTree(Graphics g, BTNode root, int r, int m) {//actually draws the tree
        if (root == null) return;
	if (root.left != null) {
            g.drawLine(panX + (root.position.x * m + r/2), panY + (root.position.y * m  + r/2), 
                       panX + (root.left.position.x * m + r/2), panY + (root.left.position.y *m + r/2));
            drawTree(g, root.left, r, m);
	}
	if (root.right != null) {
            g.drawLine(panX + (root.position.x * m + r/2), panY + (root.position.y * m  + r/2), 
                       panX + (root.right.position.x * m + r/2), panY + (root.right.position.y *m + r/2));
            drawTree(g, root.right, r, m);
	}
        
        if(accessedElems.contains(root)) {
            if(accessedElems.get(accessedElems.size()-1) == root)
                g.setColor(Color.blue);
            else
                g.setColor(Color.black);
            g.fillOval(panX + (root.position.x * m), panY + (root.position.y * m), r, r);
            g.setColor(Color.white);
        } else {
            g.setColor(Color.gray);
            g.fillOval(panX + (root.position.x * m), panY + (root.position.y * m), r, r);
            g.setColor(Color.black);
        }
        
	g.drawString(""+root.value, panX + (root.position.x * m + r/4), panY + (root.position.y * m + (int)(0.75 * r)));
        g.setColor(Color.black);
    }
    
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        double factor;

        if (e.getWheelRotation() < 0) {
            factor = (10.0 / 11.0);
        } else {
            factor = (11.0 / 10.0);
        }

        zoomFactor *= factor;

        int centerX = e.getX();
        int centerY = e.getY();
        panX = (int) Math.round((centerX + panX) / factor - centerX);
        panY = (int) Math.round((centerY + panY) / factor - centerY);

        repaint();
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if ((e.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) == MouseEvent.BUTTON3_DOWN_MASK) {
            // pan
            panX += mouseX - e.getX();
            panY += mouseY - e.getY();

            mouseX = e.getX();
            mouseY = e.getY();

            repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // Keep track of the mouse position
        mouseX = e.getX();
        mouseY = e.getY();
    }
}
