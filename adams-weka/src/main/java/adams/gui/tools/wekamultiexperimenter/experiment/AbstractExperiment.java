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
import adams.core.StatusMessageHandler;
import adams.core.Stoppable;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.OptionUtils;
import adams.core.option.WekaCommandLineHandler;
import adams.data.conversion.SpreadSheetToWekaInstances;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.HeaderRow;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
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
import java.util.logging.Level;

/**
 * Ancestor for simple experiments.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractExperiment
  extends AbstractOptionHandler
  implements Stoppable, ExperimentWithCustomizableRelationNames,
             ResettableExperiment, SpreadSheetSupporter {

  private static final long serialVersionUID = -345521029095304309L;

  /** whether to reset the results before starting the experiment. */
  protected boolean m_ResetResults;

  /** the classifiers to evaluate. */
  protected Classifier[] m_Classifiers;

  /** the datasets to evaluate. */
  protected PlaceholderFile[] m_Datasets;

  /** whether to iterate datasets first. */
  protected boolean m_DatasetsFirst;

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

  /** the current run. */
  protected transient int m_CurrentRun;

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

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

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
      "datasets-first", "datasetsFirst",
      false);

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
   * Sets whether to iterate datasets first.
   *
   * @param value	true if to iterate datasets first
   */
  public void setDatasetsFirst(boolean value) {
    m_DatasetsFirst = value;
    reset();
  }

  /**
   * Returns whether to iterate datasets first.
   *
   * @return		true if to iterate datasets first
   */
  public boolean getDatasetsFirst() {
    return m_DatasetsFirst;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String datasetsFirstsTipText() {
    return "If enabled, datasets are iterated first.";
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

    if (m_ResetResults)
      result = m_ResultsHandler.read();

    if (result == null)
      result = new DefaultSpreadSheet();

    return result;
  }

  /**
   * Initializes the experiment.
   */
  protected String initExecute() {
    m_Stopped            = false;
    m_Running            = true;
    m_CommandLineHandler = new WekaCommandLineHandler();
    m_Results            = initResults();
    if (m_Results == null)
      return "Failed to initialize results!";
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
   * @param cls		the classifier to check
   * @param data	the dataset to check
   * @return		the row finder setup
   */
  protected MultiRowFinder configureRowFinder(Classifier cls, Instances data) {
    MultiRowFinder	result;
    ByNumericValue	run;
    ByStringComparison 	dataset;
    ByStringComparison 	scheme;
    ByStringComparison 	options;
    ByStringComparison	version;

    run = new ByNumericValue();
    run.setAttributeIndex(new SpreadSheetColumnIndex("Key_Run"));
    run.setMinimum(m_CurrentRun);
    run.setMinimumIncluded(true);
    run.setMaximum(m_CurrentRun);
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
    return (rows.length == m_Runs);
  }

  /**
   * Checks whether the classifier/dataset combination is required.
   *
   * @param cls		the classifier to check
   * @param data	the dataset to check
   * @return		true if required
   */
  protected boolean isRequired(Classifier cls, Instances data) {
    RowFinder		finder;
    int[]		rows;

    if (m_Results.getRowCount() == 0)
      return true;

    finder = configureRowFinder(cls, data);
    rows   = finder.findRows(m_Results);

    return !isComplete(rows);
  }

  /**
   * Removes the incomplete rows of the classifier/dataset combination.
   *
   * @param cls		the classifier to check
   * @param data	the dataset to check
   */
  protected void removeIncomplete(Classifier cls, Instances data) {
    RowFinder		finder;
    int[]		rows;
    int			i;

    if (m_Results.getRowCount() == 0)
      return;

    finder = configureRowFinder(cls, data);
    rows   = finder.findRows(m_Results);
    if (rows.length > 0) {
      Arrays.sort(rows);
      for (i = rows.length - 1; i >= 0; i--)
	m_Results.removeRow(rows[i]);
    }
  }

  /**
   * Adds a new row in the results spreadsheet.
   */
  protected void newResults() {
    m_Results.addRow();
  }

  /**
   * Adds the metric to the results, automatically expands spreadsheet.
   *
   * @param name	the name
   * @param value	the value
   */
  protected void addMetric(String name, Object value) {
    HeaderRow	header;
    Row		row;
    int		index;

    header = m_Results.getHeaderRow();
    index  = header.indexOfContent(name);
    // not present?
    if (index == -1) {
      m_Results.insertColumn(m_Results.getColumnCount(), name);
      index = m_Results.getColumnCount() - 1;
    }
    row = m_Results.getRow(m_Results.getRowCount() - 1);
    row.addCell(index).setNative(value);
  }

  /**
   * Adds the metrics from the Evaluation object to the results.
   *
   * @param cls		the classifier to evaluate
   * @param data	the dataset to evaluate on
   * @param eval	the Evaluation object to add
   */
  protected void addMetrics(Classifier cls, Instances data, Evaluation eval) {
    boolean	nominal;
    String	metric;
    int		classLabel;

    newResults();

    // general
    addMetric("Key_Run", m_CurrentRun);
    addMetric("Key_Dataset", data.relationName());
    addMetric("Key_Scheme", cls.getClass().getName());
    addMetric("Key_Scheme_options", m_CommandLineHandler.joinOptions(m_CommandLineHandler.getOptions(cls)));
    addMetric("Key_Scheme_version_ID", "" + ObjectStreamClass.lookup(cls.getClass()).getSerialVersionUID());

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
	addMetric(metric, EvaluationHelper.getValue(eval, stat, classLabel));
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to retrieve statistic: " + stat, e);
      }
    }
  }

  /**
   * Evaluates the classifier on the dataset.
   *
   * @param cls		the classifier to evaluate
   * @param data	the dataset to evaluate on
   * @return		null if successful, otherwise error message
   */
  protected abstract String evaluate(Classifier cls, Instances data);

  /**
   * Outputs the progress.
   *
   * @param current	the current evaluation
   * @param total	the total number of evaluations
   * @param msg		the message
   */
  protected void progress(int current, int total, String msg) {
    double	perc;
    String	percStr;

    perc    = ((double) current / (double) total) * 100.0;
    percStr = "[" + Utils.doubleToString(perc, 1) + "%] ";

    log(percStr + msg);
  }

  /**
   * Runs the actual experiment.
   *
   * @return		null if successful, otherwise error message
   */
  protected String doExecute() {
    String	result;
    int		d;
    int		c;
    Instances	data;
    int		total;
    int		current;

    result  = null;
    total   = m_Runs * m_Datasets.length * m_Classifiers.length;
    current = 0;

    for (m_CurrentRun = 1; m_CurrentRun <= m_Runs; m_CurrentRun++) {
      progress(current, total, "Run " + m_CurrentRun);

      if (m_DatasetsFirst) {
	for (d = 0; d < m_Datasets.length; d++) {
	  if (m_Stopped || (result != null))
	    break;
	  progress(current, total, "Loading dataset #" + (d + 1) + ": " + m_Datasets[d]);
	  data = loadDataset(d);
	  if (data == null)
	    return "Failed to load dataset: " + m_Datasets[d];
	  for (c = 0; c < m_Classifiers.length; c++) {
	    if (m_Stopped || (result != null))
	      break;
	    current++;
	    // results already present?
	    if (!isRequired(m_Classifiers[c], data)) {
	      progress(current, total, "Classifier #" + (c + 1) + " already present: " + OptionUtils.getCommandLine(m_Classifiers[c]));
	      continue;
	    }
	    // remove any incomplete evaluation
	    removeIncomplete(m_Classifiers[c], data);
	    // evaluate classifier
	    progress(current, total, "Evaluating classifier #" + (c + 1) + ": " + OptionUtils.getCommandLine(m_Classifiers[c]));
	    result = evaluate(m_Classifiers[c], data);
	  }
	}
      }
      else {
	for (c = 0; c < m_Classifiers.length; c++) {
	  if (m_Stopped || (result != null))
	    break;
	  progress(current, total, "Evaluating classifier #" + (c + 1) + ": " + OptionUtils.getCommandLine(m_Classifiers[c]));
	  for (d = 0; d < m_Datasets.length; d++) {
	    if (m_Stopped || (result != null))
	      break;
	    current++;
	    progress(current, total, "Loading dataset #" + (d + 1) + ": " + m_Datasets[d]);
	    data = loadDataset(d);
	    if (data == null)
	      return "Failed to load dataset: " + m_Datasets[d];
	    // results already present?
	    if (!isRequired(m_Classifiers[c], data)) {
	      progress(current, total, "Classifier #" + (c + 1) + " already present: " + OptionUtils.getCommandLine(m_Classifiers[c]));
	      continue;
	    }
	    // remove any incomplete evaluation
	    removeIncomplete(m_Classifiers[c], data);
	    // evaluate classifier
	    progress(current, total, "Evaluating classifier #" + (c + 1) + ": " + OptionUtils.getCommandLine(m_Classifiers[c]));
	    result = evaluate(m_Classifiers[c], data);
	  }
	}
      }
    }

    if (m_Stopped) {
      progress(current, total, "Experiment stopped!");
      return "Experiment stopped!";
    }

    return result;
  }

  /**
   * Hook method just after the experiment was run.
   *
   * @param success	true if successfully run
   */
  protected void postExecute(boolean success) {
    String	msg;

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
  }
}
