/*
 * Copyright 2016 Dabby Ndubis.
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
import arboralexplorer.Pair;
import java.util.Arrays;

/**
*
* @author Dabby Ndubisi
*/
public class SignedGreedy {
    public static enum Sign {
        Positive, Negative
    }

    /**
     * Returns a superset of the given grid without
     * / (Positive) violations or
     * \ (Negative) violations
     *
     * @param sign
     * @param grid
     * @return
     */
    public static GridSet solve(Sign sign, GridSet grid) {
        int width = grid.getWidth(), height = grid.getHeight();
        boolean[][] newGrid = new boolean[width][height];

        int[] lowestPoint = new int[width];
        Arrays.fill(lowestPoint, -1);

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                if (grid.isGroundSet(i, j)) {
                    newGrid[i][j] = true;

                    int lowest = lowestPoint[i];
                    int k = (sign == Sign.Negative) ? i-1 : i+1; // Scan left or right

                    if (lowest != j + 1) {
                        while(((sign == Sign.Negative) ? k >= 0 : k < width) &&
                            !grid.isGroundSet(k, j)) {
                                if (lowestPoint[k] > lowest) {
                                    lowest = lowestPoint[k];
                                    newGrid[k][j] = true;
                                    lowestPoint[k] = j;
                                }
                                k += (sign == Sign.Negative) ? -1 : 1;
                            }
                    }

                    lowestPoint[i] = j;
                }
            }
        }

        return new GridSet(newGrid, grid.getGroundSet());
    }

    /**
     * Returns a superset of the given grid which is a union of
     * both \ and / signed greedy outputs
     *
     * @param grid
     * @return
     */
	public static GridSet solveSignedUnion(GridSet grid) {

	GridSet negativeSigned = solve(Sign.Negative, grid);
	GridSet positiveSigned = solve(Sign.Positive, grid);

        int width = grid.getWidth(), height = grid.getHeight();
        boolean[][] newGrid = new boolean[width][height];

		for(int j = 0; j < height; j++) {
			for(int i = 0; i < width; i++) {
                            newGrid[i][j] = negativeSigned.hasPoint(i, j) ||
                                    positiveSigned.hasPoint(i, j);
			}
		}

		return new GridSet(newGrid, grid.getGroundSet());
	}
}
