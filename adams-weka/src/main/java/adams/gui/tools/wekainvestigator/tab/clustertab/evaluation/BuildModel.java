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
 * BuildModel.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.clustertab.evaluation;

import adams.core.MessageCollection;
import adams.core.Properties;
import adams.core.SerializationHelper;
import adams.core.io.PlaceholderFile;
import adams.core.option.OptionUtils;
import adams.data.spreadsheet.MetaData;
import adams.gui.chooser.FileChooserPanel;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.core.ParameterPanel;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import adams.gui.tools.wekainvestigator.evaluation.DatasetHelper;
import adams.gui.tools.wekainvestigator.tab.clustertab.ResultItem;
import weka.clusterers.Clusterer;
import weka.core.Capabilities;
import weka.core.Instances;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.event.ChangeEvent;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Builds a model and serializes it to a file.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BuildModel
  extends AbstractClustererEvaluation {

  private static final long serialVersionUID = 1175400993991698944L;

  public static final String KEY_DATASET = "dataset";

  public static final String KEY_MODEL = "model";

  /** the panel with the parameters. */
  protected ParameterPanel m_PanelParameters;

  /** the datasets. */
  protected JComboBox<String> m_ComboBoxDatasets;

  /** the datasets model. */
  protected DefaultComboBoxModel<String> m_ModelDatasets;

  /** the serialized model. */
  protected FileChooserPanel m_PanelModel;

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
    m_ComboBoxDatasets = new JComboBox<>(m_ModelDatasets);
    m_ComboBoxDatasets.addActionListener((ActionEvent e) -> update());
    m_PanelParameters.addParameter("Dataset", m_ComboBoxDatasets);

    // model file
    m_PanelModel = new FileChooserPanel();
    m_PanelModel.setCurrentDirectory(new PlaceholderFile(props.getPath("Cluster.ModelDirectory", "%c")));
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
   * Tests whether the clusterer can be evaluated.
   *
   * @return		null if successful, otherwise error message
   */
  public String canEvaluate(Clusterer clusterer) {
    Instances		data;
    File		file;
    Capabilities 	caps;

    if (!isValidDataIndex(m_ComboBoxDatasets))
      return "No data available!";

    file = m_PanelModel.getCurrent();
    if (file.isDirectory())
      return "Model points to directory: " + file;

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
   * Initializes the result item.
   *
   * @param clusterer	the current clusterer
   * @return		the initialized history item
   * @throws Exception	if initialization fails
   */
  public ResultItem init(Clusterer clusterer) throws Exception {
    ResultItem 		result;
    Instances		data;

    data = getOwner().getData().get(m_ComboBoxDatasets.getSelectedIndex()).getData();
    result = new ResultItem(clusterer, new Instances(data, 0));

    return result;
  }

  /**
   * Evaluates the clusterer and updates the result item.
   *
   * @param clusterer	the current clusterer
   * @param item	the item to update
   * @throws Exception	if evaluation fails
   */
  @Override
  protected void doEvaluate(Clusterer clusterer, ResultItem item) throws Exception {
    Clusterer		model;
    DataContainer 	dataCont;
    Instances		data;
    Instances		header;
    String		msg;
    MetaData 		runInfo;

    model = (Clusterer) OptionUtils.shallowCopy(clusterer);

    if ((msg = canEvaluate(model)) != null)
      throw new IllegalArgumentException("Cannot evaluate clusterer!\n" + msg);

    dataCont = getOwner().getData().get(m_ComboBoxDatasets.getSelectedIndex());
    data     = dataCont.getData();
    header   = new Instances(data, 0);

    getOwner().logMessage("Using '" + dataCont.getID() + "/" + data.relationName() + "' to build " + OptionUtils.getCommandLine(clusterer));
    model.buildClusterer(data);
    getOwner().logMessage("Built model on '" + dataCont.getID() + "/" + data.relationName() + "' using " + OptionUtils.getCommandLine(clusterer));
    SerializationHelper.writeAll(m_PanelModel.getCurrent().getAbsolutePath(), new Object[]{model, header});
    getOwner().logMessage("Saved model built on '" + dataCont.getID() + "/" + data.relationName() + "' to " + m_PanelModel.getCurrent().getAbsolutePath());

    runInfo  = new MetaData();
    runInfo.add("Clusterer", OptionUtils.getCommandLine(clusterer));
    runInfo.add("Dataset ID", dataCont.getID());
    runInfo.add("Relation", data.relationName());
    runInfo.add("# Attributes", data.numAttributes());
    runInfo.add("# Instances", data.numInstances());
    runInfo.add("Model file", m_PanelModel.getCurrent().getAbsolutePath());
    addObjectSize(runInfo, "Model size", model);

    item.update(null, model, runInfo);
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
    result.put(KEY_MODEL, m_PanelModel.getCurrent().getAbsolutePath());

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
    if (data.containsKey(KEY_MODEL))
      m_PanelModel.setCurrent(new PlaceholderFile((String) data.get(KEY_MODEL)));
  }
}
