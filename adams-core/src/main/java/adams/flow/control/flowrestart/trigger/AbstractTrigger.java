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
 * AbstractTrigger.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.control.flowrestart.trigger;

import adams.core.option.AbstractOptionHandler;
import adams.flow.control.Flow;

/**
 * Ancestor for restart triggers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractTrigger
  extends AbstractOptionHandler  {

  private static final long serialVersionUID = -2532215576000442873L;

  /**
   * Starts the trigger.
   *
   * @param flow	the flow to handle
   * @return		null if successfully started, otherwise error message
   */
  public abstract String start(Flow flow);

  /**
   * Stops the trigger.
   *
   * @return		null if successfully stopped, otherwise error message
   */
  public abstract String stop();
}
