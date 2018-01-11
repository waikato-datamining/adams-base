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
 * SDR.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package weka.classifiers.evaluation;

import adams.data.statistics.StatUtils;
import gnu.trove.list.TDoubleList;
import gnu.trove.list.array.TDoubleArrayList;
import weka.core.Utils;

import java.util.Arrays;
import java.util.List;

/**
 * Computes the SDR (Standard Deviation of Residuals) for regression models.
 * <br>
 * http://cirpwiki.info/wiki/Statistics#Standard_Deviation_of_Residuals
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SDR
  extends AbstractSimpleRegressionMeasure {

  private static final long serialVersionUID = -2991979775999208848L;

  public static final String NAME = "Standard Deviation of Residuals";

  /**
   * Get the name of this metric
   *
   * @return the name of this metric
   */
  @Override
  public String getMetricName() {
    return NAME;
  }

  /**
   * Get a short description of this metric (algorithm, formulas etc.).
   *
   * @return a short description of this metric
   */
  @Override
  public String getMetricDescription() {
    return NAME + " = sqrt(mean((xc(:)-xm(:)-mean(xc(:))+mean(xm(:))).^2)); [m=measured, c=calculated]";
  }

  /**
   * Get a list of the names of the statistics that this metrics computes. E.g.
   * an information theoretic evaluation measure might compute total number of
   * bits as well as average bits/instance
   *
   * @return the names of the statistics that this metric computes
   */
  @Override
  public List<String> getStatisticNames() {
    return Arrays.asList(NAME);
  }

  /**
   * Get the value of the named statistic
   *
   * @param statName the name of the statistic to compute the value for
   * @return the computed statistic or Utils.missingValue() if the statistic
   *         can't be computed for some reason
   */
  @Override
  public double getStatistic(String statName) {
    double 	meanC;
    double 	meanM;
    int		i;
    double	val;
    TDoubleList	values;

    if (statName.equals(NAME)) {
      if (m_Actual.size() == 0) {
	return Utils.missingValue();
      }
      else {
	values = new TDoubleArrayList();
	meanC = StatUtils.mean(m_Predicted.toArray());
	meanM = StatUtils.mean(m_Actual.toArray());
	for (i = 0; i < m_Actual.size(); i++) {
	  val = m_Predicted.get(i) - m_Actual.get(i) - (meanC - meanM);
	  values.add(val * val);
	}
	return Math.sqrt(StatUtils.mean(values.toArray()));
      }
    }
    else {
      return Utils.missingValue();
    }
  }

  /**
   * Return a formatted string (suitable for displaying in console or GUI
   * output) containing all the statistics that this metric computes.
   *
   * @return a formatted string containing all the computed statistics
   */
  @Override
  public String toSummaryString() {
    return Utils.padRight(NAME, 41) + Utils.doubleToString(getStatistic(NAME), 4) + "\n";
  }
}
