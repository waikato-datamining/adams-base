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
 * JFreeChart.java
 * Copyright (C) 2016-2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.core.spreadsheettable;

import adams.core.ObjectCopyHelper;
import adams.core.Properties;
import adams.core.Range;
import adams.core.Shortening;
import adams.core.Utils;
import adams.core.option.AbstractOptionHandler;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.flow.control.Flow;
import adams.flow.control.StorageName;
import adams.flow.core.Actor;
import adams.flow.source.StorageValue;
import adams.gui.core.BaseFrame;
import adams.gui.core.GUIHelper;
import adams.gui.core.PropertiesParameterPanel;
import adams.gui.core.PropertiesParameterPanel.PropertyType;
import adams.gui.core.SpreadSheetTable;
import adams.gui.dialog.PropertiesParameterDialog;
import adams.gui.goe.GenericObjectEditorPanel;
import adams.gui.visualization.jfreechart.chart.XYLineChart;
import adams.gui.visualization.jfreechart.dataset.DefaultXY;

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
 */
public class JFreeChart
  extends AbstractOptionHandler
  implements PlotColumn, PlotRow, PlotSelectedRows {

  private static final long serialVersionUID = -5624002368001818142L;

  public static final String KEY_COLUMNS = "columns";

  public static final String KEY_PLOT = "plot";

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
      panel.setHelp(KEY_COLUMNS, "The columns to use for the plot");
    }
    panel.addPropertyType(KEY_PLOT, PropertyType.OBJECT_EDITOR);
    panel.setLabel(KEY_PLOT, "Plot");
    panel.setHelp(KEY_PLOT, "How to display the data");
    panel.setChooser(KEY_PLOT, new GenericObjectEditorPanel(Actor.class, new adams.flow.sink.JFreeChartPlot(), false));
    if (!isColumn)
      panel.setPropertyOrder(new String[]{KEY_COLUMNS, KEY_PLOT});
    last = new Properties();
    if (!isColumn)
      last.setProperty(KEY_COLUMNS, Range.ALL);
    last.setObject(KEY_PLOT, new adams.flow.sink.JFreeChartPlot());
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
   * Plots the data.
   *
   * @param table	the table this is for
   * @param isColumn	whether this is for a column or for row(s)
   * @param data	the data to plot
   * @param title	the title of the plot
   * @param spRows	the spreadsheet rows, can be null
   */
  protected void createPlot(final SpreadSheetTable table, final boolean isColumn, final SpreadSheet data, final String title, final int[] spRows) {
    SwingWorker		worker;

    worker = new SwingWorker() {
      @Override
      protected Object doInBackground() throws Exception {
	Flow flow = new Flow();
	flow.setDefaultCloseOperation(BaseFrame.DISPOSE_ON_CLOSE);

	StorageValue sv = new StorageValue();
	sv.setStorageName(new StorageName("values"));
	flow.add(sv);

	Properties last = (Properties) table.getLastSetup(JFreeChart.this.getClass(), true, !isColumn);
	adams.flow.sink.JFreeChartPlot plot = ObjectCopyHelper.copyObject(last.getObject(KEY_PLOT, adams.flow.sink.JFreeChartPlot.class, new adams.flow.sink.JFreeChartPlot()));
	if (spRows != null) {
	  DefaultXY dataset = new DefaultXY();
	  dataset.setX(new SpreadSheetColumnIndex("1"));
	  dataset.setY(new SpreadSheetColumnRange(Utils.arrayToString(spRows)));
	  plot.setDataset(dataset);
	  XYLineChart chart = new XYLineChart();
	  chart.setLegend(true);
	  chart.setTitle(title);
	  plot.setChart(chart);
	}
	else {
	  XYLineChart chart = new XYLineChart();
	  chart.setLegend(false);
	  chart.setTitle(title);
	  plot.setChart(chart);
	}
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
   * Allows the user to generate a plot from either a row or a column.
   *
   * @param sheet	the spreadsheet to use
   * @param isColumn	whether the to use column or row
   * @param index	the index of the row/column
   * @param indices 	the row indices, ignored if null
   */
  protected void plot(final SpreadSheetTable table, final SpreadSheet sheet, final boolean isColumn, int index, int[] indices) {
    List<Double>[] 		list;
    List<Double>[] 		tmp;
    final SpreadSheet		data;
    Row				srow;
    int				i;
    int				n;
    final String		title;
    Range			columns;
    Properties			last;
    int				numPoints;
    String			newPoints;
    int				col;
    int[]			cols;
    int				row;
    int[]			rows;
    Object			value;
    Cell			cell;
    boolean			sorted;
    boolean			asc;
    int[]			actRows;
    int[]			spRows;

    numPoints = isColumn ? sheet.getRowCount() : sheet.getColumnCount();
    if (numPoints > MAX_POINTS) {
      newPoints = GUIHelper.showInputDialog(null, "More than " + MAX_POINTS + " data points to plot - enter sample size:", "" + numPoints);
      if (newPoints == null)
	return;
      if (!Utils.isInteger(newPoints))
	return;
      if (Integer.parseInt(newPoints) != numPoints)
        numPoints = Integer.parseInt(newPoints);
      else
        numPoints = -1;
    }
    else {
      numPoints = -1;
    }

    // prompt user
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
    table.addLastSetup(getClass(), true, !isColumn, last);

    // get data from spreadsheet
    if (indices == null) {
      tmp = new ArrayList[]{new ArrayList<>()};
    }
    else {
      tmp = new ArrayList[indices.length];
    }
    sorted = false;
    asc    = table.isAscending();
    if (isColumn) {
      col = index;
      if (table.getShowRowColumn())
	col++;
      sorted = (table.getSortColumn() == col);
      for (i = 0; i < table.getRowCount(); i++) {
	value = table.getValueAt(i, col);
	if ((value != null) && (Utils.isDouble(value.toString())))
	  tmp[0].add(Utils.toDouble(value.toString()));
      }
    }
    else {
      if (indices == null)
        rows = new int[index];
      else
        rows = indices;
      for (n = 0; n < rows.length; n++) {
        tmp[n] = new ArrayList<>();
	row    = rows[n];
	for (i = 0; i < cols.length; i++) {
	  if (sheet.getRow(row).hasCell(cols[i])) {
	    cell = sheet.getRow(row).getCell(cols[i]);
	    if (!cell.isMissing() && cell.isNumeric())
	      tmp[n].add(cell.toDouble());
	  }
	}
      }
    }

    if (numPoints > -1) {
      list = new ArrayList[tmp.length];
      for (i = 0; i < tmp.length; i++) {
	numPoints = Math.min(numPoints, tmp[i].size());
	Collections.shuffle(tmp[i], new Random(1));
	list[i] = tmp[i].subList(0, numPoints);
	if (sorted) {
	  Collections.sort(list[i]);
	  if (!asc)
	    Collections.reverse(list[i]);
	}
      }
    }
    else {
      list = tmp;
    }

    // create new spreadsheet
    data = new DefaultSpreadSheet();
    data.getHeaderRow().addCell("x").setContentAsString(isColumn ? "Row" : "Column");
    if (isColumn) {
      data.getHeaderRow().addCell("y0").setContentAsString(sheet.getColumnName(index));
    }
    else {
      if (indices == null) {
	data.getHeaderRow().addCell("y0").setContentAsString("Row " + (index + 2));
      }
      else {
        for (i = 0; i < indices.length; i++)
	  data.getHeaderRow().addCell("y" + i).setContentAsString("Row " + (indices[i] + 2));
      }
    }
    for (i = 0; i < list[0].size(); i++) {
      srow = data.addRow();
      srow.addCell("x").setContent((double) i+1.0);
      for (n = 0; n < list.length; n++)
	srow.addCell("y" + n).setContent(list[n].get(i));
    }

    // generate plot
    if (isColumn) {
      title   = "Column " + (index + 1) + "/" + sheet.getColumnName(index);
      spRows  = null;
    }
    else {
      if (indices == null) {
        title   = "Row " + (index + 2);
	spRows  = null;
      }
      else {
        actRows = Utils.adjustIndices(indices, 2);
        spRows  = new int[indices.length];
        for (i = 0; i < indices.length; i++)
	  spRows[i]  = i + 2;
	title = "Row" + (actRows.length != 1 ? "s" : "") + " " + Shortening.shortenMiddle(Utils.arrayToString(actRows), 40);
      }
    }

    createPlot(table, isColumn, data, title, spRows);
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
    plot(table, sheet, true, column, null);
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
    plot(table, sheet, false, actRow, null);
    return true;
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
   * Plots the specified row.
   *
   * @param table	the source table
   * @param sheet	the spreadsheet to use as basis
   * @param actRows	the actual rows in the spreadsheet
   * @param selRows	the selected rows in the table
   * @return		true if successful
   */
  public boolean plotSelectedRows(SpreadSheetTable table, SpreadSheet sheet, int[] actRows, int[] selRows) {
    plot(table, sheet, false, actRows[0], actRows);
    return true;
  }
}
