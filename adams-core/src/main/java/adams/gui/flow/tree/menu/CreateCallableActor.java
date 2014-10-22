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
 * CreateCallableActor.java
 * Copyright (C) 2014 University of Waikato, Hamilton, NZ
 */
package adams.gui.flow.tree.menu;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.tree.TreePath;

import adams.flow.core.AbstractActor;
import adams.flow.core.AbstractCallableActor;
import adams.flow.core.ActorHandler;
import adams.flow.core.ActorUtils;
import adams.flow.core.CallableActorReference;
import adams.flow.sink.CallableSink;
import adams.flow.source.CallableSource;
import adams.flow.standalone.CallableActors;
import adams.flow.standalone.GridView;
import adams.flow.standalone.TabView;
import adams.flow.transformer.CallableTransformer;
import adams.gui.core.GUIHelper;
import adams.gui.event.ActorChangeEvent;
import adams.gui.event.ActorChangeEvent.Type;
import adams.gui.flow.tree.Node;
import adams.gui.flow.tree.TreeHelper;
import adams.gui.goe.FlowHelper;

/**
 * For turning an actor into a callable one.
 * 
 * @author fracpete
 * @version $Revision$
 */
public class CreateCallableActor
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
    return "Create callable actor";
  }

  /**
   * Updates the action using the current state information.
   */
  @Override
  protected void doUpdate() {
    setEnabled(m_State.editable && m_State.isSingleSel && (m_State.tree.getOwner() != null));
  }

  /**
   * Turns the selected actor into a callable actor.
   *
   * @param path	the (path to the) actor to turn into callable actor
   */
  protected void createCallableActor(TreePath path) {
    AbstractActor		currActor;
    Node 			currNode;
    Node			callableNode;
    Node			root;
    List<Node>			callable;
    List<Node>			multiview;
    CallableActors		callableActors;
    Node			moved;
    AbstractCallableActor	replacement;
    List<TreePath>		exp;
    int				index;

    currNode  = TreeHelper.pathToNode(path);
    currActor = currNode.getFullActor().shallowCopy();
    if (ActorUtils.isStandalone(currActor)) {
      GUIHelper.showErrorMessage(
	  m_State.tree,
	  "Standalone actors cannot be turned into a callable actor!");
      return;
    }
    if (currActor instanceof AbstractCallableActor) {
      GUIHelper.showErrorMessage(
	  m_State.tree,
	  "Actor points already to a callable actor!");
      return;
    }
    if ((currNode.getParent() != null) && (((Node) currNode.getParent()).getActor() instanceof CallableActors)) {
      GUIHelper.showErrorMessage(
	  m_State.tree,
	  "Actor is already a callable actor!");
      return;
    }

    addUndoPoint("Creating callable actor from '" + currNode.getActor().getFullName());

    callable  = FlowHelper.findCallableActorsHandler(currNode, (Node) currNode.getParent(), new Class[]{CallableActors.class});
    multiview = FlowHelper.findCallableActorsHandler(currNode, (Node) currNode.getParent(), new Class[]{GridView.class, TabView.class});  // TODO: superclass?

    // no CallableActors available?
    if (callable.size() == 0) {
      root = (Node) currNode.getRoot();
      if (!((ActorHandler) root.getActor()).getActorHandlerInfo().canContainStandalones()) {
	GUIHelper.showErrorMessage(
	    m_State.tree,
	    "Root actor '" + root.getActor().getName() + "' cannot contain standalones!");
	return;
      }
      callableActors = new CallableActors();
      callableNode   = new Node(m_State.tree, callableActors);
      index          = 0;
      // TODO: more generic approach?
      if (multiview.size() > 0) {
	for (Node node: multiview) {
	  if (node.getParent().getIndex(node) >= index)
	    index = node.getParent().getIndex(node) + 1;
	}
      }
      root.insert(callableNode, index);
      m_State.tree.updateActorName(callableNode);
    }
    else {
      callableNode = callable.get(callable.size() - 1);
    }

    exp = m_State.tree.getExpandedNodes();
    
    // move actor
    moved = m_State.tree.buildTree(callableNode, currActor, true);
    m_State.tree.updateActorName(moved);

    // create replacement
    replacement = null;
    if (ActorUtils.isSource(currActor))
      replacement = new CallableSource();
    else if (ActorUtils.isTransformer(currActor))
      replacement = new CallableTransformer();
    else if (ActorUtils.isSink(currActor))
      replacement = new CallableSink();
    replacement.setCallableName(new CallableActorReference(moved.getActor().getName()));
    currNode.setActor(replacement);
    currNode.removeAllChildren();
    m_State.tree.updateActorName(currNode);

    // update tree
    m_State.tree.setModified(true);
    m_State.tree.nodeStructureChanged(callableNode);
    m_State.tree.setExpandedNodes(exp);
    m_State.tree.notifyActorChangeListeners(new ActorChangeEvent(m_State.tree, callableNode, Type.MODIFY));
    m_State.tree.nodeStructureChanged((Node) currNode.getParent());
    m_State.tree.notifyActorChangeListeners(new ActorChangeEvent(m_State.tree, currNode, Type.MODIFY));
    m_State.tree.expand(callableNode);
    m_State.tree.locateAndDisplay(currNode.getFullName());
    m_State.tree.redraw();
  }

  /**
   * The action to execute.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    createCallableActor(m_State.selPath);
  }
}
