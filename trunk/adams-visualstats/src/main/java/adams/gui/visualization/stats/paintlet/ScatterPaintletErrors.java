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
 * ScatterPaintletErrors.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.paintlet;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 <!-- globalinfo-start -->
 * Paintlet that draws data on the scatter plot as crosses whose size depends on the error between x and y values. Mainly useful for plotting predicted vs actual.
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
 * <pre>-color &lt;java.awt.Color&gt; (property: color)
 * &nbsp;&nbsp;&nbsp;Stroke color for the paintlet
 * &nbsp;&nbsp;&nbsp;default: #000000
 * </pre>
 *
 <!-- options-end -->
 *
 * @author msf8
 * @version $Revision$
 */
public class ScatterPaintletErrors
extends AbstractScatterPlotPaintlet {

  /** for serialization */
  private static final long serialVersionUID = -8859664992076524292L;

  public String globalInfo() {
    return
    "Paintlet that draws data on the scatter plot as crosses whose size "
    + "depends on the error between x and y values. Mainly useful for "
    + "plotting predicted vs actual.";
  }

  protected void drawData(Graphics g) {
    super.drawData(g);
    int size = m_Size;
    //need to convert value to pos using axis class
    for(int i = 0; i< m_XData.length; i++)
    {
      //calculate size
      size = (int)((10*(m_XData[i] - m_YData[i]))/(m_XData[i]));
      if(size >10)
	size = 10;
      if(size <2)
	size = 2;

      int posX = m_AxisBottom.valueToPos(m_XData[i]);
      int posY = m_AxisLeft.valueToPos(m_YData[i]);
      //plot the points
      Graphics2D g2d = (Graphics2D)g;
      g.setColor(m_Color);
      g2d.setStroke(new BasicStroke(m_StrokeThickness));
      g2d.drawLine(posX-size, posY-size, posX+size, posY+size);
      g2d.drawLine(posX - size, posY + size, posX + size, posY - size);
    }
  }
}