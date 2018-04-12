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
 * CorrelationCoefficient.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.summarystatistics;

import adams.data.statistics.StatUtils;

/**
 * Calculates the correlation coefficient.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class CorrelationCoefficient
  extends AbstractNumericSummaryStatistic {

  private static final long serialVersionUID = -8141027950994364855L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Calculates the correlation coefficient.";
  }

  /**
   * Returns the names of the statistics.
   *
   * @return		the names
   */
  @Override
  public String[] getNames() {
    return new String[]{"Correlation coefficient"};
  }

  /**
   * Calculates the summary statistics.
   *
   * @return		the statistics
   */
  @Override
  protected double[] doCalculate() {
    double	result;
    double[][]	filtered;
    double[]	act;
    double[]	pred;
    double	meanAct;
    double	meanPred;
    int		i;
    double 	sumPred;
    double 	sumAct;
    double 	sumPredAct;

    filtered = PredictionHelper.filterMissing(m_NumericActual, m_NumericPredicted);
    act      = filtered[0];
    pred     = filtered[1];
    meanAct  = StatUtils.mean(act);
    meanPred = StatUtils.mean(pred);
    if (isLoggingEnabled()) {
      getLogger().info("meanA: " + meanAct);
      getLogger().info("meanP: " + meanPred);
    }

    // sum act
    sumAct = 0;
    for (i = 0; i < act.length; i++)
      sumAct += (act[i] - meanAct) * (act[i] - meanAct);
    if (isLoggingEnabled())
      getLogger().info("sumA: " + sumAct);

    // sum pred
    sumPred = 0;
    for (i = 0; i < pred.length; i++)
      sumPred += (pred[i] - meanPred) * (pred[i] - meanPred);
    if (isLoggingEnabled())
      getLogger().info("sumP: " + sumPred);

    // sum predact
    sumPredAct = 0;
    for (i = 0; i < act.length; i++)
      sumPredAct += (pred[i] - meanPred) * (act[i] - meanAct);
    if (isLoggingEnabled())
      getLogger().info("sumPA: " + sumPredAct);

    result = sumPredAct / (Math.sqrt(sumPred) * Math.sqrt(sumAct));
    if (isLoggingEnabled())
      getLogger().info("CC: " + result);

    return new double[]{result};
  }
}
