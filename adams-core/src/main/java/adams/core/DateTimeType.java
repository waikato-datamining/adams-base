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
 * DateType.java
 * Copyright (C) 2013-2023 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

/**
 * Lists various date/time types.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public enum DateTimeType {
  /** msecs since 1970. */
  MSECS,
  /** msecs since 1970. */
  MSECS_LONG,
  /** seconds since 1970. */
  SECONDS,
  /** seconds since 1970. */
  SECONDS_LONG,
  /** java's Date. */
  DATE,
  /** adams' DateTime. */
  DATETIME,
  /** adams' DateTimeMsec. */
  DATETIMEMSEC,
  /** adams' Time. */
  TIME,
  /** adams' DateTimeMsec. */
  TIMEMSEC,
  /** BaseDate. */
  BASEDATE,
  /** BaseDateTime. */
  BASEDATETIME,
  /** BaseDateTimeMsec. */
  BASEDATETIMEMSEC,
  /** BaseTime. */
  BASETIME,
  /** BaseTime. */
  BASETIMEMSEC,
  /** Julian date. */
  JULIANDATE,
  /** Julian date. */
  JULIANDATE_LONG,
  /** days since 0-jan-1900 date (Excel). */
  SERIAL_DATETIME,
  /** days since 0-jan-1900 date (Excel). */
  SERIAL_DATETIME_LONG,
}
