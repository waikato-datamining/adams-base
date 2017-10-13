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
 * AbstractRemoteFlowTab.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.remotecontrolcenter.panels;

import adams.core.base.BaseHostname;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.core.BaseObjectTextField;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.BaseTable;
import adams.gui.core.BaseTableWithButtons;
import adams.gui.core.GUIHelper;
import adams.gui.core.SpreadSheetTableModel;
import adams.scripting.command.RemoteCommand;
import adams.scripting.command.RemoteCommandWithResponse;
import adams.scripting.command.basic.Kill;
import adams.scripting.command.basic.Stop;
import adams.scripting.command.flow.ListFlows;
import adams.scripting.command.flow.SendFlowControlCommand;
import adams.scripting.command.flow.SendFlowControlCommand.Command;
import adams.scripting.connection.DefaultConnection;
import adams.scripting.engine.DefaultScriptingEngine;
import adams.scripting.processor.DefaultRemoteCommandProcessor;
import adams.scripting.processor.RemoteCommandProcessor;
import adams.scripting.processor.RemoteCommandProcessorHandler;
import adams.scripting.responsehandler.ResponseHandler;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

/**
 * Ancestor for tabs that get applied to remote flows using their ID(s).
 * For simplicity, only uses {@link DefaultConnection} which communicates
 * via sockets.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractRemoteFlowTab
  extends AbstractRemoteControlCenterTab
  implements ListSelectionListener, RemoteCommandProcessorHandler {

  private static final long serialVersionUID = 321058606982723480L;

  /**
   * Custom handler for intercepting the responses from the {@link ListFlows}
   * remote command.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class FlowListResponseHandler
    extends AbstractTabResponseHandler<AbstractRemoteFlowTab> {

    private static final long serialVersionUID = 6205405220037007365L;

    /**
     * Initializes the handler.
     *
     * @param tab	the tab this handler belongs to
     */
    public FlowListResponseHandler(AbstractRemoteFlowTab tab) {
      super(tab);
    }

    /**
     * Handles successful responses.
     *
     * @param cmd		the command with the response
     */
    @Override
    public void responseSuccessful(RemoteCommand cmd) {
      ListFlows			list;
      SpreadSheetTableModel	model;

      if (cmd instanceof ListFlows) {
	list = (ListFlows) cmd;
	if (list.getResponsePayloadObjects().length > 0) {
	  model = new SpreadSheetTableModel((SpreadSheet) list.getResponsePayloadObjects()[0]);
	  model.setShowRowColumn(false);
	  model.setUseSimpleHeader(true);
	  SwingUtilities.invokeLater(() -> {
	    m_Tab.getFlowsTable().setModel(model);
	    m_Tab.getFlowsTable().setOptimalColumnWidth();
	  });
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
	m_Tab.getFlowsTable().setModel(new SpreadSheetTableModel());
	m_Tab.getOwner().logError("Failed to retrieve remote flows:\n" + msg, "List remote flows");
      }
    }
  }

  /** the default port to use for refreshing flows. */
  public final static int DEFAULT_PORT = 21345;

  /** the split pane. */
  protected BaseSplitPane m_SplitPane;

  /** the panel for the connection/table. */
  protected JPanel m_PanelFlows;

  /** the remote machine. */
  protected BaseObjectTextField<BaseHostname> m_TextRemote;

  /** the local machine. */
  protected BaseObjectTextField<BaseHostname> m_TextLocal;

  /** the button for refreshing the flows. */
  protected JButton m_ButtonRefresh;

  /** the table with the remote flows. */
  protected BaseTableWithButtons m_TableFlows;

  /** the button for pausing the flow. */
  protected JButton m_ButtonPauseFlow;

  /** the button for resuming the flow. */
  protected JButton m_ButtonResumeFlow;

  /** the button for stopping the flow. */
  protected JButton m_ButtonStopFlow;

  /** the button for stopping the ADAMS instance. */
  protected JButton m_ButtonStopAdams;

  /** the button for killing the ADAMS isntance. */
  protected JButton m_ButtonKillAdams;

  /** the command processor. */
  protected RemoteCommandProcessor m_CommandProcessor;

  /**
   * For initializing members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_CommandProcessor = new DefaultRemoteCommandProcessor();
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel			panelConn;
    JPanel			panelButton;
    JLabel 			label;
    SpreadSheetTableModel	model;

    super.initGUI();

    setLayout(new BorderLayout());

    m_SplitPane = new BaseSplitPane(BaseSplitPane.VERTICAL_SPLIT);
    m_SplitPane.setDividerLocation(200);
    m_SplitPane.setResizeWeight(0.5);
    add(m_SplitPane, BorderLayout.CENTER);

    m_PanelFlows = new JPanel(new BorderLayout());
    m_SplitPane.setTopComponent(m_PanelFlows);

    panelConn = new JPanel(new FlowLayout(FlowLayout.LEFT));
    m_PanelFlows.add(panelConn, BorderLayout.NORTH);
    
    m_TextRemote = new BaseObjectTextField<>(new BaseHostname(), "127.0.0.1:12345");
    m_TextRemote.setColumns(20);
    label = new JLabel("Remote");
    label.setDisplayedMnemonic('R');
    label.setLabelFor(m_TextRemote);
    panelConn.add(label);
    panelConn.add(m_TextRemote);
    
    m_TextLocal = new BaseObjectTextField<>(new BaseHostname(), "127.0.0.1:" + DEFAULT_PORT);
    m_TextLocal.setColumns(20);
    label = new JLabel("Local");
    label.setDisplayedMnemonic('L');
    label.setLabelFor(m_TextLocal);
    panelConn.add(label);
    panelConn.add(m_TextLocal);

    m_ButtonRefresh = new JButton(GUIHelper.getIcon("refresh.gif"));
    m_ButtonRefresh.addActionListener((ActionEvent e) -> refreshFlows());
    panelButton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panelButton.add(m_ButtonRefresh);
    panelConn.add(panelButton);

    model = new SpreadSheetTableModel();
    model.setShowRowColumn(false);
    model.setUseSimpleHeader(true);
    m_TableFlows = new BaseTableWithButtons(model);
    m_TableFlows.getSelectionModel().addListSelectionListener(this);
    m_TableFlows.setAutoResizeMode(BaseTable.AUTO_RESIZE_OFF);
    m_PanelFlows.add(m_TableFlows, BorderLayout.CENTER);

    m_ButtonPauseFlow = new JButton(GUIHelper.getIcon("pause.gif"));
    m_ButtonPauseFlow.addActionListener((ActionEvent e) -> pauseFlow());
    m_TableFlows.addToButtonsPanel(m_ButtonPauseFlow);

    m_ButtonResumeFlow = new JButton(GUIHelper.getIcon("resume.gif"));
    m_ButtonResumeFlow.addActionListener((ActionEvent e) -> resumeFlow());
    m_TableFlows.addToButtonsPanel(m_ButtonResumeFlow);

    m_ButtonStopFlow = new JButton(GUIHelper.getIcon("stop_blue.gif"));
    m_ButtonStopFlow.addActionListener((ActionEvent e) -> stopFlow());
    m_TableFlows.addToButtonsPanel(m_ButtonStopFlow);

    m_ButtonStopAdams = new JButton(GUIHelper.getIcon("exit.png"));
    m_ButtonStopAdams.addActionListener((ActionEvent e) -> stopAdams());
    m_TableFlows.addToButtonsPanel(m_ButtonStopAdams);

    m_ButtonKillAdams = new JButton(GUIHelper.getIcon("kill.png"));
    m_ButtonKillAdams.addActionListener((ActionEvent e) -> killAdams());
    m_TableFlows.addToButtonsPanel(m_ButtonKillAdams);
  }
  
  /**
   * Finalizes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    updateButtons();
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
   * Returns the underlying table for the flows.
   *
   * @return		the table
   */
  public BaseTableWithButtons getFlowsTable() {
    return m_TableFlows;
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
    sendCommand(cmd, m_CommandProcessor, m_TextRemote.getObject());
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
    sendCommandWithReponse(cmd, m_CommandProcessor, responseHandler, m_TextLocal.getObject(), m_TextRemote.getObject(), DEFAULT_PORT);
  }

  /**
   * Refreshes the list of flows.
   */
  protected void refreshFlows() {
    sendCommandWithReponse(new ListFlows(), new FlowListResponseHandler(this));
  }

  /**
   * Returns an ID array of the the currently selected flows.
   *
   * @return		the flow IDs
   */
  protected int[] getSelectedFlowIDs() {
    int[]	result;
    int[]	sel;
    int		i;

    sel    = m_TableFlows.getSelectedRows();
    result = new int[sel.length];
    for (i = 0; i < sel.length; i++)
      result[i] = ((Number) m_TableFlows.getValueAt(i, ListFlows.COL_ID)).intValue();

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
    SendFlowControlCommand	cmd;

    ids = getSelectedFlowIDs();
    sel = m_TableFlows.getSelectedRows();
    for (i = 0; i < sel.length; i++) {
      paused  = Boolean.parseBoolean(m_TableFlows.getValueAt(sel[i], ListFlows.COL_PAUSED).toString());
      stopped = Boolean.parseBoolean(m_TableFlows.getValueAt(sel[i], ListFlows.COL_STOPPED).toString());
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
    sel = m_TableFlows.getSelectedRows();
    for (i = 0; i < sel.length; i++) {
      paused  = Boolean.parseBoolean(m_TableFlows.getValueAt(sel[i], ListFlows.COL_PAUSED).toString());
      stopped = Boolean.parseBoolean(m_TableFlows.getValueAt(sel[i], ListFlows.COL_STOPPED).toString());
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
    sel = m_TableFlows.getSelectedRows();
    for (i = 0; i < sel.length; i++) {
      stopped = Boolean.parseBoolean(m_TableFlows.getValueAt(sel[i], ListFlows.COL_STOPPED).toString());
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
   * Gets called when the selection in the flow table changes.
   *
   * @param e		the mouse event
   */
  @Override
  public void valueChanged(ListSelectionEvent e) {
    updateButtons();
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
