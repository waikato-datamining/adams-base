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
 * Accuracy.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.summarystatistics;

/**
 * Calculates the accuracy (aka correct percentage).
 * Outputs values from 0 to 1.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Accuracy
  extends AbstractCategoricalSummaryStatistic {

  private static final long serialVersionUID = 9093110343302975610L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Calculates the accuracy (aka correct percentage).\n"
      + "Outputs values from 0 to 1.";
  }

  /**
   * Returns the names of the statistics.
   *
   * @return		the names
   */
  public String[] getNames() {
    return new String[]{
      "Accuracy",
      "# Correct"
    };
  }

  /**
   * Calculates the summary statistic.
   *
   * @return		the statistics
   */
  @Override
  protected double[] doCalculate() {
    int		match;
    int		i;

    match = 0;
    for (i = 0; i < m_CategoricalActual.length; i++) {
      if (!m_CategoricalActual[i].equals(MISSING_CATEGORICAL)) {
	if (m_CategoricalActual[i].equals(m_CategoricalPredicted[i]))
	  match++;
      }
    }

    return new double[]{
      (double) match / m_CategoricalActual.length,
      match,
    };
  }
}
