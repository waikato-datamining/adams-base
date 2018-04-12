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
 * NoPrediction.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.summarystatistics;

/**
 * Calculates the rows with no prediction.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class NoPrediction
  extends AbstractGeneralSummaryStatistic {

  private static final long serialVersionUID = 96425796158048162L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Calculates the rows with no prediction.";
  }

  /**
   * Returns the names of the statistics.
   *
   * @return		the names
   */
  @Override
  public String[] getNames() {
    return new String[]{
      "No prediction #",
      "No prediction %",
    };
  }

  /**
   * Calculates the summary statistics.
   *
   * @return		the statistics
   */
  @Override
  protected double[] doCalculate() {
    int		i;
    int		total;
    int		count;

    count = 0;
    if (isNumeric()) {
      total = m_NumericActual.length;
      for (i = 0; i < m_NumericPredicted.length; i++) {
	if (Double.isNaN(m_NumericPredicted[i]))
	  count++;
      }
    }
    else {
      total = m_CategoricalActual.length;
      for (i = 0; i < m_CategoricalPredicted.length; i++) {
        if (m_CategoricalPredicted[i].equals(MISSING_CATEGORICAL))
          count++;
      }
    }

    return new double[]{
      count,
      count / total,
    };
  }
}
