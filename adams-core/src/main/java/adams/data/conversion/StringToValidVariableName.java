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
 * StringToValidVariableName.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.core.QuickInfoHelper;
import adams.core.Variables;

/**
 <!-- globalinfo-start -->
 * Turns any string into a valid variable name.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-replace &lt;java.lang.String&gt; (property: replace)
 * &nbsp;&nbsp;&nbsp;The replacement string for invalid characters in strings; use empty string 
 * &nbsp;&nbsp;&nbsp;for removing the invalid characters instead of replacing them.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-pad &lt;boolean&gt; (property: pad)
 * &nbsp;&nbsp;&nbsp;If enabled, the variable gets padded with &#64;{ and }.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StringToValidVariableName
  extends AbstractValidateString {

  /** for serialization. */
  private static final long serialVersionUID = 5528425779551772381L;

  /** whether to pad the variable name with @{...}. */
  protected boolean m_Pad;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns any string into a valid variable name.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "pad", "pad",
	    false);
  }

  /**
   * Sets the replacement string for invalid characters.
   *
   * @param value 	the replacement string
   */
  public void setPad(boolean value) {
    m_Pad = value;
    reset();
  }

  /**
   * Returns the replacement string for invalid characters.
   *
   * @return 		the replacement string
   */
  public boolean getPad() {
    return m_Pad;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String padTipText() {
    return "If enabled, the variable gets padded with " + Variables.START + " and " + Variables.END + ".";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	value;

    result = super.getQuickInfo();
    value  = QuickInfoHelper.toString(this, "pad", m_Pad, "pad", ", ");
    if (value != null)
      result += value;

    return result;
  }

  /**
   * Performs the actual validation.
   * 
   * @param input	the input string
   * @param replace	the replacement string for invalide characters
   */
  @Override
  protected String process(String input, String replace) throws Exception {
    String	result;

    result = Variables.toValidName(input, replace);
    if (m_Pad)
      result = Variables.padName(result);

    return result;
  }
}
