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
 * AbstractWhiteBalanceAlgorithm.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.image.transformer.whitebalance;

import java.awt.image.BufferedImage;

import adams.core.option.AbstractOptionHandler;

/**
 * Ancestor to white balance algorithms.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 6675 $
 */
public abstract class AbstractWhiteBalanceAlgorithm
  extends AbstractOptionHandler {

  /** for serialization. */
  private static final long serialVersionUID = 776508705083560079L;

  /**
   * Checks whether the image can be processed.
   * <br><br>
   * Default implementation only checks whether an image was supplied.
   * 
   * @param img				the image to check
   * @throws IllegalStateException 	if image fails check
   */
  protected void check(BufferedImage img) {
    if (img == null)
      throw new IllegalStateException("No image supplied!");
  }

  /**
   * Performs the actual white balancing.
   * 
   * @param img		the image to process
   * @return		the processed image
   */
  protected abstract BufferedImage doBalance(BufferedImage img);
  
  /**
   * Performs white balance on the image.
   * 
   * @param img		the image to process
   * @return		the processed image
   */
  public BufferedImage balance(BufferedImage img) {
    check(img);
    return doBalance(img);
  }
}
