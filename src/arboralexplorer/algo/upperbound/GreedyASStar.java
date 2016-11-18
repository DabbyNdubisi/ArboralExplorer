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

import arboralexplorer.Pair;
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
        
        List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> dependent = new ArrayList<>();
        List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> charged = wilber.getLines();
        for(int i = 0; i < charged.size(); ++i) {
            Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> line = charged.get(i);
            for(int j = 0; j < charged.size(); ++j) {
                boolean d = false;
                if(dependent(line, charged.get(j))) {
                    d = true;
                    wilber.addLine(edgyEdgy(line, charged.get(j)), WilberData.identifier.REDLINES);
                }
                if(d) {
                    dependent.add(line);
                    wilber.addPoint(midPoint(line), WilberData.identifier.REDPOINTS);
                }
            }
        }

        wilber.setLines(dependent);
        GridSet newGridSet = new GridSet(newGrid, grid.getGroundSet());
        newGridSet.setWilberData(wilber);
        return newGridSet;
    }
    
    private static Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> edgyEdgy(Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> l1, Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> l2) {
        return new Pair(midPoint(l1), midPoint(l2));
    }
    
    private static Pair<Integer, Integer> midPoint(Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> line) {
        int x1 = line.getFirst().getFirst();
        int y1 = line.getFirst().getSecond();
        int x2 = line.getSecond().getFirst();
        int y2 = line.getSecond().getSecond();
        return new Pair((x1+x2)/2, (y1+y2)/2);
    }
    
    private static boolean dependent(Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> l1, Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> l2) {
        Pair<Integer, Integer> c1 = new Pair(l1.getFirst().getFirst(), l1.getSecond().getSecond());
        Pair<Integer,Integer> c2 = new Pair( l1.getSecond().getFirst(), l1.getFirst().getSecond());
        return strictlyContains(c1, l2.getFirst(), l2.getSecond()) || strictlyContains(c2, l2.getFirst(), l2.getSecond());
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
                        Pair<Integer, Integer> cPoint = new Pair(i,j);
                        Pair<Integer, Integer> lPoint = new Pair(k, lowestPoint[k]);
                        Pair<Integer, Integer> hitLeft = growRect(grid, cPoint, lPoint, d);
                        Pair<Integer, Integer> hitLeftTop = growRect(grid, cPoint, new Pair(hitLeft.getFirst(), lowestPoint[k]), Direction.UP);
                        Pair<Integer, Integer> hitTop = growRect(grid, cPoint, lPoint, Direction.UP);
                        Pair<Integer, Integer> hitTopLeft = growRect(grid, cPoint, new Pair(k, hitTop.getSecond()), d);

                        
                        if(strictlyContains(lPoint, hitLeftTop, hitTopLeft)) {
                            wilber.addHub(lPoint.getFirst(), lPoint.getSecond(), false);
//                            wilber.addLine(i, j, lPoint.getFirst(), lPoint.getSecond());
                        }
                        
                        hitLeft = hitLeftTop;
                        hitTop = hitTopLeft;
                        if (hitLeft.getFirst() >= 0 && hitLeft.getFirst() < grid.getWidth()) {
                            wilber.addLine(i, j, hitLeft.getFirst(), hitLeft.getSecond());
                        }
                        if (hitTop.getFirst() >= 0) {
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

    private static boolean strictlyContains(Pair<Integer, Integer> point, Pair<Integer, Integer> b1, Pair<Integer, Integer> b2) {
        int px = point.getFirst();
        int py = point.getSecond();
        int bxmin = min(b1.getFirst(), b2.getFirst());
        int bymin = min(b1.getSecond(), b2.getSecond());
        int bxmax = max(b1.getFirst(), b2.getFirst());
        int bymax = max(b1.getSecond(), b2.getSecond());
        return px > bxmin && px < bxmax && py > bymin && py < bymax;
    }
    
    private static Pair<Integer, Integer> growRect(GridSet grid, Pair<Integer, Integer> point, Pair<Integer, Integer> line, Direction d) {

        int px = point.getFirst();
        int py = point.getSecond();
        int lx = line.getFirst();
        int ly = line.getSecond();
        if (px < 0 || py < 0 || lx < 0 || ly < 0) {
            return new Pair(-1, -1);
        }

        int i = -1, j = -1;

        if (d == Direction.LEFT) {
            for (i = lx; i >= 0; i--) {
                for (j = py; j > ly; j--) {
                    if (grid.isGroundSet(i, j)) {
                        return new Pair(i, j);
                    }
                }
            }
        }
        if (d == Direction.RIGHT) {
            for (i = lx; i < grid.getWidth(); i++) {
                for (j = py; j > ly; j--) {
                    if (grid.isGroundSet(i, j)) {
                        return new Pair(i, j);
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
                        return new Pair(i, j);
                    }
                }
            }
        }

        return new Pair(min(grid.getWidth() - 1, max(0, i)), min(grid.getHeight() - 1, max(0, j)));
    }

    private enum Direction {
        LEFT, RIGHT, UP, DOWN;
    }
}
