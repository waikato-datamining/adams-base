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
 * MakeTimed.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */
package adams.gui.flow.tree.menu;

import java.awt.event.ActionEvent;

import javax.swing.tree.TreePath;

import adams.flow.core.AbstractActor;
import adams.flow.core.ActorWithTimedEquivalent;
import adams.gui.core.BaseTreeNode;
import adams.gui.core.GUIHelper;
import adams.gui.event.ActorChangeEvent;
import adams.gui.event.ActorChangeEvent.Type;
import adams.gui.flow.tree.Node;
import adams.gui.flow.tree.TreeHelper;

/**
 * For turning an actor into its timed equivalent.
 * 
 * @author fracpete
 * @version $Revision: 9906 $
 */
public class MakeTimed
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
    return "Make timed...";
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
	&& (m_State.selNode.getActor() instanceof ActorWithTimedEquivalent));
  }

  /**
   * Turns the selected actor into its timed equivalent.
   *
   * @param path	the (path to the) actor to turn into its timed equivalent
   */
  protected void makeTimed(TreePath path) {
    AbstractActor		currActor;
    Node 			currNode;
    Node			parentNode;
    Class			timedEquiv;
    Node			newNode;
    AbstractActor		newActor;
    boolean			noEquiv;
    int				index;
    boolean			defaultName;
    boolean			expanded;
    
    currNode   = TreeHelper.pathToNode(path);
    parentNode = (Node) currNode.getParent();
    expanded   = m_State.tree.isExpanded(path);
    currActor  = currNode.getFullActor().shallowCopy();
    noEquiv    = false;
    timedEquiv = null;
    
    if (!(currActor instanceof ActorWithTimedEquivalent))
      noEquiv = true;

    if (!noEquiv) {
      timedEquiv = ((ActorWithTimedEquivalent) currActor).getTimedEquivalent();
      if (timedEquiv == null)
	noEquiv = true;
    }
    
    if (noEquiv) {
      GUIHelper.showErrorMessage(
	  m_State.tree,
	  "Actor '" + currActor.getClass().getName() + "' does not have a timed equivalent!");
      return;
    }

    // instantiate equivalent
    newNode  = null;
    newActor = null;
    try {
      newActor = (AbstractActor) timedEquiv.newInstance();
      // transfer some basic options
      newActor.setAnnotations(currActor.getAnnotations());
      newActor.setSkip(currActor.getSkip());
      newActor.setLoggingLevel(currActor.getLoggingLevel());
    }
    catch (Exception e) {
      GUIHelper.showErrorMessage(
	  m_State.tree,
	  "Failed to instantiate timed equivalent: " + timedEquiv.getName());
      return;
    }

    // create node
    newNode = new Node(m_State.tree, newActor);
    
    addUndoPoint("Making timed actor from '" + currNode.getActor().getFullName());

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
    makeTimed(m_State.selPath);
  }
}
