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
 * RectangleMapOverlay.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink.openstreetmapviewer;

import java.awt.Color;
import java.awt.Graphics;

import adams.flow.sink.OpenStreetMapViewer;

/**
 <!-- globalinfo-start -->
 * Paints a rectangle at the specified location.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-enabled &lt;boolean&gt; (property: enabled)
 * &nbsp;&nbsp;&nbsp;If enabled, the overlay gets painted.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 * <pre>-x &lt;int&gt; (property: x)
 * &nbsp;&nbsp;&nbsp;The X position of the overlay (&gt;=0: absolute, -1: left, -2: center, -3: 
 * &nbsp;&nbsp;&nbsp;right).
 * &nbsp;&nbsp;&nbsp;default: -3
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 * 
 * <pre>-y &lt;int&gt; (property: y)
 * &nbsp;&nbsp;&nbsp;The Y position of the overlay (&gt;=0: absolute, -1: top, -2: center, -3: bottom
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 * 
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the rectangle.
 * &nbsp;&nbsp;&nbsp;default: 100
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the rectangle.
 * &nbsp;&nbsp;&nbsp;default: 20
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-border-enabled &lt;boolean&gt; (property: borderEnabled)
 * &nbsp;&nbsp;&nbsp;If enabled, the border gets painted.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 * <pre>-border-color &lt;java.awt.Color&gt; (property: borderColor)
 * &nbsp;&nbsp;&nbsp;The color of the border.
 * &nbsp;&nbsp;&nbsp;default: #000000
 * </pre>
 * 
 * <pre>-fill-enabled &lt;boolean&gt; (property: fillEnabled)
 * &nbsp;&nbsp;&nbsp;If enabled, the rectangle gets filled.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 * <pre>-fill-color &lt;java.awt.Color&gt; (property: fillColor)
 * &nbsp;&nbsp;&nbsp;The fill color.
 * &nbsp;&nbsp;&nbsp;default: #32646464
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RectangleMapOverlay
  extends AbstractPositionableMapOverlayWithDimensions {

  /** for serialization. */
  private static final long serialVersionUID = 805661569976845842L;
  
  /** whether the border gets painted. */
  protected boolean m_BorderEnabled;
  
  /** the color of the border. */
  protected Color m_BorderColor;
  
  /** whether filling is enbaled. */
  protected boolean m_FillEnabled;

  /** the fill color. */
  protected Color m_FillColor;
  
  /**
   * Returns a string describing the object.
   *
   * @return 		a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Paints a rectangle at the specified location.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "border-enabled", "borderEnabled",
	    true);

    m_OptionManager.add(
	    "border-color", "borderColor",
	    Color.BLACK);

    m_OptionManager.add(
	    "fill-enabled", "fillEnabled",
	    true);

    m_OptionManager.add(
	    "fill-color", "fillColor",
	    new Color(100,100,100,50));
  }

  /**
   * Returns the default height for the overlay.
   *
   * @return		the default height
   */
  @Override
  protected int getDefaultHeight() {
    return 20;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String heightTipText() {
    return "The height of the rectangle.";
  }
  
  /**
   * Returns the default width for the overlay.
   *
   * @return		the default width
   */
  @Override
  protected int getDefaultWidth() {
    return 100;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String widthTipText() {
    return "The width of the rectangle.";
  }

  /**
   * Sets whether to paint the border.
   *
   * @param value	true if to paint border
   */
  public void setBorderEnabled(boolean value) {
    m_BorderEnabled = value;
    reset();
  }

  /**
   * Returns whether the border gets painted.
   *
   * @return		true if border painted
   */
  public boolean getBorderEnabled() {
    return m_BorderEnabled;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String borderEnabledTipText() {
    return "If enabled, the border gets painted.";
  }

  /**
   * Sets the color for the border.
   *
   * @param value	the border color
   */
  public void setBorderColor(Color value) {
    m_BorderColor = value;
    reset();
  }

  /**
   * Returns the color for the border.
   *
   * @return		the border color
   */
  public Color getBorderColor() {
    return m_BorderColor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String borderColorTipText() {
    return "The color of the border.";
  }

  /**
   * Sets whether to fill the rectangle.
   *
   * @param value	true if to fill
   */
  public void setFillEnabled(boolean value) {
    m_FillEnabled = value;
    reset();
  }

  /**
   * Returns whether the rectangle is filled.
   *
   * @return		true if filled
   */
  public boolean getFillEnabled() {
    return m_FillEnabled;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fillEnabledTipText() {
    return "If enabled, the rectangle gets filled.";
  }

  /**
   * Sets the fill color.
   *
   * @param value	the fill color
   */
  public void setFillColor(Color value) {
    m_FillColor = value;
    reset();
  }

  /**
   * Returns the fill color.
   *
   * @return		the fill color
   */
  public Color getFillColor() {
    return m_FillColor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fillColorTipText() {
    return "The fill color.";
  }

  /**
   * Performs the actual painting.
   * 
   * @param viewer	the associated viewer
   * @param g		the graphics context
   * @param x		the actual x coordinate
   * @param y		the actual y coordinate
   */
  @Override
  protected void doPaintOverlay(OpenStreetMapViewer viewer, Graphics g, int x, int y) {
    if (m_FillEnabled) {
      g.setColor(m_FillColor);
      g.fillRect(x, y - m_Height, m_Width, m_Height);
    }

    if (m_BorderEnabled) {
      g.setColor(m_BorderColor);
      g.drawRect(x, y - m_Height, m_Width, m_Height);
    }
  }
}
