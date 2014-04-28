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
 * SharedStringsTable.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet;

import gnu.trove.map.hash.TObjectIntHashMap;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Strings across a spreadsheet are identified using an Integer rather the
 * string itself to conserve memory.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SharedStringsTable
  implements Serializable, Cloneable {

  /** for serialization. */
  private static final long serialVersionUID = 2234384718642928954L;

  /** the string / index relation. */
  protected TObjectIntHashMap<String> m_Map;
  
  /** the ordered list of strings. */
  protected ArrayList<String> m_Strings;
  
  /**
   * Initializes the table.
   */
  public SharedStringsTable() {
    super();
    
    m_Map     = new TObjectIntHashMap<String>();
    m_Strings = new ArrayList<String>();
  }
  
  /**
   * Resets the table.
   */
  public void clear() {
    m_Map.clear();
    m_Strings.clear();
  }
  
  /**
   * Retrieves all the values from the specified table, discards its own
   * values.
   * 
   * @param table	the table to obtain the data from
   */
  public void assign(SharedStringsTable table) {
    clear();
    m_Map.putAll(table.m_Map);
    m_Strings.addAll(table.m_Strings);
  }
  
  /**
   * Returns the index for the given string. If not already present, it will
   * add the string to its internal
   * 
   * @param s		the string to get the index for
   * @return		the the index
   */
  public int getIndex(String s) {
    int		result;
    
    if (!m_Map.containsKey(s)) {
      result = m_Strings.size();
      m_Map.put(s, result);
      m_Strings.add(s);
      return result;
    }
    else {
      return m_Map.get(s);
    }
  }
  
  /**
   * Returns the string associated with the given index.
   * 
   * @param index	the index of the string to retrieve
   * @return		the associated string, null if not found
   */
  public String getString(int index) {
    return m_Strings.get(index);
  }
  
  /**
   * Returns a clone of itself.
   * 
   * @return		the cloned object
   */
  @Override
  public SharedStringsTable clone() {
    SharedStringsTable	result;
    
    result = new SharedStringsTable();
    result.m_Map.putAll(m_Map);
    result.m_Strings.addAll(m_Strings);
    
    return result;
  }
  
  /**
   * Returns a short string description of the table.
   * 
   * @return		the description
   */
  @Override
  public String toString() {
    return "size=" + m_Strings.size();
  }
}
