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
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.flow;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import adams.core.ByteFormat;
import adams.core.DiffUtils;
import adams.core.DiffUtils.SideBySideDiff;
import adams.core.Pausable;
import adams.core.Properties;
import adams.core.StatusMessageHandler;
import adams.core.Stoppable;
import adams.core.Utils;
import adams.core.VariablesHandler;
import adams.core.io.FileUtils;
import adams.core.io.FilenameProposer;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractOptionConsumer;
import adams.core.option.AbstractOptionProducer;
import adams.core.option.NestedConsumer;
import adams.core.option.NestedProducer;
import adams.core.option.OptionConsumer;
import adams.core.option.OptionProducer;
import adams.data.io.input.FlowReader;
import adams.data.io.output.DefaultFlowWriter;
import adams.data.io.output.FlowWriter;
import adams.data.statistics.InformativeStatistic;
import adams.db.LogEntryHandler;
import adams.env.Environment;
import adams.env.FlowEditorPanelDefinition;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.flow.core.ActorStatistic;
import adams.flow.core.ActorUtils;
import adams.flow.core.AutomatableInteractiveActor;
import adams.flow.processor.AbstractActorProcessor;
import adams.flow.processor.CheckVariableUsage;
import adams.flow.processor.ManageInteractiveActors;
import adams.gui.core.BaseDialog;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.GUIHelper;
import adams.gui.core.MouseUtils;
import adams.gui.core.ParameterPanel;
import adams.gui.core.RecentFilesHandler;
import adams.gui.core.TitleGenerator;
import adams.gui.core.Undo.UndoPoint;
import adams.gui.core.UndoPanel;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.dialog.TextDialog;
import adams.gui.event.ActorChangeEvent;
import adams.gui.event.ActorChangeListener;
import adams.gui.event.UndoEvent;
import adams.gui.flow.tree.Node;
import adams.gui.flow.tree.Tree;
import adams.gui.sendto.SendToActionSupporter;
import adams.gui.sendto.SendToActionUtils;
import adams.gui.tools.LogEntryViewerPanel;
import adams.gui.tools.VariableManagementPanel;
import adams.gui.visualization.debug.SideBySideDiffPanel;
import adams.gui.visualization.statistics.InformativeStatisticFactory;

