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
 * ViewShowInputOutput.java
 * Copyright (C) 2014-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.menu;

import adams.gui.flow.FlowEditorPanel;

import java.awt.event.ActionEvent;

/**
 * Enables/disables input/output hints visibility.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ViewShowInputOutput
  extends AbstractFlowEditorCheckBoxMenuItemAction {

  /** for serialization. */
  private static final long serialVersionUID = 5235570137451285010L;

  /**
   * Returns the caption of this action.
   * 
   * @return		the caption, null if not applicable
   */
  @Override
  protected String getTitle() {
    return "Show input/output";
  }

  /**
   * Returns the initial selected state of the menu item.
   * 
   * @return		true if selected initially
   */
  @Override
  protected boolean isInitiallySelected() {
    return FlowEditorPanel.getPropertiesEditor().getBoolean("ShowInputOutput", true);
  }
  
  /**
   * Invoked when an action occurs.
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    m_State.getCurrentPanel().getTree().setShowInputOutput(isSelected());
  }

  /**
   * Performs the actual update of the state of the action.
   */
  @Override
  protected void doUpdate() {
    setEnabled(m_State.hasCurrentPanel());
    if (m_State.hasCurrentPanel())
      setSelected(m_State.getCurrentPanel().getTree().getShowInputOutput());
  }
}
