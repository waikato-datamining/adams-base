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
 * BusinessDays.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.core;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Enum for defining business days.
 *
 * See here:
 * <a href="https://en.wikipedia.org/wiki/Workweek_and_weekend#Around_the_world">https://en.wikipedia.org/wiki/Workweek_and_weekend#Around_the_world</a>
 */
public enum BusinessDays
  implements EnumWithCustomDisplay<BusinessDays> {

  MONDAY_TO_FRIDAY("Mon-Fri"),
  MONDAY_TO_SATURDAY("Mon-Sat"),
  SATURDAY_TO_THURSDAY("Sat-Thu"),
  SUNDAY_TO_THURSDAY("Sun-Thu"),
  SUNDAY_TO_FRIDAY("Sun-Fri");

  /** the display string. */
  private String m_Display;

  /** the commandline string. */
  private String m_Raw;

  /**
   * The constructor.
   *
   * @param display	the string to use as display
   */
  private BusinessDays(String display) {
    m_Display = display;
    m_Raw     = super.toString();
  }

  /**
   * Returns the display string.
   *
   * @return		the display string
   */
  public String toDisplay() {
    return m_Display;
  }

  /**
   * Returns the raw enum string.
   *
   * @return		the raw enum string
   */
  public String toRaw() {
    return m_Raw;
  }

  /**
   * Returns the display string.
   *
   * @return		the display string
   */
  public String toString() {
    return toDisplay();
  }

  /**
   * Parses the given string and returns the associated enum.
   *
   * @param s		the string to parse
   * @return		the enum or null if not found
   */
  public BusinessDays parse(String s) {
    BusinessDays result;

    result = null;

    // default parsing
    try {
      result = valueOf(s);
    }
    catch (Exception e) {
      // ignored
    }

    // try display
    if (result == null) {
      for (BusinessDays bd : values()) {
	if (bd.toDisplay().equals(s)) {
	  result = bd;
	  break;
	}
      }
    }

    return result;
  }

  /**
   * Checks whether the provided date represents a business day for this
   * enum.
   *
   * @param d		the date to check
   * @return		true if a business day
   */
  public boolean isBusinessDay(Date d) {
    Calendar 	cal;
    int		dow;

    cal = new GregorianCalendar();
    cal.setTime(d);
    dow = cal.get(GregorianCalendar.DAY_OF_WEEK);

    switch (this) {
      case MONDAY_TO_FRIDAY:
	return (dow != GregorianCalendar.SATURDAY) && (dow != GregorianCalendar.SUNDAY);
      case MONDAY_TO_SATURDAY:
	return (dow != GregorianCalendar.SUNDAY);
      case SATURDAY_TO_THURSDAY:
	return (dow != GregorianCalendar.SUNDAY) && (dow != GregorianCalendar.FRIDAY);
      case SUNDAY_TO_THURSDAY:
	return (dow != GregorianCalendar.FRIDAY) && (dow != GregorianCalendar.SATURDAY);
      case SUNDAY_TO_FRIDAY:
	return (dow != GregorianCalendar.SATURDAY);
      default:
	throw new IllegalStateException("Unhandled business days enum: " + this);
    }
  }
}
