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
 * SpreadSheetRowToTimeseries.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import gnu.trove.list.array.TIntArrayList;

import java.util.Date;
import java.util.List;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseRegExp;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.data.timeseries.Timeseries;
import adams.data.timeseries.TimeseriesPoint;

/**
 <!-- globalinfo-start -->
 * Turns a spreadsheet row into a timeseries.<br/>
 * Columns for timestamps and values are extracted using the regular expressions, the timestamp columns are optional.
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
 * &nbsp;&nbsp;&nbsp;default: SpreadSheetRowToTimeseries
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
 * &nbsp;&nbsp;&nbsp;The (optional) column storing the ID for a timeseries.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-data-range &lt;adams.data.spreadsheet.SpreadSheetColumnRange&gt; (property: dataRange)
 * &nbsp;&nbsp;&nbsp;The range of columns to use for generating the timeseries.
 * &nbsp;&nbsp;&nbsp;default: first-last
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-regexp-timestamp &lt;adams.core.base.BaseRegExp&gt; (property: regExpTimestamp)
 * &nbsp;&nbsp;&nbsp;The regular expression for matching the columns that contain the timestamps 
 * &nbsp;&nbsp;&nbsp;for the data points.
 * &nbsp;&nbsp;&nbsp;default: Timestamp-
 * </pre>
 * 
 * <pre>-regexp-value &lt;adams.core.base.BaseRegExp&gt; (property: regExpValue)
 * &nbsp;&nbsp;&nbsp;The regular expression for matching the columns that contain the values 
 * &nbsp;&nbsp;&nbsp;of the data points.
 * &nbsp;&nbsp;&nbsp;default: Value-
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8016 $
 */
public class SpreadSheetRowToTimeseries
  extends AbstractArrayProvider {

  /** for serialization. */
  private static final long serialVersionUID = -4857598976178462111L;

  /** the column to use for grouping the timeseries. */
  protected SpreadSheetColumnIndex m_ColumnID;

  /** the range of columns to use for the timeseries. */
  protected SpreadSheetColumnRange m_DataRange;

  /** the regular expression for timestamp columns. */
  protected BaseRegExp m_RegExpTimestamp;

  /** the regular expression for value columns. */
  protected BaseRegExp m_RegExpValue;

  /* (non-Javadoc)
   * @see adams.core.option.AbstractOptionHandler#globalInfo()
   */
  @Override
  public String globalInfo() {
    return 
	"Turns a spreadsheet row into a timeseries.\n"
	+ "Columns for timestamps and values are extracted using the regular "
	+ "expressions, the timestamp columns are optional.";
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
	    "data-range", "dataRange",
	    new SpreadSheetColumnRange(SpreadSheetColumnRange.ALL));

    m_OptionManager.add(
	    "regexp-timestamp", "regExpTimestamp",
	    new BaseRegExp("Timestamp-.*"));

    m_OptionManager.add(
	    "regexp-value", "regExpValue",
	    new BaseRegExp("Value-.*"));
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
    result += QuickInfoHelper.toString(this, "dataRange", m_DataRange, ", Range: ");
    result += QuickInfoHelper.toString(this, "regExpTimestamp", m_RegExpTimestamp, ", Timestamp: ");
    result += QuickInfoHelper.toString(this, "regExpValue", m_RegExpValue, ", Value: ");
    
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
    return "The (optional) column storing the ID for a timeseries.";
  }

  /**
   * Sets the range of columns to use for generating the timeseries.
   *
   * @param value	the range
   */
  public void setDataRange(SpreadSheetColumnRange value) {
    m_DataRange = value;
    reset();
  }

  /**
   * Returns the range of columns to use for generating the timeseries.
   *
   * @return		the range
   */
  public SpreadSheetColumnRange getDataRange() {
    return m_DataRange;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String dataRangeTipText() {
    return "The range of columns to use for generating the timeseries.";
  }

  /**
   * Sets the regular expression for matching the timestamp columns.
   *
   * @param value	the expression
   */
  public void setRegExpTimestamp(BaseRegExp value) {
    m_RegExpTimestamp = value;
    reset();
  }

  /**
   * Returns the regular expression for matching the timestamp columns.
   *
   * @return		the expression
   */
  public BaseRegExp getRegExpTimestamp() {
    return m_RegExpTimestamp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regExpTimestampTipText() {
    return "The regular expression for matching the columns that contain the timestamps for the data points.";
  }

  /**
   * Sets the regular expression for matching the timestamp columns.
   *
   * @param value	the expression
   */
  public void setRegExpValue(BaseRegExp value) {
    m_RegExpValue = value;
    reset();
  }

  /**
   * Returns the regular expression for matching the timestamp columns.
   *
   * @return		the expression
   */
  public BaseRegExp getRegExpValue() {
    return m_RegExpValue;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regExpValueTipText() {
    return "The regular expression for matching the columns that contain the values of the data points.";
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
    TIntArrayList		colTimestamps;
    TIntArrayList		colValues;
    Timeseries			series;
    Double			value;
    Date			timestamp;
    Cell			cell;
    int				i;
    List<String>		names;
    int[]			colData;
    
    result = null;

    m_Queue.clear();
    
    sheet = (SpreadSheet) m_InputToken.getPayload();
    
    // locate columns
    m_DataRange.setData(sheet);
    m_ColumnID.setData(sheet);
    colData       = m_DataRange.getIntIndices();
    colID         = m_ColumnID.getIntIndex();
    colTimestamps = new TIntArrayList();
    colValues     = new TIntArrayList();
    names         = sheet.getColumnNames();
    for (i = 0; i < colData.length; i++) {
      if (m_RegExpValue.isMatch(names.get(colData[i])))
	colValues.add(colData[i]);
      else if (m_RegExpTimestamp.isMatch(names.get(colData[i])))
	colTimestamps.add(colData[i]);
    }
    if ((colTimestamps.size() > 0) && (colTimestamps.size() != colValues.size())) {
      getLogger().warning(
	  "Number of columns with timestamps differs from number of value "
	      + "columns, therefore ignoring timestamps: " 
	      + colTimestamps.size() + " != " + colValues.size());
      colTimestamps.clear();
    }
    
    for (Row row: sheet.rows()) {
      series = new Timeseries();
      
      // ID
      if (colID != -1) {
	cell = row.getCell(colID);
	if ((cell != null) && !cell.isMissing())
	  series.setID(row.getCell(colID).getContent());
      }
      
      // data
      if (colTimestamps.size() > 0) {
	for (i = 0; i < colTimestamps.size(); i++) {
	  cell = row.getCell(colTimestamps.get(i));
	  if ((cell != null) && !cell.isMissing()) {
	    timestamp = cell.toAnyDateType();
	    cell = row.getCell(colValues.get(i));
	    if ((cell != null) && !cell.isMissing()) {
	      value = cell.toDouble();
	      if (value != null)
		series.add(new TimeseriesPoint(timestamp, value));
	    }
	  }
	}
      }
      else {
	for (i = 0; i < colValues.size(); i++) {
	  cell = row.getCell(colValues.get(i));
	  if ((cell != null) && !cell.isMissing()) {
	    value = cell.toDouble();
	    if (value != null)
	      series.add(new TimeseriesPoint(new Date(i * 1000), value));
	  }
	}
      }
      
      m_Queue.add(series);
    }
    
    return result;
  }
}
