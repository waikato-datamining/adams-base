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
 * Copyright (C) 2012-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

import adams.core.annotation.MixedCopyright;
import adams.core.management.LocaleHelper;
import adams.data.DateFormatString;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * A helper class for common Date-related operations.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
@MixedCopyright(
  copyright = "Apache Foundation, POI 3.11",
  license = License.APACHE2,
  note = "Excel date handling taken from: org.apache.poi.ss.usermodel.DateUtil"
)
public class DateUtils {

  /** the timezone to use. */
  protected static TimeZone m_TimeZone = TimeZone.getDefault();
  
  /** the locale to use. */
  protected static Locale m_Locale = LocaleHelper.getSingleton().getDefault();

  public static final int BAD_DATE = -1;   // used to specify that date is invalid
  public static final int SECONDS_PER_MINUTE = 60;
  public static final int MINUTES_PER_HOUR = 60;
  public static final int HOURS_PER_DAY = 24;
  public static final int SECONDS_PER_DAY = (HOURS_PER_DAY * MINUTES_PER_HOUR * SECONDS_PER_MINUTE);
  public static final long DAY_MILLISECONDS = SECONDS_PER_DAY * 1000L;

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
   * @param check	the date to compare with
   * @return		true if "check" is before "base"
   */
  public static boolean isBefore(Date base, Date check) {
    return (base.compareTo(check) > 0);
  }
  
  /**
   * Returns whether the "check" date is after the "base" date.
   * 
   * @param base	the basis of the comparison
   * @param check	the date to compare with
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

  // set HH:MM:SS fields of cal to 00:00:00:000
  protected static Calendar dayStart(final Calendar cal) {
    cal.get(Calendar
      .HOUR_OF_DAY);   // force recalculation of internal fields
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    cal.get(Calendar
      .HOUR_OF_DAY);   // force recalculation of internal fields
    return cal;
  }

  /**
   * Given a Calendar, return the number of days since 1900/12/31.
   *
   * @return days number of days since 1900/12/31
   * @param  cal the Calendar
   * @exception IllegalArgumentException if date is invalid
   */
  protected static int absoluteDay(Calendar cal, boolean use1904windowing) {
    return cal.get(Calendar.DAY_OF_YEAR)
      + daysInPriorYears(cal.get(Calendar.YEAR), use1904windowing);
  }

  /**
   * Return the number of days in prior years since 1900
   *
   * @return    days  number of days in years prior to yr.
   * @param     yr    a year (1900 < yr < 4000)
   * @param use1904windowing
   * @exception IllegalArgumentException if year is outside of range.
   */
  protected static int daysInPriorYears(int yr, boolean use1904windowing) {
    if ((!use1904windowing && yr < 1900) || (use1904windowing && yr < 1900)) {
      throw new IllegalArgumentException("'year' must be 1900 or greater");
    }

    int yr1  = yr - 1;
    int leapDays =   yr1 / 4   // plus julian leap days in prior years
      - yr1 / 100 // minus prior century years
      + yr1 / 400 // plus years divisible by 400
      - 460;      // leap days in previous 1900 years

    return 365 * (yr - (use1904windowing ? 1904 : 1900)) + leapDays;
  }

  protected static double internalGetExcelDate(Calendar date, boolean use1904windowing) {
    if ((!use1904windowing && date.get(Calendar.YEAR) < 1900) ||
      (use1904windowing && date.get(Calendar.YEAR) < 1904)) {
      return BAD_DATE;
    }
    // Because of daylight time saving we cannot use
    //     date.getTime() - calStart.getTimeInMillis()
    // as the difference in milliseconds between 00:00 and 04:00
    // can be 3, 4 or 5 hours but Excel expects it to always
    // be 4 hours.
    // E.g. 2004-03-28 04:00 CEST - 2004-03-28 00:00 CET is 3 hours
    // and 2004-10-31 04:00 CET - 2004-10-31 00:00 CEST is 5 hours
    double fraction = (((date.get(Calendar.HOUR_OF_DAY) * 60
      + date.get(Calendar.MINUTE)
    ) * 60 + date.get(Calendar.SECOND)
    ) * 1000 + date.get(Calendar.MILLISECOND)
    ) / ( double ) DAY_MILLISECONDS;
    Calendar calStart = dayStart(date);

    double value = fraction + absoluteDay(calStart, use1904windowing);

    if (!use1904windowing && value >= 60) {
      value++;
    } else if (use1904windowing) {
      value--;
    }

    return value;
  }

  /**
   * Converts the milli-seconds (Java Epoch) to a Excel serial date (days
   * since 0-jan-1900).
   *
   * @param msec      the java epoch in msec
   * @return          the serial date
   */
  public static double msecToSerialDate(long msec) {
    return msecToSerialDate(new Date(msec));
  }

