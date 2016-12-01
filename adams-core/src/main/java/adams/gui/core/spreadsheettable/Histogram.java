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
 * Histogram.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.gui.core.spreadsheettable;

import adams.core.Utils;
import adams.core.option.AbstractOptionHandler;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.statistics.ArrayHistogram;
import adams.gui.core.GUIHelper;
import adams.gui.core.SpreadSheetTable;
import adams.gui.goe.GenericObjectEditorDialog;
import adams.gui.visualization.statistics.HistogramFactory;
import gnu.trove.list.array.TDoubleArrayList;

import java.awt.Dialog.ModalityType;

/**
 * Allows to generate a histogram from a column or row.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Histogram
  extends AbstractOptionHandler
  implements PlotColumn, PlotRow {

  private static final long serialVersionUID = -2452746814708360637L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Allows to plot either a row or a column from a spreadsheet";
  }

  /**
   * Returns the name for the menu item.
   *
   * @return            the name
   */
  @Override
  public String getMenuItem() {
    return "Histogram...";
  }

  /**
   * Returns the name of the icon.
   *
   * @return            the name, null if none available
   */
  public String getIconName() {
    return "histogram.png";
  }

  /**
   * For sorting the menu items.
   *
   * @param o       the other item
   * @return        -1 if less than, 0 if equal, +1 if larger than this
   *                menu item name
   */
  @Override
  public int compareTo(SpreadSheetTablePopupMenuItem o) {
    return getMenuItem().compareTo(o.getMenuItem());
  }

  /**
   * Allows the user to generate a plot from either a row or a column.
   *
   * @param sheet	the spreadsheet to use
   * @param isColumn	whether the to use column or row
   * @param index	the index of the row/column
   */
  protected void plot(final SpreadSheetTable table, final SpreadSheet sheet, final boolean isColumn, int index) {
    TDoubleArrayList                    list;
    HistogramFactory.SetupDialog	setup;
    HistogramFactory.Dialog		dialog;
    int					i;
    ArrayHistogram                      last;
    int					col;
    int					row;
    Object				value;
    Cell 				cell;

    // let user customize histogram
    if (GUIHelper.getParentDialog(table) != null)
      setup = HistogramFactory.getSetupDialog(GUIHelper.getParentDialog(table), ModalityType.DOCUMENT_MODAL);
    else
      setup = HistogramFactory.getSetupDialog(GUIHelper.getParentFrame(table), true);
    setup.setDefaultCloseOperation(HistogramFactory.SetupDialog.DISPOSE_ON_CLOSE);
    setup.setTitle("Histogram setup");
    last = (ArrayHistogram) table.getLastSetup(getClass(), true, !isColumn);
    if (last == null)
      last = new ArrayHistogram();
    setup.setCurrent(last);
    setup.setLocationRelativeTo(GUIHelper.getParentComponent(table));
    setup.setVisible(true);
    if (setup.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
      return;
    last = (ArrayHistogram) setup.getCurrent();
    table.addLastSetup(getClass(), true, !isColumn, last);

    // get data from spreadsheet
    list = new TDoubleArrayList();
    if (isColumn) {
      col = index;
      if (table.getShowRowColumn())
	col++;
      for (i = 0; i < table.getRowCount(); i++) {
	value = table.getValueAt(i, col);
	if ((value != null) && (Utils.isDouble(value.toString())))
	  list.add(Utils.toDouble(value.toString()));
      }
    }
    else {
      row = index;
      for (i = 0; i < sheet.getColumnCount(); i++) {
	if (sheet.getRow(row).hasCell(i)) {
	  cell = sheet.getRow(row).getCell(i);
	  if (!cell.isMissing() && cell.isNumeric())
	    list.add(cell.toDouble());
	}
      }
    }

    // calculate histogram
    last.clear();

    // display histogram
    if (GUIHelper.getParentDialog(table) != null)
      dialog = HistogramFactory.getDialog(GUIHelper.getParentDialog(table), ModalityType.MODELESS);
    else
      dialog = HistogramFactory.getDialog(GUIHelper.getParentFrame(table), false);
    dialog.setDefaultCloseOperation(HistogramFactory.Dialog.DISPOSE_ON_CLOSE);
    if (isColumn)
      dialog.add(last, list.toArray(), "Column " + (index + 1) + "/" + sheet.getColumnName(index));
    else
      dialog.add(last, list.toArray(), "Row " + (index + 2));
    dialog.setLocationRelativeTo(GUIHelper.getParentComponent(table));
    dialog.setVisible(true);
  }

  /**
   * Plots the specified column.
   *
   * @param table	the source table
   * @param sheet	the spreadsheet to use as basis
   * @param column	the column in the spreadsheet
   * @return		true if successful
   */
  @Override
  public boolean plotColumn(SpreadSheetTable table, SpreadSheet sheet, int column) {
    plot(table, sheet, true, column);
    return true;
  }

  /**
   * Plots the specified row.
   *
   * @param table	the source table
   * @param sheet	the spreadsheet to use as basis
   * @param actRow	the actual row in the spreadsheet
   * @param selRow	the selected row in the table
   * @return		true if successful
   */
  @Override
  public boolean plotRow(SpreadSheetTable table, SpreadSheet sheet, int actRow, int selRow) {
    plot(table, sheet, false, actRow);
    return true;
  }
}
