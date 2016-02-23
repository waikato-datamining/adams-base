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
 * MenuItem.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.scripting.command.gui;

import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.gui.application.AbstractBasicMenuItemDefinition;
import adams.gui.menu.TextEditor;
import adams.scripting.command.AbstractCommand;
import adams.scripting.command.FlowAwareRemoteCommand;
import adams.scripting.requesthandler.RequestHandler;
import adams.scripting.responsehandler.ResponseHandler;

/**
 * Launches the specified menu item on the remote machine.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MenuItem
  extends AbstractCommand
  implements FlowAwareRemoteCommand {

  private static final long serialVersionUID = -2442701299622203913L;

  /** the menu item to executre. */
  protected AbstractBasicMenuItemDefinition m_MenuItem;

  /** the flow context. */
  protected Actor m_FlowContext;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Launches the specified menu item on the remote machine.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "menu-item", "menuItem",
	    new TextEditor());
  }

  /**
   * Initializes the scheme.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_FlowContext = null;
  }

  /**
   * Sets the class label index (1-based index).
   *
   * @param value 	the index
   */
  public void setMenuItem(AbstractBasicMenuItemDefinition value) {
    m_MenuItem = value;
    reset();
  }

  /**
   * Returns the class label index (1-based index).
   *
   * @return 		the index
   */
  public AbstractBasicMenuItemDefinition getMenuItem() {
    return m_MenuItem;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String menuItemTipText() {
    return "The menu item to launch.";
  }

  /**
   * Sets the flow context.
   *
   * @param value	the actor
   */
  public void setFlowContent(Actor value) {
    m_FlowContext = value;
  }

  /**
   * Returns the flow context, if any.
   *
   * @return		the actor, null if none available
   */
  public Actor getFlowContext() {
    return m_FlowContext;
  }

  /**
   * Sets the payload for the command.
   *
   * @param value	the payload
   */
  @Override
  public void setPayload(byte[] value) {

  }

  /**
   * Returns the payload of the command, if any.
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
    if (m_FlowContext != null) {
      if (m_FlowContext.getRoot() instanceof Flow)
	m_MenuItem.setOwner(((Flow) m_FlowContext.getRoot()).getApplicationFrame());
    }
    else if (m_ApplicationContext != null) {
      m_MenuItem.setOwner(m_ApplicationContext);
    }
    m_MenuItem.launch();
  }

  /**
   * Handles the response.
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
    return m_MenuItem.getClass().getName();
  }
}
