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
 * RemoteCommandWithResponse.java
 * Copyright (C) 2016-2017 University of Waikato, Hamilton, NZ
 */

package adams.scripting.command;

import adams.scripting.connection.Connection;
import adams.scripting.engine.RemoteScriptingEngine;
import adams.scripting.processor.RemoteCommandProcessor;
import adams.scripting.responsehandler.ResponseHandler;

/**
 * Interface for remote commands that send a response back to a host.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface RemoteCommandWithResponse
  extends RemoteCommandWithErrorMessage {

  /**
   * Sets the connection to send the response to.
   *
   * @param value	the connection
   */
  public void setResponseConnection(Connection value);

  /**
   * Returns the connection to send the response to.
   *
   * @return		the connectio
   */
  public Connection getResponseConnection();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String responseConnectionTipText();

  /**
   * Sets the payload for the response.
   *
   * @param value	the payload
   */
  public void setResponsePayload(byte[] value);

  /**
   * Returns the payload of the response, if any.
   *
   * @return		the payload
   */
  public byte[] getResponsePayload();

  /**
   * Returns the objects that represent the response payload.
   *
   * @return		the objects
   */
  public Object[] getResponsePayloadObjects();

  /**
   * Assembles the command into a string, including any payload.
   *
   * @param processor 	for formatting/parsing
   * @return		the generated string, null if failed to assemble
   */
  public String assembleResponse(RemoteCommandProcessor processor);

  /**
   * Handles the response.
   *
   * @param engine	the remote engine handling the response
   * @param handler	for handling the response
   */
  public void handleResponse(RemoteScriptingEngine engine, ResponseHandler handler);

  /**
   * Hook method before sending the response.
   */
  public void beforeSendResponse();

  /**
   * Hook method after sending the response.
   *
   * @param error	null if successful, otherwise error message
   */
  public void afterSendResponse(String error);
}
