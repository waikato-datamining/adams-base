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
 * Copyright (C) 2016-2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.datatable.action;

import adams.flow.container.WekaTrainTestSetContainer;
import adams.gui.core.GUIHelper;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.event.WekaInvestigatorDataEvent;
import adams.gui.goe.GenericObjectEditorDialog;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import adams.gui.tools.wekainvestigator.data.MemoryContainer;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import weka.classifiers.DefaultRandomSplitGenerator;
import weka.classifiers.SplitGenerator;
import weka.core.Instances;

import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Creates train/test splits from a dataset and inserts these as new datasets.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Split
  extends AbstractEditableDataTableAction {

  private static final long serialVersionUID = -8374323161691034031L;

  /** the threshold for number of generated containers. */
  public final static int NUM_CONTAINERS_THRESHOLD = 10;

  /** the last splitter. */
  protected SplitGenerator m_LastSplitter;

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
    SplitGenerator			splitter;
    GenericObjectEditorDialog 		dialog;
    List<WekaTrainTestSetContainer> 	ttconts;
    WekaTrainTestSetContainer 		ttcont;
    DataContainer 			cont;
    MemoryContainer 			trainCont;
    MemoryContainer 			testCont;
    int					retVal;
    int					i;
    TIntList				indices;
    String				suffix;

    if (GUIHelper.getParentDialog(getOwner()) != null)
      dialog = new GenericObjectEditorDialog(GUIHelper.getParentDialog(getOwner()), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new GenericObjectEditorDialog(GUIHelper.getParentFrame(getOwner()), true);
    dialog.getGOEEditor().setCanChangeClassInDialog(true);
    dialog.getGOEEditor().setClassType(SplitGenerator.class);
    dialog.setCurrent(m_LastSplitter);
    dialog.setTitle("Split");
    dialog.setLocationRelativeTo(getOwner().getOwner());
    dialog.setVisible(true);
    if (dialog.getResult() != ApprovalDialog.APPROVE_OPTION)
      return;

    splitter       = (SplitGenerator) dialog.getCurrent();
    m_LastSplitter = (SplitGenerator) dialog.getCurrent();

    cont = getSelectedData()[0];
    logMessage("Splitting dataset: " + cont.getID() + "/" + cont.getData().relationName() + " [" + cont.getSource() + "]");
    splitter.setData(cont.getData());
    ttconts = new ArrayList<>();
    while (splitter.hasNext())
      ttconts.add(splitter.next());

    // too many containers?
    if (ttconts.size() > NUM_CONTAINERS_THRESHOLD) {
      retVal = GUIHelper.showConfirmMessage(getOwner(), "Splitter generated " + ttconts.size() + " containers (with train/test sets), proceed?");
      if (retVal != ApprovalDialog.APPROVE_OPTION) {
        logMessage("Splitting dataset aborted!");
        return;
      }
    }

    indices = new TIntArrayList();
    suffix  = "";
    for (i = 0; i < ttconts.size(); i++) {
      ttcont = ttconts.get(i);
      if (ttconts.size() > 1)
        suffix = "-" + (i+1);
      trainCont = new MemoryContainer((Instances) ttcont.getValue(WekaTrainTestSetContainer.VALUE_TRAIN));
      trainCont.getData().setRelationName(cont.getData().relationName() + "-train" + suffix);
      testCont = new MemoryContainer((Instances) ttcont.getValue(WekaTrainTestSetContainer.VALUE_TEST));
      testCont.getData().setRelationName(cont.getData().relationName() + "-test" + suffix);
      getData().add(trainCont);
      getData().add(testCont);
      indices.add(getData().size() - 2);
      indices.add(getData().size() - 1);
    }
    logMessage("Successfully split " + cont.getID() + " into " + (ttconts.size()*2) + " datasets!");
    fireDataChange(new WekaInvestigatorDataEvent(getOwner().getOwner(), WekaInvestigatorDataEvent.ROWS_ADDED, indices.toArray()));
  }

  /**
   * Updates the action.
   */
  @Override
  public void update() {
    setEnabled(!isBusy() && getTable().getSelectedRowCount() == 1);
  }
}
