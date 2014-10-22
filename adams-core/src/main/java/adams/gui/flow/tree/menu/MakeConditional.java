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
 * MakeConditional.java
 * Copyright (C) 2014 University of Waikato, Hamilton, NZ
 */
package adams.gui.flow.tree.menu;

import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;

import javax.swing.tree.TreePath;

import adams.flow.condition.bool.BooleanCondition;
import adams.flow.condition.bool.BooleanConditionSupporter;
import adams.flow.condition.bool.Expression;
import adams.flow.core.AbstractActor;
import adams.flow.core.ActorWithConditionalEquivalent;
import adams.gui.core.BaseTreeNode;
import adams.gui.core.GUIHelper;
import adams.gui.event.ActorChangeEvent;
import adams.gui.event.ActorChangeEvent.Type;
import adams.gui.flow.tree.Node;
import adams.gui.flow.tree.TreeHelper;
import adams.gui.goe.GenericObjectEditorDialog;

/**
 * For turning an actor into its conditonal equivalent.
 * 
 * @author fracpete
 * @version $Revision$
 */
public class MakeConditional
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
    return "Make conditional...";
  }

  /**
   * Updates the action using the current state information.
   */
  @Override
  protected void doUpdate() {
    setEnabled(	     
	   m_State.editable 
	&& m_State.isSingleSel 
	&& (m_State.tree.getOwner() != null) 
	&& (m_State.selNode.getActor() instanceof ActorWithConditionalEquivalent));
  }

  /**
   * Turns the selected actor into its conditional equivalent.
   *
   * @param path	the (path to the) actor to turn into its conditional equivalent
   */
  protected void makeConditional(TreePath path) {
    AbstractActor		currActor;
    Node 			currNode;
    Node			parentNode;
    Class			condEquiv;
    Node			newNode;
    AbstractActor		newActor;
    boolean			noEquiv;
    int				index;
    boolean			defaultName;
    boolean			expanded;
    GenericObjectEditorDialog	dialog;
    
    currNode   = TreeHelper.pathToNode(path);
    parentNode = (Node) currNode.getParent();
    expanded   = m_State.tree.isExpanded(path);
    currActor  = currNode.getFullActor().shallowCopy();
    noEquiv    = false;
    condEquiv  = null;
    
    if (!(currActor instanceof ActorWithConditionalEquivalent))
      noEquiv = true;

    if (!noEquiv) {
      condEquiv = ((ActorWithConditionalEquivalent) currActor).getConditionalEquivalent();
      if (condEquiv == null)
	noEquiv = true;
    }
    
    if (noEquiv) {
      GUIHelper.showErrorMessage(
	  m_State.tree,
	  "Actor '" + currActor.getClass().getName() + "' does not have a conditional equivalent!");
      return;
    }

    // instantiate equivalent
    newNode  = null;
    newActor = null;
    try {
      newActor = (AbstractActor) condEquiv.newInstance();
      // transfer some basic options
      newActor.setAnnotations(currActor.getAnnotations());
      newActor.setSkip(currActor.getSkip());
      newActor.setLoggingLevel(currActor.getLoggingLevel());
    }
    catch (Exception e) {
      GUIHelper.showErrorMessage(
	  m_State.tree,
	  "Failed to instantiate conditional equivalent: " + condEquiv.getName());
      return;
    }
    
    // choose condition
    if (getParentDialog() != null)
      dialog = new GenericObjectEditorDialog(getParentDialog());
    else
      dialog = new GenericObjectEditorDialog(getParentFrame());
    dialog.setTitle("Conditions");
    dialog.setModalityType(ModalityType.DOCUMENT_MODAL);
    dialog.getGOEEditor().setCanChangeClassInDialog(true);
    dialog.getGOEEditor().setClassType(BooleanCondition.class);
    dialog.setCurrent(new Expression());
    dialog.setLocationRelativeTo(GUIHelper.getParentComponent(m_State.tree));
    dialog.setVisible(true);
    if (dialog.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
      return;

    // create node
    ((BooleanConditionSupporter) newActor).setCondition((BooleanCondition) dialog.getCurrent());
    newNode = new Node(m_State.tree, newActor);
    
    addUndoPoint("Making conditional actor from '" + currNode.getActor().getFullName());

    // move children
    for (BaseTreeNode child: currNode.getChildren())
      newNode.add(child);
    
    // replace node
    defaultName = currActor.getName().equals(currActor.getDefaultName());
    index       = parentNode.getIndex(currNode);
    parentNode.insert(newNode, index);
    parentNode.remove(currNode);
    if (!defaultName) {
      newActor.setName(currActor.getName());
      newNode.setActor(newActor);
      m_State.tree.updateActorName(newNode);
    }
    if (expanded)
      m_State.tree.expand(newNode);

    // update tree
    m_State.tree.setModified(true);
    m_State.tree.nodeStructureChanged(parentNode);
    m_State.tree.notifyActorChangeListeners(new ActorChangeEvent(m_State.tree, parentNode, Type.MODIFY));
    m_State.tree.nodeStructureChanged(parentNode);
    m_State.tree.locateAndDisplay(newNode.getFullName());
    m_State.tree.redraw();
  }

  /**
   * The action to execute.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    makeConditional(m_State.selPath);
  }
}
