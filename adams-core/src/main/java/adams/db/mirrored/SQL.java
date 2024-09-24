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
 * Copyright (C) 2019-2024 University of Waikato, Hamilton, NZ
 */

package adams.db.mirrored;

import adams.core.UniqueIDs;
import adams.db.AbstractDatabaseConnection;
import adams.db.AbstractDbBackend;
import adams.db.SQLIntf;
import adams.db.SQLUtils;
import adams.db.SimpleResultSet;
import adams.db.wrapper.AbstractWrapper;
import adams.db.wrapper.WrapperManager;
import gnu.trove.list.TDoubleList;
import gnu.trove.list.TIntList;
import gnu.trove.list.TLongList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Allows mirroring to another database.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SQL
  extends AbstractWrapper<SQLIntf>
  implements SQLIntf {

  private static final long serialVersionUID = -4601570259375411398L;

  /** the table manager. */
  protected static WrapperManager<SQL> m_TableManager;

  /** the non-mirrored backend. */
  protected SQLIntf m_DB;

  /** object for blocking polling/removal of fully processed. */
  protected final String m_Updating;

  /**
   * Initializes the mirroring.
   *
   * @param dbcon	the database connection
   * @param wrapped	the mirror
   */
  protected SQL(AbstractDatabaseConnection dbcon, SQLIntf wrapped) {
    super(dbcon, wrapped);
    m_DB       = ((DbBackend) AbstractDbBackend.getSingleton()).getNonMirroredBackend().getSQL(dbcon);
    m_Updating = UniqueIDs.next();
  }

  /**
   * Sets whether debugging is enabled, outputs more on the console.
   *
   * @param value	if true debugging is enabled
   */
  @Override
  public void setDebug(boolean value) {
    getWrapped().setDebug(value);
    m_DB.setDebug(value);
  }

  /**
   * Returns whether debugging is enabled.
   *
   * @return		true if debugging is enabled
   */
  @Override
  public boolean getDebug() {
    return m_DB.getDebug();
  }

  /**
   * Checks that a given table exists.
   *
   * @param table	the table to look for
   * @return true if the table exists.
   */
  @Override
  public boolean tableExists(String table) {
    return m_DB.tableExists(table);
  }

  /**
   * Checks that a given column exists.
   *
   * @param table	the table to search
   * @param column	the column to look for
   * @return 		true if the column exists.
   */
  @Override
  public boolean columnExists(String table, String column) {
    return m_DB.columnExists(table, column);
  }

  /**
   * Execute the given SQL statement and return ResultSet.
   *
   * @param  query  SQL query String
   * @return resulting ResultSet, or null if Error
   * @throws Exception if something goes wrong
   */
  @Override
  public SimpleResultSet getSimpleResultSet(String query) throws Exception {
    return m_DB.getSimpleResultSet(query);
  }

  /**
   * Create a Prepared statement with given query.
   *
   * @param query 	the query to execute
   * @return 		PreparedStatement
   * @throws Exception 	if something goes wrong
   */
  @Override
  public PreparedStatement prepareStatement(String query) throws Exception {
    return m_DB.prepareStatement(query);
  }

  /**
   * Create a Prepared statement with given query.
   *
   * @param query 	the query to execute
   * @param returnKeys 	whether to initialize the statement that it returns
   *			the generated keys
   * @return 		PreparedStatement
   * @throws Exception 	if something goes wrong
   */
  @Override
  public PreparedStatement prepareStatement(String query, boolean returnKeys) throws Exception {
    return m_DB.prepareStatement(query, returnKeys);
  }

  /**
   * Create a Prepared statement with given query.
   *
   * @param conn	the database connection to use
   * @param query 	the query to execute
   * @param returnKeys 	whether to initialize the statement that it returns
   *			the generated keys
   * @return 		PreparedStatement
   * @throws Exception 	if something goes wrong
   */
  public PreparedStatement prepareStatement(Connection conn, String query, boolean returnKeys) throws Exception {
    return m_DB.prepareStatement(conn, query, returnKeys);
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
  @Override
  public int update(String updateString, String table, String where) throws Exception {
    synchronized(m_Updating) {
      getWrapped().update(updateString, table, where);
      return m_DB.update(updateString, table, where);
    }
  }

  /**
   * Executes a SQL query. Return any Generated Keys
   * Caller is responsible for closing the statement.
   *
   * @param query the SQL query
   * @return Generated keys as a resultset, or null if failure
   * @throws Exception if something goes wrong
   */
  @Override
  public ResultSet executeGeneratedKeys(String query) throws Exception {
    synchronized(m_Updating) {
      SQLUtils.closeAll(getWrapped().executeGeneratedKeys(query));
      return m_DB.executeGeneratedKeys(query);
    }
  }

  /**
   * Executes a SQL query.
   *
   * @param query the SQL query
   * @return true if the query generated results, false if it didn't, null in case of an error
   * @throws Exception if an error occurs
   */
  @Override
  public Boolean execute(String query) throws Exception {
    synchronized(m_Updating) {
      getWrapped().execute(query);
      return m_DB.execute(query);
    }
  }

  /**
   * Empty table.
   *
   * @param table	the table to empty
   * @return		success?
   */
  @Override
  public boolean truncate(String table) {
    synchronized(m_Updating) {
      getWrapped().truncate(table);
      return m_DB.truncate(table);
    }
  }

  /**
   * Drops the table.
   *
   * @param table	the table to empty
   * @return		success?
   */
  @Override
  public boolean drop(String table) {
    synchronized(m_Updating) {
      getWrapped().drop(table);
      return m_DB.drop(table);
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
  @Override
  public ResultSet select(String columns, String tables, String where) throws Exception {
    return m_DB.select(columns, tables, where);
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
  @Override
  public ResultSet selectDistinct(String columns, String tables, String where) throws Exception {
    return m_DB.selectDistinct(columns, tables, where);
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
  @Override
  public List<String> selectString(boolean distinct, String column, String tables, String where) throws Exception {
    return m_DB.selectString(distinct, column, tables, where);
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
  @Override
  public List<String[]> selectStrings(boolean distinct, String[] columns, String tables, String where) throws Exception {
    return m_DB.selectStrings(distinct, columns, tables, where);
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
  @Override
  public TIntList selectInt(boolean distinct, String column, String tables, String where) throws Exception {
    return m_DB.selectInt(distinct, column, tables, where);
  }

  @Override
  public TLongList selectLong(boolean distinct, String column, String tables, String where) throws Exception {
    return null;
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
  @Override
  public TDoubleList selectDouble(boolean distinct, String column, String tables, String where) throws Exception {
    return m_DB.selectDouble(distinct, column, tables, where);
  }

  /**
   * Return resultset of given query.
   *
   * @param query	sql query
   * @return resulset
   * @throws Exception if something goes wrong
   */
  @Override
  public ResultSet getResultSet(String query) throws Exception {
    return m_DB.getResultSet(query);
  }

  /**
   * Returns the maximum length for column names. In case the meta-data
   * returns 0, {@link Integer#MAX_VALUE} is used instead.
   *
   * @return			the maximum length
   * @throws SQLException	if the query fails
   */
  @Override
  public int getMaxColumnNameLength() throws SQLException {
    return m_DB.getMaxColumnNameLength();
  }

  /**
   * Returns the singleton of the table.
   *
   * @param dbcon	the database connection to get the singleton for
   * @param mirror 	the mirror
   * @return		the singleton
   */
  public static synchronized SQL getSingleton(AbstractDatabaseConnection dbcon, SQLIntf mirror) {
    if (m_TableManager == null)
      m_TableManager = new WrapperManager<>(FAKE_TABLE_NAME, dbcon.getOwner());
    if (!m_TableManager.has(dbcon))
      m_TableManager.add(dbcon, new SQL(dbcon, mirror));

    return m_TableManager.get(dbcon);
  }
}
