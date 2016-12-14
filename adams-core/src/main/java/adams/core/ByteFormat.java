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
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

import java.io.Serializable;

/**
 * Formatting class for turning byte amounts into kb, mb, etc.
 * <br><br>
 * Format: {b|B}[.N]{k|K|m|M|g|G|t|T|p|P|e|E|z|Z|y|Y[i]}
 * <br><br>
 * <ul>
 *   <li>b|B<br>
 *   "b" outputs the amount without thousand separators, "B" includes them.</li>
 *   <li>.N<br>
 *   Prints "N" decimal places</li>
 *   <li>k|K|m|M|g|G|t|T|p|P|e|E|z|Z|y|Y<br>
 *   Specifies the unit to use: k|K=kilobytes, m|M=megabytes, g|G=gigabytes,
 *   t|T=terabytes, p|P=petabytes, e|E=exabytes, z|Z=zettabytes, y|Y=yottabytes<br>
 *   Lower case does not add a specifier like "KB", upper case does.</li>
 *   <li>Adding "i" at the end uses 1024 as base instead of 1000.</li>
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
    BYTES(1000, 0, ""),
    KILO_BYTES(1000, 1, "KB"),
    MEGA_BYTES(1000, 2, "MB"),
    GIGA_BYTES(1000, 3, "GB"),
    TERA_BYTES(1000, 4, "TB"),
    PETA_BYTES(1000, 5, "PB"),
    EXA_BYTES(1000, 6, "EB"),
    ZETTA_BYTES(1000, 7, "ZB"),
    YOTTA_BYTES(1000, 8, "YB"),

    KIBI_BYTES(1024, 1, "KiB"),
    MEBI_BYTES(1024, 2, "MiB"),
    GIBI_BYTES(1024, 3, "GiB"),
    TEBI_BYTES(1024, 4, "TiB"),
    PEBI_BYTES(1024, 5, "PiB"),
    EXBI_BYTES(1024, 6, "EiB"),
    ZEBI_BYTES(1024, 7, "ZiB"),
    YOBI_BYTES(1024, 8, "YiB");

    /** the base. */
    private int m_Base;

    /** the power to base. */
    private int m_Power;

    /** the unit string. */
    private String m_Unit;

    /**
     * Initializes the unit.
     *
     * @param power	the power
     * @param unit	the unit string
     */
    private Unit(int base, int power, String unit) {
      m_Base  = base;
      m_Power = power;
      m_Unit  = unit;
    }

    /**
     * Returns the base.
     *
     * @return		the base
     */
    public int getBase() {
      return m_Base;
    }

    /**
     * Returns the power to base.
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
     * Parses the given string (k|K|m|M|g|G|t|T|p|P|e|E|z|Z|y|Y)(i)? and returns the
     * corresponding unit.
     *
     * @param s		the string to parse
     * @return		the unit, null if invalid string
     */
    public static Unit parse(String s) {
      s = s.toLowerCase();

      switch (s) {
	case "k":
	  return Unit.KILO_BYTES;
	case "m":
	  return Unit.MEGA_BYTES;
	case "g":
	  return Unit.GIGA_BYTES;
	case "t":
	  return Unit.TERA_BYTES;
	case "p":
	  return Unit.PETA_BYTES;
	case "e":
	  return Unit.EXA_BYTES;
	case "z":
	  return Unit.ZETTA_BYTES;
	case "y":
	  return Unit.YOTTA_BYTES;

	case "ki":
	  return Unit.KIBI_BYTES;
	case "mi":
	  return Unit.MEBI_BYTES;
	case "gi":
	  return Unit.GIBI_BYTES;
	case "ti":
	  return Unit.TEBI_BYTES;
	case "pi":
	  return Unit.PEBI_BYTES;
	case "ei":
	  return Unit.EXBI_BYTES;
	case "zi":
	  return Unit.ZEBI_BYTES;
	case "yi":
	  return Unit.YOBI_BYTES;

	default:
	  return null;
      }
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

    parseFormat(format, false);
  }

  /**
   * Parses the format.
   *
   * @param format	the format to parse
   * @param justTest 	if true then parameters derived won't be set
   */
  protected boolean parseFormat(String format, boolean justTest) {
    String	s;
    boolean	useSep;
    int		decimals;
    String	decimalsStr;
    boolean	addUnit;
    Unit	unit;

    if (format == null)
      return false;
    if (format.length() < 2)
      return false;

    // thousand separators?
    s = format.substring(0, 1);
    if (!s.equals("b") && !s.equals("B"))
      return false;
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
    if (!((s.length()== 1) || ((s.length() == 2) && s.endsWith("i"))))
      return false;

    // add unit?
    addUnit = s.substring(0, 1).equals(s.substring(0, 1).toUpperCase());

    // unit
    unit = Unit.parse(s);
    if (unit == null)
      return false;

    // passed all tests
    if (!justTest) {
      m_Format = format;
      m_UseThousandSeparators = useSep;
      m_NumDecimals = decimals;
      m_Unit = unit;
      m_AddUnit = addUnit;
    }

    return true;
  }

  /**
   * Tests the format.
   *
   * @param format	the format to test
   * @return		true if valid
   */
  public boolean isValid(String format) {
    return parseFormat(format, true);
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

    value  = bytes / Math.pow(m_Unit.getBase(), m_Unit.getPower());
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
   * Turns the number of bytes into ZB.
   * Uses 1024 as base.
   *
   * @param bytes	the number of bytes to format
   * @param decimals	the number of decimals after the decimal point
   * @return		the ZB string
   */
  public static String toZettaBytes(double bytes, int decimals) {
    return new ByteFormat("B." + decimals + "Z").format(bytes);
  }

  /**
   * Turns the number of bytes into YB.
   * Uses 1024 as base.
   *
   * @param bytes	the number of bytes to format
   * @param decimals	the number of decimals after the decimal point
   * @return		the YB string
   */
  public static String toYottaBytes(double bytes, int decimals) {
    return new ByteFormat("B." + decimals + "Y").format(bytes);
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
    if (exp >= 8)
      return toYottaBytes(bytes, decimals);
    else if (exp >= 7)
      return toZettaBytes(bytes, decimals);
    else if (exp >= 6)
      return toExaBytes(bytes, decimals);
    else if (exp >= 5)
      return toPetaBytes(bytes, decimals);
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

  /**
   * Turns the number of bytes into KiB.
   * Uses 1000 as base.
   *
   * @param bytes	the number of bytes to format
   * @param decimals	the number of decimals after the decimal point
   * @return		the KiB string
   */
  public static String toKibiBytes(double bytes, int decimals) {
    return new ByteFormat("B." + decimals + "Ki").format(bytes);
  }

  /**
   * Turns the number of bytes into MiB.
   * Uses 1000 as base.
   *
   * @param bytes	the number of bytes to format
   * @param decimals	the number of decimals after the decimal point
   * @return		the MiB string
   */
  public static String toMebiBytes(double bytes, int decimals) {
    return new ByteFormat("B." + decimals + "Mi").format(bytes);
  }

  /**
   * Turns the number of bytes into GiB.
   * Uses 1000 as base.
   *
   * @param bytes	the number of bytes to format
   * @param decimals	the number of decimals after the decimal point
   * @return		the GiB string
   */
  public static String toGibiBytes(double bytes, int decimals) {
    return new ByteFormat("B." + decimals + "Gi").format(bytes);
  }

  /**
   * Turns the number of bytes into TiB.
   * Uses 1000 as base.
   *
   * @param bytes	the number of bytes to format
   * @param decimals	the number of decimals after the decimal point
   * @return		the TiB string
   */
  public static String toTebiBytes(double bytes, int decimals) {
    return new ByteFormat("B." + decimals + "Ti").format(bytes);
  }

  /**
   * Turns the number of bytes into PiB.
   * Uses 1000 as base.
   *
   * @param bytes	the number of bytes to format
   * @param decimals	the number of decimals after the decimal point
   * @return		the PiB string
   */
  public static String toPebiBytes(double bytes, int decimals) {
    return new ByteFormat("B." + decimals + "Pi").format(bytes);
  }

  /**
   * Turns the number of bytes into EiB.
   * Uses 1000 as base.
   *
   * @param bytes	the number of bytes to format
   * @param decimals	the number of decimals after the decimal point
   * @return		the EiB string
   */
  public static String toExbiBytes(double bytes, int decimals) {
    return new ByteFormat("B." + decimals + "Ei").format(bytes);
  }

  /**
   * Turns the number of bytes into ZiB.
   * Uses 1000 as base.
   *
   * @param bytes	the number of bytes to format
   * @param decimals	the number of decimals after the decimal point
   * @return		the ZiB string
   */
  public static String toZebiBytes(double bytes, int decimals) {
    return new ByteFormat("B." + decimals + "Zi").format(bytes);
  }

  /**
   * Turns the number of bytes into YiB.
   * Uses 1000 as base.
   *
   * @param bytes	the number of bytes to format
   * @param decimals	the number of decimals after the decimal point
   * @return		the YB string
   */
  public static String toYobiBytes(double bytes, int decimals) {
    return new ByteFormat("B." + decimals + "Yi").format(bytes);
  }

  /**
   * Turns the number of bytes in the shortest representation (KiB, MiB, GiB, ...).
   * Uses 1000 as base.
   *
   * @param bytes	the bytes to convert
   * @param decimals	the number of decimals after the decimal point
   * @return		the optimal number string
   */
  public static String toBestFitBiBytes(double bytes, int decimals) {
    double	exp;

    exp = Math.log(bytes) / Math.log(1000);
    if (exp >= 8)
      return toYobiBytes(bytes, decimals);
    else if (exp >= 7)
      return toZebiBytes(bytes, decimals);
    else if (exp >= 6)
      return toExbiBytes(bytes, decimals);
    else if (exp >= 5)
      return toPebiBytes(bytes, decimals);
    else if (exp >= 4)
      return toTebiBytes(bytes, decimals);
    else if (exp >= 3)
      return toGibiBytes(bytes, decimals);
    else if (exp >= 2)
      return toMebiBytes(bytes, decimals);
    else if (exp >= 1)
      return toKibiBytes(bytes, decimals);
    else
      return bytes + "B";
  }
}
