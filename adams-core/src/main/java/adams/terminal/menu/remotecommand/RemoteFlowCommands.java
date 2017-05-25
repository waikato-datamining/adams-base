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
 * RemoteFlowCommands.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.terminal.menu.remotecommand;

import adams.core.MessageCollection;
import adams.core.Utils;
import adams.core.base.BaseHostname;
import adams.core.option.OptionUtils;
import adams.scripting.command.RemoteCommandOnFlow;
import adams.scripting.command.RemoteCommandWithResponse;
import adams.scripting.command.basic.StopEngine;
import adams.scripting.command.basic.StopEngine.EngineType;
import adams.scripting.command.flow.GetFlow;
import adams.scripting.connection.DefaultConnection;
import adams.scripting.engine.DefaultScriptingEngine;
import adams.scripting.responsehandler.LogTextBoxResponseHandler;
import adams.terminal.core.LogTextBox;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.BorderLayout;
import com.googlecode.lanterna.gui2.BorderLayout.Location;
import com.googlecode.lanterna.gui2.Borders;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextBox;

/**
 * For executing actions on remote flows.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RemoteFlowCommands
  extends AbstractRemoteFlowCommandAction {

  /** the commandline of the command to execute. */
  protected TextBox m_GOECommand;

  /** the button for executing the command. */
  protected Button m_ButtonExecute;

  /** the log for the responses. */
  protected LogTextBox m_Log;

  /** the response logger. */
  protected LogTextBoxResponseHandler m_ResponseLogger;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_ResponseLogger = new LogTextBoxResponseHandler();
  }

  /**
   * Returns the title of the action.
   *
   * @return 		the title
   */
  @Override
  public String getTitle() {
    return "Remote flows";
  }

  @Override
  protected Panel createPanel() {
    Panel	result;
    Panel 	panelAll;
    Panel 	panelCmd;
    Panel	panelButton;

    result = super.createPanel();

    panelAll = new Panel(new BorderLayout());
    m_PanelBottom.addComponent(panelAll);

    panelCmd = new Panel(new LinearLayout(Direction.HORIZONTAL));
    panelAll.addComponent(panelCmd, Location.TOP);

    m_GOECommand = new TextBox(new TerminalSize(40, 1), new GetFlow().toCommandLine());
    panelCmd.addComponent(new Label("Command"));
    panelCmd.addComponent(m_GOECommand);

    m_ButtonExecute = new Button("Run");
    m_ButtonExecute.addListener((Button button) -> executeCommand());
    panelButton = new Panel(new LinearLayout(Direction.HORIZONTAL));
    panelButton.addComponent(m_ButtonExecute);
    panelCmd.addComponent(panelButton);

    m_Log = new LogTextBox(new TerminalSize(40, 10));
    panelAll.addComponent(m_Log.withBorder(Borders.singleLine("Log")), Location.CENTER);
    m_ResponseLogger.setLog(m_Log);

    return result;
  }

  /**
   * Executes the current command on the specified flow.
   */
  protected void executeCommand() {
    int[]			ids;
    DefaultScriptingEngine 	engine;
    RemoteCommandOnFlow 	cmd;
    DefaultConnection 		conn;
    String			msg;
    MessageCollection 		errors;
    StopEngine 			stop;
    DefaultConnection		connResp;
    BaseHostname 		local;
    BaseHostname		remote;

    local  = new BaseHostname(m_TextLocal.getText());
    remote = new BaseHostname(m_TextRemote.getText());

    // engine
    engine = configureEngine(m_ResponseLogger);
    new Thread(() -> engine.execute()).start();

    ids      = getSelectedFlowIDs();
    conn     = new DefaultConnection();
    conn.setHost(remote.hostnameValue());
    conn.setPort(remote.portValue());
    errors   = new MessageCollection();
    connResp = new DefaultConnection();
    connResp.setPort(local.portValue());
    for (int id: ids) {
      try {
	cmd = (RemoteCommandOnFlow) OptionUtils.forCommandLine(RemoteCommandOnFlow.class, m_GOECommand.getText());
      }
      catch (Exception e) {
	errors.add("Failed to instantiate command: " + m_GOECommand.getText(), e);
	continue;
      }
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
      logError(
	"Failed to execute command for ID(s): " + Utils.arrayToString(ids) + "!\n" + errors);
    }
  }

  /**
   * Updates the state of the buttons.
   */
  protected void updateButtons() {
    super.updateButtons();
    m_ButtonExecute.setEnabled(getSelectedRows().length > 0);
  }
}
