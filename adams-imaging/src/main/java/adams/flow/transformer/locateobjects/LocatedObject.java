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
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.locateobjects;

import adams.core.base.QuadrilateralLocation;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

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

  /** the cut-out object, if available. */
  protected BufferedImage m_Image;
  
  /** the x of the top-left corner in the original image. */
  protected int m_X;
  
  /** the y of the top-left corner in the original image. */
  protected int m_Y;
  
  /** the width of the actual object sub-image. */
  protected int m_Width;
  
  /** the height of the actual object sub-image. */
  protected int m_Height;

  /** additional meta-data. */
  protected Map<String,Object> m_MetaData;

  /**
   * Initializes the container.
   *
   * @param image	the object image, can be null
   * @param x		the x of the top-left corner in the original image
   * @param y		the y of the top-left corner in the original image
   * @param width	the width of the object sub-image
   * @param height	the height of the object sub-image
   */
  public LocatedObject(BufferedImage image, int x, int y, int width, int height) {
    this(image, x, y, width, height, null);
  }

  /**
   * Initializes the container.
   * 
   * @param image	the object image, can be null
   * @param x		the x of the top-left corner in the original image
   * @param y		the y of the top-left corner in the original image
   * @param width	the width of the object sub-image
   * @param height	the height of the object sub-image
   * @param metaData	optional meta-data, can be null
   */
  public LocatedObject(BufferedImage image, int x, int y, int width, int height, Map<String,Object> metaData) {
    m_Image    = image;
    m_X        = x;
    m_Y        = y;
    m_Width    = width;
    m_Height   = height;
    m_MetaData = (metaData == null) ? new HashMap<>() : metaData;
  }
  
  /**
   * Returns the image.
   * 
   * @return		the image, null if not available
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
   * Returns the meta-data of the object, if any.
   *
   * @return		the meta-data
   */
  public Map<String,Object> getMetaData() {
    return m_MetaData;
  }

  /**
   * Returns the quadrilateral location.
   *
   * @return 		the location
   */
  public QuadrilateralLocation getLocation() {
    return new QuadrilateralLocation(
      m_X, m_Y,
      m_X + m_Width - 1, m_Y,
      m_X + m_Width - 1, m_Y + m_Height - 1,
      m_X, m_Y + m_Height - 1
    );
  }

  /**
   * Returns the object as rectangle.
   *
   * @return		the rectangle
   */
  public Rectangle getRectangle() {
    return getRectangle(1.0);
  }

  /**
   * Returns the object as rectangle.
   *
   * @param scale	the scale factor, 1.0 for 100%
   * @return		the rectangle
   */
  public Rectangle getRectangle(double scale) {
    return new Rectangle(
      (int) (getX() * scale),
      (int) (getY() * scale),
      (int) (getWidth() * scale),
      (int) (getHeight() * scale));
  }

  /**
   * Checks whether a value is within the range (allowed to be on borders).
   *
   * @param value	the value to check
   * @param min		the minimum
   * @param max		the maximum
   * @return		true if in range
   */
  protected boolean inRange(int value, int min, int max) {
    return (value >= min) && (value <= max);
  }

  /**
   * Returns whether the this and the other object overlap.
   *
   * @param other	the object object to use
   * @return		true if they overlap
   */
  public boolean overlap(LocatedObject other) {
    int		thisLeft;
    int		thisRight;
    int		thisTop;
    int		thisBottom;
    int		otherLeft;
    int		otherRight;
    int		otherTop;
    int		otherBottom;
    boolean	xOverlap;
    boolean	yOverlap;

    thisLeft    = this.getX();
    thisRight   = this.getX() + this.getWidth() - 1;
    thisTop     = this.getY();
    thisBottom  = this.getY() + this.getHeight() - 1;
    otherLeft   = other.getX();
    otherRight  = other.getX() + other.getWidth() - 1;
    otherTop    = other.getY();
    otherBottom = other.getY() + other.getHeight() - 1;

    xOverlap = inRange(thisLeft, otherLeft, otherRight)
      || inRange(otherLeft, thisLeft, thisRight);
    yOverlap = inRange(thisTop, otherTop, otherBottom)
      || inRange(otherTop, thisTop, thisBottom);

    return xOverlap && yOverlap;
  }

  /**
   * Returns the overlapping rectangle.
   *
   * @param other	the object object to use
   * @return		rectangle if they overlap, otherwise null
   * @see		#overlap(LocatedObject)
   */
  public Rectangle overlapRectangle(LocatedObject other) {
    if (!overlap(other))
      return null;
    else
      return getRectangle().intersection(other.getRectangle());
  }

  /**
   * Returns the overlap ratio (1 = full overlap, 0 = no overlap).
   *
   * @param other	the object object to use
   * @return		rectangle if they overlap, otherwise null
   * @see		#overlap(LocatedObject)
   */
  public double overlapRatio(LocatedObject other) {
    Rectangle 	overlap;

    overlap = overlapRectangle(other);
    if (overlap == null)
      return 0.0;
    else
      return (overlap.getWidth() * overlap.getHeight()) / (getWidth() * getHeight());
  }

  /**
   * Returns a short description of the container.
   * 
   * @return		the description
   */
  @Override
  public String toString() {
    if (m_Image != null)
      return "@" + m_Image.hashCode() + ", x=" + m_X + ", y=" + m_Y + ", w=" + m_Width + ", h=" + m_Height;
    else
      return "x=" + m_X + ", y=" + m_Y + ", w=" + m_Width + ", h=" + m_Height;
  }
}
