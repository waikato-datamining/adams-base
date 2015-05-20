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
 * Histogram.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.spreadsheetviewer.chart;

import adams.core.QuickInfoHelper;
import adams.core.Range;
import adams.core.base.BaseString;
import adams.data.DecimalFormatString;
import adams.data.conversion.SpreadSheetToDoubleMatrix;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.data.spreadsheet.columnfinder.ColumnFinder;
import adams.data.spreadsheet.rowfinder.RowFinder;
import adams.data.statistics.ArrayHistogram;
import adams.flow.control.Flow;
import adams.flow.sink.SequencePlotter;
import adams.flow.sink.sequenceplotter.SimplePlotUpdater;
import adams.flow.sink.sequenceplotter.ViewDataClickAction;
import adams.flow.transformer.ArrayStatistic;
import adams.flow.transformer.ArrayStatistic.DataType;
import adams.flow.transformer.Convert;
import adams.flow.transformer.SpreadSheetColumnFilter;
import adams.flow.transformer.SpreadSheetPlotGenerator;
import adams.flow.transformer.SpreadSheetRowFilter;
import adams.flow.transformer.plotgenerator.RowWisePlotGenerator;
import adams.flow.transformer.plotgenerator.SimplePlotGenerator;
import adams.gui.visualization.core.AbstractColorProvider;
import adams.gui.visualization.core.AxisPanelOptions;
import adams.gui.visualization.core.DefaultColorProvider;
import adams.gui.visualization.core.axis.FancyTickGenerator;
import adams.gui.visualization.core.axis.SimpleTickGenerator;
import adams.gui.visualization.sequence.BarPaintlet;

/**
 <!-- globalinfo-start -->
 * Generates histograms by plotting the X column against the Y column.
 * <br><br>
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
 * <pre>-selection &lt;ROW|COLUMN&gt; (property: selection)
 * &nbsp;&nbsp;&nbsp;The type of data to use as basis for the histogram
 * &nbsp;&nbsp;&nbsp;default: COLUMN
 * </pre>
 * 
 * <pre>-rows &lt;java.lang.String&gt; (property: rows)
 * &nbsp;&nbsp;&nbsp;The range of rows to use for the histogram, if the selection is ROW; A range 
 * &nbsp;&nbsp;&nbsp;is a comma-separated list of single 1-based indices or sub-ranges of indices 
 * &nbsp;&nbsp;&nbsp;('start-end'); 'inv(...)' inverts the range '...'; the following placeholders 
 * &nbsp;&nbsp;&nbsp;can be used as well: first, second, third, last_2, last_1, last
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-columns &lt;java.lang.String&gt; (property: columns)
 * &nbsp;&nbsp;&nbsp;The range of columns to use for the histogram, if selection is COLUMN; A 
 * &nbsp;&nbsp;&nbsp;range is a comma-separated list of single 1-based indices or sub-ranges 
 * &nbsp;&nbsp;&nbsp;of indices ('start-end'); 'inv(...)' inverts the range '...'; column names 
 * &nbsp;&nbsp;&nbsp;(case-sensitive) as well as the following placeholders can be used: first,
 * &nbsp;&nbsp;&nbsp; second, third, last_2, last_1, last
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-histogram &lt;adams.data.statistics.ArrayHistogram&gt; (property: histogram)
 * &nbsp;&nbsp;&nbsp;The histogram setup to use.
 * &nbsp;&nbsp;&nbsp;default: adams.data.statistics.ArrayHistogram
 * </pre>
 * 
 * <pre>-color-provider &lt;adams.gui.visualization.core.AbstractColorProvider&gt; (property: colorProvider)
 * &nbsp;&nbsp;&nbsp;The color provider to use.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.core.DefaultColorProvider
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
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 10141 $
 */
