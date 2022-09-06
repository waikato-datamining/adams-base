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
 * EncloseActorAll.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.flow.tree.menu;

import adams.gui.core.BaseMenu;

import javax.swing.JMenuItem;
import java.util.List;

/**
 * Menu item for enclosing actors in all/common/special actor handlers.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class EncloseActorCompact
    extends AbstractEncloseActor {

  private static final long serialVersionUID = 8240644179693415397L;

  /**
   * Returns the caption of this action.
   *
   * @return the caption, null if not applicable
   */
  @Override
  protected String getTitle() {
    return "Enclose...";
  }

  /**
   * Adds the menuitems to the menu.
   *
   * @param menu	the menu to add to
   * @param title	the title of the menu
   * @param menuitems	the items to add
   */
  protected void addToMenu(BaseMenu menu, String title, List<JMenuItem> menuitems) {
    JMenuItem 	menuitem;

    if (menuitems.size() > 0) {
      menuitem = BaseMenu.createCascadingMenu(menuitems, -1, "More...");
      menuitem.setEnabled(isEnabled());
    }
    else {
      menuitem = new JMenuItem();
      menuitem.setEnabled(false);
    }
    menuitem.setText(title);
    menu.add(menuitem);
  }

  /**
   * Creates a new menuitem using itself.
   */
  @Override
  public JMenuItem getMenuItem() {
    BaseMenu		result;

    result = new BaseMenu(getTitle());

    addToMenu(result, "All", getAll());
    if (m_State.tree.getRecordEnclose())
      addToMenu(result, "Common", getCommon());
    addToMenu(result, "Special", getSpecial());

    return result;
  }
}
