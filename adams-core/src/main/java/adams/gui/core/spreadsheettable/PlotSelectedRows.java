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
 * PlotSelectedRows.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.core.spreadsheettable;

import adams.data.spreadsheet.SpreadSheet;
import adams.gui.core.SpreadSheetTable;

/**
 * Interface for plugins that plot selected rows.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface PlotSelectedRows
  extends SpreadSheetTablePopupMenuItem {

  /**
   * Returns the minimum number of rows that the plugin requires.
   *
   * @return		the minimum
   */
  public int minNumRows();

  /**
   * Returns the maximum number of rows that the plugin requires.
   *
   * @return		the maximum, -1 for none
   */
  public int maxNumRows();

  /**
   * Plots the specified row.
   *
   * @param table	the source table
   * @param sheet	the spreadsheet to use as basis
   * @param actRows	the actual rows in the spreadsheet
   * @param selRows	the selected rows in the table
   * @return		true if successful
   */
  public boolean plotSelectedRows(SpreadSheetTable table, SpreadSheet sheet, int[] actRows, int[] selRows);
}
