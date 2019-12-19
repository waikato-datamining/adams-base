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

import adams.core.ObjectCopyHelper;
import adams.flow.container.WekaTrainTestSetContainer;
import adams.gui.core.BaseCheckBox;
import adams.gui.core.GUIHelper;
import adams.gui.core.NumberTextField;
import adams.gui.core.NumberTextField.Type;
import adams.gui.core.ParameterPanel;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.event.WekaInvestigatorDataEvent;
import adams.gui.goe.GenericObjectEditorPanel;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import adams.gui.tools.wekainvestigator.data.MemoryContainer;
import weka.classifiers.DefaultRandomSplitGenerator;
import weka.classifiers.RandomSplitGenerator;
import weka.core.Instances;

import java.awt.BorderLayout;
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

  /** the last percentage. */
  protected double m_LastPercentage;

  /** the last seed. */
  protected int m_LastSeed;

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
    m_LastPercentage = 66.0;
    m_LastSeed       = 1;
    m_LastSplitter   = new DefaultRandomSplitGenerator();
  }

  /**
   * Invoked when an action occurs.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    ParameterPanel		params;
    BaseCheckBox		checkboxPreserveOrder;
    NumberTextField		textSeed;
    NumberTextField		textPercentage;
    GenericObjectEditorPanel    goeSplitter;
    ApprovalDialog		dialog;
    int				seed;
    double			percentage;
    RandomSplitGenerator	splitter;
    WekaTrainTestSetContainer	ttcont;
    DataContainer 		cont;
    MemoryContainer 		trainCont;
    MemoryContainer 		testCont;

    params = new ParameterPanel();
    checkboxPreserveOrder = new BaseCheckBox();
    checkboxPreserveOrder.setToolTipText("Whether to preserve the order or randomize the data");
    params.addParameter("Preserve order", checkboxPreserveOrder);
    textSeed = new NumberTextField(Type.INTEGER);
    textSeed.setValue(m_LastSeed);
    textSeed.setToolTipText("The seed value to use when randomizing the data");
    params.addParameter("Seed", textSeed);
    textPercentage = new NumberTextField(Type.DOUBLE);
    textPercentage.setValue(m_LastPercentage);
    textPercentage.setToolTipText("The percentage to use for the training set (0;100)");
    params.addParameter("Train percentage", textPercentage);
    goeSplitter = new GenericObjectEditorPanel(RandomSplitGenerator.class, new DefaultRandomSplitGenerator(), true);
    goeSplitter.setCurrent(m_LastSplitter);
    params.addParameter("Splitter", goeSplitter);
    if (GUIHelper.getParentDialog(getOwner()) != null)
      dialog = new ApprovalDialog(GUIHelper.getParentDialog(getOwner()), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new ApprovalDialog(GUIHelper.getParentFrame(getOwner()), true);
    dialog.setTitle("Split");
    dialog.getContentPane().add(params, BorderLayout.CENTER);
    dialog.pack();
    dialog.setLocationRelativeTo(getOwner().getOwner());
    dialog.setVisible(true);
    if (dialog.getOption() != ApprovalDialog.APPROVE_OPTION)
      return;

    seed       = textSeed.getValue().intValue();
    percentage = textPercentage.getValue().doubleValue();
    splitter   = (RandomSplitGenerator) goeSplitter.getCurrent();
    if ((percentage <= 0) || (percentage >= 100)) {
      GUIHelper.showErrorMessage(getOwner(), "Percentage must satisfy 0 < x < 100, provided: " + percentage);
      return;
    }

    m_LastSeed       = seed;
    m_LastPercentage = percentage;
    m_LastSplitter   = ObjectCopyHelper.copyObject(splitter);

    cont = getSelectedData()[0];
    logMessage("Splitting dataset: " + cont.getID() + "/" + cont.getData().relationName() + " [" + cont.getSource() + "]");
    splitter.setPreserveOrder(checkboxPreserveOrder.isSelected());
    splitter.setSeed(seed);
    splitter.setPercentage(percentage / 100.0);
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
