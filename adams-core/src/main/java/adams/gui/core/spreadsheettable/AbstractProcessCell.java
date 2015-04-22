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
 * AbstractProcessCell.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.gui.core.spreadsheettable;

import adams.core.option.AbstractOptionHandler;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.core.GUIHelper;
import adams.gui.core.SpreadSheetTable;

/**
 * Ancestor for plugins that process a cell.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractProcessCell
  extends AbstractOptionHandler
  implements ProcessCell {

  private static final long serialVersionUID = -1050881505327794503L;

  /**
   * For sorting the menu items.
   *
   * @param o       the other item
   * @return        -1 if less than, 0 if equal, +1 if larger than this
   *                menu item name
   */
  @Override
  public int compareTo(SpreadSheetTablePopupMenuItem o) {
    return getMenuItem().compareTo(o.getMenuItem());
  }

  /**
   * Returns the name of the icon.
   *
   * @return            the name, null if none available
   */
  public String getIconName() {
    return null;
  }

  /**
   * Hook method for checks before attempting processing.
   *
   * @param table	the source table
   * @param sheet	the spreadsheet to use as basis
   * @param row         the row in the spreadsheet
   * @param column	the column in the spreadsheet
   * @return		null if passed, otherwise error message
   */
  protected String check(SpreadSheetTable table, SpreadSheet sheet, int row, int column) {
    if (table == null)
      return "No source table available!";
    if (sheet == null)
      return "No spreadsheet available!";
    if (row < 0)
      return "Negative row index!";
    if (row >= sheet.getRowCount())
      return "Row index too large: " + (row + 1) + " > " + sheet.getRowCount();
    if (column < 0)
      return "Negative column index!";
    if (column >= sheet.getColumnCount())
      return "Column index too large: " + (column + 1) + " > " + sheet.getColumnCount();
    return null;
  }

  /**
   * Processes the specified cell.
   *
   * @param table	the source table
   * @param sheet	the spreadsheet to use as basis
   * @param row         the row in the spreadsheet
   * @param column	the column in the spreadsheet
   * @return		true if successful
   */
  protected abstract boolean doProcessCell(SpreadSheetTable table, SpreadSheet sheet, int row, int column);

  /**
   * Processes the specified column.
   *
   * @param table	the source table
   * @param sheet	the spreadsheet to use as basis
   * @param row         the row in the spreadsheet
   * @param column	the column in the spreadsheet
   * @return		true if successful
   */
  public boolean processCell(SpreadSheetTable table, SpreadSheet sheet, int row, int column) {
    boolean	result;
    String	error;

    error = check(table, sheet, row, column);
    result = (error == null);
    if (result)
      result = doProcessCell(table, sheet, row, column);
    else
      GUIHelper.showErrorMessage(table, "Failed to process cell " + SpreadSheet.getCellPosition(row, column) + "\n" + error);

    return result;
  }
}
