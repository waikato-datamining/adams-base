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

/*
 * AbstractSearchActor.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.flow.tree.keyboardaction;

import adams.gui.flow.tree.StateContainer;

/**
 * Ancestor for actions that add an actor.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractSearchActor
  extends AbstractKeyboardAction {

  private static final long serialVersionUID = 9158512844896786075L;

  /** whether to display the GOE with the actor. */
  protected boolean m_DisplayActorOptions;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "display-actor-options", "displayActorOptions",
      true);
  }

  /**
   * Sets whether to display the actor options in GOE.
   *
   * @param value 	true if to display
   */
  public void setDisplayActorOptions(boolean value) {
    m_DisplayActorOptions = value;
    reset();
  }

  /**
   * Returns whether to display the actor options in GOE.
   *
   * @return 		true if to display
   */
  public boolean getDisplayActorOptions() {
    return m_DisplayActorOptions;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String displayActorOptionsTipText() {
    return "If enabled, the actor options are displayed in GenericObjectEditor; otherwise the actor is simply added.";
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
	result = "Flow not editable";
      else if (!state.isSingleSel)
	result = "Not a single actor selected";
    }

    return result;
  }
}
