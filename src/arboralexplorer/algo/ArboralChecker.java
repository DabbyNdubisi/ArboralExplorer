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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArboralChecker {

    public static List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> getAllAssViolations(boolean[][] grid) {
        List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> violations = new ArrayList<>();

        if (grid.length == 0 || grid[0].length == 0) {
            return violations;
        }

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

    private static boolean isEmptyRectangle(boolean[][] grid, int i1, int j1, int i2, int j2) {
        if (i1 > i2) {
            return isEmptyRectangle(grid, i2, j2, i1, j1);
        }
        // i1 < i2
        int count = 0;

        for (int i = i1; i <= i2; i++) {
            for (int j = Math.min(j1, j2); j <= Math.max(j1, j2); j++) {
                if (grid[i][j]) {
                    count++;

                    if (count > 2) {
                        return false;
                    }
                }
            }
        }

        return true;
    }
}
