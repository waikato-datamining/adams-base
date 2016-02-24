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
 * AbstractCommandWithResponse.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.scripting.command;

import adams.core.Properties;
import adams.core.option.OptionUtils;
import adams.scripting.engine.RemoteScriptingEngine;
import adams.scripting.responsehandler.ResponseHandler;

/**
 * Ancestor for commands that send a response.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractCommandWithResponse
  extends AbstractCommand
  implements RemoteCommandWithResponse {

  private static final long serialVersionUID = -2803551461382517312L;

  /** the response host. */
  protected String m_ResponseHost;

  /** the response host port. */
  protected int m_ResponsePort;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "response-host", "responseHost",
      "127.0.0.1");

    m_OptionManager.add(
      "response-port", "responsePort",
      12345, 1, 65535);
  }

  /**
   * Sets the host to send the response to.
   *
   * @param value	the host
   */
  public void setResponseHost(String value) {
    m_ResponseHost = value;
    reset();
  }

  /**
   * Returns the host to send the response to.
   *
   * @return		the host
   */
  public String getResponseHost() {
    return m_ResponseHost;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String responseHostTipText() {
    return "The host to send the response to.";
  }

  /**
   * Sets the port to send the response to.
   *
   * @param value	the port
   */
  public void setResponsePort(int value) {
    if (getOptionManager().isValid("responsePort", value)) {
      m_ResponsePort = value;
      reset();
    }
  }

  /**
   * Returns the port to send the response to.
   *
   * @return		the port
   */
  public int getResponsePort() {
    return m_ResponsePort;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String responsePortTipText() {
    return "The port to send the response to.";
  }

  /**
   * Assembles the response header.
   *
   * @return		the response header
   */
  protected Properties assembleResponseHeader() {
    Properties		result;

    result = new Properties();
    result.setProperty(KEY_COMMAND, OptionUtils.getCommandLine(this));
    result.setProperty(KEY_TYPE, VALUE_RESPONSE);

    return result;
  }

  /**
   * Hook method for preparing the response payload,
   * <br>
   * Default implementation does nothing.
   */
  protected void prepareResponsePayload() {
  }

  /**
   * Assembles the command into a string, including any payload.
   *
   * @return		the generated string, null if failed to assemble
   */
  public String assembleResponse() {
    Properties		header;
    byte[]		payload;

    // header
    header = assembleResponseHeader();

    // payload
    prepareResponsePayload();
    payload = getPayload();

    return CommandUtils.commandToString(header, payload);
  }

  /**
   * Handles the request.
   *
   * @param engine	the remote engine handling the request
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doHandleRequest(RemoteScriptingEngine engine) {
    return CommandUtils.send(this, m_ResponseHost, m_ResponsePort, false);
  }

  /**
   * Handles the response.
   *
   * @param engine	the remote engine handling the response
   * @param handler	for handling the response
   */
  @Override
  public void handleResponse(RemoteScriptingEngine engine, ResponseHandler handler) {
    handler.responseSuccessful(this);
  }
}
