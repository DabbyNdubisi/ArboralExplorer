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

import arboralexplorer.algo.upperbound.GreedyASS;
import arboralexplorer.data.GridSet;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class GridSetWriterTest {

    public GridSetWriterTest() {
    }

    @Test
    public void testExportGrid() throws Exception {
        int[] permutation = new int[]{5, 12, 4, 3, 16, 6, 14, 8, 0, 9, 17, 19, 18, 15, 2, 11, 13, 10, 1, 7};
        boolean[][] points = new boolean[permutation.length][permutation.length];

        for (int i = 0; i < permutation.length; i++) {
            points[i][permutation[i]] = true;
        }

        GridSet grid = GreedyASS.solve(new GridSet(points));

        Path temp = Paths.get("textExportGrid.tmp");
        GridSetWriter.exportGrid(grid, temp);
        GridSet grid2 = GridSetReader.importGrid(temp);
        Files.delete(temp);

        assertGridsEquals(grid, grid2);
    }

    private void assertGridsEquals(GridSet expectedGrid, GridSet actualGrid) {
        if (expectedGrid == null) {
            assertNull(actualGrid);
            return;
        }

        assertNotNull(actualGrid);

        assertEquals(expectedGrid.getWidth(), actualGrid.getWidth());
        assertEquals(expectedGrid.getHeight(), actualGrid.getHeight());

        for (int i = 0; i < expectedGrid.getWidth(); i++) {
            for (int j = 0; j < expectedGrid.getHeight(); j++) {
                assertEquals(expectedGrid.hasPoint(i, j), actualGrid.hasPoint(i, j));
                assertEquals(expectedGrid.isGroundSet(i, j), actualGrid.isGroundSet(i, j));
            }
        }
    }

}
