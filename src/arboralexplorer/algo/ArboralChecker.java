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

    public static List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> getAllAssViolations(GridSet grid) {
        List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> violations = new ArrayList<>();

        int width = grid.getWidth(), height = grid.getHeight();
        int[] lowestPoint = new int[width];
        Arrays.fill(lowestPoint, -1);

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                if (grid.hasPoint(i, j)) {
                    // Scan left
                    int lowest = lowestPoint[i];

                    if (lowest != j + 1) {
                        for (int k = i - 1; k >= 0 && !grid.hasPoint(k, j); k--) {
                            if (lowestPoint[k] > lowest) {
                                violations.add(new Pair<>(new Pair<>(k, lowestPoint[k]), new Pair<>(i, j)));
                                lowest = lowestPoint[k];
                            }
                        }
                    }
                    
                    // Scan right
                    lowest = lowestPoint[i];

                    if (lowest != j + 1) {
                        for (int k = i + 1; k < width && !grid.hasPoint(k, j); k++) {
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
}
