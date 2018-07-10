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
 * WindowDuplicateInTab.java
 * Copyright (C) 2014-2018 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.menu;

import adams.gui.flow.FlowPanel;
import adams.gui.flow.tree.Tree.TreeState;

import java.awt.event.ActionEvent;

/**
 * Duplicates the flow in a new page.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class WindowDuplicateInTab
  extends AbstractFlowEditorMenuItemAction {

  /** for serialization. */
  private static final long serialVersionUID = 5235570137451285010L;

  /**
   * Returns the caption of this action.
   * 
   * @return		the caption, null if not applicable
   */
  @Override
  protected String getTitle() {
    return "Duplicate in new page";
  }
  
  /**
   * Invoked when an action occurs.
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    FlowPanel	result;
    FlowPanel 	current;
    TreeState	state;

    current = m_State.getCurrentPanel();
    if (current != null) {
      state  = current.getTree().getState();
      result = m_State.getFlowPanels().newPanel();
      result.getTree().setState(state);
      result.setCurrentFile(current.getCurrentFile());
      result.setModified(current.isModified());
      result.update();
      result.requestFocus();
    }
  }

  /**
   * Performs the actual update of the state of the action.
   */
  @Override
  protected void doUpdate() {
    setEnabled(m_State.hasCurrentPanel());
  }
}
