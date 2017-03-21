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
import adams.gui.core.GUIHelper;
import adams.gui.goe.GenericObjectEditorPanel;
import adams.scripting.command.RemoteCommandOnFlow;
import adams.scripting.command.RemoteCommandWithResponse;
import adams.scripting.command.basic.StopEngine;
import adams.scripting.command.basic.StopEngine.EngineType;
import adams.scripting.command.flow.GetFlow;
import adams.scripting.connection.Connection;
import adams.scripting.connection.DefaultConnection;
import adams.scripting.engine.DefaultScriptingEngine;
import adams.scripting.engine.RemoteScriptingEngine;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
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

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel 	panelCmd;
    JPanel	panelButton;

    super.initGUI();

    panelCmd = new JPanel(new GridLayout(2, 1));
    add(panelCmd, BorderLayout.SOUTH);

    m_GOECommand = new GenericObjectEditorPanel(RemoteCommandOnFlow.class, new GetFlow(), true);
    m_GOECommand.setPrefix("Command");
    m_GOECommand.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    panelCmd.add(m_GOECommand);

    m_ButtonExecute = new JButton(GUIHelper.getIcon("run.gif"));
    m_ButtonExecute.addActionListener((ActionEvent e) -> executeCommand());
    panelButton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panelButton.add(m_ButtonExecute);
    panelCmd.add(panelButton);
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
    RemoteScriptingEngine	engine;
    RemoteCommandOnFlow		cmd;
    Connection			conn;
    String			msg;
    MessageCollection 		errors;
    StopEngine 			stop;
    DefaultConnection		connResp;

    // engine
    engine = (RemoteScriptingEngine) m_GOEEngine.getCurrent();
    new Thread(() -> engine.execute()).start();

    ids      = getSelectedFlowIDs();
    conn     = (Connection) m_GOEConnection.getCurrent();
    errors   = new MessageCollection();
    connResp = new DefaultConnection();
    if (engine instanceof DefaultScriptingEngine)
      connResp.setPort(((DefaultScriptingEngine) engine).getPort());
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
