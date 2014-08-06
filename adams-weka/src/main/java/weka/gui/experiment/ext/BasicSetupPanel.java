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
 * BasicSetupPanel.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package weka.gui.experiment.ext;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import weka.classifiers.Classifier;
import weka.experiment.Experiment;
import weka.experiment.RemoteExperiment;
import weka.gui.experiment.ExperimenterDefaults;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.ParameterPanel;

/**
 * Basic interface for setting up an experiment.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BasicSetupPanel
  extends AbstractSetupPanel {
  
  /** for serialization. */
  private static final long serialVersionUID = -5412911620981798767L;

  /** for listing all the options. */
  protected ParameterPanel m_PanelParameters;
  
  /** the panel for the output type. */
  protected OutputPanel m_PanelOutput;
  
  /** the number of repetitions. */
  protected JSpinner m_SpinnerRepetitions;
  
  /** how to traverse. */
  protected JComboBox m_ComboBoxOrder;
  
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
    super.initGUI();
    
    m_PanelParameters = new ParameterPanel();
    add(m_PanelParameters, BorderLayout.NORTH);

    m_PanelOutput = new OutputPanel();
    m_PanelOutput.setOwner(this);
    m_PanelParameters.addParameter("Output", m_PanelOutput);

    m_SpinnerRepetitions = new JSpinner();
    ((SpinnerNumberModel) m_SpinnerRepetitions.getModel()).setMinimum(1);
    ((SpinnerNumberModel) m_SpinnerRepetitions.getModel()).setStepSize(1);
    ((SpinnerNumberModel) m_SpinnerRepetitions.getModel()).setValue(ExperimenterDefaults.getRepetitions());
    m_SpinnerRepetitions.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
	setModified(true);
      }
    });
    m_PanelParameters.addParameter("Repetitions", m_SpinnerRepetitions);
    
    m_ComboBoxOrder = new JComboBox(new String[]{
	"Datasets -> Classifiers",
	"Classifiers -> Datasets"
    });
    m_ComboBoxOrder.setSelectedIndex(0);
    m_ComboBoxOrder.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	setModified(true);
      }
    });
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
   * Returns the name for this setup panel.
   * 
   * @return		the name
   */
  @Override
  public String getSetupName() {
    return "Basic";
  }

  /**
   * Creates the handler for the IO, i.e., loading/saving of experiments.
   * 
   * @return		the handler
   */
  @Override
  protected AbstractExperimentIO createExperimentIO() {
    return new DefaultExperimentIO();
  }

  /**
   * Returns the current experiment.
   * 
   * @return		the experiment
   */
  @Override
  public Experiment getExperiment() {
    Experiment		result;
    File[]		files;
    Classifier[]	classifiers;
    
    result = getExperimentIO().create();
    result.setResultListener(m_PanelOutput.getResultListener());
    
    result.setPropertyArray(new Classifier[0]);
    result.setUsePropertyIterator(true);
    result.setRunLower(1);
    result.setRunUpper((Integer) m_SpinnerRepetitions.getValue());
    
    // TODO iteration (datasets or classifiers first)
    // TODO regression or classification
    // TODO randomsplit or cross-validation

    files = m_PanelDatasets.getFiles();
    for (File file: files)
      result.getDatasets().addElement(file);
    
    classifiers = m_PanelClassifiers.getClassifiers();
    result.setPropertyArray(classifiers);
    
    return result;
  }

  /**
   * Sets the experiment to use.
   * 
   * @param value	the experiment
   */
  @Override
  public void setExperiment(Experiment value) {
    Classifier[]	classifiers;
    int			i;
    
    if (handlesExperiment(value)) {
      m_PanelOutput.setResultListener(value.getResultListener());
      m_SpinnerRepetitions.setValue(value.getRunUpper());

      // TODO iteration (datasets or classifiers first)
      // TODO regression or classification
      // TODO randomsplit or cross-validation
      
      classifiers = new Classifier[value.getDatasets().getSize()];
      for (i = 0; i < classifiers.length; i++)
	classifiers[i] = (Classifier) value.getDatasets().getElementAt(i);
      
      if (value.getPropertyArray() instanceof Classifier[])
	classifiers = (Classifier[]) value.getPropertyArray();
      else
	classifiers = new Classifier[0];
      m_PanelClassifiers.setClassifiers(classifiers);
    }
    else {
      throw new IllegalArgumentException("Cannot handle experiment: " + value.getClass().getName());
    }
  }

  /**
   * Checks whether the experiment can be handled.
   * 
   * @param exp		the experiment to check
   * @return		true if can be handled
   */
  @Override
  public boolean handlesExperiment(Experiment exp) {
    return 
	   !(exp instanceof RemoteExperiment) 
	&& exp.getUsePropertyIterator() 
	&& (exp.getPropertyArray() instanceof Classifier[]);
  }
}
