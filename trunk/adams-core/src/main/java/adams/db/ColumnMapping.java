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
 * ColumnMapping.java
 * Copyright (C) 2008 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.db;


import java.util.Enumeration;
import java.util.Hashtable;

import adams.db.types.Auto_increment_type;
import adams.db.types.SQL_type;

/**
 * Implements a set of table columns, mapping names to sql types
 *
 * @author dale
 * @version $Revision$
 */
public class ColumnMapping {
  //primary key
  protected String primary_key=null;
  // name -> type mapping
  private Hashtable<String,SQL_type> m_cm=new Hashtable<String,SQL_type>();

  /**
   * Constructor
   *
   */
  public ColumnMapping() {
    super();
    // TODO Auto-generated constructor stub
  }

  /**
   * Add a 'column', name-> sql type mapping
   * @param cname	column name
   * @param typ		sql type
   */
  public void addMapping(String cname,SQL_type typ) {
    m_cm.put(cname,typ);
    if (typ instanceof Auto_increment_type) {
      setPrimaryKey(cname);
    }
  }

  /**
   * Get the sql type for a column name
   * @param cname	column name
   * @return	sql type
   */
  public  SQL_type getMapping(String cname) {
    return(m_cm.get(cname));
  }

  /**
   * Return all columns as an enumeration
   * @return	column names
   */
  public Enumeration<String> keys() {
    return(m_cm.keys());
  }

  /**
   * Get number of columns
   * @return number of columns
   */
  public int size() {
    return(m_cm.size());
  }

  /**
   * Set Column name that is primary key
   * @param id column name
   */
  public void setPrimaryKey(String id) {
    primary_key=id;
  }

  /**
   * Does this mapping contain a primary key column?
   * @return has primary key?
   */
  public boolean hasPrimaryKey() {
    return(primary_key != null);
  }

  /**
   * Get primary key column
   * @return column name
   */
  public String getPrimaryKey() {
    return(primary_key);
  }

  /**
   * Returns a string representation of the column mapping.
   *
   * @return		the string representation
   */
  public String toString() {
    return "prim key=" + getPrimaryKey() + ", columns=" + m_cm;
  }
}
