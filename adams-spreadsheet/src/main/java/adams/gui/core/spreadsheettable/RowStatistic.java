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
 * RowStatistic.java
 * Copyright (C) 2016-2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.core.spreadsheettable;

import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.rowstatistic.AbstractRowStatistic;
import adams.data.spreadsheet.rowstatistic.Mean;
import adams.gui.core.GUIHelper;
import adams.gui.core.spreadsheettable.SpreadSheetTablePopupMenuItemHelper.TableState;
import adams.gui.dialog.SpreadSheetDialog;
import adams.gui.goe.GenericObjectEditorDialog;
import adams.gui.visualization.statistics.HistogramFactory;

import java.awt.Dialog.ModalityType;

/**
 * Allows the calculation of row statistics.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RowStatistic
  extends AbstractProcessRow {

  private static final long serialVersionUID = 3101728458818516005L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Allows the user to calculate statistics for a row.";
  }

  /**
   * Returns the name for the menu item.
   *
   * @return            the name
   */
  @Override
  public String getMenuItem() {
    return "Row statistics...";
  }

  /**
   * Processes the specified row.
   *
   * @param state	the table state
   * @return		true if successful
   */
  @Override
  protected boolean doProcessRow(TableState state) {
    GenericObjectEditorDialog 	setup;
    AbstractRowStatistic last;
    SpreadSheet			stats;
    SpreadSheetDialog		dialog;

    // let user customize plot
    if (GUIHelper.getParentDialog(state.table) != null)
      setup = new GenericObjectEditorDialog(GUIHelper.getParentDialog(state.table), ModalityType.DOCUMENT_MODAL);
    else
      setup = new GenericObjectEditorDialog(GUIHelper.getParentFrame(state.table), true);
    setup.setDefaultCloseOperation(HistogramFactory.SetupDialog.DISPOSE_ON_CLOSE);
    setup.getGOEEditor().setClassType(AbstractRowStatistic.class);
    setup.getGOEEditor().setCanChangeClassInDialog(true);
    last = (AbstractRowStatistic) state.table.getLastSetup(getClass(), true, false);
    if (last == null)
      last = new Mean();
    setup.setCurrent(last);
    setup.setLocationRelativeTo(GUIHelper.getParentComponent(state.table));
    setup.setVisible(true);
    if (setup.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
      return false;
    last = (AbstractRowStatistic) setup.getCurrent();
    state.table.addLastSetup(getClass(), true, false, last);
    stats = last.generate(state.table.toSpreadSheet(), state.actRow);
    if (stats == null) {
      if (last.hasLastError())
	GUIHelper.showErrorMessage(
	  GUIHelper.getParentComponent(state.table), "Failed to calculate statistics for row #" + (state.actRow + 1) + ": " + last.getLastError());
      else
	GUIHelper.showErrorMessage(
	  GUIHelper.getParentComponent(state.table), "Failed to calculate statistics for row #" + (state.actRow + 2) + "!");
    }
    else {
      if (GUIHelper.getParentDialog(state.table) != null)
	dialog = new SpreadSheetDialog(GUIHelper.getParentDialog(state.table), ModalityType.MODELESS);
      else
	dialog = new SpreadSheetDialog(GUIHelper.getParentFrame(state.table), false);
      dialog.setDefaultCloseOperation(SpreadSheetDialog.DISPOSE_ON_CLOSE);
      dialog.setTitle("Statistics for row #" + (state.actRow + 2));
      dialog.setSpreadSheet(stats);
      dialog.pack();
      dialog.setLocationRelativeTo(null);
      dialog.setVisible(true);
    }

    return (stats != null);
  }
}
