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
 * AbstractDataBackedIndex.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Extended {@link Index} class that can use an column name to determine an
 * index of a column as well.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of the underlying data
 */
public abstract class AbstractDataBackedIndex<T>
  extends Index {

  /** for serialization. */
  private static final long serialVersionUID = -4358263779315198808L;

  /** the underlying data. */
  protected T m_Data;
  
  /** the column names to replace. */
  protected List<String> m_Names;
  
  /** the indices of the column names. */
  protected HashMap<String,Integer> m_Indices;
  
  /**
   * Initializes with no index.
   */
  public AbstractDataBackedIndex() {
    super();
  }

  /**
   * Initializes with the given index, but no maximum.
   *
   * @param index	the index to use
   */
  public AbstractDataBackedIndex(String index) {
    super(index);
  }

  /**
   * Initializes with the given index and maximum.
   *
   * @param index	the index to use
   * @param max		the maximum of the 1-based index (e.g., use "10" to
   * 			allow "1-10" or -1 for uninitialized)
   */
  public AbstractDataBackedIndex(String index, int max) {
    super(index, max);
  }
  
  /**
   * For initializing the object.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Data    = null;
    m_Names   = null;
    m_Indices = null;
  }
  
  /**
   * Sets the dataset to use for interpreting the column name.
   * 
   * @param value	the dataset to use, can be null
   */
  public void setData(T value) {
    m_Data = value;
    m_Names     = null;
    if (m_Data == null)
      setMax(-1);
    else
      setMax(getNumColumns(value));
  }
  
  /**
   * Returns the underlying dataset.
   * 
   * @return		the underlying dataset, null if none set
   */
  public T getData() {
    return m_Data;
  }

  /**
   * Returns the number of columns the dataset has.
   * 
   * @param data	the dataset to retrieve the number of columns
   */
  protected abstract int getNumColumns(T data);
  
  /**
   * Returns the column name at the specified index.
   * 
   * @param data	the dataset to use
   * @param colIndex	the column index
   * @return		the column name
   */
  protected abstract String getColumnName(T data, int colIndex);
  
  /**
   * Returns the column names.
   * 
   * @return		the column names
   */
  protected synchronized List<String> getNames() {
    int		i;
    String	name;
    
    if (m_Names == null) {
      m_Names   = new ArrayList<String>();
      m_Indices = new HashMap<String,Integer>();
      for (i = 0; i < getNumColumns(m_Data); i++) {
	name = getColumnName(m_Data, i);
        m_Names.add(name);
        m_Indices.put(name, i);
      }
      Collections.sort(m_Names);
      Collections.reverse(m_Names);
    }
    
    return m_Names;
  }
  
  /**
   * Returns the indices in use.
   * 
   * @return		the indices
   */
  public synchronized HashMap<String,Integer> getIndices() {
    if (m_Names == null)
      getNames();
    return m_Indices;
  }
  
  /**
   * Replaces any column name in the string with the actual 1-based index.
   * 
   * @param s		the string to process
   * @return		the (potentially) updated string
   */
  protected String replaceColumnName(String s) {
    String		result;
    int			i;
    List<String>	names;
    
    result = s.trim();
    
    names = getNames();
    for (i = 0; i < names.size(); i++) {
      if (result.equals(names.get(i))) {
	result = Integer.toString(getIndices().get(names.get(i)) + 1);
	break;
      }
    }
    
    return result;
  }
  
  /**
   * Checks whether the strings represents a column name.
   * 
   * @param s		the string to process
   * @return		true if string is a column name
   */
  protected boolean isColumnName(String s) {
    boolean		result;
    int			i;
    List<String>	names;
    
    result = false;
    
    names = getNames();
    for (i = 0; i < names.size(); i++) {
      if (s.equals(names.get(i))) {
	result = true;
	break;
      }
    }
    
    return result;
  }
  
  /**
   * Cleanses the given string. Only allows "first", "last" and numbers.
   *
   * @param s		the string to clean
   * @return		the cleansed string, "" if invalid one provided; if 
   * 			no spreadsheet provided, all input is considered valid
   */
  @Override
  protected String clean(String s) {
    if (m_Data == null) {
      if (isPlaceholder(s))
	return super.clean(s);
      else
	return s;
    }
    else {
      if (isColumnName(s))
	return s;
      else
	return super.clean(s);
    }
  }
  
  /**
   * Parses the string and checks it against the maximum.
   *
   * @param s		the string to parse
   * @param max		the maximum to allow
   * @return		the parsed value, -1 if invalid
   */
  @Override
  protected int parse(String s, int max) {
    if (m_Data == null)
      return super.parse(s, max);
    else
      return super.parse(replaceColumnName(s), max);
  }
}
