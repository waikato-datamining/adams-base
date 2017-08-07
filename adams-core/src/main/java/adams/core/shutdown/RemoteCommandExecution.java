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
 * RemoteCommandExecution.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.core.shutdown;

import adams.core.Utils;
import adams.flow.core.RunnableWithLogging;
import adams.scripting.command.RemoteCommand;
import adams.scripting.connection.Connection;
import adams.scripting.connection.DefaultConnection;
import adams.scripting.engine.DefaultScriptingEngine;
import adams.scripting.engine.RemoteScriptingEngine;

/**
 * Executes the specified remote command(s), e.g., for shutting an ADAMS instance.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RemoteCommandExecution
  extends AbstractShutdownHook {

  private static final long serialVersionUID = 7854306477194862533L;

  /** whether to start the local scripting engine. */
  protected boolean m_StartLocalEngine;

  /** the scripting engine to use. */
  protected RemoteScriptingEngine m_Engine;

  /** the connection to use. */
  protected Connection m_Connection;

  /** the commands to execute. */
  protected RemoteCommand[] m_Commands;

  /** the timeout in msec to wait for commands to finish. */
  protected int m_TimeOut;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Executes the specified remote command(s), e.g., for shutting an ADAMS instance.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "start-local-engine", "startLocalEngine",
      false);

    m_OptionManager.add(
      "engine", "engine",
      new DefaultScriptingEngine());

    m_OptionManager.add(
      "connection", "connection",
      new DefaultConnection());

    m_OptionManager.add(
      "command", "commands",
      new RemoteCommand[0]);

    m_OptionManager.add(
      "time-out", "timeOut",
      10000, 1, null);
  }

  /**
   * Sets whether to start a local scripting engine.
   *
   * @param value	true if to start
   */
  public void setStartLocalEngine(boolean value) {
    m_StartLocalEngine = value;
    reset();
  }

  /**
   * Returns whether to start a local scripting engine.
   *
   * @return		true if to start
   */
  public boolean getStartLocalEngine() {
    return m_StartLocalEngine;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String startLocalEngineTipText() {
    return "If enabled, starts the local scripting engine.";
  }

  /**
   * Sets the scripting engine to use.
   *
   * @param value	the engine
   */
  public void setEngine(RemoteScriptingEngine value) {
    m_Engine = value;
    reset();
  }

  /**
   * Returns the scripting engine in use.
   *
   * @return		the engine
   */
  public RemoteScriptingEngine getEngine() {
    return m_Engine;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String engineTipText() {
    return "The remote scripting engine to use for processing, e.g., responses.";
  }

  /**
   * Sets the connection to use.
   *
   * @param value	the connection
   */
  public void setConnection(Connection value) {
    m_Connection = value;
    reset();
  }

  /**
   * Returns the connection in use.
   *
   * @return		the connection
   */
  public Connection getConnection() {
    return m_Connection;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String connectionTipText() {
    return "The connection to use for sending the commands to the remote host.";
  }

  /**
   * Sets the commands to execute.
   *
   * @param value	the commands
   */
  public void setCommands(RemoteCommand[] value) {
    m_Commands = value;
    reset();
  }

  /**
   * Returns the commands to execute.
   *
   * @return		the commands
   */
  public RemoteCommand[] getCommands() {
    return m_Commands;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String commandsTipText() {
    return "The remote commands to execute one after the other.";
  }

  /**
   * Sets the grace period to wait for commands to finish before shutting
   * down.
   *
   * @param value	the timeout in msec
   */
  public void setTimeOut(int value) {
    m_TimeOut = value;
    reset();
  }

  /**
   * Returns the grace period to wait for commands to finish before shutting
   * down.
   *
   * @return		the timeout in msec
   */
  public int getTimeOut() {
    return m_TimeOut;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String timeOutTipText() {
    return "The grace period in msec to wait for commands to finish before shutting down.";
  }

  /**
   * Configures the runnable that gets executed when shutting down.
   *
   * @return		the runnable
   */
  @Override
  public Runnable configure() {
    return new RunnableWithLogging() {
      private static final long serialVersionUID = 605586912132966632L;
      @Override
      protected void doRun() {
        String result = null;

	// start scripting engine
	if (m_StartLocalEngine) {
	  new Thread(() -> m_Engine.execute()).start();
	}

	// send commands
	for (int i = 0; i < m_Commands.length; i++) {
	  if (isLoggingEnabled())
	    getLogger().info("Command #" + (i+1) + ": " + m_Commands[i].toCommandLine());
	  result = m_Connection.sendRequest(m_Commands[i]);
	  if (result != null) {
	    getLogger().severe("Command #" + (i + 1) + " generated following error:\n" + result);
	    break;
	  }
	}

	if (m_StartLocalEngine) {
	  if (result != null) {
	    getLogger().severe("Shutting down scripting engine...");
	    m_Engine.stopExecution();
	  }
	  else {
	    long end = System.currentTimeMillis() + m_TimeOut;
	    while (!m_Engine.isStopped() && (System.currentTimeMillis() < end))
	      Utils.wait(this, 100, 100);
	    if (!m_Engine.isStopped()) {
	      result = "Commands didn't finish within " + m_TimeOut + " msec? Forcing shutdown now...";
	      m_Engine.stopExecution();
	    }
	  }
	}
      }
    };
  }
}
