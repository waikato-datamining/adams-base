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
 * TrainTestSplitSetup.java
 * Copyright (C) 2024-2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools.wekainvestigator.tab.experimenttab.setup;

import adams.core.MessageCollection;
import adams.core.ObjectCopyHelper;
import adams.core.Properties;
import adams.core.io.PlaceholderFile;
import adams.core.io.TempUtils;
import adams.core.option.OptionUtils;
import adams.data.spreadsheet.MetaData;
import adams.data.weka.WekaAttributeIndex;
import adams.data.weka.classattribute.AttributeIndex;
import adams.flow.container.WekaExperimentContainer;
import adams.gui.core.BaseCheckBox;
import adams.gui.core.BaseComboBox;
import adams.gui.core.NumberTextField;
import adams.gui.core.NumberTextField.Type;
import adams.gui.core.ParameterPanel;
import adams.gui.goe.GenericObjectEditorPanel;
import adams.gui.tools.wekainvestigator.evaluation.DatasetHelper;
import adams.gui.tools.wekainvestigator.tab.AbstractInvestigatorTab.SerializationOption;
import adams.gui.tools.wekainvestigator.tab.experimenttab.ResultItem;
import adams.gui.tools.wekamultiexperimenter.experiment.TrainTestSplitExperiment;
import adams.multiprocess.JobRunner;
import adams.multiprocess.LocalJobRunner;
import weka.classifiers.Classifier;
import weka.classifiers.CrossValidationFoldGenerator;
import weka.classifiers.DefaultRandomSplitGenerator;
import weka.classifiers.RandomSplitGenerator;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSink;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Setup for a train/test-split experiment.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class TrainTestSplitSetup
  extends AbstractExperimentSetup {

  private static final long serialVersionUID = 1236141032400058323L;

  public static final String KEY_DATASET = "dataset";

  public static final String KEY_RUNS = "runs";

  public static final String KEY_PERCENTAGE = "percentage";

  public static final String KEY_JOBRUNNER = "jobrunner";

  public static final String KEY_GENERATOR = "generator";

  /** the panel with the parameters. */
  protected ParameterPanel m_PanelParameters;

  /** the datasets. */
  protected BaseComboBox<String> m_ComboBoxDatasets;

  /** the datasets model. */
  protected DefaultComboBoxModel<String> m_ModelDatasets;

  /** the number of runs. */
  protected JSpinner m_SpinnerRuns;

  /** the split percentage. */
  protected NumberTextField m_TextPercentage;

  /** whether to preserve the order. */
  protected BaseCheckBox m_CheckBoxPreserveOrder;

  /** the split generator. */
  protected GenericObjectEditorPanel m_GOEGenerator;

  /** the jobrunner. */
  protected GenericObjectEditorPanel m_GOEJobRunner;

  /** the dataset in use. */
  protected Instances m_Dataset;

  /** the experiment. */
  protected TrainTestSplitExperiment m_Experiment;

  /** the dataset's tmp file. */
  protected File m_DatasetTmpFile;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Setup for a cross-validation experiment.";
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    Properties 			props;
    RandomSplitGenerator	generator;
    JobRunner			jobrunner;

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
    m_SpinnerRuns.setToolTipText("The number of repetitions");
    m_SpinnerRuns.addChangeListener((ChangeEvent e) -> update());
    m_PanelParameters.addParameter("Runs", m_SpinnerRuns);

    // percentage
    m_TextPercentage = new NumberTextField(Type.DOUBLE, "" + props.getDouble("Classify.TrainPercentage", 66.0));
    m_TextPercentage.setToolTipText("Percentage for train set (0 < x < 100)");
    m_TextPercentage.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
	update();
      }
      @Override
      public void removeUpdate(DocumentEvent e) {
	update();
      }
      @Override
      public void changedUpdate(DocumentEvent e) {
	update();
      }
    });
    m_PanelParameters.addParameter("Percentage", m_TextPercentage);

    // preserve order?
    m_CheckBoxPreserveOrder = new BaseCheckBox();
    m_CheckBoxPreserveOrder.setSelected(props.getBoolean("Classify.PreserveOrder", false));
    m_CheckBoxPreserveOrder.setToolTipText("No randomization is performed if checked");
    m_CheckBoxPreserveOrder.addActionListener((ActionEvent e) -> update());
    m_PanelParameters.addParameter("Preserve order", m_CheckBoxPreserveOrder);

    // generator
    try {
      generator = (RandomSplitGenerator) OptionUtils.forCommandLine(
	CrossValidationFoldGenerator.class,
	props.getProperty("Classify.CrossValidationFoldGenerator",
	  new DefaultRandomSplitGenerator().toCommandLine()));
    }
    catch (Exception e) {
      generator = new DefaultRandomSplitGenerator();
    }
    m_GOEGenerator = new GenericObjectEditorPanel(RandomSplitGenerator.class, generator, true);
    m_GOEGenerator.addChangeListener((ChangeEvent e) -> update());
    m_PanelParameters.addParameter("Generator", m_GOEGenerator);

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
   * @return the name
   */
  @Override
  public String getName() {
    return "Train/test split";
  }

  /**
   * Updates the settings panel.
   */
  @Override
  public void update() {
    List<String> datasets;
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

    getOwner().updateButtons();
  }

  /**
   * Activates the specified dataset.
   *
   * @param index the index of the dataset
   */
  @Override
  public void activate(int index) {
    m_ComboBoxDatasets.setSelectedIndex(index);
  }

  /**
   * Tests whether the experiment setup can be executed for the classifier.
   *
   * @param classifier 	the classifier to check
   * @return null if successful, otherwise error message
   */
  @Override
  public String canExecute(Classifier classifier) {
    Instances 		data;

    if (!getOwner().getData().isEmpty() && (m_ComboBoxDatasets.getSelectedIndex() > -1)) {
      data = getOwner().getData().get(m_ComboBoxDatasets.getSelectedIndex()).getData();
      if (data.classIndex() == -1)
	return "No class attribute set!";
      if (!classifier.getCapabilities().test(data))
	return "Classifier does not handle data!";
    }

    return null;
  }

  /**
   * Initializes the result item.
   *
   * @param classifier the current classifier
   * @throws Exception if initialization fails
   * @return the initialized history item
   */
  @Override
  public ResultItem init(Classifier classifier) throws Exception {
    ResultItem		result;
    AttributeIndex	index;

    m_Dataset = getOwner().getData().get(m_ComboBoxDatasets.getSelectedIndex()).getData();
    result    = new ResultItem(classifier, new Instances(m_Dataset, 0));

    // save dataset to tmp dir
    m_DatasetTmpFile = TempUtils.createTempFile("Investigator-" + getClass().getSimpleName() + "-", ".arff");
    getOwner().logMessage("Saving selected dataset to: " + m_DatasetTmpFile);
    DataSink.write(m_DatasetTmpFile.getAbsolutePath(), m_Dataset);

    // set up exp
    m_Experiment = new TrainTestSplitExperiment();
    m_Experiment.setRuns((Integer) m_SpinnerRuns.getValue());
    m_Experiment.setPercentage(m_TextPercentage.getValue().doubleValue());
    m_Experiment.setPreserveOrder(m_CheckBoxPreserveOrder.isSelected());
    m_Experiment.setClassifiers(new Classifier[]{ObjectCopyHelper.copyObject(classifier)});
    m_Experiment.setDatasets(new PlaceholderFile[]{new PlaceholderFile(m_DatasetTmpFile)});
    index = new AttributeIndex();
    index.setIndex(new WekaAttributeIndex("" + (m_Dataset.classIndex() + 1)));
    m_Experiment.setClassAttribute(index);
    m_Experiment.setGenerator((RandomSplitGenerator) m_GOEGenerator.getCurrent());
    m_Experiment.setJobRunner((JobRunner) m_GOEJobRunner.getCurrent());
    m_Experiment.setStatusMessageHandler(getOwner());

    return result;
  }

  /**
   * Executes the experiment setup for the classifier and updates the result item.
   *
   * @param classifier the current classifier
   * @param item       the item to update
   * @throws Exception if evaluation fails
   */
  @Override
  protected void doExecute(Classifier classifier, ResultItem item) throws Exception {
    String			msg;
    WekaExperimentContainer	cont;
    MetaData			meta;

    msg = m_Experiment.execute();
    if (msg != null)
      throw new Exception(msg);

    cont = new WekaExperimentContainer(m_Experiment, m_Experiment.toInstances(), null);
    meta = new MetaData();
    meta.add("Runs", m_SpinnerRuns.getValue());
    meta.add("Percentage", m_TextPercentage.getValue());
    meta.add("Preserve order", m_CheckBoxPreserveOrder.isSelected());
    meta.add("Classifier", OptionUtils.getCommandLine(classifier));
    meta.add("Dataset", m_Dataset.relationName());
    meta.add("# instances", m_Dataset.numInstances());
    meta.add("# attributes", m_Dataset.numAttributes());
    meta.add("Class", m_Dataset.classAttribute().name());
    meta.add("Generator", OptionUtils.getCommandLine(m_GOEGenerator.getCurrent()));
    meta.add("JobRunner", OptionUtils.getCommandLine(m_GOEJobRunner.getCurrent()));
    item.update(cont)
      .update(meta);
  }

  /**
   * Hook method for after executing the experiment, e.g., cleaning up temp files.
   *
   * @param classifier	the current classifier
   * @param item	the item to update
   */
  @Override
  protected void postExecute(Classifier classifier, ResultItem item) {
    if ((m_DatasetTmpFile != null) && m_DatasetTmpFile.exists() && m_DatasetTmpFile.isFile()) {
      getOwner().logMessage("Deleting temporary dataset: " + m_DatasetTmpFile);
      if (!m_DatasetTmpFile.delete())
	getOwner().logMessage("Failed to delete temporary dataset: " + m_DatasetTmpFile);
      m_DatasetTmpFile = null;
    }
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    if (m_Experiment != null)
      m_Experiment.stopExecution();

    super.stopExecution();
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
      result.put(KEY_PERCENTAGE, m_TextPercentage.getValue());
      result.put(KEY_JOBRUNNER, OptionUtils.getCommandLine(m_GOEJobRunner.getCurrent()));
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
    if (data.containsKey(KEY_PERCENTAGE))
      m_TextPercentage.setValue((Number) data.get(KEY_PERCENTAGE));
    if (data.containsKey(KEY_JOBRUNNER)) {
      try {
	m_GOEJobRunner.setCurrent(OptionUtils.forCommandLine(JobRunner.class, (String) data.get(KEY_JOBRUNNER)));
      }
      catch (Exception e) {
	errors.add("Failed to parse jobrunner commandline: " + data.get(KEY_JOBRUNNER), e);
      }
    }
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
