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
 * MeanAbsoluteError.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.summarystatistics;

/**
 * Calculates the MAE.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class MeanAbsoluteError
  extends AbstractNumericSummaryStatistic {

  private static final long serialVersionUID = 96425796158048162L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Calculates the MAE (mean absolute error).";
  }

  /**
   * Returns the names of the statistics.
   *
   * @return		the names
   */
  @Override
  public String[] getNames() {
    return new String[]{
      "MAE",
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
    double[][]	numFiltered;
    double[]	numAct;
    double[]	numPred;
    double	sum;

    sum         = 0;
    numFiltered = PredictionHelper.filterMissing(m_NumericActual, m_NumericPredicted);
    numAct      = numFiltered[0];
    numPred     = numFiltered[1];
    total       = numAct.length;
    for (i = 0; i < numAct.length; i++)
      sum += Math.abs(numPred[i] - numAct[i]);

    return new double[]{sum / total};
  }
}
