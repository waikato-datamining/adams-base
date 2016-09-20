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
 * WekaCrossValidation.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.multiprocess;

import adams.core.Performance;
import adams.core.StatusMessageHandler;
import adams.core.Stoppable;
import adams.core.ThreadLimiter;
import adams.core.Utils;
import adams.core.logging.CustomLoggingLevelObject;
import adams.core.option.OptionUtils;
import adams.flow.container.WekaTrainTestSetContainer;
import adams.flow.standalone.JobRunnerSetup;
import weka.classifiers.AggregateableEvaluation;
import weka.classifiers.Classifier;
import weka.classifiers.CrossValidationFoldGenerator;
import weka.classifiers.CrossValidationHelper;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.output.prediction.AbstractOutput;
import weka.core.Instances;

import java.util.Random;

/**
 * Performs cross-validation, either single or multi-threaded.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaCrossValidationExecution
  extends CustomLoggingLevelObject
  implements Stoppable {

  private static final long serialVersionUID = 2021758441076652982L;

  /** the classifier to evaluate. */
  protected Classifier m_Classifier;

  /** the data to evaluate on. */
  protected Instances m_Data;

  /** for generating predictions output. */
  protected AbstractOutput m_Output;

  /** the buffer for the predictions. */
  protected StringBuffer m_OutputBuffer;

  /** the number of folds. */
  protected int m_Folds;

  /** whether to separate folds. */
  protected boolean m_SeparateFolds;

  /** the seed value. */
  protected long m_Seed;

  /** whether to discard predictions. */
  protected boolean m_DiscardPredictions;

  /** the number of threads to use for parallel execution. */
  protected int m_NumThreads;

  /** the actual number of threads to use. */
  protected int m_ActualNumThreads;

  /** the jobrunner setup. */
  protected transient JobRunnerSetup m_JobRunnerSetup;

  /** the runner in use. */
  protected transient JobRunner m_JobRunner;

  /** the (aggregated) evaluation. */
  protected Evaluation m_Evaluation;

  /** the separate evaluations. */
  protected Evaluation[] m_Evaluations;

  /** the original indices. */
  protected int[] m_OriginalIndices;

  /** whether the execution has been stopped. */
  protected boolean m_Stopped;

  /** for outputting notifications. */
  protected StatusMessageHandler m_StatusMessageHandler;

  /** whether to wait for jobs to finish when stopping. */
  protected boolean m_WaitForJobs;

  /**
   * Initializes the execution.
   */
  public WekaCrossValidationExecution() {
    super();

    m_Classifier           = null;
    m_Data                 = null;
    m_Output               = null;
    m_JobRunner            = null;
    m_JobRunnerSetup       = null;
    m_StatusMessageHandler = null;
    m_WaitForJobs          = true;
  }

  /**
   * Sets the JobRunnerSetup.
   *
   * @param value	the setup
   */
  public void setJobRunnerSetup(JobRunnerSetup value) {
    m_JobRunnerSetup = value;
  }

  /**
   * Returns the JobRunnerSetup, if any.
   *
   * @return		the JobRunnerSetup, null if none available
   */
  public JobRunnerSetup getJobRunnerSetup() {
    return m_JobRunnerSetup;
  }

  /**
   * Sets whether to wait for jobs to finish when terminating.
   *
   * @param value	true if to wait
   */
  public void setWaitForJobs(boolean value) {
    m_WaitForJobs = value;
  }

  /**
   * Returns whether to wait for jobs to finish when terminating.
   *
   * @return		the JobRunnerSetup, null if none available
   */
  public boolean getWaitForJobs() {
    return m_WaitForJobs;
  }

  /**
   * Sets the classifier to use.
   *
   * @param value	the classifier
   */
  public void setClassifier(Classifier value) {
    m_Classifier = value;
  }

  /**
   * Returns the classifier in use.
   *
   * @return		the classifier
   */
  public Classifier getClassifier() {
    return m_Classifier;
  }

  /**
   * Sets the data to use.
   *
   * @param value	the data
   */
  public void setData(Instances value) {
    m_Data = value;
  }

  /**
   * Returns the data in use.
   *
   * @return		the data
   */
  public Instances getData() {
    return m_Data;
  }

  /**
   * Sets the prediction output generator to use.
   *
   * @param value	the output generator
   */
  public void setOutput(AbstractOutput value) {
    m_Output = value;
  }

  /**
   * Returns the prediction output generator in use.
   *
   * @return		the output generator
   */
  public AbstractOutput getOutput() {
    return m_Output;
  }

  /**
   * Sets the number of folds.
   *
   * @param value	the folds, -1 for LOOCV
   */
  public void setFolds(int value) {
    if ((value == -1) || (value >= 2)) {
      m_Folds = value;
    }
    else {
      getLogger().severe(
	"Number of folds must be >=2 or -1 for LOOCV, provided: " + value);
    }
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
   * Sets whether to separate the folds, an Evaluation object per fold.
   *
   * @param value	true if to separate
   */
  public void setSeparateFolds(boolean value) {
    m_SeparateFolds = value;
  }

  /**
   * Returns whether to separate the folds, an Evaluation object per fold.
   *
   * @return		true if to separate
   */
  public boolean getSeparateFolds() {
    return m_SeparateFolds;
  }

  /**
   * Sets the seed value.
   *
   * @param value	the seed
   */
  public void setSeed(long value) {
    m_Seed = value;
  }

  /**
   * Returns the seed value.
   *
   * @return		the seed
   */
  public long getSeed() {
    return m_Seed;
  }

  /**
   * Sets whether to discard the predictions instead of collecting them
   * for future use, in order to conserve memory.
   *
   * @param value	true if to discard predictions
   */
  public void setDiscardPredictions(boolean value) {
    m_DiscardPredictions = value;
  }

  /**
   * Returns whether to discard the predictions in order to preserve memory.
   *
   * @return		true if predictions discarded
   */
  public boolean getDiscardPredictions() {
    return m_DiscardPredictions;
  }

  /**
   * Sets the number of threads to use for cross-validation.
   *
   * @param value 	the number of threads: -1 = # of CPUs/cores; 0/1 = sequential execution
   */
  public void setNumThreads(int value) {
    m_NumThreads = value;
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
   * Sets the status message handler for outputting notifications.
   *
   * @param value 	the handler
   */
  public void setStatusMessageHandler(StatusMessageHandler value) {
    m_StatusMessageHandler = value;
  }

  /**
   * Returns the status message handler for outputting notifications.
   *
   * @return 		the handler, null if none set
   */
  public StatusMessageHandler getStatusMessageHandler() {
    return m_StatusMessageHandler;
  }

  /**
   * Initializes the output buffer.
   */
  protected void initOutputBuffer() {
    m_OutputBuffer = new StringBuffer();
    if (m_Output != null) {
      try {
	m_Output = (AbstractOutput) OptionUtils.forAnyCommandLine(
	  AbstractOutput.class, OptionUtils.getCommandLine(m_Output));
	m_Output.setBuffer(m_OutputBuffer);
      }
      catch (Exception e) {
	throw new IllegalStateException("Failed to create copy of output!", e);
      }
    }
  }

  /**
   * Returns the output buffer.
   *
   * @return		the output buffer
   */
  public StringBuffer getOutputBuffer() {
    return m_OutputBuffer;
  }

  /**
   * Returns the generated (aggregated) evaluation.
   *
   * @return		the evaluation
   */
  public Evaluation getEvaluation() {
    return m_Evaluation;
  }

  /**
   * Returns the generated evaluations (if multi-threaded).
   *
   * @return		the evaluations, null if not multi-threaded
   */
  public Evaluation[] getEvaluations() {
    return m_Evaluations;
  }

  /**
   * Returns the original indices.
   *
   * @return		the indices
   */
  public int[] getOriginalIndices() {
    return m_OriginalIndices;
  }

  /**
   * Returns whether the execution was single-threaded (after {@link #execute()}).
   *
   * @return		true if single-threaded
   */
  public boolean isSingleThreaded() {
    return (m_ActualNumThreads == 0);
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  public String execute() {
    String				result;
    Evaluation 				eval;
    AggregateableEvaluation 		evalAgg;
    int					folds;
    CrossValidationFoldGenerator 	generator;
    JobList<WekaCrossValidationJob>	list;
    WekaCrossValidationJob 		job;
    WekaTrainTestSetContainer 		cont;
    int					i;
    int					current;
    int[]				indices;
    Instances				train;
    Instances				test;
    Classifier				cls;

    result        = null;
    indices       = null;
    m_Evaluation  = null;
    m_Evaluations = null;

    try {
      // evaluate classifier
      if (m_Classifier == null)
	throw new IllegalStateException("Classifier '" + getClassifier() + "' not found!");
      if (isLoggingEnabled())
	getLogger().info(OptionUtils.getCommandLine(m_Classifier));

      folds = m_Folds;
      if (folds == -1)
	folds = m_Data.numInstances();

      m_ActualNumThreads = Performance.determineNumThreads(m_NumThreads);

      if (!m_DiscardPredictions)
	indices = CrossValidationHelper.crossValidationIndices(m_Data, folds, new Random(m_Seed));

      generator = new CrossValidationFoldGenerator(m_Data, folds, m_Seed, true);
      if ((m_ActualNumThreads == 1) && !m_SeparateFolds) {
	initOutputBuffer();
	if (m_Output != null)
	  m_Output.setHeader(m_Data);
	eval       = new Evaluation(m_Data);
	eval.setDiscardPredictions(m_DiscardPredictions);
	current    = 0;
	while (generator.hasNext()) {
          if (isStopped())
            break;
	  if (m_StatusMessageHandler != null)
	    m_StatusMessageHandler.showStatus("Fold " + current + "/" + folds + ": '" + m_Data.relationName() + "' using " + OptionUtils.getCommandLine(m_Classifier));
	  cont  = generator.next();
	  train = (Instances) cont.getValue(WekaTrainTestSetContainer.VALUE_TRAIN);
	  test  = (Instances) cont.getValue(WekaTrainTestSetContainer.VALUE_TEST);
	  cls   = (Classifier) OptionUtils.shallowCopy(m_Classifier);
	  cls.buildClassifier(train);
	  eval.setPriors(train);
	  eval.evaluateModel(cls, test, m_Output);
	  current++;
	}
	if (!isStopped())
	  m_Evaluation = eval;
      }
      else {
	if (m_JobRunnerSetup == null)
	  m_JobRunner = new LocalJobRunner<WekaCrossValidationJob>();
	else
	  m_JobRunner = m_JobRunnerSetup.newInstance();
	if (m_JobRunner instanceof ThreadLimiter)
	  ((ThreadLimiter) m_JobRunner).setNumThreads(m_NumThreads);
	list = new JobList<>();
	while (generator.hasNext()) {
	  cont = generator.next();
	  job  = new WekaCrossValidationJob(
	    (Classifier) OptionUtils.shallowCopy(m_Classifier),
	    (Instances) cont.getValue(WekaTrainTestSetContainer.VALUE_TRAIN),
	    (Instances) cont.getValue(WekaTrainTestSetContainer.VALUE_TEST),
	    (Integer) cont.getValue(WekaTrainTestSetContainer.VALUE_FOLD_NUMBER),
	    m_DiscardPredictions,
	    m_StatusMessageHandler);
	  list.add(job);
	}
	m_JobRunner.add(list);
	m_JobRunner.start();
	m_JobRunner.stop();
	// aggregate data
	if (!isStopped()) {
	  evalAgg = new AggregateableEvaluation(m_Data);
	  evalAgg.setDiscardPredictions(m_DiscardPredictions);
	  m_Evaluations = new Evaluation[m_JobRunner.getJobs().size()];
	  for (i = 0; i < m_JobRunner.getJobs().size(); i++) {
	    job = (WekaCrossValidationJob) m_JobRunner.getJobs().get(i);
	    if (job.getEvaluation() == null) {
	      result = "Fold #" + (i + 1) + " failed to evaluate";
	      if (!job.hasExecutionError())
		result += "?";
	      else
		result += ":\n" + job.getExecutionError();
	      break;
	    }
	    evalAgg.aggregate(job.getEvaluation());
	    m_Evaluations[i] = job.getEvaluation();
	    job.cleanUp();
	  }
	  m_Evaluation = evalAgg;
	}
	list.cleanUp();
	m_JobRunner.cleanUp();
	m_JobRunner = null;
      }

    }
    catch (Exception e) {
      result = Utils.handleException(this, "Failed to cross-validate classifier: ", e);
    }

    m_OriginalIndices = indices;

    return result;
  }

  /**
   * Returns whether the execution has been stopped.
   *
   * @return		true if stopped
   */
  public boolean isStopped() {
    return m_Stopped;
  }

  /**
   * Stops the execution.
   */
  public void stopExecution() {
    getLogger().severe("Execution stopped");
    if (m_JobRunner != null)
      m_JobRunner.terminate(m_WaitForJobs);
  }
}
