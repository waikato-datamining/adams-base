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
 * LittleEndian.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.core;

/**
 * Helper class for conversions related to Little-Endian.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LittleEndian {

  /**
   * Checks whether a certain bit is set.
   *
   * @param b		the byte to check
   * @param mask	the mask to use
   */
  public static boolean isBitSet(byte b, int mask) {
    return ((b & mask) > 0);
  }

  /**
   * Turns 2 bytes into an int.
   *
   * @param bytes	the bytes to convert
   * @return		the int value
   */
  public static int bytesToShort(byte[] bytes) {
    assert(bytes.length == 2);
    return
      ((bytes[1] & 0xFF) << 8)
      | ((bytes[0] & 0xFF) << 0);
  }

  /**
   * Turns 4 bytes into an int.
   *
   * @param bytes	the bytes to convert
   * @return		the int value
   */
  public static int bytesToInt(byte[] bytes) {
    assert(bytes.length == 4);
    return
      ((bytes[3] & 0xFF) << 24)
      | ((bytes[2] & 0xFF) << 16)
      | ((bytes[1] & 0xFF) << 8)
      | ((bytes[0] & 0xFF) << 0);
  }

  /**
   * Turns 8 bytes into a float.
   *
   * @param bytes	the bytes to convert
   * @return		the float value
   */
  public static float bytesToFloat(byte[] bytes) {
    assert(bytes.length == 4);
    return Float.intBitsToFloat(
      ((bytes[3] & 0xFF) << 24)
	| ((bytes[2] & 0xFF) << 16)
	| ((bytes[1] & 0xFF) << 8)
	| ((bytes[0] & 0xFF) << 0));
  }

  /**
   * Turns 8 bytes into a double.
   *
   * @param bytes	the bytes to convert
   * @return		the double value
   */
  public static double bytesToDouble(byte[] bytes) {
    assert(bytes.length == 8);
    return Double.longBitsToDouble(
      ((bytes[7] & 0xFFL) << 56)
      | ((bytes[6] & 0xFFL) << 48)
      | ((bytes[5] & 0xFFL) << 40)
      | ((bytes[4] & 0xFFL) << 32)
      | ((bytes[3] & 0xFFL) << 24)
      | ((bytes[2] & 0xFFL) << 16)
      | ((bytes[1] & 0xFFL) << 8)
      | ((bytes[0] & 0xFFL) << 0));
  }

  /**
   * Turns the bytes into a string (terminated by a NUL or end of bytes).
   *
   * @param bytes	the bytes to convert
   * @return		the string value
   */
  public static String bytesToString(byte[] bytes) {
    StringBuilder 	result;
    int		i;

    result = new StringBuilder();
    for (i = 0; i < bytes.length; i++) {
      if (bytes[0] == 0)
	break;
      result.append((char) bytes[i]);
    }

    return result.toString();
  }
}
