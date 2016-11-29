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
 * AbstractChartGenerator.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.jfreechart.chart;

import adams.core.QuickInfoHelper;
import adams.core.QuickInfoSupporter;
import adams.core.option.AbstractOptionHandler;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.Dataset;

/**
 * Ancestor for chart generators.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of dataset
 */
public abstract class AbstractChartGenerator<T extends Dataset>
  extends AbstractOptionHandler
  implements QuickInfoSupporter {

  private static final long serialVersionUID = 125224185085489847L;

  /** the title of the chart. */
  protected String m_Title;

  /** whether to show the legend. */
  protected boolean m_Legend;

  /** whether to show tool tips. */
  protected boolean m_ToolTips;

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
      "tool-tips", "toolTips",
      false);
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
   * Sets whether the tool tips are displayed..
   *
   * @param value	true if displayed
   */
  public void setToolTips(boolean value) {
    m_ToolTips = value;
    reset();
  }

  /**
   * Returns whether the tool tips are displayed.
   *
   * @return		true if displayed
   */
  public boolean getToolTips() {
    return m_ToolTips;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String toolTipsTipText() {
    return "If enabled, the tool tips of the plot get displayed.";
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
  protected String check(T data) {
    if (data == null)
      return "No data provided!";
    return null;
  }

  /**
   * Performs the actual generation of the chart.
   *
   * @param data	the data to use
   * @return		the chart
   */
  protected abstract JFreeChart doGenerate(T data);

  /**
   * Generates the chart.
   *
   * @param data	the data to use
   * @return		the chart
   */
  public JFreeChart generate(T data) {
    String	msg;

    msg = check(data);
    if (msg != null)
      throw new IllegalStateException(msg);

    return doGenerate(data);
  }
}
