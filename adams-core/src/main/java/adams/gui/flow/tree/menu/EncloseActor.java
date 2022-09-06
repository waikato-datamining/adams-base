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

import adams.core.ClassLister;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.flow.core.ActorHandler;
import adams.flow.core.MutableActorHandler;
import adams.gui.core.BaseMenu;
import adams.gui.core.GUIHelper;
import adams.gui.core.MenuItemComparator;
import adams.gui.flow.tree.enclose.AbstractEncloseActor;
import adams.gui.flow.tree.record.enclose.MostCommon;

import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * For enclosing the actors in an actor handler.
 * 
 * @author fracpete
 */
public class EncloseActor
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
    return "Enclose";
  }

  /**
   * Creates a new menuitem using itself.
   */
  @Override
  public JMenuItem getMenuItem() {
    BaseMenu		result;
    JMenuItem		menuitem;
    String[]		actors;
    int			i;
    List<JMenuItem>	menuitems;
    BaseMenu		menuCommon;
    List<JMenuItem>	menuitemsCommon;
    JMenuItem[]		others;
    BaseMenu		menuOthers;

    result    = null;
    menuitems = new ArrayList<>();
    actors    = ClassLister.getSingleton().getClassnames(ActorHandler.class);
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
      menuitems.add(menuitem);
      menuitem.addActionListener((ActionEvent e) -> m_State.tree.getOperations().encloseActor(m_State.selPaths, actor));
    }
    Collections.sort(menuitems, new MenuItemComparator());
    if (menuitems.size() > 0) {
      result = BaseMenu.createCascadingMenu(menuitems, -1, "More...");
      result.setText(getName());
      result.setEnabled(isEnabled());
      result.setIcon(getIcon());
    }

    // common ones
    if (m_State.tree.getRecordEnclose()) {
      actors          = MostCommon.getMostCommon(20).toArray(new String[0]);
      menuitemsCommon = new ArrayList<>();
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
        menuitemsCommon.add(menuitem);
        menuitem.addActionListener((ActionEvent e) -> m_State.tree.getOperations().encloseActor(m_State.selPaths, actor));
      }
      Collections.sort(menuitemsCommon, new MenuItemComparator());
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
    others = AbstractEncloseActor.encloseAll(m_State);
    if (others.length > 0) {
      menuOthers = BaseMenu.createCascadingMenu(others, -1, "More...");
      menuOthers.setText("Others...");
      menuOthers.setEnabled(isEnabled());
      if (result != null) {
        result.addSeparator();
        result.add(menuOthers);
      }
      else {
        result = menuOthers;
      }
    }

    return result;
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
