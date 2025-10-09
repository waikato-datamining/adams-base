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
 * ADAMS.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.watermark;

import adams.gui.core.ImageManager;

import java.awt.Image;

/**
 * Uses the following resource as watermark: {@link #RESOURCE}
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class ADAMS
  extends AbstractImageWatermark {

  private static final long serialVersionUID = 7965347630131076047L;

  public final static String RESOURCE = "adams/gui/images/adams_logo.png";

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses the following resource as watermark:\n" + RESOURCE;
  }

  /**
   * Returns the default scale factor to use.
   *
   * @return		the default
   */
  @Override
  protected double getDefaultScale() {
    return 0.5;
  }

  /**
   * Loads the image.
   *
   * @return the image, null if failed to load
   */
  @Override
  protected Image loadImage() {
    return ImageManager.getExternalImage(RESOURCE);
  }
}
