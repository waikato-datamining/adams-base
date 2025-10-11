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
 * Watermark.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer.draw;

import adams.data.image.BufferedImageContainer;
import adams.gui.visualization.watermark.Default;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Overlays a watermark.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Watermark
  extends AbstractDrawOperation {

  private static final long serialVersionUID = 7694610389655416953L;

  /** the watermark to apply. */
  protected adams.gui.visualization.watermark.Watermark m_Watermark;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Overlays a watermark.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "watermark", "watermark",
      new Default());
  }

  /**
   * Sets the watermark to apply.
   *
   * @param value	the watermark
   */
  public void setWatermark(adams.gui.visualization.watermark.Watermark value) {
    m_Watermark = value;
    reset();
  }

  /**
   * Returns the watermark to apply.
   *
   * @return		the watermark
   */
  public adams.gui.visualization.watermark.Watermark getWatermark() {
    return m_Watermark;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String watermarkTipText() {
    return "The watermark scheme to apply.";
  }

  /**
   * Performs the actual draw operation.
   *
   * @param image the image to draw on
   * @return null if OK, otherwise error message
   */
  @Override
  protected String doDraw(BufferedImageContainer image) {
    Graphics2D 		g;
    BufferedImage	img;

    img = image.getImage();
    g   = img.createGraphics();
    m_Watermark.applyWatermark(g, new Dimension(img.getWidth(), img.getHeight()));
    g.dispose();

    return null;
  }
}
