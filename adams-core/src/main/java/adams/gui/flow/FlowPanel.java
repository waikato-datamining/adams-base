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
 * FlowPanel.java
 * Copyright (C) 2009-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.flow;

import adams.core.Properties;
import adams.core.StatusMessageHandler;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.io.FilenameProposer;
import adams.core.io.PlaceholderFile;
import adams.core.logging.LoggingLevel;
import adams.core.option.OptionConsumer;
import adams.core.option.OptionProducer;
import adams.core.option.OptionUtils;
import adams.data.io.input.FlowReader;
import adams.data.io.output.DefaultFlowWriter;
import adams.data.io.output.FlowWriter;
import adams.env.Environment;
import adams.env.FlowEditorPanelDefinition;
import adams.flow.control.Flow;
import adams.flow.core.AbstractDisplay;
import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;
import adams.flow.processor.AbstractActorProcessor;
import adams.gui.chooser.FlowFileChooser;
import adams.gui.core.BaseDialog;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.ButtonTabComponent;
import adams.gui.core.ConsolePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.RecentFilesHandler;
import adams.gui.core.TabIconSupporter;
import adams.gui.core.TitleGenerator;
import adams.gui.core.Undo.UndoPoint;
import adams.gui.core.UndoPanel;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.event.ActorChangeEvent;
import adams.gui.event.UndoEvent;
import adams.gui.flow.tab.RegisteredDisplaysTab;
import adams.gui.flow.tree.Node;
import adams.gui.flow.tree.Tree;
import adams.gui.flow.tree.Tree.TreeState;
import adams.gui.flow.tree.keyboardaction.AbstractKeyboardAction;
import adams.gui.sendto.SendToActionSupporter;
import adams.gui.sendto.SendToActionUtils;
import adams.gui.tools.VariableManagementPanel;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A panel for setting up, modifying, saving and loading "simple" flows.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FlowPanel
  extends UndoPanel
  implements StatusMessageHandler, SendToActionSupporter, FlowTreeHandler,
             TabIconSupporter, FlowWorkerHandler {

  /** for serialization. */
  private static final long serialVersionUID = -3579084888256133873L;

  /** the prefix for the title of new tabs. */
  public final static String PREFIX_NEW = "new";

  /** the prefix for the title of custom set flows. */
  public final static String PREFIX_FLOW = "Flow";

  /** the counter for the tabs. */
  protected static int m_Counter;
  static {
    m_Counter = 0;
  }

  /** the properties. */
  protected static Properties m_Properties;

  /** the owner. */
  protected FlowTabbedPane m_Owner;

  /** the last flow that was run. */
  protected Actor m_LastFlow;

  /** the filename of the current flow. */
  protected File m_CurrentFile;

  /** the current worker. */
  protected FlowWorker m_CurrentWorker;

  /** the current worker thread. */
  protected Thread m_CurrentThread;
  
  /** whether a swingworker is currently running. */
  protected boolean m_RunningSwingWorker;
  
  /** the split pane to use for tree and notification area. */
  protected BaseSplitPane m_SplitPane;
  
  /** the tree displaying the flow structure. */
  protected Tree m_Tree;

  /** the recent files handler. */
  protected RecentFilesHandler<JMenu> m_RecentFilesHandler;

  /** for proposing filenames for new flows. */
  protected FilenameProposer m_FilenameProposer;

  /** the last variable search performed. */
  protected String m_LastVariableSearch;

  /** the panel with the variables. */
  protected VariableManagementPanel m_PanelVariables;

  /** the panel with the variables. */
  protected StoragePanel m_PanelStorage;

  /** for generating the title of the dialog/frame. */
  protected TitleGenerator m_TitleGenerator;

  /** the current title. */
  protected String m_Title;

  /** whether to execute the flow in headless mode. */
  protected boolean m_Headless;

  /** whether to perform a GC after the flow execution. */
  protected boolean m_RunGC;

  /** whether to check before saving. */
  protected boolean m_CheckOnSave;

  /** the registered panels: class of panel - (name of panel - AbstractDisplay instance). */
  protected HashMap<Class,HashMap<String,AbstractDisplay>> m_RegisteredDisplays;

  /** the panel for showing notifications. */
  protected FlowPanelNotificationArea m_PanelNotification;
  
  /**
   * Initializes the panel with no owner.
   */
  public FlowPanel() {
    this(null);
  }

  /**
   * Initializes the panel with an owner.
   *
   * @param owner	the owning tabbed pane
   */
  public FlowPanel(FlowTabbedPane owner) {
    super();

    m_Owner = owner;
    if (getEditor() != null)
      m_RecentFilesHandler = getEditor().getRecentFilesHandler();

    reset(new Flow());
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_LastFlow              = null;
    m_CurrentFile           = null;
    m_RecentFilesHandler    = null;
    m_CurrentWorker         = null;
    m_LastVariableSearch    = "";
    m_TitleGenerator        = new TitleGenerator(FlowEditorPanel.DEFAULT_TITLE, true);
    m_FilenameProposer      = new FilenameProposer(PREFIX_NEW, Actor.FILE_EXTENSION, getProperties().getPath("InitialDir", "%h"));
    m_Title                 = "";
    m_RegisteredDisplays    = new HashMap<Class,HashMap<String,AbstractDisplay>>();
    m_CheckOnSave           = getProperties().getBoolean("CheckOnSave", true);
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    Properties				props;
    String[]				keyboardShortcuts;
    AbstractKeyboardAction		keyboardAction;
    List<AbstractKeyboardAction>	keyboardActions;

    super.initGUI();

    props = getProperties();

    setLayout(new BorderLayout());

    m_SplitPane = new BaseSplitPane(BaseSplitPane.VERTICAL_SPLIT);
    m_SplitPane.setResizeWeight(1.0);
    m_SplitPane.setOneTouchExpandable(true);
    add(m_SplitPane, BorderLayout.CENTER);
    
    // the tree
    m_Tree = new Tree(this);
    m_Tree.setActorNameColor(props.getProperty("Tree.ActorName.Color", "black"));
    m_Tree.setActorNameSize(props.getProperty("Tree.ActorName.Size", "3"));
    m_Tree.setQuickInfoColor(props.getProperty("Tree.QuickInfo.Color", "#008800"));
    m_Tree.setQuickInfoSize(props.getProperty("Tree.QuickInfo.Size", "-2"));
    m_Tree.setAnnotationsColor(props.getProperty("Tree.Annotations.Color", "blue"));
    m_Tree.setAnnotationsSize(props.getProperty("Tree.Annotations.Size", "-2"));
    m_Tree.setInputOutputColor(props.getProperty("Tree.InputOutput.Color", "blue"));
    m_Tree.setInputOutputSize(props.getProperty("Tree.InputOutput.Size", "-2"));
    m_Tree.setPlaceholdersColor(props.getProperty("Tree.Placeholders.Color", "navy"));
    m_Tree.setPlaceholdersSize(props.getProperty("Tree.Placeholders.Size", "-2"));
    m_Tree.setIgnoreNameChanges(props.getBoolean("Tree.IgnoreNameChanges", false));
    m_Tree.setScaleFactor(props.getDouble("Tree.ScaleFactor", 1.0));
    m_Tree.setRecordAdd(props.getBoolean("Tree.RecordAdd", false));
    m_Tree.setVariableHighlightBackground(props.getProperty("VariableHighlight.Background", "#FFDD88"));
    m_Tree.setShowQuickInfo(props.getBoolean("ShowQuickInfo", true));
    m_Tree.setShowAnnotations(props.getBoolean("ShowAnnotations", true));
    m_Tree.setShowInputOutput(props.getBoolean("ShowInputOutput", false));
    m_Tree.setInputOutputPrefixes(props.getProperty("Tree.InputOutput.Prefixes", "java.lang.,java.io.,adams.core.io.,adams.flow.core.,adams.flow.container.").replace(" ", "").split(","));

    // keyboard actions
    try {
      keyboardShortcuts = OptionUtils.splitOptions(props.getProperty("Tree.KeyboardActions", ""));
    }
    catch (Exception e) {
      keyboardShortcuts = new String[0];
      ConsolePanel.getSingleton().append("Failed to parse 'Tree.KeyboardActions' property!", e);
    }
    keyboardActions = new ArrayList<>();
    for (String keyboardShortcut: keyboardShortcuts) {
      try {
	keyboardAction = (AbstractKeyboardAction) OptionUtils.forCommandLine(AbstractKeyboardAction.class, keyboardShortcut);
	keyboardActions.add(keyboardAction);
      }
      catch (Exception e) {
	ConsolePanel.getSingleton().append("Failed to parse keyboard action: " + keyboardShortcut, e);
      }
    }
    m_Tree.setKeyboardActions(keyboardActions);

    m_Tree.addActorChangeListener((ActorChangeEvent e) -> update());
    m_Tree.getSelectionModel().addTreeSelectionListener((TreeSelectionEvent e) -> {
      if (m_Tree.getSelectionPath() != null)
	showStatus(m_Tree.getSelectedFullName());
    });

    m_SplitPane.setTopComponent(new BaseScrollPane(m_Tree));

    // the tabs
    m_Tree.getSelectionModel().addTreeSelectionListener((TreeSelectionEvent e) -> {
      if (getEditor() != null)
	getEditor().getTabs().notifyTabs(m_Tree.getSelectionPaths(), m_Tree.getSelectedActors());
    });
    
    m_PanelNotification = new FlowPanelNotificationArea();
    m_PanelNotification.setOwner(this);
    m_PanelNotification.addCloseListener((ActionEvent e) -> clearNotification());
    m_SplitPane.setBottomComponent(m_PanelNotification);
    m_SplitPane.setBottomComponentHidden(true);
  }

  /**
   * Finishes up the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    
    clearNotification();
  }
  
  /**
   * Returns the editor this panel belongs to.
   *
   * @return		the editor, null if none set
   */
  public FlowTabbedPane getOwner() {
    return m_Owner;
  }

  /**
   * Sets the tab icon.
   * 
   * @param icon	the name of the icon, null to unset it
   */
  public void setTabIcon(String icon) {
    int		    	index;
    ButtonTabComponent  button;
    
    if (getOwner() != null) {
      index = getOwner().indexOfComponent(this);
      if (index != -1) {
        button = (ButtonTabComponent) getOwner().getTabComponentAt(index);
	button.setIcon((icon == null) ? null : GUIHelper.getIcon(icon));
      }
    }
  }

  /**
   * Returns the editor this panel belongs to.
   *
   * @return		the editor, null if none set
   */
  public FlowEditorPanel getEditor() {
    if (m_Owner != null)
      return m_Owner.getOwner();
    else
      return null;
  }

  /**
   * Sets whether to execute the flow in headless mode.
   *
   * @param value	if true the flow gets executed in headless mode
   */
  public void setHeadless(boolean value) {
    if (!isRunning() && !isStopping())
      m_Headless = value;
  }

  /**
   * Returns whether the flow gets executed in headless mode.
   *
   * @return		true if the flow gets executed in headless mode
   */
  public boolean isHeadless() {
    return m_Headless;
  }

  /**
   * Sets whether to run the GC after the flow finished executing.
   *
   * @param value	if true GC gets called
   */
  public void setRunGC(boolean value) {
    if (!isRunning() && !isStopping())
      m_RunGC = value;
  }

  /**
   * Returns whether the GC gets called after the flow execution.
   *
   * @return		true if to run GC
   */
  public boolean getRunGC() {
    return m_RunGC;
  }

  /**
   * Sets whether to perform a check before saving the flow.
   *
   * @param value	true if to check before saving
   */
  public void setCheckOnSave(boolean value) {
    m_CheckOnSave = value;
  }

  /**
   * Returns whether to perform a check before saving the flow.
   *
   * @return		true if to check before saving
   */
  public boolean getCheckOnSave() {
    return m_CheckOnSave;
  }

  /**
   * Updates the enabled state of the widgets.
   */
  protected void updateWidgets() {
    boolean	inputEnabled;

    inputEnabled = !isRunning() && !isStopping();

    getTree().setEditable(inputEnabled);
  }

  /**
   * updates the enabled state etc. of all the GUI elements.
   */
  public void update() {
    updateWidgets();
    updateTitle();
    if ((getOwner() != null) && (getOwner().getOwner() != null))
      getOwner().getOwner().update();
  }

  /**
   * Increments the flow counter and returns the new value.
   *
   * @return		the new counter value
   */
  protected synchronized int next() {
    m_Counter++;
    return m_Counter;
  }

  /**
   * Sets the title for this flow.
   *
   * @param value	the title
   */
  public void setTitle(String value) {
    m_Title = value;
  }

  /**
   * Returns the current title of the flow.
   *
   * @return		the title
   */
  public String getTitle() {
    return m_Title;
  }

  /**
   * Updates the title of the dialog/frame if the title generator is enabled.
   * 
   * @see		#getTitleGenerator()
   */
  public void updateTitle() {
    int		index;

    if (!m_TitleGenerator.isEnabled())
      return;
    
    setParentTitle(m_TitleGenerator.generate(getCurrentFile(), getTree().isModified()));

    if (getOwner() != null) {
      index = getOwner().indexOfComponent(this);
      if (index != -1)
        getOwner().setShortenedTitleAt(index, (isModified() ? "*" : "") + getTitle());
    }
  }

  /**
   * Resets the GUI to default values.
   *
   * @param actor	the actor to instantiate
   */
  public void reset(Actor actor) {
    addUndoPoint("Saving undo data...", "New " + actor.getClass().getName().replaceAll(".*\\.", ""));

    cleanUp();

    getTree().setActor(null);
    getTree().setActor(actor);
    getTree().setModified(false);
    setCurrentFile(null);

    setTitle(PREFIX_NEW + next());

    update();

    grabFocus();
  }

  /**
   * Sets the current file.
   *
   * @param value	the file
   */
  public void setCurrentFile(File value) {
    String	tmp;
    
    m_CurrentFile = value;
    if (value == null) {
      m_Title = "";
    }
    else if (value.getName().lastIndexOf('.') > -1) {
      tmp = value.getName();
      if (tmp.toLowerCase().endsWith(".gz"))
	tmp = tmp.substring(0, tmp.lastIndexOf('.'));
      m_Title = tmp.substring(0, tmp.lastIndexOf('.'));
    }
    else {
      m_Title = value.getName();
    }
    getTree().setFile(value);
    if (getOwner() != null)
      getOwner().updateCurrentDirectory();
  }

  /**
   * Returns the current file in use.
   *
   * @return		the current file, can be null
   */
  public File getCurrentFile() {
    return m_CurrentFile;
  }
  
  /**
   * Returns the title generator in use.
   * 
   * @return		the generator
   */
  public TitleGenerator getTitleGenerator() {
    return m_TitleGenerator;
  }

  /**
   * Loads a flow.
   *
   * @param reader	the reader to use
   * @param file	the flow to load
   */
  public void load(final FlowReader reader, final File file) {
    load(reader, file, false);
  }

  /**
   * Loads a flow and optionally executes it.
   *
   * @param reader	the reader to use
   * @param file	the flow to load
   */
  public void load(final FlowReader reader, final File file, final boolean execute) {
    SwingWorker		worker;

    m_RunningSwingWorker = true;
    worker = new SwingWorker() {
      protected Node m_Flow = null;
      protected List<String> m_Errors;
      protected List<String> m_Warnings;

      @Override
      protected Object doInBackground() throws Exception {
	m_Errors   = new ArrayList<String>();
	m_Warnings = new ArrayList<String>();

	cleanUp();
	update();

	addUndoPoint("Saving undo data...", "Loading '" + file.getName() + "'");
	SwingUtilities.invokeLater(() -> showStatus("Loading '" + file + "'..."));
        SwingUtilities.invokeLater(() -> setTabIcon("hourglass.png"));
	setTitle(FileUtils.replaceExtension(file.getName(), ""));
	updateTitle();

	m_Flow = reader.readNode(file);
	m_Errors.addAll(reader.getErrors());
	m_Warnings.addAll(reader.getWarnings());
	setCurrentFlow(m_Flow);
	redraw();

	showStatus("");

        return null;
      }

      @Override
      protected void done() {
        boolean   canExecute;
        String    msg;

	m_RunningSwingWorker = false;
        canExecute           = execute && m_Errors.isEmpty();

        SwingUtilities.invokeLater(() -> setTabIcon(null));

	if (m_Errors.isEmpty())
	  setCurrentFile(file);
	if (m_RecentFilesHandler != null)
	  m_RecentFilesHandler.addRecentItem(file);
	if (!m_Errors.isEmpty()) {
	  GUIHelper.showErrorMessage(
	      m_Owner, 
	      "Failed to load flow '" + file + "':\n" + Utils.flatten(m_Errors, "\n") 
	      + (m_Warnings.isEmpty() ? "" : "\nWarning(s):\n" + Utils.flatten(m_Warnings, "\n")));
	}
	if (!m_Warnings.isEmpty()) {
          msg = "Warning(s) encountered while loading flow '" + file + "':\n" + Utils.flatten(m_Warnings, "\n");
          if (canExecute)
            ConsolePanel.getSingleton().append(LoggingLevel.SEVERE, msg);
          else
            GUIHelper.showErrorMessage(m_Owner, msg);
        }

	update();

        super.done();

        // execute flow?
        if (canExecute)
          FlowPanel.this.run();
      }
    };
    worker.execute();
  }

  /**
   * Sets the flow to work on.
   *
   * @param flow	the flow to use
   */
  public void setCurrentFlow(Actor flow) {
    if (flow != null)
      getTree().setActor(flow);
    else
      getTree().setActor(null);
    getTree().setModified(false);

    setCurrentFile(null);
    setTitle(PREFIX_FLOW + next());
  }

  /**
   * Sets the flow to work on.
   *
   * @param flow	the flow to use
   */
  public void setCurrentFlow(Node flow) {
    if (flow != null)
      getTree().buildTree(flow);
    else
      getTree().setActor(null);
    getTree().setModified(false);

    setCurrentFile(null);
    setTitle(PREFIX_FLOW + next());
  }

  /**
   * Returns the current flow.
   * <br><br>
   * WARNING: Recreates an actor hierarchy based on the tree. Method gets very
   * slow for large flows. If you only need the root actor, then use getCurrentRoot()
   * instead.
   *
   * @return		the current flow
   * @see		#getCurrentRoot()
   */
  public Actor getCurrentFlow() {
    return getCurrentFlow(null);
  }

  /**
   * Returns the current flow.
   * <br><br>
   * WARNING: Recreates an actor hierarchy based on the tree. Method gets very
   * slow for large flows. If you only need the root actor, then use getCurrentRoot()
   * instead.
   *
   * @param errors	for storing potential errors, use null to ignore
   * @return		the current flow
   * @see		#getCurrentRoot()
   */
  public Actor getCurrentFlow(StringBuilder errors) {
    return getTree().getActor(errors);
  }

  /**
   * Returns the current root actor without its children.
   *
   * @return		the current root actor
   * @see		#getCurrentFlow()
   */
  public Actor getCurrentRoot() {
    return getTree().getRootActor();
  }

  /**
   * Returns the currently running flow.
   *
   * @return		the running flow, null if none running right now
   * @see		#isRunning()
   */
  public Actor getRunningFlow() {
    if (isRunning())
      return m_CurrentWorker.getFlow();
    else
      return null;
  }

  /**
   * Sets the flow that was last executed.
   * 
   * @param actor	the flow
   */
  public void setLastFlow(Actor actor) {
    m_LastFlow = actor;
  }
  
  /**
   * Returns the last executed flow (if any).
   *
   * @return		the flow, null if not available
   */
  public Actor getLastFlow() {
    return m_LastFlow;
  }

  /**
   * Sets whether the flow is modified or not.
   *
   * @param value	true if the flow is to be flagged as modified
   */
  public void setModified(boolean value) {
    getTree().setModified(value);
    update();
  }

  /**
   * Returns whether the flow is flagged as modified.
   *
   * @return		true if the flow is modified
   */
  public boolean isModified() {
    return getTree().isModified();
  }

  /**
   * Reverts a flow.
   */
  public void revert() {
    FlowFileChooser	filechooser;
    
    cleanUp();

    filechooser = null;
    if ((getOwner() != null) && (getOwner().getOwner() != null))
      filechooser = getOwner().getOwner().getFileChooser();
    if (filechooser == null)
      filechooser = new FlowFileChooser();
    
    load(filechooser.getReaderForFile(getCurrentFile()), getCurrentFile());
  }

  /**
   * Saves the flow.
   *
   * @param writer	the writer to use
   * @param file	the file to save the flow to
   */
  public void save(final FlowWriter writer, final File file) {
    SwingWorker		worker;
    final Node 		flow;

    flow = getTree().getRootNode();

    worker = new SwingWorker() {
      boolean m_Result;
      boolean m_Cancelled;

      @Override
      protected Object doInBackground() throws Exception {
	m_Cancelled = false;

	if (getCheckOnSave()) {
	  SwingUtilities.invokeLater(() -> setTabIcon("hourglass.png"));
	  String check = ActorUtils.checkFlow(flow.getFullActor(), false, false);
	  if (check != null) {
	    int retVal = GUIHelper.showConfirmMessage(
	      m_Owner, "Pre-save check failed - continue with save?\n\nDetails:\n\n" + check);
	    if (retVal != ApprovalDialog.APPROVE_OPTION) {
	      showStatus("Cancelled saving!");
	      m_Cancelled = true;
	      SwingUtilities.invokeLater(() -> setTabIcon("error_blue.png"));
	      return null;
	    }
	  }
	}

	SwingUtilities.invokeLater(() -> setTabIcon("save.gif"));
	showStatus("Saving '" + file + "'...");
	m_Result = writer.write(flow, file);
	showStatus("");
        return null;
      }

      @Override
      protected void done() {
	if (!m_Result) {
	  SwingUtilities.invokeLater(() -> setTabIcon("error_blue.png"));
          GUIHelper.showErrorMessage(
            m_Owner, "Error saving flow to '" + file.getAbsolutePath() + "'!");
        }
	else {
	  SwingUtilities.invokeLater(() -> setTabIcon("validate_blue.gif"));
	  showStatus("");
	  getTree().setModified(false);
	  if (m_RecentFilesHandler != null)
	    m_RecentFilesHandler.addRecentItem(file);
	  setCurrentFile(file);
	}

	update();

        super.done();
      }
    };
    worker.execute();
  }

  /**
   * Imports a flow.
   *
   * @param consumer	the consumer to use
   * @param file	the file to import
   */
  public void importFlow(OptionConsumer consumer, File file) {
    Actor		actor;

    actor = (Actor) consumer.fromFile(file);
    if (actor == null) {
      showNotification("Failed to load flow from:\n" + file, true);
    }
    else {
      getTree().setActor(actor);
      setCurrentFile(new PlaceholderFile(file.getAbsolutePath() + "." + Actor.FILE_EXTENSION));
      if (!consumer.hasErrors())
	showNotification("Flow successfully imported from:\n" + file, false);
      else
	showNotification("Flow import of:\n" + file + "\nResulted in errors:\n" + Utils.flatten(consumer.getErrors(), "\n"), true);
    }
  }

  /**
   * Exports the flow.
   *
   * @param producer	the producer to use
   * @param file	the file to export to
   */
  public void exportFlow(OptionProducer producer, File file) {
    producer.produce(getCurrentFlow());
    if (!FileUtils.writeToFile(file.getAbsolutePath(), producer.toString(), false)) {
      showNotification("Failed to export flow to:\n" + file, true);
    }
    else {
      showNotification("Flow successfully exported to:\n" + file, false);
    }
  }

  /**
   * Executes the flow.
   */
  public void run() {
    run(true, false);
  }

  /**
   * Executes the flow.
   *
   * @param showNotification	whether to show notifications about
   * 				errors/stopped/finished
   * @param debug		whether to run in debug mode
   */
  public void run(boolean showNotification, boolean debug) {
    m_CurrentWorker = new FlowWorker(this, getCurrentFlow(), getCurrentFile(), showNotification, debug);
    m_CurrentThread = new Thread(m_CurrentWorker);
    m_CurrentThread.start();
  }

  /**
   * Finishes up the execution, setting the worker to null.
   */
  public void finishedExecution() {
    m_CurrentWorker = null;
    m_CurrentThread = null;
    update();
  }
  
  /**
   * Returns whether a flow is currently running.
   *
   * @return		true if a flow is being executed
   */
  public boolean isRunning() {
    return (m_CurrentWorker != null) && m_CurrentWorker.isRunning();
  }

  /**
   * Returns whether a flow is currently being stopped.
   *
   * @return		true if a flow is currently being stopped
   */
  public boolean isStopping() {
    return (m_CurrentWorker != null) && m_CurrentWorker.isStopping();
  }

  /**
   * Returns whether a flow is currently running.
   *
   * @return		true if a flow is being executed
   */
  public boolean isPaused() {
    return isRunning() && m_CurrentWorker.isPaused();
  }

  /**
   * Returns whether a swing worker is currently running.
   *
   * @return		true if a swing worker is being executed
   */
  public boolean isSwingWorkerRunning() {
    return m_RunningSwingWorker;
  }

  /**
   * Pauses/resumes the flow.
   *
   * @return		true if paused, otherwise false
   */
  public boolean pauseAndResume() {
    boolean	result;

    result = false;
    if (m_CurrentWorker != null) {
      if (!m_CurrentWorker.isPaused()) {
	m_CurrentWorker.pauseExecution();
	result = true;
      }
      else {
	m_CurrentWorker.resumeExecution();
	result = false;
      }
    }

    update();

    return result;
  }

  /**
   * Stops the flow. Does not cleanUp.
   * 
   * @see 	#stop(boolean)
   */
  public void stop() {
    stop(false);
  }

  /**
   * Stops the flow.
   * 
   * @param cleanUp	whether to clean up as well
   */
  public void stop(final boolean cleanUp) {
    SwingWorker	worker;
    
    if (m_CurrentWorker != null) {
      worker = new SwingWorker() {
	@Override
	protected Object doInBackground() throws Exception {
	  m_CurrentWorker.stopExecution();
	  if (cleanUp)
	    cleanUp();
	  return null;
	}
      };
      worker.execute();
    }
  }

  /**
   * Kills the flow.
   */
  @SuppressWarnings("deprecation")
  public void kill() {
    if (m_CurrentThread != null) {
      try {
	m_CurrentThread.interrupt();
	m_CurrentThread.stop();
      }
      finally {
	setTabIcon(null);
	finishedExecution();
      }
    }
  }

  /**
   * Cleans up the last flow that was run.
   */
  public void cleanUp() {
    if (m_LastFlow != null) {
      showStatus("Cleaning up");
      try {
	clearRegisteredDisplays();
	m_LastFlow.destroy();
	m_LastFlow = null;
	showStatus("");
      }
      catch (Exception e) {
	e.printStackTrace();
	showStatus("Error cleaning up: " + e);
      }
    }
  }

  /**
   * Cleans up and closes the tab.
   */
  public void close() {
    cleanUp();
    m_Tree.cleanUp();
    if (m_Owner != null)
      m_Owner.remove(this);
  }

  /**
   * Adds an undo point, if possible.
   *
   * @param statusMsg	the status message to display while adding the undo point
   * @param undoComment	the comment for the undo point
   */
  public void addUndoPoint(String statusMsg, String undoComment) {
    if (isUndoSupported() && getUndo().isEnabled()) {
      showStatus(statusMsg);
      getUndo().addUndo(getTree().getState(), undoComment);
      showStatus("");
    }
  }

  /**
   * peforms an undo if possible.
   */
  public void undo() {
    if (!getUndo().canUndo())
      return;

    SwingWorker worker = new SwingWorker() {
      @Override
      protected Object doInBackground() throws Exception {
	showStatus("Performing Undo...");

	// add redo point
	getUndo().addRedo(getTree().getState(), getUndo().peekUndoComment());

	UndoPoint point = getUndo().undo();
	SwingUtilities.invokeLater(() -> getTree().setState((TreeState) point.getData()));
	m_CurrentFile = getTree().getFile();

	return "Done!";
      };

      @Override
      protected void done() {
        super.done();
	update();
	showStatus("");
      }
    };
    worker.execute();
  }

  /**
   * peforms a redo if possible.
   */
  public void redo() {
    if (!getUndo().canRedo())
      return;

    SwingWorker worker = new SwingWorker() {
      @Override
      protected Object doInBackground() throws Exception {
	showStatus("Performing Redo...");

	// add undo point
	getUndo().addUndo(getTree().getState(), getUndo().peekRedoComment(), true);

	UndoPoint point = getUndo().redo();
	SwingUtilities.invokeLater(() -> getTree().setState((TreeState) point.getData()));
	m_CurrentFile = getTree().getFile();

	return "Done!";
      };

      @Override
      protected void done() {
        super.done();
	update();
	showStatus("");
      }
    };
    worker.execute();
  }

  /**
   * Sets whether to ignore name changes of actors and don't prompt a dialog
   * with the user having the option to update the name throughout the glow.
   *
   * @param value	if true then name changes are ignored
   */
  public void setIgnoreNameChanges(boolean value) {
    m_Tree.setIgnoreNameChanges(value);
  }

  /**
   * Returns whether name changes of actors are ignored and no dialog is
   * prompting the user whether to propagate the changes throughout the flow.
   *
   * @return		true if the name changes are ignored
   */
  public boolean getIgnoreNameChanges() {
    return m_Tree.getIgnoreNameChanges();
  }

  /**
   * If a single actor is selected, user gets prompted whether to only
   * process below this actor instead of full flow.
   *
   * @param processor	the processor to use, null if to prompt user
   */
  public void processActorsPrompt(AbstractActorProcessor processor) {
    boolean 	selected;
    int		retVal;

    if ((getTree().getSelectionCount() == 1) && !getTree().isRootSelected()) {
      retVal = GUIHelper.showConfirmMessage(
        this,
        "<html>Process only below selected actor or process complete flow?<br><br>Currently selected actor:</html>",
	getTree().getSelectedFullName(),
        "Confirm",
        "Below selected actor", "Complete flow", "Cancel");
      if (retVal == GUIHelper.CANCEL_OPTION)
	return;
      selected = (retVal == GUIHelper.APPROVE_OPTION);
    }
    else {
      selected = false;
    }
    if (selected)
      processSelectedActor(processor);
    else
      processActors(processor);
  }

  /**
   * Processes the actors with the specified actor processor.
   *
   * @param processor	the processor to use, null if to prompt user
   */
  public void processActors(AbstractActorProcessor processor) {
    if (getTree().getOperations().processActor(null, processor))
      showNotification("Actors processed!", false);
  }

  /**
   * Processes the selected actor with a user-specified actor processor.
   * NB: The options of the selected actor will get processed.
   *
   * @param processor	the processor to use, null if to prompt user
   */
  public void processSelectedActor(AbstractActorProcessor processor) {
    TreePath	path;
    Node	node;

    if (getTree().getSelectionRows().length != 1)
      return;
    path = getTree().getSelectionPath();
    node = (Node) path.getLastPathComponent();
    if (getTree().getOperations().processActor(path, processor))
      showNotification("Actor " + node.getActor().getName() + " processed!", false);
  }

  /**
   * Enables/disables all breakpoints in the flow (before execution).
   *
   * @param enable	if true then breakpoints get enabled
   */
  public void enableBreakpoints(boolean enable) {
    getTree().enableBreakpoints(enable);
  }

  /**
   * Returns the panel with the variables.
   * 
   * @return		the panel, null if not available
   */
  public VariableManagementPanel getVariablesPanel() {
    return m_PanelVariables;
  }
  
  /**
   * Displays the variables in the currently running flow.
   */
  public void showVariables() {
    BaseDialog	dialog;

    if (m_PanelVariables == null) {
      m_PanelVariables = new VariableManagementPanel();
      if (getParentDialog() != null)
	dialog = new BaseDialog(getParentDialog());
      else
	dialog = new BaseDialog(getParentFrame());
      dialog.setTitle("Variables");
      dialog.getContentPane().setLayout(new BorderLayout());
      dialog.getContentPane().add(m_PanelVariables, BorderLayout.CENTER);
      dialog.pack();
      dialog.setLocationRelativeTo(null);
    }

    m_PanelVariables.setVariables(getRunningFlow().getVariables());
    m_PanelVariables.getParentDialog().setVisible(true);
  }

  /**
   * Displays the storage in the currently running flow.
   */
  public void showStorage() {
    BaseDialog	dialog;

    if (m_PanelStorage == null) {
      m_PanelStorage = new StoragePanel();
      if (getParentDialog() != null)
	dialog = new BaseDialog(getParentDialog());
      else
	dialog = new BaseDialog(getParentFrame());
      dialog.setTitle("Storage");
      dialog.getContentPane().setLayout(new BorderLayout());
      dialog.getContentPane().add(m_PanelStorage, BorderLayout.CENTER);
      dialog.pack();
      dialog.setLocationRelativeTo(null);
    }

    m_PanelStorage.setHandler(getRunningFlow().getStorageHandler());
    m_PanelStorage.getParentDialog().setVisible(true);
  }

  /**
   * Closes the storage dialog if currently open.
   */
  public void closeStorage() {
    if (m_PanelStorage == null)
      return;
    m_PanelStorage.closeParent();
  }

  /**
   * Sets the zoom to use.
   *
   * @param value 	the zoom, 1.0 is default
   */
  public void setZoom(double value) {
    m_Tree.setScaleFactor(value);
  }

  /**
   * Returns the current zoom.
   *
   * @return		the zoom, 1.0 is default
   */
  public double getZoom() {
    return m_Tree.getScaleFactor();
  }

  /**
   * Displays a message.
   *
   * @param msg		the message to display
   */
  @Override
  public void showStatus(String msg) {
    if (getEditor() != null)
      getEditor().showStatus(msg);
  }

  /**
   * An undo event occurred.
   *
   * @param e		the event
   */
  @Override
  public void undoOccurred(UndoEvent e) {
    update();
  }

  /**
   * Requests that this Component get the input focus, and that this
   * Component's top-level ancestor become the focused Window. This component
   * must be displayable, visible, and focusable for the request to be
   * granted.
   */
  @Override
  public void grabFocus() {
    Runnable	runnable;

    runnable = new Runnable() {
      @Override
      public void run() {
	getTree().grabFocus();
      }
    };
    SwingUtilities.invokeLater(runnable);
  }

  /**
   * Returns the split pane.
   * 
   * @return		the split pane
   */
  public BaseSplitPane getSplitPane() {
    return m_SplitPane;
  }
  
  /**
   * Returns the tree.
   *
   * @return		the tree
   */
  public Tree getTree() {
    return m_Tree;
  }

  /**
   * Sets the recent files handler to use.
   *
   * @param value	the handler to use
   */
  public void setRecentFilesHandler(RecentFilesHandler<JMenu> value) {
    m_RecentFilesHandler = value;
  }

  /**
   * Returns the recent files handler to use.
   *
   * @return		the handler in use, null if none set
   */
  public RecentFilesHandler<JMenu> getRecentFilesHandler() {
    return m_RecentFilesHandler;
  }

  /**
   * Returns the classes that the supporter generates.
   *
   * @return		the classes
   */
  @Override
  public Class[] getSendToClasses() {
    return new Class[]{PlaceholderFile.class, JComponent.class};
  }

  /**
   * Checks whether something to send is available.
   *
   * @param cls		the classes to retrieve the item for
   * @return		true if an object is available for sending
   */
  @Override
  public boolean hasSendToItem(Class[] cls) {
    return    SendToActionUtils.isAvailable(new Class[]{PlaceholderFile.class, JComponent.class}, cls)
           && !(!getTree().isModified() && (getCurrentFile() == null));
  }

  /**
   * Returns the object to send.
   *
   * @param cls		the classes to retrieve the item for
   * @return		the item to send
   */
  @Override
  public Object getSendToItem(Class[] cls) {
    Object		result;
    Node 		root;
    DefaultFlowWriter	writer;

    result = null;

    if (SendToActionUtils.isAvailable(PlaceholderFile.class, cls)) {
      if (getTree().isModified()) {
	result = SendToActionUtils.nextTmpFile("floweditor", "flow");
	root   = getTree().getRootNode();
	writer = new DefaultFlowWriter();
	writer.write(root, (PlaceholderFile) result);
      }
      else if (getCurrentFile() != null) {
	result = new PlaceholderFile(getCurrentFile());
      }
    }
    else if (SendToActionUtils.isAvailable(JComponent.class, cls)) {
      result = m_Tree;
    }

    return result;
  }

  /**
   * Redraws the tree.
   */
  public void redraw() {
    SwingUtilities.invokeLater(() -> m_Tree.redraw());
  }
  
  /**
   * Notifies the {@link RegisteredDisplaysTab} instance of a change. 
   * 
   * @param show	whether to show the tab or leave as is
   */
  protected void updateRegisteredDisplays(boolean show) {
    Runnable	run;

    run = () -> {
      if (!getEditor().getTabs().isVisible(RegisteredDisplaysTab.class) && show)
	getEditor().getTabs().setVisible(RegisteredDisplaysTab.class, true, false);
      RegisteredDisplaysTab registered = (RegisteredDisplaysTab) getEditor().getTabs().getTab(RegisteredDisplaysTab.class);
      if (registered != null)
	registered.update();
    };
    SwingUtilities.invokeLater(run);

    // close displays?
    run = () -> {
      if (!hasRegisteredDisplays())
	getEditor().getTabs().setVisible(RegisteredDisplaysTab.class, false, false);
    };
    SwingUtilities.invokeLater(run);
  }

  /**
   * Registers a display.
   * 
   * @param cls		the class to register the display for
   * @param name	the name of the display
   * @param panel	the AbstractDisplay instance
   * @return		the previously registered display, if any
   */
  public AbstractDisplay registerDisplay(Class cls, String name, AbstractDisplay panel) {
    AbstractDisplay			result;
    HashMap<String,AbstractDisplay>	panels;
    
    if (!m_RegisteredDisplays.containsKey(cls))
      m_RegisteredDisplays.put(cls, new HashMap<String,AbstractDisplay>());
    
    panels = m_RegisteredDisplays.get(cls);
    result = panels.put(name, panel);
    
    // notify panel
    updateRegisteredDisplays(true);
    
    return result;
  }

  /**
   * Deregisters a display.
   * 
   * @param cls		the class to register the display for
   * @param name	the name of the display
   * @return		the deregistered display, if any
   */
  public AbstractDisplay deregisterDisplay(Class cls, String name) {
    AbstractDisplay	result;
    
    if (m_RegisteredDisplays.containsKey(cls))
      result = m_RegisteredDisplays.get(cls).remove(name);
    else
      result = null;
    
    // notify panel
    if (result != null)
      updateRegisteredDisplays(false);
    
    return result;
  }
  
  /**
   * Removes all registered displays.
   */
  public void clearRegisteredDisplays() {
    m_RegisteredDisplays.clear();
    // notify panel
    updateRegisteredDisplays(false);
  }
  
  /**
   * Returns all currently registered displays.
   * 
   * @return		the displays
   */
  public HashMap<Class,HashMap<String,AbstractDisplay>> getRegisteredDisplays() {
    return m_RegisteredDisplays;
  }
  
  /**
   * Returns whether there are any registered displays open.
   * 
   * @return		true if at least one open
   */
  public boolean hasRegisteredDisplays() {
    int		count;
    
    count = 0;
    for (Class cls: m_RegisteredDisplays.keySet())
      count += m_RegisteredDisplays.get(cls).size();

    return (count > 0);
  }

  /**
   * Displays the notification text.
   * 
   * @param msg		the text to display
   * @param error	true if error message
   */
  public void showNotification(String msg, boolean error) {
    m_PanelNotification.showNotification(msg, error);
  }
  
  /**
   * Removes the notification.
   */
  public void clearNotification() {
    m_PanelNotification.clearNotification();
  }
  
  /**
   * Returns the properties that define the editor.
   *
   * @return		the properties
   */
  public static synchronized Properties getProperties() {
    if (m_Properties == null)
      m_Properties = Environment.getInstance().read(FlowEditorPanelDefinition.KEY);

    return m_Properties;
  }
}
