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
 * Copyright (C) 2011-2024 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.paintlet;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 <!-- globalinfo-start -->
 * Paintlet that draws data on the scatter plot as crosses whose size depends on the error between x and y values. Mainly useful for plotting predicted vs actual.
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
 <!-- options-end -->
 *
 * @author msf8
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
    int 	size;
    Graphics2D 	g2d;
    int 	i;
    int 	posX;
    int 	posY;
    double[]	x;
    double[]	y;

    super.drawData(g);

    x = m_XData;
    y = m_YData;
    if ((x == null) || (y == null))
      return;

    // need to convert value to pos using axis class
    g2d  = (Graphics2D) g;
    for (i = 0; i< x.length; i++) {
      //calculate size
      size = (int)((10*(x[i] - y[i]))/(x[i]));
      if (size > 10)
	size = 10;
      if (size <2)
	size = 2;

      posX = m_AxisBottom.valueToPos(x[i]);
      posY = m_AxisLeft.valueToPos(y[i]);
      //plot the points
      g2d.setColor(getActualColor(i, m_Color));
      g2d.setStroke(new BasicStroke(m_StrokeThickness));
      g2d.drawLine(posX - size, posY - size, posX + size, posY + size);
      g2d.drawLine(posX - size, posY + size, posX + size, posY - size);
    }
  }
}