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
 * DataComputeDifference.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.spreadsheetviewer.menu;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.flow.transformer.SpreadSheetDifference;
import adams.gui.core.GUIHelper;
import adams.gui.core.ParameterPanel;
import adams.gui.dialog.ApprovalDialog;

/**
 * Computes the difference between two spreadsheets.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DataComputeDifference
  extends AbstractSpreadSheetViewerMenuItemAction {

  /** for serialization. */
  private static final long serialVersionUID = 5235570137451285010L;

  /**
   * Returns the caption of this action.
   * 
   * @return		the caption, null if not applicable
   */
  @Override
  protected String getTitle() {
    return "Compute difference...";
  }

  /**
   * Computes the difference between the two sheets and inserts it as new tab.
   */
  protected void computeDifference(SpreadSheet sheet1, SpreadSheet sheet2, SpreadSheetColumnRange keyCols) {
    SpreadSheetDifference	filter;

    if ((sheet1 == null) || (sheet2 == null))
      return;

    filter = new SpreadSheetDifference();
    filter.setKeyColumns(keyCols);
    m_State.filterData(getTabbedPane().newTitle(), new SpreadSheet[]{sheet1, sheet2}, filter);
  }

  /**
   * Invoked when an action occurs.
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    ApprovalDialog	dialog;
    ParameterPanel	params;
    final JComboBox	sheet1;
    final JComboBox	sheet2;
    List<String>	titles;
    final JTextField	range;

    if (getParentDialog() != null)
      dialog = new ApprovalDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new ApprovalDialog(getParentFrame(), true);
    dialog.setTitle("Compute difference");
    params = new ParameterPanel();
    dialog.getContentPane().add(params, BorderLayout.CENTER);
    titles = getTabbedPane().getTabTitles();
    sheet1 = new JComboBox(titles.toArray(new String[titles.size()]));
    params.addParameter("First sheet", sheet1);
    params.addParameter("", new JLabel("minus"));
    sheet2 = new JComboBox(titles.toArray(new String[titles.size()]));
    params.addParameter("Second sheet", sheet2);
    params.addParameter("", new JLabel("using"));
    range = new JTextField(10);
    range.setText("");
    range.setToolTipText(new SpreadSheetColumnRange().getExample());
    params.addParameter("Key columns", range);
    dialog.pack();
    dialog.setLocationRelativeTo(m_State);
    dialog.setVisible(true);

    if (dialog.getOption() != ApprovalDialog.APPROVE_OPTION)
      return;

    if (sheet1.getSelectedIndex() == sheet2.getSelectedIndex()) {
      GUIHelper.showErrorMessage(m_State, "You must select two different spreadsheets!");
      return;
    }

    computeDifference(
	getTabbedPane().getSheetAt(sheet1.getSelectedIndex()),
	getTabbedPane().getSheetAt(sheet2.getSelectedIndex()),
	new SpreadSheetColumnRange(range.getText()));
  }

  /**
   * Performs the actual update of the state of the action.
   */
  @Override
  protected void doUpdate() {
    setEnabled(getTabbedPane().getTabCount() >= 2);
  }
}
