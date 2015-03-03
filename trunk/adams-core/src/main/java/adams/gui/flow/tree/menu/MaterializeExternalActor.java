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
 * MaterializeExternalActor.java
 * Copyright (C) 2014 University of Waikato, Hamilton, NZ
 */
package adams.gui.flow.tree.menu;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import adams.core.Utils;
import adams.flow.core.AbstractActor;
import adams.flow.core.ActorUtils;
import adams.flow.core.ExternalActorHandler;
import adams.gui.core.GUIHelper;
import adams.gui.event.ActorChangeEvent;
import adams.gui.event.ActorChangeEvent.Type;
import adams.gui.flow.tree.Node;

/**
 * Materializes (= includes) an external actor.
 * 
 * @author fracpete
 * @version $Revision: 9906 $
 */
public class MaterializeExternalActor
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
    return "Materialize external actor";
  }

  /**
   * Updates the action using the current state information.
   */
  @Override
  protected void doUpdate() {
    setEnabled(
	   m_State.editable 
	&& (m_State.numSel == 1) 
	&& (m_State.selNode.getActor() instanceof ExternalActorHandler));
  }

  /**
   * The action to execute.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    ExternalActorHandler	handler;
    File			file;
    AbstractActor		actor;
    List<String>		errors;
    Node			newNode;
    int				index;
    Node			parent;
    Node			currNode;
    
    currNode = m_State.selNode;
    parent   = m_State.parent;
    handler  = (ExternalActorHandler) currNode.getActor();
    
    // read external actor
    errors   = new ArrayList<String>();
    file     = handler.getActorFile();
    actor    = ActorUtils.read(file.getAbsolutePath(), errors);
    if (actor == null) {
      if (errors.size() == 0)
	GUIHelper.showErrorMessage(
	    m_State.tree, "Failed to materialize external flow: " + file);
      else
	GUIHelper.showErrorMessage(
	    m_State.tree, "Failed to materialize external flow: " + file + "\n" + Utils.flatten(errors, "\n"));
      return;
    }

    addUndoPoint("Materializing external actor '" + file + "'");

    // integrate actor
    newNode = m_State.tree.buildTree(null, actor, false);
    index   = parent.getIndex(currNode);
    parent.remove(index);
    parent.insert(newNode, index);
    currNode = newNode;
    
    m_State.tree.updateActorName(currNode);
    m_State.tree.setModified(true);
    m_State.tree.nodeStructureChanged(currNode);
    m_State.tree.notifyActorChangeListeners(new ActorChangeEvent(m_State.tree, currNode, Type.MODIFY));
    m_State.tree.locateAndDisplay(currNode.getFullName());
    m_State.tree.refreshTabs();
  }
}
