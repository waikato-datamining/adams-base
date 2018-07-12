/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * PLS1AttributeEval.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */

package weka.attributeSelection;

import adams.data.instancesanalysis.pls.AbstractSingleClassPLS;
import adams.data.instancesanalysis.pls.PLS1;
import adams.env.Environment;

/**
 * Uses the first component of PLS1 to determine the importance of the attributes
 * (defaults: no preprocessing, missing values not replaced, and 20 components)
 *
 * @author Hisham Abdel Qader (habdelqa at waikato dot ac dot nz)
 */

public class PLS1AttributeEval extends AbstractPLSAttributeEval {

  private static final long serialVersionUID = -3761260113452151430L;

  /**
   * Returns a string describing this attribute evaluator
   *
   * @return a description of the evaluator suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String globalInfo() {
    return "Uses the first component of " + PLS1.class.getName() + " to determine the "
      + "importance of the attributes (no preprocessing, missing values not replaced, and 20 components):\n"
      + "- absolute value of coefficients\n"
      + "- all coefficients normalized (ie sum up to one)";
  }

  /**
   * Creates a new instance of a PLS algorrithm.
   *
   * @return		the instance
   */
  protected AbstractSingleClassPLS newModel() {
    return new PLS1();
  }

  /**
   * Main method for running this class from commandline.
   *
   * @param args the options
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(Environment.class);
    runEvaluator(new PLS1AttributeEval(), args);
  }
}