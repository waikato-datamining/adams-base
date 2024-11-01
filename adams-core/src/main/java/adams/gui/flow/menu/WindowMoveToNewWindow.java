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
 * WindowMoveToNewWindow.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.menu;

import adams.gui.flow.FlowEditorPanel;
import adams.gui.flow.FlowPanel;

import java.awt.event.ActionEvent;

/**
 * Moves the tab to a new editor window.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class WindowMoveToNewWindow
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
    return "Move to new window";
  }
  
  /**
   * Invoked when an action occurs.
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    FlowEditorPanel 	editor;
    FlowPanel		panel;

    panel  = m_State.getCurrentPanel();
    editor = m_State.newWindow();
    m_State.getFlowPanels().remove(panel);
    editor.getFlowPanels().addPage(panel.getTitle(), panel);
    editor.getFlowPanels().setSelectedPage(panel);
    panel.requestFocus();
  }

  /**
   * Performs the actual update of the state of the action.
   */
  @Override
  protected void doUpdate() {
    setEnabled(m_State.hasCurrentPanel());
  }
}
