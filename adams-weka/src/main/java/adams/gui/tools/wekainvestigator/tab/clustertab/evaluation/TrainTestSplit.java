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

import adams.core.MessageCollection;
import adams.core.Properties;
import adams.core.Utils;
import adams.core.option.OptionUtils;
import adams.data.spreadsheet.MetaData;
import adams.flow.container.WekaTrainTestSetContainer;
import adams.gui.core.AbstractNamedHistoryPanel;
import adams.gui.core.NumberTextField;
import adams.gui.core.NumberTextField.Type;
import adams.gui.core.ParameterPanel;
import adams.gui.tools.wekainvestigator.InvestigatorPanel;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import adams.gui.tools.wekainvestigator.evaluation.DatasetHelper;
import adams.gui.tools.wekainvestigator.tab.clustertab.ResultItem;
import weka.classifiers.RandomSplitGenerator;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.Clusterer;
import weka.core.Capabilities;
import weka.core.Instances;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;

/**
 * Uses a (random) percentage split to generate train/test.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TrainTestSplit
  extends AbstractClustererEvaluation {

  private static final long serialVersionUID = -4460266467650893551L;

  public static final String KEY_DATASET = "dataset";

  public static final String KEY_PERCENTAGE = "percentage";

  public static final String KEY_SEED = "seed";

  public static final String KEY_PRESERVEORDER = "preserveorder";

  public static final String KEY_USEVIEWS = "useviews";

  /** the panel with the parameters. */
  protected ParameterPanel m_PanelParameters;

  /** the datasets. */
  protected JComboBox<String> m_ComboBoxDatasets;

  /** the datasets model. */
  protected DefaultComboBoxModel<String> m_ModelDatasets;

  /** the split percentage. */
  protected NumberTextField m_TextPercentage;

  /** whether to preserve the order. */
  protected JCheckBox m_CheckBoxPreserveOrder;

  /** the seed value. */
  protected NumberTextField m_TextSeed;

  /** whether to use views. */
  protected JCheckBox m_CheckBoxUseViews;

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
    m_CheckBoxPreserveOrder = new JCheckBox();
    m_CheckBoxPreserveOrder.setSelected(props.getBoolean("Classify.PreserveOrder", false));
    m_CheckBoxPreserveOrder.setToolTipText("No randomization is performed if checked");
    m_CheckBoxPreserveOrder.addActionListener((ActionEvent e) -> update());
    m_PanelParameters.addParameter("Preserve order", m_CheckBoxPreserveOrder);

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

    // use views?
    m_CheckBoxUseViews = new JCheckBox();
    m_CheckBoxUseViews.setSelected(props.getBoolean("Classify.UseViews", false));
    m_CheckBoxUseViews.setToolTipText("Save memory by using views instead of creating copies of datasets?");
    m_CheckBoxUseViews.addActionListener((ActionEvent e) -> update());
    m_PanelParameters.addParameter("Use views", m_CheckBoxUseViews);
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

    if (!isValidDataIndex(m_ComboBoxDatasets))
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
    try {
      if (!caps.test(data)) {
        if (caps.getFailReason() != null)
          return caps.getFailReason().getMessage();
        else
          return "Clusterer cannot handle data!";
      }
    }
    catch (Exception e) {
      return "Clusterer cannot handle data: " + e;
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
  protected ResultItem doEvaluate(Clusterer clusterer, AbstractNamedHistoryPanel<ResultItem> history) throws Exception {
    ClusterEvaluation 		eval;
    Clusterer			model;
    double			perc;
    int				seed;
    boolean		  	views;
    DataContainer		dataCont;
    Instances			data;
    Instances			train;
    Instances			test;
    RandomSplitGenerator generator;
    WekaTrainTestSetContainer	cont;
    String			msg;
    MetaData 			runInfo;

    if ((msg = canEvaluate(clusterer)) != null)
      throw new IllegalArgumentException("Cannot evaluate clusterer!\n" + msg);

    dataCont = getOwner().getData().get(m_ComboBoxDatasets.getSelectedIndex());
    data     = dataCont.getData();
    perc     = m_TextPercentage.getValue().doubleValue() / 100.0;
    seed     = m_TextSeed.getValue().intValue();
    views    = m_CheckBoxUseViews.isSelected();
    if (m_CheckBoxPreserveOrder.isSelected())
      generator = new RandomSplitGenerator(data, perc);
    else
      generator = new RandomSplitGenerator(data, seed, perc);
    generator.setUseViews(views);
    cont    = generator.next();
    train   = (Instances) cont.getValue(WekaTrainTestSetContainer.VALUE_TRAIN);
    test    = (Instances) cont.getValue(WekaTrainTestSetContainer.VALUE_TEST);
    runInfo = new MetaData();
    runInfo.add("Clusterer", OptionUtils.getCommandLine(clusterer));
    runInfo.add("Seed", seed);
    runInfo.add("Split percentage", perc);
    runInfo.add("Order preserved", m_CheckBoxPreserveOrder.isSelected());
    runInfo.add("Dataset ID", dataCont.getID());
    runInfo.add("Relation", data.relationName());
    runInfo.add("# Attributes", data.numAttributes());
    runInfo.add("# Instances (train)", train.numInstances());
    runInfo.add("# Instances (test)", test.numInstances());
    runInfo.add("Use views", views);

    model = (Clusterer) OptionUtils.shallowCopy(clusterer);
    getOwner().logMessage("Using " + m_TextPercentage.getText() + "% of '" + train.relationName() + "' to train " + OptionUtils.getCommandLine(clusterer));
    model.buildClusterer(train);
    addObjectSize(runInfo, "Model size", model);
    getOwner().logMessage("Using remainder from '" + test.relationName() + "' to evaluate " + OptionUtils.getCommandLine(clusterer));
    eval = new ClusterEvaluation();
    eval.setClusterer(model);
    eval.evaluateClusterer(test);

    // history
    return addToHistory(history, new ResultItem(eval, clusterer, model, new Instances(train, 0), runInfo));
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
   * Returns the objects for serialization.
   *
   * @return		the mapping of the objects to serialize
   */
  public Map<String,Object> serialize() {
    Map<String,Object>	result;

    result = super.serialize();
    result.put(KEY_DATASET, m_ComboBoxDatasets.getSelectedIndex());
    result.put(KEY_PERCENTAGE, m_TextPercentage.getValue().doubleValue());
    result.put(KEY_SEED, m_TextSeed.getValue().intValue());
    result.put(KEY_PRESERVEORDER, m_CheckBoxPreserveOrder.isSelected());
    result.put(KEY_USEVIEWS, m_CheckBoxUseViews.isSelected());

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
    if (data.containsKey(KEY_PERCENTAGE))
      m_TextPercentage.setValue((double) data.get(KEY_PERCENTAGE));
    if (data.containsKey(KEY_SEED))
      m_TextSeed.setValue((int) data.get(KEY_SEED));
    if (data.containsKey(KEY_PRESERVEORDER))
      m_CheckBoxPreserveOrder.setSelected((Boolean) data.get(KEY_PRESERVEORDER));
    if (data.containsKey(KEY_USEVIEWS))
      m_CheckBoxUseViews.setSelected((Boolean) data.get(KEY_USEVIEWS));
  }
}
