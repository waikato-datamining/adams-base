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
 * TimeseriesDbReader.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.source;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.data.timeseries.Timeseries;
import adams.data.timeseries.TimeseriesPoint;
import adams.db.SQL;
import adams.db.SQLStatement;
import adams.flow.core.ActorUtils;
import adams.flow.core.Token;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.Date;

/**
 <!-- globalinfo-start -->
 * Outputs timeseries containers generated from an SQL SELECT statement.<br>
 * A new container is started, whenever the value of the ID column changes (hence you need to ensure that the data is ordered on this column).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.timeseries.Timeseries<br>
 * <br><br>
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
 * &nbsp;&nbsp;&nbsp;default: TimeseriesDbReader
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
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
 * <pre>-sql &lt;adams.db.SQLStatement&gt; (property: SQL)
 * &nbsp;&nbsp;&nbsp;The SQL statement that selects the timeseries data.
 * &nbsp;&nbsp;&nbsp;default: select id,timestamp,value from table order by id
 * </pre>
 * 
 * <pre>-column-id &lt;java.lang.String&gt; (property: columnID)
 * &nbsp;&nbsp;&nbsp;The name of the column containing the ID that distinguishes the timeseries 
 * &nbsp;&nbsp;&nbsp;(accepted types: numeric, string); if left empty, the first string column 
 * &nbsp;&nbsp;&nbsp;from the SQL statement is used.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-column-timestamp &lt;java.lang.String&gt; (property: columnTimestamp)
 * &nbsp;&nbsp;&nbsp;The name of the column containing the timestamp for a data point (accepted 
 * &nbsp;&nbsp;&nbsp;types: integer, date, time, datetime, timestamp); if left empty, the first 
 * &nbsp;&nbsp;&nbsp;date-like column from the SQL statement is used.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-column-value &lt;java.lang.String&gt; (property: columnValue)
 * &nbsp;&nbsp;&nbsp;The name of the column containing the value for a data point (accepted types:
 * &nbsp;&nbsp;&nbsp; numeric); if left empty, the first numeric column from the SQL statement 
 * &nbsp;&nbsp;&nbsp;is used.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TimeseriesDbReader
  extends AbstractSource {

  /** for serialization. */
  private static final long serialVersionUID = -1030024345072684197L;

  /** the SQL statement to execute. */
  protected SQLStatement m_SQL;
  
  /** the ID column. */
  protected String m_ColumnID;
  
  /** the ID column type. */
  protected int m_ColumnIDType;
  
  /** the ID column index. */
  protected int m_ColumnIDIndex;
  
  /** the timestamp column. */
  protected String m_ColumnTimestamp;
  
  /** the timestamp column index. */
  protected int m_ColumnTimestampType;
  
  /** the timestamp column type. */
  protected int m_ColumnTimestampIndex;
  
  /** the value column. */
  protected String m_ColumnValue;
  
  /** the value column type. */
  protected int m_ColumnValueType;
  
  /** the value column index. */
  protected int m_ColumnValueIndex;
  
  /** the database connection. */
  protected adams.db.AbstractDatabaseConnection m_DatabaseConnection;

  /** the current container. */
  protected Timeseries m_Timeseries;
  
  /** the resultset for retrieving the data. */
  protected transient ResultSet m_ResultSet;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Outputs timeseries containers generated from an SQL SELECT statement.\n"
	+ "A new container is started, whenever the value of the ID column "
	+ "changes (hence you need to ensure that the data is ordered on this column).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "sql", "SQL",
	    new SQLStatement("select id,timestamp,value from table order by id"));

    m_OptionManager.add(
	    "column-id", "columnID",
	    "");

    m_OptionManager.add(
	    "column-timestamp", "columnTimestamp",
	    "");

    m_OptionManager.add(
	    "column-value", "columnValue",
	    "");
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();
    
    m_Timeseries           = null;
    m_ColumnIDType         = Types.OTHER;
    m_ColumnIDIndex        = -1;
    m_ColumnTimestampType  = Types.OTHER;
    m_ColumnTimestampIndex = -1;
    m_ColumnValueType      = Types.OTHER;
    m_ColumnValueIndex     = -1;
    SQL.closeAll(m_ResultSet);

    m_DatabaseConnection   = null;
  }

  /**
   * Sets the SQL statement to run.
   *
   * @param value	the statement
   */
  public void setSQL(SQLStatement value) {
    m_SQL = value;
    reset();
  }

  /**
   * Returns the SQL statement to run.
   *
   * @return 		the statement
   */
  public SQLStatement getSQL() {
    return m_SQL;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String SQLTipText() {
    return "The SQL statement that selects the timeseries data.";
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
    return 
	"The name of the column containing the ID that distinguishes the "
	+ "timeseries (accepted types: numeric, string); if left empty, the "
	+ "first string column from the SQL statement is used.";
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
    return 
	"The name of the column containing the timestamp for a data point "
	+ "(accepted types: integer, date, time, datetime, timestamp); if "
	+ "left empty, the first date-like column from the SQL statement is used.";
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
    return 
	"The name of the column containing the value for a data point "
	+ "(accepted types: numeric); if left empty, the first numeric "
	+ "column from the SQL statement is used.";
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{Timeseries.class};
  }

  /**
   * Determines the database connection in the flow.
   *
   * @return		the database connection to use
   */
  protected adams.db.AbstractDatabaseConnection getDatabaseConnection() {
    return ActorUtils.getDatabaseConnection(
	  this,
	  adams.flow.standalone.DatabaseConnection.class,
	  adams.db.DatabaseConnection.getSingleton());
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "SQL", Utils.shorten(m_SQL.getValue().replaceAll("\\s", " ").replaceAll("[ ]+", " "), 50));
  }

  /**
   * Reads the next timeseries data point.
   * 
   * @return		the next data point
   * @throws Exception	if reading of timeseries data fails
   */
  protected TimeseriesPoint readDataPoint() throws Exception {
    TimeseriesPoint	result;
    Date		timestamp;
    double		val;
    
    if (SQL.isInteger(m_ColumnTimestampIndex))
      timestamp = new Date(m_ResultSet.getInt(m_ColumnTimestampIndex));
    else if (m_ColumnTimestampType == Types.DATE)
      timestamp = m_ResultSet.getDate(m_ColumnTimestampIndex);
    else if (m_ColumnTimestampType == Types.TIME)
      timestamp = new Date(m_ResultSet.getTime(m_ColumnTimestampIndex).getTime());
    else if (m_ColumnTimestampType == Types.TIMESTAMP)
      timestamp = new Date(m_ResultSet.getTimestamp(m_ColumnTimestampIndex).getTime());
    else
      throw new IllegalStateException("Unhandled column type: " + m_ColumnTimestampType);
    val = m_ResultSet.getDouble(m_ColumnValueIndex);
    
    result = new TimeseriesPoint(timestamp, val);
    
    return result;
  }
  
  /**
   * Completes and returns the last timeseries that was started.
   * 
   * @return		the last timeseries, null if none previously started
   * @throws Exception	if reading of timeseries data fails
   */
  protected Timeseries read() throws Exception {
    Timeseries		result;
    TimeseriesPoint	point;
    String		idOld;
    String		id;
    boolean		dataRead;
    boolean		finished;
    
    result = m_Timeseries;
    
    dataRead = false;
    id       = null;
    idOld    = null;
    finished = true;
    if (m_Timeseries != null)
      id = m_Timeseries.getID();
    
    while (m_ResultSet.next()) {
      dataRead = true;
      
      // obtain ID
      idOld = id;
      id    = m_ResultSet.getObject(m_ColumnIDIndex).toString();
      
      // read data point
      point = readDataPoint();
      if (!id.equals(idOld)) {
	m_Timeseries = new Timeseries();
	m_Timeseries.setID(id);
	m_Timeseries.add(point);
	finished = false;
	break;
      }
      else {
	result.add(point);
      }
    }
    
    if (finished) {
      m_Timeseries = null;
      if (dataRead) {
	SQL.closeAll(m_ResultSet);
	m_ResultSet = null;
      }
    }
    if (!dataRead) {
      m_Timeseries = null;
      result       = null;
      SQL.closeAll(m_ResultSet);
      m_ResultSet = null;
    }
    
    return result;
  }
  
  /**
   * Analyzes the columns.
   * 
   * @throws Exception	if columns not present or of wrong type
   */
  protected void analyzeColumns() throws Exception {
    ResultSetMetaData	meta;
    int			i;
    String		col;
    int			type;

    meta                   = m_ResultSet.getMetaData();
    m_ColumnIDType         = Types.OTHER;
    m_ColumnIDIndex        = -1;
    m_ColumnTimestampType  = Types.OTHER;
    m_ColumnTimestampIndex = -1;
    m_ColumnValueType      = Types.OTHER;
    m_ColumnValueIndex     = -1;
    
    for (i = 1; i <= meta.getColumnCount(); i++) {
      col  = meta.getColumnName(i);
      type = meta.getColumnType(i);
      
      // ID
      if (m_ColumnIDIndex == -1) {
	if (    (m_ColumnID.isEmpty() && SQL.isString(type)) 
	     || col.toLowerCase().equals(m_ColumnID.toLowerCase()) ) {
	  m_ColumnIDIndex = i;
	  m_ColumnIDType  = type;
	  continue;
	}
      }

      // timestamp
      if (m_ColumnTimestampIndex == -1) {
	if (   (m_ColumnTimestamp.isEmpty() && SQL.isDate(type)) 
	     || col.toLowerCase().equals(m_ColumnTimestamp.toLowerCase()) ) {
	  m_ColumnTimestampIndex = i;
	  m_ColumnTimestampType  = type;
	  continue;
	}
      }
      
      // value
      if (m_ColumnValueIndex == -1) {
	if (    (m_ColumnValue.isEmpty() && SQL.isNumeric(type)) 
	     || col.toLowerCase().equals(m_ColumnValue.toLowerCase()) ) {
	  m_ColumnValueIndex = i;
	  m_ColumnValueType  = type;
	  continue;
	}
      }
    }

    // ID
    if (m_ColumnIDIndex == -1) {
      if (m_ColumnID.isEmpty())
	throw new IllegalStateException("No suitable 'ID' column found in result set!");
      else
	throw new IllegalStateException("ID column '" + m_ColumnID + "' not found in result set!");
    }
    if (!(SQL.isNumeric(m_ColumnIDType) || SQL.isString(m_ColumnIDType)))
      throw new IllegalStateException("ID column '" + m_ColumnID + "' must be a numeric or string type: " + m_ColumnIDType);
    
    // timestamp
    if (m_ColumnTimestampIndex == -1) {
      if (m_ColumnTimestamp.isEmpty())
	throw new IllegalStateException("No suitable 'timestamp' column found in result set!");
      else
	throw new IllegalStateException("Timestamp column '" + m_ColumnTimestamp + "' not found in result set!");
    }
    if (!(SQL.isInteger(m_ColumnTimestampType) || (m_ColumnTimestampType == Types.DATE) || (m_ColumnTimestampType == Types.TIME) || (m_ColumnTimestampType == Types.TIMESTAMP)))
      throw new IllegalStateException("Timestamp column '" + m_ColumnTimestamp + "' must be a integer or date-related type: " + m_ColumnTimestampType);
    
    // value
    if (m_ColumnValueIndex == -1) {
      if (m_ColumnValue.isEmpty())
	throw new IllegalStateException("No suitable 'value' column found in result set!");
      else
	throw new IllegalStateException("Value column '" + m_ColumnValue + "' not found in result set!");
    }
    if (!SQL.isNumeric(m_ColumnValueType))
      throw new IllegalStateException("Value column '" + m_ColumnValue + "' must be a numeric type: " + m_ColumnValueType);
  }

  
  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    String	query;
    
    result = null;

    if (m_DatabaseConnection == null) {
      m_DatabaseConnection = getDatabaseConnection();
      if (m_DatabaseConnection == null)
        result = "No database connection available!";
    }

    if (result == null) {
      query = null;
      try {
        query = m_SQL.getValue();
        query = getVariables().expand(query);
        m_ResultSet = SQL.getSingleton(m_DatabaseConnection).getResultSet(query);
        if (isLoggingEnabled())
          getLogger().fine("SQL: " + query);
        analyzeColumns();
        read();
      }
      catch (Exception e) {
        result = handleException("Failed to execute statement: " + ((query == null) ? m_SQL : query), e);
      }
    }
    
    return result;
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  @Override
  public boolean hasPendingOutput() {
    return (m_Timeseries != null);
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  @Override
  public Token output() {
    Token	result;
    Timeseries	series;
    
    result = null;
    
    try {
      series = read();
      if (series != null)
	result = new Token(series);
    }
    catch (Exception e) {
      handleException("Failed to read timeseries data!", e);
      result = null;
    }
    
    return result;
  }

  /**
   * Cleans up after the execution has finished. Graphical output is left
   * untouched.
   */
  @Override
  public void wrapUp() {
    SQL.closeAll(m_ResultSet);
    m_Timeseries         = null;
    m_DatabaseConnection = null;

    super.wrapUp();
  }
}
