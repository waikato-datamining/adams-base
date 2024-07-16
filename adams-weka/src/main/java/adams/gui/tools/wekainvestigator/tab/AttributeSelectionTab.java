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
 * AttributeSelectionTab.java
 * Copyright (C) 2016-2024 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab;

import adams.core.ClassLister;
import adams.core.MessageCollection;
import adams.core.Properties;
import adams.core.option.OptionUtils;
import adams.gui.chooser.WekaFileChooser;
import adams.gui.core.AbstractNamedHistoryPanel;
import adams.gui.core.AbstractNamedHistoryPanel.HistoryEntryToolTipProvider;
import adams.gui.core.BaseButton;
import adams.gui.core.BaseComboBox;
import adams.gui.core.BaseMenu;
import adams.gui.core.BasePopupMenu;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.BaseStatusBar;
import adams.gui.core.ConsolePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.ParameterPanel;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.event.WekaInvestigatorDataEvent;
import adams.gui.goe.GenericArrayEditorDialog;
import adams.gui.goe.GenericObjectEditorDialog;
import adams.gui.goe.GenericObjectEditorPanel;
import adams.gui.tools.wekainvestigator.InvestigatorPanel;
import adams.gui.tools.wekainvestigator.data.MemoryContainer;
import adams.gui.tools.wekainvestigator.history.AbstractHistoryPopupMenuItem;
import adams.gui.tools.wekainvestigator.job.InvestigatorTabJob;
import adams.gui.tools.wekainvestigator.output.RunInformationHelper;
import adams.gui.tools.wekainvestigator.tab.attseltab.ResultItem;
import adams.gui.tools.wekainvestigator.tab.attseltab.evaluation.AbstractAttributeSelectionEvaluation;
import adams.gui.tools.wekainvestigator.tab.attseltab.output.AbstractOutputGenerator;
import adams.gui.tools.wekainvestigator.tab.attseltab.output.TextStatistics;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.core.Instances;
import weka.core.converters.AbstractFileSaver;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

