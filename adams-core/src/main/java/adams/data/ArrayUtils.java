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
 * ArrayUtils.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.data;

import adams.core.Utils;

import java.lang.reflect.Array;

/**
 * Helper class for arrays.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class ArrayUtils {

  /**
   * Converts the primitive array into its equivalent object version.
   * Supports: boolean, char, byte, short, int, long, float, double
   *
   * @param arrayIn	the primitive array to convert
   * @return		the object version
   */
  public static Object primitiveToObject(Object arrayIn) {
    Object	arrayOut;
    int		i;
    int		len;

    len = Array.getLength(arrayIn);
    if (arrayIn instanceof byte[]) {
      arrayOut = new Byte[len];
      for (i = 0; i < len; i++)
	Array.set(arrayOut, i, Array.getByte(arrayIn, i));
    }
    else if (arrayIn instanceof short[]) {
      arrayOut = new Short[len];
      for (i = 0; i < len; i++)
	Array.set(arrayOut, i, Array.getShort(arrayIn, i));
    }
    else if (arrayIn instanceof int[]) {
      arrayOut = new Integer[len];
      for (i = 0; i < len; i++)
	Array.set(arrayOut, i, Array.getInt(arrayIn, i));
    }
    else if (arrayIn instanceof long[]) {
      arrayOut = new Long[len];
      for (i = 0; i < len; i++)
	Array.set(arrayOut, i, Array.getLong(arrayIn, i));
    }
    else if (arrayIn instanceof float[]) {
      arrayOut = new Float[len];
      for (i = 0; i < len; i++)
	Array.set(arrayOut, i, Array.getFloat(arrayIn, i));
    }
    else if (arrayIn instanceof double[]) {
      arrayOut = new Double[len];
      for (i = 0; i < len; i++)
	Array.set(arrayOut, i, Array.getDouble(arrayIn, i));
    }
    else if (arrayIn instanceof char[]) {
      arrayOut = new Character[len];
      for (i = 0; i < len; i++)
	Array.set(arrayOut, i, Array.getChar(arrayIn, i));
    }
    else if (arrayIn instanceof boolean[]) {
      arrayOut = new Boolean[len];
      for (i = 0; i < len; i++)
	Array.set(arrayOut, i, Array.getBoolean(arrayIn, i));
    }
    else {
      throw new IllegalArgumentException("Unhandled class: " + Utils.classToString(arrayIn));
    }

    return arrayOut;
  }

  /**
   * Converts the object array into its primitive equivalent.
   * Supports: Boolean, Character, Byte, Short, Integer, Long, Float, Double
   *
   * @param arrayIn	the object array to convert
   * @return		the primitive array
   */
  public static Object objectToPrimitive(Object arrayIn) {
    Object	arrayOut;
    int		i;
    int		len;

    len = Array.getLength(arrayIn);
    if (arrayIn instanceof Byte[]) {
      arrayOut = new byte[len];
      for (i = 0; i < len; i++)
	Array.setByte(arrayOut, i, (Byte) Array.get(arrayIn, i));
    }
    else if (arrayIn instanceof Short[]) {
      arrayOut = new short[len];
      for (i = 0; i < len; i++)
	Array.setShort(arrayOut, i, (Short) Array.get(arrayIn, i));
    }
    else if (arrayIn instanceof Integer[]) {
      arrayOut = new int[len];
      for (i = 0; i < len; i++)
	Array.setInt(arrayOut, i, (Integer) Array.get(arrayIn, i));
    }
    else if (arrayIn instanceof Long[]) {
      arrayOut = new long[len];
      for (i = 0; i < len; i++)
	Array.setLong(arrayOut, i, (Long) Array.get(arrayIn, i));
    }
    else if (arrayIn instanceof Float[]) {
      arrayOut = new float[len];
      for (i = 0; i < len; i++)
	Array.setFloat(arrayOut, i, (Float) Array.get(arrayIn, i));
    }
    else if (arrayIn instanceof Double[]) {
      arrayOut = new double[len];
      for (i = 0; i < len; i++)
	Array.setDouble(arrayOut, i, (Double) Array.get(arrayIn, i));
    }
    else if (arrayIn instanceof Character[]) {
      arrayOut = new double[len];
      for (i = 0; i < len; i++)
	Array.setChar(arrayOut, i, (Character) Array.get(arrayIn, i));
    }
    else if (arrayIn instanceof Boolean[]) {
      arrayOut = new double[len];
      for (i = 0; i < len; i++)
	Array.setBoolean(arrayOut, i, (Boolean) Array.get(arrayIn, i));
    }
    else {
      throw new IllegalArgumentException("Unhandled class: " + Utils.classToString(arrayIn));
    }

    return arrayOut;
  }
}
