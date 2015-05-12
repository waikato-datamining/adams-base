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
 * FixedTimestampRangePaintlet.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.timeseries;

import adams.core.DateUtils;
import adams.core.base.BaseDateTime;
import adams.gui.core.ColorHelper;
import adams.gui.event.PaintEvent.PaintMoment;
import adams.gui.visualization.core.AxisPanel;
import adams.gui.visualization.core.axis.FlippableAxisModel;
import adams.gui.visualization.core.plot.Axis;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Date;

/**
 <!-- globalinfo-start -->
 * Paintlet for highlighting a specific timestamp range with a background color.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-stroke-thickness &lt;float&gt; (property: strokeThickness)
 * &nbsp;&nbsp;&nbsp;The thickness of the stroke.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.01
 * </pre>
 * 
 * <pre>-start-timestamp &lt;adams.core.base.BaseDateTime&gt; (property: startTimestamp)
 * &nbsp;&nbsp;&nbsp;The timestamp indicating the start of the range.
 * &nbsp;&nbsp;&nbsp;default: NOW
 * </pre>
 * 
 * <pre>-end-timestamp &lt;adams.core.base.BaseDateTime&gt; (property: endTimestamp)
 * &nbsp;&nbsp;&nbsp;The timestamp indicating the end of the range.
 * &nbsp;&nbsp;&nbsp;default: NOW
 * </pre>
 * 
 * <pre>-color &lt;java.awt.Color&gt; (property: color)
 * &nbsp;&nbsp;&nbsp;The color of the selected range.
 * &nbsp;&nbsp;&nbsp;default: #D8D8D8
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7297 $
 */
public class FixedTimestampRangePaintlet
  extends AbstractTimeseriesPaintlet {

  /** for serialization. */
  private static final long serialVersionUID = 4296847364394457330L;

  /** the start timestamp of the range. */
  protected BaseDateTime m_StartTimestamp;

  /** the end timestamp of the range. */
  protected BaseDateTime m_EndTimestamp;

  /** the color to paint the point with. */
  protected Color m_Color;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Paintlet for highlighting a specific timestamp range with a background color.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "start-timestamp", "startTimestamp",
      new BaseDateTime(BaseDateTime.NOW));

    m_OptionManager.add(
      "end-timestamp", "endTimestamp",
      new BaseDateTime(BaseDateTime.NOW));

    m_OptionManager.add(
      "color", "color",
      ColorHelper.valueOf("#D8D8D8"));
  }

  /**
   * Sets the timestamp for the start of the range.
   *
   * @param value	the timestamp
   */
  public void setStartTimestamp(BaseDateTime value) {
    m_StartTimestamp = value;
    memberChanged();
  }

  /**
   * Returns the timestamp for the start of the range.
   *
   * @return		the timestamp
   */
  public BaseDateTime getStartTimestamp() {
    return m_StartTimestamp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String startTimestampTipText() {
    return "The timestamp indicating the start of the range.";
  }

  /**
   * Sets the timestamp for the end of the range.
   *
   * @param value	the timestamp
   */
  public void setEndTimestamp(BaseDateTime value) {
    m_EndTimestamp = value;
    memberChanged();
  }

  /**
   * Returns the timestamp for the end of the range.
   *
   * @return		the timestamp
   */
  public BaseDateTime getEndTimestamp() {
    return m_EndTimestamp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String endTimestampTipText() {
    return "The timestamp indicating the end of the range.";
  }

  /**
   * Sets the color to paint the range with.
   *
   * @param value	the color
   */
  public void setColor(Color value) {
    if (    (value != m_Color)
	|| ((value != null) && !value.equals(m_Color)) ) {
      m_Color = value;
      memberChanged();
    }
  }

  /**
   * Returns the currently set color to paint the range with.
   *
   * @return		the color
   */
  public Color getColor() {
    return m_Color;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorTipText() {
    return "The color of the selected range.";
  }

  /**
   * Returns when this paintlet is to be executed.
   *
   * @return		when this paintlet is to be executed
   */
  @Override
  public PaintMoment getPaintMoment() {
    return PaintMoment.BACKGROUND;
  }

  /**
   * The paint routine of the paintlet.
   *
   * @param g		the graphics context to use for painting
   * @param moment	what {@link PaintMoment} is currently being painted
   */
  @Override
  public void performPaint(Graphics g, PaintMoment moment) {
    AxisPanel	axisX;
    Date        startDate;
    Date        endDate;
    boolean     flipped;

    startDate = m_StartTimestamp.dateValue();
    endDate   = m_EndTimestamp.dateValue();
    axisX     = getPanel().getPlot().getAxis(Axis.BOTTOM);
    flipped   = (axisX.getAxisModel() instanceof FlippableAxisModel) && ((FlippableAxisModel) axisX.getAxisModel()).isFlipped();

    if ((startDate != null) && (endDate != null)) {
      if (DateUtils.isBefore(startDate, endDate)) {
        startDate = m_EndTimestamp.dateValue();
        endDate   = m_StartTimestamp.dateValue();
      }

      g.setColor(getColor());
      if (flipped)
        g.fillRect(
          axisX.valueToPos(endDate.getTime()),
          0,
          axisX.valueToPos(startDate.getTime()) - axisX.valueToPos(endDate.getTime()),
          getPanel().getHeight());
      else
        g.fillRect(
          axisX.valueToPos(startDate.getTime()),
          0,
          axisX.valueToPos(endDate.getTime()) - axisX.valueToPos(startDate.getTime()),
          getPanel().getHeight());
    }
  }
}
