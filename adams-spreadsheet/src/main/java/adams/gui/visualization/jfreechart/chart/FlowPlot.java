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
 * FlowPlot.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.jfreechart.chart;

import adams.gui.visualization.core.ColorProvider;
import adams.gui.visualization.core.FluoroColorProvider;
import org.jfree.chart.JFreeChart;
import org.jfree.data.flow.FlowDataset;
import org.jfree.data.general.Dataset;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates a flow plot form a FlowDataset. Similar to a Sankey diagram.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class FlowPlot
  extends AbstractChartGenerator {

  private static final long serialVersionUID = 1441351512287564875L;

  /** the color for the node labels. */
  protected Color m_NodeLabelColor;

  /** the color provider. */
  protected ColorProvider m_ColorProvider;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a flow plot form a " + FlowDataset.class + ". Similar to a Sankey diagram.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "node-label-color", "nodeLabelColor",
      Color.WHITE);

    m_OptionManager.add(
      "color-provider", "colorProvider",
      new FluoroColorProvider());
  }

  /**
   * Sets the color for the node labels.
   *
   * @param value	the color
   */
  public void setNodeLabelColor(Color value) {
    m_NodeLabelColor = value;
    reset();
  }

  /**
   * Returns the color for the node labels.
   *
   * @return		the color
   */
  public Color getNodeLabelColor() {
    return m_NodeLabelColor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String nodeLabelColorTipText() {
    return "The color for the node labels.";
  }

  /**
   * Sets the color provider for the plot.
   *
   * @param value	the provider
   */
  public void setColorProvider(ColorProvider value) {
    m_ColorProvider = value;
    reset();
  }

  /**
   * Returns the color provider for the plot.
   *
   * @return		the provider
   */
  public ColorProvider getColorProvider() {
    return m_ColorProvider;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorProviderTipText() {
    return "The color provider for the flows in the plot.";
  }

  /**
   * Performs the actual generation of the chart.
   *
   * @param data the data to use
   * @return the chart
   */
  @Override
  protected JFreeChart doGenerate(Dataset data) {
    JFreeChart 				result;
    FlowDataset				dataset;
    org.jfree.chart.plot.flow.FlowPlot 	plot;
    int					numNodes;
    List<Color> 			colors;

    dataset  = (FlowDataset) data;

    numNodes = dataset.getAllNodes().size();
    colors   = new ArrayList<>();
    while (colors.size() < numNodes)
      colors.add(m_ColorProvider.next());

    plot = new org.jfree.chart.plot.flow.FlowPlot(dataset);
    plot.setDefaultNodeLabelPaint(m_NodeLabelColor);
    plot.setNodeColorSwatch(colors);

    result = new JFreeChart(m_Title, plot);

    return result;
  }
}
