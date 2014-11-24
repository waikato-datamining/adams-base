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
 * BarPlot.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.spreadsheetviewer.chart;

import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.control.Flow;
import adams.flow.sink.SequencePlotter;
import adams.flow.sink.sequenceplotter.ViewDataClickAction;
import adams.flow.transformer.SpreadSheetPlotGenerator;
import adams.gui.visualization.sequence.AbstractXYSequencePaintlet;
import adams.gui.visualization.sequence.BarPaintlet;

/**
 <!-- globalinfo-start -->
 * Generates a bar plot by plotting the X column against the Y column.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the chart dialog.
 * &nbsp;&nbsp;&nbsp;default: 800
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the chart dialog.
 * &nbsp;&nbsp;&nbsp;default: 600
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-row-finder &lt;adams.data.spreadsheet.rowfinder.RowFinder&gt; (property: rowFinder)
 * &nbsp;&nbsp;&nbsp;The row finder to use for restricting the rows used for the chart.
 * &nbsp;&nbsp;&nbsp;default: adams.data.spreadsheet.rowfinder.ByIndex
 * </pre>
 * 
 * <pre>-x-column &lt;java.lang.String&gt; (property: XColumn)
 * &nbsp;&nbsp;&nbsp;The index of the column which values to use on the X axis; An index is a 
 * &nbsp;&nbsp;&nbsp;number starting with 1; apart from column names (case-sensitive), the following 
 * &nbsp;&nbsp;&nbsp;placeholders can be used as well: first, second, third, last_2, last_1, 
 * &nbsp;&nbsp;&nbsp;last
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-y-columns &lt;java.lang.String&gt; (property: YColumns)
 * &nbsp;&nbsp;&nbsp;The range of columns to use on the Y axis; A range is a comma-separated 
 * &nbsp;&nbsp;&nbsp;list of single 1-based indices or sub-ranges of indices ('start-end'); '
 * &nbsp;&nbsp;&nbsp;inv(...)' inverts the range '...'; apart from column names (case-sensitive
 * &nbsp;&nbsp;&nbsp;), the following placeholders can be used as well: first, second, third, 
 * &nbsp;&nbsp;&nbsp;last_2, last_1, last
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-bar-width &lt;int&gt; (property: barWidth)
 * &nbsp;&nbsp;&nbsp;The width of the bar in pixel.
 * &nbsp;&nbsp;&nbsp;default: 10
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-offset &lt;int&gt; (property: offset)
 * &nbsp;&nbsp;&nbsp;The offset in pixel for multiple plots.
 * &nbsp;&nbsp;&nbsp;default: 3
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-color-provider &lt;adams.gui.visualization.core.AbstractColorProvider&gt; (property: colorProvider)
 * &nbsp;&nbsp;&nbsp;The color provider to use.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.core.DefaultColorProvider
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BarPlot
  extends AbstractXYChartGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -2088311829009151566L;

  /** the width of the bar. */
  protected int m_BarWidth;

  /** the offset in case of multiple plots. */
  protected int m_Offset;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a bar plot by plotting the X column against the Y column.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "bar-width", "barWidth",
	    10, 1, null);

    m_OptionManager.add(
	    "offset", "offset",
	    3, 0, null);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "barWidth", m_BarWidth, ", bar: ");
    result += QuickInfoHelper.toString(this, "offset", m_Offset, ", offset: ");
    
    return result;
  }

  /**
   * Sets the width of the bar.
   *
   * @param value	width in pixel
   */
  public void setBarWidth(int value) {
    m_BarWidth = value;
    reset();
  }

  /**
   * Returns the width of the bar.
   *
   * @return		width in pixel
   */
  public int getBarWidth() {
    return m_BarWidth;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String barWidthTipText() {
    return "The width of the bar in pixel.";
  }

  /**
   * Sets the offset for multiple plots.
   *
   * @param value	offset in pixel
   */
  public void setOffset(int value) {
    m_Offset = value;
    reset();
  }

  /**
   * Returns the offset for multiple plots.
   *
   * @return		offset in pixel
   */
  public int getOffset() {
    return m_Offset;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String offsetTipText() {
    return "The offset in pixel for multiple plots.";
  }

  /**
   * Adds the chart generation to the flow. The flow already contains 
   * forwarding of spreadsheet and selecting subset of rows.
   * 
   * @param flow	the flow to extend
   * @param name	the name of the tab/sheet
   * @param sheet	the spreadsheet to generate the flow for
   */
  @Override
  protected void addChartGeneration(Flow flow, String name, SpreadSheet sheet) {
    SpreadSheetPlotGenerator	pg;
    SequencePlotter		plotter;
    ViewDataClickAction		action;
    BarPaintlet			paintlet;

    super.addChartGeneration(flow, name, sheet);
    
    pg = configureGenerator(sheet);
    flow.add(pg);
    
    plotter = new SequencePlotter();
    if (name != null)
      plotter.setName(name);
    else
      plotter.setName("Bar plot");
    plotter.setTitle(plotter.getName());

    configureSequencePlotter(sheet, plotter);

    paintlet = new BarPaintlet();
    paintlet.setOffset(3);
    paintlet.setWidth(m_BarWidth);
    plotter.setPaintlet(paintlet);

    action = new ViewDataClickAction();
    action.setHitDetector(((AbstractXYSequencePaintlet) paintlet).getHitDetector());
    plotter.setMouseClickAction(action);

    flow.add(plotter);
  }
}
