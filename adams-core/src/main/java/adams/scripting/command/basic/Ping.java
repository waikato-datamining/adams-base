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
 * Ping.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.scripting.command.basic;

import adams.core.net.InternetHelper;
import adams.scripting.command.AbstractCommandWithResponse;

/**
 * Requests an 'am alive' signal from the remote host.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Ping
  extends AbstractCommandWithResponse {

  private static final long serialVersionUID = -3350680106789169314L;

  /** the host. */
  protected String m_Host;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Requests an 'am alive' signal from the remote host.";
  }

  @Override
  protected void initialize() {
    super.initialize();

    m_Host = "";
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
    if (value.length == 0) {
      m_Host = "";
      return;
    }

    m_Host = new String(value);
  }

  /**
   * Returns the payload of the response, if any.
   *
   * @return		the payload
   */
  @Override
  public byte[] getResponsePayload() {
    return m_Host.getBytes();
  }

  /**
   * Hook method for preparing the response payload,
   */
  @Override
  protected void prepareResponsePayload() {
    m_Host = InternetHelper.getLocalHostIP() + "/" + InternetHelper.getLocalHostName();
  }

  /**
   * Returns the objects that represent the response payload.
   *
   * @return		the objects
   */
  public Object[] getResponsePayloadObjects() {
    return new Object[]{m_Host};
  }
}
