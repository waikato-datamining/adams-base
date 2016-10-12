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
 * TrainTestSet.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.classifytab.evaluation;

import adams.core.MessageCollection;
import adams.core.Properties;
import adams.core.option.OptionUtils;
import adams.data.spreadsheet.MetaData;
import adams.gui.chooser.SelectOptionPanel;
import adams.gui.core.AbstractNamedHistoryPanel;
import adams.gui.core.ParameterPanel;
import adams.gui.tools.wekainvestigator.tab.classifytab.ResultItem;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Capabilities;
import weka.core.Instances;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;

/**
 * Uses dedicated train/test sets.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TrainTestSet
  extends AbstractClassifierEvaluation {

  private static final long serialVersionUID = -4460266467650893551L;

  public static final String KEY_TRAIN = "train";

  public static final String KEY_TEST = "test";

  public static final String KEY_ADDITIONAL = "additional";

  public static final String KEY_DISCARDPREDICTIONS = "discardpredictions";

  /** the panel with the parameters. */
  protected ParameterPanel m_PanelParameters;

  /** the train set. */
  protected JComboBox<String> m_ComboBoxTrain;

  /** the test set. */
  protected JComboBox<String> m_ComboBoxTest;

  /** the datasets model. */
  protected DefaultComboBoxModel<String> m_ModelDatasets;

  /** the additional attributes to store. */
  protected SelectOptionPanel m_SelectAdditionalAttributes;

  /** whether to discard the predictions. */
  protected JCheckBox m_CheckBoxDiscardPredictions;

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
    m_ComboBoxTrain = new JComboBox<>(m_ModelDatasets);
    m_ComboBoxTrain.addActionListener((ActionEvent e) -> update());
    m_PanelParameters.addParameter("Train", m_ComboBoxTrain);

    // Test
    m_ComboBoxTest = new JComboBox<>(m_ModelDatasets);
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
    m_CheckBoxDiscardPredictions = new JCheckBox();
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
    if (!caps.test(train)) {
      if (caps.getFailReason() != null)
	return caps.getFailReason().getMessage();
      else
	return "Classifier cannot handle tain data!";
    }

    caps = classifier.getCapabilities();
    test = getOwner().getData().get(m_ComboBoxTest.getSelectedIndex()).getData();
    if (!caps.test(test)) {
      if (caps.getFailReason() != null)
	return caps.getFailReason().getMessage();
      else
	return "Classifier cannot handle test data!";
    }

    if (!train.equalHeaders(test))
      return train.equalHeadersMsg(test);

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
    Evaluation 	eval;
    Classifier  model;
    Instances	train;
    Instances	test;
    boolean	discard;
    String	msg;
    MetaData 	runInfo;
    TIntList	original;
    int		i;

    if ((msg = canEvaluate(classifier)) != null)
      throw new IllegalArgumentException("Cannot evaluate classifier!\n" + msg);

    train   = getOwner().getData().get(m_ComboBoxTrain.getSelectedIndex()).getData();
    test    = getOwner().getData().get(m_ComboBoxTest.getSelectedIndex()).getData();
    discard = m_CheckBoxDiscardPredictions.isSelected();
    runInfo = new MetaData();
    runInfo.add("Classifier", OptionUtils.getCommandLine(classifier));
    runInfo.add("Train", train.relationName());
    runInfo.add("# Attributes", train.numAttributes());
    runInfo.add("# Instances (train)", train.numInstances());
    runInfo.add("# Instances (test)", test.numInstances());
    runInfo.add("Class attribute", train.classAttribute().name());
    runInfo.add("Discard predictions", discard);

    model = (Classifier) OptionUtils.shallowCopy(classifier);
    getOwner().logMessage("Using '" + train.relationName() + "' to train " + OptionUtils.getCommandLine(classifier));
    model.buildClassifier(train);
    getOwner().logMessage("Using '" + test.relationName() + "' to evaluate " + OptionUtils.getCommandLine(classifier));
    eval = new Evaluation(train);
    eval.setDiscardPredictions(discard);
    eval.evaluateModel(model, test);

    original = new TIntArrayList();
    for (i = 0; i < test.numInstances(); i++)
      original.add(i);

    // history
    return addToHistory(
      history,
      new ResultItem(eval, classifier, model, new Instances(train, 0), runInfo,
	original.toArray(), transferAdditionalAttributes(m_SelectAdditionalAttributes, test)));
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

    if (hasDataChanged(datasets, m_ModelDatasets)) {
      // train
      index = indexOfDataset((String) m_ComboBoxTrain.getSelectedItem());
      m_ModelDatasets = new DefaultComboBoxModel<>(datasets.toArray(new String[datasets.size()]));
      m_ComboBoxTrain.setModel(m_ModelDatasets);
      if ((index == -1) && (m_ModelDatasets.getSize() > 0))
	m_ComboBoxTrain.setSelectedIndex(0);
      else if (index > -1)
	m_ComboBoxTrain.setSelectedIndex(index);

      // test
      index = indexOfDataset((String) m_ComboBoxTest.getSelectedItem());
      m_ModelDatasets = new DefaultComboBoxModel<>(datasets.toArray(new String[datasets.size()]));
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
   * @return		the mapping of the objects to serialize
   */
  public Map<String,Object> serialize() {
    Map<String,Object>	result;

    result = super.serialize();
    result.put(KEY_TRAIN, m_ComboBoxTrain.getSelectedIndex());
    result.put(KEY_TEST, m_ComboBoxTest.getSelectedIndex());
    result.put(KEY_ADDITIONAL, m_SelectAdditionalAttributes.getCurrent());
    result.put(KEY_DISCARDPREDICTIONS, m_CheckBoxDiscardPredictions.isSelected());

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
      m_ComboBoxTrain.setSelectedIndex((int) data.get(KEY_TRAIN));
    if (data.containsKey(KEY_TEST))
      m_ComboBoxTest.setSelectedIndex((int) data.get(KEY_TEST));
    if (data.containsKey(KEY_ADDITIONAL))
      m_SelectAdditionalAttributes.setCurrent((String[]) data.get(KEY_ADDITIONAL));
    if (data.containsKey(KEY_DISCARDPREDICTIONS))
      m_CheckBoxDiscardPredictions.setSelected((Boolean) data.get(KEY_DISCARDPREDICTIONS));
  }
}
