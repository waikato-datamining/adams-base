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
 * PeriodicityHelper.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.timeseries;

import gnu.trove.list.array.TDoubleArrayList;

import java.util.Calendar;
import java.util.Date;

import adams.core.Properties;

/**
 * Helper class for periodicity related calculations.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PeriodicityHelper {

  /** the properties file. */
  public final static String FILENAME = "adams/data/timeseries/Periodicity.props";
  
  /** the properties. */
  protected static Properties m_Properties;

  /**
   * Returns the properties.
   *
   * @return		the properties
   */
  protected static synchronized Properties getProperties() {
    Properties	result;

    if (m_Properties == null) {
      try {
	result = Properties.read(FILENAME);
      }
      catch (Exception e) {
	result = new Properties();
      }
      m_Properties = result;
    }

    return m_Properties;
  }
  
  /**
   * Calculates the yearly values.
   * 
   * @param left	the left-most value
   * @param leftDate	the left-most date
   * @param leftCal	the calendar for the left-most date
   * @param right	the right-most value
   * @param rightDate	the right-most date
   * @param rightCal	the calendar for the right-most date
   * @return		the raw values
   */
  protected static TDoubleArrayList calculateYearly(double left, Date leftDate, Calendar leftCal, double right, Date rightDate, Calendar rightCal) {
    TDoubleArrayList	result;
    
    result = new TDoubleArrayList();
    
    leftCal.set(Calendar.HOUR_OF_DAY, 0);
    leftCal.set(Calendar.MINUTE, 0);
    leftCal.set(Calendar.SECOND, 0);
    leftCal.set(Calendar.DAY_OF_MONTH, 1);
    leftCal.set(Calendar.MONTH, 0);
    leftCal.add(Calendar.YEAR, -1);

    rightCal.set(Calendar.HOUR_OF_DAY, 23);
    rightCal.set(Calendar.MINUTE, 59);
    rightCal.set(Calendar.SECOND, 59);
    rightCal.set(Calendar.DAY_OF_MONTH, 1);
    rightCal.set(Calendar.MONTH, 0);
    rightCal.add(Calendar.YEAR, 1);

    while (leftCal.getTimeInMillis() <= rightCal.getTimeInMillis()) {
      result.add(leftCal.getTimeInMillis());
      leftCal.add(Calendar.YEAR, 1);
    }
    
    return result;
  }
  
  /**
   * Calculates the quarterly values.
   * 
   * @param left	the left-most value
   * @param leftDate	the left-most date
   * @param leftCal	the calendar for the left-most date
   * @param right	the right-most value
   * @param rightDate	the right-most date
   * @param rightCal	the calendar for the right-most date
   * @return		the raw values
   */
  protected static TDoubleArrayList calculateQuarterly(double left, Date leftDate, Calendar leftCal, double right, Date rightDate, Calendar rightCal) {
    TDoubleArrayList	result;
    
    result = new TDoubleArrayList();
    
    leftCal.set(Calendar.HOUR_OF_DAY, 0);
    leftCal.set(Calendar.MINUTE, 0);
    leftCal.set(Calendar.SECOND, 0);
    leftCal.set(Calendar.DAY_OF_MONTH, 1);
    if (leftCal.get(Calendar.MONTH) == 0)
      leftCal.add(Calendar.MONTH, -3);
    else
      leftCal.add(Calendar.MONTH, -(leftCal.get(Calendar.MONTH) % 3));

    rightCal.set(Calendar.HOUR_OF_DAY, 23);
    rightCal.set(Calendar.MINUTE, 59);
    rightCal.set(Calendar.SECOND, 59);
    rightCal.set(Calendar.DAY_OF_MONTH, 1);
    if (rightCal.get(Calendar.MONTH) == 11)
      rightCal.add(Calendar.MONTH, 3);
    else
      rightCal.add(Calendar.MONTH, (rightCal.get(Calendar.MONTH) % 3));

    while (leftCal.getTimeInMillis() <= rightCal.getTimeInMillis()) {
      result.add(leftCal.getTimeInMillis());
      leftCal.add(Calendar.MONTH, 3);
    }

    return result;
  }
  
  /**
   * Calculates the monthly values.
   * 
   * @param left	the left-most value
   * @param leftDate	the left-most date
   * @param leftCal	the calendar for the left-most date
   * @param right	the right-most value
   * @param rightDate	the right-most date
   * @param rightCal	the calendar for the right-most date
   * @return		the raw values
   */
  protected static TDoubleArrayList calculateMonthly(double left, Date leftDate, Calendar leftCal, double right, Date rightDate, Calendar rightCal) {
    TDoubleArrayList	result;
    
    result = new TDoubleArrayList();
    
    leftCal.set(Calendar.HOUR_OF_DAY, 0);
    leftCal.set(Calendar.MINUTE, 0);
    leftCal.set(Calendar.SECOND, 0);
    leftCal.set(Calendar.DAY_OF_MONTH, 1);
    leftCal.add(Calendar.MONTH, -1);

    rightCal.set(Calendar.HOUR_OF_DAY, 23);
    rightCal.set(Calendar.MINUTE, 59);
    rightCal.set(Calendar.SECOND, 59);
    rightCal.set(Calendar.DAY_OF_MONTH, 1);
    rightCal.add(Calendar.MONTH, 1);

    while (leftCal.getTimeInMillis() <= rightCal.getTimeInMillis()) {
      result.add(leftCal.getTimeInMillis());
      leftCal.add(Calendar.MONTH, 1);
    }
    
    return result;
  }
  
  /**
   * Calculates the weekly values.
   * 
   * @param left	the left-most value
   * @param leftDate	the left-most date
   * @param leftCal	the calendar for the left-most date
   * @param right	the right-most value
   * @param rightDate	the right-most date
   * @param rightCal	the calendar for the right-most date
   * @return		the raw values
   */
  protected static TDoubleArrayList calculateWeekly(double left, Date leftDate, Calendar leftCal, double right, Date rightDate, Calendar rightCal) {
    TDoubleArrayList	result;
    
    result = new TDoubleArrayList();
    
    leftCal.set(Calendar.HOUR_OF_DAY, 0);
    leftCal.set(Calendar.MINUTE, 0);
    leftCal.set(Calendar.SECOND, 0);
    leftCal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
    leftCal.add(Calendar.DAY_OF_YEAR, -7);

    rightCal.set(Calendar.HOUR_OF_DAY, 23);
    rightCal.set(Calendar.MINUTE, 59);
    rightCal.set(Calendar.SECOND, 59);
    rightCal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
    rightCal.add(Calendar.DAY_OF_YEAR, 7);

    while (leftCal.getTimeInMillis() <= rightCal.getTimeInMillis()) {
      result.add(leftCal.getTimeInMillis());
      leftCal.add(Calendar.DAY_OF_YEAR, 7);
    }

    return result;
  }
  
  /**
   * Calculates the daily values.
   * 
   * @param left	the left-most value
   * @param leftDate	the left-most date
   * @param leftCal	the calendar for the left-most date
   * @param right	the right-most value
   * @param rightDate	the right-most date
   * @param rightCal	the calendar for the right-most date
   * @return		the raw values
   */
  protected static TDoubleArrayList calculateDaily(double left, Date leftDate, Calendar leftCal, double right, Date rightDate, Calendar rightCal) {
    TDoubleArrayList	result;
    
    result = new TDoubleArrayList();
    
    leftCal.set(Calendar.HOUR_OF_DAY, 0);
    leftCal.set(Calendar.MINUTE, 0);
    leftCal.set(Calendar.SECOND, 0);
    leftCal.add(Calendar.DAY_OF_YEAR, -1);

    rightCal.set(Calendar.HOUR_OF_DAY, 23);
    rightCal.set(Calendar.MINUTE, 59);
    rightCal.set(Calendar.SECOND, 59);
    rightCal.add(Calendar.DAY_OF_YEAR, 1);

    while (leftCal.getTimeInMillis() <= rightCal.getTimeInMillis()) {
      result.add(leftCal.getTimeInMillis());
      leftCal.add(Calendar.DAY_OF_YEAR, 1);
    }

    return result;
  }
  
  /**
   * Calculates the half-daily values.
   * 
   * @param left	the left-most value
   * @param leftDate	the left-most date
   * @param leftCal	the calendar for the left-most date
   * @param right	the right-most value
   * @param rightDate	the right-most date
   * @param rightCal	the calendar for the right-most date
   * @return		the raw values
   */
  protected static TDoubleArrayList calculateHalfDaily(double left, Date leftDate, Calendar leftCal, double right, Date rightDate, Calendar rightCal) {
    TDoubleArrayList	result;
    
    result = new TDoubleArrayList();
    
    if (leftCal.get(Calendar.HOUR_OF_DAY) < 12)
      leftCal.set(Calendar.HOUR_OF_DAY, 0);
    else
      leftCal.set(Calendar.HOUR_OF_DAY, 12);
    leftCal.set(Calendar.MINUTE, 0);
    leftCal.set(Calendar.SECOND, 0);
    leftCal.add(Calendar.HOUR_OF_DAY, -12);

    if (rightCal.get(Calendar.HOUR_OF_DAY) >= 12)
      rightCal.set(Calendar.HOUR_OF_DAY, 23);
    else
      rightCal.set(Calendar.HOUR_OF_DAY, 12);
    rightCal.set(Calendar.MINUTE, 59);
    rightCal.set(Calendar.SECOND, 59);
    rightCal.add(Calendar.HOUR_OF_DAY, 12);

    while (leftCal.getTimeInMillis() <= rightCal.getTimeInMillis()) {
      result.add(leftCal.getTimeInMillis());
      leftCal.add(Calendar.HOUR_OF_DAY, 12);
    }

    return result;
  }
  
  /**
   * Calculates the hourly values.
   * 
   * @param left	the left-most value
   * @param leftDate	the left-most date
   * @param leftCal	the calendar for the left-most date
   * @param right	the right-most value
   * @param rightDate	the right-most date
   * @param rightCal	the calendar for the right-most date
   * @return		the raw values
   */
  protected static TDoubleArrayList calculateHourly(double left, Date leftDate, Calendar leftCal, double right, Date rightDate, Calendar rightCal) {
    TDoubleArrayList	result;
    
    result = new TDoubleArrayList();
    
    leftCal.set(Calendar.MINUTE, 0);
    leftCal.set(Calendar.SECOND, 0);
    leftCal.add(Calendar.HOUR_OF_DAY, -1);

    rightCal.set(Calendar.MINUTE, 59);
    rightCal.set(Calendar.SECOND, 59);
    rightCal.add(Calendar.HOUR_OF_DAY, 1);

    while (leftCal.getTimeInMillis() <= rightCal.getTimeInMillis()) {
      result.add(leftCal.getTimeInMillis());
      leftCal.add(Calendar.HOUR_OF_DAY, 1);
    }

    return result;
  }
  
  /**
   * Calculates the half-hourly values.
   * 
   * @param left	the left-most value
   * @param leftDate	the left-most date
   * @param leftCal	the calendar for the left-most date
   * @param right	the right-most value
   * @param rightDate	the right-most date
   * @param rightCal	the calendar for the right-most date
   * @return		the raw values
   */
  protected static TDoubleArrayList calculateHalfHourly(double left, Date leftDate, Calendar leftCal, double right, Date rightDate, Calendar rightCal) {
    TDoubleArrayList	result;
    
    result = new TDoubleArrayList();
    
    if (leftCal.get(Calendar.MINUTE) < 30)
      leftCal.set(Calendar.MINUTE, 0);
    else
      leftCal.set(Calendar.MINUTE, 30);
    leftCal.set(Calendar.SECOND, 0);
    leftCal.add(Calendar.HOUR_OF_DAY, -1);

    if (rightCal.get(Calendar.HOUR_OF_DAY) >= 30)
      rightCal.set(Calendar.MINUTE, 59);
    else
      rightCal.set(Calendar.MINUTE, 30);
    rightCal.set(Calendar.SECOND, 59);
    rightCal.add(Calendar.HOUR_OF_DAY, 1);

    while (leftCal.getTimeInMillis() <= rightCal.getTimeInMillis()) {
      result.add(leftCal.getTimeInMillis());
      leftCal.add(Calendar.MINUTE, 30);
    }

    return result;
  }
  
  /**
   * Calculates the per minute values.
   * 
   * @param left	the left-most value
   * @param leftDate	the left-most date
   * @param leftCal	the calendar for the left-most date
   * @param right	the right-most value
   * @param rightDate	the right-most date
   * @param rightCal	the calendar for the right-most date
   * @return		the raw values
   */
  protected static TDoubleArrayList calculatePerMinute(double left, Date leftDate, Calendar leftCal, double right, Date rightDate, Calendar rightCal) {
    TDoubleArrayList	result;
    
    result = new TDoubleArrayList();
    
    leftCal.set(Calendar.SECOND, 0);
    leftCal.add(Calendar.MINUTE, -1);

    rightCal.set(Calendar.SECOND, 59);
    rightCal.add(Calendar.MINUTE, 1);

    while (leftCal.getTimeInMillis() <= rightCal.getTimeInMillis()) {
      result.add(leftCal.getTimeInMillis());
      leftCal.add(Calendar.MINUTE, 1);
    }

    return result;
  }
  
  /**
   * Calculates the per periodicity values.
   * 
   * @param type	the type of periodicity to calculate
   * @param left	the left-most value
   * @param leftDate	the left-most date
   * @param leftCal	the calendar for the left-most date
   * @param right	the right-most value
   * @param rightDate	the right-most date
   * @param rightCal	the calendar for the right-most date
   * @return		the raw values
   */
  public static TDoubleArrayList calculate(PeriodicityType type, double left, Date leftDate, Calendar leftCal, double right, Date rightDate, Calendar rightCal) {
    switch (type) {
      case YEARLY:
	return calculateYearly(left, leftDate, leftCal, right, rightDate, rightCal);
      case QUARTERLY:
	return calculateQuarterly(left, leftDate, leftCal, right, rightDate, rightCal);
      case MONTHLY:
	return calculateMonthly(left, leftDate, leftCal, right, rightDate, rightCal);
      case WEEKLY:
	return calculateWeekly(left, leftDate, leftCal, right, rightDate, rightCal);
      case DAILY:
	return calculateDaily(left, leftDate, leftCal, right, rightDate, rightCal);
      case HALF_DAILY:
	return calculateHalfDaily(left, leftDate, leftCal, right, rightDate, rightCal);
      case HOURLY:
	return calculateHourly(left, leftDate, leftCal, right, rightDate, rightCal);
      case HALF_HOURLY:
	return calculateHalfHourly(left, leftDate, leftCal, right, rightDate, rightCal);
      case PER_MINUTE:
	return calculatePerMinute(left, leftDate, leftCal, right, rightDate, rightCal);
      default:
	return new TDoubleArrayList();
    }
  }
  
  /**
   * Returns the format associated with the periodicity type.
   * 
   * @param type	the type of periodicity
   * @return		the associated type
   */
  public static String getFormat(PeriodicityType type) {
    return getProperties().getProperty("Format." + type, "y/M/d H:mm");
  }
}
