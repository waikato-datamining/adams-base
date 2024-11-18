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
 * StoppableEvaluation.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package weka.classifiers.evaluation;

import adams.core.Stoppable;
import adams.core.StoppableWithFeedback;
import adams.core.StoppedException;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.CostMatrix;
import weka.classifiers.evaluation.output.prediction.AbstractOutput;
import weka.core.BatchPredictor;
import weka.core.Instances;

import java.util.Random;

/**
 * Extended Evaluation class that can stop its evaluation processes better.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class StoppableEvaluation
  extends Evaluation
  implements StoppableWithFeedback {

  private static final long serialVersionUID = -4728485124248488867L;

  /** whether the execution was stopped. */
  protected boolean m_Stopped;

  /** the current classifier that is being evaluated. */
  protected transient Classifier m_CurrentClassifier;

  /**
   * Initializes all the counters for the evaluation. Use
   * <code>useNoPriors()</code> if the dataset is the test set and you can't
   * initialize with the priors from the training set via
   * <code>setPriors(Instances)</code>.
   *
   * @param data set of training instances, to get some header information and
   *             prior class distribution information
   * @throws Exception if the class is not defined
   * @see #useNoPriors()
   * @see #setPriors(Instances)
   */
  public StoppableEvaluation(Instances data) throws Exception {
    super(data);
  }

  /**
   * Initializes all the counters for the evaluation and also takes a cost
   * matrix as parameter. Use <code>useNoPriors()</code> if the dataset is the
   * test set and you can't initialize with the priors from the training set via
   * <code>setPriors(Instances)</code>.
   *
   * @param data       set of training instances, to get some header information and
   *                   prior class distribution information
   * @param costMatrix the cost matrix---if null, default costs will be used
   * @throws Exception if cost matrix is not compatible with data, the class is
   *                   not defined or the class is numeric
   * @see #useNoPriors()
   * @see #setPriors(Instances)
   */
  public StoppableEvaluation(Instances data, CostMatrix costMatrix) throws Exception {
    super(data, costMatrix);
  }

  /**
   * Evaluates the classifier on a given set of instances. Note that the data
   * must have exactly the same format (e.g. order of attributes) as the data
   * used to train the classifier! Otherwise the results will generally be
   * meaningless.
   *
   * @param classifier machine learning classifier
   * @param data set of test instances for evaluation
   * @param forPredictionsPrinting varargs parameter that, if supplied, is
   *          expected to hold a
   *          weka.classifiers.evaluation.output.prediction.AbstractOutput
   *          object
   * @return the predictions
   * @throws Exception if model could not be evaluated successfully
   */
  public double[] evaluateModel(Classifier classifier, Instances data,
				Object... forPredictionsPrinting) throws Exception {
    m_CurrentClassifier = classifier;

    // for predictions printing
    AbstractOutput classificationOutput = null;

    double[] predictions = new double[data.numInstances()];

    if (forPredictionsPrinting.length > 0) {
      classificationOutput = (AbstractOutput) forPredictionsPrinting[0];
    }

    if (classifier instanceof BatchPredictor
	  && ((BatchPredictor) classifier).implementsMoreEfficientBatchPrediction()) {
      // make a copy and set the class to missing
      Instances dataPred = new Instances(data);
      for (int i = 0; i < data.numInstances(); i++) {
	dataPred.instance(i).setClassMissing();
      }
      double[][] preds =
	((BatchPredictor) classifier).distributionsForInstances(dataPred);
      for (int i = 0; i < data.numInstances(); i++) {
	if (m_Stopped)
	  throw new StoppedException();

	double[] p = preds[i];

	predictions[i] = evaluationForSingleInstance(p, data.instance(i), true);

	if (classificationOutput != null) {
	  classificationOutput.printClassification(p, data.instance(i), i);
	}
      }
    } else {
      // Need to be able to collect predictions if appropriate (for AUC)

      for (int i = 0; i < data.numInstances(); i++) {
	if (m_Stopped)
	  throw new StoppedException();

	predictions[i] =
	  evaluateModelOnceAndRecordPrediction(classifier, data.instance(i));
	if (classificationOutput != null) {
	  classificationOutput.printClassification(classifier,
	    data.instance(i), i);
	}
      }
    }

    m_CurrentClassifier = null;

    return predictions;
  }

  /**
   * Performs a (stratified if class is nominal) cross-validation for a
   * classifier on a set of instances. Now performs a deep copy of the
   * classifier before each call to buildClassifier() (just in case the
   * classifier is not initialized properly).
   *
   * @param classifier the classifier with any options set.
   * @param data the data on which the cross-validation is to be performed
   * @param numFolds the number of folds for the cross-validation
   * @param random random number generator for randomization
   * @param forPredictionsPrinting varargs parameter that, if supplied, is
   *          expected to hold a
   *          weka.classifiers.evaluation.output.prediction.AbstractOutput
   *          object
   * @throws Exception if a classifier could not be generated successfully or
   *           the class is not defined
   */
  public void crossValidateModel(Classifier classifier, Instances data,
				 int numFolds, Random random, Object... forPredictionsPrinting)
    throws Exception {

    // Make a copy of the data we can reorder
    data = new Instances(data);
    data.randomize(random);
    if (data.classAttribute().isNominal()) {
      data.stratify(numFolds);
    }

    // We assume that the first element is a
    // weka.classifiers.evaluation.output.prediction.AbstractOutput object
    AbstractOutput classificationOutput = null;
    if (forPredictionsPrinting.length > 0) {
      // print the header first
      classificationOutput = (AbstractOutput) forPredictionsPrinting[0];
      classificationOutput.setHeader(data);
      classificationOutput.printHeader();
    }

    // Do the folds
    for (int i = 0; i < numFolds; i++) {
      if (m_Stopped)
	throw new StoppedException();
      Instances train = data.trainCV(numFolds, i, random);
      setPriors(train);
      Classifier copiedClassifier = AbstractClassifier.makeCopy(classifier);
      m_CurrentClassifier = copiedClassifier;
      copiedClassifier.buildClassifier(train);
      Instances test = data.testCV(numFolds, i);
      evaluateModel(copiedClassifier, test, forPredictionsPrinting);
    }
    m_NumFolds = numFolds;

    if (classificationOutput != null) {
      classificationOutput.printFooter();
    }
  }

  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    m_Stopped = true;
    if (m_CurrentClassifier instanceof Stoppable)
      ((Stoppable) m_CurrentClassifier).stopExecution();
  }

  /**
   * Whether the execution has been stopped.
   *
   * @return		true if stopped
   */
  @Override
  public boolean isStopped() {
    return m_Stopped;
  }
}
