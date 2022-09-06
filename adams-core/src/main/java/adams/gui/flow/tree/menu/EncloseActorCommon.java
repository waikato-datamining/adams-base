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
 * EncloseActorCommon.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.flow.tree.menu;

import adams.gui.core.BaseMenu;

import javax.swing.JMenuItem;
import java.util.List;

/**
 * Menu item for enclosing actors in common actor handlers.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class EncloseActorCommon
  extends AbstractEncloseActor {

  private static final long serialVersionUID = 8240644179693415397L;

  /**
   * Returns the caption of this action.
   *
   * @return the caption, null if not applicable
   */
  @Override
  protected String getTitle() {
    return "Enclose (common)...";
  }

  /**
   * Returns true if the action is enabled.
   *
   * @return true if the action is enabled, false otherwise
   */
  @Override
  public boolean isEnabled() {
    return (m_State != null) && (m_State.tree != null) && m_State.tree.getRecordEnclose();
  }

  /**
   * Creates a new menuitem using itself.
   */
  @Override
  public JMenuItem getMenuItem() {
    JMenuItem 		result;
    List<JMenuItem> 	menuitems;

    menuitems = getCommon();
    if (menuitems.size() > 0) {
      result = BaseMenu.createCascadingMenu(menuitems, -1, "More...");
      result.setText(getTitle());
      result.setEnabled(isEnabled());
    }
    else {
      result = new JMenuItem();
      result.setText(getTitle());
      result.setEnabled(false);
    }

    return result;
  }
}
