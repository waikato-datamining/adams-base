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
 * MetaData.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.data.indexedsplits;

import adams.core.Utils;

import java.util.HashMap;

/**
 * For storing meta-data.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class MetaData
  extends HashMap<String,Object> {

  private static final long serialVersionUID = 3534089057338513027L;

  /**
   * Returns the integer value associated with the key or the default value
   * if not present or not a number or string representing an integer.
   *
   * @param key		the key to retrieve
   * @param defValue	the default value
   * @return		the retrieved value or the default value
   */
  public int getInteger(String key, int defValue) {
    int 	result;
    Object	value;

    result = defValue;

    if (containsKey(key)) {
      value = get(key);
      if (value instanceof Number)
        result = ((Number) value).intValue();
      else if (Utils.isInteger("" + value))
        result = Integer.parseInt("" + value);
    }

    return result;
  }

  /**
   * Returns the double value associated with the key or the default value
   * if not present or not a number or string representing a double.
   *
   * @param key		the key to retrieve
   * @param defValue	the default value
   * @return		the retrieved value or the default value
   */
  public double getDouble(String key, double defValue) {
    double	result;
    Object	value;

    result = defValue;

    if (containsKey(key)) {
      value = get(key);
      if (value instanceof Number)
        result = ((Number) value).doubleValue();
      else if (Utils.isDouble("" + value))
        result = Double.parseDouble("" + value);
    }

    return result;
  }

  /**
   * Returns the boolean value associated with the key or the default value
   * if not present or not a boolean or string representing a boolean.
   *
   * @param key		the key to retrieve
   * @param defValue	the default value
   * @return		the retrieved value or the default value
   */
  public boolean getBoolean(String key, boolean defValue) {
    boolean	result;
    Object	value;

    result = defValue;

    if (containsKey(key)) {
      value = get(key);
      if (value instanceof Boolean)
        result = (Boolean) value;
      else if (Utils.isBoolean("" + value))
        result = Boolean.parseBoolean("" + value);
    }

    return result;
  }

  /**
   * Returns the string value associated with the key or the default value
   * if not present or not a string.
   *
   * @param key		the key to retrieve
   * @param defValue	the default value
   * @return		the retrieved value or the default value
   */
  public String getString(String key, String defValue) {
    String	result;
    Object	value;

    result = defValue;

    if (containsKey(key)) {
      value = get(key);
      if (value instanceof String)
        result = (String) value;
    }

    return result;
  }
}
