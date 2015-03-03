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
 * CellRange.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet.cellfinder;

import java.util.ArrayList;
import java.util.Iterator;

import adams.core.QuickInfoHelper;
import adams.core.Range;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnRange;

/**
 <!-- globalinfo-start -->
 * Locator that locates cells using a rectangular range.
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
 * <pre>-rows &lt;adams.core.Range&gt; (property: rows)
 * &nbsp;&nbsp;&nbsp;The rows to locate.
 * &nbsp;&nbsp;&nbsp;default: first-last
 * </pre>
 * 
 * <pre>-columns &lt;adams.core.Range&gt; (property: columns)
 * &nbsp;&nbsp;&nbsp;The columns to locate.
 * &nbsp;&nbsp;&nbsp;default: first-last
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CellRange
  extends AbstractCellFinder {

  /** for serialization. */
  private static final long serialVersionUID = 3956527986917157099L;

  /** the rows. */
  protected Range m_Rows;

  /** the columns. */
  protected SpreadSheetColumnRange m_Columns;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Locator that locates cells using a rectangular range.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "rows", "rows",
	    new Range(Range.ALL));

    m_OptionManager.add(
	    "columns", "columns",
	    new SpreadSheetColumnRange(Range.ALL));
  }

  /**
   * Sets the rows to locate.
   *
   * @param value	the rows
   */
  public void setRows(Range value) {
    m_Rows = value;
    reset();
  }

  /**
   * Returns the rows to locate.
   *
   * @return		the rows
   */
  public Range getRows() {
    return m_Rows;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String rowsTipText() {
    return "The rows to locate.";
  }

  /**
   * Sets the columns to locate.
   *
   * @param value	the columns
   */
  public void setColumns(SpreadSheetColumnRange value) {
    m_Columns = value;
    reset();
  }

  /**
   * Returns the columns to locate.
   *
   * @return		the columns
   */
  public SpreadSheetColumnRange getColumns() {
    return m_Columns;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String columnsTipText() {
    return "The columns to locate.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "rows", m_Rows, "rows: ");
    result += QuickInfoHelper.toString(this, "columns", m_Columns, ", cols: ");
    
    return result;
  }

  /**
   * Performs the actual locating.
   * 
   * @param sheet	the sheet to locate the cells in
   * @return		the iterator over the locations
   */
  @Override
  protected Iterator<CellLocation> doFindCells(SpreadSheet sheet) {
    int[]	rows;
    int[]	cols;

    m_Rows.setMax(sheet.getRowCount());
    m_Columns.setSpreadSheet(sheet);

    rows = m_Rows.getIntIndices();
    cols = m_Columns.getIntIndices();
    
    if ((rows.length > 0) && (cols.length > 0))
      return new RangeIterator(rows, cols);
    else
      return new ArrayList<CellLocation>().iterator();
  }
}
