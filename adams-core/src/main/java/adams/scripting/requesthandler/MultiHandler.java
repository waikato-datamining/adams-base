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

package adams.scripting.requesthandler;

import adams.scripting.command.RemoteCommand;

/**
 * Combines multiple handlers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MultiHandler
  extends AbstractRequestHandler {

  private static final long serialVersionUID = 5310361263115261329L;

  /** the handlers to combine. */
  protected RequestHandler[] m_Handlers;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Combines multiple request handlers, just forwards the events to the "
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
      new RequestHandler[0]);
  }

  /**
   * Sets the handlers to use.
   *
   * @param value	the handlers
   */
  public void setHandlers(RequestHandler[] value) {
    m_Handlers = value;
    reset();
  }

  /**
   * Returns the handlers in use.
   *
   * @return		the handlers
   */
  public RequestHandler[] getHandlers() {
    return m_Handlers;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String handlersTipText() {
    return "The request handlers to use.";
  }

  /**
   * Handles successfuly requests.
   *
   * @param cmd		the command with the request
   */
  @Override
  public void requestSuccessful(RemoteCommand cmd) {
    for (RequestHandler handler: m_Handlers)
      handler.requestSuccessful(cmd);
  }

  /**
   * Handles failed requests.
   *
   * @param cmd		the command with the request
   * @param msg		the optional error message, can be null
   */
  @Override
  public void requestFailed(RemoteCommand cmd, String msg) {
    for (RequestHandler handler: m_Handlers)
      handler.requestFailed(cmd, msg);
  }

  /**
   * Handles rejected requests.
   *
   * @param cmd		the command with the request
   * @param msg		the optional error message, can be null
   */
  @Override
  public void requestRejected(RemoteCommand cmd, String msg) {
    for (RequestHandler handler: m_Handlers)
      handler.requestRejected(cmd, msg);
  }
}
