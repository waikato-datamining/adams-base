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
 * CompareModels.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.classifytab.history;

import adams.core.MessageCollection;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetUtils;
import adams.data.statistics.ArrayHistogram;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.GUIHelper;
import adams.gui.core.SpreadSheetTable;
import adams.gui.core.SpreadSheetTableModel;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.goe.GenericObjectEditorDialog;
import adams.gui.tools.wekainvestigator.tab.ClassifyTab.HistoryPanel;
import adams.gui.tools.wekainvestigator.tab.classifytab.PredictionHelper;
import adams.gui.tools.wekainvestigator.tab.classifytab.ResultItem;
import adams.gui.visualization.spreadsheet.HistogramFactory;
import com.googlecode.jfilechooserbookmarks.gui.BaseScrollPane;

import javax.swing.JMenuItem;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;

/**
 * Compares the predictions of two models.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CompareModels
  extends AbstractHistoryPopupMenuItem {

  /**
   * The category for grouping menu items.
   *
   * @return		the group
   */
  @Override
  public String getCategory() {
    return "Evaluation";
  }

  /**
   * The menu item title.
   *
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Compare models...";
  }

  /**
   * Prompts the user for a histogram setup.
   *
   * @return		the setup, null if user cancelled
   */
  protected ArrayHistogram promptHistogramSetup() {
    HistogramFactory.SetupDialog	setup;
    ArrayHistogram 			result;

    if (getOwner().getParentDialog() != null)
      setup = HistogramFactory.getSetupDialog(getOwner().getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else
      setup = HistogramFactory.getSetupDialog(getOwner().getParentFrame(), true);
    setup.setDefaultCloseOperation(HistogramFactory.SetupDialog.DISPOSE_ON_CLOSE);
    setup.setTitle("Histogram setup");
    result = new ArrayHistogram();
    result.setDisplayRanges(true);
    setup.setCurrent(result);
    setup.setLocationRelativeTo(setup.getParent());
    setup.setVisible(true);
    if (setup.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
      return null;
    result = (ArrayHistogram) setup.getCurrent();
    return result;
  }

  /**
   * Compares the two evaluations (numeric class).
   *
   * @param name1	the name of the first entry
   * @param item1	the first item
   * @param name2	the name of the second entry
   * @param item2	the second item
   * @return		null if successfully generated/displayed, otherwise error message
   */
  protected String compareNumeric(String name1, ResultItem item1, String name2, ResultItem item2) {
    SpreadSheet			sheet1;
    SpreadSheet			sheet2;
    MessageCollection		errors;
    double[]			predicted1;
    double[]			predicted2;
    double[]			diff;
    int				i;
    SpreadSheet			sheetDiff;
    Row				row;
    ApprovalDialog		dialog;
    BaseTabbedPane		tabbedPane;
    SpreadSheetTable		table;
    SpreadSheetTableModel	model;
    HistogramFactory.Panel	histogram;
    ArrayHistogram		arrayHistogram;

    // 1st item
    errors = new MessageCollection();
    sheet1 = PredictionHelper.toSpreadSheet(null, errors, item1, false, false);
    if (sheet1 == null) {
      if (errors.isEmpty())
	return "Failed to obtain predictions from first item: " + name1;
      else
	return "Failed to obtain predictions from first item: " + name1 + "\n" + errors;
    }

    // 2nd item
    errors.clear();
    sheet2 = PredictionHelper.toSpreadSheet(null, errors, item2, false, false);
    if (sheet2 == null) {
      if (errors.isEmpty())
	return "Failed to obtain predictions from second item: " + name2;
      else
	return "Failed to obtain predictions from second item: " + name2 + "\n" + errors;
    }

    predicted1 = SpreadSheetUtils.getNumericColumn(sheet1, sheet1.getHeaderRow().indexOfContent("Predicted"));
    predicted2 = SpreadSheetUtils.getNumericColumn(sheet2, sheet2.getHeaderRow().indexOfContent("Predicted"));
    if (predicted1.length != predicted2.length)
      return "Differing number of predictions: " + predicted1.length + " != " + predicted2.length;

    diff = new double[predicted1.length];
    sheetDiff = new DefaultSpreadSheet();
    sheetDiff.getHeaderRow().addCell("I").setContentAsString("Index");
    sheetDiff.getHeaderRow().addCell("1").setContentAsString(name1);
    sheetDiff.getHeaderRow().addCell("2").setContentAsString(name2);
    sheetDiff.getHeaderRow().addCell("D").setContentAsString("Difference");
    for (i = 0; i < predicted1.length; i++) {
      diff[i] = predicted1[i] - predicted2[i];
      row = sheetDiff.addRow();
      row.addCell("I").setContent(i+1);
      row.addCell("1").setContent(predicted1[i]);
      row.addCell("2").setContent(predicted2[i]);
      row.addCell("D").setContent(diff[i]);
    }

    if (getOwner().getParentDialog() != null)
      dialog = new ApprovalDialog(getOwner().getParentDialog(), ModalityType.MODELESS);
    else
      dialog = new ApprovalDialog(getOwner().getParentFrame(), false);
    dialog.setTitle("'" + name1 + "' vs '" + name2 + "'");
    tabbedPane = new BaseTabbedPane();
    dialog.getContentPane().add(tabbedPane, BorderLayout.CENTER);

    // table
    model = new SpreadSheetTableModel(sheetDiff);
    model.setUseSimpleHeader(true);
    model.setReadOnly(true);
    model.setShowRowColumn(false);
    table = new SpreadSheetTable(model);
    table.setNumDecimals(3);
    tabbedPane.addTab("Table", new BaseScrollPane(table));

    // histogram
    arrayHistogram = promptHistogramSetup();
    if (arrayHistogram == null)
      return null;
    histogram = new HistogramFactory.Panel();
    histogram.add(arrayHistogram, diff, "Difference");
    tabbedPane.addTab("Histogram", histogram);

    // display dialog
    dialog.setSize(GUIHelper.makeWider(GUIHelper.getDefaultDialogDimension()));
    dialog.setLocationRelativeTo(getOwner());
    dialog.setVisible(true);

    return null;
  }

  /**
   * Creates the menu item to add to the history's popup menu.
   *
   * @param history	the history panel this menu is for
   * @param indices	the selected indices
   * @return		the menu item, null if failed to generate
   */
  @Override
  public JMenuItem createMenuItem(final HistoryPanel history, final int[] indices) {
    JMenuItem		result;
    boolean		enabled;
    ResultItem 		item0;
    ResultItem 		item1;

    enabled = false;
    if (indices.length == 2) {
      item0 = history.getEntry(indices[0]);
      item1 = history.getEntry(indices[1]);
      enabled =	(item0.hasEvaluation())
	&& (item0.getEvaluation().predictions() != null)
	&& (item1.hasEvaluation())
	&& (item1.getEvaluation().predictions() != null)
	&& (item0.getEvaluation().getHeader().equalHeaders(item0.getEvaluation().getHeader()))
	&& (item0.getEvaluation().numInstances() == item0.getEvaluation().numInstances())
	&& item0.getEvaluation().getHeader().classAttribute().isNumeric();
    }

    result = new JMenuItem(getTitle());
    result.setEnabled(enabled);

    if (!enabled)
      return result;

    result.addActionListener((ActionEvent e) -> {
      String msg = compareNumeric(
	history.getEntryName(indices[0]),
	history.getEntry(indices[0]),
	history.getEntryName(indices[1]),
	history.getEntry(indices[1]));
      if (msg != null)
	GUIHelper.showErrorMessage(getOwner(), msg);
    });

    return result;
  }
}
