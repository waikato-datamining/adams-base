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
 * QuickAction.java
 * Copyright (C) 2024 University of Waikato, Hamilton, NZ
 */

package adams.gui.flow.tree.keyboardaction;

import adams.gui.core.BasePopupMenu;
import adams.gui.core.BaseShortcut;
import adams.gui.flow.tree.StateContainer;

import javax.swing.SwingUtilities;
import java.awt.Rectangle;

/**
 * Brings up the quick action menu.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class QuickAction
  extends AbstractKeyboardAction {

  private static final long serialVersionUID = 5437085259210069183L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Pops up the quick action menu.";
  }

  /**
   * Returns the default shortcut of the action.
   *
   * @return 		the default
   */
  @Override
  protected BaseShortcut getDefaultShortcut() {
    return new BaseShortcut("shift pressed F10");
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
      else if (state.numSel < 1)
	result = "At least one actor must be selected";
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
    final BasePopupMenu 	menu;
    final Rectangle		rect;

    menu = state.tree.createNodeQuickActionMenu(state);
    if (menu == null)
      return "No quick action menu generated!";

    if (state.selNode != null) {
      rect = state.tree.getPathBounds(state.selPath);
      if (rect != null)
	SwingUtilities.invokeLater(() -> menu.show(state.tree, rect.x, rect.y));
    }

    return null;
  }
}
