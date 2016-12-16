/*
 * Copyright 2016 dabeluchindubisi.
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

import arboralexplorer.algo.GridSetGenerator;
import arboralexplorer.algo.upperbound.GreedyASS;
import arboralexplorer.algo.upperbound.OptStaticTree;
import arboralexplorer.algo.upperbound.SplayTreeSolver;
import arboralexplorer.data.GridSet;
import java.util.ArrayList;

/**
 *
 * @author dabeluchindubisi
 * 
 * Run tests on splay tree and greedyASS
 * solutions to ASS problem
 */
public class ArboralExplorerTests {
    
    public static void main(String[] args) {
        int row = 10000;
        System.out.println("Num Accesses: " + row);
        System.out.printf("%15s %15s %15s %15s\n", "Num points", "Opt points", "GreedyASS points", "Splay points");
        for(int np = 100; np <= 10000; np += 100) {
            int splayPoints = 0,
                greedyAssPoints = 0,
                optPoints = 0;
            for(int itr = 0; itr < 10; itr++) {
                GridSet gridSet = GridSetGenerator.randomPermutation(np, row);
                
                GridSet assSol = GreedyASS.solve(gridSet);
                greedyAssPoints += assSol.getSize() - assSol.getGroundSetSize();
                
                Pair<GridSet, ArrayList<BinarySearchTree>> sol = SplayTreeSolver.solve(gridSet, false);
                splayPoints += sol.getFirst().getSize() - sol.getFirst().getGroundSetSize();
            }
            
            optPoints = optPoints/10;
            splayPoints = splayPoints/10;
            greedyAssPoints = greedyAssPoints/10;
            System.out.printf("%15d %15d %15d %15d\n", np, optPoints, greedyAssPoints, splayPoints);
            System.gc();
        }
    }
}
