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
 * Dataset.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.xchart.dataset;

import java.io.Serializable;

/**
 * Ancestor for dataset containers.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class Dataset
  implements Serializable {

  private static final long serialVersionUID = -6535886349826448866L;

  /** the name of the dataset. */
  protected String m_Name;

  /**
   * Initializes the dataset.
   *
   * @param name	the name for the data
   */
  protected Dataset(String name) {
    m_Name = name;
  }

  /**
   * Returns the name of the dataset.
   *
   * @return		the dataset
   */
  public String getName() {
    return m_Name;
  }
}
