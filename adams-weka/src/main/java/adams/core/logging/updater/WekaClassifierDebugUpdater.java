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
 * WekaClassifierDebugUpdater.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.core.logging.updater;

import adams.core.Utils;
import weka.classifiers.AbstractClassifier;

/**
 * Updates the debug flag of classes derived from {@link AbstractClassifier}.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class WekaClassifierDebugUpdater
  extends AbstractWekaDebugUpdater {

  private static final long serialVersionUID = 3075029425346981141L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Updates the debug flag of classes derived from " + Utils.classToString(AbstractClassifier.class) + ".";
  }

  /**
   * Checks whether the logging level can be updated.
   *
   * @param obj the object to check
   * @return true if can be updated
   */
  @Override
  public boolean canUpdateLoggingLevel(Object obj) {
    return (obj instanceof AbstractClassifier);
  }

  /**
   * Updates the logging level.
   *
   * @param obj the object to update
   * @return true if level was updated
   */
  @Override
  public boolean updateLoggingLevel(Object obj) {
    if (obj instanceof AbstractClassifier) {
      ((AbstractClassifier) obj).setDebug(m_NewDebugValue);
      return (((AbstractClassifier) obj).getDebug() == m_NewDebugValue);
    }
    return false;
  }
}