/**
 * A panel for setting up, modifying, saving and loading "simple" flows.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FlowPanel
  extends UndoPanel
  implements StatusMessageHandler, SendToActionSupporter {

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

  /**
   * Specialized worker class for executing a flow.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class FlowWorker
    extends SwingWorker
    implements Pausable, Stoppable, StatusMessageHandler {

    /** the panel this flow belongs to. */
    protected FlowPanel m_Owner;
    
    /** the flow to execute. */
    protected AbstractActor m_Flow;
    
    /** the current flow file. */
    protected File m_File;

    /** generated output. */
    protected String m_Output;
    
    /** whether to show a notification. */
    protected boolean m_ShowNotification;

    /** whether the flow is still being executed. */
    protected boolean m_Running;

    /** whether the flow is being stopped. */
    protected boolean m_Stopping;
    
    /**
     * Initializes the worker.
     */
    public FlowWorker(FlowPanel owner, AbstractActor flow, File file, boolean showNotification) {
      m_Owner            = owner;
      m_Flow             = flow;
      m_File             = file;
      m_ShowNotification = showNotification;
      m_Running          = false;
      m_Stopping         = false;
    }
    
    /**
     * Executes the flow.
     *
     * @return always null
     * @throws Exception if unable to compute a result
     */
    @Override
    protected Object doInBackground() throws Exception {
      m_Owner.update();
      m_Owner.cleanUp();
      
      m_Running = true;
      m_Owner.update();

      try {
	showStatus("Initializing");
	if (m_Flow instanceof Flow)
	  ((Flow) m_Flow).setParentComponent(m_Owner);
	m_Flow.setHeadless(m_Owner.isHeadless());
	m_Flow = ActorUtils.removeDisabledActors(m_Flow);
	m_Output      = m_Flow.setUp();
	if ((m_Output == null) && !m_Flow.isStopped()) {
	  if (m_Flow instanceof VariablesHandler) {
	    if (ActorUtils.updateVariablesWithFlowFilename((VariablesHandler) m_Flow, m_File)) {
	      if (m_Owner.isModified())
		m_Flow.getLogger().warning("Flow '" + m_File + "' not saved, flow variables like '" + ActorUtils.FLOW_DIR + "' might not be accurate!");
	    }
	  }
	  
	  showStatus("Running");
	  m_Output = m_Flow.execute();
	  // did the flow get stopped by a critical actor?
	  if ((m_Output == null) && m_Flow.hasStopMessage())
	    m_Output = m_Flow.getStopMessage();
	  
	  // was flow stopped externally and we need to wait for it to finish?
	  if (m_Stopping) {
	    while (!m_Flow.isStopped()) {
	      try {
		synchronized(this) {
		  wait(100);
		}
	      }
	      catch (Exception e) {
		// ignored
	      }
	    }
	  }
	}
      }
      catch (Throwable e) {
	e.printStackTrace();
	m_Output = Utils.throwableToString(e);
      }

      if ((m_Owner.getVariablesPanel() != null) && (m_Owner.getVariablesPanel().getParentDialog() != null))
	m_Owner.getVariablesPanel().getParentDialog().setVisible(false);
      
      return null;
    }

    /**
     * Executed on the <i>Event Dispatch Thread</i> after the {@code doInBackground}
     * method is finished. 
     */
    @Override
    protected void done() {
      String	msg;
      String	errors;
      int	countErrors;

      super.done();

      showStatus("Finishing up");
      m_Flow.wrapUp();

      m_Owner.setLastFlow(m_Flow);
      m_Flow = null;
      errors = null;

      if (m_Owner.getLastFlow() instanceof LogEntryHandler) {
	countErrors = ((LogEntryHandler) m_Owner.getLastFlow()).countLogEntries();
	if (countErrors > 0)
	  errors = countErrors + " error(s) logged";
      }

      if (m_Output != null) {
	msg = "Finished with error: " + m_Output;
	if (errors != null)
	  msg += "(" + errors + ")";
	showStatus(msg);
	if (m_ShowNotification)
	  showMessage(m_Output, true);
      }
      else {
	if (m_Running)
	  msg = "Flow finished.";
	else
	  msg = "User stopped flow.";
	if (errors != null)
	  msg += " " + errors + ".";
	showStatus(msg);
	if (m_ShowNotification) {
	  if (m_Running)
	    GUIHelper.showInformationMessage(m_Owner.getOwner(), msg);
	  else
	    GUIHelper.showErrorMessage(m_Owner.getOwner(), msg);
	}
      }

      m_Running  = false;
      m_Stopping = false;

      System.gc();
      m_Owner.update();
      m_Owner.finishedExecution();
    }

    /**
     * Pauses the execution.
     */
    @Override
    public void pauseExecution() {
      showStatus("Pausing");
      ((Pausable) m_Flow).pauseExecution();
      m_Owner.update();
    }

    /**
     * Returns whether the object is currently paused.
     *
     * @return		true if object is paused
     */
    @Override
    public boolean isPaused() {
      return ((Pausable) m_Flow).isPaused();
    }

    /**
     * Resumes the execution.
     */
    @Override
    public void resumeExecution() {
      showStatus("Resuming");
      ((Pausable) m_Flow).resumeExecution();
      m_Owner.update();
    }

    /**
     * Stops the execution.
     */
    @Override
    public void stopExecution() {
      m_Stopping = true;
      m_Running  = false;
      showStatus("Stopping");
      m_Owner.update();
      m_Flow.stopExecution();
    }

    /**
     * Returns whether a flow is currently running.
     *
     * @return		true if a flow is being executed
     */
    public boolean isRunning() {
      return m_Running;
    }

    /**
     * Returns whether a flow is currently being stopped.
     *
     * @return		true if a flow is currently being stopped
     */
    public boolean isStopping() {
      return m_Stopping;
    }

    /**
     * Displays a message.
     * 
     * @param msg		the message to display
     */
    @Override
    public void showStatus(String msg) {
      m_Owner.showStatus(msg);
    }

    /**
     * Displays the given message in a separate dialog.
     *
     * @param msg		the message to display
     * @param isError	whether it is an error message
     */
    public void showMessage(String msg, boolean isError) {
      m_Owner.showMessage(msg, isError);
    }
  }
  
  /** the properties. */
  protected static Properties m_Properties;

  /** the owner. */
  protected FlowTabbedPane m_Owner;

  /** the current flow. */
  protected AbstractActor m_CurrentFlow;

  /** the last flow that was run. */
  protected AbstractActor m_LastFlow;

  /** the filename of the current flow. */
  protected File m_CurrentFile;

  /** the current worker thread. */
  protected FlowWorker m_CurrentWorker;
  
  /** whether a swingworker is currently running. */
  protected boolean m_RunningSwingWorker;
  
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

    m_CurrentFlow         = null;
    m_LastFlow            = null;
    m_CurrentFile         = null;
    m_RecentFilesHandler  = null;
    m_CurrentWorker       = null;
    m_LastVariableSearch  = "";
    m_TitleGenerator      = new TitleGenerator(FlowEditorPanel.DEFAULT_TITLE, true);
    m_FilenameProposer    = new FilenameProposer(PREFIX_NEW, AbstractActor.FILE_EXTENSION, getProperties().getPath("InitialDir", "%h"));
    m_Title               = "";
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    Properties			props;

    super.initGUI();

    props = getProperties();

    setLayout(new BorderLayout());

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
    m_Tree.setStateUsesNested(props.getBoolean("Tree.StateUsesNested", true));
    m_Tree.setIgnoreNameChanges(props.getBoolean("Tree.IgnoreNameChanges", false));
    m_Tree.setIconScaleFactor(props.getDouble("Tree.IconScaleFactor", 1.0));
    m_Tree.setVariableHighlightBackground(props.getProperty("VariableHighlight.Background", "#FFDD88"));
    m_Tree.setShowQuickInfo(props.getBoolean("ShowQuickInfo", true));
    m_Tree.setShowAnnotations(props.getBoolean("ShowAnnotations", true));
    m_Tree.setShowInputOutput(props.getBoolean("ShowInputOutput", false));
    m_Tree.setInputOutputPrefixes(props.getProperty("Tree.InputOutput.Prefixes", "java.lang.,java.io.,adams.core.io.,adams.flow.core.,adams.flow.container.").replace(" ", "").split(","));
    m_Tree.addActorChangeListener(new ActorChangeListener() {
      @Override
      public void actorChanged(ActorChangeEvent e) {
	update();
      }
    });
    m_Tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
      @Override
      public void valueChanged(TreeSelectionEvent e) {
	if (m_Tree.getSelectionPath() != null)
	  showStatus(m_Tree.getSelectedFullName());
      }
    });
    add(new BaseScrollPane(m_Tree), BorderLayout.CENTER);

    // the tabs
    m_Tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
      @Override
      public void valueChanged(TreeSelectionEvent e) {
	if (getEditor() != null)
	  getEditor().getTabs().notifyTabs(m_Tree.getSelectionPaths(), m_Tree.getSelectedActors());
      }
    });
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
  protected void update() {
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
   * Updates the title of the dialog/frame.
   */
  public void updateTitle() {
    int		index;

    setParentTitle(m_TitleGenerator.generate(getCurrentFile(), getTree().isModified()));

    if (getOwner() != null) {
      index = getOwner().indexOfComponent(this);
      if (index != -1)
	getOwner().setTitleAt(index, (isModified() ? "*" : "") + getTitle());
    }
  }

  /**
   * Resets the GUI to default values.
   *
   * @param actor	the actor to instantiate
   */
  public void reset(AbstractActor actor) {
    addUndoPoint("Saving undo data...", "New " + actor.getClass().getName().replaceAll(".*\\.", ""));

    cleanUp();

    m_CurrentFlow = null;
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
   * Loads a flow.
   *
   * @param reader	the reader to use
   * @param file	the flow to load
   */
  public void load(final FlowReader reader, final File file) {
    SwingWorker		worker;

    m_RunningSwingWorker = true;
    worker = new SwingWorker() {
      protected AbstractActor m_Flow = null;
      protected List<String> m_Errors;
      protected List<String> m_Warnings;

      @Override
      protected Object doInBackground() throws Exception {
	m_Errors   = new ArrayList<String>();
	m_Warnings = new ArrayList<String>();

	cleanUp();
	update();

	addUndoPoint("Saving undo data...", "Loading '" + file.getName() + "'");
	showStatus("Loading '" + file + "'...");

	m_Flow = (AbstractActor) reader.read(file);
	m_Errors.addAll(reader.getErrors());
	m_Warnings.addAll(reader.getWarnings());
	setCurrentFlow(m_Flow);
	m_Tree.redraw();

	showStatus("");

        return null;
      }

      @Override
      protected void done() {
	m_RunningSwingWorker = false;

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
	if (!m_Warnings.isEmpty())
	  GUIHelper.showErrorMessage(
	      m_Owner, "Warning(s) encountered while loading flow '" + file + "':\n" + Utils.flatten(m_Warnings, "\n"));

	update();

        super.done();
      }
    };
    worker.execute();
  }

  /**
   * Sets the flow to work on.
   *
   * @param flow	the flow to use
   */
  public void setCurrentFlow(AbstractActor flow) {
    m_CurrentFlow = flow;

    if (flow != null) {
      getTree().setActor(flow);
      getTree().setModified(false);
    }

    setCurrentFile(null);
    setTitle(PREFIX_FLOW + next());
  }

  /**
   * Returns the current flow.
   * <p/>
   * WARNING: Recreates an actor hierarchy based on the tree. Method gets very
   * slow for large flows. If you only need the root actor, then use getCurrentRoot()
   * instead.
   *
   * @return		the current flow
   */
  public AbstractActor getCurrentFlow() {
    return getCurrentFlow(null);
  }

  /**
   * Returns the current flow.
   * <p/>
   * WARNING: Recreates an actor hierarchy based on the tree. Method gets very
   * slow for large flows. If you only need the root actor, then use getCurrentRoot()
   * instead.
   *
   * @param errors	for storing potential errors, use null to ignore
   * @return		the current flow
   */
  public AbstractActor getCurrentFlow(StringBuilder errors) {
    return getTree().getActor(errors);
  }

  /**
   * Returns the current root actor without its children.
   *
   * @return		the current root actor
   * @see		#getCurrentFlow()
   */
  public AbstractActor getCurrentRoot() {
    return getTree().getRootActor();
  }

  /**
   * Returns the currently running flow.
   *
   * @return		the running flow, null if none running right now
   * @see		#isRunning()
   */
  public AbstractActor getRunningFlow() {
    if (isRunning())
      return m_CurrentFlow;
    else
      return null;
  }

  /**
   * Sets the flow that was last executed.
   * 
   * @param value	the flow
   */
  protected void setLastFlow(AbstractActor value) {
    m_LastFlow = value;
  }
  
  /**
   * Returns the last executed flow (if any).
   *
   * @return		the flow, null if not available
   */
  public AbstractActor getLastFlow() {
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

    worker = new SwingWorker() {
      boolean m_Result;

      @Override
      protected Object doInBackground() throws Exception {
	showStatus("Saving '" + file + "'...");
	m_Result = writer.write(getCurrentFlow(), file);
	showStatus("");
        return null;
      }

      @Override
      protected void done() {
	if (!m_Result)
	  GUIHelper.showErrorMessage(
	      m_Owner, "Error saving flow to '" + file.getAbsolutePath() + "'!");
	else {
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
    AbstractActor		actor;

    actor = (AbstractActor) consumer.fromFile(file);
    if (actor == null) {
      showMessage("Failed to load flow from:\n" + file, true);
    }
    else {
      getTree().setActor(actor);
      setCurrentFile(new PlaceholderFile(file.getAbsolutePath() + "." + AbstractActor.FILE_EXTENSION));
      if (!consumer.hasErrors())
	showMessage("Flow successfully imported from:\n" + file, false);
      else
	showMessage("Flow import of:\n" + file + "\nResulted in errors:\n" + Utils.flatten(consumer.getErrors(), "\n"), true);
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
      showMessage("Failed to export flow to:\n" + file, true);
    }
    else {
      showMessage("Flow successfully exported to:\n" + file, false);
    }
  }

  /**
   * Validates the current setup.
   */
  public void validateSetup() {
    AbstractActor	actor;
    StringBuilder	errors;
    String		msg;

    msg    = null;
    errors = new StringBuilder();
    actor  = getCurrentFlow(errors);
    if (errors.length() > 0)
      msg = errors.toString();

    if (msg == null) {
      try {
	msg = actor.setUp();
	actor.wrapUp();
	actor.cleanUp();
      }
      catch (Exception e) {
	msg = "Actor generated exception: ";
	System.err.println(msg);
	e.printStackTrace();
	msg += e;
      }
    }

    // perform some checks
    if (msg == null)
      msg = ActorUtils.checkFlow(actor);

    if (msg == null) {
      msg = "The flow passed validation!";
      showStatus(msg);
      showMessage(msg, false);
    }
    else {
      showStatus(msg);
      showMessage("The flow setup failed validation:\n" + msg, true);
    }
  }

  /**
   * Executes the flow.
   */
  public void run() {
    run(true);
  }

  /**
   * Executes the flow.
   *
   * @param showNotification	whether to show notifications about
   * 				errors/stopped/finished
   */
  public void run(boolean showNotification) {
    m_CurrentWorker = new FlowWorker(this, getCurrentFlow(), getCurrentFile(), showNotification);
    m_CurrentWorker.execute();
  }

  /**
   * Finishes up the execution, setting the worker to null.
   */
  protected void finishedExecution() {
    m_CurrentWorker = null;
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
    return (m_CurrentWorker != null);
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
	  return null;
	}
      };
      worker.execute();
    }
  }

  /**
   * Displays the errors from the last run.
   */
  public void displayErrors() {
    BaseDialog		dialog;
    LogEntryHandler	handler;
    LogEntryViewerPanel	panel;

    if (m_LastFlow == null)
      return;
    if (!(m_LastFlow instanceof LogEntryHandler))
      return;
    handler = (LogEntryHandler) m_LastFlow;
    if (handler.getLogEntries().size() == 0)
      return;

    if (getParentDialog() != null)
      dialog = new BaseDialog(getParentDialog(), ModalityType.MODELESS);
    else
      dialog = new BaseDialog(getParentFrame(), false);
    dialog.setTitle("Flow execution errors");
    panel = new LogEntryViewerPanel();
    panel.display(handler.getLogEntries());
    dialog.getContentPane().setLayout(new BorderLayout());
    dialog.getContentPane().add(panel, BorderLayout.CENTER);
    dialog.setSize(new Dimension(800, 600));
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
  }

  /**
   * Cleans up the last flow that was run.
   */
  public void cleanUp() {
    if (m_LastFlow != null) {
      showStatus("Cleaning up");
      try {
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
    if (m_Owner != null)
      m_Owner.remove(this);
  }

  /**
   * Displays statistics about the current flow.
   */
  public void showStatistics() {
    ActorStatistic			stats;
    InformativeStatisticFactory.Dialog	dialog;
    Vector<InformativeStatistic>	statsList;

    if (getTree().getSelectedNode() != null)
      stats = new ActorStatistic(getTree().getSelectedNode().getFullActor());
    else if (m_CurrentFlow != null)
      stats = new ActorStatistic(m_CurrentFlow);
    else
      stats = new ActorStatistic(getCurrentFlow());
    statsList = new Vector<InformativeStatistic>();
    statsList.add(stats);

    if (getParentDialog() != null)
      dialog = InformativeStatisticFactory.getDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else
      dialog = InformativeStatisticFactory.getDialog(getParentFrame(), true);
    dialog.setStatistics(statsList);
    dialog.setTitle("Actor statistics");
    dialog.pack();
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
  }

  /**
   * Shows the properties of the flow.
   */
  public void showProperties() {
    ApprovalDialog	dialog;
    ParameterPanel	params;
    String		file;
    String		size;
    JButton		buttonStats;
    final JTextField	textFile;
    JTextField		textSize;

    if (getCurrentFile() != null)
      file = getCurrentFile().toString();
    else
      file = "N/A";
    if ((getCurrentFile() != null) && !isModified())
      size = ByteFormat.toKiloBytes(getCurrentFile().length(), 1);
    else
      size = "N/A";
    buttonStats = new JButton("Display", GUIHelper.getIcon("statistics.png"));
    buttonStats.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	showStatistics();
      }
    });

    params = new ParameterPanel();
    textFile = new JTextField(file, 20);
    textFile.setEditable(false);
    textFile.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
	if (MouseUtils.isRightClick(e)) {
	  e.consume();
	  JPopupMenu menu = new JPopupMenu();
	  JMenuItem menuitem = new JMenuItem("Copy", GUIHelper.getIcon("copy.gif"));
	  menuitem.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
	      GUIHelper.copyToClipboard(textFile.getText());
	    }
	  });
	  menu.add(menuitem);
	  menu.show(textFile, e.getX(), e.getY());
	}
	else {
	  super.mouseClicked(e);
	}
      }
    });
    params.addParameter("File", textFile);
    textSize = new JTextField(size, 7);
    textSize.setEditable(false);
    params.addParameter("Size", textSize);
    params.addParameter("Statistics", buttonStats);

    if (getParentDialog() != null)
      dialog = new ApprovalDialog(getParentDialog());
    else
      dialog = new ApprovalDialog(getParentFrame());
    dialog.setTitle("Properties");
    dialog.setCancelVisible(false);
    dialog.setApproveVisible(true);
    dialog.setDiscardVisible(false);
    dialog.getContentPane().add(params, BorderLayout.CENTER);
    dialog.pack();
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
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
	getTree().setState((Vector) point.getData());
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
	getTree().setState((Vector) point.getData());
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
   * Searches for actor names in the tree.
   */
  public void find() {
    getTree().find();
  }

  /**
   * Searches for the next actor in the tree.
   */
  public void findNext() {
    getTree().findNext();
  }

  /**
   * Locates an actor based on the full actor name.
   */
  public void locateActor() {
    String	path;

    path = JOptionPane.showInputDialog("Please enter the full name of the actor (e.g., 'Flow[0].Sequence[3].Display'):");
    if (path == null)
      return;

    getTree().locateAndDisplay(path);
  }

  /**
   * Highlights variables in the tree (or hides the highlights again).
   *
   * @param highlight	whether to turn the highlights on or off
   */
  public void highlightVariables(boolean highlight) {
    String	regexp;

    if (highlight) {
      regexp = JOptionPane.showInputDialog(
	  GUIHelper.getParentComponent(this),
	  "Enter the regular expression for the variable name ('.*' matches all):",
	  m_LastVariableSearch);
      if (regexp == null)
	return;

      m_LastVariableSearch = regexp;
      getTree().highlightVariables(m_LastVariableSearch);
    }
    else {
      getTree().highlightVariables(null);
    }
  }

  /**
   * Cleans up the flow, e.g., removing disabled actors, unused global actors.
   *
   * @see		ActorUtils#cleanUpFlow(AbstractActor)
   */
  public void cleanUpFlow() {
    AbstractActor	cleaned;

    cleaned = ActorUtils.cleanUpFlow(getCurrentFlow());

    if (cleaned != null) {
      addUndoPoint("Saving undo data...", "Cleaning up");
      getTree().buildTree(cleaned);
      getTree().setModified(true);
      update();
    }
  }

  /**
   * Checks the variable usage, i.e., all variables must at least be set
   * once somewhere in the flow.
   */
  protected void checkVariables() {
    CheckVariableUsage	processor;
    final BaseDialog	dialog;
    JPanel		panel;
    JButton		button;

    processor = new CheckVariableUsage();
    processor.process(getCurrentFlow());
    if (processor.hasGraphicalOutput()) {
      if (getParentDialog() != null)
	dialog = new BaseDialog(getParentDialog());
      else
	dialog = new BaseDialog(getParentFrame());
      dialog.setTitle(processor.getClass().getSimpleName());
      dialog.getContentPane().setLayout(new BorderLayout());
      dialog.getContentPane().add(processor.getGraphicalOutput(), BorderLayout.CENTER);
      button = new JButton("Close");
      button.setMnemonic('C');
      button.addActionListener(new ActionListener() {
        @Override
	public void actionPerformed(ActionEvent e) {
          dialog.setVisible(false);
        }
      });
      panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      panel.add(button);
      dialog.getContentPane().add(panel, BorderLayout.SOUTH);
      dialog.pack();
      dialog.setLocationRelativeTo(this);
      dialog.setVisible(true);
    }
    else {
      showMessage("Basic check passed!\nAll variables get at least set once in the flow.", false);
    }
  }

  /**
   * Enables/disables the interactive behaviour of {@link AutomatableInteractiveActor}
   * actors.
   *
   * @param enable	true if to enable the interactive behaviour
   * @see		AutomatableInteractiveActor
   */
  public void manageInteractiveActors(boolean enable) {
    ManageInteractiveActors	processor;

    processor = new ManageInteractiveActors();
    processor.setEnable(enable);
    processor.process(getCurrentFlow());
    if (processor.isModified()) {
      addUndoPoint("Saving undo data...", (enable ? "Enable" : "Disable") + " interactive behaviour");
      getTree().buildTree(processor.getModifiedActor());
      getTree().setModified(true);
      update();
    }
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
   * process below this actor instead of full flow (user gets prompted
   * by GOE dialog).
   *
   * @see	#processActors()
   * @see	#processSelectedActor()
   */
  public void processActorsPrompt() {
    processActorsPrompt(null);
  }

  /**
   * If a single actor is selected, user gets prompted whether to only
   * process below this actor instead of full flow.
   *
   * @param processor	the processor to use, null if to prompt user
   * @see		#processActors()
   * @see		#processSelectedActor()
   */
  public void processActorsPrompt(AbstractActorProcessor processor) {
    boolean 	selected;
    int		retVal;

    if ((getTree().getSelectionCount() == 1) && !getTree().isRootSelected()) {
      retVal = GUIHelper.showConfirmMessage(
	  this,
	  "Process only below selected actor instead of complete flow?\n"
	      + getTree().getSelectedFullName());
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
   * Processes the actors with a user-specified actor processor
   * (user gets prompted with GOE dialog).
   */
  public void processActors() {
    processActors(null);
  }

  /**
   * Processes the actors with the specified actor processor.
   *
   * @param processor	the processor to use, null if to prompt user
   */
  public void processActors(AbstractActorProcessor processor) {
    if (getTree().processActor(null, processor))
      showMessage("Actors processed!", false);
  }

  /**
   * Processes the selected actor with a user-specified actor processor
   * (the user gets prompted with GOE dialog).
   * NB: The options of the selected actor will get processed.
   */
  public void processSelectedActor() {
    processSelectedActor(null);
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
    if (getTree().processActor(path, processor))
      showMessage("Actor " + node.getActor().getName() + " processed!", false);
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
  protected VariableManagementPanel getVariablesPanel() {
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

    m_PanelVariables.setVariables(m_CurrentFlow.getVariables());
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

    m_PanelStorage.setHandler(m_CurrentFlow.getStorageHandler());
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
   * Displays the source code (in nested format) of the current flow.
   */
  public void showSource() {
    TextDialog 	dialog;
    String 	buffer;

    buffer = AbstractOptionProducer.toString(NestedProducer.class, getCurrentFlow());

    if (getParentDialog() != null)
      dialog = new TextDialog(getParentDialog());
    else
      dialog = new TextDialog(getParentFrame());
    dialog.setTitle(m_TitleGenerator.generate(getCurrentFile(), getTree().isModified()) + " [Source]");
    dialog.setTabSize(2);
    dialog.setContent(buffer);
    dialog.setLocationRelativeTo(getTree());
    dialog.setVisible(true);
  }

  /**
   * Displays the given message in a separate dialog.
   *
   * @param msg		the message to display
   * @param isError	whether it is an error message
   */
  protected void showMessage(String msg, boolean isError) {
    String	status;

    status = msg.replaceAll(": ", ":\n");

    if (isError)
      GUIHelper.showErrorMessage(m_Owner, status, "Error");
    else
      GUIHelper.showInformationMessage(m_Owner, status, "Status");
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
    AbstractActor	actor;
    DefaultFlowWriter	writer;

    result = null;

    if (SendToActionUtils.isAvailable(PlaceholderFile.class, cls)) {
      if (getTree().isModified()) {
	result = SendToActionUtils.nextTmpFile("floweditor", "flow");
	actor  = getCurrentFlow();
	writer = new DefaultFlowWriter();
	writer.write(actor, (PlaceholderFile) result);
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
   * Whether a diff between current flow and undo-flow can be generated.
   *
   * @return		true if diff can be generated
   */
  public boolean canDiff() {
    return m_Undo.isEnabled() && !m_Undo.isWorking() && m_Undo.canUndo();
  }

  /**
   * Generates a diff between the current flow and the closest undo step.
   *
   * @return		the diff, null if failed to generate
   */
  public SideBySideDiff getDiff() {
    SideBySideDiff	result;
    String		current;
    String		prev;
    Vector		state;
    AbstractActor	actor;

    if (!canDiff())
      return null;

    // current flow
    current = AbstractOptionProducer.toString(NestedProducer.class, getCurrentFlow()).replace("\t", "  ");

    // undo step
    state = (Vector) m_Undo.peekUndo().getData();
    if (state.get(0) == null)
      return null;
    if (state.get(0) instanceof AbstractActor)
      actor = (AbstractActor) state.get(0);
    else
      actor = (AbstractActor) AbstractOptionConsumer.consume(NestedConsumer.class, state.get(0));
    prev = AbstractOptionProducer.toString(NestedProducer.class, actor).replace("\t", "  ");

    // generate diff
    result = DiffUtils.sideBySide(prev.split("\n"), current.split("\n"));

    return result;
  }

  /**
   * Displays (if possible) a diff between current flow and last item in undo list.
   */
  public void showDiff() {
    SideBySideDiff	diff;
    ApprovalDialog	dialog;
    SideBySideDiffPanel	panel;

    if (!canDiff())
      return;

    diff = getDiff();
    if (diff == null) {
      GUIHelper.showErrorMessage(this, "Failed to compute differences!");
      return;
    }

    if (getParentDialog() != null)
      dialog = new ApprovalDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new ApprovalDialog(getParentFrame(), true);
    dialog.setTitle("Differences");
    panel = new SideBySideDiffPanel();
    panel.setLabelText(true, "Previous");
    panel.setLabelText(false, "Current");
    panel.display(diff);
    dialog.getContentPane().add(panel, BorderLayout.CENTER);
    dialog.setSize(800, 600);
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
  }

  /**
   * Redraws the tree.
   */
  public void redraw() {
    m_Tree.redraw();
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
