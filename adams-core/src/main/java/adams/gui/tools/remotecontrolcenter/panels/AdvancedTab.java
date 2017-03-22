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
 * AdvancedTab.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.remotecontrolcenter.panels;

import adams.gui.chooser.AbstractChooserPanel;
import adams.gui.chooser.AbstractChooserPanel.ChooseListener;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.GUIHelper;
import adams.gui.core.ParameterPanel;
import adams.gui.core.SimpleLogPanel;
import adams.gui.event.RemoteScriptingEngineUpdateEvent;
import adams.gui.goe.GenericObjectEditorPanel;
import adams.gui.visualization.debug.InspectionPanel;
import adams.scripting.command.RemoteCommand;
import adams.scripting.command.RemoteCommandWithResponse;
import adams.scripting.command.basic.Ping;
import adams.scripting.connection.Connection;
import adams.scripting.connection.DefaultConnection;
import adams.scripting.responsehandler.AbstractResponseHandler;
import adams.scripting.responsehandler.SimpleLogPanelResponseHandler;

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
public class AdvancedTab
  extends AbstractRemoteControlCenterTab
  implements ChooseListener {

  private static final long serialVersionUID = -894258879660492792L;

  /**
   * Specialized response handler that populates an {@link InspectionPanel}.
   */
  public static class InspectionResponseHandler
    extends AbstractResponseHandler {

    private static final long serialVersionUID = -3925270844782740556L;

    /** the tab. */
    protected AbstractRemoteControlCenterTab m_Tab;

    /** the inspection panel to use. */
    protected InspectionPanel m_InspectionPanel;

    /**
     * Returns a string describing the object.
     *
     * @return 			a description suitable for displaying in the gui
     */
    @Override
    public String globalInfo() {
      return "Specialized response handler that populates an InspectionPanel.";
    }

    /**
     * Sets the tab this handler belongs to.
     *
     * @param value	the tab
     */
    public void setTab(AbstractRemoteControlCenterTab value) {
      m_Tab = value;
    }

    /**
     * Returns the tab this handler belongs to.
     *
     * @return		the tab, null if none set
     */
    public AbstractRemoteControlCenterTab getTab() {
      return m_Tab;
    }

    /**
     * Sets the panel to use.
     *
     * @param value		the panel
     */
    public void setInspectionPanel(InspectionPanel value) {
      m_InspectionPanel = value;
    }

    /**
     * Returns the panel in use.
     *
     * @return			the panel, null if none set
     */
    public InspectionPanel getInspectionPanel() {
      return m_InspectionPanel;
    }

    /**
     * Handles successful responses.
     *
     * @param cmd		the command with the response
     */
    @Override
    public void responseSuccessful(RemoteCommand cmd) {
      Object	current;

      if (m_InspectionPanel == null)
	return;

      current = null;

      if (cmd instanceof RemoteCommandWithResponse)
	current = ((RemoteCommandWithResponse) cmd).getResponsePayloadObjects();

      m_InspectionPanel.setCurrent(current);
    }

    /**
     * Handles failed responses.
     *
     * @param cmd		the command with the response
     * @param msg		message, can be null
     */
    @Override
    public void responseFailed(RemoteCommand cmd, String msg) {
      if (m_InspectionPanel == null)
	return;
      m_InspectionPanel.setCurrent(null);

      // display error
      if (m_Tab == null)
	GUIHelper.showErrorMessage(
	  GUIHelper.getParentComponent(m_InspectionPanel), msg, cmd.getClass().getName());
      else
	m_Tab.getOwner().logError(msg, cmd.getClass().getName());
    }
  }

  /** the parameter panel. */
  protected ParameterPanel m_PanelParams;

  /** the panel for the command. */
  protected GenericObjectEditorPanel m_GOECommand;

  /** the panel for the connection. */
  protected GenericObjectEditorPanel m_GOEConnection;

  /** the button for sending the command. */
  protected JButton m_ButtonSend;

  /** the tabbed pane for log/results. */
  protected BaseTabbedPane m_TabbedPane;

  /** the log for the responses. */
  protected SimpleLogPanel m_Log;

  /** for the results. */
  protected InspectionPanel m_Results;

  /** the response logger. */
  protected SimpleLogPanelResponseHandler m_ResponseLogger;

  /** the inspection handler. */
  protected InspectionResponseHandler m_InspectionHandler;

  /** the response connection updater. */
  protected GenericObjectEditorResponseConnectionUpdater m_ResponseConnectionUpdater;

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_ResponseLogger            = new SimpleLogPanelResponseHandler();
    m_InspectionHandler         = new InspectionResponseHandler();
    m_ResponseConnectionUpdater = new GenericObjectEditorResponseConnectionUpdater();
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
    m_GOECommand.setPostProcessObjectHandler(m_ResponseConnectionUpdater);
    m_GOECommand.addChooseListener(this);
    m_PanelParams.addParameter("Command", m_GOECommand);

    m_GOEConnection = new GenericObjectEditorPanel(Connection.class, new DefaultConnection(), true);
    m_PanelParams.addParameter("Connection", m_GOEConnection);

    panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panel.add(panelButtons, BorderLayout.SOUTH);
    m_ButtonSend = new JButton(GUIHelper.getIcon("run.gif"));
    m_ButtonSend.addActionListener((ActionEvent e) -> sendCommand());
    panelButtons.add(m_ButtonSend);

    m_TabbedPane = new BaseTabbedPane();
    add(m_TabbedPane, BorderLayout.CENTER);

    m_Log = new SimpleLogPanel();
    m_TabbedPane.addTab("Log", m_Log);

    m_Results = new InspectionPanel();
    m_Results.setMaxDepth(5);
    m_TabbedPane.addTab("Results", m_Results);

    m_TabbedPane.setSelectedIndex(0);
  }

  /**
   * Finishes up the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    m_ResponseLogger.setTab(this);
    m_ResponseLogger.setLog(m_Log);
    m_InspectionHandler.setTab(this);
    m_InspectionHandler.setInspectionPanel(m_Results);
  }

  /**
   * Returns the title of the tab.
   *
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Advanced";
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
    AbstractResponseHandler.insertHandler(this, getApplicationFrame(), m_InspectionHandler);
    m_ResponseConnectionUpdater.setApplication(getApplicationFrame());
    m_GOECommand.setCurrent(m_ResponseConnectionUpdater.postProcessObject(null, m_GOECommand.getCurrent()));
  }

  /**
   * Gets called before the user chooses a value.
   *
   * @param panel	the panel that triggered the event
   */
  @Override
  public void beforeChoose(AbstractChooserPanel panel) {
    if (panel == m_GOECommand) {
      if (panel.isNoChooseYet())
	m_ResponseConnectionUpdater.setApplication(getApplicationFrame());
    }
  }

  /**
   * Gets called after the user chose a value.
   *
   * @param panel	the panel that triggered the event
   */
  @Override
  public void afterChoose(AbstractChooserPanel panel) {
  }
}
