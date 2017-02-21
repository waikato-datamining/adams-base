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
 * AbstractAxisModel.java
 * Copyright (C) 2008-2017 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.core.axis;

import adams.core.logging.LoggingObject;
import adams.gui.visualization.core.AxisPanel;

import java.util.List;
import java.util.Stack;

/**
 * An abstract class of an axis model.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractAxisModel
  extends LoggingObject {

  /** for serialization. */
  private static final long serialVersionUID = -3950212023344727427L;

  /** the owning axis. */
  protected AxisPanel m_Parent;

  /** the minimum. */
  protected double m_Minimum;

  /** the maximum. */
  protected double m_Maximum;

  /** the actual minimum to display (including the bottom margin). */
  protected double m_ActualMinimum;

  /** the actual maximum to display (including the top margin). */
  protected double m_ActualMaximum;

  /** the top margin. */
  protected double m_MarginTop;

  /** the bottom margin. */
  protected double m_MarginBottom;

  /** indicates whether the layout has been validated. */
  protected boolean m_Validated;

  /** the format for outputting the values (SimpleDateFormat or DecimalFormat). */
  protected Formatter m_Formatter;

  /** a customer formatter to use. */
  protected Formatter m_CustomerFormatter;

  /** the zooms. */
  protected ZoomHandler m_ZoomHandler;

  /** a pixel offset due to panning. */
  protected int m_PixelOffset;

  /** stack of pixel offsets for the zooms. */
  protected Stack<Integer> m_PixelOffsets;
  
  /** the tick generator. */
  protected TickGenerator m_TickGenerator;

  /** every nth value to display. */
  protected int m_NthValueToShow;

  /** the manual minimum. */
  protected Double m_ManualMinimum;

  /** the manual maximum. */
  protected Double m_ManualMaximum;

  /** the manual top margin. */
  protected Double m_ManualMarginTop;

  /** the manual bottom margin. */
  protected Double m_ManualMarginBottom;

  /**
   * Initializes the model.
   */
  public AbstractAxisModel() {
    super();

    initialize();
  }

  /**
   * Initializes the member variables.
   */
  protected void initialize() {
    m_Parent             = null;
    m_Minimum            = 0.0;
    m_Maximum            = 1.0;
    m_ManualMinimum      = null;
    m_ManualMaximum      = null;
    m_MarginTop          = 0.0;
    m_MarginBottom       = 0.0;
    m_ManualMarginTop    = null;
    m_ManualMarginBottom = null;
    m_PixelOffset        = 0;
    m_Validated          = false;
    m_Formatter          = Formatter.getDecimalFormatter(getDefaultNumberFormat());
    m_CustomerFormatter  = null;
    m_ZoomHandler        = new ZoomHandler();
    m_PixelOffsets       = new Stack<>();
    m_TickGenerator      = new SimpleTickGenerator();
  }
  
  /**
   * Sets the owning axis panel.
   *
   * @param value	the axis panel
   */
  public void setParent(AxisPanel value) {
    m_Parent = value;
  }

  /**
   * Returns the owning axis panel.
   *
   * @return		the axis panel
   */
  public AxisPanel getParent() {
    return m_Parent;
  }

  /**
   * Returns the display name of this model.
   *
   * @return		the display name
   */
  public abstract String getDisplayName();

  /**
   * Checks whether the data range can be handled by the model.
   *
   * @param min		the minimum value
   * @param max		the maximum value
   * @return		true if the data can be handled
   */
  public abstract boolean canHandle(double min, double max);

  /**
   * Sets the minimum to display on the axis.
   *
   * @param value	the minimum value
   */
  public void setMinimum(double value) {
    m_Minimum = value;
    invalidate();
    update();
  }

  /**
   * Returns the currently set minimum on the axis.
   *
   * @return		the minimum value
   */
  public double getMinimum() {
    return m_Minimum;
  }

  /**
   * Sets the manual minimum to display on the axis.
   *
   * @param value	the minimum value, null to unset
   */
  public void setManualMinimum(Double value) {
    m_ManualMinimum = value;
    invalidate();
    update();
  }

  /**
   * Returns the currently set manual minimum on the axis.
   *
   * @return		the minimum value, null if none set
   */
  public Double getManualMinimum() {
    return m_ManualMinimum;
  }

  /**
   * Returns the actual minimum on the axis.
   *
   * @return		the actual minimum
   */
  public double getActualMinimum() {
    validate();
    return m_ActualMinimum;
  }

  /**
   * Sets the maximum to display on the axis.
   *
   * @param value	the maximum value
   */
  public void setMaximum(double value) {
    m_Maximum = value;
    invalidate();
    update();
  }

  /**
   * Returns the currently set maximum on the axis.
   *
   * @return		the maximum value
   */
  public double getMaximum() {
    return m_Maximum;
  }

  /**
   * Sets the manual maximum to display on the axis.
   *
   * @param value	the maximum value, null to unset
   */
  public void setManualMaximum(Double value) {
    m_ManualMaximum = value;
    invalidate();
    update();
  }

  /**
   * Returns the currently set manual maximum on the axis.
   *
   * @return		the manual maximum value, null if none set
   */
  public Double getManualMaximum() {
    return m_ManualMaximum;
  }

  /**
   * Returns the actual maximum on the axis.
   *
   * @return		the actual maximum
   */
  public double getActualMaximum() {
    validate();
    return m_ActualMaximum;
  }

  /**
   * Sets the top margin factor (>= 0.0).
   *
   * @param value	the top margin
   */
  public void setTopMargin(double value) {
    if (value >= 0) {
      m_MarginTop = value;
      invalidate();
      update();
    }
    else {
      getLogger().warning(
	  "Top margin factor must be at least 0.0 (provided: " + value + ")!");
    }
  }

  /**
   * Returns the currently set top margin factor (>= 0.0).
   *
   * @return		the top margin
   */
  public double getTopMargin() {
    return m_MarginTop;
  }

  /**
   * Sets the bottom margin factor (>= 0.0).
   *
   * @param value	the bottom margin
   */
  public void setBottomMargin(double value) {
    if (value >= 0) {
      m_MarginBottom = value;
      invalidate();
      update();
    }
    else {
      getLogger().warning(
	  "Bottom margin factor must be at least 0.0 (provided: " + value + ")!");
    }
  }

  /**
   * Returns the currently set bottom margin factor (>= 0.0).
   *
   * @return		the bottom margin
   */
  public double getBottomMargin() {
    return m_MarginBottom;
  }

  /**
   * Sets the manual top margin factor (>= 0.0 or null).
   *
   * @param value	the top margin
   */
  public void setManualTopMargin(Double value) {
    if ((value == null) || (value >= 0)) {
      m_ManualMarginTop = value;
      invalidate();
      update();
    }
    else {
      getLogger().warning(
	  "Manual top margin factor must be null or at least 0.0 (provided: " + value + ")!");
    }
  }

  /**
   * Returns the currently set manual top margin factor (>= 0.0 or null).
   *
   * @return		the top margin
   */
  public Double getManualTopMargin() {
    return m_ManualMarginTop;
  }

  /**
   * Sets the manual bottom margin factor (>= 0.0 or null).
   *
   * @param value	the bottom margin
   */
  public void setManualBottomMargin(Double value) {
    if ((value == null) || (value >= 0)) {
      m_ManualMarginBottom = value;
      invalidate();
      update();
    }
    else {
      getLogger().warning(
	  "Manual bottom margin factor must be null or at least 0.0 (provided: " + value + ")!");
    }
  }

  /**
   * Returns the currently set manual bottom margin factor (>= 0.0 or null).
   *
   * @return		the bottom margin
   */
  public Double getManualBottomMargin() {
    return m_ManualMarginBottom;
  }

  /**
   * Sets the pixel offset due to panning.
   *
   * @param value	the offset
   */
  public void setPixelOffset(int value) {
    m_PixelOffset = value;
    invalidate();
    update();
  }

  /**
   * Returns the current pixel offset.
   *
   * @return		the offset
   */
  public int getPixelOffset() {
    return m_PixelOffset;
  }

  /**
   * Returns the default number format.
   *
   * @return		the default format
   */
  public String getDefaultNumberFormat() {
    return "0.00E0;-0.00E0";
  }

  /**
   * Sets the pattern used for displaying the numbers on the axis.
   *
   * @param value	the value to use
   */
  public void setNumberFormat(String value) {
    m_Formatter.applyPattern(value);
    update();
  }

  /**
   * Returns the pattern used for displaying the numbers on the axis.
   *
   * @return		the pattern
   */
  public String getNumberFormat() {
    return m_Formatter.toPattern();
  }

  /**
   * Returns whether a custom formatter is in use.
   *
   * @return		true if a custom formatter is used
   */
  public boolean hasCustomFormatter() {
    return (m_CustomerFormatter != null);
  }

  /**
   * Sets the custom formatter to use. Use null to unset the formatter.
   *
   * @param value	the custom formatter to use
   */
  public void setCustomFormatter(Formatter value) {
    m_CustomerFormatter = value;
  }

  /**
   * Returns the current custom formatter, can be null if none set.
   *
   * @return		the custom formatter
   */
  public Formatter getCustomFormatter() {
    return m_CustomerFormatter;
  }

  /**
   * Returns the formatter to use for parsing/formatting.
   *
   * @return		the formatter to use
   * @see		#m_Formatter
   * @see		#m_CustomerFormatter
   */
  protected Formatter getActualFormatter() {
    if (hasCustomFormatter())
      return m_CustomerFormatter;
    else
      return m_Formatter;
  }

  /**
   * Returns the minimum difference that must exist between min/max in order
   * to allow zooming.
   * 
   * @return		the minimum difference
   */
  protected double getMinZoomDifference() {
    return 10E-8;
  }
  
  /**
   * Checks whether we can still zoom in.
   *
   * @param min		the minimum of the zoom
   * @param max		the maximum of the zoom
   * @return		true if zoom is possible
   */
  public boolean canZoom(double min, double max) {
    return (Math.abs(max - min) > getMinZoomDifference());
  }

  /**
   * Adds the zoom to its internal list and updates the axis.
   *
   * @param min		the minimum of the zoom
   * @param max		the maximum of the zoom
   */
  public void pushZoom(double min, double max) {
    m_ZoomHandler.push(min, max);
    m_PixelOffsets.push(m_PixelOffset);
    m_PixelOffset = 0;

    invalidate();
    update();
  }

  /**
   * Removes the latest zoom, if available.
   */
  public void popZoom() {
    if (isZoomed()) {
      m_ZoomHandler.pop();
      m_PixelOffset = m_PixelOffsets.pop();
      invalidate();
      update();
    }
  }

  /**
   * Returns true if the axis is currently zoomed.
   *
   * @return		true if a zoom is in place
   */
  public boolean isZoomed() {
    return m_ZoomHandler.isZoomed();
  }

  /**
   * Removes all zooms.
   */
  public void clearZoom() {
    if (isZoomed()) {
      m_ZoomHandler.clear();
      m_PixelOffset = 0;
      m_PixelOffsets.clear();
      invalidate();
      update();
    }
  }
  
  /**
   * Sets the tick generator to use.
   * 
   * @param value	the tick generator
   */
  public void setTickGenerator(TickGenerator value) {
    m_TickGenerator = value;
    m_TickGenerator.setParent(this);
    update();
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
   * Returns the ticks of this axis.
   *
   * @return		the current ticks to display
   */
  public List<Tick> getTicks() {
    return m_TickGenerator.getTicks();
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
      update();
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
   * Returns the display string of the value for the tooltip, for instance.
   *
   * @param value	the value to turn into string
   * @return		the display string
   */
  protected abstract String doValueToDisplay(double value);

  /**
   * Returns the display string of the value for the tooltip, for instance.
   *
   * @param value	the value to turn into string
   * @return		the display string
   */
  public String valueToDisplay(double value) {
    if (m_TickGenerator instanceof FixedLabelTickGenerator)
      return ((FixedLabelTickGenerator) m_TickGenerator).valueToDisplayLabel(value);
    else
      return doValueToDisplay(value);
  }

  /**
   * Returns the position on the axis for the given value.
   *
   * @param value	the value to get the position for
   * @return		the corresponding position
   */
  public abstract int valueToPos(double value);

  /**
   * Returns the value for the given position on the axis.
   *
   * @param pos	the position to get the corresponding value for
   * @return		the corresponding value
   */
  public abstract double posToValue(int pos);

  /**
   * Invalidates the current setup, calculations necessary for margins, etc.
   */
  public void invalidate() {
    m_Validated = false;
  }

  /**
   * calculates the top and bottom margin if necessary.
   */
  public void validate() {
    double	range;
    double	min;
    double	max;
    double	top;
    double	bottom;
    double	offset;
    double	size;

    if (m_Validated)
      return;

    // min/max
    if (m_ZoomHandler.isZoomed()) {
      min = m_ZoomHandler.peek().getMinimum();
      max = m_ZoomHandler.peek().getMaximum();
    }
    else {
      if (m_ManualMinimum != null)
	min = m_ManualMinimum;
      else
	min = m_Minimum;
      if (m_ManualMaximum != null)
	max = m_ManualMaximum;
      else
	max = m_Maximum;
    }

    // margins
    if (m_ManualMarginTop != null)
      top = m_ManualMarginTop;
    else
      top = m_MarginTop;
    if (m_ManualMarginBottom != null)
      bottom = m_ManualMarginBottom;
    else
      bottom = m_MarginBottom;

    if (getParent().getLength() == 0)
      size = 1;
    else
      size = getParent().getLength();
    range           = Math.abs(max - min);
    offset          = range / size * (double) m_PixelOffset;
    m_ActualMinimum = min - range * bottom - offset;
    m_ActualMaximum = max + range * top    - offset;

    m_Validated = true;
  }

  /**
   * Obtains the necessary values from the given model and updates itself.
   *
   * @param model	the model to get the parameters from
   */
  public void assign(AbstractAxisModel model) {
    m_Parent             = model.m_Parent;
    m_Minimum            = model.m_Minimum;
    m_Maximum            = model.m_Maximum;
    m_ManualMinimum      = model.m_ManualMinimum;
    m_ManualMaximum      = model.m_ManualMaximum;
    m_MarginTop          = model.m_MarginTop;
    m_MarginBottom       = model.m_MarginBottom;
    m_ManualMarginTop    = model.m_ManualMarginTop;
    m_ManualMarginBottom = model.m_ManualMarginBottom;
    m_ZoomHandler        = model.m_ZoomHandler.getClone();
    m_PixelOffsets       = (Stack<Integer>) model.m_PixelOffsets.clone();
    m_PixelOffset        = model.getPixelOffset();
    m_CustomerFormatter  = model.m_CustomerFormatter;
    m_TickGenerator      = model.getTickGenerator().shallowCopy();
    m_TickGenerator.setParent(this);
    m_NthValueToShow     = model.getNthValueToShow();

    invalidate();
    update();
  }

  /**
   * Forces the panel to repaint itself.
   */
  public void update() {
    validate();
    if (m_Parent != null) {
      m_Parent.repaint();
      m_Parent.notifyChangeListeners();
    }
  }

  /**
   * Returns a string representation of the model.
   *
   * @return		 a string representation
   */
  @Override
  public String toString() {
    String	result;

    result  = getClass().getName() + ": ";
    result += "min=" + getMinimum() + ", ";
    result += "max=" + getMaximum() + ", ";
    result += "topMargin=" + getTopMargin() + ", ";
    result += "bottomMargin=" + getBottomMargin() + ", ";
    result += "format=" + getNumberFormat();

    return result;
  }
}