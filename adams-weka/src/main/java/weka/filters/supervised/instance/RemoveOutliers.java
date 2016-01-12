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
 * RemoveOutliers.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package weka.filters.supervised.instance;

import adams.core.management.ProcessUtils;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.flow.container.WekaTrainTestSetContainer;
import adams.flow.control.removeoutliers.AbstractOutlierDetector;
import adams.flow.control.removeoutliers.Null;
import adams.flow.core.Token;
import adams.flow.transformer.WekaPredictionsToSpreadSheet;
import adams.multiprocess.JobList;
import adams.multiprocess.LocalJobRunner;
import adams.multiprocess.WekaCrossValidationJob;
import weka.classifiers.AggregateableEvaluation;
import weka.classifiers.Classifier;
import weka.classifiers.CrossValidationFoldGenerator;
import weka.classifiers.CrossValidationHelper;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LinearRegression;
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.OptionHandler;
import weka.core.Randomizable;
import weka.core.WekaOptionUtils;
import weka.filters.SimpleBatchFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * Cross-validates the specified classifier on the incoming data and applies the outlier detector to the actual vs predicted data to remove the outliers.<br/>
 * NB: only works on full dataset, not instance by instance.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre> -classifier &lt;value&gt;
 *  The classifier to use for generating the actual vs predicted data.
 *  (default: Linear Regression: No model built yet.)</pre>
 * 
 * <pre> -num-folds &lt;value&gt;
 *  The number of folds to use in the cross-validation.
 *  (default: 10)</pre>
 * 
 * <pre> -num-threads &lt;value&gt;
 *  The number of threads to use for cross-validation; -1 = number of CPUs/cores; 0 or 1 = sequential execution.
 *  (default: 1)</pre>
 * 
 * <pre> -detector
 *  The outlier detector to use.</pre>
 * 
 * <pre> -output-debug-info
 *  If set, filter is run in debug mode and
 *  may output additional info to the console</pre>
 * 
 * <pre> -do-not-check-capabilities
 *  If set, filter capabilities are not checked before filter is built
 *  (use with caution).</pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RemoveOutliers
  extends SimpleBatchFilter
  implements Randomizable {

  private static final long serialVersionUID = -8292965351930853084L;

  public static final String CLASSIFIER = "classifier";

  public static final String NUM_FOLDS = "num-folds";

  public static final String NUM_THREADS = "num-threads";

  public static final String DETECTOR = "detector";

  /** the classifier to use for evaluation. */
  protected Classifier m_Classifier = getDefaultClassifier();

  /** the seed value. */
  protected int m_Seed = getDefaultSeed();

  /** the number of folds to use. */
  protected int m_NumFolds = getDefaultNumFolds();

  /** the outlier detector to use. */
  protected AbstractOutlierDetector m_Detector = getDefaultDetector();

  /** the number of threads to use for parallel execution. */
  protected int m_NumThreads = getDefaultNumThreads();

  /**
   * Returns a string describing this filter.
   *
   * @return a description of the filter suitable for displaying in the
   *         explorer/experimenter gui
   */
  @Override
  public String globalInfo() {
    return
      "Cross-validates the specified classifier on the incoming data and "
	+ "applies the outlier detector to the actual vs predicted data to "
	+ "remove the outliers.\n"
	+ "NB: only works on full dataset, not instance by instance.";
  }

  /**
   * Returns the default classifier.
   *
   * @return  		the default classifier
   */
  protected Classifier getDefaultClassifier() {
    return new LinearRegression();
  }

  /**
   * Sets the classifier.
   *
   * @param value	the classifier
   */
  public void setClassifier(Classifier value) {
    m_Classifier = value;
    reset();
  }

  /**
   * Returns the classifier.
   *
   * @return  		the classifier
   */
  public Classifier getClassifier() {
    return m_Classifier;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String classifierTipText() {
    return "The classifier to use for generating the actual vs predicted data.";
  }

  /**
   * Returns the default seed value.
   *
   * @return  		the default seed
   */
  protected int getDefaultSeed() {
    return 1;
  }

  /**
   * Sets the seed value.
   *
   * @param value	the seed
   */
  @Override
  public void setSeed(int value) {
    m_Seed = value;
    reset();
  }

  /**
   * Returns the seed value.
   *
   * @return  		the seed
   */
  @Override
  public int getSeed() {
    return m_Seed;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String seedTipText() {
    return "The seed value for the cross-validation.";
  }

  /**
   * Returns the default number of folds to use in CV.
   *
   * @return  		the default folds
   */
  protected int getDefaultNumFolds() {
    return 10;
  }

  /**
   * Sets the number of folds to use.
   *
   * @param value	the folds
   */
  public void setNumFolds(int value) {
    m_NumFolds = value;
    reset();
  }

  /**
   * Returns the number of folds to use in CV.
   *
   * @return  		the folds
   */
  public int getNumFolds() {
    return m_NumFolds;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numFoldsTipText() {
    return "The number of folds to use in the cross-validation.";
  }

  /**
   * Returns the default number of threads to use for cross-validation.
   *
   * @return 		the default number of threads: -1 = # of CPUs/cores; 0/1 = sequential execution
   */
  protected int getDefaultNumThreads() {
    return 1;
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
    return "The number of threads to use for cross-validation; -1 = number of CPUs/cores; 0 or 1 = sequential execution.";
  }

  /**
   * Returns the default detector.
   *
   * @return  		the default detector
   */
  protected AbstractOutlierDetector getDefaultDetector() {
    return new Null();
  }

  /**
   * Sets the detector.
   *
   * @param value	the detector
   */
  public void setDetector(AbstractOutlierDetector value) {
    m_Detector = value;
    reset();
  }

  /**
   * Returns the detector.
   *
   * @return  		the detector
   */
  public AbstractOutlierDetector getDetector() {
    return m_Detector;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String detectorTipText() {
    return "The outlier detector to use.";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  @Override
  public Enumeration listOptions() {
    Vector result = new Vector();
    WekaOptionUtils.addOption(result, classifierTipText(), "" + getDefaultClassifier(), CLASSIFIER);
    WekaOptionUtils.addOption(result, numFoldsTipText(), "" + getDefaultNumFolds(), NUM_FOLDS);
    WekaOptionUtils.addOption(result, numThreadsTipText(), "" + getDefaultNumThreads(), NUM_THREADS);
    WekaOptionUtils.addFlag(result, detectorTipText(), DETECTOR);
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
    setClassifier((Classifier) WekaOptionUtils.parse(options, CLASSIFIER, (OptionHandler) getDefaultClassifier()));  // TODO we'll just assume that our classifiers will implement weka.core.OptionHandler
    setNumFolds(WekaOptionUtils.parse(options, NUM_FOLDS, getDefaultNumFolds()));
    setNumThreads(WekaOptionUtils.parse(options, NUM_THREADS, getDefaultNumThreads()));
    setDetector((AbstractOutlierDetector) WekaOptionUtils.parse(options, DETECTOR, getDefaultDetector()));
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
    WekaOptionUtils.add(result, CLASSIFIER, (OptionHandler) getClassifier());  // TODO we'll just assume that all our classifiers implement weka.core.OptionHandler
    WekaOptionUtils.add(result, NUM_FOLDS, getNumFolds());
    WekaOptionUtils.add(result, NUM_THREADS, getNumThreads());
    WekaOptionUtils.add(result, DETECTOR, getDetector());
    WekaOptionUtils.add(result, super.getOptions());
    return WekaOptionUtils.toArray(result);
  }

  /**
   * Returns the ensemble's capabilities.
   *
   * @return		the capabilities
   */
  @Override
  public Capabilities getCapabilities() {
    Capabilities	result;

    result = getClassifier().getCapabilities();
    result.setOwner(this);

    return super.getCapabilities();
  }

  /**
   * Determines the output format based on the input format and returns this. In
   * case the output format cannot be returned immediately, i.e.,
   * immediateOutputFormat() returns false, then this method will be called from
   * batchFinished().
   *
   * @param inputFormat the input format to base the output format on
   * @return the output format
   * @throws Exception in case the determination goes wrong
   */
  @Override
  protected Instances determineOutputFormat(Instances inputFormat) throws Exception {
    return new Instances(inputFormat, 0);
  }

  /**
   * Cross-validates the classifier on the given data.
   *
   * @param data	the data to use for cross-validation
   * @param folds	the number of folds
   * @return		the evaluation
   * @throws Exception	if cross-validation fails
   */
  protected Evaluation crossValidate(Instances data, int folds) throws Exception {
    String 				msg;
    int 				numThreads;
    Evaluation				eval;
    AggregateableEvaluation 		evalAgg;
    CrossValidationFoldGenerator 	generator;
    JobList<WekaCrossValidationJob> 	list;
    WekaCrossValidationJob 		job;
    WekaTrainTestSetContainer 		cont;
    int					i;
    LocalJobRunner 			jobRunner;

    if (m_NumThreads == -1)
      numThreads = ProcessUtils.getAvailableProcessors();
    else if (m_NumThreads > 1)
      numThreads = Math.min(m_NumThreads, folds);
    else
      numThreads = 0;

    if (numThreads == 0) {
      eval = new Evaluation(data);
      eval.setDiscardPredictions(false);
      eval.crossValidateModel(m_Classifier, data, folds, new Random(m_Seed));
      return eval;
    }
    else {
      generator = new CrossValidationFoldGenerator(data, folds, m_Seed, true);
      jobRunner = new LocalJobRunner<WekaCrossValidationJob>();
      jobRunner.setNumThreads(m_NumThreads);
      list = new JobList<WekaCrossValidationJob>();
      while (generator.hasNext()) {
	cont = generator.next();
	job = new WekaCrossValidationJob(
	  m_Classifier,
	  (Instances) cont.getValue(WekaTrainTestSetContainer.VALUE_TRAIN),
	  (Instances) cont.getValue(WekaTrainTestSetContainer.VALUE_TEST),
	  (Integer) cont.getValue(WekaTrainTestSetContainer.VALUE_FOLD_NUMBER),
	  false);
	list.add(job);
      }
      jobRunner.add(list);
      jobRunner.start();
      jobRunner.stop();
      // aggregate data
      msg     = null;
      evalAgg = new AggregateableEvaluation(data);
      for (i = 0; i < jobRunner.getJobs().size(); i++) {
	job = (WekaCrossValidationJob) jobRunner.getJobs().get(i);
	if (job.getEvaluation() == null) {
	  msg = "Fold #" + (i + 1) + " failed to evaluate";
	  if (!job.hasExecutionError())
	    msg += "?";
	  else
	    msg += ":\n" + job.getExecutionError();
	  break;
	}
	evalAgg.aggregate(job.getEvaluation());
	job.cleanUp();
      }
      if (msg != null)
	throw new Exception(msg);
      list.cleanUp();
      jobRunner.cleanUp();
      return evalAgg;
    }
  }

  /**
   * Turns the predictions of the evaluation object into a spreadsheet.
   *
   * @param eval	the evaluation object to convert
   * @return		the generated spreadsheet
   */
  protected SpreadSheet evaluationToSpreadSheet(Evaluation eval) throws Exception {
    SpreadSheet				result;
    WekaPredictionsToSpreadSheet 	conv;
    String				msg;
    Token token;

    conv = new WekaPredictionsToSpreadSheet();
    msg = conv.setUp();
    if (msg != null)
      throw new Exception("Failed to convert predictions to spreadsheet (setUp): " + msg);
    conv.input(new Token(eval));
    msg = conv.execute();
    if (msg != null)
      throw new Exception("Failed to convert predictions to spreadsheet (execute): " + msg);
    if (conv.hasPendingOutput()) {
      token = conv.output();
      result = (SpreadSheet) token.getPayload();
    }
    else {
      throw new Exception("No output data generated from predictions!");
    }

    return result;
  }

  /**
   * Processes the given data (may change the provided dataset) and returns the
   * modified version. This method is called in batchFinished().
   *
   * @param data the data to process
   * @return the modified data
   * @throws Exception in case the processing goes wrong
   */
  @Override
  protected Instances process(Instances data) throws Exception {
    Instances		result;
    Evaluation		eval;
    SpreadSheet		sheet;
    Set<Integer> 	outliers;
    List<Integer> 	sorted;
    int			i;
    int			folds;
    int[]		indices;

    // cross-validate
    folds = m_NumFolds;
    if (folds == -1)
      folds = data.numInstances();
    try {
      eval = crossValidate(data, folds);
    }
    catch (Exception e) {
      throw new Exception("Failed to cross-validate!", e);
    }

    // create spreadsheet
    sheet = evaluationToSpreadSheet(eval);
    if (sheet == null)
      return null;

    // apply detector
    outliers = m_Detector.detect(sheet, new SpreadSheetColumnIndex("Actual"), new SpreadSheetColumnIndex("Predicted"));
    if (outliers == null) {
      throw new Exception("Failed to detect outliers!");
    }
    else if (getDebug()) {
      sorted = new ArrayList<>(outliers);
      Collections.sort(sorted);
      System.err.println(getClass().getName() + ": Outliers (0-based index): " + sorted);
    }

    // clean dataset
    indices = CrossValidationHelper.crossValidationIndices(data, folds, new Random(m_Seed));
    result  = new Instances(data, data.numInstances() - outliers.size());
    for (i = 0; i < indices.length; i++) {
      if (outliers.contains(i))
	continue;
      result.add((Instance) data.instance(indices[i]).copy());
    }

    return result;
  }
}
