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
 * RunRemoteFlow.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.scripting.command.flow;

import adams.core.MessageCollection;
import adams.flow.control.Flow;
import adams.flow.control.RunningFlowsRegistry;
import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;
import adams.scripting.command.AbstractCommandWithResponse;

import java.io.File;

/**
 * Loads and runs a flow on a remote server.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RunRemoteFlow
  extends AbstractCommandWithResponse {

  private static final long serialVersionUID = -3350680106789169314L;

  /** the flow to run. */
  protected File m_FlowFile;

  /** whether to register the flow. */
  protected boolean m_RegisterFlow;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Loads and runs a flow on a remote server.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "flow-file", "flowFile",
      new File("."));

    m_OptionManager.add(
      "register-flow", "registerFlow",
      false);
  }

  /**
   * Sets the remote flow file.
   *
   * @param value	the flow
   */
  public void setFlowFile(File value) {
    m_FlowFile = value;
    reset();
  }

  /**
   * Returns the remote flow file.
   *
   * @return		the flow
   */
  public File getFlowFile() {
    return m_FlowFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String flowFileTipText() {
    return "The remote flow file to load and run.";
  }

  /**
   * Sets whether to register the flow.
   *
   * @param value	true if to register
   */
  public void setRegisterFlow(boolean value) {
    m_RegisterFlow = value;
    reset();
  }

  /**
   * Returns whether to register the flow.
   *
   * @return		true if to register
   */
  public boolean getRegisterFlow() {
    return m_RegisterFlow;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String registerFlowTipText() {
    return "If enabled, then the flow will get registered.";
  }

  /**
   * Ignored.
   *
   * @param value	the payload
   */
  @Override
  public void setRequestPayload(byte[] value) {
  }

  /**
   * Always zero-length array.
   *
   * @return		the payload
   */
  @Override
  public byte[] getRequestPayload() {
    return new byte[0];
  }

  /**
   * Always zero-length array.
   *
   * @return		the objects
   */
  public Object[] getRequestPayloadObjects() {
    return new Object[0];
  }

  /**
   * Ignored.
   *
   * @param value	the payload
   */
  @Override
  public void setResponsePayload(byte[] value) {
  }

  /**
   * Always zero-length array.
   *
   * @return		the payload
   */
  @Override
  public byte[] getResponsePayload() {
    return new byte[0];
  }

  /**
   * Always zero-length array.
   *
   * @return		the objects
   */
  public Object[] getResponsePayloadObjects() {
    return new Object[0];
  }

  /**
   * Hook method for preparing the response payload,
   */
  @Override
  protected void prepareResponsePayload() {
    Actor 		actor;
    final Flow		flow;
    MessageCollection 	errors;

    if (!m_FlowFile.exists()) {
      m_ErrorMessage = "Flow does not exist: " + m_FlowFile;
      return;
    }
    if (m_FlowFile.isDirectory()) {
      m_ErrorMessage = "Flow file points to a directory: " + m_FlowFile;
      return;
    }

    // load flow
    if (isLoggingEnabled())
      getLogger().info("Loading flow: " + m_FlowFile);
    errors = new MessageCollection();
    actor  = ActorUtils.read(m_FlowFile.getAbsolutePath(), errors);
    if (actor == null) {
      m_ErrorMessage = "Failed to read flow from: " + m_FlowFile;
      if (!errors.isEmpty())
	m_ErrorMessage += "\n" + errors;
      return;
    }
    if (!(actor instanceof Flow)) {
      m_ErrorMessage = "Root actor is not a " + Flow.class.getName() + "!";
      return;
    }
    flow = (Flow) actor;

    // setup
    ActorUtils.updateProgrammaticVariables(flow, m_FlowFile);
    m_ErrorMessage = flow.setUp();
    if (m_ErrorMessage != null)
      return;
    ActorUtils.updateProgrammaticVariables(flow, m_FlowFile);

    // run
    new Thread(() -> {
      flow.execute();
      flow.cleanUp();
    }).start();

    // register?
    if (m_RegisterFlow)
      RunningFlowsRegistry.getSingleton().addFlow(flow);
  }
}
