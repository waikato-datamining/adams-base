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
 * AbstractProcessWekaInstanceWithModel.java
 * Copyright (C) 2011-2017 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.VariableName;
import adams.core.io.PlaceholderFile;
import adams.core.logging.LoggingLevel;
import adams.event.VariableChangeEvent;
import adams.flow.container.AbstractContainer;
import adams.flow.container.WekaModelContainer;
import adams.flow.control.StorageName;
import adams.flow.core.AbstractModelLoader;
import adams.flow.core.AbstractModelLoader.ModelLoadingType;
import adams.flow.core.CallableActorReference;
import adams.flow.core.DynamicModelLoaderSupporter;
import adams.flow.core.Token;
import weka.core.Instance;

import java.util.Hashtable;

/**
 * Ancestor for transformers that user models for processing Instance objects,
 * e.g., classifiers making predictions.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of model to use
 */
public abstract class AbstractProcessWekaInstanceWithModel<T>
  extends AbstractTransformer
  implements DynamicModelLoaderSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -5275241130624220000L;

  /** the key for storing the current model in the backup. */
  public final static String BACKUP_MODEL = "model";

  /** the model that was loaded from the model file. */
  protected T m_Model;

  /** whether the model gets built on the fly and might not be present at the start. */
  protected boolean m_OnTheFly;

  /** whether to use a variable to monitor for changes, triggering resets of the model. */
  protected boolean m_UseModelResetVariable;

  /** the variable to monitor for changes, triggering resets of the model. */
  protected VariableName m_ModelResetVariable;

  /** whether we need to reset the model. */
  protected boolean m_ResetModel;

  /** the model loader. */
  protected AbstractModelLoader m_ModelLoader;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "model-loading-type", "modelLoadingType",
      ModelLoadingType.AUTO);

    m_OptionManager.add(
      "model", "modelFile",
      new PlaceholderFile("."));

    m_OptionManager.add(
      "model-actor", "modelActor",
      new CallableActorReference());

    m_OptionManager.add(
      "model-storage", "modelStorage",
      new StorageName());

    m_OptionManager.add(
      "on-the-fly", "onTheFly",
      false);

    m_OptionManager.add(
      "use-model-reset-variable", "useModelResetVariable",
      false);

    m_OptionManager.add(
      "model-reset-variable", "modelResetVariable",
      new VariableName());
  }

  @Override
  protected void initialize() {
    super.initialize();

    m_ModelLoader = newModelLoader();
    m_ModelLoader.setFlowContext(this);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Model = null;
    m_ModelLoader.reset();
  }

  /**
   * Sets the logging level.
   *
   * @param value 	the level
   */
  @Override
  public synchronized void setLoggingLevel(LoggingLevel value) {
    super.setLoggingLevel(value);
    m_ModelLoader.setLoggingLevel(value);
  }

  /**
   * Instantiates the model loader to use.
   *
   * @return		the model loader to use
   */
  protected abstract AbstractModelLoader newModelLoader();

  /**
   * Sets the loading type. In case of {@link ModelLoadingType#AUTO}, first
   * file, then callable actor, then storage.
   *
   * @param value	the type
   */
  public void setModelLoadingType(ModelLoadingType value) {
    m_ModelLoader.setModelLoadingType(value);
    reset();
  }

  /**
   * Returns the loading type. In case of {@link ModelLoadingType#AUTO}, first
   * file, then callable actor, then storage.
   *
   * @return		the type
   */
  public ModelLoadingType getModelLoadingType() {
    return m_ModelLoader.getModelLoadingType();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String modelLoadingTypeTipText() {
    return m_ModelLoader.modelLoadingTypeTipText();
  }

  /**
   * Sets the file to load the model from.
   *
   * @param value	the model file
   */
  public void setModelFile(PlaceholderFile value) {
    m_ModelLoader.setModelFile(value);
    reset();
  }

  /**
   * Returns the file to load the model from.
   *
   * @return		the model file
   */
  public PlaceholderFile getModelFile() {
    return m_ModelLoader.getModelFile();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String modelFileTipText() {
    return m_ModelLoader.modelFileTipText();
  }

  /**
   * Sets the filter source actor.
   *
   * @param value	the source
   */
  public void setModelActor(CallableActorReference value) {
    m_ModelLoader.setModelActor(value);
    reset();
  }

  /**
   * Returns the filter source actor.
   *
   * @return		the source
   */
  public CallableActorReference getModelActor() {
    return m_ModelLoader.getModelActor();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String modelActorTipText() {
    return m_ModelLoader.modelActorTipText();
  }

  /**
   * Sets the filter storage item.
   *
   * @param value	the storage item
   */
  public void setModelStorage(StorageName value) {
    m_ModelLoader.setModelStorage(value);
    reset();
  }

  /**
   * Returns the filter storage item.
   *
   * @return		the storage item
   */
  public StorageName getModelStorage() {
    return m_ModelLoader.getModelStorage();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String modelStorageTipText() {
    return m_ModelLoader.modelStorageTipText();
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
   * Sets the whether to use a variable to monitor for changes in order
   * to reset the model.
   *
   * @param value	true if to use monitor variable
   */
  public void setUseModelResetVariable(boolean value) {
    m_UseModelResetVariable = value;
    reset();
  }

  /**
   * Returns the whether to use a variable to monitor for changes in order
   * to reset the model.
   *
   * @return		true if to use monitor variable
   */
  public boolean getUseModelResetVariable() {
    return m_UseModelResetVariable;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useModelResetVariableTipText() {
    return
        "If enabled, chnages to the specified variable are monitored in order "
	  + "to reset the model, eg when a storage model changed.";
  }

  /**
   * Sets the variable to monitor for changes in order to reset the model.
   *
   * @param value	the variable
   */
  public void setModelResetVariable(VariableName value) {
    m_ModelResetVariable = value;
    reset();
  }

  /**
   * Returns the variable to monitor for changes in order to reset the model.
   *
   * @return		the variable
   */
  public VariableName getModelResetVariable() {
    return m_ModelResetVariable;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String modelResetVariableTipText() {
    return
        "The variable to monitor for changes in order to reset the model, eg "
	  + "when a storage model changed.";
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

    result  = QuickInfoHelper.toString(this, "modelLoadingType", getModelLoadingType(), "type: ");
    result += QuickInfoHelper.toString(this, "modelFile", getModelFile(), ", model: ");
    result += QuickInfoHelper.toString(this, "modelSource", getModelActor(), ", source: ");
    result += QuickInfoHelper.toString(this, "modelStorage", getModelStorage(), ", storage: ");
    value  = QuickInfoHelper.toString(this, "modelResetVariable", (m_UseModelResetVariable ? "reset: " + m_ModelResetVariable : ""));
    if (value != null)
      result += ", " + value;

    return result;
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
   * Gets triggered when a variable changed (added, modified, removed).
   *
   * @param e		the event
   */
  @Override
  public void variableChanged(VariableChangeEvent e) {
    super.variableChanged(e);
    if (e.getName().equals(m_ModelResetVariable.getValue()))
      m_ResetModel = true;
  }

  /**
   * Returns the model container class that is supported.
   *
   * @return		the class
   */
  protected Class getModelContainerClass() {
    return WekaModelContainer.class;
  }

  /**
   * Retrieves the model from the container.
   *
   * @param cont	the container to get the model from
   * @return		the model, null if not in container
   */
  protected T getModelFromContainer(AbstractContainer cont) {
    return (T) cont.getValue(WekaModelContainer.VALUE_MODEL);
  }

  /**
   * Loads the model from the model file.
   *
   * @return		null if everything worked, otherwise an error message
   */
  protected String setUpModel() {
    String		result;
    MessageCollection 	errors;

    result = null;

    if (m_ResetModel)
      m_ModelLoader.reset();

    errors  = new MessageCollection();
    m_Model = (T) m_ModelLoader.getModel(errors);
    if (m_Model == null)
      result = errors.toString();

    m_ResetModel = false;

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

    if ((m_OnTheFly && (m_Model == null)) || m_ResetModel) {
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
      result = handleException("Failed to process instance: " + inst, e);
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
