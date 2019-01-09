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
 * Triangle.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.jfreechart.shape;

import org.jfree.util.ShapeUtilities;

import java.awt.Shape;

/**
 * Generates a triangle.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Triangle
  extends AbstractShapeGenerator {

  private static final long serialVersionUID = 514268201924765348L;

  /** 
   * Enumeration of type of triangle. 
   */
  public enum Type {
    UP,
    DOWN,
  }
  
  /** the type of triangle. */
  protected Type m_Type;

  /** the size. */
  protected float m_Size;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Creates a triangle.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "type", "type",
      Type.DOWN);

    m_OptionManager.add(
      "size", "size",
      5.0f, 0.0f, null);
  }

  /**
   * Sets the type of the triangle.
   *
   * @param value	the type
   */
  public void setType(Type value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type of the triangle.
   *
   * @return		the type
   */
  public Type getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "The type of the triangle.";
  }

  /**
   * Sets the size of the diamond.
   *
   * @param value	the size
   */
  public void setSize(float value) {
    m_Size = value;
    reset();
  }

  /**
   * Returns the size of the diamond.
   *
   * @return		the size
   */
  public float getSize() {
    return m_Size;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sizeTipText() {
    return "The size of the diamond.";
  }

  /**
   * Generates the shape.
   *
   * @return		the shape, null if none generated
   */
  @Override
  protected Shape doGenerate() {
    switch (m_Type) {
      case DOWN:
        return ShapeUtilities.createDownTriangle(m_Size);
      case UP:
        return ShapeUtilities.createUpTriangle(m_Size);
      default:
        throw new IllegalStateException("Unhandled type: " + m_Type);
    }
  }
}
