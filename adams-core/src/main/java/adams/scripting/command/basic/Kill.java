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
 * Kill.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.scripting.command.basic;

import adams.scripting.command.AbstractCommand;
import adams.scripting.requesthandler.RequestHandler;
import adams.scripting.responsehandler.ResponseHandler;

/**
 * Kills the remote ADAMS instance.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Kill
  extends AbstractCommand {

  private static final long serialVersionUID = -1657908444959620122L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Kills the ADAMS instance.";
  }

  /**
   * Ignored.
   *
   * @param value	the payload
   */
  @Override
  public void setPayload(byte[] value) {
  }

  /**
   * Always zero-length array.
   *
   * @return		the payload
   */
  @Override
  public byte[] getPayload() {
    return new byte[0];
  }

  /**
   * Handles the request.
   *
   * @param handler	for handling the request
   */
  @Override
  public void handleRequest(RequestHandler handler) {
    System.exit(0);
  }

  /**
   * Does nothing.
   *
   * @param handler	for handling the response
   */
  @Override
  public void handleResponse(ResponseHandler handler) {

  }

  /**
   * Returns a short description of the command.
   *
   * @return		the description
   */
  @Override
  public String toString() {
    return "Kill";
  }
}
