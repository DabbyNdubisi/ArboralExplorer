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
import jCMPL.Cmpl;
import jCMPL.CmplException;
import jCMPL.CmplSolElement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CmplUtils {

    private static final Pattern VAR_NAME = Pattern.compile("x\\[(\\d+),(\\d+)\\]");
    
    public static int[][] getGroundSetPoints(GridSet grid) {
        int[][] groundSetPoints = new int[grid.getGroundSetSize()][2];
        int pointIndex = 0;
        
        for (int i = 0; i < grid.getWidth(); i++) {
            for (int j = 0; j < grid.getHeight(); j++) {
                if (grid.isGroundSet(i, j)) {
                    groundSetPoints[pointIndex] = new int[]{i + 1, j + 1};
                    pointIndex++;
                }
            }
        }
        
        return groundSetPoints;
    }

    public static boolean[][] extractSolution(Cmpl model, int width, int height) throws CmplException {
        boolean[][] solution = new boolean[width][height];
        
        for (CmplSolElement v : model.solution().variables()) {
            Matcher match = VAR_NAME.matcher(v.name());
            
            if (match.find()) {
                int i = Integer.parseInt(match.group(1)) - 1;
                int j = Integer.parseInt(match.group(2)) - 1;
                
                if (v.activity() instanceof Long) {
                    solution[i][j] = ((Long) v.activity()) > 0;
                } else if (v.activity() instanceof Double) {
                    solution[i][j] = ((Double) v.activity()) > 0;
                } else {
                    throw new UnsupportedOperationException("Activity has type " + v.activity().getClass() + ", which is not supported yet.");
                }
            }
        }
        
        return solution;
    }
    
}
