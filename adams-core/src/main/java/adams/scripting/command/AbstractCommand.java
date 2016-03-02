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
 * AbstractCommand.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.scripting.command;

import adams.core.Properties;
import adams.core.Utils;
import adams.core.io.GzipUtils;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.OptionUtils;
import adams.gui.application.AbstractApplicationFrame;
import adams.scripting.engine.RemoteScriptingEngine;
import adams.scripting.requesthandler.RequestHandler;

/**
 * Ancestor for remote commands.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractCommand
  extends AbstractOptionHandler
  implements RemoteCommand {

  private static final long serialVersionUID = 4357645457118740255L;

  /** the application context. */
  protected AbstractApplicationFrame m_ApplicationContext;

  /** whether the command is a request or response. */
  protected boolean m_Request;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Request            = true;
    m_ApplicationContext = null;
  }

  /**
   * Sets the application context.
   *
   * @param value	the context
   */
  public void setApplicationContext(AbstractApplicationFrame value) {
    m_ApplicationContext = value;
  }

  /**
   * Returns the application context.
   *
   * @return		the context, null if none set
   */
  public AbstractApplicationFrame getApplicationContext() {
    return m_ApplicationContext;
  }

  /**
   * Sets whether the command is a request or response.
   *
   * @param value	true if request
   */
  public void setRequest(boolean value) {
    m_Request = value;
  }

  /**
   * Returns whether the command is a request or response.
   *
   * @return		true if request
   */
  public boolean isRequest() {
    return m_Request;
  }

  /**
   * Assembles the request header.
   *
   * @return		the request header
   */
  protected Properties assembleRequestHeader() {
    Properties		result;

    result = new Properties();
    result.setProperty(KEY_COMMAND, OptionUtils.getCommandLine(this));
    result.setProperty(KEY_TYPE, VALUE_REQUEST);

    return result;
  }

  /**
   * Hook method for preparing the request payload,
   * <br>
   * Default implementatio does nothing.
   */
  protected void prepareRequestPayload() {
  }

  /**
   * Assembles the command into a string, including any payload.
   *
   * @return		the generated string, null if failed to assemble
   */
  public String assembleRequest() {
    Properties		header;
    byte[]		payload;

    // header
    header = assembleRequestHeader();

    // payload
    prepareRequestPayload();
    payload = getRequestPayload();
    if (payload.length > 0)
      payload = GzipUtils.compress(payload);

    return CommandUtils.commandToString(header, payload);
  }

  /**
   * Handles the request.
   *
   * @param engine	the remote engine handling the request
   * @return		null if successful, otherwise error message
   */
  protected abstract String doHandleRequest(RemoteScriptingEngine engine);

  /**
   * Handles the request.
   *
   * @param engine	the remote engine handling the request
   * @param handler	for handling the request
   */
  @Override
  public void handleRequest(RemoteScriptingEngine engine, RequestHandler handler) {
    String	msg;

    msg = doHandleRequest(engine);
    if (msg != null)
      handler.requestFailed(this, msg);
    else
      handler.requestSuccessful(this);
  }

  /**
   * Parses the header information.
   *
   * @param header	the header
   * @return		null if successfully parsed, otherwise error message
   */
  public String parse(Properties header) {
    if (!header.hasKey(KEY_TYPE))
      return "No '" + KEY_TYPE + "' property found!";

    setRequest(header.getProperty(KEY_TYPE, "").equals(VALUE_REQUEST));

    return null;
  }

  /**
   * Returns a short description of the command.
   *
   * @return		the description
   */
  public String toString() {
    return getClass().getName() + ": " + Utils.arrayToString(getRequestPayloadObjects());
  }
}
