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
 * SecondFormat.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

import java.io.Serializable;

/**
 * Formatting class for turning byte amounts into kb, mb, etc.
 * <br><br>
 * Format: {t|T}[.N]{s|m|h|d|w}
 * <br><br>
 * <ul>
 *   <li>b|B<br>
 *   "t" outputs the amount without thousand separators, "T" includes them.</li>
 *   <li>.N<br>
 *   Prints "N" decimal places</li>
 *   <li>S|M|H|D|W<br>
 *   Specifies the unit to use: s=seconds, m=minutes, h=hours, d=days, w=weeks<br>
 *   Lower case does not add a specifier like "ms", upper case does.</li>
 * </ul>
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class SecondFormat
  implements Serializable {

  /** for serialization. */
  private static final long serialVersionUID = -7848808006030837876L;

  /**
   * The available units.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   */
  public enum Unit {
    SECONDS(1, "s"),
    MINUTES(60, "m"),
    HOURS(60*60, "h"),
    DAYS(24*60*60, "d"),
    WEEKS(7*24*60*60, "w");

    /** the factor. */
    private long m_Factor;

    /** the unit string. */
    private String m_Unit;

    /**
     * Initializes the unit.
     *
     * @param factor	the factor
     * @param unit	the unit string
     */
    private Unit(long factor, String unit) {
      m_Factor = factor;
      m_Unit   = unit;
    }

    /**
     * Returns the factor.
     *
     * @return		the factor
     */
    public long getFactor() {
      return m_Factor;
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
     * Parses the given string (u|m|S|M|H|D|W) and returns the
     * corresponding unit.
     *
     * @param s		the string to parse
     * @return		the unit, null if invalid string
     */
    public static Unit parse(String s) {
      s = s.toLowerCase();

      switch (s) {
	case "s":
	  return Unit.SECONDS;
	case "m":
	  return Unit.MINUTES;
	case "h":
	  return Unit.HOURS;
	case "d":
	  return Unit.DAYS;
	case "w":
	  return Unit.WEEKS;

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
  public SecondFormat(String format) {
    super();

    m_Format                = null;
    m_Unit                  = Unit.SECONDS;
    m_UseThousandSeparators = false;
    m_NumDecimals           = 0;
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
    Unit 	unit;

    if (format == null)
      return false;
    if (format.length() < 1)
      return false;

    // thousand separators?
    s = format.substring(0, 1);
    if (!s.equals("t") && !s.equals("T"))
      return false;
    useSep = s.equals("T");

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
    if (!(s.length()== 1))
      return false;

    // add unit?
    addUnit = s.substring(0, 1).equals(s.substring(0, 1).toUpperCase());

    // unit
    unit = Unit.parse(s);
    if (unit == null)
      return false;

    // passed all tests
    if (!justTest) {
      m_Format                = format;
      m_UseThousandSeparators = useSep;
      m_NumDecimals           = decimals;
      m_Unit                  = unit;
      m_AddUnit               = addUnit;
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
   * Formats the given second amount according to the format.
   *
   * @param seconds	the amount to format
   * @return			the formatted amount
   */
  public String format(long seconds) {
    return format((double) seconds);
  }

  /**
   * Formats the given second amount according to the format.
   *
   * @param seconds	the amount to format
   * @return			the formatted amount
   */
  public String format(double seconds) {
    String	result;
    double	value;

    if (m_Format == null)
      return "" + seconds;

    value  = seconds / (double) m_Unit.getFactor();
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
   * Turns the number of seconds into minutes.
   *
   * @param seconds	the number of seconds to format
   * @param decimals	the number of decimals after the decimal point
   * @return		the minute string
   */
  public static String toMinutes(double seconds, int decimals) {
    return new SecondFormat("T." + decimals + "M").format(seconds);
  }

  /**
   * Turns the number of seconds into hours.
   *
   * @param seconds	the number of seconds to format
   * @param decimals	the number of decimals after the decimal point
   * @return		the hour string
   */
  public static String toHours(double seconds, int decimals) {
    return new SecondFormat("T." + decimals + "H").format(seconds);
  }

  /**
   * Turns the number of seconds into days.
   *
   * @param seconds	the number of seconds to format
   * @param decimals	the number of decimals after the decimal point
   * @return		the day string
   */
  public static String toDays(double seconds, int decimals) {
    return new SecondFormat("T." + decimals + "D").format(seconds);
  }

  /**
   * Turns the number of seconds into weeks.
   *
   * @param seconds	the number of seconds to format
   * @param decimals	the number of decimals after the decimal point
   * @return		the week string
   */
  public static String toWeeks(double seconds, int decimals) {
    return new SecondFormat("T." + decimals + "W").format(seconds);
  }

  /**
   * Turns the number of seconds in the shortest representation (sec, min, ...).
   *
   * @param seconds	the seconds to convert
   * @param decimals	the number of decimals after the decimal point
   * @return		the optimal number string
   */
  public static String toBestFit(double seconds, int decimals) {
    if (seconds >= Unit.WEEKS.getFactor())
      return toWeeks(seconds, decimals);
    else if (seconds >= Unit.DAYS.getFactor())
      return toDays(seconds, decimals);
    else if (seconds >= Unit.HOURS.getFactor())
      return toHours(seconds, decimals);
    else if (seconds >= Unit.MINUTES.getFactor())
      return toMinutes(seconds, decimals);
    else
      return seconds + "s";
  }

  /**
   * Turns the number of seconds into a mixed representation (1w 2d 3h 4m 5s).
   *
   * @param seconds	the seconds to convert
   * @return			the mixed string
   */
  public static String toMixed(double seconds) {
    StringBuilder 	result;
    Unit[]		units;
    int			i;

    result = new StringBuilder();
    units = Unit.values();

    for (i = units.length - 1; i >= 0; i--) {
      if (seconds >= units[i].getFactor()) {
        if (result.length() > 0)
          result.append(" ");
	result.append((int) (seconds / units[i].getFactor())).append(units[i].getUnit());
	seconds = seconds % units[i].getFactor();
      }
    }

    return result.toString();
  }
}
