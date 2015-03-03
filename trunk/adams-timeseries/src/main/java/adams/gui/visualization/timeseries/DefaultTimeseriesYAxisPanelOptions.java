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
 * DefaultTimeseriesYAxisPanelOptions.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.timeseries;

import adams.gui.core.GUIHelper;
import adams.gui.visualization.core.PlotPanel;
import adams.gui.visualization.core.plot.Axis;

/**
 <!-- globalinfo-start -->
 * Encapsulates options for the Y axis in a timeseries plot.<br/>
 * It is possible to fix the range of the Y axis.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-label &lt;java.lang.String&gt; (property: label)
 * &nbsp;&nbsp;&nbsp;The label of the axis.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-type &lt;ABSOLUTE|PERCENTAGE|LOG10_ABSOLUTE|LOG10_PERCENTAGE|LOG_ABSOLUTE|LOG_PERCENTAGE|DATE|TIME|DATETIME&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The type of the axis.
 * &nbsp;&nbsp;&nbsp;default: ABSOLUTE
 * </pre>
 * 
 * <pre>-hide-grid-lines &lt;boolean&gt; (property: showGridLines)
 * &nbsp;&nbsp;&nbsp;If enabled, grid lines are plotted as well.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 * <pre>-tick-generator &lt;adams.gui.visualization.core.axis.TickGenerator&gt; (property: tickGenerator)
 * &nbsp;&nbsp;&nbsp;Algorithm for generating the tick positions.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.core.axis.FancyTickGenerator
 * </pre>
 * 
 * <pre>-nth-value &lt;int&gt; (property: nthValueToShow)
 * &nbsp;&nbsp;&nbsp;The count of ticks a value is shown, i.e., '3' means every third tick: 1,
 * &nbsp;&nbsp;&nbsp; 4, 7, ...
 * &nbsp;&nbsp;&nbsp;default: 5
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-length-ticks &lt;int&gt; (property: lengthTicks)
 * &nbsp;&nbsp;&nbsp;The length in pixels of the ticks to display.
 * &nbsp;&nbsp;&nbsp;default: 4
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width&#47;height of the axis (&gt;= 5).
 * &nbsp;&nbsp;&nbsp;default: 20
 * &nbsp;&nbsp;&nbsp;minimum: 5
 * </pre>
 * 
 * <pre>-top-margin &lt;double&gt; (property: topMargin)
 * &nbsp;&nbsp;&nbsp;The factor for an extra margin on the top&#47;left (eg 0.05 = 5%).
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 * 
 * <pre>-bottom-margin &lt;double&gt; (property: bottomMargin)
 * &nbsp;&nbsp;&nbsp;The factor for an extra margin on the bottom&#47;right (eg 0.05 = 5%).
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 * 
 * <pre>-custom-format &lt;adams.data.DecimalFormatString&gt; (property: customFormat)
 * &nbsp;&nbsp;&nbsp;The custom format for displaying the tick labels on the axis.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-fixed &lt;boolean&gt; (property: fixed)
 * &nbsp;&nbsp;&nbsp;If enabled, fixed minimum&#47;maximum are used for the axis.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-minimum &lt;double&gt; (property: minimum)
 * &nbsp;&nbsp;&nbsp;The minimum for the axis, if fixed.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * </pre>
 * 
 * <pre>-maximum &lt;double&gt; (property: maximum)
 * &nbsp;&nbsp;&nbsp;The maximum for the axis, if fixed.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DefaultTimeseriesYAxisPanelOptions
  extends AbstractTimeseriesYAxisPanelOptions {
  
  /** for serialization. */
  private static final long serialVersionUID = -1774995113138870653L;

  /** whether to fix axis. */
  protected boolean m_Fixed;

  /** the minimum for the axis. */
  protected double m_Minimum;

  /** the maximum for the axis. */
  protected double m_Maximum;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Encapsulates options for the Y axis in a timeseries plot.\n"
	+ "It is possible to fix the range of the Y axis.";
  }
  
  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "fixed", "fixed",
	    false);

    m_OptionManager.add(
	    "minimum", "minimum",
	    0.0);

    m_OptionManager.add(
	    "maximum", "maximum",
	    1.0);
  }

  /**
   * Sets whether to fix the axis.
   *
   * @param value 	if true then the axis gets fixed
   */
  public void setFixed(boolean value) {
    m_Fixed = value;
    reset();
  }

  /**
   * Returns whether the axis is fixed.
   *
   * @return 		true if the axis is fixed
   */
  public boolean getFixed() {
    return m_Fixed;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fixedTipText() {
    return "If enabled, fixed minimum/maximum are used for the axis.";
  }

  /**
   * Sets the minimum for the axis (if fixed).
   *
   * @param value 	the minimum
   */
  public void setMinimum(double value) {
    m_Minimum = value;
    reset();
  }

  /**
   * Returns the minimum for the axis (if fixed).
   *
   * @return 		the minimum
   */
  public double getMinimum() {
    return m_Minimum;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String minimumTipText() {
    return "The minimum for the axis, if fixed.";
  }

  /**
   * Sets the maximum for the axis (if fixed).
   *
   * @param value 	the maximum
   */
  public void setMaximum(double value) {
    m_Maximum = value;
    reset();
  }

  /**
   * Returns the maximum for the axis (if fixed).
   *
   * @return 		the maximum
   */
  public double getMaximum() {
    return m_Maximum;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maximumTipText() {
    return "The maximum for the axis, if fixed.";
  }

  /**
   * Applies the options to the specified axis.
   *
   * @param plot	the plot panel to update an axis for
   * @param axis	the axis to configure
   */
  @Override
  public void configure(PlotPanel plot, Axis axis) {
    Object		parent;
    TimeseriesPanel	panel;
    
    super.configure(plot, axis);
    
    parent = GUIHelper.getParent(plot, TimeseriesPanel.class);
    if (parent != null) {
      panel = (TimeseriesPanel) parent;
      if (m_Fixed) {
	panel.setMinY(m_Minimum);
	panel.setMaxY(m_Maximum);
      }
      else {
	panel.setMinY(null);
	panel.setMaxY(null);
      }
    }
  }
}
