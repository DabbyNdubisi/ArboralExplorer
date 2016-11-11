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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
/**
 *
 * @author dabeluchindubisi
 * 
 * - Begin with the same tree that GreedyFuture(GreedyASS) begins with.
 * - ASS-solution is given by searching the intrinsic splaytree, and
 *   keeping track of accesses.
 */
public class SplayTree {
    /**
     * Returns a superset of the given grid without ASS-violations.
     *
     * @param grid
     * @return
     */
    public static GridSet solve(GridSet grid) {
        int n = grid.getWidth();
        int m = grid.getHeight();
        boolean[][] newGrid = new boolean[n][m];

        // Start with balanced Splay tree
        SplayTree tree = new SplayTree(0, n);
        
        for(int i = 0; i < m; i++) {
            for(int j = 0; j < n; j++) {
                if(grid.isGroundSet(j, i)) {
                    ArrayList<Integer> accesses = tree.getAccesses(j);
                    for(int access : accesses) {
                        newGrid[access][i] = true;
                    }
                }
            }
        }
        
        return new GridSet(newGrid, grid.getGroundSet());
    }
    
    private ArrayList<Integer> getAccesses(int x) {
        ArrayList<Integer> accesses = new ArrayList<>();
        for(BTNode node : findPath(x)) {
            accesses.add(node.value);
        }
        return accesses;
    }
    
    /** SplayTree implementation Begins Here **/
    private static class BTNode {
        public BTNode left;
	public BTNode right;
	public BTNode parent;
        public int value;
        
        public BTNode(int x) {
            left = null;
            right = null;
            parent = null;
            value = x;
        }
    }
    
    private BTNode root;
    
    private SplayTree() {
        this.root = null;
    }
    
    private SplayTree(int lb, int ub) {
        this.root = constructBalancedTree(lb, ub);
    }
    
    private BTNode constructBalancedTree(int lb, int ub) {
        if(ub == lb)
            return new BTNode(ub);
        
        int mid = (lb + ub)/2;
        BTNode r = new BTNode(mid);
        r.left = constructBalancedTree(lb, mid);
        r.right = constructBalancedTree(mid+1, ub);
        if(r.left != null)
            r.left.parent = r;
        if(r.right != null)
            r.right.parent = r;
        return r;
    }
    
    /**
     * Returns the node whose value equals `x`
     * If `x` is not contained in the tree, the
     * potential parent of `x` is returned
     * @param x: the value to search for
     * @return: Node n where n.value == `x` or
     *          n.left.value == `x` || n.right.value == `x`
     */
    private BTNode find(int x) {
        return find(x, true);
    }
    
    
    private ArrayList<BTNode> findPath(int x) {
        ArrayList<BTNode> path = new ArrayList<>();
        BTNode curr = root, prev = null;
        if(curr == null)
            return path;
        
        while(curr != null) {
            path.add(curr);
            if(curr.value != x) {
                curr = (curr.value < x) ? curr.right : curr.left;
            } else {
                break;
            }
        }
        
        splay(path.get(path.size()-1));
        return path;
    }
    
    /**
     * Returns the node whose value equals `x`
     * If `x` is not contained in the tree, the
     * potential parent of `x` is returned
     * @param x: the value to search for
     * @param shouldSplay: whether or not to splay after find
     * @return: Node n where n.value == `x` or
     *          n.left.value == `x` || n.right.value == `x`
     */
    private BTNode find(int x, boolean shouldSplay) {
        BTNode curr = root, prev = null;
        if(curr == null)
            return null;
        
        while(curr != null) {
            if(curr.value != x) {
                prev = curr;
                curr = (curr.value < x) ? curr.right : curr.left;
            } else {
                break;
            }
        }
        
        if(curr == null) {
            if(shouldSplay) { splay(prev); }
            return prev;
        } else {
            if(shouldSplay) { splay(curr); }
            return curr;
        }
    }
    
    /**
     * Inserts `x` into the tree
     * @param x
     */
    private void insert(int x) {
        BTNode node = find(x, false);
        if(node == null) {
            root = new BTNode(x);
        } else if (node.value != x) {
            // execute insert
            BTNode newNode = new BTNode(x);
            if(node.value < x) { node.right = newNode; }
            else { node.left = newNode; }
            newNode.parent = node;
            splay(newNode);
        }
    }
    
    /**
     * Bottom Up Algorithm
     * Splays `node` to the root
     * @param x 
     */
    private void splay(BTNode node) {
        BTNode curr = node;
        if(curr == null) return;
        
        // Bottom-up splay
        while(curr.parent != null) {
            if(curr.parent.value > curr.value) {
                rotateRight(curr.parent); // zig
                // curr.parent(after rotate) = curr.parent.parent (before rotate)
                // curr.right(after rotate) = curr.parent (before rotate)
                if(curr.parent != null) {
                    if(curr.parent.value < curr.right.value)
                        rotateLeft(curr.parent); // zag
                    else
                        rotateRight(curr.parent); // zig
                }                
            } else {
                rotateLeft(curr.parent); // zag
                // curr.parent(after rotate) = curr.parent.parent (before rotate)
                // curr.left(after rotate) = curr.parent (before rotate)
                if(curr.parent != null) {
                    if(curr.parent.value < curr.left.value)
                        rotateLeft(curr.parent); // zag
                    else
                        rotateRight(curr.parent); // zig
                }
            }
        }
        root = curr;
    }
    
    /**
     * Rotates `node` such that `node`.parent (before rotate)
     * equals `node`.left (after rotate)
     * @param node
     */
    private void rotateLeft(BTNode node) {
        if(node == null)
            return;
        
        BTNode newRoot = node.right;
        if(newRoot != null) {
            node.right = newRoot.left;
            if(newRoot.left != null)
                newRoot.left.parent = node;
            newRoot.left = node;
            newRoot.parent = node.parent;
        }
     
        if(node.parent != null) {
            if(node.parent.left == node)
                node.parent.left = newRoot;
            else
                node.parent.right = newRoot;
        }
        node.parent = newRoot;
    }
    
    /**
     * Rotates `node` such that `node`.parent (before rotate)
     * equals `node`.right (after rotate)
     * @param node
     */
    private void rotateRight(BTNode node) {
        if(node == null)
            return;
        
        BTNode newRoot = node.left;
        if(newRoot != null) {
            node.left = newRoot.right;
            if(newRoot.right != null)
                newRoot.right.parent = node;
            newRoot.right = node;
            newRoot.parent = node.parent;
        }
        
        if(node.parent != null) {
            if(node.parent.left == node)
                node.parent.left = newRoot;
            else
                node.parent.right = newRoot;
        }
        node.parent = newRoot;
    }
    
}
