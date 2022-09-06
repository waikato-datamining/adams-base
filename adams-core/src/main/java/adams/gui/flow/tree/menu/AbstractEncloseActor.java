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
 * AbstractEncloseActor.java
 * Copyright (C) 2022 University of Waikato, Hamilton, NZ
 */
package adams.gui.flow.tree.menu;

import adams.core.ClassLister;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.flow.core.ActorHandler;
import adams.flow.core.MutableActorHandler;
import adams.gui.core.GUIHelper;
import adams.gui.core.MenuItemComparator;
import adams.gui.flow.tree.record.enclose.MostCommon;

import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Ancestor for menu items that can enclose selected actors in an actor handler.
 *
 * @author fracpete
 */
public abstract class AbstractEncloseActor
  extends AbstractTreePopupMenuItemAction {

  /** for serialization. */
  private static final long serialVersionUID = 3991575839421394939L;

  /**
   * Sorts the menu items, if necessary.
   *
   * @param menuitems	the menu items to sort
   * @return		the (potentially) sorted items
   */
  protected List<JMenuItem> sort(List<JMenuItem> menuitems) {
    if (menuitems.size() > 1)
      Collections.sort(menuitems, new MenuItemComparator());

    return menuitems;
  }

  /**
   * Generates the menuitems for "all".
   *
   * @return		the "all" menuitems
   */
  protected List<JMenuItem> getAll() {
    List<JMenuItem> 	result;
    JMenuItem		menuitem;
    String[]		actors;
    int			i;

    result = new ArrayList<>();
    actors = ClassLister.getSingleton().getClassnames(ActorHandler.class);
    for (i = 0; i < actors.length; i++) {
      final ActorHandler actor = (ActorHandler) AbstractActor.forName(actors[i], new String[0]);
      if (!actor.getActorHandlerInfo().canEncloseActors())
        continue;
      if (actor instanceof Flow)
        continue;
      if ((m_State.selPaths != null) && (m_State.selPaths.length > 1) && (!(actor instanceof MutableActorHandler)))
        continue;
      menuitem = new JMenuItem(actor.getClass().getSimpleName());
      menuitem.setIcon(GUIHelper.getIcon(actor.getClass()));
      result.add(menuitem);
      menuitem.addActionListener((ActionEvent e) -> m_State.tree.getOperations().encloseActor(m_State.selPaths, actor));
    }

    return sort(result);
  }

  /**
   * Generates the menuitems for "common".
   *
   * @return		the "common" menuitems
   */
  protected List<JMenuItem> getCommon() {
    List<JMenuItem> 	result;
    JMenuItem		menuitem;
    String[]		actors;
    int			i;

    result = new ArrayList<>();

    if (m_State.tree.getRecordEnclose()) {
      actors          = MostCommon.getMostCommon(20).toArray(new String[0]);
      for (i = 0; i < actors.length; i++) {
	final ActorHandler actor = (ActorHandler) AbstractActor.forName(actors[i], new String[0]);
	if (!actor.getActorHandlerInfo().canEncloseActors())
	  continue;
	if (actor instanceof Flow)
	  continue;
	if ((m_State.selPaths != null) && (m_State.selPaths.length > 1) && (!(actor instanceof MutableActorHandler)))
	  continue;
	menuitem = new JMenuItem(actor.getClass().getSimpleName());
	menuitem.setIcon(GUIHelper.getIcon(actor.getClass()));
	result.add(menuitem);
	menuitem.addActionListener((ActionEvent e) -> m_State.tree.getOperations().encloseActor(m_State.selPaths, actor));
      }
    }

    return sort(result);
  }

  /**
   * Generates the menuitems for "specia enclosures".
   *
   * @return		the "special" menuitems
   */
  protected List<JMenuItem> getSpecial() {
    return new ArrayList<>(Arrays.asList(adams.gui.flow.tree.enclose.AbstractEncloseActor.encloseAll(m_State)));
  }

  /**
   * Updates the action using the current state information.
   */
  @Override
  protected void doUpdate() {
    setEnabled(m_State.editable && (m_State.parent != null) && (m_State.numSel > 0));
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
