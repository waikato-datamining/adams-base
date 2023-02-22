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
 * RemoveTestSet.java
 * Copyright (C) 2023 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.datatable.action;

import adams.core.option.OptionUtils;
import adams.data.weka.WekaAttributeIndex;
import adams.gui.core.BaseComboBox;
import adams.gui.core.GUIHelper;
import adams.gui.core.IndexTextField;
import adams.gui.core.ParameterPanel;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.event.WekaInvestigatorDataEvent;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import adams.gui.tools.wekainvestigator.data.MemoryContainer;
import adams.gui.tools.wekainvestigator.evaluation.DatasetHelper;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.RemoveTestInstances;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;

/**
 * Removes the test instances from one dataset in another.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RemoveTestSet
  extends AbstractEditableDataTableAction {

  private static final long serialVersionUID = -8374323161691034031L;

  /** the last used source ID attribute. */
  protected String m_LastSourceID;

  /** the last used test set ID attribute. */
  protected String m_LastTestSetID;

  /**
   * Instantiates the action.
   */
  public RemoveTestSet() {
    super();
    setName("Remove test set");
    setAsynchronous(true);
  }

  /**
   * Invoked when an action occurs.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    ParameterPanel		params;
    IndexTextField 		textSourceID;
    BaseComboBox<String>	comboTestSet;
    IndexTextField 		textTestSetID;
    ApprovalDialog		dialog;
    String			sourceID;
    String			testSetID;
    DataContainer 		contSource;
    int				contTestSetIndex;
    DataContainer		contTestSet;
    MemoryContainer 		newCont;
    RemoveTestInstances 	filter;
    int				i;

    contSource = getSelectedData()[0];

    params = new ParameterPanel();
    textSourceID = new IndexTextField("1");
    params.addParameter("ID attribute in source", textSourceID);
    comboTestSet = new BaseComboBox<>(DatasetHelper.generateDatasetList(getOwner().getData()));
    contTestSetIndex = 0;
    for (i = 0; i < getData().size(); i++) {
      if (getData().get(i).getID() != contSource.getID()) {
        contTestSetIndex = i;
        break;
      }
    }
    comboTestSet.setSelectedIndex(contTestSetIndex);
    params.addParameter("Test set", comboTestSet);
    textTestSetID = new IndexTextField("1");
    params.addParameter("ID attribute in test set", textTestSetID);

    if (GUIHelper.getParentDialog(getOwner()) != null)
      dialog = new ApprovalDialog(GUIHelper.getParentDialog(getOwner()), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new ApprovalDialog(GUIHelper.getParentFrame(getOwner()), true);
    dialog.setTitle("Remove test set");
    dialog.getContentPane().add(params, BorderLayout.CENTER);
    dialog.pack(null, GUIHelper.getDefaultSmallDialogDimension());
    dialog.setLocationRelativeTo(getOwner().getOwner());
    dialog.setVisible(true);
    if (dialog.getOption() != ApprovalDialog.APPROVE_OPTION)
      return;

    sourceID         = textSourceID.getText();
    contTestSetIndex = comboTestSet.getSelectedIndex();
    contTestSet      = getData().get(contTestSetIndex);
    testSetID        = textTestSetID.getText();

    m_LastSourceID   = sourceID;
    m_LastTestSetID  = testSetID;

    logMessage("Remove test set: " + contSource.getID() + "/" + contSource.getData().relationName() + " [" + contSource.getSource() + "]");

    filter = new RemoveTestInstances();
    filter.setID(new WekaAttributeIndex(sourceID));
    filter.setIDTest(new WekaAttributeIndex(testSetID));
    filter.setSuppliedTestSet(contTestSet.getData());
    logMessage("Filter setup: " + OptionUtils.getCommandLine(filter));
    try {
      filter.setInputFormat(contSource.getData());
      newCont = new MemoryContainer(Filter.useFilter(contSource.getData(), filter));
    }
    catch (Exception ex) {
      GUIHelper.showErrorMessage(getOwner(), "Failed to remove test set!", ex);
      return;
    }

    getData().add(newCont);
    logMessage("Successfully removed test set from " + contSource.getID() + " and added " + newCont.getID() + "!");
    fireDataChange(new WekaInvestigatorDataEvent(getOwner().getOwner(), WekaInvestigatorDataEvent.ROWS_ADDED, getData().size() - 1));
  }

  /**
   * Updates the action.
   */
  @Override
  public void update() {
    setEnabled(!isBusy() && (getTable().getSelectedRowCount() == 1) && (getTable().getRowCount() > 1));
  }
}
