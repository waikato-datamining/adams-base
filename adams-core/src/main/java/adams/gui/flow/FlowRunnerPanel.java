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
 * FlowRunnerPanel.java
 * Copyright (C) 2010-2017 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow;

import adams.core.Pausable;
import adams.core.Properties;
import adams.core.StatusMessageHandler;
import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.core.logging.LoggingLevel;
import adams.core.net.HtmlUtils;
import adams.data.io.input.FlowReader;
import adams.db.LogEntryHandler;
import adams.env.Environment;
import adams.env.FlowRunnerPanelDefinition;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.flow.core.ActorHandler;
import adams.flow.standalone.SetVariable;
import adams.flow.standalone.Standalones;
import adams.gui.action.AbstractBaseAction;
import adams.gui.action.ToggleAction;
import adams.gui.application.ChildFrame;
import adams.gui.application.ChildWindow;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.chooser.FlowFileChooser;
import adams.gui.core.BaseDialog;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.BaseStatusBar;
import adams.gui.core.BaseStatusBar.StatusProcessor;
import adams.gui.core.ConsolePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.MenuBarProvider;
import adams.gui.core.RecentFilesHandler;
import adams.gui.core.TitleGenerator;
import adams.gui.core.ToolBarPanel;
import adams.gui.event.RecentItemEvent;
import adams.gui.event.RecentItemListener;
import adams.gui.flow.tree.Node;
import adams.gui.tools.LogEntryViewerPanel;
import adams.gui.tools.VariableManagementPanel;
import adams.gui.visualization.debug.StoragePanel;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Panel that allows the execution of flows.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FlowRunnerPanel
  extends ToolBarPanel
  implements MenuBarProvider, StatusMessageHandler, FlowWorkerHandler {

  /** for serialization. */
  private static final long serialVersionUID = -4599322589770009727L;

  /** the name of the props file. */
  public final static String FILENAME = "FlowRunner.props";

  /** the file to store the recent files in. */
  public final static String SESSION_FILE = "FlowRunnerSession.props";

  /** the properties. */
  protected static Properties m_Properties;

  /** whether a flow is currently being loaded, etc. using a SwingWorker. */
  protected boolean m_RunningSwingWorker;

  /** the current flow. */
  protected Actor m_CurrentFlow;

  /** the filename of the current flow. */
  protected File m_CurrentFile;

  /** the filedialog for loading/saving flows. */
  protected FlowFileChooser m_FileChooser;

  /** the status. */
  protected BaseStatusBar m_StatusBar;

  /** the panel itself. */
  protected FlowRunnerPanel m_Self;

  /** for generating the title. */
  protected TitleGenerator m_TitleGenerator;

  /** the menu bar, if used. */
  protected JMenuBar m_MenuBar;

  /** the "load" action. */
  protected AbstractBaseAction m_ActionLoad;

  /** the "load recent" submenu. */
  protected JMenu m_MenuItemLoadRecent;

  /** the "close" action. */
  protected AbstractBaseAction m_ActionClose;

  /** the "headless" item. */
  protected AbstractBaseAction m_ActionHeadless;

  /** the "run" item. */
  protected AbstractBaseAction m_ActionRun;

  /** the "pause" item. */
  protected AbstractBaseAction m_ActionPauseAndResume;

  /** the "stop" action. */
  protected AbstractBaseAction m_ActionStop;

  /** the "log errors" action. */
  protected AbstractBaseAction m_ActionExecutionLogErrors;

  /** the "display errors" action. */
  protected AbstractBaseAction m_ActionExecutionDisplayErrors;

  /** the "new window" action. */
  protected AbstractBaseAction m_ActionNewWindow;

  /** the "duplicate window" action. */
  protected AbstractBaseAction m_ActionDuplicateWindow;

  /** the recent files handler. */
  protected RecentFilesHandler<JMenu> m_RecentFilesHandler;

  /** the scroll pane for the parameters. */
  protected BaseScrollPane m_ParameterScrollPane;

  /** the panel for the SetVariable singletons. */
  protected JPanel m_PanelParameters;

  /** the label for the flow annotation. */
  protected JLabel m_LabelFlowAnnotation;

  /** the current SetVariable singletons (used for displaying the parameters). */
  protected Vector<SetVariable> m_CurrentSetVariables;

  /** the textfields for the current SetVariable singletons. */
  protected Vector<JTextField> m_CurrentParameters;

  /** the buttons for the current SetVariable singletons. */
  protected Vector<JButton> m_CurrentHelpButtons;

  /** the current worker. */
  protected FlowWorker m_CurrentWorker;

  /** the current worker thread. */
  protected Thread m_CurrentThread;

  /** the last flow that was run. */
  protected Actor m_LastFlow;

  /** the panel for showing notifications. */
  protected FlowPanelNotificationArea m_PanelNotification;

  /** the split pane to use for tree and notification area. */
  protected BaseSplitPane m_SplitPane;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Self                = this;
    m_CurrentFlow         = null;
    m_CurrentFile         = null;
    m_RecentFilesHandler  = null;
    m_TitleGenerator      = new TitleGenerator("Flow runner", true);
    m_CurrentSetVariables = new Vector<SetVariable>();
    m_CurrentParameters   = new Vector<JTextField>();
    m_CurrentHelpButtons  = new Vector<JButton>();
    m_FileChooser         = new FlowFileChooser();
    m_FileChooser.setCurrentDirectory(new PlaceholderFile(getProperties().getPath("InitialDir", "%h")));
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel		panel;
    Properties		props;

    super.initGUI();

    setLayout(new BorderLayout());

    m_SplitPane = new BaseSplitPane(BaseSplitPane.VERTICAL_SPLIT);
    m_SplitPane.setResizeWeight(1.0);
    m_SplitPane.setOneTouchExpandable(true);
    add(m_SplitPane, BorderLayout.CENTER);

    props = getProperties();
    setToolBarLocation(ToolBarLocation.valueOf(props.getProperty("ToolBar.Location", "NORTH")));

    // parameters
    panel                 = new JPanel(new BorderLayout());
    m_LabelFlowAnnotation = new JLabel();
    m_PanelParameters     = new JPanel();
    m_ParameterScrollPane = new BaseScrollPane(m_PanelParameters);
    m_ParameterScrollPane.setBorder(BorderFactory.createEmptyBorder());
    panel.add(m_ParameterScrollPane, BorderLayout.CENTER);
    panel.add(m_LabelFlowAnnotation, BorderLayout.NORTH);
    m_SplitPane.setTopComponent(panel);

    // the status
    m_StatusBar = new BaseStatusBar();
    m_StatusBar.setDialogSize(new Dimension(props.getInteger("StatusBar.Width", 600), props.getInteger("StatusBar.Height", 400)));
    m_StatusBar.setMouseListenerActive(true);
    m_StatusBar.setStatusProcessor(new StatusProcessor() {
      public String process(String msg) {
        return msg.replace(": ", ":\n");
      }
    });
    add(m_StatusBar, BorderLayout.SOUTH);

    m_PanelNotification = new FlowPanelNotificationArea();
    m_PanelNotification.setOwner(this);
    m_PanelNotification.addCloseListener((ActionEvent e) -> clearNotification());
    m_SplitPane.setBottomComponent(m_PanelNotification);
    m_SplitPane.setBottomComponentHidden(true);

    reset();
  }

  /**
   * Initializes the actions.
   */
  @Override
  @SuppressWarnings("serial")
  protected void initActions() {
    AbstractBaseAction	action;

    // File/Open setup
    action = new AbstractBaseAction("Open...", "open.gif") {
      @Override
      protected void doActionPerformed(ActionEvent e) {
	open();
      }
    };
    action.setMnemonic(KeyEvent.VK_O);
    action.setAccelerator("ctrl pressed O");
    m_ActionLoad = action;

    // File/Close
    action = new AbstractBaseAction("Close", "exit.png") {
      @Override
      protected void doActionPerformed(ActionEvent e) {
	close();
      }
    };
    action.setMnemonic(KeyEvent.VK_C);
    action.setAccelerator("ctrl pressed Q");
    m_ActionClose = action;

    // Execution/Run
    action = new AbstractBaseAction("Run", "run.gif") {
      @Override
      protected void doActionPerformed(ActionEvent e) {
	run();
      }
    };
    action.setMnemonic(KeyEvent.VK_R);
    action.setAccelerator("ctrl pressed R");
    m_ActionRun = action;

    // Execution/Run
    action = new AbstractBaseAction("Pause", "pause.gif") {
      @Override
      protected void doActionPerformed(ActionEvent e) {
	pauseAndResume();
      }
    };
    action.setMnemonic(KeyEvent.VK_U);
    action.setAccelerator("ctrl pressed P");
    m_ActionPauseAndResume = action;

    // Execution/Stop
    action = new AbstractBaseAction("Stop", "stop_blue.gif") {
      @Override
      protected void doActionPerformed(ActionEvent e) {
	stop();
      }
    };
    action.setMnemonic(KeyEvent.VK_S);
    action.setAccelerator("ctrl pressed K");
    m_ActionStop = action;

    // Execution/Log errors
    action = new AbstractBaseAction("Log errors") {
      @Override
      protected void doActionPerformed(ActionEvent e) {
	// nothing to do
      }
    };
    action.setMnemonic(KeyEvent.VK_L);
    action.setSelected(false);
    m_ActionExecutionLogErrors = action;

    // Execution/Display errors
    action = new AbstractBaseAction("Display errors", "log.gif") {
      @Override
      protected void doActionPerformed(ActionEvent e) {
	displayErrors();
      }
    };
    action.setMnemonic(KeyEvent.VK_D);
    m_ActionExecutionDisplayErrors = action;

    // Execution/Headless
    action = new ToggleAction("Headless", GUIHelper.getEmptyIcon());
    action.setMnemonic(KeyEvent.VK_H);
    action.setAccelerator("ctrl pressed H");
    m_ActionHeadless = action;

    // Window/New Window
    action = new AbstractBaseAction("New window", "new.gif") {
      @Override
      protected void doActionPerformed(ActionEvent e) {
	newWindow();
      }
    };
    action.setMnemonic(KeyEvent.VK_W);
    m_ActionNewWindow = action;

    // Window/Duplicate Window
    action = new AbstractBaseAction("Duplicate window", "copy.gif") {
      @Override
      protected void doActionPerformed(ActionEvent e) {
	duplicateWindow();
      }
    };
    action.setMnemonic(KeyEvent.VK_D);
    m_ActionDuplicateWindow = action;
  }

  /**
   * Initializes the toolbar.
   */
  @Override
  protected void initToolBar() {
    addToToolBar(m_ActionLoad);
    addToToolBar(m_ActionRun);
    addToToolBar(m_ActionPauseAndResume);
    addToToolBar(m_ActionStop);
  }

  /**
   * Creates a menu bar (singleton per panel object). Can be used in frames.
   *
   * @return		the menu bar
   */
  public JMenuBar getMenuBar() {
    JMenuBar	result;
    JMenu	menu;
    JMenu	submenu;

    if (m_MenuBar == null) {
      // register window listener since we're part of a dialog or frame
      if (getParentFrame() != null) {
	final JFrame frame = (JFrame) getParentFrame();
	frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	frame.addWindowListener(new WindowAdapter() {
	  @Override
	  public void windowClosing(WindowEvent e) {
	    close();
	  }
	});
      }
      else if (getParentDialog() != null) {
	final JDialog dialog = (JDialog) getParentDialog();
	dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
	dialog.addWindowListener(new WindowAdapter() {
	  @Override
	  public void windowClosing(WindowEvent e) {
	    close();
	  }
	});
      }

      result = new JMenuBar();

      // File
      menu = new JMenu("File");
      result.add(menu);
      menu.setMnemonic('F');
      menu.addChangeListener((ChangeEvent e) -> updateActions());

      menu.add(new JMenuItem(m_ActionLoad));

      // File/Recent files
      submenu = new JMenu("Open recent");
      menu.add(submenu);
      m_RecentFilesHandler = new RecentFilesHandler<JMenu>(
	  SESSION_FILE, getProperties().getInteger("MaxRecentFlows", 5), submenu);
      m_RecentFilesHandler.addRecentItemListener(new RecentItemListener<JMenu,File>() {
	public void recentItemAdded(RecentItemEvent<JMenu,File> e) {
	  // ignored
	}
	public void recentItemSelected(RecentItemEvent<JMenu,File> e) {
	  load(m_FileChooser.getReaderForFile(e.getItem()), e.getItem());
	}
      });
      m_MenuItemLoadRecent = submenu;

      menu.addSeparator();
      menu.add(new JMenuItem(m_ActionClose));

      // Execution
      menu = new JMenu("Execution");
      result.add(menu);
      menu.setMnemonic('E');
      menu.addChangeListener((ChangeEvent e) -> updateActions());

      menu.add(new JMenuItem(m_ActionRun));
      menu.add(new JMenuItem(m_ActionPauseAndResume));
      menu.add(new JMenuItem(m_ActionStop));
      menu.add(new JCheckBoxMenuItem(m_ActionExecutionLogErrors));
      menu.add(new JMenuItem(m_ActionExecutionDisplayErrors));
      menu.addSeparator();
      menu.add(new JCheckBoxMenuItem(m_ActionHeadless));

      // Window
      if ((GUIHelper.getParent(m_Self, ChildFrame.class) != null) && (getParentDialog() == null)) {
	menu = new JMenu("Window");
	result.add(menu);
	menu.setMnemonic('W');
	menu.addChangeListener((ChangeEvent e) -> updateActions());
	menu.add(new JMenuItem(m_ActionNewWindow));
	menu.add(new JMenuItem(m_ActionDuplicateWindow));
      }

      // update menu
      m_MenuBar = result;
      updateActions();
    }
    else {
      result = m_MenuBar;
    }

    return result;
  }

  /**
   * updates the enabled state of the menu items.
   */
  @Override
  protected void updateActions() {
    boolean	inputEnabled;
    Pausable	pausable;

    updateTitle();

    if (m_MenuBar == null)
      return;

    inputEnabled = !isRunning() && !isStopping() && !m_RunningSwingWorker;

    if ((m_CurrentFlow != null) && (m_CurrentFlow instanceof Pausable))
      pausable = (Pausable) m_CurrentFlow;
    else
      pausable = null;

    // File
    m_ActionLoad.setEnabled(inputEnabled);
    m_MenuItemLoadRecent.setEnabled(inputEnabled && (m_RecentFilesHandler.size() > 0));
    m_ActionClose.setEnabled(inputEnabled);

    // Execution
    m_ActionRun.setEnabled(inputEnabled && (m_CurrentFlow instanceof Flow));
    if ((pausable != null) && pausable.isPaused()) {
      m_ActionPauseAndResume.setIcon(GUIHelper.getIcon("resume.gif"));
      m_ActionPauseAndResume.setName("Resume");
    }
    else {
      m_ActionPauseAndResume.setIcon(GUIHelper.getIcon("pause.gif"));
      m_ActionPauseAndResume.setName("Pause");
    }
    m_ActionPauseAndResume.setEnabled(isRunning());
    m_ActionStop.setEnabled(isRunning());
    m_ActionHeadless.setEnabled(inputEnabled);
    m_ActionExecutionLogErrors.setEnabled(inputEnabled);
    m_ActionExecutionDisplayErrors.setEnabled(
	inputEnabled && (m_CurrentFlow != null)
	&& (m_CurrentFlow instanceof LogEntryHandler)
	&& (((LogEntryHandler) m_CurrentFlow).countLogEntries() > 0));
  }

  /**
   * Updates the enabled state of the widgets.
   */
  protected void updateWidgets() {
    boolean	inputEnabled;
    int		i;

    inputEnabled = !isRunning() && !isStopping();

    m_PanelParameters.setEnabled(inputEnabled);
    for (i = 0; i < m_CurrentParameters.size(); i++) {
      m_CurrentParameters.get(i).setEnabled(inputEnabled);
      if (m_CurrentHelpButtons.get(i) != null)
	m_CurrentHelpButtons.get(i).setEnabled(inputEnabled);
    }
  }

  /**
   * updates the enabled state etc. of all the GUI elements.
   */
  public void update() {
    updateActions();
    updateWidgets();
    updateTitle();
    updateAnnotation();
    updateParameters();
  }

  /**
   * Locates all the SetVariable singleton actors below the parent.
   *
   * @param parent	the actor to search below
   * @param list	for storing the SetVariable actors
   */
  protected void findSetVariableActors(Actor parent, Vector<SetVariable> list) {
    int		i;
    Actor 	actor;

    if (parent instanceof ActorHandler) {
      for (i = 0; i < ((ActorHandler) parent).size(); i++) {
	actor = ((ActorHandler) parent).get(i);
	if (actor instanceof SetVariable)
	  list.add((SetVariable) actor);
	else if (actor instanceof Standalones)
	  findSetVariableActors(actor, list);
      }
    }
  }

  /**
   * Updates the top-level annotation.
   *
   * @see		#m_LabelFlowAnnotation
   */
  protected void updateAnnotation() {
    m_LabelFlowAnnotation.setBorder(null);
    m_LabelFlowAnnotation.setText("");

    if (m_CurrentFlow != null) {
      if (!m_CurrentFlow.getAnnotations().isEmpty()) {
	m_LabelFlowAnnotation.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	m_LabelFlowAnnotation.setText(
	    "<html>"
	    + HtmlUtils.toHTML(m_CurrentFlow.getAnnotations().getValue()).replaceAll("\n", "<br>")
	    + "</html>");
      }
    }
  }

  /**
   * Makes any top-level SetVariable singletons in the GUI available.
   *
   * @see		#m_PanelParameters
   */
  protected void updateParameters() {
    int		i;
    JLabel	label;
    JTextField	textfield;
    JButton	buttonHelp;
    char[]	mnemonics;
    String[]	labels;
    JPanel	panel;

    m_PanelParameters.removeAll();
    m_CurrentSetVariables.clear();
    m_CurrentParameters.clear();
    m_CurrentHelpButtons.clear();

    if (isRunning()) {
      m_ParameterScrollPane.setBorder(BorderFactory.createEmptyBorder());
      m_PanelParameters.setLayout(new BorderLayout());
      m_PanelParameters.add(new JLabel("Flow running", JLabel.CENTER), BorderLayout.CENTER);
    }
    else {
      if (m_CurrentFlow == null) {
	m_ParameterScrollPane.setBorder(BorderFactory.createEmptyBorder());
	m_PanelParameters.setLayout(new BorderLayout());
	m_PanelParameters.add(new JLabel("No parameters to configure", JLabel.CENTER), BorderLayout.CENTER);
      }
      else {
	m_ParameterScrollPane.setBorder(BorderFactory.createTitledBorder("Available parameters"));
	findSetVariableActors(m_CurrentFlow, m_CurrentSetVariables);
	m_PanelParameters.setLayout(new GridLayout(m_CurrentSetVariables.size() + 1, 2));

	// determine mnemonics
	labels = new String[m_CurrentSetVariables.size()];
	for (i = 0; i < m_CurrentSetVariables.size(); i++)
	  labels[i] = m_CurrentSetVariables.get(i).getVariableName().getValue();
	mnemonics = GUIHelper.getMnemonics(labels);

	// set up panel
	for (i = 0; i < m_CurrentSetVariables.size(); i++) {
	  // text field
	  textfield = new JTextField(15);
	  textfield.setText(m_CurrentSetVariables.get(i).getVariableValue().getValue());
	  buttonHelp = null;
	  final String annotation = m_CurrentSetVariables.get(i).getAnnotations().getValue();
	  final String variable = m_CurrentSetVariables.get(i).getVariableName().getValue();
	  if (annotation.length() > 0) {
	    buttonHelp = new JButton(GUIHelper.getIcon("help2.png"));
	    buttonHelp.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
		GUIHelper.showInformationMessage(
		  FlowRunnerPanel.this, annotation, "Information on '" + variable + "'");
	      }
	    });
	  }
	  panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	  panel.add(textfield);
	  if (buttonHelp != null)
	    panel.add(buttonHelp);
	  m_CurrentParameters.add(textfield);
	  m_CurrentHelpButtons.add(buttonHelp);

	  // label
	  label = new JLabel(labels[i]);
	  label.setDisplayedMnemonic(mnemonics[i]);
	  label.setLabelFor(textfield);

	  // add to parameter panel
	  m_PanelParameters.add(label);
	  m_PanelParameters.add(panel);
	}
      }
    }
    m_SplitPane.revalidate();
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
   * Updates the title of the dialog.
   */
  protected void updateTitle() {
    if (!m_TitleGenerator.isEnabled())
      return;
    setParentTitle(m_TitleGenerator.generate(m_CurrentFile));
  }

  /**
   * Sets the current file.
   *
   * @param value	the file
   */
  protected void setCurrentFile(File value) {
    m_CurrentFile = value;
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
   * Attempts to load the file. If non-existent, then a new flow will be
   * created and the current filename set to the provided one.
   *
   * @param file	the file to load
   */
  public void loadUnsafe(File file) {
    if (!file.exists()) {
      reset();
      setCurrentFile(new File(file.getAbsolutePath()));
      updateActions();
    }
    else {
      load(m_FileChooser.getReaderForFile(file), file, false);
    }
  }

  /**
   * Attempts to load/run the file. If non-existent, then a new flow will be
   * created and the current filename set to the provided one.
   *
   * @param file	the file to load/run
   */
  public void runUnsafe(File file) {
    if (!file.exists()) {
      reset();
      setCurrentFile(new File(file.getAbsolutePath()));
      updateActions();
    }
    else {
      load(m_FileChooser.getReaderForFile(file), file, true);
    }
  }

  /**
   * Loads a flow.
   *
   * @param reader	the reader to use
   * @param file	the flow to load
   */
  protected void load(final FlowReader reader, final File file) {
    load(reader, file, false);
  }

  /**
   * Loads a flow.
   * 
   * @param reader	the reader to use
   * @param file	the flow to load
   */
  protected void load(final FlowReader reader, final File file, final boolean execute) {
    SwingWorker		worker;

    m_RunningSwingWorker = true;
    worker = new SwingWorker() {
      Node 		m_Flow;
      List<String> 	m_Errors;
      List<String> 	m_Warnings;

      @Override
      protected Object doInBackground() throws Exception {
	cleanUp(true);
	update();

	showStatus("Loading '" + file + "'...");

	m_Errors   = new ArrayList<String>();
	m_Warnings = new ArrayList<String>();
	m_Flow     = reader.readNode(file);
	m_Errors.addAll(reader.getErrors());
	m_Warnings.addAll(reader.getWarnings());
	if (!m_Errors.isEmpty())
	  m_Flow = null;
	setCurrentFlow(m_Flow);

	showStatus("");

        return null;
      }

      @Override
      protected void done() {
        boolean      canExecute;
        String       msg;

	m_RunningSwingWorker = false;
        canExecute           = execute && m_Errors.isEmpty();

	if (m_Flow == null) {
	  if (m_Errors.isEmpty())
	    GUIHelper.showErrorMessage(
		m_Self, "Failed to load flow '" + file + "'!");
	  else
	    GUIHelper.showErrorMessage(
		m_Self, "Failed to load flow '" + file + "':\n" + Utils.flatten(m_Errors, "\n"));
	}
	else {
	  setCurrentFile(file);
	  setCurrentDirectory(file.getParentFile());
	  if (m_RecentFilesHandler != null)
	    m_RecentFilesHandler.addRecentItem(file);
	  if (!m_Warnings.isEmpty()) {
            msg = "Warning(s) encountered while loading flow '" + file + "':\n" + Utils.flatten(m_Warnings, "\n");
            if (canExecute)
              ConsolePanel.getSingleton().append(LoggingLevel.SEVERE, msg);
            else
              GUIHelper.showErrorMessage(m_Self, msg);
          }
	}

	update();

        super.done();

        // execute flow?
        if (canExecute)
          FlowRunnerPanel.this.run();
      }
    };
    worker.execute();
  }

  /**
   * Resets the GUI to default values.
   */
  protected void reset() {
    cleanUp(true);

    m_CurrentFlow = null;
    m_CurrentFile = null;

    updateActions();
    updateParameters();
  }

  /**
   * Sets the flow to work on.
   *
   * @param flow	the flow to use
   */
  public void setCurrentFlow(Actor flow) {
    m_CurrentFile = null;
    m_CurrentFlow = flow;
  }

  /**
   * Sets the flow to work on.
   *
   * @param flow	the flow to use
   */
  public void setCurrentFlow(Node flow) {
    m_CurrentFile = null;
    if (flow != null)
      m_CurrentFlow = flow.getFullActor();
    else
      m_CurrentFlow = null;
  }

  /**
   * Returns the current flow.
   *
   * @return		the current flow, can be null
   */
  public Actor getCurrentFlow() {
    return m_CurrentFlow;
  }

  /**
   * Opens a flow.
   */
  protected void open() {
    int		retVal;

    retVal = m_FileChooser.showOpenDialog(this);
    if (retVal != BaseFileChooser.APPROVE_OPTION)
      return;

    load(m_FileChooser.getReader(), m_FileChooser.getSelectedPlaceholderFile());
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
   * Returns whether a swing worker is currently running.
   *
   * @return		true if a swing worker is being executed
   */
  public boolean isRunningSwingWorker() {
    return m_RunningSwingWorker;
  }

  /**
   * Pauses/resumes the flow.
   *
   * @return		true if paused, otherwise false
   */
  protected boolean pauseAndResume() {
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
   * Displays the errors from the last run.
   */
  public void displayErrors() {
    BaseDialog		dialog;
    LogEntryHandler	handler;
    LogEntryViewerPanel	panel;

    if (m_CurrentFlow == null)
      return;
    if (!(m_CurrentFlow instanceof LogEntryHandler))
      return;
    handler = (LogEntryHandler) m_CurrentFlow;
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
   *
   * @param destroy	whether to destroy the
   */
  public void cleanUp(boolean destroy) {
    if (m_CurrentFlow != null) {
      showStatus("Cleaning up");
      try {
	if (destroy)
	  m_CurrentFlow.destroy();
	else
	  m_CurrentFlow.cleanUp();
	showStatus("");
      }
      catch (Exception e) {
	e.printStackTrace();
	showStatus("Error cleaning up: " + e);
      }
    }
  }

  /**
   * Closes the dialog or frame.
   */
  protected void close() {
    cleanUp(true);

    if (getParentFrame() != null)
      ((JFrame) getParentFrame()).setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    closeParent();
  }

  /**
   * Displays a new flow editor window/frame.
   */
  protected void newWindow() {
    FlowRunnerPanel 	panel;
    ChildFrame 		oldFrame;
    ChildFrame 		newFrame;
    ChildWindow 	oldWindow;
    ChildWindow 	newWindow;

    panel    = null;
    oldFrame = (ChildFrame) GUIHelper.getParent(m_Self, ChildFrame.class);
    if (oldFrame != null) {
      newFrame = oldFrame.getNewWindow();
      newFrame.setVisible(true);
      panel = (FlowRunnerPanel) newFrame.getContentPane().getComponent(0);
    }
    else {
      oldWindow = (ChildWindow) GUIHelper.getParent(m_Self, ChildWindow.class);
      if (oldWindow != null) {
	newWindow = oldWindow.getNewWindow();
	newWindow.setVisible(true);
	panel = (FlowRunnerPanel) newWindow.getContentPane().getComponent(0);
      }
    }

    // copy information
    if (panel != null) {
      panel.setCurrentDirectory(getCurrentDirectory());
      panel.update();
    }
  }

  /**
   * Duplicates the current window/frame, including the current flow.
   */
  protected void duplicateWindow() {
    FlowRunnerPanel 	panel;
    ChildFrame 		oldFrame;
    ChildFrame 		newFrame;
    ChildWindow 	oldWindow;
    ChildWindow 	newWindow;

    panel    = null;
    oldFrame = (ChildFrame) GUIHelper.getParent(m_Self, ChildFrame.class);
    if (oldFrame != null) {
      newFrame = oldFrame.getNewWindow();
      newFrame.setVisible(true);
      panel = (FlowRunnerPanel) newFrame.getContentPane().getComponent(0);
    }
    else {
      oldWindow = (ChildWindow) GUIHelper.getParent(m_Self, ChildWindow.class);
      if (oldWindow != null) {
	newWindow = oldWindow.getNewWindow();
	newWindow.setVisible(true);
	panel = (FlowRunnerPanel) newWindow.getContentPane().getComponent(0);
      }
    }

    // copy information
    if (panel != null) {
      panel.setCurrentDirectory(getCurrentDirectory());
      panel.setCurrentFlow(getCurrentFlow());
      panel.setCurrentFile(getCurrentFile());
      panel.update();
    }
  }

  /**
   * Displays the message in the status bar in a separate dialog.
   */
  protected void showMessage() {
    if (m_StatusBar.hasStatus())
      showMessage(m_StatusBar.getStatus());
  }

  /**
   * Displays the given message in a separate dialog.
   *
   * @param msg		the message to display
   */
  protected void showMessage(String msg) {
    String	status;

    status = msg.replaceAll(": ", ":\n");

    GUIHelper.showInformationMessage(this, status, "Status");
  }

  /**
   * Displays a message.
   *
   * @param msg		the message to display
   */
  public void showStatus(String msg) {
    m_StatusBar.showStatus(msg);
  }

  /**
   * Sets the current directory in the FileChooser use for opening flows.
   *
   * @param dir		the new current directory to use
   */
  public void setCurrentDirectory(File dir)  {
    m_FileChooser.setCurrentDirectory(dir);
  }

  /**
   * Returns the current directory set in the FileChooser used for opening the
   * flows.
   *
   * @return		the current directory
   */
  public File getCurrentDirectory() {
    return m_FileChooser.getCurrentDirectory();
  }

  /**
   * Returns the panel with the variables.
   *
   * @return		always null
   */
  public VariableManagementPanel getVariablesPanel() {
    return null;
  }

  /**
   * Returns the panel with the storage items.
   *
   * @return		always null
   */
  public StoragePanel getStoragePanel() {
    return null;
  }

  /**
   * Returns whether the flow gets executed in headless mode.
   *
   * @return		true if the flow gets executed in headless mode
   */
  public boolean isHeadless() {
    return m_ActionHeadless.isSelected();
  }

  /**
   * Returns whether the GC gets called after the flow execution.
   *
   * @return		always false
   */
  public boolean getRunGC() {
    return false;
  }

  /**
   * Returns whether the flow is flagged as modified.
   *
   * @return		always false
   */
  public boolean isModified() {
    return false;
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
   * Sets the flow that was last executed.
   *
   * @param value	the flow
   */
  public void setLastFlow(Actor value) {
    m_LastFlow = value;
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
   * Finishes up the execution, setting the worker to null.
   */
  public void finishedExecution() {
    m_CurrentWorker = null;
    m_CurrentThread = null;
    update();
  }

  /**
   * Returns the properties that define the editor.
   *
   * @return		the properties
   */
  public static synchronized Properties getProperties() {
    if (m_Properties == null)
      m_Properties = Environment.getInstance().read(FlowRunnerPanelDefinition.KEY);

    return m_Properties;
  }
}
