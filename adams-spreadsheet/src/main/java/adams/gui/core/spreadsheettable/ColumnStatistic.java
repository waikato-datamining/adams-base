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
 * ColumnStatistic.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.core.spreadsheettable;

import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.colstatistic.AbstractColumnStatistic;
import adams.data.spreadsheet.colstatistic.Mean;
import adams.gui.core.GUIHelper;
import adams.gui.core.SpreadSheetTable;
import adams.gui.dialog.SpreadSheetDialog;
import adams.gui.goe.GenericObjectEditorDialog;
import adams.gui.visualization.statistics.HistogramFactory;

import java.awt.Dialog.ModalityType;

/**
 * Allows the calculation of column statistics.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ColumnStatistic
  extends AbstractProcessColumn {

  private static final long serialVersionUID = 3101728458818516005L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Allows the user to calculate statistics for a column.";
  }

  /**
   * Returns the name for the menu item.
   *
   * @return            the name
   */
  @Override
  public String getMenuItem() {
    return "Column statistics...";
  }

  /**
   * Processes the specified column.
   *
   * @param table	the source table
   * @param sheet	the spreadsheet to use as basis
   * @param column	the column in the spreadsheet
   * @return		true if successful
   */
  @Override
  protected boolean doProcessColumn(SpreadSheetTable table, SpreadSheet sheet, int column) {
    GenericObjectEditorDialog 	setup;
    AbstractColumnStatistic 	last;
    SpreadSheet			stats;
    SpreadSheetDialog		dialog;

    // let user customize plot
    if (GUIHelper.getParentDialog(table) != null)
      setup = new GenericObjectEditorDialog(GUIHelper.getParentDialog(table), ModalityType.DOCUMENT_MODAL);
    else
      setup = new GenericObjectEditorDialog(GUIHelper.getParentFrame(table), true);
    setup.setDefaultCloseOperation(HistogramFactory.SetupDialog.DISPOSE_ON_CLOSE);
    setup.getGOEEditor().setClassType(AbstractColumnStatistic.class);
    setup.getGOEEditor().setCanChangeClassInDialog(true);
    last = (AbstractColumnStatistic) table.getLastSetup(getClass(), true, false);
    if (last == null)
      last = new Mean();
    setup.setCurrent(last);
    setup.setLocationRelativeTo(GUIHelper.getParentComponent(table));
    setup.setVisible(true);
    if (setup.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
      return false;
    last = (AbstractColumnStatistic) setup.getCurrent();
    table.addLastSetup(getClass(), true, false, last);
    stats = last.generate(sheet, column);
    if (stats == null) {
      if (last.hasLastError())
	GUIHelper.showErrorMessage(
	  GUIHelper.getParentComponent(table), "Failed to calculate statistics for column #" + (column+1) + ": " + last.getLastError());
      else
	GUIHelper.showErrorMessage(
	  GUIHelper.getParentComponent(table), "Failed to calculate statistics for column #" + (column+1) + "!");
    }
    else {
      if (GUIHelper.getParentDialog(table) != null)
	dialog = new SpreadSheetDialog(GUIHelper.getParentDialog(table), ModalityType.MODELESS);
      else
	dialog = new SpreadSheetDialog(GUIHelper.getParentFrame(table), false);
      dialog.setDefaultCloseOperation(SpreadSheetDialog.DISPOSE_ON_CLOSE);
      dialog.setTitle("Statistics for column #" + (column+1) + "/" + sheet.getColumnName(column));
      dialog.setSpreadSheet(stats);
      dialog.pack();
      dialog.setLocationRelativeTo(null);
      dialog.setVisible(true);
    }

    return (stats != null);
  }
}
