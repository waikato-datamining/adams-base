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
 * ThresholdedBinaryClassification.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package weka.classifiers.meta;

import adams.data.weka.WekaLabelIndex;
import weka.classifiers.SingleClassifierEnhancer;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.ModelOutputHandler;
import weka.core.Utils;
import weka.core.WekaOptionUtils;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * Meta classifier for binary classification problems that allows to specify a minimum probability threshold for one of the labels. If this label achieves at least this probability then this label gets chosen, otherwise the other one.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre> -label &lt;value&gt;
 *  The index of the label to check.
 *  (default: first)</pre>
 * 
 * <pre> -min-probability &lt;value&gt;
 *  The minimum probability for the label (0-1).
 *  (default: 0.5)</pre>
 * 
 * <pre> -suppress-model-output
 *  If enabled, suppresses any large model output.</pre>
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
public class ThresholdedBinaryClassification
  extends SingleClassifierEnhancer
  implements ModelOutputHandler {

  private static final long serialVersionUID = -4208248826900762835L;

  /** the label to check. */
  protected WekaLabelIndex m_Label = new WekaLabelIndex(WekaLabelIndex.FIRST);

  /** the index of the label to check. */
  protected int m_ActualLabel;

  /** the index of the other label. */
  protected int m_OtherLabel;

  /** the minimum probability for the label. */
  protected double m_MinProbability = getDefaultMinProbability();

  /** whether to suppress the model output. */
  protected boolean m_SuppressModelOutput = false;

  /**
   * Returns a string describing classifier.
   *
   * @return 		a description suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return
      "Meta classifier for binary classification problems that allows to "
	+ "specify a minimum probability threshold for one of the labels. "
	+ "If this label achieves at least this probability then this label "
	+ "gets chosen, otherwise the other one.";
  }

  /**
   * Returns the default label index.
   *
   * @return 		the default
   */
  protected WekaLabelIndex getDefaultLabel() {
    return new WekaLabelIndex(WekaLabelIndex.FIRST);
  }

  /**
   * Sets the label index to use.
   *
   * @param value 	the label index
   */
  public void setLabel(WekaLabelIndex value) {
    m_Label = value;
  }

  /**
   * Returns the label index.
   *
   * @return 		the label index
   */
  public WekaLabelIndex getLabel() {
    return m_Label;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String labelTipText() {
    return "The index of the label to check.";
  }

  /**
   * Returns the default minimum probability.
   *
   * @return 		the default
   */
  protected double getDefaultMinProbability() {
    return 0.5;
  }

  /**
   * Sets the minimum probability for the selected label.
   *
   * @param value 	the probability
   */
  public void setMinProbability(double value) {
    if ((value >= 0.0) && (value <= 1.0))
      m_MinProbability = value;
    else
      System.err.println("Minimum probability must satisfy 0 <= x <= 1.0, provided: " + value);
  }

  /**
   * Returns the minimum probability for the selected label.
   *
   * @return 		the probability
   */
  public double getMinProbability() {
    return m_MinProbability;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String minProbabilityTipText() {
    return "The minimum probability for the label (0-1).";
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
    WekaOptionUtils.addOption(result, labelTipText(), "" + getDefaultLabel().getIndex(), "label");
    WekaOptionUtils.addOption(result, minProbabilityTipText(), "" + getDefaultMinProbability(), "min-probability");
    WekaOptionUtils.addFlag(result, suppressModelOutputTipText(), "suppress-model-output");
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
    setLabel(new WekaLabelIndex(WekaOptionUtils.parse(options, "label", getDefaultLabel().getIndex())));
    setMinProbability(WekaOptionUtils.parse(options, "min-probability", getDefaultMinProbability()));
    setSuppressModelOutput(Utils.getFlag("suppress-model-output", options));
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
    WekaOptionUtils.add(result, "label", getLabel().getIndex());
    WekaOptionUtils.add(result, "min-probability", getMinProbability());
    WekaOptionUtils.add(result, "suppress-model-output", getSuppressModelOutput());
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
    result.disableAllClasses();
    result.enable(Capability.BINARY_CLASS);
    result.disable(Capability.UNARY_CLASS);

    return result;
  }

  /**
   * Builds the classifier with the training data.
   *
   * @param data	the training data
   * @throws Exception
   */
  @Override
  public void buildClassifier(Instances data) throws Exception {
    getCapabilities().testWithFail(data);
    m_Classifier.buildClassifier(data);
    m_Label.setData(data.classAttribute());
    m_ActualLabel = m_Label.getIntIndex();
    if (m_ActualLabel == 0)
      m_OtherLabel = 1;
    else
      m_OtherLabel = 0;
  }

  /**
   * Returns the class distribution for the instance.
   *
   * @param instance	the instance to make the prediction for
   * @return		the class distribution
   * @throws Exception	if classification fails
   */
  @Override
  public double[] distributionForInstance(Instance instance) throws Exception {
    double[]	result;

    result = m_Classifier.distributionForInstance(instance);

    if (result[m_ActualLabel] >= m_MinProbability) {
      result                = new double[2];
      result[m_ActualLabel] = 1.0;
    }
    else {
      result               = new double[2];
      result[m_OtherLabel] = 1.0;
    }

    return result;
  }

  /**
   * Returns the classifier's model.
   *
   * @return		the model
   */
  @Override
  public String toString() {
    StringBuilder   result;

    result = new StringBuilder();
    result.append(getClass().getSimpleName() + "\n");
    result.append(getClass().getSimpleName().replaceAll(".", "=") + "\n\n");
    result.append("Label: " + m_Label.getIndex() + "\n");
    result.append("Actual label index: " + m_ActualLabel + "\n");
    result.append("Min probability: " + m_MinProbability + "\n");
    result.append("\n");

    if (m_SuppressModelOutput)
      result.append("Model suppressed");
    else
      result.append(m_Classifier.toString());

    return result.toString();
  }
}
