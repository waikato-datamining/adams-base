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
 * SpreadSheetToMap.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.core.ClassLocator;
import adams.core.base.BaseClassname;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;

import java.util.HashMap;
import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Turns two columns (key and value) of a spreadsheet into a map object of the specified type.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-key-column &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: keyColumn)
 * &nbsp;&nbsp;&nbsp;The column in the spreadsheet to act as the 'key' for the values.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 * 
 * <pre>-value-column &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: valueColumn)
 * &nbsp;&nbsp;&nbsp;The column in the spreadsheet to stores the 'values'.
 * &nbsp;&nbsp;&nbsp;default: 2
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 * 
 * <pre>-map-class &lt;adams.core.base.BaseClassname&gt; (property: mapClass)
 * &nbsp;&nbsp;&nbsp;The type of map to instantiate and fill.
 * &nbsp;&nbsp;&nbsp;default: java.util.HashMap
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetToMap
  extends AbstractConversion {

  /** for serialization. */
  private static final long serialVersionUID = 4890225060389916155L;

  /** the key colum. */
  protected SpreadSheetColumnIndex m_KeyColumn;

  /** the value colum. */
  protected SpreadSheetColumnIndex m_ValueColumn;

  /** the map class to use. */
  protected BaseClassname m_MapClass;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns two columns (key and value) of a spreadsheet into a map object of the specified type.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "key-column", "keyColumn",
      new SpreadSheetColumnIndex("1"));

    m_OptionManager.add(
      "value-column", "valueColumn",
      new SpreadSheetColumnIndex("2"));

    m_OptionManager.add(
      "map-class", "mapClass",
      new BaseClassname(HashMap.class));
  }

  /**
   * Sets the key column.
   *
   * @param value	the column
   */
  public void setKeyColumn(SpreadSheetColumnIndex value) {
    m_KeyColumn = value;
    reset();
  }

  /**
   * Returns the key column.
   *
   * @return		the column
   */
  public SpreadSheetColumnIndex getKeyColumn() {
    return m_KeyColumn;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String keyColumnTipText() {
    return "The column in the spreadsheet to act as the 'key' for the values.";
  }

  /**
   * Sets the value column.
   *
   * @param value	the column
   */
  public void setValueColumn(SpreadSheetColumnIndex value) {
    m_ValueColumn = value;
    reset();
  }

  /**
   * Returns the value column.
   *
   * @return		the column
   */
  public SpreadSheetColumnIndex getValueColumn() {
    return m_ValueColumn;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String valueColumnTipText() {
    return "The column in the spreadsheet to stores the 'values'.";
  }

  /**
   * Sets the map class.
   *
   * @param value	the map class
   */
  public void setMapClass(BaseClassname value) {
    if (ClassLocator.hasInterface(Map.class, value.classValue())) {
      m_MapClass = value;
      reset();
    }
    else {
      getLogger().warning("Class '" + value + "' does not implement the '" + Map.class.getName() + "' interface!");
    }
  }

  /**
   * Returns the map class.
   *
   * @return		the map class
   */
  public BaseClassname getMapClass() {
    return m_MapClass;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String mapClassTipText() {
    return "The type of map to instantiate and fill.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return SpreadSheet.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return m_MapClass.classValue();
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    SpreadSheet sheet;
    Map		result;
    int		colKey;
    int		colValue;

    result = (Map) m_MapClass.classValue().newInstance();
    sheet  = (SpreadSheet) m_Input;
    m_KeyColumn.setData(sheet);
    colKey = m_KeyColumn.getIntIndex();
    if (colKey == -1)
      throw new Exception("Key column '" + m_KeyColumn + "' not found!");
    m_ValueColumn.setData(sheet);
    colValue = m_ValueColumn.getIntIndex();
    if (colValue == -1)
      throw new Exception("Value column '" + m_ValueColumn + "' not found!");

    for (Row row: sheet.rows()) {
      if (row.hasCell(colKey) && !row.getCell(colKey).isMissing()) {
	if (row.hasCell(colValue) && !row.getCell(colValue).isMissing()) {
	  result.put(row.getCell(colKey).getNative(), row.getCell(colValue).getNative());
	}
      }
    }

    return result;
  }
}
