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
 * AbstractKeyboardAction.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.gui.flow.tree.keyboardaction;

import adams.core.option.AbstractOptionHandler;
import adams.gui.core.BaseShortcut;
import adams.gui.flow.tree.StateContainer;

/**
 * Ancestor for flow tree actions that are associated with .
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractKeyboardAction
  extends AbstractOptionHandler {

  private static final long serialVersionUID = -889725470778358596L;

  /** the name of the action. */
  protected String m_Name;

  /** the shortcut. */
  protected BaseShortcut m_Shortcut;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "name", "name",
      getDefaultName());

    m_OptionManager.add(
      "shortcut", "shortcut",
      getDefaultShortcut());
  }

  /**
   * Returns the default name of the action.
   *
   * @return 		the default
   */
  protected String getDefaultName() {
    return getClass().getSimpleName();
  }

  /**
   * Sets the name of the action.
   *
   * @param value 	the name
   */
  public void setName(String value) {
      m_Name = value;
    reset();
  }

  /**
   * Returns the name of the action.
   *
   * @return 		the name
   */
  public String getName() {
    return m_Name;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String nameTipText() {
    return "The name of the action.";
  }

  /**
   * Returns the default shortcut of the action.
   *
   * @return 		the default
   */
  protected abstract BaseShortcut getDefaultShortcut();

  /**
   * Sets the shortcut of the action.
   *
   * @param value 	the shortcut
   */
  public void setShortcut(BaseShortcut value) {
      m_Shortcut = value;
    reset();
  }

  /**
   * Returns the shortcut of the action.
   *
   * @return 		the shortcut
   */
  public BaseShortcut getShortcut() {
    return m_Shortcut;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String shortcutTipText() {
    return "The shortcut of the action.";
  }

  /**
   * Checks whether the current state is suitable.
   * <br>
   * Default implementation only ensures that a state has been provided.
   *
   * @param state	the current state
   * @return		null if OK, otherwise error message
   */
  protected String check(StateContainer state) {
    if (state == null)
      return "No state container provided!";
    return null;
  }

  /**
   * Performs the actual execution of the aciton.
   *
   * @param state	the current state
   * @return		null if OK, otherwise error message
   */
  protected abstract String doExecute(StateContainer state);

  /**
   * Executes the action.
   *
   * @param state	the current state
   * @return		null if OK, otherwise error message
   */
  public String execute(StateContainer state) {
    String	result;

    result = check(state);
    if (result == null)
      result = doExecute(state);

    return result;
  }
}
