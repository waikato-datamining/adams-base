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
 * MOAHelper.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

import weka.core.Instance;

/**
 * Helper class for MOA related stuff.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MOAHelper {

  /**
   * Fixes the votes array if the length differs from the number of class
   * labels defined in the header information of the dataset.
   *
   * @param votes	the votes to fix
   * @param inst	the instance to get the dataset information from
   * @return		the (potentially) fixed votes array
   */
  public static double[] fixVotes(double[] votes, Instance inst) {
    double[]	result;

    // no class attribute information, can't do anything
    if (inst.classIndex() == -1)
      return votes;

    // nothing to fix
    if (votes.length == inst.numClasses())
      return votes;

    result = new double[inst.numClasses()];
    System.arraycopy(votes, 0, result, 0, votes.length);

    return result;
  }
}
