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
 * PolygonVertices.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.object.objectannotations.outline;

import adams.data.objectoverlap.OptionalBoundingBoxFallbackSupporter;
import adams.flow.transformer.locateobjects.LocatedObject;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * Plots the polygon vertices.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class PolygonVertices
  extends AbstractStrokeOutlinePlotter
  implements OptionalBoundingBoxFallbackSupporter {

  private static final long serialVersionUID = -2429218032837933149L;

  /** the marker shape. */
  protected VertexShape m_Shape;

  /** the maximum width/height of the shape to plot around the vertices. */
  protected int m_Extent;

  /** whether to fallback on bounding box. */
  protected boolean m_Fallback;

  /** the ratio used for determining whether to fall back from polygon on bbox. */
  protected double m_BoundingBoxFallbackRatio;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Plots the polygon vertices.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "shape", "shape",
      VertexShape.CIRCLE);

    m_OptionManager.add(
      "extend", "extent",
      7, 1, null);

    m_OptionManager.add(
      "fallback", "fallback",
      true);

    m_OptionManager.add(
      "bounding-box-fallback-ratio", "boundingBoxFallbackRatio",
      0.0, 0.0, 1.0);
  }

  /**
   * Sets the shape to use for vertices.
   *
   * @param value	the shape
   */
  public void setShape(VertexShape value) {
    m_Shape = value;
    reset();
  }

  /**
   * Returns the shape in use for vertices.
   *
   * @return		the shape
   */
  public VertexShape getShape() {
    return m_Shape;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String shapeTipText() {
    return "The shape to use for the vertices.";
  }

  /**
   * Sets the size of the vertices.
   *
   * @param value	the extent
   */
  public void setExtent(int value) {
    m_Extent = value;
    reset();
  }

  /**
   * Returns the size of the vertices.
   *
   * @return		the extent
   */
  public int getExtent() {
    return m_Extent;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String extentTipText() {
    return "The size of the shapes for the vertices.";
  }

  /**
   * Sets whether to fall back on the bounding box if no polygon available.
   *
   * @param value 	true if to use
   */
  @Override
  public void setFallback(boolean value) {
    m_Fallback = value;
    reset();
  }

  /**
   * Returns whether to fall back on the bounding box if no polygon available.
   *
   * @return 		true if to use
   */
  @Override
  public boolean getFallback() {
    return m_Fallback;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String fallbackTipText() {
    return "Whether to fall back on the bounding box if no polygon available.";
  }

  /**
   * Sets the ratio between shape area over bbox area. If below the bbox is used
   * instead of the polygon.
   *
   * @param value 	the ratio
   */
  @Override
  public void setBoundingBoxFallbackRatio(double value) {
    if (getOptionManager().isValid("boundingBoxFallbackRatio", value)) {
      m_BoundingBoxFallbackRatio = value;
      reset();
    }
  }

  /**
   * Returns the ratio between shape area over bbox area. If below the bbox is used
   * instead of the polygon.
   *
   * @return 		the ratio
   */
  @Override
  public double getBoundingBoxFallbackRatio() {
    return m_BoundingBoxFallbackRatio;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String boundingBoxFallbackRatioTipText() {
    return "The threshold for the ratio between the areas (shape / bbox), below which the bounding box is used over the polygon (ie bad masks/shapes).";
  }

  /**
   * Plots the outline.
   *
   * @param object the object to plot
   * @param color  the color to use
   * @param g      the graphics context
   */
  @Override
  protected void doPlot(LocatedObject object, Color color, Graphics2D g) {
    Rectangle	rect;
    int[]	x;
    int[]	y;
    int		i;
    boolean	fallback;

    fallback = m_Fallback && object.boundingBoxFallback(m_BoundingBoxFallbackRatio);

    g.setColor(color);
    if (!fallback) {
      x = object.getPolygonX();
      y = object.getPolygonY();
      for (i = 0; i < x.length; i++)
	m_Shape.plot(g, x[i], y[i], m_Extent);
    }
    else {
      rect = object.getRectangle();
      m_Shape.plot(g, rect.x,                  rect.y,                   m_Extent);
      m_Shape.plot(g, rect.x + rect.width - 1, rect.y,                   m_Extent);
      m_Shape.plot(g, rect.x + rect.width - 1, rect.y + rect.height - 1, m_Extent);
      m_Shape.plot(g, rect.x,                  rect.y + rect.height - 1, m_Extent);
    }
  }
}
