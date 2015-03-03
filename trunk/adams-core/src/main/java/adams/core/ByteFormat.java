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
 * ByteFormat.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

import java.io.Serializable;

/**
 * Formatting class for turning byte amounts into kb, mb, etc.
 * <p/>
 * Format: {b|B}[.N]{k|K|m|M|g|G|t|T|p|P|e|E}
 * <p/>
 * <ul>
 *   <li>b|B<br/>
 *   "b" outputs the amount without thousand separators, "B" includes them.</li>
 *   <li>.N<br/>
 *   Prints "N" decimal places</li>
 *   <li>k|K|m|M|g|G|t|T|p|P|e|E<br/>
 *   Specifies the unit to use: k|K=kilobytes, m|M=megabytes, g|G=gigabytes,
 *   t|T=terabytes, p|P=petabytes, e|E=exabytes<br/>
 *   Lower case does not add a specifier like "KB", upper case does.</li>
 * </ul>
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ByteFormat
  implements Serializable {

  /** for serialization. */
  private static final long serialVersionUID = -7848808006030837876L;

  /**
   * The available units.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum Unit {
    /** just bytes. */
    BYTES(0, ""),
    /** kilo. */
    KILO_BYTES(1, "KB"),
    /** mega. */
    MEGA_BYTES(2, "MB"),
    /** giga. */
    GIGA_BYTES(3, "GB"),
    /** tera. */
    TERA_BYTES(4, "TB"),
    /** peta. */
    PETA_BYTES(5, "PB"),
    /** exa. */
    EXA_BYTES(6, "EB");

    /** the power to base 1024. */
    private int m_Power;

    /** the unit string. */
    private String m_Unit;

    /**
     * Initializes the unit.
     *
     * @param power	the power
     * @param unit	the unit string
     */
    private Unit(int power, String unit) {
      m_Power = power;
      m_Unit  = unit;
    }

    /**
     * Returns the power to base 1024.
     *
     * @return		the power
     */
    public int getPower() {
      return m_Power;
    }

    /**
     * Returns the unit string.
     *
     * @return		the unit string
     */
    public String getUnit() {
      return m_Unit;
    }

    /**
     * Parses the given string (k|K|m|M|g|G|t|T|p|P|e|E) and returns the
     * corresponding unit.
     *
     * @param s		the string to parse
     * @return		the unit, null if invalid string
     */
    public static Unit parse(String s) {
      s = s.toLowerCase();
      if (s.equals("k"))
	return Unit.KILO_BYTES;
      else if (s.equals("m"))
	return Unit.MEGA_BYTES;
      else if (s.equals("g"))
	return Unit.GIGA_BYTES;
      else if (s.equals("t"))
	return Unit.TERA_BYTES;
      else if (s.equals("p"))
	return Unit.PETA_BYTES;
      else if (s.equals("e"))
	return Unit.EXA_BYTES;
      else
	return null;
    }
  }

  /** the format string. */
  protected String m_Format;

  /** whether to use thousand separators or not. */
  protected boolean m_UseThousandSeparators;

  /** the number of decimals to use. */
  protected int m_NumDecimals;

  /** the conversion unit. */
  protected Unit m_Unit;

  /** whether to add a unit specifier at the end. */
  protected boolean m_AddUnit;

  /**
   * Initializes the formatter.
   *
   * @param format	the format to use
   */
  public ByteFormat(String format) {
    super();

    m_Format                = null;
    m_UseThousandSeparators = false;
    m_NumDecimals           = 0;
    m_Unit                  = Unit.BYTES;
    m_AddUnit               = false;

    parseFormat(format);
  }

  /**
   * Parses the format.
   *
   * @param format	the format to parse
   */
  protected void parseFormat(String format) {
    String	s;
    boolean	useSep;
    int		decimals;
    String	decimalsStr;
    boolean	addUnit;
    Unit	unit;

    if (format == null)
      return;
    if (format.length() < 2)
      return;

    // thousand separators?
    s = format.substring(0, 1);
    if (!s.equals("b") && !s.equals("B"))
      return;
    useSep = s.equals("B");

    // decimals?
    decimals    = 0;
    s           = format.substring(1, format.length());
    decimalsStr = "";
    if (s.startsWith(".")) {
      s = s.substring(1);
      while (s.length() > 0) {
	if ((s.charAt(0) >= '0') && (s.charAt(0) <= '9')) {
	  decimalsStr += s.charAt(0);
	  s            = s.substring(1);
	}
	else {
	  break;
	}
      }
    }
    if (decimalsStr.length() > 0)
      decimals = Integer.parseInt(decimalsStr);
    if (s.length() != 1)
      return;

    // add unit?
    addUnit = s.equals(s.toUpperCase());

    // unit
    unit = Unit.parse(s);
    if (unit == null)
      return;

    // passed all tests
    m_Format                = format;
    m_UseThousandSeparators = useSep;
    m_NumDecimals           = decimals;
    m_Unit                  = unit;
    m_AddUnit               = addUnit;
  }

  /**
   * Returns the format being used.
   *
   * @return		the format, null if invalid one provided
   */
  public String getFormat() {
    return m_Format;
  }

  /**
   * Formats the given byte amount according to the format.
   *
   * @param bytes	the amount to format
   * @return		the formatted amount
   */
  public String format(long bytes) {
    return format((double) bytes);
  }

  /**
   * Formats the given byte amount according to the format.
   *
   * @param bytes	the amount to format
   * @return		the formatted amount
   */
  public String format(double bytes) {
    String	result;
    double	value;

    if (m_Format == null)
      return "" + bytes;

    value  = bytes / Math.pow(1024.0, m_Unit.getPower());
    result = Utils.doubleToStringFixed(value, m_NumDecimals);
    if (m_AddUnit)
      result = insertThousandSeparators(result) + m_Unit.getUnit();

    return result;
  }

  /**
   * Returns a short string description.
   *
   * @return		the format string
   */
  public String toString() {
    return "format=" + m_Format;
  }

  /**
   * Inserts thousand separators into the number string (integer or float).
   *
   * @param number	the string to inject with separators
   * @return		the processed string
   */
  public static String insertThousandSeparators(String number) {
    StringBuilder	result;
    int			i;
    int			start;
    int			count;

    // scientific notation?
    if (number.indexOf('E') > -1)
      return number.replace("E", "*10^");

    count  = 0;
    start  = number.indexOf('.');
    if (start == -1)
      start = number.length();
    result = new StringBuilder(number.substring(start));
    start--;
    for (i = start; i >= 0; i--) {
      if ((i > 0) && (count == 3)) {
	result.insert(0, ",");
	count = 0;
      }
      result.insert(0, number.charAt(i));
      count++;
    }

    return result.toString();
  }

  /**
   * Turns the number of bytes into KB.
   * Uses 1024 as base.
   *
   * @param bytes	the number of bytes to format
   * @param decimals	the number of decimals after the decimal point
   * @return		the KB string
   */
  public static String toKiloBytes(double bytes, int decimals) {
    return new ByteFormat("B." + decimals + "K").format(bytes);
  }

  /**
   * Turns the number of bytes into MB.
   * Uses 1024 as base.
   *
   * @param bytes	the number of bytes to format
   * @param decimals	the number of decimals after the decimal point
   * @return		the MB string
   */
  public static String toMegaBytes(double bytes, int decimals) {
    return new ByteFormat("B." + decimals + "M").format(bytes);
  }

  /**
   * Turns the number of bytes into GB.
   * Uses 1024 as base.
   *
   * @param bytes	the number of bytes to format
   * @param decimals	the number of decimals after the decimal point
   * @return		the GB string
   */
  public static String toGigaBytes(double bytes, int decimals) {
    return new ByteFormat("B." + decimals + "G").format(bytes);
  }

  /**
   * Turns the number of bytes into TB.
   * Uses 1024 as base.
   *
   * @param bytes	the number of bytes to format
   * @param decimals	the number of decimals after the decimal point
   * @return		the TB string
   */
  public static String toTeraBytes(double bytes, int decimals) {
    return new ByteFormat("B." + decimals + "T").format(bytes);
  }

  /**
   * Turns the number of bytes into PB.
   * Uses 1024 as base.
   *
   * @param bytes	the number of bytes to format
   * @param decimals	the number of decimals after the decimal point
   * @return		the PB string
   */
  public static String toPetaBytes(double bytes, int decimals) {
    return new ByteFormat("B." + decimals + "P").format(bytes);
  }

  /**
   * Turns the number of bytes into EB.
   * Uses 1024 as base.
   *
   * @param bytes	the number of bytes to format
   * @param decimals	the number of decimals after the decimal point
   * @return		the EB string
   */
  public static String toExaBytes(double bytes, int decimals) {
    return new ByteFormat("B." + decimals + "E").format(bytes);
  }

  /**
   * Turns the number of bytes in the shortest representation (KB, MB, GB, ...).
   * Uses 1024 as base.
   *
   * @param bytes	the bytes to convert
   * @param decimals	the number of decimals after the decimal point
   * @return		the optimal number string
   */
  public static String toBestFitBytes(double bytes, int decimals) {
    double	exp;

    exp = Math.log(bytes) / Math.log(1024);
    if (exp >= 6)
      return toExaBytes(bytes, decimals);
    else if (exp >= 5)
      return toTeraBytes(bytes, decimals);
    else if (exp >= 4)
      return toTeraBytes(bytes, decimals);
    else if (exp >= 3)
      return toGigaBytes(bytes, decimals);
    else if (exp >= 2)
      return toMegaBytes(bytes, decimals);
    else if (exp >= 1)
      return toKiloBytes(bytes, decimals);
    else
      return bytes + "B";
  }
}
