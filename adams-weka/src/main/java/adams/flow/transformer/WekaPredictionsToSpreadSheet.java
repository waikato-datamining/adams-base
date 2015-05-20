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
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.util.ArrayList;

import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.evaluation.Prediction;
import weka.core.Instances;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.statistics.StatUtils;
import adams.flow.container.WekaEvaluationContainer;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Generates a SpreadSheet object from the predictions of an Evaluation object.
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
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: WekaPredictionsToSpreadSheet
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
 * <pre>-add-index (property: addLabelIndex)
 * &nbsp;&nbsp;&nbsp;If set to true, then the label is prefixed with the index.
 * </pre>
 * 
 * <pre>-error (property: showError)
 * &nbsp;&nbsp;&nbsp;If set to true, then the error will be displayed as well.
 * </pre>
 * 
 * <pre>-probability (property: showProbability)
 * &nbsp;&nbsp;&nbsp;If set to true, then the probability of the prediction will be displayed 
 * &nbsp;&nbsp;&nbsp;as well (only for nominal class attributes).
 * </pre>
 * 
 * <pre>-distribution (property: showDistribution)
 * &nbsp;&nbsp;&nbsp;If set to true, then the class distribution will be displayed as well (only 
 * &nbsp;&nbsp;&nbsp;for nominal class attributes).
 * </pre>
 * 
 * <pre>-weight (property: showWeight)
 * &nbsp;&nbsp;&nbsp;If set to true, then the instance weight will be displayed as well.
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaPredictionsToSpreadSheet
  extends AbstractWekaPredictionsTransformer {

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
    int				indexProb;
    int				indexDist;
    int				indexWeight;
    boolean			nominal;
    Instances			header;
    ArrayList<Prediction>	predictions;
    Prediction			pred;
    SpreadSheet			data;
    Row				row;

    result = null;

    if (m_InputToken.getPayload() instanceof WekaEvaluationContainer)
      eval = (Evaluation) ((WekaEvaluationContainer) m_InputToken.getPayload()).getValue(WekaEvaluationContainer.VALUE_EVALUATION);
    else
      eval = (Evaluation) m_InputToken.getPayload();
    header      = eval.getHeader();
    nominal     = header.classAttribute().isNominal();
    predictions = eval.predictions();

    if (predictions != null) {
      data = new SpreadSheet();
      data.setName("Predictions");
      
      // create header
      row = data.getHeaderRow();
      row.addCell("A").setContent("Actual");
      row.addCell("P").setContent("Predicted");
      indexErr = -1;
      if (m_ShowError) {
	indexErr = row.getCellCount();
	row.addCell("E").setContent("Error");
      }
      // probability
      indexProb = -1;
      if (m_ShowProbability && nominal) {
	indexProb = row.getCellCount();
	row.addCell("Pr").setContent("Probability");
      }
      // distribution
      indexDist = -1;
      if (m_ShowDistribution && nominal) {
	indexDist = row.getCellCount();
	for (n = 0; n < header.classAttribute().numValues(); n++)
	  row.addCell("D" + n).setContent("Distribution (" + header.classAttribute().value(n) + ")");
      }
      // weight
      indexWeight = -1;
      if (m_ShowWeight) {
	indexWeight = row.getCellCount();
	row.addCell("W").setContent("Weight");
      }

      // add data
      for (i = 0; i < predictions.size(); i++) {
	pred = (Prediction) predictions.get(i);
	row  = data.addRow();
	// actual
	row.addCell(0).setContent(pred.actual());
	// predicted
	row.addCell(1).setContent(pred.predicted());
	// error
	if (m_ShowError) {
	  if (nominal)
	    row.addCell(indexErr).setContent((pred.actual() != pred.predicted() ? "y" : "n"));
	  else
	    row.addCell(indexErr).setContent(Math.abs(pred.actual() - pred.predicted()));
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
   * @return		<!-- flow-generates-start -->adams.data.spreadsheet.SpreadSheet.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{SpreadSheet.class};
  }
}
