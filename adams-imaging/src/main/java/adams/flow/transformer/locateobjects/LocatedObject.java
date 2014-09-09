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
 * LocatedObject.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.locateobjects;

import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 * Container for located objects.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 78 $
 */
public class LocatedObject
  implements Serializable {

  /** for serialization. */
  private static final long serialVersionUID = 8662599273386642371L;

  /** the cut-out object. */
  protected BufferedImage m_Image;
  
  /** the x of the top-left corner in the original image. */
  protected int m_X;
  
  /** the y of the top-left corner in the original image. */
  protected int m_Y;
  
  /** the width of the actual object sub-image. */
  protected int m_Width;
  
  /** the height of the actual object sub-image. */
  protected int m_Height;
  
  /**
   * Initializes the container.
   * 
   * @param image	the object image
   * @param x		the x of the top-left corner in the original image
   * @param y		the y of the top-left corner in the original image
   * @param width	the width of the object sub-image
   * @param height	the height of the object sub-image
   */
  public LocatedObject(BufferedImage image, int x, int y, int width, int height) {
    m_Image    = image;
    m_X      = x;
    m_Y      = y;
    m_Width  = width;
    m_Height = height;
  }
  
  /**
   * Returns the image.
   * 
   * @return		the image
   */
  public BufferedImage getImage() {
    return m_Image;
  }
  
  /**
   * Returns the X of the top-left corner.
   * 
   * @return		the X
   */
  public int getX() {
    return m_X;
  }
  
  /**
   * Returns the Y of the top-left corner.
   * 
   * @return		the Y
   */
  public int getY() {
    return m_Y;
  }
  
  /**
   * Returns the width of the object sub-image.
   * 
   * @return		the width
   */
  public int getWidth() {
    return m_Width;
  }
  
  /**
   * Returns the height of the object sub-image.
   * 
   * @return		the height
   */
  public int getHeight() {
    return m_Height;
  }
  
  /**
   * Returns a short decription of the container.
   * 
   * @return		the description
   */
  @Override
  public String toString() {
    return "@" + m_Image.hashCode() + ", x=" + m_X + ", y=" + m_Y + ", w=" + m_Width + ", h=" + m_Height;
  }
}
