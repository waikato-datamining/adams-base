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
 * ConsensusOrVote.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package weka.classifiers.meta;

import weka.classifiers.AbstainingClassifier;
import weka.classifiers.MultipleClassifiersCombiner;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.ModelOutputHandler;
import weka.core.SelectedTag;
import weka.core.Utils;
import weka.core.WekaOptionUtils;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * If the required minimum number of classifiers of the ensemble agree on a label, then this label is predicted. Otherwise, Vote with majority rule is used.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p>
 *
 * <pre> -support &lt;value&gt;
 *  The percentage (0-1 excl) or number of base-classifiers (&gt;= 1) that need to chose the label in order to predict it
 *  (default: 1.0)</pre>
 *
 * <pre> -suppress-model-output
 *  If enabled, suppresses any large model output.</pre>
 *
 * <pre> -B &lt;classifier specification&gt;
 *  Full class name of classifier to include, followed
 *  by scheme options. May be specified multiple times.
 *  (default: "weka.classifiers.rules.ZeroR")</pre>
 *
 * <pre> -output-debug-info
 *  If set, classifier is run in debug mode and
 *  may output additional info to the console</pre>
 *
 * <pre> -do-not-check-capabilities
 *  If set, classifier capabilities are not checked before classifier is built
 *  (use with caution).</pre>
 *
 * <pre> -num-decimal-places
 *  The number of decimal places for the output of numbers in the model (default 2).</pre>
 *
 * <pre> -batch-size
 *  The desired batch size for batch prediction  (default 100).</pre>
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
 * <pre> -num-decimal-places
 *  The number of decimal places for the output of numbers in the model (default 2).</pre>
 *
 * <pre> -batch-size
 *  The desired batch size for batch prediction  (default 100).</pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ConsensusOrVote
  extends MultipleClassifiersCombiner
  implements ModelOutputHandler, AbstainingClassifier {

  private static final long serialVersionUID = 943666951855888860L;

  public static final String SUPPORT = "support";

  public static final String SUPPRESS_MODEL_OUTPUT = "suppress-model-output";

  /** the percentage (0-1 excl) or number of base-classifiers (>= 1) that need
   * to chose the label in order to predict it. */
  protected double m_Support = getDefaultSupport();

  /** the actual number of classifiers that need to support the label. */
  protected int m_ActualSupport;

  /** the ensemble. */
  protected Vote m_Vote = null;

  /** whether to suppress the model output. */
  protected boolean m_SuppressModelOutput = false;

  /**
   * Returns a string describing classifier
   *
   * @return 		a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return
      "If the required minimum number of classifiers of the ensemble agree on "
        + "a label, then this label is predicted. "
	+ "Otherwise, Vote with majority rule is used.";
  }

  /**
   * Returns the percentage (0-1 excl) or number of base-classifiers (>= 1) that need
   * to chose the label in order to predict it.
   *
   * @return 		the default
   */
  protected double getDefaultSupport() {
    return 1.0;
  }

  /**
   * Sets the percentage (0-1 excl) or number of base-classifiers (>= 1) that need
   * to chose the label in order to predict it.
   *
   * @param value 	the support
   */
  public void setSupport(double value) {
    if (value > 0)
      m_Support = value;
    else
      System.err.println("Support must meet >0, provided: " + value);
  }

  /**
   * Returns the percentage (0-1 excl) or number of base-classifiers (>= 1) that need
   * to chose the label in order to predict it.
   *
   * @return 		the support
   */
  public double getSupport() {
    return m_Support;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String supportTipText() {
    return "The percentage (0-1 excl) or number of base-classifiers (>= 1) that need to chose the label in order to predict it";
  }

  /**
   * Sets whether to output the model with the toString() method or not.
   *
   * @param value 	true if to suppress model output
   */
  public void setSuppressModelOutput(boolean value) {
    m_SuppressModelOutput = value;
  }

  /**
   * Returns whether to output the model with the toString() method or not.
   *
   * @return 		the label index
   */
  public boolean getSuppressModelOutput() {
    return m_SuppressModelOutput;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String suppressModelOutputTipText() {
    return "If enabled, suppresses any large model output.";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  @Override
  public Enumeration listOptions() {
    Vector result = new Vector();
    WekaOptionUtils.addOption(result, supportTipText(), "" + getDefaultSupport(), SUPPORT);
    WekaOptionUtils.addFlag(result, suppressModelOutputTipText(), SUPPRESS_MODEL_OUTPUT);
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
    setSupport(WekaOptionUtils.parse(options, SUPPORT, getDefaultSupport()));
    setSuppressModelOutput(Utils.getFlag(SUPPRESS_MODEL_OUTPUT, options));
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
    WekaOptionUtils.add(result, SUPPORT, getSupport());
    WekaOptionUtils.add(result, SUPPRESS_MODEL_OUTPUT, getSuppressModelOutput());
    WekaOptionUtils.add(result, super.getOptions());
    return WekaOptionUtils.toArray(result);
  }

  /**
   * Returns the ensemble's capabilities.
   *
   * @return		the capabilities
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
   * Builds the ensemble.
   *
   * @param data	the training data
   * @throws Exception	if training fails
   */
  @Override
  public void buildClassifier(Instances data) throws Exception {
    getCapabilities().testWithFail(data);

    if (m_Support >= 1)
      m_ActualSupport = (int) m_Support;
    else
      m_ActualSupport = (int) Math.round(m_Support * m_Classifiers.length);
    if (getDebug())
      System.out.println("Actual support: " + m_ActualSupport);

    m_Vote = new Vote();
    m_Vote.setCombinationRule(new SelectedTag(Vote.MAJORITY_VOTING_RULE, Vote.TAGS_RULES));
    m_Vote.setClassifiers(m_Classifiers);
    m_Vote.buildClassifier(data);
  }

  /**
   * Determines whether to abstain from the prediction.
   *
   * @param instance		the instance to make the prediction for
   * @return			true if to abstain
   * @throws Exception		if prediction fails
   */
  protected boolean isAbstaining(Instance instance) throws Exception {
    int[]	support;
    double	label;
    int		i;
    int 	highestIndex;
    int		highestCount;

    // determine labels
    support = new int[instance.classAttribute().numValues()];
    for (i = 0; i < m_Classifiers.length; i++) {
      label = m_Classifiers[i].classifyInstance(instance);
      if (!Utils.isMissingValue(label))
	support[(int) label]++;
    }

    // determine label with highest count
    highestIndex = -1;
    highestCount = 0;
    for (i = 0; i < support.length; i++) {
      if ((support[i] > 0) && (support[i] > highestCount)) {
        highestIndex = i;
        highestCount = support[i];
      }
    }

    if (support[highestIndex] < m_ActualSupport)
      highestIndex = -1;

    return (highestIndex == -1);
  }

  /**
   * Predicts the class memberships for a given instance. If an instance is
   * unclassified, the returned array elements must be all zero. If the class is
   * numeric, the array must consist of only one element, which contains the
   * predicted value.
   *
   * @param instance the instance to be classified
   * @return an array containing the estimated membership probabilities of the
   *         test instance in each class or the numeric prediction, zeroed array if abstained
   * @throws Exception if distribution could not be computed successfully
   */
  @Override
  public double[] distributionForInstance(Instance instance) throws Exception {
    if (canAbstain() && isAbstaining(instance))
      return new double[instance.classAttribute().numValues()];
    else
      return m_Vote.distributionForInstance(instance);
  }

  /**
   * Predicts the class label index for the given instance.
   *
   * @param instance		the instance to make the prediction for
   * @return			the class label index, missing value if abstained
   * @throws Exception		if prediction fails
   */
  @Override
  public double classifyInstance(Instance instance) throws Exception {
    if (canAbstain() && isAbstaining(instance))
      return Utils.missingValue();
    else
      return m_Vote.classifyInstance(instance);
  }

  /**
   * Whether abstaining is possible, e.g., used in meta-classifiers.
   *
   * @return		true if abstaining is possible
   */
  public boolean canAbstain() {
    return (m_Classifiers.length > 1);
  }

  /**
   * The prediction that made the classifier abstain.
   *
   * @param inst	the instance to get the prediction for
   * @return		the prediction
   * @throws Exception	if fails to make prediction
   */
  public double getAbstentionClassification(Instance inst) throws Exception {
    return m_Vote.classifyInstance(inst);
  }

  /**
   * The class distribution that made the classifier abstain.
   *
   * @param inst	the instance to get the prediction for
   * @return		the class distribution
   * @throws Exception	if fails to make prediction
   */
  public double[] getAbstentionDistribution(Instance inst) throws Exception {
    return m_Vote.distributionForInstance(inst);
  }

  /**
   * Outputs the ensemble model.
   *
   * @return		the model
   */
  @Override
  public String toString() {
    StringBuilder	result;
    int			i;

    if (m_Vote == null)
      return "No model built yet";

    result = new StringBuilder();
    result.append(getClass().getSimpleName() + "\n");
    result.append(getClass().getSimpleName().replaceAll(".", "=") + "\n\n");
    result.append("Can abstain: " + canAbstain() + "\n");
    result.append("Support: " + m_Support + "\n");
    result.append("Actual support: " + m_ActualSupport + "\n");

    if (!m_SuppressModelOutput) {
      for (i = 0; i < m_Classifiers.length; i++) {
	result.append("\n");
	result.append("Classifier #" + (i + 1) + "\n");
	result.append(new String("Classifier #" + (i + 1)).replaceAll(".", "-") + "\n\n");
	result.append(m_Classifiers[i].toString());
      }
    }

    return result.toString();
  }
}
