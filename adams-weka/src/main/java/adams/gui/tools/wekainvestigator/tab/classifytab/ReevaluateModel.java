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
 * ReevaluateModel.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.classifytab;

import adams.core.SerializationHelper;
import adams.gui.chooser.FileChooserPanel;
import adams.gui.core.AbstractNamedHistoryPanel;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.core.ParameterPanel;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Capabilities;
import weka.core.Instances;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.event.ChangeEvent;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

/**
 * Re-evaluates a serialized model.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ReevaluateModel
  extends AbstractClassifierEvaluation {

  private static final long serialVersionUID = 1175400993991698944L;

  /** the panel with the parameters. */
  protected ParameterPanel m_PanelParameters;

  /** the datasets. */
  protected JComboBox<String> m_ComboBoxDatasets;

  /** the datasets model. */
  protected DefaultComboBoxModel<String> m_ModelDatasets;

  /** the serialized model. */
  protected FileChooserPanel m_PanelModel;

  /** the current model. */
  protected Classifier m_Model;

  /** the training header (if any). */
  protected Instances m_Header;

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

    super.initGUI();

    m_PanelParameters = new ParameterPanel();
    m_PanelOptions.add(m_PanelParameters, BorderLayout.CENTER);

    // dataset
    m_ModelDatasets    = new DefaultComboBoxModel<>();
    m_ComboBoxDatasets = new JComboBox<>(m_ModelDatasets);
    m_ComboBoxDatasets.addActionListener((ActionEvent e) -> update());
    m_PanelParameters.addParameter("Dataset", m_ComboBoxDatasets);

    // model file
    m_PanelModel = new FileChooserPanel();
    filter = ExtensionFileFilter.getModelFileFilter();
    m_PanelModel.addChoosableFileFilter(filter);
    m_PanelModel.setFileFilter(filter);
    m_PanelModel.setAcceptAllFileFilterUsed(true);
    m_PanelModel.addChangeListener((ChangeEvent e) -> loadModel());
    m_PanelParameters.addParameter("Model", m_PanelModel);
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
   * @return		true if successfully loaded
   */
  protected boolean loadModel() {
    File	file;
    Object[]	obj;

    m_Model  = null;
    m_Header = null;

    file = m_PanelModel.getCurrent();
    if (file.isDirectory())
      return false;
    if (!file.exists())
      return false;

    try {
      obj = SerializationHelper.readAll(file.getAbsolutePath());
      if (obj.length > 0)
	m_Model = (Classifier) obj[0];
      if (obj.length > 1)
	m_Header = (Instances) obj[1];
    }
    catch (Exception e) {
      showStatus("Failed to load model: " + file);
      return false;
    }

    return true;
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

    if (m_ComboBoxDatasets.getSelectedIndex() == -1)
      return "No data available!";

    file = m_PanelModel.getCurrent();
    if (file.isDirectory())
      return "Model points to directory: " + file;
    if (!file.exists())
      return "Model does not exist: " + file;

    if (m_Model == null)
      loadModel();
    if (m_Model == null)
      return "Failed to load model: " + file;

    data = getOwner().getData().get(m_ComboBoxDatasets.getSelectedIndex()).getData();
    if (m_Header != null) {
      if (!data.equalHeaders(m_Header))
	return data.equalHeadersMsg(m_Header);
    }

    caps = m_Model.getCapabilities();
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
   * @return		the evaluation
   * @param history	the history to add the result to
   * @throws Exception	if evaluation fails
   */
  @Override
  public Evaluation evaluate(Classifier classifier, AbstractNamedHistoryPanel<ResultItem> history) throws Exception {
    Evaluation	result;
    Instances	data;
    ResultItem	item;
    String	msg;

    if ((msg = canEvaluate(classifier)) != null)
      throw new IllegalArgumentException("Cannot evaluate classifier!\n" + msg);

    data   = getOwner().getData().get(m_ComboBoxDatasets.getSelectedIndex()).getData();
    result = new Evaluation(data);
    result.evaluateModel(m_Model, data);

    // history
    item = new ResultItem(result, m_Model, m_Header);
    history.addEntry(item.getName(), item);

    return result;
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
