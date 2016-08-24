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

/**
 * Interface for classes that allow the user to interact in the terminal.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface Console {

  /**
   * Lets the user enter a multi-line value.
   *
   * @param msg		the message to output before reading the input
   * @return		the entered value, null if cancelled or failed to read input
   */
  public String enterMultiLineValue(String msg);

  /**
   * Lets the user enter a value.
   *
   * @param msg		the message to output before reading the input
   * @return		the entered value, null if cancelled or failed to read input
   */
  public String enterValue(String msg);

  /**
   * Lets the user enter a value.
   *
   * @param msg		the message to output before reading the input
   * @param initial	the initial value, ignored if empty or null
   * @return		the entered value, null if cancelled or failed to read input
   */
  public String enterValue(String msg, String initial);

  /**
   * Lets the user enter multiple values. Empty string terminates the loop.
   *
   * @param msg		the message to output before reading the input
   * @return		the entered value, null if cancelled or failed to read input
   */
  public String[] enterMultipleValues(String msg);

  /**
   * Lets the user enter a password.
   *
   * @param msg		the message to output before reading the input
   * @return		the entered value, null if cancelled or failed to read input
   */
  public BasePassword enterPassword(String msg);

  /**
   * Lets the user select from a number of choices.
   *
   * @param msg		the message to output before reading the input
   * @param options	the options to choose from
   * @return		the selected option, null if cancelled or failed to read input
   */
  public String selectOption(String msg, String[] options);

  /**
   * Lets the user select from a number of choices.
   *
   * @param msg		the message to output before reading the input
   * @param options	the options to choose from
   * @param initial	the initial selection (selected if just hitting enter), null or empty string to ignore
   * @return		the selected option, null if cancelled or failed to read input
   */
  public String selectOption(String msg, String[] options, String initial);

  /**
   * Outputs the message.
   *
   * @param msg		the message to output
   */
  public void printlnOut(String msg);

  /**
   * Outputs the error message.
   *
   * @param msg		the error message to output
   */
  public void printlnErr(String msg);
}
