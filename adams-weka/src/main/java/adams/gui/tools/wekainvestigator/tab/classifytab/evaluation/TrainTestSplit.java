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
 * TrainTestSplit.java
 * Copyright (C) 2016-2025 University of Waikato, Hamilton, NZ
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
import adams.flow.container.WekaTrainTestSetContainer;
import adams.gui.chooser.SelectOptionPanel;
import adams.gui.core.BaseCheckBox;
import adams.gui.core.BaseComboBox;
import adams.gui.core.NumberTextField;
import adams.gui.core.NumberTextField.Type;
import adams.gui.core.ParameterPanel;
import adams.gui.goe.GenericObjectEditorPanel;
import adams.gui.tools.wekainvestigator.InvestigatorPanel;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import adams.gui.tools.wekainvestigator.evaluation.DatasetHelper;
import adams.gui.tools.wekainvestigator.tab.AbstractInvestigatorTab.SerializationOption;
import adams.gui.tools.wekainvestigator.tab.classifytab.ResultItem;
import weka.classifiers.Classifier;
import weka.classifiers.DefaultRandomSplitGenerator;
import weka.classifiers.RandomSplitGenerator;
import weka.classifiers.StoppableEvaluation;
import weka.classifiers.TestingHelper;
import weka.classifiers.TestingHelper.TestingUpdateListener;
import weka.core.Capabilities;
import weka.core.Instances;

