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
 * Split.java
 * Copyright (C) 2016-2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.datatable.action;

import adams.flow.container.WekaTrainTestSetContainer;
import adams.gui.core.GUIHelper;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.event.WekaInvestigatorDataEvent;
import adams.gui.goe.GenericObjectEditorDialog;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import adams.gui.tools.wekainvestigator.data.MemoryContainer;
import weka.classifiers.DefaultRandomSplitGenerator;
import weka.classifiers.RandomSplitGenerator;
import weka.core.Instances;

import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;

/**
 * Creates a percentage split (train/test) from a dataset and inserts these
 * as new datasets.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Split
  extends AbstractEditableDataTableAction {

  private static final long serialVersionUID = -8374323161691034031L;

  /** the last splitter. */
  protected RandomSplitGenerator m_LastSplitter;

  /**
   * Instantiates the action.
   */
  public Split() {
    super();
    setName("Split");
    setIcon("percentage.gif");
    setAsynchronous(true);
    m_LastSplitter   = new DefaultRandomSplitGenerator();
  }

  /**
   * Invoked when an action occurs.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    RandomSplitGenerator	splitter;
    GenericObjectEditorDialog 	dialog;
    WekaTrainTestSetContainer	ttcont;
    DataContainer 		cont;
    MemoryContainer 		trainCont;
    MemoryContainer 		testCont;

    if (GUIHelper.getParentDialog(getOwner()) != null)
      dialog = new GenericObjectEditorDialog(GUIHelper.getParentDialog(getOwner()), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new GenericObjectEditorDialog(GUIHelper.getParentFrame(getOwner()), true);
    dialog.getGOEEditor().setCanChangeClassInDialog(true);
    dialog.getGOEEditor().setClassType(RandomSplitGenerator.class);
    dialog.setCurrent(m_LastSplitter);
    dialog.setTitle("Split");
    dialog.setLocationRelativeTo(getOwner().getOwner());
    dialog.setVisible(true);
    if (dialog.getResult() != ApprovalDialog.APPROVE_OPTION)
      return;

    splitter       = (RandomSplitGenerator) dialog.getCurrent();
    m_LastSplitter = (RandomSplitGenerator) dialog.getCurrent();

    cont = getSelectedData()[0];
    logMessage("Splitting dataset: " + cont.getID() + "/" + cont.getData().relationName() + " [" + cont.getSource() + "]");
    splitter.setData(cont.getData());
    ttcont = splitter.next();
    trainCont = new MemoryContainer((Instances) ttcont.getValue(WekaTrainTestSetContainer.VALUE_TRAIN));
    trainCont.getData().setRelationName(cont.getData().relationName() + "-train");
    testCont = new MemoryContainer((Instances) ttcont.getValue(WekaTrainTestSetContainer.VALUE_TEST));
    testCont.getData().setRelationName(cont.getData().relationName() + "-test");
    getData().add(trainCont);
    getData().add(testCont);
    logMessage("Successfully split " + cont.getID() + " into " + trainCont.getID() + " and " + testCont.getID() + "!");
    fireDataChange(new WekaInvestigatorDataEvent(getOwner().getOwner(), WekaInvestigatorDataEvent.ROWS_ADDED, new int[]{getData().size() - 2, getData().size() - 1}));
  }

  /**
   * Updates the action.
   */
  @Override
  public void update() {
    setEnabled(!isBusy() && getTable().getSelectedRowCount() == 1);
  }
}
