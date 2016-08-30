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
 * WekaCrossValidationJob.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.multiprocess;

import adams.core.StatusMessageHandler;
import adams.core.option.OptionUtils;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

/**
 * For evaluation a single train/test fold in parallel.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 11741 $
 */
public class WekaCrossValidationJob
  extends AbstractJob {

  /** for serialization. */
  private static final long serialVersionUID = -9085803857529039559L;

  /** the classifier to evaluate. */
  protected Classifier m_Classifier;

  /** the fold. */
  protected int m_Fold;

  /** the training set. */
  protected Instances m_Train;

  /** the test set. */
  protected Instances m_Test;

  /** whether to discard the predictions. */
  protected boolean m_DiscardPredictions;

  /** the evaluation. */
  protected Evaluation m_Evaluation;

  /** for outputting notifications. */
  protected StatusMessageHandler m_StatusMessageHandler;

  /**
   * Initializes the job.
   *
   * @param classifier	the classifier to evaluate
   * @param train		the training set
   * @param test		the test set
   * @param fold		the fold index
   * @param discardPred	whether to discard the predictions
   */
  public WekaCrossValidationJob(Classifier classifier, Instances train, Instances test, int fold, boolean discardPred) {
    this(classifier, train, test, fold, discardPred, null);
  }

  /**
   * Initializes the job.
   *
   * @param classifier	the classifier to evaluate
   * @param train	the training set
   * @param test	the test set
   * @param fold	the fold index
   * @param discardPred	whether to discard the predictions
   * @param handler	for displaying notifications, can be null
   */
  public WekaCrossValidationJob(Classifier classifier, Instances train, Instances test, int fold, boolean discardPred, StatusMessageHandler handler) {
    super();

    try {
      m_Classifier = (Classifier) OptionUtils.shallowCopy(classifier);
    }
    catch (Exception e) {
      m_Classifier = null;
    }

    m_Train                = train;
    m_Test                 = test;
    m_Fold                 = fold;
    m_DiscardPredictions   = discardPred;
    m_StatusMessageHandler = handler;
  }

  /**
   * Returns the training set.
   *
   * @return		the dataset
   */
  public Instances getTrain() {
    return m_Train;
  }

  /**
   * Returns the test set.
   *
   * @return		the dataset
   */
  public Instances getTest() {
    return m_Test;
  }

  /**
   * Returns the fold index.
   *
   * @return		the fold
   */
  public int getFold() {
    return m_Fold;
  }

  /**
   * Returns whether the predictions are discarded.
   *
   * @return		true if discarded
   */
  public boolean getDiscardPredictions() {
    return m_DiscardPredictions;
  }

  /**
   * Returns the status message handler.
   *
   * @return		the handler
   */
  public StatusMessageHandler getStatusMessageHandler() {
    return m_StatusMessageHandler;
  }

  /**
   * Returns the generated evaluation object.
   *
   * @return		the evaluation, null if not available
   */
  public Evaluation getEvaluation() {
    return m_Evaluation;
  }

  @Override
  protected String preProcessCheck() {
    if (m_Classifier == null)
      return "No classifier set/failed to copy!";
    if (m_Train == null)
      return "No training set!";
    if (m_Test == null)
      return "No test set!";
    return null;
  }

  /**
   * Does the actual execution of the job.
   *
   * @throws Exception if fails to execute job
   */
  @Override
  protected void process() throws Exception {
    if (m_StatusMessageHandler != null)
      m_StatusMessageHandler.showStatus("Fold " + (m_Fold+1) + " - start: '" + m_Train.relationName() + "' using " + OptionUtils.getCommandLine(m_Classifier));
    m_Classifier.buildClassifier(m_Train);
    m_Evaluation = new Evaluation(m_Train);
    m_Evaluation.setDiscardPredictions(m_DiscardPredictions);
    m_Evaluation.evaluateModel(m_Classifier, m_Test);
    if (m_StatusMessageHandler != null)
      m_StatusMessageHandler.showStatus("Fold " + (m_Fold+1) + " - end: '" + m_Train.relationName() + "' using " + OptionUtils.getCommandLine(m_Classifier));
  }

  /**
   * Checks whether all post-conditions have been met.
   *
   * @return		null if everything is OK, otherwise an error message
   */
  @Override
  protected String postProcessCheck() {
    if (m_Evaluation == null)
      return "Failed to evaluate?";
    return null;
  }

  /**
   * Cleans up data structures, frees up memory.
   * Removes dependencies and job parameters.
   */
  @Override
  public void cleanUp() {
    super.cleanUp();

    m_Train      = null;
    m_Test       = null;
    m_Evaluation = null;
  }

  /**
   * Returns a string representation of this job.
   *
   * @return		the job as string
   */
  @Override
  public String toString() {
    return "classifier=" + OptionUtils.getCommandLine(m_Classifier) + ", fold=" + m_Fold;
  }
}
