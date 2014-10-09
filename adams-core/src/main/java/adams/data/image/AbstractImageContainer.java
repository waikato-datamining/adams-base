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
 * AbstractImageContainer.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.image;

import java.awt.image.BufferedImage;

import adams.data.container.AbstractSimpleContainer;

/**
 * Ancestor for various image format containers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of image to handle
 */
public abstract class AbstractImageContainer<T>
  extends AbstractSimpleContainer<T> {

  /** for serialization. */
  private static final long serialVersionUID = 2095394708673239275L;
  
  /** the field for the filename. */
  public final static String FIELD_FILENAME = "Filename";

  /**
   * Sets the image to use.
   *
   * @param value	the image
   */
  public void setImage(T value) {
    setContent(value);
  }

  /**
   * Returns the store image.
   *
   * @return		the image
   */
  public T getImage() {
    return getContent();
  }

  /**
   * Returns the width of the image.
   * 
   * @return		the width
   */
  public abstract int getWidth();

  /**
   * Returns the height of the image.
   * 
   * @return		the height
   */
  public abstract int getHeight();

  /**
   * Turns the image into a buffered image.
   * 
   * @return		the buffered image
   */
  public abstract BufferedImage toBufferedImage();
}
