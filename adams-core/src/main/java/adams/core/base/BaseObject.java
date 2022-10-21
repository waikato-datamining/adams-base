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
 * Copyright (C) 2009-2021 University of Waikato, Hamilton, New Zealand
 */

package adams.core.base;

import adams.core.CloneHandler;
import adams.core.Properties;
import adams.core.Utils;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Super class for wrappers around classes like String, Integer, etc.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class BaseObject
  implements Comparable, CloneHandler<BaseObject>, Serializable {

  /** for serialization. */
  private static final long serialVersionUID = 4619009495177712405L;

  public static final String SUFFIX_DISPLAY = ".display";

  public static final String SUFFIX_VALUE = ".value";

  /** the properties with the templates. */
  protected static Map<Class,Properties> m_Properties;

  /** the internal object. */
  protected Comparable m_Internal;

  /**
   * Initializes the object with the string to parse.
   *
   * @param s		the string to parse
   */
  public BaseObject(String s) {
    initialize();
    setValue(s);
  }

  /**
   * Initializes the internal object.
   * <br><br>
   * Default implementation does nothing.
   */
  protected void initialize() {
  }
  
  /**
   * Returns a clone of itself.
   *
   * @return		the clone or null in case of an error
   */
  public BaseObject getClone() {
    BaseObject	result;

    try {
      result = getClass().getDeclaredConstructor().newInstance();
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
    Comparable  internal;
    Comparable  otherInternal;

    if (o == null)
      return 1;
    
    if (o instanceof String) {
      try {
	other = getClass().getDeclaredConstructor().newInstance();
	other.setValue((String) o);
      }
      catch (Exception e) {
        System.err.println("Failed to initialize instance of " + getClass().getName() + " with '" + o + "'!");
	e.printStackTrace();
	return 1;
      }
    }
    else {
      if (!(o instanceof BaseObject))
	return -1;

      other = (BaseObject) o;
    }

    internal      = getInternal();
    otherInternal = other.getInternal();

    // handle nulls
    if ((internal == null) && (otherInternal == null))
      return 0;
    else if (internal == null)
      return -1;
    else if (otherInternal == null)
      return 1;

    if (internal.getClass() != otherInternal.getClass())
      return internal.getClass().getName().compareTo(otherInternal.getClass().getName());

    return internal.compareTo(otherInternal);
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
    return (o instanceof BaseObject) && (compareTo(o) == 0);
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
   * Checks whether the string value (with escaped unicode sequences) is a
   * valid presentation for this class.
   *
   * @param value	the string value to check
   * @return		true if valid
   */
  public boolean isValidUnicode(String value) {
    return isValid(Utils.unescapeUnicode(value));
  }

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
   * Sets the string value with escaped unicode sequences.
   *
   * @param value	the string value
   */
  public void setUnicode(String value) {
    setValue(Utils.unescapeUnicode(value));
  }

  /**
   * Returns the underlying object as string with escaped unicode sequences.
   *
   * @return		the underlying object
   * @see		#getValue()
   * @see		Utils#escapeUnicode(String)
   */
  public String getUnicode() {
    return Utils.escapeUnicode(getValue());
  }

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
   * Whether this object should have favorites support.
   * <br>
   * Default is false.
   *
   * @return		true if to support favorites
   */
  public boolean hasFavoritesSupport() {
    return false;
  }

  /**
   * Creates a new instance of the specified BaseObject-derived class and sets
   * the specified value.
   * 
   * @param cls		the class to instantiate
   * @param s		the value to set
   * @return		the instantiated object, null if class cannot be
   * 			instantiated or value is invalid
   */
  public static BaseObject newInstance(Class cls, String s) {
    BaseObject	result;
    
    try {
      result = (BaseObject) cls.getDeclaredConstructor().newInstance();
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

  /**
   * Turns the BaseObject array into a string list.
   *
   * @param array	the array to convert
   * @return		the generated string list
   */
  public static List<String> toStringList(BaseObject[] array) {
    return new ArrayList<>(Arrays.asList(toStringArray(array)));
  }

  /**
   * Turns the String array into a BaseObject array.
   * 
   * @param array	the array to convert
   * @param cls		the BaseObject derived class to use
   * @return		the generated object array
   */
  public static BaseObject[] toObjectArray(String[] array, Class cls) {
    Object	result;
    BaseObject	obj;
    int		i;
    
    result = Array.newInstance(cls, array.length);
    for (i = 0; i < array.length; i++) {
      try {
	obj = (BaseObject) cls.getDeclaredConstructor().newInstance();
	obj.setValue(array[i]);
	Array.set(result, i, obj);
      }
      catch (Exception e) {
	System.err.println("Failed to turn '" + array[i] + "' into a " + cls.getName() + " object:");
	e.printStackTrace();
      }
    }
    
    return (BaseObject[]) result;
  }

  /**
   * Turns the String array into a BaseObject array.
   *
   * @param array	the array to convert
   * @param cls		the BaseObject derived class to use
   * @return		the generated object array
   */
  public static BaseObject[] toObjectArray(List<String> array, Class cls) {
    return toObjectArray(array.toArray(new String[0]), cls);
  }

  /**
   * Returns the predefined templates.<br>
   *
   * Properties file in same location as class, e.g.:<br>
   * adams.core.base.BaseObject -> adams/core/base/BaseObject.props
   *
   * <br>
   * Format of props file:
   * <pre>
   * ID.display=text to display in GUI
   * ID.value=value to set
   * </pre>
   *
   * @return		the templates
   */
  public static synchronized <T extends BaseObject> Map<String,T> getTemplates(Class<T> cls) {
    Map<String,T>	result;
    String		prefix;
    String value;
    String		display;
    T 			test;
    T 			inst;
    String		filename;

    result = new HashMap<>();

    if (m_Properties == null)
      m_Properties = new HashMap<>();

    if (!m_Properties.containsKey(cls)) {
      filename = cls.getName().replace(".", "/") + ".props";
      try {
        m_Properties.put(cls, Properties.read(filename));
      }
      catch (Exception e) {
        m_Properties.put(cls, new Properties());
      }
    }

    try {
      test = cls.getDeclaredConstructor().newInstance();
      for (String key : m_Properties.get(cls).keySetAll(new BaseRegExp(".*" + BaseObject.SUFFIX_DISPLAY))) {
	display = m_Properties.get(cls).getProperty(key);
	prefix = key.replaceAll("\\" + SUFFIX_DISPLAY + "$", "");
	value = m_Properties.get(cls).getProperty(prefix + SUFFIX_VALUE, "");
	if (test.isValid(value)) {
	  inst = cls.getDeclaredConstructor().newInstance();
	  inst.setValue(value);
	  result.put(display, inst);
	}
      }
    }
    catch (Exception e) {
      // ignored
    }

    return result;
  }
}
