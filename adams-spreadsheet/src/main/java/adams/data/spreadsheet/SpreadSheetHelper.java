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

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

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

  /**
   * Compares the two cells.
   *
   * @param first	the first cell
   * @param second	the second cell
   * @return		null if the same, otherwise message on difference
   */
  protected static String compareCells(Cell first, Cell second) {
    // null?
    if ((first == null) && (second == null))
      return null;
    if (first == null)
      return "First cell is missing";
    else if (second == null)
      return "Second cell is missing";

    // missing flag?
    if (first.isMissing() && second.isMissing())
      return null;
    if (first.isMissing())
      return "First cell is missing";
    else if (second.isMissing())
      return "Second cell is missing";

    // different content?
    if (!first.getContent().equals(second.getContent()))
      return "Cells differs: '" + first.getContent() + "' != '" + second.getContent() + "'";

    return null;
  }

  /**
   * Compares two spreadsheets.
   *
   * @param first	the first spreadsheet
   * @param second	the second spreadsheet
   * @return		null if the same, otherwise message indicating
   * 			(first) difference
   */
  public static String compare(SpreadSheet first, SpreadSheet second) {
    int		x;
    int		y;
    String	msg;

    if (first.getColumnCount() != second.getColumnCount())
      return "Different number of columns: " + first.getColumnCount() + " != " + second.getColumnCount();
    if (first.getRowCount() != second.getRowCount())
      return "Different number of rows: " + first.getRowCount() + " != " + second.getRowCount();

    // header
    for (x = 0; x < first.getColumnCount(); x++) {
      msg = compareCells(first.getHeaderRow().getCell(x), second.getHeaderRow().getCell(x));
      if (msg != null)
	return "Header differs at #" + (x+1) + ": " + msg;
    }

    // data
    for (y = 0; y < first.getRowCount(); y++) {
      for (x = 0; x < first.getColumnCount(); x++) {
	msg = compareCells(first.getHeaderRow().getCell(x), second.getHeaderRow().getCell(x));
	if (msg != null)
	  return "Data differs at row " + (y+1) + "/col " + (x+1) + ": " + msg;
      }
    }

    return null;
  }

  /**
   * Turns the map into a spreadsheet.
   *
   * @param map		the map to convert
   * @param header	the header names, use null for default
   * @return		the generated spreadsheet
   */
  public static SpreadSheet mapToSpreadSheet(Map map, String[] header) {
    SpreadSheet		result;
    Row			row;
    List<String>	keys;

    result = new DefaultSpreadSheet();

    // header
    if ((header == null) || (header.length != 2))
      header = new String[]{"Key", "Value"};
    row = result.getHeaderRow();
    row.addCell("K").setContentAsString(header[0]);
    row.addCell("V").setContentAsString(header[1]);

    // data
    for (Object key: map.keySet()) {
      row = result.addRow();
      row.addCell("K").setContentAsString("" + key);
      row.addCell("V").setNative(map.get(key));
    }

    return result;
  }

  /**
   * Turns non-numeric cells into numeric ones. Missing cells can get skipped.
   * Booleans gets turned into 0/1 (false/true).
   * Date/time types get turned into numeric ones by using their Java epoch.
   * Strings (per column) get a 0-based index assigned in the order they appear.
   * Any other cell type get flagged as missing or, if provided, set to the unhandled value.
   *
   * @param sheet	the spreadsheet to convert
   * @param mappings	the mappings (empty or already filled from previous run):
   *                    col-index - (string - integer)
   * @param missing	the value to replace missing values with, null if to keep missing cells
   * @param unhandled	the value to use for replacing unhandled cell types
   * @return		the converted spreadsheet (copy)
   */
  public static SpreadSheet convertToNumeric(SpreadSheet sheet, Map<Integer,Map<String,Integer>> mappings, Double missing, double unhandled) {
    SpreadSheet		result;
    int			i;
    int			n;
    Row			row;
    Cell		cell;
    String		content;

    result = sheet.getClone();

    for (n = 0; n < result.getRowCount(); n++) {
      row = result.getRow(n);
      for (i = 0; i < result.getColumnCount(); i++) {
	if (!row.hasCell(i)) {
	  if (missing != null)
	    row.addCell(i).setContent(missing);
	  continue;
	}
	cell = row.getCell(i);
	if (cell.isNumeric())
	  continue;
	switch (cell.getContentType()) {
	  case MISSING:
	    if (missing != null)
	      cell.setContent(missing);
	    break;
	  case BOOLEAN:
	    cell.setContent(cell.toBoolean() ? 1 : 0);
	    break;
	  case TIME:
	  case TIMEMSEC:
	  case DATE:
	  case DATETIME:
	  case DATETIMEMSEC:
	    cell.setContent(cell.toAnyDateType().getTime());
	    break;
	  case STRING:
	    content = cell.getContent();
	    if (!mappings.containsKey(i))
	      mappings.put(i, new HashMap<>());
	    if (!mappings.get(i).containsKey(content))
	      mappings.get(i).put(content, mappings.get(i).size());
	    cell.setContent(mappings.get(i).get(content));
	    break;
	  default:
	    cell.setContent(unhandled);
	}
      }
    }

    return result;
  }
}
