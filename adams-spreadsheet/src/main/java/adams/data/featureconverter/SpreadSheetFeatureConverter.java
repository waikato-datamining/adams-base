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
 * SpreadSheetFeatureConverter.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.featureconverter;

import java.util.List;

import adams.data.report.DataType;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.DataRowType;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;

/**
 * Generates features in spreadsheet format.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetFeatureConverter
  extends AbstractFeatureConverter<SpreadSheet,Row> {

  /** for serialization. */
  private static final long serialVersionUID = -1696817128027564877L;

  /** the data row type to use. */
  protected DataRowType m_DataRowType;

  /** the type of spreadsheet to use. */
  protected SpreadSheet m_SpreadSheetType;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns the features into spreadsheet format.";
  }
  
  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "data-row-type", "dataRowType",
	    getDefaultDataRowType());

    m_OptionManager.add(
	    "spreadsheet-type", "spreadSheetType",
	    getDefaultSpreadSheetType());
  }

  /**
   * Returns the default row type.
   * 
   * @return		the default
   */
  protected DataRowType getDefaultDataRowType() {
    return DataRowType.DENSE;
  }
  
  /**
   * Sets the type of data row to use.
   *
   * @param value	the type
   */
  public void setDataRowType(DataRowType value) {
    m_DataRowType = value;
    reset();
  }

  /**
   * Returns the type of data row to use.
   *
   * @return		the type
   */
  public DataRowType getDataRowType() {
    return m_DataRowType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String dataRowTypeTipText() {
    return "The type of row to use for the data.";
  }

  /**
   * Returns the default spreadsheet to use.
   * 
   * @return		the spreadsheet
   */
  protected SpreadSheet getDefaultSpreadSheetType() {
    return new SpreadSheet();
  }
  
  /**
   * Sets the type of spreadsheet to use.
   *
   * @param value	the type
   */
  public void setSpreadSheetType(SpreadSheet value) {
    m_SpreadSheetType = value;
    reset();
  }

  /**
   * Returns the type of spreadsheet to use.
   *
   * @return		the type
   */
  public SpreadSheet getSpreadSheetType() {
    return m_SpreadSheetType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String spreadSheetTypeTipText() {
    return "The type of spreadsheet to use for the data.";
  }

  /**
   * Performs the actual generation of the header data structure using the 
   * names and data types.
   * 
   * @param names	the attribute names
   * @param types	the attribute types
   * @return		the dataset structure
   */
  @Override
  protected SpreadSheet doGenerateHeader(List<String> names, List<DataType> types) {
    SpreadSheet	result;
    Row		row;
    
    result = m_SpreadSheetType.getClone();
    result.setDataRowClass(m_DataRowType.getRowClass());
    result.setName(getClass().getName());
    
    row = result.getHeaderRow();
    for (String name: names)
      row.addCell("" + row.getCellCount()).setContent(name);
    
    return result;
  }

  /**
   * Performs the actual generation of a row from the raw data.
   * 
   * @param data	the data of the row, elements can be null (= missing)
   * @return		the dataset structure
   */
  @Override
  protected Row doGenerateRow(List<Object> data) {
    Row		result;
    int		i;
    Object	obj;
    Cell	cell;
    
    result = m_Header.addRow();
    
    for (i = 0; i < data.size(); i++) {
      obj  = data.get(i);
      cell = result.addCell(i);
      if (obj == null) {
	cell.setMissing();
	continue;
      }
      switch (m_Types.get(i)) {
	case BOOLEAN:
	  cell.setContent((Boolean) obj);
	  break;
	case NUMERIC:
	  if (obj instanceof Integer)
	    cell.setContent((Integer) obj);
	  else if (obj instanceof Long)
	    cell.setContent((Long) obj);
	  else
	    cell.setContent(((Number) obj).doubleValue());
	  break;
	case STRING:
	case UNKNOWN:
	  cell.setContentAsString(obj.toString());
	  break;
      }
    }
    
    return result;
  }
}
