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

import adams.core.Performance;
import adams.core.Properties;
import adams.core.Utils;
import adams.core.option.OptionUtils;
import adams.data.spreadsheet.MetaData;
import adams.gui.core.AbstractNamedHistoryPanel;
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
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Performs cross-validation.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CrossValidation
  extends AbstractClassifierEvaluation {

  private static final long serialVersionUID = 1175400993991698944L;

  /** the panel with the parameters. */
  protected ParameterPanel m_PanelParameters;

  /** the datasets. */
  protected JComboBox<String> m_ComboBoxDatasets;

  /** the datasets model. */
  protected DefaultComboBoxModel<String> m_ModelDatasets;

  /** the number of folds. */
  protected JSpinner m_SpinnerFolds;

  /** the seed value. */
  protected JTextField m_TextSeed;

  /** the number of threads. */
  protected JSpinner m_SpinnerThreads;

  /** whether to discard the predictions. */
  protected JCheckBox m_CheckBoxDiscardPredictions;

  /** whether to produce a final model. */
  protected JCheckBox m_CheckBoxFinalModel;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Performs cross-validation.";
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

    // threads
    m_SpinnerThreads = new JSpinner();
    ((SpinnerNumberModel) m_SpinnerThreads.getModel()).setStepSize(1);
    m_SpinnerThreads.setValue(props.getInteger("Classify.NumThreads", -1));
    m_SpinnerThreads.setToolTipText(Performance.getNumThreadsHelp());
    m_SpinnerThreads.addChangeListener((ChangeEvent e) -> update());
    m_PanelParameters.addParameter("Threads", m_SpinnerThreads);

    // discard predictions?
    m_CheckBoxDiscardPredictions = new JCheckBox();
    m_CheckBoxDiscardPredictions.setSelected(props.getBoolean("Classify.DiscardPredictions", false));
    m_CheckBoxDiscardPredictions.setToolTipText("Save memory by discarding predictions?");
    m_CheckBoxDiscardPredictions.addActionListener((ActionEvent e) -> update());
    m_PanelParameters.addParameter("Discard predictions", m_CheckBoxDiscardPredictions);

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

    if (m_ComboBoxDatasets.getSelectedIndex() == -1)
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
    WekaCrossValidationExecution	crossValidation;
    String				msg;
    Instances				data;
    boolean				finalModel;
    boolean				discard;
    Classifier				model;
    int					seed;
    int					folds;
    int					threads;
    MetaData 				runInfo;

    if ((msg = canEvaluate(classifier)) != null)
      throw new IllegalArgumentException("Cannot evaluate classifier!\n" + msg);

    data       = getOwner().getData().get(m_ComboBoxDatasets.getSelectedIndex()).getData();
    finalModel = m_CheckBoxFinalModel.isSelected();
    discard    = m_CheckBoxDiscardPredictions.isSelected();
    seed       = Integer.parseInt(m_TextSeed.getText());
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
    runInfo.add("Discard predictions", discard);
    crossValidation = new WekaCrossValidationExecution();
    crossValidation.setClassifier(classifier);
    crossValidation.setData(data);
    crossValidation.setFolds(folds);
    crossValidation.setSeed(seed);
    crossValidation.setNumThreads(threads);
    crossValidation.setDiscardPredictions(discard);
    crossValidation.setStatusMessageHandler(this);
    msg = crossValidation.execute();
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
    return addToHistory(history, new ResultItem(crossValidation.getEvaluation(), classifier, model, new Instances(data, 0), runInfo));
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
