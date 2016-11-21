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

import arboralexplorer.Line;
import arboralexplorer.Pair;
import arboralexplorer.Point;
import java.util.ArrayList;
import java.util.List;

public class WilberData {

    private List<Point> hubSet = null;
    private List<Point> morePoints = null;
    private List<Line> splitLines = null;
    private List<Line> moreLines = null;
    private final boolean[][] grid;

    public WilberData(boolean[][] grid) {
        this.grid = grid;
        hubSet = new ArrayList<>();
        splitLines = new ArrayList<>();
        moreLines = new ArrayList<>();
        morePoints = new ArrayList<>();
    }

    public boolean[][] getGrid() {
        return grid;
    }

    public List<Line> getLines() {
        return splitLines;
    }

    public List<Line> getLines(identifier id) {
        switch (id) {
            case REDLINES:
                return moreLines;
            default:
                return null;
        }
    }

    public void setLines(List<Line> lines) {
        this.splitLines = lines;
    }

    public List<Point> getHubs() {
        return hubSet;
    }

    public List<Point> getPoints(identifier id) {
        switch (id) {
            case REDPOINTS:
                return morePoints;
            default:
                return null;
        }
    }

    public void addLine(Number l, Number c1, Number c2, boolean invert) {
        if (invert) {
            splitLines.add(new Line(new Point(c1, l), new Point(c2, l)));
        } else {
            splitLines.add(new Line(new Point(l, c1), new Point(l, c2)));
        }
    }

    public void addLine(Number x1, Number y1, Number x2, Number y2) {
        splitLines.add(new Line(new Point(x1, y1), new Point(x2, y2)));
    }

    public void addLine(Line line, identifier id) {
        addLine(line.getFirst().getFirst(), line.getFirst().getSecond(), line.getSecond().getFirst(), line.getSecond().getSecond(), id);
    }
    
    public void addLine(Number x1, Number y1, Number x2, Number y2, identifier id) {
        switch (id) {
            case REDLINES:
                moreLines.add(new Line(new Point(x1, y1), new Point(x2, y2)));
                break;
            default:
                return;
        }
    }

    public void addHub(Number x, Number y, boolean invert) {
        setGridPoint(x, y, invert);
        if (invert) {
            hubSet.add(new Point(y, x));
        } else {
            hubSet.add(new Point(x, y));
        }
    }

    public void addPoint(Point point, identifier id) {
        addPoint(point.getFirst(), point.getSecond(), id);
    }
    
    public void addPoint(Number x, Number y, identifier id) {
        switch (id) {
            case GREENPOINTS:
                hubSet.add(new Point(x, y));
                break;
            case REDPOINTS:
                morePoints.add(new Point(x, y));
                break;
            default:
                return;
        }
    }

    public void setGridPoint(Number x, Number y, boolean invert) {
        if (invert) {
            grid[y.intValue()][x.intValue()] = true;
        } else {
            grid[x.intValue()][y.intValue()] = true;
        }
    }

    public enum identifier {
        REDLINES, REDPOINTS, GREENPOINTS
    }
}
