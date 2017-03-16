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
 * GetFlow.java
 * Copyright (C) 2016-2017 University of Waikato, Hamilton, NZ
 */

package adams.scripting.command.flow;

import adams.core.option.OptionUtils;
import adams.flow.control.RunningFlowsRegistry;
import adams.flow.core.Actor;
import adams.scripting.command.AbstractCommandWithResponse;

/**
 * Retrieves a running/registered flow using its ID.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class GetFlow
  extends AbstractCommandWithResponse {

  private static final long serialVersionUID = -3350680106789169314L;

  /** the ID of the flow to retrieve. */
  protected Integer m_ID;

  /** the flow. */
  protected Actor m_Flow;

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
      "id", "ID",
      -1, -1, null);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Flow = null;
  }

  /**
   * Sets the ID of the flow to get.
   *
   * @param value	the ID, -1 if to retrieve the only one
   */
  public void setID(int value) {
    m_ID = value;
    reset();
  }

  /**
   * Returns the ID of the flow to get.
   *
   * @return		the ID, -1 if to retrieve the only one
   */
  public int getID() {
    return m_ID;
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
    Actor	flow;

    if (value.length == 0) {
      m_Flow = null;
      return;
    }

    flow = null;
    try {
      flow = (Actor) OptionUtils.forCommandLine(Actor.class, new String(value));
    }
    catch (Exception e) {
      getLogger().severe("Failed to read actor:\n" + new String(value));
    }

    m_Flow = flow;
  }

  /**
   * Returns the payload of the response, if any.
   *
   * @return		the payload
   */
  @Override
  public byte[] getResponsePayload() {
    return m_Flow.toCommandLine().getBytes();
  }

  /**
   * Hook method for preparing the response payload,
   */
  @Override
  protected void prepareResponsePayload() {
    super.prepareResponsePayload();
    if (m_ID == -1) {
      if (RunningFlowsRegistry.getSingleton().size() == 1)
        m_Flow = RunningFlowsRegistry.getSingleton().flows()[0];
    }
    else {
      m_Flow = RunningFlowsRegistry.getSingleton().getFlow(m_ID);
    }
  }

  /**
   * Returns the objects that represent the response payload.
   *
   * @return		the objects
   */
  public Object[] getResponsePayloadObjects() {
    return new Object[]{m_Flow};
  }
}
