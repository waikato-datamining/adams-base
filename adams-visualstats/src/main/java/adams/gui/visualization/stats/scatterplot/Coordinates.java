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
 * Coordinates.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.scatterplot;

import adams.gui.visualization.stats.paintlet.CoordinatesPaintlet;

import java.awt.Color;

/**
 <!-- globalinfo-start -->
 * Display a diagonal line overlay
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-line-thickness &lt;float&gt; (property: thickness)
 * &nbsp;&nbsp;&nbsp;Thickness of the overlay line
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 * 
 * <pre>-color &lt;java.awt.Color&gt; (property: color)
 * &nbsp;&nbsp;&nbsp;Color of the overlay line
 * &nbsp;&nbsp;&nbsp;default: #808080
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Coordinates
  extends AbstractScatterPlotOverlay {

  /** for serialization */
  private static final long serialVersionUID = -1796683889341452636L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Display a diagonal line overlay";
  }

  /**
   * Returns the default thickness.
   *
   * @return		the default
   */
  @Override
  protected float getDefaultThickness() {
    return 1.0f;
  }

  /**
   * Returns the default color.
   *
   * @return		the default
   */
  @Override
  protected Color getDefaultColor() {
    return Color.GRAY;
  }

  /**
   * set up the overlay and its paintlet.
   */
  public void setUp() {
    m_Paintlet = new CoordinatesPaintlet();
    m_Paintlet.parameters(m_Data, m_Parent.getX_Index(), m_Parent.getY_Index());
    m_Paintlet.setRepaintOnChange(true);
    m_Paintlet.setStrokeThickness(m_Thickness);
    m_Paintlet.setColor(m_Color);
    m_Paintlet.setPanel(m_Parent);
    m_Paintlet.calculate();
  }
}
