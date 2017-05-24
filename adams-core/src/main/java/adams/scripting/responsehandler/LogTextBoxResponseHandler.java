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
 * LogTextBoxResponseHandler.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.scripting.responsehandler;

import adams.core.io.ConsoleHelper;
import adams.core.logging.LoggingLevel;
import adams.core.option.OptionUtils;
import adams.scripting.command.RemoteCommand;
import adams.scripting.command.RemoteCommandWithErrorMessage;
import adams.terminal.core.LogTextBox;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;

/**
 * For logging responses.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LogTextBoxResponseHandler
  extends AbstractResponseHandler {

  private static final long serialVersionUID = -6193490944184585319L;

  /** the log to use. */
  protected LogTextBox m_Log;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Logs responses.";
  }

  /**
   * Sets the log to use.
   *
   * @param value	the log
   */
  public void setLog(LogTextBox value) {
    m_Log = value;
  }

  /**
   * Returns the log in use.
   *
   * @return		the log
   */
  public LogTextBox getLog() {
    return m_Log;
  }

  /**
   * Displays the error message.
   *
   * @param cmd		the command
   */
  protected void displayErrorMessage(RemoteCommandWithErrorMessage cmd) {
    if ((m_Log != null) && (m_Log.getTextGUI() instanceof WindowBasedTextGUI))
      MessageDialog.showMessageDialog((WindowBasedTextGUI) m_Log.getTextGUI(), cmd.getClass().getName(), cmd.getErrorMessage());
    else
      ConsoleHelper.printlnErr(cmd.getClass().getName() + "\n" + cmd.getErrorMessage());
  }

  /**
   * Handles successful responses.
   *
   * @param cmd		the command with the response
   */
  @Override
  public void responseSuccessful(RemoteCommand cmd) {
    if (!m_Enabled)
      return;

    if (m_Log != null)
      m_Log.append(LoggingLevel.INFO, "Successful response: " + OptionUtils.getCommandLine(cmd) + "\n" + cmd);

    // error message?
    if (cmd instanceof RemoteCommandWithErrorMessage) {
      if (((RemoteCommandWithErrorMessage) cmd).hasErrorMessage())
	displayErrorMessage((RemoteCommandWithErrorMessage) cmd);
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
    if (!m_Enabled)
      return;

    if (m_Log != null)
      m_Log.append(LoggingLevel.SEVERE, "Failed response: " + OptionUtils.getCommandLine(cmd) + "\nMessage: " + msg + "\n" + cmd);

    // error message?
    if (cmd instanceof RemoteCommandWithErrorMessage) {
      if (((RemoteCommandWithErrorMessage) cmd).hasErrorMessage())
	displayErrorMessage((RemoteCommandWithErrorMessage) cmd);
    }
  }
}
