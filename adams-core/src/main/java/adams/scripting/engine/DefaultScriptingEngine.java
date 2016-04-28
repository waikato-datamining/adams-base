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
 * DefaultScriptingEngine.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.scripting.engine;

import adams.core.MessageCollection;
import adams.core.Utils;
import adams.core.net.PortManager;
import adams.multiprocess.PausableFixedThreadPoolExecutor;
import adams.scripting.command.CommandUtils;
import adams.scripting.command.RemoteCommand;
import gnu.trove.list.array.TByteArrayList;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.Level;

/**
 * Default implementation of scripting engine for remote commands.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DefaultScriptingEngine
  extends AbstractScriptingEngineWithJobQueue {

  private static final long serialVersionUID = -3763240773922918567L;

  /** for accepting connections. */
  protected transient ServerSocket m_Server;

  /** the timeout for the socket. */
  protected int m_Timeout;

  /** the port to listen on. */
  protected int m_Port;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Default implementation of scripting engine for remote commands.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "port", "port",
      12345, 1, 65535);

    m_OptionManager.add(
      "timeout", "timeout",
      3000, 100, null);
  }

  /**
   * Sets the port to listen on.
   *
   * @param value	the port to listen on
   */
  public void setPort(int value) {
    if (getOptionManager().isValid("port", value)) {
      m_Port = value;
      reset();
    }
  }

  /**
   * Returns the port to listen on.
   *
   * @return		the port listening on
   */
  public int getPort() {
    return m_Port;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String portTipText() {
    return "The port to listen on for remote connections.";
  }

  /**
   * Sets the timeout in milli-second to wait for new connections.
   *
   * @param value	the timeout in msec
   */
  public void setTimeout(int value) {
    if (getOptionManager().isValid("timeout", value)) {
      m_Timeout = value;
      reset();
    }
  }

  /**
   * Returns the timeout in milli-second to wait for new connections.
   *
   * @return		the timeout in msec
   */
  public int getTimeout() {
    return m_Timeout;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String timeoutTipText() {
    return "The timeout in milli-second for waiting on new client connections.";
  }

  /**
   * Closes the server socket if necessary.
   */
  protected void closeSocket() {
    if (m_Server != null) {
      PortManager.getSingleton().release(m_Port);
      try {
	m_Server.close();
      }
      catch (Exception e) {
	Utils.handleException(this, "Failed to close server socket!", e);
      }
      m_Server = null;
    }
  }

  /**
   * Handles the client connection.
   *
   * @param client	the connection to handle
   */
  protected void handleClient(Socket client) {
    InputStream		in;
    int			b;
    TByteArrayList 	bytes;
    String		data;
    RemoteCommand	cmd;
    MessageCollection	errors;
    String		msg;

    // read data
    bytes = new TByteArrayList();
    try {
      in = client.getInputStream();
      while ((b = in.read()) != -1)
      	bytes.add((byte) b);
      client.close();
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to process client connection!", e);
      return;
    }
    if (bytes.isEmpty()) {
      getLogger().warning("No data received, ignoring connection!");
      return;
    }

    // instantiate command
    data   = new String(bytes.toArray());
    errors = new MessageCollection();
    cmd    = CommandUtils.parse(data, errors);

    if (cmd != null) {
      // permitted?
      if (!m_PermissionHandler.permitted(cmd)) {
	m_RequestHandler.requestRejected(cmd, "Not permitted!");
	return;
      }

      // handle command
      msg = m_CommandHandler.handle(cmd);
      if (msg != null)
	getLogger().severe("Failed to handle command:\n" + msg);
    }
    else {
      if (!errors.isEmpty())
	getLogger().severe("Failed to parse command:\n" + errors.toString());
      else
	getLogger().severe("Failed to parse command:\n" + data);
    }
  }

  /**
   * Executes the scripting engine.
   *
   * @return		error message in case of failure to start up or run,
   * 			otherwise null
   */
  @Override
  public String execute() {
    String		result;
    Socket		client;

    result    = null;
    m_Paused  = false;
    m_Stopped = false;

    // connect to port
    try {
      m_Server = new ServerSocket(m_Port);
      m_Server.setSoTimeout(m_Timeout);
      PortManager.getSingleton().bind(this, m_Port);
    }
    catch (Exception e) {
      result   = Utils.handleException(this, "Failed to set up server socket!", e);
      m_Server = null;
    }

    // wait for connections
    if (m_Server != null) {
      // start up job queue
      m_Executor = new PausableFixedThreadPoolExecutor(m_MaxConcurrentJobs);

      while (!m_Stopped) {
	while (m_Paused && !m_Stopped) {
	  Utils.wait(this, this, 1000, 50);
	}

	try {
	  client = m_Server.accept();
	  if (client != null) {
	    handleClient(client);
	  }
	}
	catch (SocketTimeoutException t) {
	  // ignored
	}
	catch (Exception e) {
          if ((m_Server != null) && !m_Server.isClosed())
            Utils.handleException(this, "Failed to accept connection!", e);
	}
      }
    }

    closeSocket();

    if (!m_Executor.isTerminated()) {
      getLogger().info("Shutting down job queue...");
      m_Executor.shutdown();
      while (!m_Executor.isTerminated())
	Utils.wait(this, 1000, 100);
      getLogger().info("Job queue shut down");
    }

    return result;
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    super.stopExecution();
    closeSocket();
  }

  /**
   * Starts the scripting engine from commandline.
   *
   * @param args  	additional options for the scripting engine
   */
  public static void main(String[] args) {
    runScriptingEngine(DefaultScriptingEngine.class, args);
  }
}
