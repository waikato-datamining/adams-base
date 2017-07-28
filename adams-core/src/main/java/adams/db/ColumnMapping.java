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
 * Copyright (C) 2008-2017 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.db;


import adams.db.types.AutoIncrementType;
import adams.db.types.ColumnType;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Implements a set of table columns, mapping names to sql types
 *
 * @author dale
 * @version $Revision$
 */
public class ColumnMapping {

  /** primary key */
  protected String m_PrimaryKey;

  /** name -> type mapping */
  protected Hashtable<String,ColumnType> m_Mapping;

  /**
   * Constructor
   */
  public ColumnMapping() {
    super();

    m_PrimaryKey = null;
    m_Mapping    = new Hashtable<>();
  }

  /**
   * Add a 'column', name-> sql type mapping
   * @param cname	column name
   * @param typ		sql type
   */
  public void addMapping(String cname,ColumnType typ) {
    m_Mapping.put(cname,typ);
    if (typ instanceof AutoIncrementType)
      setPrimaryKey(cname);
  }

  /**
   * Get the sql type for a column name
   * @param cname	column name
   * @return	sql type
   */
  public ColumnType getMapping(String cname) {
    return m_Mapping.get(cname);
  }

  /**
   * Return all columns as an enumeration
   * @return	column names
   */
  public Enumeration<String> keys() {
    return m_Mapping.keys();
  }

  /**
   * Get number of columns
   * @return number of columns
   */
  public int size() {
    return m_Mapping.size();
  }

  /**
   * Set Column name that is primary key
   * @param id column name
   */
  public void setPrimaryKey(String id) {
    m_PrimaryKey = id;
  }

  /**
   * Does this mapping contain a primary key column?
   * @return has primary key?
   */
  public boolean hasPrimaryKey() {
    return (m_PrimaryKey != null);
  }

  /**
   * Get primary key column
   * @return column name
   */
  public String getPrimaryKey() {
    return m_PrimaryKey;
  }

  /**
   * Returns a string representation of the column mapping.
   *
   * @return		the string representation
   */
  public String toString() {
    return "prim key=" + getPrimaryKey() + ", columns=" + m_Mapping;
  }
}
