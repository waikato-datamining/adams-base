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
 * TriggerManager.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.control.flowrestart;

import adams.flow.control.Flow;
import adams.flow.control.flowrestart.operation.AbstractRestartOperation;
import adams.flow.control.flowrestart.trigger.AbstractTrigger;

/**
 * Applies the specified action once the trigger fires.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class TriggerManager
  extends AbstractFlowRestartManager
  implements RestartHandler {

  private static final long serialVersionUID = 1330531183711738855L;

  /** the trigger in use. */
  protected AbstractTrigger m_Trigger;

  /** the restart operation. */
  protected AbstractRestartOperation m_Operation;

  /** the flow to restart. */
  protected Flow m_Flow;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies the specified action once the trigger fires.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "trigger", "trigger",
      new adams.flow.control.flowrestart.trigger.Null());

    m_OptionManager.add(
      "operation", "operation",
      new adams.flow.control.flowrestart.operation.Null());
  }

  /**
   * Sets the trigger to use.
   *
   * @param value	the trigger
   */
  public void setTrigger(AbstractTrigger value) {
    m_Trigger = value;
    reset();
  }

  /**
   * Returns the trigger in use.
   *
   * @return		the trigger
   */
  public AbstractTrigger getTrigger() {
    return m_Trigger;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String triggerTipText() {
    return "The trigger for initiating the restart.";
  }

  /**
   * Sets the restart operation to use.
   *
   * @param value	the operation
   */
  public void setOperation(AbstractRestartOperation value) {
    m_Operation = value;
    reset();
  }

  /**
   * Returns the restart option in use.
   *
   * @return		the operation
   */
  public AbstractRestartOperation getOperation() {
    return m_Operation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String operationTipText() {
    return "The restart operation to execute.";
  }

  /**
   * Starts the restart handling.
   *
   * @param flow	the flow to handle
   * @return		null if successfully started, otherwise error message
   */
  @Override
  public String start(Flow flow) {
    m_Flow = flow;
    return m_Trigger.start(flow);
  }

  /**
   * Restarts the flow.
   *
   * @return		null if successfully restarted, otherwise the error message
   */
  @Override
  public String restart() {
    return m_Operation.restart(m_Flow);
  }

  /**
   * Stops the restart handling.
   *
   * @param flow	the flow to handle
   * @return		null if successfully stopped, otherwise error message
   */
  @Override
  public String stop(Flow flow) {
    return m_Trigger.stop();
  }
}
