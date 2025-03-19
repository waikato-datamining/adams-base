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
 * AbstractSubProcessEventTrigger.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone.subprocessevent;

import adams.core.MessageCollection;
import adams.core.QuickInfoSupporter;
import adams.core.Utils;
import adams.core.option.AbstractOptionHandler;
import adams.flow.standalone.SubProcessEvent;

/**
 * Ancestor for triggers.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 * @param <I> 	the data to receive
 * @param <O> 	the data to send
 */
public abstract class AbstractSubProcessEventTrigger<I, O>
  extends AbstractOptionHandler
  implements SubProcessEventTrigger<I, O>, QuickInfoSupporter {

  private static final long serialVersionUID = -3553334825518968532L;

  /** the owner. */
  protected SubProcessEvent m_Owner;

  /** the timeout in msec to wait for the actors to become available for processing. */
  protected int m_BusyTimeout;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "busy-timeout", "busyTimeout",
      0, 0, null);
  }

  /**
   * Sets the timeout for waiting for the sub-flow to stop.
   *
   * @param value	timeout in milliseconds (<= 0 for infinity)
   */
  public void setBusyTimeout(int value) {
    if (getOptionManager().isValid("busyTimeout", value)) {
      m_BusyTimeout = value;
      reset();
    }
  }

  /**
   * Returns the timeout for waiting for the sub-flow to stop.
   *
   * @return		timeout in milliseconds (<= 0 for infinity)
   */
  public int getBusyTimeout() {
    return m_BusyTimeout;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String busyTimeoutTipText() {
    return "The maximum number of msec to wait for the sub-flow to become available; 0: no timeout.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <br>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return null;
  }

  /**
   * Configures the trigger.
   *
   * @param owner 	the owning event
   * @return		null if successfully configured, otherwise error message
   */
  @Override
  public String setUp(SubProcessEvent owner) {
    if (owner == null)
      return "No owner provided!";
    m_Owner = owner;
    return null;
  }

  /**
   * Processes the data with the sub-flow. Waits for the actors to become available.
   *
   * @param data	the data to process
   * @param errors	for collecting errors
   * @return		the processed data, null if not data generated or failed to process data
   */
  protected Object process(Object data, MessageCollection errors) {
    long start;

    start = System.currentTimeMillis();
    while (m_Owner.isBusy() && !m_Owner.isStopped()) {
      Utils.wait(this, m_Owner, 1000, 50);
      if ((m_BusyTimeout > 0) && (System.currentTimeMillis() >= start + m_BusyTimeout)) {
	errors.add("Failed to get processing time slot after " + m_BusyTimeout + "msec, failed to process!");
	return null;
      }
    }

    if (m_Owner.isStopped()) {
      errors.add("Processing was stopped!");
      return null;
    }

    return m_Owner.process(data, errors);
  }

  /**
   * Wraps up the trigger.
   */
  @Override
  public void wrapUp() {
    m_Owner = null;
  }
}
