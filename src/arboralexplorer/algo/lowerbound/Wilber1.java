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
package arboralexplorer.algo.lowerbound;

import arboralexplorer.data.GridSet;

public class Wilber1 {

    /**
     * Assumption: 1 ground set point per row.
     *
     * @param grid
     * @return
     */
    public static GridSet solve(GridSet grid) {
        boolean[][] newGrid = grid.getGroundSet();

        solveRecursive(grid, getQueries(grid), newGrid, 0, grid.getWidth() - 1);

        return new GridSet(newGrid, grid.getGroundSet());
    }

    private static void solveRecursive(GridSet grid, int[] queries, boolean[][] newGrid, int left, int right) {
        System.out.printf("sr (%d, %d)%n", left, right);

        if (left >= right) {
            return;
        }

        int mid = (left + right) / 2;
        Side previousSide = null;
        int prevY = 0;

        for (int j = 0; j < queries.length; j++) {
            int queryX = queries[j];

            if (queryX < left || queryX > right) {
                continue;
            }

            Side currentSide = Side.of(mid, queryX);

            if (previousSide != null && previousSide != currentSide) {
                // Add both points on the line
                newGrid[mid][prevY] = true;
                newGrid[mid][j] = true;
            }

            previousSide = currentSide;
            prevY = j;
        }

        // Recurse
        solveRecursive(grid, queries, newGrid, left, mid - 1);
        solveRecursive(grid, queries, newGrid, mid + 1, right);
    }

    private static int[] getQueries(GridSet grid) {
        int[] input = new int[grid.getHeight()];

        // Convert Ground set to a sequence while creating a map that maps
        // row indices in grid-space to non-empty row grid-space.
        for (int j = 0; j < grid.getHeight(); j++) {
            for (int i = 0; i < grid.getWidth(); i++) {
                if (grid.isGroundSet(i, j)) {
                    input[j] = i;
                    break;
                }
            }
        }

        return input;
    }

    private enum Side {

        LEFT, RIGHT, ON;

        public static Side of(int line, int x) {
            if (x == line) {
                return ON;
            } else {
                return (x < line ? LEFT : RIGHT);
            }
        }
    }
}
