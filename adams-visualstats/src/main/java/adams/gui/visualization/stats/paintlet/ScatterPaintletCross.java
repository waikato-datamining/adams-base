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
 * ScatterPaintletCross.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.paintlet;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 <!-- globalinfo-start -->
 * Class for plotting data on a scatter plot as crosses.
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
 <!-- options-end -->
 *
 * @author msf8
 * @version $Revision$
 */
public class ScatterPaintletCross
extends AbstractScatterPlotPaintlet {

  /** For serialization */
  private static final long serialVersionUID = -2415716946174098645L;

  public String globalInfo() {
    return "Class for plotting data on a scatter plot as crosses.";
  }

  public void defineOptions() {
    super.defineOptions();
    //size of cross
    m_OptionManager.add(
	"size", "size",
	5, 1, null);
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
    for(int i = 0; i< m_XData.length; i++) {
      posX = m_AxisBottom.valueToPos(m_XData[i]);
      posY = m_AxisLeft.valueToPos(m_YData[i]);
      //plot the points
      Graphics2D g2d = (Graphics2D)g;
      g.setColor(m_Color);
      g2d.setStroke(new BasicStroke(m_StrokeThickness));
      g2d.drawLine(posX-m_Size, posY-m_Size, posX+m_Size, posY+m_Size);
      g2d.drawLine(posX - m_Size, posY + m_Size, posX + m_Size, posY - m_Size);
    }
  }
}