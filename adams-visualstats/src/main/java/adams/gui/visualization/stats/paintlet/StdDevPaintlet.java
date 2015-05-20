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
 * StdDevPaintlet.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.paintlet;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;

import adams.data.statistics.StatUtils;

/**
 <!-- globalinfo-start -->
 * paintlet for displaying stddev overlay on the Z score graph
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
public class StdDevPaintlet
extends AbstractZOverlayPaintlet{

  /** for serialization */
  private static final long serialVersionUID = -2890647906878535305L;

  /**number of standard deviations*/
  protected double m_stdDevNum;

  /**mean of data set*/
  double m_mean;

  /**standard deviation of dataset */
  double m_stddev;

  protected void drawData(Graphics g) {
    if(m_Calculated) {
      g.setColor(m_Color);
      Graphics2D g2d = (Graphics2D)g;
      g2d.setStroke(new BasicStroke(m_StrokeThickness));
      g2d.drawLine(0, m_AxisLeft.valueToPos(m_mean + (m_stdDevNum *m_stddev)), m_AxisBottom.valueToPos(m_AxisBottom.getMaximum()), m_AxisLeft.valueToPos(m_mean + (m_stdDevNum *m_stddev)));
      g2d.drawLine(0, m_AxisLeft.valueToPos(m_mean - (m_stdDevNum *m_stddev)), m_AxisBottom.valueToPos(m_AxisBottom.getMaximum()), m_AxisLeft.valueToPos(m_mean - (m_stdDevNum *m_stddev)));
    }
  }

  public String globalInfo() {
    return "paintlet for displaying stddev overlay on the Z score graph";
  }

  /**
   * Set the number of standard deviations for this overlay
   * @param val		double number of standard deviations
   */
  public void setStd(double val) {
    m_stdDevNum = val;
    memberChanged();
  }

  public void calculate() {
    super.calculate();

    double[] data = m_Instances.attributeToDoubleArray(m_Ind);
    m_mean = StatUtils.mean(data);
    m_stddev = StatUtils.stddev(data, false);
    m_Calculated = true;
  }
}