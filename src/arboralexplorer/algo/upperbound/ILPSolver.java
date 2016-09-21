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

import arboralexplorer.data.*;
import jCMPL.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 *
 * @author ingo
 */
public class ILPSolver {

    private static final Pattern varName = Pattern.compile("x\\[(\\d+),(\\d+)\\]");
    
    public static GridSet solve(GridSet d) throws CmplException {
        Cmpl m = new Cmpl("data/ass.cmpl");

        int[][] groundJ = new int[d.getGroundSetSize()][2];
        int c = 0;

        for (int i = 0; i < d.getWidth(); i++) {
            for (int j = 0; j < d.getHeight(); j++) {
                if (d.isGroundSet(i, j)) {
                    groundJ[c++] = new int[]{i + 1, j + 1};
                }
            }
        }

        CmplSet ground = new CmplSet("ground", 2);
        ground.setValues(groundJ);

        CmplParameter np = new CmplParameter("n");
        np.setValues(d.getWidth());

        CmplParameter mp = new CmplParameter("m");
        mp.setValues(d.getHeight());

        m.setSets(ground);
        m.setParameters(np, mp);

        m.solve();

        System.out.printf("Objective value: %f %n", m.solution().value());
        System.out.printf("Objective status: %s %n", m.solution().status());
        System.out.println("Variables:");
        for (CmplSolElement v : m.solution().variables()) {
            System.out.printf("%10s %3s %10d %10.0f %10.0f%n", v.name(),
                    v.type(), v.activity(), v.lowerBound(), v.upperBound());
        }
        System.out.println("Constraints:");
        for (CmplSolElement ce : m.solution().constraints()) {
            System.out.printf("%10s %3s %10.0f %10.0f %10.0f%n", ce.name(),
                    ce.type(), ce.activity(), ce.lowerBound(), ce.upperBound());
        }
        
        boolean[][] solution = new boolean[d.getWidth()][d.getHeight()];
        
        for (CmplSolElement v : m.solution().variables()) {
            System.out.printf("%10s %3s %10d %10.0f %10.0f%n", v.name(),
                    v.type(), v.activity(), v.lowerBound(), v.upperBound());
            Matcher match = varName.matcher(v.name());
            if(match.find()) {
                int i = Integer.parseInt(match.group(1))-1;
                int j = Integer.parseInt(match.group(2))-1;
                solution[i][j] = ((Long) v.activity()) > 0;
            }
        }
        
        
        
        return new GridSet(solution, d.getGroundSet());
    }

    public static void main(String[] args) throws CmplException {
        boolean[][] grid = {{true, false, false},
        {false, false, true},
        {false, true, false}};
        GridSet d = new GridSet(grid);
//        System.getEnv("CMPLBINARY") = "./lib/Cmpl/bin/cmpl";

        try {
            Cmpl m = new Cmpl("ass.cmpl");

            int[][] groundJ = new int[d.getGroundSetSize()][2];
            int c = 0;

            for (int i = 0; i < d.getWidth(); i++) {
                for (int j = 0; j < d.getHeight(); j++) {
                    if (d.isGroundSet(i, j)) {
                        groundJ[c++] = new int[]{i + 1, j + 1};
                    }
                }
            }

            CmplSet ground = new CmplSet("ground", 2);
            ground.setValues(groundJ);

            CmplParameter np = new CmplParameter("n");
            np.setValues(d.getWidth());

            CmplParameter mp = new CmplParameter("m");
            mp.setValues(d.getHeight());

            m.setSets(ground);
            m.setParameters(np, mp);

            m.solve();

            System.out.printf("Objective value: %f %n", m.solution().value());
            System.out.printf("Objective status: %s %n", m.solution().status());
            System.out.println("Variables:");
            for (CmplSolElement v : m.solution().variables()) {
                System.out.printf("%10s %3s %10d %10.0f %10.0f%n", v.name(),
                        v.type(), v.activity(), v.lowerBound(), v.upperBound());
            }
            System.out.println("Constraints:");
            for (CmplSolElement ce : m.solution().constraints()) {
                System.out.printf("%10s %3s %10.0f %10.0f %10.0f%n", ce.name(),
                        ce.type(), ce.activity(), ce.lowerBound(), ce.upperBound());
            }
        } catch (CmplException e) {
            System.out.println(e);
        }
    }
}
