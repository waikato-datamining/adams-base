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
 * AbstainMinimumProbability.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package weka.classifiers.meta;

import weka.classifiers.AbstainingClassifier;
import weka.classifiers.SingleClassifierEnhancer;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.WekaOptionUtils;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * Abstains if the probability of the chosen class label is below the specified threshold.
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
 * <pre> -min-probability &lt;value&gt;
 *  The minimum probability that the chosen label must meet.
 *  (default: 0.8)</pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class AbstainMinimumProbability
  extends SingleClassifierEnhancer
  implements AbstainingClassifier {

  private static final long serialVersionUID = 5699323936859571421L;

  public static final String MIN_PROBABILITY = "min-probability";

  /** the minimum probability that the classification must meet (0-1). */
  protected double m_MinProbability = getDefaultMinProbability();

  /** the number of class labels. */
  protected int m_NumLabels;

  /**
   * Returns a string describing classifier.
   *
   * @return 		a description suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return "Abstains if the probability of the chosen class label is below the specified threshold.";
  }

  /**
   * Returns the default minimum probability that the chosen class label must meet.
   *
   * @return value the default
   */
  protected double getDefaultMinProbability() {
    return 0.8;
  }

  /**
   * Sets the minimum probability that the chosen class label must meet.
   *
   * @param value the minimum probability
   */
  public void setMinProbability(double value) {
    if ((value >= 0) && (value <= 1.0))
      m_MinProbability = value;
    else
      System.err.println("Min probability must meet 0 < x < 1, provided: " + value);
  }

  /**
   * Returns the minimum probability that the chosen class label must meet.
   *
   * @return value the minimum probability
   */
  public double getMinProbability() {
    return m_MinProbability;
  }

  /**
   * Returns the tip text for this property
   * @return tip text for this property suitable for
   * displaying in the explorer/experimenter gui
   */
  public String minProbabilityTipText() {
    return "The minimum probability that the chosen label must meet.";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  @Override
  public Enumeration listOptions() {
    Vector result = new Vector();
    WekaOptionUtils.addOption(result, minProbabilityTipText(), ""+ getDefaultMinProbability(), MIN_PROBABILITY);
    WekaOptionUtils.add(result, super.listOptions());
    return WekaOptionUtils.toEnumeration(result);
  }

  /**
   * Sets the OptionHandler's options using the given list. All options
   * will be set (or reset) during this call (i.e. incremental setting
   * of options is not possible).
   *
   * @param options the list of options as an array of strings
   * @throws Exception if an option is not supported
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    setMinProbability(WekaOptionUtils.parse(options, MIN_PROBABILITY, getDefaultMinProbability()));
    super.setOptions(options);
  }

  /**
   * Gets the current option settings for the OptionHandler.
   *
   * @return the list of current option settings as an array of strings
   */
  @Override
  public String[] getOptions() {
    List<String> result = new ArrayList<>();
    WekaOptionUtils.add(result, MIN_PROBABILITY, getMinProbability());
    WekaOptionUtils.add(result, super.getOptions());
    return WekaOptionUtils.toArray(result);
  }

  /**
   * Returns default capabilities of the base classifier.
   *
   * @return      the capabilities of the base classifier
   */
  @Override
  public Capabilities getCapabilities() {
    Capabilities	result;

    result = super.getCapabilities();
    result.disable(Capability.DATE_CLASS);
    result.disable(Capability.NUMERIC_CLASS);
    result.disable(Capability.RELATIONAL_CLASS);

    return result;
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
  public void buildClassifier(Instances data) throws Exception {
    getCapabilities().testWithFail(data);
    m_Classifier.buildClassifier(data);
    m_NumLabels = data.classAttribute().numValues();
  }

  /**
   * Whether abstaining is possible, e.g., used in meta-classifiers.
   *
   * @return		true if abstaining is possible
   */
  @Override
  public boolean canAbstain() {
    return true;
  }

  /**
   * Returns the class distribution for an instance.
   *
   * @param instance	the instance to get the distribution for
   * @return		the distribution
   * @throws Exception	if classification fails
   */
  @Override
  public double[] distributionForInstance(Instance instance) throws Exception {
    double[]	result;
    double	max;

    result = m_Classifier.distributionForInstance(instance);
    max    = result[Utils.maxIndex(result)];
    if (max < m_MinProbability)
      return new double[m_NumLabels];
    else
      return result;
  }

  /**
   * The prediction that made the classifier abstain.
   *
   * @param inst	the instance to get the prediction for
   * @return		the prediction
   * @throws Exception	if fails to make prediction
   */
  @Override
  public double getAbstentionClassification(Instance inst) throws Exception {
    if (m_Classifier instanceof AbstainingClassifier)
      return ((AbstainingClassifier) m_Classifier).getAbstentionClassification(inst);
    else
      return m_Classifier.classifyInstance(inst);
  }

  /**
   * The class distribution that made the classifier abstain.
   *
   * @param inst	the instance to get the prediction for
   * @return		the class distribution
   * @throws Exception	if fails to make prediction
   */
  @Override
  public double[] getAbstentionDistribution(Instance inst) throws Exception {
    if (m_Classifier instanceof AbstainingClassifier)
      return ((AbstainingClassifier) m_Classifier).getAbstentionDistribution(inst);
    else
      return m_Classifier.distributionForInstance(inst);
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
    result.append("Minimum probability: " + m_MinProbability + "\n");
    result.append("\n");
    result.append(m_Classifier.toString());
    return result.toString();
  }
}
