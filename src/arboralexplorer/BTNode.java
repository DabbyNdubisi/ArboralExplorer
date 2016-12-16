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

/**
 *
 * @author dabeluchindubisi
 * @param <Node>
 */
public class BTNode<Node extends BTNode<Node>> {
    public BTNode left;
    public BTNode right;
    public BTNode parent;
    public int value;
    public Point2D position;
        
    public BTNode(int x) {
        left = null;
        right = null;
        parent = null;
        value = x;
        position = new Point2D();
    }
}