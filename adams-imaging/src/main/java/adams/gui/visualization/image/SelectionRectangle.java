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
 * SelectionRectangle.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.image;

import java.awt.Rectangle;

/**
 * Stores the index in the report as well.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 364 $
 */
public class SelectionRectangle
  extends Rectangle {

  /** for serialization. */
  private static final long serialVersionUID = -4195121456711918920L;

  /** the associated index. */
  protected int m_Index;

  /**
   * Constructs a new <code>SelectionRectangle</code> whose upper-left corner is
   * specified as
   * {@code (x,y)} and whose width and height
   * are specified by the arguments of the same name.
   * Uses -1 for associated index.
   *
   * @param x		the specified X coordinate
   * @param y		the specified Y coordinate
   * @param width	the width of the <code>SelectionRectangle</code>
   * @param height	the height of the <code>SelectionRectangle</code>
   */
  public SelectionRectangle(int x, int y, int width, int height) {
    this(x, y, width, height, -1);
  }

  /**
   * Constructs a new <code>SelectionRectangle</code> whose upper-left corner is
   * specified as
   * {@code (x,y)} and whose width and height
   * are specified by the arguments of the same name.
   *
   * @param x		the specified X coordinate
   * @param y		the specified Y coordinate
   * @param width	the width of the <code>SelectionRectangle</code>
   * @param height	the height of the <code>SelectionRectangle</code>
   * @param index	the associated index
   */
  public SelectionRectangle(int x, int y, int width, int height, int index) {
    super(x, y, width, height);
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
}
