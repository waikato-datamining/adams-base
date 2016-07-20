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
 * ClassifyTab.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab;

import adams.core.option.OptionUtils;
import adams.gui.core.ConsolePanel;
import adams.gui.goe.WekaGenericObjectEditorPanel;
import adams.gui.tools.wekainvestigator.InvestigatorPanel;
import adams.gui.tools.wekainvestigator.tab.classifytab.AbstractClassifierEvaluation;
import weka.classifiers.Classifier;
import weka.classifiers.rules.ZeroR;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.logging.Level;

/**
 * Tab for classification.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ClassifyTab
  extends AbstractInvestigatorTab {

  private static final long serialVersionUID = -4106630131554796889L;

  /** the GOe with the classifier. */
  protected WekaGenericObjectEditorPanel m_PanelGOE;

  /** the panel on the left-hand side. */
  protected JPanel m_PanelLeft;

  /** the panel with the evaluation. */
  protected JPanel m_PanelEvaluation;

  /** the combobox with the available evaluations. */
  protected JComboBox<AbstractClassifierEvaluation> m_ComboBoxEvaluations;

  /** the model with the available evaluations. */
  protected DefaultComboBoxModel<AbstractClassifierEvaluation> m_ModelEvaluations;

  /** the panel for the evaluation setup to be embedded in. */
  protected JPanel m_PanelEvaluationSetup;

  /** the current evaluation. */
  protected AbstractClassifierEvaluation m_CurrentEvaluation;

  /** the current classifier. */
  protected Classifier m_CurrentClassifier;

  /** the panel with the buttons. */
  protected JPanel m_PanelEvaluationButtons;

  /** the start button. */
  protected JButton m_ButtonStart;

  /** the stop button. */
  protected JButton m_ButtonStop;

  /** whether the evaluation is currently running. */
  protected Thread m_Worker;

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_CurrentEvaluation = null;
    m_CurrentClassifier = null;
    m_Worker            = null;
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    Classifier				cls;
    Class[]				classes;
    AbstractClassifierEvaluation	eval;
    JPanel				panel;

    super.initGUI();

    setLayout(new BorderLayout());
    setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    try {
      cls = (Classifier) OptionUtils.forAnyCommandLine(
	Classifier.class,
	InvestigatorPanel.getProperties().getProperty(
	  "Classify.Classifier", ZeroR.class.getName()));
    }
    catch (Exception e) {
      cls = new ZeroR();
    }
    m_PanelGOE = new WekaGenericObjectEditorPanel(Classifier.class, cls, true);
    m_PanelGOE.setPrefix("Classifier");
    panel = new JPanel(new BorderLayout());
    panel.add(m_PanelGOE, BorderLayout.CENTER);
    panel.setBorder(BorderFactory.createTitledBorder(""));
    add(panel, BorderLayout.NORTH);

    m_PanelLeft = new JPanel(new BorderLayout());
    add(m_PanelLeft, BorderLayout.WEST);

    // evaluation
    m_PanelEvaluation = new JPanel(new BorderLayout());
    m_PanelEvaluation.setBorder(BorderFactory.createTitledBorder("Evaluation"));
    m_PanelLeft.add(m_PanelEvaluation, BorderLayout.NORTH);

    m_ModelEvaluations = new DefaultComboBoxModel<>();
    classes            = AbstractClassifierEvaluation.getEvaluations();
    for (Class c: classes) {
      try {
	eval = (AbstractClassifierEvaluation) c.newInstance();
	eval.setOwner(this);
	m_ModelEvaluations.addElement(eval);
      }
      catch (Exception e) {
	ConsolePanel.getSingleton().append(Level.SEVERE, "Failed to instantiate classifier evaluation: " + c.getName(), e);
      }
    }
    m_ComboBoxEvaluations = new JComboBox<>(m_ModelEvaluations);
    m_ComboBoxEvaluations.addActionListener((ActionEvent e) -> {
      if (m_ComboBoxEvaluations.getSelectedIndex() == -1)
	return;
      m_CurrentEvaluation = (AbstractClassifierEvaluation) m_ComboBoxEvaluations.getSelectedItem();
      m_PanelEvaluationSetup.removeAll();
      m_PanelEvaluationSetup.add(m_CurrentEvaluation.getPanel());
      m_CurrentEvaluation.update();
      m_PanelEvaluationSetup.invalidate();
      m_PanelEvaluationSetup.revalidate();
      m_PanelEvaluationSetup.doLayout();
    });
    m_PanelEvaluation.add(m_ComboBoxEvaluations, BorderLayout.NORTH);

    // setup
    m_PanelEvaluationSetup = new JPanel(new BorderLayout());
    m_PanelEvaluation.add(m_PanelEvaluationSetup, BorderLayout.CENTER);

    // buttons
    m_PanelEvaluationButtons = new JPanel(new GridLayout(1, 2));
    m_PanelEvaluation.add(m_PanelEvaluationButtons, BorderLayout.SOUTH);

    m_ButtonStart = new JButton("Start");
    m_ButtonStart.addActionListener((ActionEvent e) -> startEvaluation());
    m_PanelEvaluationButtons.add(m_ButtonStart);

    m_ButtonStop = new JButton("Stop");
    m_ButtonStop.addActionListener((ActionEvent e) -> stopEvaluation());
    m_PanelEvaluationButtons.add(m_ButtonStop);
  }

  /**
   * Finishes up the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();

    m_ComboBoxEvaluations.setSelectedIndex(0);
  }

  /**
   * Returns the title of this table.
   *
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Classify";
  }

  /**
   * Returns the icon name for the tab icon.
   *
   * @return		the icon name, null if not available
   */
  public String getTabIcon() {
    return "classifier.png";
  }

  /**
   * Notifies the tab that the data changed.
   */
  @Override
  public void dataChanged() {
    if (m_CurrentEvaluation != null)
      m_CurrentEvaluation.update();
    updateButtons();
  }

  /**
   * Starts the evaluation.
   */
  protected void startEvaluation() {
    if (m_Worker != null)
      return;

    m_Worker = new Thread(() -> {
      m_CurrentClassifier = (Classifier) m_PanelGOE.getCurrent();
      logMessage("Starting evaluation '" + m_CurrentEvaluation.getName() + "' using: " + OptionUtils.getCommandLine(m_CurrentClassifier));
      try {
	m_CurrentEvaluation.evaluate(m_CurrentClassifier);
	logMessage("Finished evaluation '" + m_CurrentEvaluation.getName() + "' using: " + OptionUtils.getCommandLine(m_CurrentClassifier));
      }
      catch (Exception e) {
	logError("Failed to evaluate classifier", e, "Classifier evaluation");
      }
      m_Worker = null;
      updateButtons();
    });
    m_Worker.start();
    updateButtons();
  }

  /**
   * Stops the evaluation.
   */
  protected void stopEvaluation() {
    if (m_Worker == null)
      return;

    m_Worker.stop();
    logMessage("Stopped evaluation '" + m_CurrentEvaluation.getName() + "' using: " + OptionUtils.getCommandLine(m_CurrentClassifier));
    updateButtons();
  }

  /**
   * Updates the buttons.
   */
  public void updateButtons() {
    Classifier cls;

    cls = (Classifier) m_PanelGOE.getCurrent();
    m_ButtonStart.setEnabled((m_Worker == null) && (m_CurrentEvaluation != null) && m_CurrentEvaluation.canEvaluate(cls));
    m_ButtonStop.setEnabled(m_Worker != null);
  }
}
