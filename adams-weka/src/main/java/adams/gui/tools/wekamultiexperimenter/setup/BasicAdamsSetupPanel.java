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
 * BasicAdamsSetupPanel.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.wekamultiexperimenter.setup;

import adams.core.io.PlaceholderFile;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.ParameterPanel;
import adams.gui.goe.GenericObjectEditorPanel;
import adams.gui.tools.wekamultiexperimenter.experiment.AbstractExperiment;
import adams.gui.tools.wekamultiexperimenter.experiment.AbstractResultWriter;
import adams.gui.tools.wekamultiexperimenter.experiment.CrossValidationExperiment;
import adams.gui.tools.wekamultiexperimenter.experiment.FileResultWriter;
import adams.gui.tools.wekamultiexperimenter.io.AbstractExperimentIO;
import adams.gui.tools.wekamultiexperimenter.io.DefaultAdamsExperimentIO;
import weka.experiment.ResultListener;
import weka.gui.experiment.ExperimenterDefaults;

import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Basic interface for setting up an ADAMS experiment.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BasicAdamsSetupPanel
  extends AbstractAdamsSetupPanel {
  
  /** for serialization. */
  private static final long serialVersionUID = -5412911620981798767L;

  /** for listing all the options. */
  protected ParameterPanel m_PanelParameters;
  
  /** the panel for the output type. */
  protected GenericObjectEditorPanel m_PanelOutput;
  
  /** the number of repetitions. */
  protected JSpinner m_SpinnerRepetitions;

  /** the type of evaluation. */
  protected JComboBox<String> m_ComboBoxEvaluation;
  
  /** the evaluation parameter. */
  protected JTextField m_TextEvaluation;
  
  /** how to traverse. */
  protected JComboBox<String> m_ComboBoxOrder;
  
  /** the tabbed pane for datasets and classifiers. */
  protected BaseTabbedPane m_TabbedPane;
  
  /** for specifying the datasets. */
  protected DatasetPanel m_PanelDatasets;
  
  /** for specifying the classifiers. */
  protected ClassifierPanel m_PanelClassifiers;
  
  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    final int		evalIndex;
    
    super.initGUI();
    
    m_PanelParameters = new ParameterPanel();
    add(m_PanelParameters, BorderLayout.NORTH);

    m_PanelOutput = new GenericObjectEditorPanel(AbstractResultWriter.class, new FileResultWriter(), true);
    m_PanelParameters.addParameter("Output", m_PanelOutput);

    m_SpinnerRepetitions = new JSpinner();
    ((SpinnerNumberModel) m_SpinnerRepetitions.getModel()).setMinimum(1);
    ((SpinnerNumberModel) m_SpinnerRepetitions.getModel()).setStepSize(1);
    ((SpinnerNumberModel) m_SpinnerRepetitions.getModel()).setValue(ExperimenterDefaults.getRepetitions());
    m_SpinnerRepetitions.addChangeListener((ChangeEvent e) -> setModified(true));
    m_PanelParameters.addParameter("Repetitions", m_SpinnerRepetitions);
    
    m_ComboBoxEvaluation = new JComboBox<>(new String[]{
	"Cross-validation",
	"Train/test split (randomized)",
	"Train/test split (order preserved)",
    });
    m_ComboBoxEvaluation.setSelectedIndex(0);
    evalIndex = m_PanelParameters.addParameter("Evaluation", m_ComboBoxEvaluation);
    m_ComboBoxEvaluation.addActionListener((ActionEvent e) -> {
      setModified(true);
      switch (m_ComboBoxEvaluation.getSelectedIndex()) {
        case -1:
        case 0:
          m_PanelParameters.getLabel(evalIndex+1).setText("Number of folds");
          break;
        case 1:
        case 2:
          m_PanelParameters.getLabel(evalIndex+1).setText("Split percentage");
          break;
        default:
          throw new IllegalStateException("Unhandled evaluation type: " + m_ComboBoxEvaluation.getSelectedItem());
      }
    });

    // TODO threads
    
    m_TextEvaluation = new JTextField(20);
    m_PanelParameters.addParameter("", m_TextEvaluation);

    m_ComboBoxOrder = new JComboBox<>(new String[]{
      "Datasets -> Classifiers",
      "Classifiers -> Datasets"
    });
    m_ComboBoxOrder.addActionListener((ActionEvent e) -> setModified(true));
    m_PanelParameters.addParameter("Iteration", m_ComboBoxOrder);
    
    m_PanelDatasets = new DatasetPanel();
    m_PanelDatasets.setOwner(this);

    m_PanelClassifiers = new ClassifierPanel();
    m_PanelClassifiers.setOwner(this);

    m_TabbedPane       = new BaseTabbedPane();
    m_TabbedPane.addTab("Datasets", m_PanelDatasets);
    m_TabbedPane.addTab("Classifiers", m_PanelClassifiers);
    add(m_TabbedPane, BorderLayout.CENTER);
  }
  
  /**
   * finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    
    m_SpinnerRepetitions.setValue(10);
    m_ComboBoxEvaluation.setSelectedIndex(0);
    m_TextEvaluation.setText("10");
    m_ComboBoxOrder.setSelectedIndex(0);

    setModified(false);
  }
  
  /**
   * Returns the name for this setup panel.
   * 
   * @return		the name
   */
  @Override
  public String getSetupName() {
    return "Basic (Adams)";
  }

  /**
   * Creates the handler for the IO, i.e., loading/saving of experiments.
   * 
   * @return		the handler
   */
  @Override
  protected AbstractExperimentIO<AbstractExperiment> createExperimentIO() {
    return new DefaultAdamsExperimentIO();
  }

  /**
   * Returns the current experiment.
   * 
   * @return		the experiment
   */
  @Override
  public AbstractExperiment getExperiment() {
    AbstractExperiment		result;
    ResultListener		listener;

    result = getExperimentIO().create();

    switch (m_ComboBoxEvaluation.getSelectedIndex()) {
      case 0:
	result = new CrossValidationExperiment();
	((CrossValidationExperiment) result).setFolds(Integer.parseInt(m_TextEvaluation.getText()));
	break;
      // TODO
    }

    result.setResultWriter((AbstractResultWriter) m_PanelOutput.getCurrent());
    result.setRuns((Integer) m_SpinnerRepetitions.getValue());
    result.setDatasetsFirst(m_ComboBoxOrder.getSelectedIndex() <= 0);
    result.setClassifiers(m_PanelClassifiers.getClassifiers());

    for (File file: m_PanelDatasets.getFiles())
      result.addDataset(new PlaceholderFile(file));

    return result;
  }

  /**
   * Sets the experiment to use.
   * 
   * @param value	the experiment
   */
  @Override
  public void setExperiment(AbstractExperiment value) {
    if (handlesExperiment(value) == null) {
      if (value instanceof CrossValidationExperiment) {
	m_ComboBoxEvaluation.setSelectedIndex(0);
	m_TextEvaluation.setText("" + ((CrossValidationExperiment) value).getFolds());
      }
      // TODO train/test splits
      m_PanelOutput.setCurrent(value.getResultWriter());
      m_SpinnerRepetitions.setValue(value.getRuns());
      m_ComboBoxOrder.setSelectedIndex(value.getDatasetsFirst() ? 0 : 1);
      m_PanelDatasets.setFiles(value.getDatasets());
      m_PanelClassifiers.setClassifiers(value.getClassifiers());
    }
    else {
      throw new IllegalArgumentException("Cannot handle experiment: " + value.getClass().getName());
    }
  }

  /**
   * Checks whether the experiment can be handled.
   * 
   * @param exp		the experiment to check
   * @return		null if can handle, otherwise error message
   */
  @Override
  public String handlesExperiment(AbstractExperiment exp) {
    if (exp instanceof CrossValidationExperiment)
      return null;
    // TODO train/test splits
    return "Unsupported experiment type: " + exp.getClass().getName();
  }
}
