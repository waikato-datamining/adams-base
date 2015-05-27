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
 * Limits.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.data.spc;

import java.io.Serializable;

/**
 * TODO: What class does.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Limits
  implements Serializable {

  private static final long serialVersionUID = -8472061760213478622L;

  /** the lower limit. */
  protected double m_Lower;

  /** the center. */
  protected double m_Center;

  /** the upper limit. */
  protected double m_Upper;

  /**
   * Initializes the container with dummy data.
   */
  public Limits() {
    this(0.0, 0.5, 1.0);
  }

  /**
   * Initializes the container.
   *
   * @param stats	the statistics (center/lower/upper)
   */
  public Limits(double[] stats) {
    this(stats[1], stats[0], stats[2]);
  }

  /**
   * Initializes the container.
   *
   * @param lower	the lower limit
   * @param center	the center
   * @param upper       the upper limit
   */
  public Limits(double lower, double center, double upper) {
    m_Lower  = lower;
    m_Center = center;
    m_Upper  = upper;
  }

  /**
   * Returns the lower limit.
   *
   * @return		the lower limit
   */
  public double getLower() {
    return m_Lower;
  }

  /**
   * Returns the center.
   *
   * @return		the center
   */
  public double getCenter() {
    return m_Center;
  }

  /**
   * Returns the upper limit.
   *
   * @return		the upper limit
   */
  public double getUpper() {
    return m_Upper;
  }

  /**
   * Returns a short description of the container.
   *
   * @return		the description
   */
  @Override
  public String toString() {
    return "L=" + m_Lower + ", C=" + m_Center + ", U=" + m_Upper;
  }
}
