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
package arboralexplorer.algo.lowerbound;

import arboralexplorer.algo.CmplUtils;
import arboralexplorer.data.GridSet;
import jCMPL.Cmpl;
import jCMPL.CmplException;
import jCMPL.CmplParameter;
import jCMPL.CmplSet;
import jCMPL.CmplSolElement;

public class LinearProgramLB {

    public static GridSet solve(GridSet grid) throws CmplException {
        // Initialize the parameters
        CmplSet ground = new CmplSet("ground", 2);
        ground.setValues(CmplUtils.getGroundSetPoints(grid));

        CmplParameter n = new CmplParameter("n");
        n.setValues(grid.getWidth());

        CmplParameter m = new CmplParameter("m");
        m.setValues(grid.getHeight());

        // Set up and solve the model
        Cmpl model = new Cmpl("data/assLP.cmpl");
        model.setSets(ground);
        model.setParameters(n, m);
        model.setOutput(Boolean.TRUE, "LP - ");

        model.solve();
        
        System.out.printf("Objective value: %f (%s)%n", model.solution().value(), model.solution().status());
        System.out.println("Variables:");
        for (CmplSolElement v : model.solution().variables()) {
            System.out.printf("%10s %10.3f%n", v.name(), v.activity());
        }
        System.out.printf("Lower bound: %.0f%n", Math.ceil(model.solution().value()));

        return new GridSet(CmplUtils.extractSolution(model, grid.getWidth(), grid.getHeight()), grid.getGroundSet());
    }

    private LinearProgramLB() {
    }

}
