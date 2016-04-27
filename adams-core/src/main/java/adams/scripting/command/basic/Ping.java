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

import adams.core.DateUtils;
import adams.core.net.InternetHelper;
import adams.scripting.command.AbstractCommandWithResponse;

import java.util.Date;

/**
 * Requests an 'am alive' signal from the remote host.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Ping
  extends AbstractCommandWithResponse {

  private static final long serialVersionUID = -3350680106789169314L;

  /** the start time. */
  protected String m_TimestampStart;

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

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Host           = "";
    m_TimestampStart = "";
  }

  /**
   * Hook method for preparing the request payload,
   */
  @Override
  protected void prepareRequestPayload() {
    super.prepareRequestPayload();
    m_TimestampStart = DateUtils.getTimestampFormatterMsecs().format(new Date());
  }

  /**
   * Ignored.
   *
   * @param value	the payload
   */
  @Override
  public void setRequestPayload(byte[] value) {
    if (value.length == 0) {
      m_TimestampStart = "";
      return;
    }

    m_TimestampStart = new String(value);
  }

  /**
   * Always zero-length array.
   *
   * @return		the payload
   */
  @Override
  public byte[] getRequestPayload() {
    return m_TimestampStart.getBytes();
  }

  /**
   * Returns the objects that represent the request payload.
   *
   * @return		the objects
   */
  public Object[] getRequestPayloadObjects() {
    return new Object[]{m_TimestampStart};
  }

  /**
   * Sets the payload for the response.
   *
   * @param value	the payload
   */
  @Override
  public void setResponsePayload(byte[] value) {
    String	tmp;
    String[]	lines;

    m_TimestampStart = "";
    m_Host           = "";

    if (value.length == 0)
      return;

    tmp    = new String(value);
    lines  = tmp.split("\n");
    if (lines.length > 0)
      m_TimestampStart = lines[0];
    if (lines.length > 1)
      m_Host = lines[1];
  }

  /**
   * Returns the payload of the response, if any.
   *
   * @return		the payload
   */
  @Override
  public byte[] getResponsePayload() {
    return (m_TimestampStart + "\n" + m_Host).getBytes();
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
    return new Object[]{m_TimestampStart, m_Host};
  }
}
