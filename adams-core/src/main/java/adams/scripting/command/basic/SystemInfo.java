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
 * SystemInfo.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.scripting.command.basic;

import adams.core.Properties;
import adams.scripting.command.AbstractCommandWithResponse;

import java.io.StringReader;
import java.util.Hashtable;
import java.util.logging.Level;

/**
 * Sends the system info of the remote host back.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SystemInfo
  extends AbstractCommandWithResponse {

  private static final long serialVersionUID = -3350680106789169314L;

  /** the payload. */
  protected Properties m_Info;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Requests the system info information of the remote host.";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Info = new Properties();
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
   * Sets the payload for the response.
   *
   * @param value	the payload
   */
  @Override
  public void setResponsePayload(byte[] value) {
    Properties		props;
    StringReader	reader;

    if (value.length == 0) {
      m_Info = new Properties();
      return;
    }

    props  = new Properties();
    reader = new StringReader(new String(value));
    try {
      props.load(reader);
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to parse payload!", e);
      props = new Properties();
    }

    m_Info = props;
  }

  /**
   * Returns the payload of the response, if any.
   *
   * @return		the payload
   */
  @Override
  public byte[] getResponsePayload() {
    return m_Info.toString().getBytes();
  }

  /**
   * Hook method for preparing the response payload,
   */
  @Override
  protected void prepareResponsePayload() {
    Properties 			props;
    Hashtable<String,String> 	info;

    super.prepareResponsePayload();

    props = new Properties();
    info   = new adams.core.SystemInfo().getInfo();
    for (String key: info.keySet())
      props.setProperty(key, info.get(key));

    m_Info = props;
  }

  /**
   * Returns the system info.
   *
   * @return		the info
   */
  public Properties getInfo() {
    return m_Info;
  }

  /**
   * Returns a short description of the command.
   *
   * @return		the description
   */
  public String toString() {
    return m_Info.toStringSimple();
  }
}
