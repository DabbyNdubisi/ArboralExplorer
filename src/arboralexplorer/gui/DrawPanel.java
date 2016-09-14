/*
 * Copyright 2016 Sander Verdonschot <sander.verdonschot at gmail.com>.
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

import arboralexplorer.Pair;
import arboralexplorer.algo.ArboralChecker;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.List;
import javax.swing.JPanel;

public class DrawPanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {

    private final double POINT_RADIUS = 0.2; // Radius of the points, relative to the grid size
    // Fields for the scale
    private double zoomfactor = 1;
    private int panX = 0;
    private int panY = 0;
    private int mouseX = 0;
    private int mouseY = 0;
    // The grid
    private boolean[][] grid;
    private boolean[][] groundSet;

    public DrawPanel() {
        initialize();
    }

    private void initialize() {
        setFocusable(true);
        setOpaque(true);
        setBackground(Color.white);

        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        addKeyListener(this);

        grid = new boolean[6][6];
        groundSet = new boolean[grid.length][grid[0].length];
    }

    public boolean[][] getGrid() {
        return grid;
    }

    public boolean[][] getGroundSet() {
        return groundSet;
    }

    public void setGrid(boolean[][] grid) {
        this.grid = copyGrid(grid);
        groundSet = new boolean[grid.length][grid[0].length];
        zoomToFit();
    }

    public void setGroundSet(boolean[][] groundSet) {
        if (grid.length != groundSet.length || grid[0].length != groundSet[0].length) {
            grid = copyGrid(groundSet);

            zoomToFit();
        }

        this.groundSet = copyGrid(groundSet);
    }

    public void zoomToFit() {
        int margin = 20; // pixels

        double minX = -0.5, minY = -0.5,
                maxX = grid.length - 0.5, maxY = (grid.length > 0 ? grid[0].length - 0.5 : 0);

        double zoomfactorX = (maxX - minX) / (getWidth() - 2 * margin);
        double zoomfactorY = (maxY - minY) / (getHeight() - 2 * margin);

        if (zoomfactorY > zoomfactorX) {
            zoomfactor = zoomfactorY;
            panX = (int) Math.round((maxX + minX) / (2 * zoomfactor)) - getWidth() / 2;
            panY = (int) Math.round(maxY / zoomfactor) - getHeight() + margin;
        } else {
            zoomfactor = zoomfactorX;
            panX = (int) Math.round(minX / zoomfactor) - margin;
            panY = (int) Math.round((maxY + minY) / (2 * zoomfactor)) - getHeight() / 2;
        }

        repaint();
    }

    private double xScreenToWorld(int x) {
        return (x + panX) * zoomfactor;
    }

    private double yScreenToWorld(int y) {
        return (y + panY) * zoomfactor;
    }

    private int xWorldToScreen(double x) {
        return (int) Math.round((x / zoomfactor) - panX);
    }

    private int yWorldToScreen(double y) {
        return (int) Math.round((y / zoomfactor) - panY);
    }

    private void drawLine(Graphics g, double x1, double y1, double x2, double y2) {
        g.drawLine(xWorldToScreen(x1), yWorldToScreen(y1), xWorldToScreen(x2), yWorldToScreen(y2));
    }

    private void drawPoint(Graphics g, int x, int y) {
        if (groundSet[x][y]) {
            g.setColor(Color.blue);
        } else {
            g.setColor(Color.black);
        }

        int size = (int) Math.round(2 * POINT_RADIUS / zoomfactor);
        g.fillOval(xWorldToScreen(x - POINT_RADIUS), yWorldToScreen(y - POINT_RADIUS), size, size);

        g.setColor(Color.black);
        ((Graphics2D) g).setStroke(new BasicStroke((float) (0.02 / zoomfactor)));

        g.drawOval(xWorldToScreen(x - POINT_RADIUS), yWorldToScreen(y - POINT_RADIUS), size, size);
    }

    private void drawViolation(Graphics g, Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> violation) {
        drawLine(g, violation.getFirst().getFirst(), violation.getFirst().getSecond(), violation.getSecond().getFirst(), violation.getSecond().getSecond());
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (grid.length > 0) {
            // Draw the grid
            int width = grid.length;
            int height = grid[0].length;

            g.setColor(Color.lightGray);
            for (int i = 0; i < width; i++) {
                drawLine(g, i, 0, i, height - 1);
            }
            for (int j = 0; j < height; j++) {
                drawLine(g, 0, j, width - 1, j);
            }

            // Draw violations
            g.setColor(Color.red);
            List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> violations = ArboralChecker.getAllAssViolations(grid);

            for (Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> violation : violations) {
                drawViolation(g, violation);
            }

            // Draw the points
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    if (grid[i][j]) {
                        drawPoint(g, i, j);
                    }
                }
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            // Toggle grid
            int i = (int) Math.round(xScreenToWorld(e.getX()));
            int j = (int) Math.round(yScreenToWorld(e.getY()));

            if (0 <= i && i < grid.length && 0 <= j && j < grid[0].length) {
                if (grid[i][j]) {
                    grid[i][j] = false;

                    if (groundSet[i][j]) {
                        groundSet[i][j] = false;
                    }
                } else {
                    grid[i][j] = true;
                }

                repaint();
            }
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            // start panning, store the current mouse position
            mouseX = e.getX();
            mouseY = e.getY();
        }
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

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        double factor;

        if (e.getWheelRotation() < 0) {
            factor = (10.0 / 11.0);
        } else {
            factor = (11.0 / 10.0);
        }

        zoomfactor *= factor;

        int centerX = e.getX();
        int centerY = e.getY();
        panX = (int) Math.round((centerX + panX) / factor - centerX);
        panY = (int) Math.round((centerY + panY) / factor - centerY);

        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int i = (int) Math.round(xScreenToWorld(mouseX));
        int j = (int) Math.round(yScreenToWorld(mouseY));

        if (e.getKeyCode() == KeyEvent.VK_DELETE) {

        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            zoomToFit();
        } else if (e.getKeyCode() == KeyEvent.VK_G) {
            if (inGrid(i, j) && grid[i][j]) {
                groundSet[i][j] = !groundSet[i][j];
            } else {
                // Select all
                boolean anyFalse = false;

                for (int ii = 0; ii < grid.length; ii++) {
                    for (int jj = 0; jj < grid[0].length; jj++) {
                        if (grid[ii][jj] && !groundSet[ii][jj]) {
                            groundSet[ii][jj] = true;
                            anyFalse = true;
                        }
                    }
                }

                if (!anyFalse) {
                    // Everything was already in the ground set - deselect all
                    for (int ii = 0; ii < grid.length; ii++) {
                        for (int jj = 0; jj < grid[0].length; jj++) {
                            if (grid[ii][jj]) {
                                groundSet[ii][jj] = false;
                            }
                        }
                    }
                }
            }

            repaint();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    private boolean inGrid(int i, int j) {
        return 0 <= i && i < grid.length && 0 <= j && j < grid[0].length;
    }

    private boolean[][] copyGrid(boolean[][] grid) {
        if (grid.length == 0) {
            return new boolean[0][0];
        } else {
            boolean[][] newGrid = new boolean[grid.length][grid[0].length];

            for (int i = 0; i < grid.length; i++) {
                System.arraycopy(grid[i], 0, newGrid[i], 0, grid[0].length);
            }

            return newGrid;
        }
    }
}
