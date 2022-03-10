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
 * AbstractAddMostCommonActorAction.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.flow.tree.menu;

import adams.flow.core.Actor;
import adams.gui.core.BaseMenu;
import adams.gui.core.GUIHelper;
import adams.gui.core.MenuItemComparator;
import adams.gui.core.dotnotationtree.AbstractItemFilter;
import adams.gui.flow.tree.TreeOperations;
import adams.gui.flow.tree.record.add.MostCommon;

import javax.swing.JMenuItem;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Ancestor for actions that add most common actors.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractAddMostCommonActorAction
    extends AbstractTreePopupMenuItemAction{

  private static final long serialVersionUID = -4230209427910258312L;

  /**
   * Returns the classnames of the most commonly used actors.
   *
   * @param path	the path to insert the actor at
   * @param position	where to insert the actor
   * @return		the actors
   */
  protected List<String> getMostCommonActors(TreePath path, TreeOperations.InsertPosition position) {
    List<String>	result;
    List<String>	common;
    AbstractItemFilter	filter;

    result = new ArrayList<>();
    common = MostCommon.getMostCommon(20);
    filter = m_State.tree.getOperations().configureFilter(path, position);
    for (String actor: common) {
      if (filter.filter(actor))
        result.add(actor);
    }

    return result;
  }

  /**
   * Creates a new menuitem.
   *
   * @param menuitems	the list to add to
   * @param actor	the actor to create the menuitem for
   * @return		the new item
   */
  protected JMenuItem newMenuItem(List<JMenuItem> menuitems, Actor actor) {
    JMenuItem 	result;

    result = new JMenuItem(actor.getClass().getSimpleName());
    result.setIcon(GUIHelper.getIcon(actor.getClass()));
    menuitems.add(result);

    return result;
  }

  /**
   * Finalizes the menu.
   *
   * @param menuitems	the menuitems for the menu
   * @return		the generated menu
   */
  protected BaseMenu finalizeMenu(List<JMenuItem> menuitems) {
    BaseMenu	result;

    Collections.sort(menuitems, new MenuItemComparator());
    result = BaseMenu.createCascadingMenu(menuitems, -1, "More...");
    result.setText(getName());
    result.setEnabled(isEnabled() && (menuitems.size() > 0));
    result.setIcon(getIcon());

    return result;
  }
}
