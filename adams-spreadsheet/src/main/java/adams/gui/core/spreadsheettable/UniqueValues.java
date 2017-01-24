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
 * UniqueValues.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.gui.core.spreadsheettable;

import adams.core.Utils;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetUtils;
import adams.gui.core.GUIHelper;
import adams.gui.core.SpreadSheetTable;
import adams.gui.core.TableRowRange;

/**
 * Displays all the unique values in the column.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class UniqueValues
  extends AbstractProcessColumn {

  private static final long serialVersionUID = 3101728458818516005L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Displays all the unique values in the column.";
  }

  /**
   * Returns the name for the menu item.
   *
   * @return            the name
   */
  @Override
  public String getMenuItem() {
    return "Unique values";
  }

  /**
   * Processes the specified column.
   *
   * @param table	the source table
   * @param sheet	the spreadsheet to use as basis
   * @param column	the column in the spreadsheet
   * @return		true if successful
   */
  @Override
  protected boolean doProcessColumn(SpreadSheetTable table, SpreadSheet sheet, int column) {
    String[]	values;

    values = SpreadSheetUtils.getColumn(table.toSpreadSheet(TableRowRange.VISIBLE, true), column, true, true);
    GUIHelper.showInformationMessage(
      GUIHelper.getParentComponent(table),
      Utils.flatten(values, "\n"),
      "Unique values of column #" + (column+1) + "/" + sheet.getColumnName(column));

    return true;
  }
}
