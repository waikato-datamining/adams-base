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
 * DateUtils.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import adams.core.management.LocaleHelper;
import adams.data.DateFormatString;

/**
 * A helper class for common Date-related operations.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DateUtils {

  /** the timezone to use. */
  protected static TimeZone m_TimeZone = TimeZone.getDefault();
  
  /** the locale to use. */
  protected static Locale m_Locale = LocaleHelper.getSingleton().getDefault();

  /**
   * Sets the time zone to use globally.
   * 
   * @param value	the time zone
   */
  public static synchronized void setTimeZone(TimeZone value) {
    m_TimeZone = value;
  }
  
  /**
   * Returns the time zone in use.
   * 
   * @return		the time zone
   */
  public static synchronized TimeZone getTimeZone() {
    return m_TimeZone;
  }

  /**
   * Sets the locale to use globally.
   * 
   * @param value	the locale
   */
  public static synchronized void setLocale(Locale value) {
    m_Locale = value;
  }
  
  /**
   * Returns the locale in use.
   * 
   * @return		the locale
   */
  public static synchronized Locale getLocale() {
    return m_Locale;
  }
  
  /**
   * Returns a new {@link GregorianCalendar} object with the system-wide
   * defined locale and time zone.
   * 
   * @return		the calendar
   */
  public static synchronized Calendar getCalendar() {
    return new GregorianCalendar(getTimeZone(), getLocale());
  }
  
  /**
   * Returns a date formatting object, initialized with timestamp format.
   * 
   * @return		the formatter
   * @see		Constants#TIMESTAMP_FORMAT
   */
  public static DateFormat getTimestampFormatter() {
    return new DateFormat(Constants.TIMESTAMP_FORMAT);
  }
  
  /**
   * Returns a date formatting object, initialized with timestamp format that
   * sports milliseconds.
   * 
   * @return		the formatter
   * @see		Constants#TIMESTAMP_FORMAT_MSECS
   */
  public static DateFormat getTimestampFormatterMsecs() {
    return new DateFormat(Constants.TIMESTAMP_FORMAT_MSECS);
  }
  
  /**
   * Returns a time formatting object, initialized with time format.
   * 
   * @return		the formatter
   * @see		Constants#TIME_FORMAT
   */
  public static DateFormat getTimeFormatter() {
    return new DateFormat(Constants.TIME_FORMAT);
  }
  
  /**
   * Returns a time formatting object, initialized with time format (incl msecs).
   * 
   * @return		the formatter
   * @see		Constants#TIME_FORMAT_MSECS
   */
  public static DateFormat getTimeFormatterMsecs() {
    return new DateFormat(Constants.TIME_FORMAT_MSECS);
  }
  
  /**
   * Returns a date formatting object, initialized with date format.
   * 
   * @return		the formatter
   * @see		Constants#DATE_FORMAT
   */
  public static DateFormat getDateFormatter() {
    return new DateFormat(Constants.DATE_FORMAT);
  }

  /**
   * Checks whether the supplied parse pattern is valid.
   * 
   * @param pattern	the pattern to check
   * @return		true if valid
   */
  public static boolean isValid(String pattern) {
    try {
      new SimpleDateFormat(pattern);
      return true;
    }
    catch (Exception e) {
      return false;
    }
  }
  
  /**
   * Returns a date earlier relative to the provided one, e.g.,
   * earlier by 2 hours: earlier(date, Calendar.HOUR, 2).
   * 
   * @param date	the date to use as basis
   * @param type	the type of decrement, e.g., Calendar.HOUR
   * @param decrement	the amount to decrement the date with
   * @return		the new date
   * @see		Calendar
   */
  public static Date earlier(Date date, int type, int decrement) {
    Calendar	cal;
    
    cal = getCalendar();
    cal.setTime(date);
    cal.add(type, -Math.abs(decrement));
    
    return cal.getTime();
  }
  
  /**
   * Returns a date later relative to the provided one, e.g.,
   * later by 2 hours: later(date, Calendar.HOUR, 2).
   * 
   * @param date	the date to use as basis
   * @param type	the type of increment, e.g., Calendar.HOUR
   * @param increment	the amount to increment the date with
   * @return		the new date
   * @see		Calendar
   */
  public static Date later(Date date, int type, int increment) {
    Calendar	cal;
    
    cal = getCalendar();
    cal.setTime(date);
    cal.add(type, Math.abs(increment));
    
    return cal.getTime();
  }
  
  /**
   * Returns a date with a value changed, e.g., the hours set to a specific
   * value.
   * 
   * @param date	the date to use as basis
   * @param type	the type of value to change, e.g., Calendar.HOUR
   * @param value	the value to set
   * @return		the new date, null in case of an error
   * @see		Calendar
   */
  public static Date set(Date date, int type, int value) {
    Date	result;
    Calendar	cal;
    
    cal = getCalendar();
    cal.setLenient(false);
    cal.setTime(date);
    
    try {
      cal.set(type, value);
      result = cal.getTime();
    }
    catch (Exception e) {
      result = null;
    }
    
    return result;
  }
  
  /**
   * Returns whether the "check" date is before the "base" date.
   * 
   * @param base	the basis of the comparison
   * @param toCheck	the date to compare with
   * @return		true if "check" is before "base"
   */
  public static boolean isBefore(Date base, Date check) {
    return (base.compareTo(check) > 0);
  }
  
  /**
   * Returns whether the "check" date is after the "base" date.
   * 
   * @param base	the basis of the comparison
   * @param toCheck	the date to compare with
   * @return		true if "check" is after "base"
   */
  public static boolean isAfter(Date base, Date check) {
    return (base.compareTo(check) < 0);
  }
  
  /**
   * Returns the difference in time.
   * 
   * @param older	the older timestamp
   * @param newer	the new timestamp
   * @return		the time difference in msec, null if "older &lt; newer" does not hold
   */
  public static Long difference(Date older, Date newer) {
    if (older.compareTo(newer) < 0)
      return newer.getTime() - older.getTime();
    else
      return null;
  }
  
  /**
   * Returns the difference in time in days.
   * 
   * @param older	the older timestamp
   * @param newer	the new timestamp
   * @return		the time difference in days, null if "older &lt; newer" does not hold
   */
  public static Double differenceDays(Date older, Date newer) {
    Long	msecs;
    
    msecs = difference(older, newer);
    if (msecs == null)
      return null;
    else
      return (double) msecs / 1000.0 / 60.0 / 60.0 / 24.0;
  }
  
  /**
   * Returns the difference in time in hours.
   * 
   * @param older	the older timestamp
   * @param newer	the new timestamp
   * @return		the time difference in hours, null if "older &lt; newer" does not hold
   */
  public static Double differenceHours(Date older, Date newer) {
    Long	msecs;
    
    msecs = difference(older, newer);
    if (msecs == null)
      return null;
    else
      return (double) msecs / 1000.0 / 60.0 / 60.0;
  }
  
  /**
   * Returns the difference in time in minutes.
   * 
   * @param older	the older timestamp
   * @param newer	the new timestamp
   * @return		the time difference in minutes, null if "older &lt; newer" does not hold
   */
  public static Double differenceMinutes(Date older, Date newer) {
    Long	msecs;
    
    msecs = difference(older, newer);
    if (msecs == null)
      return null;
    else
      return (double) msecs / 1000.0 / 60.0;
  }
  
  /**
   * Returns the difference in time in seconds.
   * 
   * @param older	the older timestamp
   * @param newer	the new timestamp
   * @return		the time difference in seconds, null if "older &lt; newer" does not hold
   */
  public static Double differenceSeconds(Date older, Date newer) {
    Long	msecs;
    
    msecs = difference(older, newer);
    if (msecs == null)
      return null;
    else
      return (double) msecs / 1000.0;
  }

  /**
   * Turns the milli-seconds into a string: hh:mm:ss.sss
   * 
   * @param msec	the milli-seconds to convert
   * @return		the string representation
   */
  public static String msecToString(Long msec) {
    StringBuilder	result;
    long		value;
    long		position;
    
    result = new StringBuilder();
    
    if (msec == null) {
      result.append("invalid");
    }
    else {
      value = msec;
      
      // hours
      position = value / (60 * 60 * 1000);
      if (position < 10)
	result.append("0");
      result.append(position);

      // minutes
      value    = value % (60 * 60 * 1000);
      position = value / (60 * 1000);
      result.append(":");
      if (position < 10)
	result.append("0");
      result.append(position);
      
      // seconds
      value    = value % (60 * 1000);
      position = value / (1000);
      result.append(":");
      if (position < 10)
	result.append("0");
      result.append(position);
      
      // msecs
      position = value % (1000);
      result.append(".");
      if (position < 10)
	result.append("0");
      if (position < 100)
	result.append("0");
      result.append(position);
    }
    
    return result.toString();
  }
  
  /**
   * Parses the given date/time string according to the format.
   * 
   * @param s		the date/time string to parse
   * @param format	the format to use for parsing
   * @return		the parsed date or null in case of an error
   */
  public static Date parseString(String s, String format) {
    return parseString(s, new DateFormatString(format));
  }
  
  /**
   * Parses the given date/time string according to the format.
   * 
   * @param s		the date/time string to parse
   * @param format	the format to use for parsing
   * @return		the parsed date or null in case of an error
   */
  public static Date parseString(String s, DateFormatString format) {
    return format.toDateFormat().parse(s);
  }
  
  /**
   * Returns a new DateTime object with the current date/time.
   * 
   * @return		the current date/time
   */
  public static DateTime now() {
    return new DateTime();
  }
  
  /**
   * Returns a new Time object with the current time.
   * 
   * @return		the current time
   */
  public static Time nowTime() {
    Calendar	cal;
    
    cal = new GregorianCalendar(getLocale());
    cal.setTime(new Date());
    cal.set(Calendar.YEAR, 0);
    cal.set(Calendar.MONTH, 0);
    cal.set(Calendar.DAY_OF_MONTH, 1);

    return new Time(cal.getTime());
  }
  
  /**
   * Returns a new Date object with the current date.
   * 
   * @return		the current date
   */
  public static Date today() {
    Calendar	cal;
    
    cal = new GregorianCalendar(getLocale());
    cal.setTime(new Date());
    cal.set(Calendar.HOUR, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);

    return cal.getTime();
  }
}
