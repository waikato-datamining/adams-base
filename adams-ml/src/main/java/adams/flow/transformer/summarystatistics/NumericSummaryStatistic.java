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
 * NumericSummaryStatistic.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.summarystatistics;

/**
 * Interface for statistics that work on numeric predictions.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface NumericSummaryStatistic
  extends SummaryStatistic {

  /** the value for missing numeric values. */
  public double MISSING_NUMERIC = Double.NaN;

  /**
   * Sets the actual values.
   *
   * @param value	the actual
   */
  public void setNumericActual(double[] value);

  /**
   * Returns the actual values.
   *
   * @return		the actual
   */
  public double[] getNumericActual();

  /**
   * Sets the predicted values.
   *
   * @param value	the predicted
   */
  public void setNumericPredicted(double[] value);

  /**
   * Returns the predicted values.
   *
   * @return		the predicted
   */
  public double[] getNumericPredicted();
}
