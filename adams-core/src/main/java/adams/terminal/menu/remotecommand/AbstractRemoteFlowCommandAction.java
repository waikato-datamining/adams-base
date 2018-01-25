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
 * AbstractRemoteFlowCommand.java
 * Copyright (C) 2017-2018 University of Waikato, Hamilton, NZ
 */

package adams.terminal.menu.remotecommand;

import adams.core.base.BaseHostname;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.scripting.command.RemoteCommand;
import adams.scripting.command.RemoteCommandWithResponse;
import adams.scripting.command.basic.Kill;
import adams.scripting.command.basic.Stop;
import adams.scripting.command.flow.ListFlows;
import adams.scripting.command.flow.SendFlowControlCommand;
import adams.scripting.command.flow.SendFlowControlCommand.Command;
import adams.scripting.engine.DefaultScriptingEngine;
import adams.scripting.responsehandler.ResponseHandler;
import adams.terminal.application.AbstractTerminalApplication;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.BorderLayout;
import com.googlecode.lanterna.gui2.BorderLayout.Location;
import com.googlecode.lanterna.gui2.Borders;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.gui2.table.TableModel;

/**
 * Ancestor for actions that work on remote flows.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractRemoteFlowCommandAction
  extends AbstractRemoteCommandActionWithGUI {

  /**
   * Custom handler for intercepting the responses from a remote command
   * operating on remote flows.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class FlowListResponseHandler
    extends AbstractRemoteCommandActionResponseHandler<AbstractRemoteFlowCommandAction> {

    private static final long serialVersionUID = 6205405220037007365L;

    /**
     * Initializes the handler.
     *
     * @param command	the command this handler belongs to
     */
    public FlowListResponseHandler(AbstractRemoteFlowCommandAction command) {
      super(command);
    }

    /**
     * Handles successful responses.
     *
     * @param cmd		the command with the response
     */
    @Override
    public void responseSuccessful(RemoteCommand cmd) {
      ListFlows			list;
      SpreadSheet 		sheet;
      String[] 			cells;
      int			i;
      TableModel<String> 	model;

      if (cmd instanceof ListFlows) {
	list = (ListFlows) cmd;
	if (list.getResponsePayloadObjects().length > 0) {
	  sheet   = (SpreadSheet) list.getResponsePayloadObjects()[0];
	  cells = new String[sheet.getColumnCount()];
	  for (i = 0; i < sheet.getColumnCount(); i++)
	    cells[i] = sheet.getHeaderRow().getContent(i);
	  model = new TableModel<>(cells);
	  for (Row row: sheet.rows()) {
	    cells = new String[sheet.getColumnCount()];
	    for (i = 0; i < sheet.getColumnCount(); i++)
	      cells[i] = row.getContent(i);
	    model.addRow(cells);
	  }
	  m_Command.getFlowsTable().setTableModel(model);
	}
      }
    }

    /**
     * Handles failed responses.
     *
     * @param cmd		the command with the response
     * @param msg		message, can be null
     */
    @Override
    public void responseFailed(RemoteCommand cmd, String msg) {
      if (cmd instanceof ListFlows) {
	m_Command.getFlowsTable().setTableModel(new TableModel<String>(NO_DATA));
	m_Command.getOwner().logError("Failed to retrieve remote flows:\n" + msg);
      }
    }
  }

  /** the default port to use for refreshing flows. */
  public final static int DEFAULT_PORT = 21345;

  public static final String NO_DATA = "No data";

  /** the panel for the connection/table. */
  protected Panel m_PanelFlows;

  /** the bottom panel for additional content. */
  protected Panel m_PanelBottom;

  /** the remote machine. */
  protected TextBox m_TextRemote;

  /** the local machine. */
  protected TextBox m_TextLocal;

  /** the button for refreshing the flows. */
  protected Button m_ButtonRefresh;

  /** the table with the remote flows. */
  protected Table<String> m_TableFlows;

  /** the button for pausing the flow. */
  protected Button m_ButtonPauseFlow;

  /** the button for resuming the flow. */
  protected Button m_ButtonResumeFlow;

  /** the button for stopping the flow. */
  protected Button m_ButtonStopFlow;

  /** the button for stopping the ADAMS instance. */
  protected Button m_ButtonStopAdams;

  /** the button for killing the ADAMS isntance. */
  protected Button m_ButtonKillAdams;

  /**
   * Initializes the action with no owner.
   */
  public AbstractRemoteFlowCommandAction() {
    super();
  }

  /**
   * Initializes the action.
   *
   * @param owner	the owning application
   */
  public AbstractRemoteFlowCommandAction(AbstractTerminalApplication owner) {
    super(owner);
  }

  /**
   * Creates the panel to display.
   *
   * @return		the panel
   */
  @Override
  protected Panel createPanel() {
    Panel	result;
    Panel 	panelConn;
    Panel	panelButtons;

    result = new Panel(new GridLayout(1));

    m_PanelFlows = new Panel(new BorderLayout());
    result.addComponent(m_PanelFlows.withBorder(Borders.singleLine()));

    panelConn = new Panel(new LinearLayout(Direction.HORIZONTAL));
    m_PanelFlows.addComponent(panelConn.withBorder(Borders.singleLine("Connection")), Location.TOP);

    m_TextRemote = new TextBox(new TerminalSize(15, 1), "127.0.0.1:12345");
    panelConn.addComponent(new Label("Remote"));
    panelConn.addComponent(m_TextRemote);

    m_TextLocal = new TextBox(new TerminalSize(15, 1), "127.0.0.1:" + DEFAULT_PORT);
    panelConn.addComponent(new Label("Local"));
    panelConn.addComponent(m_TextLocal);

    m_ButtonRefresh = new Button("Refresh", () -> refreshFlows());
    panelConn.addComponent(m_ButtonRefresh);

    m_TableFlows = new Table<>(NO_DATA);
    m_PanelFlows.addComponent(m_TableFlows.withBorder(Borders.singleLine("Flows")), Location.CENTER);

    panelButtons = new Panel(new GridLayout(1));
    m_PanelFlows.addComponent(panelButtons.withBorder(Borders.singleLine("Actions")), Location.RIGHT);

    m_ButtonPauseFlow = new Button("Pause", () -> pauseFlow());
    panelButtons.addComponent(m_ButtonPauseFlow);

    m_ButtonResumeFlow = new Button("Resume", () -> resumeFlow());
    panelButtons.addComponent(m_ButtonResumeFlow);

    m_ButtonStopFlow = new Button("Stop", () -> stopFlow());
    panelButtons.addComponent(m_ButtonStopFlow);

    m_ButtonStopAdams = new Button("Stop Adams", () -> stopAdams());
    panelButtons.addComponent(m_ButtonStopAdams);

    m_ButtonKillAdams = new Button("Kill Adams", () -> killAdams());
    panelButtons.addComponent(m_ButtonKillAdams);

    m_PanelBottom = new Panel(new BorderLayout());
    result.addComponent(m_PanelBottom.withBorder(Borders.singleLine()));

    return result;
  }

  /**
   * Returns new instance of a configured scripting engine.
   *
   * @param responseHandler	the handler to use for intercepting the result, can be null
   * @return			the engine
   */
  protected DefaultScriptingEngine configureEngine(ResponseHandler responseHandler) {
    return configureEngine(responseHandler, DEFAULT_PORT);
  }

  /**
   * Sends the specified command, not waiting for a response.
   *
   * @param cmd			the command to send
   */
  public void sendCommand(RemoteCommand cmd) {
    sendCommand(cmd, m_CommandProcessor, new BaseHostname(m_TextRemote.getText()));
  }

  /**
   * Sends the specified command. Uses a default response handler for intercepting
   * the result.
   *
   * @param cmd			the command to send
   */
  public void sendCommandWithReponse(RemoteCommandWithResponse cmd) {
    sendCommandWithReponse(cmd, null);
  }

  /**
   * Sends the specified command and the response handler for intercepting
   * the result.
   *
   * @param cmd			the command to send
   * @param responseHandler 	the response handler for intercepting the result, can be null
   */
  public void sendCommandWithReponse(RemoteCommandWithResponse cmd, ResponseHandler responseHandler) {
    sendCommandWithReponse(cmd, m_CommandProcessor, responseHandler, new BaseHostname(m_TextLocal.getText()), new BaseHostname(m_TextRemote.getText()), DEFAULT_PORT);
  }

  /**
   * Refreshes the list of flows.
   */
  protected void refreshFlows() {
    sendCommandWithReponse(new ListFlows(), new FlowListResponseHandler(this));
  }

  /**
   * Returns an ID array of the currently selected flows.
   *
   * @return		the flow IDs
   */
  protected int[] getSelectedRows() {
    int		sel;

    sel = m_TableFlows.getSelectedRow();
    if (sel >= m_TableFlows.getTableModel().getRowCount())
      return new int[0];
    else
      return new int[]{sel};
  }

  /**
   * Returns an ID array of the currently selected flows.
   *
   * @return		the flow IDs
   */
  protected int[] getSelectedFlowIDs() {
    int[]	result;
    int[]	sel;
    int		i;

    sel    = getSelectedRows();
    result = new int[sel.length];
    for (i = 0; i < sel.length; i++)
      result[i] = Integer.parseInt(m_TableFlows.getTableModel().getRow(i).get(ListFlows.COL_ID));

    return result;
  }

  /**
   * Pauses the selected flow(s).
   */
  protected void pauseFlow() {
    int[]			ids;
    int[]			sel;
    int				i;
    boolean			paused;
    boolean			stopped;
    SendFlowControlCommand cmd;

    ids = getSelectedFlowIDs();
    sel = getSelectedRows();
    for (i = 0; i < sel.length; i++) {
      paused  = Boolean.parseBoolean(m_TableFlows.getTableModel().getRow(i).get(ListFlows.COL_PAUSED));
      stopped = Boolean.parseBoolean(m_TableFlows.getTableModel().getRow(i).get(ListFlows.COL_STOPPED));
      if (!stopped && !paused) {
	cmd = new SendFlowControlCommand();
	cmd.setID(ids[i]);
	cmd.setCommand(Command.PAUSE);
	sendCommandWithReponse(cmd);
      }
    }

    refreshFlows();
  }

  /**
   * Resumes the selected flow(s).
   */
  protected void resumeFlow() {
    int[]			ids;
    int[]			sel;
    int				i;
    boolean			paused;
    boolean			stopped;
    SendFlowControlCommand	cmd;

    ids = getSelectedFlowIDs();
    sel = getSelectedRows();
    for (i = 0; i < sel.length; i++) {
      paused  = Boolean.parseBoolean(m_TableFlows.getTableModel().getRow(i).get(ListFlows.COL_PAUSED));
      stopped = Boolean.parseBoolean(m_TableFlows.getTableModel().getRow(i).get(ListFlows.COL_STOPPED));
      if (!stopped && paused) {
	cmd = new SendFlowControlCommand();
	cmd.setID(ids[i]);
	cmd.setCommand(Command.RESUME);
	sendCommandWithReponse(cmd);
      }
    }

    refreshFlows();
  }

  /**
   * Stops the selected flow(s).
   */
  protected void stopFlow() {
    int[]			ids;
    int[]			sel;
    int				i;
    boolean			stopped;
    SendFlowControlCommand	cmd;

    ids = getSelectedFlowIDs();
    sel = getSelectedRows();
    for (i = 0; i < sel.length; i++) {
      stopped = Boolean.parseBoolean(m_TableFlows.getTableModel().getRow(i).get(ListFlows.COL_STOPPED));
      if (!stopped) {
	cmd = new SendFlowControlCommand();
	cmd.setID(ids[i]);
	cmd.setCommand(Command.STOP);
	sendCommandWithReponse(cmd);
      }
    }

    refreshFlows();
  }

  /**
   * Stops the ADAMS instance.
   */
  protected void stopAdams() {
    sendCommand(new Stop());
  }

  /**
   * Kills the ADAMS instance.
   */
  protected void killAdams() {
    sendCommand(new Kill());
  }

  /**
   * Returns the underlying table for the flows.
   *
   * @return		the table
   */
  public Table getFlowsTable() {
    return m_TableFlows;
  }

  /**
   * Updates the state of the buttons.
   */
  protected void updateButtons() {
    m_ButtonPauseFlow.setEnabled(getSelectedFlowIDs().length > 0);
    m_ButtonResumeFlow.setEnabled(getSelectedFlowIDs().length > 0);
    m_ButtonStopFlow.setEnabled(getSelectedFlowIDs().length > 0);
  }
}
