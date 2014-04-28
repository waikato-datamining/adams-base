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
 * CoordinatesPaintlet.java
 * Copyright (C) 2010-2012 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.core;


import java.awt.Color;
import java.awt.Graphics;

import adams.gui.event.PaintEvent.PaintMoment;
import adams.gui.visualization.core.plot.Axis;

/**
 <!-- globalinfo-start -->
 * Paintlet for painting the coordinates.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-stroke-thickness &lt;float&gt; (property: strokeThickness)
 * &nbsp;&nbsp;&nbsp;The thickness of the stroke.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.01
 * </pre>
 *
 * <pre>-color-x &lt;java.awt.Color&gt; (property: XColor)
 * &nbsp;&nbsp;&nbsp;The color of the X axis.
 * &nbsp;&nbsp;&nbsp;default: #808080
 * </pre>
 *
 * <pre>-color-y &lt;java.awt.Color&gt; (property: YColor)
 * &nbsp;&nbsp;&nbsp;The color of the Y axis.
 * &nbsp;&nbsp;&nbsp;default: #808080
 * </pre>
 *
 * <pre>-visible-x (property: XVisible)
 * &nbsp;&nbsp;&nbsp;If set to true then X axis is visible.
 * </pre>
 *
 * <pre>-visible-y (property: YVisible)
 * &nbsp;&nbsp;&nbsp;If set to true then Y axis is visible.
 * </pre>
 *
 * <pre>-offset-x &lt;double&gt; (property: XOffset)
 * &nbsp;&nbsp;&nbsp;The offset of the X axis.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * </pre>
 *
 * <pre>-offset-y &lt;double&gt; (property: YOffset)
 * &nbsp;&nbsp;&nbsp;The offset of the Y axis.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CoordinatesPaintlet
  extends AbstractStrokePaintlet {

  /** for serialization. */
  private static final long serialVersionUID = -3239338605889228940L;

  /**
   * Enum for the coordinates.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static enum Coordinates {
    /** the X-Axis. */
    X,
    /** the Y-Axis. */
    Y
  }

  /** the color to use for painting for the X axis. */
  protected Color m_XColor;

  /** the color to use for painting for the Y axis. */
  protected Color m_YColor;

  /** whether the X-axis is visible. */
  protected boolean m_XVisible;

  /** whether the Y-axis is visible. */
  protected boolean m_YVisible;

  /** the Y-offset for the X-axis. */
  protected double m_XOffset;

  /** the X-offset for the Y-axis. */
  protected double m_YOffset;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Paintlet for painting the coordinates.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "color-x", "XColor",
	    Color.GRAY);

    m_OptionManager.add(
	    "color-y", "YColor",
	    Color.GRAY);

    m_OptionManager.add(
	    "invisible-x", "XInvisible",
	    false);

    m_OptionManager.add(
	    "invisible-y", "YInvisible",
	    false);

    m_OptionManager.add(
	    "offset-x", "XOffset",
	    0.0);

    m_OptionManager.add(
	    "offset-y", "YOffset",
	    0.0);
  }

  /**
   * Sets the color for the X axis.
   *
   * @param value	the new color
   */
  public void setXColor(Color value) {
    m_XColor = value;
    memberChanged();
  }

  /**
   * Returns the color for the X axis.
   *
   * @return		the color
   */
  public Color getXColor() {
    return m_XColor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String XColorTipText() {
    return "The color of the X axis.";
  }

  /**
   * Sets the color for the Y axis.
   *
   * @param value	the new color
   */
  public void setYColor(Color value) {
    m_YColor = value;
    memberChanged();
  }

  /**
   * Returns the color for the Y axis.
   *
   * @return		the color
   */
  public Color getYColor() {
    return m_YColor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String YColorTipText() {
    return "The color of the Y axis.";
  }

  /**
   * Sets the offset for the X axis.
   *
   * @param value	the new offset
   */
  public void setXOffset(double value) {
    m_XOffset = value;
    memberChanged();
  }

  /**
   * Returns the offset for the X axis.
   *
   * @return		the offset
   */
  public double getXOffset() {
    return m_XOffset;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String XOffsetTipText() {
    return "The offset of the X axis.";
  }

  /**
   * Sets the offset for the Y axis.
   *
   * @param value	the new offset
   */
  public void setYOffset(double value) {
    m_YOffset = value;
    memberChanged();
  }

  /**
   * Returns the offset for the Y axis.
   *
   * @return		the offset
   */
  public double getYOffset() {
    return m_YOffset;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String YOffsetTipText() {
    return "The offset of the Y axis.";
  }

  /**
   * Sets the X axis visible or hides it.
   *
   * @param value	true to hide the axis, false to show it
   */
  public void setXInvisible(boolean value) {
    m_XVisible = !value;
    memberChanged();
  }

  /**
   * Returns whether the X axis is visible or not.
   *
   * @return		true if invisible
   */
  public boolean isXInvisible() {
    return !m_XVisible;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String XInvisibleTipText() {
    return "If set to true then X axis is invisible.";
  }

  /**
   * Sets the Y axis visible or hides it.
   *
   * @param value	true to hide the axis, false to display it
   */
  public void setYInvisible(boolean value) {
    m_YVisible = !value;
    memberChanged();
  }

  /**
   * Returns whether the Y axis is visible or not.
   *
   * @return		true if invisible
   */
  public boolean isYInvisible() {
    return !m_YVisible;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String YInvisibleTipText() {
    return "If set to true then Y axis is invisible.";
  }

  /**
   * Returns when this paintlet is to be executed.
   *
   * @return		when this paintlet is to be executed
   */
  public PaintMoment getPaintMoment() {
    return PaintMoment.GRID;
  }
  /**
   * The paint routine of the paintlet.
   *
   * @param g		the graphics context to use for painting
   * @param moment	what {@link PaintMoment} is currently being painted
   */
  public void performPaint(Graphics g, PaintMoment moment) {
    AxisPanel	axisX;
    AxisPanel	axisY;

    if (m_XVisible) {
      axisY = getPanel().getPlot().getAxis(Axis.LEFT);
      g.setColor(m_XColor);
      g.drawLine(
	  0,
	  axisY.valueToPos(m_XOffset),
	  getPlot().getWidth() - 1,
	  axisY.valueToPos(m_XOffset));
    }

    if (m_YVisible) {
      axisX = getPanel().getPlot().getAxis(Axis.BOTTOM);
      g.setColor(m_YColor);
      g.drawLine(
	  axisX.valueToPos(m_YOffset),
	  0,
	  axisX.valueToPos(m_YOffset),
	  getPlot().getHeight() - 1);
    }
  }
}
