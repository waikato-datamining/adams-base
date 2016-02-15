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

/**
 * WekaClassification.java
 * Copyright (C) 2012-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.condition.bool;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.SerializationHelper;
import adams.core.io.PlaceholderFile;
import adams.flow.container.WekaModelContainer;
import adams.flow.core.Actor;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.CallableActorReference;
import adams.flow.core.Token;
import weka.classifiers.Classifier;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Utils;

import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Uses the index of the classification, i.e., the predicted label, as index of the switch
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-model &lt;adams.core.io.PlaceholderFile&gt; (property: modelFile)
 * &nbsp;&nbsp;&nbsp;The model file to load (when not pointing to a directory).
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-model-actor &lt;adams.flow.core.CallableActorReference&gt; (property: modelActor)
 * &nbsp;&nbsp;&nbsp;The callable actor to use for obtaining the model in case serialized model 
 * &nbsp;&nbsp;&nbsp;file points to a directory.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-on-the-fly &lt;boolean&gt; (property: onTheFly)
 * &nbsp;&nbsp;&nbsp;If set to true, the model file is not required to be present at set up time 
 * &nbsp;&nbsp;&nbsp;(eg if built on the fly), only at execution time.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaClassification
  extends AbstractBooleanCondition 
  implements IndexedBooleanCondition {

  /** for serialization. */
  private static final long serialVersionUID = 3278345095591806425L;

  /** the serialized model to load. */
  protected PlaceholderFile m_ModelFile;

  /** the callable actor to get the model from. */
  protected CallableActorReference m_ModelActor;

  /** the model that was loaded from the model file. */
  protected Classifier m_Model;

  /** whether the model gets built on the fly and might not be present at the start. */
  protected boolean m_OnTheFly;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Uses the index of the classification, i.e., the predicted label, "
	+ "as index of the switch";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "model", "modelFile",
	    new PlaceholderFile("."));

    m_OptionManager.add(
	    "model-actor", "modelActor",
	    new CallableActorReference());

    m_OptionManager.add(
	    "on-the-fly", "onTheFly",
	    false);
  }

  /**
   * Sets the file to load the model from.
   *
   * @param value	the model file
   */
  public void setModelFile(PlaceholderFile value) {
    m_ModelFile = value;
    reset();
  }

  /**
   * Returns the file to load the model from.
   *
   * @return		the model file
   */
  public PlaceholderFile getModelFile() {
    return m_ModelFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String modelFileTipText() {
    return "The model file to load (when not pointing to a directory).";
  }

  /**
   * Sets the callable actor to obtain the model from if model file is pointing
   * to a directory.
   *
   * @param value	the actor reference
   */
  public void setModelActor(CallableActorReference value) {
    m_ModelActor = value;
    reset();
  }

  /**
   * Returns the callable actor to obtain the model from if model file is pointing
   * to a directory.
   *
   * @return		the actor reference
   */
  public CallableActorReference getModelActor() {
    return m_ModelActor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String modelActorTipText() {
    return
      "The callable actor to use for obtaining the model in case serialized "
	+ "model file points to a directory; can be a "
	+ WekaModelContainer.class.getName() + " as well.";
  }

  /**
   * Sets whether the model file gets built on the fly and might not be present
   * at start up time.
   *
   * @param value	if true then the model does not have to be present at
   * 			start up time
   */
  public void setOnTheFly(boolean value) {
    m_OnTheFly = value;
    reset();
  }

  /**
   * Returns whether the model file gets built on the fly and might not be present
   * at start up time.
   *
   * @return		true if the model is not necessarily present at start
   * 			up time
   */
  public boolean getOnTheFly() {
    return m_OnTheFly;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String onTheFlyTipText() {
    return
        "If set to true, the model file is not required to be present at "
      + "set up time (eg if built on the fly), only at execution time.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "modelFile", (m_ModelFile.isDirectory() ? m_ModelActor.getValue() : m_ModelFile));
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		Unknown
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Instance.class};
  }

  /**
   * Loads the model from the model file.
   *
   * @param owner	the actor this condition belongs to
   * @return		null if everything worked, otherwise an error message
   */
  protected String setUpModel(Actor owner) {
    String		result;
    String		msg;
    Capabilities	caps;
    Object		obj;
    MessageCollection errors;

    result = null;

    if (m_ModelFile.isDirectory()) {
      // obtain model from callable actor
      try {
	errors  = new MessageCollection();
	obj     = CallableActorHelper.getSetup(Classifier.class, m_ModelActor, owner, errors);
	if (obj == null) {
	  if (!errors.isEmpty())
	    result = errors.toString();
	}
	else {
	  if (obj instanceof WekaModelContainer)
	    m_Model = (Classifier) ((WekaModelContainer) obj).getValue(WekaModelContainer.VALUE_MODEL);
	  else
	    m_Model = (Classifier) obj;
	}
      }
      catch (Exception e) {
	m_Model = null;
	msg     = "Failed to obtain model from callable actor '" + m_ModelActor + "': ";
	result  = msg + e.toString();
	getLogger().log(Level.SEVERE, msg, e);
      }
    }
    else {
      // load model
      try {
	m_Model = (Classifier) SerializationHelper.read(m_ModelFile.getAbsolutePath());
      }
      catch (Exception e) {
	m_Model = null;
	msg     = "Failed to load model from '" + m_ModelFile + "': ";
	result  = msg + e.toString();
	getLogger().log(Level.SEVERE, msg, e);
      }
    }
    
    // can model handle nominal class attribute?
    if (m_Model != null) {
      caps = m_Model.getCapabilities();
      if (    !caps.handles(Capability.UNARY_CLASS) 
	   && !caps.handles(Capability.BINARY_CLASS) 
	   && !caps.handles(Capability.NOMINAL_CLASS) )
	result = "Model can neither handle unary, binary nor nominal class attribute!";
    }

    return result;
  }

  /**
   * Initializes the item for flow execution.
   *
   * @param owner	the actor this condition belongs to
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp(Actor owner) {
    String	result;

    result = super.setUp(owner);

    if (result == null) {
      if (!m_OnTheFly)
	result = setUpModel(owner);
    }

    return result;
  }

  /**
   * Performs the actual evaluation.
   *
   * @param owner	the owning actor
   * @param token	the current token passing through
   * @return		the result of the evaluation
   */
  @Override
  protected boolean doEvaluate(Actor owner, Token token) {
    return (getCaseIndex(owner, token) != -1);
  }

  /**
   * Returns the index of the case that should get executed.
   * 
   * @param owner	the owning actor
   * @param token	the current token passing through the actor
   * @return		the index, -1 if not available
   */
  public int getCaseIndex(Actor owner, Token token) {
    int		result;
    double	classification;
    String	msg;
    Instance	inst;

    result = -1;

    if (m_OnTheFly && (m_Model == null)) {
      msg = setUpModel(owner);
      if (msg != null) {
	getLogger().severe(msg);
	return result;
      }
    }
    
    if ((token != null) && (token.getPayload() != null)) {
      inst = ((Instance) token.getPayload());
      if (inst.classIndex() == -1) {
	getLogger().severe("No class set!");
	return result;
      }
      if (!inst.classAttribute().isNominal()) {
	getLogger().severe("Class attribute is not nominal!");
	return result;
      }

      try {
	classification = m_Model.classifyInstance(inst);
	if (Utils.isMissingValue(classification))
	  result = -1;
	else
	  result = (int) classification;
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to obtain classification: ", e);
      }
    }

    return result;
  }

  /**
   * Returns the index of the default case.
   * 
   * @param owner	the owning actor
   * @param token	the current token passing through the actor
   * @return		the index, -1 if not available
   */
  public int getDefaultCaseIndex(Actor owner, Token token) {
    Instance	inst;
    
    inst = ((Instance) token.getPayload());
    
    return inst.classAttribute().numValues();
  }
}
