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
 * StraightLineOverlayPaintlet.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.sequence;

import java.awt.Color;
import java.awt.Graphics;

import adams.gui.core.AntiAliasingSupporter;
import adams.gui.core.GUIHelper;
import adams.gui.event.PaintEvent.PaintMoment;
import adams.gui.visualization.core.AbstractStrokePaintlet;
import adams.gui.visualization.core.AxisPanel;
import adams.gui.visualization.core.plot.Axis;

/**
 <!-- globalinfo-start -->
 * Draws a straight line. The inclination can be influenced using the x-factor.
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
 * <pre>-x-factor &lt;double&gt; (property: XFactor)
 * &nbsp;&nbsp;&nbsp;The factor to multiply the X values with to determine the inclination of 
 * &nbsp;&nbsp;&nbsp;the line.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * </pre>
 * 
 * <pre>-color &lt;java.awt.Color&gt; (property: color)
 * &nbsp;&nbsp;&nbsp;The color for the line.
 * &nbsp;&nbsp;&nbsp;default: #000000
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StraightLineOverlayPaintlet
  extends AbstractStrokePaintlet
  implements XYSequencePaintlet, AntiAliasingSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 6292059403058224856L;

  /** the factor to mulitply the x values with. */
  protected double m_XFactor;

  /** the offset to shift the line up or down. */
  protected double m_YOffset;
  
  /** the color for the overlay. */
  protected Color m_Color;

  /** whether anti-aliasing is enabled. */
  protected boolean m_AntiAliasingEnabled;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Draws a straight line. The inclination can be influenced using the x-factor.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"x-factor", "XFactor",
	1.0);

    m_OptionManager.add(
	"y-offset", "YOffset",
	0.0);
    
    m_OptionManager.add(
	"color", "color", 
	Color.BLACK);

    m_OptionManager.add(
	    "anti-aliasing-enabled", "antiAliasingEnabled",
	    GUIHelper.getBoolean(getClass(), "antiAliasingEnabled", true));
  }

  /**
   * Sets the factor to multiply the X values with to determine the inclination.
   *
   * @param value	the factor
   */
  public void setXFactor(double value) {
    m_XFactor = value;
    memberChanged();
  }

  /**
   * Returns the factor to multiply the X values with to determine the inclination.
   *
   * @return		the factor
   */
  public double getXFactor() {
    return m_XFactor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String XFactorTipText() {
    return "The factor to multiply the X values with to determine the inclination of the line.";
  }

  /**
   * Sets the offset on the Y axis.
   *
   * @param value	the offset
   */
  public void setYOffset(double value) {
    m_YOffset = value;
    memberChanged();
  }

  /**
   * Returns the offset on the Y axis.
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
    return "The offset to shift the line up or down on the Y axis.";
  }

  /**
   * Set the stroke color for the paintlet.
   * 
   * @param value	color of the stroke
   */
  public void setColor(Color value) {
    m_Color = value;
    memberChanged();
  }

  /**
   * Get the stroke color for the paintlet.
   * 
   * @return		color of the stroke
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
    return "The color for the line.";
  }

  /**
   * Sets whether to use anti-aliasing.
   *
   * @param value	if true then anti-aliasing is used
   */
  public void setAntiAliasingEnabled(boolean value) {
    m_AntiAliasingEnabled = value;
    memberChanged();
  }

  /**
   * Returns whether anti-aliasing is used.
   *
   * @return		true if anti-aliasing is used
   */
  public boolean isAntiAliasingEnabled() {
    return m_AntiAliasingEnabled;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String antiAliasingEnabledTipText() {
    return "If enabled, uses anti-aliasing for drawing lines.";
  }

  /**
   * Returns when this paintlet is to be executed.
   *
   * @return		when this paintlet is to be executed
   */
  @Override
  public PaintMoment getPaintMoment() {
    return PaintMoment.GRID;
  }

  /**
   * Returns the XY sequence panel currently in use.
   *
   * @return		the panel in use
   */
  @Override
  public XYSequencePanel getSequencePanel() {
    return (XYSequencePanel) getPanel();
  }

  /**
   * Returns a new instance of the hit detector to use.
   *
   * @return		always null
   */
  @Override
  public AbstractXYSequencePointHitDetector newHitDetector() {
    return null;
  }

  /**
   * The paint routine of the paintlet.
   *
   * @param g		the graphics context to use for painting
   * @param moment	what {@link PaintMoment} is currently being painted
   */
  @Override
  public void performPaint(Graphics g, PaintMoment moment) {
    AxisPanel	xAxis;
    AxisPanel	yAxis;
    double	xMin;
    double	xMax;
    double 	yMin;
    double	yMax;
    
    xAxis = getPlot().getAxis(Axis.BOTTOM);
    yAxis = getPlot().getAxis(Axis.LEFT);
    xMin  = xAxis.getActualMinimum();
    yMin  = xMin * m_XFactor + m_YOffset;
    xMax  = xAxis.getActualMaximum();
    yMax  = xMax * m_XFactor + m_YOffset;
    
    g.setColor(m_Color);
    GUIHelper.configureAntiAliasing(g, m_AntiAliasingEnabled);
    g.drawLine(xAxis.valueToPos(xMin), yAxis.valueToPos(yMin), xAxis.valueToPos(xMax), yAxis.valueToPos(yMax));
  }
}
