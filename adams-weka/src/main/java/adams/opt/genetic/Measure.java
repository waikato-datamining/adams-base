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
 * Measure.java
 * Copyright (C) 2015-2020 University of Waikato, Hamilton, NZ
 */

package adams.opt.genetic;

import adams.data.weka.WekaLabelIndex;
import adams.flow.core.EvaluationHelper;
import adams.flow.core.EvaluationStatistic;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.Instances;

/**
 * The measure to use for evaluating.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public enum Measure {

  /** Accuracy. */
  ACC(false, true, false),
  /** Correlation coefficient. */
  CC(false, false, true),
  /** Mean absolute error. */
  MAE(true, true, true),
  /** Mean squared logarithmic error. */
  MSLE(false, false, true),
  /** Relative absolute error. */
  RAE(true, true, true),
  /** Root mean squared error. */
  RMSE(true, true, true),
  /** Root relative squared error. */
  RRSE(true, true, true),
  /** R^2. */
  RSQUARED(false, false, true),
  /** Kappa. */
  KAPPA(false, true, false),
  /** false positive rate. */
  FALSE_POS_RATE(true, true, false, true),
  /** false negative rate. */
  FALSE_NEG_RATE(true, true, false, true),
  /** true positive rate. */
  TRUE_POS_RATE(false, true, false, true),
  /** true negative rate. */
  TRUE_NEG_RATE(false, true, false, true),
  /** F measure. */
  F_MEASURE(false, true, false, true),
  /** precision. */
  PRECISION(false, true, false, true),
  /** recall. */
  RECALL(false, true, false, true),
  /** area under receiver operator curve. */
  AREA_UNDER_ROC(false, true, false, true),
  /** area under precision recall curve. */
  AREA_UNDER_PRC(false, true, false, true),
  ;


  /** whether the measure is multiplied by -1 or not. Only used in sorting. */
  private boolean m_Negative;

  /** whether a nominal class is allowed. */
  private boolean m_Nominal;

  /** whether a numeric class is allowed. */
  private boolean m_Numeric;

  /** whether per class label. */
  private boolean m_PerClassLabel;

  /**
   * initializes the measure with the given flags.
   *
   * @param negative	whether measures gets multiplied with -1
   * @param nominal	whether used for nominal classes
   * @param numeric	whether used for numeric classes
   */
  private Measure(boolean negative, boolean nominal, boolean numeric) {
    this(negative, nominal, numeric, false);
  }

  /**
   * initializes the measure with the given flags.
   *
   * @param negative	whether measures gets multiplied with -1
   * @param nominal	whether used for nominal classes
   * @param numeric	whether used for numeric classes
   * @param perClassLabel  	whether this measure is per class label
   */
  private Measure(boolean negative, boolean nominal, boolean numeric, boolean perClassLabel) {
    m_Negative      = negative;
    m_Nominal       = nominal;
    m_Numeric       = numeric;
    m_PerClassLabel = perClassLabel;
  }

  /**
   * Converts the measure into {@link EvaluationStatistic}.
   *
   * @return		the converted statistic
   */
  public EvaluationStatistic toEvaluationStatistic() {
    switch (this) {
      case ACC:
	return EvaluationStatistic.PERCENT_CORRECT;
      case CC:
        return EvaluationStatistic.CORRELATION_COEFFICIENT;
      case MAE:
        return EvaluationStatistic.MEAN_ABSOLUTE_ERROR;
      case RAE:
        return EvaluationStatistic.RELATIVE_ABSOLUTE_ERROR;
      case RMSE:
        return EvaluationStatistic.ROOT_MEAN_SQUARED_ERROR;
      case RRSE:
        return EvaluationStatistic.ROOT_RELATIVE_SQUARED_ERROR;
      case MSLE:
        return EvaluationStatistic.MSLE;
      case RSQUARED:
        return EvaluationStatistic.RSQUARED;
      case KAPPA:
        return EvaluationStatistic.KAPPA_STATISTIC;
      case FALSE_POS_RATE:
        return EvaluationStatistic.FALSE_POSITIVE_RATE;
      case FALSE_NEG_RATE:
        return EvaluationStatistic.FALSE_NEGATIVE_RATE;
      case TRUE_POS_RATE:
        return EvaluationStatistic.TRUE_POSITIVE_RATE;
      case TRUE_NEG_RATE:
        return EvaluationStatistic.TRUE_NEGATIVE_RATE;
      case F_MEASURE:
        return EvaluationStatistic.F_MEASURE;
      case PRECISION:
        return EvaluationStatistic.IR_PRECISION;
      case RECALL:
        return EvaluationStatistic.IR_RECALL;
      case AREA_UNDER_PRC:
        return EvaluationStatistic.AREA_UNDER_PRC;
      case AREA_UNDER_ROC:
        return EvaluationStatistic.AREA_UNDER_ROC;
      default:
        throw new IllegalStateException("Unhandled measure: " + this);
    }
  }

  /**
   * Extracts the measure from the Evaluation object.
   *
   * @param evaluation	the evaluation to use
   * @param adjust	whether to just the measure
   * @param classLabel 	the index of the class label to use (for per-class-label stats)
   * @return		the measure
   * @see		#adjust(double)
   * @throws Exception	in case the retrieval of the measure fails
   */
  public double extract(Evaluation evaluation, boolean adjust, int classLabel) throws Exception {
    double result;

    result = EvaluationHelper.getValue(evaluation, toEvaluationStatistic(), classLabel);

    if (adjust)
      result = adjust(result);

    return result;
  }

  /**
   * Adjusts the measure value for sorting: either multiplies it with -1 or 1.
   *
   * @param measure	the raw measure
   * @return		the adjusted measure
   */
  public double adjust(double measure) {
    if (m_Negative)
      return -measure;
    else
      return measure;
  }

  /**
   * Checks whether the data can be used with this measure.
   *
   * @param data	the data to check
   * @param index 	the index for per-class-label measures
   * @return		null if valid, otherwise error message
   */
  public String isValid(Instances data, String index) {
    WekaLabelIndex	idx;
    int			idxInt;

    if (data.classIndex() == -1)
      return "No class attribute set!";

    idx = new WekaLabelIndex(index);
    idx.setData(data.classAttribute());
    idxInt = idx.getIntIndex();

    if (data.classAttribute().isNominal()) {
      if (!m_Nominal)
        return this + " is not for nominal classes!";
      if (isPerClassLabel()) {
	if (idxInt == -1)
	  return "Failed to locate class label '" + index + "'!";
      }
    }
    else if (data.classAttribute().isNumeric()) {
      if (!m_Numeric)
        return this + " is not for numeric classes!";
    }
    else {
      return "Class attribute type '" + Attribute.typeToString(data.classAttribute().type()) + "' not handled!";
    }

    return null;
  }

  /**
   * Returns whether the measure is per class label.
   *
   * @return		true if per class label
   */
  public boolean isPerClassLabel() {
    return m_PerClassLabel;
  }
}
