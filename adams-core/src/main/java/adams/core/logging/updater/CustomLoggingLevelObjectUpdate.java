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
 * CustomLoggingLevelObjectUpdate.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.core.logging.updater;

import adams.core.Utils;
import adams.core.logging.CustomLoggingLevelObject;

/**
 * Updates objects implementing {@link CustomLoggingLevelObject}.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class CustomLoggingLevelObjectUpdate
  extends AbstractLoggingLevelUpdater {

  private static final long serialVersionUID = -2014681154336591648L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Updates objects implementing " + Utils.classToString(CustomLoggingLevelObject.class) + ".";
  }

  /**
   * Checks whether the logging level can be updated.
   *
   * @param obj the object to check
   * @return true if can be updated
   */
  @Override
  public boolean canUpdateLoggingLevel(Object obj) {
    return (obj instanceof CustomLoggingLevelObject);
  }

  /**
   * Updates the logging level.
   *
   * @param obj the object to update
   * @return true if level was updated
   */
  @Override
  protected boolean doUpdateLoggingLevel(Object obj) {
    if (obj instanceof CustomLoggingLevelObject) {
      ((CustomLoggingLevelObject) obj).setLoggingLevel(m_NewLevel);
      return (((CustomLoggingLevelObject) obj).getLoggingLevel() == m_NewLevel);
    }
    return false;
  }
}
