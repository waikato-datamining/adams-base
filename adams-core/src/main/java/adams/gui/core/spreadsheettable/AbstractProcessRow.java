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
 * AbstractProcessRow.java
 * Copyright (C) 2015-2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.core.spreadsheettable;

import adams.core.option.AbstractOptionHandler;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.core.GUIHelper;
import adams.gui.core.SpreadSheetTable;

/**
 * Ancestor for plugins that process a row.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractProcessRow
  extends AbstractOptionHandler
  implements ProcessRow {

  private static final long serialVersionUID = 7979833588446267882L;

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
   * @param row	the row in the spreadsheet
   * @return		null if passed, otherwise error message
   */
  protected String check(SpreadSheetTable table, SpreadSheet sheet, int row) {
    if (table == null)
      return "No source table available!";
    if (sheet == null)
      return "No spreadsheet available!";
    if (row < 0)
      return "Negative row index!";
    if (row >= sheet.getRowCount())
      return "Row index too large: " + (row + 1) + " > " + sheet.getRowCount();
    return null;
  }

  /**
   * Processes the specified row.
   *
   * @param table	the source table
   * @param sheet	the spreadsheet to use as basis
   * @param actRow	the actual row in the spreadsheet
   * @param selRow	the selected row in the table
   * @return		true if successful
   */
  protected abstract boolean doProcessRow(SpreadSheetTable table, SpreadSheet sheet, int actRow, int selRow);

  /**
   * Processes the specified row.
   *
   * @param table	the source table
   * @param sheet	the spreadsheet to use as basis
   * @param actRow	the actual row in the spreadsheet
   * @param selRow	the selected row in the table
   * @return		true if successful
   */
  public boolean processRow(SpreadSheetTable table, SpreadSheet sheet, int actRow, int selRow) {
    boolean	result;
    String	error;

    error = check(table, sheet, actRow);
    result = (error == null);
    if (result)
      result = doProcessRow(table, sheet, actRow, selRow);
    else
      GUIHelper.showErrorMessage(table, "Failed to process row #" + (actRow +1) + "\n" + error);

    return result;
  }
}
