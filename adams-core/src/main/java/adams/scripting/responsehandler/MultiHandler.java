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
 * MultiHandler.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.scripting.responsehandler;

import adams.scripting.command.RemoteCommand;

/**
 * Combines multiple handlers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MultiHandler
  extends AbstractResponseHandler {

  private static final long serialVersionUID = 7246341377185260420L;

  /** the handlers to use. */
  protected ResponseHandler[] m_Handlers;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Combines multiple response handlers, forwards the events to the "
	+ "specified handlers.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "handler", "handlers",
      new ResponseHandler[0]);
  }

  /**
   * Sets the handlers to use.
   *
   * @param value	the handlers
   */
  public void setHandlers(ResponseHandler[] value) {
    m_Handlers = value;
    reset();
  }

  /**
   * Returns the handlers in use.
   *
   * @return		the handlers
   */
  public ResponseHandler[] getHandlers() {
    return m_Handlers;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String handlersTipText() {
    return "The response handlers to use.";
  }

  /**
   * Handles successful responses.
   *
   * @param cmd		the command with the response
   */
  @Override
  public void responseSuccessful(RemoteCommand cmd) {
    for (ResponseHandler handler: m_Handlers)
      handler.responseSuccessful(cmd);
  }

  /**
   * Handles failed responses.
   *
   * @param cmd		the command with the response
   * @param msg		message, can be null
   */
  @Override
  public void responseFailed(RemoteCommand cmd, String msg) {
    for (ResponseHandler handler: m_Handlers)
      handler.responseFailed(cmd, msg);
  }
}
