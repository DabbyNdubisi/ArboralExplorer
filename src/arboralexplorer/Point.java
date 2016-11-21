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
package arboralexplorer;

/**
 * A class to represent points on the grid, including points not on the lattice.
 * @author ingo
 */
public class Point extends Pair<Number, Number> {
    
    public Point(Number x, Number y) {
        this.first = x;
        this.second = y;
    }
    
    /**
     * Returns the x coordinate cast to an integer.
     * @return The x coordinate as an integer.
     */
    public int getXInt() {
        return first.intValue();
    }
    
    /**
     * Returns the y coordinate cast to an integer.
     * @return The y coordinate as an integer.
     */
    public int getYInt() {
        return second.intValue();
    }
    
    /**
     * Returns the numerical value of the x coordinate.
     * @return The x coordinate.
     */
    public Number getX() {
        return first;
    }
    
    /**
     * Returns the numerical value of the y coordinate.
     * @return The y coordinate.
     */
    public Number getY() {
        return second;
    }
}
