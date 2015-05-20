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
 * SpreadSheetTimeseriesReader.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.timeseries.Timeseries;
import adams.data.timeseries.TimeseriesPoint;

/**
 <!-- globalinfo-start -->
 * Reads timeseries containers from columns of a spreadsheet.<br>
 * A new container is started, whenever the value of the ID column changes (hence you need to ensure that the data is ordered on this column).<br>
 * However, it is not required to have an ID column present. In this case, all of the data gets added to the same timeseries.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-input &lt;adams.core.io.PlaceholderFile&gt; (property: input)
 * &nbsp;&nbsp;&nbsp;The file to read and turn into a container.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-create-dummy-report &lt;boolean&gt; (property: createDummyReport)
 * &nbsp;&nbsp;&nbsp;If true, then a dummy report is created if none present.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-reader &lt;adams.data.io.input.SpreadSheetReader&gt; (property: reader)
 * &nbsp;&nbsp;&nbsp;The reader to use for reading the spreadsheet file.
 * &nbsp;&nbsp;&nbsp;default: adams.data.io.input.CsvSpreadSheetReader
 * </pre>
 * 
 * <pre>-column-id &lt;java.lang.String&gt; (property: columnID)
 * &nbsp;&nbsp;&nbsp;The name of the (optional) column containing the ID that distinguishes the 
 * &nbsp;&nbsp;&nbsp;timeseries.
 * &nbsp;&nbsp;&nbsp;default: id
 * </pre>
 * 
 * <pre>-column-timestamp &lt;java.lang.String&gt; (property: columnTimestamp)
 * &nbsp;&nbsp;&nbsp;The name of the column containing the timestamp for a data point (accepted 
 * &nbsp;&nbsp;&nbsp;types: integer, date, time, datetime, timestamp).
 * &nbsp;&nbsp;&nbsp;default: timestamp
 * </pre>
 * 
 * <pre>-column-value &lt;java.lang.String&gt; (property: columnValue)
 * &nbsp;&nbsp;&nbsp;The name of the column containing the value for a data point (accepted types:
 * &nbsp;&nbsp;&nbsp; numeric).
 * &nbsp;&nbsp;&nbsp;default: value
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetTimeseriesReader
  extends AbstractTimeseriesReader 
  implements MetaFileReader {

  /** for serialization. */
  private static final long serialVersionUID = -1030024345072684197L;

  /** the spreadsheet reader to use. */
  protected SpreadSheetReader m_Reader;
  
  /** the ID column. */
  protected String m_ColumnID;
  
  /** the ID column index. */
  protected int m_ColumnIDIndex;
  
  /** the timestamp column. */
  protected String m_ColumnTimestamp;
  
  /** the timestamp column type. */
  protected int m_ColumnTimestampIndex;
  
  /** the value column. */
  protected String m_ColumnValue;
  
  /** the value column index. */
  protected int m_ColumnValueIndex;

  /** the current container. */
  protected Timeseries m_Timeseries;
  
  /** the current row index. */
  protected int m_RowIndex;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Reads timeseries containers from columns of a spreadsheet.\n"
	+ "A new container is started, whenever the value of the ID column "
	+ "changes (hence you need to ensure that the data is ordered on this column).\n"
	+ "However, it is not required to have an ID column present. In this case, "
	+ "all of the data gets added to the same timeseries.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "Spreadsheet timeseries";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"*"};
  }

  /**
   * Returns the underlying format extensions.
   * 
   * @return		the format extensions (excluding dot)
   */
  public String[] getActualFormatExtensions() {
    return m_Reader.getFormatExtensions();
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "reader", "reader",
	    new CsvSpreadSheetReader());

    m_OptionManager.add(
	    "column-id", "columnID",
	    "id");

    m_OptionManager.add(
	    "column-timestamp", "columnTimestamp",
	    "timestamp");

    m_OptionManager.add(
	    "column-value", "columnValue",
	    "value");
  }
  
  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();
    
    m_Timeseries           = null;
    m_ColumnIDIndex        = -1;
    m_ColumnTimestampIndex = -1;
    m_ColumnValueIndex     = -1;
  }

  /**
   * Sets the reader to use.
   *
   * @param value	the reader
   */
  public void setReader(SpreadSheetReader value) {
    m_Reader = value;
    reset();
  }

  /**
   * Returns the reader to use.
   *
   * @return 		the reader
   */
  public SpreadSheetReader getReader() {
    return m_Reader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String readerTipText() {
    return "The reader to use for reading the spreadsheet file.";
  }

  /**
   * Sets the name of the ID column.
   *
   * @param value	the column name
   */
  public void setColumnID(String value) {
    m_ColumnID = value;
    reset();
  }

  /**
   * Returns the name of the ID column.
   *
   * @return 		the column name
   */
  public String getColumnID() {
    return m_ColumnID;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String columnIDTipText() {
    return "The name of the (optional) column containing the ID that distinguishes the timeseries.";
  }

  /**
   * Sets the name of the timestamp column.
   *
   * @param value	the column name
   */
  public void setColumnTimestamp(String value) {
    m_ColumnTimestamp = value;
    reset();
  }

  /**
   * Returns the name of the timestamp column.
   *
   * @return 		the column name
   */
  public String getColumnTimestamp() {
    return m_ColumnTimestamp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String columnTimestampTipText() {
    return "The name of the column containing the timestamp for a data point (accepted types: integer, date, time, datetime, timestamp).";
  }

  /**
   * Sets the name of the value column.
   *
   * @param value	the column name
   */
  public void setColumnValue(String value) {
    m_ColumnValue = value;
    reset();
  }

  /**
   * Returns the name of the value column.
   *
   * @return 		the column name
   */
  public String getColumnValue() {
    return m_ColumnValue;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String columnValueTipText() {
    return "The name of the column containing the value for a data point (accepted types: numeric).";
  }

  /**
   * Reads the next timeseries data point.
   * 
   * @param sheet	the current sheet
   * @return		the next data point
   * @throws Exception	if reading of timeseries data fails
   */
  protected TimeseriesPoint readDataPoint(SpreadSheet sheet) throws Exception {
    TimeseriesPoint	result;
    Date		timestamp;
    double		val;
    Cell		cell;
    
    cell = sheet.getCell(m_RowIndex, m_ColumnTimestampIndex);
    switch (cell.getContentType()) {
      case DATE:
      case TIME:
      case DATETIME:
	timestamp = cell.toAnyDateType();
	break;
      case LONG:
      case DOUBLE:
	timestamp = new Date(cell.toDouble().longValue());
	break;
      default:
	throw new IllegalStateException("Unhandled column type: " + cell.getContentType());
    }

    cell = sheet.getCell(m_RowIndex, m_ColumnValueIndex);
    val  = cell.toDouble();
    
    result = new TimeseriesPoint(timestamp, val);
    
    return result;
  }
  
  /**
   * Completes and returns the last timeseries that was started.
   * 
   * @param sheet	the current sheet
   * @return		the last timeseries, null if none previously started
   * @throws Exception	if reading of timeseries data fails
   */
  protected void performRead(SpreadSheet sheet) throws Exception {
    TimeseriesPoint	point;
    String		idOld;
    String		id;
    
    id         = null;
    idOld      = null;
    m_RowIndex = 0;
    if (m_ColumnIDIndex == -1) {
      if (sheet.getName() != null)
	id = sheet.getName();
      else
	id = m_Input.getName();
    }
    
    while (m_RowIndex < sheet.getRowCount()) {
      if (m_ColumnIDIndex != -1) {
	if (!sheet.hasCell(m_RowIndex, m_ColumnIDIndex)) {
	  m_RowIndex++;
	  continue;
	}
      }
      
      // obtain ID
      if (m_ColumnIDIndex != -1) {
	idOld = id;
	id    = sheet.getCell(m_RowIndex, m_ColumnIDIndex).getContent();
      }
      
      // read data point
      point = readDataPoint(sheet);
      if (!id.equals(idOld)) {
	m_Timeseries = new Timeseries();
	m_Timeseries.setID(id);
	m_Timeseries.add(point);
	m_ReadData.add(m_Timeseries);
      }
      else {
	m_Timeseries.add(point);
      }

      if (m_ColumnIDIndex == -1)
	idOld = id;
      
      m_RowIndex++;
    }
  }
  
  /**
   * Analyzes the columns.
   * 
   * @param sheet	the current sheet
   * @throws Exception	if columns not present or of wrong type
   */
  protected void analyzeColumns(SpreadSheet sheet) throws Exception {
    m_ColumnIDIndex        = sheet.getHeaderRow().indexOfContent(m_ColumnID);
    m_ColumnTimestampIndex = sheet.getHeaderRow().indexOfContent(m_ColumnTimestamp);
    m_ColumnValueIndex     = sheet.getHeaderRow().indexOfContent(m_ColumnValue);
    
    // timestamp
    if (m_ColumnTimestampIndex == -1)
      throw new IllegalStateException("Timestamp column '" + m_ColumnTimestamp + "' not found in results set!");
    
    // value
    if (m_ColumnValueIndex == -1)
      throw new IllegalStateException("Value column '" + m_ColumnValue + "' not found in results set!");
    if (!sheet.isNumeric(m_ColumnValueIndex))
      throw new IllegalStateException("Value column '" + m_ColumnValue + "' must be numeric!");
  }

  /**
   * Performs the actual reading.
   */
  @Override
  protected void readData() {
    List<SpreadSheet>	sheets;
    
    try {
      if (m_Reader instanceof MultiSheetSpreadSheetReader) {
	sheets = ((MultiSheetSpreadSheetReader) m_Reader).readRange(m_Input);
      }
      else {
	sheets = new ArrayList<SpreadSheet>();
	sheets.add(m_Reader.read(m_Input));
      }
      for (SpreadSheet sheet: sheets) {
	analyzeColumns(sheet);
	performRead(sheet);
      }
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to read file: " + m_Input, e);
    }
  }
}
