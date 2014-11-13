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
 * GenericPlot.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.spreadsheetviewer.chart;

import adams.data.DecimalFormatString;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.control.Flow;
import adams.flow.sink.SequencePlotter;
import adams.flow.sink.sequenceplotter.SimplePlotUpdater;
import adams.flow.sink.sequenceplotter.ViewDataClickAction;
import adams.flow.transformer.SpreadSheetPlotGenerator;
import adams.flow.transformer.plotgenerator.AbstractPlotGenerator;
import adams.flow.transformer.plotgenerator.XYPlotGenerator;
import adams.gui.visualization.core.AbstractColorProvider;
import adams.gui.visualization.core.AxisPanelOptions;
import adams.gui.visualization.core.DefaultColorProvider;
import adams.gui.visualization.core.axis.SimpleTickGenerator;
import adams.gui.visualization.core.axis.TickGenerator;
import adams.gui.visualization.sequence.AbstractXYSequencePaintlet;
import adams.gui.visualization.sequence.LinePaintlet;
import adams.gui.visualization.sequence.XYSequencePaintlet;

/**
 <!-- globalinfo-start -->
 * Flexible chart generator.
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
 * <pre>-generator &lt;adams.flow.transformer.plotgenerator.AbstractPlotGenerator&gt; (property: generator)
 * &nbsp;&nbsp;&nbsp;The generator for producing the plot containers.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.transformer.plotgenerator.XYPlotGenerator
 * </pre>
 * 
 * <pre>-paintlet &lt;adams.gui.visualization.sequence.XYSequencePaintlet&gt; (property: paintlet)
 * &nbsp;&nbsp;&nbsp;The paintlet to use for the data.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.sequence.LinePaintlet
 * </pre>
 * 
 * <pre>-color-provider &lt;adams.gui.visualization.core.AbstractColorProvider&gt; (property: colorProvider)
 * &nbsp;&nbsp;&nbsp;The color provider to use.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.core.DefaultColorProvider
 * </pre>
 * 
 * <pre>-x-tick-generator &lt;adams.gui.visualization.core.axis.TickGenerator&gt; (property: XTickGenerator)
 * &nbsp;&nbsp;&nbsp;The tick generator to use for the X axis.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.core.axis.SimpleTickGenerator
 * </pre>
 * 
 * <pre>-y-tick-generator &lt;adams.gui.visualization.core.axis.TickGenerator&gt; (property: YTickGenerator)
 * &nbsp;&nbsp;&nbsp;The tick generator to use for the Y axis.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.core.axis.SimpleTickGenerator
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9429 $
 */
