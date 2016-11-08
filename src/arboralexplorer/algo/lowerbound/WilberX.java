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
import arboralexplorer.data.WilberData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WilberX {

    /**
     * Assumption: 1 ground set point per row.
     *
     * @param grid
     * @return
     */
    public static GridSet solve(GridSet grid) {
        boolean[][] newGrid = grid.getGroundSet();

        WilberData wilber = new WilberData(newGrid);

        int[] queries = getQueries(grid);

        solveRecursive(wilber, queries, invertPermutation(queries), 0, grid.getWidth() - 1, 0, grid.getHeight() - 1, false);

        GridSet newGridSet = new GridSet(wilber.getGrid(), grid.getGroundSet());
        newGridSet.setWilberData(wilber);

        return newGridSet;
    }

    private static void solveRecursive(WilberData wilber, int[] queries, int[] invertq, int left, int right, int bot, int top, boolean invert) {
        //System.out.printf("sr (%d, %d, %d, %d)%n", left, right, bot, top);

        if (bot >= top) {
            return;
        }

//        if (left >= right) {
//            return;
//        }

        if (countRange(queries, left, right, bot, top) <= 0) {
            return;
        }

        int mid = median(queries, left, right, bot, top);
        //mid = right - 1;

        boolean newPoint = false;
        Side previousSide = null;
        int prevCoord = 0;

        for (int j = bot; j <= top; j++) {
            int queryCoord = queries[j];

            if (queryCoord < left || queryCoord > right) {
                continue;
            }

            Side currentSide = Side.of(mid, queryCoord);

            if (previousSide != null && previousSide != currentSide) {
                // Add both points on the line
                wilber.setGridPoint(mid, prevCoord, invert);
                wilber.setGridPoint(mid, j, invert);
                newPoint = true;
            }

            previousSide = currentSide;
            prevCoord = j;
        }

        if (newPoint) {
            int topBounded = top + 1;
            int botBounded = bot - 1;
            if (top + 1 < queries.length) {
                wilber.addHub(mid, top + 1, invert);
            } else {
                topBounded--;
            }
            if (bot - 1 > 0) {
                wilber.addHub(mid, bot - 1, invert);
            } else {
                botBounded++;
            }
            wilber.addLine(mid, topBounded, botBounded, invert);
        }

        if (countRange(queries, left, right, bot, top) == 1) {
            int only = firstInRange(queries, left, right, bot, top);
            if (top + 1 < queries.length) {
                wilber.setGridPoint(queries[only], top + 1, invert);
            }
            if (bot - 1 > 0) {
                wilber.setGridPoint(queries[only], bot - 1, invert);
            }
            if (right + 1 < queries.length) {
                wilber.setGridPoint(right + 1, only, invert);
            }
            if (left - 1 > 0) {
                wilber.setGridPoint(left - 1, only, invert);
            }
        }

//        solveRecursive(wilber, queries, invertq, left, mid - 1, bot, top, invert);
//        solveRecursive(wilber, queries, invertq, mid + 1, right, bot, top, invert);
        solveRecursive(wilber, invertq, queries, bot, top, left, mid - 1, !invert);
        solveRecursive(wilber, invertq, queries, bot, top, mid + 1, right, !invert);
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

    private static int[] invertPermutation(int[] permutation) {
        int[] invert = new int[permutation.length];

        for (int j = 0; j < permutation.length; j++) {
            invert[permutation[j]] = j;
        }

        return invert;
    }

    private static int firstInRange(int[] arr, int l, int r, int b, int t) {
        for (int i = b; i <= t; i++) {
            if (arr[i] < l || arr[i] > r) {
                continue;
            }
            return i;
        }
        return -1;
    }

    private static int median(int[] arr, int l, int r, int b, int t) {
        int i = b;
        List<Integer> coords = new ArrayList();
        for (; i <= t; i++) {

            if (arr[i] < l || arr[i] > r) {
                continue;
            }
            coords.add(arr[i]);
        }
        Collections.sort(coords);
        return coords.get(coords.size() / 2);
    }

    private static int countRange(int[] arr, int l, int r, int b, int t) {
        int c = 0;
        for (int i = b; i <= t; i++) {
            if (arr[i] < l || arr[i] > r) {
                continue;
            }
            c++;
        }
        return c;
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
