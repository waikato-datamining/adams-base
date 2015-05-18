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
 * DefaultTimeseriesXAxisPanelOptions.java
 * Copyright (C) 2014-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.timeseries;

import adams.data.timeseries.PeriodicityType;
import adams.gui.core.GUIHelper;
import adams.gui.visualization.core.PlotPanel;
import adams.gui.visualization.core.axis.PeriodicityTickGenerator;
import adams.gui.visualization.core.plot.Axis;

/**
 <!-- globalinfo-start -->
 * Encapsulates options for the X axis in a timeseries plot.
 * <br><br>
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
 * <pre>-periodicity &lt;NONE|YEARLY|QUARTERLY|MONTHLY|WEEKLY|DAILY|HALF_DAILY|HOURLY|HALF_HOURLY|PER_MINUTE&gt; (property: periodicity)
 * &nbsp;&nbsp;&nbsp;The type of periodicity to use for the background.
 * &nbsp;&nbsp;&nbsp;default: NONE
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DefaultTimeseriesXAxisPanelOptions
  extends AbstractTimeseriesXAxisPanelOptions {

  /** for serialization. */
  private static final long serialVersionUID = -1774995113138870653L;

  /** the periodicity to use. */
  protected PeriodicityType m_Periodicity;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
	"Encapsulates options for the X axis in a timeseries plot.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "periodicity", "periodicity",
	    PeriodicityType.NONE);
  }

  /**
   * Sets the type of periodicity to use for the background.
   *
   * @param value	the type
   */
  public void setPeriodicity(PeriodicityType value) {
    m_Periodicity = value;
    reset();
  }

  /**
   * Returns the type of periodicity to use for the background.
   *
   * @return		the type
   */
  public PeriodicityType getPeriodicity() {
    return m_Periodicity;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String periodicityTipText() {
    return "The type of periodicity to use for the background.";
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
      panel.getPeriodicityPaintlet().setPeriodicity(m_Periodicity);
    }

    if (plot.getAxis(axis).getTickGenerator() instanceof PeriodicityTickGenerator)
      ((PeriodicityTickGenerator) plot.getAxis(axis).getTickGenerator()).setPeriodicity(m_Periodicity);
  }
}
