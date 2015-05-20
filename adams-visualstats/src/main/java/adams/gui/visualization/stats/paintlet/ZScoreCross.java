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
 * ZScoreCross.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.paintlet;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;

import adams.data.statistics.StatUtils;

/**
 <!-- globalinfo-start -->
 * paintlet for plotting cross error points on the z score graph whose size depends on the difference between x and y values
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
 * @version $Revision$
 */
public class ZScoreCross
extends AbstractZScorePaintlet{

  /** for serialization */
  private static final long serialVersionUID = 6710459160837483521L;

  public String globalInfo() {
    return "paintlet for plotting cross error points on the z score graph whose size depends on the difference between x and y values";
  }

  protected void drawData(Graphics g) {
    super.drawData(g);

    int posX;
    int posY;

    int crossSize;
    int halfCross;
    double mean = StatUtils.mean(m_Data);
    double max = Math.max((StatUtils.max(m_Data) -mean), (mean -StatUtils.min(m_Data)));

    for(int i = 0; i< m_Data.length; i++) {
      posX = m_AxisBottom.valueToPos(i);
      posY = m_AxisLeft.valueToPos(m_Data[i]);
      crossSize = (int)(((m_Data[i] - mean)/max)* 20);
      halfCross = (int)(crossSize/2);
      g.setColor(m_Color);
      Graphics2D g2d = (Graphics2D)g;
      g2d.setStroke(new BasicStroke(m_StrokeThickness));
      g2d.drawLine(posX-halfCross, posY-halfCross, posX+halfCross, posY+ halfCross);
      g2d.drawLine(posX-halfCross, posY+halfCross, posX+ halfCross, posY-halfCross);

      if(i+1 < m_Data.length) {
	int posX2 = m_AxisBottom.valueToPos(i+1);
	int posY2 = m_AxisLeft.valueToPos(m_Data[i+1]);
	g.setColor(m_LineColor);
	g.drawLine(posX, posY, posX2, posY2);
      }
    }
  }
}