  /**
   * Converts the date to a Excel serial date (days since 0-jan-1900).
   *
   * @param date      the date
   * @return          the serial date
   */
  public static double msecToSerialDate(Date date) {
    Calendar calStart = new GregorianCalendar();
    calStart.setTime(date);   // If date includes hours, minutes, and seconds, set them to 0
    return internalGetExcelDate(calStart, false);
  }

    /**
     * Given a double, checks if it is a valid Excel date.
     *
     * @return true if valid
     * @param  value the double value
     */
    protected static boolean isValidExcelDate(double value) {
        return (value > -Double.MIN_VALUE);
    }

    protected static void setCalendar(Calendar calendar, int wholeDays,
            int millisecondsInDay, boolean use1904windowing, boolean roundSeconds) {
        int startYear = 1900;
        int dayAdjust = -1; // Excel thinks 2/29/1900 is a valid date, which it isn't
        if (use1904windowing) {
            startYear = 1904;
            dayAdjust = 1; // 1904 date windowing uses 1/2/1904 as the first day
        }
        else if (wholeDays < 61) {
            // Date is prior to 3/1/1900, so adjust because Excel thinks 2/29/1900 exists
            // If Excel date == 2/29/1900, will become 3/1/1900 in Java representation
            dayAdjust = 0;
        }
        calendar.set(startYear,0, wholeDays + dayAdjust, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, millisecondsInDay);
        if (roundSeconds) {
            calendar.add(Calendar.MILLISECOND, 500);
            calendar.clear(Calendar.MILLISECOND);
        }
    }

    /**
     * Get EXCEL date as Java Calendar with given time zone.
     * @param date  The Excel date.
     * @param use1904windowing  true if date uses 1904 windowing,
     *  or false if using 1900 date windowing.
     * @param timeZone The TimeZone to evaluate the date in
     * @param roundSeconds round to closest second
     * @return Java representation of the date, or null if date is not a valid Excel date
     */
    protected static Calendar getJavaCalendar(double date, boolean use1904windowing, TimeZone timeZone, boolean roundSeconds) {
        if (!isValidExcelDate(date)) {
            return null;
        }
        int wholeDays = (int)Math.floor(date);
        int millisecondsInDay = (int)((date - wholeDays) * DAY_MILLISECONDS + 0.5);
      Calendar calendar;
      if (timeZone != null) {
            calendar = new GregorianCalendar(timeZone);
        } else {
            calendar = new GregorianCalendar();     // using default time-zone
        }
        setCalendar(calendar, wholeDays, millisecondsInDay, use1904windowing, roundSeconds);
        return calendar;
    }

  /**
   * Converts the Excel serial date (days since 0-jan-1900) to a Java date.
   *
   * @param serial    the serial date
   * @return          the date
   */
  public static Date serialDateToDate(double serial) {
    return new Date(serialDateToMsec(serial));
  }

  /**
   * Converts the Excel serial date (days since 0-jan-1900) to milli-seconds
   * (Java Epoch).
   *
   * @param serial    the serial date
   * @return          the java epoch in msec
   */
  public static long serialDateToMsec(double serial) {
    return getJavaCalendar(serial, false, null, false).getTimeInMillis();
  }

  /**
   * Checks whether the string represents a time.
   *
   * @param s		the string to check
   * @return		true if time
   */
  public static boolean checkTime(String s) {
    return checkTime(s, null);
  }

  /**
   * Checks whether the string represents a time.
   *
   * @param s		the string to check
   * @param df          the format, can be null
   * @return		true if time
   */
  public static boolean checkTime(String s, DateFormat df) {
    if (df == null)
      df = getTimeFormatter();
    if (s.indexOf(':') > -1)
      return df.check(s);
    else
      return false;
  }

  /**
   * Checks whether the string represents a date.
   *
   * @param s		the string to check
   * @return		true if date
   */
  public static boolean checkDate(String s) {
    return checkDate(s, null);
  }

  /**
   * Checks whether the string represents a date.
   *
   * @param s		the string to check
   * @param df          the format, can be null
   * @return		true if date
   */
  public static boolean checkDate(String s, DateFormat df) {
    if (df == null)
      df = getDateFormatter();
    if (s.indexOf('-') > -1)
      return df.check(s);
    else
      return false;
  }

  /**
   * Checks whether the string represents a date/time.
   *
   * @param s		the string to check
   * @return		true if date/time
   */
  public static boolean checkDateTime(String s) {
    return checkDate(s, null);
  }

  /**
   * Checks whether the string represents a date/time.
   *
   * @param s		the string to check
   * @param df          the format, can be null
   * @return		true if date/time
   */
  public static boolean checkDateTime(String s, DateFormat df) {
    if (df == null)
      df = getTimestampFormatter();
    if ((s.indexOf('-') > -1) && (s.indexOf(':') > -1))
      return df.check(s);
    else
      return false;
  }
}
