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
 * RequestHandler.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.scripting.requesthandler;

import adams.scripting.command.RemoteCommand;
import adams.scripting.engine.RemoteScriptingEngine;

/**
 * Interface for request handlers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface RequestHandler {

  /**
   * Sets the owning engine.
   *
   * @param value	the owner
   */
  public void setOwner(RemoteScriptingEngine value);

  /**
   * Returns the owning engine.
   *
   * @return		the owner, null if none set
   */
  public RemoteScriptingEngine getOwner();

  /**
   * Handles successfuly requests.
   *
   * @param cmd		the command with the request
   */
  public void requestSuccessful(RemoteCommand cmd);

  /**
   * Handles failed requests.
   *
   * @param cmd		the command with the request
   * @param msg		the optional error message, can be null
   */
  public void requestFailed(RemoteCommand cmd, String msg);

  /**
   * Handles rejected requests.
   *
   * @param cmd		the command with the request
   * @param msg		the optional error message, can be null
   */
  public void requestRejected(RemoteCommand cmd, String msg);
}
