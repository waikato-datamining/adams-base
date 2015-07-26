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
 * DateTimeMsec.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

import java.util.Date;

/**
 * A thin wrapper to distibguish between date/teim and date/time msec.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DateTimeMsec
  extends Date {

  /** for serialization. */
  private static final long serialVersionUID = -8619742925203285132L;

  /**
   * Default constructor.
   */
  public DateTimeMsec() {
    super();
  }

  /**
   * Initializes the date/time with the given msecs.
   *
   * @param date	the msecs since 1970
   */
  public DateTimeMsec(long date) {
    super(date);
  }

  /**
   * Initializes the date/time with the given date.
   *
   * @param date	the Java date
   */
  public DateTimeMsec(Date date) {
    super(date.getTime());
  }
}
