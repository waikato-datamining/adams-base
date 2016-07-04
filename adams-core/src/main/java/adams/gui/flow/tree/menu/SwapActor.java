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
 * SwapActor.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */
package adams.gui.flow.tree.menu;

import adams.flow.core.Actor;
import adams.gui.core.BaseMenu;
import adams.gui.core.MenuItemComparator;
import adams.gui.flow.tree.actorswap.AbstractActorSwapSuggestion;

import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * For swapping one actor with another.
 * 
 * @author fracpete
 * @version $Revision$
 */
public class SwapActor
  extends AbstractTreePopupMenuItemAction {

  /** for serialization. */
  private static final long serialVersionUID = 3991575839421394939L;
  
  /**
   * Returns the caption of this action.
   * 
   * @return		the caption, null if not applicable
   */
  @Override
  protected String getTitle() {
    return "Swap";
  }

  /**
   * Creates a new menuitem using itself.
   */
  @Override
  public JMenuItem getMenuItem() {
    BaseMenu		result;
    JMenuItem		menuitem;
    int			i;
    List<JMenuItem>	menuitems;
    List<Actor>		suggestions;
    Actor		current;

    current     = m_State.selNode.getActor();
    suggestions = AbstractActorSwapSuggestion.suggestAll(current);
    menuitems   = new ArrayList<>();
    for (i = 0; i < suggestions.size(); i++) {
      final Actor actor = suggestions.get(i);
      if (current.getClass().getSimpleName().equals(actor.getClass().getSimpleName()))
        menuitem = new JMenuItem(actor.getClass().getSimpleName() + " (" + actor.getClass().getPackage().getName() + ")");
      else
        menuitem = new JMenuItem(actor.getClass().getSimpleName());
      menuitems.add(menuitem);
      menuitem.addActionListener((ActionEvent e) -> m_State.tree.getOperations().swapActor(m_State.selPath, actor));
    }
    Collections.sort(menuitems, new MenuItemComparator());
    result = BaseMenu.createCascadingMenu(menuitems, -1, "More...");
    result.setText(getName());
    result.setEnabled(isEnabled());
    result.setIcon(getIcon());

    return result;
  }

  /**
   * Updates the action using the current state information.
   */
  @Override
  protected void doUpdate() {
    setEnabled(
      m_State.editable
        && (m_State.numSel == 1)
        && (AbstractActorSwapSuggestion.suggestAll(m_State.selNode.getActor()).size() > 0));
  }

  /**
   * The action to execute.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    // obsolete
  }
}
