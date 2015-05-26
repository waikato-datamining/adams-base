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
 * AbstractValidateString.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.core.QuickInfoHelper;

/**
 * Ancestor for string conversions that ensure a string only contains valid
 * characters according to some rules.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractValidateString
  extends AbstractStringConversion {

  /** for serialization. */
  private static final long serialVersionUID = -4017583319699378889L;

  /** the replacement string. */
  protected String m_Replace;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "replace", "replace",
	    getDefaultReplace());
  }

  /**
   * Returns the default replacement string.
   * 
   * @return		the default
   */
  protected String getDefaultReplace() {
    return "";
  }
  
  /**
   * Sets the replacement string for invalid characters.
   *
   * @param value 	the replacement string
   */
  public void setReplace(String value) {
    m_Replace = value;
    reset();
  }

  /**
   * Returns the replacement string for invalid characters.
   *
   * @return 		the replacement string
   */
  public String getReplace() {
    return m_Replace;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String replaceTipText() {
    return
        "The replacement string for invalid characters in strings; use empty "
	+ "string for removing the invalid characters instead of replacing them.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "replace", "'" + m_Replace + "'", "replace: ");
  }

  /**
   * Performs the actual validation.
   * 
   * @param input	the input string
   * @param replace	the replacement string for invalide characters
   */
  protected abstract String process(String input, String replace) throws Exception;
  
  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    return process((String) m_Input, m_Replace);
  }
}
