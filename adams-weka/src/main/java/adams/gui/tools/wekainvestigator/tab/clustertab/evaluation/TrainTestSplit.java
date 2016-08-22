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
 * TrainTestSplit.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.clustertab.evaluation;

import adams.core.Properties;
import adams.core.Utils;
import adams.core.option.OptionUtils;
import adams.flow.container.WekaTrainTestSetContainer;
import adams.gui.core.AbstractNamedHistoryPanel;
import adams.gui.core.ParameterPanel;
import adams.gui.tools.wekainvestigator.InvestigatorPanel;
import adams.gui.tools.wekainvestigator.tab.clustertab.ResultItem;
import weka.classifiers.RandomSplitGenerator;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.Clusterer;
import weka.core.Capabilities;
import weka.core.Instances;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Uses a (random) percentage split to generate train/test.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TrainTestSplit
  extends AbstractClustererEvaluation {

  private static final long serialVersionUID = -4460266467650893551L;

  /** the panel with the parameters. */
  protected ParameterPanel m_PanelParameters;

  /** the datasets. */
  protected JComboBox<String> m_ComboBoxDatasets;

  /** the datasets model. */
  protected DefaultComboBoxModel<String> m_ModelDatasets;

  /** the split percentage. */
  protected JTextField m_TextPercentage;

  /** whether to preserve the order. */
  protected JCheckBox m_CheckBoxPreserveOrder;

  /** the seed value. */
  protected JTextField m_TextSeed;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return
      "Generates train/test sets from the selected dataset using the specified "
	+ "split percentage and builds/evaluates the clusterer accordingly.";
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    Properties	props;

    super.initGUI();

    props = InvestigatorPanel.getProperties();

    m_PanelParameters = new ParameterPanel();
    m_PanelOptions.add(m_PanelParameters, BorderLayout.CENTER);

    // dataset
    m_ModelDatasets    = new DefaultComboBoxModel<>();
    m_ComboBoxDatasets = new JComboBox<>(m_ModelDatasets);
    m_ComboBoxDatasets.addActionListener((ActionEvent e) -> update());
    m_PanelParameters.addParameter("Dataset", m_ComboBoxDatasets);

    // percentage
    m_TextPercentage = new JTextField("" + props.getInteger("Classify.TrainPercentage", 1));
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
    m_CheckBoxPreserveOrder = new JCheckBox();
    m_CheckBoxPreserveOrder.setSelected(props.getBoolean("Classify.PreserveOrder", false));
    m_CheckBoxPreserveOrder.setToolTipText("No randomization is performed if checked");
    m_CheckBoxPreserveOrder.addActionListener((ActionEvent e) -> update());
    m_PanelParameters.addParameter("Preserve order", m_CheckBoxPreserveOrder);

    // seed
    m_TextSeed = new JTextField("" + props.getInteger("Classify.Seed", 1));
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
  }

  /**
   * Returns the name of the evaluation (displayed in combobox).
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "Train/test split";
  }

  /**
   * Tests whether the clusterer can be evaluated.
   *
   * @return		null if successful, otherwise error message
   */
  public String canEvaluate(Clusterer clusterer) {
    Instances		data;
    double		perc;
    Capabilities	caps;

    if (m_ComboBoxDatasets.getSelectedIndex() == -1)
      return "No data available!";

    if (!Utils.isInteger(m_TextSeed.getText()))
      return "Seed value is not an integer!";

    if (!Utils.isDouble(m_TextPercentage.getText()))
      return "Percentage is not a number!";
    perc = Utils.toDouble(m_TextPercentage.getText());
    if ((perc <= 0) || (perc >= 100))
      return "Percentage must satisfy 0 < x < 100!";

    data = getOwner().getData().get(m_ComboBoxDatasets.getSelectedIndex()).getData();
    caps = clusterer.getCapabilities();
    if (!caps.test(data)) {
      if (caps.getFailReason() != null)
	return caps.getFailReason().getMessage();
      else
	return "Clusterer cannot handle data!";
    }

    return null;
  }

  /**
   * Evaluates the clusterer and returns the generated evaluation object.
   *
   * @param history	the history to add the result to
   * @return		the generate history item
   * @throws Exception	if evaluation fails
   */
  @Override
  public ResultItem evaluate(Clusterer clusterer, AbstractNamedHistoryPanel<ResultItem> history) throws Exception {
    ClusterEvaluation 		eval;
    Instances			data;
    Instances			train;
    Instances			test;
    RandomSplitGenerator generator;
    WekaTrainTestSetContainer	cont;
    String			msg;

    if ((msg = canEvaluate(clusterer)) != null)
      throw new IllegalArgumentException("Cannot evaluate clusterer!\n" + msg);

    data = getOwner().getData().get(m_ComboBoxDatasets.getSelectedIndex()).getData();
    if (m_CheckBoxPreserveOrder.isSelected())
      generator = new RandomSplitGenerator(data, Utils.toDouble(m_TextPercentage.getText()) / 100.0);
    else
      generator = new RandomSplitGenerator(data, Integer.parseInt(m_TextSeed.getText()), Utils.toDouble(m_TextPercentage.getText()) / 100.0);
    cont  = generator.next();
    train = (Instances) cont.getValue(WekaTrainTestSetContainer.VALUE_TRAIN);
    test  = (Instances) cont.getValue(WekaTrainTestSetContainer.VALUE_TEST);
    clusterer = (Clusterer) OptionUtils.shallowCopy(clusterer);
    getOwner().logMessage("Using " + m_TextPercentage.getText() + "% of '" + train.relationName() + "' to train " + OptionUtils.getCommandLine(clusterer));
    clusterer.buildClusterer(train);
    getOwner().logMessage("Using remainder from '" + test.relationName() + "' to evaluate " + OptionUtils.getCommandLine(clusterer));
    eval = new ClusterEvaluation();
    eval.setClusterer(clusterer);
    eval.evaluateClusterer(test);

    // history
    return addToHistory(history, new ResultItem(eval, clusterer, new Instances(train, 0)));
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

    getOwner().updateButtons();
  }
}
