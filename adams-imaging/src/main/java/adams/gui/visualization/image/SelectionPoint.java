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
 * SelectionPoint.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.image;

import java.awt.Point;

/**
 * Stores the index in the point as well.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class SelectionPoint
  extends Point {

  /** for serialization. */
  private static final long serialVersionUID = -4195121456711918920L;

  /** the associated index. */
  protected int m_Index;

  /**
   * Constructs a new <code>SelectionPoint</code> whose upper-left corner is
   * specified as
   * {@code (x,y)} and whose width and height
   * are specified by the arguments of the same name.
   * Uses -1 for associated index.
   *
   * @param x		the specified X coordinate
   * @param y		the specified Y coordinate
   */
  public SelectionPoint(int x, int y) {
    this(x, y, -1);
  }

  /**
   * Constructs a new <code>SelectionPoint</code> whose upper-left corner is
   * specified as
   * {@code (x,y)} and whose width and height
   * are specified by the arguments of the same name.
   *
   * @param x		the specified X coordinate
   * @param y		the specified Y coordinate
   * @param index	the associated index
   */
  public SelectionPoint(int x, int y, int index) {
    super(x, y);
    m_Index = index;
  }

  /**
   * Constructs a new <code>SelectionPoint</code> using the specified point.
   *
   * @param p		the point
   */
  public SelectionPoint(Point p) {
    this(p, -1);
  }

  /**
   * Constructs a new <code>SelectionPoint</code> using the specified point.
   *
   * @param p		the point
   * @param index	the associated index
   */
  public SelectionPoint(Point p, int index) {
    super(p.x, p.y);
    m_Index = index;
  }

  /**
   * Returns the stored index.
   *
   * @return		the index
   */
  public int getIndex() {
    return m_Index;
  }

  /**
   * Sets the index.
   *
   * @param value	the index
   */
  public void setIndex(int value) {
    m_Index = value;
  }
}
