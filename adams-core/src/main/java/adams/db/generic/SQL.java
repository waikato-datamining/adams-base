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
 * Copyright (C) 2009-2018 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.db.generic;

import adams.core.Utils;
import adams.core.logging.LoggingHelper;
import adams.core.logging.LoggingObject;
import adams.db.AbstractDatabaseConnection;
import adams.db.SQLIntf;
import adams.db.SQLUtils;
import adams.db.SimpleResultSet;
import adams.db.TableManager;
import gnu.trove.list.TDoubleList;
import gnu.trove.list.TIntList;
import gnu.trove.list.TLongList;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.list.array.TLongArrayList;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Basic SQL support.
 *
 * @author dale
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SQL
  extends LoggingObject
  implements SQLIntf {

  /** for serialization. */
  private static final long serialVersionUID = -7708896486343190549L;

  /** whether debugging is turned on. */
  protected boolean m_Debug;

  /** connection to database. */
  protected AbstractDatabaseConnection m_DatabaseConnection;

  /** the table manager. */
  protected static TableManager<SQL> m_TableManager;

  /**
   * Constructor.
   *
   * @param dbcon	the database context to use
   */
  protected SQL(AbstractDatabaseConnection dbcon) {
    super();

    m_DatabaseConnection = dbcon;

    updatePrefix();
  }

  /**
   * Returns fake table name.
   *
   * @return		the name
   */
  @Override
  public String getTableName() {
    return FAKE_TABLE_NAME;
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
      SQLUtils.closeAll(rs);
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
      SQLUtils.closeAll(rs);
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
    if (isLoggingEnabled())
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
    if (isLoggingEnabled())
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
    if (isLoggingEnabled())
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
    if (isLoggingEnabled())
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
      SQLUtils.close(stmt);
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

    if (isLoggingEnabled())
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
   * Selects all strings from the specified column. Can be distinct.
   *
   * @param distinct	whether values in column have to be distinct
   * @param column	the string column to select
   * @param tables	the tables to select from, ignored if null
   * @param where	condition, can be null
   * @return		resultset of data
   * @throws Exception 	if something goes wrong
   */
  public List<String> selectString(boolean distinct, String column, String tables, String where) throws Exception {
    List<String>	result;
    ResultSet		rs;

    result = new ArrayList<>();

    rs = null;
    try {
      rs = doSelect(distinct, column, tables, where);
      while (rs.next())
	result.add(rs.getString(1));
    }
    catch (SQLException e) {
      getLogger().log(Level.SEVERE, "Error executing 'selectString'!", e);
      throw e;
    }
    finally {
      SQLUtils.closeAll(rs);
    }

    return result;
  }

  /**
   * Selects all strings from the specified columns. Can be distinct
   * (uses a CONCAT internally for that).
   *
   * @param distinct	whether values in column have to be distinct
   * @param columns	the string columns to select
   * @param tables	the tables to select from, ignored if null
   * @param where	condition, can be null
   * @return		resultset of data
   * @throws Exception 	if something goes wrong
   */
  public List<String[]> selectStrings(boolean distinct, String[] columns, String tables, String where) throws Exception {
    List<String[]>	result;
    StringBuilder	columnsStr;
    ResultSet		rs;
    String[]		row;
    int			i;

    result = new ArrayList<>();

    rs = null;
    try {
      columnsStr = new StringBuilder();
      if (distinct) {
        columnsStr.append("CONCAT(");
        columnsStr.append(Utils.flatten(columns, ", '\t', "));
	columnsStr.append(")");
      }
      else {
        columnsStr.append(Utils.flatten(columns, ", "));
      }
      rs = doSelect(distinct, columnsStr.toString(), tables, where);
      while (rs.next()) {
        if (distinct) {
	  row = rs.getString(1).split("\t");
	}
	else {
	  row = new String[columns.length];
	  for (i = 1; i <= columns.length; i++) {
	    row[i - 1] = rs.getString(i);
	  }
	}
	result.add(row);
      }
    }
    catch (SQLException e) {
      getLogger().log(Level.SEVERE, "Error executing 'selectStrings'!", e);
      throw e;
    }
    finally {
      SQLUtils.closeAll(rs);
    }

    return result;
  }

  /**
   * Selects all integers from the specified column. Can be distinct.
   *
   * @param distinct	whether values in column have to be distinct
   * @param column	the int column to select
   * @param tables	the tables to select from, ignored if null
   * @param where	condition, can be null
   * @return		resultset of data
   * @throws Exception 	if something goes wrong
   */
  public TIntList selectInt(boolean distinct, String column, String tables, String where) throws Exception {
    TIntList	result;
    ResultSet	rs;

    result = new TIntArrayList();

    rs = null;
    try {
      rs = doSelect(distinct, column, tables, where);
      while (rs.next())
	result.add(rs.getInt(1));
    }
    catch (SQLException e) {
      getLogger().log(Level.SEVERE, "Error executing 'selectInt'!", e);
      throw e;
    }
    finally {
      SQLUtils.closeAll(rs);
    }

    return result;
  }

  /**
   * Selects all longs from the specified column. Can be distinct.
   *
   * @param distinct	whether values in column have to be distinct
   * @param column	the long column to select
   * @param tables	the tables to select from, ignored if null
   * @param where	condition, can be null
   * @return		resultset of data
   * @throws Exception 	if something goes wrong
   */
  public TLongList selectLong(boolean distinct, String column, String tables, String where) throws Exception {
    TLongList	result;
    ResultSet	rs;

    result = new TLongArrayList();

    rs = null;
    try {
      rs = doSelect(distinct, column, tables, where);
      while (rs.next())
	result.add(rs.getLong(1));
    }
    catch (SQLException e) {
      getLogger().log(Level.SEVERE, "Error executing 'selectInt'!", e);
      throw e;
    }
    finally {
      SQLUtils.closeAll(rs);
    }

    return result;
  }

  /**
   * Selects all doubles from the specified column. Can be distinct.
   *
   * @param distinct	whether values in column have to be distinct
   * @param column	the long column to select
   * @param tables	the tables to select from, ignored if null
   * @param where	condition, can be null
   * @return		resultset of data
   * @throws Exception 	if something goes wrong
   */
  public TDoubleList selectDouble(boolean distinct, String column, String tables, String where) throws Exception {
    TDoubleList result;
    ResultSet	rs;

    result = new TDoubleArrayList();

    rs = null;
    try {
      rs = doSelect(distinct, column, tables, where);
      while (rs.next())
	result.add(rs.getDouble(1));
    }
    catch (SQLException e) {
      getLogger().log(Level.SEVERE, "Error executing 'selectInt'!", e);
      throw e;
    }
    finally {
      SQLUtils.closeAll(rs);
    }

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
    if (isLoggingEnabled())
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
   * Returns the singleton instance.
   *
   * @return		the singleton
   */
  public synchronized static SQL singleton(AbstractDatabaseConnection dbcon) {
    if (m_TableManager == null)
      m_TableManager = new TableManager<>("SQL", null);
    if (!m_TableManager.has(dbcon))
      m_TableManager.add(dbcon, new SQL(dbcon));

    return m_TableManager.get(dbcon);
  }
}
