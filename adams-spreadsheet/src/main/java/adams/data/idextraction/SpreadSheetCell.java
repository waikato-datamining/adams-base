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
 * SpreadSheetCell.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.data.idextraction;

import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheetColumnIndex;

/**
 * Returns the cell value of the spreadsheet row.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SpreadSheetCell
  extends AbstractIDExtractor {

  private static final long serialVersionUID = 6130414784797102811L;

  /** the column index. */
  protected SpreadSheetColumnIndex m_Column;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Returns the value of the specified cell of the spreadsheet row coming through.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "column", "column",
      new SpreadSheetColumnIndex(SpreadSheetColumnIndex.FIRST));
  }

  /**
   * Sets the column to get the ID from.
   *
   * @param value	the column
   */
  public void setColumn(SpreadSheetColumnIndex value) {
    m_Column = value;
    reset();
  }

  /**
   * Returns the column to get the ID from.
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
   * 			displaying in the GUI or for listing the options.
   */
  public String columnTipText() {
    return "The column of the cell to get the ID from.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "column", m_Column);
  }

  /**
   * Checks whether the data type is handled.
   *
   * @param obj		the object to check
   * @return		true if handled
   */
  public boolean handles(Object obj) {
    return (obj instanceof Row);
  }

  /**
   * Extracts the ID from the object.
   *
   * @param obj		the object to process
   * @return		the extracted group
   */
  @Override
  protected String doExtractID(Object obj) {
    Row		row;
    Cell	cell;

    row = (Row) obj;
    m_Column.setSpreadSheet(row.getOwner());
    if (m_Column.getIntIndex() == -1)
      throw new IllegalStateException("Failed to locate column: " + m_Column);

    cell = row.getCell(m_Column.getIntIndex());
    if ((cell != null) && !cell.isMissing())
      return cell.getContent();
    else
      return null;
  }
}
