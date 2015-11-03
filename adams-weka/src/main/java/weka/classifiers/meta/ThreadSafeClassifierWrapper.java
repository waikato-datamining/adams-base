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
 * ThreadSafeClassifierWrapper.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package weka.classifiers.meta;

import weka.classifiers.AbstainingClassifier;
import weka.classifiers.SingleClassifierEnhancer;
import weka.classifiers.ThreadSafeClassifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

/**
 <!-- globalinfo-start -->
 * Wraps an abstaining classifier and allows turning on/of abstaining.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre> -W
 *  Full name of base classifier.
 *  (default: weka.classifiers.rules.ZeroR)</pre>
 * 
 * <pre> -output-debug-info
 *  If set, classifier is run in debug mode and
 *  may output additional info to the console</pre>
 * 
 * <pre> -do-not-check-capabilities
 *  If set, classifier capabilities are not checked before classifier is built
 *  (use with caution).</pre>
 * 
 * <pre> 
 * Options specific to classifier weka.classifiers.rules.ZeroR:
 * </pre>
 * 
 * <pre> -output-debug-info
 *  If set, classifier is run in debug mode and
 *  may output additional info to the console</pre>
 * 
 * <pre> -do-not-check-capabilities
 *  If set, classifier capabilities are not checked before classifier is built
 *  (use with caution).</pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ThreadSafeClassifierWrapper
  extends SingleClassifierEnhancer
  implements AbstainingClassifier, ThreadSafeClassifier {

  private static final long serialVersionUID = 5699323936859571421L;

  /** whether the base classifier can abstain. */
  protected boolean m_CanAbstain = false;

  /**
   * Returns a string describing classifier.
   *
   * @return 		a description suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return "Wraps an abstaining classifier and allows turning on/of abstaining.";
  }

  /**
   * Generates a classifier. Must initialize all fields of the classifier
   * that are not being set via options (ie. multiple calls of buildClassifier
   * must always lead to the same result). Must not change the dataset
   * in any way.
   *
   * @param data set of instances serving as training data
   * @exception Exception if the classifier has not been
   * generated successfully
   */
  @Override
  public synchronized void buildClassifier(Instances data) throws Exception {
    getCapabilities().testWithFail(data);
    m_Classifier.buildClassifier(data);
    m_CanAbstain = (m_Classifier instanceof AbstainingClassifier) && ((AbstainingClassifier) m_Classifier).canAbstain();
  }

  /**
   * Synchronized method for classifying data.
   *
   * @param instance	the instance to classify
   * @return		the classification
   * @throws Exception	if classification fails
   */
  @Override
  public synchronized double classifyInstance(Instance instance) throws Exception {
    return super.classifyInstance(instance);
  }

  /**
   * Returns the class distribution for an instance.
   *
   * @param instance	the instance to get the distribution for
   * @return		the distribution
   * @throws Exception	if classification fails
   */
  @Override
  public synchronized double[] distributionForInstance(Instance instance) throws Exception {
    return m_Classifier.distributionForInstance(instance);
  }

  /**
   * Whether abstaining is possible, e.g., used in meta-classifiers.
   *
   * @return		true if abstaining is possible
   */
  public boolean canAbstain() {
    return m_CanAbstain;
  }

  /**
   * The prediction that made the classifier abstain.
   *
   * @param inst	the instance to get the prediction for
   * @return		the prediction
   * @throws Exception	if fails to make prediction
   */
  public synchronized double getAbstentionClassification(Instance inst) throws Exception {
    if (canAbstain())
      return ((AbstainingClassifier) m_Classifier).getAbstentionClassification(inst);
    else
      return Utils.missingValue();
  }

  /**
   * The class distribution that made the classifier abstain.
   *
   * @param inst	the instance to get the prediction for
   * @return		the class distribution
   * @throws Exception	if fails to make prediction
   */
  public synchronized double[] getAbstentionDistribution(Instance inst) throws Exception {
    if (canAbstain())
      return ((AbstainingClassifier) m_Classifier).getAbstentionDistribution(inst);
    else
      return null;
  }

  /**
   * Returns the model.
   *
   * @return		the model
   */
  @Override
  public String toString() {
    StringBuilder	result;

    result = new StringBuilder();
    result.append(getClass().getSimpleName() + "\n");
    result.append(getClass().getSimpleName().replaceAll(".", "=") + "\n");
    result.append("\n");
    result.append(m_Classifier.toString());
    return result.toString();
  }

  /**
   * Main method for running this class.
   *
   * @param args the options
   */
  public static void main(String[] args) {
    runClassifier(new ThreadSafeClassifierWrapper(), args);
  }
}
