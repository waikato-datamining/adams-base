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
 * AdditionalOptions.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.core;

import java.util.Hashtable;

import adams.core.Utils;

/**
 * An extended Hashtable class for easier retrieval of options.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class AdditionalOptions
  extends Hashtable<String, String> {

  /** for serialization. */
  private static final long serialVersionUID = -2378624659828475769L;

  /**
   * Initializes an empty container.
   */
  public AdditionalOptions() {
    super();
  }

  /**
   * Stores the given boolean value under the specified key.
   *
   * @param key		the key to store the value under
   * @param value	the boolean value to store
   */
  public void putBoolean(String key, Boolean value) {
    put(key, Boolean.toString(value));
  }

  /**
   * Stores the given integer value under the specified key.
   *
   * @param key		the key to store the value under
   * @param value	the integer value to store
   */
  public void putInteger(String key, Integer value) {
    put(key, Integer.toString(value));
  }

  /**
   * Stores the given double value under the specified key.
   *
   * @param key		the key to store the value under
   * @param value	the double value to store
   */
  public void putDouble(String key, Double value) {
    put(key, Double.toString(value));
  }

  /**
   * Stores the given string value under the specified key.
   *
   * @param key		the key to store the value under
   * @param value	the string value to store
   */
  public void putString(String key, String value) {
    put(key, value);
  }

  /**
   * Returns the stored boolean.
   *
   * @param key		the key to look for
   * @return		the value or null if not found
   */
  public Boolean getBoolean(String key) {
    return getBoolean(key, null);
  }

  /**
   * Returns the stored boolean or the default value if not found.
   *
   * @param key		the key to look for
   * @param defValue	the default value
   * @return		the value or the default value if not found
   */
  public Boolean getBoolean(String key, Boolean defValue) {
    Boolean	result;

    result = defValue;

    try {
      if (containsKey(key))
	result = Boolean.parseBoolean(get(key));
    }
    catch (Exception e) {
      result = defValue;
    }

    return result;
  }

  /**
   * Returns the stored integer.
   *
   * @param key		the key to look for
   * @return		the value or null if not found
   */
  public Integer getInteger(String key) {
    return getInteger(key, null);
  }

  /**
   * Returns the stored integer or the default value if not found.
   *
   * @param key		the key to look for
   * @param defValue	the default value
   * @return		the value or the default value if not found
   */
  public Integer getInteger(String key, Integer defValue) {
    Integer	result;

    result = defValue;

    try {
      if (containsKey(key))
	result = Integer.parseInt(get(key));
    }
    catch (Exception e) {
      result = defValue;
    }

    return result;
  }

  /**
   * Returns the stored double.
   *
   * @param key		the key to look for
   * @return		the value or null if not found
   */
  public Double getDouble(String key) {
    return getDouble(key, null);
  }

  /**
   * Returns the stored double or the default value if not found.
   *
   * @param key		the key to look for
   * @param defValue	the default value
   * @return		the value or the default value if not found
   */
  public Double getDouble(String key, Double defValue) {
    Double	result;

    result = defValue;

    try {
      if (containsKey(key))
	result = Utils.toDouble(get(key));
    }
    catch (Exception e) {
      result = defValue;
    }

    return result;
  }

  /**
   * Returns the stored string.
   *
   * @param key		the key to look for
   * @return		the value or null if not found
   */
  public String getString(String key) {
    return getString(key, null);
  }

  /**
   * Returns the stored string or the default value if not found.
   *
   * @param key		the key to look for
   * @param defValue	the default value
   * @return		the value or the default value if not found
   */
  public String getString(String key, String defValue) {
    if (containsKey(key))
      return get(key);
    else
      return defValue;
  }
}
