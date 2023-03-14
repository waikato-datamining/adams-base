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
 * RectangleVertices.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.object.objectannotations.outline;

import adams.flow.transformer.locateobjects.LocatedObject;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Plots the vertices of the bounding box.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class RectangleVertices
  extends AbstractStrokeOutlinePlotter
  implements SubsetPlotSupporter {

  private static final long serialVersionUID = -2429218032837933149L;

  /** the marker shape. */
  protected VertexShape m_Shape;

  /** the maximum width/height of the shape to plot around the vertices. */
  protected int m_Extent;

  /** the objects to limit the plotting to. */
  protected Set<LocatedObject> m_PlotSubset;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Plots the vertices of the bounding box.";
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
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_PlotSubset = null;
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
   * Sets the object to limit the plotting to.
   *
   * @param obj		the object, can be null
   */
  @Override
  public void setPlotSubset(LocatedObject obj) {
    m_PlotSubset = new HashSet<>();
    if (obj != null)
      m_PlotSubset.add(obj);
  }

  /**
   * Sets the objects to limit the plotting to.
   *
   * @param objs	the objects, can be null
   */
  @Override
  public void setPlotSubset(LocatedObject[] objs) {
    m_PlotSubset = new HashSet<>();
    if (objs != null)
      m_PlotSubset.addAll(Arrays.asList(objs));
  }

  /**
   * Sets the objects to limit the plotting to.
   *
   * @param objs	the objects
   */
  public void setPlotSubset(List<LocatedObject> objs) {
    m_PlotSubset = new HashSet<>();
    if (objs != null)
      m_PlotSubset.addAll(objs);
  }

  /**
   * Returns the current subset to plot.
   *
   * @return		the objects, or null if none set
   */
  @Override
  public LocatedObject[] getPlotSubset() {
    if (m_PlotSubset == null)
      return null;
    else
      return m_PlotSubset.toArray(new LocatedObject[0]);
  }

  /**
   * Clears the objects to limit plotting to.
   */
  @Override
  public void clearPlotSubset() {
    m_PlotSubset = null;
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

    if (m_PlotSubset != null) {
      if (!m_PlotSubset.contains(object))
        return;
    }

    rect = object.getRectangle();
    g.setColor(color);

    m_Shape.plot(g, rect.x,                  rect.y,                   m_Extent);
    m_Shape.plot(g, rect.x + rect.width - 1, rect.y,                   m_Extent);
    m_Shape.plot(g, rect.x + rect.width - 1, rect.y + rect.height - 1, m_Extent);
    m_Shape.plot(g, rect.x,                  rect.y + rect.height - 1, m_Extent);
  }
}
