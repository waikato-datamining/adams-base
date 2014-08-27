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
 * HeaderDefinition.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.featureconverter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import adams.data.report.DataType;

/**
 * Container for storing header information.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class HeaderDefinition
  implements Serializable, Comparable<HeaderDefinition> {

  /** for serialization. */
  private static final long serialVersionUID = 3616920834737849123L;

  /** the names. */
  protected List<String> m_Names;
  
  /** the type. */
  protected List<DataType> m_Types;
  
  /** the hashcode. */
  protected Integer m_HashCode;
  
  /**
   * Initializes the header.
   */
  public HeaderDefinition() {
    this(new ArrayList<String>(), new ArrayList<DataType>());
  }
  
  /**
   * Initializes the header.
   * 
   * @param names	the names of the fields
   * @param types	the types of the fields
   */
  public HeaderDefinition(List<String> names, List<DataType> types) {
    if (names.size() != types.size())
      throw new IllegalArgumentException("Number of names and types vary: " + names.size() + " != " + types.size());
    
    m_Names = new ArrayList<String>(names);
    m_Types = new ArrayList<DataType>(types);
  }
  
  /**
   * Returns the names.
   * 
   * @return		the names
   */
  public List<String> getNames() {
    return m_Names;
  }
  
  /**
   * Returns the data types.
   * 
   * @return		the types
   */
  public List<DataType> getTypes() {
    return m_Types;
  }
  
  /**
   * Returns the number of fields.
   * 
   * @return		the total
   */
  public int size() {
    return m_Names.size();
  }
  
  /**
   * Returns the name at the specified index.
   * 
   * @param index	the index of the name to retrieve
   * @return		the name
   */
  public String getName(int index) {
    return m_Names.get(index);
  }
  
  /**
   * Returns the data type at the specified index.
   * 
   * @param index	the index of the data type to retrieve
   * @return		the data type
   */
  public DataType getType(int index) {
    return m_Types.get(index);
  }
  
  /**
   * Renames the name at the specified index.
   * 
   * @param index	the index of the name to rename
   * @param name	the new name
   */
  public void rename(int index, String name) {
    m_Names.set(index, name);
  }
  
  /**
   * Adds the definition at the end.
   * 
   * @param name	the feature name
   * @param type	the data type of the feature
   */
  public void add(String name, DataType type) {
    m_Names.add(name);
    m_Types.add(type);
  }
  
  /**
   * Adds all the definitions at the end.
   * 
   * @param definition	the definitions to add
   */
  public void add(HeaderDefinition definition) {
    m_Names.addAll(definition.getNames());
    m_Types.addAll(definition.getTypes());
  }
  
  /**
   * Returns the hashcode.
   * 
   * @return		the hashcode
   */
  @Override
  public synchronized int hashCode() {
    if (m_HashCode == null)
      m_HashCode = toString().hashCode();
    return m_HashCode;
  }

  /**
   * Compares itself with the specified object.
   * 
   * @param o		the object to compare with
   * @return		returns less than, equal to or greater than zero if this
   * 			object is less than, equal to or greater than the 
   * 			specified object
   */
  @Override
  public int compareTo(HeaderDefinition o) {
    int		result;
    int		i;

    result = new Integer(m_Names.size()).compareTo(o.m_Names.size());

    if (result == 0) {
      for (i = 0; i < m_Names.size(); i++) {
	result = m_Names.get(i).compareTo(o.m_Names.get(i));
	if (result != 0)
	  break;
      }
    }

    if (result == 0) {
      for (i = 0; i < m_Types.size(); i++) {
	result = m_Types.get(i).compareTo(o.m_Types.get(i));
	if (result != 0)
	  break;
      }
    }
    
    return result;
  }
  
  /**
   * Returns whether this object is the same as the the speficied one.
   * 
   * @param obj		the object to compare with
   * @return		true if the same
   */
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof HeaderDefinition)
      return (compareTo((HeaderDefinition) obj) == 0);
    else
      return false;
  }
  
  /**
   * Returns the header definition as string.
   * 
   * @return		the header definition
   */
  @Override
  public String toString() {
    StringBuilder	result;
    int			i;
    
    result = new StringBuilder();
    
    for (i = 0; i < size(); i++) {
      if (i > 0)
	result.append(",");
      result.append(getName(i) + ":" + getType(i));
    }
    
    return result.toString();
  }
}
