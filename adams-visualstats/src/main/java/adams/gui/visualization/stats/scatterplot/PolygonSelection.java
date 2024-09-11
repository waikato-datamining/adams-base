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
 * PolygonSelection.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.scatterplot;

import adams.gui.visualization.stats.paintlet.ScatterPlotPolygonPaintlet;

import java.awt.Color;

/**
 * Displays the polygon that the user is selecting.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class PolygonSelection
  extends AbstractScatterPlotOverlay {

  private static final long serialVersionUID = 534709318955680421L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Displays the polygon that the user is selecting.";
  }

  /**
   * Returns the default color.
   *
   * @return		the default
   */
  @Override
  protected Color getDefaultColor() {
    return Color.RED;
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
   * set up the overlay and its paintlet.
   */
  @Override
  public void setUp() {
    ScatterPlotPolygonPaintlet	paintlet;

    paintlet = new ScatterPlotPolygonPaintlet();
    paintlet.setColor(m_Color);
    paintlet.setStrokeThickness(m_Thickness);
    paintlet.setPanel(m_Parent);
    m_Parent.setSelectionEnabled(true);

    m_Paintlet = paintlet;
    m_Paintlet.calculate();
  }
}
