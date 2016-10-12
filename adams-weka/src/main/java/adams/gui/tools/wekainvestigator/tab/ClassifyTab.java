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

import adams.core.ClassLister;
import adams.core.MessageCollection;
import adams.core.Properties;
import adams.core.SerializationHelper;
import adams.core.Stoppable;
import adams.core.option.OptionUtils;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.core.AbstractNamedHistoryPanel;
import adams.gui.core.BaseMenu;
import adams.gui.core.BasePopupMenu;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.BaseStatusBar;
import adams.gui.core.ConsolePanel;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.core.GUIHelper;
import adams.gui.event.WekaInvestigatorDataEvent;
import adams.gui.goe.GenericArrayEditorDialog;
import adams.gui.goe.GenericObjectEditorDialog;
import adams.gui.goe.WekaGenericObjectEditorPanel;
import adams.gui.tools.wekainvestigator.InvestigatorPanel;
import adams.gui.tools.wekainvestigator.history.AbstractHistoryPopupMenuItem;
import adams.gui.tools.wekainvestigator.tab.classifytab.ResultItem;
import adams.gui.tools.wekainvestigator.tab.classifytab.evaluation.AbstractClassifierEvaluation;
import adams.gui.tools.wekainvestigator.tab.classifytab.output.AbstractOutputGenerator;
import adams.gui.tools.wekainvestigator.tab.classifytab.output.TextStatistics;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;
import weka.classifiers.Classifier;
import weka.classifiers.rules.ZeroR;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

  public static final String KEY_HISTORY = "history";

  /**
   * Customized history panel.
   */
  public static class HistoryPanel
    extends AbstractNamedHistoryPanel<ResultItem> {

    private static final long serialVersionUID = 8740813441072965573L;

    /** the owner. */
    protected ClassifyTab m_Owner;

    /** the file chooser for models. */
    protected BaseFileChooser m_ModelFileChooser;

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
     * Initializes the members.
     */
    @Override
    protected void initialize() {
      ExtensionFileFilter	filter;

      super.initialize();

      filter             = ExtensionFileFilter.getModelFileFilter();
      m_ModelFileChooser = new BaseFileChooser();
      m_ModelFileChooser.addChoosableFileFilter(filter);
      m_ModelFileChooser.setFileFilter(filter);
    }

    /**
     * Removes all entries and payloads.
     */
    public void clear() {
      for (ResultItem item: m_Entries.values())
        item.cleanUp();
      super.clear();
    }

    /**
     * Removes the specified entry.
     *
     * @param name	the name of the entry
     * @return		the entry that was stored under this name or null if
     * 			no entry was stored with this name
     */
    public ResultItem removeEntry(String name) {
      ResultItem	result;

      result = super.removeEntry(name);
      if (result != null)
	result.cleanUp();

      return result;
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

    /**
     * Saves the model to a file.
     *
     * @param item	the result item to use
     */
    protected void saveModel(ResultItem item) {
      int	retVal;

      retVal = m_ModelFileChooser.showSaveDialog(this);
      if (retVal != BaseFileChooser.APPROVE_OPTION)
        return;

      try {
        if (item.hasHeader())
          SerializationHelper.writeAll(
            m_ModelFileChooser.getSelectedFile().getAbsolutePath(),
            new Object[]{item.getModel(), item.getHeader()});
        else
          SerializationHelper.write(
            m_ModelFileChooser.getSelectedFile().getAbsolutePath(),
            item.getModel());
      }
      catch (Exception e) {
        GUIHelper.showErrorMessage(
          this, "Failed to save model to: " + m_ModelFileChooser.getSelectedFile(), e);
      }
    }

    /**
     * Regenerates the output.
     *
     * @param item	the result item to use
     */
    protected void regenerateOutput(final ResultItem item) {
      SwingWorker	worker;

      worker = new SwingWorker() {
        @Override
        protected Object doInBackground() throws Exception {
          item.getTabbedPane().removeAll();
          for (int i = 0; i < m_Owner.getOutputGenerators().length; i++) {
            try {
	      if (m_Owner.getOutputGenerators()[i].canGenerateOutput(item))
		m_Owner.getOutputGenerators()[i].generateOutput(item);
            }
            catch (Exception e) {
              m_Owner.logError("Failed to generate output with " + m_Owner.getOutputGenerators()[i].toCommandLine(), e, "Classifier output generation");
            }
          }
          return null;
        }
      };
      worker.execute();
    }

    /**
     * Prompts the user with a GOE for configuring the output generator and
     * then generates the output.
     *
     * @param generator		the generator to use customize
     * @param item		the result item to use
     */
    protected void generateOutput(AbstractOutputGenerator generator, ResultItem item) {
      GenericObjectEditorDialog		dialog;
      final AbstractOutputGenerator 	current;
      SwingWorker			worker;

      if (getParentDialog() != null)
        dialog = new GenericObjectEditorDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
      else
        dialog = new GenericObjectEditorDialog(getParentFrame(), true);
      dialog.setDefaultCloseOperation(GenericArrayEditorDialog.DISPOSE_ON_CLOSE);
      dialog.setTitle("Configure output");
      dialog.getGOEEditor().setCanChangeClassInDialog(false);
      dialog.getGOEEditor().setClassType(AbstractOutputGenerator.class);
      dialog.setCurrent(generator);
      dialog.pack();
      dialog.setLocationRelativeTo(getParent());
      dialog.setVisible(true);
      if (dialog.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
        return;

      current = (AbstractOutputGenerator) dialog.getCurrent();
      worker = new SwingWorker() {
        @Override
        protected Object doInBackground() throws Exception {
          current.generateOutput(item);
          return null;
        }
      };
      worker.execute();
    }

    /**
     * Generates the right-click menu for the JList.
     *
     * @param e		the event that triggered the popup
     * @return		the generated menu
     * @see		#showPopup(MouseEvent)
     */
    protected BasePopupMenu createPopup(MouseEvent e) {
      BasePopupMenu		result;
      JMenuItem			menuitem;
      BaseMenu 			submenu;
      final int[]		indices;
      Class[]			classes;

      result  = super.createPopup(e);
      indices = getSelectedIndices();

      result.addSeparator();

      menuitem = new JMenuItem("Copy setup");
      menuitem.setEnabled((indices.length == 1));
      menuitem.addActionListener((ActionEvent ae) -> {
        String setup = OptionUtils.getCommandLine(getEntry(indices[0]).getTemplate());
        ClipboardHelper.copyToClipboard(setup);
      });
      result.add(menuitem);

      menuitem = new JMenuItem("Save model...");
      menuitem.setEnabled((indices.length == 1) && getEntry(indices[0]).hasModel());
      menuitem.addActionListener((ActionEvent ae) -> saveModel(getEntry(indices[0])));
      result.add(menuitem);

      menuitem = new JMenuItem("Regenerate output" + (m_Owner.getOutputGenerators().length > 1 ? "s" : ""));
      menuitem.addActionListener((ActionEvent ae) -> regenerateOutput(getEntry(indices[0])));
      result.add(menuitem);

      submenu = new BaseMenu("Additional output");
      classes = ClassLister.getSingleton().getClasses(AbstractOutputGenerator.class);
      for (Class cls: classes) {
        try {
          final AbstractOutputGenerator generator = (AbstractOutputGenerator) cls.newInstance();
          menuitem = new JMenuItem(generator.getTitle());
          menuitem.setEnabled(generator.canGenerateOutput(getEntry(indices[0])));
          menuitem.addActionListener((ActionEvent ae) -> generateOutput(generator, getEntry(indices[0])));
          submenu.add(menuitem);
        }
        catch (Exception ex) {
          ConsolePanel.getSingleton().append(
            Level.SEVERE, "Failed to instantiate output generator: " + cls.getName(), ex);
        }
      }
      submenu.sort();
      result.add(submenu);

      AbstractHistoryPopupMenuItem.updatePopupMenu(
	this, m_Owner, indices, result,
	adams.gui.tools.wekainvestigator.tab.classifytab.history.AbstractHistoryPopupMenuItem.class);

      return result;
    }
  }

  public static final String KEY_LEFTPANELWIDTH = "leftpanelwidth";

  public static final String KEY_CLASSIFIER = "classifier";

  public static final String KEY_EVALUATION = "evaluation";

  public static final String KEY_EVALUATION_PREFIX = "evaluation.";

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

  /** the status bar. */
  protected BaseStatusBar m_StatusBar;

  /** whether the evaluation is currently running. */
  protected Thread m_Worker;

  /** whether the execution is in the process of stopping. */
  protected boolean m_Stopping;

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
    m_Stopping          = false;

    try {
      cmds = OptionUtils.splitOptions(
        props.getProperty("Classify.OutputGenerators", TextStatistics.class.getName()));
    }
    catch (Exception e) {
      ConsolePanel.getSingleton().append(
        Level.SEVERE,
        "Failed to parse output generators:\n" + props.getProperty("Classify.OutputGenerators"), e);
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

    m_ContentPanel.setLayout(new BorderLayout());
    m_ContentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

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
    m_PanelGOE.setPrefix("Classifier ");
    m_PanelGOE.addChangeListener((ChangeEvent e) -> updateButtons());
    m_PanelGOE.moveChooseButton(true);
    panel = new JPanel(new BorderLayout());
    panel.add(m_PanelGOE, BorderLayout.CENTER);
    panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
    m_ContentPanel.add(panel, BorderLayout.NORTH);

    m_SplitPane = new BaseSplitPane(BaseSplitPane.HORIZONTAL_SPLIT);
    m_SplitPane.setOneTouchExpandable(true);
    m_ContentPanel.add(m_SplitPane, BorderLayout.CENTER);
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
    m_ButtonStart.addActionListener((ActionEvent e) -> startExecution());
    m_PanelExecutionButtons.add(m_ButtonStart);

    m_ButtonStop = new JButton("Stop");
    m_ButtonStop.addActionListener((ActionEvent e) -> stopExecution());
    m_PanelExecutionButtons.add(m_ButtonStop);

    // history
    m_History = new HistoryPanel(this);
    m_PanelLeft.add(m_History, BorderLayout.CENTER);

    // status bar
    m_StatusBar = new BaseStatusBar();
    m_ContentPanel.add(m_StatusBar, BorderLayout.SOUTH);
  }

  /**
   * Finishes up the initialization.
   */
  @Override
  protected void finishInit() {
    Properties		props;
    String		evalDefault;
    int			evalIndex;
    int			i;

    super.finishInit();

    props = InvestigatorPanel.getProperties();

    evalDefault = props.getProperty("Classify.Evaluation", "");
    evalIndex   = 0;
    for (i = 0; i < m_ModelEvaluations.getSize(); i++) {
      if (m_ModelEvaluations.getElementAt(i).getClass().getName().equals(evalDefault)) {
	evalIndex = i;
	break;
      }
    }
    m_ComboBoxEvaluations.setSelectedIndex(evalIndex);
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
   *
   * @param e		the event
   */
  public void dataChanged(WekaInvestigatorDataEvent e) {
    int		i;

    for (i = 0; i < m_ModelEvaluations.getSize(); i++) {
      if (e.getType() == WekaInvestigatorDataEvent.ROW_ACTIVATED)
        m_ModelEvaluations.getElementAt(i).activate(e.getRows()[0]);
      else
        m_ModelEvaluations.getElementAt(i).update();
    }

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
  protected void startExecution() {
    if (m_Worker != null)
      return;

    m_Worker = new Thread(() -> {
      m_CurrentClassifier = (Classifier) m_PanelGOE.getCurrent();
      logMessage("Starting evaluation '" + m_CurrentEvaluation.getName() + "' using: " + OptionUtils.getCommandLine(m_CurrentClassifier));
      ResultItem item;
      try {
        item = m_CurrentEvaluation.evaluate(m_CurrentClassifier, m_History);
        logMessage("Finished evaluation '" + m_CurrentEvaluation.getName() + "' using: " + OptionUtils.getCommandLine(m_CurrentClassifier));
      }
      catch (Exception e) {
        logError("Failed to evaluate classifier", e, "Classifier evaluation");
        item = null;
      }
      if (item != null)
        generateOutput(item);
      m_Worker = null;
      updateButtons();
    });
    m_Worker.start();
    updateButtons();
  }

  /**
   * Stops the evaluation.
   */
  protected void stopExecution() {
    SwingWorker		worker;

    if (m_Worker == null)
      return;

    if (m_CurrentEvaluation instanceof Stoppable) {
      logMessage("Stopping evaluation '" + m_CurrentEvaluation.getName() + "' using: " + OptionUtils.getCommandLine(m_CurrentClassifier));
      worker = new SwingWorker() {
	@Override
	protected Object doInBackground() throws Exception {
	  m_Stopping = true;
	  updateButtons();
	  ((Stoppable) m_CurrentEvaluation).stopExecution();
	  return null;
	}

	@Override
	protected void done() {
	  super.done();
	  m_Stopping = false;
	  logMessage("Stopped evaluation '" + m_CurrentEvaluation.getName() + "' using: " + OptionUtils.getCommandLine(m_CurrentClassifier));
	  updateButtons();
	}
      };
      worker.execute();
    }
    else {
      m_Worker.stop();
      logMessage("Stopped evaluation '" + m_CurrentEvaluation.getName() + "' using: " + OptionUtils.getCommandLine(m_CurrentClassifier));
      updateButtons();
    }
  }

  /**
   * Generates the output from the item.
   *
   * @param item	the item to process
   */
  public void generateOutput(ResultItem item) {
    for (int i = 0; i < m_OutputGenerators.length; i++) {
      try {
	m_OutputGenerators[i].generateOutput(item);
      }
      catch (Exception e) {
	logError("Failed to generate output with " + m_OutputGenerators[i].toCommandLine(), e, "Classifier output generation");
      }
    }
  }

  /**
   * Returns whether the tab is busy.
   *
   * @return		true if busy
   */
  public boolean isBusy() {
    return (m_Worker != null) || m_Stopping;
  }

  /**
   * Updates the buttons.
   */
  public void updateButtons() {
    Classifier cls;

    cls = (Classifier) m_PanelGOE.getCurrent();
    m_ButtonStart.setEnabled(!isBusy() && (m_CurrentEvaluation != null) && (m_CurrentEvaluation.canEvaluate(cls) == null));
    m_ButtonStop.setEnabled(isBusy() && !m_Stopping);
  }

  /**
   * Logs the message.
   *
   * @param msg		the log message
   */
  public void logMessage(String msg) {
    super.logMessage(msg);
    m_StatusBar.showStatus(msg);
  }

  /**
   * Logs the exception and also displays an error dialog.
   *
   * @param msg		the log message
   * @param t		the exception
   * @param title	the title for the dialog
   */
  public void logError(String msg, Throwable t, String title) {
    super.logError(msg, t, title);
    m_StatusBar.showStatus(msg);
  }

  /**
   * Logs the error message and also displays an error dialog.
   *
   * @param msg		the error message
   * @param title	the title for the dialog
   */
  public void logError(String msg, String title) {
    super.logError(msg, title);
    m_StatusBar.showStatus(msg);
  }

  /**
   * Displays a message.
   *
   * @param msg		the message to display
   */
  public void showStatus(String msg) {
    super.showStatus(msg);
    m_StatusBar.showStatus(msg);
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

  /**
   * Returns the current output generators.
   *
   * @return		the generators
   */
  public AbstractOutputGenerator[] getOutputGenerators() {
    return m_OutputGenerators;
  }

  /**
   * Returns the objects for serialization.
   *
   * @return		the mapping of the objects to serialize
   */
  protected Map<String,Object> doSerialize() {
    Map<String,Object>			result;
    int					i;
    AbstractClassifierEvaluation 	eval;

    result = super.doSerialize();
    result.put(KEY_LEFTPANELWIDTH, m_SplitPane.getDividerLocation());
    result.put(KEY_CLASSIFIER, OptionUtils.getCommandLine(m_PanelGOE.getCurrent()));
    result.put(KEY_EVALUATION, m_ComboBoxEvaluations.getSelectedIndex());
    for (i = 0; i < m_ModelEvaluations.getSize(); i++) {
      eval = m_ModelEvaluations.getElementAt(i);
      result.put(KEY_EVALUATION_PREFIX + eval.getName(), eval.serialize());
    }
    result.put(KEY_HISTORY, m_History.serialize());

    return result;
  }

  /**
   * Restores the objects.
   *
   * @param data	the data to restore
   * @param errors	for storing errors
   */
  protected void doDeserialize(Map<String,Object> data, MessageCollection errors) {
    Map<String,Object> 			evaldata;
    int					i;
    AbstractClassifierEvaluation 	eval;

    super.doDeserialize(data, errors);
    if (data.containsKey(KEY_LEFTPANELWIDTH))
      m_SplitPane.setDividerLocation((int) data.get(KEY_LEFTPANELWIDTH));
    if (data.containsKey(KEY_CLASSIFIER)) {
      try {
        m_CurrentClassifier = (Classifier) OptionUtils.forAnyCommandLine(Classifier.class, (String) data.get(KEY_CLASSIFIER));
        m_PanelGOE.setCurrent(m_CurrentClassifier);
      }
      catch (Exception e) {
        errors.add("Failed to restore classifier: " + data.get(KEY_CLASSIFIER), e);
      }
    }
    if (data.containsKey(KEY_EVALUATION))
      m_ComboBoxEvaluations.setSelectedIndex((int) data.get(KEY_EVALUATION));
    for (i = 0; i < m_ModelEvaluations.getSize(); i++) {
      eval = m_ModelEvaluations.getElementAt(i);
      if (data.containsKey(KEY_EVALUATION_PREFIX + eval.getName())) {
	evaldata = (Map<String,Object>) data.get(KEY_EVALUATION_PREFIX + eval.getName());
	eval.deserialize(evaldata, errors);
      }
    }
    if (data.containsKey(KEY_HISTORY))
      m_History.deserialize(data.get(KEY_HISTORY), errors);
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    super.cleanUp();
    m_History.clear();
  }
}
