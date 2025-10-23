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
 * AbstractChartGenerator.java
 * Copyright (C) 2025 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.xchart.chart;

import adams.core.QuickInfoHelper;
import adams.core.QuickInfoSupporter;
import adams.core.option.AbstractOptionHandler;
import adams.gui.visualization.xchart.dataset.Dataset;
import adams.gui.visualization.xchart.dataset.Datasets;
import org.knowm.xchart.internal.chartpart.Chart;
import org.knowm.xchart.style.Styler.LegendPosition;

import java.util.List;

/**
 * Ancestor for chart generators.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @param <C> the type of chart
 * @param <D> the type of data
 */
public abstract class AbstractChartGenerator<C extends Chart, D extends Dataset>
  extends AbstractOptionHandler
  implements QuickInfoSupporter {

  private static final long serialVersionUID = 125224185085489847L;

  /** the title of the chart. */
  protected String m_Title;

  /** whether to show the legend. */
  protected boolean m_Legend;

  /** the position of the legend. */
  protected LegendPosition m_LegendPosition;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "title", "title",
      "Plot");

    m_OptionManager.add(
      "legend", "legend",
      false);

    m_OptionManager.add(
      "legend-position", "legendPosition",
      LegendPosition.InsideNE);
  }

  /**
   * Sets the title for the plot.
   *
   * @param value	the title
   */
  public void setTitle(String value) {
    m_Title = value;
    reset();
  }

  /**
   * Returns the title for the plot.
   *
   * @return		the title
   */
  public String getTitle() {
    return m_Title;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String titleTipText() {
    return "The title for the plot.";
  }

  /**
   * Sets whether the legend is displayed..
   *
   * @param value	true if displayed
   */
  public void setLegend(boolean value) {
    m_Legend = value;
    reset();
  }

  /**
   * Returns whether the legend is displayed.
   *
   * @return		true if displayed
   */
  public boolean getLegend() {
    return m_Legend;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String legendTipText() {
    return "If enabled, the legend of the plot is displayed.";
  }

  /**
   * Sets the position for the legend.
   *
   * @param value	the position
   */
  public void setLegendPosition(LegendPosition value) {
    m_LegendPosition = value;
    reset();
  }

  /**
   * Returns the position for the legend.
   *
   * @return		the position
   */
  public LegendPosition getLegendPosition() {
    return m_LegendPosition;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String legendPositionTipText() {
    return "Where to display the legend, if being displayed.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "title", (m_Title.isEmpty() ? "-none-" : m_Title), "title: ");
  }

  /**
   * Hook method for checks before generating the chart.
   *
   * @param data	the data to use
   * @return		null if checks passed, otherwise error message
   */
  protected String check(List<D> data) {
    if ((data == null) || data.isEmpty())
      return "No data provided!";
    return null;
  }

  /**
   * Builds the chart.
   *
   * @return		the chart
   */
  protected abstract C buildChart();

  /**
   * Applies styling to the chart.
   *
   * @param data	the data to will be added
   * @param chart	the chart to style
   */
  protected abstract void styleChart(C chart, Datasets<D> data);

  /**
   * Adds the data to the chart.
   *
   * @param chart	the chart to add the data to
   * @param data	the data to add
   */
  protected abstract void addData(C chart, Datasets<D> data);

  /**
   * Hook method for after the chart has been generated.
   * <br>
   * Default implementation does nothing.
   *
   * @param chart	the chart to add the data to
   * @param data	the data to add
   */
  protected void postGenerate(C chart, Datasets<D> data) {
  }

  /**
   * Performs the actual generation of the chart.
   *
   * @param data	the data to use
   * @return		the chart
   */
  protected C doGenerate(Datasets<D> data) {
    C 	result;

    result = buildChart();
    styleChart(result, data);
    addData(result, data);
    postGenerate(result, data);

    return result;
  }

  /**
   * Generates the chart.
   *
   * @param data	the data to use
   * @return		the chart
   */
  public C generate(Datasets<D> data) {
    String	msg;

    msg = check(data);
    if (msg != null)
      throw new IllegalStateException(msg);

    return doGenerate(data);
  }
}
