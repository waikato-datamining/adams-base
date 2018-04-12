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
 * CategoricalSummaryStatistic.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.summarystatistics;

/**
 * Interface for statistics that work on categorical predictions.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface CategoricalSummaryStatistic
  extends SummaryStatistic {

  /** the placeholder for missing values. */
  public String MISSING_CATEGORICAL = "???";

  /**
   * Sets the actual values.
   *
   * @param value	the actual
   */
  public void setCategoricalActual(String[] value);

  /**
   * Returns the actual values.
   *
   * @return		the actual
   */
  public String[] getCategoricalActual();

  /**
   * Sets the predicted values.
   *
   * @param value	the predicted
   */
  public void setCategoricalPredicted(String[] value);

  /**
   * Returns the predicted values.
   *
   * @return		the predicted
   */
  public String[] getCategoricalPredicted();

  /**
   * Sets the probabilities.
   *
   * @param value	the probabilities
   */
  public void setCategoricalProbabilities(double[] value);

  /**
   * Returns the probabilities values.
   *
   * @return		the probabilities
   */
  public double[] getCategoricalProbabilities();

  // TODO class distributions
}
