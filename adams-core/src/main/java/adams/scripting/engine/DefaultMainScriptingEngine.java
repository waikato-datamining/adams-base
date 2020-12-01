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
 * DefaultMainScriptingEngine.java
 * Copyright (C) 2016-2020 University of Waikato, Hamilton, NZ
 */

package adams.scripting.engine;

import adams.core.logging.LoggingLevel;
import adams.scripting.command.RemoteCommand;
import adams.scripting.command.basic.Kill;
import adams.scripting.command.distributed.DeregisterWorker;
import adams.scripting.command.distributed.KillWorkers;
import adams.scripting.command.distributed.RegisterWorker;
import adams.scripting.connection.Connection;
import adams.scripting.connection.LoadBalancer;
import adams.scripting.processor.RemoteCommandProcessor;

/**
 * Manages worker scripting engines and sends them jobs for execution.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class DefaultMainScriptingEngine
  extends AbstractScriptingEngineEnhancer
  implements MainScriptingEngine {

  private static final long serialVersionUID = 8181130583432049922L;

  /**
   * Handles the registering/deregistering of workers.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   */
  public static class RemoteCommandGrabber
    extends AbstractRemoteCommandHandler {

    private static final long serialVersionUID = 3426984517327055710L;

    /** the main. */
    protected MainScriptingEngine m_Main;

    /**
     * Returns a string describing the object.
     *
     * @return 		a description suitable for displaying in the gui
     */
    @Override
    public String globalInfo() {
      return "Handles the register/deregister of workers.";
    }

    /**
     * Sets the main scripting engine this command handler belongs to.
     *
     * @param value	the owner
     */
    public void setMain(MainScriptingEngine value) {
      m_Main = value;
    }

    /**
     * Returns the main scripting engine this command handler belongs to.
     *
     * @return		the owner
     */
    public MainScriptingEngine getMain() {
      return m_Main;
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
	if (m_Main == null)
	  result = "No " + MainScriptingEngine.class.getName() + " set!";
      }

      return result;
    }

    /**
     * Handles the command.
     *
     * @param cmd	the command to handle
     * @param processor the processor for formatting/parsing
     * @return		null if successful, otherwise error message
     */
    @Override
    protected String doHandle(RemoteCommand cmd, RemoteCommandProcessor processor) {
      String	result;

      result = null;

      if (cmd instanceof RegisterWorker)
	m_Main.registerWorker(((RegisterWorker) cmd).getConnection());
      else if (cmd instanceof DeregisterWorker)
	m_Main.deregisterWorker(((DeregisterWorker) cmd).getConnection());
      else if (cmd instanceof KillWorkers)
	m_Main.killWorkers();
      else
	result = m_Main.sendCommand(cmd);

      return result;
    }
  }

  /** for keeping track of workers. */
  protected LoadBalancer m_Workers;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Manages worker scripting engines and sends them jobs for execution.";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Workers = new LoadBalancer();
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Workers.setConnections(new Connection[0]);
  }

  /**
   * Sets the logging level.
   *
   * @param value 	the level
   */
  @Override
  public synchronized void setLoggingLevel(LoggingLevel value) {
    super.setLoggingLevel(value);
    m_Workers.setLoggingLevel(value);
  }

  /**
   * Registers a worker with the given connection.
   *
   * @param conn	the connection of the worker
   */
  public void registerWorker(Connection conn) {
    if (isLoggingEnabled())
      getLogger().info("Registering: " + conn);
    m_Workers.addConnection(conn);
  }

  /**
   * Deregisters a worker with the given connection.
   *
   * @param conn	the connection of the worker
   */
  public void deregisterWorker(Connection conn) {
    if (isLoggingEnabled())
      getLogger().info("Deregistering: " + conn);
    m_Workers.removeConnection(conn);
  }

  /**
   * Kills all workers registered.
   */
  public void killWorkers() {
    Connection[]	conns;
    Kill		kill;
    String		msg;

    conns = m_Workers.getConnections().clone();
    for (Connection conn: conns) {
      if (isLoggingEnabled())
	getLogger().info("Sending kill to " + conn);
      m_Workers.removeConnection(conn);
      kill = new Kill();
      msg  = conn.sendRequest(kill, m_CommandProcessor);
      if (msg != null)
	getLogger().severe(msg);
    }
  }

  /**
   * Sends the command to a worker.
   *
   * @param cmd		the command to send
   * @return		null if successful, otherwise error message
   */
  public String sendCommand(RemoteCommand cmd) {
    String	result;

    if (cmd.isRequest()) {
      if (isLoggingEnabled())
	getLogger().info("Sending request to worker: " + cmd.getClass().getName());
      result = m_Workers.sendRequest(cmd, m_CommandProcessor);
    }
    else {
      if (isLoggingEnabled())
	getLogger().info("Sending response to worker: " + cmd.getClass().getName());
      result = m_Workers.sendResponse(cmd, m_CommandProcessor);
    }

    return result;
  }

  @Override
  protected String preExecute() {
    String			result;
    RemoteCommandGrabber	handler;

    result = super.preExecute();

    if (result == null) {
      handler = new RemoteCommandGrabber();
      handler.setMain(this);
      handler.setLoggingLevel(getLoggingLevel());
      m_ScriptingEngine.setCommandHandler(handler);
    }

    return result;
  }
}
