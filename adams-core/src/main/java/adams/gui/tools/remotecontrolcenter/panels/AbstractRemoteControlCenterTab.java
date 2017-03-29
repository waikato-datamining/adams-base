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
 * AbstractRemoteControlCenterTab.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.remotecontrolcenter.panels;

import adams.core.base.BaseHostname;
import adams.core.net.PortManager;
import adams.gui.application.AbstractApplicationFrame;
import adams.gui.core.BasePanel;
import adams.gui.event.RemoteScriptingEngineUpdateEvent;
import adams.gui.event.RemoteScriptingEngineUpdateListener;
import adams.gui.tools.remotecontrolcenter.RemoteControlCenterLogPanel;
import adams.gui.tools.remotecontrolcenter.RemoteControlCenterPanel;
import adams.scripting.command.RemoteCommandWithResponse;
import adams.scripting.command.basic.StopEngine;
import adams.scripting.command.basic.StopEngine.EngineType;
import adams.scripting.connection.DefaultConnection;
import adams.scripting.engine.DefaultScriptingEngine;
import adams.scripting.requesthandler.RequestHandler;
import adams.scripting.requesthandler.SimpleLogPanelRequestHandler;
import adams.scripting.responsehandler.ResponseHandler;
import adams.scripting.responsehandler.SimpleLogPanelResponseHandler;

/**
 * Ancestor for tabs to be shown in the Remote Control Center.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractRemoteControlCenterTab
  extends BasePanel
  implements RemoteScriptingEngineUpdateListener {

  private static final long serialVersionUID = 5896827287386114875L;

  /** the owning control center panel. */
  protected RemoteControlCenterPanel m_Owner;

  /**
   * Sets the owner.
   *
   * @param value	the owner
   */
  public void setOwner(RemoteControlCenterPanel value) {
    m_Owner = value;
  }

  /**
   * Returns the owner.
   *
   * @return		the owner
   */
  public RemoteControlCenterPanel getOwner() {
    return m_Owner;
  }

  /**
   * Returns the application frame this tab belongs to.
   *
   * @return		the frame, null if not part of an app frame
   */
  public AbstractApplicationFrame getApplicationFrame() {
    if (getOwner() != null)
      return getOwner().getApplicationFrame();
    return null;
  }

  /**
   * Returns the log panel.
   *
   * @return		the panel, null if no owner set
   */
  public RemoteControlCenterLogPanel getLogPanel() {
    if (getOwner() != null)
      return getOwner().getLogPanel();
    return null;
  }

  /**
   * Returns the title of the tab.
   *
   * @return		the title
   */
  public abstract String getTitle();

  /**
   * Returns the name of icon to use for the tab.
   *
   * @return		the icon
   */
  public abstract String getTabIcon();

  /**
   * Gets called in case the remote scripting engine got updated.
   *
   * @param e		the event
   */
  public void remoteScriptingEngineUpdated(RemoteScriptingEngineUpdateEvent e) {
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
    SimpleLogPanelRequestHandler simpleRequest;
    SimpleLogPanelResponseHandler simpleResponse;

    result = new DefaultScriptingEngine();
    result.setPort(PortManager.getSingleton().next(result.getClass(), defPort));

    // request
    simpleRequest = new SimpleLogPanelRequestHandler();
    simpleRequest.setLog(getLogPanel().getRequestLog());
    multiRequest = new adams.scripting.requesthandler.MultiHandler();
    multiRequest.setHandlers(new RequestHandler[]{
      new adams.scripting.requesthandler.LoggingHandler(),
      simpleRequest,
    });
    result.setRequestHandler(multiRequest);

    // response
    simpleResponse = new SimpleLogPanelResponseHandler();
    simpleResponse.setLog(getLogPanel().getRequestLog());
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
   * Sends the specified command and the response handler for intercepting
   * the result.
   *
   * @param cmd			the command to send
   * @param responseHandler 	the response handler for intercepting the result, can be null
   * @param local 		the local host
   * @param remote 		the remote host
   * @param defPort		the default port to use
   */
  public void sendCommand(RemoteCommandWithResponse cmd, ResponseHandler responseHandler, BaseHostname local, BaseHostname remote, int defPort) {
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
    msg  = conn.sendRequest(cmd);
    if (msg != null) {
      engine.stopExecution();
      getOwner().logError("Failed to send command '" + cmd.toCommandLine() + "':\n" + msg, "Scripting error");
    }
    else {
      // send stop signal
      stop = new StopEngine();
      stop.setType(EngineType.RESPONSE);
      stop.setResponseConnection(connResp);
      conn.sendRequest(stop);
    }
  }
}
