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
 * MakeInteractive.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */
package adams.gui.flow.tree.menu;

import adams.flow.standalone.SetVariable;
import adams.gui.flow.tree.Node;
import adams.gui.flow.tree.TreeHelper;

import java.awt.event.ActionEvent;

/**
 * Takes one or more SetVariable standalones and turns them into a EnterManyValues
 * actor inside a Trigger.
 * 
 * @author fracpete
 */
public class MakeInteractive
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
    return "Make interactive...";
  }

  /**
   * Updates the action using the current state information.
   */
  @Override
  protected void doUpdate() {
    boolean	enabled;
    Node[]	nodes;

    enabled = m_State.editable && (m_State.numSel > 0) && m_State.isParentMutable;
    if (enabled) {
      nodes = TreeHelper.pathsToNodes(m_State.selPaths);
      for (Node node: nodes) {
        if (!(node.getActor() instanceof SetVariable)) {
          enabled = false;
          break;
	}
      }
    }
    setEnabled(enabled);
  }

  /**
   * The action to execute.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    m_State.tree.getOperations().makeInteractive(m_State.selPaths);
  }
}
