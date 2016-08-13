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

import adams.core.option.OptionUtils;
import adams.gui.core.AbstractNamedHistoryPanel;
import adams.gui.core.ParameterPanel;
import adams.gui.tools.wekainvestigator.tab.classifytab.ResultItem;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Capabilities;
import weka.core.Instances;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Uses dedicated train/test sets.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TrainTestSet
  extends AbstractClassifierEvaluation {

  private static final long serialVersionUID = -4460266467650893551L;

  /** the panel with the parameters. */
  protected ParameterPanel m_PanelParameters;

  /** the train set. */
  protected JComboBox<String> m_ComboBoxTrain;

  /** the test set. */
  protected JComboBox<String> m_ComboBoxTest;

  /** the datasets model. */
  protected DefaultComboBoxModel<String> m_ModelDatasets;

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
    m_ComboBoxTrain = new JComboBox<>(m_ModelDatasets);
    m_ComboBoxTrain.addActionListener((ActionEvent e) -> update());
    m_PanelParameters.addParameter("Train", m_ComboBoxTrain);

    // Test
    m_ComboBoxTest = new JComboBox<>(m_ModelDatasets);
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
   * Tests whether the classifier can be evaluated.
   *
   * @return		null if successful, otherwise error message
   */
  public String canEvaluate(Classifier classifier) {
    Instances 		train;
    Instances 		test;
    Capabilities 	caps;

    if (m_ComboBoxTrain.getSelectedIndex() == -1)
      return "No train data available!";
    if (m_ComboBoxTest.getSelectedIndex() == -1)
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
  public ResultItem evaluate(Classifier classifier, AbstractNamedHistoryPanel<ResultItem> history) throws Exception {
    ResultItem 	result;
    Evaluation 	eval;
    Instances	train;
    Instances	test;
    String	msg;

    if ((msg = canEvaluate(classifier)) != null)
      throw new IllegalArgumentException("Cannot evaluate classifier!\n" + msg);

    train = getOwner().getData().get(m_ComboBoxTrain.getSelectedIndex()).getData();
    test  = getOwner().getData().get(m_ComboBoxTest.getSelectedIndex()).getData();
    classifier = (Classifier) OptionUtils.shallowCopy(classifier);
    getOwner().logMessage("Using '" + train.relationName() + "' to train " + OptionUtils.getCommandLine(classifier));
    classifier.buildClassifier(train);
    getOwner().logMessage("Using '" + test.relationName() + "' to evaluate " + OptionUtils.getCommandLine(classifier));
    eval = new Evaluation(train);
    eval.evaluateModel(classifier, test);

    // history
    return addToHistory(history, new ResultItem(eval, classifier, new Instances(train, 0)));
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
}
