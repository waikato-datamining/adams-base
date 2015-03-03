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
 * Diagonal.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.scatterplot;

import adams.gui.visualization.stats.paintlet.DiagonalPaintlet;

/**
 <!-- globalinfo-start -->
 * Display a diagonal line overlay
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
 * <pre>-line-thickness &lt;float&gt; (property: thickness)
 * &nbsp;&nbsp;&nbsp;Thickness of the overlay line
 * &nbsp;&nbsp;&nbsp;default: 2.0
 * &nbsp;&nbsp;&nbsp;minimum: 1.0
 * &nbsp;&nbsp;&nbsp;maximum: 5.0
 * </pre>
 *
 * <pre>-color &lt;java.awt.Color&gt; (property: color)
 * &nbsp;&nbsp;&nbsp;Color of the overlay line
 * &nbsp;&nbsp;&nbsp;default: #0000ff
 * </pre>
 *
 * <pre>-indicator (property: indicator)
 * &nbsp;&nbsp;&nbsp;Display indicator lines on the axis to show what side diagonal on
 * </pre>
 *
 <!-- options-end -->
 *
 * @author msf8
 * @version $Revision$
 */
public class Diagonal
extends AbstractScatterPlotOverlay {
  /** Whether an indicator should be displayed on the side of the plot showing where
   * the diagonal is and at what gradient
   */
  protected boolean m_Indicator;

  /** for serialization */
  private static final long serialVersionUID = -1796683889341452636L;

  public String globalInfo() {
    return "Display a diagonal line overlay";
  }

  public void setUp() {
    m_Paintlet = new DiagonalPaintlet();
    m_Paintlet.parameters(m_Instances, m_Parent.getX_Index(), m_Parent.getY_Index());
    m_Paintlet.setRepaintOnChange(true);
    m_Paintlet.setStrokeThickness(m_Thickness);
    m_Paintlet.setIndicator(m_Indicator);
    m_Paintlet.setColor(m_Color);
    m_Paintlet.setPanel(m_Parent);
    m_Paintlet.calculate();
  }

  public void defineOptions() {
    super.defineOptions();
    //Whether indicator should be displayed
    m_OptionManager.add(
	"indicator", "indicator", true);
  }

  /**
   * Set whether an indicator should be displayed
   * @param val			True if indicator displayed
   */
  public void setIndicator(boolean val) {
    m_Indicator = val;
  }

  /**
   * get whether an indicator should be displayed
   * @return			True if indicator displayed
   */
  public boolean getIndicator() {
    return m_Indicator;
  }

  /**
   * Tip text for the indicator property
   * @return			String describing the property
   */
  public String indicatorTipText() {
    return "Display indicator lines on the axis to show what side diagonal on";
  }
}