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
 * UISettingsSupporter.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

/**
 * Interface for classes that support storing of UI settings.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface UISettingsSupporter {

  /**
   * Sets the parameters for storing the UI settings.
   *
   * @param cls		the class
   * @param property	the property
   */
  public void setUISettingsParameters(Class cls, String property);

  /**
   * Clears the parameters for storing the UI settings.
   */
  public void clearUISettingsParameters();
}
