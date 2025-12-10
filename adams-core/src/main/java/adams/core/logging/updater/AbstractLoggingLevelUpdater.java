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
 * AbstractLoggingLevelUpdater.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.core.logging.updater;

import adams.core.Utils;
import adams.core.logging.LoggingLevel;
import adams.core.option.AbstractOptionHandler;

/**
 * Ancestor for logging level updaters.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractLoggingLevelUpdater
  extends AbstractOptionHandler
  implements LoggingLevelUpdater {

  private static final long serialVersionUID = -8119038853333641082L;

  /** the new level. */
  protected LoggingLevel m_NewLevel;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "new-level", "newLevel",
      LoggingLevel.INFO);
  }

  /**
   * Sets the new level to use.
   *
   * @param value	the level
   */
  public void setNewLevel(LoggingLevel value) {
    m_NewLevel = value;
    reset();
  }

  /**
   * Returns the new level to use.
   *
   * @return		the level
   */
  public LoggingLevel getNewLevel() {
    return m_NewLevel;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String newLevelTipText() {
    return "The new level to use.";
  }

  /**
   * Updates the logging level.
   *
   * @param obj		the object to update
   * @return		true if level was updated
   */
  protected abstract boolean doUpdateLoggingLevel(Object obj);

  /**
   * Updates the logging level.
   *
   * @param obj		the object to update
   * @return		true if level was updated
   */
  @Override
  public boolean updateLoggingLevel(Object obj) {
    boolean	result;

    result = doUpdateLoggingLevel(obj);
    if (!result)
      getLogger().warning("Failed to update logging level for: " + Utils.classToString(obj));

    return result;
  }

}
