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
 * Class.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.weka.columnfinder;

import weka.core.Instances;

/**
 * Column finder which finds the class column (if one is set).
 *
 * @author Corey Sterling (csterlin at waikato dot ac dot nz)
 */
public class Class extends AbstractColumnFinder {

  /** Auto-generated serialisation UID#. */
  private static final long serialVersionUID = 277213650707904675L;

  /**
   * Returns the columns of interest in the dataset.
   *
   * @param data	the dataset to inspect
   * @return		the columns of interest
   */
  @Override
  protected int[] doFindColumns(Instances data) {
    // Get the index of the class column
    int classIndex = data.classIndex();

    // Return the class column or the empty set if there isn't one
    if (classIndex != -1)
      return new int[] { data.classIndex() };
    else
      return new int[0];
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Column finder which finds the class column (if one is set).";
  }
}