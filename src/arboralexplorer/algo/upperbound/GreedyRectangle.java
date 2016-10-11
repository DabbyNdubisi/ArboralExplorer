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

import arboralexplorer.algo.ArboralChecker;
import java.util.*;
import arboralexplorer.data.GridSet;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ingo
 */
public class GreedyRectangle {

    public static GridSet solve(GridSet grid) {
        List<EmptyRect> rectangles = new ArrayList();

        int width = grid.getWidth();
        int height = grid.getHeight();
        boolean[][] newGrid = grid.getGroundSet();
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                newGrid[i][j] = grid.hasPoint(i, j);
            }
        }

        PrefixSum prefix = new PrefixSum(width, height);
        prefix.fillTable(grid);

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                System.out.print(prefix.getPrefix(i, j));
                System.out.print(" ");
            }
            System.out.print('\n');
        }
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                for (int l = j; l < height; l++) {
                    for (int k = i; k < width; k++) {
                        if (prefix.getSum(i, j, k, l) == 0) {
                            rectangles.add(new EmptyRect(i, j, k, l));
                        }
                    }
                }
            }
        }

        Collections.sort(rectangles, new EmptyRectComparator());
        Collections.reverse(rectangles);

        while (!rectangles.isEmpty()) {
            EmptyRect big = rectangles.get(0);
            System.out.print(big.print() + "\n");

            GridSet old = new GridSet(newGrid, grid.getGroundSet());
            int oldViolations = ArboralChecker.getAllAssViolations(old).size();

            boolean c = true;

            if (big.volume() < 3) {
                break;
            }
            if (big.i > 0) {
                if (big.j > 0) {
                    c = c && newGrid[big.i - 1][big.j - 1];
                    newGrid[big.i - 1][big.j - 1] = true;
                }
                if (big.l < height - 1) {
                    c = c && newGrid[big.i - 1][big.l + 1];
                    newGrid[big.i - 1][big.l + 1] = true;
                }
            }
            if (big.k < width - 1) {
                if (big.j > 0) {
                    c = c && newGrid[big.k + 1][big.j - 1];
                    newGrid[big.k + 1][big.j - 1] = true;
                }
                if (big.l < height - 1) {
                    c = c && newGrid[big.k + 1][big.l + 1];
                    newGrid[big.k + 1][big.l + 1] = true;
                }
            }

            GridSet newG = new GridSet(newGrid, grid.getGroundSet());
            int newViolations = ArboralChecker.getAllAssViolations(newG).size();

            if (newViolations + 5 < oldViolations) {

                List<EmptyRect> newRectangles = new ArrayList();
                for (int count = 0; count < rectangles.size(); count++) {
                    if (!big.intersects(rectangles.get(count))) {
                        newRectangles.add(rectangles.get(count));
                    }
                }

                rectangles = newRectangles;
                //if (!c)
                //    break;
            } else {
                for (int j = 0; j < height; j++) {
                    for (int i = 0; i < width; i++) {
                        newGrid[i][j] = old.hasPoint(i, j);
                    }
                }
                rectangles.remove(0);
            }
        }

        return new GridSet(newGrid, grid.getGroundSet());
    }

    /**
     * Computes the prefix sum of a given grid and can query for emptiness. The
     * internal table is padded by 1 in all four directions.
     *
     * All public methods use grid space. All private methods use prefix-sum
     * space (offset by 1).
     */
    public static class PrefixSum {

        public int[][] prefix;
        public int width, height;

        public PrefixSum(int width, int height) {
            this.width = width;
            this.height = height;
            prefix = new int[width + 2][height + 2];
            prefix[0][0] = 0;
        }

        public int getPrefix(int i, int j) {
            return prefix[i + 1][j + 1];
        }

        /**
         * Returns the number of elements in the square defined by (i,j),(k,l)
         * where (i,j) is the upper left corner and (k,l) the lower right.
         *
         * All coordinates are in grid space.
         *
         * @param i smallest column index
         * @param j smallest row index
         * @param k largest column index
         * @param l smallest row index
         * @return
         */
        public int getSum(int i, int j, int k, int l) {
            i++;
            j++;
            k++;
            l++;
            return getSumP(i, j, k, l);
        }

        private int getSumP(int i, int j, int k, int l) {
            return prefix[k][l]
                    - prefix[i - 1][l]
                    - prefix[k][j - 1]
                    + prefix[i - 1][j - 1];
        }

        private void fillCol(GridSet grid, int col) {
            prefix[col][0] = 0;
            for (int j = 1; j < height + 1; j++) {
                prefix[col][j] = -this.getSumP(col, j, col, j);
                if (grid.hasPoint(col - 1, j - 1)) {
                    prefix[col][j] += 1;
                }
            }
        }

        private void fillRow(GridSet grid, int row) {
            prefix[0][row] = 0;
            for (int i = 1; i < width + 1; i++) {
                prefix[i][row] = -this.getSumP(i, row, i, row);
                if (grid.hasPoint(i - 1, row - 1)) {
                    prefix[i][row] += 1;
                }
            }
        }

        public void fillTable(GridSet grid) {
            for (int j = 0; j < height + 2; j++) {
                prefix[0][j] = 0;
            }

            for (int i = 0; i < width + 2; i++) {
                prefix[i][0] = 0;
            }

            for (int j = 1; j < height + 1; j++) {
                fillRow(grid, j);
            }

            for (int j = 1; j < height + 2; j++) {
                prefix[width + 1][j] = this.getSumP(width + 1, j, width + 1, j);
            }
            for (int i = 1; i < width + 2; i++) {
                prefix[i][height + 1] = this.getSumP(i, height + 1, i, height + 1);
            }
        }
    }

    public static class EmptyRectComparator implements Comparator<EmptyRect> {

        @Override
        public int compare(EmptyRect r1, EmptyRect r2) {
            return r1.sortValue() - r2.sortValue();
        }
    }

    public static class EmptyRect {

        public int i, j, k, l;

        public EmptyRect(int i, int j, int k, int l) {
            this.i = i;
            this.j = j;
            this.k = k;
            this.l = l;
        }

        public boolean intersects(EmptyRect other) {
            return !(this.leftOf(other) || other.leftOf(this)
                    || this.above(other) || other.above(this));
        }

        public boolean leftOf(EmptyRect other) {
            return this.k < other.i;
        }

        public boolean above(EmptyRect other) {
            return this.l < other.j;
        }

        public boolean contains(int x, int y) {
            return (this.i <= x && x <= this.k)
                    && (this.j <= y && y <= this.l);
        }

        public int height() {
            return l - j + 1;
        }

        public int width() {
            return k - i + 1;
        }

        public int volume() {
            return this.width() * this.height();
        }

        public int circum() {
            return 2 * (this.width() + this.height()) + 4;
        }

        public int ratio() {
            return this.volume() / this.circum();
        }

        public int sortValue() {
            return this.ratio();
        }

        public String print() {
            String s = "(" + i + " ," + j + " ," + k + " ," + l + ")";
            return s;
        }
    }
}
