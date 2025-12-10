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
 * MultiUpdater.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.core.logging.updater;

import adams.core.option.AbstractOptionHandler;

/**
 * Applies the specified updaters one by one.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class MultiUpdater
  extends AbstractOptionHandler
  implements LoggingLevelUpdater {

  private static final long serialVersionUID = -2014681154336591648L;

  /** the base updaters to apply. */
  protected LoggingLevelUpdater[] m_Updaters;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies the specified updaters one by one.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "updater", "updaters",
      new LoggingLevelUpdater[0]);
  }

  /**
   * Sets the base updaters to apply one by one.
   *
   * @param value	the base updaters
   */
  public void setUpdaters(LoggingLevelUpdater[] value) {
    m_Updaters = value;
    reset();
  }

  /**
   * Returns the base updaters to apply one by one.
   *
   * @return		the base updaters
   */
  public LoggingLevelUpdater[] getUpdaters() {
    return m_Updaters;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String updatersTipText() {
    return "The updaters to apply one after the other.";
  }

  /**
   * Checks whether the logging level can be updated.
   *
   * @param obj the object to check
   * @return true if can be updated
   */
  @Override
  public boolean canUpdateLoggingLevel(Object obj) {
    boolean	result;

    result = false;

    for (LoggingLevelUpdater updater: m_Updaters) {
      result = updater.canUpdateLoggingLevel(obj);
      if (result)
	break;
    }

    return result;
  }

  /**
   * Updates the logging level.
   *
   * @param obj the object to update
   * @return true if level was updated
   */
  @Override
  public boolean updateLoggingLevel(Object obj) {
    boolean	result;

    result = false;

    for (LoggingLevelUpdater updater: m_Updaters)
      result = updater.updateLoggingLevel(obj) || result;

    return result;
  }
}
