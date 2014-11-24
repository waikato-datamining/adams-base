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
 * FileExport.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.menu;

import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;

import adams.gui.flow.ExportDialog;

/**
 * Lets user export a flow.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FileExport
  extends AbstractFlowEditorMenuItemAction {

  /** for serialization. */
  private static final long serialVersionUID = 5235570137451285010L;

  /** the dialog for exporting the flow. */
  protected ExportDialog m_ExportDialog;

  /**
   * Returns the caption of this action.
   * 
   * @return		the caption, null if not applicable
   */
  @Override
  protected String getTitle() {
    return "Export...";
  }

  /**
   * Invoked when an action occurs.
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    if (m_ExportDialog == null) {
      if (getParentDialog() != null)
	m_ExportDialog = new ExportDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
      else
	m_ExportDialog = new ExportDialog(getParentFrame(), true);
    }

    m_ExportDialog.setLocationRelativeTo(m_State);
    m_ExportDialog.setVisible(true);
    if (m_ExportDialog.getOption() != ExportDialog.APPROVE_OPTION)
      return;

    m_State.getCurrentPanel().exportFlow(m_ExportDialog.getExport(), m_ExportDialog.getFile());
  }

  /**
   * Performs the actual update of the state of the action.
   */
  @Override
  protected void doUpdate() {
    setEnabled(
	   m_State.hasCurrentPanel() 
	&& isInputEnabled());
  }
  
  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    super.cleanUp();
    
    if (m_ExportDialog != null) {
      m_ExportDialog.dispose();
      m_ExportDialog = null;
    }
  }
}
