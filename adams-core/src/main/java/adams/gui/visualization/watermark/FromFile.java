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
 * FromFile.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.watermark;

import adams.core.io.PlaceholderFile;
import adams.data.image.BufferedImageContainer;
import adams.data.image.BufferedImageHelper;

import java.awt.Image;

/**
 * Uses the specified image file as watermark.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class FromFile
  extends AbstractImageWatermark {

  private static final long serialVersionUID = 5408359726087314133L;

  /** the image file. */
  protected PlaceholderFile m_ImageFile;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses the specified image file as watermark.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "image-file", "imageFile",
      new PlaceholderFile());
  }

  /**
   * Sets the image to load, ignored if pointing to a directory.
   *
   * @param value	the file
   */
  public void setImageFile(PlaceholderFile value) {
    m_ImageFile = value;
    reset();
  }

  /**
   * Returns the image to load, ignored if pointing to a directory.
   *
   * @return		the file
   */
  public PlaceholderFile getImageFile() {
    return m_ImageFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String imageFileTipText() {
    return "The image to load, ignored if pointing to a directory.";
  }

  /**
   * Loads the image.
   *
   * @return		the image, null if failed to load
   */
  @Override
  protected Image loadImage() {
    BufferedImageContainer	cont;

    if (!m_ImageFile.exists() || m_ImageFile.isDirectory())
      return null;

    cont = BufferedImageHelper.read(m_ImageFile);
    if (cont != null)
      return cont.toBufferedImage();

    return null;
  }
}
