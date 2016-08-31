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
import adams.multiprocess.WekaCrossValidationExecution;
import weka.classifiers.Classifier;
import weka.core.Instances;

/**
 * Performs cross-validation.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CrossValidationExperiment
  extends AbstractExperiment
  implements ThreadLimiter {

  private static final long serialVersionUID = -4147644361063132314L;

  /** the number of folds. */
  protected int m_Folds;

  /** the number of threads to use for parallel execution. */
  protected int m_NumThreads;

  /** the current fold. */
  protected transient int m_CurrentFold;

  /** for performing cross-validation. */
  protected transient WekaCrossValidationExecution m_CrossValidation;

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

    m_OptionManager.add(
      "num-threads", "numThreads",
      1, -1, null);
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
   * Sets the number of threads to use for cross-validation.
   *
   * @param value 	the number of threads: -1 = # of CPUs/cores; 0/1 = sequential execution
   */
  public void setNumThreads(int value) {
    m_NumThreads = value;
    reset();
  }

  /**
   * Returns the number of threads to use for cross-validation.
   *
   * @return 		the number of threads: -1 = # of CPUs/cores; 0/1 = sequential execution
   */
  public int getNumThreads() {
    return m_NumThreads;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numThreadsTipText() {
    return "The number of threads to use for cross-validation -1 = number of CPUs/cores; 0 or 1 = sequential execution.";
  }

  /**
   * Checks whether the number of rows located in the current results are
   * complete.
   *
   * @param rows	the located results
   * @return		true if complete
   */
  protected boolean isComplete(int[] rows) {
    return (rows.length == m_Runs * m_Folds);
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
    String	result;

    m_CrossValidation = new WekaCrossValidationExecution();
    m_CrossValidation.setClassifier(cls);
    m_CrossValidation.setData(data);
    m_CrossValidation.setFolds(m_Folds);
    m_CrossValidation.setSeed(m_CurrentRun);
    m_CrossValidation.setDiscardPredictions(true);
    m_CrossValidation.setNumThreads(m_NumThreads);
    m_CrossValidation.setSeparateFolds(true);
    result = m_CrossValidation.execute();

    if (result == null) {
      for (m_CurrentFold = 0; m_CurrentFold < m_Folds; m_CurrentFold++)
	addMetrics(cls, data, m_CrossValidation.getEvaluations()[m_CurrentFold]);
    }

    return result;
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
