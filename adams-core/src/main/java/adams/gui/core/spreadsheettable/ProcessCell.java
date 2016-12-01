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
 * ProcessCell.java
 * Copyright (C) 2015-2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.core.spreadsheettable;

import adams.data.spreadsheet.SpreadSheet;
import adams.gui.core.SpreadSheetTable;

/**
 * Interface for plugins that process a cell.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface ProcessCell
  extends SpreadSheetTablePopupMenuItem {

  /**
   * Processes the specified cell.
   *
   * @param table	the source table
   * @param sheet	the spreadsheet to use as basis
   * @param actRow 	the actual row in the spreadsheet
   * @param selRow 	the selected row in the table
   * @param column	the column in the spreadsheet
   * @return		true if successful
   */
  public boolean processCell(SpreadSheetTable table, SpreadSheet sheet, int actRow, int selRow, int column);
}
