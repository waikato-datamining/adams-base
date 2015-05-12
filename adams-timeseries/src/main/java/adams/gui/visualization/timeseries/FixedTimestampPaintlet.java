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
 * FixedTimestampPaintlet.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.timeseries;

import adams.core.base.BaseDateTime;
import adams.gui.event.PaintEvent.PaintMoment;
import adams.gui.visualization.core.AxisPanel;
import adams.gui.visualization.core.plot.Axis;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Date;

/**
 <!-- globalinfo-start -->
 * Paintlet for highlighting a specific timestamp with a vertical indicator line.
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
 * <pre>-timestamp &lt;adams.core.base.BaseDateTime&gt; (property: timestamp)
 * &nbsp;&nbsp;&nbsp;The timestamp to indicate.
 * &nbsp;&nbsp;&nbsp;default: NOW
 * </pre>
 * 
 * <pre>-prefix &lt;java.lang.String&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The prefix to use for the date label printed next to the indicator.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-offset-x &lt;int&gt; (property: offsetX)
 * &nbsp;&nbsp;&nbsp;The number of pixels to offset the string from the left of the indicator.
 * &nbsp;&nbsp;&nbsp;default: 10
 * </pre>
 * 
 * <pre>-offset-y &lt;int&gt; (property: offsetY)
 * &nbsp;&nbsp;&nbsp;The number of pixels to offset the string from the top of the panel.
 * &nbsp;&nbsp;&nbsp;default: 10
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-color &lt;java.awt.Color&gt; (property: color)
 * &nbsp;&nbsp;&nbsp;The color of the indicator&#47;text.
 * &nbsp;&nbsp;&nbsp;default: #ff0000
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FixedTimestampPaintlet
  extends AbstractTimeseriesPaintlet {

  /** for serialization. */
  private static final long serialVersionUID = 4296847364394457330L;

  /** the timestamp to indicate. */
  protected BaseDateTime m_Timestamp;

  /** the prefix for the date label. */
  protected String m_Prefix;

  /** the pixel offset from the top. */
  protected int m_OffsetY;

  /** the pixel offset from the left. */
  protected int m_OffsetX;

  /** the color to paint the point with. */
  protected Color m_Color;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Paintlet for highlighting a specific timestamp with a vertical indicator line.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "timestamp", "timestamp",
      new BaseDateTime(BaseDateTime.NOW));

    m_OptionManager.add(
      "prefix", "prefix",
      "");

    m_OptionManager.add(
      "offset-x", "offsetX",
      10);

    m_OptionManager.add(
      "offset-y", "offsetY",
      10, 0, null);

    m_OptionManager.add(
      "color", "color",
      Color.RED);
  }

  /**
   * Sets the timestamp to indicate.
   *
   * @param value	the timestamp
   */
  public void setTimestamp(BaseDateTime value) {
    m_Timestamp = value;
    memberChanged();
  }

  /**
   * Returns the currently set timestamp to indicate.
   *
   * @return		the timestamp
   */
  public BaseDateTime getTimestamp() {
    return m_Timestamp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String timestampTipText() {
    return "The timestamp to indicate.";
  }

  /**
   * Sets the prefix for the date label.
   *
   * @param value	the prefix
   */
  public void setPrefix(String value) {
    if (    (value != m_Prefix)
	|| ((value != null) && !value.equals(m_Prefix)) ) {
      m_Prefix = value;
      memberChanged();
    }
  }

  /**
   * Returns the currently set prefix for the date label.
   *
   * @return		the prefix
   */
  public String getPrefix() {
    return m_Prefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String prefixTipText() {
    return "The prefix to use for the date label printed next to the indicator.";
  }

  /**
   * Sets the pixel offset from the left.
   *
   * @param value	the offset
   */
  public void setOffsetX(int value) {
    if (value != m_OffsetX) {
      m_OffsetX = value;
      memberChanged();
    }
  }

  /**
   * Returns the pixel offset from the left.
   *
   * @return		the offset
   */
  public int getOffsetX() {
    return m_OffsetX;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String offsetXTipText() {
    return "The number of pixels to offset the string from the left of the indicator.";
  }

  /**
   * Sets the pixel offset from the top.
   *
   * @param value	the offset
   */
  public void setOffsetY(int value) {
    if (    (value != m_OffsetY)
	 && (value >= 0) ) {
      m_OffsetY = value;
      memberChanged();
    }
  }

  /**
   * Returns the pixel offset from the top.
   *
   * @return		the offset
   */
  public int getOffsetY() {
    return m_OffsetY;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String offsetYTipText() {
    return "The number of pixels to offset the string from the top of the panel.";
  }

  /**
   * Sets the color to paint the point with.
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
   * Returns the currently set color to paint the point with.
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
    return "The color of the indicator/text.";
  }

  /**
   * Returns when this paintlet is to be executed.
   *
   * @return		when this paintlet is to be executed
   */
  @Override
  public PaintMoment getPaintMoment() {
    return PaintMoment.POST_PAINT;
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
    Date        date;

    date  = m_Timestamp.dateValue();
    axisX = getPanel().getPlot().getAxis(Axis.BOTTOM);

    g.setColor(m_Color);
    g.drawLine(
      axisX.valueToPos(date.getTime()),
      0,
      axisX.valueToPos(date.getTime()),
      getPanel().getHeight());
    g.drawString(
      m_Prefix + axisX.valueToDisplay(date.getTime()),
      axisX.valueToPos(date.getTime()) + m_OffsetX,
      m_OffsetY);
  }
}
