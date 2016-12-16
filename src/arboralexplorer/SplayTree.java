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
package arboralexplorer;

import java.util.ArrayList;
import arboralexplorer.data.GridSet;

/**
 *
 * @author dabeluchindubisi
 * 
 * Implementation of Splay tree that supports
 * only insertions and searching
 */
public class SplayTree extends BinarySearchTree {
    
    public static SplayTree SplayTreeFromGridSet(GridSet grid) {
        SplayTree tree = new SplayTree();
        tree.root = greedyFutureTree(grid);
        return tree;
    }
    
    public static SplayTree SplayTreeFromBalancedTree(int lb, int ub) {
        SplayTree tree = new SplayTree();
        tree.root = constructBalancedTree(lb, ub);
        return tree;
    }
     
    /* Constructors */  
    public SplayTree() {
        this.root = null;
    }
    
    /**
     * Returns the node whose value equals `x`
     * If `x` is not contained in the tree, the
     * potential parent of `x` is returned
     * @param x: the value to search for
     * @return: Node n where n.value == `x` or
     *          n.left.value == `x` || n.right.value == `x`
     */
    @Override
    public BTNode find(int x) {
        BTNode node = super.find(x);
        if(node != null)
            splay(node);
        return node;
    }
    
    /**
     * Inserts `x` into the tree
     * @param x
     * @return 
     */
    @Override
    public BTNode insert(int x) {
        BTNode node = super.insert(x);
        splay(node);
        return node;
    }
      
    /**
     * Returns the search path for a node
     * - Assume successful searches only
     * @param x
     * @return 
     */
    @Override
    public ArrayList<BTNode> findPath(int x) {
        ArrayList<BTNode> path = super.findPath(x);
        splay(path.get(path.size()-1));
        return path;
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