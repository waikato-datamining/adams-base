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
 * WekaPredictionsToInstances.java
 * Copyright (C) 2009-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.data.statistics.StatUtils;
import adams.flow.container.WekaEvaluationContainer;
import adams.flow.core.Token;
import weka.classifiers.CrossValidationHelper;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.evaluation.Prediction;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

import java.util.ArrayList;

/**
 <!-- globalinfo-start -->
 * Generates weka.core.Instances from the predictions of an Evaluation object.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.classifiers.Evaluation<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.WekaEvaluationContainer<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.WekaEvaluationContainer: Evaluation, Model, Prediction output, Original indices
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: WekaPredictionsToInstances
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console.
 * &nbsp;&nbsp;&nbsp;default: false
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
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaPredictionsToInstances
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
    return "Generates weka.core.Instances from the predictions of an Evaluation object.";
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
    ArrayList<Attribute>	atts;
    ArrayList<String>		values;
    ArrayList<Prediction>	predictions;
    Prediction			pred;
    double[]			vals;
    Instances			data;
    int[]			indices;

    result = null;

    if (m_InputToken.getPayload() instanceof WekaEvaluationContainer) {
      eval    = (Evaluation) ((WekaEvaluationContainer) m_InputToken.getPayload()).getValue(WekaEvaluationContainer.VALUE_EVALUATION);
      indices = (int[]) ((WekaEvaluationContainer) m_InputToken.getPayload()).getValue(WekaEvaluationContainer.VALUE_ORIGINALINDICES);
    }
    else {
      eval    = (Evaluation) m_InputToken.getPayload();
      indices = null;
    }
    header      = eval.getHeader();
    nominal     = header.classAttribute().isNominal();
    predictions = eval.predictions();

    if (predictions != null) {
      // create header
      atts = new ArrayList<Attribute>();
      // actual
      if (nominal && m_AddLabelIndex) {
	values = new ArrayList<String>();
	for (i = 0; i < header.classAttribute().numValues(); i++)
	  values.add((i+1) + ":" + header.classAttribute().value(i));
	atts.add(new Attribute("Actual", values));
      }
      else {
	atts.add(header.classAttribute().copy("Actual"));
      }
      // predicted
      if (nominal && m_AddLabelIndex) {
	values = new ArrayList<String>();
	for (i = 0; i < header.classAttribute().numValues(); i++)
	  values.add((i+1) + ":" + header.classAttribute().value(i));
	atts.add(new Attribute("Predicted", values));
      }
      else {
	atts.add(header.classAttribute().copy("Predicted"));
      }
      // error
      indexErr = -1;
      if (m_ShowError) {
	indexErr = atts.size();
	if (nominal) {
	  values = new ArrayList<String>();
	  values.add("n");
	  values.add("y");
	  atts.add(new Attribute("Error", values));
	}
	else {
	  atts.add(new Attribute("Error"));
	}
      }
      // probability
      indexProb = -1;
      if (m_ShowProbability && nominal) {
	indexProb = atts.size();
	atts.add(new Attribute("Probability"));
      }
      // distribution
      indexDist = -1;
      if (m_ShowDistribution && nominal) {
	indexDist = atts.size();
	for (n = 0; n < header.classAttribute().numValues(); n++)
	  atts.add(new Attribute("Distribution (" + header.classAttribute().value(n) + ")"));
      }
      // weight
      indexWeight = -1;
      if (m_ShowWeight) {
	indexWeight = atts.size();
	atts.add(new Attribute("Weight"));
      }

      data = new Instances("Predictions", atts, predictions.size());
      data.setClassIndex(1);  // predicted

      // add data
      if (indices != null)
        predictions = CrossValidationHelper.alignPredictions(predictions, indices);
      for (i = 0; i < predictions.size(); i++) {
	pred = predictions.get(i);
	vals = new double[data.numAttributes()];
	// actual
	vals[0] = pred.actual();
	// predicted
	vals[1] = pred.predicted();
	// error
	if (m_ShowError) {
	  if (nominal)
	    vals[indexErr] = ((pred.actual() != pred.predicted()) ? 1.0 : 0.0);
	  else
	    vals[indexErr] = Math.abs(pred.actual() - pred.predicted());
	}
	// probability
	if (m_ShowProbability && nominal) {
	  vals[indexProb] = StatUtils.max(((NominalPrediction) pred).distribution());
	}
	// distribution
	if (m_ShowDistribution && nominal) {
	  for (n = 0; n < header.classAttribute().numValues(); n++)
	    vals[indexDist + n] = ((NominalPrediction) pred).distribution()[n];
	}
	// weight
	if (m_ShowWeight) {
	  vals[indexWeight] = pred.weight();
	}
	// add row
	data.add(new DenseInstance(1.0, vals));
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
   * @return		<!-- flow-generates-start -->weka.core.Instances.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{Instances.class};
  }
}
