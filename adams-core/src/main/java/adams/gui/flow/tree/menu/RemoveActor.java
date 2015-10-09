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
 * RemoveActor.java
 * Copyright (C) 2014 University of Waikato, Hamilton, NZ
 */
package adams.gui.flow.tree.menu;

import adams.gui.event.ActorChangeEvent;
import adams.gui.event.ActorChangeEvent.Type;
import adams.gui.flow.tree.Node;
import adams.gui.flow.tree.TreeHelper;

import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Vector;

/**
 * For removing actors.
 * 
 * @author fracpete
 * @version $Revision$
 */
public class RemoveActor
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
    return "Remove";
  }

  /**
   * Updates the action using the current state information.
   */
  @Override
  protected void doUpdate() {
    setEnabled(m_State.editable && m_State.canRemove);
  }

  /**
   * Removes the nodes (incl. sub-tree).
   *
   * @param paths	the paths of the nodes to remove
   */
  protected void removeActor(TreePath[] paths) {
    Node		node;
    int			index;
    Node		parent;
    List<Boolean>	state;
    int			row;
    Node		selNode;
    Node[]		nodes;
    int			i;

    nodes = TreeHelper.pathsToNodes(paths);
    if (nodes.length == 1)
      addUndoPoint("Removing node '" + nodes[0].getActor().getFullName() + "'");
    else
      addUndoPoint("Removing nodes");

    // backup expanded state
    state = new Vector<Boolean>(m_State.tree.getExpandedStateList());

    selNode = null;
    for (i = nodes.length - 1; i >= 0; i--) {
      node   = nodes[i];
      parent = (Node) node.getParent();
      index  = parent.getIndex(node);
      row    = m_State.tree.getRowForPath(paths[i]);

      // remove node
      parent.remove(index);
      m_State.tree.nodeStructureChanged(parent);

      // restore expanded state
      state.remove(row);

      // select appropriate node
      if (parent.getChildCount() > index) {
        selNode = (Node) parent.getChildAt(index);
      }
      else {
	if ((parent.getChildCount() > 0) && (parent.getChildCount() > index - 1))
	  selNode = (Node) parent.getChildAt(index - 1);
	else
	  selNode = parent;
      }
    }

    m_State.tree.setExpandedStateList(state);

    if (selNode != null)
      m_State.tree.locateAndDisplay(selNode.getFullName());

    m_State.tree.setModified(true);

    // notify listeners
    if (nodes.length == 1)
      m_State.tree.notifyActorChangeListeners(new ActorChangeEvent(m_State.tree, nodes[0], Type.REMOVE));
    else
      m_State.tree.notifyActorChangeListeners(new ActorChangeEvent(m_State.tree, nodes, Type.REMOVE_RANGE));
  }

  /**
   * The action to execute.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    removeActor(m_State.selPaths);
  }
}
