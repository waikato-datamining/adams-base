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
 * JFreeChart.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.core.spreadsheettable;

import adams.core.Utils;
import adams.core.option.AbstractOptionHandler;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.control.Flow;
import adams.flow.control.StorageName;
import adams.flow.core.Actor;
import adams.flow.source.StorageValue;
import adams.gui.core.BaseFrame;
import adams.gui.core.GUIHelper;
import adams.gui.core.SpreadSheetTable;
import adams.gui.goe.GenericObjectEditorDialog;

import javax.swing.SwingWorker;
import java.awt.Dialog.ModalityType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Allows to create a JFreeChart plot of a column or row.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class JFreeChart
  extends AbstractOptionHandler
  implements PlotColumn, PlotRow {

  private static final long serialVersionUID = -5624002368001818142L;

  /** the maximum of data points to plot. */
  public final static int MAX_POINTS = 1000;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Allows to generate a JFreeChart plot from a spreadsheet row or column";
  }

  /**
   * Returns the name for the menu item.
   *
   * @return            the name
   */
  @Override
  public String getMenuItem() {
    return "JFreeChart...";
  }

  /**
   * Returns the name of the icon.
   *
   * @return            the name, null if none available
   */
  public String getIconName() {
    return "jfreechart.gif";
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
    List<Double> 		list;
    List<Double> 		tmp;
    final SpreadSheet		data;
    Row				srow;
    GenericObjectEditorDialog 	setup;
    int				i;
    final String		title;
    SwingWorker 		worker;
    adams.flow.sink.JFreeChartPlot	last;
    int				numPoints;
    String			newPoints;
    int				col;
    int				row;
    Object			value;
    Cell			cell;

    numPoints = isColumn ? sheet.getRowCount() : sheet.getColumnCount();
    if (numPoints > MAX_POINTS) {
      newPoints = GUIHelper.showInputDialog(null, "More than " + MAX_POINTS + " data points to plot - enter sample size:", "" + numPoints);
      if (newPoints == null)
	return;
      if (!Utils.isInteger(newPoints))
	return;
      numPoints = Integer.parseInt(newPoints);
    }
    else {
      numPoints = -1;
    }

    // let user customize plot
    if (GUIHelper.getParentDialog(table) != null)
      setup = new GenericObjectEditorDialog(GUIHelper.getParentDialog(table), ModalityType.DOCUMENT_MODAL);
    else
      setup = new GenericObjectEditorDialog(GUIHelper.getParentFrame(table), true);
    setup.setDefaultCloseOperation(GenericObjectEditorDialog.DISPOSE_ON_CLOSE);
    setup.getGOEEditor().setClassType(Actor.class);
    setup.getGOEEditor().setCanChangeClassInDialog(false);
    last = (adams.flow.sink.JFreeChartPlot) table.getLastSetup(getClass(), true, !isColumn);
    if (last == null)
      last = new adams.flow.sink.JFreeChartPlot();
    setup.setCurrent(last);
    setup.setLocationRelativeTo(GUIHelper.getParentComponent(table));
    setup.setVisible(true);
    if (setup.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
      return;
    last = (adams.flow.sink.JFreeChartPlot) setup.getCurrent();
    table.addLastSetup(getClass(), true, !isColumn, last);

    // get data from spreadsheet
    tmp = new ArrayList<>();
    if (isColumn) {
      col = index;
      if (table.getShowRowColumn())
	col++;
      for (i = 0; i < table.getRowCount(); i++) {
	value = table.getValueAt(i, col);
	if ((value != null) && (Utils.isDouble(value.toString())))
	  tmp.add(Utils.toDouble(value.toString()));
      }
    }
    else {
      row = index;
      for (i = 0; i < sheet.getColumnCount(); i++) {
	if (sheet.getRow(row).hasCell(i)) {
	  cell = sheet.getRow(row).getCell(i);
	  if (!cell.isMissing() && cell.isNumeric())
	    tmp.add(cell.toDouble());
	}
      }
    }

    if (numPoints > -1) {
      Collections.shuffle(tmp, new Random(1));
      list = tmp.subList(0, numPoints);
    }
    else {
      list = tmp;
    }

    // create new spreadsheet
    data = new DefaultSpreadSheet();
    data.getHeaderRow().addCell("x").setContentAsString(isColumn ? "Row" : "Column");
    data.getHeaderRow().addCell("y").setContentAsString(isColumn ? sheet.getColumnName(index) : ("Row " + (index+2)));
    for (i = 0; i < list.size(); i++) {
      srow = data.addRow();
      srow.addCell("x").setContent((double) i+1.0);
      srow.addCell("y").setContent(list.get(i));
    }

    // generate plot
    if (isColumn)
      title = "Column " + (index + 1) + "/" + sheet.getColumnName(index);
    else
      title = "Row " + (index + 2);
    last.getChart().setTitle(data.getColumnName(1));

    worker = new SwingWorker() {
      @Override
      protected Object doInBackground() throws Exception {
	Flow flow = new Flow();
	flow.setDefaultCloseOperation(BaseFrame.DISPOSE_ON_CLOSE);

	StorageValue sv = new StorageValue();
	sv.setStorageName(new StorageName("values"));
	flow.add(sv);

        Object last = table.getLastSetup(JFreeChart.this.getClass(), true, !isColumn);
	adams.flow.sink.JFreeChartPlot plot = (adams.flow.sink.JFreeChartPlot) ((adams.flow.sink.JFreeChartPlot) last).shallowCopy();
	plot.setShortTitle(true);
	plot.setName(title);
        plot.setX(-2);
        plot.setY(-2);
	flow.add(plot);

	flow.setUp();
	flow.getStorage().put(new StorageName("values"), data);
	flow.execute();
	flow.wrapUp();
	return null;
      }
    };
    worker.execute();
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
