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
 * ChangeColumnType.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.gui.core.spreadsheettable;

import adams.data.conversion.AbstractSpreadSheetColumnConverter;
import adams.data.conversion.SpreadSheetAnyColumnToString;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.gui.core.GUIHelper;
import adams.gui.core.SpreadSheetTable;
import adams.gui.core.SpreadSheetTableModel;
import adams.gui.goe.GenericObjectEditorDialog;
import adams.gui.visualization.statistics.HistogramFactory;

import java.awt.Dialog.ModalityType;

/**
 * Allows the conversion of a column to a different type.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
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
   * Processes the specified column.
   *
   * @param table	the source table
   * @param sheet	the spreadsheet to use as basis
   * @param column	the column in the spreadsheet
   * @return		true if successful
   */
  @Override
  protected boolean doProcessColumn(SpreadSheetTable table, SpreadSheet sheet, int column) {
    GenericObjectEditorDialog 		setup;
    AbstractSpreadSheetColumnConverter	last;
    String				msg;

    // let user customize plot
    if (GUIHelper.getParentDialog(table) != null)
      setup = new GenericObjectEditorDialog(GUIHelper.getParentDialog(table), ModalityType.DOCUMENT_MODAL);
    else
      setup = new GenericObjectEditorDialog(GUIHelper.getParentFrame(table), true);
    setup.setDefaultCloseOperation(HistogramFactory.SetupDialog.DISPOSE_ON_CLOSE);
    setup.getGOEEditor().setClassType(AbstractSpreadSheetColumnConverter.class);
    setup.getGOEEditor().setCanChangeClassInDialog(true);
    last = (AbstractSpreadSheetColumnConverter) table.getLastSetup(getClass(), true, false);
    if (last == null)
      last = new SpreadSheetAnyColumnToString();
    last.setNoCopy(true);
    last.setColumn(new SpreadSheetColumnIndex("" + (column + 1)));
    setup.setCurrent(last);
    setup.setLocationRelativeTo(GUIHelper.getParentComponent(table));
    setup.setVisible(true);
    if (setup.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
      return false;
    last = (AbstractSpreadSheetColumnConverter) setup.getCurrent();
    last.setNoCopy(true);
    last.setColumn(new SpreadSheetColumnIndex("" + (column + 1)));
    table.addLastSetup(getClass(), true, false, last);
    last.setInput(sheet);
    msg = last.convert();
    if (msg != null) {
      GUIHelper.showErrorMessage(
	GUIHelper.getParentComponent(table), "Failed to convert column: " + msg);
    }
    else {
      sheet = (SpreadSheet) last.getOutput();
      table.setModel(new SpreadSheetTableModel(sheet));
    }
    last.cleanUp();

    return (msg == null);
  }
}
