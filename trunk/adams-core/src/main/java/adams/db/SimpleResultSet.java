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
 * SimpleResultSet.java
 * Copyright (C) 2008 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Simplified resultset that will close itself once you get to the end
 * 
 * @author dale
 * @version $Revision$
 */
public class SimpleResultSet {
  
  // actual resulset
  private ResultSet m_rs=null;
  
  /**
   * Constructor 
   * @param rs	Resultset to use
   */
  public SimpleResultSet(ResultSet rs) {
    super();
    m_rs=rs;
    // TODO Auto-generated constructor stub
  }
  
  /**
   * Move to next record. Autoclose
   * @return	record available?
   * @throws SQLException
   */
  public boolean next() throws SQLException{
    if (m_rs == null) {
      return(false);
    }
    boolean res=m_rs.next();
    if (!res) {
      close();
    }
    return(res);
  }
  
  /**
   * Manually close resulset
   * @throws SQLException
   */
  public void close() throws SQLException{
    if (m_rs != null) {
      Statement s = m_rs.getStatement();
      m_rs.close();
      if (s != null) {
	s.close();
	s=null;
      }
      m_rs=null;
    }
  }
  /**
   * get timestamp
   * @param columnName
   * @return	timestamp
   * @throws SQLException
   */
  public java.sql.Timestamp getTimestamp(String columnName) throws SQLException {
    return(m_rs.getTimestamp(columnName));
  }
  
  /**
   * get long
   * @param columnName
   * @return long
   * @throws SQLException
   */
  public long getLong(String columnName) throws SQLException{
    return(m_rs.getLong(columnName));
  }
  
  /**
   * Get int
   * @param columnName
   * @return int
   * @throws SQLException
   */
  public int getInt(String columnName) throws SQLException{
    return(m_rs.getInt(columnName));
  }
  
  /**
   * get float
   * @param columnName
   * @return float
   * @throws SQLException
   */
  public float getFloat(String columnName) throws SQLException{
    return(m_rs.getFloat(columnName));
  }
  
  /**
   * get double
   * @param columnName
   * @return double
   * @throws SQLException
   */
  public double getDouble(String columnName) throws SQLException{
    return(m_rs.getDouble(columnName));
  }
  
  /**
   * get boolean
   * @param columnName
   * @return boolean
   * @throws SQLException
   */
  public boolean getBoolean(String columnName) throws SQLException{
    return(m_rs.getBoolean(columnName));
  }
  
  /**
   * get String
   * @param columnName
   * @return string
   * @throws SQLException
   */
  public String getString(String columnName) throws SQLException{
    return(m_rs.getString(columnName));
  }
}
