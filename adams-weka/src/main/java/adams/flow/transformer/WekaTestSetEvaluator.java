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
 * WekaTestSetEvaluator.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.output.prediction.Null;
import weka.core.Instances;
import adams.core.QuickInfoHelper;
import adams.flow.container.WekaEvaluationContainer;
import adams.flow.container.WekaModelContainer;
import adams.flow.core.CallableActorReference;
import adams.flow.core.Token;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;
import adams.flow.source.CallableSource;

/**
 <!-- globalinfo-start -->
 * Evaluates a trained classifier (obtained from input) on the dataset obtained from the callable actor.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;weka.classifiers.Classifier<br/>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.WekaModelContainer<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.WekaEvaluationContainer<br/>
 * <p/>
 * Container information:<br/>
 * - adams.flow.container.WekaModelContainer: Model, Header, Dataset<br/>
 * - adams.flow.container.WekaEvaluationContainer: Evaluation, Model
 * <p/>
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
 * &nbsp;&nbsp;&nbsp;default: WekaTestSetEvaluator
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
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
 * <pre>-output &lt;weka.classifiers.evaluation.output.prediction.AbstractOutput&gt; (property: output)
 * &nbsp;&nbsp;&nbsp;The class for generating prediction output; if 'Null' is used, then an Evaluation 
 * &nbsp;&nbsp;&nbsp;object is forwarded instead of a String.
 * &nbsp;&nbsp;&nbsp;default: weka.classifiers.evaluation.output.prediction.Null
 * </pre>
 * 
 * <pre>-testset &lt;adams.flow.core.CallableActorReference&gt; (property: testset)
 * &nbsp;&nbsp;&nbsp;The callable actor to use for obtaining the test set.
 * &nbsp;&nbsp;&nbsp;default: Testset
 * </pre>
 * 
 * <pre>-no-predictions &lt;boolean&gt; (property: discardPredictions)
 * &nbsp;&nbsp;&nbsp;If enabled, the collection of predictions during evaluation is suppressed,
 * &nbsp;&nbsp;&nbsp; wich will conserve memory.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaTestSetEvaluator
  extends AbstractWekaClassifierEvaluator
  implements ProvenanceSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -8528709957864675275L;

  /** the name of the callable trainset provider. */
  protected CallableActorReference m_Testset;

  /** whether to discard predictions. */
  protected boolean m_DiscardPredictions;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Evaluates a trained classifier (obtained from input) on the dataset "
      + "obtained from the callable actor.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "testset", "testset",
	    new CallableActorReference("Testset"));

    m_OptionManager.add(
	    "no-predictions", "discardPredictions",
	    false);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "testset", m_Testset);
    result += QuickInfoHelper.toString(this, "discardPredictions", m_DiscardPredictions, "discarding predictions", ", ");

    return result;
  }

  /**
   * Sets the name of the callable classifier to use.
   *
   * @param value	the name
   */
  public void setTestset(CallableActorReference value) {
    m_Testset = value;
    reset();
  }

  /**
   * Returns the name of the callable classifier in use.
   *
   * @return		the name
   */
  public CallableActorReference getTestset() {
    return m_Testset;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String testsetTipText() {
    return "The callable actor to use for obtaining the test set.";
  }

  /**
   * Sets whether to discard the predictions instead of collecting them
   * for future use, in order to conserve memory.
   *
   * @param value	true if to discard predictions
   */
  public void setDiscardPredictions(boolean value) {
    m_DiscardPredictions = value;
    reset();
  }

  /**
   * Returns whether to discard the predictions in order to preserve memory.
   *
   * @return		true if predictions discarded
   */
  public boolean getDiscardPredictions() {
    return m_DiscardPredictions;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String discardPredictionsTipText() {
    return
        "If enabled, the collection of predictions during evaluation is "
      + "suppressed, wich will conserve memory.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->weka.classifiers.Classifier.class, adams.flow.container.WekaModelContainer.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{weka.classifiers.Classifier.class, WekaModelContainer.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    Instances			test;
    Evaluation			eval;
    weka.classifiers.Classifier	cls;
    CallableSource		gs;
    Token			output;

    result = null;

    try {
      // get test set
      test = null;
      gs   = new CallableSource();
      gs.setCallableName(m_Testset);
      gs.setParent(getParent());
      gs.setUp();
      gs.execute();
      output = gs.output();
      if (output != null)
	test = (Instances) output.getPayload();
      else
	result = "No test set available!";
      gs.wrapUp();

      // evaluate classifier
      if (result == null) {
	if (m_InputToken.getPayload() instanceof weka.classifiers.Classifier)
	  cls = (weka.classifiers.Classifier) m_InputToken.getPayload();
	else
	  cls = (weka.classifiers.Classifier) ((WekaModelContainer) m_InputToken.getPayload()).getValue(WekaModelContainer.VALUE_MODEL);
        initOutputBuffer();
	m_Output.setHeader(test);
	eval = new Evaluation(test);
	eval.setDiscardPredictions(m_DiscardPredictions);
	eval.evaluateModel(cls, test, m_Output);

	// broadcast result
	if (m_Output instanceof Null)
	  m_OutputToken = new Token(new WekaEvaluationContainer(eval, cls));
	else
	  m_OutputToken = new Token(m_Output.getBuffer().toString());
      }
    }
    catch (Exception e) {
      m_OutputToken = null;
      result = handleException("Failed to evaluate: ", e);
    }

    if (m_OutputToken != null)
      updateProvenance(m_OutputToken);

    return result;
  }

  /**
   * Updates the provenance information in the provided container.
   *
   * @param cont	the provenance container to update
   */
  public void updateProvenance(ProvenanceContainer cont) {
    if (Provenance.getSingleton().isEnabled()) {
      if (m_InputToken.hasProvenance())
	cont.setProvenance(m_InputToken.getProvenance().getClone());
      cont.addProvenance(new ProvenanceInformation(ActorType.EVALUATOR, m_InputToken.getPayload().getClass(), this, m_OutputToken.getPayload().getClass()));
    }
  }
}
