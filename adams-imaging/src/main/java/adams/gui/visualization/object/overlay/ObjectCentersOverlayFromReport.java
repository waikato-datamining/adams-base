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
 * ObjectCentersOverlayFromReport.java
 * Copyright (C) 2017-2020 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.object.overlay;

import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.gui.visualization.object.ObjectAnnotationPanel;

import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.List;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ObjectCentersOverlayFromReport
  extends AbstractObjectOverlayFromReport {

  /** for serialization. */
  private static final long serialVersionUID = 6356419097401574024L;

  /** the diameter of the circle. */
  protected int m_Diameter;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Displays the centers of objects in the image, using data from the "
        + "attached report.\n"
        + "Suffixes:\n"
        + LocatedObjects.KEY_X + "\n"
        + LocatedObjects.KEY_Y + "\n"
        + LocatedObjects.KEY_WIDTH + "\n"
        + LocatedObjects.KEY_HEIGHT + "\n"
        + "Optionally, if type information is available per object, the locations "
        + "can be displayed in distinct colors per type. The type itself can be "
        + "displayed as well.";
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
   * Performs the actual painting of the objects.
   *
   * @param panel	the panel this overlay is for
   * @param g		the graphics context
   * @param locations	the locations to paint
   */
  @Override
  protected void doPaintObjects(ObjectAnnotationPanel panel, Graphics g, List<Polygon> locations) {
    String	label;
    Rectangle	rect;

    g.setColor(getColor());
    g.setFont(getLabelFont());
    for (Polygon poly : locations) {
      if (poly == null)
        continue;
      if (getUseColorsPerType()) {
        if (m_Overlays.hasColor(poly))
          g.setColor(m_Overlays.getColor(poly));
      }

      rect = poly.getBounds();
      if (m_Diameter < 1)
	g.fillOval((int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight());
      else
        g.fillOval((int) (rect.getCenterX() - m_Diameter), (int) (rect.getCenterY() - m_Diameter), m_Diameter*2, m_Diameter*2);

      if (m_Overlays.hasLabel(poly)) {
        label = m_Overlays.getLabel(poly);
        if (label != null)
          g.drawString(label, (int) (rect.getX() + rect.getWidth() + getLabelOffsetX()), (int) (rect.getY() + getLabelOffsetY()));
      }
    }
  }
}
