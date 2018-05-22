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

import adams.core.CloneHandler;
import adams.core.Utils;
import adams.core.base.QuadrilateralLocation;
import adams.data.image.BufferedImageHelper;
import adams.data.statistics.StatUtils;

import java.awt.Polygon;
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
  implements Serializable, CloneHandler<LocatedObject> {

  /** for serialization. */
  private static final long serialVersionUID = 8662599273386642371L;

  /** the key for the Xs of the polygon in the meta-data (comma-separated list). */
  public final static String KEY_POLY_X = "poly_x";

  /** the key for the Ys of the polygon in the meta-data (comma-separated list). */
  public final static String KEY_POLY_Y = "poly_y";

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

  /** the actual rectangle. */
  protected Rectangle m_Actual;

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
    m_Actual   = null;
  }

  /**
   * Returns a clone of the object.
   *
   * @return		the clone
   */
  public LocatedObject getClone() {
    return new LocatedObject(
      (m_Image != null ? BufferedImageHelper.deepCopy(m_Image) : null),
      m_X,
      m_Y,
      m_Width,
      m_Height,
      getMetaData(true));
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
   * Returns the index string of the object.
   *
   * @return		the index, null if not available
   */
  public String getIndexString() {
    String		result;

    result = null;
    if (getMetaData() != null) {
      if (getMetaData().containsKey(LocatedObjects.KEY_INDEX))
	result = "" + getMetaData().get(LocatedObjects.KEY_INDEX);
    }

    return result;
  }

  /**
   * Returns the index of the object.
   *
   * @return		the index, -1 if not available
   */
  public int getIndex() {
    String	index;
    int		result;

    result = -1;
    index  = getIndexString();
    if (index != null) {
      try {
	result = Integer.parseInt(index);
      }
      catch (Exception e) {
	result = -1;
      }
    }

    return result;
  }

  /**
   * Returns the meta-data of the object, if any.
   *
   * @return		the meta-data
   */
  public Map<String,Object> getMetaData() {
    return getMetaData(false);
  }

  /**
   * Returns the meta-data of the object, if any.
   *
   * @param copy	whether to return a copy
   * @return		the meta-data
   */
  public Map<String,Object> getMetaData(boolean copy) {
    Map<String,Object>	result;

    if (!copy)
      return m_MetaData;

    result = new HashMap<>();
    result.putAll(m_MetaData);

    return result;
  }

  /**
   * Returns the actual size rectangle.
   *
   * @return		the actual size rectangle
   */
  public Rectangle getActual() {
    if (m_Actual == null)
      m_Actual = getRectangle();
    return m_Actual;
  }

  /**
   * Scales the actual size rectangle with the given factor.
   *
   * @param scale	the scale factor
   */
  public void scale(double scale) {
    m_Actual = getRectangle(scale);
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
   * Checks whether polygon meta-data is present.
   *
   * @return		true if present
   */
  public boolean hasPolygon() {
    return m_MetaData.containsKey(KEY_POLY_X) && m_MetaData.containsKey(KEY_POLY_Y);
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
   * Returns the specified polygon coordinates.
   *
   * @param key		{@link #KEY_POLY_X} or {@link #KEY_POLY_Y}
   * @return		the coordinates, 0-length if none available or failed to parse
   */
  protected int[] getPolyCoords(String key) {
    int[]	result;
    String[]	parts;
    int		i;

    if (!hasPolygon()) {
      result = new int[0];
    }
    else {
      parts = m_MetaData.get(key).toString().split(",");
      result = new int[parts.length];
      try {
	for (i = 0; i < parts.length; i++)
	  result[i] = (int) Double.parseDouble(parts[i]);
      }
      catch (Exception e) {
        result = new int[0];
      }
    }

    return result;
  }

  /**
   * Returns the X coordinates of the polygon (if any).
   *
   * @return		the X coordinates
   */
  public int[] getPolygonX() {
    return getPolyCoords(KEY_POLY_X);
  }

  /**
   * Returns the Y coordinates of the polygon (if any).
   *
   * @return		the Y coordinates
   */
  public int[] getPolygonY() {
    return getPolyCoords(KEY_POLY_Y);
  }

  /**
   * Returns the polygon, if possible.
   *
   * @return		the polygon, null if no/incorrect data stored
   */
  public Polygon getPolygon() {
    int[]	x;
    int[]	y;

    x = getPolygonX();
    y = getPolygonY();
    if ((x.length == 0) || (x.length != y.length))
      return null;
    else
      return new Polygon(x, y, x.length);
  }

  /**
   * Stores the polygon in the meta-data.
   *
   * @param value	the polygon
   */
  public void setPolygon(Polygon value) {
    getMetaData().put(KEY_POLY_X, Utils.flatten(StatUtils.toNumberArray(value.xpoints), ","));
    getMetaData().put(KEY_POLY_Y, Utils.flatten(StatUtils.toNumberArray(value.ypoints), ","));
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
