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
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.Date;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.data.timeseries.Timeseries;
import adams.data.timeseries.TimeseriesPoint;
import adams.db.SQL;
import adams.db.SQLStatement;
import adams.flow.core.ActorUtils;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Outputs timeseries containers generated from an SQL SELECT statement.<br/>
 * A new container is started, whenever the value of the ID column changes (hence you need to ensure that the data is ordered on this column).
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
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
 * @version $Revision: 9925 $
 */
public class TimeseriesDbReader
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -1030024345072684197L;
  
  /** the placeholder for the ID. */
  public final static String PLACEHOLDER_ID = "{ID}";

  /** the SQL statement to execute. */
  protected SQLStatement m_SQL;
  
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
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Outputs timeseries containers generated from an SQL SELECT statement.\n"
	+ "The input is interpreted as the ID of the timeseries to load.\n"
	+ "This ID can be used in the SQL statement using the " 
	+ PLACEHOLDER_ID + " placeholder.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "sql", "SQL",
	    new SQLStatement("select timestamp,value from table where id = " + PLACEHOLDER_ID + " order by timestamp"));

    m_OptionManager.add(
	    "column-timestamp", "columnTimestamp",
	    "");

    m_OptionManager.add(
	    "column-value", "columnValue",
	    "");
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_DatabaseConnection = null;
  }
  
  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();
    
    m_ColumnTimestampType  = Types.OTHER;
    m_ColumnTimestampIndex = -1;
    m_ColumnValueType      = Types.OTHER;
    m_ColumnValueIndex     = -1;
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
    return 
	"The SQL statement that selects the timeseries data; use "
	+ "the " + PLACEHOLDER_ID + " placeholder in the statement to "
	+ "specify the ID of the timeseries to load.";
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
   * Returns the class of objects that it accepts.
   *
   * @return		the Class of the accepted tokens
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String.class, Integer.class, Long.class};
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
   * Configures the database connection if necessary.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;

    result = super.setUp();

    if (result == null) {
      m_DatabaseConnection = getDatabaseConnection();
      if (m_DatabaseConnection == null)
	result = "No database connection available!";
    }

    return result;
  }

  /**
   * Reads the next timeseries data point.
   * 
   * @param rs		the resultset to read from
   * @return		the next data point
   * @throws Exception	if reading of timeseries data fails
   */
  protected TimeseriesPoint readDataPoint(ResultSet rs) throws Exception {
    TimeseriesPoint	result;
    Date		timestamp;
    double		val;
    
    if (SQL.isInteger(m_ColumnTimestampIndex))
      timestamp = new Date(rs.getInt(m_ColumnTimestampIndex));
    else if (m_ColumnTimestampType == Types.DATE)
      timestamp = rs.getDate(m_ColumnTimestampIndex);
    else if (m_ColumnTimestampType == Types.TIME)
      timestamp = new Date(rs.getTime(m_ColumnTimestampIndex).getTime());
    else if (m_ColumnTimestampType == Types.TIMESTAMP)
      timestamp = new Date(rs.getTimestamp(m_ColumnTimestampIndex).getTime());
    else
      throw new IllegalStateException("Unhandled column type: " + m_ColumnTimestampType);
    val = rs.getDouble(m_ColumnValueIndex);
    
    result = new TimeseriesPoint(timestamp, val);
    
    return result;
  }
  
  /**
   * Completes and returns the last timeseries that was started.
   * 
   * @param rs		the resultset to read from
   * @return		the last timeseries, null if none previously started
   * @throws Exception	if reading of timeseries data fails
   */
  protected Timeseries read(ResultSet rs) throws Exception {
    Timeseries		result;
    TimeseriesPoint	point;
    
    result = new Timeseries();
    
    while (rs.next()) {
      point = readDataPoint(rs);
      result.add(point);
    }
    
    SQL.closeAll(rs);
    
    return result;
  }
  
  /**
   * Analyzes the columns.
   * 
   * @param rs		the resultset to obtain the meta-data from
   * @throws Exception	if columns not present or of wrong type
   */
  protected void analyzeColumns(ResultSet rs) throws Exception {
    ResultSetMetaData	meta;
    int			i;
    String		col;
    int			type;

    meta                   = rs.getMetaData();
    m_ColumnTimestampType  = Types.OTHER;
    m_ColumnTimestampIndex = -1;
    m_ColumnValueType      = Types.OTHER;
    m_ColumnValueIndex     = -1;
    
    for (i = 1; i <= meta.getColumnCount(); i++) {
      col  = meta.getColumnName(i);
      type = meta.getColumnType(i);

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
    String	id;
    String	query;
    ResultSet	rs;
    Timeseries	ts;
    
    result = null;
    
    query = null;
    id    = m_InputToken.getPayload().toString();
    try {
      query = m_SQL.getValue().replace(PLACEHOLDER_ID, id);
      query = getVariables().expand(query);
      rs    = SQL.getSingleton(m_DatabaseConnection).getResultSet(query);
      if (isLoggingEnabled())
	getLogger().fine("SQL: " + query);
      analyzeColumns(rs);
      ts = read(rs);
      ts.setID(id);
      m_OutputToken = new Token(ts);
    }
    catch (Exception e) {
      result = handleException("Failed to execute statement: " + ((query == null) ? m_SQL : query), e);
    }
    
    return result;
  }
}
