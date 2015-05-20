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
 * AbstractContainer.java
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.container;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import adams.core.CloneHandler;
import adams.core.Utils;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetSupporter;

/**
 * Ancestor of all containers. A container allows the access to the stored
 * values via their names.
 * <br><br>
 * NB: containers need to declare a default constructor. The default constructor
 * is used to generate help information.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractContainer
  implements Serializable, CloneHandler<AbstractContainer>, SpreadSheetSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -6949950627956848217L;

  /** for storing the values. */
  protected Hashtable<String,Object> m_Values;

  /** additional names for values. */
  protected HashSet<String> m_AdditionalNames;
  
  /**
   * Initializes the container.
   */
  protected AbstractContainer() {
    checkDefaultConstructor();
    
    m_Values          = new Hashtable<String,Object>();
    m_AdditionalNames = new HashSet<String>();
  }

  /**
   * Checks whether a default constructor is available - necessary for
   * generating HTML help.
   */
  protected void checkDefaultConstructor() {
    try {
      getClass().getConstructor(new Class[0]);
    }
    catch (Exception e) {
      throw new IllegalStateException(getClass().getName() + " does not have default constructor!");
    }
  }

  /**
   * Returns a clone of itself using serialization.
   *
   * @return		the clone
   */
  public AbstractContainer getClone() {
    return (AbstractContainer) Utils.deepCopy(this);
  }

  /**
   * Returns a short info on the container, namely the value names.
   * 
   * @return		the info
   */
  public String globalInfo() {
    StringBuilder	result;
    Iterator<String>	iter;
    
    result = new StringBuilder();
    
    result.append(getClass().getName() + "\n");
    result.append("Value names:\n");
    iter = names();
    while (iter.hasNext()) {
      result.append(" - ");
      result.append(iter.next());
      result.append("\n");
    }
    
    return result.toString();
  }
  
  /**
   * Returns all value names that can be used (theoretically).
   *
   * @return		iterator over all possible value names
   */
  public abstract Iterator<String> names();

  /**
   * Returns all available stored value names.
   *
   * @return		enumeration over all stored value names
   */
  public Iterator<String> stored() {
    List<String>	result;

    result = new ArrayList<String>(m_Values.keySet());
    Collections.sort(result);

    return result.iterator();
  }

  /**
   * Checks whether a given value is non-null.
   *
   * @param name	the name of the value to check
   * @return		true if the value is non-null
   */
  public boolean hasValue(String name) {
    return (getValue(name) != null);
  }

  /**
   * Returns the value associated with the given name.
   *
   * @param name	the name of the value
   * @return		the associated value or null if not available
   */
  public Object getValue(String name) {
    return m_Values.get(name);
  }
  
  /**
   * Checks whether the name of the object is valid.
   *
   * @param name	the name to check
   * @return		true if valid
   * @see		#names()
   */
  protected boolean isValidName(String name) {
    boolean		result;
    Iterator<String>	names;

    result = m_AdditionalNames.contains(name);
    
    if (!result) {
      names = names();
      while (names.hasNext()) {
	if (names.next().equals(name)) {
	  result = true;
	  break;
	}
      }
    }

    return result;
  }

  /**
   * Stores the value under the name.
   *
   * @param name	the name of the value
   * @param value	the value to store
   * @return		true if successfully stored, i.e., value is not null
   */
  protected boolean store(String name, Object value) {
    if (value == null)
      return false;

    m_Values.put(name, value);

    return true;
  }

  /**
   * Sets the named value.
   *
   * @param name	the name of the value
   * @param value	the value to store in the container
   * @return		true if the name was recognized and the value was
   * 			stored successfully, false otherwise
   */
  public boolean setValue(String name, Object value) {
    if (isValidName(name))
      return store(name, value);
    else
      return false;
  }

  /**
   * Checks whether the setup of the container is valid.
   *
   * @return		true if all the necessary values are available
   */
  public abstract boolean isValid();

  /**
   * Adds the specified name as valid name for a value.
   * 
   * @param name	the name to add
   * @return		true if the additional names list changed
   */
  public boolean addAdditionalName(String name) {
    return m_AdditionalNames.add(name);
  }
  
  /**
   * Removes the additional name again.
   * 
   * @param name	the name to remove
   * @return		true if the name was present and got removed
   */
  public boolean removeAdditionalName(String name) {
    return m_AdditionalNames.remove(name);
  }
  
  /**
   * Turns the object into a string.
   * 
   * @param obj		the object to turn into a string
   * @return		the generated string
   */
  protected String toString(Object obj) {
    if (obj == null)
      return "null";
    if (obj.getClass().isArray())
      return Utils.arrayToString(obj);
    else
      return obj.toString();
  }
  
  /**
   * Returns a short description of the stored data.
   *
   * @return		short description
   */
  @Override
  public String toString() {
    String		result;
    Iterator<String>	names;
    String		name;

    result = "";
    names  = stored();
    while (names.hasNext()) {
      name = names.next();
      if (result.length() > 0)
	result += ", ";
      result += name + "=" + toString(getValue(name));
    }

    return result;
  }

  /**
   * Returns the content as spreadsheet.
   * 
   * @return		the content
   */
  public SpreadSheet toSpreadSheet() {
    SpreadSheet		result;
    Row			row;
    int			i;
    ArrayList<String>	names;
    Object		value;
    
    synchronized(m_Values) {
      names  = new ArrayList<String>(m_Values.keySet());
      result = new SpreadSheet();
      row    = result.getHeaderRow();
      row.addCell("name").setContent("Name");
      row.addCell("value").setContent("Value");
      for (i = 0; i < names.size(); i++) {
	value = m_Values.get(names.get(i));
	row   = result.addRow("" + result.getRowCount());
	row.addCell("name").setContent(names.get(i));
	if (value == null)
	  row.addCell("value").setContent(SpreadSheet.MISSING_VALUE);
	else if (value.getClass().isArray())
	  row.addCell("value").setContent(Utils.arrayToString(value));
	else if (value instanceof Integer)
	  row.addCell("value").setContent((Integer) value);
	else if (value instanceof Double)
	  row.addCell("value").setContent((Double) value);
	else
	  row.addCell("value").setContent("" + value);
      }
    }
    
    return result;
  }
}
