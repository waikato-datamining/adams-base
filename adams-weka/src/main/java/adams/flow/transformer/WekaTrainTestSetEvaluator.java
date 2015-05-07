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
 * WekaTrainTestSetEvaluator.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.output.prediction.Null;
import weka.core.Instances;
import adams.flow.container.WekaEvaluationContainer;
import adams.flow.container.WekaTrainTestSetContainer;
import adams.flow.core.Token;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;

/**
 <!-- globalinfo-start -->
 * Trains a classifier on an incoming training dataset (from a container) and then evaluates it on the test set (also from a container).<br/>
 * The classifier setup being used in the evaluation is a callable 'Classifier' actor.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.WekaTrainTestSetContainer<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.WekaEvaluationContainer<br/>
 * <p/>
 * Container information:<br/>
 * - adams.flow.container.WekaTrainTestSetContainer: Train, Test, Seed, FoldNumber, FoldCount<br/>
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
 * &nbsp;&nbsp;&nbsp;default: WekaTrainTestSetEvaluator
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
 * <pre>-classifier &lt;adams.flow.core.CallableActorReference&gt; (property: classifier)
 * &nbsp;&nbsp;&nbsp;The callable classifier actor to train and evaluate on the test data.
 * &nbsp;&nbsp;&nbsp;default: WekaClassifierSetup
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
public class WekaTrainTestSetEvaluator
  extends AbstractCallableWekaClassifierEvaluator
  implements ProvenanceSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -1092101024095887007L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Trains a classifier on an incoming training dataset (from a container) "
      + "and then evaluates it on the test set (also from a container).\n"
      + "The classifier setup being used in the evaluation is a callable 'Classifier' actor.";
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String classifierTipText() {
    return "The callable classifier actor to train and evaluate on the test data.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.flow.container.WekaTrainTestSetContainer.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{WekaTrainTestSetContainer.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    Instances			train;
    Instances			test;
    weka.classifiers.Classifier	cls;
    Evaluation			eval;
    WekaTrainTestSetContainer	cont;

    result = null;

    try {
      // cross-validate classifier
      cls = getClassifierInstance();
      if (cls == null)
	throw new IllegalStateException("Classifier '" + getClassifier() + "' not found!");

      cont  = (WekaTrainTestSetContainer) m_InputToken.getPayload();
      train = (Instances) cont.getValue(WekaTrainTestSetContainer.VALUE_TRAIN);
      test  = (Instances) cont.getValue(WekaTrainTestSetContainer.VALUE_TEST);
      cls.buildClassifier(train);
      initOutputBuffer();
      m_Output.setHeader(train);
      eval  = new Evaluation(train);
      eval.setDiscardPredictions(m_DiscardPredictions);
      eval.evaluateModel(cls, test, m_Output);

      // broadcast result
      if (m_Output instanceof Null)
	m_OutputToken = new Token(new WekaEvaluationContainer(eval, cls));
      else
	m_OutputToken = new Token(m_Output.getBuffer().toString());
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