import javax.swing.DefaultComboBoxModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Uses a (random) percentage split to generate train/test.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class TrainTestSplit
  extends AbstractClassifierEvaluation
  implements StoppableWithFeedback {

  private static final long serialVersionUID = -4460266467650893551L;

  public static final String KEY_DATASET = "dataset";

  public static final String KEY_PERCENTAGE = "percentage";

  public static final String KEY_SEED = "seed";

  public static final String KEY_PRESERVEORDER = "preserveorder";

  public static final String KEY_ADDITIONAL = "additional";

  public static final String KEY_USEVIEWS = "useviews";

  public static final String KEY_GENERATOR = "generator";

  public static final String KEY_DISCARDPREDICTIONS = "discardpredictions";

  /** the panel with the parameters. */
  protected ParameterPanel m_PanelParameters;

  /** the datasets. */
  protected BaseComboBox<String> m_ComboBoxDatasets;

  /** the datasets model. */
  protected DefaultComboBoxModel<String> m_ModelDatasets;

  /** the split percentage. */
  protected NumberTextField m_TextPercentage;

  /** whether to preserve the order. */
  protected BaseCheckBox m_CheckBoxPreserveOrder;

  /** the seed value. */
  protected NumberTextField m_TextSeed;

  /** the additional attributes to store. */
  protected SelectOptionPanel m_SelectAdditionalAttributes;

  /** whether to use views. */
  protected BaseCheckBox m_CheckBoxUseViews;

  /** the split generator. */
  protected GenericObjectEditorPanel m_GOEGenerator;

  /** whether to discard the predictions. */
  protected BaseCheckBox m_CheckBoxDiscardPredictions;

  /** the current model. */
  protected transient Classifier m_Model;

  /** the current evaluation. */
  protected transient StoppableEvaluation m_Evaluation;

  /** whether the build was stopped. */
  protected boolean m_Stopped;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return
      "Generates train/test sets from the selected dataset using the specified "
        + "split percentage and builds/evaluates the classifier accordingly.";
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    Properties			props;
    RandomSplitGenerator	generator;

    super.initGUI();

    props = InvestigatorPanel.getProperties();

    m_PanelParameters = new ParameterPanel();
    m_PanelOptions.add(m_PanelParameters, BorderLayout.CENTER);

    // dataset
    m_ModelDatasets    = new DefaultComboBoxModel<>();
    m_ComboBoxDatasets = new BaseComboBox<>(m_ModelDatasets);
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
    m_CheckBoxPreserveOrder = new BaseCheckBox();
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

    // additional attributes
    m_SelectAdditionalAttributes = new SelectOptionPanel();
    m_SelectAdditionalAttributes.setCurrent(new String[0]);
    m_SelectAdditionalAttributes.setMultiSelect(true);
    m_SelectAdditionalAttributes.setLenient(true);
    m_SelectAdditionalAttributes.setDialogTitle("Select additional attributes");
    m_SelectAdditionalAttributes.setToolTipText("Additional attributes to make available in plots");
    m_PanelParameters.addParameter("Additional attributes", m_SelectAdditionalAttributes);

    // generator
    try {
      generator = (RandomSplitGenerator) OptionUtils.forCommandLine(
        RandomSplitGenerator.class,
	props.getProperty("Classify.TrainTestSplitGenerator",
	  new DefaultRandomSplitGenerator().toCommandLine()));
    }
    catch (Exception e) {
      generator = new DefaultRandomSplitGenerator();
    }
    m_GOEGenerator = new GenericObjectEditorPanel(RandomSplitGenerator.class, generator, true);
    m_GOEGenerator.addChangeListener((ChangeEvent e) -> update());
    m_PanelParameters.addParameter("Generator", m_GOEGenerator);

    // use views?
    m_CheckBoxUseViews = new BaseCheckBox();
    m_CheckBoxUseViews.setSelected(props.getBoolean("Classify.UseViews", false));
    m_CheckBoxUseViews.setToolTipText("Save memory by using views instead of creating copies of datasets?");
    m_CheckBoxUseViews.addActionListener((ActionEvent e) -> update());
    m_PanelParameters.addParameter("Use views", m_CheckBoxUseViews);

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
    return "Train/test split";
  }

  /**
   * Tests whether the classifier can be evaluated.
   *
   * @return		null if successful, otherwise error message
   */
  public String canEvaluate(Classifier classifier) {
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
    double			perc;
    int				seed;
    boolean		  	views;
    boolean			discard;
    DataContainer 		dataCont;
    Instances			data;
    Instances			train;
    Instances			test;
    RandomSplitGenerator 	generator;
    WekaTrainTestSetContainer	cont;
    String			msg;
    MetaData 			runInfo;

    if ((msg = canEvaluate(classifier)) != null)
      throw new IllegalArgumentException("Cannot evaluate classifier!\n" + msg);

    m_Stopped = false;
    dataCont = getOwner().getData().get(m_ComboBoxDatasets.getSelectedIndex());
    data     = dataCont.getData();
    perc     = m_TextPercentage.getValue().doubleValue() / 100.0;
    seed     = m_TextSeed.getValue().intValue();
    views    = m_CheckBoxUseViews.isSelected();
    discard  = m_CheckBoxDiscardPredictions.isSelected();
    generator = (RandomSplitGenerator) m_GOEGenerator.getCurrent();
    generator.setData(data);
    generator.setSeed(seed);
    generator.setPercentage(perc);
    generator.setPreserveOrder(m_CheckBoxPreserveOrder.isSelected());
    generator.setUseViews(views);
    cont      = generator.next();
    train     = (Instances) cont.getValue(WekaTrainTestSetContainer.VALUE_TRAIN);
    test      = (Instances) cont.getValue(WekaTrainTestSetContainer.VALUE_TEST);
    runInfo   = new MetaData();
    runInfo.add("Classifier", OptionUtils.getCommandLine(classifier));
    runInfo.add("Seed", seed);
    runInfo.add("Split percentage", perc);
    runInfo.add("Order preserved", m_CheckBoxPreserveOrder.isSelected());
    runInfo.add("Dataset ID", dataCont.getID());
    runInfo.add("Relation", data.relationName());
    runInfo.add("# Attributes", data.numAttributes());
    runInfo.add("# Instances (train)", train.numInstances());
    runInfo.add("# Instances (test)", test.numInstances());
    runInfo.add("Class attribute", data.classAttribute().name());
    runInfo.add("Discard predictions", discard);
    runInfo.add("Use views", views);
    runInfo.add("Generator", generator.toCommandLine());
    if (m_SelectAdditionalAttributes.getCurrent().length > 0)
      runInfo.add("Additional attributes: ", Utils.flatten(m_SelectAdditionalAttributes.getCurrent(), ", "));

    m_Model = ObjectCopyHelper.copyObject(classifier);
    getOwner().logMessage("Using " + m_TextPercentage.getText() + "% of '" + dataCont.getID() + "/" + train.relationName() + "' to train " + OptionUtils.getCommandLine(classifier));
    m_Model.buildClassifier(train);
    addObjectSize(runInfo, "Model size", m_Model);
    getOwner().logMessage("Using remainder from '" + dataCont.getID() + "/" + test.relationName() + "' to evaluate " + OptionUtils.getCommandLine(classifier));
    m_Evaluation = new StoppableEvaluation(train);
    m_Evaluation.setDiscardPredictions(discard);
    TestingHelper.evaluateModel(m_Model, test, m_Evaluation, getTestingUpdateInterval(), new TestingUpdateListener() {
      @Override
      public void testingUpdateRequested(Instances data, int numTested, int numTotal) {
        getOwner().logMessage("Used " + numTested + "/" + numTotal + " of '" + test.relationName() + "' to evaluate " + OptionUtils.getCommandLine(classifier));
      }
    });

    item.update(m_Evaluation)
      .update(m_Model)
      .update(runInfo)
      .update(transferAdditionalAttributes(m_SelectAdditionalAttributes, test));

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
    index    = DatasetHelper.indexOfDataset(getOwner().getData(), m_ComboBoxDatasets.getSelectedItem());
    if (DatasetHelper.hasDataChanged(datasets, m_ModelDatasets)) {
      m_ModelDatasets = new DefaultComboBoxModel<>(datasets.toArray(new String[0]));
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
    if (options.contains(SerializationOption.GUI))
      result.put(KEY_DATASET, m_ComboBoxDatasets.getSelectedIndex());
    if (options.contains(SerializationOption.PARAMETERS)) {
      result.put(KEY_PERCENTAGE, m_TextPercentage.getValue().doubleValue());
      result.put(KEY_SEED, m_TextSeed.getValue().intValue());
      result.put(KEY_PRESERVEORDER, m_CheckBoxPreserveOrder.isSelected());
      result.put(KEY_ADDITIONAL, m_SelectAdditionalAttributes.getCurrent());
      result.put(KEY_USEVIEWS, m_CheckBoxUseViews.isSelected());
      result.put(KEY_GENERATOR, OptionUtils.getCommandLine(m_GOEGenerator.getCurrent()));
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
    if (data.containsKey(KEY_DATASET))
      m_ComboBoxDatasets.setSelectedIndex(((Number) data.get(KEY_DATASET)).intValue());
    if (data.containsKey(KEY_PERCENTAGE))
      m_TextPercentage.setValue(((Number) data.get(KEY_PERCENTAGE)).doubleValue());
    if (data.containsKey(KEY_SEED))
      m_TextSeed.setValue(((Number) data.get(KEY_SEED)).intValue());
    if (data.containsKey(KEY_PRESERVEORDER))
      m_CheckBoxPreserveOrder.setSelected((boolean) data.get(KEY_PRESERVEORDER));
    if (data.containsKey(KEY_ADDITIONAL))
      m_SelectAdditionalAttributes.setCurrent(listOrArray(data.get(KEY_ADDITIONAL)));
    if (data.containsKey(KEY_USEVIEWS))
      m_CheckBoxUseViews.setSelected((Boolean) data.get(KEY_USEVIEWS));
    if (data.containsKey(KEY_GENERATOR)) {
      try {
	m_GOEGenerator.setCurrent(OptionUtils.forCommandLine(RandomSplitGenerator.class, (String) data.get(KEY_GENERATOR)));
      }
      catch (Exception e) {
        errors.add("Failed to parse generator commandline: " + data.get(KEY_GENERATOR), e);
      }
    }
    if (data.containsKey(KEY_DISCARDPREDICTIONS))
      m_CheckBoxDiscardPredictions.setSelected((Boolean) data.get(KEY_DISCARDPREDICTIONS));
  }
}
