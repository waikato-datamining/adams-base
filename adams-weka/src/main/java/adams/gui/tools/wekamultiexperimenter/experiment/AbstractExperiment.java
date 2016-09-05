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
 * AbstractExperiment.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekamultiexperimenter.experiment;

import adams.core.Index;
import adams.core.Shortening;
import adams.core.StatusMessageHandler;
import adams.core.StoppableWithFeedback;
import adams.core.ThreadLimiter;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.core.management.ProcessUtils;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.OptionUtils;
import adams.core.option.WekaCommandLineHandler;
import adams.data.conversion.SpreadSheetToWekaInstances;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.HeaderRow;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.data.spreadsheet.SpreadSheetHelper;
import adams.data.spreadsheet.SpreadSheetSupporter;
import adams.data.spreadsheet.rowfinder.ByNumericValue;
import adams.data.spreadsheet.rowfinder.ByStringComparison;
import adams.data.spreadsheet.rowfinder.MultiRowFinder;
import adams.data.spreadsheet.rowfinder.MultiRowFinder.Combination;
import adams.data.spreadsheet.rowfinder.RowFinder;
import adams.data.weka.classattribute.AbstractClassAttributeHeuristic;
import adams.data.weka.classattribute.LastAttribute;
import adams.flow.core.EvaluationHelper;
import adams.flow.core.EvaluationStatistic;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import java.io.File;
import java.io.ObjectStreamClass;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Ancestor for simple experiments.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractExperiment
  extends AbstractOptionHandler
  implements StoppableWithFeedback, ExperimentWithCustomizableRelationNames,
  ResettableExperiment, SpreadSheetSupporter, ThreadLimiter {

  private static final long serialVersionUID = -345521029095304309L;

  /**
   * For evaluating a single classifier/dataset combination.
   *
   * @param <T> the type of experiment
   * @author FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static abstract class AbstractRun<T extends AbstractExperiment>
    implements Runnable {

    /** the owner. */
    protected T m_Owner;

    /** the run. */
    protected int m_Run;

    /** the classifier. */
    protected Classifier m_Classifier;

    /** the dataset. */
    protected Instances m_Data;

    /** the class label index. */
    protected Index m_ClassLabelIndex;

    /** the generated results. */
    protected SpreadSheet m_Results;

    /**
     * Initializes the run.
     *
     * @param owner		the owning experiment
     * @param run		the current run
     * @param classifier	the classifier to evaluate
     * @param data		the data to use for evaluation
     */
    public AbstractRun(T owner, int run, Classifier classifier, Instances data) {
      m_Owner           = owner;
      m_Run             = run;
      m_Classifier      = classifier;
      m_Data            = data;
      m_ClassLabelIndex = m_Owner.getClassLabelIndex().getClone();
      m_ClassLabelIndex.setMax(m_Data.classAttribute().numValues());
      m_Results         = new DefaultSpreadSheet();
    }

    /**
     * Adds the metric to the results, automatically expands spreadsheet.
     *
     * @param results	the results to add the metrics to
     * @param name	the name
     * @param value	the value
     */
    protected void addMetric(SpreadSheet results, String name, Object value) {
      HeaderRow	header;
      Row		row;
      int		index;

      header = results.getHeaderRow();
      index  = header.indexOfContent(name);
      // not present?
      if (index == -1) {
	results.insertColumn(results.getColumnCount(), name);
	index = results.getColumnCount() - 1;
      }
      row = results.getRow(results.getRowCount() - 1);
      row.addCell(index).setNative(value);
    }

    /**
     * Adds the metrics from the Evaluation object to the results.
     *
     * @param results	the results to add the metrics to
     * @param currentRun	the current run
     * @param cls		the classifier to evaluate
     * @param data	the dataset to evaluate on
     * @param eval	the Evaluation object to add
     */
    protected void addMetrics(SpreadSheet results, int currentRun, Classifier cls, Instances data, Evaluation eval) {
      boolean			nominal;
      String			metric;
      int				classLabel;
      WekaCommandLineHandler 	cmdlineHandler;

      results.addRow();

      cmdlineHandler = new WekaCommandLineHandler();

      // general
      addMetric(results, "Key_Run", currentRun);
      addMetric(results, "Key_Dataset", data.relationName());
      addMetric(results, "Key_Scheme", cls.getClass().getName());
      addMetric(results, "Key_Scheme_options", cmdlineHandler.joinOptions(cmdlineHandler.getOptions(cls)));
      addMetric(results, "Key_Scheme_version_ID", "" + ObjectStreamClass.lookup(cls.getClass()).getSerialVersionUID());

      // evaluation
      nominal = eval.getHeader().classAttribute().isNominal();
      m_ClassLabelIndex.setMax(eval.getHeader().classAttribute().numValues());
      classLabel = m_ClassLabelIndex.getIntIndex();
      for (EvaluationStatistic stat: EvaluationStatistic.values()) {
	if (stat.isOnlyNominal() && !nominal)
	  continue;
	if (stat.isOnlyNumeric() && nominal)
	  continue;
	metric = stat.toDisplayShort().replace(" ", "_");
	try {
	  addMetric(results, metric, EvaluationHelper.getValue(eval, stat, classLabel));
	}
	catch (Exception e) {
	  m_Owner.getLogger().log(Level.SEVERE, "Failed to retrieve statistic: " + stat, e);
	}
      }
    }

    /**
     * Performs the evaluation.
     */
    protected abstract void evaluate();

    /**
     * Performs the evaluation.
     */
    public void run() {
      if (m_Owner.isStopped())
	return;

      try {
        evaluate();
        m_Owner.appendResults(m_Results);
      }
      catch (Exception e) {
        m_Owner.log(
          "Failed to evaluate: "
            + "run=" + m_Run
            + ", dataset=" + m_Data.relationName()
            + ", classifier=" + Shortening.shortenEnd(OptionUtils.getCommandLine(m_Classifier), 100),
	    e);
      }
    }
  }

  /** the number of threads to use for parallel execution. */
  protected int m_NumThreads;

  /** whether to reset the results before starting the experiment. */
  protected boolean m_ResetResults;

  /** the classifiers to evaluate. */
  protected Classifier[] m_Classifiers;

  /** the datasets to evaluate. */
  protected PlaceholderFile[] m_Datasets;

  /** how to determine the class attribute. */
  protected AbstractClassAttributeHeuristic m_ClassAttribute;

  /** the class label index for per-class stats. */
  protected Index m_ClassLabelIndex;

  /** whether to use the filename (w/o path) instead of relationname. */
  protected boolean m_UseFilename;

  /** whether to prefix the relation names with the index. */
  protected boolean m_PrefixDatasetsWithIndex;

  /** the number of runs. */
  protected int m_Runs;

  /** the handler for the results. */
  protected AbstractResultsHandler m_ResultsHandler;

  /** for notifications. */
  protected transient StatusMessageHandler m_StatusMessageHandler;

  /** whether the experiment is running. */
  protected transient boolean m_Running;

  /** whether the experiment was stopped. */
  protected transient boolean m_Stopped;

  /** for handling commandlines. */
  protected transient WekaCommandLineHandler m_CommandLineHandler;

  /** the generated results. */
  protected SpreadSheet m_Results;

  /** the results generated by the evaluations. */
  protected List<SpreadSheet> m_Generated;

  /** Pool of threads to train models with */
  protected transient ThreadPoolExecutor m_ExecutorPool;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "num-threads", "numThreads",
      1, -1, null);

    m_OptionManager.add(
      "reset-results", "resetResults",
      false);

    m_OptionManager.add(
      "results-handler", "resultsHandler",
      new FileResultsHandler());

    m_OptionManager.add(
      "classifier", "classifiers",
      new Classifier[0]);

    m_OptionManager.add(
      "dataset", "datasets",
      new PlaceholderFile[0]);

    m_OptionManager.add(
      "class-attribute", "classAttribute",
      new LastAttribute());

    m_OptionManager.add(
      "class-label-index", "classLabelIndex",
      new Index(Index.FIRST));

    m_OptionManager.add(
      "use-filename", "useFilename",
      false);

    m_OptionManager.add(
      "prefix-datasets-with-index", "prefixDatasetsWithIndex",
      false);

    m_OptionManager.add(
      "runs", "runs",
      10, 1, null);
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
   * Sets whether to clear the results before starting the experiment.
   *
   * @param value	true if to clear results
   */
  public void setResetResults(boolean value) {
    m_ResetResults = value;
    reset();
  }

  /**
   * Returns whether to clear the results before starting the experiment.
   *
   * @return		true if to clear results
   */
  public boolean getResetResults() {
    return m_ResetResults;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String resetResultsTipText() {
    return "If enabled, any pre-existing results get discarded before the experiment starts.";
  }

  /**
   * Sets the results handler to use.
   *
   * @param value	the handler
   */
  public void setResultsHandler(AbstractResultsHandler value) {
    m_ResultsHandler = value;
    reset();
  }

  /**
   * Returns the results handler to use.
   *
   * @return		the handler
   */
  public AbstractResultsHandler getResultsHandler() {
    return m_ResultsHandler;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String resultWriterTipText() {
    return "The handler to use for the results (read/write).";
  }

  /**
   * Sets the classifiers to use.
   *
   * @param value	the classifiers
   */
  public void setClassifiers(Classifier[] value) {
    m_Classifiers = value;
    reset();
  }

  /**
   * Returns the classifiers.
   *
   * @return		the classifiers
   */
  public Classifier[] getClassifiers() {
    return m_Classifiers;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String classifiersTipText() {
    return "The classifiers to evaluate.";
  }

  /**
   * Adds a classifier.
   *
   * @param cls		the classifier to add
   */
  public void addClassifier(Classifier cls) {
    List<Classifier> 	classifiers;

    classifiers = new ArrayList<>(Arrays.asList(m_Classifiers));
    classifiers.add(cls);

    setClassifiers(classifiers.toArray(new Classifier[classifiers.size()]));
  }

  /**
   * Sets the datasets to use.
   *
   * @param value	the datasets
   */
  public void setDatasets(PlaceholderFile[] value) {
    m_Datasets = value;
    reset();
  }

  /**
   * The datasets in use.
   *
   * @return		the datasets
   */
  public PlaceholderFile[] getDatasets() {
    return m_Datasets;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String datasetsTipText() {
    return "The datasets to use";
  }

  /**
   * Adds a dataset.
   *
   * @param file		the dataset to add
   */
  public void addDataset(PlaceholderFile file) {
    List<PlaceholderFile> datasets;

    datasets = new ArrayList<>(Arrays.asList(m_Datasets));
    datasets.add(file);

    setDatasets(datasets.toArray(new PlaceholderFile[datasets.size()]));
  }

  /**
   * Sets the heuristic for determining the class attribute (if not explicitly set).
   *
   * @param value	the heuristic
   */
  public void setClassAttribute(AbstractClassAttributeHeuristic value) {
    m_ClassAttribute = value;
    reset();
  }

  /**
   * Returns the heuristic for determining the class attribute (if not explicitly set).
   *
   * @return		the heuristic
   */
  public AbstractClassAttributeHeuristic getClassAttribute() {
    return m_ClassAttribute;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String classAttributeTipText() {
    return "The heuristic for determining the class attribute in the datasets (if not explicitly set).";
  }

  /**
   * Sets the index of the class label to use when generating per-class statistics.
   *
   * @param value	the index
   */
  public void setClassLabelIndex(Index value) {
    m_ClassLabelIndex = value;
    reset();
  }

  /**
   * Returns the index of the class label to use when generating per-class statistics.
   *
   * @return		the index
   */
  public Index getClassLabelIndex() {
    return m_ClassLabelIndex;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String classLabelIndexTipText() {
    return "The index of the class label to use when generating per-class statistics.";
  }

  /**
   * Sets whether to use the filename (w/o path) instead of the relationname.
   *
   * @param value	true if to use filename
   */
  public void setUseFilename(boolean value) {
    m_UseFilename = value;
    reset();
  }

  /**
   * Returns whether to use the filename (w/o path) instead of the relationname.
   *
   * @return		true if to use the filename
   */
  public boolean getUseFilename() {
    return m_UseFilename;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useFilenameTipText() {
    return "If enabled, uses the filename (w/o path) as the name.";
  }

  /**
   * Sets whether to prefix the datasets with the index.
   *
   * @param value	true if to prefix
   */
  public void setPrefixDatasetsWithIndex(boolean value) {
    m_PrefixDatasetsWithIndex = value;
    reset();
  }

  /**
   * Returns whether to prefix the datasets with the index.
   *
   * @return		true if to prefix
   */
  public boolean getPrefixDatasetsWithIndex() {
    return m_PrefixDatasetsWithIndex;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String prefixDatasetsWithIndexTipText() {
    return "If enabled, prefixes the dataset name with the index.";
  }

  /**
   * Sets the number of runs.
   *
   * @param value	the runs
   */
  public void setRuns(int value) {
    m_Runs = value;
    reset();
  }

  /**
   * Returns the number of runs.
   *
   * @return		the runs
   */
  public int getRuns() {
    return m_Runs;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String runsTipText() {
    return "The number of runs to perform.";
  }

  /**
   * Sets the status message handler to use.
   *
   * @param value	the handler, null to turn off
   */
  public void setStatusMessageHandler(StatusMessageHandler value) {
    m_StatusMessageHandler = value;
  }

  /**
   * Returns the current status message handler in use.
   *
   * @return		the handler, null if none set
   */
  public StatusMessageHandler getStatusMessageHandler() {
    return m_StatusMessageHandler;
  }

  /**
   * Hook method just before the experiment is run (after initialization).
   *
   * @return		null if successful, otherwise error message
   */
  protected String preExecute() {
    return null;
  }

  /**
   * Returns the collected results.
   *
   * @return		the results
   */
  public SpreadSheet toSpreadSheet() {
    return m_Results;
  }

  /**
   * Returns the collected results.
   *
   * @return		the results as Instances
   */
  public Instances toInstances() {
    Instances			result;
    SpreadSheetToWekaInstances	conv;
    String			msg;

    if (m_Results == null)
      return null;

    conv = new SpreadSheetToWekaInstances();
    conv.setMaxLabels(m_Results.getRowCount() + 1);
    conv.setInput(m_Results);
    msg = conv.convert();
    if (msg != null) {
      getLogger().severe("Failed to convert results into Instances: " + msg);
      return null;
    }
    result = (Instances) conv.getOutput();

    return result;
  }

  /**
   * Displays the message.
   *
   * @param msg		the message to display
   */
  protected void log(String msg) {
    if (m_StatusMessageHandler != null)
      m_StatusMessageHandler.showStatus(msg);
    else
      getLogger().info(msg);
  }

  /**
   * Displays the error.
   *
   * @param msg		the message to display
   * @param t		the exception
   */
  protected void log(String msg, Throwable t) {
    if (m_StatusMessageHandler != null)
      m_StatusMessageHandler.showStatus(msg + "\n" + Utils.throwableToString(t));
    else
      getLogger().log(Level.SEVERE, msg, t);
  }

  /**
   * Initializes the results.
   *
   * @return		the results
   */
  protected SpreadSheet initResults() {
    SpreadSheet		result;

    result = null;

    if (!m_ResetResults)
      result = m_ResultsHandler.read();

    if (result == null)
      result = new DefaultSpreadSheet();

    return result;
  }

  /**
   * Initializes the experiment.
   */
  protected String initExecute() {
    int 		actNumThreads;

    m_Stopped            = false;
    m_Running            = true;
    m_CommandLineHandler = new WekaCommandLineHandler();
    m_Results            = initResults();
    m_Generated          = new ArrayList<>();
    if (m_Results == null)
      return "Failed to initialize results!";

    if (m_NumThreads < 1)
      actNumThreads = ProcessUtils.getAvailableProcessors();
    else
      actNumThreads = Math.min(m_NumThreads, ProcessUtils.getAvailableProcessors());
    m_ExecutorPool = new ThreadPoolExecutor(
      actNumThreads, actNumThreads,
      120, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

    return null;
  }

  /**
   * Loads the dataset.
   *
   * @param index	the index of the dataset to load
   * @return		the dataset
   */
  protected Instances loadDataset(int index) {
    Instances	result;
    File	file;

    file = m_Datasets[index];
    try {
      result = DataSource.read(file.getAbsolutePath());
      if (result.classIndex() == -1)
	result.setClassIndex(m_ClassAttribute.determineClassAttribute(result));
      if (m_UseFilename)
	result.setRelationName(FileUtils.replaceExtension(file, "").getName());
      if (m_PrefixDatasetsWithIndex)
	result.setRelationName((index+1) + ":" + result.relationName());
    }
    catch (Exception e) {
      result = null;
      getLogger().log(Level.SEVERE, "Failed to load dataset: " + file, e);
    }

    return result;
  }

  /**
   * Configures the row finder that determines whether the classifier/dataset
   * combination is still required.
   *
   * @param currentRun	the current run
   * @param cls		the classifier to check
   * @param data	the dataset to check
   * @return		the row finder setup
   */
  protected MultiRowFinder configureRowFinder(int currentRun, Classifier cls, Instances data) {
    MultiRowFinder	result;
    ByNumericValue	run;
    ByStringComparison 	dataset;
    ByStringComparison 	scheme;
    ByStringComparison 	options;
    ByStringComparison	version;

    run = new ByNumericValue();
    run.setAttributeIndex(new SpreadSheetColumnIndex("Key_Run"));
    run.setMinimum(currentRun);
    run.setMinimumIncluded(true);
    run.setMaximum(currentRun);
    run.setMaximumIncluded(true);

    dataset = new ByStringComparison();
    dataset.setAttributeIndex(new SpreadSheetColumnIndex("Key_Dataset"));
    dataset.setMinimum(data.relationName());
    dataset.setMinimumIncluded(true);
    dataset.setMaximum(data.relationName());
    dataset.setMaximumIncluded(true);

    scheme = new ByStringComparison();
    scheme.setAttributeIndex(new SpreadSheetColumnIndex("Key_Scheme"));
    scheme.setMinimum(cls.getClass().getName());
    scheme.setMinimumIncluded(true);
    scheme.setMaximum(cls.getClass().getName());
    scheme.setMaximumIncluded(true);

    options = new ByStringComparison();
    options.setAttributeIndex(new SpreadSheetColumnIndex("Key_Scheme_options"));
    options.setMinimum(m_CommandLineHandler.joinOptions(m_CommandLineHandler.getOptions(cls)));
    options.setMinimumIncluded(true);
    options.setMaximum(m_CommandLineHandler.joinOptions(m_CommandLineHandler.getOptions(cls)));
    options.setMaximumIncluded(true);

    version = new ByStringComparison();
    version.setAttributeIndex(new SpreadSheetColumnIndex("Key_Scheme_version_ID"));
    version.setMinimum("" + ObjectStreamClass.lookup(cls.getClass()).getSerialVersionUID());
    version.setMinimumIncluded(true);
    version.setMaximum("" + ObjectStreamClass.lookup(cls.getClass()).getSerialVersionUID());
    version.setMaximumIncluded(true);

    result = new MultiRowFinder();
    result.setCombination(Combination.INTERSECT);
    result.setFinders(new RowFinder[]{
      run,
      dataset,
      scheme,
      options,
      version,
    });

    return result;
  }

  /**
   * Checks whether the number of rows located in the current results are
   * complete.
   *
   * @param rows	the located results
   * @return		true if complete
   */
  protected boolean isComplete(int[] rows) {
    return (rows.length == 1);
  }

  /**
   * Checks whether the classifier/dataset combination is required.
   *
   * @param currentRun	the current run
   * @param cls		the classifier to check
   * @param data	the dataset to check
   * @return		true if required
   */
  protected synchronized boolean isRequired(int currentRun, Classifier cls, Instances data) {
    RowFinder		finder;
    int[]		rows;

    if (m_Results.getRowCount() == 0)
      return true;

    finder = configureRowFinder(currentRun, cls, data);
    rows = finder.findRows(m_Results);

    return !isComplete(rows);
  }

  /**
   * Removes the incomplete rows of the classifier/dataset combination.
   *
   * @param currentRun	the current run
   * @param cls		the classifier to check
   * @param data	the dataset to check
   */
  protected synchronized void removeIncomplete(int currentRun, Classifier cls, Instances data) {
    RowFinder		finder;
    int[]		rows;
    int			i;

    if (m_Results.getRowCount() == 0)
      return;

    finder = configureRowFinder(currentRun, cls, data);
    rows = finder.findRows(m_Results);
    if (rows.length > 0) {
      Arrays.sort(rows);
      for (i = rows.length - 1; i >= 0; i--)
	m_Results.removeRow(rows[i]);
    }
  }

  /**
   * Adds the results to the existing ones.
   *
   * @param results	the results to add
   */
  public synchronized void appendResults(SpreadSheet results) {
    m_Generated.add(results);
  }

  /**
   * Creates a runnabel to evaluate the classifier on the dataset.
   *
   * @param currentRun	the current run
   * @param cls		the classifier to evaluate
   * @param data	the dataset to evaluate on
   * @return		the runnable
   */
  protected abstract AbstractRun<? extends AbstractExperiment> evaluate(int currentRun, Classifier cls, Instances data);

  /**
   * Runs the actual experiment.
   *
   * @return		null if successful, otherwise error message
   */
  protected String doExecute() {
    int currentRun;
    int		d;
    int		c;
    Instances	data;

    for (d = 0; d < m_Datasets.length; d++) {
      if (m_Stopped)
	break;
      log("Loading dataset #" + (d + 1) + ": " + m_Datasets[d]);
      data = loadDataset(d);
      if (data == null)
	return "Failed to load dataset: " + m_Datasets[d];

      for (currentRun = 1; currentRun <= m_Runs; currentRun++) {
	if (m_Stopped)
	  break;
	for (c = 0; c < m_Classifiers.length; c++) {
	  if (m_Stopped)
	    break;
	  // results already present?
	  if (!isRequired(currentRun, m_Classifiers[c], data)) {
	    log("Run " + currentRun + ": " + data.relationName() + " on " + Shortening.shortenEnd(OptionUtils.getCommandLine(m_Classifiers[c]), 100) + " already present!");
	    continue;
	  }
	  // make sure no partial results
	  removeIncomplete(currentRun, m_Classifiers[c], data);
	  // start job
	  while (m_ExecutorPool.getActiveCount() >= m_ExecutorPool.getMaximumPoolSize()) {
	    Utils.wait(this, 100, 20);
	  }
	  m_ExecutorPool.submit(evaluate(currentRun, m_Classifiers[c], data));
	}
      }
    }

    log("Waiting for jobs to finish...");
    m_ExecutorPool.shutdown();
    while (!m_ExecutorPool.isTerminated()) {
      Utils.wait(this, 1000, 50);
    }

    if (m_Stopped) {
      log("Experiment stopped!");
      return "Experiment stopped!";
    }

    return null;
  }

  /**
   * Hook method just after the experiment was run.
   *
   * @param success	true if successfully run
   */
  protected void postExecute(boolean success) {
    String	msg;
    int		i;

    for (i = 0; i < m_Generated.size(); i++) {
      if (m_Results.getRowCount() == 0)
	m_Results = m_Generated.get(i);
      else
	SpreadSheetHelper.append(m_Results, m_Generated.get(i), true);
    }

    if (m_Results.getRowCount() > 0) {
      msg = m_ResultsHandler.write(m_Results);
      if (msg != null)
	log("Failed to store the results: " + msg);
    }
  }

  /**
   * Executes the experiment.
   *
   * @return		null if successful, otherwise error message
   */
  public String execute() {
    String	result;

    log("Initializing...");
    result = initExecute();
    if (result == null) {
      log("Pre-Execute...");
      result = preExecute();
    }
    if (result == null) {
      log("Execute...");
      result = doExecute();
      log("Post-Execute...");
      postExecute((result == null) && !m_Stopped);
    }

    if (result != null)
      log(result);

    return result;
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    m_Stopped = true;
    if (m_ExecutorPool != null)
      m_ExecutorPool.shutdownNow();
  }

  /**
   * Returns whether the experiment has been stopped.
   *
   * @return		true if stopped
   */
  public boolean isStopped() {
    return m_Stopped;
  }
}
