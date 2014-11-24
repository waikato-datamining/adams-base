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
 * FileImport.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.menu;

import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;

import adams.gui.flow.FlowPanel;
import adams.gui.flow.ImportDialog;

/**
 * Lets user import a flow.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FileImport
  extends AbstractFlowEditorMenuItemAction {

  /** for serialization. */
  private static final long serialVersionUID = 5235570137451285010L;

  /** the dialog for importing the flow. */
  protected ImportDialog m_ImportDialog;

  /**
   * Returns the caption of this action.
   * 
   * @return		the caption, null if not applicable
   */
  @Override
  protected String getTitle() {
    return "Import...";
  }

  /**
   * Invoked when an action occurs.
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    FlowPanel	panel;

    if (m_ImportDialog == null) {
      if (getParentDialog() != null)
	m_ImportDialog = new ImportDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
      else
	m_ImportDialog = new ImportDialog(getParentFrame(), true);
    }

    m_ImportDialog.setLocationRelativeTo(m_State);
    m_ImportDialog.setVisible(true);
    if (m_ImportDialog.getOption() != ImportDialog.APPROVE_OPTION)
      return;

    panel = m_State.getFlowPanels().newPanel();
    panel.importFlow(m_ImportDialog.getImport(), m_ImportDialog.getFile());
  }

  /**
   * Performs the actual update of the state of the action.
   */
  @Override
  protected void doUpdate() {
    setEnabled(true);
  }
  
  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    super.cleanUp();
    
    if (m_ImportDialog != null) {
      m_ImportDialog.dispose();
      m_ImportDialog = null;
    }
  }
}
