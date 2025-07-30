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
 * RepeatedCrossValidation.java
 * Copyright (C) 2024 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.classifytab.evaluation;

import adams.core.MessageCollection;
import adams.core.ObjectCopyHelper;
import adams.core.Properties;
import adams.core.StoppableWithFeedback;
import adams.core.option.OptionUtils;
import adams.data.spreadsheet.MetaData;
import adams.gui.chooser.SelectOptionPanel;
import adams.gui.core.BaseComboBox;
import adams.gui.core.ParameterPanel;
import adams.gui.goe.GenericObjectEditorPanel;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import adams.gui.tools.wekainvestigator.evaluation.DatasetHelper;
import adams.gui.tools.wekainvestigator.tab.AbstractInvestigatorTab.SerializationOption;
import adams.gui.tools.wekainvestigator.tab.classifytab.ResultItem;
import adams.multiprocess.JobRunner;
import adams.multiprocess.LocalJobRunner;
import adams.multiprocess.WekaCrossValidationExecution;
import weka.classifiers.Classifier;
import weka.classifiers.CrossValidationFoldGenerator;
import weka.classifiers.DefaultCrossValidationFoldGenerator;
import weka.classifiers.Evaluation;
import weka.core.Capabilities;
import weka.core.Instances;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Performs repeated cross-validation.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RepeatedCrossValidation
  extends AbstractClassifierEvaluation
  implements StoppableWithFeedback {

  private static final long serialVersionUID = 1175400993991698944L;

  public static final String KEY_DATASET = "dataset";

  public static final String KEY_RUNS = "runs";

  public static final String KEY_FOLDS = "folds";

  public static final String KEY_JOBRUNNER = "jobrunner";

  public static final String KEY_ADDITIONAL = "additional";

  public static final String KEY_GENERATOR = "generator";

  /** the panel with the parameters. */
  protected ParameterPanel m_PanelParameters;

  /** the datasets. */
  protected BaseComboBox<String> m_ComboBoxDatasets;

  /** the datasets model. */
  protected DefaultComboBoxModel<String> m_ModelDatasets;

  /** the number of runs. */
  protected JSpinner m_SpinnerRuns;

  /** the number of folds. */
  protected JSpinner m_SpinnerFolds;

  /** the additional attributes to store. */
  protected SelectOptionPanel m_SelectAdditionalAttributes;

  /** the fold generator. */
  protected GenericObjectEditorPanel m_GOEGenerator;

  /** the jobrunner. */
  protected GenericObjectEditorPanel m_GOEJobRunner;

  /** performs the actual evaluation. */
  protected WekaCrossValidationExecution m_CrossValidation;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Performs cross-validation.";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_CrossValidation = null;
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    Properties 				props;
    CrossValidationFoldGenerator	generator;
    JobRunner				jobrunner;

    super.initGUI();

    props = getProperties();

    // 1. basic options
    m_PanelParameters = new ParameterPanel();
    m_PanelOptions.add(m_PanelParameters, BorderLayout.CENTER);

    // dataset
    m_ModelDatasets    = new DefaultComboBoxModel<>();
    m_ComboBoxDatasets = new BaseComboBox<>(m_ModelDatasets);
    m_ComboBoxDatasets.addActionListener((ActionEvent e) -> update());
    m_PanelParameters.addParameter("Dataset", m_ComboBoxDatasets);

    // runs
    m_SpinnerRuns = new JSpinner();
    ((SpinnerNumberModel) m_SpinnerRuns.getModel()).setMinimum(-1);
    ((SpinnerNumberModel) m_SpinnerRuns.getModel()).setStepSize(1);
    m_SpinnerRuns.setValue(props.getInteger("Classify.NumRuns", 10));
    m_SpinnerRuns.setToolTipText("The number of runs to perform");
    m_SpinnerRuns.addChangeListener((ChangeEvent e) -> update());
    m_PanelParameters.addParameter("Runs", m_SpinnerRuns);

    // folds
    m_SpinnerFolds = new JSpinner();
    ((SpinnerNumberModel) m_SpinnerFolds.getModel()).setMinimum(-1);
    ((SpinnerNumberModel) m_SpinnerFolds.getModel()).setStepSize(1);
    m_SpinnerFolds.setValue(props.getInteger("Classify.NumFolds", 10));
    m_SpinnerFolds.setToolTipText("The number of folds to use (< 2 for LOO-CV)");
    m_SpinnerFolds.addChangeListener((ChangeEvent e) -> update());
    m_PanelParameters.addParameter("Folds", m_SpinnerFolds);

    // generator
    try {
      generator = (CrossValidationFoldGenerator) OptionUtils.forCommandLine(
        CrossValidationFoldGenerator.class,
	props.getProperty("Classify.CrossValidationFoldGenerator",
	  new DefaultCrossValidationFoldGenerator().toCommandLine()));
    }
    catch (Exception e) {
      generator = new DefaultCrossValidationFoldGenerator();
    }
    m_GOEGenerator = new GenericObjectEditorPanel(CrossValidationFoldGenerator.class, generator, true);
    m_GOEGenerator.addChangeListener((ChangeEvent e) -> update());
    m_PanelParameters.addParameter("Generator", m_GOEGenerator);

    // additional attributes
    m_SelectAdditionalAttributes = new SelectOptionPanel();
    m_SelectAdditionalAttributes.setCurrent(new String[0]);
    m_SelectAdditionalAttributes.setMultiSelect(true);
    m_SelectAdditionalAttributes.setLenient(true);
    m_SelectAdditionalAttributes.setDialogTitle("Select additional attributes");
    m_SelectAdditionalAttributes.setToolTipText("Additional attributes to make available in plots");
    m_PanelParameters.addParameter("Additional attributes", m_SelectAdditionalAttributes);

    // jobrunner
    try {
      jobrunner = (JobRunner) OptionUtils.forCommandLine(
        JobRunner.class,
	props.getProperty("Classify.JobRunner", new LocalJobRunner().toCommandLine()));
    }
    catch (Exception e) {
      jobrunner = new LocalJobRunner();
    }
    m_GOEJobRunner = new GenericObjectEditorPanel(JobRunner.class, jobrunner, true);
    m_GOEJobRunner.setToolTipText("Whether to execute the jobs locally or remotely");
    m_GOEJobRunner.addChangeListener((ChangeEvent e) -> update());
    m_PanelParameters.addParameter("Job runner", m_GOEJobRunner);
  }

  /**
   * Returns the name of the evaluation (displayed in combobox).
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "Repeated cross-validation";
  }

  /**
   * Tests whether the classifier can be evaluated.
   *
   * @return		null if successful, otherwise error message
   */
  public String canEvaluate(Classifier classifier) {
    Instances		data;
    Capabilities 	caps;

    if (!isValidDataIndex(m_ComboBoxDatasets))
      return "No data available!";

    data = getOwner().getData().get(m_ComboBoxDatasets.getSelectedIndex()).getData();
    caps = classifier.getCapabilities();
    try {
      if (!caps.test(data)) {
        if (caps.getFailReason() != null)
          return caps.getFailReason().getMessage();
        else
          return "Classifier cannot handle data!";
      }
    }
    catch (Exception e) {
      return "Classifier cannot handle data: " + e;
    }

    return null;
  }

  /**
   * Initializes the result item.
   *
   * @param classifier	the current classifier
   * @return		the initialized history item
   * @throws Exception	if initialization fails
   */
  @Override
  public ResultItem init(Classifier classifier) throws Exception {
    ResultItem		result;
    Instances		data;

    data = getOwner().getData().get(m_ComboBoxDatasets.getSelectedIndex()).getData();
    result = new ResultItem(classifier, new Instances(data, 0));

    return result;
  }

  /**
   * Evaluates the classifier and updates the result item.
   *
   * @param classifier	the current classifier
   * @param item	the item to update
   * @throws Exception	if evaluation fails
   */
  @Override
  protected void doEvaluate(Classifier classifier, ResultItem item) throws Exception {
    String				msg;
    DataContainer			dataCont;
    Instances				data;
    int					runs;
    int					run;
    int					folds;
    JobRunner 				jobrunner;
    CrossValidationFoldGenerator	generator;
    MetaData 				runInfo;
    Evaluation[]			evals;
    Classifier[]			models;
    int[][]				original;

    if ((msg = canEvaluate(classifier)) != null)
      throw new IllegalArgumentException("Cannot evaluate classifier!\n" + msg);

    dataCont   = getOwner().getData().get(m_ComboBoxDatasets.getSelectedIndex());
    data       = dataCont.getData();
    runs       = ((Number) m_SpinnerRuns.getValue()).intValue();
    folds      = ((Number) m_SpinnerFolds.getValue()).intValue();
    jobrunner  = (JobRunner) m_GOEJobRunner.getCurrent();
    generator  = (CrossValidationFoldGenerator) m_GOEGenerator.getCurrent();
    runInfo    = new MetaData();
    runInfo.add("Classifier", OptionUtils.getCommandLine(classifier));
    runInfo.add("Folds", folds);
    runInfo.add("JobRunner", jobrunner.toCommandLine());
    runInfo.add("Dataset ID", dataCont.getID());
    runInfo.add("Relation", data.relationName());
    runInfo.add("# Attributes", data.numAttributes());
    runInfo.add("# Instances", data.numInstances());
    runInfo.add("Class attribute", data.classAttribute().name());
    runInfo.add("Fold generator", generator.toCommandLine());

    evals = new Evaluation[runs];
    models = new Classifier[runs];
    original = new int[runs][];
    for (run = 0; run < runs; run++) {
      if (isStopped())
	break;
      getOwner().logMessage("Repeated CV run #" + (run + 1) + " on '" + dataCont.getID() + "/" + data.relationName() + "' using " + OptionUtils.getCommandLine(classifier));

      m_CrossValidation = new WekaCrossValidationExecution();
      m_CrossValidation.setClassifier(classifier);
      m_CrossValidation.setData(data);
      m_CrossValidation.setFolds(folds);
      m_CrossValidation.setSeed(run + 1);
      m_CrossValidation.setJobRunner(jobrunner);
      m_CrossValidation.setGenerator(ObjectCopyHelper.copyObject(generator));
      m_CrossValidation.setStatusMessageHandler(this);
      msg = m_CrossValidation.execute();
      if (msg != null)
	throw new Exception("Failed to cross-validate (run #" + (run + 1) + "):\n" + msg);
      evals[run]    = m_CrossValidation.getEvaluation();
      models[run]   = m_CrossValidation.getClassifier();
      original[run] = m_CrossValidation.getOriginalIndices();
      m_CrossValidation.cleanUp();
    }

    if (!isStopped()) {
      item.update(runInfo)
	.update(transferAdditionalAttributes(m_SelectAdditionalAttributes, data))
	.updateRuns(evals)
	.updateRuns(models)
	.updateRuns(original);
    }

    m_CrossValidation.cleanUp();
    m_CrossValidation = null;
  }

  /**
   * Updates the settings panel.
   */
  @Override
  public void update() {
    List<String>	datasets;
    int			index;

    if (getOwner() == null)
      return;
    if (getOwner().getOwner() == null)
      return;

    datasets = DatasetHelper.generateDatasetList(getOwner().getData());
    index    = DatasetHelper.indexOfDataset(getOwner().getData(), m_ComboBoxDatasets.getSelectedItem());
    if (DatasetHelper.hasDataChanged(datasets, m_ModelDatasets)) {
      m_ModelDatasets = new DefaultComboBoxModel<>(datasets.toArray(new String[0]));
      m_ComboBoxDatasets.setModel(m_ModelDatasets);
      if ((index == -1) && (m_ModelDatasets.getSize() > 0))
	m_ComboBoxDatasets.setSelectedIndex(0);
      else if (index > -1)
	m_ComboBoxDatasets.setSelectedIndex(index);
    }

    fillWithAttributeNames(m_SelectAdditionalAttributes, m_ComboBoxDatasets.getSelectedIndex());

    getOwner().updateButtons();
  }

  /**
   * Activates the specified dataset.
   *
   * @param index	the index of the dataset
   */
  public void activate(int index) {
    m_ComboBoxDatasets.setSelectedIndex(index);
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    if (m_CrossValidation != null)
      m_CrossValidation.stopExecution();
  }

  /**
   * Whether the execution has been stopped.
   *
   * @return		true if stopped
   */
  @Override
  public boolean isStopped() {
    return (m_CrossValidation != null) && m_CrossValidation.isStopped();
  }

  /**
   * Returns the objects for serialization.
   *
   * @param options 	what to serialize
   * @return		the mapping of the objects to serialize
   */
  public Map<String,Object> serialize(Set<SerializationOption> options) {
    Map<String,Object>	result;

    result = super.serialize(options);
    if (options.contains(SerializationOption.GUI))
      result.put(KEY_DATASET, m_ComboBoxDatasets.getSelectedIndex());
    if (options.contains(SerializationOption.PARAMETERS)) {
      result.put(KEY_RUNS, m_SpinnerRuns.getValue());
      result.put(KEY_FOLDS, m_SpinnerFolds.getValue());
      result.put(KEY_JOBRUNNER, OptionUtils.getCommandLine(m_GOEJobRunner.getCurrent()));
      result.put(KEY_ADDITIONAL, m_SelectAdditionalAttributes.getCurrent());
      result.put(KEY_GENERATOR, OptionUtils.getCommandLine(m_GOEGenerator.getCurrent()));
    }

    return result;
  }

  /**
   * Restores the objects.
   *
   * @param data	the data to restore
   * @param errors	for storing errors
   */
  public void deserialize(Map<String,Object> data, MessageCollection errors) {
    super.deserialize(data, errors);
    if (data.containsKey(KEY_DATASET))
      m_ComboBoxDatasets.setSelectedIndex(((Number) data.get(KEY_DATASET)).intValue());
    if (data.containsKey(KEY_RUNS))
      m_SpinnerRuns.setValue(data.get(KEY_RUNS));
    if (data.containsKey(KEY_FOLDS))
      m_SpinnerFolds.setValue(data.get(KEY_FOLDS));
    if (data.containsKey(KEY_JOBRUNNER)) {
      try {
	m_GOEJobRunner.setCurrent(OptionUtils.forCommandLine(JobRunner.class, (String) data.get(KEY_JOBRUNNER)));
      }
      catch (Exception e) {
        errors.add("Failed to parse jobrunner commandline: " + data.get(KEY_JOBRUNNER), e);
      }
    }
    if (data.containsKey(KEY_ADDITIONAL))
      m_SelectAdditionalAttributes.setCurrent(listOrArray(data.get(KEY_ADDITIONAL)));
    if (data.containsKey(KEY_GENERATOR)) {
      try {
	m_GOEGenerator.setCurrent(OptionUtils.forCommandLine(CrossValidationFoldGenerator.class, (String) data.get(KEY_GENERATOR)));
      }
      catch (Exception e) {
        errors.add("Failed to parse generator commandline: " + data.get(KEY_GENERATOR), e);
      }
    }
  }
}
