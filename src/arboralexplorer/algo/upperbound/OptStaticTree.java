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

import arboralexplorer.data.GridSet;
import java.util.Arrays;

public class OptStaticTree {

    /**
     * Returns a superset of the given grid without ASS-violations.
     *
     * @param grid
     * @return
     */
    public static GridSet solve(GridSet grid) {
        return (new OptStaticTree(grid)).computeBestTree();
    }

    private static final int NOT_COMPUTED = -1;

    private GridSet grid;
    private int[][] bestCostForSubtree; // contains elements in [i, j)
    private int[][] bestRootForSubtree;
    private int[][] rootCostForSubtree; // Number of times at which elements in this subtree are accessed

    private OptStaticTree(GridSet grid) {
        this.grid = grid;
        bestCostForSubtree = new int[grid.getWidth() + 1][grid.getWidth() + 1];
        bestRootForSubtree = new int[grid.getWidth() + 1][grid.getWidth() + 1];
        rootCostForSubtree = new int[grid.getWidth() + 1][grid.getWidth() + 1];

        int[] frequency = new int[grid.getWidth()]; // The number of times each point is queried

        for (int i = 0; i < grid.getWidth(); i++) {
            for (int j = 0; j < grid.getHeight(); j++) {
                if (grid.isGroundSet(i, j)) {
                    frequency[i]++;
                }
            }
        }

        for (int i = 0; i < grid.getWidth(); i++) {
            Arrays.fill(bestCostForSubtree[i], NOT_COMPUTED);
            bestCostForSubtree[i][i] = 0;
            bestCostForSubtree[i][i + 1] = frequency[i];

            Arrays.fill(bestRootForSubtree[i], NOT_COMPUTED);
            bestRootForSubtree[i][i + 1] = i;

            rootCostForSubtree[i][i] = 0;
            rootCostForSubtree[i][i + 1] = frequency[i];
        }

        bestCostForSubtree[grid.getWidth()][grid.getWidth()] = 0;

        for (int lower = 0; lower < grid.getWidth(); lower++) {
            for (int upper = lower + 2; upper <= grid.getWidth(); upper++) {
                for (int j = 0; j < grid.getHeight(); j++) {
                    for (int i = lower; i < upper; i++) {
                        if (grid.isGroundSet(i, j)) {
                            rootCostForSubtree[lower][upper]++;
                            break; // Go to the next row
                        }
                    }
                }
            }
        }

        for (int length = 2; length <= grid.getWidth(); length++) {
            for (int i = 0; i <= grid.getWidth() - length; i++) {
                computeBestCost(i, i + length);
            }
        }
    }

    private void computeBestCost(int lower, int upper) {
        int minCost = Integer.MAX_VALUE;
        int bestRoot = NOT_COMPUTED;

        // Pick the best root
        for (int root = lower; root < upper; root++) {
            if (bestCostForSubtree[lower][root] == NOT_COMPUTED) {
                System.err.println("Shouldn't happen!");
                computeBestCost(lower, root);
            }
            if (bestCostForSubtree[root + 1][upper] == NOT_COMPUTED) {
                System.err.println("Shouldn't happen!");
                computeBestCost(root + 1, upper);
            }

            int cost = bestCostForSubtree[lower][root] + bestCostForSubtree[root + 1][upper];

            if (cost < minCost) {
                minCost = cost;
                bestRoot = root;
            }
        }

        bestCostForSubtree[lower][upper] = rootCostForSubtree[lower][upper] + minCost;
        bestRootForSubtree[lower][upper] = bestRoot;
    }

    private GridSet computeBestTree() {
        boolean[][] newGrid = new boolean[grid.getWidth()][grid.getHeight()];

        markTree(newGrid, 0, grid.getWidth());

        return new GridSet(newGrid, grid.getGroundSet());
    }

    private void markTree(boolean[][] newGrid, int lower, int upper) {
        // Mark the root
        int root = bestRootForSubtree[lower][upper];

        for (int j = 0; j < grid.getHeight(); j++) {
            for (int i = lower; i < upper; i++) {
                if (grid.isGroundSet(i, j)) {
                    newGrid[root][j] = true;
                    break; // Go to the next row
                }
            }
        }

        if (upper - lower > 1) {
            // Mark subtrees
            markTree(newGrid, lower, root);
            markTree(newGrid, root + 1, upper);
        }
    }
}
