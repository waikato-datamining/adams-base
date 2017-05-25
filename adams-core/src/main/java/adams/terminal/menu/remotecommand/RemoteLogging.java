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
 * RemoteLogging.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.terminal.menu.remotecommand;

import adams.core.Utils;
import adams.core.base.BaseHostname;
import adams.core.logging.LoggingHelper;
import adams.core.logging.RemoteReceiveHandler.AbstractRemoteListenerRunnable;
import adams.scripting.command.RemoteCommand;
import adams.scripting.command.basic.StartRemoteLogging;
import adams.scripting.command.basic.StopRemoteLogging;
import adams.terminal.application.AbstractTerminalApplication;
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
import com.googlecode.lanterna.gui2.TextBox.Style;

import java.util.logging.LogRecord;
import java.util.regex.Pattern;

/**
 * Allows remote logging.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RemoteLogging
  extends AbstractRemoteCommandActionWithGUI {

  /** the default port to use for receiving logging messages. */
  public final static int DEFAULT_PORT = 31345;

  /**
   * Runnable that outputs the log records to a log.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class RemoteListenerRunnableWithLog
    extends AbstractRemoteListenerRunnable {

    private static final long serialVersionUID = -1375651567275850732L;

    /** the window to output the records to. */
    protected RemoteLogging m_Command;

    /**
     * Initializes the runnable.
     *
     * @param port    the port to listen on
     * @param timeout the timeout
     */
    protected RemoteListenerRunnableWithLog(int port, int timeout, RemoteLogging command) {
      super(port, timeout);
      m_Command = command;
    }

  /**
   * Hook method after the run finished.
   */
    @Override
    protected void postRun() {
      super.postRun();
      m_Command.updateButtons();
    }

    /**
     * Publishes the record.
     *
     * @param record	the record
     */
    @Override
    protected void publish(LogRecord record) {
      if (record != null) {
	m_Command.append(LoggingHelper.assembleMessage(record).toString());
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
    extends AbstractRemoteCommandActionResponseHandler<RemoteLogging> {

    private static final long serialVersionUID = -3200878133646726226L;

    /**
     * Initializes the handler.
     *
     * @param window the tab this handler belongs to
     */
    public LoggingResponseHandler(RemoteLogging window) {
      super(window);
    }

    /**
     * Handles successful responses.
     *
     * @param cmd		the command with the response
     */
    @Override
    public void responseSuccessful(RemoteCommand cmd) {
      getCommand().updateButtons();
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
	getCommand().stopListenerRunnable();
      System.err.println(msg + "\n" + cmd);
    }
  }

  /** the remote host. */
  protected TextBox m_TextRemote;

  /** the maximum number of connection failures. */
  protected TextBox m_TextMaxFailures;

  /** the local host. */
  protected TextBox m_TextLocal;

  /** for outputting the logging info. */
  protected LogTextBox m_TextLog;

  /** the button for starting the logging. */
  protected Button m_ButtonStart;

  /** the button for stopping the logging. */
  protected Button m_ButtonStop;

  /** the runnable. */
  protected RemoteListenerRunnableWithLog m_Runnable;

  /**
   * Initializes the action with no owner.
   */
  public RemoteLogging() {
    super();
  }

  /**
   * Initializes the action.
   *
   * @param owner	the owning application
   */
  public RemoteLogging(AbstractTerminalApplication owner) {
    super(owner);
  }

  /**
   * Returns the title of the action.
   *
   * @return 		the title
   */
  @Override
  public String getTitle() {
    return "Remote logging";
  }

  /**
   * Returns the LogTextBox to use.
   *
   * @return		the log to use
   */
  protected LogTextBox getLogTextBox() {
    return m_TextLog;
  }

  /**
   * Logs the message.
   *
   * @param msg		the message to log
   */
  public void logMessage(String msg) {
    m_TextLog.addLine(msg);
  }

  /**
   * Logs the error.
   *
   * @param msg		the error message to log
   */
  public void logError(String msg) {
    m_TextLog.addLine(msg);
  }

  /**
   * Logs the error.
   *
   * @param msg		the error message to log
   * @param t 		the exception
   */
  public void logError(String msg, Throwable t) {
    m_TextLog.addLine(msg + "\n" + Utils.throwableToString(t));
  }

  /**
   * Starts the listening.
   */
  protected void startListening() {
    StartRemoteLogging	start;

    m_Runnable = new RemoteListenerRunnableWithLog(new BaseHostname(m_TextLocal.getText()).portValue(), RemoteListenerRunnableWithLog.DEFAULT_TIMEOUT, this);
    new Thread(m_Runnable).start();
    start = new StartRemoteLogging();
    start.setInstallListener(false);
    start.setMaxFailures(Integer.parseInt(m_TextMaxFailures.getText()));
    start.setLoggingHost(new BaseHostname(m_TextLocal.getText()));
    m_TextLog.takeFocus();
    updateButtons();
    sendCommandWithReponse(start, new LoggingResponseHandler(this), new BaseHostname(m_TextLocal.getText()), new BaseHostname(m_TextRemote.getText()), DEFAULT_PORT);
  }

  /**
   * Stops the listening.
   */
  protected void stopListening() {
    StopRemoteLogging stop;

    stop = new StopRemoteLogging();
    stop.setLoggingHost(new BaseHostname(m_TextLocal.getText()));
    m_Runnable.stopExecution();
    m_Runnable = null;
    m_TextLog.takeFocus();
    updateButtons();
    sendCommandWithReponse(stop, new LoggingResponseHandler(this), new BaseHostname(m_TextLocal.getText()), new BaseHostname(m_TextRemote.getText()), DEFAULT_PORT);
  }

  /**
   * Updates the buttons.
   */
  public void updateButtons() {
    m_ButtonStart.setEnabled((m_Runnable == null));
    m_ButtonStop.setEnabled((m_Runnable != null) && m_Runnable.isRunning());
  }

  /**
   * Appends the log message.
   *
   * @param msg		the message
   */
  public void append(String msg) {
    m_TextLog.addLine(msg);
  }

  /**
   * Stops the listener runnable.
   */
  public void stopListenerRunnable() {
    if (m_Runnable != null)
      m_Runnable.stopExecution();
  }

  /**
   * Creates the panel to display.
   *
   * @return		the panel
   */
  @Override
  protected Panel createPanel() {
    Panel 	result;
    Panel	panel;

    result = new Panel(new BorderLayout());

    panel = new Panel(new LinearLayout(Direction.HORIZONTAL));
    result.addComponent(panel, Location.TOP);
    
    m_TextRemote = new TextBox(new TerminalSize(15, 1), "127.0.0.1:12345");
    panel.addComponent(new Label("Remote"));
    panel.addComponent(m_TextRemote);
    
    m_TextMaxFailures = new TextBox(new TerminalSize(3, 1), "5");
    m_TextMaxFailures.setValidationPattern(Pattern.compile("(-)?[0-9]+"));
    panel.addComponent(new Label("Max failures"));
    panel.addComponent(m_TextMaxFailures);
    
    m_TextLocal = new TextBox(new TerminalSize(15, 1), "127.0.0.1:" + DEFAULT_PORT);
    panel.addComponent(new Label("Local"));
    panel.addComponent(m_TextLocal);

    m_ButtonStart = new Button("Go", () -> startListening());
    panel.addComponent(m_ButtonStart);

    m_ButtonStop = new Button("X", () -> stopListening());
    panel.addComponent(m_ButtonStop);

    m_TextLog = new LogTextBox("", Style.MULTI_LINE);
    result.addComponent(m_TextLog.withBorder(Borders.singleLine("Log")), Location.CENTER);

    return result;
  }
}
