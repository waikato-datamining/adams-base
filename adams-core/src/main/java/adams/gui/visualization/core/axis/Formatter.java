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
 * Formatter.java
 * Copyright (C) 2008-2010 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.core.axis;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A wrapper class for Java formatters DecimalFormat and SimpleDateFormat.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see DecimalFormat
 * @see SimpleDateFormat
 */
public class Formatter
  implements Serializable {

  /** for serialization. */
  private static final long serialVersionUID = 2565690777491622761L;

  /** the constant for NaN. */
  public final static String NAN = "NaN";

  /** the Java formatter. */
  protected Format m_Format;

  /**
   * Initializes the formatter.
   *
   * @param format	the Java formatter to use
   * @see		#setFormat(Format)
   */
  public Formatter(Format format) {
    super();

    setFormat(format);
  }

  /**
   * Sets the Java formatter to use. Only DecimalFormat and SimpleDateFormat
   * are allowed.
   *
   * @param format	the Java formatter to use
   */
  public void setFormat(Format format) {
    if (format instanceof DecimalFormat)
      m_Format = format;
    else if (format instanceof SimpleDateFormat)
      m_Format = format;
    else
      throw new IllegalArgumentException(
          "Formatters of type " + format.getClass().getName() + " are not allowed!");
  }

  /**
   * Returns in the Java formatter used internally.
   *
   * @return		the formatter
   */
  public Format getFormat() {
    return m_Format;
  }

  /**
   * Applies the given pattern.
   *
   * @param pattern	the pattern to use
   */
  public void applyPattern(String pattern) {
    if (m_Format instanceof DecimalFormat)
      ((DecimalFormat) m_Format).applyPattern(pattern);
    else if (m_Format instanceof SimpleDateFormat)
      ((SimpleDateFormat) m_Format).applyPattern(pattern);
    else
      throw new IllegalArgumentException(
          "Formatters of type " + m_Format.getClass().getName() + " are not supported!");
  }

  /**
   * Returns the pattern in use by the Java formatter.
   *
   * @return		the pattern in use
   */
  public String toPattern() {
    if (m_Format instanceof DecimalFormat)
      return ((DecimalFormat) m_Format).toPattern();
    else if (m_Format instanceof SimpleDateFormat)
      return ((SimpleDateFormat) m_Format).toPattern();
    else
      throw new IllegalArgumentException(
          "Formatters of type " + m_Format.getClass().getName() + " are not supported!");
  }

  /**
   * Parses the given string. Returns Double.NaN in case of an error.
   *
   * @param s		the string to parse
   * @return		the value or Double.NaN in case of an error
   */
  public Double parse(String s) {
    Double	result;

    if (s.equals(NAN)) {
      result = Double.NaN;
    }
    else {
      try {
        if (m_Format instanceof DecimalFormat)
          result = (Double) ((DecimalFormat) m_Format).parse(s);
        else if (m_Format instanceof SimpleDateFormat)
          result = new Double(((SimpleDateFormat) m_Format).parse(s).getTime());
        else
          throw new IllegalArgumentException(
      	"Formatters of type " + m_Format.getClass().getName() + " are not supported!");
      }
      catch (Exception e) {
        e.printStackTrace();
        result = Double.NaN;
      }
    }

    return result;
  }

  /**
   * Formats the given value and returns the string.
   *
   * @param value	the value to turn into a string
   * @return		the generated string
   */
  public String format(Double value) {
    String	result;

    if (Double.isNaN(value)) {
      result = NAN;
    }
    else {
      try {
        if (m_Format instanceof DecimalFormat)
          result = ((DecimalFormat) m_Format).format(value);
        else if (m_Format instanceof SimpleDateFormat)
          result =((SimpleDateFormat) m_Format).format(new Date(value.longValue()));
        else
          throw new IllegalArgumentException(
      	"Formatters of type " + m_Format.getClass().getName() + " are not supported!");
      }
      catch (Exception e) {
        e.printStackTrace();
        result = NAN;
      }
    }

    return result;
  }

  /**
   * Returns an instance of a decimal formatter with a specified pattern.
   *
   * @param format	the Java formatter to use
   * @param pattern	the pattern to use, ignored if null
   * @return		the formatter
   */
  protected static Formatter getFormatter(Format format, String pattern) {
    Formatter		result;

    result = new Formatter(format);
    if (pattern != null)
      result.applyPattern(pattern);

    return result;
  }

  /**
   * A string representation of the formatter.
   *
   * @return		a string representation
   */
  public String toString() {
    return m_Format.getClass().getName() + "/" + toPattern();
  }

  /**
   * Returns an instance of a decimal formatter.
   *
   * @return		the formatter
   */
  public static Formatter getDecimalFormatter() {
    return getFormatter(new DecimalFormat(), null);
  }

  /**
   * Returns an instance of a decimal formatter with a specified pattern.
   *
   * @param pattern	the pattern to use, ignored if null
   * @return		the formatter
   */
  public static Formatter getDecimalFormatter(String pattern) {
    return getFormatter(new DecimalFormat(), pattern);
  }

  /**
   * Returns an instance of a date formatter.
   *
   * @return		the formatter
   */
  public static Formatter getDateFormatter() {
    return getFormatter(new SimpleDateFormat(), null);
  }

  /**
   * Returns an instance of a date formatter with a specified pattern.
   *
   * @param pattern	the pattern to use, ignored if null
   * @return		the formatter
   */
  public static Formatter getDateFormatter(String pattern) {
    return getFormatter(new SimpleDateFormat(), pattern);
  }
}