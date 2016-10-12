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
 * CrossValidation.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.classifytab.evaluation;

import adams.core.MessageCollection;
import adams.core.Performance;
import adams.core.Properties;
import adams.core.StoppableWithFeedback;
import adams.core.Utils;
import adams.core.option.OptionUtils;
import adams.data.spreadsheet.MetaData;
import adams.gui.chooser.SelectOptionPanel;
import adams.gui.core.AbstractNamedHistoryPanel;
import adams.gui.core.NumberTextField;
import adams.gui.core.NumberTextField.Type;
import adams.gui.core.ParameterPanel;
import adams.gui.tools.wekainvestigator.tab.classifytab.ResultItem;
import adams.multiprocess.WekaCrossValidationExecution;
import weka.classifiers.Classifier;
import weka.core.Capabilities;
import weka.core.Instances;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;

/**
 * Performs cross-validation.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CrossValidation
  extends AbstractClassifierEvaluation
  implements StoppableWithFeedback {

  private static final long serialVersionUID = 1175400993991698944L;

  public static final String KEY_DATASET = "dataset";

  public static final String KEY_FOLDS = "folds";

  public static final String KEY_SEED = "seed";

  public static final String KEY_THREADS = "threads";

  public static final String KEY_ADDITIONAL = "additional";

  public static final String KEY_USEVIEWS = "useviews";

  public static final String KEY_DISCARDPREDICTIONS = "discardpredictions";

  public static final String KEY_FINALMODEL = "finalmodel";

  /** the panel with the parameters. */
  protected ParameterPanel m_PanelParameters;

  /** the datasets. */
  protected JComboBox<String> m_ComboBoxDatasets;

  /** the datasets model. */
  protected DefaultComboBoxModel<String> m_ModelDatasets;

  /** the number of folds. */
  protected JSpinner m_SpinnerFolds;

  /** the seed value. */
  protected NumberTextField m_TextSeed;

  /** the number of threads. */
  protected JSpinner m_SpinnerThreads;

  /** the additional attributes to store. */
  protected SelectOptionPanel m_SelectAdditionalAttributes;

  /** whether to use views. */
  protected JCheckBox m_CheckBoxUseViews;

  /** whether to discard the predictions. */
  protected JCheckBox m_CheckBoxDiscardPredictions;
  
  /** whether to produce a final model. */
  protected JCheckBox m_CheckBoxFinalModel;

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
    Properties 		props;

    super.initGUI();

    props = getProperties();

    m_PanelParameters = new ParameterPanel();
    m_PanelOptions.add(m_PanelParameters, BorderLayout.CENTER);

    // dataset
    m_ModelDatasets    = new DefaultComboBoxModel<>();
    m_ComboBoxDatasets = new JComboBox<>(m_ModelDatasets);
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

    // threads
    m_SpinnerThreads = new JSpinner();
    ((SpinnerNumberModel) m_SpinnerThreads.getModel()).setStepSize(1);
    m_SpinnerThreads.setValue(props.getInteger("Classify.NumThreads", -1));
    m_SpinnerThreads.setToolTipText(Performance.getNumThreadsHelp());
    m_SpinnerThreads.addChangeListener((ChangeEvent e) -> update());
    m_PanelParameters.addParameter("Threads", m_SpinnerThreads);

    // use views?
    m_CheckBoxUseViews = new JCheckBox();
    m_CheckBoxUseViews.setSelected(props.getBoolean("Classify.UseViews", false));
    m_CheckBoxUseViews.setToolTipText("Save memory by using views instead of creating copies of datasets?");
    m_CheckBoxUseViews.addActionListener((ActionEvent e) -> update());
    m_PanelParameters.addParameter("Use views", m_CheckBoxUseViews);

    // discard predictions?
    m_CheckBoxDiscardPredictions = new JCheckBox();
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
    m_CheckBoxFinalModel = new JCheckBox();
    m_CheckBoxFinalModel.setSelected(props.getBoolean("Classify.CrossValidationFinalModel", true));
    m_CheckBoxFinalModel.setToolTipText("Produce a final model using the full training data?");
    m_CheckBoxFinalModel.addActionListener((ActionEvent e) -> update());
    m_PanelParameters.addParameter("Final model", m_CheckBoxFinalModel);
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
    if (!caps.test(data)) {
      if (caps.getFailReason() != null)
	return caps.getFailReason().getMessage();
      else
	return "Classifier cannot handle data!";
    }

    return null;
  }

  /**
   * Evaluates the classifier and returns the generated evaluation object.
   *
   * @param history	the history to add the result to
   * @return		the generate history item
   * @throws Exception	if evaluation fails
   */
  @Override
  protected ResultItem doEvaluate(Classifier classifier, AbstractNamedHistoryPanel<ResultItem> history) throws Exception {
    ResultItem		result;
    String		msg;
    Instances		data;
    boolean		finalModel;
    boolean		views;
    boolean		discard;
    Classifier		model;
    int			seed;
    int			folds;
    int			threads;
    MetaData 		runInfo;

    if ((msg = canEvaluate(classifier)) != null)
      throw new IllegalArgumentException("Cannot evaluate classifier!\n" + msg);

    data       = getOwner().getData().get(m_ComboBoxDatasets.getSelectedIndex()).getData();
    finalModel = m_CheckBoxFinalModel.isSelected();
    views      = m_CheckBoxUseViews.isSelected();
    discard    = m_CheckBoxDiscardPredictions.isSelected();
    seed       = m_TextSeed.getValue().intValue();
    folds      = ((Number) m_SpinnerFolds.getValue()).intValue();
    threads    = ((Number) m_SpinnerThreads.getValue()).intValue();
    runInfo    = new MetaData();
    runInfo.add("Classifier", OptionUtils.getCommandLine(classifier));
    runInfo.add("Seed", seed);
    runInfo.add("Folds", folds);
    runInfo.add("Threads", threads);
    runInfo.add("Dataset", data.relationName());
    runInfo.add("# Attributes", data.numAttributes());
    runInfo.add("# Instances", data.numInstances());
    runInfo.add("Class attribute", data.classAttribute().name());
    runInfo.add("Use views", views);
    runInfo.add("Discard predictions", discard);
    m_CrossValidation = new WekaCrossValidationExecution();
    m_CrossValidation.setClassifier(classifier);
    m_CrossValidation.setData(data);
    m_CrossValidation.setFolds(folds);
    m_CrossValidation.setSeed(seed);
    m_CrossValidation.setNumThreads(threads);
    m_CrossValidation.setUseViews(views);
    m_CrossValidation.setDiscardPredictions(discard);
    m_CrossValidation.setStatusMessageHandler(this);
    msg = m_CrossValidation.execute();
    if (msg != null)
      throw new Exception("Failed to cross-validate:\n" + msg);

    // final model?
    model = null;
    if (finalModel) {
      getOwner().logMessage("Building final model on '" + data.relationName() + "' using " + OptionUtils.getCommandLine(classifier));
      model = (Classifier) OptionUtils.shallowCopy(classifier);
      model.buildClassifier(data);
    }

    // history
    result = addToHistory(
      history, new ResultItem(m_CrossValidation.getEvaluation(),
	classifier, model, new Instances(data, 0), runInfo,
	m_CrossValidation.getOriginalIndices(),
	transferAdditionalAttributes(m_SelectAdditionalAttributes, data)));

    m_CrossValidation = null;

    return result;
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

    datasets = generateDatasetList();
    index    = indexOfDataset((String) m_ComboBoxDatasets.getSelectedItem());
    if (hasDataChanged(datasets, m_ModelDatasets)) {
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
   * @return		the mapping of the objects to serialize
   */
  public Map<String,Object> serialize() {
    Map<String,Object>	result;

    result = super.serialize();
    result.put(KEY_DATASET, m_ComboBoxDatasets.getSelectedIndex());
    result.put(KEY_FOLDS, m_SpinnerFolds.getValue());
    result.put(KEY_SEED, m_TextSeed.getValue().intValue());
    result.put(KEY_THREADS, m_SpinnerThreads.getValue());
    result.put(KEY_ADDITIONAL, m_SelectAdditionalAttributes.getCurrent());
    result.put(KEY_USEVIEWS, m_CheckBoxUseViews.isSelected());
    result.put(KEY_DISCARDPREDICTIONS, m_CheckBoxDiscardPredictions.isSelected());
    result.put(KEY_FINALMODEL, m_CheckBoxFinalModel.isSelected());

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
      m_ComboBoxDatasets.setSelectedIndex((int) data.get(KEY_DATASET));
    if (data.containsKey(KEY_FOLDS))
      m_SpinnerFolds.setValue(data.get(KEY_FOLDS));
    if (data.containsKey(KEY_SEED))
      m_TextSeed.setValue((int) data.get(KEY_SEED));
    if (data.containsKey(KEY_THREADS))
      m_SpinnerThreads.setValue(data.get(KEY_THREADS));
    if (data.containsKey(KEY_ADDITIONAL))
      m_SelectAdditionalAttributes.setCurrent((String[]) data.get(KEY_ADDITIONAL));
    if (data.containsKey(KEY_USEVIEWS))
      m_CheckBoxUseViews.setSelected((Boolean) data.get(KEY_USEVIEWS));
    if (data.containsKey(KEY_DISCARDPREDICTIONS))
      m_CheckBoxDiscardPredictions.setSelected((Boolean) data.get(KEY_DISCARDPREDICTIONS));
    if (data.containsKey(KEY_FINALMODEL))
      m_CheckBoxFinalModel.setSelected((Boolean) data.get(KEY_FINALMODEL));
  }
}
