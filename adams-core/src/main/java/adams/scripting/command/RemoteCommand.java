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
 * Copyright (C) 2016-2017 University of Waikato, Hamilton, NZ
 */

package adams.scripting.command;

import adams.core.Properties;
import adams.core.logging.LoggingSupporter;
import adams.core.option.OptionHandler;
import adams.gui.application.ApplicationContext;
import adams.scripting.engine.RemoteScriptingEngine;
import adams.scripting.requesthandler.RequestHandler;

import java.io.Serializable;

/**
 * Interface for remote commands. A string representation of a command
 * consists of two parse: header and (optional) payload. The header is
 * a commented out Java Properties string representation. The payload
 * is a base64-encoded byte array.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface RemoteCommand
  extends OptionHandler, Serializable, LoggingSupporter {

  /** the key for the command in the header section of a command. */
  String KEY_COMMAND = "Command";

  /** the key for the type in the header section of a command (Request|Resonse). */
  String KEY_TYPE = "Type";

  /** the Request value (type key). */
  String VALUE_REQUEST = "Request";

  /** the Response value (type key). */
  String VALUE_RESPONSE = "Response";

  /**
   * Sets the application context.
   *
   * @param value	the context
   */
  public void setApplicationContext(ApplicationContext value);

  /**
   * Returns the application context.
   *
   * @return		the context, null if none set
   */
  public ApplicationContext getApplicationContext();

  /**
   * Parses the header information.
   *
   * @param header	the header
   * @return		null if successfully parsed, otherwise error message
   */
  public String parse(Properties header);

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
   * Sets the payload for the request.
   *
   * @param value	the payload
   */
  public void setRequestPayload(byte[] value);

  /**
   * Returns the payload of the request, if any.
   *
   * @return		the payload
   */
  public byte[] getRequestPayload();

  /**
   * Returns the objects that represent the request payload.
   *
   * @return		the objects
   */
  public Object[] getRequestPayloadObjects();

  /**
   * Assembles the command into a string, including any payload.
   *
   * @return		the generated string, null if failed to assemble
   */
  public String assembleRequest();

  /**
   * Handles the request.
   *
   * @param engine	the remote engine handling the request
   * @param handler	for handling the request
   */
  public void handleRequest(RemoteScriptingEngine engine, RequestHandler handler);

  /**
   * Hook method before sending the request.
   */
  public void beforeSendRequest();

  /**
   * Hook method after sending the request.
   *
   * @param error	null if successful, otherwise error message
   */
  public void afterSendRequest(String error);
}
