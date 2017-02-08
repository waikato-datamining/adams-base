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
 * SpreadSheet.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.debug.inspectionhandler;

import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Row;
import nz.ac.waikato.cms.locator.ClassLocator;

import java.util.Hashtable;

/**
 * Provides further insight into spreadsheets.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheet
  extends AbstractInspectionHandler {

  /**
   * Checks whether the handler can handle the specified class.
   *
   * @param cls		the class to check
   * @return		true if the handler can handle this type of object
   */
  @Override
  public boolean handles(Class cls) {
    return 
	   ClassLocator.hasInterface(adams.data.spreadsheet.SpreadSheet.class, cls)
	|| ClassLocator.hasInterface(Row.class, cls)
	|| ClassLocator.isSubclass(Cell.class, cls);
  }

  /**
   * Returns further inspection values.
   *
   * @param obj		the object to further inspect
   * @return		the named inspected values
   */
  @Override
  public Hashtable<String,Object> inspect(Object obj) {
    Hashtable<String,Object>		result;
    adams.data.spreadsheet.SpreadSheet	sheet;
    Row					row;
    Cell				cell;
    int					index;

    result = new Hashtable<String,Object>();

    if (obj instanceof adams.data.spreadsheet.SpreadSheet) {
      sheet = (adams.data.spreadsheet.SpreadSheet) obj;
      result.put("Sheet.Full",    sheet.toString());
      result.put("Sheet.Header",  sheet.getHeader().toString());
      result.put("Sheet.Rows",    sheet.getRowCount());
      result.put("Sheet.Columns", sheet.getColumnCount());
    }
    else if (obj instanceof Row) {
      row  = (Row) obj;
      result.put("Row", row.toString());
    }
    else if (obj instanceof Cell) {
      cell  = ((Cell) obj);
      index = cell.index();
      result.put("Name",    cell.getSpreadSheet().getHeaderRow().getCell(index).getContent());
      result.put("Index",   index);
      result.put("Type",    cell.getContentType());
      result.put("Content", cell.getContent());
    }

    return result;
  }
}
