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

import adams.core.base.AbstractBaseString;
import adams.core.base.BaseString;
import adams.flow.control.Flow;
import adams.gui.application.AbstractApplicationFrame;
import adams.gui.application.AbstractBasicMenuItemDefinition;
import adams.gui.application.AdditionalParameterHandler;
import adams.gui.menu.TextEditor;
import adams.scripting.command.AbstractFlowAwareCommand;
import adams.scripting.engine.RemoteScriptingEngine;

/**
 * Launches the specified menu item on the remote machine.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MenuItem
  extends AbstractFlowAwareCommand {

  private static final long serialVersionUID = -2442701299622203913L;

  /** the menu item to executre. */
  protected AbstractBasicMenuItemDefinition m_MenuItem;

  /** additional paramers. */
  protected BaseString[] m_AdditionalParameters;

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

    m_OptionManager.add(
	    "additional-parameter", "additionalParameters",
	    new BaseString[0]);
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
   * Sets the (optional) additional parameters for the menu item, in case
   * it implements {@link AdditionalParameterHandler}.
   *
   * @param value 	the parameters
   */
  public void setAdditionalParameters(BaseString[] value) {
    m_AdditionalParameters = value;
    reset();
  }

  /**
   * Returns the (optional) additional parameters for the menu item, in case
   * it implements {@link AdditionalParameterHandler}.
   *
   * @return 		the parameters
   */
  public BaseString[] getAdditionalParameters() {
    return m_AdditionalParameters;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String additionalParametersTipText() {
    return
      "The (optional) additional parameters for the menu item, in case it "
	+ "implements " + AdditionalParameterHandler.class.getName() + ".";
  }

  /**
   * Sets the payload for the request.
   *
   * @param value	the payload
   */
  @Override
  public void setRequestPayload(byte[] value) {

  }

  /**
   * Returns the payload of the request, if any.
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
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doHandleRequest(RemoteScriptingEngine engine) {
    if (m_FlowContext != null) {
      if (m_FlowContext.getRoot() instanceof Flow)
	m_MenuItem.setOwner(((Flow) m_FlowContext.getRoot()).getApplicationFrame());
    }
    else if (m_ApplicationContext != null) {
      if (m_ApplicationContext instanceof AbstractApplicationFrame)
      m_MenuItem.setOwner((AbstractApplicationFrame) m_ApplicationContext);
    }

    // additional parameters?
    if ((m_AdditionalParameters.length > 0) && (m_MenuItem instanceof AdditionalParameterHandler))
      ((AdditionalParameterHandler) m_MenuItem).setAdditionalParameters(AbstractBaseString.toStringArray(m_AdditionalParameters));

    m_MenuItem.launch();

    return null;
  }
}
