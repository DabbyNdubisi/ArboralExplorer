/*
 * Copyright 2016 dabeluchindubisi.
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
import arboralexplorer.SplayTree;
import arboralexplorer.BTNode;
import arboralexplorer.BinarySearchTree;
import arboralexplorer.Pair;
import java.util.ArrayList;
/**
 *
 * @author dabeluchindubisi
 * 
 * - Begin with the same tree that GreedyFuture(GreedyASS) begins with.
 * - ASS-solution is given by searching the intrinsic splaytree, and
 *   keeping track of accesses.
 */
public class SplayTreeSolver {
    /**
     * Returns a superset of the given grid without ASS-violations.
     *
     * @param grid
     * @return
     */
    public static Pair<GridSet, ArrayList<BinarySearchTree>> solve(GridSet grid, boolean trackPath) {
        int n = grid.getWidth();
        int m = grid.getHeight();
        boolean[][] newGrid = new boolean[n][m];
        ArrayList<BinarySearchTree> trees = new ArrayList<>();

        // Start with balanced Splay tree
        //SplayTree tree = new SplayTree(0, n);
        
        // Start with tree with same representation
        // as GreedyFuture
        SplayTree tree = SplayTree.SplayTreeFromGridSet(grid);
        trees.add(tree.geometricCopy());
        for(int i = 0; i < m; i++) {
            for(int j = 0; j < n; j++) {
                if(grid.isGroundSet(j, i)) {
                    ArrayList<Integer> accesses = getAccesses(tree, j);
                    for(int access : accesses) {
                        newGrid[access][i] = true;
                    }
                    
                    if(trackPath) {
                        // copy current splay tree
                        trees.add(tree.geometricCopy());
                    }
                }
            }
        }
        
        return new Pair<>(new GridSet(newGrid, grid.getGroundSet()), trees);
    }
    
    /**
     * Returns the list of elements accessed while accessing `x`
     *
     * @param x
     * @return
     */
    private static ArrayList<Integer> getAccesses(SplayTree tree, int x) {
        ArrayList<Integer> accesses = new ArrayList<>();
        for(BTNode node : tree.findPath(x)) {
            accesses.add(node.value);
        }
        return accesses;
    }
}
