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
 * CallableActorsHandler.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.scripting.requesthandler;

import adams.core.option.OptionUtils;
import adams.flow.container.RemoteCommandContainer;
import adams.flow.core.Actor;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.CallableActorReference;
import adams.flow.core.CallableActorUser;
import adams.flow.core.InputConsumer;
import adams.flow.core.Token;
import adams.scripting.command.RemoteCommand;

/**
 * Forwards the requests to the specified (optional) callable actor..
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CallableActorsHandler
  extends AbstractRequestHandler
  implements CallableActorUser {

  private static final long serialVersionUID = -1573977100207493603L;

  /** the callable name. */
  protected CallableActorReference m_CallableName;

  /** whether the callable actor has been initialized. */
  protected boolean m_CallableActorInitialized;

  /** the callable actor. */
  protected Actor m_CallableActor;

  /** the helper class. */
  protected CallableActorHelper m_Helper;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Forwards the requests to the specified (optional) callable actor.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "callable", "callableName",
	    new CallableActorReference("unknown"));
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_CallableActor            = null;
    m_CallableActorInitialized = false;
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Helper = new CallableActorHelper();
  }

  /**
   * Sets the name of the callable transformer to use.
   *
   * @param value 	the callable name
   */
  public void setCallableName(CallableActorReference value) {
    m_CallableName = value;
    reset();
  }

  /**
   * Returns the name of the callable transformer in use.
   *
   * @return 		the callable name
   */
  public CallableActorReference getCallableName() {
    return m_CallableName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String callableNameTipText() {
    return "The name of the callable transformer to use.";
  }

  /**
   * Returns the currently set callable actor.
   *
   * @return		the actor, can be null
   */
  @Override
  public Actor getCallableActor() {
    return m_CallableActor;
  }

  /**
   * Tries to find the callable actor referenced by its global name.
   *
   * @return		the callable actor or null if not found
   */
  protected Actor findCallableActor() {
    Actor	result;

    if (getOwner() == null) {
      getLogger().severe("No owner set!");
      return null;
    }
    if (getOwner().getFlowContext() == null) {
      getLogger().severe("No flow context available!");
      return null;
    }

    result = m_Helper.findCallableActorRecursive(getOwner().getFlowContext(), getCallableName());
    if (!(result instanceof InputConsumer)) {
      getLogger().severe("Callable actor '" + result.getFullName() + "' does not accept input" + (m_CallableActor == null ? "!" : m_CallableActor.getClass().getName()));
      result = null;
    }

    return result;
  }

  /**
   * Sends the remote command to the callable actor.
   *
   * @param event	the event type
   * @param cmd		the remote command
   * @param msg		the message, null if none available
   * @return		null if successful, otherwise error message
   */
  protected String send(String event, RemoteCommand cmd, String msg) {
    RemoteCommandContainer	cont;
    String			result;

    result = null;

    if (!m_CallableActorInitialized) {
      m_CallableActor        = findCallableActor();
      m_CallableActorInitialized = true;
    }

    if (m_CallableActor != null) {
      if (msg != null)
	cont = new RemoteCommandContainer(event, cmd, msg);
      else
	cont = new RemoteCommandContainer(event, cmd);
      synchronized(m_CallableActor) {
	((InputConsumer) m_CallableActor).input(new Token(cont));
	result = m_CallableActor.execute();
      }
    }
    else {
      result = "No callable actor available!";
    }

    return result;
  }

  /**
   * Handles successfuly requests.
   *
   * @param cmd		the command with the request
   */
  @Override
  public void requestSuccessful(RemoteCommand cmd) {
    String 	error;

    if (!m_Enabled)
      return;

    if (isLoggingEnabled())
      getLogger().fine("Successful request: " + OptionUtils.getCommandLine(cmd) + "\n" + cmd);

    error = send(RemoteCommandContainer.EVENT_REQUESTSUCCESSFUL, cmd, null);
    if (error != null)
      getLogger().severe(error);
  }

  /**
   * Handles failed requests.
   *
   * @param cmd		the command with the request
   * @param msg		the optional error message, can be null
   */
  @Override
  public void requestFailed(RemoteCommand cmd, String msg) {
    String 	error;

    if (!m_Enabled)
      return;

    if (isLoggingEnabled())
      getLogger().fine("Failed request: " + OptionUtils.getCommandLine(cmd) + "\nMessage: " + msg + "\n" + cmd);

    error = send(RemoteCommandContainer.EVENT_REQUESTFAILED, cmd, msg);
    if (error != null)
      getLogger().severe(error);
  }

  /**
   * Handles rejected requests.
   *
   * @param cmd		the command with the request
   * @param msg		the optional error message, can be null
   */
  @Override
  public void requestRejected(RemoteCommand cmd, String msg) {
    String 	error;

    if (!m_Enabled)
      return;

    if (isLoggingEnabled())
      getLogger().info("Rejected request: " + OptionUtils.getCommandLine(cmd) + "\nMessage: " + msg + "\n" + cmd);

    error = send(RemoteCommandContainer.EVENT_REQUESTREJECTED, cmd, msg);
    if (error != null)
      getLogger().severe(error);
  }
}
