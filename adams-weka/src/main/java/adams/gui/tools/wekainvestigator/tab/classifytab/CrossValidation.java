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

package adams.gui.tools.wekainvestigator.tab.classifytab;

import adams.gui.core.ParameterPanel;
import adams.gui.tools.wekainvestigator.InvestigatorPanel;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Performs cross-validation.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CrossValidation
  extends AbstractClassifierEvaluation {

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

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    m_PanelParameters = new ParameterPanel();
    m_PanelOptions.add(m_PanelParameters, BorderLayout.CENTER);

    // dataset
    m_ModelDatasets    = new DefaultComboBoxModel<>();
    m_ComboBoxDatasets = new JComboBox<>(m_ModelDatasets);
    m_PanelParameters.addParameter("Dataset", m_ComboBoxDatasets);

    // folds
    m_SpinnerFolds = new JSpinner();
    ((SpinnerNumberModel) m_SpinnerFolds.getModel()).setMinimum(2);
    ((SpinnerNumberModel) m_SpinnerFolds.getModel()).setStepSize(1);
    m_SpinnerFolds.setValue(InvestigatorPanel.getProperties().getInteger("Classify.NumFolds", 10));
    m_PanelParameters.addParameter("Folds", m_SpinnerFolds);

    // seed
    m_TextSeed = new JTextField("" + InvestigatorPanel.getProperties().getInteger("Classify.Seed", 1));
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
   * Tests whether the classifier can be evaluated.
   *
   * @return		true if possible
   */
  public boolean canEvaluate(Classifier classifier) {
    Instances	data;

    if (m_ComboBoxDatasets.getSelectedIndex() == -1)
      return false;

    data = getOwner().getData().get(m_ComboBoxDatasets.getSelectedIndex()).getData();
    return classifier.getCapabilities().test(data);
  }

  /**
   * Evaluates the classifier and returns the generated evaluation object.
   *
   * @return		the evaluation
   * @throws Exception	if evaluation fails
   */
  @Override
  public Evaluation evaluate(Classifier classifier) throws Exception {
    Evaluation	result;
    Instances	data;

    if (!canEvaluate(classifier))
      throw new IllegalArgumentException("Cannot evaluate classifier!");

    data   = getOwner().getData().get(m_ComboBoxDatasets.getSelectedIndex()).getData();
    result = new Evaluation(data);
    result.crossValidateModel(
      classifier, data, ((Number) m_SpinnerFolds.getValue()).intValue(),
      new Random(Integer.parseInt(m_TextSeed.getText())));

    return result;
  }

  /**
   * Updates the settings panel.
   */
  @Override
  public void update() {
    List<String>	datasets;
    int			i;
    String		oldDataset;
    int			index;
    DataContainer 	data;

    if (getOwner() == null)
      return;
    if (getOwner().getOwner() == null)
      return;

    oldDataset = (String) m_ComboBoxDatasets.getSelectedItem();
    if (oldDataset != null)
      oldDataset = oldDataset.replaceAll("^[0-9]]+: ", "");
    datasets = new ArrayList<>();
    index    = -1;
    for (i = 0; i < getOwner().getData().size(); i++) {
      data = getOwner().getData().get(i);
      datasets.add((i + 1) + ": " + data.getData().relationName());
      if ((oldDataset != null) && data.getData().relationName().equals(oldDataset))
	index = i;
    }
    m_ModelDatasets = new DefaultComboBoxModel<>(datasets.toArray(new String[datasets.size()]));
    m_ComboBoxDatasets.setModel(m_ModelDatasets);
    if ((index == -1) && (m_ModelDatasets.getSize() > 0))
      m_ComboBoxDatasets.setSelectedIndex(0);
    else if (index > -1)
      m_ComboBoxDatasets.setSelectedIndex(index);

    getOwner().updateButtons();
  }
}
