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
 * Copyright (C) 2016-2017 University of Waikato, Hamilton, NZ
 */

package adams.scripting.command.basic;

import adams.core.ClassCrossReference;
import adams.scripting.command.AbstractCommand;
import adams.scripting.engine.RemoteScriptingEngine;
import adams.scripting.processor.RemoteCommandProcessor;

/**
 * Kills the remote ADAMS instance.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Kill
  extends AbstractCommand
  implements ClassCrossReference {

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
   * Returns the cross-referenced classes.
   *
   * @return		the classes
   */
  public Class[] getClassCrossReferences() {
    return new Class[]{Stop.class, Restart.class};
  }

  /**
   * Ignored.
   *
   * @param value	the payload
   */
  @Override
  public void setRequestPayload(byte[] value) {
  }

  /**
   * Always zero-length array.
   *
   * @return		the payload
   */
  @Override
  public byte[] getRequestPayload() {
    return new byte[0];
  }

  /**
   * Returns the objects that represent the request payload.
   *
   * @return		the objects
   */
  public Object[] getRequestPayloadObjects() {
    return new Object[0];
  }

  /**
   * Handles the request.
   *
   * @param engine	the remote engine handling the request
   * @param processor 	the processor for formatting/parsing
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doHandleRequest(RemoteScriptingEngine engine, RemoteCommandProcessor processor) {
    getLogger().info("Exiting");
    System.exit(0);
    return null;
  }
}