public class GenericPlot
  extends AbstractChartGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -2088311829009151566L;

  /** the plot generator to use. */
  protected AbstractPlotGenerator m_Generator;

  /** the paintlet to use for painting the XY data. */
  protected XYSequencePaintlet m_Paintlet;
  
  /** the color provider to use. */
  protected AbstractColorProvider m_ColorProvider;
  
  /** the tick generator for the X axis. */
  protected TickGenerator m_XTickGenerator;
  
  /** the tick generator for the Y axis. */
  protected TickGenerator m_YTickGenerator;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Flexible chart generator.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "generator", "generator",
	    new XYPlotGenerator());

    m_OptionManager.add(
	    "paintlet", "paintlet",
	    new LinePaintlet());

    m_OptionManager.add(
	    "color-provider", "colorProvider",
	    new DefaultColorProvider());

    m_OptionManager.add(
	    "x-tick-generator", "XTickGenerator",
	    new SimpleTickGenerator());

    m_OptionManager.add(
	    "y-tick-generator", "YTickGenerator",
	    new SimpleTickGenerator());
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

    result = "generator: ";
    variable = getOptionManager().getVariableForProperty("generator");
    if (variable != null)
      result += variable;
    else
      result += m_Generator.getClass().getSimpleName();
    
    result   += ", paintlet: ";
    variable = getOptionManager().getVariableForProperty("paintlet");
    if (variable != null)
      result += variable;
    else
      result += m_Paintlet.getClass().getSimpleName();
    
    result   += ", color: ";
    variable = getOptionManager().getVariableForProperty("colorProvider");
    if (variable != null)
      result += variable;
    else
      result += m_ColorProvider.getClass().getSimpleName();
    
    result   += ", x-ticks: ";
    variable = getOptionManager().getVariableForProperty("XTickGenerator");
    if (variable != null)
      result += variable;
    else
      result += m_XTickGenerator.getClass().getSimpleName();
    
    result   += ", y-ticks: ";
    variable = getOptionManager().getVariableForProperty("YTickGenerator");
    if (variable != null)
      result += variable;
    else
      result += m_YTickGenerator.getClass().getSimpleName();
    
    return result;
  }

  /**
   * Sets the generator for producing the plot containers.
   *
   * @param value	the generator
   */
  public void setGenerator(AbstractPlotGenerator value) {
    m_Generator = value;
    reset();
  }

  /**
   * Returns the generator for producing the plot containers.
   *
   * @return		the generator
   */
  public AbstractPlotGenerator getGenerator() {
    return m_Generator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String generatorTipText() {
    return "The generator for producing the plot containers.";
  }

  /**
   * Sets the paintlet to use.
   *
   * @param value	the paintlet
   */
  public void setPaintlet(XYSequencePaintlet value) {
    m_Paintlet = value;
    reset();
  }

  /**
   * Returns the paintlet to use.
   *
   * @return		the paintlet
   */
  public XYSequencePaintlet getPaintlet() {
    return m_Paintlet;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String paintletTipText() {
    return "The paintlet to use for the data.";
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
   * Sets the tick generator for the X axis.
   *
   * @param value 	the tick generator
   */
  public void setXTickGenerator(TickGenerator value) {
    m_XTickGenerator = value;
    reset();
  }

  /**
   * Returns the tick generator for the X axis.
   *
   * @return 		the tick generator
   */
  public TickGenerator getXTickGenerator() {
    return m_XTickGenerator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String XTickGeneratorTipText() {
    return "The tick generator to use for the X axis.";
  }

  /**
   * Sets the tick generator for the X axis.
   *
   * @param value 	the tick generator
   */
  public void setYTickGenerator(TickGenerator value) {
    m_YTickGenerator = value;
    reset();
  }

  /**
   * Returns the tick generator for the Y axis.
   *
   * @return 		the tick generator
   */
  public TickGenerator getYTickGenerator() {
    return m_YTickGenerator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String YTickGeneratorTipText() {
    return "The tick generator to use for the Y axis.";
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
    ViewDataClickAction		action;
    
    pg = new SpreadSheetPlotGenerator();
    pg.setGenerator(m_Generator);
    flow.add(pg);
    
    updater = new SimplePlotUpdater();
    updater.setUpdateInterval(0);
    action = null;
    if (m_Paintlet instanceof AbstractXYSequencePaintlet) {
      action = new ViewDataClickAction();
      action.setHitDetector(((AbstractXYSequencePaintlet) m_Paintlet).getHitDetector());
    }
    plotter = new SequencePlotter();
    if (name != null)
      plotter.setName(name);
    else
      plotter.setName("Plot");
    plotter.setTitle("Plot");
    plotter.setShortTitle(true);
    plotter.setPaintlet(m_Paintlet);
    plotter.setColorProvider(m_ColorProvider.shallowCopy());
    plotter.setPlotUpdater(updater);
    plotter.setWidth(m_Width);
    plotter.setHeight(m_Height);
    if (action != null)
      plotter.setMouseClickAction(action);

    axis = plotter.getAxisX();
    axis.setLabel("X");
    axis.setNthValueToShow(1);
    axis.setTickGenerator(m_XTickGenerator);
    axis.setCustomFormat(new DecimalFormatString("0.0"));
    plotter.setAxisX(axis);
    
    axis = plotter.getAxisY();
    axis.setLabel("Y");
    axis.setNthValueToShow(1);
    axis.setTickGenerator(m_YTickGenerator);
    axis.setCustomFormat(new DecimalFormatString("0.0"));
    axis.setWidth(80);
    plotter.setAxisY(axis);

    flow.add(plotter);
  }
}
