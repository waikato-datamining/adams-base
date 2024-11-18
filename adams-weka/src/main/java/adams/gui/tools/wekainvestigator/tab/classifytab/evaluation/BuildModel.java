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
 * BuildModel.java
 * Copyright (C) 2016-2024 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.classifytab.evaluation;

import adams.core.MessageCollection;
import adams.core.ObjectCopyHelper;
import adams.core.Properties;
import adams.core.SerializationHelper;
import adams.core.Stoppable;
import adams.core.StoppableWithFeedback;
import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.core.option.OptionUtils;
import adams.data.spreadsheet.MetaData;
import adams.gui.chooser.FileChooserPanel;
import adams.gui.core.BaseCheckBox;
import adams.gui.core.BaseComboBox;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.core.NumberTextField;
import adams.gui.core.NumberTextField.Type;
import adams.gui.core.ParameterPanel;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import adams.gui.tools.wekainvestigator.evaluation.DatasetHelper;
import adams.gui.tools.wekainvestigator.tab.AbstractInvestigatorTab.SerializationOption;
import adams.gui.tools.wekainvestigator.tab.classifytab.ResultItem;
import weka.classifiers.Classifier;
import weka.core.Capabilities;
import weka.core.Instances;

import javax.swing.DefaultComboBoxModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Builds a model and serializes it to a file.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class BuildModel
  extends AbstractClassifierEvaluation
  implements StoppableWithFeedback {

  private static final long serialVersionUID = 1175400993991698944L;

  public static final String KEY_DATASET = "dataset";

  public static final String KEY_PRESERVEORDER = "preserveorder";

  public static final String KEY_SEED = "seed";

  public static final String KEY_MODEL = "model";

  /** the panel with the parameters. */
  protected ParameterPanel m_PanelParameters;

  /** the datasets. */
  protected BaseComboBox<String> m_ComboBoxDatasets;

  /** whether to preserve the order. */
  protected BaseCheckBox m_CheckBoxPreserveOrder;

  /** the seed value. */
  protected NumberTextField m_TextSeed;

  /** the datasets model. */
  protected DefaultComboBoxModel<String> m_ModelDatasets;

  /** the serialized model. */
  protected FileChooserPanel m_PanelModel;

  /** the model that is being built. */
  protected transient Classifier m_Model;

  /** whether the build was stopped. */
  protected boolean m_Stopped;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Builds a model on a training set and serializes it to disk.";
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

    // preserve order?
    m_CheckBoxPreserveOrder = new BaseCheckBox();
    m_CheckBoxPreserveOrder.setSelected(props.getBoolean("Classify.BuildModelPreserveOrder", false));
    m_CheckBoxPreserveOrder.setToolTipText("No randomization is performed if checked");
    m_CheckBoxPreserveOrder.addActionListener((ActionEvent e) -> update());
    m_PanelParameters.addParameter("Preserve order", m_CheckBoxPreserveOrder);

    // seed
    m_TextSeed = new NumberTextField(Type.INTEGER, "" + props.getInteger("Classify.BuildModelSeed", 1));
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

    // model file
    m_PanelModel = new FileChooserPanel();
    m_PanelModel.setCurrentDirectory(new PlaceholderFile(props.getPath("Classify.ModelDirectory", "%c")));
    filter = ExtensionFileFilter.getModelFileFilter();
    m_PanelModel.addChoosableFileFilter(filter);
    m_PanelModel.setFileFilter(filter);
    m_PanelModel.setAcceptAllFileFilterUsed(true);
    m_PanelModel.addChangeListener((ChangeEvent e) -> update());
    m_PanelParameters.addParameter("Model", m_PanelModel);
  }

  /**
   * Returns the name of the evaluation (displayed in combobox).
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "Build model";
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

    if (!Utils.isInteger(m_TextSeed.getText()))
      return "Seed value is not an integer!";

    file = m_PanelModel.getCurrent();
    if (file.isDirectory())
      return "Model points to directory: " + file;

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
    DataContainer 	dataCont;
    Instances		data;
    boolean		order;
    int			seed;
    Instances		header;
    String		msg;
    MetaData 		runInfo;

    m_Stopped = false;
    m_Model   = ObjectCopyHelper.copyObject(classifier);

    if ((msg = canEvaluate(m_Model)) != null)
      throw new IllegalArgumentException("Cannot evaluate classifier!\n" + msg);

    dataCont = getOwner().getData().get(m_ComboBoxDatasets.getSelectedIndex());
    data     = dataCont.getData();
    header   = new Instances(data, 0);
    order    = m_CheckBoxPreserveOrder.isSelected();
    seed     = m_TextSeed.getValue().intValue();

    getOwner().logMessage("Using '" + dataCont.getID() + "/" + data.relationName() + "' to build " + OptionUtils.getCommandLine(classifier));
    if (!order)
      data.randomize(new Random(seed));
    m_Model.buildClassifier(data);
    getOwner().logMessage("Built model on '" + dataCont.getID() + "/" + data.relationName() + "' using " + OptionUtils.getCommandLine(classifier));
    SerializationHelper.writeAll(m_PanelModel.getCurrent().getAbsolutePath(), new Object[]{m_Model, header});
    getOwner().logMessage("Saved model built on '" + dataCont.getID() + "/" + data.relationName() + "' to " + m_PanelModel.getCurrent().getAbsolutePath());

    runInfo  = new MetaData();
    runInfo.add("Classifier", OptionUtils.getCommandLine(classifier));
    runInfo.add("Dataset ID", dataCont.getID());
    runInfo.add("Relation", data.relationName());
    runInfo.add("# Attributes", data.numAttributes());
    runInfo.add("# Instances", data.numInstances());
    runInfo.add("Class attribute", data.classAttribute().name());
    runInfo.add("Preserve order", order);
    if (!order)
      runInfo.add("Seed", seed);
    runInfo.add("Model file", m_PanelModel.getCurrent().getAbsolutePath());
    addObjectSize(runInfo, "Model size", m_Model);

    item.update(null, m_Model, runInfo);

    m_Model = null;
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
    if (m_Model instanceof Stoppable)
      ((Stoppable) m_Model).stopExecution();
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
      result.put(KEY_PRESERVEORDER, m_CheckBoxPreserveOrder.isSelected());
      result.put(KEY_SEED, m_TextSeed.getValue().intValue());
      result.put(KEY_MODEL, m_PanelModel.getCurrent().getAbsolutePath());
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
    if (data.containsKey(KEY_PRESERVEORDER))
      m_CheckBoxPreserveOrder.setSelected((boolean) data.get(KEY_PRESERVEORDER));
    if (data.containsKey(KEY_SEED))
      m_TextSeed.setValue(((Number) data.get(KEY_SEED)).intValue());
    if (data.containsKey(KEY_MODEL))
      m_PanelModel.setCurrent(new PlaceholderFile((String) data.get(KEY_MODEL)));
  }
}
