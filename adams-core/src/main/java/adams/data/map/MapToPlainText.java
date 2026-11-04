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
 * MapToPlainText.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.data.map;

import adams.core.DateUtils;
import adams.data.conversion.MapToString;

import java.util.Map;

/**
 * Turns a map object into a plain-text string.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class MapToPlainText
  extends AbstractMapToString {

  private static final long serialVersionUID = -7794207056977316170L;

  /** whether to sort the keys. */
  protected boolean m_SortKeys;

  /** how many spaces to use for indentation. */
  protected int m_Indentation;

  /** the actual conversion. */
  protected transient MapToString m_Conversion;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns a map object into a plain-text string.\n"
	     + "When encountering date objects as values, they get turned into a string "
	     + "using: " + DateUtils.getTimestampFormatter().toPattern() + "\n"
	     + "Output format:\n"
	     + "<key>:\n"
	     + "  <value>";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "sort-keys", "sortKeys",
      false);

    m_OptionManager.add(
      "indentation", "indentation",
      2, 0, null);
  }

  /**
   * Sets whether to sort the keys before generating the output.
   *
   * @param value true if to sort the keys
   */
  public void setSortKeys(boolean value) {
    m_SortKeys = value;
    reset();
  }

  /**
   * Returns whether to sort the keys before generating the output.
   *
   * @return true if to sort the keys
   */
  public boolean getSortKeys() {
    return m_SortKeys;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for
   * displaying in the GUI or for listing the options.
   */
  public String sortKeysTipText() {
    return "If enabled, the keys in the map get sorted before generating the output.";
  }

  /**
   * Sets the number of spaces to use for indentation.
   *
   * @param value 	the number of spaces
   */
  public void setIndentation(int value) {
    if (getOptionManager().isValid("indentation", value)) {
      m_Indentation = value;
      reset();
    }
  }

  /**
   * Returns the number of spaces to use for indentation.
   *
   * @return 		the number of spaces
   */
  public int getIndentation() {
    return m_Indentation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String indentationTipText() {
    return "The number of spaces to use for indentation.";
  }

  /**
   * Converts the map into a string.
   *
   * @param map the map to convert
   * @return the generated string
   */
  @Override
  protected String toString(Map map) {
    String	result;
    String	msg;

    if (m_Conversion == null) {
      m_Conversion = new MapToString();
      m_Conversion.setSortKeys(m_SortKeys);
      m_Conversion.setIndentation(m_Indentation);
    }

    m_Conversion.setInput(map);
    msg = m_Conversion.convert();
    if (msg != null)
      throw new IllegalArgumentException(msg);
    result = "" + m_Conversion.getOutput();
    m_Conversion.cleanUp();

    return result;
  }
}
