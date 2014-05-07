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
 * AbstractProcessWekaInstanceWithModel.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import java.util.Hashtable;

import weka.core.Instance;
import adams.core.QuickInfoHelper;
import adams.core.SerializationHelper;
import adams.core.io.PlaceholderFile;
import adams.flow.core.CallableActorReference;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.Token;

/**
 * Ancestor for transformers that user models for processing Instance objects,
 * e.g., classifiers making predictions.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of model to use
 */
public abstract class AbstractProcessWekaInstanceWithModel<T>
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -5275241130624220000L;

  /** the key for storing the current model in the backup. */
  public final static String BACKUP_MODEL = "model";

  /** the serialized model to load. */
  protected PlaceholderFile m_ModelFile;

  /** the callable actor to get the model from. */
  protected CallableActorReference m_ModelActor;

  /** the model that was loaded from the model file. */
  protected T m_Model;

  /** whether the model gets built on the fly and might not be present at the start. */
  protected boolean m_OnTheFly;

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
      + "model file points to a directory.";
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
    return QuickInfoHelper.toString(this, "modelFile", (m_ModelFile.isDirectory() ? m_ModelActor : m_ModelFile));
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		weka.core.Instance.class
   */
  public Class[] accepts() {
    return new Class[]{Instance.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the classes
   */
  public abstract Class[] generates();

  /**
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();

    pruneBackup(BACKUP_MODEL);
  }

  /**
   * Backs up the current state of the actor before update the variables.
   *
   * @return		the backup
   */
  @Override
  protected Hashtable<String,Object> backupState() {
    Hashtable<String,Object>	result;

    result = super.backupState();

    if (m_Model != null)
      result.put(BACKUP_MODEL, m_Model);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_MODEL)) {
      m_Model = (T) state.get(BACKUP_MODEL);
      state.remove(BACKUP_MODEL);
    }

    super.restoreState(state);
  }

  /**
   * Loads the model from the model file.
   *
   * @return		null if everything worked, otherwise an error message
   */
  protected String setUpModel() {
    String	result;

    result = null;

    if (m_ModelFile.isDirectory()) {
      // obtain model from callable actor
      try {
	m_Model = (T) CallableActorHelper.getSetup(null, m_ModelActor, this);
      }
      catch (Exception e) {
	m_Model = null;
	result  = handleException("Failed to obtain model from callable actor '" + m_ModelActor + "': ", e);
      }
    }
    else {
      // load model
      try {
	m_Model = (T) SerializationHelper.read(m_ModelFile.getAbsolutePath());
      }
      catch (Exception e) {
	m_Model = null;
	result  = handleException("Failed to load model from '" + m_ModelFile + "': ", e);
      }
    }

    return result;
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;

    result = super.setUp();

    if (result == null) {
      if (!m_OnTheFly)
	result = setUpModel();
    }

    return result;
  }

  /**
   * Processes the instance and generates the output token.
   *
   * @param inst	the instance to process
   * @return		the generated output token (e.g., container)
   * @throws Exception	if processing fails
   */
  protected abstract Token processInstance(Instance inst) throws Exception;

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    Instance	inst;

    result = null;

    if (m_OnTheFly) {
      result = setUpModel();
      if (result != null)
	return result;
    }

    inst = null;
    try {
      inst          = (Instance) m_InputToken.getPayload();
      m_OutputToken = processInstance(inst);
    }
    catch (Exception e) {
      m_OutputToken = null;
      handleException("Failed to process instance: " + inst, e);
    }

    return result;
  }

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    super.wrapUp();

    m_Model = null;
  }
}
