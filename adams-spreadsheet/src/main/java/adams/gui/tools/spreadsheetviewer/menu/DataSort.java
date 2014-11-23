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
 * DataSort.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.spreadsheetviewer.menu;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;

import adams.gui.dialog.ApprovalDialog;
import adams.gui.event.SortSetupEvent;
import adams.gui.event.SortSetupListener;
import adams.gui.tools.spreadsheetviewer.SortPanel;

/**
 * Sorts the spreadsheet.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DataSort
  extends AbstractSpreadSheetViewerMenuItemAction {

  /** for serialization. */
  private static final long serialVersionUID = 5235570137451285010L;
  
  /** the sort panel. */
  protected SortPanel m_SortPanel;

  /**
   * Returns the caption of this action.
   * 
   * @return		the caption, null if not applicable
   */
  @Override
  protected String getTitle() {
    return "Sort...";
  }

  /**
   * Invoked when an action occurs.
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    final ApprovalDialog	dialog;

    if (getParentDialog() != null)
      dialog = new ApprovalDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new ApprovalDialog(getParentFrame(), true);
    dialog.setDefaultCloseOperation(ApprovalDialog.DISPOSE_ON_CLOSE);
    dialog.setTitle("Sort");
    dialog.getApproveButton().setEnabled(false);
    if (m_SortPanel == null) {
      m_SortPanel = new SortPanel();
      m_SortPanel.addSortSetupListener(new SortSetupListener() {
	@Override
	public void sortSetupChanged(SortSetupEvent e) {
	  dialog.getApproveButton().setEnabled(e.getSortPanel().isValidSetup());
	}
      });
    }
    if (m_SortPanel.setSpreadSheet(getTabbedPane().getCurrentSheet()))
      m_SortPanel.addDefinition();
    dialog.getApproveButton().setEnabled(m_SortPanel.isValidSetup());
    dialog.getContentPane().add(m_SortPanel, BorderLayout.CENTER);
    dialog.pack();
    dialog.setLocationRelativeTo(m_State);
    dialog.setVisible(true);
    if (dialog.getOption() != ApprovalDialog.APPROVE_OPTION)
      return;
    getTabbedPane().getCurrentTable().sort(m_SortPanel.getComparator());
  }

  /**
   * Performs the actual update of the state of the action.
   */
  @Override
  protected void doUpdate() {
    setEnabled(isSheetSelected());
  }
}
