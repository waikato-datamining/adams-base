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
 * CrossValidationExperiment.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekamultiexperimenter.experiment;

import adams.core.ThreadLimiter;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.SpreadSheet;
import adams.multiprocess.WekaCrossValidationExecution;
import weka.classifiers.Classifier;
import weka.core.Instances;

/**
 * Performs cross-validation.
 * If only one dataset and one classifier specified, cross-validation is
 * multi-threaded (in case experiment is multi-threaded).
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CrossValidationExperiment
  extends AbstractExperiment {

  private static final long serialVersionUID = -4147644361063132314L;

  /**
   * Performs cross-validation on the classifier/data combination.
   *
   */
  public static class CrossValidationExperimentJob
    extends AbstractExperimentJob<CrossValidationExperiment> {

    private static final long serialVersionUID = -4979824848864995696L;

    /** for executing the cross-validation. */
    protected WekaCrossValidationExecution m_CrossValidation;

    /**
     * Initializes the run.
     *
     * @param owner      the owning experiment
     * @param run        the current run
     * @param classifier the classifier to evaluate
     * @param data       the data to use for evaluation
     */
    public CrossValidationExperimentJob(CrossValidationExperiment owner, int run, Classifier classifier, Instances data) {
      super(owner, run, classifier, data);
    }

    /**
     * Performs the cross-validation.
     */
    @Override
    protected void evaluate() {
      String				result;
      SpreadSheet 			results;
      int 				fold;
      boolean				simple;

      m_Owner.log("Run " + m_Run + " [start]: " + m_Data.relationName() + " on " + shortenCommandLine(m_Classifier));

      simple = (m_Owner.getDatasets().length == 1)
	&& (m_Owner.getClassifiers().length == 1);

      m_CrossValidation = new WekaCrossValidationExecution();
      m_CrossValidation.setClassifier(m_Classifier);
      m_CrossValidation.setData(m_Data);
      m_CrossValidation.setFolds(m_Owner.getFolds());
      m_CrossValidation.setSeed(m_Run);
      m_CrossValidation.setDiscardPredictions(true);
      m_CrossValidation.setNumThreads(1);
      if (simple && (m_Owner.getJobRunner() instanceof ThreadLimiter))
	m_CrossValidation.setNumThreads(((ThreadLimiter) m_Owner.getJobRunner()).getNumThreads());
      m_CrossValidation.setSeparateFolds(true);
      m_CrossValidation.setStatusMessageHandler(m_Owner.getStatusMessageHandler());
      m_CrossValidation.setWaitForJobs(false);
      result = m_CrossValidation.execute();

      if (result == null) {
	results = new DefaultSpreadSheet();
	for (fold = 0; fold < m_Owner.getFolds(); fold++) {
	  addMetrics(results, m_Run, m_Classifier, m_Data, m_CrossValidation.getEvaluations()[fold]);
	  addMetric(results, "Key_Fold", fold);
	}
	m_Owner.appendResults(results);
      }

      m_Owner.log("Run " + m_Run + " [end]: " + m_Data.relationName() + " on " + shortenCommandLine(m_Classifier));
    }

    /**
     * Stops the execution.
     */
    @Override
    public void stopExecution() {
      super.stopExecution();
      if (m_CrossValidation != null)
	m_CrossValidation.stopExecution();
    }
  }

  /** the number of folds. */
  protected int m_Folds;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Performs cross-validation on each classifier/dataset combination.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "folds", "folds",
      10, -1, null);
  }

  /**
   * Sets the number of folds.
   *
   * @param value	the folds
   */
  public void setFolds(int value) {
    m_Folds = value;
    reset();
  }

  /**
   * Returns the number of folds.
   *
   * @return		the folds
   */
  public int getFolds() {
    return m_Folds;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String foldsTipText() {
    return "The number of folds to perform.";
  }

  /**
   * Checks whether the number of rows located in the current results are
   * complete.
   *
   * @param rows	the located results
   * @return		true if complete
   */
  protected boolean isComplete(int[] rows) {
    return (rows.length == m_Folds);
  }

  /**
   * Evaluates the classifier on the dataset.
   *
   * @param currentRun	the current run
   * @param cls		the classifier to evaluate
   * @param data	the dataset to evaluate on
   * @return		null if successful, otherwise error message
   */
  @Override
  protected CrossValidationExperimentJob evaluate(int currentRun, Classifier cls, Instances data) {
    return new CrossValidationExperimentJob(this, currentRun, cls, data);
  }
}
