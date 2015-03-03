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
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;

import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import weka.classifiers.Classifier;
import weka.experiment.ClassifierSplitEvaluator;
import weka.experiment.CrossValidationResultProducer;
import weka.experiment.Experiment;
import weka.experiment.PropertyNode;
import weka.experiment.RandomSplitResultProducer;
import weka.experiment.RegressionSplitEvaluator;
import weka.experiment.RemoteExperiment;
import weka.experiment.SplitEvaluator;
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
  
  /** classification or regression. */
  protected JComboBox m_ComboBoxClassificationRegression;
  
  /** the type of evaluation. */
  protected JComboBox m_ComboBoxEvaluation;
  
  /** the evaluation parameter. */
  protected JTextField m_TextEvaluation;
  
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
    final int		evalIndex;
    
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
    
    m_ComboBoxClassificationRegression = new JComboBox(new String[]{
	"Classification",
	"Regression"
    });
    m_ComboBoxClassificationRegression.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	setModified(true);
      }
    });
    m_PanelParameters.addParameter("Task", m_ComboBoxClassificationRegression);
    
    m_ComboBoxEvaluation = new JComboBox(new String[]{
	"Cross-validation",
	"Train/test split (randomized)",
	"Train/test split (order preserved)",
    });
    m_ComboBoxEvaluation.setSelectedIndex(0);
    evalIndex = m_PanelParameters.addParameter("Evaluation", m_ComboBoxEvaluation);
    m_ComboBoxEvaluation.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
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
      }
    });
    
    m_TextEvaluation = new JTextField(20);
    m_PanelParameters.addParameter("", m_TextEvaluation);
    
    m_ComboBoxOrder = new JComboBox(new String[]{
	"Datasets -> Classifiers",
	"Classifiers -> Datasets"
    });
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
   * finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    
    m_SpinnerRepetitions.setValue(10);
    m_ComboBoxClassificationRegression.setSelectedIndex(0);
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
    Experiment				result;
    File[]				files;
    Classifier[]			classifiers;
    SplitEvaluator 			se;
    Classifier 				sec;
    CrossValidationResultProducer 	cvrp;
    RandomSplitResultProducer 		rsrp;
    PropertyNode[] 			propertyPath;
    
    result = getExperimentIO().create();
    result.setResultListener(m_PanelOutput.getResultListener());
    
    result.setPropertyArray(new Classifier[0]);
    result.setUsePropertyIterator(true);
    result.setRunLower(1);
    result.setRunUpper((Integer) m_SpinnerRepetitions.getValue());
    result.setAdvanceDataSetFirst(m_ComboBoxOrder.getSelectedIndex() <= 0);

    // classification/regression?
    se             = null;
    sec            = null;
    switch (m_ComboBoxClassificationRegression.getSelectedIndex()) {
      case 0:
	se  = new ClassifierSplitEvaluator();
	sec = ((ClassifierSplitEvaluator) se).getClassifier();
	break;
      case 1:
	se  = new RegressionSplitEvaluator();
	sec = ((RegressionSplitEvaluator) se).getClassifier();
	break;
      default:
	throw new IllegalStateException("Either select classification or regression!");
    }
    
    // cross-validation/random-split?
    switch (m_ComboBoxEvaluation.getSelectedIndex()) {
      case -1:
      case 0:
	cvrp = new CrossValidationResultProducer();
	if (m_TextEvaluation.getText().length() == 0)
	  throw new IllegalArgumentException("No folds provided!");
	cvrp.setNumFolds(Integer.parseInt(m_TextEvaluation.getText()));
	cvrp.setSplitEvaluator(se);
	propertyPath = new PropertyNode[2];
	try {
	  propertyPath[0] = new PropertyNode(
	      se, 
	      new PropertyDescriptor("splitEvaluator",
		  CrossValidationResultProducer.class),
		  CrossValidationResultProducer.class);
	  propertyPath[1] = new PropertyNode(
	      sec, 
	      new PropertyDescriptor("classifier",
		  se.getClass()),
		  se.getClass());
	}
	catch (IntrospectionException e) {
	  e.printStackTrace();
	}
	result.setResultProducer(cvrp);
	result.setPropertyPath(propertyPath);
	break;
	
      case 1:
      case 2:
	rsrp = new RandomSplitResultProducer();
	rsrp.setRandomizeData(m_ComboBoxEvaluation.getSelectedIndex() == 1);
	if (m_TextEvaluation.getText().length() == 0)
	  throw new IllegalArgumentException("No percentage provided!");
	rsrp.setTrainPercent(Double.parseDouble(m_TextEvaluation.getText()));
	rsrp.setSplitEvaluator(se);
	propertyPath = new PropertyNode[2];
	try {
	  propertyPath[0] = new PropertyNode(
	      se, 
	      new PropertyDescriptor("splitEvaluator",
		  RandomSplitResultProducer.class),
		  RandomSplitResultProducer.class);
	  propertyPath[1] = new PropertyNode(
	      sec, 
	      new PropertyDescriptor("classifier",
		  se.getClass()),
		  se.getClass());
	}
	catch (IntrospectionException e) {
	  e.printStackTrace();
	}

	result.setResultProducer(rsrp);
	result.setPropertyPath(propertyPath);
	break;

      default:
	throw new IllegalStateException("Unhandled evaluation: " + m_ComboBoxEvaluation.getSelectedItem());
    }

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
    Classifier[]			classifiers;
    File[]				files;
    int					i;
    CrossValidationResultProducer	cvrp;
    RandomSplitResultProducer		rsrp;
    
    if (handlesExperiment(value) == null) {
      m_PanelOutput.setResultListener(value.getResultListener());
      m_SpinnerRepetitions.setValue(value.getRunUpper());
      m_ComboBoxOrder.setSelectedIndex(value.getAdvanceDataSetFirst() ? 0 : 1);

      if (value.getResultProducer() instanceof CrossValidationResultProducer) {
	cvrp = (CrossValidationResultProducer) value.getResultProducer();
	m_ComboBoxEvaluation.setSelectedIndex(0);
	m_TextEvaluation.setText("" + cvrp.getNumFolds());
	if (cvrp.getSplitEvaluator() instanceof ClassifierSplitEvaluator)
	  m_ComboBoxClassificationRegression.setSelectedIndex(0);
	else if (cvrp.getSplitEvaluator() instanceof RegressionSplitEvaluator)
	  m_ComboBoxClassificationRegression.setSelectedIndex(1);
      }
      else if (value.getResultProducer() instanceof RandomSplitResultProducer) {
	rsrp = (RandomSplitResultProducer) value.getResultProducer();
	if (rsrp.getRandomizeData())
	  m_ComboBoxEvaluation.setSelectedIndex(1);
	else
	  m_ComboBoxEvaluation.setSelectedIndex(2);
	m_TextEvaluation.setText("" + rsrp.getTrainPercent());
	if (rsrp.getSplitEvaluator() instanceof ClassifierSplitEvaluator)
	  m_ComboBoxClassificationRegression.setSelectedIndex(0);
	else if (rsrp.getSplitEvaluator() instanceof RegressionSplitEvaluator)
	  m_ComboBoxClassificationRegression.setSelectedIndex(1);
      }
      
      files = new File[value.getDatasets().size()];
      for (i = 0; i < value.getDatasets().size(); i++)
	files[i] = (File) value.getDatasets().get(i);
      m_PanelDatasets.setFiles(files);
      
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
   * @return		null if can handle, otherwise error message
   */
  @Override
  public String handlesExperiment(Experiment exp) {
    SplitEvaluator	spliteval;
    
    if (exp instanceof RemoteExperiment)
      return "Cannot handle remote experiment";

    if (!exp.getUsePropertyIterator())
      return "Does not use property iterator";

    if (!(exp.getPropertyArray() instanceof Classifier[]))
      return "Does not iterate on classifier";

    if (exp.getRunLower() != 1)
      return "Lower run number must be 1";

    if (!((exp.getResultProducer() instanceof CrossValidationResultProducer) || (exp.getResultProducer() instanceof RandomSplitResultProducer)))
      return "Can only handle " + CrossValidationResultProducer.class.getName() + " or " + RandomSplitResultProducer.class.getName();

    spliteval = null;
    if (exp.getResultProducer() instanceof CrossValidationResultProducer)
      spliteval = ((CrossValidationResultProducer) exp.getResultProducer()).getSplitEvaluator();
    else if (exp.getResultProducer() instanceof RandomSplitResultProducer)
      spliteval = ((RandomSplitResultProducer) exp.getResultProducer()).getSplitEvaluator();
    if (!((spliteval instanceof ClassifierSplitEvaluator) || (spliteval instanceof RegressionSplitEvaluator)))
      return "Can only handle " + ClassifierSplitEvaluator.class.getName() + " and " + RegressionSplitEvaluator.class.getName() + "";

    return null;
  }
}
