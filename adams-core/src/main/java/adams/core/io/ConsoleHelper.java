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

package adams.core.io;

import adams.core.base.BasePassword;
import adams.core.io.console.Basic;
import adams.core.io.console.Console;
import adams.core.io.console.Lanterna;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.TextBox;

/**
 * Helper for input in a terminal.
 * By default, it uses System.out and System.in for interaction. By providing
 * a lanterna context via {@link #useLanterna(MultiWindowTextGUI)}, it is
 * possible to make use of lanterna.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ConsoleHelper {

  /** the actual helper. */
  protected static Console m_Helper;
  static {
    useBasic();
  }

  /**
   * Switches to the basic helper.
   *
   * @see Basic
   */
  public static void useBasic() {
    m_Helper = new Basic();
  }

  /**
   * Switches to the lanterna helper.
   *
   * @param context	the lanterna context
   * @param textBoxLog	the textbox to use for logging output, can be null
   * @see		Lanterna
   */
  public static void useLanterna(MultiWindowTextGUI context, TextBox textBoxLog) {
    m_Helper = new Lanterna(context, textBoxLog);
  }

  /**
   * Lets the user enter a multi-line value.
   *
   * @param msg		the message to output before reading the input
   * @return		the entered value, null if cancelled or failed to read input
   */
  public static String enterMultiLineValue(String msg) {
    return m_Helper.enterMultiLineValue(msg);
  }

  /**
   * Lets the user enter a value.
   *
   * @param msg		the message to output before reading the input
   * @return		the entered value, null if cancelled or failed to read input
   */
  public static String enterValue(String msg) {
    return m_Helper.enterValue(msg);
  }

  /**
   * Lets the user enter a value.
   *
   * @param msg		the message to output before reading the input
   * @param initial	the initial value, ignored if empty or null
   * @return		the entered value, null if cancelled or failed to read input
   */
  public static String enterValue(String msg, String initial) {
    return m_Helper.enterValue(msg, initial);
  }

  /**
   * Lets the user enter multiple values. Empty string terminates the loop.
   *
   * @param msg		the message to output before reading the input
   * @return		the entered value, null if cancelled or failed to read input
   */
  public static String[] enterMultipleValues(String msg) {
    return m_Helper.enterMultipleValues(msg);
  }

  /**
   * Lets the user enter a password.
   *
   * @param msg		the message to output before reading the input
   * @return		the entered value, null if cancelled or failed to read input
   */
  public static BasePassword enterPassword(String msg) {
    return m_Helper.enterPassword(msg);
  }

  /**
   * Lets the user select from a number of choices.
   *
   * @param msg		the message to output before reading the input
   * @param options	the options to choose from
   * @return		the selected option, null if cancelled or failed to read input
   */
  public static String selectOption(String msg, String[] options) {
    return m_Helper.selectOption(msg, options);
  }

  /**
   * Lets the user select from a number of choices.
   *
   * @param msg		the message to output before reading the input
   * @param options	the options to choose from
   * @param initial	the initial selection (selected if just hitting enter), null or empty string to ignore
   * @return		the selected option, null if cancelled or failed to read input
   */
  public static String selectOption(String msg, String[] options, String initial) {
    return m_Helper.selectOption(msg, options, initial);
  }

  /**
   * Outputs the message.
   *
   * @param msg		the message to output
   */
  public static void printlnOut(String msg) {
    m_Helper.printlnOut(msg);
  }

  /**
   * Outputs the error message.
   *
   * @param msg		the error message to output
   */
  public static void printlnErr(String msg) {
    m_Helper.printlnErr(msg);
  }
}
