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
 * WekaRegressor.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.ml.model.regression;

import adams.core.option.OptionUtils;
import adams.ml.capabilities.Capabilities;
import adams.ml.data.Dataset;
import adams.ml.data.WekaConverter;
import weka.classifiers.functions.LinearRegressionJ;
import weka.core.Instances;

/**
 <!-- globalinfo-start -->
 * Wraps around a Weka classifier that handles numeric classes (= regression).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-strict-capabilities &lt;boolean&gt; (property: strictCapabilities)
 * &nbsp;&nbsp;&nbsp;If enabled, a strict capabilities test is performed; otherwise, it is attempted 
 * &nbsp;&nbsp;&nbsp;to adjust the data to fit the algorithm's capabilities.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-classifier &lt;weka.classifiers.Classifier&gt; (property: classifier)
 * &nbsp;&nbsp;&nbsp;The classifier to use.
 * &nbsp;&nbsp;&nbsp;default: weka.classifiers.functions.LinearRegressionJ -S 0 -R 1.0E-8 -num-decimal-places 4
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaRegressor
  extends AbstractRegressor {

  private static final long serialVersionUID = -4086036132431888958L;

  /** the weka classifier to use. */
  protected weka.classifiers.Classifier m_Classifier;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Wraps around a Weka classifier that handles numeric classes (= regression).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "classifier", "classifier",
	    new LinearRegressionJ());
  }

  /**
   * Sets the classifier to use.
   *
   * @param value	the classifier
   */
  public void setClassifier(weka.classifiers.Classifier value) {
    m_Classifier = value;
    reset();
  }

  /**
   * Returns the classifier to use.
   *
   * @return		the classifier
   */
  public weka.classifiers.Classifier getClassifier() {
    return m_Classifier;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String classifierTipText() {
    return "The classifier to use.";
  }

  /**
   * Returns the algorithm's capabilities in terms of data.
   *
   * @return		the algorithm's capabilities
   */
  @Override
  public Capabilities getCapabilities() {
    Capabilities	result;

    result = super.getCapabilities();
    result.assign(WekaConverter.convertCapabilities(m_Classifier.getCapabilities()));

    return result;
  }

  /**
   * Builds a model from the data.
   *
   * @param data	the data to use for building the model
   * @return		the generated model
   * @throws Exception	if the build fails
   */
  @Override
  protected RegressionModel doBuildModel(Dataset data) throws Exception {
    Instances			inst;
    weka.classifiers.Classifier	classifier;

    inst       = WekaConverter.toInstances(data);
    classifier = (weka.classifiers.Classifier) OptionUtils.shallowCopy(m_Classifier);
    if (classifier == null)
      throw new Exception("Failed to create shallow copy of classifier: " + OptionUtils.getCommandLine(m_Classifier));

    classifier.buildClassifier(inst);

    return new WekaRegressionModel(classifier, data, inst);
  }
}
