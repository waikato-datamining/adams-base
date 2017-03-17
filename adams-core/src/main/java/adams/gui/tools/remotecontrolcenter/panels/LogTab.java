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
 * LogTab.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.remotecontrolcenter.panels;

import adams.gui.core.BaseTabbedPane;
import adams.gui.core.SimpleLogPanel;
import adams.gui.event.RemoteScriptingEngineUpdateEvent;
import adams.scripting.requesthandler.AbstractRequestHandler;
import adams.scripting.responsehandler.AbstractResponseHandler;

import java.awt.BorderLayout;

/**
 * For logging.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LogTab
  extends AbstractRemoteControlCenterTab {

  private static final long serialVersionUID = 4437668312078401117L;

  /** the request log. */
  protected SimpleLogPanel m_LogRequest;

  /** the response log. */
  protected SimpleLogPanel m_LogResponse;

  /** the log for other messages. */
  protected SimpleLogPanel m_LogOther;

  /** the tabbed pane. */
  protected BaseTabbedPane m_TabbedPane;

  /** request logger. */
  protected SimpleLogPanelRequestHandler m_RequestLogger;

  /** response logger. */
  protected SimpleLogPanelResponseHandler m_ResponseLogger;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_RequestLogger  = new SimpleLogPanelRequestHandler();
    m_ResponseLogger = new SimpleLogPanelResponseHandler();
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_TabbedPane = new BaseTabbedPane();
    add(m_TabbedPane, BorderLayout.CENTER);

    m_LogRequest = new SimpleLogPanel();
    m_TabbedPane.addTab("Requests", m_LogRequest);

    m_LogResponse = new SimpleLogPanel();
    m_TabbedPane.addTab("Responses", m_LogResponse);

    m_LogOther = new SimpleLogPanel();
    m_TabbedPane.addTab("Other", m_LogOther);
  }

  /**
   * Finishes up the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();

    m_RequestLogger.setLog(getRequestLog());
    m_ResponseLogger.setLog(getResponseLog());
  }

  /**
   * Returns the title of the tab.
   *
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Log";
  }

  /**
   * Returns the name of icon to use for the tab.
   *
   * @return		the icon
   */
  public String getTabIcon() {
    return "log.gif";
  }

  /**
   * Returns the log for requests.
   *
   * @return		the log
   */
  public SimpleLogPanel getRequestLog() {
    return m_LogRequest;
  }

  /**
   * Returns the log for response.
   *
   * @return		the log
   */
  public SimpleLogPanel getResponseLog() {
    return m_LogResponse;
  }

  /**
   * Returns the log for other messages.
   *
   * @return		the log
   */
  public SimpleLogPanel getOtherLog() {
    return m_LogOther;
  }

  /**
   * Gets called in case the remote scripting engine got updated.
   *
   * @param e		the event
   */
  public void remoteScriptingEngineUpdated(RemoteScriptingEngineUpdateEvent e) {
    AbstractRequestHandler.insertHandler(this, getApplicationFrame(), m_RequestLogger);
    AbstractResponseHandler.insertHandler(this, getApplicationFrame(), m_ResponseLogger);
  }
}
