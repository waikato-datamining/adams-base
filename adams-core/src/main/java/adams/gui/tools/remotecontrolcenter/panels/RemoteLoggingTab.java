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
 * RemoteLoggingTab.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.remotecontrolcenter.panels;

import adams.core.base.BaseHostname;
import adams.core.logging.LoggingHelper;
import adams.core.logging.LoggingLevel;
import adams.core.logging.RemoteReceiveHandler.AbstractRemoteListenerRunnable;
import adams.gui.core.BaseObjectTextField;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.GUIHelper;
import adams.gui.core.NumberTextField;
import adams.gui.core.NumberTextField.BoundedNumberCheckModel;
import adams.gui.core.NumberTextField.Type;
import adams.gui.core.SimpleLogPanel;
import adams.scripting.command.RemoteCommand;
import adams.scripting.command.basic.StartRemoteLogging;
import adams.scripting.command.basic.StopRemoteLogging;
import adams.scripting.responsehandler.SimpleLogPanelResponseHandler;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.logging.LogRecord;

/**
 * Tab for starting/stopping tapping into the logging messages of a remote
 * flow (both machines can communicate via host/port).
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RemoteLoggingTab
  extends AbstractRemoteControlCenterTab {

  private static final long serialVersionUID = 6645831134460386650L;

  /**
   * Runnable that outputs the log records to a log.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class RemoteListenerRunnableWithLog
    extends AbstractRemoteListenerRunnable {

    private static final long serialVersionUID = -1375651567275850732L;

    /** the tab to output the records to. */
    protected RemoteLoggingTab m_Tab;

    /**
     * Initializes the runnable.
     *
     * @param port    the port to listen on
     * @param timeout the timeout
     */
    protected RemoteListenerRunnableWithLog(int port, int timeout, RemoteLoggingTab tab) {
      super(port, timeout);
      m_Tab = tab;
    }

  /**
   * Hook method after the run finished.
   */
    @Override
    protected void postRun() {
      super.postRun();
      m_Tab.updateButtons();
    }

    /**
     * Publishes the record.
     *
     * @param record	the record
     */
    @Override
    protected void publish(LogRecord record) {
      if (record != null) {
	m_Tab.getLog().append(
	  LoggingLevel.valueOf(record.getLevel()),
	  LoggingHelper.assembleMessage(record).toString());
      }
    }
  }

  /**
   * Updates the buttons accordingly and displays error messages.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class LoggingResponseHandler
    extends AbstractTabResponseHandler<RemoteLoggingTab> {

    private static final long serialVersionUID = -3200878133646726226L;

    /**
     * Initializes the handler.
     *
     * @param tab the tab this handler belongs to
     */
    public LoggingResponseHandler(RemoteLoggingTab tab) {
      super(tab);
    }

    /**
     * Handles successful responses.
     *
     * @param cmd		the command with the response
     */
    @Override
    public void responseSuccessful(RemoteCommand cmd) {
      getTab().updateButtons();
    }

    /**
     * Handles failed responses.
     *
     * @param cmd		the command with the response
     * @param msg		message, can be null
     */
    @Override
    public void responseFailed(RemoteCommand cmd, String msg) {
      if (cmd instanceof StartRemoteLogging)
	getTab().stopListenerRunnable();
      m_Tab.getOwner().logError(msg + "\n" + cmd, "Response failed");
    }
  }

  /** the default port to use for receiving logging messages. */
  public final static int DEFAULT_PORT = 31345;

  /** the split pane. */
  protected BaseSplitPane m_SplitPane;

  /** the remote machine. */
  protected BaseObjectTextField<BaseHostname> m_TextRemote;

  /** the maximum number of failures to tolerate. */
  protected NumberTextField m_TextMaxFailures;

  /** the local machine. */
  protected BaseObjectTextField<BaseHostname> m_TextLocal;

  /** the button for executing the command. */
  protected JButton m_ButtonStartStop;

  /** the log for the responses. */
  protected SimpleLogPanel m_Log;

  /** the response logger. */
  protected SimpleLogPanelResponseHandler m_ResponseLogger;

  /** whether logging is currently active. */
  protected boolean m_LoggingActive;

  /** the runnable. */
  protected RemoteListenerRunnableWithLog m_Runnable;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_ResponseLogger = new SimpleLogPanelResponseHandler();
    m_LoggingActive  = false;
    m_Runnable       = null;
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel 	panelAll;
    JPanel 	panelHosts;
    JPanel	panelButton;
    JLabel	label;

    super.initGUI();

    setLayout(new BorderLayout());

    m_SplitPane = new BaseSplitPane(BaseSplitPane.VERTICAL_SPLIT);
    m_SplitPane.setDividerLocation(200);
    m_SplitPane.setResizeWeight(0.5);
    add(m_SplitPane, BorderLayout.CENTER);

    panelAll = new JPanel(new BorderLayout());
    m_SplitPane.setBottomComponent(panelAll);

    panelHosts = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelAll.add(panelHosts, BorderLayout.NORTH);

    m_TextRemote = new BaseObjectTextField<>(new BaseHostname(), "127.0.0.1:12345");
    m_TextRemote.setColumns(20);
    label = new JLabel("Remote");
    label.setDisplayedMnemonic('R');
    label.setLabelFor(m_TextRemote);
    panelHosts.add(label);
    panelHosts.add(m_TextRemote);

    m_TextMaxFailures = new NumberTextField(Type.INTEGER, "-1", 5);
    m_TextMaxFailures.setCheckModel(new BoundedNumberCheckModel(Type.INTEGER, -1, null));
    m_TextMaxFailures.setToolTipText("The maximum number of connection failures to tolerate on the sending side; -1 for infinite attempts");
    label = new JLabel("Max failures");
    label.setDisplayedMnemonic('f');
    label.setLabelFor(m_TextMaxFailures);
    panelHosts.add(label);
    panelHosts.add(m_TextMaxFailures);

    m_TextLocal = new BaseObjectTextField<>(new BaseHostname(), "127.0.0.1:" + DEFAULT_PORT);
    m_TextLocal.setColumns(20);
    label = new JLabel("Local");
    label.setDisplayedMnemonic('L');
    label.setLabelFor(m_TextLocal);
    panelHosts.add(label);
    panelHosts.add(m_TextLocal);

    m_ButtonStartStop = new JButton(GUIHelper.getIcon("run.gif"));
    m_ButtonStartStop.addActionListener((ActionEvent e) -> startStopLogging());
    panelButton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panelButton.add(m_ButtonStartStop);
    panelHosts.add(panelButton);

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
    m_ResponseLogger.setLog(m_Log);
  }

  /**
   * Starts/stops the logging.
   */
  protected void startStopLogging() {
    StartRemoteLogging	start;
    StopRemoteLogging	stop;

    if ((m_Runnable != null) && m_Runnable.isRunning()) {
      stop = new StopRemoteLogging();
      stop.setLoggingHost(m_TextLocal.getObject());
      m_Runnable.stopExecution();
      m_Runnable = null;
      updateButtons();
      sendCommandWithReponse(stop, new LoggingResponseHandler(this), m_TextLocal.getObject(), m_TextRemote.getObject(), DEFAULT_PORT);
    }
    else {
      m_Runnable = new RemoteListenerRunnableWithLog(m_TextLocal.getObject().portValue(), RemoteListenerRunnableWithLog.DEFAULT_TIMEOUT, this);
      new Thread(m_Runnable).start();
      start = new StartRemoteLogging();
      start.setInstallListener(false);
      start.setMaxFailures(m_TextMaxFailures.getValue().intValue());
      start.setLoggingHost(m_TextLocal.getObject());
      updateButtons();
      sendCommandWithReponse(start, new LoggingResponseHandler(this), m_TextLocal.getObject(), m_TextRemote.getObject(), DEFAULT_PORT);
    }
  }

  /**
   * Returns the title of the tab.
   *
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Remote logging";
  }

  /**
   * Returns the name of icon to use for the tab.
   *
   * @return		the icon
   */
  @Override
  public String getTabIcon() {
    return "log.gif";
  }

  /**
   * Returns the log panel.
   *
   * @return		the panel
   */
  public SimpleLogPanel getLog() {
    return m_Log;
  }

  /**
   * Stops the listener runnable.
   */
  public void stopListenerRunnable() {
    if (m_Runnable != null)
      m_Runnable.stopExecution();
  }

  /**
   * Updates the buttons.
   */
  public void updateButtons() {
    if ((m_Runnable != null) && m_Runnable.isRunning())
      m_ButtonStartStop.setIcon(GUIHelper.getIcon("stop_blue.gif"));
    else
      m_ButtonStartStop.setIcon(GUIHelper.getIcon("run.gif"));
  }
}
