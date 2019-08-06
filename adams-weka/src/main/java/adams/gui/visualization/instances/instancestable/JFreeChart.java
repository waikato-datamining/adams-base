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
 * Copyright (C) 2015-2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.instances.instancestable;

import adams.core.ObjectCopyHelper;
import adams.core.Properties;
import adams.core.Range;
import adams.core.Shortening;
import adams.core.Utils;
import adams.core.option.AbstractOptionHandler;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.data.weka.WekaAttributeRange;
import adams.flow.control.Flow;
import adams.flow.control.StorageName;
import adams.flow.core.Actor;
import adams.flow.source.StorageValue;
import adams.gui.core.BaseFrame;
import adams.gui.core.GUIHelper;
import adams.gui.core.PropertiesParameterPanel;
import adams.gui.core.PropertiesParameterPanel.PropertyType;
import adams.gui.core.TableRowRange;
import adams.gui.dialog.PropertiesParameterDialog;
import adams.gui.goe.GenericObjectEditorPanel;
import adams.gui.visualization.instances.InstancesTable;
import adams.gui.visualization.instances.instancestable.InstancesTablePopupMenuItemHelper.TableState;
import adams.gui.visualization.jfreechart.chart.XYLineChart;
import adams.gui.visualization.jfreechart.dataset.DefaultXY;
import weka.core.Instances;

import javax.swing.SwingWorker;
import java.awt.Dialog.ModalityType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Allows to perform a simple plot of a column or row.
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
  public int compareTo(InstancesTablePopupMenuItem o) {
    return getMenuItem().compareTo(o.getMenuItem());
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
   * Prompts the user to configure the parameters.
   *
   * @param state	the table state
   * @param isColumn	whether column or row(s)
   * @return		the parameters, null if cancelled
   */
  protected Properties promptParameters(TableState state, boolean isColumn) {
    PropertiesParameterDialog dialog;
    PropertiesParameterPanel panel;
    Properties			last;

    if (GUIHelper.getParentDialog(state.table) != null)
      dialog = new PropertiesParameterDialog(GUIHelper.getParentDialog(state.table), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new PropertiesParameterDialog(GUIHelper.getParentFrame(state.table), true);
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
    last = (Properties) state.table.getLastSetup(getClass(), true, !isColumn);
    if (last != null)
      dialog.setProperties(last);
    dialog.setTitle(getMenuItem());
    dialog.pack();
    dialog.setLocationRelativeTo(state.table.getParent());
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
  protected void createPlot(final InstancesTable table, final boolean isColumn, final SpreadSheet data, final String title, final int[] spRows) {
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
   * @param state	the table state
   * @param isColumn	whether the to use column or row
   * @param index	the index of the row/column
   * @param indices 	the row indices, ignored if null
   */
  protected void plot(final TableState state, final boolean isColumn, final int index, final int[] indices) {
    Properties			last;
    final List<Double>[]	list;
    List<Double>[] 		tmp;
    int				i;
    int				n;
    final String		title;
    WekaAttributeRange		columns;
    int				numPoints;
    String			newPoints;
    int[]			cols;
    int				row;
    int[]			rows;
    final SpreadSheet		sheet;
    Row				srow;
    boolean			sorted;
    boolean			asc;
    int[]			actRows;
    int[]			spRows;
    Instances			data;

    if (isColumn)
      data = state.table.toInstances(state.range, true);
    else
      data = state.table.getInstances();
    numPoints = isColumn ? data.numInstances() : data.numAttributes();
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
    last = promptParameters(state, isColumn);
    if (last == null)
      return;

    state.table.addLastSetup(getClass(), true, !isColumn, last);

    // get data from instances
    if (indices == null) {
      tmp = new ArrayList[]{new ArrayList<>()};
    }
    else {
      tmp = new ArrayList[indices.length];
    }
    sorted = false;
    asc    = state.table.isAscending();
    if (isColumn) {
      sorted = (state.table.getSortColumn() == state.selCol);
      for (i = 0; i < data.numInstances(); i++) {
	if (data.attribute(state.actCol).isNumeric() && !data.instance(i).isMissing(state.actCol))
	  tmp[0].add(data.instance(i).value(state.actCol));
      }
    }
    else {
      if (indices == null)
        rows = new int[index];
      else
        rows = indices;
      columns = new WekaAttributeRange(last.getProperty(KEY_COLUMNS, WekaAttributeRange.ALL));
      columns.setData(data);
      cols = columns.getIntIndices();
      for (n = 0; n < rows.length; n++) {
	tmp[n] = new ArrayList<>();
	row = rows[n];
	for (i = 0; i < cols.length; i++) {
	  if (data.attribute(cols[i]).isNumeric() && !data.instance(row).isMissing(cols[i]))
	    tmp[n].add(data.instance(row).value(cols[i]));
	}
      }
    }

    if (numPoints > -1) {
      list = new ArrayList[tmp.length];
      for (i = 0; i < tmp.length; i++) {
	numPoints = Math.min(numPoints, tmp[i].size());
	Collections.shuffle(tmp[i], new Random(1));
	list[i] = tmp[i].subList(0, numPoints);
      }
    }
    else {
      list = tmp;
    }

    // sort data
    if (sorted) {
      for (i = 0; i < list.length; i++) {
	Collections.sort(list[i]);
	if (!asc)
	  Collections.reverse(list[i]);
      }
    }

    // create new spreadsheet
    sheet = new DefaultSpreadSheet();
    sheet.getHeaderRow().addCell("x").setContentAsString(isColumn ? "Column" : "Row");
    if (isColumn) {
      sheet.getHeaderRow().addCell("y0").setContentAsString(data.attribute(index).name());
    }
    else {
      if (indices == null) {
	sheet.getHeaderRow().addCell("y0").setContentAsString("Row " + (index + 1));
      }
      else {
        for (i = 0; i < indices.length; i++)
	  sheet.getHeaderRow().addCell("y" + i).setContentAsString("Row " + (indices[i] + 1));
      }
    }
    for (i = 0; i < list[0].size(); i++) {
      srow = sheet.addRow();
      srow.addCell("x").setContent((double) i+1.0);
      for (n = 0; n < list.length; n++)
	srow.addCell("y" + n).setContent(list[n].get(i));
    }

    // generate plot
    if (isColumn) {
      title   = "Column " + (index + 1) + "/" + data.attribute(index).name();
      spRows  = null;
    }
    else {
      if (indices == null) {
        title   = "Row " + (index + 1);
	spRows  = null;
      }
      else {
        actRows = Utils.adjustIndices(indices, 1);
        spRows  = new int[indices.length];
        for (i = 0; i < indices.length; i++)
	  spRows[i]  = i + 2;
	title = "Row" + (actRows.length != 1 ? "s" : "") + " " + Shortening.shortenMiddle(Utils.arrayToString(actRows), 40);
      }
    }

    createPlot(state.table, isColumn, sheet, title, spRows);
  }

  /**
   * Plots the specified column.
   *
   * @param state	the table state
   * @return		true if successful
   */
  @Override
  public boolean plotColumn(TableState state) {
    plot(state, true, state.actCol, null);
    return true;
  }

  /**
   * Plots the specified row.
   *
   * @param state	the table state
   * @return		true if successful
   */
  @Override
  public boolean plotRow(TableState state) {
    plot(state, false, state.actRow, null);
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
   * @param state	the table state
   * @return		true if successful
   */
  public boolean plotSelectedRows(TableState state) {
    plot(state, false, state.actRow, state.actRows);
    return true;
  }
}
