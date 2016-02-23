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
import adams.core.io.GzipUtils;
import adams.scripting.command.AbstractCommandWithResponse;
import adams.scripting.requesthandler.RequestHandler;
import adams.scripting.responsehandler.ResponseHandler;

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
   * Sets the payload for the command.
   *
   * @param value	the payload
   */
  @Override
  public void setPayload(byte[] value) {
    Properties		props;
    StringReader	reader;
    byte[]		decomp;

    decomp = GzipUtils.decompress(value, 1024);
    props  = new Properties();
    if (decomp != null) {
      reader = new StringReader(new String(decomp));
      try {
	props.load(reader);
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to parse payload!", e);
	props = new Properties();
      }
    }
    else {
      getLogger().severe("Failed to decompress payload!");
    }

    m_Info = props;
  }

  /**
   * Returns the payload of the command, if any.
   *
   * @return		the payload
   */
  @Override
  public byte[] getPayload() {
    return GzipUtils.compress(m_Info.toString().getBytes());
  }

  /**
   * Handles the request.
   *
   * @param handler	for handling the request
   */
  public void handleRequest(RequestHandler handler) {
    Properties 			props;
    Hashtable<String,String> 	info;
    String			msg;

    props = new Properties();
    info   = new adams.core.SystemInfo().getInfo();
    for (String key: info.keySet())
      props.setProperty(key, info.get(key));

    m_Info = props;

    msg = send(m_ResponseHost, m_ResponsePort, false);
    if (msg != null)
      handler.requestFailed(this, msg);
    else
      handler.requestSuccessful(this);
  }

  /**
   * Handles the response.
   *
   * @param handler	for handling the response
   */
  public void handleResponse(ResponseHandler handler) {
    handler.responseSuccessful(this);
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
