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
 * Cross.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.jfreechart.shape;

import org.jfree.util.ShapeUtilities;

import java.awt.Shape;

/**
 * Generates a cross.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Cross
  extends AbstractShapeGenerator {

  private static final long serialVersionUID = 514268201924765348L;

  /** 
   * Enumeration of type of cross. 
   */
  public enum Type {
    REGULAR,
    DIAGONAL,
  }
  
  /** the type of cross. */
  protected Type m_Type;

  /** the length of the arms. */
  protected float m_Length;

  /** the thickness of the arms. */
  protected float m_Thickness;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Creates a cross.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "type", "type",
      Type.DIAGONAL);

    m_OptionManager.add(
      "length", "length",
      3.0f, 0.0f, null);

    m_OptionManager.add(
      "thickness", "thickness",
      0.25f, 0.0f, null);
  }

  /**
   * Sets the type of the cross.
   *
   * @param value	the type
   */
  public void setType(Type value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type of the cross.
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
    return "The type of the cross.";
  }

  /**
   * Sets the length of the arms of the cross.
   *
   * @param value	the length
   */
  public void setLength(float value) {
    m_Length = value;
    reset();
  }

  /**
   * Returns the length of the arms of the cross.
   *
   * @return		the length
   */
  public float getLength() {
    return m_Length;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String lengthTipText() {
    return "The length of the arms of the cross.";
  }

  /**
   * Sets the thickness of the arms of the cross.
   *
   * @param value	the thickness
   */
  public void setThickness(float value) {
    m_Thickness = value;
    reset();
  }

  /**
   * Returns the thickness of the arms of the cross.
   *
   * @return		the thickness
   */
  public float getThickness() {
    return m_Thickness;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String thicknessTipText() {
    return "The thickness of the arms of the cross.";
  }

  /**
   * Generates the shape.
   *
   * @return		the shape, null if none generated
   */
  @Override
  protected Shape doGenerate() {
    switch (m_Type) {
      case DIAGONAL:
        return ShapeUtilities.createDiagonalCross(m_Length, m_Thickness);
      case REGULAR:
        return ShapeUtilities.createRegularCross(m_Length, m_Thickness);
      default:
        throw new IllegalStateException("Unhandled type: " + m_Type);
    }
  }
}
