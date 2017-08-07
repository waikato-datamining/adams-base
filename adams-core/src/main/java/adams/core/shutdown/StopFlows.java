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
 * StopFlows.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.core.shutdown;

import adams.core.Utils;
import adams.flow.control.Flow;
import adams.flow.control.RunningFlowsRegistry;
import adams.flow.core.ActorUtils;

/**
 * Stops registered flows.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StopFlows
  extends AbstractShutdownHook {

  private static final long serialVersionUID = -4069724495805072093L;

  /** the timeout period in msec for stopping a flow. */
  protected int m_TimeOut;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Stops registered flows.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "time-out", "timeOut",
      1000, 1, null);
  }

  /**
   * Sets the timeout period before considering a flow to be unresponsive.
   *
   * @param value	the timeout in msec
   */
  public void setTimeOut(int value) {
    m_TimeOut = value;
    reset();
  }

  /**
   * Returns the timeout period before considering a flow to be unresponsive.
   *
   * @return		the timeout in msec
   */
  public int getTimeOut() {
    return m_TimeOut;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String timeOutTipText() {
    return "The time-out period in msec for flows before considering them unresponsive.";
  }

  /**
   * Generates a flow ID string.
   *
   * @param id		the registry ID
   * @param flow	the flow
   * @return		the ID string
   */
  protected String createFlowID(int id, Flow flow) {
    return "ID=" + id + ", path="  + flow.getVariables().get(ActorUtils.FLOW_FILENAME_LONG);
  }

  /**
   * Configures the runnable that gets executed when shutting down.
   *
   * @return		the runnable
   */
  @Override
  public Runnable configure() {
    return () -> {
      for (Integer id: RunningFlowsRegistry.getSingleton().ids()) {
        final Flow flow = RunningFlowsRegistry.getSingleton().getFlow(id);
        String idStr = createFlowID(id, flow);
        if (isLoggingEnabled())
          getLogger().info("Stopping flow: " + idStr);
        new Thread(() -> flow.stopExecution()).start();
        long end = System.currentTimeMillis() + m_TimeOut;
        while (!flow.isStopped() && (System.currentTimeMillis() < end))
          Utils.wait(this, 100, 100);
        if (!flow.isStopped())
          getLogger().warning("Flow did not finish within " + m_TimeOut + "msec: " + idStr);
        else
          getLogger().warning("Flow stopped: " + idStr);
      }
    };
  }
}
