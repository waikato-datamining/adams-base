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
 * WekaEvaluationValuePicker.java
 * Copyright (C) 2009-2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.data.weka.WekaLabelIndex;
import adams.flow.container.WekaEvaluationContainer;
import adams.flow.core.EvaluationHelper;
import adams.flow.core.EvaluationStatistic;
import adams.flow.core.Token;
import weka.classifiers.Evaluation;


/**
 <!-- globalinfo-start -->
 * Picks a specific value from an evaluation object.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.classifiers.Evaluation<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.WekaEvaluationContainer<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Double<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.WekaEvaluationContainer: Evaluation, Model, Prediction output, Original indices, Test data
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: WekaEvaluationValuePicker
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-statistic &lt;NUMBER_CORRECT|NUMBER_INCORRECT|NUMBER_UNCLASSIFIED|NUMBER_TOTAL|PERCENT_CORRECT|PERCENT_INCORRECT|PERCENT_UNCLASSIFIED|KAPPA_STATISTIC|MEAN_ABSOLUTE_ERROR|ROOT_MEAN_SQUARED_ERROR|RELATIVE_ABSOLUTE_ERROR|ROOT_RELATIVE_SQUARED_ERROR|CORRELATION_COEFFICIENT|SF_PRIOR_ENTROPY|SF_SCHEME_ENTROPY|SF_ENTROPY_GAIN|SF_MEAN_PRIOR_ENTROPY|SF_MEAN_SCHEME_ENTROPY|SF_MEAN_ENTROPY_GAIN|KB_INFORMATION|KB_MEAN_INFORMATION|KB_RELATIVE_INFORMATION|TRUE_POSITIVE_RATE|NUM_TRUE_POSITIVES|FALSE_POSITIVE_RATE|NUM_FALSE_POSITIVES|TRUE_NEGATIVE_RATE|NUM_TRUE_NEGATIVES|FALSE_NEGATIVE_RATE|NUM_FALSE_NEGATIVES|IR_PRECISION|IR_RECALL|F_MEASURE|MATTHEWS_CORRELATION_COEFFICIENT|AREA_UNDER_ROC|AREA_UNDER_PRC|WEIGHTED_TRUE_POSITIVE_RATE|WEIGHTED_FALSE_POSITIVE_RATE|WEIGHTED_TRUE_NEGATIVE_RATE|WEIGHTED_FALSE_NEGATIVE_RATE|WEIGHTED_IR_PRECISION|WEIGHTED_IR_RECALL|WEIGHTED_F_MEASURE|WEIGHTED_MATTHEWS_CORRELATION_COEFFICIENT|WEIGHTED_AREA_UNDER_ROC|WEIGHTED_AREA_UNDER_PRC|UNWEIGHTED_MACRO_F_MEASURE|UNWEIGHTED_MICRO_F_MEASURE|BIAS|MSLE|RSQUARED|SDR|RPD&gt; (property: statisticValue)
 * &nbsp;&nbsp;&nbsp;The evaluation value to extract.
 * &nbsp;&nbsp;&nbsp;default: PERCENT_CORRECT
 * </pre>
 *
 * <pre>-index &lt;adams.data.weka.WekaLabelIndex&gt; (property: classIndex)
 * &nbsp;&nbsp;&nbsp;The class label index (eg used for AUC).
 * &nbsp;&nbsp;&nbsp;default: first
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; apart from label names (case-sensitive), the following placeholders can be used as well: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); label names can be surrounded by double quotes.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class WekaEvaluationValuePicker
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -3113058781746945626L;

  /** the comparison field. */
  protected EvaluationStatistic m_StatisticValue;

  /** the index of the class label. */
  protected WekaLabelIndex m_ClassIndex;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Picks a specific value from an evaluation object.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "statistic", "statisticValue",
      EvaluationStatistic.PERCENT_CORRECT);

    m_OptionManager.add(
      "index", "classIndex",
      new WekaLabelIndex(WekaLabelIndex.FIRST));
  }

  /**
   * Sets the value to extract.
   *
   * @param value	the value
   */
  public void setStatisticValue(EvaluationStatistic value) {
    m_StatisticValue = value;
    reset();
  }

  /**
   * Returns the value to extract.
   *
   * @return		the value
   */
  public EvaluationStatistic getStatisticValue() {
    return m_StatisticValue;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String statisticValueTipText() {
    return "The evaluation value to extract.";
  }

  /**
   * Sets the class label index (1-based).
   *
   * @param value	the label index
   */
  public void setClassIndex(WekaLabelIndex value) {
    m_ClassIndex = value;
    reset();
  }

  /**
   * Returns the current class label index (1-based).
   *
   * @return		the label index
   */
  public WekaLabelIndex getClassIndex() {
    return m_ClassIndex;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String classIndexTipText() {
    return "The class label index (eg used for AUC).";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "classIndex", m_ClassIndex);
    result += QuickInfoHelper.toString(this, "statisticValue", m_StatisticValue, ": ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->weka.classifiers.Evaluation.class, adams.flow.container.WekaEvaluationContainer.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Evaluation.class, WekaEvaluationContainer.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    double	value;
    Evaluation	eval;

    result = null;

    value = Double.NaN;
    if (m_InputToken.getPayload() instanceof WekaEvaluationContainer)
      eval = (Evaluation) ((WekaEvaluationContainer) m_InputToken.getPayload()).getValue(WekaEvaluationContainer.VALUE_EVALUATION);
    else
      eval = (Evaluation) m_InputToken.getPayload();
    try {
      m_ClassIndex.setData(eval.getHeader().classAttribute());
      value         = EvaluationHelper.getValue(eval, m_StatisticValue, m_ClassIndex.getIntIndex());
      m_OutputToken = new Token(value);
    }
    catch (Exception e) {
      result = handleException("Error retrieving value for '" + m_StatisticValue + "':\n", e);
    }

    return result;
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->java.lang.Double.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{Double.class};
  }
}
