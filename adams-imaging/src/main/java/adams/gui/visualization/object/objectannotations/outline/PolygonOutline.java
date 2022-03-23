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
 * PolygonOutline.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.object.objectannotations.outline;

import adams.flow.transformer.locateobjects.LocatedObject;
import adams.gui.visualization.object.objectannotations.AnnotationUtils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;

/**
 * Plots the polygon.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class PolygonOutline
  extends AbstractStrokeOutlinePlotter {

  private static final long serialVersionUID = -2429218032837933149L;

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
    return "Plots the polygon.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
        "fallback", "fallback",
        true);

    m_OptionManager.add(
        "bounding-box-fallback-ratio", "boundingBoxFallbackRatio",
        0.0, 0.0, 1.0);
  }

  /**
   * Sets whether to fall back on the bounding box if no polygon available.
   *
   * @param value 	true if to use
   */
  public void setFallback(boolean value) {
    m_Fallback = value;
    reset();
  }

  /**
   * Returns whether to fall back on the bounding box if no polygon available.
   *
   * @return 		true if to use
   */
  public boolean getFallback() {
    return m_Fallback;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fallbackTipText() {
    return "Whether to fall back on the bounding box if no polygon available.";
  }

  /**
   * Sets the ratio between shape area over bbox area. If below the bbox is used
   * instead of the polygon.
   *
   * @param value 	the ratio
   */
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
  public double getBoundingBoxFallbackRatio() {
    return m_BoundingBoxFallbackRatio;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
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
    Polygon   	poly;
    Rectangle	rect;
    int[]	x;
    int[]	y;
    boolean	fallback;

    fallback = m_Fallback && AnnotationUtils.boundingBoxFallback(object, m_BoundingBoxFallbackRatio);

    g.setColor(color);
    if (!fallback) {
      x = object.getPolygonX();
      y = object.getPolygonY();
      g.drawPolygon(x, y, x.length);
    }
    else {
      rect = object.getRectangle();
      g.drawRect(rect.x, rect.y, rect.width, rect.height);
    }
  }
}
