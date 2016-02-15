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
 * AddActorHere.java
 * Copyright (C) 2015-2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.flow.tree.keyboardaction;

import adams.flow.control.Sequence;
import adams.flow.core.Actor;
import adams.flow.core.ActorHandler;
import adams.gui.core.BaseShortcut;
import adams.gui.flow.tree.StateContainer;

/**
 * Adds an actor at the current location.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class EncloseActor
  extends AbstractKeyboardActionWithActor {

  private static final long serialVersionUID = 5437085259210069183L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Encloses the selected actor(s) with the specified actor.";
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String actorTipText() {
    return "The actor to enclose the selected one(s) with.";
  }

  /**
   * Returns the default shortcut of the action.
   *
   * @return 		the default
   */
  @Override
  protected BaseShortcut getDefaultShortcut() {
    return new BaseShortcut("F4");
  }

  /**
   * Returns the default actor of the action.
   *
   * @return 		the default
   */
  @Override
  protected Actor getDefaultActor() {
    return new Sequence();
  }

  /**
   * Sets the actor of the action.
   *
   * @param value 	the actor
   */
  @Override
  public void setActor(Actor value) {
    if (value instanceof ActorHandler)
      super.setActor(value);
    else
      getLogger().warning("Enclosing actor does not implement " + ActorHandler.class.getName() + "!");
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
      if (!state.editable)
	result = "Not editable";
      else if (state.parent == null)
        result = "Parent is not set";
      else if (state.numSel == 0)
	result = "No actors to enclose";
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
    state.tree.getOperations().encloseActor(state.selPaths, (ActorHandler) getActor());
    return null;
  }
}
