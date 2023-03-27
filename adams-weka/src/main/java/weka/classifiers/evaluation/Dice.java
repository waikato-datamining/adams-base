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
 * Dice.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package weka.classifiers.evaluation;

import weka.core.Instance;
import weka.core.Utils;

import java.util.Arrays;
import java.util.List;

/**
 * Sørensen–Dice coefficient:
 *
 * https://en.wikipedia.org/wiki/S%C3%B8rensen%E2%80%93Dice_coefficient
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Dice
  extends AbstractEvaluationMetric
  implements StandardEvaluationMetric, InformationRetrievalEvaluationMetric {

  private static final long serialVersionUID = -6174771017324139350L;

  public static final String NAME = "Dice";

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
   * Get a short description of this metric (algorithm, forumulas etc.).
   *
   * @return a short description of this metric
   */
  @Override
  public String getMetricDescription() {
    return "Sørensen–Dice coefficient: DSC = 2TP / (2TP + FP + FN); calculated for the first class label.";
  }

  /**
   * Return true if this evaluation metric can be computed when the class is
   * nominal
   *
   * @return true if this evaluation metric can be computed when the class is
   * nominal
   */
  @Override
  public boolean appliesToNominalClass() {
    return true;
  }

  /**
   * Return true if this evaluation metric can be computed when the class is
   * numeric
   *
   * @return true if this evaluation metric can be computed when the class is
   * numeric
   */
  @Override
  public boolean appliesToNumericClass() {
    return false;
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
   * Get the value of the named statistic - for the first class label.
   *
   * @param statName the name of the statistic to compute the value for
   * @return the computed statistic or Utils.missingValue() if the statistic
   * can't be computed for some reason
   * @see #getStatistic(String, int) 
   */
  @Override
  public double getStatistic(String statName) {
    return getStatistic(statName, 0);
  }

  /**
   * Get the value of the named statistic for the given class index.
   * <p>
   * If the implementing class is extending AbstractEvaluationMetric then the
   * implementation of getStatistic(String statName) should just call this
   * method with a classIndex of 0.
   *
   * @param statName   the name of the statistic to compute the value for
   * @param classIndex the class index for which to compute the statistic
   * @return the value of the named statistic for the given class index or
   * Utils.missingValue() if the statistic can't be computed for some
   * reason
   */
  @Override
  public double getStatistic(String statName, int classIndex) {
    if (statName.equals(NAME)) {
      return 2 * m_baseEvaluation.numTruePositives(classIndex)
	/ (2 * m_baseEvaluation.numTruePositives(classIndex) + m_baseEvaluation.numFalsePositives(classIndex) + m_baseEvaluation.numFalseNegatives(classIndex));
    }
    else {
      return Utils.missingValue();
    }
  }

  /**
   * Get the weighted (by class) average for this statistic.
   *
   * @param statName the name of the statistic to compute
   * @return the weighted (by class) average value of the statistic or
   * Utils.missingValue() if this can't be computed (or isn't
   * appropriate).
   */
  @Override
  public double getClassWeightedAverageStatistic(String statName) {
    return Utils.missingValue();
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

  /**
   * Updates the statistics about a classifiers performance for the current test
   * instance. Gets called when the class is nominal. Implementers need only
   * implement this method if it is not possible to compute their statistics
   * from what is stored in the base Evaluation object.
   *
   * @param predictedDistribution the probabilities assigned to each class
   * @param instance              the instance to be classified
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
   * @param instance       the instance to be classified
   * @throws Exception if the class of the instance is not set
   */
  @Override
  public void updateStatsForPredictor(double predictedValue, Instance instance) throws Exception {
    // ignored
  }
}
