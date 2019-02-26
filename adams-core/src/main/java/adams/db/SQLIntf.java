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
 * SQLIntf.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.db;

import adams.core.logging.LoggingSupporter;
import gnu.trove.list.TDoubleList;
import gnu.trove.list.TIntList;
import gnu.trove.list.TLongList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Basic SQL support.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface SQLIntf
  extends LoggingSupporter, DatabaseConnectionProvider, TableInterface {

  /** fake table name. */
  public static String FAKE_TABLE_NAME = "SQL";

  /**
   * Sets whether debugging is enabled, outputs more on the console.
   *
   * @param value	if true debugging is enabled
   */
  public void setDebug(boolean value);

  /**
   * Returns whether debugging is enabled.
   *
   * @return		true if debugging is enabled
   */
  public boolean getDebug();

  /**
   * Checks that a given table exists.
   *
   * @param table	the table to look for
   * @return true if the table exists.
   */
  public boolean tableExists(String table);

  /**
   * Checks that a given column exists.
   *
   * @param table	the table to search
   * @param column	the column to look for
   * @return 		true if the column exists.
   */
  public boolean columnExists(String table, String column);

  /**
   * Execute the given SQL statement and return ResultSet.
   *
   * @param  query  SQL query String
   * @return resulting ResultSet, or null if Error
   * @throws Exception if something goes wrong
   */
  public SimpleResultSet getSimpleResultSet(String query) throws Exception;

  /**
   * Create a Prepared statement with given query.
   *
   * @param query 	the query to execute
   * @return 		PreparedStatement
   * @throws Exception 	if something goes wrong
   */
  public PreparedStatement prepareStatement(String query) throws Exception;

  /**
   * Create a Prepared statement with given query.
   *
   * @param query 	the query to execute
   * @param returnKeys 	whether to initialize the statement that it returns
   *			the generated keys
   * @return 		PreparedStatement
   * @throws Exception 	if something goes wrong
   */
  public PreparedStatement prepareStatement(String query, boolean returnKeys) throws Exception;

  /**
   * Update table.
   *
   * @param updateString 	comma separated updates. e.g weight='80',height=180
   * @param table		the table to update
   * @param where		condition. e.g name='bob smith'
   * @return			number of rows affected
   * @throws Exception 		if something goes wrong
   */
  public int update(String updateString, String table, String where) throws Exception;

  /**
   * Executes a SQL query. Return any Generated Keys
   * Caller is responsible for closing the statement.
   *
   * @param query the SQL query
   * @return Generated keys as a resultset, or null if failure
   * @throws Exception if something goes wrong
   */
  public ResultSet executeGeneratedKeys(String query) throws Exception;

  /**
   * Executes a SQL query.
   *
   * @param query the SQL query
   * @return true if the query generated results, false if it didn't, null in case of an error
   * @throws Exception if an error occurs
   */
  public Boolean execute(String query) throws Exception;

  /**
   * Empty table.
   *
   * @param table	the table to empty
   * @return		success?
   */
  public boolean truncate(String table);

  /**
   * Drops the table.
   *
   * @param table	the table to empty
   * @return		success?
   */
  public boolean drop(String table);

  /**
   * Do a select on given columns for all data in joined tables, with condition.
   *
   * @param columns	columns to select
   * @param tables	the tables to select from
   * @param where	condition
   * @return		resultset of data
   * @throws Exception 	if something goes wrong
   */
  public ResultSet select(String columns, String tables, String where) throws Exception;

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
  public ResultSet selectDistinct(String columns, String tables, String where) throws Exception;

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
  public List<String> selectString(boolean distinct, String column, String tables, String where) throws Exception;

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
  public List<String[]> selectStrings(boolean distinct, String[] columns, String tables, String where) throws Exception;

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
  public TIntList selectInt(boolean distinct, String column, String tables, String where) throws Exception;

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
  public TLongList selectLong(boolean distinct, String column, String tables, String where) throws Exception;

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
  public TDoubleList selectDouble(boolean distinct, String column, String tables, String where) throws Exception;

  /**
   * Return resultset of given query.
   *
   * @param query	sql query
   * @return resulset
   * @throws Exception if something goes wrong
   */
  public ResultSet getResultSet(String query) throws Exception;

  /**
   * Returns the maximum length for column names. In case the meta-data
   * returns 0, {@link Integer#MAX_VALUE} is used instead.
   * 
   * @return			the maximum length
   * @throws SQLException	if the query fails
   */
  public int getMaxColumnNameLength() throws SQLException;
}
