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
package arboralexplorer.data;

import arboralexplorer.Pair;
import java.util.ArrayList;
import java.util.List;

public class WilberData {

    private List<Pair<Integer, Integer>> hubSet = null;
    private List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> splitLines = null;
    private final boolean[][] grid;

    public WilberData(boolean[][] grid) {
        this.grid = grid;
        hubSet = new ArrayList<>();
        splitLines = new ArrayList<>();
    }

    public boolean[][] getGrid() {
        return grid;
    }

    public List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> getLines() {
        return splitLines;
    }

    public List<Pair<Integer, Integer>> getHubs() {
        return hubSet;
    }

    public void addLine(int l, int c1, int c2, boolean invert) {
        if (invert) {
            splitLines.add(new Pair<>(new Pair<>(c1, l), new Pair<>(c2, l)));
        } else {
            splitLines.add(new Pair<>(new Pair<>(l, c1), new Pair<>(l, c2)));
        }
    }

    public void addHub(int x, int y, boolean invert) {
        setGridPoint(x, y, invert);
        if (invert) {
            hubSet.add(new Pair<>(y, x));
        } else {
            hubSet.add(new Pair<>(x, y));
        }
    }

    public void setGridPoint(int x, int y, boolean invert) {
        if (invert) {
            grid[y][x] = true;
        } else {
            grid[x][y] = true;
        }
    }
}
