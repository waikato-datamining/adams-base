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
 * AbstractXYChartGenerator.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.spreadsheetviewer.chart;

import adams.core.QuickInfoHelper;
import adams.data.DecimalFormatString;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.flow.sink.SequencePlotter;
import adams.flow.sink.sequenceplotter.SimplePlotUpdater;
import adams.flow.transformer.SpreadSheetPlotGenerator;
import adams.flow.transformer.plotgenerator.AbstractPlotGenerator;
import adams.flow.transformer.plotgenerator.SimplePlotGenerator;
import adams.flow.transformer.plotgenerator.XYPlotGenerator;
import adams.gui.visualization.core.AbstractColorProvider;
import adams.gui.visualization.core.AxisPanelOptions;
import adams.gui.visualization.core.DefaultColorProvider;

/**
 * Ancestor for X/Y (or just Y) plot generators.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractXYChartGenerator
  extends AbstractChartGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -3167297287561137402L;

  /** the column to use as X value. */
  protected SpreadSheetColumnIndex m_XColumn;

  /** the columns to use as Y values. */
  protected SpreadSheetColumnRange m_YColumns;
  
  /** the color provider to use. */
  protected AbstractColorProvider m_ColorProvider;

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
	    "y-columns", "YColumns",
	    "");

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

    m_YColumns = new SpreadSheetColumnRange();
    m_XColumn  = new SpreadSheetColumnIndex();
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "XColumn", (m_XColumn.isEmpty() ? "-none-" : m_XColumn), "x: ");
    result += QuickInfoHelper.toString(this, "YColumn", (m_YColumns.isEmpty() ? "-none-" : m_YColumns), ", y: ");
    result += QuickInfoHelper.toString(this, "colorProvider", m_ColorProvider, ", color: ");

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
    return 
	"The index of the (optional) column which values to use on the X axis; "
	+ "if no column provided, the row index is used instead; " 
	+ m_XColumn.getExample();
  }

  /**
   * Sets the Y column range to use in the chart.
   *
   * @param value	the column range
   */
  public void setYColumns(String value) {
    m_YColumns.setRange(value);
    reset();
  }

  /**
   * Returns the current Y column range to use in the chart.
   *
   * @return		the column range
   */
  public String getYColumns() {
    return m_YColumns.getRange();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String YColumnsTipText() {
    return "The range of columns to use on the Y axis; " + m_YColumns.getExample();
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
   * Checks whether the spreadsheet can be processed.
   * <p/>
   * Default implementation only ensures that data is present.
   * 
   * @param name	the name of the tab/sheet
   * @param sheet	the spreadsheet to check
   */
  @Override
  protected void check(String name, SpreadSheet sheet) {
    if (sheet == null)
      throw new IllegalArgumentException("No spreadsheet supplied!");

    m_XColumn.setData(sheet);
    m_YColumns.setData(sheet);
    if (m_YColumns.getIntIndices().length == 0)
      throw new IllegalArgumentException("No (valid) plot columns supplied: " + m_YColumns.getRange());
  }

  /**
   * Configures the plot generator.
   * 
   * @param sheet	the sheet to use as basis
   * @return		the plot generator
   */
  protected SpreadSheetPlotGenerator configureGenerator(SpreadSheet sheet) {
    SpreadSheetPlotGenerator	result;
    AbstractPlotGenerator	generator;
    
    if (m_XColumn.getIntIndex() == -1) {
      generator = new SimplePlotGenerator();
      ((SimplePlotGenerator) generator).setPlotColumns(m_YColumns.getRange());
    }
    else {
      generator = new XYPlotGenerator();
      ((XYPlotGenerator) generator).setXColumn(m_XColumn.getIndex());
      ((XYPlotGenerator) generator).setPlotColumns(m_YColumns.getRange());
    }
    result = new SpreadSheetPlotGenerator();
    result.setGenerator(generator);
    
    return result;
  }
  
  /**
   * Returns the label for the X axis.
   * 
   * @param sheet	the sheet to get the column name from
   * @return		the label
   */
  protected String getXAxisLabel(SpreadSheet sheet) {
    return (m_XColumn.getIntIndex() == -1) ? "X" : sheet.getColumnName(m_XColumn.getIntIndex());
  }
  
  /**
   * Returns the label for the X axis.
   * 
   * @param sheet	the sheet to get the column name from
   * @return		the label
   */
  protected String getYAxisLabel(SpreadSheet sheet) {
    int[]	indices;
    
    indices = m_YColumns.getIntIndices();
    
    return (indices.length == 1) ? sheet.getColumnName(indices[0]) : "Y";
  }
  
  /**
   * Configures the sequence plotter.
   * 
   * @param sheet	the sheet to use
   * @param plotter	the plotter instance to configure
   */
  protected void configureSequencePlotter(SpreadSheet sheet, SequencePlotter plotter) {
    SimplePlotUpdater		updater;
    AxisPanelOptions		axis;
    int[]			indices;
    
    plotter.setShortTitle(true);
    plotter.setColorProvider(m_ColorProvider.shallowCopy());
    plotter.setWidth(m_Width);
    plotter.setHeight(m_Height);

    updater = new SimplePlotUpdater();
    updater.setUpdateInterval(-1);
    plotter.setPlotUpdater(updater);
    
    axis = plotter.getAxisX();
    axis.setLabel(getXAxisLabel(sheet));
    axis.setNthValueToShow(1);
    axis.setTickGenerator(columnTypeToTickGenerator(sheet, m_XColumn.getIntIndex()));
    axis.setType(columnTypeToAxisType(sheet, m_XColumn.getIntIndex()));
    axis.setCustomFormat(new DecimalFormatString("0.0"));
    plotter.setAxisX(axis);
    
    indices = m_YColumns.getIntIndices();
    axis = plotter.getAxisY();
    axis.setLabel(getYAxisLabel(sheet));
    axis.setNthValueToShow(1);
    axis.setTickGenerator(columnTypesToTickGenerator(sheet, indices));
    axis.setType(columnTypesToAxisType(sheet, indices));
    axis.setCustomFormat(new DecimalFormatString("0.0"));
    axis.setWidth(80);
    plotter.setAxisY(axis);
  }
}
