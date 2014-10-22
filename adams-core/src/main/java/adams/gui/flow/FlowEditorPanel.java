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
 * FlowEditorPanel.java
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.flow;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Vector;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JPopupMenu.Separator;
import javax.swing.JSplitPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import adams.core.Pausable;
import adams.core.Properties;
import adams.core.StatusMessageHandler;
import adams.core.io.FilenameProposer;
import adams.core.io.PlaceholderFile;
import adams.data.statistics.InformativeStatistic;
import adams.db.LogEntryHandler;
import adams.env.Environment;
import adams.env.FlowEditorPanelDefinition;
import adams.env.FlowEditorPanelMenuDefinition;
import adams.env.FlowEditorTreePopupMenuDefinition;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.flow.core.ActorStatistic;
import adams.flow.core.ActorUtils;
import adams.flow.core.AutomatableInteractiveActor;
import adams.flow.processor.RemoveBreakpoints;
import adams.gui.action.AbstractBaseAction;
import adams.gui.action.ToggleAction;
import adams.gui.application.ChildFrame;
import adams.gui.application.ChildWindow;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.chooser.FlowFileChooser;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.BaseStatusBar;
import adams.gui.core.BaseStatusBar.PopupMenuCustomizer;
import adams.gui.core.BaseStatusBar.StatusProcessor;
import adams.gui.core.GUIHelper;
import adams.gui.core.MenuBarProvider;
import adams.gui.core.RecentFilesHandler;
import adams.gui.core.ToolBarPanel;
import adams.gui.event.RecentItemEvent;
import adams.gui.event.RecentItemListener;
import adams.gui.event.TabVisibilityChangeEvent;
import adams.gui.event.TabVisibilityChangeListener;
import adams.gui.event.UndoEvent;
import adams.gui.flow.menu.AbstractFlowEditorMenuItem;
import adams.gui.flow.tab.FlowTabManager;
import adams.gui.flow.tree.Tree;
import adams.gui.sendto.SendToActionSupporter;
import adams.gui.sendto.SendToActionUtils;
import adams.gui.visualization.statistics.InformativeStatisticFactory;

