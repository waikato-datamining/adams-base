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
 * AbstractRequestHandler.java
 * Copyright (C) 2016-2017 University of Waikato, Hamilton, NZ
 */

package adams.scripting.requesthandler;

import adams.core.option.AbstractOptionHandler;
import adams.gui.application.AbstractApplicationFrame;
import adams.gui.event.RemoteScriptingEngineUpdateEvent;
import adams.scripting.engine.RemoteScriptingEngine;

/**
 * Ancestor for request handlers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractRequestHandler
  extends AbstractOptionHandler
  implements RequestHandler {

  private static final long serialVersionUID = -5131376236748142498L;

  /** the owner. */
  protected RemoteScriptingEngine m_Owner;

  /** whether the handler is enabled. */
  protected boolean m_Enabled;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "enabled", "enabled",
      true);
  }

  /**
   * Sets the owning engine.
   *
   * @param value	the owner
   */
  public void setOwner(RemoteScriptingEngine value) {
    m_Owner = value;
  }

  /**
   * Returns the owning engine.
   *
   * @return		the owner, null if none set
   */
  public RemoteScriptingEngine getOwner() {
    return m_Owner;
  }

  /**
   * Sets whether the handler is enabled.
   *
   * @param value	true if enabled
   */
  public void setEnabled(boolean value) {
    m_Enabled = value;
    reset();
  }

  /**
   * Returns whether the handler is enabled.
   *
   * @return		true if enabled
   */
  public boolean getEnabled() {
    return m_Enabled;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String enabledTipText() {
    return "Determines whether the handler is enabled, i.e., reacting to events.";
  }

  /**
   * For inserting a request handler into the scripting engine.
   *
   * @param source 	the caller
   * @param app		the application frame to update
   * @param handler 	the handler to insert
   * @return		true if not present and therefore inserted
   */
  public static boolean insertHandler(Object source, AbstractApplicationFrame app, RequestHandler handler) {
    boolean 						result;
    RemoteScriptingEngine 				engine;
    adams.scripting.requesthandler.MultiHandler 	multiReq;
    RequestHandler[]					reqHandlers;
    int							i;
    boolean						found;

    engine = app.getRemoteScriptingEngine();
    if (engine == null)
      return false;

    result = false;

    if (engine.getRequestHandler() != handler) {
      if (engine.getRequestHandler() instanceof adams.scripting.requesthandler.MultiHandler) {
	multiReq = (adams.scripting.requesthandler.MultiHandler) engine.getRequestHandler();
	found        = false;
	for (i = 0; i < multiReq.getHandlers().length; i++) {
	  if (multiReq.getHandlers()[i] == handler) {
	    found = true;
	    break;
	  }
	}
	if (!found) {
	  reqHandlers = new RequestHandler[multiReq.getHandlers().length + 1];
	  for (i = 0; i < multiReq.getHandlers().length; i++)
	    reqHandlers[i] = multiReq.getHandlers()[i];
	  reqHandlers[reqHandlers.length - 1] = handler;
	  multiReq.setHandlers(reqHandlers);
	  engine.setRequestHandler(multiReq);
	  result = true;
	}
      }
      else {
	multiReq = new adams.scripting.requesthandler.MultiHandler();
	multiReq.setHandlers(new RequestHandler[]{
	  engine.getRequestHandler(),
	  handler,
	});
	engine.setRequestHandler(multiReq);
	result = true;
      }
    }

    // updated?
    if (result)
      app.notifyRemoteScriptingEngineUpdateListeners(new RemoteScriptingEngineUpdateEvent(source));

    return result;
  }
}
