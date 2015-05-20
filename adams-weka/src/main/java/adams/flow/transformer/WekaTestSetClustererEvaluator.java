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
 * WekaTestSetClustererEvaluator.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import weka.clusterers.ClusterEvaluation;
import weka.core.Instances;
import adams.core.QuickInfoHelper;
import adams.flow.container.WekaClusterEvaluationContainer;
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
 * Evaluates a trained clusterer (obtained from input) on the dataset obtained from the callable actor.<br>
 * If a class attribute is set, a classes-to-clusters evaluation is performed automatically
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.clusterers.Clusterer<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.WekaModelContainer<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.WekaClusterEvaluationContainer<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.WekaModelContainer: Model, Header, Dataset<br>
 * - adams.flow.container.WekaClusterEvaluationContainer: Evaluation, Model, Log-likelohood
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
 * &nbsp;&nbsp;&nbsp;default: WekaTestSetClustererEvaluator
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
 * <pre>-testset &lt;adams.flow.core.CallableActorReference&gt; (property: testset)
 * &nbsp;&nbsp;&nbsp;The callable actor to use for obtaining the test set.
 * &nbsp;&nbsp;&nbsp;default: Testset
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
public class WekaTestSetClustererEvaluator
  extends AbstractTransformer
  implements ProvenanceSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -8528709957864675275L;

  /** the name of the callable trainset provider. */
  protected CallableActorReference m_Testset;

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
        "Evaluates a trained clusterer (obtained from input) on the dataset "
        + "obtained from the callable actor.\n"
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
	    "testset", "testset",
	    new CallableActorReference("Testset"));

    m_OptionManager.add(
	    "output-model", "outputModel",
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
    String	value;

    result = QuickInfoHelper.toString(this, "testset", m_Testset);
    value = QuickInfoHelper.toString(this, "outputModel", m_OutputModel, "output model", ", ");
    if (value != null)
      result += value;

    return result;
  }

  /**
   * Sets the name of the callable clusterer to use.
   *
   * @param value	the name
   */
  public void setTestset(CallableActorReference value) {
    m_Testset = value;
    reset();
  }

  /**
   * Returns the name of the callable clusterer in use.
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
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->weka.clusterers.Clusterer.class, adams.flow.container.WekaModelContainer.class<!-- flow-accepts-end -->
   */
  @Override
  public Class[] accepts() {
    return new Class[]{weka.clusterers.Clusterer.class, WekaModelContainer.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		String.class or weka.clusterers.Evaluation.class
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
    Instances			test;
    ClusterEvaluation		eval;
    weka.clusterers.Clusterer	cls;
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

      // evaluate clusterer
      if (result == null) {
	if (m_InputToken.getPayload() instanceof weka.clusterers.Clusterer)
	  cls = (weka.clusterers.Clusterer) m_InputToken.getPayload();
	else
	  cls = (weka.clusterers.Clusterer) ((WekaModelContainer) m_InputToken.getPayload()).getValue(WekaModelContainer.VALUE_MODEL);
	eval = new ClusterEvaluation();
	eval.setClusterer(cls);
	eval.evaluateClusterer(test, null, m_OutputModel);
	m_OutputToken = new Token(new WekaClusterEvaluationContainer(eval, cls));
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
  @Override
  public void updateProvenance(ProvenanceContainer cont) {
    if (Provenance.getSingleton().isEnabled()) {
      if (m_InputToken.hasProvenance())
	cont.setProvenance(m_InputToken.getProvenance().getClone());
      cont.addProvenance(new ProvenanceInformation(ActorType.EVALUATOR, m_InputToken.getPayload().getClass(), this, m_OutputToken.getPayload().getClass()));
    }
  }
}
