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
 * RemoteFlowExecution.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.scripting.command.flow;

import adams.core.SerializationHelper;
import adams.core.VariableName;
import adams.core.io.FlowFile;
import adams.flow.control.StorageName;
import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;
import adams.multiprocess.CallableWithResult;
import adams.scripting.command.AbstractFlowAwareCommand;
import adams.scripting.engine.RemoteScriptingEngine;

import java.util.HashMap;
import java.util.logging.Level;

/**
 * Allows the remote execution of flows, including the transfer of
 * storage items from the flow triggering the remote execution.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RemoteFlowExecution
  extends AbstractFlowAwareCommand {

  private static final long serialVersionUID = -3350680106789169314L;

  /** the flow to execute remotely. */
  protected FlowFile m_Flow;

  /** the storage items to transmit. */
  protected StorageName[] m_StorageNames;

  /** the variables to transmit. */
  protected VariableName[] m_VariableNames;

  /** the instantiated flow. */
  protected Actor m_Actor;

  /** the storage items. */
  protected HashMap<String,Object> m_Storage;

  /** the variables. */
  protected HashMap<String,String> m_Variables;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Executes the specified flow remotely, optional transfer of storage "
	+ "value items (must be serializable!).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "flow", "flow",
      new FlowFile("."));

    m_OptionManager.add(
      "storage-name", "storageNames",
      new StorageName[0]);

    m_OptionManager.add(
      "variable-name", "variableNames",
      new VariableName[0]);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Actor     = null;
    m_Storage   = new HashMap<>();
    m_Variables = new HashMap<>();
  }

  /**
   * Sets the flow to execute remotely.
   *
   * @param value	the flow
   */
  public void setFlow(FlowFile value) {
    m_Flow = value;
    reset();
  }

  /**
   * Returns the flow to execute remotely.
   *
   * @return		the flow
   */
  public FlowFile getFlow() {
    return m_Flow;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String flowTipText() {
    return "The flow to execute remotely.";
  }

  /**
   * Sets the names of the storage items to transfer.
   *
   * @param value	the storage names
   */
  public void setStorageNames(StorageName[] value) {
    m_StorageNames = value;
    reset();
  }

  /**
   * Returns the names of the storage items to transfer.
   *
   * @return		the storage names
   */
  public StorageName[] getStorageNames() {
    return m_StorageNames;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String storageNamesTipText() {
    return "The (optional) storage items to transfer.";
  }

  /**
   * Sets the names of the variables to transfer.
   *
   * @param value	the variable names
   */
  public void setVariableNames(VariableName[] value) {
    m_VariableNames = value;
    reset();
  }

  /**
   * Returns the names of the variables to transfer.
   *
   * @return		the variable names
   */
  public VariableName[] getVariableNames() {
    return m_VariableNames;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String variableNamesTipText() {
    return "The (optional) variables to transfer.";
  }

  /**
   * Sets the actor to use instead of loading it from disk. {@link #m_Flow}
   * must point to a directory.
   *
   * @param value	the actor to use instead
   */
  public void setActor(Actor value) {
    m_Actor = value;
  }

  /**
   * Sets the payload for the request.
   *
   * @param value	the payload
   */
  @Override
  public void setRequestPayload(byte[] value) {
    Object[]	data;

    if (value.length == 0)
      return;

    try {
      data        = SerializationHelper.fromByteArray(value);
      m_Actor     = (Actor) data[0];
      m_Storage   = (HashMap<String,Object>) data[1];
      m_Variables = (HashMap<String,String>) data[2];
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to deserialize payload!", e);
    }
  }

  /**
   * Returns the payload of the request, if any.
   *
   * @return		the payload
   */
  @Override
  public byte[] getRequestPayload() {
    byte[]	result;
    Object[]	data;

    data = new Object[]{m_Actor, m_Storage, m_Variables};
    try {
      result = SerializationHelper.toByteArray(data);
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to serialize payload data!", e);
      result = new byte[0];
    }

    return result;
  }

  /**
   * Returns the objects that represent the request payload.
   *
   * @return		the objects
   */
  public Object[] getRequestPayloadObjects() {
    return new Object[]{m_Actor, m_Storage, m_Variables};
  }

  /**
   * Hook method for preparing the request payload,
   */
  @Override
  protected void prepareRequestPayload() {
    Actor			actor;
    HashMap<String,Object> 	storage;
    HashMap<String,String> 	vars;

    super.prepareRequestPayload();

    if (!m_Flow.isDirectory()) {
      actor = ActorUtils.read(m_Flow.getAbsolutePath());
      if (actor == null)
	getLogger().severe("Failed to load flow from: " + m_Flow);
      m_Actor = actor;
    }

    storage = new HashMap<>();
    vars    = new HashMap<>();
    if (getFlowContext() != null) {
      for (StorageName name : m_StorageNames)
	storage.put(name.getValue(), getFlowContext().getStorageHandler().getStorage().get(name));
      for (VariableName name : m_VariableNames)
	vars.put(name.getValue(), getFlowContext().getVariables().get(name.getValue()));
    }
    m_Storage   = storage;
    m_Variables = vars;
  }

  /**
   * Handles the request.
   *
   * @param engine	the remote engine handling the request
   * @return		null if successful, otherwise error message
   */
  protected String doHandleRequest(RemoteScriptingEngine engine) {
    String 	result;

    if (m_Actor != null) {
      result = m_Actor.setUp();
      if (result == null) {
	// transfer data
	for (String key : m_Storage.keySet())
	  m_Actor.getStorageHandler().getStorage().put(new StorageName(key), m_Storage.get(key));
	for (String key : m_Variables.keySet())
	  m_Actor.getVariables().set(key, m_Variables.get(key));

	// execute job
	engine.executeJob(new CallableWithResult<String>() {
	  @Override
	  protected String doCall() throws Exception {
	    String res = m_Actor.execute();
	    if (res != null)
	      getLogger().severe("Actor not successful:\n" + res);
	    m_Actor.cleanUp();
	    return res;
	  }
	});
      }
    }
    else {
      result = "No actor to execute!";
    }

    return result;
  }
}
