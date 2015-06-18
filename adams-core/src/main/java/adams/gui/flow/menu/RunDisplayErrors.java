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
 * RunDisplayErrors.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.menu;

import adams.db.LogEntryHandler;
import adams.gui.core.BaseDialog;
import adams.gui.tools.LogEntryViewerPanel;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

/**
 * Displays errors from last run.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RunDisplayErrors
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
    return "Display errors...";
  }

  /**
   * Invoked when an action occurs.
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    BaseDialog		dialog;
    LogEntryHandler	handler;
    LogEntryViewerPanel	panel;

    if (m_State.getLastFlow() == null)
      return;
    if (!(m_State.getLastFlow() instanceof LogEntryHandler))
      return;
    handler = (LogEntryHandler) m_State.getLastFlow();
    if (handler.getLogEntries().size() == 0)
      return;

    if (getParentDialog() != null)
      dialog = new BaseDialog(getParentDialog(), ModalityType.MODELESS);
    else
      dialog = new BaseDialog(getParentFrame(), false);
    dialog.setTitle("Flow execution errors");
    panel = new LogEntryViewerPanel();
    panel.display(handler.getLogEntries());
    dialog.getContentPane().setLayout(new BorderLayout());
    dialog.getContentPane().add(panel, BorderLayout.CENTER);
    dialog.setSize(new Dimension(800, 600));
    dialog.setLocationRelativeTo(m_State);
    dialog.setVisible(true);
  }

  /**
   * Performs the actual update of the state of the action.
   */
  @Override
  protected void doUpdate() {
    setEnabled(
	   m_State.hasCurrentPanel() 
	&& isInputEnabled()
	&& (m_State.getLastFlow() != null)
	&& (m_State.getLastFlow() instanceof LogEntryHandler)
	&& (((LogEntryHandler) m_State.getLastFlow()).countLogEntries() > 0));
  }
}
