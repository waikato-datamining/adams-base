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
 * ExperimentTab.java
 * Copyright (C) 2024 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab;

import adams.core.ClassLister;
import adams.core.MessageCollection;
import adams.core.Properties;
import adams.core.Stoppable;
import adams.core.option.OptionUtils;
import adams.flow.container.WekaExperimentContainer;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.chooser.WekaFileChooser;
import adams.gui.core.AbstractNamedHistoryPanel;
import adams.gui.core.AbstractNamedHistoryPanel.HistoryEntryToolTipProvider;
import adams.gui.core.BaseButton;
import adams.gui.core.BaseComboBox;
import adams.gui.core.BaseMenu;
import adams.gui.core.BasePopupMenu;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.BaseStatusBar;
import adams.gui.core.ConsolePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.ImageManager;
import adams.gui.event.WekaInvestigatorDataEvent;
import adams.gui.goe.Favorites;
import adams.gui.goe.Favorites.FavoriteSelectionEvent;
import adams.gui.goe.GenericArrayEditorDialog;
import adams.gui.goe.GenericObjectEditorDialog;
import adams.gui.goe.GenericObjectEditorPanel;
import adams.gui.tools.wekainvestigator.InvestigatorPanel;
import adams.gui.tools.wekainvestigator.job.InvestigatorTabJob;
import adams.gui.tools.wekainvestigator.output.RunInformationHelper;
import adams.gui.tools.wekainvestigator.tab.experimenttab.ResultItem;
import adams.gui.tools.wekainvestigator.tab.experimenttab.output.AbstractOutputGenerator;
import adams.gui.tools.wekainvestigator.tab.experimenttab.output.TextOutput;
import adams.gui.tools.wekainvestigator.tab.experimenttab.setup.AbstractExperimentSetup;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;
import weka.classifiers.Classifier;
import weka.classifiers.rules.ZeroR;
import weka.core.Instances;
import weka.core.converters.AbstractFileSaver;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
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
import java.util.Set;
import java.util.logging.Level;

