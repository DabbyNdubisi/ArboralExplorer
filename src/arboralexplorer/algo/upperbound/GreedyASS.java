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

public class GreedyASS {

    /**
     * Returns a superset of the given grid without ASS-violations.
     *
     * @param grid
     * @return
     */
    public static GridSet solve(GridSet grid) {
        int width = grid.getWidth(), height = grid.getHeight();
        boolean[][] newGrid = new boolean[width][height];

        int[] lowestPoint = new int[width];
        Arrays.fill(lowestPoint, -1);

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                if (grid.isGroundSet(i, j)) {
                    newGrid[i][j] = true;

                    // Scan left
                    int lowest = lowestPoint[i];

                    if (lowest != j + 1) {
                        for (int k = i - 1; k >= 0 && !grid.isGroundSet(k, j); k--) {
                            if (lowestPoint[k] > lowest) {
                                lowest = lowestPoint[k];
                                newGrid[k][j] = true;
                                lowestPoint[k] = j;
                            }
                        }
                    }

                    // Scan right
                    lowest = lowestPoint[i];

                    if (lowest != j + 1) {
                        for (int k = i + 1; k < width && !grid.isGroundSet(k, j); k++) {
                            if (lowestPoint[k] > lowest) {
                                lowest = lowestPoint[k];
                                newGrid[k][j] = true;
                                lowestPoint[k] = j;
                            }
                        }
                    }

                    lowestPoint[i] = j;
                }
            }
        }

        return new GridSet(newGrid, grid.getGroundSet());
    }
}
