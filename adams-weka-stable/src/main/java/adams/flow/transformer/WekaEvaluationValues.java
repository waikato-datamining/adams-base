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
 * WekaEvaluationValues.java
 * Copyright (C) 2009-2017 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.weka.WekaLabelRange;
import adams.flow.container.WekaEvaluationContainer;
import adams.flow.core.EvaluationHelper;
import adams.flow.core.EvaluationStatistic;
import adams.flow.core.Token;
import weka.classifiers.Evaluation;

/**
 <!-- globalinfo-start -->
 * Generates a spreadsheet from statistics of an Evaluation object.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.classifiers.Evaluation<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.WekaEvaluationContainer<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.WekaEvaluationContainer: Evaluation, Model
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: WekaEvaluationValues
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
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 * 
 * <pre>-statistic &lt;Number correct (nominal)|Number incorrect (nominal)|Number unclassified (nominal)|Percent correct (nominal)|Percent incorrect (nominal)|Percent unclassified (nominal)|Kappa statistic (nominal)|Mean absolute error|Root mean squared error|Relative absolute error|Root relative squared error|Correlation coefficient (numeric)|SF prior entropy|SF scheme entropy|SF entropy gain|SF mean prior entropy|SF mean scheme entropy|SF mean entropy gain|KB information (nominal)|KB mean information (nominal)|KB relative information (nominal)|True positive rate (nominal)|Num true positives (nominal)|False positive rate (nominal)|Num false positives (nominal)|True negative rate (nominal)|Num true negatives (nominal)|False negative rate (nominal)|Num false negatives (nominal)|IR precision (nominal)|IR recall (nominal)|F measure (nominal)|Matthews correlation coefficient (nominal)|Area under ROC (nominal)|Area under PRC (nominal)|Weighted true positive rate (nominal)|Weighted false positive rate (nominal)|Weighted true negative rate (nominal)|Weighted false negative rate (nominal)|Weighted IR precision (nominal)|Weighted IR recall (nominal)|Weighted F measure (nominal)|Weighted Matthews correlation coefficient (nominal)|Weighted area under ROC (nominal)|Weighted area under PRC (nominal)&gt; [-statistic ...] (property: statisticValues)
 * &nbsp;&nbsp;&nbsp;The evaluation values to extract and turn into a spreadsheet.
 * &nbsp;&nbsp;&nbsp;default: PERCENT_CORRECT, ROOT_MEAN_SQUARED_ERROR, ROOT_RELATIVE_SQUARED_ERROR
 * </pre>
 * 
 * <pre>-index &lt;adams.core.Range&gt; (property: classIndex)
 * &nbsp;&nbsp;&nbsp;The range of class label indices (eg used for AUC); A range is a comma-separated 
 * &nbsp;&nbsp;&nbsp;list of single 1-based indices or sub-ranges of indices ('start-end'); '
 * &nbsp;&nbsp;&nbsp;inv(...)' inverts the range '...'; the following placeholders can be used 
 * &nbsp;&nbsp;&nbsp;as well: first, second, third, last_2, last_1, last
 * &nbsp;&nbsp;&nbsp;default: first
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaEvaluationValues
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -1977976026411517458L;

  /** the comparison fields. */
  protected EvaluationStatistic[] m_StatisticValues;

  /** the range of the class labels. */
  protected WekaLabelRange m_ClassIndex;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a spreadsheet from statistics of an Evaluation object.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "statistic", "statisticValues",
	    new EvaluationStatistic[]{
	      EvaluationStatistic.PERCENT_CORRECT,
	      EvaluationStatistic.ROOT_MEAN_SQUARED_ERROR,
	      EvaluationStatistic.ROOT_RELATIVE_SQUARED_ERROR});

    m_OptionManager.add(
	    "index", "classIndex",
	    new WekaLabelRange(WekaLabelRange.FIRST));
  }

  /**
   * Sets the values to extract.
   *
   * @param value	the value
   */
  public void setStatisticValues(EvaluationStatistic[] value) {
    m_StatisticValues = value;
    reset();
  }

  /**
   * Returns the values to extract.
   *
   * @return		the value
   */
  public EvaluationStatistic[] getStatisticValues() {
    return m_StatisticValues;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String statisticValuesTipText() {
    return "The evaluation values to extract and turn into a spreadsheet.";
  }

  /**
   * Sets the range of class labels indices (1-based).
   *
   * @param value	the label indices
   */
  public void setClassIndex(WekaLabelRange value) {
    m_ClassIndex = value;
    reset();
  }

  /**
   * Returns the current range of class label indices (1-based).
   *
   * @return		the label indices
   */
  public WekaLabelRange getClassIndex() {
    return m_ClassIndex;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String classIndexTipText() {
    return "The range of class label indices (eg used for AUC).";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "classIndex", m_ClassIndex, "Labels: ");
    result += QuickInfoHelper.toString(this, "statisticValues", m_StatisticValues.length + " value" + (m_StatisticValues.length != 1 ? "s" : ""), ", ");
    
    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->weka.classifiers.Evaluation.class, adams.flow.container.WekaEvaluationContainer.class<!-- flow-accepts-end -->
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Evaluation.class, WekaEvaluationContainer.class};
  }
  
  /**
   * Adds the specified statistic to the spreadsheet.
   * 
   * @param eval	the {@link Evaluation} object to get the statist from
   * @param sheet	the sheet to add the data to
   * @param statistic	the statistic to add
   * @param classIndex	the class index to use (for class-specific stats)
   * @param useIndex	whether to use the index in the "Statistic" column
   * @return		null if successfully added, otherwise error message
   */
  protected String addStatistic(Evaluation eval, SpreadSheet sheet, EvaluationStatistic statistic, int classIndex, boolean useIndex) {
    String	result;
    Row		row;
    double	value;
    String	name;
    
    result = null;
    
    try {
      value = EvaluationHelper.getValue(eval, statistic, classIndex);
      row   = sheet.addRow();
      name  = statistic.toDisplayShort();
      if (useIndex && statistic.isPerClass())
	name += " (" + eval.getHeader().classAttribute().value(classIndex) + ")";
      row.addCell(0).setContent(name);
      row.addCell(1).setContent(Double.toString(value));
    }
    catch (Exception e) {
      result = handleException("Error retrieving value for '" + statistic + "':\n", e);
    }

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    Evaluation		eval;
    SpreadSheet		sheet;
    int[]		indices;
    String		msg;

    result = null;

    // fill spreadsheet
    if (m_InputToken.getPayload() instanceof WekaEvaluationContainer)
      eval = (Evaluation) ((WekaEvaluationContainer) m_InputToken.getPayload()).getValue(WekaEvaluationContainer.VALUE_EVALUATION);
    else
      eval = (Evaluation) m_InputToken.getPayload();
    m_ClassIndex.setData(eval.getHeader().classAttribute());
    indices = m_ClassIndex.getIntIndices();
    sheet = new DefaultSpreadSheet();
    sheet.getHeaderRow().addCell("0").setContent("Statistic");
    sheet.getHeaderRow().addCell("1").setContent("Value");
    if (indices.length <= 1) {
      for (EvaluationStatistic statistic: m_StatisticValues) {
	msg = addStatistic(eval, sheet, statistic, (indices.length == 0 ? 0 : indices[0]), true);
	if (msg != null) {
	  if (result == null)
	    result = "";
	  else
	    result += "\n";
	  result += msg;
	}
      }
    }
    else if (indices.length > 1) {
      // not class-specific stats
      for (EvaluationStatistic statistic: m_StatisticValues) {
	if (statistic.isPerClass())
	  continue;
	msg = addStatistic(eval, sheet, statistic, 0, false);  // class index is irrelevant
	if (msg != null) {
	  if (result == null)
	    result = "";
	  else
	    result += "\n";
	  result += msg;
	}
      }
      // class-specific stats
      for (int index: indices) {
	for (EvaluationStatistic statistic: m_StatisticValues) {
	  if (!statistic.isPerClass())
	    continue;
	  msg = addStatistic(eval, sheet, statistic, index, true);
	  if (msg != null) {
	    if (result == null)
	      result = "";
	    else
	      result += "\n";
	    result += msg;
	  }
	}
      }
    }

    // generate output token
    m_OutputToken = new Token(sheet);

    return result;
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.data.spreadsheet.SpreadSheet.class<!-- flow-generates-end -->
   */
  @Override
  public Class[] generates() {
    return new Class[]{SpreadSheet.class};
  }
}