/**
 * A panel for setting up, modifying, saving and loading "simple" flows.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FlowEditorPanel
  extends ToolBarPanel
  implements MenuBarProvider, StatusMessageHandler, SendToActionSupporter,
             PopupMenuCustomizer {

  /** for serialization. */
  private static final long serialVersionUID = -3579084888256133873L;

  /** the File menu text. */
  public static final String MENU_FILE = "File";

  /** the Edit menu text. */
  public static final String MENU_EDIT = "Edit";

  /** the Debug menu text. */
  public static final String MENU_DEBUG = "Debug";

  /** the Execution menu text. */
  public static final String MENU_EXECUTION = "Execution";

  /** the View menu text. */
  public static final String MENU_VIEW = "View";

  /** the Window menu text. */
  public static final String MENU_WINDOW = "Window";

  /** the name of the props file with the general properties. */
  public final static String FILENAME = "FlowEditor.props";

  /** the name of the props file with the menu. */
  public final static String FILENAME_MENU = "FlowEditorMenu.props";

  /** the name of the props file with the tree menu. */
  public final static String FILENAME_TREEPOPUPMENU = "FlowEditorTreePopupMenu.props";

  /** the file to store the recent files in. */
  public final static String SESSION_FILE = "FlowSession.props";

  /** the default title for dialogs/frames. */
  public final static String DEFAULT_TITLE = "Flow editor";

  /** the general properties. */
  protected static Properties m_Properties;

  /** the menu properties. */
  protected static Properties m_PropertiesMenu;

  /** the tree popup menu properties. */
  protected static Properties m_PropertiesTreePopup;

  /** the panel itself. */
  protected FlowEditorPanel m_Self;

  /** the menu bar, if used. */
  protected JMenuBar m_MenuBar;

  /** the "new" sub-menu. */
  protected JMenu m_MenuFileNew;

  /** the "load" action. */
  protected AbstractBaseAction m_ActionFileLoad;

  /** the "load recent" submenu. */
  protected JMenu m_MenuFileLoadRecent;

  /** the "new" action. */
  protected AbstractBaseAction m_ActionFileNew;

  /** the "save" action. */
  protected AbstractBaseAction m_ActionFileSave;

  /** the "save as" action. */
  protected AbstractBaseAction m_ActionFileSaveAs;

  /** the "revert" action. */
  protected AbstractBaseAction m_ActionFileRevert;

  /** the "export" action. */
  protected AbstractBaseAction m_ActionFileExport;

  /** the "import" action. */
  protected AbstractBaseAction m_ActionFileImport;

  /** the "properties" action. */
  protected AbstractBaseAction m_ActionFileProperties;

  /** the "close tab" action. */
  protected AbstractBaseAction m_ActionFileCloseTab;

  /** the "close" action. */
  protected AbstractBaseAction m_ActionFileClose;

  /** the toggle undo action. */
  protected AbstractBaseAction m_ActionEditEnableUndo;

  /** the undo action. */
  protected AbstractBaseAction m_ActionEditUndo;

  /** the redo action. */
  protected AbstractBaseAction m_ActionEditRedo;

  /** the diff action. */
  protected AbstractBaseAction m_ActionEditDiff;

  /** the find action. */
  protected AbstractBaseAction m_ActionEditFind;

  /** the find next action. */
  protected AbstractBaseAction m_ActionEditFindNext;

  /** the locate actor action. */
  protected AbstractBaseAction m_ActionEditLocateActor;

  /** the remove disabled actors action. */
  protected AbstractBaseAction m_ActionEditCleanUpFlow;

  /** the check variables action. */
  protected AbstractBaseAction m_ActionEditCheckVariables;

  /** the interactive actors action. */
  protected AbstractBaseAction m_ActionEditInteractiveActors;

  /** the ignore name changes action. */
  protected AbstractBaseAction m_ActionEditIgnoreNameChanges;

  /** the "process actors" action. */
  protected AbstractBaseAction m_ActionEditProcessActors;

  /** the "enable all breakpoints" action. */
  protected AbstractBaseAction m_ActionDebugEnableAllBreakpoints;

  /** the "remove all breakpoints" action. */
  protected AbstractBaseAction m_ActionDebugRemoveAllBreakpoints;

  /** the "disable all breakpoints" action. */
  protected AbstractBaseAction m_ActionDebugDisableAllBreakpoints;

  /** the "variables" action. */
  protected AbstractBaseAction m_ActionDebugVariables;

  /** the "storage" action. */
  protected AbstractBaseAction m_ActionDebugStorage;

  /** the "headless" action. */
  protected AbstractBaseAction m_ActionExecutionHeadless;

  /** the "check setup" action. */
  protected AbstractBaseAction m_ActionExecutionValidateSetup;

  /** the "run" action. */
  protected AbstractBaseAction m_ActionExecutionRun;

  /** the "pause" action. */
  protected AbstractBaseAction m_ActionExecutionPauseAndResume;

  /** the "pause" menu item. */
  protected JMenuItem m_MenuItemExecutionPauseAndResume;

  /** the "stop" action. */
  protected AbstractBaseAction m_ActionExecutionStop;

  /** the "display errors" action. */
  protected AbstractBaseAction m_ActionExecutionDisplayErrors;

  /** the "Clear graphical output" action. */
  protected AbstractBaseAction m_ActionExecutionClearGraphicalOutput;

  /** the "show toolbar" action. */
  protected AbstractBaseAction m_ActionViewShowToolbar;

  /** the "show quick info" action. */
  protected AbstractBaseAction m_ActionViewShowQuickInfo;

  /** the "show annotations" action. */
  protected AbstractBaseAction m_ActionViewShowAnnotations;

  /** the "show input/output" action. */
  protected AbstractBaseAction m_ActionViewShowInputOutput;

  /** the highlight variables action. */
  protected AbstractBaseAction m_ActionViewHighlightVariables;

  /** the remove variable highlights action. */
  protected AbstractBaseAction m_ActionViewRemoveVariableHighlights;

  /** the "show source" action. */
  protected AbstractBaseAction m_ActionViewShowSource;

  /** the "statistic" action. */
  protected AbstractBaseAction m_ActionViewStatistics;

  /** the "redraw" action. */
  protected AbstractBaseAction m_ActionViewRedraw;

  /** the "new window" action. */
  protected AbstractBaseAction m_ActionNewWindow;

  /** the "duplicate tab in new window" action. */
  protected AbstractBaseAction m_ActionDuplicateTabInNewWindow;

  /** the "duplicate tab" action. */
  protected AbstractBaseAction m_ActionDuplicateTab;

  /** additional menu items. */
  protected Vector<AbstractFlowEditorMenuItem> m_AdditionalMenuItems;

  /** the filedialog for loading/saving flows. */
  protected FlowFileChooser m_FileChooser;

  /** the status. */
  protected BaseStatusBar m_StatusBar;

  /** the recent files handler. */
  protected RecentFilesHandler<JMenu> m_RecentFilesHandler;

  /** for proposing filenames for new flows. */
  protected FilenameProposer m_FilenameProposer;

  /** the split pane for displaying flow and tabs. */
  protected BaseSplitPane m_SplitPane;

  /** the tabbedpane for the flow panels. */
  protected FlowTabbedPane m_FlowPanels;

  /** the tabbedpane for the tabs. */
  protected FlowTabManager m_Tabs;

  /** the last variable search performed. */
  protected String m_LastVariableSearch;

  /** the dialog for importing the flow. */
  protected ImportDialog m_ImportDialog;

  /** the dialog for exporting the flow. */
  protected ExportDialog m_ExportDialog;

  /** the default toolbar location to use. */
  protected ToolBarLocation m_PreferredToolBarLocation;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    String[]			additionals;
    AbstractFlowEditorMenuItem	item;

    super.initialize();

    m_Self                = this;
    m_RecentFilesHandler  = null;
    m_LastVariableSearch  = "";
    m_FileChooser         = new FlowFileChooser();
    m_FileChooser.setMultiSelectionEnabled(true);
    m_FileChooser.setCurrentDirectory(new PlaceholderFile(getProperties().getPath("InitialDir", "%h")));
    m_FilenameProposer    = new FilenameProposer(FlowPanel.PREFIX_NEW, AbstractActor.FILE_EXTENSION, getProperties().getPath("InitialDir", "%h"));
    m_ExportDialog        = null;

    m_AdditionalMenuItems = new Vector<AbstractFlowEditorMenuItem>();
    additionals           = AbstractFlowEditorMenuItem.getMenuItems();
    for (String additional: additionals) {
      try {
	item = (AbstractFlowEditorMenuItem) Class.forName(additional).newInstance();
	item.setOwner(m_Self);
	m_AdditionalMenuItems.add(item);
      }
      catch (Exception e) {
	System.err.println("Failed to instantiate additional menu item '" + additional + "':");
	e.printStackTrace();
      }
    }
    Collections.sort(m_AdditionalMenuItems);
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    Properties			props;

    super.initGUI();

    props = getProperties();

    getContentPanel().setLayout(new BorderLayout());

    m_PreferredToolBarLocation = ToolBarLocation.valueOf(props.getProperty("ToolBar.Location", "NORTH"));
    if (m_PreferredToolBarLocation == ToolBarLocation.HIDDEN)
      m_PreferredToolBarLocation = ToolBarLocation.NORTH;
    setToolBarLocation(ToolBarLocation.valueOf(props.getProperty("ToolBar.Location", "NORTH")));

    m_SplitPane = new BaseSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
    m_SplitPane.setDividerLocation(props.getInteger("DividerLocation", 500));
    m_SplitPane.setOneTouchExpandable(true);
    m_SplitPane.setResizeWeight(0.5);
    getContentPanel().add(m_SplitPane, BorderLayout.CENTER);

    // the flows
    m_FlowPanels = new FlowTabbedPane(this);
    if (props.getBoolean("Tabs.ScrollLayout", true))
      m_FlowPanels.setTabLayoutPolicy(FlowTabbedPane.SCROLL_TAB_LAYOUT);
    else
      m_FlowPanels.setTabLayoutPolicy(FlowTabbedPane.WRAP_TAB_LAYOUT);
    m_SplitPane.setLeftComponent(m_FlowPanels);

    // the tabs
    m_Tabs = new FlowTabManager(this);
    m_Tabs.addTabVisibilityChangeListener(new TabVisibilityChangeListener() {
      @Override
      public void tabVisibilityChanged(TabVisibilityChangeEvent e) {
	m_SplitPane.setRightComponentHidden(m_Tabs.getTabCount() == 0);
      }
    });
    m_SplitPane.setRightComponent(m_Tabs);
    m_SplitPane.setRightComponentHidden(m_Tabs.getTabCount() == 0);

    // the status
    m_StatusBar = new BaseStatusBar();
    m_StatusBar.setDialogSize(new Dimension(props.getInteger("StatusBar.Width", 600), props.getInteger("StatusBar.Height", 400)));
    m_StatusBar.setMouseListenerActive(true);
    m_StatusBar.setPopupMenuCustomizer(this);
    m_StatusBar.setStatusProcessor(new StatusProcessor() {
      @Override
      public String process(String msg) {
        return msg.replace(": ", ":\n");
      }
    });
    getContentPanel().add(m_StatusBar, BorderLayout.SOUTH);
  }

  /**
   * Initializes the actions.
   */
  @Override
  @SuppressWarnings("serial")
  protected void initActions() {
    AbstractBaseAction	action;
    Properties		props;

    props = getProperties();

    // File/New (flow)
    action = new AbstractBaseAction("Flow", "new.gif") {
      @Override
      public void actionPerformed(ActionEvent e) {
	newTab();
      }
    };
    action.setMnemonic(KeyEvent.VK_N);
    action.setAccelerator(getEditorShortcut("File.New"));
    m_ActionFileNew = action;

    // File/Open
    action = new AbstractBaseAction("Open...", "open.gif") {
      @Override
      public void actionPerformed(ActionEvent e) {
	open();
      }
    };
    action.setMnemonic(KeyEvent.VK_O);
    action.setAccelerator(getEditorShortcut("File.Open"));
    m_ActionFileLoad = action;

    // File/Save
    action = new AbstractBaseAction("Save", "save.gif") {
      @Override
      public void actionPerformed(ActionEvent e) {
	save();
      }
    };
    action.setMnemonic(KeyEvent.VK_S);
    action.setAccelerator(getEditorShortcut("File.Save"));
    m_ActionFileSave = action;

    // File/Save
    action = new AbstractBaseAction("Save as...", GUIHelper.getEmptyIcon()) {
      @Override
      public void actionPerformed(ActionEvent e) {
	saveAs();
      }
    };
    action.setMnemonic(KeyEvent.VK_A);
    action.setAccelerator(getEditorShortcut("File.SaveAs"));
    m_ActionFileSaveAs = action;

    // File/Revert
    action = new AbstractBaseAction("Revert", "refresh.gif") {
      @Override
      public void actionPerformed(ActionEvent e) {
	revert();
      }
    };
    action.setMnemonic(KeyEvent.VK_R);
    action.setAccelerator(getEditorShortcut("File.Revert"));
    m_ActionFileRevert = action;

    // File/Close tab
    action = new AbstractBaseAction("Close tab") {
      @Override
      public void actionPerformed(ActionEvent e) {
	m_FlowPanels.removeSelectedTab();
      }
    };
    action.setMnemonic(KeyEvent.VK_T);
    action.setAccelerator(getEditorShortcut("File.CloseTab"));
    m_ActionFileCloseTab = action;

    // File/Import
    action = new AbstractBaseAction("Import...", GUIHelper.getEmptyIcon()) {
      @Override
      public void actionPerformed(ActionEvent e) {
	importFlow();
      }
    };
    action.setMnemonic(KeyEvent.VK_I);
    action.setAccelerator(getEditorShortcut("File.Import"));
    m_ActionFileImport = action;

    // File/Export
    action = new AbstractBaseAction("Export...", GUIHelper.getEmptyIcon()) {
      @Override
      public void actionPerformed(ActionEvent e) {
	exportFlow();
      }
    };
    action.setMnemonic(KeyEvent.VK_E);
    action.setAccelerator(getEditorShortcut("File.Export"));
    m_ActionFileExport = action;

    // File/Properties
    action = new AbstractBaseAction("Properties...", GUIHelper.getIcon("properties.gif")) {
      @Override
      public void actionPerformed(ActionEvent e) {
	showProperties();
      }
    };
    action.setMnemonic(KeyEvent.VK_R);
    action.setAccelerator(getEditorShortcut("File.Properties"));
    m_ActionFileProperties = action;

    // File/Close
    action = new AbstractBaseAction("Close", "exit.png") {
      @Override
      public void actionPerformed(ActionEvent e) {
	close();
      }
    };
    action.setMnemonic(KeyEvent.VK_C);
    action.setAccelerator(getEditorShortcut("File.Exit"));
    m_ActionFileClose = action;

    // Edit/Enable Undo
    action = new AbstractBaseAction("Undo enabled", GUIHelper.getEmptyIcon()) {
      @Override
      public void actionPerformed(ActionEvent e) {
	if (hasCurrentPanel())
	  getCurrentPanel().getUndo().setEnabled(!getCurrentPanel().getUndo().isEnabled());
      }
    };
    action.setMnemonic(KeyEvent.VK_N);
    action.setAccelerator(getEditorShortcut("Edit.ToggleUndo"));
    action.setEnabled(false);
    action.setSelected(true);
    m_ActionEditEnableUndo = action;

    // Edit/Undo
    action = new AbstractBaseAction("Undo", "undo.gif") {
      @Override
      public void actionPerformed(ActionEvent e) {
	undo();
      }
    };
    action.setMnemonic(KeyEvent.VK_U);
    action.setAccelerator(getEditorShortcut("Edit.Undo"));
    action.setEnabled(false);
    m_ActionEditUndo = action;

    // Edit/Redo
    action = new AbstractBaseAction("Redo", "redo.gif") {
      @Override
      public void actionPerformed(ActionEvent e) {
	redo();
      }
    };
    action.setMnemonic(KeyEvent.VK_R);
    action.setAccelerator(getEditorShortcut("Edit.Redo"));
    action.setEnabled(false);
    m_ActionEditRedo = action;

    // Edit/Diff
    action = new AbstractBaseAction("Show changes", "diff.png") {
      @Override
      public void actionPerformed(ActionEvent e) {
	showDiff();
      }
    };
    action.setMnemonic(KeyEvent.VK_D);
    action.setAccelerator(getEditorShortcut("Edit.Diff"));
    action.setEnabled(false);
    m_ActionEditDiff = action;

    // Edit/Find
    action = new AbstractBaseAction("Find", "find.gif") {
      @Override
      public void actionPerformed(ActionEvent e) {
	find();
      }
    };
    action.setMnemonic(KeyEvent.VK_F);
    action.setAccelerator(getEditorShortcut("Edit.Find"));
    m_ActionEditFind = action;

    // Edit/Find next
    action = new AbstractBaseAction("Find next", GUIHelper.getEmptyIcon()) {
      @Override
      public void actionPerformed(ActionEvent e) {
	findNext();
      }
    };
    action.setMnemonic(KeyEvent.VK_N);
    action.setAccelerator(getEditorShortcut("Edit.FindNext"));
    m_ActionEditFindNext = action;

    // Edit/Locate actor
    action = new AbstractBaseAction("Locate actor", GUIHelper.getEmptyIcon()) {
      @Override
      public void actionPerformed(ActionEvent e) {
	locateActor();
      }
    };
    action.setMnemonic(KeyEvent.VK_L);
    action.setAccelerator(getEditorShortcut("Edit.LocateActor"));
    m_ActionEditLocateActor = action;

    // Edit/Clean up flow
    action = new AbstractBaseAction("Clean up flow", "delete.gif") {
      @Override
      public void actionPerformed(ActionEvent e) {
	cleanUpFlow();
      }
    };
    action.setMnemonic(KeyEvent.VK_C);
    m_ActionEditCleanUpFlow = action;

    // Edit/Check variables
    action = new AbstractBaseAction("Check variables", "check_variables.png") {
      @Override
      public void actionPerformed(ActionEvent e) {
	checkVariables();
      }
    };
    action.setMnemonic(KeyEvent.VK_V);
    m_ActionEditCheckVariables = action;

    // Edit/Interactive actors (checkbox)
    action = new AbstractBaseAction("Interactive actors", GUIHelper.getEmptyIcon()) {
      @Override
      public void actionPerformed(ActionEvent e) {
	manageInteractiveActors(!m_ActionEditInteractiveActors.isSelected());
      }
    };
    action.setMnemonic(KeyEvent.VK_I);
    action.setSelected(true);
    m_ActionEditInteractiveActors = action;

    // Edit/Ignore name changes (checkbox)
    action = new AbstractBaseAction("Ignore name changes", GUIHelper.getEmptyIcon()) {
      @Override
      public void actionPerformed(ActionEvent e) {
	setIgnoreNameChanges(m_ActionEditIgnoreNameChanges.isSelected());
      }
    };
    action.setMnemonic(KeyEvent.VK_G);
    action.setSelected(true);
    m_ActionEditIgnoreNameChanges = action;

    // Edit/Process actors
    action = new AbstractBaseAction("Process actors...", GUIHelper.getEmptyIcon()) {
      @Override
      public void actionPerformed(ActionEvent e) {
	processActorsPrompt();
      }
    };
    action.setMnemonic(KeyEvent.VK_P);
    action.setAccelerator(getEditorShortcut("Edit.ProcessActors"));
    m_ActionEditProcessActors = action;

    // Debug/Enable all breakpoints
    action = new AbstractBaseAction("Enable all breakpoints", "debug.png") {
      @Override
      public void actionPerformed(ActionEvent e) {
	enableBreakpoints(true);
      }
    };
    action.setMnemonic(KeyEvent.VK_E);
    action.setAccelerator(getEditorShortcut("Debug.EnableAllBreakpoints"));
    m_ActionDebugEnableAllBreakpoints = action;

    // Debug/Disable all breakpoints
    action = new AbstractBaseAction("Disable all breakpoints", "debug_off.png") {
      @Override
      public void actionPerformed(ActionEvent e) {
	enableBreakpoints(false);
      }
    };
    action.setMnemonic(KeyEvent.VK_D);
    action.setAccelerator(getEditorShortcut("Debug.DisableAllBreakpoints"));
    m_ActionDebugDisableAllBreakpoints = action;

    // Debug/Remove all breakpoints
    action = new AbstractBaseAction("Remove all breakpoints", "debug_delete.png") {
      @Override
      public void actionPerformed(ActionEvent e) {
	removeAllBreakpoints();
      }
    };
    action.setMnemonic(KeyEvent.VK_E);
    action.setAccelerator(getEditorShortcut("Debug.RemoveAllBreakpoints"));
    m_ActionDebugRemoveAllBreakpoints = action;

    // Debug/Variables
    action = new AbstractBaseAction("Variables", "variable.gif") {
      @Override
      public void actionPerformed(ActionEvent e) {
	showVariables();
      }
    };
    action.setMnemonic(KeyEvent.VK_V);
    action.setAccelerator(getEditorShortcut("Debug.Variables"));
    m_ActionDebugVariables = action;

    // Debug/Storage
    action = new AbstractBaseAction("Storage", "storage.gif") {
      @Override
      public void actionPerformed(ActionEvent e) {
	showStorage();
      }
    };
    action.setMnemonic(KeyEvent.VK_S);
    action.setAccelerator(getEditorShortcut("Debug.Storage"));
    m_ActionDebugStorage = action;

    // Execution/Validate setup
    action = new AbstractBaseAction("Validate setup", "validate.png") {
      @Override
      public void actionPerformed(ActionEvent e) {
	validateSetup();
      }
    };
    action.setMnemonic(KeyEvent.VK_V);
    action.setAccelerator(getEditorShortcut("Execution.ValidateSetup"));
    m_ActionExecutionValidateSetup = action;

    // Execution/Run
    action = new AbstractBaseAction("Run", "run.gif") {
      @Override
      public void actionPerformed(ActionEvent e) {
	run();
      }
    };
    action.setMnemonic(KeyEvent.VK_R);
    action.setAccelerator(getEditorShortcut("Execution.Run"));
    m_ActionExecutionRun = action;

    // Execution/Pause+Resume
    action = new AbstractBaseAction("Pause", "pause.gif") {
      @Override
      public void actionPerformed(ActionEvent e) {
	pauseAndResume();
      }
    };
    action.setMnemonic(KeyEvent.VK_U);
    action.setAccelerator(getEditorShortcut("Execution.PauseResume"));
    m_ActionExecutionPauseAndResume = action;

    // Execution/Stop
    action = new AbstractBaseAction("Stop", "stop_blue.gif") {
      @Override
      public void actionPerformed(ActionEvent e) {
	stop();
      }
    };
    action.setMnemonic(KeyEvent.VK_S);
    action.setAccelerator(getEditorShortcut("Execution.Stop"));
    m_ActionExecutionStop = action;

    // Execution/Display errors
    action = new AbstractBaseAction("Display errors...", "log.gif") {
      @Override
      public void actionPerformed(ActionEvent e) {
	displayErrors();
      }
    };
    action.setMnemonic(KeyEvent.VK_D);
    m_ActionExecutionDisplayErrors = action;

    // Execution/Clear graphical output
    action = new AbstractBaseAction("Clear graphical output", "close_window.png") {
      @Override
      public void actionPerformed(ActionEvent e) {
	cleanUp();
	update();
      }
    };
    action.setMnemonic(KeyEvent.VK_C);
    action.setAccelerator(getEditorShortcut("Execution.ClearGraphicalOutput"));
    m_ActionExecutionClearGraphicalOutput = action;

    // Execution/Headless
    action = new ToggleAction("Headless", GUIHelper.getEmptyIcon());
    action.setMnemonic(KeyEvent.VK_H);
    action.setAccelerator(getEditorShortcut("Execution.ToggleHeadless"));
    m_ActionExecutionHeadless = action;

    // View/Show toolbar
    action = new AbstractBaseAction("Show toolbar", GUIHelper.getEmptyIcon()) {
      @Override
      public void actionPerformed(ActionEvent e) {
	if (getToolBarLocation() == ToolBarLocation.HIDDEN)
	  setToolBarLocation(m_PreferredToolBarLocation);
	else
	  setToolBarLocation(ToolBarLocation.HIDDEN);
      }
    };
    action.setMnemonic(KeyEvent.VK_T);
    action.setAccelerator(getEditorShortcut("View.ShowToolbar"));
    action.setSelected(getToolBarLocation() != ToolBarLocation.HIDDEN);
    m_ActionViewShowToolbar = action;

    // View/Show quick info
    action = new AbstractBaseAction("Show quick info", GUIHelper.getEmptyIcon()) {
      @Override
      public void actionPerformed(ActionEvent e) {
	if (hasCurrentPanel())
	  getCurrentPanel().getTree().setShowQuickInfo(m_ActionViewShowQuickInfo.isSelected());
      }
    };
    action.setMnemonic(KeyEvent.VK_Q);
    action.setAccelerator(getEditorShortcut("View.ShowQuickInfo"));
    action.setSelected(props.getBoolean("ShowQuickInfo", true));
    m_ActionViewShowQuickInfo = action;

    // View/Show annotations
    action = new AbstractBaseAction("Show annotations", GUIHelper.getEmptyIcon()) {
      @Override
      public void actionPerformed(ActionEvent e) {
	if (hasCurrentPanel())
	  getCurrentPanel().getTree().setShowAnnotations(m_ActionViewShowAnnotations.isSelected());
      }
    };
    action.setMnemonic(KeyEvent.VK_A);
    action.setAccelerator(getEditorShortcut("View.ShowAnnotations"));
    action.setSelected(props.getBoolean("ShowAnnotations", false));
    m_ActionViewShowAnnotations = action;

    // View/Show input/output info
    action = new AbstractBaseAction("Show input/output", GUIHelper.getEmptyIcon()) {
      @Override
      public void actionPerformed(ActionEvent e) {
	if (hasCurrentPanel())
	  getCurrentPanel().getTree().setShowInputOutput(m_ActionViewShowInputOutput.isSelected());
      }
    };
    action.setMnemonic(KeyEvent.VK_P);
    action.setAccelerator(getEditorShortcut("View.ShowInputOutput"));
    action.setSelected(props.getBoolean("ShowInputOutput", false));
    m_ActionViewShowInputOutput = action;

    // View/Highlight variables
    action = new AbstractBaseAction("Highlight variables...", GUIHelper.getEmptyIcon()) {
      @Override
      public void actionPerformed(ActionEvent e) {
	highlightVariables(true);
      }
    };
    action.setMnemonic(KeyEvent.VK_V);
    action.setAccelerator(getEditorShortcut("View.HighlightVariables"));
    m_ActionViewHighlightVariables = action;

    // View/Remove variable highlights
    action = new AbstractBaseAction("Remove variable highlights", GUIHelper.getEmptyIcon()) {
      @Override
      public void actionPerformed(ActionEvent e) {
	highlightVariables(false);
      }
    };
    action.setMnemonic(KeyEvent.VK_R);
    action.setAccelerator(getEditorShortcut("View.RemoveVariableHighlights"));
    m_ActionViewRemoveVariableHighlights = action;

    // View/Show source
    action = new AbstractBaseAction("Show source...", "source.png") {
      @Override
      public void actionPerformed(ActionEvent e) {
	showSource();
      }
    };
    action.setMnemonic(KeyEvent.VK_S);
    action.setAccelerator(getEditorShortcut("View.ShowSource"));
    m_ActionViewShowSource = action;

    // View/Statistics
    action = new AbstractBaseAction("Statistics...", "statistics.png") {
      @Override
      public void actionPerformed(ActionEvent e) {
	showStatistics();
      }
    };
    action.setMnemonic(KeyEvent.VK_T);
    action.setAccelerator(getEditorShortcut("View.Statistics"));
    m_ActionViewStatistics = action;

    // View/Redraw
    action = new AbstractBaseAction("Redraw", GUIHelper.getEmptyIcon()) {
      @Override
      public void actionPerformed(ActionEvent e) {
	if (hasCurrentPanel())
	  getCurrentPanel().redraw();
      }
    };
    action.setMnemonic(KeyEvent.VK_R);
    action.setAccelerator(getEditorShortcut("View.Redraw"));
    m_ActionViewRedraw = action;

    // Window/New Window
    action = new AbstractBaseAction("New window") {
      @Override
      public void actionPerformed(ActionEvent e) {
	newWindow();
      }
    };
    action.setMnemonic(KeyEvent.VK_W);
    action.setAccelerator(getEditorShortcut("Window.NewWindow"));
    m_ActionNewWindow = action;

    // Window/Duplicate in new window
    action = new AbstractBaseAction("Duplicate in new window") {
      @Override
      public void actionPerformed(ActionEvent e) {
	duplicateTabInNewWindow();
      }
    };
    action.setMnemonic(KeyEvent.VK_D);
    action.setAccelerator(getEditorShortcut("Window.DuplicateInNewWindow"));
    m_ActionDuplicateTabInNewWindow = action;

    // Window/Duplicate in new tab
    action = new AbstractBaseAction("Duplicate in new tab", "copy.gif") {
      @Override
      public void actionPerformed(ActionEvent e) {
	duplicateTab();
      }
    };
    action.setMnemonic(KeyEvent.VK_D);
    action.setAccelerator(getEditorShortcut("Window.DuplicateInNewWindow"));
    m_ActionDuplicateTab = action;
  }

  /**
   * Initializes the toolbar.
   */
  @Override
  protected void initToolBar() {
    addToToolBar(m_ActionFileNew);
    addToToolBar(m_ActionFileLoad);
    addToToolBar(m_ActionFileSave);
    addSeparator();
    addToToolBar(m_ActionEditUndo);
    addToToolBar(m_ActionEditRedo);
    addSeparator();
    addToToolBar(m_ActionEditFind);
    addSeparator();
    addToToolBar(m_ActionExecutionValidateSetup);
    addToToolBar(m_ActionExecutionRun);
    addToToolBar(m_ActionExecutionPauseAndResume);
    addToToolBar(m_ActionExecutionStop);
  }

  /**
   * Adds the additional menu items to the menubar.
   */
  protected void addAdditionalMenuitems() {
    int			i;
    JMenu		menu;
    int			index;
    HashSet<String>	separatorAdded;

    separatorAdded = new HashSet<String>();
    for (AbstractFlowEditorMenuItem item: m_AdditionalMenuItems) {
      // determine menu to add the time to
      menu = null;
      for (i = 0; i < m_MenuBar.getMenuCount(); i++) {
	if (m_MenuBar.getMenu(i).getText().equals(item.getMenu())) {
	  menu = m_MenuBar.getMenu(i);
	  break;
	}
      }
      if (menu == null) {
	menu = new JMenu(item.getMenu());
	for (i = 0; i < m_MenuBar.getMenuCount(); i++) {
	  if (m_MenuBar.getMenu(i).getText().equals(MENU_VIEW))
	    m_MenuBar.add(menu, i + 1);
	}
      }
      // add item to menu
      index = menu.getItemCount();
      if (menu.getItemCount() > 0) {
	// never add anything below "Close"
	if (item.getMenu().equals(MENU_FILE))
	  index -= 2;
	if (menu.getItem(index - 1) != null) {
	  if (!separatorAdded.contains(item.getMenu())) {
	    separatorAdded.add(item.getMenu());
	    menu.add(new Separator(), index);
	    index++;
	  }
	}
      }
      menu.insert(item.getAction(), index);
    }
  }

  /**
   * Creates a menu bar (singleton per panel object). Can be used in frames.
   *
   * @return		the menu bar
   */
  @Override
  public JMenuBar getMenuBar() {
    JMenuBar		result;
    JMenu		menu;
    JMenu		submenu;
    JMenuItem		menuitem;
    String[]		actors;
    int			i;
    Vector<String>	prefixes;
    String		prefix;
    String		prefixPrev;

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
      menu = new JMenu(MENU_FILE);
      result.add(menu);
      menu.setMnemonic('F');
      menu.addChangeListener(new ChangeListener() {
	@Override
	public void stateChanged(ChangeEvent e) {
	  updateActions();
	}
      });

      // File/New
      submenu = new JMenu("New");
      menu.add(submenu);
      submenu.setMnemonic('N');
      submenu.setIcon(GUIHelper.getIcon("new.gif"));
      m_MenuFileNew = submenu;
      actors = getProperties().getProperty("NewList", Flow.class.getName()).replace(" ", "").split(",");
      prefixes = new Vector<String>();
      for (i = 0; i < actors.length; i++) {
	prefix = actors[i].substring(0, actors[i].lastIndexOf('.'));
	if (!prefixes.contains(prefix))
	  prefixes.add(prefix);
      }
      prefixPrev = "";
      for (i = 0; i < actors.length; i++) {
	final AbstractActor actor = AbstractActor.forName(actors[i], new String[0]);
	prefix = actors[i].substring(0, actors[i].lastIndexOf('.'));
	if (!prefix.equals(prefixPrev)) {
	  menuitem = new JMenuItem(prefix);
	  menuitem.setEnabled(false);
	  if (prefixPrev.length() > 0)
	    submenu.addSeparator();
	  submenu.add(menuitem);
	  prefixPrev = prefix;
	}
	if (actor instanceof Flow) {
	  menuitem = new JMenuItem(m_ActionFileNew);
	  submenu.add(menuitem);
	}
	else {
	  menuitem = new JMenuItem(actors[i].replaceAll(".*\\.", ""));
	  submenu.add(menuitem);
	  menuitem.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
	      newFlow(actor);
	    }
	  });
	}
      }

      menu.add(new JMenuItem(m_ActionFileLoad));

      // File/Recent files
      submenu = new JMenu("Open recent");
      menu.add(submenu);
      m_RecentFilesHandler = new RecentFilesHandler<JMenu>(
	  SESSION_FILE, getProperties().getInteger("MaxRecentFlows", 5), submenu);
      m_RecentFilesHandler.addRecentItemListener(new RecentItemListener<JMenu,File>() {
	@Override
	public void recentItemAdded(RecentItemEvent<JMenu,File> e) {
	  // ignored
	}
	@Override
	public void recentItemSelected(RecentItemEvent<JMenu,File> e) {
	  FlowPanel panel = m_FlowPanels.newPanel();
	  panel.load(m_FileChooser.getReaderForFile(e.getItem()), e.getItem());
	}
      });
      m_MenuFileLoadRecent = submenu;

      menu.add(new JMenuItem(m_ActionFileSave));
      menu.add(new JMenuItem(m_ActionFileSaveAs));
      menu.add(new JMenuItem(m_ActionFileRevert));
      menu.add(new JMenuItem(m_ActionFileCloseTab));
      menu.addSeparator();
      menu.add(new JMenuItem(m_ActionFileImport));
      menu.add(new JMenuItem(m_ActionFileExport));
      SendToActionUtils.addSendToSubmenu(this, menu);
      menu.addSeparator();
      menu.add(new JMenuItem(m_ActionFileProperties));
      menu.addSeparator();
      menu.add(new JMenuItem(m_ActionFileClose));

      // Edit
      menu = new JMenu(MENU_EDIT);
      result.add(menu);
      menu.setMnemonic('E');
      menu.addChangeListener(new ChangeListener() {
	@Override
	public void stateChanged(ChangeEvent e) {
	  updateActions();
	}
      });

      menu.add(new JCheckBoxMenuItem(m_ActionEditEnableUndo));
      menu.add(new JMenuItem(m_ActionEditUndo));
      menu.add(new JMenuItem(m_ActionEditRedo));
      menu.add(new JMenuItem(m_ActionEditDiff));
      menu.addSeparator();
      menu.add(new JMenuItem(m_ActionEditFind));
      menu.add(new JMenuItem(m_ActionEditFindNext));
      menu.add(new JMenuItem(m_ActionEditLocateActor));
      menu.addSeparator();
      menu.add(new JMenuItem(m_ActionEditCleanUpFlow));
      menu.add(new JMenuItem(m_ActionEditCheckVariables));
      menu.add(new JCheckBoxMenuItem(m_ActionEditInteractiveActors));
      menu.add(new JCheckBoxMenuItem(m_ActionEditIgnoreNameChanges));
      menu.addSeparator();
      menu.add(new JMenuItem(m_ActionEditProcessActors));

      // Debug
      menu = new JMenu(MENU_DEBUG);
      result.add(menu);
      menu.setMnemonic('D');
      menu.addChangeListener(new ChangeListener() {
	@Override
	public void stateChanged(ChangeEvent e) {
	  updateActions();
	}
      });

      menu.add(new JMenuItem(m_ActionDebugEnableAllBreakpoints));
      menu.add(new JMenuItem(m_ActionDebugDisableAllBreakpoints));
      menu.add(new JMenuItem(m_ActionDebugRemoveAllBreakpoints));
      menu.add(new JMenuItem(m_ActionDebugVariables));
      menu.add(new JMenuItem(m_ActionDebugStorage));

      // Execution
      menu = new JMenu(MENU_EXECUTION);
      result.add(menu);
      menu.setMnemonic('E');
      menu.addChangeListener(new ChangeListener() {
	@Override
	public void stateChanged(ChangeEvent e) {
	  updateActions();
	}
      });

      menu.add(new JMenuItem(m_ActionExecutionValidateSetup));
      menu.add(new JMenuItem(m_ActionExecutionRun));
      m_MenuItemExecutionPauseAndResume = new JMenuItem(m_ActionExecutionPauseAndResume);
      menu.add(m_MenuItemExecutionPauseAndResume);
      menu.add(new JMenuItem(m_ActionExecutionStop));
      menu.add(new JMenuItem(m_ActionExecutionDisplayErrors));
      menu.add(new JMenuItem(m_ActionExecutionClearGraphicalOutput));
      menu.addSeparator();
      menu.add(new JCheckBoxMenuItem(m_ActionExecutionHeadless));

      // View
      menu = new JMenu(MENU_VIEW);
      result.add(menu);
      menu.setMnemonic('V');
      menu.addChangeListener(new ChangeListener() {
	@Override
	public void stateChanged(ChangeEvent e) {
	  updateActions();
	}
      });

      menu.add(new JCheckBoxMenuItem(m_ActionViewShowToolbar));
      menu.add(new JCheckBoxMenuItem(m_ActionViewShowQuickInfo));
      menu.add(new JCheckBoxMenuItem(m_ActionViewShowAnnotations));
      menu.add(new JCheckBoxMenuItem(m_ActionViewShowInputOutput));
      m_Tabs.addTabsSubmenu(menu);
      menu.addSeparator();
      menu.add(new JMenuItem(m_ActionViewHighlightVariables));
      menu.add(new JMenuItem(m_ActionViewRemoveVariableHighlights));
      menu.add(new JMenuItem(m_ActionViewRedraw));
      menu.addSeparator();
      menu.add(new JMenuItem(m_ActionViewShowSource));
      menu.add(new JMenuItem(m_ActionViewStatistics));

      // Window
      if ((GUIHelper.getParent(m_Self, ChildFrame.class) != null) && (getParentDialog() == null)) {
	menu = new JMenu(MENU_WINDOW);
	result.add(menu);
	menu.setMnemonic('W');
	menu.addChangeListener(new ChangeListener() {
	  @Override
	  public void stateChanged(ChangeEvent e) {
	    updateActions();
	  }
	});

	menu.add(new JMenuItem(m_ActionNewWindow));
	menu.add(new JMenuItem(m_ActionDuplicateTabInNewWindow));
	menu.add(new JMenuItem(m_ActionDuplicateTab));
      }

      m_MenuBar = result;

      // add additional menu items
      addAdditionalMenuitems();

      // update menu
      updateActions();
    }
    else {
      result = m_MenuBar;
    }

    return result;
  }

  /**
   * Returns the tab manager.
   *
   * @return		the tabs
   */
  public FlowTabManager getTabs() {
    return m_Tabs;
  }

  /**
   * Returns the flow panels.
   *
   * @return		the flow panels
   */
  public FlowTabbedPane getFlowPanels() {
    return m_FlowPanels;
  }

  /**
   * updates the enabled state of the menu items.
   */
  @Override
  protected void updateActions() {
    boolean		inputEnabled;
    Pausable		pausable;
    boolean		hasCurrent;
    AbstractActor	root;

    hasCurrent = hasCurrentPanel();
    if (hasCurrent)
      getCurrentPanel().updateTitle();

    if (m_MenuBar == null)
      return;

    inputEnabled = !isRunning() && !isStopping() && !isSwingWorkerRunning();

    root = getRunningFlow();
    if ((root != null) && (root instanceof Pausable))
      pausable = (Pausable) root;
    else
      pausable = null;

    // File
    m_MenuFileNew.setEnabled(true);
    m_ActionFileNew.setEnabled(true);
    m_ActionFileLoad.setEnabled(true);
    m_MenuFileLoadRecent.setEnabled(m_RecentFilesHandler.size() > 0);
    m_ActionFileSave.setEnabled(inputEnabled && hasCurrent);
    m_ActionFileSaveAs.setEnabled(inputEnabled && hasCurrent);
    m_ActionFileImport.setEnabled(true);
    m_ActionFileExport.setEnabled(inputEnabled && hasCurrent);
    m_ActionFileRevert.setEnabled(inputEnabled && hasCurrent && (getCurrentPanel().getCurrentFile() != null) && getCurrentPanel().getTree().isModified());
    m_ActionFileCloseTab.setEnabled(hasCurrent);
    m_ActionFileClose.setEnabled(!isAnyRunning() && !isAnyStopping() && !isAnySwingWorkerRunning());
    m_ActionFileProperties.setEnabled(hasCurrent);

    // Edit
    m_ActionEditEnableUndo.setEnabled(inputEnabled && hasCurrent);
    m_ActionEditEnableUndo.setSelected(hasCurrent && getCurrentPanel().getUndo().isEnabled());
    m_ActionEditUndo.setEnabled(inputEnabled && hasCurrent && getCurrentPanel().getUndo().canUndo());
    if (hasCurrent && getCurrentPanel().getUndo().canUndo()) {
      m_ActionEditUndo.setName("Undo - " + getCurrentPanel().getUndo().peekUndoComment(true));
      m_ActionEditUndo.setToolTipText(getCurrentPanel().getUndo().peekUndoComment());
    }
    else {
      m_ActionEditUndo.setName("Undo");
      m_ActionEditUndo.setToolTipText(null);
    }
    m_ActionEditRedo.setEnabled(inputEnabled && hasCurrent && getCurrentPanel().getUndo().canRedo());
    if (hasCurrent && getCurrentPanel().getUndo().canRedo()) {
      m_ActionEditRedo.setName("Redo - " + getCurrentPanel().getUndo().peekRedoComment(true));
      m_ActionEditRedo.setToolTipText(getCurrentPanel().getUndo().peekRedoComment());
    }
    else {
      m_ActionEditRedo.setName("Redo");
      m_ActionEditRedo.setToolTipText(null);
    }
    m_ActionEditDiff.setEnabled(inputEnabled && hasCurrent && getCurrentPanel().canDiff());
    m_ActionEditCleanUpFlow.setEnabled(inputEnabled && hasCurrent);
    m_ActionEditCheckVariables.setEnabled(inputEnabled && hasCurrent);
    m_ActionEditInteractiveActors.setEnabled(inputEnabled && hasCurrent);
    m_ActionEditIgnoreNameChanges.setSelected(hasCurrent && getCurrentPanel().getIgnoreNameChanges());
    m_ActionEditProcessActors.setEnabled(inputEnabled && hasCurrent);
    m_ActionEditFind.setEnabled(hasCurrent && !isSwingWorkerRunning());
    m_ActionEditFindNext.setEnabled(hasCurrent && (getCurrentPanel().getTree().getLastSearchNode() != null));
    m_ActionEditLocateActor.setEnabled(hasCurrent && !isSwingWorkerRunning());

    // Debug
    m_ActionDebugEnableAllBreakpoints.setEnabled(inputEnabled && hasCurrent);
    m_ActionDebugDisableAllBreakpoints.setEnabled(inputEnabled && hasCurrent);
    m_ActionDebugRemoveAllBreakpoints.setEnabled(inputEnabled && hasCurrent);
    m_ActionDebugVariables.setEnabled(isRunning());
    m_ActionDebugStorage.setEnabled(isPaused());

    // Execution
    m_ActionExecutionValidateSetup.setEnabled(inputEnabled && hasCurrent);
    m_ActionExecutionRun.setEnabled(inputEnabled && hasCurrent && getCurrentPanel().getTree().isFlow());
    if ((pausable != null) && pausable.isPaused()) {
      m_ActionExecutionPauseAndResume.setIcon(GUIHelper.getIcon("resume.gif"));
      m_ActionExecutionPauseAndResume.setName("Resume");
    }
    else {
      m_ActionExecutionPauseAndResume.setIcon(GUIHelper.getIcon("pause.gif"));
      m_ActionExecutionPauseAndResume.setName("Pause");
    }
    m_ActionExecutionPauseAndResume.setEnabled(isRunning());
    m_ActionExecutionStop.setEnabled(isRunning());
    m_ActionExecutionHeadless.setEnabled(inputEnabled);
    m_ActionExecutionDisplayErrors.setEnabled(
	inputEnabled && (getLastFlow() != null)
	&& (getLastFlow() instanceof LogEntryHandler)
	&& (((LogEntryHandler) getLastFlow()).countLogEntries() > 0));
    m_ActionExecutionClearGraphicalOutput.setEnabled(inputEnabled && (getLastFlow() != null));

    // View
    m_ActionViewShowQuickInfo.setEnabled(hasCurrent);
    m_ActionViewShowAnnotations.setEnabled(hasCurrent);
    m_ActionViewShowInputOutput.setEnabled(hasCurrent);
    m_ActionViewStatistics.setEnabled(hasCurrent && !isSwingWorkerRunning());
    m_ActionViewRedraw.setEnabled(hasCurrent);
    m_ActionViewShowSource.setEnabled(hasCurrent && !isSwingWorkerRunning());
    m_ActionViewHighlightVariables.setEnabled(hasCurrent && !isSwingWorkerRunning());
    m_ActionViewRemoveVariableHighlights.setEnabled(hasCurrent && !isSwingWorkerRunning());
    if (hasCurrent) {
      m_ActionViewShowQuickInfo.setSelected(getCurrentPanel().getTree().getShowQuickInfo());
      m_ActionViewShowAnnotations.setSelected(getCurrentPanel().getTree().getShowAnnotations());
      m_ActionViewShowInputOutput.setSelected(getCurrentPanel().getTree().getShowInputOutput());
    }

    // Window
    m_ActionNewWindow.setEnabled(true);
    m_ActionDuplicateTabInNewWindow.setEnabled(hasCurrent);
    m_ActionDuplicateTab.setEnabled(hasCurrent);

    // additional menu items
    for (AbstractFlowEditorMenuItem item: m_AdditionalMenuItems)
      item.updateAction();
  }

  /**
   * Updates the enabled state of the widgets.
   */
  protected void updateWidgets() {
    boolean	inputEnabled;

    inputEnabled = !isRunning() && !isStopping();

    if (hasCurrentPanel())
      getCurrentPanel().getTree().setEditable(inputEnabled);
  }

  /**
   * updates the enabled state etc. of all the GUI elements.
   */
  public void update() {
    updateActions();
    updateWidgets();
    if (hasCurrentPanel())
      getCurrentPanel().updateTitle();
  }

  /**
   * Returns the shortcut stored in the props file.
   *
   * @param key		the key for the shortcut
   * @return		the shortcut, empty string if not found or none defined
   */
  public static String getEditorShortcut(String key) {
    return getPropertiesMenu().getProperty("Shortcuts." + key, "");
  }

  /**
   * Returns whether we can proceed with the operation or not, depending on
   * whether the user saved the flow or discarded the changes.
   *
   * @return		true if safe to proceed
   */
  protected boolean checkForModified() {
    boolean 	result;
    int		retVal;
    String	msg;

    if (!hasCurrentPanel())
      return true;

    result = !getCurrentPanel().isModified();

    if (!result) {
      if (getCurrentPanel().getCurrentFile() == null)
	msg = "Flow not saved - save?";
      else
	msg = "Flow not saved - save?\n" + getCurrentPanel().getCurrentFile();
      retVal = GUIHelper.showConfirmMessage(this, msg, "Flow not saved");
      switch (retVal) {
	case GUIHelper.APPROVE_OPTION:
	  if (getCurrentPanel().getCurrentFile() != null)
	    save();
	  else
	    saveAs();
	  result = !getCurrentPanel().isModified();
	  break;
	case GUIHelper.DISCARD_OPTION:
	  result = true;
	  break;
	case GUIHelper.CANCEL_OPTION:
	  result = false;
	  break;
      }
    }

    return result;
  }

  /**
   * Returns the filechooser in use.
   * 
   * @return		the filechooser
   */
  public FlowFileChooser getFileChooser() {
    return m_FileChooser;
  }
  
  /**
   * Adds new panel with the specified actor.
   *
   * @param actor	the actor to display in the new panel
   */
  protected void newFlow(AbstractActor actor) {
    FlowPanel	panel;

    panel = m_FlowPanels.newPanel();
    panel.reset(actor);

    updateActions();
    updateWidgets();

    grabFocus();
  }

  /**
   * Sets the current file.
   *
   * @param value	the file
   */
  protected void setCurrentFile(File value) {
    if (hasCurrentPanel())
      getCurrentPanel().setCurrentFile(value);
  }

  /**
   * Returns the current file in use.
   *
   * @return		the current file, can be null
   */
  public File getCurrentFile() {
    if (hasCurrentPanel())
      return getCurrentPanel().getCurrentFile();
    else
      return null;
  }

  /**
   * Attempts to load the file. If non-existent, then a new flow will be
   * created and the current filename set to the provided one.
   *
   * @param file	the file to load
   */
  public void loadUnsafe(File file) {
    FlowPanel	panel;

    panel = m_FlowPanels.newPanel();
    if (!file.exists()) {
      panel.reset(new Flow());
      panel.setCurrentFile(new File(file.getAbsolutePath()));
      updateActions();
    }
    else {
      panel.load(m_FileChooser.getReaderForFile(file), file);
    }
  }

  /**
   * Sets the flow to work on.
   *
   * @param flow	the flow to use
   */
  public void setCurrentFlow(AbstractActor flow) {
    if (hasCurrentPanel())
      getCurrentPanel().setCurrentFlow(flow);
  }

  /**
   * Returns whether a flow panel is available.
   *
   * @return		true if flow panel available
   */
  public boolean hasCurrentPanel() {
    return m_FlowPanels.hasCurrentPanel();
  }

  /**
   * Returns the current flow panel.
   *
   * @return		the current flow panel, null if not available
   */
  public FlowPanel getCurrentPanel() {
    return m_FlowPanels.getCurrentPanel();
  }

  /**
   * Returns the current root actor without its children.
   *
   * @return		the current root, null if not available
   * @see		#getCurrentFlow()
   */
  public AbstractActor getCurrentRoot() {
    if (hasCurrentPanel())
      return getCurrentPanel().getCurrentRoot();
    else
      return null;
  }

  /**
   * Returns the current flow.
   * <p/>
   * WARNING: Recreates an actor hierarchy based on the tree. Method gets very
   * slow for large flows. If you only need the root actor, then use getCurrentRoot()
   * instead.
   *
   * @return		the current flow, null if not available
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
   * @param errors	for storing errors, use null to ignore
   * @return		the current flow, null if not available
   */
  public AbstractActor getCurrentFlow(StringBuilder errors) {
    if (hasCurrentPanel())
      return getCurrentPanel().getCurrentFlow(errors);
    else
      return null;
  }

  /**
   * Returns the currently running flow.
   *
   * @return		the currently running flow, null if not available
   */
  public AbstractActor getRunningFlow() {
    if (hasCurrentPanel())
      return getCurrentPanel().getRunningFlow();
    else
      return null;
  }

  /**
   * Returns the last flow executed (currently selected flow).
   *
   * @return		the last executed flow, null if not available
   */
  public AbstractActor getLastFlow() {
    if (hasCurrentPanel())
      return getCurrentPanel().getLastFlow();
    else
      return null;
  }

  /**
   * Sets whether the flow is modified or not.
   *
   * @param value	true if the flow is to be flagged as modified
   */
  public void setModified(boolean value) {
    if (hasCurrentPanel())
      getCurrentPanel().getTree().setModified(value);
    update();
  }

  /**
   * Returns whether the flow is flagged as modified.
   *
   * @return		true if the flow is modified
   */
  public boolean isModified() {
    return hasCurrentPanel() && getCurrentPanel().getTree().isModified();
  }

  /**
   * Adds a new tab.
   */
  public void newTab() {
    m_FlowPanels.newPanel();
  }

  /**
   * Opens a flow.
   */
  public void open() {
    int			retVal;
    FlowPanel		panel;

    retVal = m_FileChooser.showOpenDialog(this);
    if (retVal != BaseFileChooser.APPROVE_OPTION)
      return;

    for (PlaceholderFile file: m_FileChooser.getSelectedPlaceholderFiles()) {
      panel = m_FlowPanels.newPanel();
      panel.load(m_FileChooser.getReader(), file);
    }
  }

  /**
   * Reverts a flow.
   */
  protected void revert() {
    if (!hasCurrentPanel())
      return;
    if (!checkForModified())
      return;

    getCurrentPanel().revert();
  }

  /**
   * Saves the flow.
   */
  public void save() {
    FlowPanel	panel;

    panel = getCurrentPanel();
    if (panel == null)
      return;

    if (panel.getCurrentFile() == null) {
      saveAs();
      return;
    }

    panel.save(m_FileChooser.getWriterForFile(panel.getCurrentFile()), panel.getCurrentFile());
  }

  /**
   * Saves the flow.
   */
  public void saveAs() {
    int			retVal;
    File		file;
    FlowPanel		panel;

    panel = getCurrentPanel();
    if (panel == null)
      return;

    file = panel.getCurrentFile();
    if (file == null)
      file = new PlaceholderFile(getCurrentDirectory() + File.separator + panel.getTitle() + "." + AbstractActor.FILE_EXTENSION);
    if (file.exists())
      file = m_FilenameProposer.propose(file);
    m_FileChooser.setSelectedFile(file);
    retVal = m_FileChooser.showSaveDialog(this);
    if (retVal != BaseFileChooser.APPROVE_OPTION)
      return;

    file = m_FileChooser.getSelectedPlaceholderFile();
    panel.addUndoPoint("Saving undo data...", "Saving as '" + file.getName() + "'");
    showStatus("Saving as '" + file + "'...");

    panel.save(m_FileChooser.getWriter(), m_FileChooser.getSelectedPlaceholderFile());
  }

  /**
   * Imports a flow.
   */
  public void importFlow() {
    FlowPanel	panel;

    if (m_ImportDialog == null) {
      if (getParentDialog() != null)
	m_ImportDialog = new ImportDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
      else
	m_ImportDialog = new ImportDialog(getParentFrame(), true);
    }

    m_ImportDialog.setLocationRelativeTo(this);
    m_ImportDialog.setVisible(true);
    if (m_ImportDialog.getOption() != ImportDialog.APPROVE_OPTION)
      return;

    panel = m_FlowPanels.newPanel();
    panel.importFlow(m_ImportDialog.getImport(), m_ImportDialog.getFile());
  }

  /**
   * Exports the flow.
   */
  public void exportFlow() {
    FlowPanel	panel;

    panel = getCurrentPanel();
    if (panel == null)
      return;

    if (m_ExportDialog == null) {
      if (getParentDialog() != null)
	m_ExportDialog = new ExportDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
      else
	m_ExportDialog = new ExportDialog(getParentFrame(), true);
    }

    m_ExportDialog.setLocationRelativeTo(this);
    m_ExportDialog.setVisible(true);
    if (m_ExportDialog.getOption() != ExportDialog.APPROVE_OPTION)
      return;

    panel.exportFlow(m_ExportDialog.getExport(), m_ExportDialog.getFile());
  }

  /**
   * Validates the current setup.
   */
  public void validateSetup() {
    if (hasCurrentPanel())
      getCurrentPanel().validateSetup();
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
    if (hasCurrentPanel())
      getCurrentPanel().run(showNotification);
  }

  /**
   * Returns whether a flow is currently running.
   *
   * @return		true if a flow is being executed
   */
  public boolean isRunning() {
    if (getCurrentPanel() == null)
      return false;
    else
      return getCurrentPanel().isRunning();
  }

  /**
   * Returns whether any flow is currently running.
   *
   * @return		true if at least one flow is being executed
   */
  public boolean isAnyRunning() {
    boolean	result;
    int		i;

    result = false;

    for (i = 0; i < m_FlowPanels.getPanelCount(); i++) {
      if (m_FlowPanels.getPanelAt(i).isRunning()) {
	result = true;
	break;
      }
    }

    return result;
  }

  /**
   * Returns whether a flow is currently being stopped.
   *
   * @return		true if a flow is currently being stopped
   */
  public boolean isStopping() {
    if (getCurrentPanel() == null)
      return false;
    else
      return getCurrentPanel().isStopping();
  }

  /**
   * Returns whether any flow is currently stopping.
   *
   * @return		true if at least one flow is being stopped
   */
  public boolean isAnyStopping() {
    boolean	result;
    int		i;

    result = false;

    for (i = 0; i < m_FlowPanels.getPanelCount(); i++) {
      if (m_FlowPanels.getPanelAt(i).isStopping()) {
	result = true;
	break;
      }
    }

    return result;
  }

  /**
   * Returns whether the current flow is paused.
   *
   * @return		true if flow is paused
   */
  public boolean isPaused() {
    if (getCurrentPanel() == null)
      return false;
    else
      return getCurrentPanel().isPaused();
  }

  /**
   * Returns whether any flow is currently paused.
   *
   * @return		true if at least one flow is paused
   */
  public boolean isAnyPaused() {
    boolean	result;
    int		i;

    result = false;

    for (i = 0; i < m_FlowPanels.getPanelCount(); i++) {
      if (m_FlowPanels.getPanelAt(i).isPaused()) {
	result = true;
	break;
      }
    }

    return result;
  }

  /**
   * Returns whether a swing worker is currently running.
   *
   * @return		true if a swing worker is being executed
   */
  public boolean isSwingWorkerRunning() {
    if (getCurrentPanel() == null)
      return false;
    else
      return getCurrentPanel().isSwingWorkerRunning();
  }

  /**
   * Returns whether any swing worker is currently running.
   *
   * @return		true if at least one swing worker is being executed
   */
  public boolean isAnySwingWorkerRunning() {
    boolean	result;
    int		i;

    result = false;

    for (i = 0; i < m_FlowPanels.getPanelCount(); i++) {
      if (m_FlowPanels.getPanelAt(i).isSwingWorkerRunning()) {
	result = true;
	break;
      }
    }

    return result;
  }

  /**
   * Pauses/resumes the flow.
   */
  public void pauseAndResume() {
    if (!hasCurrentPanel())
      return;

    getCurrentPanel().closeStorage();
    getCurrentPanel().pauseAndResume();
    updateActions();
  }

  /**
   * Stops the flow.
   */
  public void stop() {
    if (hasCurrentPanel())
      getCurrentPanel().stop();
  }

  /**
   * Displays the errors from the last run.
   */
  public void displayErrors() {
    if (hasCurrentPanel())
      getCurrentPanel().displayErrors();
  }

  /**
   * Cleans up the last flow that was run.
   */
  public void cleanUp() {
    m_FlowPanels.cleanUp();
  }

  /**
   * Shows the properties of the flow.
   */
  public void showProperties() {
    if (!hasCurrentPanel())
      return;
    getCurrentPanel().showProperties();
  }

  /**
   * Used by the close() method to re-display the flow, in case the flow
   * cannot or should not be closed after all.
   *
   * @see	#close()
   */
  protected void setVisibleAgain() {
    if (getParentDialog() != null)
      getParentDialog().setVisible(true);
    else if (getParentFrame() != null)
      getParentFrame().setVisible(true);
  }

  /**
   * Closes the dialog or frame. But only if no flows are running, being stopped
   * or are modified (in the latter, the user can choose to save the flow).
   */
  protected void close() {
    if (isAnyRunning()) {
      GUIHelper.showErrorMessage(this, "Flows are being executed - closing cancelled!");
      setVisibleAgain();
      return;
    }

    if (isAnyStopping()) {
      GUIHelper.showErrorMessage(this, "Flows are being stopped - closing cancelled!");
      setVisibleAgain();
      return;
    }

    if (!checkForModified()) {
      setVisibleAgain();
      return;
    }

    cleanUp();

    if (getParentFrame() != null)
      ((JFrame) getParentFrame()).setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    closeParent();
  }

  /**
   * Displays statistics about the current flow.
   */
  public void showStatistics() {
    ActorStatistic			stats;
    InformativeStatisticFactory.Dialog	dialog;
    Vector<InformativeStatistic>	statsList;

    if (!hasCurrentPanel())
      return;

    stats = null;
    if (getCurrentPanel().getTree().getSelectedNode() != null)
      stats = new ActorStatistic(getCurrentPanel().getTree().getSelectedNode().getFullActor());
    else if (getCurrentRoot() != null)
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
   * peforms an undo if possible.
   */
  public void undo() {
    FlowPanel	panel;

    panel = getCurrentPanel();
    if (panel == null)
      return;

    panel.undo();
  }

  /**
   * peforms a redo if possible.
   */
  public void redo() {
    FlowPanel	panel;

    panel = getCurrentPanel();
    if (panel == null)
      return;

    panel.redo();
  }

  /**
   * Searches for actor names in the tree.
   */
  public void find() {
    if (hasCurrentPanel())
      getCurrentPanel().getTree().find();
  }

  /**
   * Searches for the next actor in the tree.
   */
  public void findNext() {
    if (hasCurrentPanel())
      getCurrentPanel().getTree().findNext();
  }

  /**
   * Locates an actor based on the full actor name.
   */
  public void locateActor() {
    String	path;

    path = JOptionPane.showInputDialog("Please enter the full name of the actor (e.g., 'Flow[0].Sequence[3].Display'):");
    if (path == null)
      return;

    if (hasCurrentPanel())
      getCurrentPanel().getTree().locateAndDisplay(path);
  }

  /**
   * Highlights variables in the tree (or hides the highlights again).
   *
   * @param highlight	whether to turn the highlights on or off
   */
  public void highlightVariables(boolean highlight) {
    String	regexp;

    if (!hasCurrentPanel())
      return;

    if (highlight) {
      regexp = JOptionPane.showInputDialog(
	  GUIHelper.getParentComponent(this),
	  "Enter the regular expression for the variable name ('.*' matches all):",
	  m_LastVariableSearch);
      if (regexp == null)
	return;

      m_LastVariableSearch = regexp;
      getCurrentPanel().getTree().highlightVariables(m_LastVariableSearch);
    }
    else {
      getCurrentPanel().getTree().highlightVariables(null);
    }
  }

  /**
   * Cleans up the flow, e.g., removing disabled actors, unused global actors.
   *
   * @see		ActorUtils#cleanUpFlow(AbstractActor)
   */
  public void cleanUpFlow() {
    if (hasCurrentPanel())
      getCurrentPanel().cleanUpFlow();
  }

  /**
   * Checks the variable usage, i.e., all variables must at least be set
   * once somewhere in the flow.
   */
  public void checkVariables() {
    if (hasCurrentPanel())
      getCurrentPanel().checkVariables();
  }

  /**
   * Enables/disables the interactive behaviour of {@link AutomatableInteractiveActor}
   * actors.
   *
   * @param enable	true if to enable the interactive behaviour
   */
  public void manageInteractiveActors(boolean enable) {
    if (hasCurrentPanel())
      getCurrentPanel().manageInteractiveActors(enable);
  }

  /**
   * Sets whether to ignore name changes of actors or to prompt the user with
   * a dialog for propagating the changes throughout the tree.
   *
   * @param ignore	true if to ignore the changes and suppress dialog
   */
  public void setIgnoreNameChanges(boolean ignore) {
    if (hasCurrentPanel())
      getCurrentPanel().setIgnoreNameChanges(ignore);
  }

  /**
   * If a single actor is selected, user gets prompted whether to only
   * process below this actor instead of full flow.
   */
  public void processActorsPrompt() {
    if (hasCurrentPanel())
      getCurrentPanel().processActorsPrompt();
  }

  /**
   * Enables/disables all breakpoints in the flow (before execution).
   *
   * @param enable	if true then breakpoints get enabled
   */
  public void enableBreakpoints(boolean enable) {
    if (hasCurrentPanel())
      getCurrentPanel().getTree().enableBreakpoints(enable);
  }

  /**
   * Removes all breakpoints in the flow.
   */
  public void removeAllBreakpoints() {
    if (hasCurrentPanel())
      getCurrentPanel().getTree().processActor(null, new RemoveBreakpoints());
  }

  /**
   * Displays the variables in the currently running flow.
   */
  public void showVariables() {
    if (hasCurrentPanel())
      getCurrentPanel().showVariables();
  }

  /**
   * Displays the storage in the currently running flow.
   */
  public void showStorage() {
    if (hasCurrentPanel())
      getCurrentPanel().showStorage();
  }

  /**
   * Displays a new flow editor window/frame.
   *
   * @return		the new editor panel
   */
  public FlowEditorPanel newWindow() {
    return newWindow(null);
  }

  /**
   * Displays a new flow editor window/frame with the specified actor.
   *
   * @param actor	the actor to display, use null to ignore
   * @return		the new editor panel
   */
  public FlowEditorPanel newWindow(AbstractActor actor) {
    FlowEditorPanel 	result;
    ChildFrame 		oldFrame;
    ChildFrame 		newFrame;
    ChildWindow 	oldWindow;
    ChildWindow 	newWindow;

    result    = null;
    oldFrame = (ChildFrame) GUIHelper.getParent(m_Self, ChildFrame.class);
    if (oldFrame != null) {
      newFrame = oldFrame.getNewWindow();
      newFrame.setVisible(true);
      result  = (FlowEditorPanel) newFrame.getContentPane().getComponent(0);
    }
    else {
      oldWindow = (ChildWindow) GUIHelper.getParent(m_Self, ChildWindow.class);
      if (oldWindow != null) {
	newWindow = oldWindow.getNewWindow();
	newWindow.setVisible(true);
	result  = (FlowEditorPanel) newWindow.getContentPane().getComponent(0);
      }
    }

    // use same directory
    if (result != null) {
      result.setCurrentDirectory(getCurrentDirectory());
      if (actor != null) {
	result.setCurrentFlow(actor);
	result.getCurrentPanel().getTree().setModified(true);
      }
      result.update();
    }

    return result;
  }

  /**
   * Displays the source code (in nested format) of the current flow.
   */
  public void showSource() {
    if (hasCurrentPanel())
      getCurrentPanel().showSource();
  }

  /**
   * Displays a diff between current and last item in undo list.
   */
  public void showDiff() {
    if (hasCurrentPanel())
      getCurrentPanel().showDiff();
  }

  /**
   * Duplicates the current window/frame, including the current flow.
   *
   * @return		the new window
   */
  public Window duplicateTabInNewWindow() {
    Window		result;
    FlowEditorPanel 	panel;
    ChildFrame 		oldFrame;
    ChildFrame 		newFrame;
    ChildWindow 	oldWindow;
    ChildWindow 	newWindow;

    result   = null;
    panel    = null;
    oldFrame = (ChildFrame) GUIHelper.getParent(m_Self, ChildFrame.class);
    if (oldFrame != null) {
      newFrame = oldFrame.getNewWindow();
      newFrame.setVisible(true);
      panel  = (FlowEditorPanel) newFrame.getContentPane().getComponent(0);
      result = newFrame;
    }
    else {
      oldWindow = (ChildWindow) GUIHelper.getParent(m_Self, ChildWindow.class);
      if (oldWindow != null) {
	newWindow = oldWindow.getNewWindow();
	newWindow.setVisible(true);
	panel  = (FlowEditorPanel) newWindow.getContentPane().getComponent(0);
	result = newWindow;
      }
    }

    // copy information
    if (panel != null) {
      panel.setCurrentDirectory(getCurrentDirectory());
      panel.newTab();
      panel.setCurrentFlow(getCurrentPanel().getCurrentFlow());
      panel.setCurrentFile(getCurrentPanel().getCurrentFile());
      panel.setModified(getCurrentPanel().isModified());
      panel.update();
    }

    return result;
  }

  /**
   * Duplicates the current tab.
   *
   * @return		the new panel
   */
  public FlowPanel duplicateTab() {
    FlowPanel	result;
    FlowPanel 	current;

    result  = null;
    current = getCurrentPanel();

    if (current != null) {
      result = m_FlowPanels.newPanel();
      result.setCurrentFlow(current.getCurrentFlow());
      result.setCurrentFile(current.getCurrentFile());
      result.setModified(current.isModified());
    }

    return result;
  }

  /**
   * Displays the message in the status bar in a separate dialog.
   */
  protected void showMessage() {
    if (m_StatusBar.hasStatus())
      showMessage(m_StatusBar.getStatus(), false);
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
      GUIHelper.showErrorMessage(this, status, "Error");
    else
      GUIHelper.showInformationMessage(this, status, "Status");
  }

  /**
   * Displays a message.
   *
   * @param msg		the message to display
   */
  @Override
  public void showStatus(String msg) {
    m_StatusBar.showStatus(msg);
  }

  /**
   * An undo event occurred.
   *
   * @param e		the event
   */
  public void undoOccurred(UndoEvent e) {
    updateActions();
  }

  /**
   * Sets the current directory in the FileChooser use for opening flows.
   *
   * @param value	the new current directory to use
   */
  public void setCurrentDirectory(File value)  {
    m_FileChooser.setCurrentDirectory(value);
    m_FilenameProposer.setDirectory(value.getAbsolutePath());
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
   * Requests that this Component get the input focus, and that this
   * Component's top-level ancestor become the focused Window. This component
   * must be displayable, visible, and focusable for the request to be
   * granted.
   */
  @Override
  public void grabFocus() {
    if (hasCurrentPanel())
      getCurrentPanel().grabFocus();
  }

  /**
   * Refreshes the tabs.
   */
  public void refreshTabs() {
    m_Tabs.refresh(getCurrentTree());
  }

  /**
   * Returns the tree.
   *
   * @return		the tree, null if none available
   */
  public Tree getCurrentTree() {
    return m_FlowPanels.getCurrentTree();
  }

  /**
   * Returns the recent files handler in use.
   *
   * @return		the handler
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
    return new FlowPanel(m_FlowPanels).getSendToClasses();
  }

  /**
   * Checks whether something to send is available.
   *
   * @param cls		the classes to retrieve the item for
   * @return		true if an object is available for sending
   */
  @Override
  public boolean hasSendToItem(Class[] cls) {
    return hasCurrentPanel() && getCurrentPanel().hasSendToItem(cls);
  }

  /**
   * Returns the object to send.
   *
   * @param cls		the classes to retrieve the item for
   * @return		the item to send
   */
  @Override
  public Object getSendToItem(Class[] cls) {
    if (hasCurrentPanel())
      return getCurrentPanel().getSendToItem(cls);
    else
      return null;
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

  /**
   * Returns the properties that define the menu in the editor.
   *
   * @return		the properties
   */
  public static synchronized Properties getPropertiesMenu() {
    if (m_PropertiesMenu == null)
      m_PropertiesMenu = Environment.getInstance().read(FlowEditorPanelMenuDefinition.KEY);

    return m_PropertiesMenu;
  }

  /**
   * Returns the properties for the tree popup menu.
   *
   * @return		the properties
   */
  public static synchronized Properties getPropertiesTreePopup() {
    if (m_PropertiesTreePopup == null)
      m_PropertiesTreePopup = Environment.getInstance().read(FlowEditorTreePopupMenuDefinition.KEY);

    return m_PropertiesTreePopup;
  }

  /**
   * For customizing the popup menu.
   *
   * @param source	the source statusbar
   * @param menu	the menu to customize
   */
  @Override
  public void customizePopupMenu(final BaseStatusBar source, JPopupMenu menu) {
    JMenuItem	menuitem;
    
    if ((source.getStatus() != null) && (source.getStatus().length() > 0)) {
      menuitem = new JMenuItem("Copy");
      menuitem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          GUIHelper.copyToClipboard(source.getStatus());
        }
      });
      menu.add(menuitem);
    }
  }
  
  /**
   * Returns the preferred toolbar location.
   * 
   * @return		the location
   */
  public ToolBarLocation getPreferredToolBarLocation() {
    return m_PreferredToolBarLocation;
  }
}
