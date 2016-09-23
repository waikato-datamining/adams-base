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
 * PreferencesPanel.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.application;

import java.io.Serializable;

/**
 * Interface for preferences panels.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface PreferencesPanel
  extends Serializable, Comparable<PreferencesPanel> {

  /**
   * The title of the preferences panel.
   * 
   * @return		the title
   */
  public String getTitle();

  /**
   * Returns whether the panel requires a wrapper scrollpane/panel for display.
   * 
   * @return		true if wrapper required
   */
  public boolean requiresWrapper();
  
  /**
   * Activates the settings.
   * 
   * @return		null if successfully activated, otherwise error message
   */
  public abstract String activate();

  /**
   * Returns whether the panel supports resetting the options.
   *
   * @return		true if supported
   */
  public boolean canReset();

  /**
   * Resets the settings to their default.
   *
   * @return		null if successfully reset, otherwise error message
   */
  public String reset();
}
