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
 * Reader.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.spreadsheet.sql;

import adams.core.Stoppable;
import adams.core.logging.LoggingLevel;
import adams.core.logging.LoggingObject;
import adams.data.spreadsheet.Cell.ContentType;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.DenseDataRow;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.db.SQL;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * For reading data from a database.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 13404 $
 */
public class Reader
  extends LoggingObject
  implements Stoppable {

  /** for serialization. */
  private static final long serialVersionUID = -958340824375198629L;

  /** the row class to use. */
  protected Class m_RowClass;

  /** indicates whether the reading has finished. */
  protected boolean m_Finished;

  /** whether the reading was stopped. */
  protected boolean m_Stopped;

  /** the header. */
  protected SpreadSheet m_Header;

  /** the column types. */
  protected int[] m_Type;

  /**
   * Initializes the reader.
   *
   * @param rowClass	the class for the rows in the spreadsheet,
   * 			e.g. {@link DenseDataRow}
   */
  public Reader(Class rowClass) {
    if (rowClass == null)
      throw new IllegalArgumentException("No row class specified!");

    m_RowClass = rowClass;
    m_Header   = null;
    m_Type     = new int[0];
  }

  /**
   * Sets the logging level.
   *
   * @param value 	the level
   */
  public void setLoggingLevel(LoggingLevel value) {
    m_LoggingLevel = value;
  }

  /**
   * Initializes the header
   *
   * @param rs	the resultset to use as basis
   * @throws SQLException        if accessing of meta-data fails
   */
  public void initHeader(ResultSet rs) throws SQLException {
    SpreadSheet	result;
    Row row;
    String[]		names;
    int		i;

    result = new DefaultSpreadSheet();
    result.setDataRowClass(getRowClass());

    // header
    row    = result.getHeaderRow();
    m_Type = SQL.getColumnTypes(rs);
    names  = SQL.getColumnNames(rs);
    for (i = 1; i <= names.length; i++)
      row.addCell("" + i).setContentAsString(names[i - 1]);
    m_Header = result.getHeader();
  }

  /**
   * Returns the row class in use.
   *
   * @return		the class
   */
  public Class getRowClass() {
    return m_RowClass;
  }

  /**
   * Returns the current header.
   *
   * @return		the header, null if not initialized yet
   * @see		#initHeader(ResultSet)
   */
  public SpreadSheet getHeader() {
    return m_Header;
  }

  /**
   * Reads all the data from the provided result set.
   *
   * @param rs	the result set to turn into a spreadsheet
   * @return		the generated spreadsheet
   * @throws SQLException	if reading fails
   */
  public SpreadSheet read(ResultSet rs) throws SQLException {
    return read(rs, -1);
  }

  /**
   * Reads the data from the provided result set, up to the specified
   * maximum of rows. Automatically closes the result set if all data
   * has been read.
   *
   * @param rs	the result set to turn into a spreadsheet
   * @param max	the maximum number of rows to read, 0 or less means all
   * @return		the generated spreadsheet
   * @throws SQLException	if reading fails
   */
  public SpreadSheet read(ResultSet rs, int max) throws SQLException {
    SpreadSheet	result;
    Row		row;
    int		i;
    ContentType type;

    m_Stopped = false;
    m_Finished  = false;

    if (m_Header == null) {
      initHeader(rs);
      m_Finished = !rs.next();
    }
    result = m_Header.getHeader();

    while (!m_Finished && !m_Stopped) {
      row = result.addRow();
      for (i = 1; i <= result.getColumnCount(); i++) {
	type = SqlUtils.sqlTypeToContentType(m_Type[i - 1]);
	switch (type) {
	  case TIME:
	    row.addCell(i - 1).setContentAs(rs.getTime(i).toString(), type);
	    break;
	  case TIMEMSEC:
	    row.addCell(i - 1).setContentAs(rs.getTime(i).toString(), type);
	    break;
	  case DATE:
	    row.addCell(i - 1).setContentAs(rs.getDate(i).toString(), type);
	    break;
	  case DATETIME:
	    row.addCell(i - 1).setContentAs(rs.getTimestamp(i).toString(), type);
	    break;
	  case DATETIMEMSEC:
	    row.addCell(i - 1).setContentAs(rs.getTimestamp(i).toString(), type);
	    break;
	  case LONG:
	    row.addCell(i - 1).setContent(rs.getLong(i));
	    break;
	  case DOUBLE:
	    row.addCell(i - 1).setContent(rs.getDouble(i));
	    break;
	  case STRING:
	    row.addCell(i - 1).setContentAsString(rs.getString(i));
	    break;
	  default:
	    throw new IllegalStateException("Unhandled content type: " + type);
	}
	if (rs.wasNull())
	  row.getCell(i - 1).setMissing();
      }

      m_Finished = !rs.next();

      // max chunk size?
      if ((max > 0) && (result.getRowCount() == max))
	break;
    }

    if (m_Finished || m_Stopped)
      SQL.closeAll(rs);

    return result;
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    m_Stopped = true;
  }

  /**
   * Returns whether the reader has been stopped.
   *
   * @return		true if stopped
   */
  public boolean isStopped() {
    return m_Stopped;
  }

  /**
   * Returns whether the reader has finished reading data.
   *
   * @return		true if finished
   */
  public boolean isFinished() {
    return m_Finished;
  }
}
