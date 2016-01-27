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
 * Performance.java
 * Copyright (C) 2008-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.core;

import adams.env.Environment;
import adams.env.PerformanceDefinition;


/**
 * A convenience class for accessing the performance tuning parameters.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Performance {

  /** the name of the props file. */
  public final static String FILENAME = "Performance.props";

  /** the properties. */
  protected static Properties m_Properties;

  /**
   * loads the props file.
   */
  protected synchronized static void initialize() {
    if (m_Properties == null) {
      m_Properties = Environment.getInstance().read(PerformanceDefinition.KEY);
    }
  }

  /**
   * Returns the specified boolean property.
   *
   * @param key		the key of the property to retrieve
   * @param defValue	the default value
   */
  public static boolean getBoolean(String key, boolean defValue) {
    initialize();

    return m_Properties.getBoolean(key, defValue);
  }

  /**
   * Returns the specified integer property.
   *
   * @param key		the key of the property to retrieve
   * @param defValue	the default value
   */
  public static int getInteger(String key, int defValue) {
    initialize();

    return m_Properties.getInteger(key, defValue);
  }

  /**
   * Returns the specified double property.
   *
   * @param key		the key of the property to retrieve
   * @param defValue	the default value
   */
  public static double getDouble(String key, double defValue) {
    initialize();

    return m_Properties.getDouble(key, defValue);
  }

  /**
   * Returns the maximum number of processors to use, -1 is all available.
   *
   * @return		the maximum number
   */
  public static int getMaxNumProcessors() {
    initialize();

    return m_Properties.getInteger("maxNumProcessors", -1);
  }

  /**
   * Returns the minimum number of JobComplete events in the JobCompleteManager.
   *
   * @return		the minimum number
   */
  public static int getMinKeepJobComplete() {
    initialize();

    return m_Properties.getInteger("minKeepJobComplete", 50);
  }

  /**
   * Returns the maximum number of JobComplete events in the JobCompleteManager.
   *
   * @return		the maximum number
   */
  public static int getMaxKeepJobComplete() {
    initialize();

    return m_Properties.getInteger("maxKeepJobComplete", 100);
  }

  /**
   * Returns whether only events of failed jobs are kept or all.
   *
   * @return		true if only events of failed jobs are kept
   */
  public static boolean getKeepOnlyFailedJobComplete() {
    initialize();

    return m_Properties.getBoolean("keepOnlyFailedJobComplete", true);
  }

  /**
   * Returns whether multiprocessing is enabled.
   *
   * @return		true if multiprocessing enabled
   */
  public static boolean getMultiProcessingEnabled() {
    initialize();

    return m_Properties.getBoolean("multiProcessingEnabled", true);
  }
}
