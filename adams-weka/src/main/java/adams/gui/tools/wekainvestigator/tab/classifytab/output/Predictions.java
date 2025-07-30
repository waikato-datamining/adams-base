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
 * Predictions.java
 * Copyright (C) 2016-2025 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.classifytab.output;

import adams.core.MessageCollection;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.core.BaseCheckBox;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.GUIHelper;
import adams.gui.core.ImageManager;
import adams.gui.core.SpreadSheetTable;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.event.WekaInvestigatorDataEvent;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import adams.gui.tools.wekainvestigator.data.MemoryContainer;
import adams.gui.tools.wekainvestigator.datatable.DataTable;
import adams.gui.tools.wekainvestigator.datatable.DataTableModel;
import adams.gui.tools.wekainvestigator.output.TableContentPanel;
import adams.gui.tools.wekainvestigator.tab.ClassifyTab;
import adams.gui.tools.wekainvestigator.tab.classifytab.PredictionHelper;
import adams.gui.tools.wekainvestigator.tab.classifytab.ResultItem;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import weka.classifiers.Evaluation;
import weka.core.Instances;

import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

/**
 * Displays the predictions.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Predictions
  extends AbstractOutputGeneratorWithSeparateFoldsSupport<TableContentPanel> {

  private static final long serialVersionUID = -6829245659118360739L;

  public static final String INSTANCE_INDEX = "Instance Index";

  /** whether to prefix the labels with a 1-based index (only nominal classes). */
  protected boolean m_AddLabelIndex;

  /** whether to add an error colunm. */
  protected boolean m_ShowError;

  /** whether to add a relative error column (numeric class only). */
  protected boolean m_ShowRelativeError;

  /** whether to use absolute errors. */
  protected boolean m_UseAbsoluteError;

  /** whether to output the probability of the prediction (only nominal classes). */
  protected boolean m_ShowProbability;

  /** whether to output the class distribution (only nominal classes). */
  protected boolean m_ShowDistribution;

  /** whether to output the weight as well. */
  protected boolean m_ShowWeight;

  /** the ID of the last dataset selected. */
  protected int m_LastID;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates classifier errors plot.\n"
	     + "\n"
	     + "CAUTION:"
	     + "The removal works solely by instance index and will only work correctly with results from "
	     + "cross-validations and explicit test sets that have been loaded into the Investigator.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "add-index", "addLabelIndex",
      false);

    m_OptionManager.add(
      "error", "showError",
      false);

    m_OptionManager.add(
      "relative-error", "showRelativeError",
      false);

    m_OptionManager.add(
      "absolute-error", "useAbsoluteError",
      true);

    m_OptionManager.add(
      "probability", "showProbability",
      false);

    m_OptionManager.add(
      "distribution", "showDistribution",
      false);

    m_OptionManager.add(
      "weight", "showWeight",
      false);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_LastID = -1;
  }

  /**
   * Sets whether to prefix the labels with the index.
   *
   * @param value	true if the label is prefixed with the index
   */
  public void setAddLabelIndex(boolean value) {
    m_AddLabelIndex = value;
    reset();
  }

  /**
   * Returns whether to show the error as well.
   *
   * @return		true if the label is prefixed with the index
   */
  public boolean getAddLabelIndex() {
    return m_AddLabelIndex;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String addLabelIndexTipText() {
    return "If set to true, then the label is prefixed with the index.";
  }

  /**
   * Sets whether to show the error as well.
   *
   * @param value	true if the error is to be displayed as well
   */
  public void setShowError(boolean value) {
    m_ShowError = value;
    reset();
  }

  /**
   * Returns whether to show the error as well.
   *
   * @return		true if the error is displayed as well
   */
  public boolean getShowError() {
    return m_ShowError;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String showErrorTipText() {
    return "If set to true, then the error will be displayed as well.";
  }

  /**
   * Sets whether to show the relative error as well.
   *
   * @param value	true if the relative error is to be displayed as well
   */
  public void setShowRelativeError(boolean value) {
    m_ShowRelativeError = value;
    reset();
  }

  /**
   * Returns whether to show the relative error as well.
   *
   * @return		true if the relative error is displayed as well
   */
  public boolean getShowRelativeError() {
    return m_ShowRelativeError;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String showRelativeErrorTipText() {
    return "If set to true, then the relative error will be displayed as well (numeric class only).";
  }

  /**
   * Sets whether to use an absolute error (ie no direction).
   *
   * @param value	true if to use absolute error
   */
  public void setUseAbsoluteError(boolean value) {
    m_UseAbsoluteError = value;
    reset();
  }

  /**
   * Returns whether to use an absolute error (ie no direction).
   *
   * @return		true if to use absolute error
   */
  public boolean getUseAbsoluteError() {
    return m_UseAbsoluteError;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useAbsoluteErrorTipText() {
    return "If set to true, then the error will be absolute (no direction).";
  }

  /**
   * Sets whether to show the probability of the prediction as well.
   *
   * @param value	true if the probability is to be displayed as well
   */
  public void setShowProbability(boolean value) {
    m_ShowProbability = value;
    reset();
  }

  /**
   * Returns whether to show the probability as well.
   *
   * @return		true if the probability is displayed as well
   */
  public boolean getShowProbability() {
    return m_ShowProbability;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String showProbabilityTipText() {
    return
      "If set to true, then the probability of the prediction will be "
	+ "displayed as well (only for nominal class attributes).";
  }

  /**
   * Sets whether to show the class distribution as well.
   *
   * @param value	true if the class distribution is to be displayed as well
   */
  public void setShowDistribution(boolean value) {
    m_ShowDistribution = value;
    reset();
  }

  /**
   * Returns whether to show the class distribution as well.
   *
   * @return		true if the class distribution is displayed as well
   */
  public boolean getShowDistribution() {
    return m_ShowDistribution;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String showDistributionTipText() {
    return
      "If set to true, then the class distribution will be displayed as "
	+ "well (only for nominal class attributes).";
  }

  /**
   * Sets whether to show the weight as well.
   *
   * @param value	true if the weight is to be displayed as well
   */
  public void setShowWeight(boolean value) {
    m_ShowWeight = value;
    reset();
  }

  /**
   * Returns whether to show the weight as well.
   *
   * @return		true if the weight is displayed as well
   */
  public boolean getShowWeight() {
    return m_ShowWeight;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String showWeightTipText() {
    return
      "If set to true, then the instance weight will be displayed as well.";
  }

  /**
   * The title to use for the tab.
   *
   * @return		the title
   */
  public String getTitle() {
    return "Predictions";
  }

  /**
   * Checks whether output can be generated from this item.
   *
   * @param item	the item to check
   * @return		true if output can be generated
   */
  public boolean canGenerateOutput(ResultItem item) {
    return item.hasEvaluation() && (item.getEvaluation().predictions() != null);
  }

  /**
   * Generates the output from the evaluation.
   *
   * @param eval		the evaluation to use
   * @param originalIndices	the original indices, can be null
   * @param additionalAttributes	the additional attributes to use, can be null
   * @param errors		for collecting error messages
   * @return			the generated output
   */
  protected TableContentPanel createOutput(ResultItem item, Evaluation eval, int[] originalIndices, SpreadSheet additionalAttributes, MessageCollection errors) {
    SpreadSheet		sheet;
    SpreadSheetTable	table;

    sheet = PredictionHelper.toSpreadSheet(
      this, errors, eval, originalIndices, additionalAttributes, m_AddLabelIndex, m_ShowDistribution, m_ShowProbability,
      m_ShowError, m_ShowWeight, m_UseAbsoluteError, m_ShowRelativeError);
    if (sheet == null) {
      if (errors.isEmpty())
	errors.add("Failed to generate prediction!");
      return null;
    }
    table = new SpreadSheetTable(sheet);
    table.setCellPopupMenuCustomizer((MouseEvent e, JPopupMenu menu) -> {
      JMenuItem menuitem = new JMenuItem("Remove from dataset", ImageManager.getIcon("delete-row"));
      menuitem.setEnabled(table.getSelectedRows().length > 0);
      menuitem.addActionListener((ActionEvent ae) -> {
	SpreadSheet data = sheet.getHeader();
	for (int index: table.getSelectedRows())
	  data.addRow().assign(sheet.getRow(table.getActualRow(index)));
	removeData(item, data);
      });
      menu.addSeparator();
      menu.add(menuitem);
    });

    return new TableContentPanel(table, true, true);
  }

  /**
   * Determines the last selected row.
   *
   * @param tab		the classify tab to obtain the data from
   * @return		the last selected row (0 by default)
   */
  protected int determineLastSelectedRow(ClassifyTab tab) {
    int		d;

    if (m_LastID > -1) {
      for (d = 0; d < tab.getData().size(); d++) {
	if (tab.getData().get(d).getID() == m_LastID)
	  return d;
      }
    }

    return 0;
  }

  /**
   * Removes the instances from the current dataset.
   *
   * @param data	the data points to remove
   */
  protected void removeData(ResultItem item, SpreadSheet data) {
    int			colIndex;
    ClassifyTab 	tab;
    ApprovalDialog 	dialog;
    DataTableModel 	model;
    DataTable 		table;
    BaseCheckBox 	checkCopy;
    JPanel 		panel;
    DataContainer 	cont;
    DataContainer	contNew;
    Instances 		inst;
    int			index;
    TIntList 		indices;
    int 		selRow;

    colIndex = data.getHeaderRow().indexOfContent(INSTANCE_INDEX);
    if (colIndex == -1) {
      GUIHelper.showErrorMessage(null, "Failed to locate column: " + INSTANCE_INDEX);
      return;
    }

    tab = (ClassifyTab) GUIHelper.getParent(item.getTabbedPane(), ClassifyTab.class);
    if (tab == null) {
      GUIHelper.showErrorMessage(null, "Failed to get classify tab!");
      return;
    }

    // let user select dataset to remove the data points from
    if (GUIHelper.getParentDialog(tab) != null)
      dialog = new ApprovalDialog(GUIHelper.getParentDialog(tab), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new ApprovalDialog(GUIHelper.getParentFrame(tab), true);
    dialog.setTitle("Select dataset to update");
    model = new DataTableModel(tab.getData(), true);
    table = new DataTable(model);
    table.setAutoResizeMode(DataTable.AUTO_RESIZE_OFF);
    table.setOptimalColumnWidth();
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setSelectedRow(determineLastSelectedRow(tab));
    checkCopy = new BaseCheckBox("Create copy of dataset first before removing rows");
    panel = new JPanel(new BorderLayout(5, 5));
    panel.add(new BaseScrollPane(table), BorderLayout.CENTER);
    panel.add(checkCopy, BorderLayout.SOUTH);
    panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    dialog.getContentPane().add(panel, BorderLayout.CENTER);
    dialog.pack();
    dialog.setLocationRelativeTo(tab);
    dialog.setVisible(true);
    if (dialog.getOption() != ApprovalDialog.APPROVE_OPTION)
      return;
    selRow = table.getSelectedRow();
    if (selRow < 0)
      return;

    // remove data
    cont = tab.getData().get(selRow);
    if (checkCopy.isSelected()) {
      contNew = new MemoryContainer(new Instances(cont.getData()));
      contNew.getData().setRelationName("Copy of " + cont.getData().relationName());
      tab.getData().add(contNew);
      cont = contNew;
    }
    m_LastID = cont.getID();
    cont.addUndoPoint("Predictions: remove rows");
    inst    = cont.getData();
    indices = new TIntArrayList();
    for (Row row: data.rows()) {
      index = row.getCell(colIndex).toLong().intValue() - 1;
      if (index >= 0)
	indices.add(index);
    }
    indices.sort();
    indices.reverse();
    for (int i: indices.toArray())
      inst.remove(i);
    cont.setModified(true);
    tab.getOwner().fireDataChange(new WekaInvestigatorDataEvent(tab.getOwner()));
  }
}
