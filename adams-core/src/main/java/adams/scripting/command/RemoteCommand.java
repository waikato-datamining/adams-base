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
 * RemoteCommand.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.scripting.command;

import adams.core.Properties;
import adams.gui.application.AbstractApplicationFrame;
import adams.scripting.requesthandler.RequestHandler;
import adams.scripting.responsehandler.ResponseHandler;

/**
 * Interface for remote commands. A string representation of a command
 * consists of two parse: header and (optional) payload. The header is
 * a commented out Java Properties string representation. The payload
 * is a base64-encoded byte array.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface RemoteCommand {

  /** the key for the command in the header section of a command. */
  String KEY_COMMAND = "Command";

  /** the key for the type in the header section of a command (Request|Resonse). */
  String KEY_TYPE = "Type";

  /** the Request value (type key). */
  String VALUE_REQUEST = "Request";

  /** the Response value (type key). */
  String VALUE_RESPONSE = "Response";

  /** the width in characters for the base64 encoded payload. */
  int PAYLOAD_WIDTH = 72;

  /**
   * Sets the application context.
   *
   * @param value	the context
   */
  public void setApplicationContext(AbstractApplicationFrame value);

  /**
   * Returns the application context.
   *
   * @return		the context, null if none set
   */
  public AbstractApplicationFrame getApplicationContext();

  /**
   * Parses the header information.
   *
   * @param header	the header
   * @return		null if successfully parsed, otherwise error message
   */
  public String parse(Properties header);

  /**
   * Assembles the command into a string, including any payload.
   *
   * @param request	whether Request or Response
   * @return		the generated string, null if failed to assemble
   */
  public String assemble(boolean request);

  /**
   * Sets whether the command is a request or response.
   *
   * @param value	true if request
   */
  public void setRequest(boolean value);

  /**
   * Returns whether the command is a request or response.
   *
   * @return		true if request
   */
  public boolean isRequest();

  /**
   * Sets the payload for the command.
   *
   * @param value	the payload
   */
  public void setPayload(byte[] value);

  /**
   * Returns the payload of the command, if any.
   *
   * @return		the payload
   */
  public byte[] getPayload();

  /**
   * Sends the command to the specified sscripting engine.
   *
   * @param host	the host to send the command to
   * @param port	the host port
   * @param request	whether Request or Response
   * @return		null if successfully sent, otherwise error message
   */
  public String send(String host, int port, boolean request);

  /**
   * Handles the request.
   *
   * @param handler	for handling the request
   */
  public void handleRequest(RequestHandler handler);

  /**
   * Handles the response.
   *
   * @param handler	for handling the response
   */
  public void handleResponse(ResponseHandler handler);
}
