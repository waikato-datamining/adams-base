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
 * AbstractProcessRow.java
 * Copyright (C) 2015-2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.core.spreadsheettable;

import adams.core.option.AbstractOptionHandler;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.core.GUIHelper;
import adams.gui.core.TableRowRange;
import adams.gui.core.spreadsheettable.SpreadSheetTablePopupMenuItemHelper.TableState;

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
   * @param state	the table state
   * @return		null if passed, otherwise error message
   */
  protected String check(TableState state) {
    SpreadSheet 	sheet;

    if (state.table == null)
      return "No source table available!";
    if (state.actRow < 0)
      return "Negative row index!";
    sheet = state.table.toSpreadSheet(TableRowRange.ALL, true);
    if (state.actRow >= sheet.getRowCount())
      return "Row index too large: " + (state.actRow + 1) + " > " + sheet.getRowCount();
    return null;
  }

  /**
   * Processes the specified row.
   *
   * @param state	the table state
   * @return		true if successful
   */
  protected abstract boolean doProcessRow(TableState state);

  /**
   * Processes the specified row.
   *
   * @param state	the table state
   * @return		true if successful
   */
  public boolean processRow(TableState state) {
    boolean	result;
    String	error;

    error = check(state);
    result = (error == null);
    if (result)
      result = doProcessRow(state);
    else
      GUIHelper.showErrorMessage(state.table, "Failed to process row #" + (state.actRow +1) + "\n" + error);

    return result;
  }
}
