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
 * Copyright (C) 2009-2017 University of Waikato, Hamilton, New Zealand
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
    switch (statistic) {
      case NUMBER_CORRECT:
	return eval.correct();
      case NUMBER_INCORRECT:
	return eval.incorrect();
      case NUMBER_UNCLASSIFIED:
	return eval.unclassified();
      case PERCENT_CORRECT:
	return eval.pctCorrect();
      case PERCENT_INCORRECT:
	return eval.pctIncorrect();
      case PERCENT_UNCLASSIFIED:
	return eval.pctUnclassified();
      case KAPPA_STATISTIC:
	return eval.kappa();
      case MEAN_ABSOLUTE_ERROR:
	return eval.meanAbsoluteError();
      case ROOT_MEAN_SQUARED_ERROR:
	return eval.rootMeanSquaredError();
      case RELATIVE_ABSOLUTE_ERROR:
	return eval.relativeAbsoluteError();
      case ROOT_RELATIVE_SQUARED_ERROR:
	return eval.rootRelativeSquaredError();
      case CORRELATION_COEFFICIENT:
	return eval.correlationCoefficient();
      case SF_PRIOR_ENTROPY:
	return eval.SFPriorEntropy();
      case SF_SCHEME_ENTROPY:
	return eval.SFSchemeEntropy();
      case SF_ENTROPY_GAIN:
	return eval.SFEntropyGain();
      case SF_MEAN_PRIOR_ENTROPY:
	return eval.SFMeanPriorEntropy();
      case SF_MEAN_SCHEME_ENTROPY:
	return eval.SFMeanSchemeEntropy();
      case SF_MEAN_ENTROPY_GAIN:
	return eval.SFMeanEntropyGain();
      case KB_INFORMATION:
	return eval.KBInformation();
      case KB_MEAN_INFORMATION:
	return eval.KBMeanInformation();
      case KB_RELATIVE_INFORMATION:
	return eval.KBRelativeInformation();
      case TRUE_POSITIVE_RATE:
	return eval.truePositiveRate(classIndex);
      case NUM_TRUE_POSITIVES:
	return eval.numTruePositives(classIndex);
      case FALSE_POSITIVE_RATE:
	return eval.falsePositiveRate(classIndex);
      case NUM_FALSE_POSITIVES:
	return eval.numFalsePositives(classIndex);
      case TRUE_NEGATIVE_RATE:
	return eval.trueNegativeRate(classIndex);
      case NUM_TRUE_NEGATIVES:
	return eval.numTrueNegatives(classIndex);
      case FALSE_NEGATIVE_RATE:
	return eval.falseNegativeRate(classIndex);
      case NUM_FALSE_NEGATIVES:
	return eval.numFalseNegatives(classIndex);
      case IR_PRECISION:
	return eval.precision(classIndex);
      case IR_RECALL:
	return eval.recall(classIndex);
      case F_MEASURE:
	return eval.fMeasure(classIndex);
      case MATTHEWS_CORRELATION_COEFFICIENT:
	return eval.matthewsCorrelationCoefficient(classIndex);
      case AREA_UNDER_ROC:
	return eval.areaUnderROC(classIndex);
      case AREA_UNDER_PRC:
	return eval.areaUnderPRC(classIndex);
      case WEIGHTED_TRUE_POSITIVE_RATE:
	return eval.weightedTruePositiveRate();
      case WEIGHTED_FALSE_POSITIVE_RATE:
	return eval.weightedFalsePositiveRate();
      case WEIGHTED_TRUE_NEGATIVE_RATE:
	return eval.weightedTrueNegativeRate();
      case WEIGHTED_FALSE_NEGATIVE_RATE:
	return eval.weightedFalseNegativeRate();
      case WEIGHTED_IR_PRECISION:
	return eval.weightedPrecision();
      case WEIGHTED_IR_RECALL:
	return eval.weightedRecall();
      case WEIGHTED_F_MEASURE:
	return eval.weightedFMeasure();
      case WEIGHTED_MATTHEWS_CORRELATION_COEFFICIENT:
	return eval.weightedMatthewsCorrelation();
      case WEIGHTED_AREA_UNDER_ROC:
	return eval.weightedAreaUnderROC();
      case WEIGHTED_AREA_UNDER_PRC:
	return eval.weightedAreaUnderPRC();
      case UNWEIGHTED_MACRO_F_MEASURE:
	return eval.unweightedMacroFmeasure();
      case UNWEIGHTED_MICRO_F_MEASURE:
	return eval.unweightedMicroFmeasure();
      default:
	throw new IllegalArgumentException("Unhandled statistic field: " + statistic);
    }
  }
}
