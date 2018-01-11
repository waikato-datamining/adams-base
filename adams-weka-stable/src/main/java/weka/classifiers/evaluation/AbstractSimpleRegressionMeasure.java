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
 * AbstractSimpleRegressionMeasure.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package weka.classifiers.evaluation;

import gnu.trove.list.TDoubleList;
import gnu.trove.list.array.TDoubleArrayList;
import weka.core.Instance;
import weka.core.Utils;

/**
 * Computes the mean error.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractSimpleRegressionMeasure
  extends AbstractEvaluationMetric
  implements StandardEvaluationMetric {

  private static final long serialVersionUID = -2991979775999208848L;

  /** the collected actual. */
  protected TDoubleList m_Actual = new TDoubleArrayList();

  /** the collected predicted. */
  protected TDoubleList m_Predicted = new TDoubleArrayList();

  /**
   * Return true if this evaluation metric can be computed when the class is
   * nominal
   *
   * @return true if this evaluation metric can be computed when the class is
   *         nominal
   */
  @Override
  public boolean appliesToNominalClass() {
    return false;
  }

  /**
   * Return true if this evaluation metric can be computed when the class is
   * numeric
   *
   * @return true if this evaluation metric can be computed when the class is
   *         numeric
   */
  @Override
  public boolean appliesToNumericClass() {
    return true;
  }

  /**
   * Ignored.
   *
   * @param predictedDistribution the probabilities assigned to each class
   * @param instance the instance to be classified
   * @throws Exception if the class of the instance is not set
   */
  @Override
  public void updateStatsForClassifier(double[] predictedDistribution, Instance instance) throws Exception {
    // ignored
  }

  /**
   * Updates the statistics about a predictors performance for the current test
   * instance. Gets called when the class is numeric. Implementers need only
   * implement this method if it is not possible to compute their statistics
   * from what is stored in the base Evaluation object.
   *
   * @param predictedValue the numeric value the classifier predicts
   * @param instance the instance to be classified
   * @throws Exception if the class of the instance is not set
   */
  @Override
  public void updateStatsForPredictor(double predictedValue, Instance instance) throws Exception {
    if (!instance.classIsMissing() && !Utils.isMissingValue(predictedValue)) {
      m_Actual.add(instance.classValue());
      m_Predicted.add(predictedValue);
    }
  }
}
