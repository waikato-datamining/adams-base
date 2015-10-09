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
 * ExternalizeActor.java
 * Copyright (C) 2014 University of Waikato, Hamilton, NZ
 */
package adams.gui.flow.tree.menu;

import java.awt.event.ActionEvent;

import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;

import adams.core.Utils;
import adams.core.io.FlowFile;
import adams.flow.core.AbstractActor;
import adams.flow.core.AbstractExternalActor;
import adams.flow.core.ActorUtils;
import adams.flow.sink.ExternalSink;
import adams.flow.source.ExternalSource;
import adams.flow.standalone.ExternalStandalone;
import adams.flow.transformer.ExternalTransformer;
import adams.gui.core.GUIHelper;
import adams.gui.event.ActorChangeEvent;
import adams.gui.event.ActorChangeEvent.Type;
import adams.gui.flow.FlowEditorDialog;
import adams.gui.flow.tree.Node;
import adams.gui.flow.tree.TreeHelper;

/**
 * For turning an actor into an external one.
 * 
 * @author fracpete
 * @version $Revision$
 */
public class ExternalizeActor
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
    return "Externalize...";
  }

  /**
   * Updates the action using the current state information.
   */
  @Override
  protected void doUpdate() {
    setEnabled(m_State.editable && (m_State.numSel >= 1) && (m_State.tree.getOwner() != null));
  }

  /**
   * Opens a new FlowEditor window with the currently selected sub-flow.
   * If the selected actors itself are not implementing the {@link InstantiatableActor}
   * interface, they get enclosed in appropriate wrappers.
   *
   * @param paths	the (paths to the) actors to externalize
   */
  protected void externalizeActor(TreePath[] paths) {
    AbstractActor	handler;
    AbstractActor[]	actors;
    Node		newNode;
    Node		currNode;
    Node		parent;
    int			index;
    int			i;

    if (paths.length == 0)
      return;
    if (paths.length == 1) {
      externalizeActor(paths[0]);
      return;
    }

    // externalize actors
    actors = new AbstractActor[paths.length];
    parent = null;
    for (i = 0; i < paths.length; i++) {
      currNode  = TreeHelper.pathToNode(paths[i]);
      actors[i] = currNode.getFullActor().shallowCopy();
      if (parent == null)
	parent = (Node) currNode.getParent();
    }
    try {
      handler = (AbstractActor) ActorUtils.createExternalActor(actors);
    }
    catch (Exception e) {
      GUIHelper.showErrorMessage(
	  m_State.tree, "Failed to externalize actor(s):\n" + Utils.throwableToString(e));
      return;
    }

    addUndoPoint("Enclosing " + paths.length + " nodes in " + handler.getClass().getName());

    // update tree
    newNode = m_State.tree.buildTree(null, handler, false);
    for (i = 0; i < paths.length; i++) {
      currNode = TreeHelper.pathToNode(paths[i]);
      index    = parent.getIndex(currNode);
      parent.remove(index);
      if (i == 0)
	parent.insert(newNode, index);
    }
    m_State.tree.updateActorName(newNode);
    m_State.tree.setModified(true);
    if (paths.length == 1) {
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          m_State.tree.nodeStructureChanged(newNode);
          m_State.tree.locateAndDisplay(newNode.getFullName());
          m_State.tree.notifyActorChangeListeners(new ActorChangeEvent(m_State.tree, newNode, Type.MODIFY));
        }
      });
    }
    else {
      final Node fParent = parent;
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          m_State.tree.nodeStructureChanged(fParent);
          m_State.tree.locateAndDisplay(fParent.getFullName());
          m_State.tree.notifyActorChangeListeners(new ActorChangeEvent(m_State.tree, fParent, Type.MODIFY));
        }
      });
    }

    externalizeActor(new TreePath(newNode.getPath()));
  }

  /**
   * Opens a new FlowEditor window with the currently selected sub-flow.
   * If the selected actor itself is not implementing the {@link InstantiatableActor}
   * interface, it gets enclosed in the appropriate instantiatable wrapper
   * actor.
   *
   * @param path	the (path to the) actor to externalize
   */
  protected void externalizeActor(TreePath path) {
    AbstractActor		currActor;
    Node 			currNode;
    AbstractExternalActor	extActor;
    FlowEditorDialog		dialog;

    currNode  = TreeHelper.pathToNode(path);
    currActor = currNode.getFullActor().shallowCopy();
    if (getParentDialog() != null)
      dialog = new FlowEditorDialog(getParentDialog());
    else
      dialog = new FlowEditorDialog(getParentFrame());
    dialog.getFlowEditorPanel().newTab();
    dialog.getFlowEditorPanel().setCurrentFlow(currActor);
    dialog.getFlowEditorPanel().setModified(true);
    dialog.setVisible(true);
    if (dialog.getFlowEditorPanel().getCurrentFile() == null)
      return;

    addUndoPoint("Externalizing node '" + currNode.getFullName() + "'");

    extActor = null;
    if (ActorUtils.isStandalone(currActor))
      extActor = new ExternalStandalone();
    else if (ActorUtils.isSource(currActor))
      extActor = new ExternalSource();
    else if (ActorUtils.isTransformer(currActor))
      extActor = new ExternalTransformer();
    else if (ActorUtils.isSink(currActor))
      extActor = new ExternalSink();
    extActor.setActorFile(new FlowFile(dialog.getFlowEditorPanel().getCurrentFile()));

    m_State.tree.setModified(true);
    currNode.setActor(extActor);
    currNode.removeAllChildren();
    m_State.tree.nodeStructureChanged(currNode);
    m_State.tree.notifyActorChangeListeners(new ActorChangeEvent(m_State.tree, currNode, Type.MODIFY));
  }

  /**
   * The action to execute.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    externalizeActor(m_State.selPaths);
  }
}
