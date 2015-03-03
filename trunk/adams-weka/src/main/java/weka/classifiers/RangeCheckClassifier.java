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
 * RangeCheckClassifier.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package weka.classifiers;

import java.util.List;

import weka.core.Instance;

/**
 * Interface for classifiers that allow checks whether data is outside
 * the training range of the classifier.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface RangeCheckClassifier
  extends Classifier {

  /**
   * Checks the range for the instance. The array contains an entry for each
   * attribute that exceeded the stored ranges.
   * 
   * @param inst	the instance to check
   * @return		the failed checks
   */
  public List<String> checkRangeForInstance(Instance inst);
}
