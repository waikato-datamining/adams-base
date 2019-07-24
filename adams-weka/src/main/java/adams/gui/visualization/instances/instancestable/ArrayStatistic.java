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
 * ArrayStatistic.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.instances.instancestable;

import adams.core.Utils;
import adams.data.statistics.AbstractArrayStatistic;
import adams.data.statistics.AbstractArrayStatistic.StatisticContainer;
import adams.data.statistics.ArrayMean;
import adams.data.statistics.StatUtils;
import adams.gui.core.GUIHelper;
import adams.gui.dialog.SpreadSheetDialog;
import adams.gui.goe.GenericObjectEditorDialog;
import adams.gui.visualization.instances.InstancesTable;
import weka.core.Instances;

import java.awt.Dialog.ModalityType;

/**
 * Allows the calculation of row statistics.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ArrayStatistic
  extends AbstractProcessSelectedRows
  implements ProcessRow {

  private static final long serialVersionUID = 3101728458818516005L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Allows the user to calculate array statistics from selected rows (internal format).";
  }

  /**
   * Returns the default name for the menu item.
   *
   * @return            the name
   */
  protected String getDefaultMenuItem() {
    return "Array statistics...";
  }

  /**
   * Returns the minimum number of rows that the plugin requires.
   *
   * @return		the minimum
   */
  public int minNumRows() {
    return 1;
  }

  /**
   * Returns the maximum number of rows that the plugin requires.
   *
   * @return		the maximum, -1 for none
   */
  public int maxNumRows() {
    return -1;
  }

  /**
   * Processes the specified row.
   *
   * @param table	the source table
   * @param data	the spreadsheet to use as basis
   * @param actRows	the actual row in the spreadsheet
   * @param selRows	the selected row in the table
   * @return		true if successful
   */
  @Override
  protected boolean doProcessSelectedRows(InstancesTable table, Instances data, int[] actRows, int[] selRows) {
    GenericObjectEditorDialog 	setup;
    AbstractArrayStatistic 	last;
    StatisticContainer 		stats;
    SpreadSheetDialog		dialog;
    int[] 			rows;

    rows = Utils.adjustIndices(actRows, 2);

    // let user customize plot
    if (GUIHelper.getParentDialog(table) != null)
      setup = new GenericObjectEditorDialog(GUIHelper.getParentDialog(table), ModalityType.DOCUMENT_MODAL);
    else
      setup = new GenericObjectEditorDialog(GUIHelper.getParentFrame(table), true);
    setup.setDefaultCloseOperation(GenericObjectEditorDialog.DISPOSE_ON_CLOSE);
    setup.getGOEEditor().setClassType(AbstractArrayStatistic.class);
    setup.getGOEEditor().setCanChangeClassInDialog(true);
    last = (AbstractArrayStatistic) table.getLastSetup(getClass(), true, false);
    if (last == null)
      last = new ArrayMean();
    setup.setCurrent(last);
    setup.setLocationRelativeTo(GUIHelper.getParentComponent(table));
    setup.setVisible(true);
    if (setup.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
      return false;
    last = (AbstractArrayStatistic) setup.getCurrent();
    if ((last.getMin() != -1) && (last.getMin() > actRows.length)) {
      GUIHelper.showErrorMessage(
	GUIHelper.getParentComponent(table), "Statistic " + Utils.classToString(last) + " requires at least " + last.getMin() + " rows!");
    }
    if ((last.getMax() != -1) && (last.getMax() < actRows.length)) {
      GUIHelper.showErrorMessage(
	GUIHelper.getParentComponent(table), "Statistic " + Utils.classToString(last) + " can only handle at most " + last.getMax() + " rows!");
    }
    table.addLastSetup(getClass(), true, false, last);
    for (int row: actRows)
      last.add(StatUtils.toNumberArray(data.instance(row).toDoubleArray()));
    try {
      stats = last.calculate();
    }
    catch (Exception e) {
      GUIHelper.showErrorMessage(
	GUIHelper.getParentComponent(table), "Failed to calculate statistics for rows #" + Utils.arrayToString(rows) + "!", e);
      return false;
    }

    if (GUIHelper.getParentDialog(table) != null)
      dialog = new SpreadSheetDialog(GUIHelper.getParentDialog(table), ModalityType.MODELESS);
    else
      dialog = new SpreadSheetDialog(GUIHelper.getParentFrame(table), false);
    dialog.setDefaultCloseOperation(SpreadSheetDialog.DISPOSE_ON_CLOSE);
    dialog.setTitle("Statistics for rows #" + Utils.arrayToString(rows));
    dialog.setSpreadSheet(stats.toSpreadSheet());
    dialog.pack();
    dialog.setLocationRelativeTo(null);
    dialog.setVisible(true);

    return true;
  }

  /**
   * Processes the specified row.
   *
   * @param table	the source table
   * @param sheet	the spreadsheet to use as basis
   * @param actRow	the actual row in the spreadsheet
   * @param selRow	the selected row in the table
   * @return		true if successful
   */
  public boolean processRow(InstancesTable table, Instances sheet, int actRow, int selRow) {
    return processSelectedRows(table, sheet, new int[]{actRow}, new int[]{selRow});
  }
}
