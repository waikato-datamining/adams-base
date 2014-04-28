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
 * WekaTrainTestSetClustererEvaluator.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import weka.clusterers.ClusterEvaluation;
import weka.core.Instances;
import adams.core.QuickInfoHelper;
import adams.flow.container.WekaClusterEvaluationContainer;
import adams.flow.container.WekaTrainTestSetContainer;
import adams.flow.core.Token;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;

/**
 <!-- globalinfo-start -->
 * Trains a clusterer on an incoming training dataset (from a container) and then evaluates it on the test set (also from a container).<br/>
 * The clusterer setup being used in the evaluation is a callable 'Clusterer' actor.<br/>
 * If a class attribute is set, a classes-to-clusters evaluation is performed automatically
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.WekaTrainTestSetContainer<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.WekaClusterEvaluationContainer<br/>
 * <p/>
 * Container information:<br/>
 * - adams.flow.container.WekaTrainTestSetContainer: Train, Test, Seed, FoldNumber, FoldCount<br/>
 * - adams.flow.container.WekaClusterEvaluationContainer: Evaluation, Model, Log-likelohood
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
 * &nbsp;&nbsp;&nbsp;default: WekaTrainTestSetClustererEvaluator
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
 * <pre>-clusterer &lt;adams.flow.core.CallableActorReference&gt; (property: clusterer)
 * &nbsp;&nbsp;&nbsp;The callable clusterer actor to train and evaluate on the test data.
 * &nbsp;&nbsp;&nbsp;default: WekaClustererSetup
 * </pre>
 * 
 * <pre>-output-model &lt;boolean&gt; (property: outputModel)
 * &nbsp;&nbsp;&nbsp;If enabled, the clusterer model is output as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaTrainTestSetClustererEvaluator
  extends AbstractGlobalWekaClustererEvaluator
  implements ProvenanceSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -1092101024095887007L;

  /** whether to output the model as well. */
  protected boolean m_OutputModel;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Trains a clusterer on an incoming training dataset (from a container) "
      + "and then evaluates it on the test set (also from a container).\n"
      + "The clusterer setup being used in the evaluation is a callable 'Clusterer' actor.\n"
      + "If a class attribute is set, a classes-to-clusters evaluation is "
      + "performed automatically";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "output-model", "outputModel",
	    false);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String clustererTipText() {
    return "The callable clusterer actor to train and evaluate on the test data.";
  }

  /**
   * Sets whether to output the clusterer model as well.
   *
   * @param value	true if to output model
   */
  public void setOutputModel(boolean value) {
    m_OutputModel = value;
    reset();
  }

  /**
   * Returns whether to output the clusterer model as well.
   *
   * @return		true if model is output
   */
  public boolean getOutputModel() {
    return m_OutputModel;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputModelTipText() {
    return "If enabled, the clusterer model is output as well.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	value;

    result = super.getQuickInfo();
    value = QuickInfoHelper.toString(this, "outputModel", m_OutputModel, "output model", ", ");
    if (value != null)
      result += value;

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.flow.container.WekaTrainTestSetContainer.class<!-- flow-accepts-end -->
   */
  @Override
  public Class[] accepts() {
    return new Class[]{WekaTrainTestSetContainer.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		String.class or weka.classifiers.Evaluation.class
   */
  @Override
  public Class[] generates() {
    return new Class[]{WekaClusterEvaluationContainer.class};
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
    weka.clusterers.Clusterer	cls;
    ClusterEvaluation		eval;
    WekaTrainTestSetContainer	cont;

    result = null;

    try {
      // cross-validate clusterer
      cls = getClustererInstance();
      if (cls == null)
	throw new IllegalStateException("Clusterer '" + getClusterer() + "' not found!");

      cont  = (WekaTrainTestSetContainer) m_InputToken.getPayload();
      train = (Instances) cont.getValue(WekaTrainTestSetContainer.VALUE_TRAIN);
      test  = (Instances) cont.getValue(WekaTrainTestSetContainer.VALUE_TEST);
      cls.buildClusterer(train);
      eval  = new ClusterEvaluation();
      eval.setClusterer(cls);
      eval.evaluateClusterer(test, null, m_OutputModel);

      // broadcast result
      m_OutputToken = new Token(new WekaClusterEvaluationContainer(eval, cls));
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
  @Override
  public void updateProvenance(ProvenanceContainer cont) {
    if (Provenance.getSingleton().isEnabled()) {
      if (m_InputToken.hasProvenance())
	cont.setProvenance(m_InputToken.getProvenance().getClone());
      cont.addProvenance(new ProvenanceInformation(ActorType.EVALUATOR, m_InputToken.getPayload().getClass(), this, m_OutputToken.getPayload().getClass()));
    }
  }
}
