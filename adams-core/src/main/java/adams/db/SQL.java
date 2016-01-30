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
 * SQL.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.db;

import adams.core.Utils;
import adams.core.base.BaseRegExp;
import adams.core.logging.LoggingHelper;
import adams.core.logging.LoggingObject;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Basic SQL support.
 *
 * @author dale
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SQL
  extends LoggingObject
  implements DatabaseConnectionProvider {

  /** for serialization. */
  private static final long serialVersionUID = -7708896486343190549L;

  /** whether debugging is turned on. */
  protected boolean m_Debug;

  /** connection to database. */
  protected AbstractDatabaseConnection m_DatabaseConnection;

  /** the table manager. */
  protected static TableManager<SQL> m_TableManager;
  
  /** the static logger. */
  protected static Logger LOGGER = LoggingHelper.getConsoleLogger(SQL.class);

  /**
   * Constructor.
   *
   * @param dbcon	the database context to use
   */
  public SQL(AbstractDatabaseConnection dbcon) {
    super();

    m_DatabaseConnection = dbcon;

    updatePrefix();
  }

  /**
   * Updates the prefix of the console object output streams.
   */
  protected void updatePrefix() {
    String	prefix;

    prefix   = getClass().getName() + "(" + getDatabaseConnection().toStringShort() + "/" + getDatabaseConnection().hashCode() + ")";
    m_Logger = LoggingHelper.getLogger(prefix);
    m_Logger.setLevel(getDebug() ? Level.INFO : Level.OFF);
  }

  /**
   * Returns the database connection this table is for.
   *
   * @return		the database connection
   */
  public AbstractDatabaseConnection getDatabaseConnection() {
    return m_DatabaseConnection;
  }

  /**
   * Sets whether debugging is enabled, outputs more on the console.
   *
   * @param value	if true debugging is enabled
   */
  public void setDebug(boolean value) {
    m_Debug = value;
    getLogger().setLevel(value ? Level.INFO : Level.WARNING);
  }

  /**
   * Returns whether debugging is enabled.
   *
   * @return		true if debugging is enabled
   */
  public boolean getDebug() {
    return m_Debug;
  }

  /**
   * Replaces all single quotes -' with double quotes -".
   *
   * @param in  Input String to replace quotes
   * @return  String with quotes replaced
   */
  public String escapeQuotes(String in) {
    String ret=in.replaceAll("'","\"");
    return(ret);
  }

  /**
   * Checks that a given table exists.
   *
   * @param table	the table to look for
   * @return true if the table exists.
   */
  public boolean tableExists(String table) {
    boolean tableExists = false;
    ResultSet rs = null;
    Connection connection = m_DatabaseConnection.getConnection(true);
    try{
      DatabaseMetaData dbmd = connection.getMetaData();
      rs = dbmd.getTables (null, null, table, null);
      tableExists = rs.next();
    }
    catch (SQLException e) {
      // try again
      try {
	DatabaseMetaData dbmd = connection.getMetaData();
	rs = dbmd.getTables (null, null, table, null);
	tableExists = rs.next();
      }
      catch (Exception ex) {
	// ignored?
      }
    }
    catch (Exception e) {
      // ignored?
    }
    finally{
      if (rs!=null) {
	try{
	  rs.close();
	}
	catch (Exception e) {
	  // ignored?
	}
      }
    }
    return tableExists;
  }

  /**
   * Checks that a given column exists.
   *
   * @param table	the table to search
   * @param column	the column to look for
   * @return 		true if the column exists.
   */
  public boolean columnExists(String table, String column) {
    boolean 		result;
    ResultSet 		rs;
    Connection 		conn;
    DatabaseMetaData 	dbmd;
    
    result = false;
    rs     = null;
    conn   = m_DatabaseConnection.getConnection(true);
    try{
      dbmd   = conn.getMetaData();
      rs     = dbmd.getColumns(null, null, table, column);
      result = rs.next();
    }
    catch (SQLException e) {
      // try again
      try {
	dbmd   = conn.getMetaData();
	rs     = dbmd.getColumns(null, null, table, column);
	result = rs.next();
      }
      catch (Exception ex) {
	// ignored?
      }
    }
    catch (Exception e) {
      // ignored?
    }
    finally{
      if (rs != null) {
	try {
	  rs.close();
	}
	catch (Exception e) {
	  // ignored?
	}
      }
    }
    
    return result;
  }

  /**
   * Execute the given SQL statement and return ResultSet.
   *
   * @param  query  SQL query String
   * @return resulting ResultSet, or null if Error
   * @throws Exception if something goes wrong
   */
  public SimpleResultSet getSimpleResultSet(String query) throws Exception {
    return(new SimpleResultSet(getResultSet(query)));
  }

  /**
   * Create a Prepared statement with given query.
   *
   * @param query 	the query to execute
   * @return 		PreparedStatement
   * @throws Exception 	if something goes wrong
   */
  public PreparedStatement prepareStatement(String query) throws Exception{
    return prepareStatement(query, false);
  }

  /**
   * Create a Prepared statement with given query.
   *
   * @param query 	the query to execute
   * @param returnKeys 	whether to initialize the statement that it returns
			the generated keys
   * @return 		PreparedStatement
   * @throws Exception 	if something goes wrong
   */
  public PreparedStatement prepareStatement(String query, boolean returnKeys) throws Exception{
    Connection connection = m_DatabaseConnection.getConnection(true);
    PreparedStatement stmt = null;
    getLogger().info("Preparing statement for: " + query);
    try {
      if (returnKeys)
	stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
      else
	stmt = connection.prepareStatement(query);
    }
    catch (SQLException e) {
      // try again
      if (returnKeys)
	stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
      else
	stmt = connection.prepareStatement(query);
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Error preparing statement for: " + query, e);
      throw new Exception(e);
    }
    return(stmt);
  }

  /**
   * Update table.
   *
   * @param updateString 	comma separated updates. e.g weight='80',height=180
   * @param table		the table to update
   * @param where		condition. e.g name='bob smith'
   * @return			number of rows affected
   * @throws Exception 		if something goes wrong
   */
  public int update(String updateString, String table, String where) throws Exception{
    String query="UPDATE " + table + " SET " + updateString + " WHERE " + where;
    Connection connection = m_DatabaseConnection.getConnection(true);
    Statement stmt = null;
    getLogger().info("Updating: " + query);
    int uc = 0;
    try {
      stmt = connection.createStatement();
      stmt.execute(query);
    }
    catch (SQLException e) {
      // try again
      try {
	if (stmt != null)
	  stmt.close();
	stmt = connection.createStatement();
	stmt.execute(query);
      }
      catch (Exception ex) {
	getLogger().log(Level.SEVERE, "Error executing 'update': " + query, ex);
	return(-1);
      }
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to create/execute statement", e);
      return(-1);
    }
    finally {
      if (stmt != null) {
	uc = stmt.getUpdateCount();
	stmt.close();
      }
    }
    return(uc);
  }

  /**
   * Executes a SQL query. Return any Generated Keys
   * Caller is responsible for closing the statement.
   *
   * @param query the SQL query
   * @return Generated keys as a resultset, or null if failure
   * @throws Exception if something goes wrong
   */
  public ResultSet executeGeneratedKeys(String query) throws Exception {
    Connection connection = m_DatabaseConnection.getConnection(true);
    Statement stmt = null;
    getLogger().info("Execute generated keys: " + query);
    try {
      stmt = connection.createStatement();
      stmt.execute(query, Statement.RETURN_GENERATED_KEYS);
      return(stmt.getGeneratedKeys());
    }
    catch (SQLException e) {
      getLogger().log(Level.SEVERE, "Error executing 'executeGeneratedKeys': " + query, e);
      if (stmt != null)
	stmt.close();
      return(null);
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to generate keys", e);
      if (stmt != null)
	stmt.close();
      return(null);
    }
  }

  /**
   * Executes a SQL query.
   *
   * @param query the SQL query
   * @return true if the query generated results, false if it didn't, null in case of an error
   * @throws Exception if an error occurs
   */
  public Boolean execute(String query) throws Exception {
    Connection 	connection;
    Statement 	stmt;
    Boolean 	result;

    connection = m_DatabaseConnection.getConnection(true);
    if (connection == null)
      throw new IllegalStateException(
	  "Connection object is null (" + m_DatabaseConnection.toStringShort() + "/" + m_DatabaseConnection.hashCode() + ")!");
    stmt = null;
    getLogger().info("Execute: " + query);
    try {
      stmt   = connection.createStatement();
      result = stmt.execute(query);
    }
    catch (SQLException e) {
      // try again
      try {
	if (stmt != null)
	  stmt.close();
	stmt   = connection.createStatement();
	result = stmt.execute(query);
      }
      catch (Exception ex) {
	getLogger().log(Level.SEVERE, "Error executing 'execute': " + query, e);
	result = null;
      }
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Error executing query: " + query, e);
      result = null;
    }
    finally {
      close(stmt);
    }

    return result;
  }

  /**
   * Empty table.
   *
   * @param table	the table to empty
   * @return		success?
   */
  public boolean truncate(String table) {
    boolean	result;

    try{
      execute("TRUNCATE TABLE " + table);
      result = true;
    }
    catch(Exception e) {
      getLogger().log(Level.SEVERE, "Error truncating table '" + table + "':", e);
      result = false;
    }

    return result;
  }

  /**
   * Drops the table.
   *
   * @param table	the table to empty
   * @return		success?
   */
  public boolean drop(String table) {
    boolean	result;

    try{
      execute("DROP TABLE " + table);
      result = true;
    }
    catch(Exception e) {
      getLogger().log(Level.SEVERE, "Error dropping table '" + table + "':", e);
      result = false;
    }

    return result;
  }

  /**
   * Checks whether the given column type is numeric.
   * 
   * @param colType	the column type
   * @return		true if numeric
   */
  public static boolean isNumeric(int colType) {
    return 
	   (colType == Types.BIGINT)
	|| (colType == Types.BIT)
	|| (colType == Types.DECIMAL)
	|| (colType == Types.DOUBLE)
	|| (colType == Types.FLOAT)
	|| (colType == Types.INTEGER)
	|| (colType == Types.NUMERIC)
	|| (colType == Types.REAL)
	|| (colType == Types.SMALLINT)
	|| (colType == Types.TINYINT)
	;
  }

  /**
   * Checks whether the given column type is an integer type.
   * 
   * @param colType	the column type
   * @return		true if an integer type
   */
  public static boolean isInteger(int colType) {
    return 
	   (colType == Types.BIGINT)
	|| (colType == Types.BIT)
	|| (colType == Types.INTEGER)
	|| (colType == Types.SMALLINT)
	|| (colType == Types.TINYINT)
	;
  }

  /**
   * Checks whether the given column type represents strings.
   * 
   * @param colType	the column type
   * @return		true if a string type
   */
  public static boolean isString(int colType) {
    return 
	   (colType == Types.CHAR)
	|| (colType == Types.CLOB)
	|| (colType == Types.LONGNVARCHAR)
	|| (colType == Types.LONGVARCHAR)
	|| (colType == Types.NCHAR)
	|| (colType == Types.NVARCHAR)
	|| (colType == Types.VARCHAR)
	;
  }

  /**
   * Checks whether the given column type represents a date-like type.
   * 
   * @param colType	the column type
   * @return		true if a date-like type
   */
  public static boolean isDate(int colType) {
    return 
	   (colType == Types.DATE)
	|| (colType == Types.TIME)
	|| (colType == Types.TIMESTAMP)
	;
  }
  
  /**
   * Close this statement to avoid memory leaks.
   *
   * @param s		the statement to close
   */
  public static void close(Statement s) {
    if (s != null) {
      try {
	s.close();
	s = null;
      }
      catch (Exception e) {
	LOGGER.log(Level.SEVERE, "Error closing statement", e);
      }
    }
  }

  /**
   * Close objects related to this ResultSet. Important because some (most,all?) jdbc drivers
   * do not clean up after themselves, resulting in memory leaks.
   *
   * @param r  The ResultSet to clean up after
   */
  public static void closeAll(ResultSet r) {
    if (r != null) {
      try {
	Statement s = r.getStatement();
	r.close();
	close(s);
	s = null;
	r = null;
      }
      catch (Exception e) {
	LOGGER.log(Level.SEVERE, "Error closing resultset", e);
      }
    }
  }

  /**
   * Close objects related to this ResultSet.
   *
   * @param r  The ResultSet to clean up after
   */
  public static void closeAll(SimpleResultSet r) {
    if (r != null) {
      try {
	r.close();
      }
      catch (Exception e) {
	LOGGER.log(Level.SEVERE, "Error closing resultset/statement", e);
      }
    }
  }

  /**
   * Do a select on given columns for all data in joined tables, with condition.
   *
   * @param columns	columns to select
   * @param tables	the tables to select from
   * @param where	condition
   * @return		resultset of data
   * @throws Exception 	if something goes wrong
   */
  public ResultSet select(String columns, String tables, String where) throws Exception {
    return doSelect(false, columns, tables, where);
  }

  /**
   * Do a select distinct on given columns for all data in joined tables, with
   * condition.
   *
   * @param columns	columns to select
   * @param tables	the tables to select from
   * @param where	condition
   * @return		resultset of data
   * @throws Exception 	if something goes wrong
   */
  public ResultSet selectDistinct(String columns, String tables, String where) throws Exception{
    return doSelect(true, columns, tables, where);
  }

  /**
   * Do a select on given columns for all data in joined tables, with condition.
   * Can be distinct.
   *
   * @param distinct	whether values in columns has to be distinct
   * @param columns	columns to select
   * @param tables	the tables to select from, ignored if null
   * @param where	condition, can be null
   * @return		resultset of data
   * @throws Exception 	if something goes wrong
   */
  protected ResultSet doSelect(boolean distinct, String columns, String tables, String where) throws Exception {
    String	query;

    // select
    query = "SELECT ";
    if (distinct)
      query += "DISTINCT ";
    query += columns;

    // from
    if (tables != null)
      query += " FROM " + tables;

    // where
    if ((where != null) && (where.length() > 0)) {
      if (   !where.trim().toUpperCase().startsWith("LIMIT ")
	  && !where.trim().toUpperCase().startsWith("ORDER ") )
	query += " WHERE";
      query += " " + where;
    }

    getLogger().info("doSelect: " + query);
    try {
      return getResultSet(query);
    }
    catch (SQLException e) {
      getLogger().log(Level.SEVERE, "Error executing 'doSelect': " + query, e);
      throw e;
    }
  }

  /**
   * MySQL boolean to tinyint.
   *
   * @param b	boolean
   * @return	tiny int value
   */
  public static int booleanToTinyInt(boolean b) {
    if (b) {
      return(1);
    } else {
      return(0);
    }
  }

  /**
   * MySQL tinyint to boolean.
   * @param i	tiny int
   * @return	boolean
   */
  public static boolean tinyIntToBoolean(int i) {
    if (i==0) {
      return(false);
    } else {
      return(true);
    }
  }

  /**
   * Backquotes the regular expression and ensures that it is surrounded by single
   * quotes.
   *
   * @param s		the regular expression to backquote and enclose
   * @return		the processed string
   */
  public static String backquote(BaseRegExp s) {
    return backquote(s.getValue());
  }

  /**
   * Backquotes the string and ensures that it is surrounded by single
   * quotes.
   *
   * @param s		the string to backquote and enclose
   * @return		the processed string
   */
  public static String backquote(String s) {
    String	result;

    result = Utils.backQuoteChars(s);
    if (!result.startsWith("'"))
      result = "'" + result + "'";

    return result;
  }

  /**
   * Return resultset of given query.
   *
   * @param query	sql query
   * @return resulset
   * @throws Exception if something goes wrong
   */
  public ResultSet getResultSet(String query) throws Exception {
    Connection connection = m_DatabaseConnection.getConnection(true);
    getLogger().info("Get ResultSet for : " + query);
    if (connection == null)
      throw new IllegalStateException("Connection object is null!");

    Statement stmt = null;
    try {
      stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
    }
    catch (SQLException e) {
      // try again
      stmt = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
    }
    return(stmt.executeQuery(query));
  }

  /**
   * Returns a short string representation.
   *
   * @return		the string representation
   */
  @Override
  public String toString() {
    return "SQL: " + getDatabaseConnection().toString();
  }
  
  /**
   * Returns the maximum length for column names. In case the meta-data
   * returns 0, {@link Integer#MAX_VALUE} is used instead.
   * 
   * @return			the maximum length
   * @throws SQLException	if the query fails
   */
  public int getMaxColumnNameLength() throws SQLException {
    int			result;
    DatabaseMetaData	meta;

    meta = m_DatabaseConnection.getConnection(false).getMetaData();
    result = meta.getMaxColumnNameLength();
    if (result == 0)
      result = Integer.MAX_VALUE;
    
    return result;
  }
  
  /**
   * Determines the SQL column types used in the provided resultset.
   * 
   * @param rs		the resultset to inspect
   * @return		the SQL column types
   * @see		Types
   * @throws SQLException	if querying the meta-data fails
   */
  public static int[] getColumnTypes(ResultSet rs) throws SQLException {
    return getColumnTypes(rs.getMetaData());
  }
  
  /**
   * Determines the SQL column types used in the provided resultset.
   * 
   * @param rs		the metadata resultset to inspect
   * @return		the SQL column types
   * @see		Types
   * @throws SQLException	if querying the meta-data fails
   */
  public static int[] getColumnTypes(ResultSetMetaData rs) throws SQLException {
    int[]	result;
    int		i;

    result = new int[rs.getColumnCount()];
    for (i = 1; i <= rs.getColumnCount(); i++)
      result[i - 1] = rs.getColumnType(i);
    
    return result;
  }
  
  /**
   * Determines the SQL column names used in the provided resultset.
   * 
   * @param rs		the resultset to inspect
   * @return		the SQL column names (or label if present)
   * @throws SQLException	if querying the meta-data fails
   */
  public static String[] getColumnNames(ResultSet rs) throws SQLException {
    return getColumnNames(rs.getMetaData());
  }
  
  /**
   * Determines the SQL column names used in the provided resultset.
   * 
   * @param rs		the meta-data resultset to inspect
   * @return		the SQL column names (or label if present)
   * @throws SQLException	if querying the meta-data fails
   */
  public static String[] getColumnNames(ResultSetMetaData rs) throws SQLException {
    String[]	result;
    int		i;

    result = new String[rs.getColumnCount()];
    for (i = 1; i <= rs.getColumnCount(); i++)
      result[i - 1] = rs.getColumnLabel(i);
    
    return result;
  }

  /**
   * Returns the singleton instance.
   *
   * @return		the singleton
   */
  public synchronized static SQL getSingleton(AbstractDatabaseConnection dbcon) {
    if (m_TableManager == null)
      m_TableManager = new TableManager<SQL>("SQL", null);
    if (!m_TableManager.has(dbcon))
      m_TableManager.add(dbcon, new SQL(dbcon));

    return m_TableManager.get(dbcon);
  }
}
