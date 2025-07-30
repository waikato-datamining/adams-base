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
 * Copyright (C) 2016-2025 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.clustertab.evaluation;

import adams.core.MessageCollection;
import adams.core.ObjectCopyHelper;
import adams.core.option.OptionUtils;
import adams.data.spreadsheet.MetaData;
import adams.gui.core.BaseComboBox;
import adams.gui.core.ParameterPanel;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import adams.gui.tools.wekainvestigator.evaluation.DatasetHelper;
import adams.gui.tools.wekainvestigator.tab.AbstractInvestigatorTab.SerializationOption;
import adams.gui.tools.wekainvestigator.tab.clustertab.ResultItem;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.Clusterer;
import weka.core.Capabilities;
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
  extends AbstractClustererEvaluation {

  private static final long serialVersionUID = -4460266467650893551L;

  public static final String KEY_TRAIN = "train";

  public static final String KEY_TEST = "test";

  /** the panel with the parameters. */
  protected ParameterPanel m_PanelParameters;

  /** the train set. */
  protected BaseComboBox<String> m_ComboBoxTrain;

  /** the test set. */
  protected BaseComboBox<String> m_ComboBoxTest;

  /** the datasets model. */
  protected DefaultComboBoxModel<String> m_ModelDatasets;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return
      "Builds the clusterer on the selected training set and evaluates it "
	+ "against the selected test set.";
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

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
   * Tests whether the clusterer can be evaluated.
   *
   * @return		null if successful, otherwise error message
   */
  public String canEvaluate(Clusterer clusterer) {
    Instances 		train;
    Instances 		test;
    Capabilities 	caps;

    if (!isValidDataIndex(m_ComboBoxTrain))
      return "No train data available!";
    if (!isValidDataIndex(m_ComboBoxTest))
      return "No test data available!";

    caps  = clusterer.getCapabilities();
    train = getOwner().getData().get(m_ComboBoxTrain.getSelectedIndex()).getData();
    try {
      if (!caps.test(train)) {
        if (caps.getFailReason() != null)
          return caps.getFailReason().getMessage();
        else
          return "Clusterer cannot handle tain data!";
      }
    }
    catch (Exception e) {
      return "Clusterer cannot handle data: " + e;
    }

    caps = clusterer.getCapabilities();
    test = getOwner().getData().get(m_ComboBoxTest.getSelectedIndex()).getData();
    try {
      if (!caps.test(test)) {
        if (caps.getFailReason() != null)
          return caps.getFailReason().getMessage();
        else
          return "Clusterer cannot handle test data!";
      }
    }
    catch (Exception e) {
      return "Clusterer cannot handle data: " + e;
    }

    if (!train.equalHeaders(test))
      return train.equalHeadersMsg(test);

    return null;
  }

  /**
   * Initializes the result item.
   *
   * @param clusterer	the current clusterer
   * @return		the initialized history item
   * @throws Exception	if initialization fails
   */
  public ResultItem init(Clusterer clusterer) throws Exception {
    ResultItem 		result;
    Instances		data;

    data = getOwner().getData().get(m_ComboBoxTrain.getSelectedIndex()).getData();
    result = new ResultItem(clusterer, new Instances(data, 0));

    return result;
  }

  /**
   * Evaluates the clusterer and updates the result item.
   *
   * @param clusterer	the current clusterer
   * @param item	the item to update
   * @throws Exception	if evaluation fails
   */
  @Override
  protected void doEvaluate(Clusterer clusterer, ResultItem item) throws Exception {
    ClusterEvaluation 	eval;
    Clusterer		model;
    DataContainer 	trainCont;
    DataContainer	testCont;
    Instances		train;
    Instances		test;
    String		msg;
    MetaData 		runInfo;

    if ((msg = canEvaluate(clusterer)) != null)
      throw new IllegalArgumentException("Cannot evaluate clusterer!\n" + msg);

    trainCont = getOwner().getData().get(m_ComboBoxTrain.getSelectedIndex());
    train     = trainCont.getData();
    testCont  = getOwner().getData().get(m_ComboBoxTest.getSelectedIndex());
    test      = testCont.getData();
    runInfo   = new MetaData();
    runInfo.add("Clusterer", OptionUtils.getCommandLine(clusterer));
    runInfo.add("Train ID", trainCont.getID());
    runInfo.add("Test ID", testCont.getID());
    runInfo.add("Relation", train.relationName());
    runInfo.add("# Attributes", train.numAttributes());
    runInfo.add("# Instances (train)", train.numInstances());
    runInfo.add("# Instances (test)", test.numInstances());

    model = ObjectCopyHelper.copyObject(clusterer);
    getOwner().logMessage("Using '" + train.relationName() + "' to train " + OptionUtils.getCommandLine(clusterer));
    model.buildClusterer(train);
    addObjectSize(runInfo, "Model size", model);
    getOwner().logMessage("Using '" + test.relationName() + "' to evaluate " + OptionUtils.getCommandLine(clusterer));
    eval = new ClusterEvaluation();
    eval.setClusterer(model);
    eval.evaluateClusterer(test);

    item.update(eval)
      .update(model)
      .update(runInfo);
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
  }
}
