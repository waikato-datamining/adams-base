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
 * DataSort.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.instances.instancestable;

import adams.gui.core.GUIHelper;
import adams.gui.core.TableRowRange;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.event.InstancesSortSetupEvent;
import adams.gui.event.InstancesSortSetupListener;
import adams.gui.visualization.instances.instancestable.InstancesTablePopupMenuItemHelper.TableState;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;

/**
 * Allows sorting the data using multiple columns.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class DataSort
  extends AbstractProcessColumn {

  private static final long serialVersionUID = 7761583015659462758L;

  /** the sort panel. */
  protected InstancesSortPanel m_InstancesSortPanel;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Allows sorting the data using multiple columns.";
  }

  /**
   * Returns the name of the icon.
   *
   * @return            the name, null if none available
   */
  public String getIconName() {
    return "sort-ascending.png";
  }

  /**
   * Returns the name for the menu item.
   *
   * @return            the name
   */
  @Override
  public String getMenuItem() {
    return "Sort...";
  }

  /**
   * Checks whether the row range can be handled.
   *
   * @param range	the range to check
   * @return		true if handled
   */
  public boolean handlesRowRange(TableRowRange range) {
    return (range == TableRowRange.ALL);
  }

  /**
   * Processes the specified column.
   *
   * @param state	the table state
   * @return		true if successful
   */
  @Override
  protected boolean doProcessColumn(TableState state) {
    final ApprovalDialog dialog;

    if (GUIHelper.getParentDialog(state.table) != null)
      dialog = new ApprovalDialog(GUIHelper.getParentDialog(state.table), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new ApprovalDialog(GUIHelper.getParentFrame(state.table), true);
    dialog.setDefaultCloseOperation(ApprovalDialog.DISPOSE_ON_CLOSE);
    dialog.setTitle("Sort");
    dialog.getApproveButton().setEnabled(false);
    if (m_InstancesSortPanel == null) {
      m_InstancesSortPanel = new InstancesSortPanel();
      m_InstancesSortPanel.addInstancesSortSetupListener(new InstancesSortSetupListener() {
	@Override
	public void sortSetupChanged(InstancesSortSetupEvent e) {
	  dialog.getApproveButton().setEnabled(e.getSortPanel().isValidSetup());
	}
      });
    }
    if (m_InstancesSortPanel.setInstances(state.table.getInstances()))
      m_InstancesSortPanel.addDefinition();
    dialog.getApproveButton().setEnabled(m_InstancesSortPanel.isValidSetup());
    dialog.getContentPane().add(m_InstancesSortPanel, BorderLayout.CENTER);
    dialog.pack();
    dialog.setLocationRelativeTo(dialog.getParent());
    dialog.setVisible(true);
    if (dialog.getOption() != ApprovalDialog.APPROVE_OPTION)
      return false;
    state.table.sort(m_InstancesSortPanel.getComparator());
    return true;
  }
}
