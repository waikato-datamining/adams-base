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
 * LocatedObject.java
 * Copyright (C) 2013-2022 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.locateobjects;

import adams.core.CloneHandler;
import adams.core.CompareUtils;
import adams.core.Utils;
import adams.core.base.QuadrilateralLocation;
import adams.data.image.BufferedImageHelper;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetSupporter;
import adams.data.statistics.StatUtils;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;

import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Container for located objects.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class LocatedObject
    implements Serializable, CloneHandler<LocatedObject>, Comparable<LocatedObject>,
    SpreadSheetSupporter {

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
  protected Rectangle m_ActualRectangle;

  /** the actual polygon. */
  protected Polygon m_ActualPolygon;

  /** additional meta-data. */
  protected Map<String,Object> m_MetaData;

  /**
   * Initializes the container.
   *
   * @param polygon	the polygon to use
   */
  public LocatedObject(Polygon polygon) {
    this(null, polygon);
  }

  /**
   * Initializes the container.
   *
   * @param image	the object image, can be null
   * @param polygon	the polygon to use
   */
  public LocatedObject(BufferedImage image, Polygon polygon) {
    this(image, polygon, null);
  }

  /**
   * Initializes the container.
   *
   * @param image	the object image, can be null
   * @param polygon	the polygon to use
   * @param metaData	optional meta-data, can be null
   */
  public LocatedObject(BufferedImage image, Polygon polygon, Map<String,Object> metaData) {
    this(image, polygon.getBounds().x, polygon.getBounds().y, polygon.getBounds().width, polygon.getBounds().height, metaData);
    setPolygon(polygon);
  }

  /**
   * Initializes the container.
   *
   * @param x		the x of the top-left corner in the original image
   * @param y		the y of the top-left corner in the original image
   * @param width	the width of the object sub-image
   * @param height	the height of the object sub-image
   */
  public LocatedObject(int x, int y, int width, int height) {
    this(null, x, y, width, height, null);
  }

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
    // fix negative width
    if (width < 0) {
      x     += width;
      width  = -width;
    }
    // fix negative height
    if (height < 0) {
      y      += height;
      height  = -height;
    }

    m_Image    = image;
    m_X        = x;
    m_Y        = y;
    m_Width    = width;
    m_Height   = height;
    m_MetaData = (metaData == null) ? new HashMap<>() : metaData;
    m_ActualRectangle = null;
    m_ActualPolygon   = null;
  }

  /**
   * Initializes the container.
   *
   * @param rect	the rectangle
   */
  public LocatedObject(Rectangle rect) {
    this(null, rect);
  }

  /**
   * Initializes the container.
   *
   * @param image	the object image, can be null
   * @param rect	the rectangle
   */
  public LocatedObject(BufferedImage image, Rectangle rect) {
    this(image, rect, null);
  }

  /**
   * Initializes the container.
   *
   * @param image	the object image, can be null
   * @param rect	the rectangle
   * @param metaData	optional meta-data, can be null
   */
  public LocatedObject(BufferedImage image, Rectangle rect, Map<String,Object> metaData) {
    this(image, rect.x, rect.y, rect.width, rect.height, metaData);
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
  public Rectangle getActualRectangle() {
    if (m_ActualRectangle == null)
      m_ActualRectangle = getRectangle();
    return m_ActualRectangle;
  }

  /**
   * Returns the actual size polygon.
   *
   * @return		the actual size polygon
   */
  public Polygon getActualPolygon() {
    if (m_ActualPolygon == null)
      m_ActualPolygon = getPolygon();
    return m_ActualPolygon;
  }

  /**
   * Scales the actual size rectangle with the given factor.
   *
   * @param scale	the scale factor
   */
  public void scale(double scale) {
    m_ActualRectangle = getRectangle(scale);
    m_ActualPolygon   = getPolygon(scale);
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
    return m_MetaData.containsKey(KEY_POLY_X)
	&& m_MetaData.containsKey(KEY_POLY_Y);
  }

  /**
   * Checks whether polygon meta-data is present.
   *
   * @return		true if present
   */
  public boolean hasValidPolygon() {
    return hasPolygon() && (getPolygonX().length >= 4) && (getPolygonY().length >= 4);
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
    return getPolygon(1.0);
  }

  /**
   * Returns the polygon, if possible.
   *
   * @param scale 	the scale to use
   * @return		the polygon, null if no/incorrect data stored
   */
  public Polygon getPolygon(double scale) {
    int[]	x;
    int[]	y;
    int		i;

    x = getPolygonX();
    y = getPolygonY();
    if ((x.length == 0) || (x.length != y.length))
      return null;

    if (scale != 1.0) {
      for (i = 0; i < x.length; i++) {
	x[i] = (int) (x[i] * scale);
	y[i] = (int) (y[i] * scale);
      }
    }

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
   * Stores the JTS polygon in the meta-data.
   *
   * @param value	the JTS polygon to use
   */
  public void setPolygon(org.locationtech.jts.geom.Polygon value) {
    org.locationtech.jts.geom.Polygon 	bbox;
    Coordinate[]			coords;
    int[]				polyX;
    int[]				polyY;
    int 				i;

    coords = value.getCoordinates();
    polyX = new int[coords.length];
    polyY = new int[coords.length];
    for (i = 0; i < coords.length; i++) {
      polyX[i] = (int) coords[i].x;
      polyY[i] = (int) coords[i].y;
    }
    setPolygon(new Polygon(polyX, polyY, polyX.length));
  }

  /**
   * Converts the polygon or rectangle into a JTS polygon (for proper intersects).
   *
   * @return		the polygon
   * @see		#toGeometry(Polygon)
   * @see		#toGeometry(Rectangle)
   */
  public org.locationtech.jts.geom.Polygon toGeometry() {
    if (hasPolygon())
      return toGeometry(getPolygon());
    else
      return toGeometry(getRectangle());
  }

  /**
   * Turns the bounding box into a polygon.
   *
   * @return		the polygon
   */
  public Polygon bboxToPolygon() {
    Polygon 	result;
    int[] 	bbox_x;
    int[] 	bbox_y;

    bbox_x = new int[]{getX(), getX() + getWidth() - 1, getX() + getWidth() - 1, getX()};
    bbox_y = new int[]{getY(), getY(), getY() + getHeight() - 1, getY() + getHeight() - 1};
    result = new Polygon(bbox_x, bbox_y, bbox_x.length);

    return result;
  }

  /**
   * Checks whether we need fall back on the bounding box due to the object either not having a
   * polygon or the polygon being too small in relation to the required polygon/bbox ratio.
   *
   * @param minRatio	the minimum poly/bbox ratio that we need
   * @return		true if fall back on bbox
   */
  public boolean boundingBoxFallback(double minRatio) {
    boolean	result;
    Polygon	poly;
    Polygon	bbox;
    double	area_poly;
    double	area_bbox;
    double	ratio;

    result = false;

    poly = hasValidPolygon() ? getPolygon() : null;
    bbox = bboxToPolygon();

    if ((poly != null) && (minRatio > 0)) {
      area_bbox = LocatedObject.toGeometry(bbox).getArea();
      area_poly = LocatedObject.toGeometry(poly).getArea();
      if (area_bbox > 0) {
	ratio = area_poly / area_bbox;
	if (ratio < minRatio)
	  result = true;
      }
    }
    else if (poly == null) {
      result = true;
    }

    return result;
  }

  /**
   * Ensures that the object fits within this region.
   *
   * @param width	the width of the region
   * @param height	the height of the region
   * @return		true if object got adjusted
   */
  public boolean makeFit(int width, int height) {
    boolean	result;
    int[]	px;
    int[]	py;
    boolean	padjusted;
    int		i;

    result = false;

    if (m_X < 0) {
      m_Width += m_X;
      m_X      = 0;
      result   = true;
    }
    if (m_X + m_Width > width) {
      m_Width -= (m_X + m_Width) - width;
      result   = true;
    }

    if (m_Y < 0) {
      m_Height += m_Y;
      m_Y       = 0;
      result    = true;
    }
    if (m_Y + m_Height > height) {
      m_Height -= (m_Y + m_Height) - height;
      result    = true;
    }

    if (hasPolygon()) {
      px = getPolygonX();
      py = getPolygonY();
      padjusted = false;
      for (i = 0; i < px.length; i++) {
	if (px[i] < 0) {
	  px[i]     = 0;
	  padjusted = true;
	}
	if (px[i] >= width) {
	  px[i]     = width - 1;
	  padjusted = true;
	}
	if (py[i] < 0) {
	  py[i]     = 0;
	  padjusted = true;
	}
	if (py[i] >= height) {
	  py[i]     = height - 1;
	  padjusted = true;
	}
      }
      if (padjusted) {
	setPolygon(new Polygon(px, py, px.length));
	result = true;
      }
    }

    return result;
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
   * Renames the meta-data key.
   *
   * @param oldKey	the old key
   * @param newKey	the new key
   * @return		if key got updated
   */
  public boolean renameMetaDataKey(String oldKey, String newKey) {
    boolean	result;
    Object	value;

    result = false;

    if (getMetaData().containsKey(oldKey)) {
      value = getMetaData().remove(oldKey);
      getMetaData().put(newKey, value);
      result = true;
    }

    return result;
  }

  /**
   * Compares this object with the provided one. Bounding box, then polygon (if present).
   *
   * @param o		the object to compare with
   * @return		if x/y/width/height are less, equal to, or larger than
   * 			the other one
   */
  @Override
  public int compareTo(LocatedObject o) {
    int		result;

    result = Integer.compare(m_X, o.getX());
    if (result == 0)
      result = Integer.compare(m_Y, o.getY());
    if (result == 0)
      result = Integer.compare(m_Width, o.getWidth());
    if (result == 0)
      result = Integer.compare(m_Height, o.getHeight());
    if (result == 0)
      result = Boolean.compare(hasPolygon(), o.hasPolygon());
    if (result == 0) {
      result = CompareUtils.compare(getPolygonX(), o.getPolygonX());
      if (result == 0)
	result = CompareUtils.compare(getPolygonY(), o.getPolygonY());
    }

    return result;
  }

  /**
   * Tests if this object is the same as the other one.
   *
   * @param obj		the object to compare with
   * @return		true if the same
   * @see		#compareTo(LocatedObject)
   */
  @Override
  public boolean equals(Object obj) {
    return (obj instanceof LocatedObject) && (compareTo((LocatedObject) obj) == 0);
  }

  /**
   * Returns the hashcode of the rectangle.
   *
   * @return		the hash
   */
  @Override
  public int hashCode() {
    return getRectangle().hashCode();
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

  /**
   * Returns the content as spreadsheet.
   *
   * @return		the content
   */
  public SpreadSheet toSpreadSheet() {
    SpreadSheet		result;
    Row			row;

    result = new DefaultSpreadSheet();

    // header
    row    = result.getHeaderRow();
    row.addCell("I").setContentAsString("Index");
    row.addCell("X").setContentAsString("X");
    row.addCell("Y").setContentAsString("Y");
    row.addCell("W").setContentAsString("Width");
    row.addCell("H").setContentAsString("Height");
    row.addCell("P").setContentAsString("Polygon");
    row.addCell("M").setContentAsString("Meta-data");

    // data
    row = result.addRow();
    row.addCell("I").setContentAsString(getIndexString());
    row.addCell("X").setContent(getX());
    row.addCell("Y").setContent(getY());
    row.addCell("W").setContent(getWidth());
    row.addCell("H").setContent(getHeight());
    row.addCell("P").setContent(hasPolygon());
    row.addCell("M").setContent(getMetaData().toString());

    return result;
  }

  /**
   * Converts the polygon into a JTS polygon (for proper intersects).
   *
   * @return		the polygon
   */
  public static org.locationtech.jts.geom.Polygon toGeometry(Polygon polygon) {
    org.locationtech.jts.geom.Polygon	result;
    LinearRing 				ring;
    List<Coordinate> 			coords;
    GeometryFactory 			factory;
    int[]				x;
    int[]				y;
    int					i;

    factory = new GeometryFactory();
    coords = new ArrayList<>();
    x = polygon.xpoints;
    y = polygon.ypoints;
    for (i = 0; i < x.length; i++)
      coords.add(new Coordinate(x[i], y[i]));
    coords.add(new Coordinate(x[0], y[0]));
    ring = new LinearRing(new CoordinateArraySequence(coords.toArray(new Coordinate[0])), factory);
    result = new org.locationtech.jts.geom.Polygon(ring, null, factory);

    return result;
  }

  /**
   * Converts the rectangle into a JTS polygon (for proper intersects).
   *
   * @return		the polygon
   */
  public static org.locationtech.jts.geom.Polygon toGeometry(Rectangle rectangle) {
    org.locationtech.jts.geom.Polygon	result;
    LinearRing 				ring;
    List<Coordinate> 			coords;
    GeometryFactory 			factory;
    int[]				x;
    int[]				y;
    int					i;

    factory = new GeometryFactory();
    coords = new ArrayList<>();
    coords.add(new Coordinate(rectangle.getX(), rectangle.getY()));
    coords.add(new Coordinate(rectangle.getX() + rectangle.getWidth() - 1, rectangle.getY()));
    coords.add(new Coordinate(rectangle.getX() + rectangle.getWidth() - 1, rectangle.getY() + rectangle.getHeight() - 1));
    coords.add(new Coordinate(rectangle.getX(), rectangle.getY() + rectangle.getHeight() - 1));
    coords.add(new Coordinate(rectangle.getX(), rectangle.getY()));
    ring = new LinearRing(new CoordinateArraySequence(coords.toArray(new Coordinate[0])), factory);
    result = new org.locationtech.jts.geom.Polygon(ring, null, factory);

    return result;
  }

  /**
   * Returns the boundaries of the JTS polygon.
   *
   * @param polygon	the polygon to get the bounds for
   * @return		the bounds, empty rectangle if failed to compute
   */
  public static Rectangle polygonBounds(org.locationtech.jts.geom.Polygon polygon) {
    Rectangle				result;
    Geometry				envelope;
    org.locationtech.jts.geom.Polygon	bbox;
    Coordinate[]			coords;
    Point				point;

    result   = new Rectangle();
    envelope = polygon.getEnvelope();
    if (envelope instanceof org.locationtech.jts.geom.Polygon) {
      bbox = (org.locationtech.jts.geom.Polygon) polygon.getEnvelope();
      coords = bbox.getCoordinates();
      result = new Rectangle(
	  (int) coords[0].x,
	  (int) coords[0].y,
	  (int) (coords[2].x - coords[0].x + 1),
	  (int) (coords[2].y - coords[0].y + 1));
    }
    else if (envelope instanceof org.locationtech.jts.geom.Point) {
      point = (org.locationtech.jts.geom.Point) envelope;
      if (!point.isEmpty())
	result = new Rectangle((int) point.getX(), (int) point.getY(), 0, 0);
    }
    return result;
  }
}
