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
import java.util.ArrayList;
import java.util.List;

public class StaticBalancedTree {

    /**
     * Returns a superset of the given grid without ASS-violations.
     *
     * @param grid
     * @return
     */
    public static GridSet solve(GridSet grid) {
        int n = grid.getWidth();
        boolean[][] newGrid = new boolean[grid.getWidth()][grid.getHeight()];

        for (int i = 0; i < grid.getWidth(); i++) {
            List<Integer> accessed = getAccesses(i, n);

            for (int j = 0; j < grid.getHeight(); j++) {
                if (grid.isGroundSet(i, j)) {
                    for (Integer access : accessed) {
                        newGrid[access][j] = true;
                    }
                }
            }
        }

        return new GridSet(newGrid, grid.getGroundSet());
    }

    private static List<Integer> getAccesses(int query, int n) {
        List<Integer> accesses = new ArrayList<>();

        // The query is in [LB, UB)
        int lowerBound = 0;
        int upperBound = n;
        int root;

        do {
            root = lowerBound + (upperBound - lowerBound - 1) / 2;
            accesses.add(root);

            if (query < root) {
                upperBound = root;
            } else if (query > root) {
                lowerBound = root + 1;
            }
        } while (query != root);

        return accesses;
    }
}
