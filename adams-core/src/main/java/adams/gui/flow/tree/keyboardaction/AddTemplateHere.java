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
 * AddTemplateHere.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.gui.flow.tree.keyboardaction;

import adams.flow.template.AbstractActorTemplate;
import adams.flow.template.EndlessLoop;
import adams.gui.core.BaseShortcut;
import adams.gui.flow.tree.StateContainer;
import adams.gui.flow.tree.TreeOperations;

/**
 * Adds an actor generated from a template at the current location.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class AddTemplateHere
  extends AbstractAddTemplate {

  private static final long serialVersionUID = 5437085259210069183L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Adds an actor generated from a template at the current location.";
  }

  /**
   * Returns the default shortcut of the action.
   *
   * @return 		the default
   */
  @Override
  protected BaseShortcut getDefaultShortcut() {
    return new BaseShortcut("F5");
  }

  /**
   * Returns the default actor of the action.
   *
   * @return 		the default
   */
  @Override
  protected AbstractActorTemplate getDefaultTemplate() {
    return new EndlessLoop();
  }

  /**
   * Checks whether the current state is suitable.
   *
   * @param state	the current state
   * @return		null if OK, otherwise error message
   */
  @Override
  protected String check(StateContainer state) {
    String	result;

    result = super.check(state);

    if (result == null) {
      if (!state.isParentMutable)
	result = "Parent is not mutable";
    }

    return result;
  }

  /**
   * Performs the actual execution of the aciton.
   *
   * @param state	the current state
   * @return		null if OK, otherwise error message
   */
  @Override
  protected String doExecute(StateContainer state) {
    state.tree.getOperations().addActor(state.selPath, m_Template.generate(), TreeOperations.InsertPosition.HERE, true);
    return null;
  }
}
