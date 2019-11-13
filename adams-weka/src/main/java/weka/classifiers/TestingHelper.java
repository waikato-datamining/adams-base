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
 * TestingHelper.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package weka.classifiers;

import adams.core.StoppableWithFeedback;
import adams.core.Utils;
import weka.core.BatchPredictor;
import weka.core.Instances;

/**
 * Helper class for evaluating models on test data.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class TestingHelper {

  /**
   * The interface for objects that listen for testing updates.
   */
  public interface TestingUpdateListener {

    /**
     * Gets called when the testing interval reached or all instances processed.
     *
     * @param data	the data being used for testing
     * @param numTested	the number instances tested so far
     * @param numTotal	the total number of instances tested
     */
    public void testingUpdateRequested(Instances data, int numTested, int numTotal);
  }

  /**
   * Evaluates the model on the test data and sends updates to the listener (if available).
   *
   * @param model	the model to test
   * @param test	the test data
   * @param eval 	the evaluation object to use
   * @param interval 	the update interval
   * @param listener	the listener to send the updates to, can be null
   * @throws Exception	if evaluation fails
   */
  public static void evaluateModel(Classifier model, Instances test, Evaluation eval, int interval, TestingUpdateListener listener) throws Exception {
    evaluateModel(model, test, eval, interval, listener, null);
  }

  /**
   * Evaluates the model on the test data and sends updates to the listener (if available).
   *
   * @param model	the model to test
   * @param test	the test data
   * @param eval 	the evaluation object to use
   * @param interval 	the update interval
   * @param listener	the listener to send the updates to, can be null
   * @param owner 	the owner to check whether stopped, can be null
   * @throws Exception	if evaluation fails
   */
  public static void evaluateModel(Classifier model, Instances test, Evaluation eval, int interval, TestingUpdateListener listener, StoppableWithFeedback owner) throws Exception {
    int 	batchSize;
    int		i;
    boolean	stopped;
    int		nextUpdate;
    Instances	batch;
    double[][]	preds;
    int 	num;
    int		n;

    // determine batch size
    batchSize = 1;
    if (model instanceof BatchPredictor) {
      if (((BatchPredictor) model).implementsMoreEfficientBatchPrediction()) {
	if (Utils.isInteger(((BatchPredictor) model).getBatchSize()))
	  batchSize = Integer.parseInt(((BatchPredictor) model).getBatchSize());
      }
    }

    stopped = false;
    if (batchSize == 1) {
      for (i = 0; i < test.numInstances(); i++) {
	eval.evaluateModelOnceAndRecordPrediction(model, test.instance(i));
	if (listener != null) {
	  if ((i + 1) % interval == 0)
	    listener.testingUpdateRequested(test, i+1, test.numInstances());
	}
	if ((owner != null) && owner.isStopped()) {
	  stopped = true;
	  break;
	}
      }
    }
    else {
      i          = 0;
      nextUpdate = interval;
      while (i < test.numInstances()) {
        num   = Math.min(batchSize, test.numInstances() - i);
        batch = new Instances(test, i, num);
        preds = ((BatchPredictor) model).distributionsForInstances(batch);
        for (n = 0; n < num; n++)
          eval.evaluationForSingleInstance(preds[n], batch.instance(n), !eval.getDiscardPredictions());
        i += num;
        if (nextUpdate <= i) {
	  listener.testingUpdateRequested(test, i+1, test.numInstances());
          nextUpdate = i + interval;
	}
	if ((owner != null) && owner.isStopped()) {
	  stopped = true;
	  break;
	}
      }
    }

    // final update
    if (!stopped && (listener != null))
      listener.testingUpdateRequested(test, test.numInstances(), test.numInstances());
  }
}
