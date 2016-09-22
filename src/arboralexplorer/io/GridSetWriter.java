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
package arboralexplorer.io;

import arboralexplorer.data.GridSet;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

public class GridSetWriter {

    public static void exportGrid(GridSet grid, Path path) throws IOException {
        try (BufferedWriter out = Files.newBufferedWriter(path, Charset.forName("UTF-8"))) {
            out.write("# Size");
            out.newLine();

            out.write("S " + grid.getWidth() + " " + grid.getHeight());
            out.newLine();

            out.newLine();
            out.write("# Ground set");
            out.newLine();

            for (int i = 0; i < grid.getWidth(); i++) {
                for (int j = 0; j < grid.getHeight(); j++) {
                    if (grid.isGroundSet(i, j)) {
                        out.write("G " + i + " " + j);
                        out.newLine();
                    }
                }
            }

            out.newLine();
            out.write("# Other points");
            out.newLine();

            for (int i = 0; i < grid.getWidth(); i++) {
                for (int j = 0; j < grid.getHeight(); j++) {
                    if (grid.hasPoint(i, j) && !grid.isGroundSet(i, j)) {
                        out.write("P " + i + " " + j);
                        out.newLine();
                    }
                }
            }
        }
    }
}
