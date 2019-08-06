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

package adams.gui.core.spreadsheettable;

import adams.core.Properties;
import adams.core.Utils;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.data.spreadsheet.SpreadSheetUtils;
import adams.data.statistics.AbstractArrayStatistic;
import adams.data.statistics.AbstractArrayStatistic.StatisticContainer;
import adams.data.statistics.ArrayMean;
import adams.data.statistics.StatUtils;
import adams.gui.core.GUIHelper;
import adams.gui.core.PropertiesParameterPanel;
import adams.gui.core.PropertiesParameterPanel.PropertyType;
import adams.gui.core.SpreadSheetTable;
import adams.gui.core.spreadsheettable.SpreadSheetTablePopupMenuItemHelper.TableState;
import adams.gui.dialog.PropertiesParameterDialog;
import adams.gui.dialog.SpreadSheetDialog;
import adams.gui.goe.GenericObjectEditorPanel;

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

  public static final String KEY_COLUMNS = "columns";

  public static final String KEY_STATISTIC = "statistic";

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Allows the user to calculate array statistics from selected rows.";
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
   * Prompts the user for parameters.
   *
   * @param table	the table this is for
   * @return		the parameters, null if cancelled dialog
   */
  protected Properties promptParameters(SpreadSheetTable table) {
    PropertiesParameterDialog 	dialog;
    PropertiesParameterPanel 	panel;
    Properties 			last;

    if (GUIHelper.getParentDialog(table) != null)
      dialog = new PropertiesParameterDialog(GUIHelper.getParentDialog(table), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new PropertiesParameterDialog(GUIHelper.getParentFrame(table), true);
    panel = dialog.getPropertiesParameterPanel();
    panel.addPropertyType(KEY_COLUMNS, PropertyType.RANGE);
    panel.setLabel(KEY_COLUMNS, "Columns");
    panel.setHelp(KEY_COLUMNS, "The columns to operate on");
    panel.addPropertyType(KEY_STATISTIC, PropertyType.OBJECT_EDITOR);
    panel.setLabel(KEY_STATISTIC, "Array statistic");
    panel.setHelp(KEY_STATISTIC, "The array statistics to apply");
    panel.setChooser(KEY_STATISTIC, new GenericObjectEditorPanel(AbstractArrayStatistic.class, new ArrayMean(), true));
    panel.setPropertyOrder(new String[]{KEY_COLUMNS, KEY_STATISTIC});
    last = new Properties();
    last.setProperty(KEY_COLUMNS, SpreadSheetColumnRange.ALL);
    last.setObject(KEY_STATISTIC, new ArrayMean());
    dialog.setProperties(last);
    last = (Properties) table.getLastSetup(getClass(), true, false);
    if (last != null)
      dialog.setProperties(last);
    dialog.setTitle(getMenuItem());
    dialog.pack();
    dialog.setLocationRelativeTo(table.getParent());
    dialog.setVisible(true);
    if (dialog.getOption() != PropertiesParameterDialog.APPROVE_OPTION)
      return null;

    return dialog.getProperties();
  }

  /**
   * Processes the specified row.
   *
   * @param state	the table state
   * @return		true if successful
   */
  @Override
  protected boolean doProcessSelectedRows(TableState state) {
    Properties 			last;
    StatisticContainer 		stats;
    SpreadSheetDialog 		dialogStats;
    int[] 			rows;
    int[]			cols;
    AbstractArrayStatistic	array;
    SpreadSheetColumnRange	columns;
    SpreadSheet			sheet;
    int				row;

    rows = Utils.adjustIndices(state.actRows, 2);

    // prompt user
    last = promptParameters(state.table);
    if (last == null)
      return false;

    array = last.getObject(KEY_STATISTIC, AbstractArrayStatistic.class);
    if (array == null) {
      GUIHelper.showErrorMessage(
	GUIHelper.getParentComponent(state.table), "Failed to instantiate array statistic!");
      return false;
    }
    if ((array.getMin() != -1) && (array.getMin() > state.actRows.length)) {
      GUIHelper.showErrorMessage(
	GUIHelper.getParentComponent(state.table), "Statistic " + Utils.classToString(last) + " requires at least " + array.getMin() + " rows!");
    }
    if ((array.getMax() != -1) && (array.getMax() < state.actRows.length)) {
      GUIHelper.showErrorMessage(
	GUIHelper.getParentComponent(state.table), "Statistic " + Utils.classToString(last) + " can only handle at most " + array.getMax() + " rows!");
    }
    state.table.addLastSetup(getClass(), true, false, last);
    sheet = state.table.toSpreadSheet(state.range, true);
    columns = new SpreadSheetColumnRange(last.getProperty(KEY_COLUMNS, SpreadSheetColumnRange.ALL));
    columns.setData(sheet);
    cols = columns.getIntIndices();
    for (row = 0; row < sheet.getRowCount(); row++)
      array.add(StatUtils.toNumberArray(SpreadSheetUtils.getNumericRow(sheet, row, cols)));
    try {
      stats = array.calculate();
    }
    catch (Exception e) {
      GUIHelper.showErrorMessage(
	GUIHelper.getParentComponent(state.table), "Failed to calculate statistics for rows #" + Utils.arrayToString(rows) + "!", e);
      return false;
    }

    if (GUIHelper.getParentDialog(state.table) != null)
      dialogStats = new SpreadSheetDialog(GUIHelper.getParentDialog(state.table), ModalityType.MODELESS);
    else
      dialogStats = new SpreadSheetDialog(GUIHelper.getParentFrame(state.table), false);
    dialogStats.setDefaultCloseOperation(SpreadSheetDialog.DISPOSE_ON_CLOSE);
    dialogStats.setTitle("Statistics for rows #" + Utils.arrayToString(rows));
    dialogStats.setSpreadSheet(stats.toSpreadSheet());
    dialogStats.pack();
    dialogStats.setLocationRelativeTo(null);
    dialogStats.setVisible(true);

    return true;
  }

  /**
   * Processes the specified row.
   *
   * @param state	the table state
   * @return		true if successful
   */
  public boolean processRow(TableState state) {
    return processSelectedRows(state);
  }
}
