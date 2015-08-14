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
 * Measure.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.genetic;

import weka.core.Instances;
import weka.core.UnassignedClassException;

/**
 * The measure to use for evaluating.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 4322 $
 */
public enum Measure {

  /** evaluation via: Correlation coefficient. */
  CC(false, false, true),
  /** evaluation via: Root mean squared error. */
  RMSE(true, true, true),
  /** evaluation via: Root relative squared error. */
  RRSE(true, true, true),
  /** evaluation via: Mean absolute error. */
  MAE(true, true, true),
  /** evaluation via: Relative absolute error. */
  RAE(true, true, true),
  /** evaluation via: Accuracy. */
  ACC(false, true, false);


  /** whether the measure is multiplied by -1 or not. Only used in sorting. */
  private boolean m_Negative;

  /** whether a nominal class is allowed. */
  private boolean m_Nominal;

  /** whether a numeric class is allowed. */
  private boolean m_Numeric;

  /**
   * initializes the measure with the given flags.
   *
   * @param negative	whether measures gets multiplied with -1
   * @param nominal	whether used for nominal classes
   * @param numeric	whether used for numeric classes
   */
  private Measure(boolean negative, boolean nominal, boolean numeric) {
    m_Negative = negative;
    m_Nominal  = nominal;
    m_Numeric  = numeric;
  }

  /**
   * Adjusts the measure value for sorting: either multiplies it with -1 or 1.
   *
   * @param measure	the raw measure
   * @return		the adjusted measure
   */
  public double adjust(double measure) {
    if (m_Negative)
      return -measure;
    else
      return measure;
  }

  /**
   * Checks whether the data can be used with this measure.
   *
   * @param data	the data to check
   * @return		true if the measure can be obtain for this kind of data
   */
  public boolean isValid(Instances data) {
    if (data.classIndex() == -1)
      throw new UnassignedClassException("No class attribute set!");

    if (data.classAttribute().isNominal())
      return m_Nominal;
    else if (data.classAttribute().isNumeric())
      return m_Numeric;
    else
      throw new IllegalStateException(
	"Class attribute '" + data.classAttribute().type() + "' not handled!");
  }
}
