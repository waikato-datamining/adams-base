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
 * WekaPredictionsToSpreadSheet.java
 * Copyright (C) 2013-2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.ClassCrossReference;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.statistics.StatUtils;
import adams.flow.container.WekaEvaluationContainer;
import adams.flow.core.Token;
import adams.ml.data.InstancesView;
import weka.classifiers.CrossValidationHelper;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.evaluation.Prediction;
import weka.core.Instances;

import java.util.ArrayList;

/**
 <!-- globalinfo-start -->
 * Generates a SpreadSheet object from the predictions of an Evaluation object.<br>
 * <br>
 * See also:<br>
 * adams.flow.transformer.WekaSpreadSheetToPredictions
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
 * &nbsp;&nbsp;&nbsp;default: WekaPredictionsToSpreadSheet
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
 * <pre>-add-index &lt;boolean&gt; (property: addLabelIndex)
 * &nbsp;&nbsp;&nbsp;If set to true, then the label is prefixed with the index.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-error &lt;boolean&gt; (property: showError)
 * &nbsp;&nbsp;&nbsp;If set to true, then the error will be displayed as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-relative-error &lt;boolean&gt; (property: showRelativeError)
 * &nbsp;&nbsp;&nbsp;If set to true, then the relative error will be displayed as well (numeric
 * &nbsp;&nbsp;&nbsp;class only).
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-absolute-error &lt;boolean&gt; (property: useAbsoluteError)
 * &nbsp;&nbsp;&nbsp;If set to true, then the error will be absolute (no direction).
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 *
 * <pre>-probability &lt;boolean&gt; (property: showProbability)
 * &nbsp;&nbsp;&nbsp;If set to true, then the probability of the prediction will be displayed
 * &nbsp;&nbsp;&nbsp;as well (only for nominal class attributes).
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-distribution &lt;boolean&gt; (property: showDistribution)
 * &nbsp;&nbsp;&nbsp;If set to true, then the class distribution will be displayed as well (only
 * &nbsp;&nbsp;&nbsp;for nominal class attributes).
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-weight &lt;boolean&gt; (property: showWeight)
 * &nbsp;&nbsp;&nbsp;If set to true, then the instance weight will be displayed as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-use-original-indices &lt;boolean&gt; (property: useOriginalIndices)
 * &nbsp;&nbsp;&nbsp;If set to true, the input token is a adams.flow.container.WekaEvaluationContainer
 * &nbsp;&nbsp;&nbsp;and it contains the original indices ('Original indices') then the output
 * &nbsp;&nbsp;&nbsp;will get aligned with the original data.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 *
 * <pre>-test-attributes &lt;adams.data.weka.WekaAttributeRange&gt; (property: testAttributes)
 * &nbsp;&nbsp;&nbsp;The range of attributes from the test set to add to the output (if test
 * &nbsp;&nbsp;&nbsp;data available).
 * &nbsp;&nbsp;&nbsp;default:
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; apart from attribute names (case-sensitive), the following placeholders can be used as well: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); attribute names can be surrounded by double quotes.
 * </pre>
 *
 * <pre>-measures-prefix &lt;java.lang.String&gt; (property: measuresPrefix)
 * &nbsp;&nbsp;&nbsp;The prefix to use for the measure attributes being output.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class WekaPredictionsToSpreadSheet
  extends AbstractWekaPredictionsTransformer
  implements ClassCrossReference {

  /** for serialization. */
  private static final long serialVersionUID = -1552754008462778501L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a SpreadSheet object from the predictions of an Evaluation object.";
  }

  /**
   * Returns the cross-referenced classes.
   *
   * @return		the classes
   */
  public Class[] getClassCrossReferences() {
    return new Class[]{WekaSpreadSheetToPredictions.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    Evaluation			eval;
    int				i;
    int				n;
    int				indexErr;
    int				indexRelErr;
    int				indexProb;
    int				indexDist;
    int				indexWeight;
    boolean			nominal;
    Instances			header;
    ArrayList<Prediction>	predictions;
    Prediction			pred;
    SpreadSheet			data;
    Instances 			testData;
    InstancesView		testView;
    Row				row;
    int[]			indices;

    result = null;

    if (m_InputToken.getPayload() instanceof WekaEvaluationContainer) {
      eval     = (Evaluation) ((WekaEvaluationContainer) m_InputToken.getPayload()).getValue(WekaEvaluationContainer.VALUE_EVALUATION);
      indices  = (int[]) ((WekaEvaluationContainer) m_InputToken.getPayload()).getValue(WekaEvaluationContainer.VALUE_ORIGINALINDICES);
      testData = (Instances) ((WekaEvaluationContainer) m_InputToken.getPayload()).getValue(WekaEvaluationContainer.VALUE_TESTDATA);
    }
    else {
      eval     = (Evaluation) m_InputToken.getPayload();
      indices  = null;
      testData = null;
    }
    header      = eval.getHeader();
    nominal     = header.classAttribute().isNominal();
    predictions = eval.predictions();

    if (predictions != null) {
      data = new DefaultSpreadSheet();
      data.setName("Predictions");

      // create header
      row = data.getHeaderRow();
      row.addCell("A").setContent(m_MeasuresPrefix + "Actual");
      row.addCell("P").setContent(m_MeasuresPrefix + "Predicted");
      indexErr = -1;
      if (m_ShowError) {
	indexErr = row.getCellCount();
	row.addCell("E").setContent(m_MeasuresPrefix + "Error");
      }
      indexRelErr = -1;
      if (m_ShowRelativeError && !nominal) {
	indexRelErr = row.getCellCount();
	row.addCell("RE").setContent(m_MeasuresPrefix + "Relative-Error (%)");
      }
      // probability
      indexProb = -1;
      if (m_ShowProbability && nominal) {
	indexProb = row.getCellCount();
	row.addCell("Pr").setContent(m_MeasuresPrefix + "Probability");
      }
      // distribution
      indexDist = -1;
      if (m_ShowDistribution && nominal) {
	indexDist = row.getCellCount();
	for (n = 0; n < header.classAttribute().numValues(); n++)
	  row.addCell("D" + n).setContent(m_MeasuresPrefix + "Distribution (" + header.classAttribute().value(n) + ")");
      }
      // weight
      indexWeight = -1;
      if (m_ShowWeight) {
	indexWeight = row.getCellCount();
	row.addCell("W").setContent(m_MeasuresPrefix + "Weight");
      }

      // add data
      if ((indices != null) && m_UseOriginalIndices)
	predictions = CrossValidationHelper.alignPredictions(predictions, indices);
      for (i = 0; i < predictions.size(); i++) {
	pred = predictions.get(i);
	row  = data.addRow();
	// actual
	if (Double.isNaN(pred.actual()))
	  row.addCell(0).setMissing();
	else if (nominal)
	  row.addCell(0).setContentAsString(header.classAttribute().value((int) pred.actual()));
	else
	  row.addCell(0).setContent(pred.actual());
	// predicted
	if (Double.isNaN(pred.predicted()))
	  row.addCell(1).setMissing();
	else if (nominal)
	  row.addCell(1).setContentAsString(header.classAttribute().value((int) pred.predicted()));
	else
	  row.addCell(1).setContent(pred.predicted());
	// error
	if (m_ShowError) {
	  if (nominal) {
	    row.addCell(indexErr).setContent((pred.actual() != pred.predicted() ? "true" : "false"));
	  }
	  else {
	    if (m_UseAbsoluteError)
	      row.addCell(indexErr).setContent(Math.abs(pred.actual() - pred.predicted()));
	    else
	      row.addCell(indexErr).setContent(pred.actual() - pred.predicted());
	  }
	}
	// relative error
	if (m_ShowRelativeError) {
	  if (!nominal) {
	    if (pred.actual() == 0) {
	      row.addCell(indexRelErr).setContent(Double.NaN);
	    }
	    else {
	      if (m_UseAbsoluteError)
		row.addCell(indexRelErr).setContent(Math.abs(pred.actual() - pred.predicted()) / pred.actual() * 100.0);
	      else
		row.addCell(indexRelErr).setContent((pred.actual() - pred.predicted()) / pred.actual() * 100.0);
	    }
	  }
	}
	// probability
	if (m_ShowProbability && nominal) {
	  row.addCell(indexProb).setContent(StatUtils.max(((NominalPrediction) pred).distribution()));
	}
	// distribution
	if (m_ShowDistribution && nominal) {
	  for (n = 0; n < header.classAttribute().numValues(); n++)
	    row.addCell(indexDist + n).setContent(((NominalPrediction) pred).distribution()[n]);
	}
	// weight
	if (m_ShowWeight) {
	  row.addCell(indexWeight).setContent(pred.weight());
	}
      }

      // add test data?
      if ((testData != null) && !m_TestAttributes.isEmpty()) {
	testData = filterTestData(testData);
	if (testData != null) {
	  testView = new InstancesView(testData);
	  data.mergeWith(testView);
	}
      }

      // generate output token
      m_OutputToken = new Token(data);
    }
    else {
      getLogger().severe("No predictions available from Evaluation object!");
    }

    return result;
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the generated data type
   */
  public Class[] generates() {
    return new Class[]{SpreadSheet.class};
  }
}
