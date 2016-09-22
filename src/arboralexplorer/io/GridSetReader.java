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
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

public class GridSetReader {

    public static GridSet importGrid(Path path) throws IOException {
        try (BufferedReader in = Files.newBufferedReader(path, Charset.forName("UTF-8"))) {
            boolean[][] points = new boolean[0][0];
            boolean[][] groundSet = new boolean[0][0];

            for (String line = in.readLine(); line != null; line = in.readLine()) {
                if (line.startsWith("#") || line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split(" ");

                if (parts.length != 3 || parts[0].length() != 1) {
                    continue;
                }

                int i, j;

                try {
                    i = Integer.parseInt(parts[1]);
                    j = Integer.parseInt(parts[2]);
                } catch (NumberFormatException ex) {
                    continue;
                }

                switch (parts[0]) {
                    case "S": // Size
                        points = new boolean[i][j];
                        groundSet = new boolean[i][j];
                        break;
                    case "G": // Ground set point
                        points[i][j] = true;
                        groundSet[i][j] = true;
                        break;
                    case "P": // Regular point
                        points[i][j] = true;
                        break;
                }
            }

            return new GridSet(points, groundSet);
        }
    }
}
