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
 * ConsoleHelper.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.core.io.console;

import adams.core.base.BasePassword;
import adams.terminal.dialog.OptionDialog;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper for input using <a href="https://github.com/mabe02/lanterna" target="_blank">lanterna</a>.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Lanterna
  implements Console {

  /** the lanterna context. */
  protected MultiWindowTextGUI m_Context;

  /** the text box for logging output. */
  protected TextBox m_TextBoxLog;

  /**
   * Initializes the console helper.
   *
   * @param context	the lanterna context
   * @param textBoxLog	the textbox to use for logging output, can be null
   */
  public Lanterna(MultiWindowTextGUI context, TextBox textBoxLog) {
    if (context == null)
      throw new IllegalArgumentException("Lanterna context cannot be null!");

    m_Context    = context;
    m_TextBoxLog = textBoxLog;
  }

  /**
   * Lets the user enter a multi-line value.
   *
   * @param msg		the message to output before reading the input
   * @return		the entered value, null if cancelled or failed to read input
   */
  public String enterMultiLineValue(String msg) {
    return enterValue(msg);
  }

  /**
   * Lets the user enter a value.
   *
   * @param msg		the message to output before reading the input
   * @return		the entered value, null if cancelled or failed to read input
   */
  public String enterValue(String msg) {
    return enterValue(msg, null);
  }

  /**
   * Lets the user enter a value.
   *
   * @param msg		the message to output before reading the input
   * @param initial	the initial value, ignored if empty or null
   * @return		the entered value, null if cancelled or failed to read input
   */
  public String enterValue(String msg, String initial) {
    if (msg.isEmpty())
      msg = "Please enter a value:";
    return TextInputDialog.showDialog(
      m_Context, "Enter value", msg, (initial == null) ? "" : initial);
  }

  /**
   * Lets the user enter multiple values. Empty string terminates the loop.
   *
   * @param msg		the message to output before reading the input
   * @return		the entered value, null if cancelled or failed to read input
   */
  public String[] enterMultipleValues(String msg) {
    List<String>	result;
    String		input;

    result = new ArrayList<>();

    if (msg.isEmpty())
      msg = "Please enter one or more values:";
    msg += "\n(empty input will terminate input)";

    do {
      input = enterValue(msg);
      if (input != null) {
	if (!input.isEmpty())
	  result.add(input);
      }
    }
    while ((input != null) && !input.isEmpty());

    if (result.size() > 0)
      return result.toArray(new String[result.size()]);
    else
      return null;
  }

  /**
   * Lets the user enter a password.
   *
   * @param msg		the message to output before reading the input
   * @return		the entered value, null if cancelled or failed to read input
   */
  public BasePassword enterPassword(String msg) {
    BasePassword	result;
    String		input;

    result = null;

    if (msg.isEmpty())
      msg = "Please enter password:";
    input = TextInputDialog.showPasswordDialog(m_Context, "Enter password", msg, "");
    if (input != null)
      result = new BasePassword(input);

    return result;
  }

  /**
   * Lets the user select from a number of choices.
   *
   * @param msg		the message to output before reading the input
   * @param options	the options to choose from
   * @return		the selected option, null if cancelled or failed to read input
   */
  public String selectOption(String msg, String[] options) {
    return selectOption(msg, options, null);
  }

  /**
   * Lets the user select from a number of choices.
   *
   * @param msg		the message to output before reading the input
   * @param options	the options to choose from
   * @param initial	the initial selection (selected if just hitting enter), null or empty string to ignore
   * @return		the selected option, null if cancelled or failed to read input
   */
  public String selectOption(String msg, String[] options, String initial) {
    if (initial == null)
      initial = "";

    if (msg.isEmpty())
      msg = "Please select an option:";

    return OptionDialog.showDialog(m_Context, "Select option", msg, options, initial);
  }

  /**
   * Outputs the message.
   *
   * @param msg		the message to output
   */
  public void printlnOut(String msg) {
    if (m_TextBoxLog != null) {
      m_TextBoxLog.addLine(msg);
      m_TextBoxLog.setCaretPosition(m_TextBoxLog.getLineCount(), 0);
    }
    else {
      System.out.println(msg);
    }
  }

  /**
   * Outputs the error message.
   *
   * @param msg		the error message to output
   */
  public void printlnErr(String msg) {
    if (m_TextBoxLog != null) {
      m_TextBoxLog.addLine(msg);
      m_TextBoxLog.setCaretPosition(m_TextBoxLog.getLineCount(), 0);
    }
    else {
      System.err.println(msg);
    }
  }
}
