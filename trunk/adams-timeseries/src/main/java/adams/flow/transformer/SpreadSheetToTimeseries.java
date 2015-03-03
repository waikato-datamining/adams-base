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
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import java.util.Date;
import java.util.HashSet;

import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Cell.ContentType;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.RowComparator;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.data.timeseries.Timeseries;
import adams.data.timeseries.TimeseriesPoint;

/**
 <!-- globalinfo-start -->
 * Extracts one or more timeseries from a spreadsheet.<br/>
 * It uses one column ('ID') to identify all the rows that belong to a single timeseries. The 'Timestamp' and 'Value' columns make up data points of a timeseries.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.timeseries.Timeseries<br/>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: SpreadSheetToTimeseries
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-output-array &lt;boolean&gt; (property: outputArray)
 * &nbsp;&nbsp;&nbsp;If enabled, outputs an array of Timeseries objects rather than one-by-one.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-column-id &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: columnID)
 * &nbsp;&nbsp;&nbsp;The column to use for grouping the timeseries data points; if left empty 
 * &nbsp;&nbsp;&nbsp;all rows are added to the same timeseries.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-column-timestamp &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: columnTimestamp)
 * &nbsp;&nbsp;&nbsp;The column that contains the timestamp for the data points.
 * &nbsp;&nbsp;&nbsp;default: 2
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-column-value &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: columnValue)
 * &nbsp;&nbsp;&nbsp;The column that contains the value of the data points.
 * &nbsp;&nbsp;&nbsp;default: 3
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetToTimeseries
  extends AbstractArrayProvider {

  /** for serialization. */
  private static final long serialVersionUID = -4857598976178462111L;

  /** the column to use for grouping the timeseries. */
  protected SpreadSheetColumnIndex m_ColumnID;

  /** the column to use for the date/time. */
  protected SpreadSheetColumnIndex m_ColumnTimestamp;

  /** the column to use for value of the timeseries. */
  protected SpreadSheetColumnIndex m_ColumnValue;

  /* (non-Javadoc)
   * @see adams.core.option.AbstractOptionHandler#globalInfo()
   */
  @Override
  public String globalInfo() {
    return 
	"Extracts one or more timeseries from a spreadsheet.\n"
	+ "It uses one column ('ID') to identify all the rows that belong to "
	+ "a single timeseries. The 'Timestamp' and 'Value' columns make up "
	+ "data points of a timeseries.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "column-id", "columnID",
	    new SpreadSheetColumnIndex("1"));

    m_OptionManager.add(
	    "column-timestamp", "columnTimestamp",
	    new SpreadSheetColumnIndex("2"));

    m_OptionManager.add(
	    "column-value", "columnValue",
	    new SpreadSheetColumnIndex("3"));
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "columnID", m_ColumnID, "ID: ");
    result += QuickInfoHelper.toString(this, "columnTimestamp", m_ColumnTimestamp, ", Timestamp: ");
    result += QuickInfoHelper.toString(this, "columnValue", m_ColumnValue, ", Value: ");
    
    return result;
  }

  /**
   * Sets the index/name of the column to use for grouping the timeseries 
   * data points.
   *
   * @param value	the index/name
   */
  public void setColumnID(SpreadSheetColumnIndex value) {
    m_ColumnID = value;
    reset();
  }

  /**
   * Returns the index/name of the column to use for grouping the timeseries 
   * data points.
   *
   * @return		the index/name
   */
  public SpreadSheetColumnIndex getColumnID() {
    return m_ColumnID;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnIDTipText() {
    return "The column to use for grouping the timeseries data points; if left empty all rows are added to the same timeseries.";
  }

  /**
   * Sets the the index/name of the column that contains the timestamp of the
   * data points.
   *
   * @param value	the index/name
   */
  public void setColumnTimestamp(SpreadSheetColumnIndex value) {
    m_ColumnTimestamp = value;
    reset();
  }

  /**
   * Returns the index/name of the column that contains the timestamp of the
   * data points.
   *
   * @return		the index/name
   */
  public SpreadSheetColumnIndex getColumnTimestamp() {
    return m_ColumnTimestamp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnTimestampTipText() {
    return "The column that contains the timestamp for the data points.";
  }

  /**
   * Sets the index/name of the column that contains the value of the 
   * data points.
   *
   * @param value	the index/name
   */
  public void setColumnValue(SpreadSheetColumnIndex value) {
    m_ColumnValue = value;
    reset();
  }

  /**
   * Returns the index/name of the column that contains the value of the 
   * data points.
   *
   * @return		the index/name
   */
  public SpreadSheetColumnIndex getColumnValue() {
    return m_ColumnValue;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnValueTipText() {
    return "The column that contains the value of the data points.";
  }

  /**
   * Returns the class that the consumer accepts.
   * 
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{SpreadSheet.class};
  }

  /**
   * Returns the base class of the items.
   *
   * @return		the class
   */
  @Override
  protected Class getItemClass() {
    return Timeseries.class;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputArrayTipText() {
    return "If enabled, outputs an array of Timeseries objects rather than one-by-one.";
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    SpreadSheet			sheet;
    int				colID;
    int				colTimestamp;
    int				colValue;
    String			currID;
    Timeseries			series;
    HashSet<ContentType>	types;
    double			value;
    Date			timestamp;
    Cell			cell;
    
    result = null;

    m_Queue.clear();
    
    sheet = (SpreadSheet) m_InputToken.getPayload();
    
    // locate columns
    m_ColumnID.setSpreadSheet(sheet);
    colID = m_ColumnID.getIntIndex();
    
    colTimestamp = -1;
    if (result == null) {
      m_ColumnTimestamp.setSpreadSheet(sheet);
      colTimestamp = m_ColumnTimestamp.getIntIndex();
      if (colTimestamp == -1)
        result = "Failed to locate timestamp column: " + m_ColumnTimestamp.getIndex();
      if (result == null) {
	types = new HashSet<ContentType>(sheet.getContentTypes(colTimestamp));
	types.remove(ContentType.TIME);
	types.remove(ContentType.DATE);
	types.remove(ContentType.DATETIME);
	if (types.size() != 0)
	  result = "Timestamp column ('" + m_ColumnTimestamp.getIndex() + "') contains other types than time/date/datetime: " + types;
      }
    }

    colValue = -1;
    if (result == null) {
      m_ColumnValue.setSpreadSheet(sheet);
      colValue = m_ColumnValue.getIntIndex();
      if (colValue == -1)
        result = "Failed to locate value column: " + m_ColumnValue.getIndex();
      else if (!sheet.isNumeric(colValue))
	result = "Value column is not numeric: " + m_ColumnValue.getIndex();
    }

    // sort spreadsheet (if possible) and generate timeseries objects
    if (result == null) {
      if (colID > -1) {
	sheet = sheet.getClone();
	sheet.sort(new RowComparator(new int[]{colID}));
      }
      currID = null;
      series = null;
      for (Row row: sheet.rows()) {
	if (colID == -1) {
	  if (series == null) {
	    series = new Timeseries();
	    if (sheet.getName() != null)
	      series.setID(sheet.getName());
	    else
	      series.setID(new Date().toString());
	    m_Queue.add(series);
	    if (isLoggingEnabled())
	      getLogger().info("New timeseries");
	  }
	}
	else {
	  if (!row.getContent(colID).equals(currID)) {
	    if (isLoggingEnabled() && (series != null))
	      getLogger().info("# data points: " + series.size());
	    currID = row.getContent(colID);
	    series = new Timeseries();
	    series.setID(currID);
	    m_Queue.add(series);
	    if (isLoggingEnabled())
	      getLogger().info("New timeseries: " + currID);
	  }
	}
	
	// data available?
	if (!row.hasCell(colTimestamp) || row.getCell(colTimestamp).isMissing())
	  continue;
	if (!row.hasCell(colValue) || row.getCell(colValue).isMissing())
	  continue;
	
	value = row.getCell(colValue).toDouble();
	cell  = row.getCell(colTimestamp);
	switch (cell.getContentType()) {
	  case TIME:
	    timestamp = new Date(cell.toTime().getTime());
	    break;
	  case DATE:
	    timestamp = cell.toDate();
	    break;
	  case DATETIME:
	    timestamp = new Date(cell.toDateTime().getTime());
	    break;
	  default:
	    getLogger().severe("Unhandled cell type (for timestamp column): " + cell.getContentType());
	    continue;
	}
	
	series.add(new TimeseriesPoint(timestamp, value));
      }
    }
    
    return result;
  }
}
