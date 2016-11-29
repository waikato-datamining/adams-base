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
 * AbstractChartGeneratorWithAxisLabels.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.jfreechart.chart;

import adams.core.QuickInfoHelper;
import org.jfree.data.general.Dataset;

/**
 * Ancestor for chart generators that support axis labels.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractChartGeneratorWithAxisLabels<T extends Dataset>
  extends AbstractChartGenerator<T> {

  private static final long serialVersionUID = 5407383750282696552L;
  
  /** the label for the X axis. */
  protected String m_LabelX;
  
  /** the label for the Y axis. */
  protected String m_LabelY;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "label-x", "labelX",
      "x");

    m_OptionManager.add(
      "label-y", "labelY",
      "y");
  }

  /**
   * Sets the label for the X axis.
   *
   * @param value	the label
   */
  public void setLabelX(String value) {
    m_LabelX = value;
    reset();
  }

  /**
   * Returns the label for the X axis.
   *
   * @return		the label
   */
  public String getLabelX() {
    return m_LabelX;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelXTipText() {
    return "The label for the X axis.";
  }

  /**
   * Sets the label for the Y axis.
   *
   * @param value	the label
   */
  public void setLabelY(String value) {
    m_LabelY = value;
    reset();
  }

  /**
   * Returns the label for the Y axis.
   *
   * @return		the label
   */
  public String getLabelY() {
    return m_LabelY;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelYTipText() {
    return "The label for the Y axis.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    String	result;

    result  = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "labelX", (m_LabelX.isEmpty() ? "-none-" : m_LabelX), ", x: ");
    result += QuickInfoHelper.toString(this, "labelY", (m_LabelY.isEmpty() ? "-none-" : m_LabelY), ", y: ");

    return result;
  }
}
