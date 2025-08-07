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
 * AbstractImageLayer.java
 * Copyright (C) 2020-2025 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.segmentation.layer;

import adams.data.image.BufferedImageHelper;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Layers that manage an image.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractImageLayer
  extends AbstractLayer {

  private static final long serialVersionUID = 2430218535175155529L;

  /** the underlying image. */
  protected BufferedImage m_Image;

  /**
   * For storing the state of an image layer.
   */
  public static class AbstractImageLayerState
    extends AbstractLayerState {

    private static final long serialVersionUID = -5652014216527524598L;

    /** the image. */
    protected BufferedImage image;
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Image = null;
  }

  /**
   * Sets the name of the layer.
   *
   * @param value	the name
   */
  public abstract void setName(String value);

  /**
   * Sets the image to display.
   *
   * @param value	the image, null to clear
   */
  public void setImage(BufferedImage value) {
    m_Image = value;
  }

  /**
   * Returns the underlying image.
   *
   * @return		the image, null if none set
   */
  public BufferedImage getImage() {
    return m_Image;
  }

  /**
   * Performs the drawing.
   *
   * @param g2d		the graphics context
   */
  public void draw(Graphics2D g2d) {
    if (isEnabled() && (m_Image != null))
      doDraw(g2d);
  }

  /**
   * Rotates the images by the specified number of degrees.
   * Caller must call the update() method.
   *
   * @param degrees	the rotation
   */
  public void rotate(int degrees) {
    m_Image = BufferedImageHelper.rotate(m_Image, degrees, new Color(0, 0, 0, 0));
  }
}
