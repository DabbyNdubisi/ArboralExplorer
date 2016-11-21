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

import arboralexplorer.Line;
import arboralexplorer.Pair;
import arboralexplorer.algo.ArboralChecker;
import arboralexplorer.algo.GridSetWorker;
import arboralexplorer.data.GridSet;
import arboralexplorer.gui.DrawPanel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class computes a smallest superset of the given grid without
 * ASS-violations.
 *
 * This is super slow, as it tries ALL supersets (modulo a little pruning).
 */
public class StupidOpt extends GridSetWorker {

    private static final int COUNT_DEPTH = 15;
    private final int totalSubsets;
    private final int totalGridSize;
    private final int maxNonGroundPos;
    private final int[] posToNonGroundPos;
    private int handled = 0;

    public StupidOpt(DrawPanel drawPanel, GridSet inputGrid) {
        super(drawPanel, inputGrid);
        totalGridSize = inputGrid.getWidth() * inputGrid.getHeight();

        posToNonGroundPos = new int[totalGridSize];

        int nonGroundPos = 0;

        for (int p = 0; p < totalGridSize; p++) {
            int i = p % inputGrid.getWidth();
            int j = p / inputGrid.getWidth();

            posToNonGroundPos[p] = nonGroundPos;

            if (!inputGrid.isGroundSet(i, j)) {
                nonGroundPos++;
            }
        }

        totalSubsets = (int) Math.pow(2, Math.min(COUNT_DEPTH, nonGroundPos));
        maxNonGroundPos = nonGroundPos - 1;
    }

    @Override
    protected GridSet doInBackground() throws Exception {
        boolean[][] groundSetCopy = inputGrid.getGroundSet();

        // Compute a reasonable upper bound
        GridSet greedySolution = GreedyASS.solve(new GridSet(groundSetCopy));
        int greedy = greedySolution.getSize() - greedySolution.getGroundSetSize();
        publish(greedySolution);

        // Compute the optimum
        boolean[][] workingCopy = GridSet.copyGrid(groundSetCopy);
        Pair<boolean[][], Integer> minAss = getMinimumASS(groundSetCopy, workingCopy, 0, greedy, 0, ArboralChecker.getAllAssViolations(inputGrid));

        if (minAss.getSecond() == Integer.MAX_VALUE) {
            // Greedy was optimal
            return greedySolution;
        } else {
            return new GridSet(minAss.getFirst(), groundSetCopy);
        }
    }

    private Pair<boolean[][], Integer> getMinimumASS(boolean[][] groundSet, boolean[][] newGrid, int addedPoints, int bestBound, int pos, List<Line> violations) {
        if (isCancelled()) {
            return new Pair<>(null, Integer.MAX_VALUE);
        }
        if (addedPoints >= bestBound) {
            updateProgress(pos, true);
            return new Pair<>(null, Integer.MAX_VALUE);
        }
        if (violations.isEmpty() && ArboralChecker.isArborallySatisfied(newGrid)) {
            updateProgress(pos, true);

            if (addedPoints < bestBound) {
                System.out.println("Publishing new solution of size " + addedPoints);
                publish(new GridSet(newGrid, groundSet));
                return new Pair<>(GridSet.copyGrid(newGrid), addedPoints);
            } else {
                return new Pair<>(null, Integer.MAX_VALUE);
            }
        }
        if ((pos == totalGridSize - 1) || addedPoints == bestBound - 1) {
            updateProgress(pos, true);
            return new Pair<>(null, Integer.MAX_VALUE);
        }

        int i = pos % newGrid.length;
        int j = pos / newGrid.length;

        if (groundSet[i][j]) {
            return getMinimumASS(groundSet, newGrid, addedPoints, bestBound, pos + 1, violations);
        }

        Pair<boolean[][], Integer> minAssWithout = getMinimumASS(groundSet, newGrid, addedPoints, bestBound, pos + 1, violations);

        if (isCancelled()) {
            return new Pair<>(null, Integer.MAX_VALUE);
        }

        // Add the point (i, j)
        newGrid[i][j] = true;

        List<Line> satisfiedViolations = new ArrayList<>();

        for (Iterator<Line> iterator = violations.iterator(); iterator.hasNext();) {
            Line violation = iterator.next();

            int x1 = violation.getFirst().getXInt();
            int y1 = violation.getFirst().getYInt();
            int x2 = violation.getSecond().getXInt();
            int y2 = violation.getSecond().getYInt();

            if (Math.min(x1, x2) <= i && i <= Math.max(x1, x2) && Math.min(y1, y2) <= j && j <= Math.max(y1, y2)) {
                satisfiedViolations.add(violation);
                iterator.remove();
            }
        }

        Pair<boolean[][], Integer> minAssWith = getMinimumASS(groundSet, newGrid, addedPoints + 1, Math.min(bestBound, minAssWithout.getSecond()), pos + 1, violations);

        // Restore state
        newGrid[i][j] = false;
        violations.addAll(satisfiedViolations);

        updateProgress(pos, false);

        if (minAssWith.getSecond() < minAssWithout.getSecond()) {
            return minAssWith;
        } else {
            return minAssWithout;
        }
    }

    private void updateProgress(int pos, boolean pruned) {
        int nonGroundPos = posToNonGroundPos[pos];

        if (pruned && nonGroundPos > COUNT_DEPTH) { // Too deep to count as progress
            return;
        }

        if (!pruned && (maxNonGroundPos <= COUNT_DEPTH || nonGroundPos != COUNT_DEPTH)) {
            return;
        }

        handled += Math.pow(2, Math.min(COUNT_DEPTH, maxNonGroundPos) - nonGroundPos);
        int progress = (100 * handled) / totalSubsets;
        System.out.println("updP. pos: " + pos + " ngPos: " + nonGroundPos + " pruned? " + pruned + " handled: " + handled + " total: " + totalSubsets + " progress: " + progress);
        setProgress(progress);
    }
}
