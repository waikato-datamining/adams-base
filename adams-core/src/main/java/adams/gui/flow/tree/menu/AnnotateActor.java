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
 * AnnotateActor.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */
package adams.gui.flow.tree.menu;

import adams.core.base.BaseAnnotation;
import adams.flow.core.AbstractActor;
import adams.gui.core.GUIHelper;
import adams.gui.event.ActorChangeEvent;
import adams.gui.event.ActorChangeEvent.Type;
import adams.gui.flow.tree.Node;
import adams.gui.flow.tree.TreeHelper;

import javax.swing.tree.DefaultTreeModel;
import java.awt.event.ActionEvent;

/**
 * Shortcut for annotating actors.
 * 
 * @author fracpete
 * @version $Revision$
 */
public class AnnotateActor
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
    return "Annotate actor";
  }

  /**
   * Updates the action using the current state information.
   */
  @Override
  protected void doUpdate() {
    setEnabled(m_State.editable && (m_State.numSel == 1));
  }

  /**
   * The action to execute.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    Node		node;
    AbstractActor       actor;
    String              annotationOld;
    String              annotationNew;

    node = TreeHelper.pathToNode(m_State.selPath);
    addUndoPoint("Annotating " + node.getFullName());

    actor = node.getActor();
    annotationOld = actor.getAnnotations().getValue();
    annotationNew = GUIHelper.showInputDialog(
      GUIHelper.getParentComponent(m_State.tree),
      "Please enter new annotation:", annotationOld);
    if (annotationNew == null)
      return;
    if (annotationNew.equals(annotationOld))
      return;

    actor.setAnnotations(new BaseAnnotation(annotationNew));
    node.setActor(actor);
    ((DefaultTreeModel) m_State.tree.getModel()).nodeChanged(node);

    m_State.tree.setModified(true);
    m_State.tree.notifyActorChangeListeners(new ActorChangeEvent(m_State.tree, node, Type.MODIFY));
  }
}
