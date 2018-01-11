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
 * WekaPluginManagerExtensions.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.gui.application;

import adams.core.Properties;
import weka.core.PluginManager;

/**
 * Enables further extensions through Weka's PluginManager.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaPluginManagerExtensions
  extends AbstractInitialization {

  private static final long serialVersionUID = -4846777150024246805L;

  public static final String FILENAME = "weka/core/PluginManagerExt.props";

  /**
   * The title of the initialization.
   *
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Weka plugin manager extensions";
  }

  /**
   * Performs the initialization.
   *
   * @param parent	the application this initialization is for
   * @return		true if successful
   */
  @Override
  public boolean initialize(AbstractApplicationFrame parent) {
    Properties		props;

    try {
      props = Properties.read(FILENAME);
      PluginManager.addFromProperties(props);
      return true;
    }
    catch (Exception e) {
      System.err.println("Failed to read PluginManager extensions: " + FILENAME);
      e.printStackTrace();
      return false;
    }
  }
}
