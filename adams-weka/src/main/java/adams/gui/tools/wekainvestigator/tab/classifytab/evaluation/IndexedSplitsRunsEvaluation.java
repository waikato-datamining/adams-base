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
 * IndexedSplitsRunsEvaluation.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.classifytab.evaluation;

import adams.core.MessageCollection;
import adams.core.ObjectCopyHelper;
import adams.core.Properties;
import adams.core.io.PlaceholderFile;
import adams.core.option.OptionUtils;
import adams.data.indexedsplits.IndexedSplitsRuns;
import adams.data.indexedsplits.InstancesIndexedSplitsRunsCompatibility;
import adams.data.io.input.AbstractIndexedSplitsRunsReader;
import adams.data.io.input.JsonIndexedSplitsRunsReader;
import adams.data.spreadsheet.MetaData;
import adams.flow.container.WekaEvaluationContainer;
import adams.flow.transformer.indexedsplitsrunsevaluation.InstancesIndexedSplitsRunsEvaluation;
import adams.gui.chooser.FileChooserPanel;
import adams.gui.chooser.SelectOptionPanel;
import adams.gui.core.BaseCheckBox;
import adams.gui.core.BaseComboBox;
import adams.gui.core.BaseTextField;
import adams.gui.core.ParameterPanel;
import adams.gui.goe.GenericObjectEditorPanel;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import adams.gui.tools.wekainvestigator.evaluation.DatasetHelper;
import adams.gui.tools.wekainvestigator.tab.AbstractInvestigatorTab.SerializationOption;
import adams.gui.tools.wekainvestigator.tab.classifytab.ResultItem;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Capabilities;
import weka.core.Instances;

