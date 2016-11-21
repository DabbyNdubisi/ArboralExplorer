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
package arboralexplorer.algo.upperbound;

import arboralexplorer.Line;
import arboralexplorer.Pair;
import arboralexplorer.Point;
import arboralexplorer.data.GridSet;
import arboralexplorer.data.WilberData;
import static java.lang.Integer.max;
import static java.lang.Integer.min;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class GreedyASStar {

    /**
     * Returns a superset of the given grid without ASS-violations.
     *
     * @param grid
     * @return
     */
    public static GridSet solve(GridSet grid) {
        int width = grid.getWidth(), height = grid.getHeight();
        boolean[][] newGrid = new boolean[width][height];

//        List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> violations = grid.getViolations();
//        boolean[][][][] violins = new boolean[width][height][width][height];
//        for(Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> violation : violations) {
//            violins[violation.getFirst().getFirst()][violation.getFirst().getSecond()][violation.getSecond().getFirst()][violation.getSecond().getSecond()] = true;
//        }
        int[] lowestPoint = new int[width];
        Arrays.fill(lowestPoint, -1);

        WilberData wilber = new WilberData(grid.getGridSet());

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                if (grid.isGroundSet(i, j)) {
                    newGrid[i][j] = true;

                    // Scan left
                    int lowest = lowestPoint[i];
                    int previousX = i;

                    if (lowest != j + 1) {
                        scan(grid, wilber, newGrid, lowestPoint, i, j, Direction.LEFT);
                    }

                    // Scan right
                    lowest = lowestPoint[i];
                    previousX = i;

                    if (lowest != j + 1) {
                        scan(grid, wilber, newGrid, lowestPoint, i, j, Direction.RIGHT);
                    }

                    lowestPoint[i] = j;
                }
            }
        }
        
        List<Line> dependent = new ArrayList<>();
        List<Line> charged = wilber.getLines();
        for(int i = 0; i < charged.size(); ++i) {
            Line line = charged.get(i);
            for(int j = 0; j < charged.size(); ++j) {
                boolean d = false;
                if(dependent(line, charged.get(j))) {
                    d = true;
                    wilber.addLine(edgyEdgy(line, charged.get(j)), WilberData.identifier.REDLINES);
                }
                if(d) {
                    dependent.add(line);
                    wilber.addPoint(line.midPoint(), WilberData.identifier.REDPOINTS);
                }
            }
        }

        wilber.setLines(dependent);
        GridSet newGridSet = new GridSet(newGrid, grid.getGroundSet());
        newGridSet.setWilberData(wilber);
        return newGridSet;
    }
    
    private static Line edgyEdgy(Line l1, Line l2) {
        return new Line(l1.midPoint(), l2.midPoint());
    }
    
    private static boolean dependent(Line l1, Line l2) {
        Point c1 = new Point(l1.getFirst().getFirst(), l1.getSecond().getSecond());
        Point c2 = new Point( l1.getSecond().getFirst(), l1.getFirst().getSecond());
        return strictlyContains(c1, l2) || strictlyContains(c2, l2);
    }

    private static void scan(GridSet grid, WilberData wilber, boolean[][] newGrid, int[] lowestPoint, int i, int j, Direction d) {
        // Scan left
        int lowest = lowestPoint[i];
        int previousX = i;

        int dir = d == Direction.LEFT ? -1 : 1;

        if (lowest != j + 1) {
            for (int k = i + dir; k >= 0 && k < grid.getWidth() && !grid.isGroundSet(k, j); k += dir) {
                if (lowestPoint[k] > lowest) {
                    if (grid.isGroundSet(k, lowestPoint[k])) {
                        wilber.addLine(i, j, k, lowestPoint[k]);
                    } else {
                        Point cPoint = new Point(i,j);
                        Point lPoint = new Point(k, lowestPoint[k]);
                        Point hitLeft = growRect(grid, cPoint, lPoint, d);
                        Point hitLeftTop = growRect(grid, cPoint, new Point(hitLeft.getFirst(), lowestPoint[k]), Direction.UP);
                        Point hitTop = growRect(grid, cPoint, lPoint, Direction.UP);
                        Point hitTopLeft = growRect(grid, cPoint, new Point(k, hitTop.getSecond()), d);

                        
                        if(strictlyContains(lPoint, new Line(hitLeftTop, hitTopLeft))) {
                            wilber.addHub(lPoint.getFirst(), lPoint.getSecond(), false);
//                            wilber.addLine(i, j, lPoint.getFirst(), lPoint.getSecond());
                        }
                        
                        hitLeft = hitLeftTop;
                        hitTop = hitTopLeft;
                        if (hitLeft.getXInt() >= 0 && hitLeft.getXInt() < grid.getWidth()) {
                            wilber.addLine(i, j, hitLeft.getFirst(), hitLeft.getSecond());
                        }
                        if (hitTop.getXInt() >= 0) {
//                            wilber.addLine(i, j, hitTop.getFirst(), hitTop.getSecond());
                        }
                    }
                    lowest = lowestPoint[k];
                    previousX = k;
                    newGrid[k][j] = true;
                    lowestPoint[k] = j;

                }
            }
        }
    }

    /**
     * Checks whether point is contained in the box defined by the line.
     * The coordinates of all objects involved are assumed to be integers.
     * @param point
     * @param b1
     * @param b2
     * @return 
     */
    private static boolean strictlyContains(Point point, Line line) {
        int px = point.getXInt();
        int py = point.getYInt();
        int bxmin = min(line.getFirst().getXInt(), line.getSecond().getXInt());
        int bymin = min(line.getFirst().getYInt(), line.getSecond().getYInt());
        int bxmax = max(line.getFirst().getXInt(), line.getSecond().getXInt());
        int bymax = max(line.getFirst().getYInt(), line.getSecond().getYInt());
        return px > bxmin && px < bxmax && py > bymin && py < bymax;
    }
    
    private static Point growRect(GridSet grid, Point point, Point lineEnd, Direction d) {

        int px = point.getXInt();
        int py = point.getYInt();
        int lx = lineEnd.getXInt();
        int ly = lineEnd.getYInt();
        if (px < 0 || py < 0 || lx < 0 || ly < 0) {
            return new Point(-1, -1);
        }

        int i = -1, j = -1;

        if (d == Direction.LEFT) {
            for (i = lx; i >= 0; i--) {
                for (j = py; j > ly; j--) {
                    if (grid.isGroundSet(i, j)) {
                        return new Point(i, j);
                    }
                }
            }
        }
        if (d == Direction.RIGHT) {
            for (i = lx; i < grid.getWidth(); i++) {
                for (j = py; j > ly; j--) {
                    if (grid.isGroundSet(i, j)) {
                        return new Point(i, j);
                    }
                }
            }
        }

        int top = min(py, ly);
        int left = min(px, lx);
        int right = max(px, lx);
        if (d == Direction.UP) {

            for (j = top; j >= 0; j--) {
                for (i = left; i <= right; i++) {
                    if (grid.isGroundSet(i, j)) {
                        return new Point(i, j);
                    }
                }
            }
        }

        return new Point(min(grid.getWidth() - 1, max(0, i)), min(grid.getHeight() - 1, max(0, j)));
    }

    private enum Direction {
        LEFT, RIGHT, UP, DOWN;
    }
}
