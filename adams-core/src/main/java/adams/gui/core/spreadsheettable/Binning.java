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
 * Binning.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.core.spreadsheettable;

import adams.core.Properties;
import adams.core.Range;
import adams.core.Utils;
import adams.core.option.AbstractOptionHandler;
import adams.data.binning.Bin;
import adams.data.binning.Binnable;
import adams.data.binning.algorithm.BinningAlgorithm;
import adams.data.binning.algorithm.ManualBinning;
import adams.data.binning.operation.Wrapping;
import adams.data.sequence.XYSequencePoint;
import adams.data.sequence.XYSequencePointComparator.Comparison;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.sink.sequenceplotter.SequencePlotPoint;
import adams.flow.sink.sequenceplotter.SequencePlotSequence;
import adams.flow.sink.sequenceplotter.SequencePlotterPanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.PropertiesParameterPanel;
import adams.gui.core.PropertiesParameterPanel.PropertyType;
import adams.gui.core.SpreadSheetTable;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.dialog.PropertiesParameterDialog;
import adams.gui.goe.GenericObjectEditorPanel;
import adams.gui.visualization.core.TranslucentColorProvider;
import adams.gui.visualization.core.axis.FancyTickGenerator;
import adams.gui.visualization.core.axis.SimpleFixedLabelTickGenerator;
import adams.gui.visualization.core.plot.Axis;
import adams.gui.visualization.sequence.BarPaintlet;
import adams.gui.visualization.sequence.XYSequenceContainer;
import adams.gui.visualization.sequence.XYSequenceContainerManager;
import gnu.trove.list.array.TDoubleArrayList;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.util.List;

/**
 * Allows to perform binning of the values from a column or row.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Binning
  extends AbstractOptionHandler
  implements PlotColumn, PlotRow {

  private static final long serialVersionUID = -2452746814708360637L;

  public static final String KEY_COLUMNS = "columns";

  public static final String KEY_BINNING = "binning";

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Allows to perform binning of the values from a column or row.";
  }

  /**
   * Returns the name for the menu item.
   *
   * @return            the name
   */
  @Override
  public String getMenuItem() {
    return "Binning...";
  }

  /**
   * Returns the name of the icon.
   *
   * @return            the name, null if none available
   */
  public String getIconName() {
    return null;
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
    panel.addPropertyType(KEY_BINNING, PropertyType.OBJECT_EDITOR);
    panel.setLabel(KEY_BINNING, "Algorithm");
    panel.setHelp(KEY_BINNING, "The binning algorithm to apply");
    panel.setChooser(KEY_BINNING, new GenericObjectEditorPanel(BinningAlgorithm.class, new ManualBinning(), true));
    if (!isColumn)
      panel.setPropertyOrder(new String[]{KEY_COLUMNS, KEY_BINNING});
    last = new Properties();
    if (!isColumn)
      last.setProperty(KEY_COLUMNS, Range.ALL);
    last.setObject(KEY_BINNING, new ManualBinning());
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
   * @param isColumn	whether column or row
   * @param list	the data to use
   * @param title	the title for the plot
   */
  protected void createPlot(SpreadSheetTable table, boolean isColumn, TDoubleArrayList list, String title) {
    Properties 				last;
    BinningAlgorithm 			binning;
    List<Binnable<Integer>> 		binnable;
    List<Bin<Integer>> 			bins;
    ApprovalDialog			dialog;
    SequencePlotterPanel 		plotPanel;
    BarPaintlet 			paintlet;
    XYSequenceContainerManager 		manager;
    XYSequenceContainer 		seqcont;
    SequencePlotSequence 		seq;
    XYSequencePoint 			point;
    double				x;
    int					i;

    // generate bins
    last    = (Properties) table.getLastSetup(getClass(), true, !isColumn);
    binning = last.getObject(KEY_BINNING, BinningAlgorithm.class, new ManualBinning());
    try {
      binnable = Wrapping.wrap(list.toArray());
    }
    catch (Exception e) {
      GUIHelper.showErrorMessage(
        table.getParent(),
	"Failed to wrap " + (isColumn ? "column" : "row") + " values!", e);
      return;
    }
    bins = binning.generateBins(binnable);

    // display bins
    if (GUIHelper.getParentDialog(table) != null)
      dialog = new ApprovalDialog(GUIHelper.getParentDialog(table), ModalityType.MODELESS);
    else
      dialog = new ApprovalDialog(GUIHelper.getParentFrame(table), false);
    dialog.setTitle(title);

    paintlet = new BarPaintlet();
    paintlet.setWidth(10);
    plotPanel = new SequencePlotterPanel("Binning");
    plotPanel.setColorProvider(new TranslucentColorProvider());
    plotPanel.setDataPaintlet(paintlet);
    plotPanel.getPlot().clearToolTipAxes();
    plotPanel.getPlot().getAxis(Axis.BOTTOM).setTickGenerator(new SimpleFixedLabelTickGenerator());
    plotPanel.getPlot().getAxis(Axis.BOTTOM).setNumberFormat("");
    plotPanel.getPlot().getAxis(Axis.BOTTOM).setNthValueToShow(2);
    plotPanel.getPlot().getAxis(Axis.LEFT).setTickGenerator(new FancyTickGenerator());
    plotPanel.getPlot().getAxis(Axis.LEFT).setNumberFormat("0.0");

    manager = plotPanel.getContainerManager();
    manager.startUpdate();
    seq = new SequencePlotSequence();
    seq.setID(title);
    seq.setComparison(Comparison.X);
    for (i = 0; i < bins.size(); i++) {
      x     = seq.putMappingX(bins.get(i).getInterval().getValue());
      point = new SequencePlotPoint("" + seq.size(), x, bins.get(i).size());
      seq.add(point);
    }
    seqcont = manager.newContainer(seq);
    manager.add(seqcont);
    manager.finishUpdate();

    dialog.getContentPane().add(plotPanel, BorderLayout.CENTER);
    dialog.setSize(GUIHelper.getDefaultDialogDimension());
    dialog.setLocationRelativeTo(GUIHelper.getParentComponent(table));
    dialog.setVisible(true);
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
    int					i;
    Properties				last;
    Range				columns;
    int					col;
    int[]				cols;
    int					row;
    Object				value;
    Cell 				cell;
    String				title;

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

    // generate plot
    if (isColumn)
      title = "Column " + (index + 1) + "/" + sheet.getColumnName(index);
    else
      title = "Row " + (index + 2);
    createPlot(table, isColumn, list, title);
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
