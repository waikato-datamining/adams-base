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
 * EncloseActor.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.tree.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import adams.core.ClassLister;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.flow.core.ActorHandler;
import adams.flow.core.MutableActorHandler;
import adams.flow.sink.DisplayPanelManager;
import adams.flow.sink.DisplayPanelProvider;
import adams.gui.core.BaseMenu;
import adams.gui.core.GUIHelper;
import adams.gui.flow.tree.StateContainer;

/**
 * For enclosing the actors in an actor handler.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class EncloseActor
  extends AbstractTreePopupMenuItem {

  /** for serialization. */
  private static final long serialVersionUID = 2861368330653134074L;

  /**
   * Creates the menuitem to add to the menus.
   * 
   * @param state	the current state of the tree
   * @return		the menu item, null if not possible to use
   */
  @Override
  protected JMenuItem getMenuItem(final StateContainer state) {
    BaseMenu	result;
    JMenuItem	menuitem;
    String[]	actors;
    int		i;
    
    result = new BaseMenu("Enclose");
    result.setEnabled(state.editable && (state.parent != null) && (state.numSel > 0));
    result.setIcon(GUIHelper.getEmptyIcon());

    actors = ClassLister.getSingleton().getClassnames(ActorHandler.class);
    for (i = 0; i < actors.length; i++) {
      final ActorHandler actor = (ActorHandler) AbstractActor.forName(actors[i], new String[0]);
      if (!actor.getActorHandlerInfo().canEncloseActors())
	continue;
      if (actor instanceof Flow)
	continue;
      if ((state.selPaths != null) && (state.selPaths.length > 1) && (!(actor instanceof MutableActorHandler)))
	continue;
      menuitem = new JMenuItem(actor.getClass().getSimpleName());
      result.add(menuitem);
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  state.tree.encloseActor(state.selPaths, actor);
	}
      });
    }
    result.sort();

    if (state.isSingleSel && (state.selNode.getActor() instanceof DisplayPanelProvider)) {
      result.addSeparator();
      menuitem = new JMenuItem(DisplayPanelManager.class.getSimpleName());
      result.add(menuitem);
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  state.tree.encloseInDisplayPanelManager(state.selPaths[0]);
	}
      });
    }
    
    return result;
  }

  /**
   * Creates the associated shortcut.
   * 
   * @return		the shortcut, null if not used
   */
  @Override
  protected AbstractTreeShortcut newShortcut() {
    return null;
  }
}
