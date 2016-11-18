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

package adams.gui.tools.wekainvestigator.tab.attseltab.evaluation;

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
import adams.gui.tools.wekainvestigator.data.DataContainer;
import adams.gui.tools.wekainvestigator.evaluation.DatasetHelper;
import adams.gui.tools.wekainvestigator.tab.attseltab.ResultItem;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import weka.attributeSelection.AttributeSelection;
import weka.classifiers.CrossValidationFoldGenerator;
import weka.core.Capabilities;
import weka.core.Instances;

import javax.swing.DefaultComboBoxModel;
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
  extends AbstractAttributeSelectionEvaluation {

  private static final long serialVersionUID = 1175400993991698944L;

  public static final String KEY_DATASET = "dataset";

  public static final String KEY_FOLDS = "folds";

  public static final String KEY_SEED = "seed";

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

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Cross-validates the attribute selection on the selected dataset.";
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
    ((SpinnerNumberModel) m_SpinnerFolds.getModel()).setMinimum(2);
    ((SpinnerNumberModel) m_SpinnerFolds.getModel()).setStepSize(1);
    m_SpinnerFolds.setValue(props.getInteger("AttributeSelection.NumFolds", 10));
    m_SpinnerFolds.setToolTipText("The number of folds to use (>= 2)");
    m_SpinnerFolds.addChangeListener((ChangeEvent e) -> update());
    m_PanelParameters.addParameter("Folds", m_SpinnerFolds);

    // seed
    m_TextSeed = new NumberTextField(Type.INTEGER, "" + props.getInteger("AttributeSelection.Seed", 1));
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
    return "Cross-validation";
  }

  /**
   * Tests whether attribute selection can be performed.
   *
   * @return		null if successful, otherwise error message
   */
  public String canEvaluate(ASEvaluation evaluator, ASSearch search) {
    Instances		data;
    Capabilities 	caps;

    if (!isValidDataIndex(m_ComboBoxDatasets))
      return "No data available!";

    if (!Utils.isInteger(m_TextSeed.getText()))
      return "Seed value is not an integer!";

    data = getOwner().getData().get(m_ComboBoxDatasets.getSelectedIndex()).getData();
    caps = evaluator.getCapabilities();
    try {
      if (!caps.test(data)) {
        if (caps.getFailReason() != null)
          return caps.getFailReason().getMessage();
        else
          return "Evaluator cannot handle data!";
      }
    }
    catch (Exception e) {
      return "Evaluator cannot handle data: " + e;
    }

    return null;
  }

  /**
   * Performs attribute selection and returns the generated evaluation object.
   *
   * @param history	the history to add the result to
   * @return		the generate history item
   * @throws Exception	if evaluation fails
   */
  @Override
  protected ResultItem doEvaluate(ASEvaluation evaluator, ASSearch search, AbstractNamedHistoryPanel<ResultItem> history) throws Exception {
    String				msg;
    DataContainer			dataCont;
    Instances				data;
    int					seed;
    int					folds;
    ASEvaluation			eval;
    ASSearch				srch;
    AttributeSelection			attsel;
    CrossValidationFoldGenerator	generator;
    WekaTrainTestSetContainer 		cont;
    int					current;
    Instances				train;
    MetaData 				runInfo;

    if ((msg = canEvaluate(evaluator, search)) != null)
      throw new IllegalArgumentException("Cannot perform attribute selection!\n" + msg);

    dataCont = getOwner().getData().get(m_ComboBoxDatasets.getSelectedIndex());
    data     = dataCont.getData();
    seed     = m_TextSeed.getValue().intValue();
    folds    = ((Number) m_SpinnerFolds.getValue()).intValue();
    eval     = (ASEvaluation) OptionUtils.shallowCopy(evaluator);
    srch     = (ASSearch) OptionUtils.shallowCopy(search);
    runInfo    = new MetaData();
    runInfo.add("Evaluator", OptionUtils.getCommandLine(evaluator));
    runInfo.add("Search", OptionUtils.getCommandLine(search));
    runInfo.add("Seed", seed);
    runInfo.add("Folds", folds);
    runInfo.add("Dataset ID", dataCont.getID());
    runInfo.add("Relation", data.relationName());
    runInfo.add("# Attributes", data.numAttributes());
    runInfo.add("# Instances", data.numInstances());
    if (data.classIndex() > -1)
      runInfo.add("Class attribute", data.classAttribute().name());

    attsel = new AttributeSelection();
    attsel.setSearch(srch);
    attsel.setEvaluator(eval);
    attsel.setSeed(seed);
    attsel.setFolds(folds);

    generator  = new CrossValidationFoldGenerator(data, folds, seed, true);
    current    = 0;
    while (generator.hasNext()) {
      current++;
      getOwner().logMessage("Fold " + current + "/" + folds + ": '" + data.relationName() + "' using " + OptionUtils.getCommandLine(eval) + " and " + OptionUtils.getCommandLine(srch));
      cont  = generator.next();
      train = (Instances) cont.getValue(WekaTrainTestSetContainer.VALUE_TRAIN);
      attsel.selectAttributesCVSplit(train);
    }

    // history
    return addToHistory(history, new ResultItem(attsel, eval, srch, folds, new Instances(data, 0), runInfo));
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
    result.put(KEY_FOLDS, m_SpinnerFolds.getValue());
    result.put(KEY_SEED, m_TextSeed.getValue().intValue());

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
  }
}
