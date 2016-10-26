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
 * NoChange.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.weka.relationname;

import weka.core.Instances;

import java.io.File;

/**
 * Simply returns the current relation name.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class NoChange
  extends AbstractRelationNameHeuristic {

  private static final long serialVersionUID = 5951436518450210725L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Simply returns the current relation name.";
  }

  /**
   * Determines the relation name for the given file/dataset pair.
   *
   * @param file	the file the dataset was loaded from (maybe null)
   * @param data	the loaded dataset
   * @return		the relation name, null if failed to determine
   */
  @Override
  public String determineRelationName(File file, Instances data) {
    return data.relationName();
  }
}
