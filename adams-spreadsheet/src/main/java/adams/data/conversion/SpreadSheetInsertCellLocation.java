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
 * SpreadSheetInsertCellLocation.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.SpreadSheet;

/**
 <!-- globalinfo-start -->
 * Replaces the specified placeholder with a cell location generated from the user-supplied row and column.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-placeholder &lt;java.lang.String&gt; (property: placeholder)
 * &nbsp;&nbsp;&nbsp;The placeholder to replace in the string with the generated cell location.
 * &nbsp;&nbsp;&nbsp;default: {C}
 * </pre>
 * 
 * <pre>-row &lt;int&gt; (property: row)
 * &nbsp;&nbsp;&nbsp;The 1-based row index to use for the cell location.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
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
public class SpreadSheetInsertCellLocation
  extends AbstractStringConversion {

  /** for serialization. */
  private static final long serialVersionUID = 3314208609415992655L;

  /** the placeholder to replace. */
  protected String m_Placeholder;
  
  /** the row to use (1-based). */
  protected int m_Row;
  
  /** the column to use (1-based). */
  protected int m_Column;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Replaces the specified placeholder with a cell location generated from the user-supplied row and column.";
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
	    "row", "row",
	    1, 1, null);

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
    result += QuickInfoHelper.toString(this, "row", m_Row, ", row: ");
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
   * Sets the row index to use for the cell location.
   *
   * @param value	the index (1-based)
   */
  public void setRow(int value) {
    if (value > 0) {
      m_Row = value;
      reset();
    }
    else {
      getLogger().severe("Row index must be greater than 0, provided: " + value);
    }
  }

  /**
   * Returns the row index in use for the cell location.
   *
   * @return 		the index (1-based)
   */
  public int getRow() {
    return m_Row;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rowTipText() {
    return "The 1-based row index to use for the cell location.";
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
    
    cell   = SpreadSheet.getCellPosition(m_Row - 1, m_Column - 1);
    result = ((String) m_Input).replace(m_Placeholder, cell);
    
    return result;
  }
}
