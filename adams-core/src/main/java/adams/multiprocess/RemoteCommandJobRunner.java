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
 * RemoteCommandJobRunner.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.multiprocess;

import adams.scripting.command.RemoteCommand;
import adams.scripting.connection.Connection;
import adams.scripting.connection.DefaultConnection;
import adams.scripting.engine.AbstractRemoteCommandHandler;
import adams.scripting.engine.DefaultScriptingEngine;
import adams.scripting.engine.MasterScriptingEngine;
import adams.scripting.engine.RemoteScriptingEngine;

/**
 * Utilizes the remote command framework for sending jobs to a remote machine.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RemoteCommandJobRunner
  extends AbstractMetaJobRunner {

  private static final long serialVersionUID = 6907498356453602816L;

  /**
   * Handles the job response.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class RemoteCommandGrabber
    extends AbstractRemoteCommandHandler {

    private static final long serialVersionUID = 3426984517327055710L;

    /** the master. */
    protected RemoteCommandJobRunner m_Master;

    /**
     * Returns a string describing the object.
     *
     * @return 		a description suitable for displaying in the gui
     */
    @Override
    public String globalInfo() {
      return "Handles job response.";
    }

    /**
     * Sets the master scripting engine this command handler belongs to.
     *
     * @param value	the owner
     */
    public void setMaster(RemoteCommandJobRunner value) {
      m_Master = value;
    }

    /**
     * Returns the master scripting engine this command handler belongs to.
     *
     * @return		the owner
     */
    public RemoteCommandJobRunner getMaster() {
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

      if (cmd instanceof adams.scripting.command.distributed.JobRunner) {
	// TODO
      }
      else {
	getLogger().warning("Handles only " + adams.scripting.command.distributed.JobRunner.class.getName() + " commands - ignored: " + cmd);
      }

      return result;
    }
  }

  /** the connection to use for sending the command. */
  protected Connection m_RequestConnection;

  /** the connection to use for sending back the response. */
  protected Connection m_ResponseConnection;

  /** the scriping engine to use for listening for the response. */
  protected RemoteScriptingEngine m_ResponseScriptingEngine;

  /** the response JobRunner object. */
  protected JobRunner m_ResponseJobRunner;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Uses the remote command framework for sending the jobs to the specified "
	+ "connection for execution.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "request-connection", "requestConnection",
      getDefaultRequestConnection());

    m_OptionManager.add(
      "response-connection", "responseConnection",
      getDefaultResponseConnection());

    m_OptionManager.add(
      "response-scripting-engine", "responseScriptingEngine",
      getDefaultResponseScriptingEngine());
  }

  /**
   * Returns the default connection for sending the request.
   *
   * @return		the connection
   */
  protected Connection getDefaultRequestConnection() {
    return new DefaultConnection();
  }

  /**
   * Sets the connection to use for sending the request.
   *
   * @param value 	the connection
   */
  public void setRequestConnection(Connection value) {
    m_RequestConnection = value;
    reset();
  }

  /**
   * Returns the connection to use for sending the request.
   *
   * @return		the connection
   */
  public Connection getRequestConnection() {
    return m_RequestConnection;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String requestConnectionTipText() {
    return "The connection to use for sending the request.";
  }

  /**
   * Returns the default connection for sending the response back.
   *
   * @return		the connection
   */
  protected Connection getDefaultResponseConnection() {
    DefaultConnection	result;

    result = new DefaultConnection();
    result.setPort(result.getPort() + 1);

    return result;
  }

  /**
   * Sets the connection to use for sending the response back.
   *
   * @param value 	the connection
   */
  public void setResponseConnection(Connection value) {
    m_ResponseConnection = value;
    reset();
  }

  /**
   * Returns the connection to use for sending the response back.
   *
   * @return		the connection
   */
  public Connection getResponseConnection() {
    return m_ResponseConnection;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String responseConnectionTipText() {
    return "The connection to use for sending the response back (must match the response scripting engine!).";
  }

  /**
   * Returns the default connection for sending the response back.
   *
   * @return		the connection
   */
  protected RemoteScriptingEngine getDefaultResponseScriptingEngine() {
    DefaultScriptingEngine	result;

    result = new DefaultScriptingEngine();
    result.setPort(result.getPort() + 1);

    return result;
  }

  /**
   * Sets the scripting engine to use for listening for the results.
   *
   * @param value 	the engine
   */
  public void setScriptingEngine(RemoteScriptingEngine value) {
    m_ResponseScriptingEngine = value;
    reset();
  }

  /**
   * Returns the connection to use for sending the response back.
   *
   * @return		the connection
   */
  public RemoteScriptingEngine getResponseScriptingEngine() {
    return m_ResponseScriptingEngine;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String responseScriptingEngineTipText() {
    return "The scripting engine to use for listening for the incoming results (must match the response connecction!).";
  }

  /**
   * Sets the JobRunner sent back as response.
   *
   * @param value	the job runner
   */
  public void setResponseJobRunner(JobRunner value) {
    m_ResponseJobRunner = value;
  }

  /**
   * Returns the JobRunner sent back as response.
   *
   * @return		the job runner, null if not available
   */
  public JobRunner getResponseJobRunner() {
    return m_ResponseJobRunner;
  }

  /**
   * Performing actual start up.
   * Only gets executed if {@link #preStart()} was successful.
   *
   * @return		null if successful, otherwise error message
   * @see		#preStart()
   */
  @Override
  protected String doStart() {
    String						result;
    adams.scripting.command.distributed.JobRunner 	cmd;

    m_ResponseJobRunner = null;

    // start scripting engine
    new Thread(() -> {
      RemoteCommandGrabber handler = new RemoteCommandGrabber();
      handler.setOwner(m_ResponseScriptingEngine);
      handler.setMaster(this);
      m_ResponseScriptingEngine.setCommandHandler(handler);
      String msg = m_ResponseScriptingEngine.execute();
      if (msg != null)
	getLogger().severe(msg);
    }).start();

    // send command
    cmd = new adams.scripting.command.distributed.JobRunner();
    cmd.setJobRunner(getJobRunner());
    cmd.setRequest(true);
    result = m_RequestConnection.sendRequest(cmd);

    return result;
  }

  /**
   * Performing actual stop.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doStop() {
    return null;
  }

  /**
   * Performing actual terminate up.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doTerminate() {
    return null;
  }

  /**
   * Ignored.
   *
   * @param j	job
   * @param jr	job result
   */
  @Override
  public void complete(Job j, JobResult jr) {
    getLogger().warning("complete(Job,JobResult) - ignored");
  }
}
