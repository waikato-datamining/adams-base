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
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.flow;

import adams.core.Properties;
import adams.core.StatusMessageHandler;
import adams.core.io.FilenameProposer;
import adams.core.io.PlaceholderFile;
import adams.env.Environment;
import adams.env.FlowEditorPanelDefinition;
import adams.env.FlowEditorPanelMenuDefinition;
import adams.env.FlowEditorTreePopupMenuDefinition;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.gui.application.ChildFrame;
import adams.gui.application.ChildWindow;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.chooser.FlowFileChooser;
import adams.gui.core.BaseMenu;
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
import adams.gui.flow.menu.DebugDisableAllBreakpoints;
import adams.gui.flow.menu.DebugEnableAllBreakpoints;
import adams.gui.flow.menu.DebugRemoveAllBreakpoints;
import adams.gui.flow.menu.DebugStorage;
import adams.gui.flow.menu.DebugVariables;
import adams.gui.flow.menu.EditCheckVariables;
import adams.gui.flow.menu.EditCleanUpFlow;
import adams.gui.flow.menu.EditDiff;
import adams.gui.flow.menu.EditEnableUndo;
import adams.gui.flow.menu.EditFind;
import adams.gui.flow.menu.EditFindNext;
import adams.gui.flow.menu.EditIgnoreNameChanges;
import adams.gui.flow.menu.EditInteractiveActors;
import adams.gui.flow.menu.EditLocateActor;
import adams.gui.flow.menu.EditProcessActors;
import adams.gui.flow.menu.EditRedo;
import adams.gui.flow.menu.EditTimedActors;
import adams.gui.flow.menu.EditUndo;
import adams.gui.flow.menu.ExecutionClearGraphicalOutput;
import adams.gui.flow.menu.ExecutionDisplayErrors;
import adams.gui.flow.menu.ExecutionGC;
import adams.gui.flow.menu.ExecutionHeadless;
import adams.gui.flow.menu.ExecutionKill;
import adams.gui.flow.menu.ExecutionPauseResume;
import adams.gui.flow.menu.ExecutionRun;
import adams.gui.flow.menu.ExecutionStop;
import adams.gui.flow.menu.ExecutionValidateSetup;
import adams.gui.flow.menu.FileCheckOnSave;
import adams.gui.flow.menu.FileClose;
import adams.gui.flow.menu.FileCloseTab;
import adams.gui.flow.menu.FileExport;
import adams.gui.flow.menu.FileImport;
import adams.gui.flow.menu.FileNewFlow;
import adams.gui.flow.menu.FileOpen;
import adams.gui.flow.menu.FileOpenRemoteFlow;
import adams.gui.flow.menu.FileProperties;
import adams.gui.flow.menu.FileRevert;
import adams.gui.flow.menu.FileSave;
import adams.gui.flow.menu.FileSaveAs;
import adams.gui.flow.menu.FlowEditorAction;
import adams.gui.flow.menu.ViewHighlightVariables;
import adams.gui.flow.menu.ViewRedraw;
import adams.gui.flow.menu.ViewRemoveVariableHighlights;
import adams.gui.flow.menu.ViewShowAnnotations;
import adams.gui.flow.menu.ViewShowInputOutput;
import adams.gui.flow.menu.ViewShowQuickInfo;
import adams.gui.flow.menu.ViewShowSource;
import adams.gui.flow.menu.ViewShowToolbar;
import adams.gui.flow.menu.ViewStatistics;
import adams.gui.flow.menu.WindowDuplicateInTab;
import adams.gui.flow.menu.WindowDuplicateInWindow;
import adams.gui.flow.menu.WindowNew;
import adams.gui.flow.tab.FlowTabManager;
import adams.gui.flow.tree.Tree;
import adams.gui.sendto.SendToActionSupporter;
import adams.gui.sendto.SendToActionUtils;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

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

  /** the "open" action. */
  protected FlowEditorAction m_ActionFileOpen;

  /** the "open remote flow" action. */
  protected FlowEditorAction m_ActionFileOpenRemote;

  /** the "open recent" submenu. */
  protected JMenu m_MenuFileOpenRecent;

  /** the "new" action. */
  protected FlowEditorAction m_ActionFileNew;

  /** the "save" action. */
  protected FlowEditorAction m_ActionFileSave;

  /** the "save as" action. */
  protected FlowEditorAction m_ActionFileSaveAs;

  /** the "check on save" action. */
  protected FlowEditorAction m_ActionFileCheckOnSave;

  /** the "revert" action. */
  protected FlowEditorAction m_ActionFileRevert;

  /** the "export" action. */
  protected FlowEditorAction m_ActionFileExport;

  /** the "import" action. */
  protected FlowEditorAction m_ActionFileImport;

  /** the "properties" action. */
  protected FlowEditorAction m_ActionFileProperties;

  /** the "close tab" action. */
  protected FlowEditorAction m_ActionFileCloseTab;

  /** the "close" action. */
  protected FlowEditorAction m_ActionFileClose;

  /** the toggle undo action. */
  protected FlowEditorAction m_ActionEditEnableUndo;

  /** the undo action. */
  protected FlowEditorAction m_ActionEditUndo;

  /** the redo action. */
  protected FlowEditorAction m_ActionEditRedo;

  /** the diff action. */
  protected FlowEditorAction m_ActionEditDiff;

  /** the find action. */
  protected FlowEditorAction m_ActionEditFind;

  /** the find next action. */
  protected FlowEditorAction m_ActionEditFindNext;

  /** the locate actor action. */
  protected FlowEditorAction m_ActionEditLocateActor;

  /** the remove disabled actors action. */
  protected FlowEditorAction m_ActionEditCleanUpFlow;

  /** the check variables action. */
  protected FlowEditorAction m_ActionEditCheckVariables;

  /** the interactive actors action. */
  protected FlowEditorAction m_ActionEditInteractiveActors;

  /** the timed actors action. */
  protected FlowEditorAction m_ActionEditTimedActors;

  /** the ignore name changes action. */
  protected FlowEditorAction m_ActionEditIgnoreNameChanges;

  /** the "process actors" action. */
  protected FlowEditorAction m_ActionEditProcessActors;

  /** the "enable all breakpoints" action. */
  protected FlowEditorAction m_ActionDebugEnableAllBreakpoints;

  /** the "remove all breakpoints" action. */
  protected FlowEditorAction m_ActionDebugRemoveAllBreakpoints;

  /** the "disable all breakpoints" action. */
  protected FlowEditorAction m_ActionDebugDisableAllBreakpoints;

  /** the "variables" action. */
  protected FlowEditorAction m_ActionDebugVariables;

  /** the "storage" action. */
  protected FlowEditorAction m_ActionDebugStorage;

  /** the "headless" action. */
  protected FlowEditorAction m_ActionExecutionHeadless;

  /** the "gc" action. */
  protected FlowEditorAction m_ActionExecutionGC;

  /** the "check setup" action. */
  protected FlowEditorAction m_ActionExecutionValidateSetup;

  /** the "run" action. */
  protected FlowEditorAction m_ActionExecutionRun;

  /** the "pause" action. */
  protected FlowEditorAction m_ActionExecutionPauseAndResume;

  /** the "pause" menu item. */
  protected FlowEditorAction m_MenuItemExecutionPauseAndResume;

  /** the "stop" action. */
  protected FlowEditorAction m_ActionExecutionStop;

  /** the "kill" action. */
  protected FlowEditorAction m_ActionExecutionKill;

  /** the "display errors" action. */
  protected FlowEditorAction m_ActionExecutionDisplayErrors;

  /** the "Clear graphical output" action. */
  protected FlowEditorAction m_ActionExecutionClearGraphicalOutput;

  /** the "show toolbar" action. */
  protected FlowEditorAction m_ActionViewShowToolbar;

  /** the "show quick info" action. */
  protected FlowEditorAction m_ActionViewShowQuickInfo;

  /** the "show annotations" action. */
  protected FlowEditorAction m_ActionViewShowAnnotations;

  /** the "show input/output" action. */
  protected FlowEditorAction m_ActionViewShowInputOutput;

  /** the highlight variables action. */
  protected FlowEditorAction m_ActionViewHighlightVariables;

  /** the remove variable highlights action. */
  protected FlowEditorAction m_ActionViewRemoveVariableHighlights;

  /** the "show source" action. */
  protected FlowEditorAction m_ActionViewShowSource;

  /** the "statistic" action. */
  protected FlowEditorAction m_ActionViewStatistics;

  /** the "redraw" action. */
  protected FlowEditorAction m_ActionViewRedraw;

  /** the "new window" action. */
  protected FlowEditorAction m_ActionNewWindow;

  /** the "duplicate tab in new window" action. */
  protected FlowEditorAction m_ActionDuplicateTabInNewWindow;

  /** the "duplicate tab" action. */
  protected FlowEditorAction m_ActionDuplicateTab;

  /** menu items. */
  protected List<FlowEditorAction> m_MenuItems;

  /** additional menu items. */
  protected List<AbstractFlowEditorMenuItem> m_AdditionalMenuItems;

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
    m_FileChooser         = new FlowFileChooser();
    m_FileChooser.setMultiSelectionEnabled(true);
    m_FileChooser.setCurrentDirectory(new PlaceholderFile(getPropertiesEditor().getPath("InitialDir", "%h")));
    m_FilenameProposer    = new FilenameProposer(FlowPanel.PREFIX_NEW, AbstractActor.FILE_EXTENSION, getPropertiesEditor().getPath("InitialDir", "%h"));

    m_MenuItems           = new ArrayList<FlowEditorAction>();
    m_AdditionalMenuItems = new ArrayList<AbstractFlowEditorMenuItem>();
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

    props = getPropertiesEditor();

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
  protected void initActions() {
    FlowEditorAction	action;
    
    // File/New (flow)
    action = new FileNewFlow();
    m_ActionFileNew = action;
    m_MenuItems.add(action);

    // File/Open
    action = new FileOpen();
    m_ActionFileOpen = action;
    m_MenuItems.add(action);

    // File/Open remote flow
    action = new FileOpenRemoteFlow();
    m_ActionFileOpenRemote = action;
    m_MenuItems.add(action);

    // File/Save
    action = new FileSave();
    m_ActionFileSave = action;
    m_MenuItems.add(action);

    // File/Save as
    action = new FileSaveAs();
    m_ActionFileSaveAs = action;
    m_MenuItems.add(action);

    // File/Check on save
    action = new FileCheckOnSave();
    m_ActionFileCheckOnSave = action;
    m_MenuItems.add(action);

    // File/Revert
    action = new FileRevert();
    m_ActionFileRevert = action;
    m_MenuItems.add(action);

    // File/Close tab
    action = new FileCloseTab();
    m_ActionFileCloseTab = action;
    m_MenuItems.add(action);

    // File/Import
    action = new FileImport();
    m_ActionFileImport = action;
    m_MenuItems.add(action);

    // File/Export
    action = new FileExport();
    m_ActionFileExport = action;
    m_MenuItems.add(action);

    // File/Properties
    action = new FileProperties();
    m_ActionFileProperties = action;
    m_MenuItems.add(action);

    // File/Close
    action = new FileClose();
    m_ActionFileClose = action;
    m_MenuItems.add(action);

    // Edit/Enable Undo
    action = new EditEnableUndo();
    m_ActionEditEnableUndo = action;
    m_MenuItems.add(action);

    // Edit/Undo
    action = new EditUndo();
    m_ActionEditUndo = action;
    m_MenuItems.add(action);

    // Edit/Redo
    action = new EditRedo();
    m_ActionEditRedo = action;
    m_MenuItems.add(action);

    // Edit/Diff
    action = new EditDiff();
    m_ActionEditDiff = action;
    m_MenuItems.add(action);

    // Edit/Find
    action = new EditFind();
    m_ActionEditFind = action;
    m_MenuItems.add(action);

    // Edit/Find next
    action = new EditFindNext();
    m_ActionEditFindNext = action;
    m_MenuItems.add(action);

    // Edit/Locate actor
    action = new EditLocateActor();
    m_ActionEditLocateActor = action;
    m_MenuItems.add(action);

    // Edit/Clean up flow
    action = new EditCleanUpFlow();
    m_ActionEditCleanUpFlow = action;
    m_MenuItems.add(action);

    // Edit/Check variables
    action = new EditCheckVariables();
    m_ActionEditCheckVariables = action;
    m_MenuItems.add(action);

    // Edit/Interactive actors (checkbox)
    action = new EditInteractiveActors();
    m_ActionEditInteractiveActors = action;
    m_MenuItems.add(action);

    // Edit/Timed actors (checkbox)
    action = new EditTimedActors();
    m_ActionEditTimedActors = action;
    m_MenuItems.add(action);

    // Edit/Ignore name changes (checkbox)
    action = new EditIgnoreNameChanges();
    m_ActionEditIgnoreNameChanges = action;
    m_MenuItems.add(action);

    // Edit/Process actors
    action = new EditProcessActors();
    m_ActionEditProcessActors = action;
    m_MenuItems.add(action);

    // Debug/Enable all breakpoints
    action = new DebugEnableAllBreakpoints();
    m_ActionDebugEnableAllBreakpoints = action;
    m_MenuItems.add(action);

    // Debug/Disable all breakpoints
    action = new DebugDisableAllBreakpoints();
    m_ActionDebugDisableAllBreakpoints = action;
    m_MenuItems.add(action);

    // Debug/Remove all breakpoints
    action = new DebugRemoveAllBreakpoints();
    m_ActionDebugRemoveAllBreakpoints = action;
    m_MenuItems.add(action);

    // Debug/Variables
    action = new DebugVariables();
    m_ActionDebugVariables = action;
    m_MenuItems.add(action);

    // Debug/Storage
    action = new DebugStorage();
    m_ActionDebugStorage = action;
    m_MenuItems.add(action);

    // Execution/Validate setup
    action = new ExecutionValidateSetup();
    m_ActionExecutionValidateSetup = action;
    m_MenuItems.add(action);

    // Execution/Run
    action = new ExecutionRun();
    m_ActionExecutionRun = action;
    m_MenuItems.add(action);

    // Execution/Pause+Resume
    action = new ExecutionPauseResume();
    m_ActionExecutionPauseAndResume = action;
    m_MenuItems.add(action);

    // Execution/Stop
    action = new ExecutionStop();
    m_ActionExecutionStop = action;
    m_MenuItems.add(action);

    // Execution/Kill
    action = new ExecutionKill();
    m_ActionExecutionKill = action;
    m_MenuItems.add(action);

    // Execution/Display errors
    action = new ExecutionDisplayErrors();
    m_ActionExecutionDisplayErrors = action;
    m_MenuItems.add(action);

    // Execution/Clear graphical output
    action = new ExecutionClearGraphicalOutput();
    m_ActionExecutionClearGraphicalOutput = action;
    m_MenuItems.add(action);

    // Execution/Headless
    action = new ExecutionHeadless();
    m_ActionExecutionHeadless = action;
    m_MenuItems.add(action);

    // Execution/GC
    action = new ExecutionGC();
    m_ActionExecutionGC = action;
    m_MenuItems.add(action);

    // View/Show toolbar
    action = new ViewShowToolbar();
    m_ActionViewShowToolbar = action;
    m_MenuItems.add(action);

    // View/Show quick info
    action = new ViewShowQuickInfo();
    m_ActionViewShowQuickInfo = action;
    m_MenuItems.add(action);

    // View/Show annotations
    action = new ViewShowAnnotations();
    m_ActionViewShowAnnotations = action;
    m_MenuItems.add(action);

    // View/Show input/output info
    action = new ViewShowInputOutput();
    m_ActionViewShowInputOutput = action;
    m_MenuItems.add(action);

    // View/Highlight variables
    action = new ViewHighlightVariables();
    m_ActionViewHighlightVariables = action;
    m_MenuItems.add(action);

    // View/Remove variable highlights
    action = new ViewRemoveVariableHighlights();
    m_ActionViewRemoveVariableHighlights = action;
    m_MenuItems.add(action);

    // View/Show source
    action = new ViewShowSource();
    m_ActionViewShowSource = action;
    m_MenuItems.add(action);

    // View/Statistics
    action = new ViewStatistics();
    m_ActionViewStatistics = action;
    m_MenuItems.add(action);

    // View/Redraw
    action = new ViewRedraw();
    m_ActionViewRedraw = action;
    m_MenuItems.add(action);

    // Window/New Window
    action = new WindowNew();
    m_ActionNewWindow = action;
    m_MenuItems.add(action);

    // Window/Duplicate in new window
    action = new WindowDuplicateInWindow();
    m_ActionDuplicateTabInNewWindow = action;
    m_MenuItems.add(action);

    // Window/Duplicate in new tab
    action = new WindowDuplicateInTab();
    m_ActionDuplicateTab = action;
    m_MenuItems.add(action);
  }

  /**
   * Initializes the toolbar.
   */
  @Override
  protected void initToolBar() {
    addToToolBar(m_ActionFileNew);
    addToToolBar(m_ActionFileOpen);
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
	    menu.insertSeparator(index);
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
    BaseMenu		menu;
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
      menu = new BaseMenu(MENU_FILE);
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
      actors = getPropertiesEditor().getProperty("NewList", Flow.class.getName()).replace(" ", "").split(",");
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
	  submenu.add(m_ActionFileNew);
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

      menu.add(m_ActionFileOpen);

      // File/Recent files
      submenu = new JMenu("Open recent");
      menu.add(submenu);
      m_RecentFilesHandler = new RecentFilesHandler<JMenu>(
	  SESSION_FILE, getPropertiesEditor().getInteger("MaxRecentFlows", 5), submenu);
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
      m_MenuFileOpenRecent = submenu;

      menu.add(m_ActionFileOpenRemote);
      menu.add(m_ActionFileSave);
      menu.add(m_ActionFileSaveAs);
      menu.add(m_ActionFileCheckOnSave);
      menu.add(m_ActionFileRevert);
      menu.add(m_ActionFileCloseTab);
      menu.addSeparator();
      menu.add(m_ActionFileImport);
      menu.add(m_ActionFileExport);
      SendToActionUtils.addSendToSubmenu(this, menu);
      menu.addSeparator();
      menu.add(m_ActionFileProperties);
      menu.addSeparator();
      menu.add(m_ActionFileClose);

      // Edit
      menu = new BaseMenu(MENU_EDIT);
      result.add(menu);
      menu.setMnemonic('E');
      menu.addChangeListener(new ChangeListener() {
	@Override
	public void stateChanged(ChangeEvent e) {
	  updateActions();
	}
      });

      menu.add(m_ActionEditEnableUndo);
      menu.add(m_ActionEditUndo);
      menu.add(m_ActionEditRedo);
      menu.add(m_ActionEditDiff);
      menu.addSeparator();
      menu.add(m_ActionEditFind);
      menu.add(m_ActionEditFindNext);
      menu.add(m_ActionEditLocateActor);
      menu.addSeparator();
      menu.add(m_ActionEditCleanUpFlow);
      menu.add(m_ActionEditCheckVariables);
      menu.add(m_ActionEditInteractiveActors);
      menu.add(m_ActionEditTimedActors);
      menu.add(m_ActionEditIgnoreNameChanges);
      menu.addSeparator();
      menu.add(m_ActionEditProcessActors);

      // Debug
      menu = new BaseMenu(MENU_DEBUG);
      result.add(menu);
      menu.setMnemonic('D');
      menu.addChangeListener(new ChangeListener() {
	@Override
	public void stateChanged(ChangeEvent e) {
	  updateActions();
	}
      });

      menu.add(m_ActionDebugEnableAllBreakpoints);
      menu.add(m_ActionDebugDisableAllBreakpoints);
      menu.add(m_ActionDebugRemoveAllBreakpoints);
      menu.add(m_ActionDebugVariables);
      menu.add(m_ActionDebugStorage);

      // Execution
      menu = new BaseMenu(MENU_EXECUTION);
      result.add(menu);
      menu.setMnemonic('E');
      menu.addChangeListener(new ChangeListener() {
	@Override
	public void stateChanged(ChangeEvent e) {
	  updateActions();
	}
      });

      menu.add(m_ActionExecutionValidateSetup);
      menu.add(m_ActionExecutionRun);
      menu.add(m_ActionExecutionPauseAndResume);
      menu.add(m_ActionExecutionStop);
      menu.add(m_ActionExecutionKill);
      menu.addSeparator();
      menu.add(m_ActionExecutionDisplayErrors);
      menu.add(m_ActionExecutionClearGraphicalOutput);
      menu.addSeparator();
      menu.add(m_ActionExecutionHeadless);
      menu.add(m_ActionExecutionGC);

      // View
      menu = new BaseMenu(MENU_VIEW);
      result.add(menu);
      menu.setMnemonic('V');
      menu.addChangeListener(new ChangeListener() {
	@Override
	public void stateChanged(ChangeEvent e) {
	  updateActions();
	}
      });

      menu.add(m_ActionViewShowToolbar);
      menu.add(m_ActionViewShowQuickInfo);
      menu.add(m_ActionViewShowAnnotations);
      menu.add(m_ActionViewShowInputOutput);
      m_Tabs.addTabsSubmenu(menu);
      menu.addSeparator();
      menu.add(m_ActionViewHighlightVariables);
      menu.add(m_ActionViewRemoveVariableHighlights);
      menu.add(m_ActionViewRedraw);
      menu.addSeparator();
      menu.add(m_ActionViewShowSource);
      menu.add(m_ActionViewStatistics);

      // Window
      if ((GUIHelper.getParent(m_Self, ChildFrame.class) != null) && (getParentDialog() == null)) {
	menu = new BaseMenu(MENU_WINDOW);
	result.add(menu);
	menu.setMnemonic('W');
	menu.addChangeListener(new ChangeListener() {
	  @Override
	  public void stateChanged(ChangeEvent e) {
	    updateActions();
	  }
	});

	menu.add(m_ActionNewWindow);
	menu.add(m_ActionDuplicateTabInNewWindow);
	menu.add(m_ActionDuplicateTab);
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
  public void updateActions() {
    // regular menu items
    for (FlowEditorAction action: m_MenuItems)
      action.update(m_Self);
    
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
  public void setCurrentFile(File value) {
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
      panel.load(m_FileChooser.getReaderForFile(file), file, false);
    }
  }

  /**
   * Attempts to load/run the file. If non-existent, then a new flow will be
   * created and the current filename set to the provided one.
   *
   * @param file	the file to load
   */
  public void runUnsafe(File file) {
    FlowPanel	panel;

    panel = m_FlowPanels.newPanel();
    if (!file.exists()) {
      panel.reset(new Flow());
      panel.setCurrentFile(new File(file.getAbsolutePath()));
      updateActions();
    }
    else {
      panel.load(m_FileChooser.getReaderForFile(file), file, true);
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
   * <br><br>
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
   * <br><br>
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
   * Stops the flow.
   */
  public void stop() {
    if (hasCurrentPanel())
      getCurrentPanel().stop();
  }

  /**
   * Kills the flow.
   */
  public void kill() {
    if (hasCurrentPanel())
      getCurrentPanel().kill();
  }

  /**
   * Cleans up the last flow that was run.
   */
  public void cleanUp() {
    m_FlowPanels.cleanUp();
    for (FlowEditorAction action: m_MenuItems)
      action.cleanUp();
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
  public void close() {
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

  /**
   * Returns the properties that define the editor.
   *
   * @return		the properties
   */
  public static synchronized Properties getPropertiesEditor() {
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
}
