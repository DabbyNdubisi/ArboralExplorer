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
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class RandomMinimal {

    private static final Random rand = new Random();
    
    /**
     * Returns a superset of the given grid without ASS-violations.
     *
     * TODO: maintain the neighbourly grid and criticality, instead of recomputing every round
     * 
     * @param grid
     * @return
     */
    public static GridSet solve(GridSet grid) {
        List<Pair<Integer,Integer>> groundSet = getGroundSetPoints(grid);
        boolean[][] newGrid = new boolean[grid.getWidth()][grid.getHeight()];
        
        // Add all points
        for (boolean[] newGridColumn : newGrid) {
            Arrays.fill(newGridColumn, true);
        }
        
        List<Pair<Integer,Integer>> subCriticalPoints = ArboralChecker.getAllSubCriticalPoints(newGrid);
        subCriticalPoints.removeAll(groundSet);
        
        while (!subCriticalPoints.isEmpty()) {
            // Remove a random subcritical point
            Pair<Integer, Integer> toRemove = subCriticalPoints.get(rand.nextInt(subCriticalPoints.size()));
            newGrid[toRemove.getFirst()][toRemove.getSecond()] = false;
            
            subCriticalPoints = ArboralChecker.getAllSubCriticalPoints(newGrid);
            subCriticalPoints.removeAll(groundSet);
        }
        
        return new GridSet(newGrid, grid.getGroundSet());
    }
    
    public static List<Pair<Integer,Integer>> getGroundSetPoints(GridSet grid) {
        List<Pair<Integer,Integer>> groundSetPoints = new ArrayList<>(grid.getGroundSetSize());
        
        for (int i = 0; i < grid.getWidth(); i++) {
            for (int j = 0; j < grid.getHeight(); j++) {
                if (grid.isGroundSet(i, j)) {
                    groundSetPoints.add(new Pair<>(i, j));
                }
            }
        }
        
        return groundSetPoints;
    }
}
