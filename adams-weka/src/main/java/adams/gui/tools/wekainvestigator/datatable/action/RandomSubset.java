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
 * RandomSubset.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.datatable.action;

import adams.gui.core.GUIHelper;
import adams.gui.core.NumberTextField;
import adams.gui.core.NumberTextField.BoundedNumberCheckModel;
import adams.gui.core.NumberTextField.Type;
import adams.gui.core.ParameterPanel;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.event.WekaInvestigatorDataEvent;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import adams.gui.tools.wekainvestigator.data.MemoryContainer;
import weka.filters.Filter;

import javax.swing.JCheckBox;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;

/**
 * Creates a random subset from a dataset and inserts it as a new dataset.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RandomSubset
  extends AbstractEditableDataTableAction {

  private static final long serialVersionUID = -8374323161691034031L;

  /**
   * Instantiates the action.
   */
  public RandomSubset() {
    super();
    setName("Random subset");
    setIcon("spreadsheet_subset_rows.gif");
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
    NumberTextField		textSeed;
    NumberTextField		textPercentage;
    JCheckBox 			checkboxReplacement;
    JCheckBox 			checkboxSupervised;
    NumberTextField		textBias;
    ApprovalDialog		dialog;
    int				seed;
    double			percentage;
    boolean			replacement;
    boolean			supervised;
    double			bias;
    DataContainer 		cont;
    MemoryContainer 		newCont;
    Filter 			filter;

    cont = getSelectedData()[0];

    params = new ParameterPanel();
    textSeed = new NumberTextField(Type.INTEGER);
    textSeed.setValue(1);
    textSeed.setToolTipText("The seed value to use for randomizing the data");
    params.addParameter("Seed", textSeed);
    textPercentage = new NumberTextField(Type.DOUBLE);
    textPercentage.setValue(66.0);
    textPercentage.setToolTipText("The size of the subset (0;100)");
    params.addParameter("Percentage", textPercentage);
    checkboxReplacement = new JCheckBox();
    checkboxReplacement.setToolTipText("Whether to allow instances being drawn multiple times");
    params.addParameter("With replacement", checkboxReplacement);
    if ((cont.getData().classIndex() > -1) && cont.getData().classAttribute().isNominal()) {
      checkboxSupervised = new JCheckBox();
      checkboxSupervised.setToolTipText("Whether to take the class distribution into account");
      params.addParameter("Supervised?", checkboxSupervised);
      textBias = new NumberTextField(Type.DOUBLE);
      textBias.setValue(0.0);
      textBias.setCheckModel(new BoundedNumberCheckModel(Type.DOUBLE, 0.0, 1.0));
      textBias.setToolTipText("Bias towards uniform class distribution: 0 = as in data, 1 = uniform");
      params.addParameter("Bias (if supervised)", textBias);
    }
    else {
      checkboxSupervised = null;
      textBias           = null;
    }

    if (GUIHelper.getParentDialog(getOwner()) != null)
      dialog = new ApprovalDialog(GUIHelper.getParentDialog(getOwner()), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new ApprovalDialog(GUIHelper.getParentFrame(getOwner()), true);
    dialog.setTitle("Random subset");
    dialog.getContentPane().add(params, BorderLayout.CENTER);
    dialog.pack();
    dialog.setLocationRelativeTo(getOwner().getOwner());
    dialog.setVisible(true);
    if (dialog.getOption() != ApprovalDialog.APPROVE_OPTION)
      return;

    seed        = textSeed.getValue().intValue();
    percentage  = textPercentage.getValue().doubleValue();
    replacement = checkboxReplacement.isSelected();
    supervised  = (checkboxSupervised != null) && checkboxSupervised.isSelected();
    bias        = supervised ? textBias.getValue().doubleValue() : 0;

    logMessage("Generating subset: " + cont.getID() + "/" + cont.getData().relationName() + " [" + cont.getSource() + "]");

    if (supervised) {
      filter = new weka.filters.supervised.instance.Resample();
      ((weka.filters.supervised.instance.Resample) filter).setRandomSeed(seed);
      ((weka.filters.supervised.instance.Resample) filter).setSampleSizePercent(percentage);
      ((weka.filters.supervised.instance.Resample) filter).setNoReplacement(!replacement);
      ((weka.filters.supervised.instance.Resample) filter).setBiasToUniformClass(bias);
    }
    else {
      filter = new weka.filters.unsupervised.instance.Resample();
      ((weka.filters.unsupervised.instance.Resample) filter).setRandomSeed(seed);
      ((weka.filters.unsupervised.instance.Resample) filter).setSampleSizePercent(percentage);
      ((weka.filters.unsupervised.instance.Resample) filter).setNoReplacement(!replacement);
    }
    try {
      filter.setInputFormat(cont.getData());
      newCont = new MemoryContainer(Filter.useFilter(cont.getData(), filter));
    }
    catch (Exception ex) {
      GUIHelper.showErrorMessage(getOwner(), "Failed to generate subset!", ex);
      return;
    }

    getData().add(newCont);
    logMessage("Successfully generated subset from " + cont.getID() + ": " + newCont.getID() + "!");
    fireDataChange(new WekaInvestigatorDataEvent(getOwner().getOwner(), WekaInvestigatorDataEvent.ROWS_ADDED, getData().size() - 1));
  }

  /**
   * Updates the action.
   */
  @Override
  public void update() {
    setEnabled(!isBusy() && getTable().getSelectedRowCount() == 1);
  }
}
