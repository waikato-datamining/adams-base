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
 * AbstractArrayDistance.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.statistics;

/**
 * Ancestor for distance measures.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 4584 $
 * @param <T> the data to process
 */
public abstract class AbstractArrayDistance<T extends Number>
  extends AbstractArrayStatistic<T>
  implements EqualLengthArrayStatistic {

  /** for serialization. */
  private static final long serialVersionUID = 9045810089203101126L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Calculates the " + getDistanceName() + " distance between the first array "
      + "and the remaining arrays. The arrays must be numeric, of course.";
  }
  
  /**
   * Returns the name of the distance.
   * 
   * @return		the name
   */
  protected abstract String getDistanceName();

  /**
   * Returns the length of the stored arrays.
   *
   * @return		the length of the arrays, -1 if none stored
   */
  public int getLength() {
    if (size() > 0)
      return get(0).length;
    else
      return -1;
  }

  /**
   * Returns the minimum number of arrays that need to be present.
   * -1 for unbounded.
   *
   * @return		the minimum number, -1 for unbounded
   */
  @Override
  public int getMin() {
    return 2;
  }

  /**
   * Returns the maximum number of arrays that need to be present.
   * -1 for unbounded.
   *
   * @return		the maximum number, -1 for unbounded
   */
  @Override
  public int getMax() {
    return -1;
  }
  
  /**
   * Calculates the distance between the two arrays.
   * 
   * @param first	the first array
   * @param second	the second array
   * @return		the distance
   */
  protected abstract double calcDistance(double[] first, double[] second);
  
  /**
   * Creates the cell header prefix to use.
   * 
   * @return		the prefix
   */
  protected String createCellHeader() {
    return getDistanceName() + " distance";
  }
  
  /**
   * Generates the actual result.
   *
   * @return		the generated result
   */
  @Override
  protected StatisticContainer doCalculate() {
    StatisticContainer<Double>	result;
    int				i;
    double[]			first;
    double[]			other;

    result = new StatisticContainer<Double>(1, size() - 1);

    first = StatUtils.toDoubleArray(get(0));
    for (i = 1; i < size(); i++) {
      other = StatUtils.toDoubleArray(get(i));
      result.setHeader(i - 1, createCellHeader() + " 1-" + (i+1));
      result.setCell(0, i - 1, calcDistance(first, other));
    }

    return result;
  }
}
