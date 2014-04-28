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

/*
 * TimeseriesReportDbReader.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;
import adams.data.timeseries.Timeseries;
import adams.db.SQL;
import adams.db.SQLStatement;
import adams.flow.core.ActorUtils;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Adds all the data to the report of the timeseries passing through that the SQL statement returns.<br/>
 * The {ID} placeholder can be used in the SQL statement to represent the current timeseries' ID.<br/>
 * The following types of SQL statements are supported:<br/>
 * - multiple rows of key-value pairs.<br/>
 * - single row, with the key being the column name.<br/>
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.timeseries.Timeseries<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.timeseries.Timeseries<br/>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: TimeseriesReportDbReader
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
 * &nbsp;&nbsp;&nbsp;The SQL statement that selects the key-value pairs for the timeseries report;
 * &nbsp;&nbsp;&nbsp; you can use the {ID} placeholder for the current timeseries' ID in your 
 * &nbsp;&nbsp;&nbsp;SQL statement.
 * &nbsp;&nbsp;&nbsp;default: select key,value from table where id = \\\"{ID}\\\"
 * </pre>
 * 
 * <pre>-query-type &lt;KEY_VALUE|COLUMN_AS_KEY&gt; (property: queryType)
 * &nbsp;&nbsp;&nbsp;The type of query that the SQL statement represents; multiple rows with 
 * &nbsp;&nbsp;&nbsp;key-value pairs (KEY_VALUE) or single row with the column name as key (COLUMN_AS_KEY
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: KEY_VALUE
 * </pre>
 * 
 * <pre>-column-key &lt;java.lang.String&gt; (property: columnKey)
 * &nbsp;&nbsp;&nbsp;The name of the column containing the key for the key-value pairs to be 
 * &nbsp;&nbsp;&nbsp;added to the timeseries report.
 * &nbsp;&nbsp;&nbsp;default: key
 * </pre>
 * 
 * <pre>-column-value &lt;java.lang.String&gt; (property: columnValue)
 * &nbsp;&nbsp;&nbsp;The name of the column containing the value for the key-value pairs to be 
 * &nbsp;&nbsp;&nbsp;added to the timeseries report.
 * &nbsp;&nbsp;&nbsp;default: value
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TimeseriesReportDbReader
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 1429977151568224156L;
  
  /** the placeholder for the timeseries ID. */
  public final static String PLACEHOLDER_ID = "{ID}";

  /**
   * Enumeration for the supported types of queries.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum QueryType {
    /** two columns: key and value. */
    KEY_VALUE,
    /** using the column name as key. */
    COLUMN_AS_KEY
  }
  
  /** the SQL statement to execute. */
  protected SQLStatement m_SQL;
  
  /** the query type. */
  protected QueryType m_QueryType;
  
  /** the key column. */
  protected String m_ColumnKey;
  
  /** the value column. */
  protected String m_ColumnValue;
  
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
        "Adds all the data to the report of the timeseries "
	+ "passing through that the SQL statement returns.\n"
        + "The " + PLACEHOLDER_ID + " placeholder can be used in the SQL "
        + "statement to represent the current timeseries' ID.\n"
        + "The following types of SQL statements are supported:\n"
        + "- multiple rows of key-value pairs.\n"
        + "- single row, with the key being the column name.\n";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "sql", "SQL",
	    new SQLStatement("select key,value from table where id = \"" + PLACEHOLDER_ID + "\""));

    m_OptionManager.add(
	    "query-type", "queryType",
	    QueryType.KEY_VALUE);

    m_OptionManager.add(
	    "column-key", "columnKey",
	    "key");

    m_OptionManager.add(
	    "column-value", "columnValue",
	    "value");
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
	"The SQL statement that selects the key-value pairs for the timeseries "
	+ "report; you can use the " + PLACEHOLDER_ID + " placeholder for the "
	+ "current timeseries' ID in your SQL statement.";
  }

  /**
   * Sets the type of query.
   *
   * @param value	the type
   */
  public void setQueryType(QueryType value) {
    m_QueryType = value;
    reset();
  }

  /**
   * Returns the type of query.
   *
   * @return 		the type
   */
  public QueryType getQueryType() {
    return m_QueryType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String queryTypeTipText() {
    return 
	"The type of query that the SQL statement represents; multiple rows "
	+ "with key-value pairs (" +  QueryType.KEY_VALUE + ") or single row "
	+ "with the column name as key (" + QueryType.COLUMN_AS_KEY + ").";
  }

  /**
   * Sets the name of the key column.
   *
   * @param value	the column name
   */
  public void setColumnKey(String value) {
    m_ColumnKey = value;
    reset();
  }

  /**
   * Returns the name of the key column.
   *
   * @return 		the column name
   */
  public String getColumnKey() {
    return m_ColumnKey;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String columnKeyTipText() {
    return "The name of the column containing the key for the key-value pairs to be added to the timeseries report.";
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
    return "The name of the column containing the value for the key-value pairs to be added to the timeseries report.";
  }

  /**
   * Returns the class of objects that it accepts.
   *
   * @return		the data type
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Timeseries.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the data type
   */
  @Override
  public Class[] generates() {
    return new Class[]{Timeseries.class};
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "queryType", m_QueryType, "type: ");
    result += QuickInfoHelper.toString(this, "SQL", Utils.shorten(m_SQL.getValue().replaceAll("\\s", " ").replaceAll("[ ]+", " "), 50), ", query: ");
    
    return result;
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
   * Adds the specified value to the report.
   * 
   * @param report	the report to modify
   * @param key		the key for the value
   * @param value	the value to add
   */
  protected void addToReport(Report report, String key, Object value) {
    String	str;

    if (isLoggingEnabled()) {
      getLogger().fine("key: " + key);
      getLogger().fine("value: " + value);
    }
    
    if (value == null)
      return;
    str = value.toString();
    
    if (Utils.isDouble(str)) {
      report.addField(new Field(key, DataType.NUMERIC));
      report.setNumericValue(key, Double.parseDouble(str));
    }
    else if (Utils.isBoolean(str)) {
      report.addField(new Field(key, DataType.BOOLEAN));
      report.setBooleanValue(key, Boolean.parseBoolean(str.toLowerCase()));
    }
    else {
      report.addField(new Field(key, DataType.STRING));
      report.setStringValue(key, str);
    }
  }
  
  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    Timeseries		series;
    Report		report;
    String		query;
    ResultSet		rs;
    boolean		dataRead;
    String		key;
    Object		value;
    int			i;
    ResultSetMetaData	meta;
    
    result = null;
    
    series = (Timeseries) m_InputToken.getPayload();
    if (!series.hasReport())
      series.setReport(new Report());
    report = series.getReport();
    
    try {
      query = m_SQL.getValue();
      query = query.replace(PLACEHOLDER_ID, series.getID());
      query = getVariables().expand(query);
      if (isLoggingEnabled())
	getLogger().fine("query: " + query);
      
      rs       = SQL.getSingleton(m_DatabaseConnection).getResultSet(query);
      dataRead = false;
      
      switch (m_QueryType) {
	case KEY_VALUE:
	  while (rs.next()) {
	    dataRead = true;
	    key      = rs.getObject(m_ColumnKey).toString();
	    value    = rs.getObject(m_ColumnValue);
	    addToReport(report, key, value);
	  }
	  break;
	  
	case COLUMN_AS_KEY:
	  if (rs.next()) {
	    dataRead = true;
	    meta     = rs.getMetaData();
	    for (i = 1; i <= meta.getColumnCount(); i++) {
	      if (meta.getColumnName(i).toLowerCase().equals(m_ColumnKey.toString()))
		continue;
	      key   = meta.getColumnName(i).toLowerCase();
	      value = rs.getObject(i);
	      addToReport(report, key, value);
	    }
	  }
	  break;
	  
	default:
	  throw new IllegalStateException("Unhandled query type: " + m_QueryType);
      }
      SQL.closeAll(rs);
      if (!dataRead)
	result = "No data found for ID: " + series.getID();
      
      m_OutputToken = new Token(series);
    }
    catch (Exception e) {
      result = handleException("Failed to read timeseries report data for ID: " + series.getID(), e);
    }
    
    return result;
  }
}
