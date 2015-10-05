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
 * SuppressModelOutput.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package weka.classifiers.meta;

import weka.classifiers.SingleClassifierEnhancer;
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
 * Meta-classifier that enables the user to suppress the model output.<br/>
 * Useful for ensembles, since their output can be extremely long.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
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
public class SuppressModelOutput
  extends SingleClassifierEnhancer
  implements ModelOutputHandler {

  private static final long serialVersionUID = 6575314591188728994L;

  /** whether to suppress the model output. */
  protected boolean m_SuppressModelOutput = false;

  /**
   * Returns a string describing classifier
   *
   * @return 		a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return
      "Meta-classifier that enables the user to suppress the model output.\n"
	+ "Useful for ensembles, since their output can be extremely long.";
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
    WekaOptionUtils.add(result, "suppress-model-output", getSuppressModelOutput());
    WekaOptionUtils.add(result, super.getOptions());
    return WekaOptionUtils.toArray(result);
  }

  /**
   * Builds the base classifier using the provided data.
   *
   * @param data	the data to use
   * @throws Exception	if build fails or data not supported
   */
  @Override
  public void buildClassifier(Instances data) throws Exception {
    getCapabilities().testWithFail(data);
    m_Classifier.buildClassifier(data);
  }

  /**
   * Classifies the given instance.
   *
   * @param instance	the instance to classify
   * @return		the classification (label index) or regression value
   * @throws Exception	if classification fails
   */
  @Override
  public double classifyInstance(Instance instance) throws Exception {
    return m_Classifier.classifyInstance(instance);
  }

  /**
   * Returns the class distribution for the given instance.
   *
   * @param instance	the instance to obtain the class distribution for
   * @return		the class distribution
   * @throws Exception	if classification fails
   */
  @Override
  public double[] distributionForInstance(Instance instance) throws Exception {
    return m_Classifier.distributionForInstance(instance);
  }

  /**
   * Returns a string representation of the model.
   *
   * @return		the representation
   */
  @Override
  public String toString() {
    if (m_SuppressModelOutput)
      return "Model suppressed";
    else
      return m_Classifier.toString();
  }
}
