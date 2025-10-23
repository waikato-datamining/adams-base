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
 * XYDataset.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.xchart.dataset;

/**
 * Container for XY data.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class XYDataset
  extends Dataset {

  private static final long serialVersionUID = 8071626595959879341L;

  /** the x data. */
  protected double[] m_X;

  /** the y data. */
  protected double[] m_Y;

  /**
   * Initializes the container.
   *
   * @param name 	the name of the dataset
   * @param x		the x data
   * @param y		the y data
   */
  public XYDataset(String name, double[] x, double[] y) {
    super(name);
    m_X = x;
    m_Y = y;
  }

  /**
   * Returns the X data.
   *
   * @return		the data
   */
  public double[] getX() {
    return m_X;
  }

  /**
   * Returns the Y data.
   *
   * @return		the data
   */
  public double[] getY() {
    return m_Y;
  }
}
