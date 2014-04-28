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
 * StatCalc.java
 * Copyright (C) 2008-2011 University of Waikato, Hamilton, New Zealand
 */

package adams.data.statistics;

import java.io.Serializable;

public class StatCalc
  implements Serializable {

  private static final long serialVersionUID = 5628657151878022137L;

  private int count;   // Number of numbers that have been entered.

  private double sum;  // The sum of all the items that have been entered.

  private double squareSum;  // The sum of the squares of all the items.

  private double min= Double.POSITIVE_INFINITY;

  private double max= Double.NEGATIVE_INFINITY;

  public void enter(double num) {
    // Add the number to the dataset.
    count++;
    if (num < min) {
      min = num;
    }
    if (num > max) {
      max = num;
    }
    sum += num;
    squareSum += num*num;
  }

  public int getCount() {
    // Return number of items that have been entered.
    return count;
  }

  public double getSum() {
    // Return the sum of all the items that have been entered.
    return sum;
  }

  public double getMean() {
    // Return average of all the items that have been entered.
    // Value is Double.NaN if count == 0.
    return sum / count;
  }

  public double getSumSquares(){
    return(squareSum);
  }

  public double getStandardDeviation() {
    // Return standard deviation of all the items that have been entered.
    // Value will be Double.NaN if count == 0.
    double mean = getMean();
    return Math.sqrt( squareSum/count - mean*mean );
  }

  public double getMin() {
    return(min);
  }

  public double  getMax() {
    return(max);
  }
}
