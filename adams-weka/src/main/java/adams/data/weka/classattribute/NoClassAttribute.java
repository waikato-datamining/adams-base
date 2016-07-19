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
 * NoClassAttribute.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.weka.classattribute;

import weka.core.Instances;

/**
 * Never returns a class attribute.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class NoClassAttribute
  extends AbstractClassAttributeHeuristic {

  private static final long serialVersionUID = -912826971225798159L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Never returns a class attribute.";
  }

  /**
   * Determines the class attribute index for the given dataset.
   *
   * @param data	the dataset to inspect
   * @return		the index, -1 if failed to determine
   */
  @Override
  public int determineClassAttribute(Instances data) {
    return -1;
  }
}
