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
package arboralexplorer.data;

import arboralexplorer.Pair;
import arboralexplorer.algo.ArboralChecker;
import java.util.List;

public class GridSet {

    private final boolean[][] gridSet;
    private final boolean[][] groundSet;
    private List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> violations = null;

    public GridSet(boolean[][] groundSet) {
        this(groundSet, groundSet);
    }

    /**
     * Creates a new GridSet with the given grid and ground sets.
     *
     * @param gridSet
     * @param groundSet
     * @throws IllegalArgumentException if the dimensions of the grids are zero,
     * or don't match, or if the ground set is not a subset of the grid set.
     */
    public GridSet(boolean[][] gridSet, boolean[][] groundSet) {
        if (gridSet.length == 0 || gridSet[0].length == 0) {
            throw new IllegalArgumentException("Grid must have non-zero size.");
        }
        if (gridSet.length != groundSet.length || gridSet[0].length != groundSet[0].length) {
            throw new IllegalArgumentException("Grid set and ground set must have the same size.");
        }
        for (int i = 0; i < gridSet.length; i++) {
            for (int j = 0; j < gridSet[0].length; j++) {
                if (groundSet[i][j] && !gridSet[i][j]) {
                    throw new IllegalArgumentException("Ground set must be a subset of the grid set.");
                }
            }
        }

        this.gridSet = copyGrid(gridSet);
        this.groundSet = copyGrid(groundSet);
    }

    /**
     * Returns the width of the grid. The first coordinate can take values in
     * [0, width).
     *
     * @return
     */
    public int getWidth() {
        return gridSet.length;
    }

    /**
     * Returns the height of the grid. The second coordinate can take values in
     * [0, height).
     *
     * @return
     */
    public int getHeight() {
        return gridSet[0].length;
    }

    /**
     * Adds the point (i, j) to the grid set. Has no effect if the point is
     * already there.
     *
     * @param i
     * @param j
     */
    public void addPoint(int i, int j) {
        if (!gridSet[i][j]) {
            invalidate();
        }

        gridSet[i][j] = true;
    }

    /**
     * Removes the point (i, j) from the grid and ground set. Has no effect if
     * the point is not there
     *
     * @param i
     * @param j
     */
    public void removePoint(int i, int j) {
        if (gridSet[i][j]) {
            invalidate();
        }

        gridSet[i][j] = false;
        groundSet[i][j] = false;
    }

    /**
     * Checks whether the point (i, j) is in the grid set.
     *
     * @param i
     * @param j
     * @return
     */
    public boolean hasPoint(int i, int j) {
        return gridSet[i][j];
    }

    /**
     * Adds the point (i, j) to both the grid and ground set. Has no effect if
     * the point is already there.
     *
     * @param i
     * @param j
     */
    public void addToGroundSet(int i, int j) {
        if (!gridSet[i][j]) {
            invalidate();
        }

        gridSet[i][j] = true;
        groundSet[i][j] = true;
    }

    /**
     * Removes the point (i, j) from the ground set. Has no effect if the point
     * is not in the ground set.
     *
     * @param i
     * @param j
     */
    public void removeFromGroundSet(int i, int j) {
        groundSet[i][j] = false;
    }

    /**
     * Checks whether the point (i, j) is in the ground set.
     *
     * @param i
     * @param j
     * @return
     */
    public boolean isGroundSet(int i, int j) {
        return groundSet[i][j];
    }

    /**
     * Returns a copy of the grid set. This is expensive and should only be used
     * when regular access methods do not suffice.
     *
     * @return
     */
    public boolean[][] getGridSet() {
        return copyGrid(gridSet);
    }

    /**
     * Returns a copy of the ground set. This is expensive and should only be
     * used when regular access methods do not suffice.
     *
     * @return
     */
    public boolean[][] getGroundSet() {
        return copyGrid(groundSet);
    }

    /**
     * Returns a list of all ASS violations in the current grid set.
     *
     * @return
     */
    public List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> getViolations() {
        if (violations == null) {
            violations = ArboralChecker.getAllAssViolations(this);
        }

        return violations;
    }

    private void invalidate() {
        violations = null;
    }

    private boolean[][] copyGrid(boolean[][] grid) {
        if (grid.length == 0) {
            return new boolean[0][0];
        } else {
            boolean[][] newGrid = new boolean[grid.length][grid[0].length];

            for (int i = 0; i < grid.length; i++) {
                System.arraycopy(grid[i], 0, newGrid[i], 0, grid[0].length);
            }

            return newGrid;
        }
    }
}
