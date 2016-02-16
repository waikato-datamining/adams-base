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
 * SpreadSheetHelper.java
 * Copyright (C) 2014-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet;

import java.util.Hashtable;

/**
 * A helper class for spreadsheet operations.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetHelper
  extends SpreadSheetUtils {

  /**
   * Appends two spreadsheets.
   * 
   * @param first	the first spreadsheet
   * @param second	the second spreadsheet
   * @param noCopy	whether to work with a copy of the first spreadsheet
   * 			rather than appending the second one directly to it
   * @return		the combined spreadsheet
   */
  public static SpreadSheet append(SpreadSheet first, SpreadSheet second, boolean noCopy) {
    int				i;
    int				n;
    Row				headerSecond;
    Row				headerFirst;
    Row				row;
    Row				newRow;
    Integer			index;
    String			key;
    Hashtable<String,Integer>	headerIndex;

    if (!noCopy)
      first = first.getClone();
    headerFirst  = first.getHeaderRow();
    headerSecond = second.getHeaderRow();
    headerIndex  = new Hashtable<String,Integer>();
    // current header
    for (i = 0; i < headerFirst.getCellCount(); i++)
      headerIndex.put(headerFirst.getCell(i).getContent(), i);
    // extend header, if necessary
    for (i = 0; i < headerSecond.getCellCount(); i++) {
      key = headerSecond.getCell(i).getContent();
      if (!headerIndex.containsKey(key)) {
	headerFirst.addCell("" + headerFirst.getCellCount()).setContent(key);
	headerIndex.put(key, headerFirst.getCellCount() - 1);
      }
    }
    for (n = 0; n < second.getRowCount(); n++) {
      row    = second.getRow(n);
      newRow = first.addRow();
      for (i = 0; i < headerSecond.getCellCount(); i++) {
	index = headerIndex.get(headerSecond.getCell(i).getContent());
	if (index == null)
	  continue;
	if (row.hasCell(headerSecond.getCellKey(i)))
	  newRow.addCell(headerFirst.getCellKey(index)).assign(row.getCell(headerSecond.getCellKey(i)));
      }
    }
    
    return first;
  }
}
