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
 * AbstractBaseTableModel.java
 * Copyright (C) 2009-2012 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;

import javax.swing.table.AbstractTableModel;

import adams.core.Utils;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetSupporter;

/**
 * Abstract ancestor for table models. The models are automatically sortable.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractBaseTableModel
  extends AbstractTableModel 
  implements SpreadSheetSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 1379439060928152100L;

  /**
   * Returns the content as spreadsheet.
   * 
   * @return		the content
   */
  public SpreadSheet toSpreadSheet() {
    SpreadSheet 	result;
    Row			row;
    int			i;
    int			n;
    Object		value;
    
    result = new SpreadSheet();
    
    // header
    row = result.getHeaderRow();
    for (i = 0; i < getColumnCount(); i++)
      row.addCell("" + i).setContent(getColumnName(i));
    
    // data
    for (n = 0; n < getRowCount(); n++) {
      row = result.addRow("" + result.getRowCount());
      for (i = 0; i < getColumnCount(); i++) {
	value = getValueAt(n, i);
	if (value == null)
	  row.addCell("" + i).setContent(SpreadSheet.MISSING_VALUE);
	else if (value.getClass().isArray())
	  row.addCell("" + i).setContent(Utils.arrayToString(value));
	else if (value instanceof Integer)
	  row.addCell("" + i).setContent((Integer) value);
	else if (value instanceof Double)
	  row.addCell("" + i).setContent((Double) value);
	else
	  row.addCell("" + i).setContent("" + value);
      }
    }
    
    return result;
  }
  
  /**
   * Returns the table content as spreadsheet.
   * 
   * @return		the content
   */
  @Override
  public String toString() {
    return toSpreadSheet().toString();
  }
}
