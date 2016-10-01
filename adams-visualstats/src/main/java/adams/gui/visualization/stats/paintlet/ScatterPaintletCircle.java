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
 * ScatterPaintletCircle.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.paintlet;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 <!-- globalinfo-start -->
 * Paintlet for displaying points on the scatter point as circles.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
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
 * <pre>-color &lt;java.awt.Color&gt; (property: color)
 * &nbsp;&nbsp;&nbsp;Stroke color for the paintlet
 * &nbsp;&nbsp;&nbsp;default: #000000
 * </pre>
 *
 * <pre>-size &lt;int&gt; (property: size)
 * &nbsp;&nbsp;&nbsp;The size of each data point.
 * &nbsp;&nbsp;&nbsp;default: 5
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-fill-point (property: fillPoint)
 * &nbsp;&nbsp;&nbsp;Whether to fill the data point with solid color
 * </pre>
 *
 * <pre>-fill-color &lt;java.awt.Color&gt; (property: fillColor)
 * &nbsp;&nbsp;&nbsp;Color for filling data points
 * &nbsp;&nbsp;&nbsp;default: #ff0000
 * </pre>
 *
 <!-- options-end -->
 *
 * @author msf8
 * @version $Revision$
 */
public class ScatterPaintletCircle
  extends AbstractScatterPlotPaintlet
  implements SizeBasedPaintlet {

  /** for serialization	*/
  private static final long serialVersionUID = -4535962737391965432L;

  /** Whether to fill data points */
  protected boolean m_Fill;

  /** the fill color. */
  protected Color m_FillColor;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Paintlet for displaying points on the scatter point as circles.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "size", "size",
      5, 1, null);

    m_OptionManager.add(
      "fill-point", "fillPoint",
      false);

    m_OptionManager.add(
      "fill-color", "fillColor",
      Color.RED);
  }

  /**
   * Set the color for filling the data points
   * @param val			Color to fill points
   */
  public void setFillColor(Color val) {
    m_FillColor = val;
    memberChanged();
  }

  /**
   * Get the color for filling the data points
   * @return			Color to fill points
   */
  public Color getFillColor() {
    return m_FillColor;
  }

  /**
   * Tip text for the fill color property
   * @return		String describing the property
   */
  public String fillColorTipText() {
    return "Color for filling data points";
  }

  /**
   * Set whether to fill data points
   * @param val			True if data points to be filled
   */
  public void setFillPoint(boolean val) {
    m_Fill = val;
    memberChanged();
  }

  /**
   * Get whether data points are filled
   * @return		True if data points filled
   */
  public boolean getFillPoint() {
    return m_Fill;
  }

  /**
   * Tip text for the fill property
   * @return			String describing the property
   */
  public String fillPointTipText() {
    return "Whether to fill the data point with solid color";
  }

  /**
   * Set the size of each data point
   * @param val		size in pixels
   */
  public void setSize(int val) {
    m_Size = val;
    memberChanged();
  }

  /**
   * Get the size of each data point
   * @return		size in pixels
   */
  public int getSize() {
    return m_Size;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sizeTipText() {
    return "The size of each data point.";
  }

  /**
   * Returns a new instance of the hit detector to use.
   *
   * @return		the hit detector
   */
  @Override
  public AbstractScatterPlotHitDetector newHitDetector() {
    return new ScatterPlotCircleHitDetector(this);
  }

  /**
   * draws the data on the graphics object
   * @param g		Graphics object to draw on
   */
  protected void drawData(Graphics g) {
    super.drawData(g);
    int posX;
    int posY;

    for(int i = 0; i< m_XData.length; i++) {
      posX = m_AxisBottom.valueToPos(m_XData[i]);
      posY = m_AxisLeft.valueToPos(m_YData[i]);
      //plot the points
      Graphics2D g2d = (Graphics2D)g;
      //if fill the data points
      if (m_Fill) {
        g2d.setColor(getActualColor(i, m_FillColor));
        g2d.setStroke(new BasicStroke(0));
        g2d.fillOval(posX-(int)(.5*m_Size), posY-(int)(.5*m_Size), m_Size, m_Size);
      }
      //outline of data point
      g2d.setStroke(new BasicStroke(m_StrokeThickness));
      g2d.setColor(m_Fill ? m_Color : getActualColor(i, m_Color));
      g2d.drawOval(posX-(int)(.5*m_Size), posY-(int)(.5*m_Size), m_Size, m_Size);
    }
  }
}