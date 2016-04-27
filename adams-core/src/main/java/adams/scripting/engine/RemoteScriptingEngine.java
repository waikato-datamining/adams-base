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
 * RemoteScriptingEngine.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.scripting.engine;

import adams.core.Pausable;
import adams.core.Stoppable;
import adams.core.option.OptionHandler;
import adams.gui.application.AbstractApplicationFrame;
import adams.scripting.permissionhandler.PermissionHandler;
import adams.scripting.requesthandler.RequestHandler;
import adams.scripting.responsehandler.ResponseHandler;

/**
 * Scripting engine for remote commands.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface RemoteScriptingEngine
  extends OptionHandler, Pausable, Stoppable {

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
   * Sets the permission handler to use.
   *
   * @param value	the permission handler
   */
  public void setPermissionHandler(PermissionHandler value);

  /**
   * Returns the permission handler in use.
   *
   * @return		the permission handler
   */
  public PermissionHandler getPermissionHandler();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String permissionHandlerTipText();

  /**
   * Sets the request handler to use.
   *
   * @param value	the request handler
   */
  public void setRequestHandler(RequestHandler value);

  /**
   * Returns the request handler in use.
   *
   * @return		the request handler
   */
  public RequestHandler getRequestHandler();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String requestHandlerTipText();

  /**
   * Sets the response handler to use.
   *
   * @param value	the response handler
   */
  public void setResponseHandler(ResponseHandler value);

  /**
   * Returns the response handler in use.
   *
   * @return		the response handler
   */
  public ResponseHandler getResponseHandler();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String responseHandlerTipText();

  /**
   * Executes the scripting engine.
   *
   * @return		error message in case of failure to start up or run,
   * 			otherwise null
   */
  public String execute();
}