public class Histogram
  extends AbstractChartGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -2088311829009151566L;

  /**
   * Determines how the data is selected for the histogram.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision: 10141 $
   */
  public enum Selection {
    ROW,
    COLUMN
  }

  /** what data to select. */
  protected Selection m_Selection;
  
  /** the range of rows to use. */
  protected Range m_Rows;

  /** the range of columns to use. */
  protected SpreadSheetColumnRange m_Columns;

  /** the histogram setup. */
  protected ArrayHistogram m_Histogram;
  
  /** the color provider to use. */
  protected AbstractColorProvider m_ColorProvider;

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
    return "Generates histograms by plotting the X column against the Y column.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "selection", "selection",
	    Selection.COLUMN);

    m_OptionManager.add(
	    "rows", "rows",
	    "");

    m_OptionManager.add(
	    "columns", "columns",
	    "");

    m_OptionManager.add(
	    "histogram", "histogram",
	    new ArrayHistogram());

    m_OptionManager.add(
	    "color-provider", "colorProvider",
	    new DefaultColorProvider());

    m_OptionManager.add(
	    "bar-width", "barWidth",
	    10, 1, null);

    m_OptionManager.add(
	    "offset", "offset",
	    3, 0, null);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Rows    = new Range();
    m_Columns = new SpreadSheetColumnRange();
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "selection", m_Selection, "selection: ");
    result += QuickInfoHelper.toString(this, "rows", (m_Rows.isEmpty() ? "-none-" : m_Rows), ", rows: ");
    result += QuickInfoHelper.toString(this, "columns", (m_Columns.isEmpty() ? "-none-" : m_Columns), ", cols: ");
    result += QuickInfoHelper.toString(this, "histogram", m_Histogram, ", histo: ");
    result += QuickInfoHelper.toString(this, "colorProvider", m_ColorProvider, ", color: ");
    result += QuickInfoHelper.toString(this, "barWidth", m_BarWidth, ", bar: ");
    result += QuickInfoHelper.toString(this, "offset", m_Offset, ", offset: ");
    
    return result;
  }

  /**
   * Sets what data to use as basis for the histogram.
   *
   * @param value	the selection
   */
  public void setSelection(Selection value) {
    m_Selection = value;
    reset();
  }

  /**
   * Returns what data to use as basis for the histogram.
   *
   * @return		the selection
   */
  public Selection getSelection() {
    return m_Selection;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String selectionTipText() {
    return "The type of data to use as basis for the histogram";
  }

  /**
   * Sets the range of rows to use, if {@link Selection#ROW}.
   *
   * @param value	the range
   */
  public void setRows(String value) {
    m_Rows.setRange(value);
    reset();
  }

  /**
   * Returns the range of rows to use, if {@link Selection#ROW}.
   *
   * @return		the range
   */
  public String getRows() {
    return m_Rows.getRange();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rowsTipText() {
    return 
	"The range of rows to use for the histogram, if the selection is " 
	+ Selection.ROW + "; " + m_Rows.getExample();
  }

  /**
   * Sets the column range to use, if {@link Selection#COLUMN}.
   *
   * @param value	the column range
   */
  public void setColumns(String value) {
    m_Columns.setRange(value);
    reset();
  }

  /**
   * Returns the current column range to use, if {@link Selection#COLUMN}.
   *
   * @return		the column range
   */
  public String getColumns() {
    return m_Columns.getRange();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnsTipText() {
    return 
	"The range of columns to use for the histogram, if selection is " 
	+ Selection.COLUMN + "; " + m_Columns.getExample();
  }

  /**
   * Sets the histogram setup to use.
   *
   * @param value	the histogram
   */
  public void setHistogram(ArrayHistogram value) {
    m_Histogram = value;
    reset();
  }

  /**
   * Returns the histogram setup to use.
   *
   * @return		the histogram
   */
  public ArrayHistogram getHistogram() {
    return m_Histogram;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String histogramTipText() {
    return "The histogram setup to use.";
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
    SimplePlotUpdater		updater;
    AxisPanelOptions		axis;
    SimplePlotGenerator		generator;
    BarPaintlet			paintlet;
    ViewDataClickAction		action;
    ArrayStatistic		stats;
    Convert			conv;
    SpreadSheetColumnFilter	cfilter;
    SpreadSheetRowFilter	rfilter;
    ColumnFinder		cfinder;
    RowFinder			rfinder;
    int				num;
    BaseString[]		locations;
    int				i;
    RowWisePlotGenerator	rowwise;
    
    switch (m_Selection) {
      case COLUMN:
	m_Columns.setData(sheet);
	num = m_Columns.getIntIndices().length;
	cfinder = new adams.data.spreadsheet.columnfinder.ByIndex();
	((adams.data.spreadsheet.columnfinder.ByIndex) cfinder).setColumns(m_Columns.getClone());
	cfilter = new SpreadSheetColumnFilter();
	cfilter.setFinder(cfinder);
	flow.add(cfilter);
	break;
      case ROW:
	m_Rows.setMax(sheet.getRowCount());
	num = m_Rows.getIntIndices().length;
	rfinder = new adams.data.spreadsheet.rowfinder.ByIndex();
	((adams.data.spreadsheet.rowfinder.ByIndex) rfinder).setRows(m_Rows.getClone());
	rfilter = new SpreadSheetRowFilter();
	rfilter.setFinder(rfinder);
	flow.add(rfilter);
	break;
      default:
	throw new IllegalStateException("Unhandled selection: " + m_Selection);
    }

    conv = new Convert();
    conv.setConversion(new SpreadSheetToDoubleMatrix());
    flow.add(conv);

    stats = new ArrayStatistic();
    stats.setStatistic(m_Histogram.shallowCopy(true));
    switch (m_Selection) {
      case COLUMN:
	stats.setDataType(DataType.COLUMN_BY_INDEX);
	break;
      case ROW:
	stats.setDataType(DataType.ROW_BY_INDEX);
	break;
      default:
	throw new IllegalStateException("Unhandled selection: " + m_Selection);
    }
    locations = new BaseString[num];
    for (i = 0; i < num; i++)
      locations[i] = new BaseString("" + (i+1));
    stats.setLocations(locations);
    flow.add(stats);
    
    rowwise = new RowWisePlotGenerator();
    rowwise.setDataColumns(Range.ALL);
    pg = new SpreadSheetPlotGenerator();
    pg.setGenerator(rowwise);
    flow.add(pg);
    
    updater = new SimplePlotUpdater();
    updater.setUpdateInterval(0);

    generator = new SimplePlotGenerator();
    generator.setPlotColumns(Range.ALL);

    paintlet = new BarPaintlet();
    paintlet.setOffset(3);
    paintlet.setWidth(m_BarWidth);

    action = new ViewDataClickAction();
    action.setHitDetector(paintlet.getHitDetector());
    
    plotter = new SequencePlotter();
    if (name != null)
      plotter.setName(name);
    else
      plotter.setName("Histogram");
    plotter.setTitle(plotter.getName());
    plotter.setShortTitle(true);
    plotter.setPaintlet(paintlet);
    plotter.setColorProvider(m_ColorProvider.shallowCopy());
    plotter.setPlotUpdater(updater);
    plotter.setWidth(m_Width);
    plotter.setHeight(m_Height);
    if (action != null)
      plotter.setMouseClickAction(action);

    axis = plotter.getAxisX();
    axis.setLabel("Bin");
    axis.setNthValueToShow(2);
    axis.setTopMargin(0.05);
    axis.setBottomMargin(0.05);
    axis.setTickGenerator(new SimpleTickGenerator());
    axis.setCustomFormat(new DecimalFormatString("0"));
    plotter.setAxisX(axis);
    
    axis = plotter.getAxisY();
    axis.setLabel("Value");
    axis.setNthValueToShow(1);
    axis.setTopMargin(0.05);
    axis.setTickGenerator(new FancyTickGenerator());
    axis.setCustomFormat(new DecimalFormatString("0.0"));
    axis.setWidth(80);
    plotter.setAxisY(axis);

    flow.add(plotter);
  }
}
