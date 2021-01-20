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
 * CrossValidation.java
 * Copyright (C) 2016-2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.classifytab.evaluation;

import adams.core.MessageCollection;
import adams.core.ObjectCopyHelper;
import adams.core.Properties;
import adams.core.StoppableWithFeedback;
import adams.core.Utils;
import adams.core.option.OptionUtils;
import adams.data.spreadsheet.MetaData;
import adams.gui.chooser.SelectOptionPanel;
import adams.gui.core.BaseCheckBox;
import adams.gui.core.BaseComboBox;
import adams.gui.core.NumberTextField;
import adams.gui.core.NumberTextField.Type;
import adams.gui.core.ParameterPanel;
import adams.gui.goe.GenericObjectEditorPanel;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import adams.gui.tools.wekainvestigator.evaluation.DatasetHelper;
import adams.gui.tools.wekainvestigator.tab.AbstractInvestigatorTab.SerializationOption;
import adams.gui.tools.wekainvestigator.tab.classifytab.ResultItem;
import adams.gui.tools.wekainvestigator.tab.classifytab.evaluation.finalmodel.AbstractFinalModelGenerator;
import adams.gui.tools.wekainvestigator.tab.classifytab.evaluation.finalmodel.Simple;
import adams.multiprocess.JobRunner;
import adams.multiprocess.LocalJobRunner;
import adams.multiprocess.WekaCrossValidationExecution;
import weka.classifiers.Classifier;
import weka.classifiers.CrossValidationFoldGenerator;
import weka.classifiers.DefaultCrossValidationFoldGenerator;
import weka.core.Capabilities;
import weka.core.Instances;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Performs cross-validation.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class CrossValidation
  extends AbstractClassifierEvaluation
  implements StoppableWithFeedback {

  private static final long serialVersionUID = 1175400993991698944L;

  public static final String KEY_DATASET = "dataset";

  public static final String KEY_FOLDS = "folds";

  public static final String KEY_PERFOLDOUTPUT = "perfoldoutput";

  public static final String KEY_SEED = "seed";

  public static final String KEY_JOBRUNNER = "jobrunner";

  public static final String KEY_ADDITIONAL = "additional";

  public static final String KEY_USEVIEWS = "useviews";

  public static final String KEY_GENERATOR = "generator";

  public static final String KEY_DISCARDPREDICTIONS = "discardpredictions";

  public static final String KEY_FINALMODEL = "finalmodel";

  /** the panel with the parameters. */
  protected ParameterPanel m_PanelParameters;

  /** the datasets. */
  protected BaseComboBox<String> m_ComboBoxDatasets;

  /** the datasets model. */
  protected DefaultComboBoxModel<String> m_ModelDatasets;

  /** the number of folds. */
  protected JSpinner m_SpinnerFolds;

  /** whether to use separate evaluations per fold. */
  protected BaseCheckBox m_CheckBoxPerFoldOutput;

  /** the seed value. */
  protected NumberTextField m_TextSeed;

  /** the jobrunner. */
  protected GenericObjectEditorPanel m_GOEJobRunner;

  /** the additional attributes to store. */
  protected SelectOptionPanel m_SelectAdditionalAttributes;

  /** whether to use views. */
  protected BaseCheckBox m_CheckBoxUseViews;

  /** the fold generator. */
  protected GenericObjectEditorPanel m_GOEGenerator;

  /** whether to discard the predictions. */
  protected BaseCheckBox m_CheckBoxDiscardPredictions;
  
  /** how to produce the final model. */
  protected GenericObjectEditorPanel m_GOEFinalModel;

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
    AbstractFinalModelGenerator		finalmodel;

    super.initGUI();

    props = getProperties();

    m_PanelParameters = new ParameterPanel();
    m_PanelOptions.add(m_PanelParameters, BorderLayout.CENTER);

    // dataset
    m_ModelDatasets    = new DefaultComboBoxModel<>();
    m_ComboBoxDatasets = new BaseComboBox<>(m_ModelDatasets);
    m_ComboBoxDatasets.addActionListener((ActionEvent e) -> update());
    m_PanelParameters.addParameter("Dataset", m_ComboBoxDatasets);

    // folds
    m_SpinnerFolds = new JSpinner();
    ((SpinnerNumberModel) m_SpinnerFolds.getModel()).setMinimum(-1);
    ((SpinnerNumberModel) m_SpinnerFolds.getModel()).setStepSize(1);
    m_SpinnerFolds.setValue(props.getInteger("Classify.NumFolds", 10));
    m_SpinnerFolds.setToolTipText("The number of folds to use (< 2 for LOO-CV)");
    m_SpinnerFolds.addChangeListener((ChangeEvent e) -> update());
    m_PanelParameters.addParameter("Folds", m_SpinnerFolds);

    // per fold output?
    m_CheckBoxPerFoldOutput = new BaseCheckBox();
    m_CheckBoxPerFoldOutput.setSelected(props.getBoolean("Classify.PerFoldOutput", false));
    m_CheckBoxPerFoldOutput.setToolTipText("Keep separate evaluations per fold to inspect per fold performance");
    m_CheckBoxPerFoldOutput.addActionListener((ActionEvent e) -> update());
    m_PanelParameters.addParameter("Per fold output", m_CheckBoxPerFoldOutput);

    // seed
    m_TextSeed = new NumberTextField(Type.INTEGER, "" + props.getInteger("Classify.Seed", 1));
    m_TextSeed.setToolTipText("The seed value for randomizing the data");
    m_TextSeed.getDocument().addDocumentListener(new DocumentListener() {
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
    m_PanelParameters.addParameter("Seed", m_TextSeed);

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

    // use views?
    m_CheckBoxUseViews = new BaseCheckBox();
    m_CheckBoxUseViews.setSelected(props.getBoolean("Classify.UseViews", false));
    m_CheckBoxUseViews.setToolTipText("Save memory by using views instead of creating copies of datasets?");
    m_CheckBoxUseViews.addActionListener((ActionEvent e) -> update());
    m_PanelParameters.addParameter("Use views", m_CheckBoxUseViews);

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

    // discard predictions?
    m_CheckBoxDiscardPredictions = new BaseCheckBox();
    m_CheckBoxDiscardPredictions.setSelected(props.getBoolean("Classify.DiscardPredictions", false));
    m_CheckBoxDiscardPredictions.setToolTipText("Save memory by discarding predictions?");
    m_CheckBoxDiscardPredictions.addActionListener((ActionEvent e) -> update());
    m_PanelParameters.addParameter("Discard predictions", m_CheckBoxDiscardPredictions);

    // additional attributes
    m_SelectAdditionalAttributes = new SelectOptionPanel();
    m_SelectAdditionalAttributes.setCurrent(new String[0]);
    m_SelectAdditionalAttributes.setMultiSelect(true);
    m_SelectAdditionalAttributes.setLenient(true);
    m_SelectAdditionalAttributes.setDialogTitle("Select additional attributes");
    m_SelectAdditionalAttributes.setToolTipText("Additional attributes to make available in plots");
    m_PanelParameters.addParameter("Additional attributes", m_SelectAdditionalAttributes);

    // final model?
    try {
      finalmodel = (AbstractFinalModelGenerator) OptionUtils.forCommandLine(
        AbstractFinalModelGenerator.class,
	props.getProperty("Classify.CrossValidationFinalModel", new Simple().toCommandLine()));
    }
    catch (Exception e) {
      finalmodel = new Simple();
    }
    m_GOEFinalModel = new GenericObjectEditorPanel(AbstractFinalModelGenerator.class, finalmodel, true);
    m_GOEFinalModel.setToolTipText("How to produce a final model");
    m_GOEFinalModel.addChangeListener((ChangeEvent e) -> update());
    m_PanelParameters.addParameter("Final model", m_GOEFinalModel);
  }

  /**
   * Returns the name of the evaluation (displayed in combobox).
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "Cross-validation";
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

    if (!Utils.isInteger(m_TextSeed.getText()))
      return "Seed value is not an integer!";

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
    AbstractFinalModelGenerator		finalModel;
    boolean				views;
    boolean				discard;
    int					seed;
    int					folds;
    boolean				sepFolds;
    JobRunner 				jobrunner;
    CrossValidationFoldGenerator	generator;
    MetaData 				runInfo;

    if ((msg = canEvaluate(classifier)) != null)
      throw new IllegalArgumentException("Cannot evaluate classifier!\n" + msg);

    dataCont   = getOwner().getData().get(m_ComboBoxDatasets.getSelectedIndex());
    data       = dataCont.getData();
    finalModel = (AbstractFinalModelGenerator) m_GOEFinalModel.getCurrent();
    views      = m_CheckBoxUseViews.isSelected();
    discard    = m_CheckBoxDiscardPredictions.isSelected();
    seed       = m_TextSeed.getValue().intValue();
    folds      = ((Number) m_SpinnerFolds.getValue()).intValue();
    sepFolds   = m_CheckBoxPerFoldOutput.isSelected();
    jobrunner  = (JobRunner) m_GOEJobRunner.getCurrent();
    generator  = (CrossValidationFoldGenerator) m_GOEGenerator.getCurrent();
    runInfo    = new MetaData();
    runInfo.add("Classifier", OptionUtils.getCommandLine(classifier));
    runInfo.add("Seed", seed);
    runInfo.add("Folds", folds);
    runInfo.add("Separate folds", sepFolds);
    runInfo.add("JobRunner", jobrunner.toCommandLine());
    runInfo.add("Dataset ID", dataCont.getID());
    runInfo.add("Relation", data.relationName());
    runInfo.add("# Attributes", data.numAttributes());
    runInfo.add("# Instances", data.numInstances());
    runInfo.add("Class attribute", data.classAttribute().name());
    runInfo.add("Use views", views);
    runInfo.add("Fold generator", generator.toCommandLine());
    runInfo.add("Discard predictions", discard);
    if (m_SelectAdditionalAttributes.getCurrent().length > 0)
      runInfo.add("Additional attributes: ", Utils.flatten(m_SelectAdditionalAttributes.getCurrent(), ", "));
    m_CrossValidation = new WekaCrossValidationExecution();
    m_CrossValidation.setClassifier(classifier);
    m_CrossValidation.setData(data);
    m_CrossValidation.setFolds(folds);
    m_CrossValidation.setSeparateFolds(sepFolds);
    m_CrossValidation.setSeed(seed);
    m_CrossValidation.setJobRunner(jobrunner);
    m_CrossValidation.setUseViews(views);
    m_CrossValidation.setGenerator(ObjectCopyHelper.copyObject(generator));
    m_CrossValidation.setDiscardPredictions(discard);
    m_CrossValidation.setStatusMessageHandler(this);
    msg = m_CrossValidation.execute();
    if (msg != null)
      throw new Exception("Failed to cross-validate:\n" + msg);

    item.update(
      m_CrossValidation.getEvaluation(),
      sepFolds ? m_CrossValidation.getEvaluations() : null,
      null,
      sepFolds ? m_CrossValidation.getClassifiers() : null,
      runInfo,
      m_CrossValidation.getOriginalIndices(),
      transferAdditionalAttributes(m_SelectAdditionalAttributes, data));

    getOwner().logMessage("Building final model on '" + dataCont.getID() + "/" + data.relationName() + "' using " + OptionUtils.getCommandLine(classifier));
    finalModel.generate(this, data, item);

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
    index    = DatasetHelper.indexOfDataset(getOwner().getData(), (String) m_ComboBoxDatasets.getSelectedItem());
    if (DatasetHelper.hasDataChanged(datasets, m_ModelDatasets)) {
      m_ModelDatasets = new DefaultComboBoxModel<>(datasets.toArray(new String[datasets.size()]));
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
      result.put(KEY_FOLDS, m_SpinnerFolds.getValue());
      result.put(KEY_PERFOLDOUTPUT, m_CheckBoxPerFoldOutput.isSelected());
      result.put(KEY_SEED, m_TextSeed.getValue().intValue());
      result.put(KEY_JOBRUNNER, OptionUtils.getCommandLine(m_GOEJobRunner.getCurrent()));
      result.put(KEY_ADDITIONAL, m_SelectAdditionalAttributes.getCurrent());
      result.put(KEY_USEVIEWS, m_CheckBoxUseViews.isSelected());
      result.put(KEY_GENERATOR, OptionUtils.getCommandLine(m_GOEGenerator.getCurrent()));
      result.put(KEY_DISCARDPREDICTIONS, m_CheckBoxDiscardPredictions.isSelected());
      result.put(KEY_FINALMODEL, OptionUtils.getCommandLine(m_GOEFinalModel.getCurrent()));
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
    if (data.containsKey(KEY_FOLDS))
      m_SpinnerFolds.setValue(data.get(KEY_FOLDS));
    if (data.containsKey(KEY_PERFOLDOUTPUT))
      m_CheckBoxPerFoldOutput.setSelected((Boolean) data.get(KEY_PERFOLDOUTPUT));
    if (data.containsKey(KEY_SEED))
      m_TextSeed.setValue(((Number) data.get(KEY_SEED)).intValue());
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
    if (data.containsKey(KEY_USEVIEWS))
      m_CheckBoxUseViews.setSelected((Boolean) data.get(KEY_USEVIEWS));
    if (data.containsKey(KEY_GENERATOR)) {
      try {
	m_GOEGenerator.setCurrent(OptionUtils.forCommandLine(CrossValidationFoldGenerator.class, (String) data.get(KEY_GENERATOR)));
      }
      catch (Exception e) {
        errors.add("Failed to parse generator commandline: " + data.get(KEY_GENERATOR), e);
      }
    }
    if (data.containsKey(KEY_DISCARDPREDICTIONS))
      m_CheckBoxDiscardPredictions.setSelected((Boolean) data.get(KEY_DISCARDPREDICTIONS));
    if (data.containsKey(KEY_FINALMODEL)) {
      try {
	m_GOEFinalModel.setCurrent(OptionUtils.forCommandLine(AbstractFinalModelGenerator.class, (String) data.get(KEY_FINALMODEL)));
      }
      catch (Exception e) {
        errors.add("Failed to parse final model generator commandline: " + data.get(KEY_FINALMODEL), e);
      }
    }
  }
}
