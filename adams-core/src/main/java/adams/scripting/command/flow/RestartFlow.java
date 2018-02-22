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
 * RestartFlow.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.scripting.command.flow;

import adams.core.Utils;
import adams.flow.core.Actor;
import adams.scripting.command.AbstractRemoteCommandOnFlowWithResponse;

/**
 * Restarts a registered flow via its ID.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RestartFlow
  extends AbstractRemoteCommandOnFlowWithResponse {

  private static final long serialVersionUID = -3350680106789169314L;

  /** the time to wait in milliseconds before restarting the flow. */
  protected int m_Interval;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Retrieves a running/registered flow using its ID.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "interval", "interval",
      0, 0, null);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String IDTipText() {
    return "The ID of the flow to get; -1 if to retrieve the only one.";
  }

  /**
   * Sets the interval in milli-seconds to wait.
   *
   * @param value	the interval
   */
  public void setInterval(int value) {
    m_Interval = value;
    reset();
  }

  /**
   * Returns the interval to wait in milli-seconds.
   *
   * @return		the interval
   */
  public int getInterval() {
    return m_Interval;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String intervalTipText() {
    return "The interval in milli-seconds to wait before restarting the flow.";
  }

  /**
   * Ignored.
   *
   * @param value	the payload
   */
  @Override
  public void setRequestPayload(byte[] value) {
  }

  /**
   * Always zero-length array.
   *
   * @return		the payload
   */
  @Override
  public byte[] getRequestPayload() {
    return new byte[0];
  }

  /**
   * Returns the objects that represent the request payload.
   *
   * @return		the objects
   */
  public Object[] getRequestPayloadObjects() {
    return new Object[0];
  }

  /**
   * Sets the payload for the response.
   *
   * @param value	the payload
   */
  @Override
  public void setResponsePayload(byte[] value) {
  }

  /**
   * Returns the payload of the response, if any.
   *
   * @return		the payload
   */
  @Override
  public byte[] getResponsePayload() {
    return new byte[0];
  }

  /**
   * Returns the objects that represent the response payload.
   *
   * @return		the objects
   */
  public Object[] getResponsePayloadObjects() {
    return new Object[0];
  }

  /**
   * Hook method for preparing the response payload,
   */
  @Override
  protected void prepareResponsePayload() {
    Actor 		flow;
    final Actor 	fflow;
    String		msg;

    super.prepareResponsePayload();

    flow  = retrieveFlow(false);
    fflow = retrieveFlow(true);
    if ((flow != null) && (fflow != null)) {
      flow.stopExecution();
      flow.cleanUp();
      if (m_Interval > 0)
	Utils.wait(this, m_Interval, 50);
      msg = fflow.setUp();
      if (msg != null) {
        m_ErrorMessage = "Failed to restart flow:\n" + msg;
      }
      else {
	new Thread(() -> {
	  fflow.execute();
	  fflow.cleanUp();
	}).start();
      }
    }
  }
}
