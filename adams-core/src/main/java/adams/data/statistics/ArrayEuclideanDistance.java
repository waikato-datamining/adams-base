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
 * ArrayEuclideanDistance.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.statistics;

/**
 <!-- globalinfo-start -->
 * Calculates the Euclidean distance between the first array and the remaining arrays. The arrays must be numeric, of course.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ArrayEuclideanDistance
  extends AbstractArrayDistance {

  /** for serialization. */
  private static final long serialVersionUID = 810451252808629187L;

  /**
   * Returns the name of the distance.
   * 
   * @return		the name
   */
  @Override
  protected String getDistanceName() {
    return "Euclidean";
  }

  /**
   * Calculates the distance between the two arrays.
   * 
   * @param first	the first array
   * @param second	the second array
   * @return		the distance
   */
  @Override
  protected double calcDistance(double[] first, double[] second) {
    double	result;
    int		i;
    
    result = 0.0;

    for (i = 0; i < first.length; i++)
      result += Math.pow(first[i] - second[i], 2);
    
    result = Math.sqrt(result);
    
    return result;
  }
}
