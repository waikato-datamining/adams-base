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
 * XChartPlot.java
 * Copyright (C) 2025 University of Waikato, Hamilton, NZ
 */

package adams.flow.sink;

import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.core.Token;
import adams.gui.core.BasePanel;
import adams.gui.visualization.watermark.Null;
import adams.gui.visualization.watermark.Watermark;
import adams.gui.visualization.watermark.WatermarkedPanel;
import adams.gui.visualization.xchart.chart.AbstractChartGenerator;
import adams.gui.visualization.xchart.chart.XYLineChart;
import adams.gui.visualization.xchart.dataset.AbstractDatasetGenerator;
import adams.gui.visualization.xchart.dataset.ChartUtils;
import adams.gui.visualization.xchart.dataset.Dataset;
import adams.gui.visualization.xchart.dataset.Datasets;
import adams.gui.visualization.xchart.dataset.XYDatasetGenerator;
import adams.gui.visualization.xchart.marker.AbstractMarkerGenerator;
import adams.gui.visualization.xchart.marker.None;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.internal.chartpart.Chart;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.BorderLayout;

/**
 <!-- globalinfo-start -->
 * Generates and displays a plot using XChart.Dataset generation is skipped if the incoming data already represents a XChart dataset.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
 * &nbsp;&nbsp;&nbsp;adams.gui.visualization.xchart.dataset.Datasets<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: XChartPlot
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-short-title &lt;boolean&gt; (property: shortTitle)
 * &nbsp;&nbsp;&nbsp;If enabled uses just the name for the title instead of the actor's full
 * &nbsp;&nbsp;&nbsp;name.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-display-type &lt;adams.flow.core.displaytype.AbstractDisplayType&gt; (property: displayType)
 * &nbsp;&nbsp;&nbsp;Determines how to show the display, eg as standalone frame (default) or
 * &nbsp;&nbsp;&nbsp;in the Flow editor window.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.core.displaytype.Default
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 800
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 *
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 600
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 *
 * <pre>-x &lt;int&gt; (property: x)
 * &nbsp;&nbsp;&nbsp;The X position of the dialog (&gt;=0: absolute, -1: left, -2: center, -3: right
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 *
 * <pre>-y &lt;int&gt; (property: y)
 * &nbsp;&nbsp;&nbsp;The Y position of the dialog (&gt;=0: absolute, -1: top, -2: center, -3: bottom
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 *
 * <pre>-writer &lt;adams.gui.print.JComponentWriter&gt; (property: writer)
 * &nbsp;&nbsp;&nbsp;The writer to use for generating the graphics output.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.print.NullWriter
 * </pre>
 *
 * <pre>-show-flow-control-submenu &lt;boolean&gt; (property: showFlowControlSubMenu)
 * &nbsp;&nbsp;&nbsp;If enabled, adds a flow control sub-menu to the menubar.
 * &nbsp;&nbsp;&nbsp;default: false
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-dataset &lt;adams.gui.visualization.xchart.dataset.AbstractDatasetGenerator&gt; (property: dataset)
 * &nbsp;&nbsp;&nbsp;The dataset generator to use.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.xchart.dataset.XYDatasetGenerator
 * </pre>
 *
 * <pre>-chart &lt;adams.gui.visualization.xchart.chart.AbstractChartGenerator&gt; (property: chart)
 * &nbsp;&nbsp;&nbsp;The chart generator to use.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.xchart.chart.XYLineChart -color-provider adams.gui.visualization.core.DefaultColorProvider
 * </pre>
 *
 * <pre>-marker &lt;adams.gui.visualization.xchart.marker.AbstractMarkerGenerator&gt; (property: marker)
 * &nbsp;&nbsp;&nbsp;The marker generator to use.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.xchart.marker.None
 * </pre>
 *
 * <pre>-watermark &lt;adams.gui.visualization.watermark.Watermark&gt; (property: watermark)
 * &nbsp;&nbsp;&nbsp;The watermark to use for painting the data.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.watermark.Default
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class XChartPlot
  extends AbstractGraphicalDisplay
  implements DisplayPanelProvider {

  private static final long serialVersionUID = -2648121220428217287L;

  /** the dataset generator. */
  protected AbstractDatasetGenerator m_Dataset;

  /** the chart generator. */
  protected AbstractChartGenerator m_Chart;

  /** the marker generator. */
  protected AbstractMarkerGenerator m_Marker;

  /** the watermark to use. */
  protected Watermark m_Watermark;

  /** the generated chart. */
  protected XYChart m_XYChart;

  /** the panel to embed. */
  protected JPanel m_PlotPanel;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates and displays a plot using XChart."
	     + "Dataset generation is skipped if the incoming data already represents a XChart dataset.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "dataset", "dataset",
      new XYDatasetGenerator());

    m_OptionManager.add(
      "chart", "chart",
      new XYLineChart());

    m_OptionManager.add(
      "marker", "marker",
      new None());

    m_OptionManager.add(
      "watermark", "watermark",
      new adams.gui.visualization.watermark.Default());
  }

  /**
   * Sets the dataset generator.
   *
   * @param value	the generator
   */
  public void setDataset(AbstractDatasetGenerator value) {
    m_Dataset = value;
    reset();
  }

  /**
   * Returns the dataset generator.
   *
   * @return		the generator
   */
  public AbstractDatasetGenerator getDataset() {
    return m_Dataset;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String datasetTipText() {
    return "The dataset generator to use.";
  }

  /**
   * Sets the chart generator.
   *
   * @param value	the generator
   */
  public void setChart(AbstractChartGenerator value) {
    m_Chart = value;
    reset();
  }

  /**
   * Returns the chart generator.
   *
   * @return		the generator
   */
  public AbstractChartGenerator getChart() {
    return m_Chart;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String chartTipText() {
    return "The chart generator to use.";
  }

  /**
   * Sets the marker generator.
   *
   * @param value	the generator
   */
  public void setMarker(AbstractMarkerGenerator value) {
    m_Marker = value;
    reset();
  }

  /**
   * Returns the marker generator.
   *
   * @return		the generator
   */
  public AbstractMarkerGenerator getMarker() {
    return m_Marker;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String markerTipText() {
    return "The marker generator to use.";
  }

  /**
   * Sets the watermark to use.
   *
   * @param value 	the watermark
   */
  public void setWatermark(Watermark value) {
    m_Watermark = value;
    reset();
  }

  /**
   * Returns the watermark to use.
   *
   * @return 		the watermark
   */
  public Watermark getWatermark() {
    return m_Watermark;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String watermarkTipText() {
    return "The watermark to use for painting the data.";
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
    result += QuickInfoHelper.toString(this, "dataset", m_Dataset, ", dataset: ");
    result += QuickInfoHelper.toString(this, "chart", m_Chart, ", chart: ");
    result += QuickInfoHelper.toString(this, "marker", m_Marker, ", marker: ");
    result += QuickInfoHelper.toString(this, "watermark", m_Watermark, ", watermark: ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{SpreadSheet.class, Datasets.class};
  }

  /**
   * Displays the token (the panel and dialog have already been created at
   * this stage).
   *
   * @param token	the token to display
   */
  @Override
  protected void display(Token token) {
    SpreadSheet		sheet;
    Datasets<Dataset>	dataset;
    Chart 		chart;
    JPanel 		panel;

    if (token.hasPayload(SpreadSheet.class)) {
      sheet = (SpreadSheet) token.getPayload();
      dataset = m_Dataset.generate(sheet);
    }
    else {
      dataset = (Datasets<Dataset>) token.getPayload();
    }
    chart = m_Chart.generate(dataset);
    ChartUtils.setMarkers(chart, m_Marker);
    panel = new XChartPanel(chart);
    // add watermark
    if (!(m_Watermark instanceof Null))
      m_PlotPanel = new WatermarkedPanel(panel, m_Watermark);
    else
      m_PlotPanel = panel;
    m_Panel.removeAll();
    m_Panel.add(m_PlotPanel, BorderLayout.CENTER);
  }

  /**
   * Whether "clear" is supported and shows up in the menu.
   *
   * @return		true if supported
   */
  @Override
  public boolean supportsClear() {
    return true;
  }

  /**
   * Clears the content of the panel.
   */
  @Override
  public void clearPanel() {
    m_Panel.removeAll();
  }

  /**
   * Creates the panel to display in the dialog.
   *
   * @return		the panel
   */
  @Override
  protected BasePanel newPanel() {
    return new BasePanel();
  }

  /**
   * Returns the current component.
   *
   * @return		the current component, can be null
   */
  @Override
  public JComponent supplyComponent() {
    return m_PlotPanel;
  }

  /**
   * Creates a new display panel for the token.
   *
   * @param token	the token to display in a new panel, can be null
   * @return		the generated panel
   */
  @Override
  public DisplayPanel createDisplayPanel(Token token) {
    AbstractDisplayPanel	result;

    result = new AbstractComponentDisplayPanel(getClass().getSimpleName()) {
      private static final long serialVersionUID = -3785685146120118884L;
      private JPanel m_PlotPanel;
      @Override
      protected void initGUI() {
	super.initGUI();
	setLayout(new BorderLayout());
      }
      @Override
      public void display(Token token) {
	SpreadSheet sheet = (SpreadSheet) token.getPayload();
	Datasets<Dataset> dataset = m_Dataset.generate(sheet);
	Chart chart = m_Chart.generate(dataset);
	ChartUtils.setMarkers(chart, m_Marker);
	XChartPanel panel = new XChartPanel(chart);
	// add watermark
	if (!(m_Watermark instanceof Null))
	  m_PlotPanel = new WatermarkedPanel(panel, m_Watermark);
	else
	  m_PlotPanel = panel;
	removeAll();
	add(m_PlotPanel, BorderLayout.CENTER);
      }
      @Override
      public void clearPanel() {
	removeAll();
      }
      @Override
      public void cleanUp() {
	removeAll();
      }
      @Override
      public JComponent supplyComponent() {
	return m_PlotPanel;
      }
    };

    if (token != null)
      result.display(token);

    return result;
  }

  /**
   * Returns whether the created display panel requires a scroll pane or not.
   *
   * @return		true if the display panel requires a scroll pane
   */
  @Override
  public boolean displayPanelRequiresScrollPane() {
    return false;
  }
}
