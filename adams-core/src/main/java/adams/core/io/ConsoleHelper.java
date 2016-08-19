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

import adams.core.Utils;
import adams.core.base.BasePassword;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper for input from stdin.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ConsoleHelper {

  /**
   * Reads a line from stdin.
   *
   * @return		the line, null if failed to read
   */
  protected static String readLine() {
    InputStreamReader	inreader;
    BufferedReader	breader;

    inreader = new InputStreamReader(System.in);
    breader = new BufferedReader(inreader);

    try {
      return breader.readLine();
    }
    catch (Exception e) {
      return null;
    }
  }

  /**
   * Lets the user enter a value.
   *
   * @param msg		the message to output before reading the input
   * @return		the entered value, null if cancelled or failed to read input
   */
  public static String enterValue(String msg) {
    return enterValue(msg, null);
  }

  /**
   * Lets the user enter a value.
   *
   * @param msg		the message to output before reading the input
   * @param initial	the initial value, ignored if empty or null
   * @return		the entered value, null if cancelled or failed to read input
   */
  public static String enterValue(String msg, String initial) {
    String	result;

    if (msg.isEmpty())
      msg = "Please enter a value:";
    if ((initial != null) && !initial.isEmpty())
      msg += "(initial: " + initial + ")";
    System.out.println(msg);
    result = readLine();

    if (result != null)
      result = result.trim();

    return result;
  }

  /**
   * Lets the user enter multiple values. Empty string terminates the loop.
   *
   * @param msg		the message to output before reading the input
   * @return		the entered value, null if cancelled or failed to read input
   */
  public static String[] enterMultipleValues(String msg) {
    List<String> 	result;
    String		line;

    if (msg.isEmpty())
      msg = "Please enter one or more values:";
    msg += "\n(empty line will terminate input)";
    System.out.println(msg);
    result = new ArrayList<>();
    line   = "";
    while (line != null) {
      line = readLine();
      if (line != null) {
	if (line.isEmpty())
	  line = null;
	else
	  result.add(line.trim());
      }
    }

    if (result.size() == 0)
      return null;
    else
      return result.toArray(new String[result.size()]);
  }

  /**
   * Lets the user enter a password.
   *
   * @param msg		the message to output before reading the input
   * @return		the entered value, null if cancelled or failed to read input
   */
  public static BasePassword enterPassword(String msg) {
    BasePassword	result;
    String		value;

    if (msg.isEmpty())
      msg = "Please enter password:";
    System.out.println(msg);
    value = readLine();

    if (value != null)
      result = new BasePassword(value);
    else
      result = null;

    return result;
  }

  /**
   * Lets the user select from a number of choices.
   *
   * @param msg		the message to output before reading the input
   * @param options	the options to choose from
   * @return		the selected option, null if cancelled or failed to read input
   */
  public static String selectOption(String msg, String[] options) {
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
  public static String selectOption(String msg, String[] options, String initial) {
    String		result;
    String		indexStr;
    StringBuilder 	fullMsg;
    int			i;
    int			index;
    int			initialIndex;

    result = null;

    if (initial == null)
      initial = "";
    initialIndex = -1;

    if (msg.isEmpty())
      msg = "Please select an option:";
    fullMsg = new StringBuilder(msg);
    for (i = 0; i < options.length; i++) {
      fullMsg.append("\n");
      fullMsg.append("[" + (i+1) + "] " + options[i]);
      if (initial.equals(options[i]))
	initialIndex = i;
    }
    fullMsg.append("\nChoice (1-" + options.length + ")");
    if (initialIndex > -1)
      fullMsg.append(" (default: " + (initialIndex + 1) + ")");
    fullMsg.append(": ");
    System.out.println(fullMsg);
    indexStr = readLine();

    if (indexStr != null) {
      index = -1;
      if (Utils.isInteger(indexStr.trim()))
	index = Integer.parseInt(indexStr.trim()) - 1;
      if ((index >= 0) && (index < options.length))
	result = options[index];
      if ((result == null) && (initialIndex > -1))
	result = options[initialIndex];
    }

    return result;
  }
}
