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
 * AbstainingClassifierWrapper.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package weka.classifiers.meta;

import weka.classifiers.AbstainingClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.SingleClassifierEnhancer;
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
 * Wraps an abstaining classifier and allows turning on/of abstaining.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre> -turn-off-abstaining &lt;value&gt;
 *  If enabled, abstaining of the base classifier is turned off.
 *  (default: false)</pre>
 * 
 * <pre> -W
 *  Full name of base classifier.
 *  (default: weka.classifiers.meta.AbstainMinimumProbability)</pre>
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
 * Options specific to classifier weka.classifiers.meta.AbstainMinimumProbability:
 * </pre>
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
public class AbstainingClassifierWrapper
  extends SingleClassifierEnhancer
  implements AbstainingClassifier {

  private static final long serialVersionUID = 5699323936859571421L;

  public static final String TURN_OFF_ABSTAINING = "turn-off-abstaining";

  /** whether to turn off abstaining. */
  protected boolean m_TurnOffAbstaining = false;

  /** whether the base classifier can abstain. */
  protected boolean m_CanAbstain = false;

  /**
   * Initializes the classifier.
   */
  public AbstainingClassifierWrapper() {
    super();
    m_Classifier = new AbstainMinimumProbability();
  }

  /**
   * String describing default classifier.
   */
  @Override
  protected String defaultClassifierString() {
    return AbstainMinimumProbability.class.getName();
  }

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
   * Set the base learner.
   *
   * @param value the classifier to use.
   */
  @Override
  public void setClassifier(Classifier value) {
    if (value instanceof AbstainingClassifier)
      super.setClassifier(value);
    else
      System.err.println(
        getClass().getName() + ": an abstaining classifier is required, provided: "
          + value.getClass().getName());
  }

  /**
   * Sets whether to turn off abstaining of the base classifier.
   *
   * @param value true if to turn off abstaining
   */
  public void setTurnOffAbstaining(boolean value) {
    m_TurnOffAbstaining = value;
  }

  /**
   * Returns whether abstaining of the base classifier is turned off.
   *
   * @return value true if abstaining is turned off
   */
  public boolean getTurnOffAbstaining() {
    return m_TurnOffAbstaining;
  }

  /**
   * Returns the tip text for this property
   * @return tip text for this property suitable for
   * displaying in the explorer/experimenter gui
   */
  public String turnOffAbstainingTipText() {
    return "If enabled, abstaining of the base classifier is turned off.";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  @Override
  public Enumeration listOptions() {
    Vector result = new Vector();
    WekaOptionUtils.addOption(result, turnOffAbstainingTipText(), "false", TURN_OFF_ABSTAINING);
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
    setTurnOffAbstaining(Utils.getFlag(TURN_OFF_ABSTAINING, options));
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
    WekaOptionUtils.add(result, TURN_OFF_ABSTAINING, getTurnOffAbstaining());
    WekaOptionUtils.add(result, super.getOptions());
    return WekaOptionUtils.toArray(result);
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
    m_CanAbstain = (m_Classifier instanceof AbstainingClassifier) && ((AbstainingClassifier) m_Classifier).canAbstain();
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
    if (m_TurnOffAbstaining)
      return ((AbstainingClassifier) m_Classifier).getAbstentionDistribution(instance);
    else
      return m_Classifier.distributionForInstance(instance);
  }

  /**
   * Whether abstaining is possible, e.g., used in meta-classifiers.
   *
   * @return		true if abstaining is possible
   */
  public boolean canAbstain() {
    return m_CanAbstain && !m_TurnOffAbstaining;
  }

  /**
   * The prediction that made the classifier abstain.
   *
   * @param inst	the instance to get the prediction for
   * @return		the prediction
   * @throws Exception	if fails to make prediction
   */
  public double getAbstentionClassification(Instance inst) throws Exception {
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
  public double[] getAbstentionDistribution(Instance inst) throws Exception {
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
    result.append("Abstaining turned off: " + m_TurnOffAbstaining + "\n");
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
    runClassifier(new AbstainingClassifierWrapper(), args);
  }
}
