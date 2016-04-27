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
 * ForwardingScriptingEngine.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.scripting.engine;

import adams.scripting.command.RemoteCommand;
import adams.scripting.connection.Connection;
import adams.scripting.connection.DefaultConnection;

/**
 * Simply forwards incoming commands to the specified connection.
 *
 * TODO: directory for failed commands, reset failed commands on startup
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ForwardingScriptingEngine
  extends AbstractScriptingEngineEnhancer {

  private static final long serialVersionUID = -8291934093502029945L;

  /**
   * Simply used to grab the remote commands from the base scripting engine.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class RemoteCommandGrabber
    extends AbstractRemoteCommandHandler {

    private static final long serialVersionUID = 3897472061596797706L;

    /** the forwarding scripting engine. */
    protected ForwardingScriptingEngine m_Forwarder;

    /**
     * Returns a string describing the object.
     *
     * @return 			a description suitable for displaying in the gui
     */
    @Override
    public String globalInfo() {
      return "Simply used to grab the remote commands from the base scripting engine.";
    }

    /**
     * Sets the forwarding scripting engine.
     *
     * @param value	the scripting engine
     */
    public void setForwarder(ForwardingScriptingEngine value) {
      m_Forwarder = value;
    }

    /**
     * Returns the forwarding scripting engine.
     *
     * @return		the scripting engine, null if none set
     */
    public ForwardingScriptingEngine getForwarder() {
      return m_Forwarder;
    }

    /**
     * Hook method for checks before handling the command.
     *
     * @param cmd		the command to handle
     * @return		null if checks passed, otherwise error message
     */
    @Override
    protected String check(RemoteCommand cmd) {
      String	result;

      result = super.check(cmd);

      if (result == null) {
	if (m_Forwarder == null)
	  result = "No " + ForwardingScriptingEngine.class.getName() + " set!";
      }

      return result;
    }

    /**
     * Handles the command.
     *
     * @param cmd	the command to handle
     * @return		null if successful, otherwise error message
     */
    @Override
    protected String doHandle(RemoteCommand cmd) {
      return m_Forwarder.forward(cmd);
    }
  }

  /** the connection to forward the commands to. */
  protected Connection m_Forward;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Simply forwards incoming commands to the specified connection.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "forward", "forward",
      new DefaultConnection());
  }

  /**
   * Sets the connection to forward the commands to.
   *
   * @param value	the connection
   */
  public void setForward(Connection value) {
    m_Forward = value;
    reset();
  }

  /**
   * Returns the connection to forward the commands to.
   *
   * @return		the connection
   */
  public Connection getForward() {
    return m_Forward;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String forwardTipText() {
    return "The connection to forward the commands to.";
  }

  /**
   * Forwards a command.
   *
   * @param cmd		the command to forward
   * @return		null if successful, otherwise error message
   */
  public String forward(RemoteCommand cmd) {
    String	result;

    if (cmd.isRequest()) {
      if (isLoggingEnabled())
	getLogger().info("Forwarding request '" + cmd.getClass().getName() + "' to: " + m_Forward);
      result = m_Forward.sendRequest(cmd);
    }
    else {
      if (isLoggingEnabled())
	getLogger().info("Forwarding response '" + cmd.getClass().getName() + "' to: " + m_Forward);
      result = m_Forward.sendResponse(cmd);
    }

    if (result != null) {
      // TODO store command in "failed" directory
    }

    return result;
  }

  /**
   * Executes the scripting engine.
   *
   * @return		error message in case of failure to start up or run,
   * 			otherwise null
   */
  @Override
  public String execute() {
    RemoteCommandGrabber handler;

    handler = new RemoteCommandGrabber();
    handler.setForwarder(this);
    handler.setLoggingLevel(getLoggingLevel());
    m_ScriptingEngine.setCommandHandler(handler);

    // TODO load failed commands from "failed" directory

    return m_ScriptingEngine.execute();
  }

  /**
   * Pauses the execution.
   */
  @Override
  public void pauseExecution() {
    m_ScriptingEngine.pauseExecution();
    super.pauseExecution();
  }

  /**
   * Resumes the execution.
   */
  @Override
  public void resumeExecution() {
    m_ScriptingEngine.resumeExecution();
    super.resumeExecution();
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    m_ScriptingEngine.stopExecution();
    super.stopExecution();
  }
}
