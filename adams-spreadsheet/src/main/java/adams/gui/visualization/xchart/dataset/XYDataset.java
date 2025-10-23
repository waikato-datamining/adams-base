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

import gnu.trove.list.TDoubleList;
import gnu.trove.list.array.TDoubleArrayList;

/**
 * Container for XY data.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class XYDataset
  extends Dataset {

  private static final long serialVersionUID = 8071626595959879341L;

  /** the x data. */
  protected TDoubleList m_X;

  /** the y data. */
  protected TDoubleList m_Y;

  /**
   * Initializes the container with no data.
   *
   * @param name 	the name of the dataset
   */
  public XYDataset(String name) {
    super(name);
    m_X = new TDoubleArrayList();
    m_Y = new TDoubleArrayList();
  }

  /**
   * Initializes the container with the specified x/y values.
   *
   * @param name 	the name of the dataset
   * @param x		the x data
   * @param y		the y data
   */
  public XYDataset(String name, double[] x, double[] y) {
    super(name);
    m_X = new TDoubleArrayList(x);
    m_Y = new TDoubleArrayList(y);
  }

  /**
   * Adds the data point to the dataset.
   *
   * @param x		the X to add
   * @param y		the Y to add
   */
  public void add(double x, double y) {
    m_X.add(x);
    m_Y.add(y);
  }

  /**
   * Returns the X data.
   *
   * @return		the data
   */
  public double[] getX() {
    return m_X.toArray();
  }

  /**
   * Returns the Y data.
   *
   * @return		the data
   */
  public double[] getY() {
    return m_Y.toArray();
  }
}
