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

import arboralexplorer.algo.CmplUtils;
import arboralexplorer.data.GridSet;
import jCMPL.Cmpl;
import jCMPL.CmplException;
import jCMPL.CmplParameter;
import jCMPL.CmplSet;

public class ILPSolver {

    public static GridSet solve(GridSet grid) throws CmplException {
        // Initialize the parameters
        CmplSet ground = new CmplSet("ground", 2);
        ground.setValues(CmplUtils.getGroundSetPoints(grid));

        CmplParameter n = new CmplParameter("n");
        n.setValues(grid.getWidth());

        CmplParameter m = new CmplParameter("m");
        m.setValues(grid.getHeight());

        // Set up and solve the model
        Cmpl model = new Cmpl("data/ass.cmpl");
        model.setSets(ground);
        model.setParameters(n, m);

        model.solve();

        return new GridSet(CmplUtils.extractSolution(model, grid.getWidth(), grid.getHeight()), grid.getGroundSet());
    }

    private ILPSolver() {
    }
}
