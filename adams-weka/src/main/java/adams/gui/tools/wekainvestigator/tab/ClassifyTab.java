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

import adams.core.Properties;
import adams.core.option.OptionUtils;
import adams.gui.core.AbstractNamedHistoryPanel;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.ConsolePanel;
import adams.gui.goe.GenericArrayEditorDialog;
import adams.gui.goe.WekaGenericObjectEditorPanel;
import adams.gui.tools.wekainvestigator.InvestigatorPanel;
import adams.gui.tools.wekainvestigator.tab.classifytab.ResultItem;
import adams.gui.tools.wekainvestigator.tab.classifytab.evaluation.AbstractClassifierEvaluation;
import adams.gui.tools.wekainvestigator.tab.classifytab.output.AbstractOutputGenerator;
import adams.gui.tools.wekainvestigator.tab.classifytab.output.TextStatistics;
import weka.classifiers.Classifier;
import weka.classifiers.rules.ZeroR;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
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

  /**
   * Customized history panel.
   */
  public static class HistoryPanel
    extends AbstractNamedHistoryPanel<ResultItem> {

    private static final long serialVersionUID = 8740813441072965573L;

    /** the owner. */
    protected ClassifyTab m_Owner;

    /**
     * Initializes the history.
     *
     * @param owner	the owning tab
     */
    public HistoryPanel(ClassifyTab owner) {
      super();
      m_Owner = owner;
      setAllowRemove(true);
      setAllowRename(false);
    }

    /**
     * Displays the specified entry.
     *
     * @param name	the name of the entry, can be null to empty display
     */
    @Override
    protected void updateEntry(String name) {
      m_Owner.getPanelRight().removeAll();
      if (name != null) {
	if (hasEntry(name))
	  m_Owner.getPanelRight().add(getEntry(name).getTabbedPane());
      }
      m_Owner.getPanelRight().invalidate();
      m_Owner.getPanelRight().revalidate();
      m_Owner.getPanelRight().doLayout();
      m_Owner.getPanelRight().repaint();
    }
  }

  /** the GOe with the classifier. */
  protected WekaGenericObjectEditorPanel m_PanelGOE;

  /** the split pane for left/right panels. */
  protected BaseSplitPane m_SplitPane;

  /** the panel on the left-hand side. */
  protected JPanel m_PanelLeft;

  /** the panel on the right-hand side (displays results). */
  protected JPanel m_PanelRight;

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

  /** the panel with output generators. */
  protected JPanel m_PanelOutputGenerators;

  /** the button for editing the output generators. */
  protected JButton m_ButtonOutputGenerators;

  /** the panel with the buttons. */
  protected JPanel m_PanelExecutionButtons;

  /** the start button. */
  protected JButton m_ButtonStart;

  /** the stop button. */
  protected JButton m_ButtonStop;

  /** the history. */
  protected HistoryPanel m_History;

  /** whether the evaluation is currently running. */
  protected Thread m_Worker;

  /** the output generators to use. */
  protected AbstractOutputGenerator[] m_OutputGenerators;

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initialize() {
    Properties				props;
    String[]				cmds;
    List<AbstractOutputGenerator>	generators;
    AbstractOutputGenerator		generator;
    int					i;

    super.initialize();

    props = InvestigatorPanel.getProperties();

    m_CurrentEvaluation = null;
    m_CurrentClassifier = null;
    m_Worker            = null;

    try {
      cmds = OptionUtils.splitOptions(
	props.getProperty("Classify.DefaultOutputGenerators", TextStatistics.class.getName()));
    }
    catch (Exception e) {
      ConsolePanel.getSingleton().append(
	Level.SEVERE,
	"Failed to parse output generators:\n" + props.getProperty("Classify.DefaultOutputGenerators"), e);
      cmds = new String[]{TextStatistics.class.getName()};
    }

    generators = new ArrayList<>();
    for (i = 0; i < cmds.length; i++) {
      try {
	generator = (AbstractOutputGenerator) OptionUtils.forAnyCommandLine(AbstractOutputGenerator.class, cmds[i]);
	generators.add(generator);
      }
      catch (Exception e) {
	ConsolePanel.getSingleton().append(
	  Level.SEVERE,
	  "Failed to instantiate output generator:\n" + cmds[i], e);
      }
    }
    m_OutputGenerators = generators.toArray(new AbstractOutputGenerator[generators.size()]);
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
    Properties				props;
    JPanel				buttonsAll;
    JLabel 				label;

    super.initGUI();

    props = InvestigatorPanel.getProperties();

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

    m_SplitPane = new BaseSplitPane(BaseSplitPane.HORIZONTAL_SPLIT);
    m_SplitPane.setOneTouchExpandable(true);
    add(m_SplitPane, BorderLayout.CENTER);
    m_PanelLeft = new JPanel(new BorderLayout());
    m_SplitPane.setLeftComponent(m_PanelLeft);
    m_PanelRight = new JPanel(new BorderLayout());
    m_SplitPane.setRightComponent(m_PanelRight);
    m_SplitPane.setDividerLocation(props.getInteger("Classify.LeftPanelWidth", 200));

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

    // all buttons
    buttonsAll = new JPanel(new GridLayout(2, 1));
    m_PanelEvaluation.add(buttonsAll, BorderLayout.SOUTH);

    // output generators
    m_ButtonOutputGenerators = new JButton("...");
    m_ButtonOutputGenerators.addActionListener((ActionEvent) -> editOutputGenerators());
    label = new JLabel("Output generators");
    label.setLabelFor(m_ButtonOutputGenerators);
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(label);
    panel.add(m_ButtonOutputGenerators);
    buttonsAll.add(panel);

    // start/stop buttons
    m_PanelExecutionButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
    buttonsAll.add(m_PanelExecutionButtons);

    m_ButtonStart = new JButton("Start");
    m_ButtonStart.addActionListener((ActionEvent e) -> startEvaluation());
    m_PanelExecutionButtons.add(m_ButtonStart);

    m_ButtonStop = new JButton("Stop");
    m_ButtonStop.addActionListener((ActionEvent e) -> stopEvaluation());
    m_PanelExecutionButtons.add(m_ButtonStop);

    // history
    m_History = new HistoryPanel(this);
    m_PanelLeft.add(m_History, BorderLayout.CENTER);
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
   * Allows the user to modify the output generators.
   */
  protected void editOutputGenerators() {
    GenericArrayEditorDialog	dialog;

    if (getParentDialog() != null)
      dialog = new GenericArrayEditorDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new GenericArrayEditorDialog(getParentFrame(), true);
    dialog.setDefaultCloseOperation(GenericArrayEditorDialog.DISPOSE_ON_CLOSE);
    dialog.setOkAlwaysEnabled(true);
    dialog.setTitle("Output generators");
    dialog.setCurrent(m_OutputGenerators);
    dialog.pack();
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
    if (dialog.getResult() != GenericArrayEditorDialog.APPROVE_OPTION)
      return;
    m_OutputGenerators = (AbstractOutputGenerator[]) dialog.getCurrent();
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
	ResultItem item = m_CurrentEvaluation.evaluate(m_CurrentClassifier, m_History);
	for (int i = 0; i < m_OutputGenerators.length; i++)
	  m_OutputGenerators[i].generateOutput(item);
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
    m_ButtonStart.setEnabled((m_Worker == null) && (m_CurrentEvaluation != null) && (m_CurrentEvaluation.canEvaluate(cls) == null));
    m_ButtonStop.setEnabled(m_Worker != null);
  }

  /**
   * Returns the left panel.
   *
   * @return		the left panel
   */
  public JPanel getPanelLeft() {
    return m_PanelLeft;
  }

  /**
   * Returns the right panel.
   *
   * @return		the right panel
   */
  public JPanel getPanelRight() {
    return m_PanelRight;
  }
}