/**
 * Tab for running experiment on selected dataset/classifier.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ExperimentTab
  extends AbstractInvestigatorTab {

  private static final long serialVersionUID = -4106630131554796889L;

  public static final String KEY_HISTORY = "history";

  /** the key for the output generators. */
  public final static String KEY_OUTPUTGENERATORS = "output generators";

  /**
   * Customized history panel.
   */
  public static class HistoryPanel
    extends AbstractNamedHistoryPanel<ResultItem>
    implements HistoryEntryToolTipProvider<ResultItem> {

    private static final long serialVersionUID = 8740813441072965573L;

    /** the owner. */
    protected ExperimentTab m_Owner;

    /** the file chooser for the results. */
    protected WekaFileChooser m_ResultsFileChooser;

    /**
     * Initializes the history.
     *
     * @param owner	the owning tab
     */
    public HistoryPanel(ExperimentTab owner) {
      super();
      m_Owner = owner;
      setAllowRemove(true);
      setAllowRename(true);
      setButtonPanelVisible(true);
    }

    /**
     * Initializes the members.
     */
    @Override
    protected void initialize() {
      super.initialize();

      m_ResultsFileChooser = new WekaFileChooser();
      m_ResultsFileChooser.setDialogTitle("Experiment data");
    }

    /**
     * Gets called when a tooltip needs to get generated.
     *
     * @param history	the history
     * @param index 	the index in the history
     * @return		the generated tool tip, null if not available
     */
    public String createHistoryEntryToolTip(AbstractNamedHistoryPanel<ResultItem> history, int index) {
      String 		result;
      ResultItem 	item;

      result = null;
      item   = history.getEntry(index);
      if (item.hasRunInformation())
	result = "<html>" + RunInformationHelper.toHTML(item.getRunInformation().toSpreadSheet()) + "</html>";

      return result;
    }

    /**
     * Sets whether to show tool tips.
     *
     * @param value	true if to show
     */
    public void setToolTipsEnabled(boolean value) {
      if (value)
	setHistoryEntryToolTipProvider(this);
      else
	setHistoryEntryToolTipProvider(null);
    }

    /**
     * Returns whether to show tool tips.
     *
     * @return		true if to show
     */
    public boolean getToolTipsEnabled() {
      return (getHistoryEntryToolTipProvider() != null);
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
     * Saves the results to a file.
     *
     * @param item	the result item to use
     */
    protected void saveExperimentData(ResultItem item) {
      int		retVal;
      AbstractFileSaver	saver;

      retVal = m_ResultsFileChooser.showSaveDialog(this);
      if (retVal != BaseFileChooser.APPROVE_OPTION)
        return;

      try {
	saver = m_ResultsFileChooser.getWriter();
	saver.setFile(m_ResultsFileChooser.getSelectedFile());
	saver.setInstances(item.getExperiment().getValue(WekaExperimentContainer.VALUE_INSTANCES, Instances.class));
	saver.writeBatch();
      }
      catch (Exception e) {
        GUIHelper.showErrorMessage(
          this, "Failed to save experiment data to: " + m_ResultsFileChooser.getSelectedFile(), e);
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
     * returns it if accepted.
     *
     * @param generator		the generator to customize
     * @return			the customized generator, null if cancelled
     */
    protected AbstractOutputGenerator configureOutput(AbstractOutputGenerator generator) {
      GenericObjectEditorDialog		dialog;

      if (getParentDialog() != null)
        dialog = new GenericObjectEditorDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
      else
        dialog = new GenericObjectEditorDialog(getParentFrame(), true);
      dialog.setDefaultCloseOperation(GenericArrayEditorDialog.DISPOSE_ON_CLOSE);
      dialog.setTitle("Configure output");
      dialog.setUISettingsPrefix(AbstractOutputGenerator.class);
      dialog.getGOEEditor().setCanChangeClassInDialog(false);
      dialog.getGOEEditor().setClassType(AbstractOutputGenerator.class);
      dialog.setCurrent(generator);
      dialog.pack();
      dialog.setLocationRelativeTo(getParent());
      dialog.setVisible(true);
      if (dialog.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
        return null;
      else
        return (AbstractOutputGenerator) dialog.getCurrent();
    }

    /**
     * Prompts the user with a GOE for configuring the output generator and
     * then generates the output.
     *
     * @param generator		the generator to use customize
     * @param item		the result item to use
     */
    protected void generateOutput(AbstractOutputGenerator generator, ResultItem item) {
      final AbstractOutputGenerator 	current;
      SwingWorker			worker;

      current = configureOutput(generator);
      if (current == null)
	return;

      worker = new SwingWorker() {
        @Override
        protected Object doInBackground() throws Exception {
          String msg = current.generateOutput(item);
	  if (msg != null)
	    m_Owner.logError(
	      "Failed to generate output using: " + current.toCommandLine() + "\n" + msg,
	      "Error generating output");
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

      menuitem = new JMenuItem("Save experiment data...");
      menuitem.setEnabled((indices.length == 1) && getEntry(indices[0]).hasExperiment());
      menuitem.addActionListener((ActionEvent ae) -> saveExperimentData(getEntry(indices[0])));
      result.add(menuitem);

      menuitem = new JMenuItem("Regenerate output" + (m_Owner.getOutputGenerators().length > 1 ? "s" : ""));
      menuitem.setEnabled((indices.length >= 1));
      menuitem.addActionListener((ActionEvent ae) -> { for (int index: indices) regenerateOutput(getEntry(index)); });
      result.add(menuitem);

      submenu = new BaseMenu("Additional output");
      classes = ClassLister.getSingleton().getClasses(AbstractOutputGenerator.class);
      for (Class cls: classes) {
        try {
          final AbstractOutputGenerator generator = (AbstractOutputGenerator) cls.getDeclaredConstructor().newInstance();
          menuitem = new JMenuItem(generator.getTitle());
          menuitem.setEnabled((indices.length == 1) && generator.canGenerateOutput(getEntry(indices[0])));
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

      menuitem = new JMenuItem("Export output...");
      menuitem.setEnabled((indices.length == 1) && getEntry(indices[0]).getTabbedPane().getTabCount() > 0);
      menuitem.addActionListener((ActionEvent ae) -> getEntry(indices[0]).getTabbedPane().export());
      result.add(menuitem);

      return result;
    }
  }

  public static final String KEY_LEFTPANELWIDTH = "leftpanelwidth";

  public static final String KEY_CLASSIFIER = "classifier";

  public static final String KEY_EVALUATION = "evaluation";

  public static final String KEY_EVALUATION_PREFIX = "evaluation.";

  /** the GOe with the classifier. */
  protected GenericObjectEditorPanel m_PanelGOE;

  /** the split pane for left/right panels. */
  protected BaseSplitPane m_SplitPane;

  /** the panel on the left-hand side. */
  protected JPanel m_PanelLeft;

  /** the panel on the right-hand side (displays results). */
  protected JPanel m_PanelRight;

  /** the panel with the evaluation. */
  protected JPanel m_PanelEvaluation;

  /** the combobox with the available setups. */
  protected BaseComboBox<AbstractExperimentSetup> m_ComboBoxSetups;

  /** the model with the available setups. */
  protected DefaultComboBoxModel<AbstractExperimentSetup> m_ModelSetups;

  /** the panel for the execution setup to be embedded in. */
  protected JPanel m_PanelSetup;

  /** the current p. */
  protected AbstractExperimentSetup m_CurrentSetup;

  /** the current classifier. */
  protected Classifier m_CurrentClassifier;

  /** the button for editing the output generators. */
  protected BaseButton m_ButtonOutputGenerators;

  /** the button for the output generator favorites. */
  protected BaseButton m_ButtonOutputGeneratorsFavorites;

  /** the panel with the buttons. */
  protected JPanel m_PanelExecutionButtons;

  /** the start button. */
  protected BaseButton m_ButtonStart;

  /** the stop button. */
  protected BaseButton m_ButtonStop;

  /** the history. */
  protected HistoryPanel m_History;

  /** the status bar. */
  protected BaseStatusBar m_StatusBar;

  /** whether the execution is in the process of stopping. */
  protected boolean m_Stopping;

  /** the output generators to use. */
  protected AbstractOutputGenerator[] m_OutputGenerators;

  /**
   * Initializes the members.
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

    m_CurrentSetup      = null;
    m_CurrentClassifier = null;
    m_Stopping          = false;

    try {
      cmds = OptionUtils.splitOptions(
        props.getProperty("Experiment.OutputGenerators", TextOutput.class.getName()));
    }
    catch (Exception e) {
      ConsolePanel.getSingleton().append(
        Level.SEVERE,
        "Failed to parse output generators:\n" + props.getProperty("Experiment.OutputGenerators"), e);
      cmds = new String[]{TextOutput.class.getName()};
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
    m_OutputGenerators = generators.toArray(new AbstractOutputGenerator[0]);
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    Classifier				cls;
    Class[]				classes;
    AbstractExperimentSetup setup;
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
          "Experiment.Classifier", ZeroR.class.getName()));
    }
    catch (Exception e) {
      cls = new ZeroR();
    }
    m_PanelGOE = new GenericObjectEditorPanel(Classifier.class, cls, true);
    m_PanelGOE.setPrefix("Classifier ");
    m_PanelGOE.addChangeListener((ChangeEvent e) -> updateButtons());
    m_PanelGOE.moveChooseButton(true);
    panel = new JPanel(new BorderLayout());
    panel.add(m_PanelGOE, BorderLayout.CENTER);
    panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
    m_ContentPanel.add(panel, BorderLayout.NORTH);

    m_SplitPane = new BaseSplitPane(BaseSplitPane.HORIZONTAL_SPLIT);
    m_SplitPane.setOneTouchExpandable(true);
    m_SplitPane.setUISettingsParameters(getClass(), "HorizontalDivider");
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

    m_ModelSetups = new DefaultComboBoxModel<>();
    classes       = AbstractExperimentSetup.getSetups();
    for (Class c: classes) {
      try {
        setup = (AbstractExperimentSetup) c.getDeclaredConstructor().newInstance();
        setup.setOwner(this);
        m_ModelSetups.addElement(setup);
      }
      catch (Exception e) {
        ConsolePanel.getSingleton().append(Level.SEVERE, "Failed to instantiate classifier evaluation: " + c.getName(), e);
      }
    }
    m_ComboBoxSetups = new BaseComboBox<>(m_ModelSetups);
    m_ComboBoxSetups.addActionListener((ActionEvent e) -> {
      if (m_ComboBoxSetups.getSelectedIndex() == -1)
        return;
      m_CurrentSetup = m_ComboBoxSetups.getSelectedItem();
      m_PanelSetup.removeAll();
      m_PanelSetup.add(m_CurrentSetup.getPanel());
      m_CurrentSetup.update();
      m_PanelSetup.invalidate();
      m_PanelSetup.revalidate();
      m_PanelSetup.doLayout();
    });
    panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
    panel.add(m_ComboBoxSetups, BorderLayout.CENTER);
    m_PanelEvaluation.add(panel, BorderLayout.NORTH);

    // setup
    m_PanelSetup = new JPanel(new BorderLayout());
    m_PanelEvaluation.add(m_PanelSetup, BorderLayout.CENTER);

    // all buttons
    buttonsAll = new JPanel(new GridLayout(2, 1));
    m_PanelEvaluation.add(buttonsAll, BorderLayout.SOUTH);

    // output generators
    m_ButtonOutputGenerators = new BaseButton("...");
    m_ButtonOutputGenerators.addActionListener((ActionEvent) -> editOutputGenerators());
    label = new JLabel("Output generators");
    label.setLabelFor(m_ButtonOutputGenerators);
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(label);
    panel.add(m_ButtonOutputGenerators);
    buttonsAll.add(panel);

    m_ButtonOutputGeneratorsFavorites = new BaseButton(ImageManager.getIcon("favorite.gif"));
    m_ButtonOutputGeneratorsFavorites.addActionListener((ActionEvent) -> showOutputGeneratorsFavorites());
    panel.add(m_ButtonOutputGeneratorsFavorites);

    // start/stop buttons
    m_PanelExecutionButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
    buttonsAll.add(m_PanelExecutionButtons);

    m_ButtonStart = new BaseButton("Start");
    m_ButtonStart.addActionListener((ActionEvent e) -> startExecution());
    m_PanelExecutionButtons.add(m_ButtonStart);

    m_ButtonStop = new BaseButton("Stop");
    m_ButtonStop.addActionListener((ActionEvent e) -> stopExecution());
    m_PanelExecutionButtons.add(m_ButtonStop);

    // history
    m_History = new HistoryPanel(this);
    m_History.setToolTipsEnabled(props.getBoolean("General.ResultHistoryToolTips", true));
    m_PanelLeft.add(m_History, BorderLayout.CENTER);

    // status bar
    m_StatusBar = new BaseStatusBar();
    m_StatusBar.setExpiryTime(true, 60);
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
    for (i = 0; i < m_ModelSetups.getSize(); i++) {
      if (m_ModelSetups.getElementAt(i).getClass().getName().equals(evalDefault)) {
	evalIndex = i;
	break;
      }
    }
    m_ComboBoxSetups.setSelectedIndex(evalIndex);
  }

  /**
   * Returns the title of this table.
   *
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Experiment";
  }

  /**
   * Returns the icon name for the tab icon.
   *
   * @return		the icon name, null if not available
   */
  public String getTabIcon() {
    return "experiment.png";
  }

  /**
   * Notifies the tab that the data changed.
   *
   * @param e		the event
   */
  public void dataChanged(WekaInvestigatorDataEvent e) {
    int		i;

    for (i = 0; i < m_ModelSetups.getSize(); i++) {
      if (e.getType() == WekaInvestigatorDataEvent.ROW_ACTIVATED)
        m_ModelSetups.getElementAt(i).activate(e.getRows()[0]);
      else
        m_ModelSetups.getElementAt(i).update();
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
   * Shows the favorites popup menu for the output generators.
   */
  protected void showOutputGeneratorsFavorites() {
    JPopupMenu menu;

    menu = new JPopupMenu();
    Favorites.getSingleton().addFavoritesMenuItems(
      menu,
      AbstractOutputGenerator[].class,
      getOutputGenerators(),
      (FavoriteSelectionEvent fe) -> setOutputGenerators((AbstractOutputGenerator[]) fe.getFavorite().getObject()));
    menu.show(m_ButtonOutputGeneratorsFavorites, 0, m_ButtonOutputGeneratorsFavorites.getHeight());
  }

  /**
   * Hook method that gets called after successfully starting a job.
   *
   * @param job		the job that got started
   */
  @Override
  protected void postStartExecution(InvestigatorTabJob job) {
    super.postStartExecution(job);
    updateButtons();
  }

  /**
   * Starts the evaluation.
   */
  protected void startExecution() {
    m_CurrentClassifier = (Classifier) m_PanelGOE.getCurrent();
    startExecution(new InvestigatorTabJob(this, "Starting evaluation '" + m_CurrentSetup.getName() + "' using: " + OptionUtils.getCommandLine(m_CurrentClassifier)) {
      @Override
      protected void doRun() {
	ResultItem item;
	try {
          item = m_CurrentSetup.init(m_CurrentClassifier);
          m_CurrentSetup.addToHistory(m_History, item);
          String entry = item.getName();
	  m_CurrentSetup.execute(m_CurrentClassifier, item);
	  if (!entry.equals(item.getName()))
	    m_History.renameEntry(entry, item.getName());
	}
	catch (Exception e) {
	  logError("Failed to evaluate classifier", e, "Classifier evaluation");
	  item = null;
	}
	if (item != null) {
          generateOutput(item);
          if (item.hasNestedItems()) {
            for (String name: item.nestedItemNames()) {
              ResultItem nested = (ResultItem) item.getNestedItem(name);
              m_CurrentSetup.addToHistory(m_History, nested);
              generateOutput(nested);
            }
          }
        }
      }
    });
  }

  /**
   * Hook method that gets called after finishing a job.
   */
  @Override
  protected void postExecutionFinished() {
    super.postExecutionFinished();
    logMessage("Finished evaluation '" + m_CurrentSetup.getName() + "' using: " + OptionUtils.getCommandLine(m_CurrentClassifier));
    updateButtons();
  }

  /**
   * Hook method that gets called after stopping a job.
   */
  @Override
  protected void postStopExecution() {
    super.postStopExecution();
    logMessage("Stopped evaluation '" + m_CurrentSetup.getName() + "' using: " + OptionUtils.getCommandLine(m_CurrentClassifier));
    updateButtons();
  }

  /**
   * Stops the evaluation.
   */
  public void stopExecution() {
    SwingWorker		worker;

    if (m_Worker == null)
      return;

    if (m_CurrentSetup != null) {
      logMessage("Stopping evaluation '" + m_CurrentSetup.getName() + "' using: " + OptionUtils.getCommandLine(m_CurrentClassifier));
      worker = new SwingWorker() {
	@Override
	protected Object doInBackground() throws Exception {
	  m_Stopping = true;
	  updateButtons();
	  ((Stoppable) m_CurrentSetup).stopExecution();
	  return null;
	}

	@Override
	protected void done() {
	  super.done();
	  m_Stopping = false;
	  logMessage("Stopped evaluation '" + m_CurrentSetup.getName() + "' using: " + OptionUtils.getCommandLine(m_CurrentClassifier));
	  updateButtons();
	}
      };
      worker.execute();
    }
    else {
      super.stopExecution();
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
        if (m_OutputGenerators[i].canGenerateOutput(item))
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
    return super.isBusy() || m_Stopping;
  }

  /**
   * Updates the buttons.
   */
  public void updateButtons() {
    Classifier 	cls;
    String	msg;

    cls = (Classifier) m_PanelGOE.getCurrent();
    msg = m_CurrentSetup.canExecute(cls);
    m_ButtonStart.setEnabled(!isBusy() && (m_CurrentSetup != null) && (msg == null));
    m_ButtonStart.setToolTipText(GUIHelper.processTipText(msg));
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
   * Sets the output generators to use.
   *
   * @param value	the generators
   */
  public void setOutputGenerators(AbstractOutputGenerator[] value) {
    m_OutputGenerators = value;
  }

  /**
   * Returns the objects for serialization.
   *
   * @param options 	what to serialize
   * @return		the mapping of the objects to serialize
   */
  protected Map<String,Object> doSerialize(Set<SerializationOption> options) {
    Map<String,Object>			result;
    int					i;
    AbstractExperimentSetup 	eval;

    result = super.doSerialize(options);
    if (options.contains(SerializationOption.GUI))
      result.put(KEY_LEFTPANELWIDTH, m_SplitPane.getDividerLocation());
    if (options.contains(SerializationOption.PARAMETERS)) {
      result.put(KEY_CLASSIFIER, OptionUtils.getCommandLine(m_PanelGOE.getCurrent()));
      result.put(KEY_EVALUATION, m_ComboBoxSetups.getSelectedIndex());
      result.put(KEY_OUTPUTGENERATORS, OptionUtils.getCommandLines(m_OutputGenerators));
    }
    for (i = 0; i < m_ModelSetups.getSize(); i++) {
      eval = m_ModelSetups.getElementAt(i);
      result.put(KEY_EVALUATION_PREFIX + eval.getName(), eval.serialize(options));
    }
    if (options.contains(SerializationOption.HISTORY))
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
    AbstractExperimentSetup 	eval;

    super.doDeserialize(data, errors);
    if (data.containsKey(KEY_LEFTPANELWIDTH))
      m_SplitPane.setDividerLocation(((Number) data.get(KEY_LEFTPANELWIDTH)).intValue());
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
      m_ComboBoxSetups.setSelectedIndex(((Number) data.get(KEY_EVALUATION)).intValue());
    for (i = 0; i < m_ModelSetups.getSize(); i++) {
      eval = m_ModelSetups.getElementAt(i);
      if (data.containsKey(KEY_EVALUATION_PREFIX + eval.getName())) {
	evaldata = (Map<String,Object>) data.get(KEY_EVALUATION_PREFIX + eval.getName());
	eval.deserialize(evaldata, errors);
      }
    }
    if (data.containsKey(KEY_HISTORY))
      m_History.deserialize(data.get(KEY_HISTORY), errors);
    if (data.containsKey(KEY_OUTPUTGENERATORS)) {
      try {
	m_OutputGenerators = (AbstractOutputGenerator[]) OptionUtils.forCommandLines(AbstractOutputGenerator.class, toParamsArray(data.get(KEY_OUTPUTGENERATORS)));
      }
      catch (Exception e) {
        errors.add("Failed to restore output generators!", e);
      }
    }
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    super.cleanUp();
    m_History.clear();
  }
}
