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
import arboralexplorer.algo.ArboralChecker;
import arboralexplorer.data.GridSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StupidOpt {

    /**
     * Returns a superset of the given grid without ASS-violations. This is
     * super slow, as it tries ALL supersets.
     *
     * @param grid
     * @return
     */
    public static GridSet solve(GridSet grid) {
        Pair<boolean[][], Integer> minAss = getMinimumASS(grid.getGroundSet(), grid.getGroundSet(), 0, 0, 0, ArboralChecker.getAllAssViolations(grid));
        return new GridSet(minAss.getFirst(), grid.getGroundSet());
    }

    private static Pair<boolean[][], Integer> getMinimumASS(boolean[][] groundSet, boolean[][] newGrid, int addedPoints, int i, int j, List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> violations) {
        if (violations.isEmpty() && ArboralChecker.isArborallySatisfied(newGrid)) {
            return new Pair<>(GridSet.copyGrid(newGrid), addedPoints);
        }
        if (i == groundSet.length - 1 && j == groundSet[0].length - 1) {
            return new Pair<>(null, Integer.MAX_VALUE);
        }

        int nextI = (i == groundSet.length - 1 ? 0 : i + 1);
        int nextJ = (i == groundSet.length - 1 ? j + 1 : j);

        if (groundSet[i][j]) {
            return getMinimumASS(groundSet, newGrid, addedPoints, nextI, nextJ, violations);
        }

        Pair<boolean[][], Integer> minAssWithout = getMinimumASS(groundSet, newGrid, addedPoints, nextI, nextJ, violations);

        // Add the point (i, j)
        newGrid[i][j] = true;

        List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> satisfiedViolations = new ArrayList<>();

        for (Iterator<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> iterator = violations.iterator(); iterator.hasNext();) {
            Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> violation = iterator.next();

            int x1 = violation.getFirst().getFirst();
            int y1 = violation.getFirst().getSecond();
            int x2 = violation.getSecond().getFirst();
            int y2 = violation.getSecond().getSecond();

            if (Math.min(x1, x2) <= i && i <= Math.max(x1, x2) && Math.min(y1, y2) <= j && j <= Math.max(y1, y2)) {
                satisfiedViolations.add(violation);
                iterator.remove();
            }
        }

        Pair<boolean[][], Integer> minAssWith = getMinimumASS(groundSet, newGrid, addedPoints + 1, nextI, nextJ, violations);

        // Restore state
        newGrid[i][j] = false;
        violations.addAll(satisfiedViolations);

        if (minAssWith.getSecond() < minAssWithout.getSecond()) {
            return minAssWith;
        } else {
            return minAssWithout;
        }
    }
}
