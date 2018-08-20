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
import adams.flow.control.flowrestart.RestartHandler;

/**
 * Ancestor for restart triggers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractTrigger
  extends AbstractOptionHandler  {

  private static final long serialVersionUID = -2532215576000442873L;

  /** the restart handler to notify. */
  protected RestartHandler m_RestartHandler;

  /**
   * Sets the restart handler to use.
   *
   * @param value 	the handler to use
   */
  public void setRestartHandler(RestartHandler value) {
    m_RestartHandler = value;
  }

  /**
   * Returns the restart handler.
   *
   * @return		the handler
   */
  public RestartHandler getRestartHandler() {
    return m_RestartHandler;
  }

  /**
   * Performs checks before starting the trigger.
   *
   * @param flow	the flow to check
   * @return		null if successfully checked, otherwise error message
   */
  protected String check(Flow flow) {
    if (m_RestartHandler == null)
      return "No restart handler!";
    if (flow == null)
      return "No flow provided!";
    return null;
  }

  /**
   * Starts the trigger.
   *
   * @param flow	the flow to handle
   * @return		null if successfully started, otherwise error message
   */
  protected abstract String doStart(Flow flow);

  /**
   * Starts the trigger.
   *
   * @param flow	the flow to handle
   * @return		null if successfully started, otherwise error message
   */
  public String start(Flow flow) {
    String	result;

    result = check(flow);
    if (result == null)
      result = doStart(flow);

    return result;
  }

  /**
   * Stops the trigger.
   *
   * @return		null if successfully stopped, otherwise error message
   */
  public abstract String stop();
}
