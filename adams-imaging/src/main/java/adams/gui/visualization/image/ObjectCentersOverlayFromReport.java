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
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image;

import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.gui.visualization.image.ImagePanel.PaintPanel;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Displays the centers of objects in the image, using data from the attached report.<br>
 * Suffixes:<br>
 * .x<br>
 * .y<br>
 * .width<br>
 * .height<br>
 * Optionally, if type information is available per object, the locations can be displayed in distinct colors per type. The type itself can be displayed as well.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-enabled &lt;boolean&gt; (property: enabled)
 * &nbsp;&nbsp;&nbsp;If enabled, this overlay is painted over the image.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 *
 * <pre>-prefix &lt;java.lang.String&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The prefix of fields in the report to identify as object location, eg 'Object.
 * &nbsp;&nbsp;&nbsp;'.
 * &nbsp;&nbsp;&nbsp;default: Object.
 * </pre>
 *
 * <pre>-color &lt;java.awt.Color&gt; (property: color)
 * &nbsp;&nbsp;&nbsp;The color to use for the objects.
 * &nbsp;&nbsp;&nbsp;default: #ff0000
 * </pre>
 *
 * <pre>-use-colors-per-type &lt;boolean&gt; (property: useColorsPerType)
 * &nbsp;&nbsp;&nbsp;If enabled, individual colors per type are used.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-type-color-provider &lt;adams.gui.visualization.core.ColorProvider&gt; (property: typeColorProvider)
 * &nbsp;&nbsp;&nbsp;The color provider to use for the various types.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.core.DefaultColorProvider
 * </pre>
 *
 * <pre>-type-suffix &lt;java.lang.String&gt; (property: typeSuffix)
 * &nbsp;&nbsp;&nbsp;The suffix of fields in the report to identify the type.
 * &nbsp;&nbsp;&nbsp;default: .type
 * </pre>
 *
 * <pre>-label-format &lt;java.lang.String&gt; (property: labelFormat)
 * &nbsp;&nbsp;&nbsp;The label format string to use for the rectangles; '#' for index, '&#64;' for
 * &nbsp;&nbsp;&nbsp;type and '$' for short type (type suffix must be defined for '&#64;' and '$'
 * &nbsp;&nbsp;&nbsp;); for instance: '# &#64;'.
 * &nbsp;&nbsp;&nbsp;default: #
 * </pre>
 *
 * <pre>-label-font &lt;java.awt.Font&gt; (property: labelFont)
 * &nbsp;&nbsp;&nbsp;The font to use for the labels.
 * &nbsp;&nbsp;&nbsp;default: Display-PLAIN-14
 * </pre>
 *
 * <pre>-diameter &lt;int&gt; (property: diameter)
 * &nbsp;&nbsp;&nbsp;The diameter of the circle that is drawn; &lt; 1 to use the rectangle's dimensions.
 * &nbsp;&nbsp;&nbsp;default: 10
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 198 $
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
  protected void doPaintObjects(PaintPanel panel, Graphics g, List<Rectangle> locations) {
    String	label;

    g.setColor(getColor());
    g.setFont(getLabelFont());
    for (Rectangle rect: locations) {
      if (getUseColorsPerType()) {
        if (m_Overlays.hasColor(rect))
          g.setColor(m_Overlays.getColor(rect));
      }

      if (m_Diameter < 1)
	g.fillOval((int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight());
      else
        g.fillOval((int) (rect.getCenterX() - m_Diameter), (int) (rect.getCenterY() - m_Diameter), m_Diameter*2, m_Diameter*2);

      if (m_Overlays.hasLabel(rect)) {
        label = m_Overlays.getLabel(rect);
        if (label != null)
          g.drawString(label, (int) (rect.getX() + rect.getWidth()), (int) rect.getY());
      }
    }
  }
}
