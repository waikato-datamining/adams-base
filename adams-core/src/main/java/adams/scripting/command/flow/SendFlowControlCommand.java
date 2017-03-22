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
 * SendFlowControlCommand.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.scripting.command.flow;

import adams.core.Pausable;
import adams.flow.control.Flow;
import adams.flow.control.RunningFlowsRegistry;
import adams.flow.core.Actor;
import adams.scripting.command.AbstractRemoteCommandOnFlowWithResponse;

/**
 * Sends a control command to a flow (pause/resume/stop/start).
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SendFlowControlCommand
  extends AbstractRemoteCommandOnFlowWithResponse {

  private static final long serialVersionUID = -3350680106789169314L;

  /**
   * Enumeration of available commands.
   */
  public enum Command {
    PAUSE,
    RESUME,
    STOP,
    RESTART,
  }

  /** response: success. */
  public final static String RESPONSE_SUCCESS = "Success";

  /** response: failed. */
  public final static String RESPONSE_FAILED = "Failed";

  /** response: command not supported. */
  public final static String RESPONSE_NO_SUPPORTED = "Command not supported";

  /** response: already paused. */
  public final static String RESPONSE_ALREADY_PAUSED = "Already paused";

  /** response: already running. */
  public final static String RESPONSE_ALREADY_RUNNING = "Already running";

  /** response: root not a Flow actor. */
  public final static String RESPONSE_ROOT_NOT_FLOW = "Root is not a Flow actor";

  /** the command. */
  protected Command m_Command;

  /** the response. */
  protected String m_Response;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Sends a control command to the running/registered flow using its ID.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "command", "command",
      Command.PAUSE);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String IDTipText() {
    return "The ID of the flow to operate on.";
  }

  /**
   * Sets the command to send.
   *
   * @param value	the command
   */
  public void setCommand(Command value) {
    m_Command = value;
    reset();
  }

  /**
   * Returns the command to send.
   *
   * @return		the command
   */
  public Command getCommand() {
    return m_Command;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String commandTipText() {
    return "The command to send.";
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
   * Returns the objects that represent the request payload.
   *
   * @return		the objects
   */
  public Object[] getRequestPayloadObjects() {
    return new Object[0];
  }

  /**
   * Sets the payload for the response.
   *
   * @param value	the payload
   */
  @Override
  public void setResponsePayload(byte[] value) {
    if (value.length == 0) {
      m_Response = null;
      return;
    }

    m_Response = new String(value);
  }

  /**
   * Returns the payload of the response, if any.
   *
   * @return		the payload
   */
  @Override
  public byte[] getResponsePayload() {
    if (m_Response == null)
      return new byte[0];
    else
      return m_Response.getBytes();
  }

  /**
   * Hook method for preparing the response payload,
   */
  @Override
  protected void prepareResponsePayload() {
    Flow flow;
    Actor	actor;

    super.prepareResponsePayload();

    actor = retrieveFlow(false);
    flow  = null;
    if (actor != null) {
      if (actor instanceof Flow)
	flow = (Flow) actor;
      else
	m_Response = RESPONSE_ROOT_NOT_FLOW;
    }

    if (flow != null) {
      switch (m_Command) {
	case PAUSE:
	  if (((Pausable) actor).isPaused()) {
	    m_Response = RESPONSE_ALREADY_PAUSED;
	  }
	  else {
	    flow.pauseExecution();
	    m_Response = RESPONSE_SUCCESS;
	  }
	  break;

	case RESUME:
	  if (!((Pausable) actor).isPaused()) {
	    m_Response = RESPONSE_ALREADY_RUNNING;
	  }
	  else {
	    flow.resumeExecution();
	    m_Response = RESPONSE_SUCCESS;
	  }
	  break;

	case STOP:
	  flow.stopExecution();
	  flow.wrapUp();
	  flow.cleanUp();
	  m_Response = RESPONSE_SUCCESS;
	  break;

	case RESTART:
	  if (!flow.isStopped())
	    flow.stopExecution();
	  m_ErrorMessage = flow.setUp();
	  if (m_ErrorMessage == null)
	    m_ErrorMessage = flow.execute();
	  if (m_ErrorMessage == null) {
	    m_Response = RESPONSE_SUCCESS;
	    // make sure flow is reregistered
	    RunningFlowsRegistry.getSingleton().addFlow(flow);
	  }
	  else {
	    m_Response = RESPONSE_FAILED;
	  }
	  break;

	default:
	  m_Response = RESPONSE_NO_SUPPORTED;
	  break;
      }
    }
  }

  /**
   * Returns the objects that represent the response payload.
   *
   * @return		the objects
   */
  public Object[] getResponsePayloadObjects() {
    return new Object[]{m_Response};
  }
}
