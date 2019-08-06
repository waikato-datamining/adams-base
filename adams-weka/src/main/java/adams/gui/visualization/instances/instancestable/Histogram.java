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
 * Copyright (C) 2016-2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.instances.instancestable;

import adams.core.Properties;
import adams.core.option.AbstractOptionHandler;
import adams.data.statistics.AbstractArrayStatistic;
import adams.data.statistics.ArrayHistogram;
import adams.data.weka.WekaAttributeRange;
import adams.gui.core.GUIHelper;
import adams.gui.core.PropertiesParameterPanel;
import adams.gui.core.PropertiesParameterPanel.PropertyType;
import adams.gui.core.TableRowRange;
import adams.gui.dialog.PropertiesParameterDialog;
import adams.gui.goe.GenericObjectEditorPanel;
import adams.gui.visualization.instances.instancestable.InstancesTablePopupMenuItemHelper.TableState;
import adams.gui.visualization.statistics.HistogramFactory;
import gnu.trove.list.array.TDoubleArrayList;
import weka.core.Instances;

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

  public static final String KEY_ATTRIBUTES = "attributes";

  public static final String KEY_HISTOGRAM = "histogram";

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Allows to generate a histogram from either a row or a column from a dataset.";
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
      panel.addPropertyType(KEY_ATTRIBUTES, PropertyType.RANGE);
      panel.setLabel(KEY_ATTRIBUTES, "Attributes");
      panel.setHelp(KEY_ATTRIBUTES, "The attributes to use for the histogram");
    }
    panel.addPropertyType(KEY_HISTOGRAM, PropertyType.OBJECT_EDITOR);
    panel.setLabel(KEY_HISTOGRAM, "Histogram");
    panel.setHelp(KEY_HISTOGRAM, "How to generate the histogram");
    panel.setChooser(KEY_HISTOGRAM, new GenericObjectEditorPanel(AbstractArrayStatistic.class, new ArrayHistogram(), false));
    if (!isColumn)
      panel.setPropertyOrder(new String[]{KEY_ATTRIBUTES, KEY_HISTOGRAM});
    last = new Properties();
    if (!isColumn)
      last.setProperty(KEY_ATTRIBUTES, WekaAttributeRange.ALL);
    last.setObject(KEY_HISTOGRAM, new ArrayHistogram());
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
   * Allows the user to generate a plot from either a row or a column.
   *
   * @param state	the table state
   * @param isColumn	whether to use column or row
   */
  protected void plot(final TableState state, final boolean isColumn) {
    TDoubleArrayList                    list;
    HistogramFactory.Dialog		dialog;
    int					i;
    Properties				last;
    ArrayHistogram			histo;
    WekaAttributeRange			columns;
    int					col;
    int[]				cols;
    int					row;
    Instances				data;
    int					index;

    // prompt user for parameters
    last = promptParameters(state, isColumn);
    if (last == null)
      return;

    histo = last.getObject(KEY_HISTOGRAM, ArrayHistogram.class, new ArrayHistogram());
    state.table.addLastSetup(getClass(), true, !isColumn, last);

    if (isColumn)
      index = state.actCol;
    else
      index = state.actRow;

    // get data from instances
    list = new TDoubleArrayList();
    if (isColumn) {
      data = state.table.toInstances(state.range, true);
      for (i = 0; i < data.numInstances(); i++) {
	if (data.attribute(index).isNumeric() && !data.instance(i).isMissing(index))
	  list.add(data.instance(i).value(index));
      }
    }
    else {
      data    = state.table.getInstances();
      columns = new WekaAttributeRange(last.getProperty(KEY_ATTRIBUTES, WekaAttributeRange.ALL));
      columns.setData(data);
      cols = columns.getIntIndices();
      for (i = 0; i < cols.length; i++) {
	if (data.attribute(cols[i]).isNumeric() && !data.instance(index).isMissing(cols[i]))
	  list.add(data.instance(index).value(cols[i]));
      }
    }

    // calculate histogram
    histo.clear();

    // display histogram
    if (GUIHelper.getParentDialog(state.table) != null)
      dialog = HistogramFactory.getDialog(GUIHelper.getParentDialog(state.table), ModalityType.MODELESS);
    else
      dialog = HistogramFactory.getDialog(GUIHelper.getParentFrame(state.table), false);
    dialog.setDefaultCloseOperation(HistogramFactory.Dialog.DISPOSE_ON_CLOSE);
    if (isColumn)
      dialog.add(histo, list.toArray(), "Column " + (index + 1) + "/" + data.attribute(index).name());
    else
      dialog.add(histo, list.toArray(), "Row " + (index + 1));
    dialog.setLocationRelativeTo(GUIHelper.getParentComponent(state.table));
    dialog.setVisible(true);
  }

  /**
   * Plots the specified column.
   *
   * @param state	the table state
   * @return		true if successful
   */
  @Override
  public boolean plotColumn(TableState state) {
    plot(state, true);
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
    plot(state, false);
    return true;
  }
}
