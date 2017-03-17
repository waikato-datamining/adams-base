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
 * SendTab.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.remotecontrolcenter.panels;

import adams.gui.core.GUIHelper;
import adams.gui.core.ParameterPanel;
import adams.gui.core.SimpleLogPanel;
import adams.gui.event.RemoteScriptingEngineUpdateEvent;
import adams.gui.goe.GenericObjectEditorPanel;
import adams.scripting.command.RemoteCommand;
import adams.scripting.command.basic.Ping;
import adams.scripting.connection.Connection;
import adams.scripting.connection.DefaultConnection;
import adams.scripting.responsehandler.AbstractResponseHandler;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

/**
 * Sends a command.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SendCommandTab
  extends AbstractRemoteControlCenterTab {

  private static final long serialVersionUID = -894258879660492792L;

  /** the parameter panel. */
  protected ParameterPanel m_PanelParams;

  /** the panel for the command. */
  protected GenericObjectEditorPanel m_GOECommand;

  /** the panel for the connection. */
  protected GenericObjectEditorPanel m_GOEConnection;

  /** the button for sending the command. */
  protected JButton m_ButtonSend;

  /** the log for the responses. */
  protected SimpleLogPanel m_Log;

  /** the response logger. */
  protected SimpleLogPanelResponseHandler m_ResponseLogger;

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_ResponseLogger = new SimpleLogPanelResponseHandler();
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel	panel;
    JPanel	panelButtons;

    super.initGUI();

    setLayout(new BorderLayout());

    panel = new JPanel(new BorderLayout());
    add(panel, BorderLayout.NORTH);

    m_PanelParams = new ParameterPanel();
    panel.add(m_PanelParams, BorderLayout.CENTER);

    m_GOECommand = new GenericObjectEditorPanel(RemoteCommand.class, new Ping(), true);
    m_PanelParams.addParameter("Command", m_GOECommand);

    m_GOEConnection = new GenericObjectEditorPanel(Connection.class, new DefaultConnection(), true);
    m_PanelParams.addParameter("Connection", m_GOEConnection);

    panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panel.add(panelButtons, BorderLayout.SOUTH);
    m_ButtonSend = new JButton(GUIHelper.getIcon("run.gif"));
    m_ButtonSend.addActionListener((ActionEvent e) -> sendCommand());
    panelButtons.add(m_ButtonSend);

    m_Log = new SimpleLogPanel();
    add(m_Log, BorderLayout.CENTER);
  }

  /**
   * Finishes up the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    m_ResponseLogger.setLog(m_Log);
  }

  /**
   * Returns the title of the tab.
   *
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Send command";
  }

  /**
   * Returns the name of icon to use for the tab.
   *
   * @return		the icon
   */
  @Override
  public String getTabIcon() {
    return "remote_command_execute.png";
  }

  /**
   * Sends the command.
   */
  protected void sendCommand() {
    RemoteCommand	cmd;
    Connection		conn;
    String		msg;

    conn = (Connection) m_GOEConnection.getCurrent();
    cmd  = (RemoteCommand) m_GOECommand.getCurrent();

    msg = conn.sendRequest(cmd);
    if (msg != null)
      getOwner().logError(msg, "Failed to send command");
  }

  /**
   * Gets called in case the remote scripting engine got updated.
   *
   * @param e		the event
   */
  public void remoteScriptingEngineUpdated(RemoteScriptingEngineUpdateEvent e) {
    AbstractResponseHandler.insertHandler(this, getApplicationFrame(), m_ResponseLogger);
  }
}
