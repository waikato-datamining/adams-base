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
 * ActiveRun.java
 * Copyright (C) 2019-2025 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.menu;

import adams.gui.flow.FlowEditorPanel;

import java.awt.event.ActionEvent;

/**
 * Executes/restarts the flow flagged as active (if any).
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ActiveRun
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
    return "Run";
  }

  /**
   * Invoked when an action occurs.
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    if (m_State.getActivePanel().isInputEnabled())
      m_State.getActivePanel().run(true, false);
    else
      m_State.getActivePanel().restart(true, false);
  }

  /**
   * Performs the actual update of the state of the action.
   */
  @Override
  protected void doUpdate() {
    if (m_State.hasActivePanel()) {
      if (m_State.getActivePanel().isInputEnabled()) {
	setName(getTitle());
	setIcon(FlowEditorPanel.getPropertiesMenu().getProperty(getClass().getName() + "-Icon"));
	setEnabled(!m_State.getActivePanel().getTree().isDebug()
		     && m_State.getActivePanel().getTree().isFlow());
      }
      else if (m_State.getActivePanel().isRunning()
		 && !m_State.getActivePanel().isStopping()
		 && !m_State.getActivePanel().getTree().isDebug()
		 && m_State.getActivePanel().getTree().isFlow()) {
	setName("Restart");
	setIcon(FlowEditorPanel.getPropertiesMenu().getProperty(getClass().getName() + "Restart-Icon"));
	setEnabled(true);
      }
    }
    else {
      setEnabled(false);
    }
  }
}
