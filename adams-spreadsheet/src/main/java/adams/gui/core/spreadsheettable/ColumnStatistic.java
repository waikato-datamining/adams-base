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
 * ColumnStatistic.java
 * Copyright (C) 2016-2022 University of Waikato, Hamilton, NZ
 */

package adams.gui.core.spreadsheettable;

import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.colstatistic.AbstractColumnStatistic;
import adams.data.spreadsheet.colstatistic.Mean;
import adams.gui.core.GUIHelper;
import adams.gui.core.TableRowRange;
import adams.gui.core.spreadsheettable.SpreadSheetTablePopupMenuItemHelper.TableState;
import adams.gui.dialog.SpreadSheetDialog;
import adams.gui.goe.GenericObjectEditorDialog;
import adams.gui.visualization.statistics.HistogramFactory;

import java.awt.Dialog.ModalityType;

/**
 * Allows the calculation of column statistics.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
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
   * Checks whether the row range can be handled.
   *
   * @param range	the range to check
   * @return		true if handled
   */
  public boolean handlesRowRange(TableRowRange range) {
    return true;
  }

  /**
   * Processes the specified column.
   *
   * @param state	the table state
   * @return		true if successful
   */
  @Override
  public boolean doProcessColumn(TableState state) {
    GenericObjectEditorDialog 	setup;
    AbstractColumnStatistic 	last;
    SpreadSheet			stats;
    SpreadSheetDialog		dialog;
    SpreadSheet			sheet;

    // let user customize plot
    if (GUIHelper.getParentDialog(state.table) != null)
      setup = new GenericObjectEditorDialog(GUIHelper.getParentDialog(state.table), ModalityType.DOCUMENT_MODAL);
    else
      setup = new GenericObjectEditorDialog(GUIHelper.getParentFrame(state.table), true);
    setup.setDefaultCloseOperation(HistogramFactory.SetupDialog.DISPOSE_ON_CLOSE);
    setup.setUISettingsPrefix(AbstractColumnStatistic.class);
    setup.getGOEEditor().setClassType(AbstractColumnStatistic.class);
    setup.getGOEEditor().setCanChangeClassInDialog(true);
    last = (AbstractColumnStatistic) state.table.getLastSetup(getClass(), true, false);
    if (last == null)
      last = new Mean();
    setup.setCurrent(last);
    setup.setLocationRelativeTo(GUIHelper.getParentComponent(state.table));
    setup.setVisible(true);
    if (setup.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
      return false;
    last = (AbstractColumnStatistic) setup.getCurrent();
    state.table.addLastSetup(getClass(), true, false, last);
    sheet = state.table.toSpreadSheet(state.range, true);
    stats = last.generate(sheet, state.actCol);
    if (stats == null) {
      if (last.hasLastError())
	GUIHelper.showErrorMessage(
	  GUIHelper.getParentComponent(state.table), "Failed to calculate statistics for column #" + (state.actCol+1) + ": " + last.getLastError());
      else
	GUIHelper.showErrorMessage(
	  GUIHelper.getParentComponent(state.table), "Failed to calculate statistics for column #" + (state.actCol+1) + "!");
    }
    else {
      if (GUIHelper.getParentDialog(state.table) != null)
	dialog = new SpreadSheetDialog(GUIHelper.getParentDialog(state.table), ModalityType.MODELESS);
      else
	dialog = new SpreadSheetDialog(GUIHelper.getParentFrame(state.table), false);
      dialog.setDefaultCloseOperation(SpreadSheetDialog.DISPOSE_ON_CLOSE);
      dialog.setTitle("Statistics for column #" + (state.actCol+1) + "/" + sheet.getColumnName(state.actCol));
      dialog.setSpreadSheet(stats);
      dialog.pack();
      dialog.setLocationRelativeTo(null);
      dialog.setVisible(true);
    }

    return (stats != null);
  }
}
