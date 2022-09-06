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
 * EncloseActor.java
 * Copyright (C) 2014-2022 University of Waikato, Hamilton, NZ
 */
package adams.gui.flow.tree.menu;

import adams.gui.core.BaseMenu;

import javax.swing.JMenuItem;
import java.util.List;

/**
 * For enclosing the actors in an actor handler.
 *
 * @author fracpete
 */
public class EncloseActor
    extends AbstractEncloseActor {

  /** for serialization. */
  private static final long serialVersionUID = 3991575839421394939L;

  /**
   * Returns the caption of this action.
   *
   * @return		the caption, null if not applicable
   */
  @Override
  protected String getTitle() {
    return "Enclose";
  }

  /**
   * Creates a new menuitem using itself.
   */
  @Override
  public JMenuItem getMenuItem() {
    BaseMenu		result;
    List<JMenuItem> 	menuitemsAll;
    List<JMenuItem>	menuitemsCommon;
    BaseMenu		menuCommon;
    List<JMenuItem> menuitemsSpecial;
    BaseMenu menuSpecial;

    result       = null;
    menuitemsAll = getAll();
    if (menuitemsAll.size() > 0) {
      result = BaseMenu.createCascadingMenu(menuitemsAll, -1, "More...");
      result.setText(getName());
      result.setEnabled(isEnabled());
      result.setIcon(getIcon());
    }

    // common ones
    if (m_State.tree.getRecordEnclose()) {
      menuitemsCommon = getCommon();
      if (menuitemsCommon.size() > 0) {
	menuCommon = BaseMenu.createCascadingMenu(menuitemsCommon, -1, "More...");
	menuCommon.setText("Common...");
	menuCommon.setEnabled(isEnabled());
	if (result != null) {
	  result.addSeparator();
	  result.add(menuCommon);
	}
	else {
	  result = menuCommon;
	}
      }
    }

    // special cases
    menuitemsSpecial = getSpecial();
    if (menuitemsSpecial.size() > 0) {
      menuSpecial = BaseMenu.createCascadingMenu(menuitemsSpecial, -1, "More...");
      menuSpecial.setText("Special...");
      menuSpecial.setEnabled(isEnabled());
      if (result != null) {
	result.addSeparator();
	result.add(menuSpecial);
      }
      else {
	result = menuSpecial;
      }
    }

    return result;
  }
}
