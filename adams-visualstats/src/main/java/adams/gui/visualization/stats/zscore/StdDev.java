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
 * StdDev.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.zscore;

import adams.gui.visualization.stats.paintlet.StdDevPaintlet;

/**
 <!-- globalinfo-start -->
 * Display a standard deviation line on the z score above and below the mean
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
 * <pre>-color &lt;java.awt.Color&gt; (property: color)
 * &nbsp;&nbsp;&nbsp;Colour to draw the overlay
 * &nbsp;&nbsp;&nbsp;default: #000000
 * </pre>
 *
 * <pre>-line-thickness &lt;float&gt; (property: thickness)
 * &nbsp;&nbsp;&nbsp;Thickness of the overlay line
 * &nbsp;&nbsp;&nbsp;default: 2.0
 * &nbsp;&nbsp;&nbsp;minimum: 1.0
 * &nbsp;&nbsp;&nbsp;maximum: 5.0
 * </pre>
 *
 * <pre>-standard-deviations &lt;double&gt; (property: standardDeviations)
 * &nbsp;&nbsp;&nbsp;number of standard deviations from mean
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * &nbsp;&nbsp;&nbsp;minimum: 1.0
 * &nbsp;&nbsp;&nbsp;maximum: 5.0
 * </pre>
 *
 <!-- options-end -->
 *
 * @author msf8
 * @version $Revision$
 */
public class StdDev
extends AbstractZScoreOverlay{

  /** for serialization */
  private static final long serialVersionUID = -6223718756093227787L;

  /**Number of standard deviations from the mean */
  protected double m_StdDev;

  public void setUp() {
    m_Paintlet = new StdDevPaintlet();
    m_Paintlet.parameters(m_Data, m_Parent.getIndex());
    m_Paintlet.setRepaintOnChange(true);
    m_Paintlet.setStrokeThickness(m_Thickness);
    m_Paintlet.setStd(m_StdDev);
    m_Paintlet.setColor(m_Color);
    m_Paintlet.setPanel(m_Parent);
  }

  public String globalInfo() {
    return "Display a standard deviation line on the z score above and below the mean";
  }

  public void defineOptions() {
    super.defineOptions();

    //number of standard deviations for overlay
    m_OptionManager.add(
	"standard-deviations", "standardDeviations",
	1.0, 1.0,5.0);
  }

  /**
   * Set the number of standard deviations for the overlay
   * @param val			Number of standard deviations from mean
   */
  public void setStandardDeviations(double val) {
    m_StdDev = val;
  }

  /**
   * get the number of standard deviations for the overlay
   * @return			Number of standard deviations from mean
   */
  public double getStandardDeviations() {
    return m_StdDev;
  }

  /**
   * return a string for the standard deviations option
   * @return			String for the property
   */
  public String standardDeviationsTipText() {
    return "number of standard deviations from mean";
  }

  public String shortName() {
    String toReturn = "----" + m_StdDev + " std";
    return toReturn;
  }
}