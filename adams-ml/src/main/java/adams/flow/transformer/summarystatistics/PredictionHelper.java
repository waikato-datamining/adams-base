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
 * PredictionHelper.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.summarystatistics;

import gnu.trove.list.TDoubleList;
import gnu.trove.list.array.TDoubleArrayList;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for predictions.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PredictionHelper {

  /**
   * Filters out missing actual values/predicted values.
   *
   * @param actual	the actual values
   * @param predicted	the predicted values
   * @return		actual (index 0), predicted (index 1)
   */
  public static double[][] filterMissing(double[] actual, double[] predicted) {
    TDoubleList		actNew;
    TDoubleList		predNew;
    int			i;

    actNew  = new TDoubleArrayList();
    predNew = new TDoubleArrayList();
    for (i = 0; i < actual.length; i++) {
      if (Double.isNaN(actual[i]) || Double.isNaN(predicted[i]))
        continue;
      actNew.add(actual[i]);
      predNew.add(predicted[i]);
    }

    return new double[][]{actNew.toArray(), predNew.toArray()};
  }

  /**
   * Filters out missing actual values/predicted values.
   *
   * @param actual	the actual values
   * @param predicted	the predicted values
   * @param prob	the probabilities, can be null
   * @return		actual (index 0), predicted (index 1), probabilities (index 2)
   */
  public static Object[] filterMissing(String[] actual, String[] predicted, double[] prob) {
    Object[]		result;
    List<String>	actNew;
    List<String> 	predNew;
    TDoubleList		probNew;
    int			i;

    actNew  = new ArrayList<>();
    predNew = new ArrayList<>();
    probNew = new TDoubleArrayList();

    for (i = 0; i < actual.length; i++) {
      if (actual[i].equals(CategoricalSummaryStatistic.MISSING_CATEGORICAL) || predicted[i].equals(CategoricalSummaryStatistic.MISSING_CATEGORICAL))
        continue;
      actNew.add(actual[i]);
      predNew.add(predicted[i]);
      if (prob != null)
        probNew.add(prob[i]);
    }

    result    = new Object[3];
    result[0] = actNew.toArray(new String[actNew.size()]);
    result[1] = predNew.toArray(new String[predNew.size()]);
    result[2] = null;
    if (prob != null)
      result[2] = probNew.toArray();

    return result;
  }
}
