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
 * Center.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.object.objectannotations.shape;

import adams.flow.transformer.locateobjects.LocatedObject;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * Plots a circle in the center of the object.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Center
  extends AbstractShapePlotter {

  private static final long serialVersionUID = 5516830542182177734L;

  /** the diameter of the circle. */
  protected int m_Diameter;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Plots a circle in the center of the object.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
        "diameter", "diameter",
        10, -1, null);
  }

  /**
   * Sets the diameter to use for drawing the circle
   * (if < 1 to draw an ellipse using the rectangle's dimensions).
   *
   * @param value 	the diameter, < 1 if using the rectangle's dimensions
   */
  public void setDiameter(int value) {
    if (getOptionManager().isValid("diameter", value)) {
      m_Diameter = value;
      reset();
    }
  }

  /**
   * Returns the diameter to use for drawing the circle
   * (if < 1 to draw an ellipse using the rectangle's dimensions).
   *
   * @return 		the diameter, < 1 if using the rectangle's dimensions
   */
  public int getDiameter() {
    return m_Diameter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String diameterTipText() {
    return "The diameter of the circle that is drawn; < 1 to use the rectangle's dimensions to draw an ellipse.";
  }

  /**
   * Plots the outline.
   *
   * @param object the object to plot
   * @param color  the color to use
   * @param g      the graphics context
   */
  @Override
  protected void doPlotShape(LocatedObject object, Color color, Graphics2D g) {
    Rectangle rect;

    rect = object.getRectangle();
    g.setColor(color);
    if (m_Diameter < 1)
      g.fillOval((int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight());
    else
      g.fillOval((int) (rect.getCenterX() - m_Diameter), (int) (rect.getCenterY() - m_Diameter), m_Diameter*2, m_Diameter*2);
  }
}
