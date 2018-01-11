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

/**
 * PassThrough.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */
package adams.data.weka.evaluator;

import weka.core.Instance;

/**
 <!-- globalinfo-start -->
 * A dummy evaluator that OKs all data.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D (property: debug)
 * &nbsp;&nbsp;&nbsp;If set to true, scheme may output additional info to the console.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PassThrough
  extends AbstractInstanceEvaluator {

  /** for serialization. */
  private static final long serialVersionUID = -8364993668286801038L;

  /**
   * Returns a string describing the object.
   *
   * @return 		a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "A dummy evaluator that OKs all data.";
  }

  /**
   * Performs no real evaluation, just returns 1.0.
   *
   * @param inst	the instance to evaluate
   * @return		evaluation range, between 0 and 1 (0 = bad, 1 = good, -1 = if unable to evaluate)
   */
  protected double doEvaluate(Instance inst) {
    return 1.0;
  }
}
