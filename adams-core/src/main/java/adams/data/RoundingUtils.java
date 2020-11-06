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
 * RoundUtils.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.data;

/**
 * Helper class for rounding.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RoundingUtils {

  /**
   * Round to specific number of digits after decimal point.
   *
   * @param value	the value to round
   * @param decimals	the decimals after the decimal point to use
   * @return		the rounded value
   */
  public static double round(double value, int decimals) {
    double	factor;

    factor = Math.pow(10, decimals);
    return Math.round(value * factor) / factor;
  }

  /**
   * Round to specific number of digits after decimal point.
   *
   * @param value	the value to round
   * @param decimals	the decimals after the decimal point to use
   * @return		the rounded value
   */
  public static float round(float value, int decimals) {
    float	factor;

    factor = (float) Math.pow(10, decimals);
    return Math.round(value * factor) / factor;
  }

  /**
   * Round up to specific number of digits after decimal point.
   *
   * @param value	the value to round
   * @param decimals	the decimals after the decimal point to use
   * @return		the rounded value
   */
  public static double ceil(double value, int decimals) {
    double	factor;

    factor = Math.pow(10, decimals);
    return Math.ceil(value * factor) / factor;
  }

  /**
   * Round up to specific number of digits after decimal point.
   *
   * @param value	the value to round
   * @param decimals	the decimals after the decimal point to use
   * @return		the rounded value
   */
  public static float ceil(float value, int decimals) {
    float	factor;

    factor = (float) Math.pow(10, decimals);
    return (float) Math.ceil(value * factor) / factor;
  }

  /**
   * Round down to specific number of digits after decimal point.
   *
   * @param value	the value to round
   * @param decimals	the decimals after the decimal point to use
   * @return		the rounded value
   */
  public static double floor(double value, int decimals) {
    double	factor;

    factor = Math.pow(10, decimals);
    return Math.floor(value * factor) / factor;
  }

  /**
   * Round down to specific number of digits after decimal point.
   *
   * @param value	the value to round
   * @param decimals	the decimals after the decimal point to use
   * @return		the rounded value
   */
  public static float floor(float value, int decimals) {
    float	factor;

    factor = (float) Math.pow(10, decimals);
    return (float) Math.floor(value * factor) / factor;
  }

  /**
   * Round (to nearest integer) to specific number of digits after decimal point.
   *
   * @param value	the value to round
   * @param decimals	the decimals after the decimal point to use
   * @return		the rounded value
   */
  public static double rint(double value, int decimals) {
    double	factor;

    factor = Math.pow(10, decimals);
    return Math.rint(value * factor) / factor;
  }

  /**
   * Round (to nearest integer) to specific number of digits after decimal point.
   *
   * @param value	the value to round
   * @param decimals	the decimals after the decimal point to use
   * @return		the rounded value
   */
  public static float rint(float value, int decimals) {
    float	factor;

    factor = (float) Math.pow(10, decimals);
    return (float) Math.rint(value * factor) / factor;
  }

  /**
   * Apply the specified rounding type.
   *
   * @param type 	the rounding type
   * @param value	the value to round
   * @param decimals	the decimals after the decimal point to use
   * @return		the rounded value
   */
  public static double apply(RoundingType type, double value, int decimals) {
    switch (type) {
      case ROUND:
        if (decimals == 0)
	  return Math.round(value);
        else
          return round(value, decimals);
      case CEILING:
        if (decimals == 0)
	  return (int) Math.ceil(value);
        else
          return ceil(value, decimals);
      case FLOOR:
        if (decimals == 0)
	  return Math.floor(value);
        else
          return floor(value, decimals);
      case RINT:
        if (decimals == 0)
	  return Math.rint(value);
        else
          return rint(value, decimals);
      default:
	throw new IllegalStateException("Unhandled type: " + type);
    }
  }

  /**
   * Apply the specified rounding type.
   *
   * @param type 	the rounding type
   * @param value	the value to round
   * @param decimals	the decimals after the decimal point to use
   * @return		the rounded value
   */
  public static float apply(RoundingType type, float value, int decimals) {
    switch (type) {
      case ROUND:
        if (decimals == 0)
	  return Math.round(value);
        else
          return round(value, decimals);
      case CEILING:
        if (decimals == 0)
	  return (float) Math.ceil(value);
        else
          return ceil(value, decimals);
      case FLOOR:
        if (decimals == 0)
	  return (float) Math.floor(value);
        else
          return floor(value, decimals);
      case RINT:
        if (decimals == 0)
	  return (float) Math.rint(value);
        else
          return rint(value, decimals);
      default:
	throw new IllegalStateException("Unhandled type: " + type);
    }
  }

  /**
   * Fixes rounding issues, ensuring that only the specified number of
   * decimals get returned.
   *
   * @param s		the string to process
   * @param decimals	the number of decimals to ensure
   * @return		the processed string
   */
  protected static String fixDecimals(String s, int decimals) {
    int		pos;

    pos = s.lastIndexOf('.');
    if (s.length() > pos + decimals + 1)
      s = s.substring(0, pos + decimals + 1);

    return s;
  }

  /**
   * Apply the specified rounding type and generates a string.
   *
   * @param type 	the rounding type
   * @param value	the value to round
   * @param decimals	the decimals after the decimal point to use
   * @return		the rounded value as string
   */
  public static String toString(RoundingType type, double value, int decimals) {
    String	result;
    double	rounded;

    rounded = apply(type, value, decimals);
    result  = fixDecimals("" + rounded, decimals);
    return result;
  }

  /**
   * Apply the specified rounding type and generates a string.
   *
   * @param type 	the rounding type
   * @param value	the value to round
   * @param decimals	the decimals after the decimal point to use
   * @return		the rounded value as string
   */
  public static String toString(RoundingType type, float value, int decimals) {
    String	result;
    float	rounded;

    rounded = apply(type, value, decimals);
    result  = fixDecimals("" + rounded, decimals);
    return result;
  }
}
