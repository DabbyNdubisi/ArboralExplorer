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
 *
 * @author ingo
 */
public class Line extends Pair<Point, Point> {

    public Line(Point p1, Point p2) {
        this.first = p1;
        this.second = p2;
    }

    public Point midPoint() {
        double x1 = first.getFirst().floatValue();
        double y1 = first.getSecond().floatValue();
        double x2 = second.getFirst().floatValue();
        double y2 = second.getSecond().floatValue();
        return new Point((x1 + x2) / 2, (y1 + y2) / 2);
    }
}
