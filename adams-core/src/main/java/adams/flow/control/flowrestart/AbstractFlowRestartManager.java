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
 * AbstractFlowRestartManager.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.control.flowrestart;

import adams.core.option.AbstractOptionHandler;

/**
 * Ancestor for schemes that can trigger a flow restart.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractFlowRestartManager
  extends AbstractOptionHandler  {

  private static final long serialVersionUID = -7658888648857332698L;

  /**
   * Starts the restart handling.
   *
   * @return		null if successfully started, otherwise error message
   */
  public abstract String start();

  /**
   * Stops the restart handling.
   *
   * @return		null if successfully stopped, otherwise error message
   */
  public abstract String stop();
}
