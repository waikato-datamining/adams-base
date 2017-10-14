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
 * AbstractRemoteCommandAction.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.terminal.menu.remotecommand;

import adams.core.base.BaseHostname;
import adams.core.net.PortManager;
import adams.scripting.ScriptingHelper;
import adams.scripting.command.RemoteCommand;
import adams.scripting.command.RemoteCommandWithResponse;
import adams.scripting.command.basic.StopEngine;
import adams.scripting.command.basic.StopEngine.EngineType;
import adams.scripting.connection.DefaultConnection;
import adams.scripting.engine.DefaultScriptingEngine;
import adams.scripting.processor.RemoteCommandProcessor;
import adams.scripting.processor.RemoteCommandProcessorHandler;
import adams.scripting.requesthandler.LogTextBoxRequestHandler;
import adams.scripting.requesthandler.RequestHandler;
import adams.scripting.responsehandler.LogTextBoxResponseHandler;
import adams.scripting.responsehandler.ResponseHandler;
import adams.terminal.application.AbstractTerminalApplication;
import adams.terminal.core.LogTextBox;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;

/**
 * Ancestor for remote command actions.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractRemoteCommandAction
  implements Comparable<AbstractRemoteCommandAction>, RemoteCommandProcessorHandler {

  /** the owning application. */
  protected AbstractTerminalApplication m_Owner;

  /** the command processor. */
  protected RemoteCommandProcessor m_CommandProcessor;

  /**
   * Initializes the action with no owner.
   */
  public AbstractRemoteCommandAction() {
    this(null);
  }

  /**
   * Initializes the action.
   *
   * @param owner	the owning application
   */
  public AbstractRemoteCommandAction(AbstractTerminalApplication owner) {
    super();

    m_Owner = owner;

    initialize();
  }

  /**
   * Initializes members.
   */
  protected void initialize() {
    m_CommandProcessor = ScriptingHelper.getSingleton().getDefaultProcessor();
  }

  /**
   * Sets the owning application.
   *
   * @param value	the owner
   */
  public void setOwner(AbstractTerminalApplication value) {
    m_Owner = value;
  }

  /**
   * Returns the owning application.
   *
   * @return		the owner
   */
  public AbstractTerminalApplication getOwner() {
    return m_Owner;
  }

  /**
   * Sets the command processor to use.
   *
   * @param value	the processor
   */
  public void setCommandProcessor(RemoteCommandProcessor value) {
    m_CommandProcessor = value;
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
   * Returns the title of the action.
   *
   * @return 		the title
   */
  public abstract String getTitle();

  /**
   * Returns the LogTextBox to use.
   *
   * @return		the log to use
   */
  protected LogTextBox getLogTextBox() {
    return m_Owner.getLogTextBox();
  }

  /**
   * Logs the message.
   *
   * @param msg		the message to log
   */
  public void logMessage(String msg) {
    m_Owner.logMessage(msg);
  }

  /**
   * Logs the error.
   *
   * @param msg		the error message to log
   */
  public void logError(String msg) {
    m_Owner.logError(msg);
  }

  /**
   * Logs the error.
   *
   * @param msg		the error message to log
   * @param t 		the exception
   */
  public void logError(String msg, Throwable t) {
    m_Owner.logError(msg, t);
  }

  /**
   * Returns new instance of a configured scripting engine.
   *
   * @param responseHandler	the handler to use for intercepting the result, can be null
   * @param defPort		the default port to use
   * @return			the engine
   */
  protected DefaultScriptingEngine configureEngine(ResponseHandler responseHandler, int defPort) {
    DefaultScriptingEngine 				result;
    adams.scripting.requesthandler.MultiHandler		multiRequest;
    adams.scripting.responsehandler.MultiHandler	multiResponse;
    LogTextBoxRequestHandler 				simpleRequest;
    LogTextBoxResponseHandler 				simpleResponse;

    result = new DefaultScriptingEngine();
    result.setPort(PortManager.getSingleton().next(result.getClass(), defPort));

    // request
    simpleRequest = new LogTextBoxRequestHandler();
    simpleRequest.setLog(getLogTextBox());
    multiRequest = new adams.scripting.requesthandler.MultiHandler();
    multiRequest.setHandlers(new RequestHandler[]{
      new adams.scripting.requesthandler.LoggingHandler(),
      simpleRequest,
    });
    result.setRequestHandler(multiRequest);

    // response
    simpleResponse = new LogTextBoxResponseHandler();
    simpleResponse.setLog(getLogTextBox());
    multiResponse = new adams.scripting.responsehandler.MultiHandler();
    multiResponse.setHandlers(new ResponseHandler[]{
      new adams.scripting.responsehandler.LoggingHandler(),
      simpleResponse,
    });
    if (responseHandler != null)
      multiResponse.addHandler(responseHandler);
    result.setResponseHandler(multiResponse);

    return result;
  }

  /**
   * Sends the specified command, not waiting for a response.
   *
   * @param cmd			the command to send
   * @param processor 		the processor for formatting/parsing
   * @param remote 		the remote host
   */
  public void sendCommand(RemoteCommand cmd, RemoteCommandProcessor processor, BaseHostname remote) {
    DefaultConnection 	conn;
    String		msg;

    // send command
    conn = new DefaultConnection();
    conn.setHost(remote.hostnameValue());
    conn.setPort(remote.portValue());
    msg = conn.sendRequest(cmd, processor);
    if (msg != null)
      logError("Failed to send command '" + cmd.toCommandLine() + "':\n" + msg);
  }

  /**
   * Sends the specified command and the response handler for intercepting
   * the result.
   *
   * @param cmd			the command to send
   * @param processor 		the processor for formatting/parsing
   * @param responseHandler 	the response handler for intercepting the result, can be null
   * @param local 		the local host
   * @param remote 		the remote host
   * @param defPort		the default port to use
   */
  public void sendCommandWithReponse(RemoteCommandWithResponse cmd, RemoteCommandProcessor processor, ResponseHandler responseHandler, BaseHostname local, BaseHostname remote, int defPort) {
    StopEngine stop;
    DefaultConnection conn;
    DefaultScriptingEngine	engine;
    DefaultConnection		connResp;
    String			msg;

    // engine
    engine = configureEngine(responseHandler, defPort);
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
    msg  = conn.sendRequest(cmd, processor);
    if (msg != null) {
      engine.stopExecution();
      logError("Failed to send command '" + cmd.toCommandLine() + "':\n" + msg);
    }
    else {
      // send stop signal
      stop = new StopEngine();
      stop.setType(EngineType.RESPONSE);
      stop.setResponseConnection(connResp);
      conn.sendRequest(stop, processor);
    }
  }

  /**
   * Prepares before the execution.
   *
   * @param context	the context to use
   */
  protected void preRun(WindowBasedTextGUI context) {
  }

  /**
   * Actual execution.
   *
   * @param context	the context to use
   */
  protected abstract void doRun(WindowBasedTextGUI context);

  /**
   * Cleans up after the execution.
   *
   * @param context	the context to use
   */
  protected void postRun(WindowBasedTextGUI context) {
  }

  /**
   * Returns the Runnable to use.
   *
   * @param context	the context to use
   * @return		the runnable
   */
  public Runnable getRunnable(final WindowBasedTextGUI context) {
    return () -> {
      preRun(context);
      doRun(context);
      postRun(context);
    };
  }

  /**
   * Uses category and title for sorting.
   *
   * @param o		the other definition to compare with
   * @return		less than zero, zero, or greater than zero if this
   * 			menuitem is less than, equal to or greater than the
   * 			other definition
   * @see		#getTitle()
   */
  @Override
  public int compareTo(AbstractRemoteCommandAction o) {
    return getTitle().compareTo(o.getTitle());
  }

  /**
   * Checks whether the obj is the same definition (using category/title).
   *
   * @param obj		the object to compare with
   * @return		true if the same definition
   * @see		#compareTo(AbstractRemoteCommandAction)
   */
  @Override
  public boolean equals(Object obj) {
    return (obj instanceof AbstractRemoteCommandAction)
      && (compareTo((AbstractRemoteCommandAction) obj) == 0);
  }
}
