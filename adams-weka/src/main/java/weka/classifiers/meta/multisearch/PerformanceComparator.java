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
 * PerformanceComparator.java
 * Copyright (C) 2008-2014 University of Waikato, Hamilton, New Zealand
 */

package weka.classifiers.meta.multisearch;

import java.io.Serializable;
import java.util.Comparator;

import weka.classifiers.meta.MultiSearch;

/**
 * A concrete Comparator for the Performance class.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see Performance
 */
public class PerformanceComparator
  implements Comparator<Performance>, Serializable {

  /** for serialization. */
  private static final long serialVersionUID = 6507592831825393847L;

  /** the performance measure to use for comparison.
   * @see MultiSearch#TAGS_EVALUATION */
  protected int m_Evaluation;

  /**
   * initializes the comparator with the given performance measure.
   *
   * @param evaluation	the performance measure to use
   * @see MultiSearch#TAGS_EVALUATION
   */
  public PerformanceComparator(int evaluation) {
    super();

    m_Evaluation = evaluation;
  }

  /**
   * returns the performance measure that's used to compare the objects.
   *
   * @return the performance measure
   * @see MultiSearch#TAGS_EVALUATION
   */
  public int getEvaluation() {
    return m_Evaluation;
  }

  /**
   * Compares its two arguments for order. Returns a negative integer,
   * zero, or a positive integer as the first argument is less than,
   * equal to, or greater than the second.
   *
   * @param o1 	the first performance
   * @param o2 	the second performance
   * @return 		the order
   */
  public int compare(Performance o1, Performance o2) {
    int	result;
    double	p1;
    double	p2;

    if (o1.getEvaluation() != o2.getEvaluation())
      throw new IllegalArgumentException("Comparing different types of performances!");

    p1 = o1.getPerformance();
    p2 = o2.getPerformance();

    if (p1 < p2)
      result = -1;
    else if (p1 > p2)
      result = 1;
    else
      result = 0;

    // only correlation coefficient/accuracy/kappa obey to this order, for the
    // errors (and the combination of all three), the smaller the number the
    // better -> hence invert them
    if (    (getEvaluation() != Performance.EVALUATION_CC)
        && (getEvaluation() != Performance.EVALUATION_ACC)
        && (getEvaluation() != Performance.EVALUATION_KAPPA) )
      result = -result;

    return result;
  }

  /**
   * Indicates whether some other object is "equal to" this Comparator.
   *
   * @param obj	the object to compare with
   * @return		true if the same evaluation type is used
   */
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof PerformanceComparator))
      throw new IllegalArgumentException("Must be PerformanceComparator!");

    return (m_Evaluation == ((PerformanceComparator) obj).m_Evaluation);
  }
}