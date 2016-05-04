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
 * ZScoreCircle.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.paintlet;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 <!-- globalinfo-start -->
 * paintlet for plotting cirle points on the z score visualisation
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
public class ZScoreCircle
extends AbstractZScorePaintlet{

  /** for serialization */
  private static final long serialVersionUID = -2909051757826954366L;

  /** size of data point */
  protected int m_Size;

  /** Whether to fill the data points */
  protected boolean m_Fill;

  /**Color for filling data points */
  protected Color m_FillColor;

  public String globalInfo() {
    return "paintlet for plotting cirle points on the z score visualisation";
  }

  public void defineOptions() {
    super.defineOptions();
    //size of points
    m_OptionManager.add(
	"size", "size",
	5, 1, null);
    //Whether to fill points
    m_OptionManager.add(
	"fill-point", "fillPoint", false);

    //color for filling data points
    m_OptionManager.add(
	"fill-color", "fillColor", Color.RED);
  }

  /**
   * Set the color to fill the data points
   * @param val			Color for filling points
   */
  public void setFillColor(Color val) {
    m_FillColor = val;
  }

  /**
   * Get the color for filling the data points
   * @return			Color for filling points
   */
  public Color getFillColor() {
    return m_FillColor;
  }

  /**
   * Tip text for the fill color property
   * @return			String to describe the property
   */
  public String fillColorTipText() {
    return "Color for filling data points";
  }

  /**
   * Set whether the data points are filled
   * @param val			True if points filled
   */
  public void setFillPoint(boolean val) {
    m_Fill = val;
    memberChanged();
  }

  /**
   * get whether the data points are filled
   * @return			True if points filled
   */
  public boolean getFillPoint() {
    return m_Fill;
  }

  /**
   * Tip text for the fill points property
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

  protected void drawData(Graphics g) {
    super.drawData(g);

    int posX;
    int posY;

    for(int i = 0; i< m_Values.length; i++) {
      posX = m_AxisBottom.valueToPos(i);
      posY = m_AxisLeft.valueToPos(m_Values[i]);
      Graphics2D g2d = (Graphics2D)g;
      //if filling points
      if(m_Fill) {
	g2d.setColor(m_FillColor);
	g2d.setStroke(new BasicStroke(0));
	g2d.fillOval(posX -(int)(0.5*m_Size), posY - (int)(0.5*m_Size), m_Size, m_Size);
      }
      //outline of points
      g2d.setStroke(new BasicStroke(m_StrokeThickness));
      g2d.setColor(m_Color);
      g2d.drawOval(posX -(int)(0.5*m_Size), posY - (int)(0.5*m_Size), m_Size, m_Size);
      //line connecting data points
      if(i+1 < m_Values.length) {
	int posX2 = m_AxisBottom.valueToPos(i+1);
	int posY2 = m_AxisLeft.valueToPos(m_Values[i+1]);
	g.setColor(m_LineColor);
	g2d.setStroke(new BasicStroke(1.0f));
	g.drawLine(posX, posY, posX2, posY2);
      }
    }
  }
}