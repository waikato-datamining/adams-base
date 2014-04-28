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
 * Zoom.java
 * Copyright (C) 2008-2010 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.core.axis;

import java.io.Serializable;

import adams.core.CloneHandler;

/**
 * A container class for the min/max of a zoom.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Zoom
  implements Serializable, CloneHandler<Zoom> {

  /** for serialization. */
  private static final long serialVersionUID = 2216082562295422476L;

  /** the minimum. */
  protected double m_Minimum;

  /** the maximum. */
  protected double m_Maximum;

  /**
   * Initializes the container.
   *
   * @param min	the minimum
   * @param max	the maximum
   */
  public Zoom(double min, double max) {
    super();

    m_Minimum = min;
    m_Maximum = max;
  }

  /**
   * Returns a copy of itself.
   *
   * @return		the copy
   */
  public Zoom getClone() {
    return new Zoom(m_Minimum, m_Maximum);
  }

  /**
   * Returns the stored minimum.
   *
   * @return		the minimum
   */
  public double getMinimum() {
    return m_Minimum;
  }

  /**
   * Returns the stored maximum.
   *
   * @return		the maximum
   */
  public double getMaximum() {
    return m_Maximum;
  }

  /**
   * Returns the zoom as string.
   *
   * @return		the zoom as string
   */
  public String toString() {
    return "Zoom: min=" + getMinimum() + ", max=" + getMaximum();
  }
}