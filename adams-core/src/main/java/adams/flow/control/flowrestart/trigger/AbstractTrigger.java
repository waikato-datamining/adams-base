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

import adams.core.QuickInfoSupporter;
import adams.core.option.AbstractOptionHandler;
import adams.flow.control.Flow;
import adams.flow.control.flowrestart.TriggerHandler;

/**
 * Ancestor for restart triggers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractTrigger
  extends AbstractOptionHandler
  implements QuickInfoSupporter {

  private static final long serialVersionUID = -2532215576000442873L;

  /** the restart handler to notify. */
  protected TriggerHandler m_TriggerHandler;

  /**
   * Sets the trigger handler to use.
   *
   * @param value 	the handler to use
   */
  public void setTriggerHandler(TriggerHandler value) {
    m_TriggerHandler = value;
  }

  /**
   * Returns the trigger handler.
   *
   * @return		the handler
   */
  public TriggerHandler getTriggerHandler() {
    return m_TriggerHandler;
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <br>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return null;
  }

  /**
   * Performs checks before starting the trigger.
   *
   * @param flow	the flow to check
   * @return		null if successfully checked, otherwise error message
   */
  protected String check(Flow flow) {
    if (m_TriggerHandler == null)
      return "No trigger handler provided!";
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
