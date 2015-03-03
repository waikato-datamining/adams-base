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
 * AbstainingClassifier.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package weka.classifiers;

import weka.core.Instance;

/**
 * Interface for classifiers that may support abstaining.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface AbstainingClassifier
  extends Classifier {
  
  /**
   * Whether abstaining is possible, e.g., used in meta-classifiers.
   * 
   * @return		true if abstaining is possible
   */
  public boolean canAbstain();
  
  /**
   * The prediction that made the classifier abstain.
   * 
   * @param inst	the instance to get the prediction for
   * @return		the prediction
   * @throws Exception	if fails to make prediction
   */
  public double getAbstentionClassification(Instance inst) throws Exception;
  
  /**
   * The class distribution that made the classifier abstain.
   * 
   * @param inst	the instance to get the prediction for
   * @return		the class distribution
   * @throws Exception	if fails to make prediction
   */
  public double[] getAbstentionDistribution(Instance inst) throws Exception;
}
