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
 * SimplePlot.java
 * Copyright (C) 2015-2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.core.spreadsheettable;

import adams.core.ObjectCopyHelper;
import adams.core.Properties;
import adams.core.Range;
import adams.core.Shortening;
import adams.core.Utils;
import adams.core.VariableName;
import adams.core.base.BaseText;
import adams.core.option.AbstractOptionHandler;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.control.Flow;
import adams.flow.control.StorageName;
import adams.flow.control.Trigger;
import adams.flow.core.Actor;
import adams.flow.sink.sequenceplotter.ViewDataClickAction;
import adams.flow.source.StorageValue;
import adams.flow.standalone.SetVariable;
import adams.flow.transformer.ArrayToSequence;
import adams.flow.transformer.CollectionToSequence;
import adams.flow.transformer.GetArrayElement;
import adams.flow.transformer.IncVariable;
import adams.flow.transformer.IncVariable.IncrementType;
import adams.flow.transformer.MakePlotContainer;
import adams.gui.core.BaseFrame;
import adams.gui.core.GUIHelper;
import adams.gui.core.PropertiesParameterPanel;
import adams.gui.core.PropertiesParameterPanel.PropertyType;
import adams.gui.core.SpreadSheetTable;
import adams.gui.core.TableRowRange;
import adams.gui.core.spreadsheettable.SpreadSheetTablePopupMenuItemHelper.TableState;
import adams.gui.dialog.PropertiesParameterDialog;
import adams.gui.goe.GenericObjectEditorPanel;

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
public class SimplePlot
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
    return "Allows to generate a simple plot from a spreadsheet row or column";
  }

  /**
   * Returns the name for the menu item.
   *
   * @return            the name
   */
  @Override
  public String getMenuItem() {
    return "Simple plot...";
  }

  /**
   * Returns the name of the icon.
   *
   * @return            the name, null if none available
   */
  public String getIconName() {
    return "plot.gif";
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
   * @param table	the table to do this for
   * @param isColumn	whether column or row(s)
   * @return		the parameters, null if cancelled
   */
  protected Properties promptParameters(SpreadSheetTable table, boolean isColumn) {
    PropertiesParameterDialog 	dialog;
    PropertiesParameterPanel 	panel;
    adams.flow.sink.SimplePlot 	defPlot;
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
    panel.setChooser(KEY_PLOT, new GenericObjectEditorPanel(Actor.class, new adams.flow.sink.SimplePlot(), false));
    if (!isColumn)
      panel.setPropertyOrder(new String[]{KEY_COLUMNS, KEY_PLOT});
    defPlot = new adams.flow.sink.SimplePlot();
    defPlot.setNoToolTips(true);
    defPlot.setMouseClickAction(new ViewDataClickAction());
    last = new Properties();
    if (!isColumn)
      last.setProperty(KEY_COLUMNS, Range.ALL);
    last.setObject(KEY_PLOT, defPlot);
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
   * Generates the plot.
   *
   * @param table	the table this is for
   * @param isColumn	whether column or row(s)
   * @param list	the data to plot
   * @param title	the title of the plot
   * @param titles	the titles array
   */
  protected void createPlot(final SpreadSheetTable table, final boolean isColumn, final List<Double>[] list, final String title, final String[] titles) {
    SwingWorker 		worker;

    worker = new SwingWorker() {
      @Override
      protected Object doInBackground() throws Exception {
	Flow flow = new Flow();
	flow.setDefaultCloseOperation(BaseFrame.DISPOSE_ON_CLOSE);

	SetVariable svInit = new SetVariable();
	svInit.setVariableName(new VariableName("index"));
	svInit.setVariableValue(new BaseText("0"));
	flow.add(svInit);

	StorageValue sv = new StorageValue();
	sv.setStorageName(new StorageName("values"));
	flow.add(sv);

	flow.add(new ArrayToSequence());

	IncVariable inc = new IncVariable();
        inc.setVariableName(new VariableName("index"));
        inc.setIncrementType(IncrementType.INTEGER);
        inc.setIntegerIncrement(1);
        flow.add(inc);

	Trigger trigTitle = new Trigger();
	trigTitle.setName("get title");
	flow.add(trigTitle);
	{
	  StorageValue svalue = new StorageValue();
	  svalue.setStorageName(new StorageName("titles"));
	  trigTitle.add(svalue);

	  GetArrayElement get = new GetArrayElement();
	  get.getOptionManager().setVariableForProperty("index", "index");
	  trigTitle.add(get);

	  adams.flow.transformer.SetVariable svTitle = new adams.flow.transformer.SetVariable();
	  svTitle.setVariableName(new VariableName("title"));
	  trigTitle.add(svTitle);
	}

	flow.add(new CollectionToSequence());

	MakePlotContainer mpc = new MakePlotContainer();
	mpc.getOptionManager().setVariableForProperty("plotName", "title");
	flow.add(mpc);

        Properties props = (Properties) table.getLastSetup(SimplePlot.this.getClass(), true, !isColumn);
        adams.flow.sink.SimplePlot lastPlot = props.getObject(KEY_PLOT, adams.flow.sink.SimplePlot.class);
	adams.flow.sink.SimplePlot plot = ObjectCopyHelper.copyObject(lastPlot);
	plot.setShortTitle(true);
	plot.setShowSidePanel(titles.length > 1);
	plot.setName(title);
        plot.setX(-2);
        plot.setY(-2);
	flow.add(plot);

	flow.setUp();
	flow.getStorage().put(new StorageName("values"), list);
	flow.getStorage().put(new StorageName("titles"), titles);
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
   * @param indices	the indices of the selected rows, ignored if null
   */
  protected void plot(final TableState state, final boolean isColumn, int index, int[] indices) {
    Properties			last;
    final List<Double>[] 	list;
    List<Double>[] 		tmp;
    int				i;
    int				n;
    final String		title;
    final String[]		titles;
    Range			columns;
    int				numPoints;
    String			newPoints;
    int[]			cols;
    int				row;
    int[]			rows;
    Cell			cell;
    boolean			sorted;
    boolean			asc;
    int[]			actRows;
    SpreadSheet			sheet;

    if (isColumn)
      sheet = state.table.toSpreadSheet(state.range, true);
    else
      sheet = state.table.toSpreadSheet();
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

    // prompt user for parameters
    last = promptParameters(state.table, isColumn);
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
    state.table.addLastSetup(getClass(), true, !isColumn, last);

    // get data from spreadsheet
    if (indices == null)
      tmp = new ArrayList[]{new ArrayList<>()};
    else
      tmp = new ArrayList[indices.length];
    sorted = false;
    asc    = state.table.isAscending();
    if (isColumn) {
      sorted = (state.table.getSortColumn() == state.selCol);
      for (i = 0; i < sheet.getRowCount(); i++) {
        cell = sheet.getCell(i, state.actCol);
	if (!cell.isMissing() && cell.isNumeric())
	  tmp[0].add(cell.toDouble());
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

    // generate plot
    if (isColumn) {
      title  = "Column " + (index + 1) + "/" + sheet.getColumnName(index);
      titles = new String[]{title};
    }
    else {
      if (indices == null) {
        title  = "Row " + (index + 2);
	titles = new String[]{title};
      }
      else {
        titles  = new String[indices.length];
        actRows = Utils.adjustIndices(indices, 2);
        for (i = 0; i < indices.length; i++)
	  titles[i]  = "Row " + actRows[i];
	title = "Row" + (actRows.length != 1 ? "s" : "") + " " + Shortening.shortenMiddle(Utils.arrayToString(actRows), 40);
      }
    }
    createPlot(state.table, isColumn, list, title, titles);
  }

  /**
   * Plots the specified column.
   *
   * @param state	the source table
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
    plot(state, false, state.actRows[0], state.actRows);
    return true;
  }
}
