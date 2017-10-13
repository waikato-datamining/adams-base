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
 * Logging.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.console;

import adams.core.StoppableWithFeedback;
import adams.core.Utils;
import adams.core.base.BaseHostname;
import adams.core.logging.LoggingHelper;
import adams.core.logging.RemoteReceiveHandler.AbstractRemoteListenerRunnable;
import adams.core.net.PortManager;
import adams.env.Environment;
import adams.scripting.command.RemoteCommandWithResponse;
import adams.scripting.command.basic.StartRemoteLogging;
import adams.scripting.command.basic.StopEngine;
import adams.scripting.command.basic.StopEngine.EngineType;
import adams.scripting.connection.DefaultConnection;
import adams.scripting.engine.DefaultScriptingEngine;
import adams.scripting.processor.DefaultRemoteCommandProcessor;
import adams.scripting.processor.RemoteCommandProcessor;
import adams.scripting.processor.RemoteCommandProcessorHandler;

import java.util.logging.LogRecord;

/**
 * Allows to capture the logging of an ADAMS instance.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Logging
  extends AbstractConsoleApplication
  implements StoppableWithFeedback, RemoteCommandProcessorHandler {

  private static final long serialVersionUID = 1373827735768376022L;

  /**
   * Runnable that outputs the log records to stdout.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class RemoteListenerRunnableWithLog
    extends AbstractRemoteListenerRunnable {

    private static final long serialVersionUID = -1375651567275850732L;

    /**
     * Initializes the runnable.
     *
     * @param port    the port to listen on
     * @param timeout the timeout
     */
    protected RemoteListenerRunnableWithLog(int port, int timeout) {
      super(port, timeout);
    }

    /**
     * Publishes the record.
     *
     * @param record	the record
     */
    @Override
    protected void publish(LogRecord record) {
      if (record != null)
	System.out.println(LoggingHelper.assembleMessage(record));
    }
  }

  /** the default port to use for receiving logging messages. */
  public final static int DEFAULT_PORT = 31345;

  /** the remote host/port to connect to. */
  protected BaseHostname m_RemoteHost;

  /** the local host/port to use. */
  protected BaseHostname m_LocalHost;

  /** the command processor. */
  protected RemoteCommandProcessor m_CommandProcessor;

  /** the maximum number of connection failures to handle (-1 for indefinite). */
  protected int m_MaxFailures;

  /** whether listening was stopped. */
  protected boolean m_Stopped;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Allows to capture the logging of an ADAMS instance.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "command-processor", "commandProcessor",
      new DefaultRemoteCommandProcessor());

    m_OptionManager.add(
      "remote-host", "remoteHost",
      new BaseHostname("127.0.0.1:" + DefaultScriptingEngine.DEFAULT_PORT));

    m_OptionManager.add(
      "local-host", "localHost",
      new BaseHostname("127.0.0.1:" + (DefaultScriptingEngine.DEFAULT_PORT + 1)));

    m_OptionManager.add(
      "max-failures", "maxFailures",
      5, -1, null);
  }

  /**
   * Sets the command processor to use.
   *
   * @param value	the processor
   */
  public void setCommandProcessor(RemoteCommandProcessor value) {
    m_CommandProcessor = value;
    reset();
  }

  /**
   * Returns the command processor in use.
   *
   * @return		the processor
   */
  public RemoteCommandProcessor getCommandProcessor() {
    return m_CommandProcessor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String commandProcessorTipText() {
    return "The processor for formatting/parsing the commands.";
  }

  /**
   * Sets the host to capture the logging from.
   *
   * @param value	the host/port
   */
  public void setRemoteHost(BaseHostname value) {
    m_RemoteHost = value;
    reset();
  }

  /**
   * Returns the host to capture the logging from.
   *
   * @return		the host/port
   */
  public BaseHostname getRemoteHost() {
    return m_RemoteHost;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String remoteHostTipText() {
    return "The host/port to capture the logging from.";
  }

  /**
   * Sets the local host/port to send the log records to.
   *
   * @param value	the local host/port
   */
  public void setLocalHost(BaseHostname value) {
    m_LocalHost = value;
    reset();
  }

  /**
   * Returns the local host/port to send the log records to.
   *
   * @return		the local host/port
   */
  public BaseHostname getLocalHost() {
    return m_LocalHost;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String localHostTipText() {
    return "The local host/port to send the log records to.";
  }

  /**
   * Sets the maximum number of conection failures to tolerate.
   *
   * @param value	the maximum, -1 for indefinite
   */
  public void setMaxFailures(int value) {
    m_MaxFailures = value;
    reset();
  }

  /**
   * Returns the maximum number of connection failures to tolerate.
   *
   * @return		the maximum, -1 for indefinite
   */
  public int getMaxFailures() {
    return m_MaxFailures;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxFaiuresTipText() {
    return "The maximum number of connection failures to tolerate.";
  }

  /**
   * Returns new instance of a configured scripting engine.
   *
   * @param defPort		the default port to use
   * @return			the engine
   */
  protected DefaultScriptingEngine configureEngine(int defPort) {
    DefaultScriptingEngine 				result;

    result = new DefaultScriptingEngine();
    result.setPort(PortManager.getSingleton().next(result.getClass(), defPort));
    result.setRequestHandler(new adams.scripting.requesthandler.LoggingHandler());
    result.setResponseHandler(new adams.scripting.responsehandler.LoggingHandler());

    return result;
  }

  /**
   * Sends the specified command and the response handler for intercepting
   * the result.
   *
   * @param cmd			the command to send
   * @param local 		the local host
   * @param remote 		the remote host
   * @param defPort		the default port to use
   */
  public String sendCommandWithReponse(RemoteCommandWithResponse cmd, BaseHostname local, BaseHostname remote, int defPort) {
    String 			result;
    StopEngine 			stop;
    DefaultConnection 		conn;
    DefaultScriptingEngine	engine;
    DefaultConnection		connResp;

    result = null;

    // engine
    engine = configureEngine(defPort);
    new Thread(() -> engine.execute()).start();

    // command
    connResp = new DefaultConnection();
    connResp.setHost(local.hostnameValue());
    connResp.setPort(engine.getPort());
    cmd.setResponseConnection(connResp);

    // send command
    conn = new DefaultConnection();
    conn.setHost(remote.hostnameValue());
    conn.setPort(remote.portValue());
    result = conn.sendRequest(cmd, m_CommandProcessor);
    if (result != null) {
      engine.stopExecution();
      getLogger().severe("Failed to send command '" + cmd.toCommandLine() + "':\n" + result);
    }
    else {
      // send stop signal
      stop = new StopEngine();
      stop.setType(EngineType.RESPONSE);
      stop.setResponseConnection(connResp);
      conn.sendRequest(stop, m_CommandProcessor);
    }

    return result;
  }

  /**
   * Executes the application.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String execute() {
    String				result;
    StartRemoteLogging 			start;
    RemoteListenerRunnableWithLog	run;

    m_Stopped = false;

    run = new RemoteListenerRunnableWithLog(m_LocalHost.portValue(), RemoteListenerRunnableWithLog.DEFAULT_TIMEOUT);
    new Thread(run).start();
    start = new StartRemoteLogging();
    start.setInstallListener(false);
    start.setLoggingHost(m_LocalHost);
    start.setMaxFailures(m_MaxFailures);
    result = sendCommandWithReponse(start, m_LocalHost, m_RemoteHost, DEFAULT_PORT);
    m_Stopped = (result != null);

    while (!m_Stopped)
      Utils.wait(this, 1000, 100);

    return result;
  }

  /**
   * Whether the execution has been stopped.
   *
   * @return		true if stopped
   */
  @Override
  public boolean isStopped() {
    return m_Stopped;
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    m_Stopped = true;
  }

  /**
   * Starts the terminal application from commandline.
   *
   * @param args	the arguments
   * @throws Exception	if start up fails
   */
  public static void main(String[] args) throws Exception {
    runApplication(Environment.class, Logging.class, args);
    System.exit(0);
  }
}
