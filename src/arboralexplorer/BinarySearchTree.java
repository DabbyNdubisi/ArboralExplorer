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

import arboralexplorer.data.GridSet;
import java.util.ArrayList;

/**
 *
 * @author dabeluchindubisi
 */
/** Quick Binary Tree which supports find and inserts **/
public class BinarySearchTree {
    public BTNode root;
    
    /**
     * Computed instance variables
     */
    public int numNodes = 0;
    public int maxHeight = 0;
    
    /**
     * Creates a Binary search tree which matches
     * the ground-set on the grid
     * @param grid
     * @return
     */
    public static BTNode greedyFutureTree(GridSet grid) {
        BinarySearchTree t = new BinarySearchTree();
        for(int i = 0; i < grid.getHeight(); i++) {
            for(int j = 0; j < grid.getWidth(); j++) {
                if(grid.isGroundSet(j, i))
                    t.insert(j);
            }
        }
        return t.root;
    }
    
    /**
     * Creates a balanced Binary search tree on
     * elements from `lb` to `ub`
     * @param grid
     * @return
     */
    public static BTNode constructBalancedTree(int lb, int ub) {
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
     * Insert `x` into the tree
     * @param x
     * @return 
     */
    public BTNode insert(int x) {
        BTNode node = find(x);
        BTNode newNode = new BTNode(x);
        if(node == null) // root
            root = newNode;
        else {
            if(node.value == x)
                return node;
            else {
                if(node.value < x) { node.right = newNode; }
                else { node.left = newNode; }
                newNode.parent = node;
            }
        }
        
        return newNode;
    }

    
    /**
     * Find `x` in the tree
     * @param x
     * @return 
     */
    public BTNode find(int x) {
        BTNode currNode = root;
        BTNode prevNode = null;
        while(currNode != null) {
            prevNode = currNode;
            if(currNode.value < x) { currNode = currNode.right; }
            else if(currNode.value > x) { currNode = currNode.left; }
            else { return currNode; }
        }
        return prevNode;
    }
    
        /**
     * Returns the search path for a node
     * - Assume successful searches only
     * @param x
     * @return 
     */
    public ArrayList<BTNode> findPath(int x) {
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
        
        return path;
    }
    
    
    /**
     * Creates a Geometric copy of the BST with 2d points for each node
     * @return 
     */
    public BinarySearchTree geometricCopy() {
        maxHeight = 0;
        numNodes = 0;
        BTNode rCopy = geometricCopy(this.root);
        BTNode u = rCopy, prev = null, next;
	while (u != null) {
            if (prev == u.parent) {
		if (u.left != null) next = u.left;
		else if (u.right != null) {
                    u.position.x = numNodes++;
                    next = u.right;
		}
		else {
                    next = u.parent;
                    u.position.x = numNodes++;
                }
            } else if (prev == u.left) {
		u.position.x = numNodes++;
		if (u.right != null) next = u.right;
		else next = u.parent;
            } else {
		next = u.parent;
            }
            prev = u;
            u = next;
	}
        
        BinarySearchTree t = new BinarySearchTree();
        t.root = rCopy;
        t.numNodes = numNodes;
        t.maxHeight = maxHeight;
        return t;
    }
    
    private BTNode geometricCopy(BTNode r) {
        if(r == null)
            return null;
        BTNode copyRoot = new BTNode(r.value);
        copyRoot.position.y = maxHeight++;
        copyRoot.left = geometricCopy(r.left);
        copyRoot.right = geometricCopy(r.right);
        if(copyRoot.left != null)
            copyRoot.left.parent = copyRoot;
        if(copyRoot.right != null)
            copyRoot.right.parent = copyRoot;
        return copyRoot; 
    }
}
