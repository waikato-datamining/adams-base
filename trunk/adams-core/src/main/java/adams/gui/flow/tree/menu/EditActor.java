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
 * EditActor.java
 * Copyright (C) 2014 University of Waikato, Hamilton, NZ
 */
package adams.gui.flow.tree.menu;

import java.awt.event.ActionEvent;

import javax.swing.tree.TreePath;

import adams.flow.core.AbstractActor;
import adams.flow.core.ActorHandler;
import adams.gui.core.GUIHelper;
import adams.gui.event.ActorChangeEvent;
import adams.gui.event.ActorChangeEvent.Type;
import adams.gui.flow.tree.Node;
import adams.gui.flow.tree.TreeHelper;
import adams.gui.flow.tree.postprocessor.AbstractEditPostProcessor;
import adams.gui.goe.GenericObjectEditorDialog;

/**
 * For editing/showing the options of an actor.
 * 
 * @author fracpete
 * @version $Revision$
 */
public class EditActor
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
    return "Cut";
  }

  /**
   * Updates the action using the current state information.
   */
  @Override
  protected void doUpdate() {
    setEnabled(m_State.isSingleSel);
    if (m_State.editable)
      setName("Edit...");
    else
      setName("Show...");
  }

  /**
   * Brings up the GOE dialog for editing the selected actor.
   *
   * @param path	the path to the actor
   */
  protected void editActor(TreePath path) {
    GenericObjectEditorDialog	dialog;
    Node 			currNode;
    Node			newNode;
    Node			parent;
    AbstractActor		actor;
    AbstractActor		actorOld;
    int				index;
    boolean			changed;
    ActorHandler		handler;
    ActorHandler		handlerOld;
    int				i;
    boolean			editable;

    if (path == null)
      return;

    currNode = TreeHelper.pathToNode(path);
    m_State.tree.updateCurrentEditing((Node) currNode.getParent(), currNode);
    actorOld = currNode.getActor().shallowCopy();
    dialog   = GenericObjectEditorDialog.createDialog(m_State.tree);
    editable = m_State.tree.isEditable() && currNode.isEditable();
    if (editable)
      dialog.setTitle("Edit...");
    else
      dialog.setTitle("Show...");
    dialog.getGOEEditor().setCanChangeClassInDialog(true);
    dialog.getGOEEditor().setClassType(AbstractActor.class);
    dialog.setProposedClasses(null);
    dialog.setCurrent(currNode.getActor().shallowCopy());
    dialog.getGOEEditor().setReadOnly(!editable);
    dialog.getGOEEditor().setFilter(m_State.tree.configureFilter(path, null));
    dialog.setLocationRelativeTo(GUIHelper.getParentComponent(m_State.tree));
    dialog.setVisible(true);
    m_State.tree.updateCurrentEditing(null, null);
    if (dialog.getResult() == GenericObjectEditorDialog.APPROVE_OPTION) {
      actor = (AbstractActor) dialog.getEditor().getValue();
      // make sure name is not empty
      if (actor.getName().length() == 0)
	actor.setName(actor.getDefaultName());
      if (actor.equals(actorOld)) {
	actorOld.destroy();
	return;
      }
      parent = (Node) currNode.getParent();

      // does parent allow singletons?
      if (!m_State.tree.checkForStandalones(actor, parent))
	return;

      addUndoPoint("Updating node '" + currNode.getFullName() + "'");

      // check whether actor class or actor structure (for ActorHandlers) has changed
      changed = (actor.getClass() != actorOld.getClass());
      if (!changed && (actor instanceof ActorHandler)) {
	handler    = (ActorHandler) actor;
	handlerOld = (ActorHandler) actorOld;
	changed    = (handler.size() != handlerOld.size());
	if (!changed) {
	  for (i = 0; i < handler.size(); i++) {
	    if (handler.get(i).getClass() != handlerOld.get(i).getClass()) {
	      changed = true;
	      break;
	    }
	  }
	}
      }

      if (changed) {
	if (parent == null) {
	  m_State.tree.buildTree(actor);
	  currNode = (Node) m_State.tree.getModel().getRoot();
	}
	else {
	  newNode = m_State.tree.buildTree(null, actor, false);
	  index   = parent.getIndex(currNode);
	  parent.remove(index);
	  parent.insert(newNode, index);
	  currNode = newNode;
	}
      }
      else {
	currNode.setActor(actor);
      }
      m_State.tree.updateActorName(currNode);
      m_State.tree.setModified(true);
      m_State.tree.nodeStructureChanged(currNode);
      m_State.tree.notifyActorChangeListeners(new ActorChangeEvent(m_State.tree, currNode, Type.MODIFY));
      m_State.tree.locateAndDisplay(currNode.getFullName());
      m_State.tree.refreshTabs();
      // update all occurrences, if necessary
      if (!m_State.tree.getIgnoreNameChanges())
	AbstractEditPostProcessor.apply(m_State.tree, ((parent != null) ? parent.getActor() : null), actorOld, currNode.getActor());
    }
  }

  /**
   * The action to execute.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    editActor(m_State.selPath);
  }
}
