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
 * AbstainingCascade.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package weka.classifiers.meta;

import weka.classifiers.MultipleClassifiersCombiner;
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
 * The specified classifiers represent a cascade: if the first one abstains, the second is used (and so on), otherwise the prediction is returned.<br>
 * If all classifiers prior to the last one abstained then the prediction of the last one is returned.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p>
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
public class AbstainingCascade
  extends MultipleClassifiersCombiner
  implements ModelOutputHandler {

  private static final long serialVersionUID = 943666951855888860L;

  public static final String SUPPRESS_MODEL_OUTPUT = "suppress-model-output";

  /** whether to suppress the model output. */
  protected boolean m_SuppressModelOutput = false;

  /** whether the models got built. */
  protected boolean m_Built = false;

  /**
   * Returns a string describing classifier
   *
   * @return 		a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return
      "The specified classifiers represent a cascade: if the first one abstains (or encounters an error making a prediction), "
        + "the second is used (and so on), otherwise the prediction is returned.\n"
        + "If all classifiers prior to the last one abstained then the prediction "
        + "of the last one is returned.";
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
    WekaOptionUtils.add(result, SUPPRESS_MODEL_OUTPUT, getSuppressModelOutput());
    WekaOptionUtils.add(result, super.getOptions());
    return WekaOptionUtils.toArray(result);
  }

  /**
   * Builds the ensemble.
   *
   * @param data	the training data
   * @throws Exception	if training fails
   */
  @Override
  public void buildClassifier(Instances data) throws Exception {
    int		i;

    getCapabilities().testWithFail(data);

    for (i = 0; i < m_Classifiers.length; i++) {
      if (getDebug())
        System.err.println("Building classifier #" + (i+1));
      m_Classifiers[i].buildClassifier(data);
    }

    m_Built = true;
  }

  /**
   * Predicts the class label index for the given instance.
   *
   * @param instance		the instance to make the prediction for
   * @return			the class label index
   * @throws Exception		if prediction fails
   */
  @Override
  public double classifyInstance(Instance instance) throws Exception {
    double	result;
    int		i;

    result = Utils.missingValue();

    for (i = 0; i < m_Classifiers.length; i++) {
      try {
	result = m_Classifiers[i].classifyInstance(instance);
	if (!Utils.isMissingValue(result))
	  break;
      }
      catch (Exception e) {
        // only throw an exception when last classifier fails
        if (i == m_Classifiers.length - 1)
          throw e;
      }
    }

    return result;
  }

  /**
   * Predicts the class memberships for a given instance. If an instance is
   * unclassified, the returned array elements must be all zero. If the class is
   * numeric, the array must consist of only one element, which contains the
   * predicted value.
   *
   * @param instance the instance to be classified
   * @return an array containing the estimated membership probabilities of the
   *         test instance in each class or the numeric prediction
   * @throws Exception if distribution could not be computed successfully
   */
  @Override
  public double[] distributionForInstance(Instance instance) throws Exception {
    double[]	result;
    int		i;

    result = new double[instance.classAttribute().numValues()];

    for (i = 0; i < m_Classifiers.length; i++) {
      try {
	result = m_Classifiers[i].distributionForInstance(instance);
	if (Utils.sum(result) > 0)
	  break;
      }
      catch (Exception e) {
	// only throw an exception when last classifier fails
	if (i == m_Classifiers.length - 1)
	  throw e;
      }
    }

    return result;
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

    if (!m_Built)
      return "No model built yet";

    result = new StringBuilder();
    result.append(getClass().getSimpleName() + "\n");
    result.append(getClass().getSimpleName().replaceAll(".", "=") + "\n\n");

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
