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
 * AxisPanelOptions.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.core;

import adams.core.option.AbstractOptionHandler;
import adams.data.DecimalFormatString;
import adams.gui.visualization.core.axis.FancyTickGenerator;
import adams.gui.visualization.core.axis.TickGenerator;
import adams.gui.visualization.core.axis.Type;
import adams.gui.visualization.core.plot.Axis;

/**
 <!-- globalinfo-start -->
 * Encapsulates options for an axis in a plot.
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
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class AxisPanelOptions
  extends AbstractOptionHandler {

  /** for serialization. */
  private static final long serialVersionUID = 1675594412675760089L;

  /** the label of the axis. */
  protected String m_Label;

  /** the type of the axis. */
  protected Type m_Type;

  /** whether to show gridlines or not. */
  protected boolean m_ShowGridLines;

  /** the tick generator to use. */
  protected TickGenerator m_TickGenerator;

  /** every nth value to display. */
  protected int m_NthValueToShow;

  /** the length in pixles of ticks to use. */
  protected int m_LengthTicks;

  /** the custom number/date format. */
  protected DecimalFormatString m_CustomFormat;

  /** the top margin. */
  protected double m_TopMargin;

  /** the bottom margin. */
  protected double m_BottomMargin;

  /** the width of the axis (for HORIZONTAL axes, this is the height, of course). */
  protected int m_Width;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Encapsulates options for an axis in a plot.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "label", "label",
	    "");

    m_OptionManager.add(
	    "type", "type",
	    Type.ABSOLUTE);

    m_OptionManager.add(
	    "hide-grid-lines", "showGridLines",
	    true);

    m_OptionManager.add(
	    "tick-generator", "tickGenerator",
	    new FancyTickGenerator());

    m_OptionManager.add(
	"nth-value", "nthValueToShow",
        5, 0, null);

    m_OptionManager.add(
	    "length-ticks", "lengthTicks",
	    4, 1, null);

    m_OptionManager.add(
	    "width", "width",
	    20, 5, null);

    m_OptionManager.add(
	    "top-margin", "topMargin",
	    0.0, 0.0, null);

    m_OptionManager.add(
	    "bottom-margin", "bottomMargin",
	    0.0, 0.0, null);

    m_OptionManager.add(
	    "custom-format", "customFormat",
	    new DecimalFormatString(""));
  }

  /**
   * Sets label of the axis.
   *
   * @param value 	the label
   */
  public void setLabel(String value) {
    m_Label = value;
    reset();
  }

  /**
   * Returns the label of the axis.
   *
   * @return 		the label
   */
  public String getLabel() {
    return m_Label;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelTipText() {
    return "The label of the axis.";
  }

  /**
   * Sets type of the axis.
   *
   * @param value 	the type
   */
  public void setType(Type value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type of the axis.
   *
   * @return 		the type
   */
  public Type getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "The type of the axis.";
  }

  /**
   * Sets whether to plot grid lines as well.
   *
   * @param value 	if true grid lines will be plotted
   */
  public void setShowGridLines(boolean value) {
    m_ShowGridLines = value;
    reset();
  }

  /**
   * Returns whether to plot grid lines as well.
   *
   * @return 		true if grid lines are plotted
   */
  public boolean getShowGridLines() {
    return m_ShowGridLines;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String showGridLinesTipText() {
    return "If enabled, grid lines are plotted as well.";
  }

  /**
   * Sets tick generator to use.
   *
   * @param value	the tick generator
   */
  public void setTickGenerator(TickGenerator value) {
    m_TickGenerator = value;
    reset();
  }
  
  /**
   * Returns the current tick generator in use.
   * 
   * @return		the tick generator
   */
  public TickGenerator getTickGenerator() {
    return m_TickGenerator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String tickGeneratorTipText() {
    return "Algorithm for generating the tick positions.";
  }

  /**
   * Sets the count of ticks a value is shown, i.e., "3" means every third tick:
   * 1, 4, 7, ...
   *
   * @param value	the count
   */
  public void setNthValueToShow(int value) {
    if (value >= 0) {
      m_NthValueToShow = value;
      reset();
    }
    else {
      getLogger().warning("'n-th value to show' must be >=0, provided: " + value);
    }
  }

  /**
   * Returns the count of ticks a value is shown, i.e., "3" means every third
   * tick: 1, 4, 7, ...
   *
   * @return		the count
   */
  public int getNthValueToShow() {
    return m_NthValueToShow;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String nthValueToShowTipText() {
    return "The count of ticks a value is shown, i.e., '3' means every third tick: 1, 4, 7, ...";
  }

  /**
   * Sets the length of ticks to display along the axis.
   *
   * @param value	the length of ticks (in pixels)
   */
  public void setLengthTicks(int value) {
    if (value > 0) {
      m_LengthTicks = value;
      reset();
    }
    else {
      System.err.println("Length of ticks must be >0, provided: " + value);
    }
  }

  /**
   * Returns the length of ticks currently displayed.
   *
   * @return		the length of ticks (in pixles)
   */
  public int getLengthTicks() {
    return m_LengthTicks;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String lengthTicksTipText() {
    return "The length in pixels of the ticks to display.";
  }

  /**
   * Sets the top margin factor (>= 0.0).
   *
   * @param value	the top margin
   */
  public void setTopMargin(double value) {
    if (value >= 0) {
      m_TopMargin = value;
      reset();
    }
    else {
      System.err.println(
	  "Top margin factor must be at least 0.0 (provided: " + value + ")!");
    }
  }

  /**
   * Returns the currently set top margin factor (>= 0.0).
   *
   * @return		the top margin
   */
  public double getTopMargin() {
    return m_TopMargin;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String topMarginTipText() {
    return "The factor for an extra margin on the top/left (eg 0.05 = 5%).";
  }

  /**
   * Sets the bottom margin factor (>= 0.0).
   *
   * @param value	the bottom margin
   */
  public void setBottomMargin(double value) {
    if (value >= 0) {
      m_BottomMargin = value;
      reset();
    }
    else {
      System.err.println(
	  "Bottom margin factor must be at least 0.0 (provided: " + value + ")!");
    }
  }

  /**
   * Returns the currently set bottom margin factor (>= 0.0).
   *
   * @return		the bottom margin
   */
  public double getBottomMargin() {
    return m_BottomMargin;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String bottomMarginTipText() {
    return "The factor for an extra margin on the bottom/right (eg 0.05 = 5%).";
  }

  /**
   * Sets the width of the axis (this is height for HORIZONTAL axes, of
   * course), at least 5 pixel.
   *
   * @param value	the new width
   */
  public void setWidth(int value) {
    if (value >= 5) {
      m_Width = value;
      reset();
    }
    else {
      System.err.println(
	  "The width must be at least 5 pixels (provided: " + value + ")!");
    }
  }

  /**
   * Returns the current width of the axis.
   *
   * @return		the width
   */
  public int getWidth() {
    return m_Width;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String widthTipText() {
    return "The width/height of the axis (>= 5).";
  }

  /**
   * Sets the custom format for the tick labels.
   *
   * @param value 	the custom format
   */
  public void setCustomFormat(DecimalFormatString value) {
    m_CustomFormat = value;
    reset();
  }

  /**
   * Returns the custom format for the tick labels.
   *
   * @return 		the custom format
   */
  public DecimalFormatString getCustomFormat() {
    return m_CustomFormat;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String customFormatTipText() {
    return
        "The custom format for displaying the tick labels on the axis.";
  }

  /**
   * Applies the options to the specified axis.
   *
   * @param plot	the plot panel to update an axis for
   * @param axis	the axis to configure
   */
  public void configure(PlotPanel plot, Axis axis) {
    plot.setAxisWidth(axis, m_Width);
    plot.getAxis(axis).setType(m_Type);
    plot.getAxis(axis).setShowGridLines(m_ShowGridLines);
    plot.getAxis(axis).setTickGenerator(m_TickGenerator.shallowCopy());
    plot.getAxis(axis).setNthValueToShow(m_NthValueToShow);
    plot.getAxis(axis).setLengthTicks(m_LengthTicks);
    plot.getAxis(axis).setTopMargin(m_TopMargin);
    plot.getAxis(axis).setBottomMargin(m_BottomMargin);
    plot.getAxis(axis).setAxisName(m_Label);
    if (!m_CustomFormat.isEmpty())
      plot.getAxis(axis).setNumberFormat(m_CustomFormat.getValue());
  }
}
