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
 * AbstractModelLoader.java
 * Copyright (C) 2017-2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.core;

import adams.core.MessageCollection;
import adams.core.SerializationHelper;
import adams.core.Utils;
import adams.core.io.ModelFileHandler;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractOptionHandler;
import adams.flow.container.AbstractContainer;
import adams.flow.control.StorageName;

/**
 * Ancestor for model loaders.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @param <T> the type of model
 */
public abstract class AbstractModelLoader<T>
  extends AbstractOptionHandler
  implements FlowContextHandler, ModelFileHandler {

  private static final long serialVersionUID = 7549636204208995661L;

  /**
   * The enumeration for the loading type.
   */
  public enum ModelLoadingType {
    AUTO,
    FILE,
    SOURCE_ACTOR,
    STORAGE
  }

  /** the flow context. */
  protected Actor m_FlowContext;

  /** the loading type. */
  protected ModelLoadingType m_ModelLoadingType;

  /** the mode file. */
  protected PlaceholderFile m_ModelFile;

  /** the model actor. */
  protected CallableActorReference m_ModelActor;

  /** the model storage. */
  protected StorageName m_ModelStorage;

  /** the model. */
  protected T m_Model;

  /** for locating callable actors. */
  protected CallableActorHelper m_Helper;

  /**
   * Returns information how the model is loaded in case of {@link ModelLoadingType#AUTO}.
   *
   * @return		the description
   */
  public String automaticOrderInfo() {
    return
      "The following order is used to obtain the model (when using " + ModelLoadingType.AUTO + "):\n"
	+ "1. model file present?\n"
	+ "2. source actor present?\n"
	+ "3. storage item present?";
  }

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
      "model-file", "modelFile",
      new PlaceholderFile("."));

    m_OptionManager.add(
      "model-actor", "modelActor",
      new CallableActorReference());

    m_OptionManager.add(
      "model-storage", "modelStorage",
      new StorageName());
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_FlowContext = null;
    m_Helper      = new CallableActorHelper();
  }

  /**
   * Resets the scheme.
   */
  @Override
  public void reset() {
    super.reset();

    m_Model = null;
  }

  /**
   * Sets the flow context.
   *
   * @param value	the actor
   */
  public void setFlowContext(Actor value) {
    m_FlowContext = value;
  }

  /**
   * Returns the flow context, if any.
   *
   * @return		the actor, null if none available
   */
  public Actor getFlowContext() {
    return m_FlowContext;
  }

  /**
   * Sets the loading type. In case of {@link ModelLoadingType#AUTO}, first
   * file, then callable actor, then storage.
   *
   * @param value	the type
   */
  public void setModelLoadingType(ModelLoadingType value) {
    m_ModelLoadingType = value;
    reset();
  }

  /**
   * Returns the loading type. In case of {@link ModelLoadingType#AUTO}, first
   * file, then callable actor, then storage.
   *
   * @return		the type
   */
  public ModelLoadingType getModelLoadingType() {
    return m_ModelLoadingType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String modelLoadingTypeTipText() {
    return
      "Determines how to load the model, in case of " + ModelLoadingType.AUTO + ", "
	+ "first the model file is checked, then the callable actor and then the storage.";
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
    return "The file to load the model from, ignored if pointing to a directory.";
  }

  /**
   * Sets the callable actor to obtain the model from.
   *
   * @param value	the actor reference
   */
  public void setModelActor(CallableActorReference value) {
    m_ModelActor = value;
    reset();
  }

  /**
   * Returns the callable actor to obtain the model from.
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
    return "The callable actor (source) to obtain the model from, ignored if not present.";
  }

  /**
   * Sets the storage item name to get the model from.
   *
   * @param value	the storage name
   */
  public void setModelStorage(StorageName value) {
    m_ModelStorage = value;
    reset();
  }

  /**
   * Returns the storage item name to get the model from.
   *
   * @return		the storage name
   */
  public StorageName getModelStorage() {
    return m_ModelStorage;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String modelStorageTipText() {
    return "The storage item to obtain the model from, ignored if not present.";
  }

  /**
   * Adds an "unhandled container type" error message.
   *
   * @param cont	the unhandled container type
   * @param errors	the errors to update
   */
  protected void unhandledContainer(AbstractContainer cont, MessageCollection errors) {
    errors.add("Unhandled container type: " + Utils.classToString(cont));
  }

  /**
   * Retrieves the model from the container.
   *
   * @param cont	the container to get the model from
   * @param errors	for collecting errors
   * @return		the model, null if not in container
   */
  protected abstract T getModelFromContainer(AbstractContainer cont, MessageCollection errors);

  /**
   * Unwraps the model if necessary.
   *
   * @param obj		the model to process
   * @param errors	for collecting errors
   * @return		the unwrapped model, null if failed or input was null
   */
  protected T unwrapModel(Object obj, MessageCollection errors) {
    if (obj == null)
      return null;

    if (obj instanceof AbstractContainer)
      return getModelFromContainer((AbstractContainer) obj, errors);
    else
      return (T) obj;
  }

  /**
   * Deserializes the model file.
   *
   * @param errors	for collecting errors
   * @return		the object read from the file, null if failed
   */
  protected Object deserializeFile(MessageCollection errors) {
    try {
      return SerializationHelper.read(m_ModelFile.getAbsolutePath());
    }
    catch (Exception e) {
      errors.add("Failed to deserialize '" + m_ModelFile + "': ", e);
    }
    return null;
  }

  /**
   * Loads the model from the specified file.
   *
   * @param errors	for collecting errors
   * @return		the model, null if failed to load
   */
  protected T loadFromFile(MessageCollection errors) {
    T 		result;
    Object	obj;

    if (!m_ModelFile.exists()) {
      errors.add("Model file does not exist: " + m_ModelFile);
      return null;
    }
    if (m_ModelFile.isDirectory()) {
      errors.add("Model file points to a directory: " + m_ModelFile);
      return null;
    }

    try {
      obj    = deserializeFile(errors);
      result = unwrapModel(obj, errors);
    }
    catch (Exception e) {
      result = null;
      errors.add("Failed to load model from '" + m_ModelFile + "': ", e);
    }

    return result;
  }

  /**
   * Loads the model from the callable actor.
   *
   * @param errors	for collecting errors
   * @return		the model, null if failed to load
   */
  protected T loadFromCallableActor(MessageCollection errors) {
    T 		result;
    Object	obj;

    if (m_FlowContext == null) {
      errors.add("No flow context specified!");
      return null;
    }

    // obtain model from callable actor
    try {
      obj = CallableActorHelper.getSetup(null, m_ModelActor, m_FlowContext, errors);
    }
    catch (Exception e) {
      obj = null;
      errors.add("Failed to obtain model from callable actor '" + m_ModelActor + "': ", e);
    }
    result = unwrapModel(obj, errors);

    return result;
  }

  /**
   * Loads the model from the storage item.
   *
   * @param errors	for collecting errors
   * @return		the model, null if failed to load
   */
  protected T loadFromStorage(MessageCollection errors) {
    T 		result;
    Object	obj;

    if (m_FlowContext == null) {
      errors.add("No flow context specified!");
      return null;
    }
    if (!m_FlowContext.getStorageHandler().getStorage().has(m_ModelStorage)) {
      errors.add("Model storage item not available: " + m_ModelStorage);
      return null;
    }

    obj    = m_FlowContext.getStorageHandler().getStorage().get(m_ModelStorage);
    result = unwrapModel(obj, errors);

    return result;
  }

  /**
   * Loads the model automatically from file/callable actor/storage.
   *
   * @param errors	for collecting errors
   * @return		the model, null if failed
   */
  protected T loadAutomatically(MessageCollection errors) {
    T		result;

    result = null;

    if (m_ModelFile.exists() && !m_ModelFile.isDirectory()) {
      result = loadFromFile(errors);
    }
    else {
      if (m_FlowContext == null) {
	errors.add("No flow context set!");
      }
      else {
	if (m_Helper.findCallableActorRecursive(m_FlowContext, m_ModelActor) != null)
	  result = loadFromCallableActor(errors);
	else
	  result = loadFromStorage(errors);
      }
    }

    return result;
  }

  /**
   * Obtains the model, loads it if necessary.
   *
   * @param errors	for collecting errors
   * @return		the model, null if failed to load
   */
  public T getModel(MessageCollection errors) {
    if (m_Model == null) {
      if (isLoggingEnabled())
        getLogger().info("Reloading model...");
      switch (m_ModelLoadingType) {
	case AUTO:
	  m_Model = loadAutomatically(errors);
	  break;
	case FILE:
	  m_Model = loadFromFile(errors);
	  break;
	case SOURCE_ACTOR:
	  m_Model = loadFromCallableActor(errors);
	  break;
	case STORAGE:
	  m_Model = loadFromStorage(errors);
	  break;
	default:
	  throw new IllegalStateException("Unhandled loading type: " + m_ModelLoadingType);
      }
      if (isLoggingEnabled())
        getLogger().info("Reload of model: " + (errors.isEmpty() ? "successful" : errors.toString()));

      // set context
      if (m_Model instanceof FlowContextHandler)
        ((FlowContextHandler) m_Model).setFlowContext(m_FlowContext);
    }

    return m_Model;
  }
}
