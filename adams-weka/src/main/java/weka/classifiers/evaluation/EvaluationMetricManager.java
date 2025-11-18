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
 * EvaluationMetricManager.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package weka.classifiers.evaluation;

import adams.core.Properties;

/**
 * Manages the additional metrics.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class EvaluationMetricManager {

  /** the file to store the default watermark setup in. */
  public final static String FILENAME = "weka/classifiers/evaluation/EvaluationMetricManager.props";

  /** the underlying properties. */
  protected static Properties m_Properties;

  /**
   * Checks whether the metric is enabled.
   *
   * @param obj		the metric to check
   * @return		true if enabled
   */
  public static synchronized boolean isEnabled(Object obj) {
    if (obj == null)
      return false;
    else
      return isEnabled(obj.getClass());
  }

  /**
   * Checks whether the metric is enabled.
   *
   * @param cls		the metric to check
   * @return		true if enabled
   */
  public static synchronized boolean isEnabled(Class cls) {
    return getProperties().getBoolean(cls.getName(), false);
  }

  /**
   * Returns the properties.
   *
   * @return		the properties
   */
  public static synchronized Properties getProperties() {
    Properties 	props;

    if (m_Properties == null) {
      try {
	props = Properties.read(FILENAME);
      }
      catch (Exception e) {
	props = new Properties();
      }
      m_Properties = props;
    }

    return m_Properties;
  }

}
