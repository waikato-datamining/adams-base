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
 * SpreadSheetInsertColumnPosition.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.SpreadSheetUtils;

/**
 <!-- globalinfo-start -->
 * Replaces the specified placeholder with a column string (e.g., BG) generated from the user-supplied column.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-placeholder &lt;java.lang.String&gt; (property: placeholder)
 * &nbsp;&nbsp;&nbsp;The placeholder to replace in the string with the generated cell location.
 * &nbsp;&nbsp;&nbsp;default: {C}
 * </pre>
 * 
 * <pre>-column &lt;int&gt; (property: column)
 * &nbsp;&nbsp;&nbsp;The 1-based column index to use for the cell location.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetInsertColumnPosition
  extends AbstractStringConversion {

  /** for serialization. */
  private static final long serialVersionUID = 3314208609415992655L;

  /** the placeholder to replace. */
  protected String m_Placeholder;

  /** the column to use (1-based). */
  protected int m_Column;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Replaces the specified placeholder with a column string (e.g., BG) generated from the user-supplied column.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "placeholder", "placeholder",
	    "{C}");

    m_OptionManager.add(
	    "column", "column",
	    1, 1, null);
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "placeholder", m_Placeholder, "placeholder: ");
    result += QuickInfoHelper.toString(this, "column", m_Column, ", col: ");

    return result;
  }

  /**
   * Sets the placeholder to replace with the cell location in the string 
   * passing through.
   *
   * @param value	the placeholder
   */
  public void setPlaceholder(String value) {
    if (value.length() > 0) {
      m_Placeholder = value;
      reset();
    }
    else {
      getLogger().severe("The placeholder cannot be an empty string!");
    }
  }

  /**
   * Returns the placeholder string to replace with the cell location in the 
   * string passing through.
   *
   * @return 		the placeholder
   */
  public String getPlaceholder() {
    return m_Placeholder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String placeholderTipText() {
    return "The placeholder to replace in the string with the generated cell location.";
  }

  /**
   * Sets the column index to use for the cell location.
   *
   * @param value	the index (1-based)
   */
  public void setColumn(int value) {
    if (value > 0) {
      m_Column = value;
      reset();
    }
    else {
      getLogger().severe("Column index must be greater than 0, provided: " + value);
    }
  }

  /**
   * Returns the column index in use for the cell location.
   *
   * @return 		the index (1-based)
   */
  public int getColumn() {
    return m_Column;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnTipText() {
    return "The 1-based column index to use for the cell location.";
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    String	result;
    String	cell;
    
    cell   = SpreadSheetUtils.getColumnPosition(m_Column - 1);
    result = ((String) m_Input).replace(m_Placeholder, cell);
    
    return result;
  }
}
