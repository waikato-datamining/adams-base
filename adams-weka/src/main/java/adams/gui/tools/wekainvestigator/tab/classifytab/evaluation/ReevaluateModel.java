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
 * ReevaluateModel.java
 * Copyright (C) 2016-2024 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.classifytab.evaluation;

import adams.core.MessageCollection;
import adams.core.ObjectCopyHelper;
import adams.core.Properties;
import adams.core.SerializationHelper;
import adams.core.StoppableWithFeedback;
import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.core.option.OptionUtils;
import adams.data.spreadsheet.MetaData;
import adams.gui.chooser.FileChooserPanel;
import adams.gui.chooser.SelectOptionPanel;
import adams.gui.core.BaseCheckBox;
import adams.gui.core.BaseComboBox;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.core.ParameterPanel;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import adams.gui.tools.wekainvestigator.evaluation.DatasetHelper;
import adams.gui.tools.wekainvestigator.tab.AbstractInvestigatorTab.SerializationOption;
import adams.gui.tools.wekainvestigator.tab.classifytab.ResultItem;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import weka.classifiers.Classifier;
import weka.classifiers.StoppableEvaluation;
import weka.classifiers.TestingHelper;
import weka.classifiers.TestingHelper.TestingUpdateListener;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
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
 * Re-evaluates a serialized model.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ReevaluateModel
  extends AbstractClassifierEvaluation
  implements StoppableWithFeedback {

  private static final long serialVersionUID = 1175400993991698944L;

  public static final String KEY_DATASET = "dataset";

  public static final String KEY_MODEL = "model";

  public static final String KEY_ADDITIONAL = "additional";

  public static final String KEY_DISCARDPREDICTIONS = "discardpredictions";

  /** the panel with the parameters. */
  protected ParameterPanel m_PanelParameters;

  /** the datasets. */
  protected BaseComboBox<String> m_ComboBoxDatasets;

  /** the datasets model. */
  protected DefaultComboBoxModel<String> m_ModelDatasets;

  /** the additional attributes to store. */
  protected SelectOptionPanel m_SelectAdditionalAttributes;

  /** whether to discard the predictions. */
  protected BaseCheckBox m_CheckBoxDiscardPredictions;

  /** the serialized model. */
  protected FileChooserPanel m_PanelModel;

  /** the current model. */
  protected Classifier m_Model;

  /** the training header (if any). */
  protected Instances m_Header;

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
    return "Re-evaluates a serialized model on a test set.";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Model  = null;
    m_Header = null;
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    ExtensionFileFilter filter;
    Properties		props;

    super.initGUI();

    props = getProperties();

    m_PanelParameters = new ParameterPanel();
    m_PanelOptions.add(m_PanelParameters, BorderLayout.CENTER);

    // dataset
    m_ModelDatasets    = new DefaultComboBoxModel<>();
    m_ComboBoxDatasets = new BaseComboBox<>(m_ModelDatasets);
    m_ComboBoxDatasets.addActionListener((ActionEvent e) -> update());
    m_PanelParameters.addParameter("Dataset", m_ComboBoxDatasets);

    // model file
    m_PanelModel = new FileChooserPanel();
    filter = ExtensionFileFilter.getModelFileFilter();
    m_PanelModel.addChoosableFileFilter(filter);
    m_PanelModel.setFileFilter(filter);
    m_PanelModel.setAcceptAllFileFilterUsed(true);
    m_PanelModel.addChangeListener((ChangeEvent e) -> {
      m_Model = null;
      getOwner().updateButtons();
    });
    m_PanelParameters.addParameter("Model", m_PanelModel);

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
    return "Re-evaluate model";
  }

  /**
   * Attempts to load the model and (if available) the header.
   *
   * @return		null if successfully loaded, otherwise error message
   */
  protected String loadModel() {
    File	file;
    Object[]	obj;

    m_Model  = null;
    m_Header = null;

    file = m_PanelModel.getCurrent();
    if (file.isDirectory())
      return "Model points to a directory: " + file;
    if (!file.exists())
      return "Model does not exist: " + file;

    if (!SerializationHelper.isSerializedObject(file.getAbsolutePath()))
      return "Model file is not a serialized object: " + file;

    try {
      obj = SerializationHelper.readAll(file.getAbsolutePath());
      if (obj.length > 0)
	m_Model = (Classifier) obj[0];
      if (obj.length > 1)
	m_Header = (Instances) obj[1];
    }
    catch (Exception e) {
      return "Failed to load model: " + file + "\n" + e;
    }

    return null;
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
    String		msg;

    if (!isValidDataIndex(m_ComboBoxDatasets))
      return "No data available!";

    file = m_PanelModel.getCurrent();
    if (file.isDirectory())
      return "Model points to directory: " + file;
    if (!file.exists())
      return "Model does not exist: " + file;

    if (m_Model == null) {
      msg = loadModel();
      if (msg != null)
	return msg;
    }
    if (m_Model == null)
      return "Failed to load model: " + file;

    data = getOwner().getData().get(m_ComboBoxDatasets.getSelectedIndex()).getData();
    if (m_Header != null) {
      if (!data.equalHeaders(m_Header))
	return data.equalHeadersMsg(m_Header);
    }

    caps = m_Model.getCapabilities();
    // necessary when just wanting to make predictions
    caps.enable(Capability.MISSING_CLASS_VALUES);
    caps.setMinimumNumberInstances(0);
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
    DataContainer 	dataCont;
    Instances		data;
    boolean		discard;
    String		msg;
    MetaData 		runInfo;
    TIntList 		original;
    int			i;

    m_Stopped  = false;
    classifier = ObjectCopyHelper.copyObject(m_Model);

    if ((msg = canEvaluate(classifier)) != null)
      throw new IllegalArgumentException("Cannot evaluate classifier!\n" + msg);

    item.setTemplate(classifier);
    dataCont = getOwner().getData().get(m_ComboBoxDatasets.getSelectedIndex());
    data     = dataCont.getData();
    discard  = m_CheckBoxDiscardPredictions.isSelected();
    runInfo  = new MetaData();
    runInfo.add("Classifier", OptionUtils.getCommandLine(m_Model));
    runInfo.add("Dataset ID", dataCont.getID());
    runInfo.add("Relation", data.relationName());
    runInfo.add("# Attributes", data.numAttributes());
    runInfo.add("# Instances", data.numInstances());
    runInfo.add("Class attribute", data.classAttribute().name());
    runInfo.add("Discard predictions", discard);
    if (m_SelectAdditionalAttributes.getCurrent().length > 0)
      runInfo.add("Additional attributes: ", Utils.flatten(m_SelectAdditionalAttributes.getCurrent(), ", "));
    addObjectSize(runInfo, "Model size", m_Model);

    m_Evaluation = new StoppableEvaluation(data);
    m_Evaluation.setDiscardPredictions(discard);
    TestingHelper.evaluateModel(m_Model, data, m_Evaluation, getTestingUpdateInterval(), new TestingUpdateListener() {
      @Override
      public void testingUpdateRequested(Instances data, int numTested, int numTotal) {
        getOwner().logMessage("Used " + numTested + "/" + numTotal + " of '" + dataCont.getID() + "/" + data.relationName() + "' to evaluate " + OptionUtils.getCommandLine(m_Model));
      }
    });

    original = new TIntArrayList();
    for (i = 0; i < data.numInstances(); i++)
      original.add(i);

    item.update(
      m_Evaluation, m_Model, runInfo,
      original.toArray(), transferAdditionalAttributes(m_SelectAdditionalAttributes, data));

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
    if (m_Evaluation != null)
      m_Evaluation.stopExecution();
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
      result.put(KEY_MODEL, m_PanelModel.getCurrent().getAbsolutePath());
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
    if (data.containsKey(KEY_DATASET))
      m_ComboBoxDatasets.setSelectedIndex(((Number) data.get(KEY_DATASET)).intValue());
    if (data.containsKey(KEY_MODEL))
      m_PanelModel.setCurrent(new PlaceholderFile((String) data.get(KEY_MODEL)));
    if (data.containsKey(KEY_ADDITIONAL))
      m_SelectAdditionalAttributes.setCurrent(listOrArray(data.get(KEY_ADDITIONAL)));
    if (data.containsKey(KEY_DISCARDPREDICTIONS))
      m_CheckBoxDiscardPredictions.setSelected((Boolean) data.get(KEY_DISCARDPREDICTIONS));
  }
}
