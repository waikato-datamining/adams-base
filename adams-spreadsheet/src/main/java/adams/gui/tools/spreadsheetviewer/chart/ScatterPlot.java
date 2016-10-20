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
 * ScatterPlot.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.spreadsheetviewer.chart;

import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.control.Flow;
import adams.flow.sink.SequencePlotter;
import adams.flow.sink.sequenceplotter.ViewDataClickAction;
import adams.flow.transformer.SpreadSheetPlotGenerator;
import adams.gui.visualization.sequence.AbstractXYSequencePaintlet;
import adams.gui.visualization.sequence.CirclePaintlet;
import adams.gui.visualization.sequence.CrossPaintlet;
import adams.gui.visualization.sequence.NullPaintlet;
import adams.gui.visualization.sequence.XYSequencePaintlet;

/**
 <!-- globalinfo-start -->
 * Generates a scatter plot by plotting the X column against one or more Y columns.
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
 * <pre>-x-column &lt;java.lang.String&gt; (property: XColumn)
 * &nbsp;&nbsp;&nbsp;The index of the (optional) column which values to use on the X axis; if 
 * &nbsp;&nbsp;&nbsp;no column provided, the row index is used instead; An index is a number 
 * &nbsp;&nbsp;&nbsp;starting with 1; column names (case-sensitive) as well as the following 
 * &nbsp;&nbsp;&nbsp;placeholders can be used: first, second, third, last_2, last_1, last; numeric 
 * &nbsp;&nbsp;&nbsp;indices can be enforced by preceding them with '#' (eg '#12'); column names 
 * &nbsp;&nbsp;&nbsp;can be surrounded by double quotes.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-y-columns &lt;java.lang.String&gt; (property: YColumns)
 * &nbsp;&nbsp;&nbsp;The range of columns to use on the Y axis; A range is a comma-separated 
 * &nbsp;&nbsp;&nbsp;list of single 1-based indices or sub-ranges of indices ('start-end'); '
 * &nbsp;&nbsp;&nbsp;inv(...)' inverts the range '...'; column names (case-sensitive) as well 
 * &nbsp;&nbsp;&nbsp;as the following placeholders can be used: first, second, third, last_2, 
 * &nbsp;&nbsp;&nbsp;last_1, last; numeric indices can be enforced by preceding them with '#' 
 * &nbsp;&nbsp;&nbsp;(eg '#12'); column names can be surrounded by double quotes.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-color-provider &lt;adams.gui.visualization.core.AbstractColorProvider&gt; (property: colorProvider)
 * &nbsp;&nbsp;&nbsp;The color provider to use.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.core.DefaultColorProvider
 * </pre>
 * 
 * <pre>-plot-type &lt;CIRCLE|CROSS&gt; (property: plotType)
 * &nbsp;&nbsp;&nbsp;The plot type to use.
 * &nbsp;&nbsp;&nbsp;default: CIRCLE
 * </pre>
 * 
 * <pre>-diameter &lt;int&gt; (property: diameter)
 * &nbsp;&nbsp;&nbsp;The diameter of the circle&#47;cross in pixels (if no error data supplied).
 * &nbsp;&nbsp;&nbsp;default: 7
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-overlay-paintlet &lt;adams.gui.visualization.sequence.XYSequencePaintlet&gt; (property: overlayPaintlet)
 * &nbsp;&nbsp;&nbsp;The paintlet to use for painting the overlay data (if any).
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.sequence.NullPaintlet
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ScatterPlot
  extends AbstractXYChartGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -2088311829009151566L;

  /**
   * The type of plot to use.
   */
  public enum PlotType {
    CIRCLE,
    CROSS
  }

  /** the plot type. */
  protected PlotType m_PlotType;

  /** the diameter. */
  protected int m_Diameter;

  /** the overlay paintlet to use for painting the overlays. */
  protected XYSequencePaintlet m_OverlayPaintlet;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a scatter plot by plotting the X column against one or more Y columns.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "plot-type", "plotType",
      PlotType.CIRCLE);

    m_OptionManager.add(
      "diameter", "diameter",
      7, 1, null);

    m_OptionManager.add(
      "overlay-paintlet", "overlayPaintlet",
      new NullPaintlet());
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
    result += QuickInfoHelper.toString(this, "plotType", m_PlotType, ", type: ");
    result += QuickInfoHelper.toString(this, "diameter", m_Diameter, ", diameter: ");

    return result;
  }

  /**
   * Sets the plot type.
   *
   * @param value	the type
   */
  public void setPlotType(PlotType value) {
    m_PlotType = value;
    reset();
  }

  /**
   * Returns the plot type.
   *
   * @return		the type
   */
  public PlotType getPlotType() {
    return m_PlotType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String plotTypeTipText() {
    return "The plot type to use.";
  }

  /**
   * Sets the circle/cross diameter.
   *
   * @param value	the diameter
   */
  public void setDiameter(int value) {
    if (getOptionManager().isValid("diameter", value)) {
      m_Diameter = value;
      reset();
    }
  }

  /**
   * Returns the diameter of the circle/cross.
   *
   * @return		the diameter
   */
  public int getDiameter() {
    return m_Diameter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String diameterTipText() {
    return "The diameter of the circle/cross in pixels (if no error data supplied).";
  }

  /**
   * Sets the overlay paintlet to use.
   *
   * @param value 	the paintlet
   */
  public void setOverlayPaintlet(XYSequencePaintlet value) {
    m_OverlayPaintlet = value;
    reset();
  }

  /**
   * Returns the overlay paintlet to use.
   *
   * @return 		the paintlet
   */
  public XYSequencePaintlet getOverlayPaintlet() {
    return m_OverlayPaintlet;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String overlayPaintletTipText() {
    return "The paintlet to use for painting the overlay data (if any).";
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
    AbstractXYSequencePaintlet	paintlet;

    super.addChartGeneration(flow, name, sheet);

    pg = configureGenerator(sheet);
    flow.add(pg);

    plotter = new SequencePlotter();
    if (name != null)
      plotter.setName(name);
    else
      plotter.setName("Scatter plot");
    plotter.setTitle(plotter.getName());

    configureSequencePlotter(sheet, plotter);
    plotter.setOverlayPaintlet((XYSequencePaintlet) m_OverlayPaintlet.shallowCopy());

    switch (m_PlotType) {
      case CIRCLE:
	paintlet = new CirclePaintlet();
	((CirclePaintlet) paintlet).setDiameter(m_Diameter);
	break;
      case CROSS:
	paintlet = new CrossPaintlet();
	((CrossPaintlet) paintlet).setDiameter(m_Diameter);
	break;
      default:
	throw new IllegalStateException("Unhandled plot type: " + m_PlotType);
    }
    plotter.setPaintlet(paintlet);

    action = new ViewDataClickAction();
    action.setHitDetector(paintlet.getHitDetector());
    plotter.setMouseClickAction(action);

    flow.add(plotter);
  }
}
