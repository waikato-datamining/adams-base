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
 * InspectMemory.java
 * Copyright (C) 2014 University of Waikato, Hamilton, NZ
 */
package adams.gui.flow.tree.menu;

import adams.core.SizeOf;
import adams.flow.control.Flow;

import java.awt.event.ActionEvent;

/**
 * For analyzing the memory consumption of an actor.
 * 
 * @author fracpete
 */
public class InspectMemory
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
    return "Inspect memory";
  }

  /**
   * Updates the action using the current state information.
   */
  @Override
  protected void doUpdate() {
    setEnabled(m_State.isSingleSel && (m_State.runningFlow instanceof Flow) && SizeOf.isSizeOfAgentAvailable());
  }

  /**
   * The action to execute.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    m_State.tree.getOperations().inspectMemory(m_State.selPath, (Flow) m_State.runningFlow);
  }
}
