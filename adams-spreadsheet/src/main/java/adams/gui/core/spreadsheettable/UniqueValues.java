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
 * UniqueValues.java
 * Copyright (C) 2017-2024 University of Waikato, Hamilton, NZ
 */

package adams.gui.core.spreadsheettable;

import adams.core.Utils;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetUtils;
import adams.gui.core.GUIHelper;
import adams.gui.core.TableRowRange;
import adams.gui.core.spreadsheettable.SpreadSheetTablePopupMenuItemHelper.TableState;
import adams.gui.dialog.TextDialog;

import java.awt.Dialog.ModalityType;

/**
 * Displays all the unique values in the column.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class UniqueValues
  extends AbstractProcessColumn {

  private static final long serialVersionUID = 3101728458818516005L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Displays all the unique values in the column.";
  }

  /**
   * Returns the name for the menu item.
   *
   * @return            the name
   */
  @Override
  public String getMenuItem() {
    return "Unique values";
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
    String[]	values;
    SpreadSheet sheet;
    TextDialog	dlg;

    sheet  = state.table.toSpreadSheet(state.range, true);
    values = SpreadSheetUtils.getColumn(sheet, state.actCol, true, true);
    if (GUIHelper.getParentDialog(state.table) != null)
      dlg = new TextDialog(GUIHelper.getParentDialog(state.table), ModalityType.MODELESS);
    else
      dlg = new TextDialog(GUIHelper.getParentFrame(state.table), false);
    dlg.setDialogTitle("Unique values of column #" + (state.actCol+1) + "/" + sheet.getColumnName(state.actCol) + " (rows: " + state.range + ")");
    dlg.setContent(Utils.flatten(values, "\n"));
    dlg.setSize(GUIHelper.getDefaultSmallDialogDimension());
    dlg.setLocationRelativeTo(dlg.getParent());
    dlg.setDefaultCloseOperation(TextDialog.DISPOSE_ON_CLOSE);
    dlg.setVisible(true);

    return true;
  }
}
