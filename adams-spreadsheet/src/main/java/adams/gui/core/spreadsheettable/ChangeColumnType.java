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
 * ChangeColumnType.java
 * Copyright (C) 2015-2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.core.spreadsheettable;

import adams.data.conversion.AbstractSpreadSheetColumnConverter;
import adams.data.conversion.SpreadSheetAnyColumnToString;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.gui.core.GUIHelper;
import adams.gui.core.SpreadSheetTableModel;
import adams.gui.core.TableRowRange;
import adams.gui.core.spreadsheettable.SpreadSheetTablePopupMenuItemHelper.TableState;
import adams.gui.goe.GenericObjectEditorDialog;
import adams.gui.visualization.statistics.HistogramFactory;

import java.awt.Dialog.ModalityType;

/**
 * Allows the conversion of a column to a different type.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ChangeColumnType
  extends AbstractProcessColumn {

  private static final long serialVersionUID = 3101728458818516005L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Allows the user to change the type of a column.";
  }

  /**
   * Returns the name for the menu item.
   *
   * @return            the name
   */
  @Override
  public String getMenuItem() {
    return "Change column type...";
  }

  /**
   * Checks whether the row range can be handled.
   *
   * @param range	the range to check
   * @return		true if handled
   */
  public boolean handlesRowRange(TableRowRange range) {
    return (range == TableRowRange.ALL);
  }

  /**
   * Processes the specified column.
   *
   * @param state	the table state
   * @return		true if successful
   */
  @Override
  public boolean doProcessColumn(TableState state) {
    GenericObjectEditorDialog 		setup;
    AbstractSpreadSheetColumnConverter	last;
    String				msg;

    // let user customize plot
    if (GUIHelper.getParentDialog(state.table) != null)
      setup = new GenericObjectEditorDialog(GUIHelper.getParentDialog(state.table), ModalityType.DOCUMENT_MODAL);
    else
      setup = new GenericObjectEditorDialog(GUIHelper.getParentFrame(state.table), true);
    setup.setDefaultCloseOperation(HistogramFactory.SetupDialog.DISPOSE_ON_CLOSE);
    setup.getGOEEditor().setClassType(AbstractSpreadSheetColumnConverter.class);
    setup.getGOEEditor().setCanChangeClassInDialog(true);
    last = (AbstractSpreadSheetColumnConverter) state.table.getLastSetup(getClass(), true, false);
    if (last == null)
      last = new SpreadSheetAnyColumnToString();
    last.setNoCopy(true);
    last.setColumn(new SpreadSheetColumnIndex("" + (state.actCol + 1)));
    setup.setCurrent(last);
    setup.setLocationRelativeTo(GUIHelper.getParentComponent(state.table));
    setup.setVisible(true);
    if (setup.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
      return false;
    last = (AbstractSpreadSheetColumnConverter) setup.getCurrent();
    last.setNoCopy(true);
    last.setColumn(new SpreadSheetColumnIndex("" + (state.actCol + 1)));
    state.table.addLastSetup(getClass(), true, false, last);
    last.setInput(state.table.toSpreadSheet());
    msg = last.convert();
    if (msg != null) {
      GUIHelper.showErrorMessage(
	GUIHelper.getParentComponent(state.table), "Failed to convert column: " + msg);
    }
    else {
      state.table.setModel(new SpreadSheetTableModel((SpreadSheet) last.getOutput()));
    }
    last.cleanUp();

    return (msg == null);
  }
}
