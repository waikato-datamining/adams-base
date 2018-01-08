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
 * ObjectRetriever.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.core;

import adams.core.MessageCollection;
import adams.core.Utils;
import adams.core.base.BaseClassname;
import adams.core.option.AbstractOptionHandler;
import adams.flow.control.StorageName;
import nz.ac.waikato.cms.locator.ClassLocator;

/**
 * For retrieving objects from storage or source actors.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ObjectRetriever
  extends AbstractOptionHandler
  implements FlowContextHandler {

  private static final long serialVersionUID = 7549636204208995661L;

  /**
   * The enumeration for the retrieval type.
   */
  public enum RetrievalType {
    AUTO,
    SOURCE_ACTOR,
    STORAGE
  }

  /** the flow context. */
  protected Actor m_FlowContext;

  /** the retrieval type. */
  protected RetrievalType m_RetrievalType;

  /** the object actor. */
  protected CallableActorReference m_ObjectActor;

  /** the object storage. */
  protected StorageName m_ObjectStorage;

  /** the superclass or interface to restrict to. */
  protected BaseClassname m_ObjectType;

  /** for locating callable actors. */
  protected CallableActorHelper m_Helper;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return
      "Helper class for retrieving objects from within the flow, source actors or storage.\n"
        + "The following order is used to retrieve the object (when using " + RetrievalType.AUTO + "):\n"
	+ "1. source actor present?\n"
	+ "2. storage item present?";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "retrieval-type", "retrievalType",
      RetrievalType.AUTO);

    m_OptionManager.add(
      "object-actor", "objectActor",
      new CallableActorReference());

    m_OptionManager.add(
      "object-storage", "objectStorage",
      new StorageName());

    m_OptionManager.add(
      "object-type", "objectType",
      new BaseClassname(Object.class));
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
   * Sets the retrieval type. In case of {@link RetrievalType#AUTO}, first
   * file, then callable actor, then storage.
   *
   * @param value	the type
   */
  public void setRetrievalType(RetrievalType value) {
    m_RetrievalType = value;
    reset();
  }

  /**
   * Returns the retrieval type. In case of {@link RetrievalType#AUTO}, first
   * file, then callable actor, then storage.
   *
   * @return		the type
   */
  public RetrievalType getRetrievalType() {
    return m_RetrievalType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String retrievalTypeTipText() {
    return
      "Determines how to retrieve the object, in case of " + RetrievalType.AUTO + ", "
	+ "first the callable actor is checked and then the storage.";
  }

  /**
   * Sets the callable actor to retrieve the object from.
   *
   * @param value	the actor reference
   */
  public void setObjectActor(CallableActorReference value) {
    m_ObjectActor = value;
    reset();
  }

  /**
   * Returns the callable actor to retrieve the object from.
   *
   * @return		the actor reference
   */
  public CallableActorReference getObjectActor() {
    return m_ObjectActor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String objectActorTipText() {
    return "The callable actor (source) to retrieve the object from, ignored if not present.";
  }

  /**
   * Sets the storage item name to get the object from.
   *
   * @param value	the storage name
   */
  public void setObjectStorage(StorageName value) {
    m_ObjectStorage = value;
    reset();
  }

  /**
   * Returns the storage item name to get the object from.
   *
   * @return		the storage name
   */
  public StorageName getObjectStorage() {
    return m_ObjectStorage;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String objectStorageTipText() {
    return "The storage item to retrieve the object from, ignored if not present.";
  }

  /**
   * Sets the interface or superclass to restrict the objects to.
   *
   * @param value	the class
   */
  public void setObjectType(BaseClassname value) {
    m_ObjectType = value;
    reset();
  }

  /**
   * Returns the interface or superclass to restrict the objects to.
   *
   * @return		the class
   */
  public BaseClassname getObjectType() {
    return m_ObjectType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String objectTypeTipText() {
    return "The interface or superclass to restrict the object to.";
  }

  /**
   * Retrieves the object from the callable actor.
   *
   * @param errors	for collecting errors
   * @return		the object, null if failed to retrieve
   */
  protected Object retrieveFromCallableActor(MessageCollection errors) {
    Object	result;

    if (m_FlowContext == null) {
      errors.add("No flow context specified!");
      return null;
    }

    // retrieve object from callable actor
    try {
      result = CallableActorHelper.getSetup(null, m_ObjectActor, m_FlowContext, errors);
    }
    catch (Exception e) {
      result = null;
      errors.add("Failed to retrieve object from callable actor '" + m_ObjectActor + "': ", e);
    }

    return result;
  }

  /**
   * Retrieves the object from the storage item.
   *
   * @param errors	for collecting errors
   * @return		the object, null if failed to load
   */
  protected Object retrieveFromStorage(MessageCollection errors) {
    if (m_FlowContext == null) {
      errors.add("No flow context specified!");
      return null;
    }
    if (!m_FlowContext.getStorageHandler().getStorage().has(m_ObjectStorage)) {
      errors.add("Storage item not available: " + m_ObjectStorage);
      return null;
    }

    return m_FlowContext.getStorageHandler().getStorage().get(m_ObjectStorage);
  }

  /**
   * Retrieves the object automatically from file/callable actor/storage.
   *
   * @param errors	for collecting errors
   * @return		the object, null if failed
   */
  protected Object retrieveAutomatically(MessageCollection errors) {
    Object 	result;

    result = null;

    if (m_FlowContext == null) {
      errors.add("No flow context set!");
    }
    else {
      if (m_Helper.findCallableActorRecursive(m_FlowContext, m_ObjectActor) != null)
        result = retrieveFromCallableActor(errors);
      else
        result = retrieveFromStorage(errors);
    }

    return result;
  }

  /**
   * Retrieves the object.
   *
   * @param errors	for collecting errors
   * @return		the object, null if failed to load
   */
  public Object getObject(MessageCollection errors) {
    Object	result;

    if (isLoggingEnabled())
      getLogger().info("Retrieving object...");

    switch (m_RetrievalType) {
      case AUTO:
	result = retrieveAutomatically(errors);
	break;
      case SOURCE_ACTOR:
	result = retrieveFromCallableActor(errors);
	break;
      case STORAGE:
	result = retrieveFromStorage(errors);
	break;
      default:
	throw new IllegalStateException("Unhandled retrieval type: " + m_RetrievalType);
    }

    // check type
    if (errors.isEmpty()) {
      if (!(ClassLocator.matches(m_ObjectType.classValue(), result.getClass())))
        errors.add("The retrieved object is not of type " + m_ObjectType + ": " + Utils.classToString(result));
    }

    if (isLoggingEnabled())
      getLogger().info("Retrieval of object: " + (errors.isEmpty() ? "successful" : errors.toString()));

    return result;
  }
}