import javax.swing.DefaultComboBoxModel;
import javax.swing.event.ChangeEvent;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Performs the evaluation according to the provided indexed splits.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class IndexedSplitsRunsEvaluation
  extends AbstractClassifierEvaluation {

  private static final long serialVersionUID = 1175400993991698944L;

  public static final String KEY_DATASET = "dataset";

  public static final String KEY_FILE = "file";

  public static final String KEY_READER = "reader";

  public static final String KEY_TRAINSPLIT = "train split";

  public static final String KEY_TESTSPLIT = "test split";

  public static final String KEY_LENIENT = "lenient";

  /** the panel with the parameters. */
  protected ParameterPanel m_PanelParameters;

  /** the datasets. */
  protected BaseComboBox<String> m_ComboBoxDatasets;

  /** the chooser panel for the indexed splits file. */
  protected FileChooserPanel m_PanelFile;

  /** the GOE panel for the indexed splits reader. */
  protected GenericObjectEditorPanel m_PanelReader;

  /** the text with the name for the train split. */
  protected BaseTextField m_TextTrainSplitName;

  /** the text with the name for the test split. */
  protected BaseTextField m_TextTestSplitName;

  /** whether to be lenient with checks. */
  protected BaseCheckBox m_CheckBoxLenient;

  /** the additional attributes to store. */
  protected SelectOptionPanel m_SelectAdditionalAttributes;

  /** the datasets model. */
  protected DefaultComboBoxModel<String> m_ModelDatasets;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Performs the evaluation according to the provided indexed splits.";
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    Properties				props;
    AbstractIndexedSplitsRunsReader	reader;

    super.initGUI();

    props = getProperties();

    m_PanelParameters = new ParameterPanel();
    m_PanelOptions.add(m_PanelParameters, BorderLayout.CENTER);

    // dataset
    m_ModelDatasets    = new DefaultComboBoxModel<>();
    m_ComboBoxDatasets = new BaseComboBox<>(m_ModelDatasets);
    m_ComboBoxDatasets.addActionListener((ActionEvent e) -> update());
    m_PanelParameters.addParameter("Dataset", m_ComboBoxDatasets);

    // splits file
    m_PanelFile = new FileChooserPanel();
    m_PanelFile.setCurrentDirectory(new PlaceholderFile(props.getPath("Classify.IndexedSplitsRunsFile", "%c")));
    m_PanelFile.setAcceptAllFileFilterUsed(true);
    m_PanelFile.addChangeListener((ChangeEvent e) -> update());
    m_PanelParameters.addParameter("Splits file", m_PanelFile);

    // reader
    try {
      reader = (AbstractIndexedSplitsRunsReader) OptionUtils.forCommandLine(
        AbstractIndexedSplitsRunsReader.class,
	props.getProperty("Classify.IndexedSplitsRunsReader", new JsonIndexedSplitsRunsReader().toCommandLine()));
    }
    catch (Exception e) {
      reader = new JsonIndexedSplitsRunsReader();
    }
    m_PanelReader = new GenericObjectEditorPanel(AbstractIndexedSplitsRunsReader.class, reader, true);
    m_PanelReader.addChangeListener((ChangeEvent e) -> update());
    m_PanelParameters.addParameter("Reader", m_PanelReader);

    // train split name
    m_TextTrainSplitName = new BaseTextField(props.getProperty("Classify.IndexedSplitsRunsTrainSplitName", "train"));
    m_TextTrainSplitName.setToolTipText("The name of the split to be used for training");
    m_PanelParameters.addParameter("Train split", m_TextTrainSplitName);

    // test split name
    m_TextTestSplitName = new BaseTextField(props.getProperty("Classify.IndexedSplitsRunsTestSplitName", "test"));
    m_TextTestSplitName.setToolTipText("The name of the split to be used for testing");
    m_PanelParameters.addParameter("Test split", m_TextTestSplitName);

    // lenient?
    m_CheckBoxLenient = new BaseCheckBox();
    m_CheckBoxLenient.setSelected(props.getBoolean("Classify.IndexedSplitsRunsLenient", false));
    m_CheckBoxLenient.setToolTipText("Strict or lenient with checks?");
    m_CheckBoxLenient.addActionListener((ActionEvent e) -> update());
    m_PanelParameters.addParameter("Lenient", m_CheckBoxLenient);

    // additional attributes
    m_SelectAdditionalAttributes = new SelectOptionPanel();
    m_SelectAdditionalAttributes.setCurrent(new String[0]);
    m_SelectAdditionalAttributes.setMultiSelect(true);
    m_SelectAdditionalAttributes.setLenient(true);
    m_SelectAdditionalAttributes.setDialogTitle("Select additional attributes");
    m_SelectAdditionalAttributes.setToolTipText("Additional attributes to make available in plots");
    m_PanelParameters.addParameter("Additional attributes", m_SelectAdditionalAttributes);
  }

  /**
   * Returns the name of the evaluation (displayed in combobox).
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "Indexed splits runs";
  }

  /**
   * Tests whether the classifier can be evaluated.
   *
   * @return		null if successful, otherwise error message
   */
  public String canEvaluate(Classifier classifier) {
    Instances		data;
    File		file;
    Capabilities 	caps;

    if (!isValidDataIndex(m_ComboBoxDatasets))
      return "No data available!";

    file = m_PanelFile.getCurrent();
    if (file.isDirectory())
      return "Splits file points to directory: " + file;

    if (m_PanelReader.getCurrent() == null)
      return "No splits reader defined!";

    if (m_TextTrainSplitName.getText().isEmpty())
      return "No train split name provided!";

    if (m_TextTestSplitName.getText().isEmpty())
      return "No test split name provided!";

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
    Classifier					model;
    DataContainer 				dataCont;
    Instances					data;
    boolean 					lenient;
    File					file;
    AbstractIndexedSplitsRunsReader 		reader;
    MessageCollection				errors;
    IndexedSplitsRuns				runs;
    String					trainSplit;
    String					testSplit;
    String					msg;
    MetaData 					runInfo;
    InstancesIndexedSplitsRunsCompatibility	comp;
    InstancesIndexedSplitsRunsEvaluation	eval;
    WekaEvaluationContainer[]			conts;
    int						i;
    ResultItem					nested;

    model = ObjectCopyHelper.copyObject(classifier);

    if ((msg = canEvaluate(model)) != null)
      throw new IllegalArgumentException("Cannot evaluate classifier!\n" + msg);

    dataCont   = getOwner().getData().get(m_ComboBoxDatasets.getSelectedIndex());
    data       = dataCont.getData();
    file       = m_PanelFile.getCurrent();
    reader     = (AbstractIndexedSplitsRunsReader) m_PanelReader.getCurrent();
    trainSplit = m_TextTrainSplitName.getText();
    testSplit  = m_TextTestSplitName.getText();
    lenient    = m_CheckBoxLenient.isSelected();

    errors     = new MessageCollection();
    runs       = reader.read(file, errors);
    if (runs == null) {
      if (errors.isEmpty())
        getOwner().logError("Failed to load indexed splits runs from: " + file, "Failed to load splits");
      else
        getOwner().logError("Failed to load indexed splits runs from: " + file + "\n" + errors, "Failed to load splits");
      return;
    }

    // check compatibility
    comp = new InstancesIndexedSplitsRunsCompatibility();
    comp.setLenient(lenient);
    msg = comp.isCompatible(data, runs);
    if (msg != null) {
      getOwner().logError("Loaded indexed splits runs are not compatible with selected dataset!\n" + msg, "Splits not compatible");
      return;
    }

    getOwner().logMessage("Using '" + dataCont.getID() + "/" + data.relationName() + "' to build " + OptionUtils.getCommandLine(classifier));

    // evaluate
    errors = new MessageCollection();
    eval   = new InstancesIndexedSplitsRunsEvaluation();
    eval.setManualClassifier(classifier);
    eval.setTrainSplitName(trainSplit);
    eval.setTestSplitName(testSplit);
    conts = eval.evaluate(data, runs, errors);
    if (conts == null) {
      if (errors.isEmpty())
        getOwner().logError("Failed to evaluate indexed splits runs!", "Failed to evaluate splits");
      else
        getOwner().logError("Failed to evaluate indexed splits runs:\n" + errors, "Failed to evaluate splits");
      return;
    }

    runInfo  = new MetaData();
    runInfo.add("Classifier", OptionUtils.getCommandLine(classifier));
    runInfo.add("Dataset ID", dataCont.getID());
    runInfo.add("Relation", data.relationName());
    runInfo.add("# Attributes", data.numAttributes());
    runInfo.add("# Instances", data.numInstances());
    runInfo.add("Class attribute", data.classAttribute().name());
    if (conts.length > 1)
      runInfo.add("Run", 1);

    item.update(
      (Evaluation) conts[0].getValue(WekaEvaluationContainer.VALUE_EVALUATION),
      null,
      runInfo,
      null,
      transferAdditionalAttributes(m_SelectAdditionalAttributes, data));

    if (conts.length > 1) {
      item.setNameSuffix("run #1");
      for (i = 1; i < conts.length; i++) {
        runInfo = runInfo.getClone();
	runInfo.add("Run", (i+1));
        nested = new ResultItem(item.getTemplate(), item.getHeader());
        nested.setNameSuffix("run #" + (i+1));
	nested.update(
	  (Evaluation) conts[i].getValue(WekaEvaluationContainer.VALUE_EVALUATION),
	  null,
	  runInfo,
	  null,
	  transferAdditionalAttributes(m_SelectAdditionalAttributes, data));
        item.addNestedItem(item.getName() + "run #" + (i+1), nested);
      }
    }
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
      result.put(KEY_FILE, m_PanelFile.getCurrent().getAbsolutePath());
      result.put(KEY_READER, OptionUtils.getCommandLine(m_PanelReader.getCurrent()));
      result.put(KEY_TRAINSPLIT, m_TextTrainSplitName.getText());
      result.put(KEY_TESTSPLIT, m_TextTestSplitName.getText());
      result.put(KEY_LENIENT, m_CheckBoxLenient.isSelected());
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
    if (data.containsKey(KEY_FILE))
      m_PanelFile.setCurrent(new PlaceholderFile((String) data.get(KEY_FILE)));
    if (data.containsKey(KEY_READER)) {
      try {
	m_PanelReader.setCurrent(OptionUtils.forCommandLine(AbstractIndexedSplitsRunsReader.class, (String) data.get(KEY_READER)));
      }
      catch (Exception e) {
        errors.add("Failed to parse reader commandline: " + data.get(KEY_READER), e);
      }
    }
    if (data.containsKey(KEY_TRAINSPLIT))
      m_TextTrainSplitName.setText((String) data.get(KEY_TRAINSPLIT));
    if (data.containsKey(KEY_TESTSPLIT))
      m_TextTestSplitName.setText((String) data.get(KEY_TESTSPLIT));
    if (data.containsKey(KEY_LENIENT))
      m_CheckBoxLenient.setSelected((boolean) data.get(KEY_LENIENT));
  }
}
