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
 * ZoomHandler.java
 * Copyright (C) 2008-2010 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.core.axis;

import java.io.Serializable;
import java.util.Stack;

import adams.core.CloneHandler;

/**
 * Class for handling zooms.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ZoomHandler
  implements Serializable, CloneHandler<ZoomHandler> {

  /** for serialization. */
  private static final long serialVersionUID = 6810197787847563561L;

  /** contains the zooms. */
  protected Stack<Zoom> m_Zooms;

  /**
   * Initializes the handler.
   */
  public ZoomHandler() {
    super();

    m_Zooms = new Stack<Zoom>();
  }

  /**
   * Returns an exact copy of itself.
   *
   * @return		copy of itself
   */
  public ZoomHandler getClone() {
    ZoomHandler	result;
    int		i;

    result = new ZoomHandler();
    for (i = 0; i < m_Zooms.size(); i++)
      result.push(m_Zooms.get(i));

    return result;
  }

  /**
   * Adds the zoom to its internal list and updates the axis.
   *
   * @param min	the minimum of the zoom
   * @param max	the maximum of the zoom
   */
  public void push(double min, double max) {
    m_Zooms.push(new Zoom(min, max));
  }

  /**
   * Adds the zoom to its internal list and updates the axis.
   *
   * @param zoom	the zoom to add
   */
  public void push(Zoom zoom) {
    m_Zooms.push((Zoom) zoom.getClone());
  }

  /**
   * Peeks the topmost zoom.
   *
   * @return		the topmost zoom
   */
  public Zoom peek() {
    return m_Zooms.peek();
  }

  /**
   * Pops the topmost zoom.
   *
   * @return		the topmost zoom
   */
  public Zoom pop() {
    return m_Zooms.pop();
  }

  /**
   * Removes all zooms.
   */
  public void clear() {
    m_Zooms.clear();
  }

  /**
   * Returns true if at least one zoom is stored.
   *
   * @return		true if a zoom is in place
   */
  public boolean isZoomed() {
    return (!m_Zooms.empty());
  }

  /**
   * Returns a string representation of the handler.
   *
   * @return		a string representation
   */
  public String toString() {
    return "# of zooms: " + m_Zooms.size();
  }
}