/**
 * Tab for attribute selection.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class AttributeSelectionTab
  extends AbstractInvestigatorTab {

  private static final long serialVersionUID = -4106630131554796889L;

  public static final String KEY_SEARCH = "search";

  public static final String KEY_EVALUATOR = "evaluator";

  public static final String KEY_EVALUATION = "evaluation";

  public static final String KEY_EVALUATION_PREFIX = "evaluation.";

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
    protected AttributeSelectionTab m_Owner;

    /** the file chooser for models. */
    protected WekaFileChooser m_DatasetFileChooser;

    /**
     * Initializes the history.
     *
     * @param owner	the owning tab
     */
    public HistoryPanel(AttributeSelectionTab owner) {
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

      m_DatasetFileChooser = new WekaFileChooser();
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
     * Generates the reduced data.
     *
     * @param item	the result item to use
     * @return		the reduced data
     */
    protected Instances generateReducedData(ResultItem item) {
      Instances		result;

      try {
	result = item.getAttributeSelection().reduceDimensionality(item.getFull());
      }
      catch (Exception e) {
	m_Owner.logError("Failed to reduce data!", e, "Data reduction");
	result = null;
      }

      return result;
    }

    /**
     * Saves the reduced data to a file.
     *
     * @param item	the result item to use
     */
    protected void saveReducedData(ResultItem item) {
      Instances		reduced;
      int		retVal;
      AbstractFileSaver	writer;
      File		file;

      reduced = generateReducedData(item);
      if (reduced == null)
	return;

      retVal = m_DatasetFileChooser.showSaveDialog(m_Owner);
      if (retVal != WekaFileChooser.APPROVE_OPTION)
	return;

      file   = m_DatasetFileChooser.getSelectedFile();
      writer = m_DatasetFileChooser.getWriter();
      writer.setInstances(reduced);
      try {
	writer.setFile(file);
	writer.writeBatch();
      }
      catch (Exception e) {
	m_Owner.logError("Failed to save reduce data to: " + file, e, "Save reduced data");
      }
    }

    /**
     * Makes the reduced data available as data container.
     *
     * @param item	the result item to use
     */
    protected void useReducedData(ResultItem item) {
      Instances		reduced;
      MemoryContainer	cont;

      reduced = generateReducedData(item);
      if (reduced == null)
	return;

      cont = new MemoryContainer(reduced);
      SwingUtilities.invokeLater(() -> {
	m_Owner.getData().add(cont);
	m_Owner.logMessage("Added reduced data: " + reduced.relationName());
	m_Owner.fireDataChange(new WekaInvestigatorDataEvent(m_Owner.getOwner(), WekaInvestigatorDataEvent.ROWS_ADDED, m_Owner.getData().size() - 1));
      });
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
	      m_Owner.getOutputGenerators()[i].generateOutput(item);
	    }
	    catch (Exception e) {
	      m_Owner.logError("Failed to generate output with " + m_Owner.getOutputGenerators()[i].toCommandLine(), e, "Clusterer output generation");
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
      final AbstractOutputGenerator current;
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
     * Prompts the user with a GOE for configuring the output generator and
     * then displays the generated outputs side by side.
     *
     * @param generator		the generator to use customize
     * @param indices		the indices of the items to display
     */
    protected void compareOutput(AbstractOutputGenerator generator, final int[] indices) {
      final AbstractOutputGenerator current;
      SwingWorker			worker;

      current = configureOutput(generator);
      if (current == null)
	return;

      worker = new SwingWorker() {
        @Override
        protected Object doInBackground() throws Exception {
	  List<JComponent> comps = new ArrayList<>();
	  List<String> labels = new ArrayList<>();
	  MessageCollection errors = new MessageCollection();
	  for (int index: indices) {
	    ResultItem item = getEntry(index);
	    JComponent comp = current.createOutput(item, errors);
	    if (comp != null) {
	      comps.add(comp);
	      labels.add(getEntryName(index));
	    }
	  }
	  JPanel panel = null;
	  if (comps.size() > 0) {
	    panel = new JPanel(new GridLayout(1, comps.size()));
	    for (int i = 0; i < comps.size(); i++) {
	      JPanel subPanel = new JPanel(new BorderLayout());
	      JLabel label = new JLabel(labels.get(i));
	      subPanel.add(label, BorderLayout.NORTH);
	      subPanel.add(comps.get(i), BorderLayout.CENTER);
	      panel.add(subPanel);
	    }
	  }
	  if (!errors.isEmpty())
	    m_Owner.logError(
	      "Failed to generate output using: " + current.toCommandLine() + "\n" + errors,
	      "Error generating output");
	  if (panel != null) {
	    ApprovalDialog dialog;
	    if (m_Owner.getParentDialog() != null)
	      dialog = new ApprovalDialog(m_Owner.getParentDialog(), ModalityType.MODELESS);
	    else
	      dialog = new ApprovalDialog(m_Owner.getParentFrame(), false);
	    dialog.setDefaultCloseOperation(ApprovalDialog.DISPOSE_ON_CLOSE);
	    dialog.setTitle("Compare output");
	    dialog.getContentPane().add(new BaseScrollPane(panel), BorderLayout.CENTER);
	    dialog.setSize(GUIHelper.makeWider(GUIHelper.getDefaultDialogDimension()));
	    dialog.setLocationRelativeTo(dialog.getParent());
	    dialog.setVisible(true);
	  }
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

      menuitem = new JMenuItem("Use evaluator");
      menuitem.setEnabled((indices.length == 1));
      menuitem.addActionListener((ActionEvent ae) -> m_Owner.getPanelEvaluator().setCurrent(getEntry(indices[0]).getEvaluator()));
      result.add(menuitem);

      menuitem = new JMenuItem("Copy evaluator");
      menuitem.setEnabled((indices.length == 1));
      menuitem.addActionListener((ActionEvent ae) -> {
        String setup = OptionUtils.getCommandLine(getEntry(indices[0]).getEvaluator());
        ClipboardHelper.copyToClipboard(setup);
      });
      result.add(menuitem);

      result.addSeparator();

      menuitem = new JMenuItem("Use search");
      menuitem.setEnabled((indices.length == 1));
      menuitem.addActionListener((ActionEvent ae) -> m_Owner.getPanelSearch().setCurrent(getEntry(indices[0]).getSearch()));
      result.add(menuitem);

      menuitem = new JMenuItem("Copy search");
      menuitem.setEnabled((indices.length == 1));
      menuitem.addActionListener((ActionEvent ae) -> {
        String setup = OptionUtils.getCommandLine(getEntry(indices[0]).getSearch());
        ClipboardHelper.copyToClipboard(setup);
      });
      result.add(menuitem);

      result.addSeparator();

      menuitem = new JMenuItem("Save reduced data...");
      menuitem.setEnabled((indices.length == 1) && !getEntry(indices[0]).isCrossValidation() && getEntry(indices[0]).hasFull());
      menuitem.addActionListener((ActionEvent ae) -> saveReducedData(getEntry(indices[0])));
      result.add(menuitem);

      menuitem = new JMenuItem("Use reduced data...");
      menuitem.setEnabled((indices.length == 1) && !getEntry(indices[0]).isCrossValidation() && getEntry(indices[0]).hasFull());
      menuitem.addActionListener((ActionEvent ae) -> useReducedData(getEntry(indices[0])));
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

      submenu = new BaseMenu("Compare output");
      submenu.setEnabled((indices.length >= 2));
      if (indices.length >= 2) {
	classes = ClassLister.getSingleton().getClasses(AbstractOutputGenerator.class);
	for (Class cls : classes) {
	  try {
	    final AbstractOutputGenerator generator = (AbstractOutputGenerator) cls.getDeclaredConstructor().newInstance();
	    menuitem = new JMenuItem(generator.getTitle());
	    menuitem.setEnabled(generator.canGenerateOutput(getEntry(indices[0])));
	    menuitem.addActionListener((ActionEvent ae) -> compareOutput(generator, indices));
	    submenu.add(menuitem);
	  }
	  catch (Exception ex) {
	    ConsolePanel.getSingleton().append(
	      Level.SEVERE, "Failed to instantiate output generator: " + cls.getName(), ex);
	  }
	}
	submenu.sort();
      }
      result.add(submenu);

      menuitem = new JMenuItem("Export output...");
      menuitem.setEnabled((indices.length == 1) && getEntry(indices[0]).getTabbedPane().getTabCount() > 0);
      menuitem.addActionListener((ActionEvent ae) -> getEntry(indices[0]).getTabbedPane().export());
      result.add(menuitem);

      AbstractHistoryPopupMenuItem.updatePopupMenu(
	this, m_Owner, indices, result,
	adams.gui.tools.wekainvestigator.tab.attseltab.history.AbstractHistoryPopupMenuItem.class);

      return result;
    }
  }

  public static final String KEY_LEFTPANELWIDTH = "leftpanelwidth";

  /** the GOe with the evaluator. */
  protected GenericObjectEditorPanel m_PanelEvaluator;

  /** the GOe with the search. */
  protected GenericObjectEditorPanel m_PanelSearch;

  /** the split pane for left/right panels. */
  protected BaseSplitPane m_SplitPane;

  /** the panel on the left-hand side. */
  protected JPanel m_PanelLeft;

  /** the panel on the right-hand side (displays results). */
  protected JPanel m_PanelRight;

  /** the panel with the evaluation. */
  protected JPanel m_PanelEvaluation;

  /** the combobox with the available evaluations. */
  protected BaseComboBox<AbstractAttributeSelectionEvaluation> m_ComboBoxEvaluations;

  /** the model with the available evaluations. */
  protected DefaultComboBoxModel<AbstractAttributeSelectionEvaluation> m_ModelEvaluations;

  /** the panel for the evaluation setup to be embedded in. */
  protected JPanel m_PanelEvaluationSetup;

  /** the current evaluation. */
  protected AbstractAttributeSelectionEvaluation m_CurrentEvaluation;

  /** the current evaluator. */
  protected ASEvaluation m_CurrentEvaluator;

  /** the current search. */
  protected ASSearch m_CurrentSearch;

  /** the button for editing the output generators. */
  protected BaseButton m_ButtonOutputGenerators;

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
    m_CurrentEvaluator  = null;
    m_CurrentSearch     = null;

    try {
      cmds = OptionUtils.splitOptions(
	props.getProperty("AttributeSelection.OutputGenerators", TextStatistics.class.getName()));
    }
    catch (Exception e) {
      ConsolePanel.getSingleton().append(
	Level.SEVERE,
	"Failed to parse output generators:\n" + props.getProperty("AttributeSelection.OutputGenerators"), e);
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
    m_OutputGenerators = generators.toArray(new AbstractOutputGenerator[0]);
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    ASEvaluation 				evaluator;
    ASSearch					search;
    Class[]					classes;
    AbstractAttributeSelectionEvaluation	eval;
    JPanel					panel;
    ParameterPanel				panelTop;
    Properties					props;
    JPanel					buttonsAll;
    JLabel 					label;

    super.initGUI();

    props = InvestigatorPanel.getProperties();

    m_ContentPanel.setLayout(new BorderLayout());
    m_ContentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    panelTop = new ParameterPanel();
    panelTop.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
    m_ContentPanel.add(panelTop, BorderLayout.NORTH);

    try {
      evaluator = (ASEvaluation) OptionUtils.forAnyCommandLine(
	ASEvaluation.class,
	InvestigatorPanel.getProperties().getProperty(
	  "AttributeSelection.Evaluator", CfsSubsetEval.class.getName()));
    }
    catch (Exception e) {
      evaluator = new CfsSubsetEval();
    }
    m_PanelEvaluator = new GenericObjectEditorPanel(ASEvaluation.class, evaluator, true);
    m_PanelEvaluator.setPrefix("Evaluator");
    m_PanelEvaluator.moveChooseButton(true);
    m_PanelEvaluator.addChangeListener((ChangeEvent e) -> updateButtons());
    panelTop.addParameter(m_PanelEvaluator);

    try {
      search = (ASSearch) OptionUtils.forAnyCommandLine(
	ASSearch.class,
	InvestigatorPanel.getProperties().getProperty(
	  "AttributeSelection.Search", BestFirst.class.getName()));
    }
    catch (Exception e) {
      search = new BestFirst();
    }
    m_PanelSearch = new GenericObjectEditorPanel(ASSearch.class, search, true);
    m_PanelSearch.setPrefix("Search ");
    m_PanelSearch.moveChooseButton(true);
    m_PanelSearch.addChangeListener((ChangeEvent e) -> updateButtons());
    panelTop.addParameter(m_PanelSearch);

    m_SplitPane = new BaseSplitPane(BaseSplitPane.HORIZONTAL_SPLIT);
    m_SplitPane.setOneTouchExpandable(true);
    m_SplitPane.setUISettingsParameters(getClass(), "HorizontalDivider");
    m_ContentPanel.add(m_SplitPane, BorderLayout.CENTER);
    m_PanelLeft = new JPanel(new BorderLayout());
    m_SplitPane.setLeftComponent(m_PanelLeft);
    m_PanelRight = new JPanel(new BorderLayout());
    m_SplitPane.setRightComponent(m_PanelRight);
    m_SplitPane.setDividerLocation(props.getInteger("AttributeSelection.LeftPanelWidth", 200));

    // evaluation
    m_PanelEvaluation = new JPanel(new BorderLayout());
    m_PanelEvaluation.setBorder(BorderFactory.createTitledBorder("Evaluation"));
    m_PanelLeft.add(m_PanelEvaluation, BorderLayout.NORTH);

    m_ModelEvaluations = new DefaultComboBoxModel<>();
    classes            = AbstractAttributeSelectionEvaluation.getEvaluations();
    for (Class c: classes) {
      try {
	eval = (AbstractAttributeSelectionEvaluation) c.getDeclaredConstructor().newInstance();
	eval.setOwner(this);
	m_ModelEvaluations.addElement(eval);
      }
      catch (Exception e) {
	ConsolePanel.getSingleton().append(Level.SEVERE, "Failed to instantiate clusterer evaluation: " + c.getName(), e);
      }
    }
    m_ComboBoxEvaluations = new BaseComboBox<>(m_ModelEvaluations);
    m_ComboBoxEvaluations.addActionListener((ActionEvent e) -> {
      if (m_ComboBoxEvaluations.getSelectedIndex() == -1)
	return;
      m_CurrentEvaluation = m_ComboBoxEvaluations.getSelectedItem();
      m_PanelEvaluationSetup.removeAll();
      m_PanelEvaluationSetup.add(m_CurrentEvaluation.getPanel());
      m_CurrentEvaluation.update();
      m_PanelEvaluationSetup.invalidate();
      m_PanelEvaluationSetup.revalidate();
      m_PanelEvaluationSetup.doLayout();
    });
    panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
    panel.add(m_ComboBoxEvaluations, BorderLayout.CENTER);
    m_PanelEvaluation.add(panel, BorderLayout.NORTH);

    // setup
    m_PanelEvaluationSetup = new JPanel(new BorderLayout());
    m_PanelEvaluation.add(m_PanelEvaluationSetup, BorderLayout.CENTER);

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

    evalDefault = props.getProperty("AttributeSelection.Evaluation", "");
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
    return "Attribute selection";
  }

  /**
   * Returns the icon name for the tab icon.
   *
   * @return		the icon name, null if not available
   */
  public String getTabIcon() {
    return "attributeselection.png";
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
    m_CurrentEvaluator = (ASEvaluation) m_PanelEvaluator.getCurrent();
    m_CurrentSearch    = (ASSearch) m_PanelSearch.getCurrent();
    startExecution(new InvestigatorTabJob(this, "Starting attribute selection '" + m_CurrentEvaluation.getName() + "' using: " + OptionUtils.getCommandLine(m_CurrentEvaluator)) {
      @Override
      protected void doRun() {
	ResultItem item;
	try {
          item = m_CurrentEvaluation.init(m_CurrentEvaluator, m_CurrentSearch);
          m_CurrentEvaluation.addToHistory(m_History, item);
	  m_CurrentEvaluation.evaluate(m_CurrentEvaluator, m_CurrentSearch, item);
	}
	catch (Exception e) {
	  logError("Failed to perform attribute selection", e, "Attribute selection");
	  item = null;
	}
	if (item != null)
	  generateOutput(item);
      }
    });
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
   * Hook method that gets called after stopping a job.
   */
  @Override
  protected void postStopExecution() {
    super.postStopExecution();
    logMessage("Stopped evaluation '" + m_CurrentEvaluation.getName() + "' using: " + OptionUtils.getCommandLine(m_CurrentEvaluator));
    updateButtons();
  }

  /**
   * Hook method that gets called after finishing a job.
   */
  @Override
  protected void postExecutionFinished() {
    super.postExecutionFinished();
    logMessage("Finished attribute selection '" + m_CurrentEvaluation.getName() + "' using: " + OptionUtils.getCommandLine(m_CurrentEvaluator));
    updateButtons();
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
	logError("Failed to generate output with " + m_OutputGenerators[i].toCommandLine(), e, "Attribute selection output generation");
      }
    }
  }

  /**
   * Updates the buttons.
   */
  public void updateButtons() {
    ASEvaluation 	eval;
    ASSearch		search;
    String		msg;

    eval   = (ASEvaluation) m_PanelEvaluator.getCurrent();
    search = (ASSearch) m_PanelSearch.getCurrent();
    msg    = m_CurrentEvaluation.canEvaluate(eval, search);
    m_ButtonStart.setEnabled(!isBusy() && (m_CurrentEvaluation != null) && (msg == null));
    m_ButtonStart.setToolTipText(msg);
    m_ButtonStop.setEnabled(isBusy());
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
   * Returns the panel with the evaluator.
   *
   * @return		the panel
   */
  public GenericObjectEditorPanel getPanelEvaluator() {
    return m_PanelEvaluator;
  }

  /**
   * Returns the panel with the search.
   *
   * @return		the panel
   */
  public GenericObjectEditorPanel getPanelSearch() {
    return m_PanelSearch;
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
  protected Map<String,Object> doSerialize(Set<SerializationOption> options) {
    Map<String,Object>				result;
    int						i;
    AbstractAttributeSelectionEvaluation 	eval;

    result = super.doSerialize(options);
    if (options.contains(SerializationOption.GUI))
      result.put(KEY_LEFTPANELWIDTH, m_SplitPane.getDividerLocation());
    if (options.contains(SerializationOption.PARAMETERS)) {
      result.put(KEY_SEARCH, OptionUtils.getCommandLine(m_PanelSearch.getCurrent()));
      result.put(KEY_EVALUATOR, OptionUtils.getCommandLine(m_PanelEvaluator.getCurrent()));
      result.put(KEY_EVALUATION, m_ComboBoxEvaluations.getSelectedIndex());
      result.put(KEY_OUTPUTGENERATORS, OptionUtils.getCommandLines(m_OutputGenerators));
    }
    for (i = 0; i < m_ModelEvaluations.getSize(); i++) {
      eval = m_ModelEvaluations.getElementAt(i);
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
    Map<String,Object> 				evaldata;
    int						i;
    AbstractAttributeSelectionEvaluation 	eval;

    super.doDeserialize(data, errors);
    if (data.containsKey(KEY_LEFTPANELWIDTH))
      m_SplitPane.setDividerLocation(((Number) data.get(KEY_LEFTPANELWIDTH)).intValue());
    if (data.containsKey(KEY_SEARCH)) {
      try {
        m_CurrentSearch = (ASSearch) OptionUtils.forAnyCommandLine(ASSearch.class, (String) data.get(KEY_SEARCH));
        m_PanelSearch.setCurrent(m_CurrentSearch);
      }
      catch (Exception e) {
        errors.add("Failed to restore search: " + data.get(KEY_SEARCH), e);
      }
    }
    if (data.containsKey(KEY_EVALUATOR)) {
      try {
        m_CurrentEvaluator = (ASEvaluation) OptionUtils.forAnyCommandLine(ASEvaluation.class, (String) data.get(KEY_EVALUATOR));
        m_PanelEvaluator.setCurrent(m_CurrentEvaluator);
      }
      catch (Exception e) {
        errors.add("Failed to restore evaluator: " + data.get(KEY_EVALUATOR), e);
      }
    }
    if (data.containsKey(KEY_EVALUATION))
      m_ComboBoxEvaluations.setSelectedIndex(((Number) data.get(KEY_EVALUATION)).intValue());
    for (i = 0; i < m_ModelEvaluations.getSize(); i++) {
      eval = m_ModelEvaluations.getElementAt(i);
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
