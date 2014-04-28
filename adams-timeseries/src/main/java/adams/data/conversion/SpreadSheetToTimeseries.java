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
 * SpreadSheetToTimeseries.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import java.util.Date;

import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.data.timeseries.Timeseries;
import adams.data.timeseries.TimeseriesPoint;

/**
 <!-- globalinfo-start -->
 * Turns a SpreadSheet object into a Timeseries.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-date-column &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: dateColumn)
 * &nbsp;&nbsp;&nbsp;The index of the date column in the spreadsheet to use a timestamp for the 
 * &nbsp;&nbsp;&nbsp;timeseries data points.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; apart from column names (case-sensitive), the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-value-column &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: valueColumn)
 * &nbsp;&nbsp;&nbsp;The index of the column with the timeseries values in the dataset.
 * &nbsp;&nbsp;&nbsp;default: 2
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; apart from column names (case-sensitive), the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetToTimeseries
  extends AbstractConversion {

  /** for serialization. */
  private static final long serialVersionUID = 5898182986872208113L;

  /** the date column to use. */
  protected SpreadSheetColumnIndex m_DateColumn;
  
  /** the value column to use. */
  protected SpreadSheetColumnIndex m_ValueColumn;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns a SpreadSheet object into a Timeseries.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "date-column", "dateColumn",
	    new SpreadSheetColumnIndex("1"));

    m_OptionManager.add(
	    "value-column", "valueColumn",
	    new SpreadSheetColumnIndex("2"));
  }

  /**
   * Sets the index of the date column to use as timestamp for timeseries.
   *
   * @param value	the index
   */
  public void setDateColumn(SpreadSheetColumnIndex value) {
    m_DateColumn = value;
    reset();
  }

  /**
   * Returns the index of the date column to use as timestamp for timeseries.
   *
   * @return		the index
   */
  public SpreadSheetColumnIndex getDateColumn() {
    return m_DateColumn;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String dateColumnTipText() {
    return "The index of the date column in the spreadsheet to use a timestamp for the timeseries data points.";
  }

  /**
   * Sets the index of the column with the timeseries values.
   *
   * @param value	the index
   */
  public void setValueColumn(SpreadSheetColumnIndex value) {
    m_ValueColumn = value;
    reset();
  }

  /**
   * Returns the index of the column with the timeseries values.
   *
   * @return		the index
   */
  public SpreadSheetColumnIndex getValueColumn() {
    return m_ValueColumn;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String valueColumnTipText() {
    return "The index of the column with the timeseries values in the dataset.";
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
    return Timeseries.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    Timeseries			result;
    SpreadSheet			input;
    Row				row;
    int				indexDate;
    int				indexValue;
    TimeseriesPoint		point;
    int				i;
    Date			timestamp;
    double			value;
    
    input = (SpreadSheet) m_Input;

    // determine column indices
    m_DateColumn.setData(input);
    indexDate = m_DateColumn.getIntIndex();
    if (indexDate == -1)
      throw new IllegalStateException("Failed to located date column: " + m_DateColumn.getIndex());
    m_ValueColumn.setData(input);
    indexValue = m_ValueColumn.getIntIndex();
    if (indexValue == -1)
      throw new IllegalStateException("Failed to located value column: " + m_ValueColumn.getIndex());
    
    result = new Timeseries((input.getName() != null ? (input.getName() + "-") : ("")) + input.getColumnName(indexValue));
    for (i = 0; i < input.getRowCount(); i++) {
      row = input.getRow(i);
      if (row.hasCell(indexDate) && !row.getCell(indexDate).isMissing() && row.hasCell(indexValue) && !row.getCell(indexValue).isMissing()) {
	timestamp = row.getCell(indexDate).toAnyDateType();
	value     = row.getCell(indexValue).toDouble();
	point     = new TimeseriesPoint(timestamp, value);
	result.add(point);
      }
    }
    
    return result;
  }
}
