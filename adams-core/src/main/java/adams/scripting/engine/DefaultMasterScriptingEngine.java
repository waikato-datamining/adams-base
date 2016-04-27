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
 * MasterScriptingEngine.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.scripting.engine;

import adams.core.logging.LoggingLevel;
import adams.scripting.command.RemoteCommand;
import adams.scripting.command.distributed.DeregisterSlave;
import adams.scripting.command.distributed.RegisterSlave;
import adams.scripting.connection.Connection;
import adams.scripting.connection.LoadBalancer;

/**
 * Manages slave scripting engines and sends them jobs for execution.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DefaultMasterScriptingEngine
  extends AbstractScriptingEngineEnhancer
  implements MasterScriptingEngine {

  private static final long serialVersionUID = 8181130583432049922L;

  /**
   * Handles the registering/deregistering of slaves.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class RemoteCommandGrabber
    extends AbstractRemoteCommandHandler {

    private static final long serialVersionUID = 3426984517327055710L;

    /** the master. */
    protected MasterScriptingEngine m_Master;

    /**
     * Returns a string describing the object.
     *
     * @return 		a description suitable for displaying in the gui
     */
    @Override
    public String globalInfo() {
      return "Handles the register/derigister of slaves.";
    }

    /**
     * Sets the master scripting engine this command handler belongs to.
     *
     * @param value	the owner
     */    public void setMaster(MasterScriptingEngine value) {
      m_Master = value;
    }

    /**
     * Returns the master scripting engine this command handler belongs to.
     *
     * @return		the owner
     */
    public MasterScriptingEngine getMaster() {
      return m_Master;
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
	if (m_Master == null)
	  result = "No " + MasterScriptingEngine.class.getName() + " set!";
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
      String	result;

      result = null;

      if (cmd instanceof RegisterSlave)
	m_Master.registerSlave(((RegisterSlave) cmd).getConnection());
      else if (cmd instanceof DeregisterSlave)
	m_Master.deregisterSlave(((DeregisterSlave) cmd).getConnection());
      else
	result = m_Master.sendCommand(cmd);

      return result;
    }
  }

  /** for keeping track of slaves. */
  protected LoadBalancer m_Slaves;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Manages slave scripting engines and sends them jobs for execution.";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Slaves = new LoadBalancer();
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Slaves.setConnections(new Connection[0]);
  }

  /**
   * Sets the logging level.
   *
   * @param value 	the level
   */
  @Override
  public synchronized void setLoggingLevel(LoggingLevel value) {
    super.setLoggingLevel(value);
    m_Slaves.setLoggingLevel(value);
  }

  /**
   * Registers a slave with the given connection.
   *
   * @param conn	the connection of the slave
   */
  public void registerSlave(Connection conn) {
    if (isLoggingEnabled())
      getLogger().info("Registering: " + conn);
    m_Slaves.addConnection(conn);
  }

  /**
   * Deregisters a slave with the given connection.
   *
   * @param conn	the connection of the slave
   */
  public void deregisterSlave(Connection conn) {
    if (isLoggingEnabled())
      getLogger().info("Deregistering: " + conn);
    m_Slaves.removeConnection(conn);
  }

  /**
   * Sends the command to a slave.
   *
   * @param cmd		the command to send
   * @return		null if successful, otherwise error message
   */
  public String sendCommand(RemoteCommand cmd) {
    String	result;

    if (cmd.isRequest()) {
      if (isLoggingEnabled())
	getLogger().info("Sending request to slave: " + cmd.getClass().getName());
      result = m_Slaves.sendRequest(cmd);
    }
    else {
      if (isLoggingEnabled())
	getLogger().info("Sending response to slave: " + cmd.getClass().getName());
      result = m_Slaves.sendResponse(cmd);
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
      handler.setMaster(this);
      handler.setLoggingLevel(getLoggingLevel());
      m_ScriptingEngine.setCommandHandler(handler);
    }

    return result;
  }
}
