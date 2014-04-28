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
 * RDataHelper.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

import java.util.Arrays;
import java.util.HashSet;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPDouble;
import org.rosuda.REngine.REXPInteger;
import org.rosuda.REngine.REXPList;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REXPString;
import org.rosuda.REngine.REXPVector;
import org.rosuda.REngine.RList;

import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Cell.ContentType;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;

/**
 * Helper class for R-related stuff.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RDataHelper {

  /**
   * Turns a spreadsheet into an R dataframe.
   * 
   * @param sheet	the sheet to convert
   * @return		the dataframe object
   */
  public static REXP spreadsheetToDataframe(SpreadSheet sheet) throws REXPMismatchException {
    RList 	atts;
    int 	col;
    int		row;
    ContentType	type;
    int[]	i;
    double[]	d;
    String[]	s;
    Cell 	cell;
    
    atts = new RList(sheet.getColumnCount(), true);
    for (col = 0; col < sheet.getColumnCount(); col++) {
      // determine type of column
      type = sheet.getContentType(col);
      if (type == null) {
	if (sheet.isNumeric(col))
	  type = ContentType.DOUBLE;
	else
	  type = ContentType.STRING;
      }
      
      // add dataframe columns
      switch (type) {
	case LONG:
	  i = new int[sheet.getRowCount()];
	  for (row = 0; row < sheet.getRowCount(); row++) {
	    cell = sheet.getCell(row, col);
	    if ((cell == null) || cell.isMissing())
	      i[row] = REXPInteger.NA;
	    else
	      i[row] = cell.toLong().intValue();
	  }
	  atts.put(sheet.getHeaderRow().getCell(col).getContent(), new REXPInteger(i));
	  break;
	case DOUBLE:
	  d = new double[sheet.getRowCount()];
	  for (row = 0; row < sheet.getRowCount(); row++) {
	    cell = sheet.getCell(row, col);
	    if ((cell == null) || cell.isMissing())
	      d[row] = REXPDouble.NA;
	    else
	      d[row] = cell.toDouble();
	  }
	  atts.put(sheet.getHeaderRow().getCell(col).getContent(), new REXPDouble(d));
	  break;
	default:
	  s = new String[sheet.getRowCount()];
	  for (row = 0; row < sheet.getRowCount(); row++) {
	    cell = sheet.getCell(row, col);
	    if ((cell == null) || cell.isMissing())
	      s[row] = "";  // TODO null?
	    else
	      s[row] = cell.getContent();
	  }
	  atts.put(sheet.getHeaderRow().getCell(col).getContent(), new REXPString(s));
	  break;
      }
    }
    
    return REXP.createDataFrame(atts);
  }
  
  /**
   * Converts the R dataframe into a spreadsheet.
   * 
   * @param dframe	the dataframe to convert
   * @return		the generated spreadsheet
   */
  public static SpreadSheet dataframeToSpreadsheet(REXP dframe) throws REXPMismatchException {
    return dataframeToSpreadsheet(dframe, null);
  }
  
  /**
   * Converts the R dataframe into a spreadsheet.
   * 
   * @param dframe	the dataframe to convert
   * @param columns	the list of columns to retrieve, null to retrieve all
   * @return		the generated spreadsheet
   */
  public static SpreadSheet dataframeToSpreadsheet(REXP dframe, String[] columns) throws REXPMismatchException {
    SpreadSheet		result;
    RList		list;
    RList		elements;
    REXPVector		dcol;
    Row			row;
    int			c;
    int			r;
    int[]		i;
    double[]		d;
    HashSet<String>	cols;
    String		colName;
    
    result = new SpreadSheet();
    list   = dframe.asList();
    
    if (columns == null)
      cols = null;
    else
      cols = new HashSet<String>(Arrays.asList(columns));
    
    // header
    row = result.getHeaderRow();
    for (Object key: list.keySet()) {
      colName = key.toString();
      if (cols != null) {
	if (!cols.contains(colName))
	  continue;
      }
      row.addCell(colName).setContent(colName);
    }
    
    // data
    for (Object key: list.keySet()) {
      colName = key.toString();
      if (cols != null) {
	if (!cols.contains(colName))
	  continue;
      }
      dcol = (REXPVector) list.get(key);
      c    = result.getHeaderRow().indexOf(colName);
      while (result.getRowCount() < dcol.length())
	result.addRow();
      if (dcol.isInteger()) {
	i = dcol.asIntegers();
	for (r = 0; r < i.length; r++)
	  result.getCell(r, c).setContent(i[r]);
      }
      else if (dcol.isNumeric()) {
	d = dcol.asDoubles();
	for (r = 0; r < d.length; r++)
	  result.getCell(r, c).setContent(d[r]);
      }
      else if (dcol.isList()) {
	elements = dcol.asList();
	for (r = 0; r < elements.size(); r++)
	  result.getCell(r, c).setContent(objectToString(elements.get(r)));
      }
    }
    
    return result;
  }
  
  /**
   * Turns an {@link RList} into a string.
   * 
   * @param list	the list to convert
   * @return		the generated string
   */
  protected static String listToString(RList list) {
    StringBuilder	result;
    int			i;
    
    result = new StringBuilder();
    
    for (i = 0; i < list.size(); i++) {
      if (i > 0)
	result.append(",");
      if (list.get(i) instanceof RList) {
	result.append("{");
	result.append(objectToString((RList) list.get(i)));
	result.append("}");
      }
      else {
	result.append("" + objectToString(list.get(i)));
      }
    }
    
    return result.toString();
  }
  
  /**
   * Turns an R object into a string.
   * 
   * @param obj		the object to turn into a string
   * @return		the generated string
   */
  public static String objectToString(Object obj) {
    if (obj instanceof RList)
      return listToString((RList) obj);
    else if (obj instanceof REXPInteger)
      return Utils.arrayToString(((REXPInteger) obj).asIntegers());
    else if (obj instanceof REXPDouble)
      return Utils.arrayToString(((REXPDouble) obj).asDoubles());
    else if (obj instanceof REXPString)
      return Utils.arrayToString(((REXPString) obj).asStrings());
    else if (obj instanceof REXPList)
      return listToString(((REXPList) obj).asList());
    else
      return obj.toString();
  }
}
