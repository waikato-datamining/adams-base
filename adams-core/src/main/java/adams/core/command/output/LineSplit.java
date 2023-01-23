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
 * LineSplit.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.core.command.output;

import adams.core.base.BaseRegExp;
import adams.core.command.ExternalCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Splits the incoming data on the new line character and forwards string arrays.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class LineSplit
  extends AbstractOutputFormatter {

  private static final long serialVersionUID = 6046334565837331199L;

  /** the regular expression to match. */
  protected BaseRegExp m_RegExp;

  /** whether to invert the matching sense. */
  protected boolean m_Invert;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Splits the incoming data on the new line character and forwards string arrays.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "regexp", "regExp",
      new BaseRegExp(BaseRegExp.MATCH_ALL));

    m_OptionManager.add(
      "invert", "invert",
      false);
  }

  /**
   * Sets the regular expression to match the strings against.
   *
   * @param value	the regular expression
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regular expression to match the strings against.
   *
   * @return		the regular expression
   */
  public BaseRegExp getRegExp() {
    return m_RegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regExpTipText() {
    return "The regular expression used for matching the strings.";
  }

  /**
   * Sets whether to invert the matching sense.
   *
   * @param value	true if inverting matching sense
   */
  public void setInvert(boolean value) {
    m_Invert = value;
    reset();
  }

  /**
   * Returns whether to invert the matching sense.
   *
   * @return		true if matching sense is inverted
   */
  public boolean getInvert() {
    return m_Invert;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String invertTipText() {
    return "If set to true, then the matching sense is inverted.";
  }

  /**
   * Returns what output type the formatter generates.
   *
   * @param blocking returns the type when used on blocking mode output
   * @return the type
   */
  @Override
  public Class generates(boolean blocking) {
    return String[].class;
  }

  /**
   * Formats the output received from the command. Feeds the formatted data back into the
   * ExternalCommand instance.
   *
   * @param command  the external command to feed the output back into
   * @param stdout   whether the output was from stdout or stderr
   * @param blocking whether the output was received via blocking or async execution
   * @param output   the output to format
   */
  @Override
  public void formatOutput(ExternalCommand command, boolean stdout, boolean blocking, String output) {
    List<String>	result;
    List<String>	lines;
    boolean		pass;

    result = new ArrayList<>();
    lines  = Arrays.asList(output.split("\n"));

    for (String line: lines) {
      if (m_Invert)
	pass = !m_RegExp.isMatch(line);
      else
	pass = m_RegExp.isMatch(line);
      if (pass)
        result.add(line);
    }

    command.addFormattedOutput(result.toArray(new String[0]));
  }
}
