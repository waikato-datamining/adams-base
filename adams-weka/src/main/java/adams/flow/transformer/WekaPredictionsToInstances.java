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
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.util.ArrayList;

import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.evaluation.Prediction;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import adams.data.statistics.StatUtils;
import adams.flow.container.WekaEvaluationContainer;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Generates weka.core.Instances from the predictions of an Evaluation object.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input/output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;weka.classifiers.Evaluation<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br/>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
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
 * &nbsp;&nbsp;&nbsp;default: PredictionsToInstances
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

    result = null;

    if (m_InputToken.getPayload() instanceof WekaEvaluationContainer)
      eval = (Evaluation) ((WekaEvaluationContainer) m_InputToken.getPayload()).getValue(WekaEvaluationContainer.VALUE_EVALUATION);
    else
      eval = (Evaluation) m_InputToken.getPayload();
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
      for (i = 0; i < predictions.size(); i++) {
	pred = (Prediction) predictions.get(i);
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
