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
 * ResponseHandler.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.scripting.responsehandler;

import adams.scripting.command.RemoteCommand;
import adams.scripting.engine.RemoteScriptingEngine;

/**
 * For handling responses.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface ResponseHandler {

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
   * Handles successful responses.
   *
   * @param cmd		the command with the response
   */
  public void responseSuccessful(RemoteCommand cmd);

  /**
   * Handles failed responses.
   *
   * @param cmd		the command with the response
   * @param msg		message, can be null
   */
  public void responseFailed(RemoteCommand cmd, String msg);
}
