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
 * RenameActor.java
 * Copyright (C) 2014 University of Waikato, Hamilton, NZ
 */
package adams.gui.flow.tree.menu;

import adams.flow.core.AbstractActor;
import adams.gui.core.GUIHelper;
import adams.gui.event.ActorChangeEvent;
import adams.gui.event.ActorChangeEvent.Type;
import adams.gui.flow.tree.Node;
import adams.gui.flow.tree.TreeHelper;
import adams.gui.flow.tree.postprocessor.AbstractEditPostProcessor;

import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * For removing breakpoints either below currently selected node or everywhere
 * (if no actor selected).
 * 
 * @author fracpete
 * @version $Revision$
 */
public class RenameActor
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
    return "Rename...";
  }

  /**
   * Updates the action using the current state information.
   */
  @Override
  protected void doUpdate() {
    setEnabled(m_State.editable && m_State.isSingleSel);
  }

  /**
   * Renames an actor.
   *
   * @param path	the path to the actor
   */
  protected void renameActor(TreePath path) {
    String		oldName;
    String 		newName;
    Node		node;
    Node		parent;
    AbstractActor	actorOld;
    AbstractActor	actorNew;
    List<TreePath> 	exp;

    node    = TreeHelper.pathToNode(path);
    oldName = node.getActor().getName();
    newName = GUIHelper.showInputDialog(
	GUIHelper.getParentComponent(m_State.tree), 
	"Please enter new name:", oldName);
    if (newName != null) {
      actorOld = node.getActor();
      // make sure name is not empty
      if (newName.length() == 0)
	newName = actorOld.getDefaultName();
      addUndoPoint("Renaming actor " + actorOld.getName() + " to " + newName);
      exp = m_State.tree.getExpandedNodes();
      actorNew = actorOld.shallowCopy();
      actorNew.setName(newName);
      node.setActor(actorNew);
      m_State.tree.updateActorName(node);
      ((DefaultTreeModel) m_State.tree.getModel()).nodeChanged(node);
      m_State.tree.setModified(m_State.tree.isModified() || !oldName.equals(node.getActor().getName()));
      m_State.tree.notifyActorChangeListeners(new ActorChangeEvent(m_State.tree, node, Type.MODIFY));
      SwingUtilities.invokeLater(new Runnable() {
	@Override
	public void run() {
	  m_State.tree.setExpandedNodes(exp);
	}
      });
      // update all occurrences, if necessary
      parent = (Node) node.getParent();
      if (!m_State.tree.getIgnoreNameChanges())
	AbstractEditPostProcessor.apply(m_State.tree, ((parent != null) ? parent.getActor() : null), actorOld, actorNew);
      SwingUtilities.invokeLater(new Runnable() {
	@Override
	public void run() {
	  m_State.tree.locateAndDisplay(node.getFullName());
	}
      });
    }
  }

  /**
   * The action to execute.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    renameActor(m_State.selPath);
  }
}
