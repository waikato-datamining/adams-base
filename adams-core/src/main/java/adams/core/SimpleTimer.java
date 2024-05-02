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
 * SimpleTimer.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.core;

/**
 * Simple class for generating timings.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class SimpleTimer {

  /** the placeholder for milliseconds. */
  public final static String PH_MSECS = "{MSEC}";

  /** the placeholder for milliseconds (with msec unit). */
  public final static String PH_MSECS_UNIT = "{MSEC_UNIT}";

  /** the placeholder for seconds. */
  public final static String PH_SECS = "{SECS}";

  /** the placeholder for seconds (with sec unit). */
  public final static String PH_SECS_UNIT = "{SECS_UNIT}";

  /** the start time in milliseconds. */
  protected Long m_Start;

  /** the singleton. */
  protected static SimpleTimer m_Singleton;

  /**
   * Initializes the timer.
   */
  public SimpleTimer() {
    reset();
  }

  /**
   * Resets the start time.
   */
  public synchronized void reset() {
    if (m_Start == null)
      m_Start = System.currentTimeMillis();
  }

  /**
   * Expands the placeholders in the template and returns the generated string.
   *
   * @param obj		the object's class to prefix the template with
   * @param template	the template string
   * @return		the expanded string
   */
  public String expand(Object obj, String template) {
    if (obj != null)
      return expand(obj.getClass(), template);
    else
      return expand(template);
  }

  /**
   * Expands the placeholders in the template and returns the generated string.
   *
   * @param cls		the class to prefix the template with
   * @param template	the template string
   * @return		the expanded string
   */
  public String expand(Class cls, String template) {
    return expand(cls.getName() + ": " + template);
  }

  /**
   * Expands the placeholders in the template and returns the generated string.
   *
   * @param template	the template string
   * @return		the expanded string
   */
  public String expand(String template) {
    String 	result;
    long 	diffMilli;
    double 	diffSec;

    diffMilli = System.currentTimeMillis() - m_Start;
    diffSec   = diffMilli / 1000.0;
    result    = template;
    result    = result.replace(PH_MSECS, "" + diffMilli);
    result    = result.replace(PH_MSECS_UNIT, diffMilli + " msec");
    result    = result.replace(PH_SECS, "" + diffSec);
    result    = result.replace(PH_SECS_UNIT, diffSec + " sec");

    return result;
  }

  /**
   * Expands the placeholders in the template and outputs the generated string on stdout.
   *
   * @param obj		the object's class to prefix the template with
   * @param template	the template string
   */
  public void println(Object obj, String template) {
    if (obj != null)
      println(obj.getClass(), template);
    else
      println(template);
  }

  /**
   * Expands the placeholders in the template and outputs the generated string on stdout.
   *
   * @param cls		the class to prefix the template with
   * @param template	the template string
   */
  public void println(Class cls, String template) {
    System.out.println(expand(cls, template));
  }

  /**
   * Expands the placeholders in the template and outputs the generated string on stdout.
   *
   * @param template	the template string
   */
  public void println(String template) {
    System.out.println(expand(template));
  }

  /**
   * Returns the global singleton.
   *
   * @return		the singleton
   */
  public static synchronized SimpleTimer getSingleton() {
    if (m_Singleton == null)
      m_Singleton = new SimpleTimer();
    return m_Singleton;
  }
}
