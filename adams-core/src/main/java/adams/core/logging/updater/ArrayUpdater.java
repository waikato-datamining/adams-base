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
 * ArrayUpdater.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.core.logging.updater;

import adams.core.option.AbstractOptionHandler;

/**
 * Applies the specified base updater to the elements of the array or, if not an array, just to the object.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class ArrayUpdater
  extends AbstractOptionHandler
  implements LoggingLevelUpdater {

  private static final long serialVersionUID = -296567538525662284L;

  /** the logging level updater. */
  protected LoggingLevelUpdater m_LoggingLevelUpdater;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies the specified base updater to the elements of the array or, if not an array, just to the object.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "logging-level-updater", "loggingLevelUpdater",
      new NoUpdate());
  }

  /**
   * Sets the scheme for updating the logging level of the deserialized object.
   *
   * @param value	the updater
   */
  public void setLoggingLevelUpdater(LoggingLevelUpdater value) {
    m_LoggingLevelUpdater = value;
    reset();
  }

  /**
   * Returns the scheme for updating the logging level of the deserialized object.
   *
   * @return		the updater
   */
  public LoggingLevelUpdater getLoggingLevelUpdater() {
    return m_LoggingLevelUpdater;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String loggingLevelUpdaterTipText() {
    return "The scheme for updating the logging level of the deserialized object.";
  }

  /**
   * Checks whether the logging level can be updated.
   *
   * @param obj the object to check
   * @return true if can be updated
   */
  @Override
  public boolean canUpdateLoggingLevel(Object obj) {
    return ((obj != null) && (obj.getClass().isArray()))
	     || m_LoggingLevelUpdater.canUpdateLoggingLevel(obj);
  }

  /**
   * Updates the logging level.
   *
   * @param obj the object to update
   * @return true if level was updated
   */
  @Override
  public boolean updateLoggingLevel(Object obj) {
    return false;
  }
}
