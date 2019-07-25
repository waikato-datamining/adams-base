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
 * Histogram.java
 * Copyright (C) 2015-2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.core.spreadsheettable;

import adams.core.Properties;
import adams.core.Range;
import adams.core.Utils;
import adams.core.option.AbstractOptionHandler;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.statistics.AbstractArrayStatistic;
import adams.data.statistics.ArrayHistogram;
import adams.gui.core.GUIHelper;
import adams.gui.core.PropertiesParameterPanel;
import adams.gui.core.PropertiesParameterPanel.PropertyType;
import adams.gui.core.SpreadSheetTable;
import adams.gui.dialog.PropertiesParameterDialog;
import adams.gui.goe.GenericObjectEditorPanel;
import adams.gui.visualization.statistics.HistogramFactory;
import gnu.trove.list.array.TDoubleArrayList;

import java.awt.Dialog.ModalityType;

/**
 * Allows to generate a histogram from a column or row.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Histogram
  extends AbstractOptionHandler
  implements PlotColumn, PlotRow {

  private static final long serialVersionUID = -2452746814708360637L;

  public static final String KEY_COLUMNS = "columns";

  public static final String KEY_HISTOGRAM = "histogram";

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Allows to generate a histogram from either a row or a column from a spreadsheet.";
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
   * Prompts the user to configure the parameters.
   *
   * @param table	the table to do this for
   * @param isColumn	whether column or row(s)
   * @return		the parameters, null if cancelled
   */
  protected Properties promptParameters(SpreadSheetTable table, boolean isColumn) {
    PropertiesParameterDialog 	dialog;
    PropertiesParameterPanel 	panel;
    Properties			last;

    if (GUIHelper.getParentDialog(table) != null)
      dialog = new PropertiesParameterDialog(GUIHelper.getParentDialog(table), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new PropertiesParameterDialog(GUIHelper.getParentFrame(table), true);
    panel = dialog.getPropertiesParameterPanel();
    if (!isColumn) {
      panel.addPropertyType(KEY_COLUMNS, PropertyType.RANGE);
      panel.setLabel(KEY_COLUMNS, "Columns");
      panel.setHelp(KEY_COLUMNS, "The columns to use for the histogram");
    }
    panel.addPropertyType(KEY_HISTOGRAM, PropertyType.OBJECT_EDITOR);
    panel.setLabel(KEY_HISTOGRAM, "Histogram");
    panel.setHelp(KEY_HISTOGRAM, "How to generate the histogram");
    panel.setChooser(KEY_HISTOGRAM, new GenericObjectEditorPanel(AbstractArrayStatistic.class, new ArrayHistogram(), false));
    if (!isColumn)
      panel.setPropertyOrder(new String[]{KEY_COLUMNS, KEY_HISTOGRAM});
    last = new Properties();
    if (!isColumn)
      last.setProperty(KEY_COLUMNS, Range.ALL);
    last.setObject(KEY_HISTOGRAM, new ArrayHistogram());
    dialog.setProperties(last);
    last = (Properties) table.getLastSetup(getClass(), true, !isColumn);
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
   * Allows the user to generate a plot from either a row or a column.
   *
   * @param sheet	the spreadsheet to use
   * @param isColumn	whether the to use column or row
   * @param index	the index of the row/column
   */
  protected void plot(final SpreadSheetTable table, final SpreadSheet sheet, final boolean isColumn, int index) {
    TDoubleArrayList                    list;
    HistogramFactory.Dialog		dialog;
    int					i;
    Properties				last;
    ArrayHistogram			histo;
    Range				columns;
    int					col;
    int[]				cols;
    int					row;
    Object				value;
    Cell 				cell;

    // prompt user for parameters
    last = promptParameters(table, isColumn);
    if (last == null)
      return;

    if (!isColumn) {
      columns = new Range(last.getProperty(KEY_COLUMNS, Range.ALL));
      columns.setMax(sheet.getColumnCount());
      cols = columns.getIntIndices();
    }
    else {
      cols = null;
    }
    histo = last.getObject(KEY_HISTOGRAM, ArrayHistogram.class, new ArrayHistogram());
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
      for (i = 0; i < cols.length; i++) {
	if (sheet.getRow(row).hasCell(cols[i])) {
	  cell = sheet.getRow(row).getCell(cols[i]);
	  if (!cell.isMissing() && cell.isNumeric())
	    list.add(cell.toDouble());
	}
      }
    }

    // calculate histogram
    histo.clear();

    // display histogram
    if (GUIHelper.getParentDialog(table) != null)
      dialog = HistogramFactory.getDialog(GUIHelper.getParentDialog(table), ModalityType.MODELESS);
    else
      dialog = HistogramFactory.getDialog(GUIHelper.getParentFrame(table), false);
    dialog.setDefaultCloseOperation(HistogramFactory.Dialog.DISPOSE_ON_CLOSE);
    if (isColumn)
      dialog.add(histo, list.toArray(), "Column " + (index + 1) + "/" + sheet.getColumnName(index));
    else
      dialog.add(histo, list.toArray(), "Row " + (index + 2));
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
