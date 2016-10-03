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
package arboralexplorer.algo;

import arboralexplorer.data.GridSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GridSetGenerator {
    
    private static final Random rand = new Random();

    public static GridSet randomPermutation(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("size must be positive");
        }

        List<Integer> permutation = new ArrayList<>(width);

        for (int i = 0; i < width; i++) {
            permutation.add(i);
        }

        Collections.shuffle(permutation, rand);

        boolean[][] newGrid = new boolean[width][height];

        for (int j = 0; j < newGrid[0].length; j++) {
            newGrid[permutation.get(j % width)][j] = true;
        }

        return new GridSet(newGrid);
    }

    public static GridSet random(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("size must be positive");
        }

        boolean[][] newGrid = new boolean[width][height];

        for (int j = 0; j < height; j++) {
            newGrid[rand.nextInt(width)][j] = true;
        }

        return new GridSet(newGrid);
    }

}
