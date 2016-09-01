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
 * TrainTestSplitExperiment.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekamultiexperimenter.experiment;

import adams.core.Utils;
import adams.core.option.OptionUtils;
import adams.flow.container.WekaTrainTestSetContainer;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.RandomSplitGenerator;
import weka.core.Instances;

/**
 * Performs train-test splits.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TrainTestSplitExperiment
  extends AbstractExperiment {

  private static final long serialVersionUID = -4147644361063132314L;

  /** the split percentage. */
  protected double m_Percentage;

  /** whether to preserve the order. */
  protected boolean m_PreserveOrder;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Performs train-test splits for each classifier/dataset combination.\n"
        + "Order can be preserved in the datasets.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "percentage", "percentage",
      66.0, 0.0, 100.0);

    m_OptionManager.add(
      "preserve-order", "preserveOrder",
      false);
  }

  /**
   * Sets the split percentage.
   *
   * @param value	the percentage
   */
  public void setPercentage(double value) {
    m_Percentage = value;
    reset();
  }

  /**
   * Returns the split percentage.
   *
   * @return		the percentage
   */
  public double getPercentage() {
    return m_Percentage;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String percentageTipText() {
    return "The split percentage to use.";
  }

  /**
   * Sets whether to preserve the order.
   *
   * @param value 	true if to preserve
   */
  public void setPreserveOrder(boolean value) {
    m_PreserveOrder = value;
    reset();
  }

  /**
   * Returns whether to preserve the order.
   *
   * @return 		true if to preserve
   */
  public boolean getPreserveOrder() {
    return m_PreserveOrder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String preserveOrderTipText() {
    return "If enabled, no data randomization is occurring before splitting the data into train and test set.";
  }

  /**
   * Evaluates the classifier on the dataset.
   *
   * @param cls		the classifier to evaluate
   * @param data	the dataset to evaluate on
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String evaluate(Classifier cls, Instances data) {
    String			result;
    RandomSplitGenerator	generator;
    WekaTrainTestSetContainer	cont;
    Instances			train;
    Instances			test;
    Classifier			classifier;
    Evaluation 			eval;

    result = null;

    if (!m_PreserveOrder)
      generator = new RandomSplitGenerator(data, m_CurrentRun, m_Percentage / 100.0);
    else
      generator = new RandomSplitGenerator(data, m_Percentage);
    cont  = generator.next();
    train = (Instances) cont.getValue(WekaTrainTestSetContainer.VALUE_TRAIN);
    test  = (Instances) cont.getValue(WekaTrainTestSetContainer.VALUE_TEST);
    try {
      classifier = (Classifier) OptionUtils.shallowCopy(cls);
      classifier.buildClassifier(train);
      eval       = new Evaluation(train);
      eval.evaluateModel(classifier, test);
      addMetrics(cls, data, eval);
    }
    catch (Exception e) {
      result = Utils.handleException(this, "Failed to evaluate classifier on train/test split!", e);
    }

    return result;
  }
}
