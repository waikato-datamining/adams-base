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
 * WekaInstanceEvaluator.java
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.filters.unsupervised.attribute.Add;
import adams.core.QuickInfoHelper;
import adams.data.weka.evaluator.AbstractDatasetInstanceEvaluator;
import adams.data.weka.evaluator.AbstractInstanceEvaluator;
import adams.data.weka.evaluator.PassThrough;
import adams.flow.core.CallableActorReference;
import adams.flow.core.Compatibility;
import adams.flow.core.Token;
import adams.flow.source.CallableSource;

/**
 <!-- globalinfo-start -->
 * Adds a new attribute to the data being passed through (normally 'evaluation') and sets the value to the evaluation value returned by the chosen evaluator scheme.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;weka.core.Instance<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;weka.core.Instance<br/>
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
 * &nbsp;&nbsp;&nbsp;default: WekaInstanceEvaluator
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
 * <pre>-evaluator &lt;adams.data.weka.evaluator.AbstractInstanceEvaluator&gt; (property: evaluator)
 * &nbsp;&nbsp;&nbsp;The evaluator to use.
 * &nbsp;&nbsp;&nbsp;default: adams.data.weka.evaluator.PassThrough
 * </pre>
 * 
 * <pre>-instances &lt;adams.flow.core.CallableActorReference&gt; (property: instancesActor)
 * &nbsp;&nbsp;&nbsp;The name of the callable actor from which to retrieve Instances in case 
 * &nbsp;&nbsp;&nbsp;of adams.data.weka.evaluator.AbstractDatasetInstanceEvaluator-derived classes,
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaInstanceEvaluator
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -8810671831368685057L;

  /** the default name of the attribute with the evaluation value. */
  public final static String ATTRIBUTE_NAME = "evaluation";

  /** the evaluator to use. */
  protected AbstractInstanceEvaluator m_Evaluator;

  /** the callable actor to get the Instances from in case of AbstractDatasetInstanceEvaluator. */
  protected CallableActorReference m_InstancesActor;

  /** the new header. */
  protected Instances m_Header;

  /** the attribute name of the evaluation object. */
  protected String m_AttributeName;

  /** the callable actor to use. */
  protected CallableSource m_GlobalSource;

  /** the filter that is used for generating the new data format. */
  protected Add	m_Filter;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Adds a new attribute to the data being passed through "
      + "(normally '" + ATTRIBUTE_NAME + "') and sets the value to the "
      + "evaluation value returned by the chosen evaluator scheme.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "evaluator", "evaluator",
	    new PassThrough());

    m_OptionManager.add(
	    "instances", "instancesActor",
	    new CallableActorReference(""));
  }

  /**
   * Resets the actor.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Header        = null;
    m_AttributeName = null;
    m_GlobalSource  = null;
    m_Filter        = null;
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "evaluator", (m_Evaluator instanceof AbstractDatasetInstanceEvaluator ? m_AttributeName : null));
  }

  /**
   * Sets the evaluator to use.
   *
   * @param value	the evaluator
   */
  public void setEvaluator(AbstractInstanceEvaluator value) {
    m_Evaluator = value;
    reset();
  }

  /**
   * Returns the evaluator to use.
   *
   * @return		the evaluator
   */
  public AbstractInstanceEvaluator getEvaluator() {
    return m_Evaluator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String evaluatorTipText() {
    return "The evaluator to use.";
  }

  /**
   * Sets the callable actor from which to retrieve Instances in case of
   * AbstractDatasetInstanceEvaluator-derived evaluators.
   *
   * @param value	the name of the actor
   */
  public void setInstancesActor(CallableActorReference value) {
    m_InstancesActor = value;
    reset();
  }

  /**
   * Returns the callable actor from which to retrieve Instances in case of
   * AbstractDatasetInstanceEvaluator-derived evaluators.
   *
   * @return		the name of the actor
   */
  public CallableActorReference getInstancesActor() {
    return m_InstancesActor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String instancesActorTipText() {
    return
        "The name of the callable actor from which to retrieve Instances in case "
      + "of " + AbstractDatasetInstanceEvaluator.class.getName() + "-derived "
      + "classes,";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->weka.core.Instance.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Instance.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->weka.core.Instance.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{Instance.class};
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String		result;
    Compatibility	comp;

    result = super.setUp();

    if (result == null) {
      if (m_Evaluator instanceof AbstractDatasetInstanceEvaluator) {
	if (m_InstancesActor.toString().length() == 0) {
	  result = "No callable actor defined for obtaining the dataset from to initialize the evaluator with!";
	}
	else {
	  m_GlobalSource = new CallableSource();
	  m_GlobalSource.setParent(getParent());
	  m_GlobalSource.setCallableName(m_InstancesActor);
	  result = m_GlobalSource.setUp();
	  if (result == null) {
	    comp = new Compatibility();
	    if (!comp.isCompatible(m_GlobalSource.generates(), new Class[]{Instances.class}))
	      result = "Global actor '" + m_InstancesActor + "' does not produce weka.core.Instances!";
	  }
	}
      }
    }

    return result;
  }

  /**
   * Determines the name of the evaluation attribute.
   *
   * @param data	the original input data
   * @return		the generated name
   * @see		#m_AttributeName
   */
  protected String determineAttributeName(Instances data) {
    String	result;
    int		i;

    result = ATTRIBUTE_NAME;

    i = 0;
    while (data.attribute(result) != null) {
      i++;
      result = ATTRIBUTE_NAME + i;
    }

    m_AttributeName = result;
    if (isLoggingEnabled())
      getLogger().info("Chosen attribute name: " + m_AttributeName);

    return result;
  }

  /**
   * Initializes the evaluator.
   *
   * @return		null if everything is fine, otherwise error message
   * @see		#m_Evaluator
   */
  protected String setUpEvaluator() {
    String	result;
    Instances	data;

    result = null;

    if (m_Evaluator instanceof AbstractDatasetInstanceEvaluator) {
      result = m_GlobalSource.execute();
      if (result == null) {
	if (!m_GlobalSource.hasPendingOutput()) {
	  result = "Global actor '" + m_InstancesActor + "' did not produce weka.core.Instances!";
	}
	else {
	  data = (Instances) m_GlobalSource.output().getPayload();
	  ((AbstractDatasetInstanceEvaluator) m_Evaluator).setData(data);
	}
      }
    }

    return result;
  }

  /**
   * Generates the new header for the data.
   *
   * @param inst	the instance to get the original data format from
   * @return		null if everything is fine, otherwise error message
   * @see		#m_Header
   * @see		#m_Filter
   */
  protected String generateHeader(Instance inst) {
    String	result;

    result = null;

    m_Filter = new Add();
    m_Filter.setAttributeName(determineAttributeName(inst.dataset()));
    m_Filter.setAttributeType(new SelectedTag(Attribute.NUMERIC, Add.TAGS_TYPE));
    if (inst.dataset().classIndex() == inst.dataset().numAttributes() - 1)
      m_Filter.setAttributeIndex("" + inst.dataset().numAttributes());
    else
      m_Filter.setAttributeIndex("" + (inst.dataset().numAttributes() + 1));
    try {
      m_Filter.setInputFormat(inst.dataset());
      m_Header = weka.filters.Filter.useFilter(inst.dataset(), m_Filter);
    }
    catch (Exception e) {
      result = handleException("Failed to generate header:", e);
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
    String	result;
    Instance	inst;
    Instance	newInst;
    double	eval;

    result = null;

    // the Instance to evaluate
    inst = (Instance) m_InputToken.getPayload();

    // obtain dataset first?
    if (m_Header == null)
      result = setUpEvaluator();

    // generate new header?
    if ((result == null) && (m_Header == null))
      result = generateHeader(inst);

    // generate evaluation
    if (result == null) {
      try {
	eval = m_Evaluator.evaluate(inst);
	if (isLoggingEnabled())
	  getLogger().info("Evaluation " + eval + " for instance: " + inst);
	m_Filter.input(inst);
	m_Filter.batchFinished();
	newInst = m_Filter.output();
	newInst.setValue(newInst.dataset().attribute(m_AttributeName), eval);
	m_OutputToken = new Token(newInst);
      }
      catch (Exception e) {
	m_OutputToken = null;
	result = handleException("Failed to evaluate instance: " + inst, e);
      }
    }

    return result;
  }

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    m_Filter       = null;
    m_Header       = null;
    m_GlobalSource = null;

    super.wrapUp();
  }
}
