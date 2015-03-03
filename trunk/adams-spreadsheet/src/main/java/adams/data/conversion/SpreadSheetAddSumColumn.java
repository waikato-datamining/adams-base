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
 * SpreadSheetAddSumColumn.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.core.Range;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;

/**
 <!-- globalinfo-start -->
 * Adds a column with 'sum' formulas for the specified rows.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-no-copy (property: noCopy)
 * &nbsp;&nbsp;&nbsp;If enabled, no copy of the spreadsheet is created before processing it.
 * </pre>
 * 
 * <pre>-rows &lt;adams.core.Range&gt; (property: rows)
 * &nbsp;&nbsp;&nbsp;The range of rows to add the 'sum' formula for; A range is a comma-separated 
 * &nbsp;&nbsp;&nbsp;list of single 1-based indices or sub-ranges of indices ('start-end'); '
 * &nbsp;&nbsp;&nbsp;inv(...)' inverts the range '...'; the following placeholders can be used 
 * &nbsp;&nbsp;&nbsp;as well: first, second, third, last_2, last_1, last
 * &nbsp;&nbsp;&nbsp;default: first-last
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetAddSumColumn
  extends AbstractInPlaceSpreadSheetConversion {

  /** for serialization. */
  private static final long serialVersionUID = 1320593539567099350L;

  /** the the column header. */
  protected String m_Header;
  
  /** the rows to add the sum to. */
  protected Range m_Rows;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Adds a column with 'sum' formulas for the specified rows.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "header", "header",
	    "Sum");

    m_OptionManager.add(
	    "rows", "rows",
	    new Range(Range.ALL));
  }

  /**
   * Sets the column header to use.
   *
   * @param value	the header
   */
  public void setHeader(String value) {
    m_Header = value;
    reset();
  }

  /**
   * Returns the column header in use.
   *
   * @return		the header
   */
  public String getHeader() {
    return m_Header;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String headerTipText() {
    return "The title of the formula column.";
  }

  /**
   * Sets the range of rows to add the sum for.
   *
   * @param value	the range
   */
  public void setRows(Range value) {
    m_Rows = value;
    reset();
  }

  /**
   * Returns the range of rows to add the sum for.
   *
   * @return		true range
   */
  public Range getRows() {
    return m_Rows;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rowsTipText() {
    return "The range of rows to add the 'sum' formula for.";
  }

  /**
   * Generates the new spreadsheet from the input.
   * 
   * @param input	the incoming spreadsheet
   * @return		the generated spreadsheet
   * @throws Exception	if conversion fails for some reason
   */
  @Override
  protected SpreadSheet convert(SpreadSheet input) throws Exception {
    SpreadSheet	result;
    Row		row;
    int		i;
    int[]	rows;
    String	from;
    String	to;
    
    if (m_NoCopy)
      result = input;
    else
      result = input.getClone();
    
    m_Rows.setMax(result.getRowCount());
    rows = m_Rows.getIntIndices();
    result.insertColumn(result.getColumnCount(), m_Header);
    
    for (i = 0; i < rows.length; i++) {
      from = SpreadSheet.getCellPosition(rows[i], 0);
      to   = SpreadSheet.getCellPosition(rows[i], result.getColumnCount() - 2);
      row  = result.getRow(rows[i]);
      row.addCell(result.getColumnCount() - 1).setContent("=SUM(" + from +  ":" + to + ")");
    }
    
    return result;
  }
}
