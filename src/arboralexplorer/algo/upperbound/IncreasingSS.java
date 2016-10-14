/*
 * Copyright 2016 ingo.
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
import arboralexplorer.data.GridSet;
import static java.lang.Integer.min;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author ingo
 */
public class IncreasingSS {

    /**
     * Computes the longest increasing subsequence, splits the input and
     * recurses.
     *
     * @param grid
     * @return
     */
    public static GridSet solve(GridSet grid) {
        
        LISS(grid);
        
        int width = grid.getWidth(), height = grid.getHeight();
        boolean[][] newGrid = new boolean[width][height];

        //return new GridSet(newGrid, grid.getGroundSet());
        return grid;
    }

    public static int[] LISS(GridSet grid) {

        int[] input = new int[grid.getHeight()];
        int[] map = new int[grid.getHeight()];
        int[] mapinv = new int[grid.getHeight()];
        int count = 0;

        // Convert Ground set to a sequence while creating a map that maps
        // row indices in grid-space to non-empty row grid-space.
        for (int j = 0; j < grid.getHeight(); j++) {
            for (int i = 0; i < grid.getWidth(); i++) {
                mapinv[j] = count;
                map[count] = j;
                if (grid.isGroundSet(i, j)) {
                    input[j] = i;
                    count++;
                    break;
                }
            }
        }
        
        System.out.print(Arrays.toString(map) + "\n");
        System.out.print(Arrays.toString(mapinv) + "\n");

        // Create the sequence based on non-empty rows only.
        int[] seq = new int[count];
        for (int j = 0; j < count; j++) {
            seq[j] = input[map[j]];
        }

        int m = seq.length;
        int n = 0;

        // The maximum value in the sequence is an upper bound for the length of
        // the longest (strictly) increasing subsequence.
        for (int i = 0; i < m; i++) {
            n = Integer.max(n, seq[i]);
        }
        int[] lss = new int[Integer.min(n, m)];
        int[] ptrs = new int[m];

        int max = 1;
        lss[0] = 0;
        ptrs[0] = -1;
        for (int j = 1; j < m; j++) {
            for (int i = 0; i < max; i++) {
                if (seq[j] < seq[lss[i]]) {
                    // Smaller ending element found, two cases:
                    if (i == 0) {
                        // First element of sequence, no predecessor
                        ptrs[j] = -1;
                    } else {
                        // Non-starting element, set its predecessor
                        ptrs[j] = lss[i - 1];
                    }
                    // The sequence of length i ends in value with index j
                    lss[i] = j;
                    //System.out.print("New smallest end point.\n");
                    break;
                }
            }
            // The value with index j is larger than anything so far.
            // A subsequence of length max + 1 has been found.
            if (seq[j] > seq[lss[max - 1]]) {
                ptrs[j] = lss[max - 1];
                lss[max] = j;
                max += 1;
                //System.out.print("New longest subsequence of length " + max + ".\n");
            }
            //System.out.print(Arrays.toString(lss) + "\n");
            //System.out.print(Arrays.toString(ptrs) + "\n");
        }

        // Reconstruct an actual longest subsequence.
        int ptr = lss[max - 1];
        List<Integer> longest = new ArrayList();
        while (ptr != -1) {
            longest.add(ptr);
            ptr = ptrs[ptr];
        }
        Collections.reverse(longest);
        System.out.print(longest + "\n");

        // Map the indices back to original grid space.
        int[] output = new int[longest.size()];
        for(int j = 0; j < longest.size(); j ++) {
            output[j] = map[longest.get(j)];
        }
        
        System.out.print(Arrays.toString(output) + "\n");
        
        return output;
    }
}
