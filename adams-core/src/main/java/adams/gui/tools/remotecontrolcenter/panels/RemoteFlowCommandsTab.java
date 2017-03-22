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
 * RemoteFlowCommandsTab.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.remotecontrolcenter.panels;

import adams.core.MessageCollection;
import adams.core.Utils;
import adams.core.base.BaseHostname;
import adams.gui.core.GUIHelper;
import adams.gui.core.SimpleLogPanel;
import adams.gui.goe.GenericObjectEditorPanel;
import adams.scripting.command.RemoteCommandOnFlow;
import adams.scripting.command.RemoteCommandWithResponse;
import adams.scripting.command.basic.StopEngine;
import adams.scripting.command.basic.StopEngine.EngineType;
import adams.scripting.command.flow.GetFlow;
import adams.scripting.connection.DefaultConnection;
import adams.scripting.engine.DefaultScriptingEngine;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

/**
 * Tab for executing actions on remote flows.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RemoteFlowCommandsTab
  extends AbstractRemoteFlowTab {

  private static final long serialVersionUID = -1461293385281234642L;

  /** the GOE for the remote flow commands. */
  protected GenericObjectEditorPanel m_GOECommand;

  /** the button for executing the command. */
  protected JButton m_ButtonExecute;

  /** the log for the responses. */
  protected SimpleLogPanel m_Log;

  /** the response logger. */
  protected SimpleLogPanelResponseHandler m_ResponseLogger;

  /**
   * Initializes the members.
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
    JPanel	panelAll;
    JPanel 	panelCmd;
    JPanel	panelButton;

    super.initGUI();

    panelAll = new JPanel(new BorderLayout());
    add(panelAll, BorderLayout.SOUTH);

    panelCmd = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelAll.add(panelCmd, BorderLayout.NORTH);

    m_GOECommand = new GenericObjectEditorPanel(RemoteCommandOnFlow.class, new GetFlow(), true);
    m_GOECommand.setPrefix("Command");
    panelCmd.add(m_GOECommand);

    m_ButtonExecute = new JButton(GUIHelper.getIcon("run.gif"));
    m_ButtonExecute.addActionListener((ActionEvent e) -> executeCommand());
    panelButton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panelButton.add(m_ButtonExecute);
    panelCmd.add(panelButton);

    m_Log = new SimpleLogPanel();
    m_Log.setRows(20);
    panelAll.add(m_Log, BorderLayout.CENTER);
  }

  /**
   * Finishes up the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    m_ResponseLogger.setTab(this);
    m_ResponseLogger.setLog(m_Log);
  }

  /**
   * Returns the title of the tab.
   *
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Remote flows";
  }

  /**
   * Returns the name of icon to use for the tab.
   *
   * @return		the icon
   */
  @Override
  public String getTabIcon() {
    return "flow.gif";
  }

  /**
   * Executes the current command on the specified flow.
   */
  protected void executeCommand() {
    int[]			ids;
    DefaultScriptingEngine	engine;
    RemoteCommandOnFlow		cmd;
    DefaultConnection		conn;
    String			msg;
    MessageCollection 		errors;
    StopEngine 			stop;
    DefaultConnection		connResp;
    BaseHostname 		local;
    BaseHostname		remote;

    local  = m_TextLocal.getObject();
    remote = m_TextRemote.getObject();

    // engine
    engine = new DefaultScriptingEngine();
    engine.setPort(local.portValue());
    engine.setResponseHandler(m_ResponseLogger);
    new Thread(() -> engine.execute()).start();

    ids      = getSelectedFlowIDs();
    conn     = new DefaultConnection();
    conn.setHost(remote.hostnameValue());
    conn.setPort(remote.portValue());
    errors   = new MessageCollection();
    connResp = new DefaultConnection();
    connResp.setPort(local.portValue());
    for (int id: ids) {
      cmd = (RemoteCommandOnFlow) m_GOECommand.getCurrent();
      cmd.setID(id);
      if (cmd instanceof RemoteCommandWithResponse)
	((RemoteCommandWithResponse) cmd).setResponseConnection(connResp);
      msg = conn.sendRequest(cmd);
      if (msg != null)
	errors.add(msg);
    }

    // send stop signal
    stop = new StopEngine();
    stop.setType(EngineType.RESPONSE);
    stop.setResponseConnection(connResp);
    conn.sendRequest(stop);

    if (!errors.isEmpty()) {
      getOwner().logError(
	"Failed to execute command for ID(s): " + Utils.arrayToString(ids) + "!\n"
	  + errors, "Scripting error");
    }
  }
}
