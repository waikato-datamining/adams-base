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

package adams.gui.visualization.instances.instancestable;

import adams.core.Utils;
import adams.core.VariableName;
import adams.core.base.BaseText;
import adams.core.option.AbstractOptionHandler;
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
import adams.gui.goe.GenericObjectEditorDialog;
import adams.gui.visualization.instances.InstancesTable;
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
public class SimplePlot
  extends AbstractOptionHandler
  implements PlotColumn, PlotRow, PlotSelectedRows {

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
    return "Allows to generate a simple plot from a instances row or column";
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
  public int compareTo(InstancesTablePopupMenuItem o) {
    return getMenuItem().compareTo(o.getMenuItem());
  }

  /**
   * Allows the user to generate a plot from either a row or a column.
   *
   * @param data	the instances to use
   * @param isColumn	whether the to use column or row
   * @param index	the index of the row/column
   * @param indices 	the indices of the rows, ignored if null
   */
  protected void plot(final InstancesTable table, final Instances data, final boolean isColumn, int index, int[] indices) {
    final List<Double>[] 	list;
    List<Double>[] 		tmp;
    GenericObjectEditorDialog 	setup;
    int				i;
    int				n;
    final String		title;
    final String[]		titles;
    SwingWorker 		worker;
    adams.flow.sink.SimplePlot	last;
    int				numPoints;
    String			newPoints;
    int				col;
    int				row;
    int[]			rows;
    Object			value;
    boolean			sorted;
    boolean			asc;
    int[]			actRows;

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

    // let user customize plot
    if (GUIHelper.getParentDialog(table) != null)
      setup = new GenericObjectEditorDialog(GUIHelper.getParentDialog(table), ModalityType.DOCUMENT_MODAL);
    else
      setup = new GenericObjectEditorDialog(GUIHelper.getParentFrame(table), true);
    setup.setDefaultCloseOperation(GenericObjectEditorDialog.DISPOSE_ON_CLOSE);
    setup.getGOEEditor().setClassType(Actor.class);
    setup.getGOEEditor().setCanChangeClassInDialog(false);
    last = (adams.flow.sink.SimplePlot) table.getLastSetup(getClass(), true, !isColumn);
    if (last == null) {
      last = new adams.flow.sink.SimplePlot();
      last.setNoToolTips(true);
      last.setMouseClickAction(new ViewDataClickAction());
    }
    setup.setCurrent(last);
    setup.setLocationRelativeTo(GUIHelper.getParentComponent(table));
    setup.setVisible(true);
    if (setup.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
      return;
    last = (adams.flow.sink.SimplePlot) setup.getCurrent();
    table.addLastSetup(getClass(), true, !isColumn, last);

    // get data from instances
    if (indices == null) {
      tmp = new ArrayList[]{new ArrayList<>()};
    }
    else {
      tmp = new ArrayList[indices.length];
    }
    sorted = false;
    asc    = table.isAscending();
    if (isColumn) {
      col    = index + 1;
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
	row = rows[n];
	for (i = 0; i < data.numAttributes(); i++) {
	  if (data.attribute(i).isNumeric() && !data.instance(row).isMissing(i))
	    tmp[n].add(data.instance(row).value(i));
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

    // generate plot
    if (isColumn) {
      title  = "Column " + (index + 1) + "/" + data.attribute(index).name();
      titles = new String[]{title};
    }
    else {
      if (indices == null) {
        title  = "Row " + (index + 2);
	titles = new String[]{title};
      }
      else {
        titles  = new String[indices.length];
        actRows = new int[indices.length];
        for (i = 0; i < indices.length; i++) {
	  titles[i]  = "Row " + (indices[i] + 2);
	  actRows[i] = indices[i] + 2;
	}
	title = "Row" + (actRows.length != 1 ? "s" : "") + " " + Utils.arrayToString(actRows);
      }
    }

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

        Object last = table.getLastSetup(SimplePlot.this.getClass(), true, !isColumn);
	adams.flow.sink.SimplePlot plot = (adams.flow.sink.SimplePlot) ((adams.flow.sink.SimplePlot) last).shallowCopy();
	plot.setShortTitle(true);
	plot.setShowSidePanel((indices != null) && (indices.length > 1));
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
   * Plots the specified column.
   *
   * @param table	the source table
   * @param data	the instances to use as basis
   * @param column	the column in the instances
   * @return		true if successful
   */
  @Override
  public boolean plotColumn(InstancesTable table, Instances data, int column) {
    plot(table, data, true, column, null);
    return true;
  }

  /**
   * Plots the specified row.
   *
   * @param table	the source table
   * @param data	the instances to use as basis
   * @param actRow	the actual row in the instances
   * @param selRow 	the selected row in the table
   * @return		true if successful
   */
  @Override
  public boolean plotRow(InstancesTable table, Instances data, int actRow, int selRow) {
    plot(table, data, false, actRow, null);
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
   * @param data	the instances to use as basis
   * @param actRows	the actual rows in the Instances
   * @param selRows	the selected rows in the table
   * @return		true if successful
   */
  public boolean plotSelectedRows(InstancesTable table, Instances data, int[] actRows, int[] selRows) {
    plot(table, data, false, actRows[0], actRows);
    return true;
  }
}
