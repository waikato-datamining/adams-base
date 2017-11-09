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
 * CronSchedule.java
 * Copyright (C) 2012-2017 University of Waikato, Hamilton, New Zealand
 */
package adams.core.base;

import adams.core.Properties;
import org.quartz.CronExpression;

import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulates a cron schedule.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @see org.quartz.CronExpression
 */
public class CronSchedule
  extends AbstractBaseString {

  /** for serialization. */
  private static final long serialVersionUID = -8650732173194720137L;

  /** the default schedule. */
  public final static String DEFAULT = "0 0 1 * * ?";

  /** the template file. */
  public final static String FILENAME = "adams/core/base/CronSchedule.props";

  /** the properties with the templates. */
  protected static Properties m_Properties;

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
    return (value != null) && CronExpression.isValidExpression(value);
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

  /**
   * Returns the predefined templates.
   *
   * @return		the templates
   */
  public static synchronized Map<String,CronSchedule> getTemplates() {
    Map<String,CronSchedule>	result;
    String			prefix;
    String			schedule;
    String			display;
    CronSchedule 		test;

    result = new HashMap<>();

    if (m_Properties == null) {
      try {
        m_Properties = Properties.read(FILENAME);
      }
      catch (Exception e) {
        m_Properties = new Properties();
      }
    }

    test = new CronSchedule();
    for (String key: m_Properties.keySetAll(new BaseRegExp(".*\\.display"))) {
      display  = m_Properties.getProperty(key);
      prefix   = key.replaceAll("\\.display$", "");
      schedule = m_Properties.getProperty(prefix + ".schedule", "");
      if (test.isValid(schedule))
        result.put(display, new CronSchedule(schedule));
    }

    return result;
  }
}
