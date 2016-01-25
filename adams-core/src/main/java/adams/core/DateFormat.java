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
 * DateFormat.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.core;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import adams.core.TechnicalInformation.Field;
import adams.core.TechnicalInformation.Type;

/**
 * A threadsafe class for date formatting/parsing.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see SimpleDateFormat
 */
public class DateFormat
  implements Serializable, TechnicalInformationHandler {

  /** for serialization. */
  private static final long serialVersionUID = 304114530994640191L;

  /** the default format. */
  public final static String DEFAULT_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

  /** the actual formatter/parser. */
  protected SimpleDateFormat m_Format;

  /**
   * Initializes the formatter.
   *
   * @see		#DEFAULT_FORMAT
   */
  public DateFormat() {
    this(DEFAULT_FORMAT);
  }

  /**
   * Initializes the formatter.
   *
   * @param format	the format to use
   * @see		SimpleDateFormat#SimpleDateFormat(String)
   */
  public DateFormat(String format) {
    m_Format = new SimpleDateFormat(format);
  }

  /**
   * Returns an instance of a TechnicalInformation object, containing
   * detailed information about the technical background of this class,
   * e.g., paper reference or book this class is based on.
   *
   * @return the technical information about this class
   */
  public TechnicalInformation getTechnicalInformation() {
    TechnicalInformation 	result;

    result = new TechnicalInformation(Type.MISC);
    result.setValue(Field.AUTHOR, "Javadoc");
    result.setValue(Field.TITLE, "java.text.SimpleDateFormat");
    result.setValue(Field.HTTP, "http://download.oracle.com/javase/1,5.0/docs/api/java/text/SimpleDateFormat.html");

    return result;
  }

  /**
   * Sets a new pattern to use for formatting the dates.
   *
   * @param pattern	the new pattern
   * @see		SimpleDateFormat#applyPattern(String)
   */
  public void applyPattern(String pattern) {
    synchronized(m_Format) {
      m_Format.applyPattern(pattern);
    }
  }

  /**
   * Returns the current pattern used for formatting the dates.
   *
   * @return		the current pattern
   * @see		SimpleDateFormat#toPattern()
   */
  public String toPattern() {
    synchronized(m_Format) {
      return m_Format.toPattern();
    }
  }

  /**
   * Sets the new calendar to use.
   *
   * @param value	the new calendar
   * @see		SimpleDateFormat#setCalendar(Calendar)
   */
  public void setCalendar(Calendar value) {
    synchronized(m_Format) {
      m_Format.setCalendar(value);
    }
  }

  /**
   * Returns the currently used calendar.
   *
   * @return		the current calendar
   * @see		SimpleDateFormat#getCalendar()
   */
  public Calendar getCalendar() {
    synchronized(m_Format) {
      return m_Format.getCalendar();
    }
  }

  /**
   * Sets the timezone to use.
   *
   * @param value	the new timezone
   * @see		SimpleDateFormat#setTimeZone(TimeZone)
   */
  public void setTimeZone(TimeZone value) {
    synchronized(m_Format) {
      m_Format.setTimeZone(value);
    }
  }

  /**
   * Returns the currently used timezone.
   *
   * @return		the current timezone
   * @see		SimpleDateFormat#getTimeZone()
   */
  public TimeZone getTimeZone() {
    synchronized(m_Format) {
      return m_Format.getTimeZone();
    }
  }

  /**
   * Sets whether parsing is to be lenient or not.
   *
   * @param value	if true lenient parsing is used, otherwise not
   * @see		SimpleDateFormat#setLenient(boolean)
   */
  public void setLenient(boolean value) {
    synchronized(m_Format) {
      m_Format.setLenient(value);
    }
  }

  /**
   * Returns whether the parsing is lenient or not.
   *
   * @return		true if parsing is lenient
   * @see		SimpleDateFormat#isLenient()
   */
  public boolean isLenient() {
    synchronized(m_Format) {
      return m_Format.isLenient();
    }
  }

  /**
   * Returns the formatted string of the specified date object.
   *
   * @param date	the date to format
   * @return		the generated string
   * @see		SimpleDateFormat#format(Date)
   */
  public String format(Date date) {
    synchronized(m_Format) {
      return m_Format.format(date);
    }
  }

  /**
   * Tries to parse the given string and turns it into a date object. In
   * contrast to SimpleDateFormat's <code>parse(String)</code> method, no
   * exception is thrown here.
   *
   * @param source		the string to parse
   * @return			the generated date object, or null in case of
   * 				an error
   * @see			SimpleDateFormat#parse(String)
   */
  public Date parse(String source) {
    Date	result;

    try {
      synchronized(m_Format) {
	result = m_Format.parse(source);
      }
    }
    catch (Exception e) {
      result = null;
      e.printStackTrace();
    }

    return result;
  }

  /**
   * Checks whether the string can be parsed. Does not throw an exception.
   *
   * @param source		the string to parse
   * @return			true if string can be parsed
   * @see			SimpleDateFormat#parse(String)
   */
  public boolean check(String source) {
    try {
      synchronized(m_Format) {
	m_Format.parse(source);
      }
      return true;
    }
    catch (Exception e) {
      return false;
    }
  }

  /**
   * Returns a string representation of the string formatter.
   *
   * @return		the string
   * @see		SimpleDateFormat#toString()
   */
  @Override
  public String toString() {
    synchronized(m_Format) {
      return m_Format.toString();
    }
  }
}
