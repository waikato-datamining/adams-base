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
 * CronSchedule.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.core.base;

import org.quartz.CronExpression;

/**
 * Encapsulates a cron schedule.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see org.quartz.CronExpression
 */
public class CronSchedule
  extends AbstractBaseString {

  /** for serialization. */
  private static final long serialVersionUID = -8650732173194720137L;

  /** the default schedule. */
  public final static String DEFAULT = "0 0 1 * * ?";

  /**
   * Initializes with the default schedule.
   *
   * @see	#DEFAULT
   */
  public CronSchedule() {
    this(DEFAULT);
  }

  /**
   * Initializes the object with the specified schedule.
   *
   * @param s		the schedule
   */
  public CronSchedule(String s) {
    super(s);
  }

  /**
   * Initializes the internal object.
   */
  @Override
  protected void initialize() {
    m_Internal = DEFAULT;
  }

  /**
   * Checks whether the string value is a valid presentation for this class.
   *
   * @param value	the string value to check
   * @return		always true
   */
  @Override
  public boolean isValid(String value) {
    if (value == null)
      return false;
    return CronExpression.isValidExpression(value);
  }

  /**
   * Returns a tool tip for the GUI editor (ignored if null is returned).
   *
   * @return		the tool tip
   */
  @Override
  public String getTipText() {
    return "An cron schedule.";
  }
}
