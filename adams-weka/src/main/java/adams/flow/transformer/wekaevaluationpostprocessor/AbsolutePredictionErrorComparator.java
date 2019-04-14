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
 * AbsolutePredictionErrorComparator.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.wekaevaluationpostprocessor;

import weka.classifiers.evaluation.Prediction;

import java.util.Comparator;

/**
 * Comparator for predictions using the (absolute) prediction error (sorting increasingly).
 */
public class AbsolutePredictionErrorComparator
  implements Comparator<Prediction> {

  /**
   * Compares the error of the predictions.
   *
   * @param o1	the first prediction
   * @param o2	the second prediction
   * @return		the result of the comparison
   */
  @Override
  public int compare(Prediction o1, Prediction o2) {
    return Double.compare(Math.abs(o1.actual() - o1.predicted()), Math.abs(o2.actual() - o2.predicted()));
  }
}
