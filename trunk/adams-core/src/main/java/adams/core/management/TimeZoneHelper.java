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
 * TimeZoneHelper.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.core.management;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.TimeZone;

import adams.core.Properties;
import adams.env.Environment;
import adams.env.TimeZoneDefinition;

/**
 * Helper class for timezone setup.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TimeZoneHelper {

  /** the props file. */
  public final static String FILENAME = "TimeZone.props";

  /** the timezone to use. */
  public final static String TIMEZONE = "TimeZone";

  /** the constant for the system's default timezone. */
  public final static String DEFAULT_TIMEZONE = "Default";

  /** the singleton. */
  protected static TimeZoneHelper m_Singleton;

  /** the properties. */
  protected Properties m_Properties;

  /** whether the settings got modified. */
  protected boolean m_Modified;

  /**
   * Initializes the helper.
   */
  private TimeZoneHelper() {
    super();
    reload();
  }

  /**
   * Initializes the timezone with the current settings.
   */
  public void initializeTimezone() {
    TimeZone.setDefault(TimeZone.getTimeZone(getTimezone()));
  }

  /**
   * Whether the settings got modified.
   *
   * @return		true if modified
   */
  public boolean isModified() {
    return m_Modified;
  }

  /**
   * Returns the timezone.
   *
   * @return		the timezone
   */
  public String getTimezone() {
    return m_Properties.getProperty(TIMEZONE, TimeZone.getDefault().getID());
  }

  /**
   * Updates the timezone.
   *
   * @param value	the timezone
   */
  public void setTimezone(String value) {
    m_Modified = true;
    m_Properties.setProperty(TIMEZONE, value);
  }

  /**
   * Reloads the properties file. Discards any unsaved settings.
   */
  public synchronized void reload() {
    m_Modified = false;

    try {
      m_Properties = Environment.getInstance().read(TimeZoneDefinition.KEY);
    }
    catch (Exception e) {
      m_Properties = new Properties();
    }
  }

  /**
   * Saves the settings in the user's home directory.
   *
   * @return		true if successfully saved
   */
  public synchronized boolean save() {
    boolean	result;

    result = Environment.getInstance().write(TimeZoneDefinition.KEY, m_Properties);
    if (result)
      m_Modified = false;

    return result;
  }

  /**
   * Returns the singleton.
   *
   * @return		the singleton
   */
  public synchronized static TimeZoneHelper getSingleton() {
    if (m_Singleton == null)
      m_Singleton = new TimeZoneHelper();

    return m_Singleton;
  }
  
  /**
   * Returns the timezone as string.
   *
   * @param object	the timezone object to convert
   * @return		the generated string
   */
  public static String toString(TimeZone tz) {
    String	result;
    
    result = tz.getID();
    if (result.equals(TimeZone.getDefault().getID()))
      result = DEFAULT_TIMEZONE;
    
    return result;
  }

  /**
   * Returns a timezone generated from the string.
   *
   * @param str		the string to convert to a timezone
   * @return		the generated timezone
   */
  public static TimeZone valueOf(String str) {
    if (str.equals(DEFAULT_TIMEZONE))
      return TimeZone.getDefault();
    else
      return TimeZone.getTimeZone(str);
  }
  
  /**
   * Returns all the timezone IDs, including {@link #DEFAULT_TIMEZONE}.
   * 
   * @return		the IDs
   */
  public static String[] getIDs() {
    ArrayList<String>	result;
    
    result = new ArrayList<String>(Arrays.asList(TimeZone.getAvailableIDs().clone()));
    Collections.sort(result);
    result.add(0, TimeZoneHelper.DEFAULT_TIMEZONE);
    
    return result.toArray(new String[result.size()]);
  }
}
