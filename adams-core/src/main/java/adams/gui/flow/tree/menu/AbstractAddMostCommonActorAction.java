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

import adams.flow.core.AbstractActor;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Ancestor for actions that add most common actors.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractAddMostCommonActorAction
    extends AbstractTreePopupMenuItemAction{

  private static final long serialVersionUID = -4230209427910258312L;

  /** for blacklisting actor classnames that couldn't be instantiated. */
  static protected Set<String> m_Blacklisted;

  /**
   * Flags the actor as blacklisted for the current session.
   *
   * @param classname	the classname to blacklist
   */
  protected void blacklistActor(String classname) {
    if (m_Blacklisted == null)
      m_Blacklisted = new HashSet<>();
    m_Blacklisted.add(classname);
  }

  /**
   * Checks whether the actor has been blacklisted.
   *
   * @param classname	the classname to check
   * @return		true if blacklisted
   */
  protected boolean isBlacklistedActor(String classname) {
    return (m_Blacklisted != null) && m_Blacklisted.contains(classname);
  }

  /**
   * Instantiates the specified actor if not blacklisted. Blacklists it automatically if it can't be instantiated.
   *
   * @param classname	the classname of the actor to instantiate
   * @return		the actor, null if failed to instantiate
   */
  protected Actor newActor(String classname) {
    Actor	result;

    if (isBlacklistedActor(classname))
      return null;

    result = AbstractActor.forName(classname, new String[0], true);
    if (result == null)
      blacklistActor(classname);

    return result;
  }

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
      if (isBlacklistedActor(actor))
        continue;
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
