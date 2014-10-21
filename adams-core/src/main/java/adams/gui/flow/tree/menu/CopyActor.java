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
 * CopyActor.java
 * Copyright (C) 2014 University of Waikato, Hamilton, NZ
 */
package adams.gui.flow.tree.menu;

import java.awt.event.ActionEvent;

import adams.core.option.AbstractOptionProducer;
import adams.core.option.NestedProducer;
import adams.flow.core.AbstractActor;
import adams.gui.core.GUIHelper;
import adams.gui.flow.tree.ClipboardActorContainer;
import adams.gui.flow.tree.TreeHelper;

/**
 * For copying the currently selected actor(s) and placing them on the 
 * clipboard.
 * 
 * @author fracpete
 * @version $Revision$
 */
public class CopyActor
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
    return "Copy";
  }

  /**
   * Updates the action using the current state information.
   */
  @Override
  protected void doUpdate() {
    setEnabled((m_State.numSel > 0));
  }

  /**
   * Puts the actor in nested form on the clipboard.
   *
   * @param actor	the actor to put on the clipboard
   */
  protected void putActorOnClipboard(AbstractActor actor) {
    putActorOnClipboard(new AbstractActor[]{actor});
  }

  /**
   * Puts the actors in nested form on the clipboard.
   *
   * @param actors	the actors to put on the clipboard
   */
  protected void putActorOnClipboard(AbstractActor[] actors) {
    ClipboardActorContainer	cont;

    if (actors.length == 1) {
      GUIHelper.copyToClipboard(AbstractOptionProducer.toString(NestedProducer.class, actors[0]));
    }
    else if (actors.length > 1) {
      cont = new ClipboardActorContainer();
      cont.setActors(actors);
      GUIHelper.copyToClipboard(cont.toNestedString());
    }
  }

  /**
   * The action to execute.
   *
   * @param e		the event
   */
  @Override
  public void actionPerformed(ActionEvent e) {
    putActorOnClipboard(TreeHelper.pathsToActors(m_State.selPaths, true));
  }
}
