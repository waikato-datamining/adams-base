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
 * Mean.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.zscore;

import adams.gui.visualization.stats.paintlet.MeanPaintlet;

/**
 <!-- globalinfo-start -->
 * Plot a mean line as an overlay on the z score graph
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
 <!-- options-end -->
 *
 * @author msf8
 * @version $Revision$
 *
 */
public class Mean
extends AbstractZScoreOverlay{

  /** for serialization */
  private static final long serialVersionUID = 3285245400372222299L;

  public void setUp() {
    m_Paintlet = new MeanPaintlet();
    m_Paintlet.parameters(m_Instances, m_Parent.getIndex());
    m_Paintlet.setRepaintOnChange(true);
    m_Paintlet.setStrokeThickness(m_Thickness);
    m_Paintlet.setColor(m_Color);
    m_Paintlet.setPanel(m_Parent);
  }

  public String globalInfo() {
    return "Plot a mean line as an overlay on the z score graph";
  }

  public String shortName() {
    return "---- mean";
  }
}