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
 * EvaluationHelper.java
 * Copyright (C) 2009-2012 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.core;

import weka.classifiers.Evaluation;

/**
 * A helper class for Evaluation related things.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class EvaluationHelper {

  /**
   * Returns a statistical value from the evaluation object.
   *
   * @param eval	the evaluation object to get the value from
   * @param statistic	the type of value to return
   * @param classIndex	the class label index, for statistics like AUC
   * @return		the determined value, Double.NaN if not found
   * @throws Exception	if evaluation fails
   */
  public static double getValue(Evaluation eval, EvaluationStatistic statistic, int classIndex) throws Exception {
    double	result;

    result = Double.NaN;

    if (statistic == EvaluationStatistic.NUMBER_CORRECT)
      result = eval.correct();
    else if (statistic == EvaluationStatistic.NUMBER_INCORRECT)
      result = eval.incorrect();
    else if (statistic == EvaluationStatistic.NUMBER_UNCLASSIFIED)
      result = eval.unclassified();
    else if (statistic == EvaluationStatistic.PERCENT_CORRECT)
      result = eval.pctCorrect();
    else if (statistic == EvaluationStatistic.PERCENT_INCORRECT)
      result = eval.pctIncorrect();
    else if (statistic == EvaluationStatistic.PERCENT_UNCLASSIFIED)
      result = eval.pctUnclassified();
    else if (statistic == EvaluationStatistic.KAPPA_STATISTIC)
      result = eval.kappa();
    else if (statistic == EvaluationStatistic.MEAN_ABSOLUTE_ERROR)
      result = eval.meanAbsoluteError();
    else if (statistic == EvaluationStatistic.ROOT_MEAN_SQUARED_ERROR)
      result = eval.rootMeanSquaredError();
    else if (statistic == EvaluationStatistic.RELATIVE_ABSOLUTE_ERROR)
      result = eval.relativeAbsoluteError();
    else if (statistic == EvaluationStatistic.ROOT_RELATIVE_SQUARED_ERROR)
      result = eval.rootRelativeSquaredError();
    else if (statistic == EvaluationStatistic.CORRELATION_COEFFICIENT)
      result = eval.correlationCoefficient();
    else if (statistic == EvaluationStatistic.SF_PRIOR_ENTROPY)
      result = eval.SFPriorEntropy();
    else if (statistic == EvaluationStatistic.SF_SCHEME_ENTROPY)
      result = eval.SFSchemeEntropy();
    else if (statistic == EvaluationStatistic.SF_ENTROPY_GAIN)
      result = eval.SFEntropyGain();
    else if (statistic == EvaluationStatistic.SF_MEAN_PRIOR_ENTROPY)
      result = eval.SFMeanPriorEntropy();
    else if (statistic == EvaluationStatistic.SF_MEAN_SCHEME_ENTROPY)
      result = eval.SFMeanSchemeEntropy();
    else if (statistic == EvaluationStatistic.SF_MEAN_ENTROPY_GAIN)
      result = eval.SFMeanEntropyGain();
    else if (statistic == EvaluationStatistic.KB_INFORMATION)
      result = eval.KBInformation();
    else if (statistic == EvaluationStatistic.KB_MEAN_INFORMATION)
      result = eval.KBMeanInformation();
    else if (statistic == EvaluationStatistic.KB_RELATIVE_INFORMATION)
      result = eval.KBRelativeInformation();
    else if (statistic == EvaluationStatistic.TRUE_POSITIVE_RATE)
      result = eval.truePositiveRate(classIndex);
    else if (statistic == EvaluationStatistic.NUM_TRUE_POSITIVES)
      result = eval.numTruePositives(classIndex);
    else if (statistic == EvaluationStatistic.FALSE_POSITIVE_RATE)
      result = eval.falsePositiveRate(classIndex);
    else if (statistic == EvaluationStatistic.NUM_FALSE_POSITIVES)
      result = eval.numFalsePositives(classIndex);
    else if (statistic == EvaluationStatistic.TRUE_NEGATIVE_RATE)
      result = eval.trueNegativeRate(classIndex);
    else if (statistic == EvaluationStatistic.NUM_TRUE_NEGATIVES)
      result = eval.numTrueNegatives(classIndex);
    else if (statistic == EvaluationStatistic.FALSE_NEGATIVE_RATE)
      result = eval.falseNegativeRate(classIndex);
    else if (statistic == EvaluationStatistic.NUM_FALSE_NEGATIVES)
      result = eval.numFalseNegatives(classIndex);
    else if (statistic == EvaluationStatistic.IR_PRECISION)
      result = eval.precision(classIndex);
    else if (statistic == EvaluationStatistic.IR_RECALL)
      result = eval.recall(classIndex);
    else if (statistic == EvaluationStatistic.F_MEASURE)
      result = eval.fMeasure(classIndex);
    else if (statistic == EvaluationStatistic.MATTHEWS_CORRELATION_COEFFICIENT)
      result = eval.matthewsCorrelationCoefficient(classIndex);
    else if (statistic == EvaluationStatistic.AREA_UNDER_ROC)
      result = eval.areaUnderROC(classIndex);
    else if (statistic == EvaluationStatistic.AREA_UNDER_PRC)
      result = eval.areaUnderPRC(classIndex);
    else if (statistic == EvaluationStatistic.WEIGHTED_TRUE_POSITIVE_RATE)
      result = eval.weightedTruePositiveRate();
    else if (statistic == EvaluationStatistic.WEIGHTED_FALSE_POSITIVE_RATE)
      result = eval.weightedFalsePositiveRate();
    else if (statistic == EvaluationStatistic.WEIGHTED_TRUE_NEGATIVE_RATE)
      result = eval.weightedTrueNegativeRate();
    else if (statistic == EvaluationStatistic.WEIGHTED_FALSE_NEGATIVE_RATE)
      result = eval.weightedFalseNegativeRate();
    else if (statistic == EvaluationStatistic.WEIGHTED_IR_PRECISION)
      result = eval.weightedPrecision();
    else if (statistic == EvaluationStatistic.WEIGHTED_IR_RECALL)
      result = eval.weightedRecall();
    else if (statistic == EvaluationStatistic.WEIGHTED_F_MEASURE)
      result = eval.weightedFMeasure();
    else if (statistic == EvaluationStatistic.WEIGHTED_MATTHEWS_CORRELATION_COEFFICIENT)
      result = eval.weightedMatthewsCorrelation();
    else if (statistic == EvaluationStatistic.WEIGHTED_AREA_UNDER_ROC)
      result = eval.weightedAreaUnderROC();
    else if (statistic == EvaluationStatistic.WEIGHTED_AREA_UNDER_PRC)
      result = eval.weightedAreaUnderPRC();
    else
      throw new IllegalArgumentException("Unhandled statistic field: " + statistic);

    return result;
  }
}
