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

package adams.opt.cso;

import weka.classifiers.Evaluation;
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
  CC(false, true),
  /** evaluation via: Root mean squared error. */
  RMSE(true, true),
  /** evaluation via: Root relative squared error. */
  RRSE(true, true),
  /** evaluation via: Mean absolute error. */
  MAE(true, true),
  /** evaluation via: Relative absolute error. */
  RAE(true, true),
  /** evaluation via: Accuracy. */
  ACC(true, false);

  /** whether a nominal class is allowed. */
  private boolean m_Nominal;

  /** whether a numeric class is allowed. */
  private boolean m_Numeric;

  /**
   * initializes the measure with the given flags.
   *
   * @param nominal	whether used for nominal classes
   * @param numeric	whether used for numeric classes
   */
  private Measure(boolean nominal, boolean numeric) {
    m_Nominal = nominal;
    m_Numeric = numeric;
  }

  /**
   * Extracts the measure from the Evaluation object.
   *
   * @param evaluation	the evaluation to use
   * @param adjust	whether to adjust the measure
   * @return		the measure
   * @throws Exception	in case the retrieval of the measure fails
   */
  public double extract(Evaluation evaluation, boolean adjust) throws Exception {
    switch (this) {
      case ACC:
        if (adjust)
          return 100.0 - evaluation.pctCorrect();
        else
          return evaluation.pctCorrect();
      case CC:
        if (adjust)
          return 1.0 - evaluation.correlationCoefficient();
        else
          return evaluation.correlationCoefficient();
      case MAE:
	return evaluation.meanAbsoluteError();
      case RAE:
	return evaluation.relativeAbsoluteError();
      case RMSE:
	return evaluation.rootMeanSquaredError();
      case RRSE:
	return evaluation.rootRelativeSquaredError();
      default:
	throw new IllegalStateException("Unhandled measure '" + this + "'!");
    }
  }

  /**
   * Transforms the statistic back into its normal space.
   *
   * @param value	the statistics to transformer
   * @return		the actual statistics value
   * @throws Exception	in case the retrieval of the measure fails
   */
  public double actual(double value) {
    switch (this) {
      case ACC:
	return 100.0 - value;
      case CC:
	return 1.0 - value;
      case MAE:
      case RAE:
      case RMSE:
      case RRSE:
	return value;
      default:
	throw new IllegalStateException("Unhandled measure '" + this + "'!");
    }
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
