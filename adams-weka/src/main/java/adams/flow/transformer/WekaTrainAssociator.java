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
 * WekaTrainAssociator.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.flow.container.WekaAssociatorContainer;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.CallableActorReference;
import adams.flow.core.Token;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;
import adams.flow.source.WekaAssociatorSetup;
import weka.associations.AssociationRulesProducer;
import weka.core.Instance;
import weka.core.Instances;

/**
 <!-- globalinfo-start -->
 * Trains a associator based on the incoming dataset and outputs the built associator alongside the training header and rules (in a model container)..
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.WekaAssociatorContainer<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.WekaAssociatorContainer: Model, Header, Dataset, Rules
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
 * &nbsp;&nbsp;&nbsp;default: WekaTrainAssociator
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
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing 
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-associator &lt;adams.flow.core.CallableActorReference&gt; (property: associator)
 * &nbsp;&nbsp;&nbsp;The Weka associator to train on the input data.
 * &nbsp;&nbsp;&nbsp;default: WekaAssociatorSetup
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaTrainAssociator
  extends AbstractTransformer 
  implements ProvenanceSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -3019442578354930841L;

  /** the name of the callable weka associator. */
  protected CallableActorReference m_Associator;

  /** the actual weka associator. */
  protected weka.associations.Associator m_ActualAssociator;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Trains a associator based on the incoming dataset and outputs the "
      + "built associator alongside the training header and rules (in a model container)..";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "associator", "associator",
	    new CallableActorReference(WekaAssociatorSetup.class.getSimpleName()));
  }

  /**
   * Sets the name of the callable associator to use.
   *
   * @param value	the name
   */
  public void setAssociator(CallableActorReference value) {
    m_Associator = value;
    reset();
  }

  /**
   * Returns the name of the callable associator in use.
   *
   * @return		the name
   */
  public CallableActorReference getAssociator() {
    return m_Associator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String associatorTipText() {
    return "The Weka associator to train on the input data.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "associator", m_Associator);
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->weka.core.Instances.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Instances.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.flow.container.WekaAssociatorContainer.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{WekaAssociatorContainer.class};
  }

  /**
   * Returns an instance of the callable associator.
   *
   * @return		the associator
   * @throws Exception  if fails to obtain associator
   */
  protected weka.associations.Associator getAssociatorInstance() throws Exception {
    weka.associations.Associator	result;
    MessageCollection 			errors;

    errors = new MessageCollection();
    result = (weka.associations.Associator) CallableActorHelper.getSetup(weka.associations.Associator.class, m_Associator, this, errors);
    if (result == null) {
      if (errors.isEmpty())
	throw new IllegalStateException("Failed to obtain associator from '" + m_Associator + "'!");
      else
	throw new IllegalStateException("Failed to obtain associator from '" + m_Associator + "':\n" + errors);
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
    String				result;
    Instances				data;
    Instance				inst;
    weka.associations.Associator	cls;

    result = null;

    try {
      if ((m_InputToken != null) && (m_InputToken.getPayload() instanceof Instances)) {
	cls  = getAssociatorInstance();
	data = (Instances) m_InputToken.getPayload();
	cls.buildAssociations(data);
	if ((cls instanceof AssociationRulesProducer) && ((AssociationRulesProducer) cls).canProduceRules())
	  m_OutputToken = new Token(new WekaAssociatorContainer(cls, new Instances(data, 0), data, ((AssociationRulesProducer) cls).getAssociationRules().getRules()));
	else
	  m_OutputToken = new Token(new WekaAssociatorContainer(cls, new Instances(data, 0), data));
      }
    }
    catch (Exception e) {
      m_OutputToken = null;
      result        = handleException("Failed to process data:", e);
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
      cont.addProvenance(new ProvenanceInformation(ActorType.MODEL_GENERATOR, m_InputToken.getPayload().getClass(), this, m_OutputToken.getPayload().getClass()));
    }
  }
}
