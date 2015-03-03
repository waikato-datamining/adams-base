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
 * SingleCell.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet.cellfinder;

import java.util.ArrayList;
import java.util.Iterator;

import adams.core.Index;
import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;

/**
 <!-- globalinfo-start -->
 * Simple locator that just locates a single cell.
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
 * <pre>-row &lt;adams.core.Index&gt; (property: row)
 * &nbsp;&nbsp;&nbsp;The row to locate.
 * &nbsp;&nbsp;&nbsp;default: first
 * </pre>
 * 
 * <pre>-column &lt;adams.core.Index&gt; (property: column)
 * &nbsp;&nbsp;&nbsp;The column to locate.
 * &nbsp;&nbsp;&nbsp;default: first
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SingleCell
  extends AbstractCellFinder {

  /** for serialization. */
  private static final long serialVersionUID = 7552127288975155281L;

  /** the row. */
  protected Index m_Row;

  /** the column. */
  protected SpreadSheetColumnIndex m_Column;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Simple locator that just locates a single cell.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "row", "row",
	    new Index(Index.FIRST));

    m_OptionManager.add(
	    "column", "column",
	    new SpreadSheetColumnIndex(Index.FIRST));
  }

  /**
   * Sets the row to locate.
   *
   * @param value	the row
   */
  public void setRow(Index value) {
    m_Row = value;
    reset();
  }

  /**
   * Returns the row to locate.
   *
   * @return		the row
   */
  public Index getRow() {
    return m_Row;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String rowTipText() {
    return "The row to locate.";
  }

  /**
   * Sets the column to locate.
   *
   * @param value	the column
   */
  public void setColumn(SpreadSheetColumnIndex value) {
    m_Column = value;
    reset();
  }

  /**
   * Returns the column to locate.
   *
   * @return		the column
   */
  public SpreadSheetColumnIndex getColumn() {
    return m_Column;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String columnTipText() {
    return "The column to locate.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "row", m_Row, "row: ");
    result += QuickInfoHelper.toString(this, "column", m_Column, ", col: ");
    
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
    ArrayList<CellLocation>	result;
    int				row;
    int				col;
    
    result = new ArrayList<CellLocation>();

    m_Row.setMax(sheet.getRowCount());
    m_Column.setSpreadSheet(sheet);

    row = m_Row.getIntIndex();
    col = m_Column.getIntIndex();
    
    if ((row != -1) && (col != -1))
      result.add(new CellLocation(row, col));
    
    return result.iterator();
  }
}
