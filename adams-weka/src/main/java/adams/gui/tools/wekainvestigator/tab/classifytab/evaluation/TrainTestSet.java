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
 * TrainTestSet.java
 * Copyright (C) 2016-2024 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.classifytab.evaluation;

import adams.core.MessageCollection;
import adams.core.ObjectCopyHelper;
import adams.core.Properties;
import adams.core.StoppableUtils;
import adams.core.StoppableWithFeedback;
import adams.core.Utils;
import adams.core.option.OptionUtils;
import adams.data.spreadsheet.MetaData;
import adams.gui.chooser.SelectOptionPanel;
import adams.gui.core.BaseCheckBox;
import adams.gui.core.BaseComboBox;
import adams.gui.core.ParameterPanel;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import adams.gui.tools.wekainvestigator.evaluation.DatasetHelper;
import adams.gui.tools.wekainvestigator.tab.AbstractInvestigatorTab.SerializationOption;
import adams.gui.tools.wekainvestigator.tab.classifytab.ResultItem;
import weka.classifiers.Classifier;
import weka.classifiers.StoppableEvaluation;
import weka.classifiers.TestingHelper;
import weka.classifiers.TestingHelper.TestingUpdateListener;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instances;

import javax.swing.DefaultComboBoxModel;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Uses dedicated train/test sets.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class TrainTestSet
  extends AbstractClassifierEvaluation
  implements StoppableWithFeedback {

  private static final long serialVersionUID = -4460266467650893551L;

  public static final String KEY_TRAIN = "train";

  public static final String KEY_TEST = "test";

  public static final String KEY_ADDITIONAL = "additional";

  public static final String KEY_DISCARDPREDICTIONS = "discardpredictions";

  /** the panel with the parameters. */
  protected ParameterPanel m_PanelParameters;

  /** the train set. */
  protected BaseComboBox<String> m_ComboBoxTrain;

  /** the test set. */
  protected BaseComboBox<String> m_ComboBoxTest;

  /** the datasets model. */
  protected DefaultComboBoxModel<String> m_ModelDatasets;

  /** the additional attributes to store. */
  protected SelectOptionPanel m_SelectAdditionalAttributes;

  /** whether to discard the predictions. */
  protected BaseCheckBox m_CheckBoxDiscardPredictions;

  /** the current model. */
  protected transient Classifier m_Model;

  /** the current evaluation. */
  protected StoppableEvaluation m_Evaluation;

  /** whether the build was stopped. */
  protected boolean m_Stopped;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return
      "Trains the classifier on the selected training set and evaluates it "
	+ "against the selected test set.";
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    Properties		props;

    super.initGUI();

    props = getProperties();

    m_PanelParameters = new ParameterPanel();
    m_PanelOptions.add(m_PanelParameters, BorderLayout.CENTER);

    m_ModelDatasets = new DefaultComboBoxModel<>();

    // Train
    m_ComboBoxTrain = new BaseComboBox<>(m_ModelDatasets);
    m_ComboBoxTrain.addActionListener((ActionEvent e) -> update());
    m_PanelParameters.addParameter("Train", m_ComboBoxTrain);

    // Test
    m_ComboBoxTest = new BaseComboBox<>(m_ModelDatasets);
    m_ComboBoxTest.addActionListener((ActionEvent e) -> update());
    m_PanelParameters.addParameter("Test", m_ComboBoxTest);

    // additional attributes
    m_SelectAdditionalAttributes = new SelectOptionPanel();
    m_SelectAdditionalAttributes.setCurrent(new String[0]);
    m_SelectAdditionalAttributes.setMultiSelect(true);
    m_SelectAdditionalAttributes.setLenient(true);
    m_SelectAdditionalAttributes.setDialogTitle("Select additional attributes");
    m_SelectAdditionalAttributes.setToolTipText("Additional attributes to make available in plots");
    m_PanelParameters.addParameter("Additional attributes", m_SelectAdditionalAttributes);

    // discard predictions?
    m_CheckBoxDiscardPredictions = new BaseCheckBox();
    m_CheckBoxDiscardPredictions.setSelected(props.getBoolean("Classify.DiscardPredictions", false));
    m_CheckBoxDiscardPredictions.setToolTipText("Save memory by discarding predictions?");
    m_CheckBoxDiscardPredictions.addActionListener((ActionEvent e) -> update());
    m_PanelParameters.addParameter("Discard predictions", m_CheckBoxDiscardPredictions);
  }

  /**
   * Returns the name of the evaluation (displayed in combobox).
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "Train/test set";
  }

  /**
   * Tests whether the classifier can be evaluated.
   *
   * @return		null if successful, otherwise error message
   */
  public String canEvaluate(Classifier classifier) {
    Instances 		train;
    Instances 		test;
    Capabilities 	caps;

    if (!isValidDataIndex(m_ComboBoxTrain))
      return "No train data available!";
    if (!isValidDataIndex(m_ComboBoxTest))
      return "No test data available!";

    caps  = classifier.getCapabilities();
    train = getOwner().getData().get(m_ComboBoxTrain.getSelectedIndex()).getData();
    try {
      if (!caps.test(train)) {
	if (caps.getFailReason() != null)
	  return caps.getFailReason().getMessage();
	else
	  return "Classifier cannot handle train data!";
      }
    }
    catch (Exception e) {
      return "Classifier cannot handle data: " + e;
    }

    caps = classifier.getCapabilities();
    caps.enable(Capability.MISSING_CLASS_VALUES);  // necessary when just wanting to make predictions
    test = getOwner().getData().get(m_ComboBoxTest.getSelectedIndex()).getData();
    try {
      if (!caps.test(test)) {
        if (caps.getFailReason() != null)
          return caps.getFailReason().getMessage();
        else
          return "Classifier cannot handle test data!";
      }
    }
    catch (Exception e) {
      return "Classifier cannot handle data: " + e;
    }

    if (!train.equalHeaders(test))
      return train.equalHeadersMsg(test);

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

    data = getOwner().getData().get(m_ComboBoxTrain.getSelectedIndex()).getData();
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
    DataContainer 	trainCont;
    DataContainer 	testCont;
    Instances		train;
    Instances		test;
    boolean		discard;
    String		msg;
    MetaData 		runInfo;

    if ((msg = canEvaluate(classifier)) != null)
      throw new IllegalArgumentException("Cannot evaluate classifier!\n" + msg);

    m_Stopped = false;
    trainCont = getOwner().getData().get(m_ComboBoxTrain.getSelectedIndex());
    testCont  = getOwner().getData().get(m_ComboBoxTest.getSelectedIndex());
    train     = trainCont.getData();
    test      = testCont.getData();
    discard   = m_CheckBoxDiscardPredictions.isSelected();
    runInfo   = new MetaData();
    runInfo.add("Classifier", OptionUtils.getCommandLine(classifier));
    runInfo.add("Train ID", trainCont.getID());
    runInfo.add("Test ID", testCont.getID());
    runInfo.add("Relation (train)", train.relationName());
    runInfo.add("Relation (test)", test.relationName());
    runInfo.add("# Attributes", train.numAttributes());
    runInfo.add("# Instances (train)", train.numInstances());
    runInfo.add("# Instances (test)", test.numInstances());
    runInfo.add("Class attribute", train.classAttribute().name());
    runInfo.add("Discard predictions", discard);
    if (m_SelectAdditionalAttributes.getCurrent().length > 0)
      runInfo.add("Additional attributes: ", Utils.flatten(m_SelectAdditionalAttributes.getCurrent(), ", "));

    m_Model = ObjectCopyHelper.copyObject(classifier);
    getOwner().logMessage("Using '" + trainCont.getID() + "/" + train.relationName() + "' to train " + OptionUtils.getCommandLine(classifier));
    m_Model.buildClassifier(train);
    addObjectSize(runInfo, "Model size", m_Model);
    getOwner().logMessage("Using '" + testCont.getID() + "/" + test.relationName() + "' to evaluate " + OptionUtils.getCommandLine(classifier));

    m_Evaluation = new StoppableEvaluation(train);
    m_Evaluation.setDiscardPredictions(discard);
    TestingHelper.evaluateModel(m_Model, test, m_Evaluation, getTestingUpdateInterval(), new TestingUpdateListener() {
      @Override
      public void testingUpdateRequested(Instances data, int numTested, int numTotal) {
        getOwner().logMessage("Used " + numTested + "/" + numTotal + " of '" + testCont.getID() + "/" + test.relationName() + "' to evaluate " + OptionUtils.getCommandLine(classifier));
      }
    });

    item.update(
      m_Evaluation, m_Model, runInfo,
      null, transferAdditionalAttributes(m_SelectAdditionalAttributes, test));

    m_Model      = null;
    m_Evaluation = null;
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

    if (DatasetHelper.hasDataChanged(datasets, m_ModelDatasets)) {
      // train
      index = DatasetHelper.indexOfDataset(getOwner().getData(), m_ComboBoxTrain.getSelectedItem());
      m_ModelDatasets = new DefaultComboBoxModel<>(datasets.toArray(new String[0]));
      m_ComboBoxTrain.setModel(m_ModelDatasets);
      if ((index == -1) && (m_ModelDatasets.getSize() > 0))
	m_ComboBoxTrain.setSelectedIndex(0);
      else if (index > -1)
	m_ComboBoxTrain.setSelectedIndex(index);

      // test
      index = DatasetHelper.indexOfDataset(getOwner().getData(), m_ComboBoxTest.getSelectedItem());
      m_ModelDatasets = new DefaultComboBoxModel<>(datasets.toArray(new String[0]));
      m_ComboBoxTest.setModel(m_ModelDatasets);
      if ((index == -1) && (m_ModelDatasets.getSize() > 0))
	m_ComboBoxTest.setSelectedIndex(0);
      else if (index > -1)
	m_ComboBoxTest.setSelectedIndex(index);
    }

    fillWithAttributeNames(m_SelectAdditionalAttributes, m_ComboBoxTest.getSelectedIndex());

    getOwner().updateButtons();
  }

  /**
   * Activates the specified dataset.
   *
   * @param index	the index of the dataset
   */
  public void activate(int index) {
    m_ComboBoxTrain.setSelectedIndex(index);
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    m_Stopped = true;
    StoppableUtils.stopAnyExecution(m_Model);
    StoppableUtils.stopExecution(m_Evaluation);
  }

  /**
   * Whether the execution has been stopped.
   *
   * @return		true if stopped
   */
  @Override
  public boolean isStopped() {
    return m_Stopped;
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
    if (options.contains(SerializationOption.GUI)) {
      result.put(KEY_TRAIN, m_ComboBoxTrain.getSelectedIndex());
      result.put(KEY_TEST, m_ComboBoxTest.getSelectedIndex());
    }
    if (options.contains(SerializationOption.PARAMETERS)) {
      result.put(KEY_ADDITIONAL, m_SelectAdditionalAttributes.getCurrent());
      result.put(KEY_DISCARDPREDICTIONS, m_CheckBoxDiscardPredictions.isSelected());
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
    if (data.containsKey(KEY_TRAIN))
      m_ComboBoxTrain.setSelectedIndex(((Number) data.get(KEY_TRAIN)).intValue());
    if (data.containsKey(KEY_TEST))
      m_ComboBoxTest.setSelectedIndex(((Number) data.get(KEY_TEST)).intValue());
    if (data.containsKey(KEY_ADDITIONAL))
      m_SelectAdditionalAttributes.setCurrent(listOrArray(data.get(KEY_ADDITIONAL)));
    if (data.containsKey(KEY_DISCARDPREDICTIONS))
      m_CheckBoxDiscardPredictions.setSelected((Boolean) data.get(KEY_DISCARDPREDICTIONS));
  }
}
