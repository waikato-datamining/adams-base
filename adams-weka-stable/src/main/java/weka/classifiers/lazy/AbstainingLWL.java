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
 * AbstainingLWL.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package weka.classifiers.lazy;

import weka.classifiers.AbstainingClassifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

/**
 * LWL variant that supports abstaining if the base classifier is able to.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class AbstainingLWL
extends LWLSynchro
implements AbstainingClassifier {

  /** for serialization. */
  private static final long serialVersionUID = 2392909523778511111L;

  /** whether the base classifier can abstain. */
  protected boolean m_CanAbstain = false;

  /**
   * Returns a string describing classifier.
   * 
   * @return 		a description suitable for
   * 			displaying in the explorer/experimenter gui
   */
  @Override
  public String globalInfo() {
    return 
	super.globalInfo() + "\n\n"
	+ "Supports abstaining if the base classifier is able to do that.";
  }

  /**
   * Generates the classifier.
   *
   * @param instances set of instances serving as training data 
   * @throws Exception if the classifier has not been generated successfully
   */
  @Override
  public void buildClassifier(Instances instances) throws Exception {
    super.buildClassifier(instances);
    m_CanAbstain = (m_ZeroR == null) && (m_Classifier instanceof AbstainingClassifier) && ((AbstainingClassifier) m_Classifier).canAbstain();
  }

  /**
   * Whether abstaining is possible, e.g., used in meta-classifiers.
   * 
   * @return		true if abstaining is possible
   */
  @Override
  public boolean canAbstain() {
    return m_CanAbstain;
  }

  /**
   * The prediction that made the classifier abstain.
   * 
   * @param inst	the instance to get the prediction for
   * @return		the prediction, {@link Utils#missingValue()} if abstaining is not possible
   * @throws Exception	if fails to make prediction
   */
  @Override
  public synchronized double getAbstentionClassification(Instance inst) throws Exception {
    if (m_CanAbstain) {
      build(inst);
      if (m_Train.numInstances() == 0)
	throw new Exception("No training instances!");
      return ((AbstainingClassifier) m_Classifier).getAbstentionClassification(inst);
    }
    else
      return Utils.missingValue();
  }

  /**
   * The class distribution that made the classifier abstain.
   * 
   * @param inst	the instance to get the prediction for
   * @return		the class distribution, null if abstaining is not possible
   * @throws Exception	if fails to make prediction
   */
  @Override
  public synchronized double[] getAbstentionDistribution(Instance inst) throws Exception {
    if (m_CanAbstain){
      if (m_ZeroR != null)
	return m_ZeroR.distributionForInstance(inst);
      if (m_Train.numInstances() == 0)
	throw new Exception("No training instances!");
      build(inst);
      return ((AbstainingClassifier) m_Classifier).getAbstentionDistribution(inst);
    }
    else
      return null;
  }

  /**
   * Returns a description of this classifier.
   *
   * @return a description of this classifier as a string.
   */
  @Override
  public String toString() {
    StringBuilder	result;

    result = new StringBuilder(super.toString());
    result.append("\nCan abstain: " + m_CanAbstain + "\n");

    return result.toString();
  }
}
