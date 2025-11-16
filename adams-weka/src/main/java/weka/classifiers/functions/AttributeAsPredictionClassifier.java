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
 * AttributeAsPredictionClassifier.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package weka.classifiers.functions;

import adams.data.weka.WekaAttributeIndex;
import weka.classifiers.simple.AbstractSimpleClassifier;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.WekaException;

/**
 * Simply uses the specified attribute as prediction output.
 * Workaround for making co-variables available to Stacking.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class AttributeAsPredictionClassifier
  extends AbstractSimpleClassifier {

  private static final long serialVersionUID = -4216755838428894160L;

  /** the attribute to use as prediction. */
  protected WekaAttributeIndex m_AttIndex;

  /** the actual attribute index. */
  protected int m_ActualIndex;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Simply uses the specified attribute as prediction output.\n"
	     + "Workaround for making co-variables available to Stacking.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "att-index", "attIndex",
      new WekaAttributeIndex("first"));
  }

  /**
   * Sets the attribute to use for prediction.
   *
   * @param value	the index
   */
  public void setAttIndex(WekaAttributeIndex value) {
    m_AttIndex = value;
    reset();
  }

  /**
   * Returns the attribute to use for prediction.
   *
   * @return		the index
   */
  public WekaAttributeIndex getAttIndex() {
    return m_AttIndex;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String attIndexTipText() {
    return "The attribute to use for predictions.";
  }

  /**
   * Returns the Capabilities of this classifier.
   *
   * @return the capabilities of this object
   * @see Capabilities
   */
  @Override
  public Capabilities getCapabilities() {
    Capabilities result;

    result = new Capabilities(this);

    result.enable(Capability.NUMERIC_ATTRIBUTES);
    result.enable(Capability.MISSING_VALUES);

    result.enable(Capability.NUMERIC_CLASS);
    result.enable(Capability.MISSING_CLASS_VALUES);

    result.setMinimumNumberInstances(0);

    return result;
  }

  /**
   * Generates a classifier. Must initialize all fields of the classifier
   * that are not being set via options (ie. multiple calls of buildClassifier
   * must always lead to the same result). Must not change the dataset
   * in any way.
   *
   * @param data set of instances serving as training data
   * @throws Exception if the classifier has not been
   *                   generated successfully
   */
  @Override
  public void buildClassifier(Instances data) throws Exception {
    getCapabilities().testWithFail(data);

    m_AttIndex.setData(data);
    m_ActualIndex = m_AttIndex.getIntIndex();

    if (data.attribute(m_ActualIndex).type() != data.classAttribute().type())
      throw new WekaException("Predicted attribute must be the same type as the class!");
  }

  /**
   * Classifies the given test instance. The instance has to belong to a dataset
   * when it's being classified.
   *
   * @param instance 	the instance to be classified
   * @return 		the predicted most likely class for the instance or
   *         		Utils.missingValue() if no prediction is made
   * @throws Exception 	if an error occurred during the prediction
   */
  @Override
  public double classifyInstance(Instance instance) throws Exception {
    return instance.value(m_ActualIndex);
  }

  /**
   * Returns a string representation of the options.
   *
   * @return		 a string representation
   */
  @Override
  public String toString() {
    return "Attribute used for prediction: " + m_AttIndex.getIndex();
  }
}
