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

import arboralexplorer.Line;
import arboralexplorer.Pair;
import arboralexplorer.Point;
import arboralexplorer.algo.ArboralChecker;
import arboralexplorer.data.GridSet;
import arboralexplorer.data.WilberData;
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
import java.awt.geom.Line2D;
import java.util.ArrayList;
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
    private GridSet grid;
    private List<SetChangeListener> changeListeners;
    // Drawing options
    boolean drawCriticality = false;
    boolean drawBlack = true;
    boolean drawGrid = true;

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

        grid = new GridSet(new boolean[6][6]);
        changeListeners = new ArrayList<>();
    }

    public void addChangeListener(SetChangeListener listener) {
        changeListeners.add(listener);
    }

    public void removeChangeListener(SetChangeListener listener) {
        changeListeners.remove(listener);
    }

    public GridSet getGrid() {
        return grid;
    }

    public void setGrid(GridSet grid) {
        if (grid == null) {
            throw new NullPointerException();
        }

        this.grid = grid;
        zoomToFit();
        notifyChangeListeners();
    }

    public boolean isDrawCriticality() {
        return drawCriticality;
    }

    public void setDrawCriticality(boolean drawCriticality) {
        this.drawCriticality = drawCriticality;
        repaint();
    }

    public void setDrawBlack(boolean drawBlack) {
        this.drawBlack = drawBlack;
        repaint();
    }

    public void setDrawGrid(boolean drawGrid) {
        this.drawGrid = drawGrid;
        repaint();
    }

    public void zoomToFit() {
        int margin = 20; // pixels

        double minX = -0.5, minY = -0.5,
                maxX = grid.getWidth() - 0.5, maxY = grid.getHeight() - 0.5;

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
        if (grid.isGroundSet(x, y)) {
            g.setColor(Color.blue);
        } else {
            if (drawCriticality) {
                int criticality = ArboralChecker.computeCriticality(grid, x, y);

                if (criticality <= 0) {
                    g.setColor(new Color(255, 241, 35, 128));
                } else if (criticality == 1) {
                    g.setColor(new Color(255, 152, 57, 128));
                } else {
                    g.setColor(new Color(240, 25, 117, 128));
                }

                int backdropRadius = (int) Math.round(1.5 * POINT_RADIUS / zoomfactor);
                g.fillOval(xWorldToScreen(x) - backdropRadius, yWorldToScreen(y) - backdropRadius, 2 * backdropRadius, 2 * backdropRadius);
            }

            g.setColor(Color.black);
        }

        int radius = (int) Math.max(Math.round(POINT_RADIUS / zoomfactor), 2);
        g.fillOval(xWorldToScreen(x) - radius, yWorldToScreen(y) - radius, 2 * radius, 2 * radius);

        g.setColor(Color.black);
        ((Graphics2D) g).setStroke(new BasicStroke((float) (0.02 / zoomfactor)));

        g.drawOval(xWorldToScreen(x) - radius, yWorldToScreen(y) - radius, 2 * radius, 2 * radius);
    }

    private void fillViolation(Graphics g, Line violation) {
        int x1 = violation.getFirst().getXInt();
        int y1 = violation.getFirst().getYInt();
        int x2 = violation.getSecond().getXInt();
        int y2 = violation.getSecond().getYInt();

        g.setColor(new Color(255, 32, 32, 32));
        g.fillRect(xWorldToScreen(Math.min(x1, x2) + 0.1), yWorldToScreen(Math.min(y1, y2) + 0.1), (int) Math.round((Math.abs(x1 - x2) - 0.2) / zoomfactor), (int) Math.round((Math.abs(y1 - y2) - 0.2) / zoomfactor));
    }

    private void drawViolation(Graphics g, Line violation) {
        int x1 = violation.getFirst().getXInt();
        int y1 = violation.getFirst().getYInt();
        int x2 = violation.getSecond().getXInt();
        int y2 = violation.getSecond().getYInt();

        g.setColor(Color.red);
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(2));
        drawLine(g, x1, y1, x2, y2);
    }

    private void drawWilberData(Graphics g, WilberData wilber) {
        wilber.getHubs().forEach((hub) -> {
            drawColoredPoint(g, hub, Color.green);
        });
        wilber.getLines().forEach((splitLine) -> {
            drawColoredLine(g, splitLine, Color.green);
        });
        wilber.getLines(WilberData.identifier.REDLINES).forEach((line) -> {
            drawColoredLine(g, line, Color.red);
        });
        wilber.getPoints(WilberData.identifier.REDPOINTS).forEach((point) -> {
            drawColoredPoint(g, point, Color.orange);
        });
    }

    private void drawColoredLine(Graphics g, Line splitLine, Color c) {
        Number x1 = splitLine.getFirst().getFirst();
        Number y1 = splitLine.getFirst().getSecond();
        Number x2 = splitLine.getSecond().getFirst();
        Number y2 = splitLine.getSecond().getSecond();

        g.setColor(c);
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(2));
        drawLine(g, x1.floatValue(), y1.floatValue(), x2.floatValue(), y2.floatValue());
    }

    private void drawColoredPoint(Graphics g, Point point, Color c) {
        Number x = point.getX();
        Number y = point.getY();
        g.setColor(c);
        int radius = (int) Math.max(Math.round(0.5 * POINT_RADIUS / zoomfactor), 3);
        g.fillOval(xWorldToScreen(x.floatValue()) - radius, yWorldToScreen(y.floatValue()) - radius, 2 * radius, 2 * radius);
    }

    @Override
    protected void paintComponent(Graphics g) {
        int width = grid.getWidth();
        int height = grid.getHeight();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fill violation rectangles
        for (Line violation : grid.getViolations()) {
            fillViolation(g, violation);
        }

        // Draw the grid
        if (drawGrid) {
            g.setColor(Color.lightGray);
            for (int i = 0; i < width; i++) {
                drawLine(g, i, 0, i, height - 1);
            }
            for (int j = 0; j < height; j++) {
                drawLine(g, 0, j, width - 1, j);
            }
        }

        // Draw violations
        for (Line violation : grid.getViolations()) {
            drawViolation(g, violation);
        }

        // Draw the Wilber data
        WilberData wilber = grid.getWilberData();
        if (wilber != null) {
            drawWilberData(g, wilber);
        }

        // Draw the points
        if (drawBlack) {
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    if (grid.hasPoint(i, j)) {
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

            if (inGrid(i, j)) {
                if (grid.hasPoint(i, j)) {
                    grid.removePoint(i, j);
                } else {
                    grid.addPoint(i, j);
                }

                notifyChangeListeners();
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
            if (inGrid(i, j) && grid.hasPoint(i, j)) {
                if (grid.isGroundSet(i, j)) {
                    grid.removeFromGroundSet(i, j);
                } else {
                    grid.addToGroundSet(i, j);
                }
            } else {
                // Select all
                boolean anyFalse = false;
                int width = grid.getWidth();
                int height = grid.getHeight();

                for (int ii = 0; ii < width; ii++) {
                    for (int jj = 0; jj < height; jj++) {
                        if (grid.hasPoint(ii, jj) && !grid.isGroundSet(ii, jj)) {
                            grid.addToGroundSet(ii, jj);
                            anyFalse = true;
                        }
                    }
                }

                if (!anyFalse) {
                    // Everything was already in the ground set - deselect all
                    for (int ii = 0; ii < width; ii++) {
                        for (int jj = 0; jj < height; jj++) {
                            grid.removeFromGroundSet(ii, jj);
                        }
                    }
                }
            }

            notifyChangeListeners();
            repaint();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    private boolean inGrid(int i, int j) {
        return 0 <= i && i < grid.getWidth() && 0 <= j && j < grid.getHeight();
    }

    private void notifyChangeListeners() {
        for (SetChangeListener changeListener : changeListeners) {
            changeListener.gridChanged(this, grid);
        }
    }
}
