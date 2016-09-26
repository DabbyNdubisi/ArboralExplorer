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
package arboralexplorer.algo;

import arboralexplorer.Pair;
import arboralexplorer.data.GridSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArboralChecker {

    public static boolean isArborallySatisfied(GridSet grid) {
        // Can be made smarter
        return getAllAssViolations(grid).isEmpty();
    }

    public static boolean isArborallySatisfied(boolean[][] grid) {
        // Can be made smarter
        return getAllAssViolations(grid).isEmpty();
    }

    public static List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> getAllAssViolations(GridSet grid) {
        return getAllAssViolations(grid.getGridSet());
    }

    public static List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> getAllAssViolations(boolean[][] grid) {
        List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> violations = new ArrayList<>();

        int width = grid.length, height = grid[0].length;
        int[] lowestPoint = new int[width];
        Arrays.fill(lowestPoint, -1);

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                if (grid[i][j]) {
                    // Scan left
                    int lowest = lowestPoint[i];

                    if (lowest != j + 1) {
                        for (int k = i - 1; k >= 0 && !grid[k][j]; k--) {
                            if (lowestPoint[k] > lowest) {
                                violations.add(new Pair<>(new Pair<>(k, lowestPoint[k]), new Pair<>(i, j)));
                                lowest = lowestPoint[k];
                            }
                        }
                    }

                    // Scan right
                    lowest = lowestPoint[i];

                    if (lowest != j + 1) {
                        for (int k = i + 1; k < width && !grid[k][j]; k++) {
                            if (lowestPoint[k] > lowest) {
                                violations.add(new Pair<>(new Pair<>(i, j), new Pair<>(k, lowestPoint[k])));
                                lowest = lowestPoint[k];
                            }
                        }
                    }

                    lowestPoint[i] = j;
                }
            }
        }

        return violations;
    }

    /**
     * Returns a list of all subcritical points in a given ASS (points whose
     * removal does not create any violations). Not guaranteed to do anything
     * useful if the input is not an ASS.
     *
     * @param grid
     * @return
     */
    public static List<Pair<Integer, Integer>> getAllSubCriticalPoints(GridSet grid) {
        return getAllSubCriticalPoints(grid.getGridSet());
    }

    /**
     * Returns a list of all subcritical points in a given ASS (points whose
     * removal does not create any violations). Not guaranteed to do anything
     * useful if the input is not an ASS.
     *
     * @param grid
     * @return
     */
    public static List<Pair<Integer, Integer>> getAllSubCriticalPoints(boolean[][] grid) {
        List<Pair<Integer, Integer>> subCritical = new ArrayList<>();

        int[][] criticality = computeCriticality(getNeighbourlyMatrix(grid));
        
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (criticality[i][j] == 0) {
                    subCritical.add(new Pair<>(i, j));
                }
            }
        }
        
        return subCritical;
    }

    private static NeighbourlyPoint[][] getNeighbourlyMatrix(boolean[][] grid) {
        int width = grid.length, height = grid[0].length;
        NeighbourlyPoint[][] matrix = new NeighbourlyPoint[width][height];

        int[] lowestPoint = new int[width];
        Arrays.fill(lowestPoint, -1);

        for (int j = 0; j < height; j++) {
            int leftNeighbour = -1;

            for (int i = 0; i < width; i++) {
                if (grid[i][j]) {
                    matrix[i][j] = new NeighbourlyPoint(i, j);

                    if (leftNeighbour > -1) {
                        matrix[i][j].left = matrix[leftNeighbour][j];
                        matrix[leftNeighbour][j].right = matrix[i][j];
                    }

                    if (lowestPoint[i] > -1) {
                        matrix[i][j].top = matrix[i][lowestPoint[i]];
                        matrix[i][lowestPoint[i]].bottom = matrix[i][j];
                    }

                    lowestPoint[i] = j;
                    leftNeighbour = i;
                }
            }
        }

        return matrix;
    }

    private static int[][] computeCriticality(NeighbourlyPoint[][] grid) {
        int width = grid.length, height = grid[0].length;
        int[][] matrix = new int[width][height];

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                NeighbourlyPoint p = grid[i][j];

                if (grid[i][j] != null) {
                    // top left
                    if (p.left != null && p.top != null && (p.left.top == null || p.left.top.y < p.top.y)) {
                        matrix[i][j]++;
                    }
                    // top right
                    if (p.right != null && p.top != null && (p.right.top == null || p.right.top.y < p.top.y)) {
                        matrix[i][j]++;
                    }
                    // bottom left
                    if (p.left != null && p.bottom != null && (p.left.bottom == null || p.left.bottom.y > p.bottom.y)) {
                        matrix[i][j]++;
                    }
                    // bottom right
                    if (p.right != null && p.bottom != null && (p.right.bottom == null || p.right.bottom.y > p.bottom.y)) {
                        matrix[i][j]++;
                    }
                } else {
                    matrix[i][j] = -1;
                }
            }
        }

        return matrix;
    }

    private static class NeighbourlyPoint {

        int x, y;
        NeighbourlyPoint left, right, bottom, top;

        public NeighbourlyPoint(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
