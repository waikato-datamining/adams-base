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
 * SortSubFlow.java
 * Copyright (C) 2026 University of Waikato, Hamilton, NZ
 */
package adams.gui.flow.tree.menu;

import adams.gui.core.BaseTreeNode;
import adams.gui.event.ActorChangeEvent;
import adams.gui.event.ActorChangeEvent.Type;
import adams.gui.flow.tree.Node;

import javax.swing.tree.DefaultTreeModel;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Sorts the sub-flow by name.
 * 
 * @author fracpete
 */
public class SortSubFlow
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
    return "Sort sub-flow";
  }

  /**
   * Updates the action using the current state information.
   */
  @Override
  protected void doUpdate() {
    setEnabled(	     
	   m_State.editable 
	&& (m_State.numSel == 1)
	&& m_State.isMutable);
  }

  /**
   * The action to execute.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    Node 		parent;
    List<BaseTreeNode> 	nodes;
    int			i;

    parent = m_State.selNode;
    addUndoPoint("Sorting sub-flow: " + parent.getActor().getName());

    nodes = parent.getChildren();
    Collections.sort(nodes, new Comparator<BaseTreeNode>() {
      @Override
      public int compare(BaseTreeNode o1, BaseTreeNode o2) {
	return ((Node) o1).getActor().getName().compareToIgnoreCase(((Node) o2).getActor().getName());
      }
    });

    parent.removeAllChildren();
    for (i = 0; i < nodes.size(); i++)
      parent.add(nodes.get(i));

    ((DefaultTreeModel) m_State.tree.getModel()).nodeChanged(parent);

    m_State.tree.setModified(true);
    m_State.tree.notifyActorChangeListeners(new ActorChangeEvent(m_State.tree, parent, Type.MODIFY));
    m_State.tree.getOwner().redraw();
  }
}
