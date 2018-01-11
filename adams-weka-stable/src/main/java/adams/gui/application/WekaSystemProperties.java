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
 * WekaSystemProperties.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.gui.application;

/**
 * Sets some Weka-specific system properties to improve performance.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaSystemProperties
  extends AbstractInitialization {

  private static final long serialVersionUID = -4846777150024246805L;

  /**
   * The title of the initialization.
   *
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Weka System Properties";
  }

  /**
   * Performs the initialization.
   *
   * @param parent	the application this initialization is for
   * @return		true if successful
   */
  @Override
  public boolean initialize(AbstractApplicationFrame parent) {
    System.setProperty("weka.test.maventest", "true");
    return true;
  }
}
