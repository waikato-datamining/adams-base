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
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import weka.classifiers.Evaluation;
import adams.core.QuickInfoHelper;
import adams.flow.container.WekaEvaluationContainer;
import adams.flow.core.EvaluationHelper;
import adams.flow.core.EvaluationStatistic;
import adams.flow.core.Token;


/**
 <!-- globalinfo-start -->
 * Picks a specific value from an evaluation object.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input/output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.classifiers.Evaluation<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Double<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D (property: debug)
 * &nbsp;&nbsp;&nbsp;If set to true, scheme may output additional info to the console.
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: EvaluationValuePicker
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 *
 * <pre>-statistic &lt;ELAPSED_TIME_TRAINING|ELAPSED_TIME_TESTING|USERCPU_TIME_TRAINING|USERCPU_TIME_TESTING|SERIALIZED_MODEL_SIZE|SERIALIZED_TRAIN_SET_SIZE|SERIALIZED_TEST_SET_SIZE|NUMBER_OF_TRAINING_INSTANCES|NUMBER_OF_TESTING_INSTANCES|NUMBER_CORRECT|NUMBER_INCORRECT|NUMBER_UNCLASSIFIED|PERCENT_CORRECT|PERCENT_INCORRECT|PERCENT_UNCLASSIFIED|KAPPA_STATISTIC|MEAN_ABSOLUTE_ERROR|ROOT_MEAN_SQUARED_ERROR|RELATIVE_ABSOLUTE_ERROR|ROOT_RELATIVE_SQUARED_ERROR|CORRELATION_COEFFICIENT|SF_PRIOR_ENTROPY|SF_SCHEME_ENTROPY|SF_ENTROPY_GAIN|SF_MEAN_PRIOR_ENTROPY|SF_MEAN_SCHEME_ENTROPY|SF_MEAN_ENTROPY_GAIN|KB_INFORMATION|KB_MEAN_INFORMATION|KB_RELATIVE_INFORMATION|TRUE_POSITIVE_RATE|NUM_TRUE_POSITIVES|FALSE_POSITIVE_RATE|NUM_FALSE_POSITIVES|TRUE_NEGATIVE_RATE|NUM_TRUE_NEGATIVES|FALSE_NEGATIVE_RATE|NUM_FALSE_NEGATIVES|IR_PRECISION|IR_RECALL|F_MEASURE|AREA_UNDER_ROC&gt; (property: statisticValue)
 * &nbsp;&nbsp;&nbsp;The evaluation value to extract.
 * &nbsp;&nbsp;&nbsp;default: PERCENT_CORRECT
 * </pre>
 *
 * <pre>-index &lt;int&gt; (property: classIndex)
 * &nbsp;&nbsp;&nbsp;The class label index (eg used for AUC).
 * &nbsp;&nbsp;&nbsp;default: 1
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaEvaluationValuePicker
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -3113058781746945626L;

  /** the comparison field. */
  protected EvaluationStatistic m_StatisticValue;

  /** the index of the class label. */
  protected int m_ClassIndex;

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
	    1);
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
  public void setClassIndex(int value) {
    m_ClassIndex = value;
    reset();
  }

  /**
   * Returns the current class label index (1-based).
   *
   * @return		the label index
   */
  public int getClassIndex() {
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
   * @return		<!-- flow-accepts-start -->weka.classifiers.Evaluation.class<!-- flow-accepts-end -->
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
      value         = EvaluationHelper.getValue(eval, m_StatisticValue, m_ClassIndex);
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
