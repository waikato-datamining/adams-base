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
 * NullManager.java
 * Copyright (C) 2018-2025 University of Waikato, Hamilton, NZ
 */

package adams.flow.control.flowrestart;

import adams.flow.control.Flow;

/**
 * Does nothing.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class NullManager
  extends AbstractFlowRestartManager {

  private static final long serialVersionUID = 7335736971034402122L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Dummy, does nothing.";
  }

  /**
   * Starts the restart handling.
   *
   * @param flow	the flow to handle
   * @return		null if successfully started, otherwise error message
   */
  @Override
  protected String doStart(Flow flow) {
    return null;
  }

  /**
   * Stops the restart handling.
   *
   * @param flow	the flow to handle
   * @return		null if successfully stopped, otherwise error message
   */
  @Override
  protected String doStop(Flow flow) {
    return null;
  }
}
