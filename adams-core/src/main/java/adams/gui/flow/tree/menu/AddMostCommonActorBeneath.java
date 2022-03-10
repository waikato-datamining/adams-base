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
 * AddMostCommonActorBeneath.java
 * Copyright (C) 2022 University of Waikato, Hamilton, NZ
 */
package adams.gui.flow.tree.menu;

import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.gui.flow.tree.TreeOperations;

import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * For adding one of the most common actors beneath.
 * 
 * @author fracpete
 */
public class AddMostCommonActorBeneath
  extends AbstractAddMostCommonActorAction {

  /** for serialization. */
  private static final long serialVersionUID = 3991575839421394939L;
  
  /**
   * Returns the caption of this action.
   * 
   * @return		the caption, null if not applicable
   */
  @Override
  protected String getTitle() {
    return "Add beneath...";
  }

  /**
   * Creates a new menuitem using itself.
   */
  @Override
  public JMenuItem getMenuItem() {
    JMenuItem		menuitem;
    List<String>	actors;
    int			i;
    List<JMenuItem>	menuitems;

    menuitems = new ArrayList<>();
    actors    = getMostCommonActors(m_State.selPath, TreeOperations.InsertPosition.BENEATH);
    for (i = 0; i < actors.size(); i++) {
      final Actor actor = AbstractActor.forName(actors.get(i), new String[0]);
      menuitem = newMenuItem(menuitems, actor);
      menuitem.addActionListener((ActionEvent e) -> m_State.tree.getOperations().addActor(
          m_State.selPath, actor, TreeOperations.InsertPosition.BENEATH, true, TreeOperations.ActorDialog.GOE_FORCED));
    }

    return finalizeMenu(menuitems);
  }

  /**
   * Updates the action using the current state information.
   */
  @Override
  protected void doUpdate() {
    setEnabled(m_State.editable && m_State.isSingleSel && m_State.isMutable);
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
