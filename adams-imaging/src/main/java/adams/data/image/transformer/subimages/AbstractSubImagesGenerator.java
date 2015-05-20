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

/**
 * AbstractSubImagesGenerator.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.image.transformer.subimages;

import java.util.List;

import adams.core.option.AbstractOptionHandler;
import adams.data.image.BufferedImageContainer;

/**
 * Ancestor for classes that generate subimages from a single image.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractSubImagesGenerator
  extends AbstractOptionHandler {

  /** for serialization. */
  private static final long serialVersionUID = 2258244755943306047L;

  /**
   * Checks whether the image can be processed.
   * <br><br>
   * Default implementation only ensures that an image is present.
   * 
   * @param image	the image to check
   */
  protected void check(BufferedImageContainer image) {
    if (image == null)
      throw new IllegalArgumentException("No image provided!");
  }
  
  /**
   * Performs the actual generation of the subimages.
   * 
   * @param image	the image to process
   * @return		the list of subimages generated
   */
  protected abstract List<BufferedImageContainer> doProcess(BufferedImageContainer image);
  
  /**
   * Generates subimages from the provided image.
   * 
   * @param image	the image to process
   * @return		the list of subimages generated
   */
  public List<BufferedImageContainer> process(BufferedImageContainer image) {
    check(image);
    return doProcess(image);
  }
}
