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
 * BaseObject.java
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.core.base;

import java.io.Serializable;

import adams.core.CloneHandler;

/**
 * Super class for wrappers around classes like String, Integer, etc.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class BaseObject
  implements Comparable, CloneHandler<BaseObject>, Serializable {

  /** for serialization. */
  private static final long serialVersionUID = 4619009495177712405L;

  /** the internal object. */
  protected Comparable m_Internal;

  /**
   * Initializes the object with the string to parse.
   *
   * @param s		the string to parse
   */
  public BaseObject(String s) {
    setValue(s);
  }

  /**
   * Returns a clone of itself.
   *
   * @return		the clone or null in case of an error
   */
  public BaseObject getClone() {
    BaseObject	result;

    try {
      result = (BaseObject) getClass().newInstance();
      result.setValue(getValue());
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   *
   * @param o 		the object to be compared.
   * @return  		a negative integer, zero, or a positive integer as this object
   *			is less than, equal to, or greater than the specified object.
   * @throws ClassCastException 	if the specified object's type prevents it
   *         				from being compared to this object.
   */
  public int compareTo(Object o) {
    BaseObject	other;

    if (o == null)
      return 1;
    
    if (o instanceof String) {
      try {
	other = (BaseObject) getClass().newInstance();
	other.setValue((String) o);
      }
      catch (Exception e) {
	e.printStackTrace();
	return 1;
      }
    }
    else {
      if (!(o instanceof BaseObject))
	return -1;

      other = (BaseObject) o;
    }

    // handle nulls
    if ((getInternal() == null) && (other.getInternal() == null))
      return 0;
    else if (getInternal() == null)
      return -1;
    else if (other.getInternal() == null)
      return 1;

    return getInternal().compareTo(other.getInternal());
  }

  /**
   * Compares itself against the other base object.
   *
   * @param o		the object to compare against
   * @return		true if the internal string is the same
   * @see		#compareTo(Object)
   */
  @Override
  public boolean equals(Object o) {
    if (!(o instanceof BaseObject))
      return false;
    else
      return (compareTo((BaseObject) o) == 0);
  }

  /**
   * Hashcode so can be used as hashtable key. Returns the hashcode of the
   * internal string.
   *
   * @return		the hashcode
   */
  @Override
  public int hashCode() {
    return m_Internal.hashCode();
  }

  /**
   * Returns the internal object.
   *
   * @return		the internal object
   */
  public Comparable getInternal() {
    return m_Internal;
  }

  /**
   * Checks whether the string value is a valid presentation for this class.
   *
   * @param value	the string value to check
   * @return		true if valid
   */
  public abstract boolean isValid(String value);

  /**
   * Sets the string value.
   *
   * @param value	the string value
   */
  public abstract void setValue(String value);

  /**
   * Returns the current string value.
   *
   * @return		the string value
   */
  public abstract String getValue();

  /**
   * Returns a tool tip for the GUI editor (ignored if null is returned).
   *
   * @return		the tool tip
   */
  public abstract String getTipText();

  /**
   * Returns the underlying object as string.
   *
   * @return		the underlying object
   * @see		#getValue()
   */
  @Override
  public String toString() {
    return getValue();
  }
  
  /**
   * Creates a new instance of the specified BaseObject-derived class and sets
   * the specified value.
   * 
   * @param cls		the class to instantiate
   * @param s		the value to set
   * @returen		the instantiated object, null if class cannot be 
   * 			instantiated or value is invalid
   */
  public static BaseObject newInstance(Class cls, String s) {
    BaseObject	result;
    
    try {
      result = (BaseObject) cls.newInstance();
      if (result.isValid(s))
	result.setValue(s);
      else
	result = null;
    }
    catch (Exception e) {
      System.err.println("Failed to instantiate new instance of BaseObject-derived class with value '" + s + "':");
      e.printStackTrace();
      result = null;
    }
    
    return result;
  }
  
  /**
   * Turns the BaseObject array into a string array.
   * 
   * @param array	the array to convert
   * @return		the generated string array
   */
  public static String[] toStringArray(BaseObject[] array) {
    String[]	result;
    int		i;
    
    result = new String[array.length];
    for (i = 0; i < array.length; i++)
      result[i] = array[i].getValue();
    
    return result;
  }
}
