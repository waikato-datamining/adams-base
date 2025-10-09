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
 * FromResource.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.watermark;

import adams.gui.core.ImageManager;

import java.awt.Image;

/**
 * Uses the specified image (available from the classpath) as watermark.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class FromResource
  extends AbstractImageWatermark {

  private static final long serialVersionUID = 5408359726087314133L;

  /** the image resource. */
  protected String m_ImageResource;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses the specified image (available from the classpath) as watermark.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "image-resource", "imageResource",
      "");
  }

  /**
   * Sets the classpath resource to load the image from, ignored if empty.
   *
   * @param value	the resource path
   */
  public void setImageResource(String value) {
    m_ImageResource = value;
    reset();
  }

  /**
   * Returns the classpath resource to load the image from, ignored if empty.
   *
   * @return		the resource path
   */
  public String getImageResource() {
    return m_ImageResource;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String imageResourceTipText() {
    return "The classpath resource to load the image from, ignored if empty.";
  }

  /**
   * Loads the image.
   *
   * @return		the image, null if failed to load
   */
  @Override
  protected Image loadImage() {
    if (m_ImageResource.isEmpty())
      return null;

    return ImageManager.getExternalImage(m_ImageResource);
  }
}
