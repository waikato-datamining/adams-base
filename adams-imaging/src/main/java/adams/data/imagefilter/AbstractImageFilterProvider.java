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
 * AbstractImageFilterProvider.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.imagefilter;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageFilter;

import adams.core.option.AbstractOptionHandler;

/**
 * Ancestor for providers of {@link ImageFilter} objects.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractImageFilterProvider
  extends AbstractOptionHandler {

  /** for serialization. */
  private static final long serialVersionUID = 2092237222859238898L;

  /**
   * Turns an {@link Image} into a {@link BufferedImage}.
   * 
   * @param img		the image to convert
   * @return		the buffered image
   */
  public static BufferedImage imageToBufferedImage(Image image, int type) {
    BufferedImage 	result;
    Graphics2D 		g2;
    
    result = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
    g2     = result.createGraphics();  
    g2.drawImage(image, 0, 0, null);  
    g2.dispose(); 
    
    return result;  
  }  
  
  /**
   * Hook method for performing checks.
   * <br><br>
   * Default implementation does nothing.
   */
  protected void check() {
  }
  
  /**
   * Generates the actor {@link ImageFilter} instance.
   * 
   * @param img		the buffered image to filter
   * @return		the image filter instance
   */
  protected abstract ImageFilter doGenerate(BufferedImage img);
  
  /**
   * Returns the {@link ImageFilter} to use.
   * 
   * @param img		the buffered image to filter
   * @return		the image filter
   */
  public ImageFilter generate(BufferedImage img) {
    check();
    return doGenerate(img);
  }
}
