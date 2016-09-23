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
 * AbstractPreferencePanel.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.application;

import adams.core.ClassLister;
import adams.gui.core.BasePanel;

/**
 * Ancestor for preference panels.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractPreferencesPanel 
  extends BasePanel
  implements PreferencesPanel {

  /** for serialization. */
  private static final long serialVersionUID = -7234105748710063237L;

  /**
   * The title of the preferences.
   * 
   * @return		the title
   */
  public abstract String getTitle();

  /**
   * Returns whether the panel requires a wrapper scrollpane/panel for display.
   * 
   * @return		true if wrapper required
   */
  public abstract boolean requiresWrapper();

  /**
   * Activates the settings.
   * 
   * @return		null if successfully activated, otherwise error message
   */
  public abstract String activate();

  /**
   * Returns whether the panel supports resetting the options.
   * <br>
   * Default implementation returns false.
   *
   * @return		true if supported
   */
  public boolean canReset() {
    return false;
  }

  /**
   * Resets the settings to their default.
   * <br>
   * Default implementation does nothing.
   *
   * @return		null if successfully reset, otherwise error message
   */
  public String reset() {
    return null;
  }

  /**
   * Uses the title to compare the panels.
   * 
   * @return	-1, 0 or +1 if title string is less than, equal to or larger
   * 		than the one of the provided object
   */
  public int compareTo(PreferencesPanel o) {
    return getTitle().compareTo(o.getTitle());
  }

  /**
   * Checks whether the objects are the same. Uses the title in case of
   * {@link PreferencesPanel} objects.
   * 
   * @return		true if the same title
   */
  @Override
  public boolean equals(Object o) {
    if (o instanceof PreferencesPanel)
      return (compareTo((PreferencesPanel) o) == 0);
    else
      return false;
  }

  /**
   * Returns a list with classnames of setup panels.
   *
   * @return		the setup panel classnames
   */
  public static String[] getPanels() {
    return ClassLister.getSingleton().getClassnames(PreferencesPanel.class);
  }
}
