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
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.spreadsheetviewer.chart;

import adams.data.DecimalFormatString;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.flow.control.Flow;
import adams.flow.sink.SequencePlotter;
import adams.flow.sink.sequenceplotter.SimplePlotUpdater;
import adams.flow.transformer.SpreadSheetPlotGenerator;
import adams.flow.transformer.plotgenerator.XYPlotGenerator;
import adams.gui.visualization.core.AbstractColorProvider;
import adams.gui.visualization.core.AxisPanelOptions;
import adams.gui.visualization.core.DefaultColorProvider;
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
 * <pre>-y-column &lt;java.lang.String&gt; (property: YColumn)
 * &nbsp;&nbsp;&nbsp;The index of the column to use on the Y axis; An index is a number starting 
 * &nbsp;&nbsp;&nbsp;with 1; apart from column names (case-sensitive), the following placeholders 
 * &nbsp;&nbsp;&nbsp;can be used as well: first, second, third, last_2, last_1, last
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-bar-width &lt;int&gt; (property: barWidth)
 * &nbsp;&nbsp;&nbsp;The width of the bar in pixel.
 * &nbsp;&nbsp;&nbsp;default: 10
 * &nbsp;&nbsp;&nbsp;minimum: 1
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
  extends AbstractChartGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -2088311829009151566L;

  /** the column to use as X value. */
  protected SpreadSheetColumnIndex m_XColumn;

  /** the columns to use as Y value. */
  protected SpreadSheetColumnIndex m_YColumn;

  /** the width of the bar. */
  protected int m_BarWidth;
  
  /** the color provider to use. */
  protected AbstractColorProvider m_ColorProvider;

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
	    "x-column", "XColumn",
	    "");

    m_OptionManager.add(
	    "y-column", "YColumn",
	    "");

    m_OptionManager.add(
	    "bar-width", "barWidth",
	    10, 1, null);

    m_OptionManager.add(
	    "color-provider", "colorProvider",
	    new DefaultColorProvider());
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_YColumn = new SpreadSheetColumnIndex();
    m_XColumn = new SpreadSheetColumnIndex();
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	variable;

    result = "x: ";
    variable = getOptionManager().getVariableForProperty("XColumn");
    if (variable != null)
      result += variable;
    else
      result += m_XColumn.getIndex();
    
    result   += ", y: ";
    variable = getOptionManager().getVariableForProperty("YColumn");
    if (variable != null)
      result += variable;
    else
      result += m_YColumn.getIndex();
    
    result   += ", bar: ";
    variable = getOptionManager().getVariableForProperty("barWidth");
    if (variable != null)
      result += variable;
    else
      result += m_BarWidth;
    
    result   += ", color: ";
    variable = getOptionManager().getVariableForProperty("colorProvider");
    if (variable != null)
      result += variable;
    else
      result += m_ColorProvider.getClass().getSimpleName();
    
    return result;
  }

  /**
   * Sets the index of the column which values to use as X values.
   *
   * @param value	the column index
   */
  public void setXColumn(String value) {
    m_XColumn.setIndex(value);
    reset();
  }

  /**
   * Returns the index of the column which values to use as X values.
   *
   * @return		the column index
   */
  public String getXColumn() {
    return m_XColumn.getIndex();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String XColumnTipText() {
    return "The index of the column which values to use on the X axis; " + m_XColumn.getExample();
  }

  /**
   * Sets the Y column index to use in the chart.
   *
   * @param value	the column index
   */
  public void setYColumn(String value) {
    m_YColumn.setIndex(value);
    reset();
  }

  /**
   * Returns the current Y column range to use in the chart.
   *
   * @return		the column range
   */
  public String getYColumn() {
    return m_YColumn.getIndex();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String YColumnTipText() {
    return "The index of the column to use on the Y axis; " + m_YColumn.getExample();
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
   * Sets the color provider to use.
   *
   * @param value 	the color provider
   */
  public void setColorProvider(AbstractColorProvider value) {
    m_ColorProvider = value;
    reset();
  }

  /**
   * Returns the color provider in use.
   *
   * @return 		the color provider
   */
  public AbstractColorProvider getColorProvider() {
    return m_ColorProvider;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorProviderTipText() {
    return "The color provider to use.";
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
    XYPlotGenerator		generator;
    SpreadSheetPlotGenerator	pg;
    SequencePlotter		plotter;
    SimplePlotUpdater		updater;
    AxisPanelOptions		axis;
    BarPaintlet	paintlet;

    m_XColumn.setData(sheet);
    m_YColumn.setData(sheet);
    
    generator = new XYPlotGenerator();
    generator.setXColumn(m_XColumn.getIndex());
    generator.setPlotColumns(m_YColumn.getIndex());
    pg = new SpreadSheetPlotGenerator();
    pg.setGenerator(generator);
    flow.add(pg);
    
    updater = new SimplePlotUpdater();
    updater.setUpdateInterval(0);
    plotter = new SequencePlotter();
    if (name != null)
      plotter.setName(name);
    else
      plotter.setName("Bar plot");
    plotter.setTitle("Bar plot");
    plotter.setShortTitle(true);
    paintlet = new BarPaintlet();
    paintlet.setWidth(m_BarWidth);
    plotter.setPaintlet(paintlet);
    plotter.setColorProvider(m_ColorProvider.shallowCopy());
    plotter.setPlotUpdater(updater);
    plotter.setWidth(m_Width);
    plotter.setHeight(m_Height);

    axis = plotter.getAxisX();
    axis.setLabel("X");
    axis.setNthValueToShow(1);
    axis.setTickGenerator(columnTypeToTickGenerator(sheet, m_XColumn.getIntIndex()));
    axis.setType(columnTypeToAxisType(sheet, m_XColumn.getIntIndex()));
    axis.setCustomFormat(new DecimalFormatString("0.0"));
    plotter.setAxisX(axis);
    
    axis = plotter.getAxisY();
    axis.setLabel("Y");
    axis.setNthValueToShow(1);
    axis.setTickGenerator(columnTypeToTickGenerator(sheet, m_YColumn.getIntIndex()));
    axis.setType(columnTypeToAxisType(sheet, m_YColumn.getIntIndex()));
    axis.setCustomFormat(new DecimalFormatString("0.0"));
    plotter.setAxisX(axis);

    flow.add(plotter);
  }
}
