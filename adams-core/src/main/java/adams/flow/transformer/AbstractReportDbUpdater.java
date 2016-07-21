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
 * AbstractReportDbUpdater.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.Constants;
import adams.core.QuickInfoHelper;
import adams.core.Shortening;
import adams.core.Utils;
import adams.data.id.IDHandler;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.MutableReportHandler;
import adams.data.report.Report;
import adams.db.SQL;
import adams.db.SQLStatement;
import adams.flow.core.ActorUtils;
import adams.flow.core.Token;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

/**
 * Ancestor for transformers that update {@link Report} objects or reports that 
 * are part of a {@link MutableReportHandler} object.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8087 $
 */
public abstract class AbstractReportDbUpdater
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 1429977151568224156L;

  /**
   * Enumeration for the supported types of queries.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision: 8087 $
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
  
  /** whether to be lenient, ie accept empyt resultsets. */
  protected boolean m_Lenient;
  
  /** the database connection. */
  protected adams.db.AbstractDatabaseConnection m_DatabaseConnection;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "sql", "SQL",
	    new SQLStatement("select key,value from table where id = \"" + Constants.PLACEHOLDER_ID + "\""));

    m_OptionManager.add(
	    "query-type", "queryType",
	    QueryType.KEY_VALUE);

    m_OptionManager.add(
	    "column-key", "columnKey",
	    "key");

    m_OptionManager.add(
	    "column-value", "columnValue",
	    "value");

    m_OptionManager.add(
	    "lenient", "lenient",
	    false);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_DatabaseConnection = null;
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
	"The SQL statement that selects the key-value pairs for the "
	+ "report; you can use the " + Constants.PLACEHOLDER_ID + " placeholder for the "
	+ "current handler's ID in your SQL statement (if it is an " 
	+ IDHandler.class.getName() + ").";
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
    return 
	"The name of the column containing the key for the key-value pairs "
	+ "to be added to the report.";
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
	"The name of the column containing the value for the key-value pairs "
	+ "to be added to the report.";
  }

  /**
   * Sets whether to be lenient, i.e., quietly handle empty resultsets.
   *
   * @param value	true if lenient
   */
  public void setLenient(boolean value) {
    m_Lenient = value;
    reset();
  }

  /**
   * Returns whether to be lenient, i.e., quietly handle empty resultsets.
   *
   * @return 		true if lenient
   */
  public boolean getLenient() {
    return m_Lenient;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String lenientTipText() {
    return 
	"If enabled, error messages are suppressed in case empty resultsets "
	+ "are encountered.";
  }

  /**
   * Returns the class of objects that it accepts.
   *
   * @return		the data type
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Report.class, MutableReportHandler.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the data type
   */
  @Override
  public Class[] generates() {
    return new Class[]{Report.class, MutableReportHandler.class};
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	value;
    
    result  = QuickInfoHelper.toString(this, "queryType", m_QueryType, "type: ");
    result += QuickInfoHelper.toString(this, "SQL", Shortening.shortenEnd(m_SQL.getValue().replaceAll("\\s", " ").replaceAll("[ ]+", " "), 50), ", query: ");
    value   = QuickInfoHelper.toString(this, "lenient", m_Lenient, "lenient", ", ");
    if (value != null)
      result += value;
    
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
    String			result;
    boolean			isHandler;
    MutableReportHandler	handler;
    Report			report;
    String			query;
    ResultSet			rs;
    boolean			dataRead;
    String			key;
    Object			value;
    int				i;
    ResultSetMetaData		meta;
    
    result = null;

    if (m_DatabaseConnection == null) {
      m_DatabaseConnection = getDatabaseConnection();
      if (m_DatabaseConnection == null)
	result = "No database connection available!";
    }

    if (result == null) {
      report    = null;
      handler   = null;
      isHandler = false;
      if (m_InputToken.getPayload() instanceof MutableReportHandler) {
        handler = (MutableReportHandler) m_InputToken.getPayload();
        isHandler = true;
        if (!handler.hasReport())
          handler.setReport(new Report());
        report = handler.getReport();
      }
      else {
        report = (Report) m_InputToken.getPayload();
      }

      query = null;
      try {
        query = m_SQL.getValue();
        if (isHandler && (handler instanceof IDHandler))
          query = query.replace(Constants.PLACEHOLDER_ID, ((IDHandler) handler).getID());
        query = getVariables().expand(query);
        if (isLoggingEnabled())
          getLogger().fine("query: " + query);

        rs = SQL.getSingleton(m_DatabaseConnection).getResultSet(query);
        dataRead = false;

        switch (m_QueryType) {
          case KEY_VALUE:
            while (rs.next()) {
              dataRead = true;
              key = rs.getObject(m_ColumnKey).toString();
              value = rs.getObject(m_ColumnValue);
              addToReport(report, key, value);
            }
            break;

          case COLUMN_AS_KEY:
            if (rs.next()) {
              dataRead = true;
              meta = rs.getMetaData();
              for (i = 1; i <= meta.getColumnCount(); i++) {
                if (meta.getColumnName(i).toLowerCase().equals(m_ColumnKey.toString()))
                  continue;
                key = meta.getColumnName(i).toLowerCase();
                value = rs.getObject(i);
                addToReport(report, key, value);
              }
            }
            break;

          default:
            throw new IllegalStateException("Unhandled query type: " + m_QueryType);
        }
        SQL.closeAll(rs);
        if (!dataRead && !m_Lenient)
          result = "No data found: " + query;

        if (isHandler) {
          handler.setReport(report);
          m_OutputToken = new Token(handler);
        }
        else {
          m_OutputToken = new Token(report);
        }
      }
      catch (Exception e) {
        result = handleException("Failed to read report data: " + query, e);
      }
    }
    
    return result;
  }
}
