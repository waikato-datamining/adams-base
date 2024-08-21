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
 * SpreadSheetViewerPanel.java
 * Copyright (C) 2009-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools;

import adams.core.CleanUpHandler;
import adams.core.Properties;
import adams.core.classmanager.ClassManager;
import adams.core.io.PlaceholderFile;
import adams.core.logging.LoggingHelper;
import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.io.input.MultiSheetSpreadSheetReader;
import adams.data.io.input.SpreadSheetReader;
import adams.data.io.output.CsvSpreadSheetWriter;
import adams.data.io.output.SpreadSheetWriter;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;
import adams.gui.application.ChildFrame;
import adams.gui.chooser.SpreadSheetFileChooser;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.GUIHelper;
import adams.gui.core.ImageManager;
import adams.gui.core.MenuBarProvider;
import adams.gui.core.MouseUtils;
import adams.gui.core.RecentFilesHandlerWithCommandline;
import adams.gui.core.RecentFilesHandlerWithCommandline.Setup;
import adams.gui.core.SpreadSheetTable;
import adams.gui.core.SpreadSheetTableModel;
import adams.gui.core.ToolBarPanel;
import adams.gui.core.UISettings;
import adams.gui.core.spreadsheettable.CellRenderingCustomizer;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.event.RecentItemEvent;
import adams.gui.event.RecentItemListener;
import adams.gui.event.TabVisibilityChangeEvent;
import adams.gui.goe.GenericObjectEditorDialog;
import adams.gui.sendto.SendToActionSupporter;
import adams.gui.sendto.SendToActionUtils;
import adams.gui.tools.spreadsheetviewer.AbstractDataPlugin;
import adams.gui.tools.spreadsheetviewer.AbstractViewPlugin;
import adams.gui.tools.spreadsheetviewer.MultiPagePane;
import adams.gui.tools.spreadsheetviewer.SpreadSheetPanel;
import adams.gui.tools.spreadsheetviewer.menu.DataChart;
import adams.gui.tools.spreadsheetviewer.menu.DataComputeDifference;
import adams.gui.tools.spreadsheetviewer.menu.DataConvert;
import adams.gui.tools.spreadsheetviewer.menu.DataFilterColumns;
import adams.gui.tools.spreadsheetviewer.menu.DataFilterRows;
import adams.gui.tools.spreadsheetviewer.menu.DataSort;
import adams.gui.tools.spreadsheetviewer.menu.DataTransform;
import adams.gui.tools.spreadsheetviewer.menu.EditClearClipboard;
import adams.gui.tools.spreadsheetviewer.menu.EditPasteAsNew;
import adams.gui.tools.spreadsheetviewer.menu.FileClosePage;
import adams.gui.tools.spreadsheetviewer.menu.FileExit;
import adams.gui.tools.spreadsheetviewer.menu.FileGarbageCollectionOnClose;
import adams.gui.tools.spreadsheetviewer.menu.FileOpen;
import adams.gui.tools.spreadsheetviewer.menu.FileSave;
import adams.gui.tools.spreadsheetviewer.menu.FileSaveAs;
import adams.gui.tools.spreadsheetviewer.menu.HelpFormulas;
import adams.gui.tools.spreadsheetviewer.menu.HelpQuery;
import adams.gui.tools.spreadsheetviewer.menu.SpreadSheetViewerAction;
import adams.gui.tools.spreadsheetviewer.menu.ViewApplyToAll;
import adams.gui.tools.spreadsheetviewer.menu.ViewCellRenderingCustomizer;
import adams.gui.tools.spreadsheetviewer.menu.ViewDecimals;
import adams.gui.tools.spreadsheetviewer.menu.ViewShowCellTypes;
import adams.gui.tools.spreadsheetviewer.menu.ViewShowFormulas;
import adams.gui.tools.spreadsheetviewer.tab.ViewerTabManager;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A panel for viewing SpreadSheet files.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class SpreadSheetViewerPanel
  extends ToolBarPanel
  implements MenuBarProvider, SendToActionSupporter, CleanUpHandler {

  /** for serialization. */
  private static final long serialVersionUID = -7759194648757624838L;

  /** the name of the props file. */
  public final static String FILENAME = "SpreadSheetViewer.props";

  /** the name of the menu props file. */
  public final static String FILENAME_MENU = "SpreadSheetViewerMenu.props";

  /** the file to store the recent files in. */
  public final static String SESSION_FILE = "SpreadSheetViewerSession.props";

  /** the properties. */
  protected static Properties m_Properties;

  /** the menu properties. */
  protected static Properties m_PropertiesMenu;

  /** the split pane. */
  protected BaseSplitPane m_SplitPane;

  /** the multi-page pane for displaying the spreadsheets. */
  protected MultiPagePane m_MultiPagePane;

  /** the viewer tabs. */
  protected ViewerTabManager m_ViewerTabs;

  /** the menu bar, if used. */
  protected JMenuBar m_MenuBar;

  /** the "open" menu item. */
  protected SpreadSheetViewerAction m_ActionFileOpen;

  /** the "load recent" submenu. */
  protected JMenu m_MenuFileOpenRecent;

  /** the "save" menu item. */
  protected SpreadSheetViewerAction m_ActionFileSave;

  /** the "save as" menu item. */
  protected SpreadSheetViewerAction m_ActionFileSaveAs;

  /** the "close" menu item. */
  protected SpreadSheetViewerAction m_ActionFileClosePage;

  /** the "use GC" menu item. */
  protected SpreadSheetViewerAction m_ActionFileGarbageCollectionOnClose;

  /** the "exit" menu item. */
  protected SpreadSheetViewerAction m_ActionFileExit;

  /** the "clear clipboard" menu item. */
  protected SpreadSheetViewerAction m_ActionEditClearClipboard;

  /** the "paste from clipboard" menu item. */
  protected SpreadSheetViewerAction m_ActionEditPasteFromClipboard;

  /** the "filter columns" menu item. */
  protected SpreadSheetViewerAction m_ActionDataFilterColumns;

  /** the "filter rows" menu item. */
  protected SpreadSheetViewerAction m_ActionDataFilterRows;

  /** the "compute difference" menu item. */
  protected SpreadSheetViewerAction m_ActionDataComputeDifference;

  /** the "Convert" menu item. */
  protected SpreadSheetViewerAction m_ActionDataConvert;

  /** the "Transform" menu item. */
  protected SpreadSheetViewerAction m_ActionDataTransform;

  /** the "Sort" menu item. */
  protected SpreadSheetViewerAction m_ActionDataSort;

  /** the "Chart" menu item. */
  protected SpreadSheetViewerAction m_ActionDataChart;

  /** the "apply to all" menu item. */
  protected SpreadSheetViewerAction m_ActionViewApplyToAll;

  /** the "displayed decimals" menu item. */
  protected SpreadSheetViewerAction m_ActionViewDisplayedDecimals;

  /** the "rendering" menu item. */
  protected SpreadSheetViewerAction m_ActionViewCellRenderingCustomizer;

  /** the "show formulas" menu item. */
  protected SpreadSheetViewerAction m_ActionViewShowFormulas;

  /** the "show cell types" menu item. */
  protected SpreadSheetViewerAction m_ActionViewShowCellTypes;

  /** the "formulas" help menu item. */
  protected SpreadSheetViewerAction m_ActionHelpFormulas;

  /** the "query" help menu item. */
  protected SpreadSheetViewerAction m_MenuItemHelpQuery;

  /** the data plugin menu items. */
  protected List<JMenuItem> m_MenuItemDataPlugins;

  /** the data plugins. */
  protected List<AbstractDataPlugin> m_DataPlugins;

  /** the view plugin menu items. */
  protected List<JMenuItem> m_MenuItemViewPlugins;

  /** the view plugins. */
  protected List<AbstractViewPlugin> m_ViewPlugins;

  /** the filedialog for loading CSV files. */
  protected SpreadSheetFileChooser m_FileChooser;

  /** the recent files handler. */
  protected RecentFilesHandlerWithCommandline<JMenu> m_RecentFilesHandler;

  /** whether to apply settings to all tabs or just current one. */
  protected boolean m_ApplyToAll;

  /** menu items. */
  protected List<SpreadSheetViewerAction> m_Actions;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_FileChooser = new SpreadSheetFileChooser();
    m_FileChooser.setMultiSelectionEnabled(true);

    m_RecentFilesHandler = null;
    m_ApplyToAll         = false;
    m_Actions            = new ArrayList<>();
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    setToolBarLocation(ToolBarLocation.NORTH);

    m_SplitPane = new BaseSplitPane(BaseSplitPane.HORIZONTAL_SPLIT, true);
    m_SplitPane.setOneTouchExpandable(true);
    m_SplitPane.setResizeWeight(1.0);
    m_SplitPane.setUISettingsParameters(getClass(), "TabsDivider");
    add(m_SplitPane, BorderLayout.CENTER);

    m_MultiPagePane = new MultiPagePane(this);
    m_MultiPagePane.setDividerLocation(UISettings.get(getClass(), "SheetsDivider", 250));
    m_MultiPagePane.setUISettingsParameters(getClass(), "SheetsDivider");
    m_SplitPane.setLeftComponent(m_MultiPagePane);

    m_ViewerTabs = new ViewerTabManager(this);
    m_ViewerTabs.addTabVisibilityChangeListener((TabVisibilityChangeEvent e) ->
      m_SplitPane.setRightComponentHidden(m_ViewerTabs.getTabCount() == 0));
    m_SplitPane.setRightComponent(m_ViewerTabs);
    m_SplitPane.setRightComponentHidden(m_ViewerTabs.getTabCount() == 0);
    if (!UISettings.has(getClass(), "TabsDivider"))
      m_SplitPane.setDividerLocation(0.8);
    else
      m_SplitPane.setDividerLocation(UISettings.get(getClass(), "TabsDivider", 850));
  }

  /**
   * Initializes the actions.
   */
  @Override
  protected void initActions() {
    SpreadSheetViewerAction	action;

    // File/Open
    action = new FileOpen();
    m_ActionFileOpen = action;
    m_Actions.add(action);

    // File/Save as
    action = new FileSave();
    m_ActionFileSave = action;
    m_Actions.add(action);

    // File/Save as
    action = new FileSaveAs();
    m_ActionFileSaveAs = action;
    m_Actions.add(action);

    // File/Close page
    action = new FileClosePage();
    m_ActionFileClosePage = action;
    m_Actions.add(action);

    // File/Use GC
    action = new FileGarbageCollectionOnClose();
    m_ActionFileGarbageCollectionOnClose = action;
    m_Actions.add(action);

    // File/Exit
    action = new FileExit();
    m_ActionFileExit = action;
    m_Actions.add(action);

    // Edit/Clear clipboard
    action = new EditClearClipboard();
    m_ActionEditClearClipboard = action;
    m_Actions.add(action);

    // Edit/Paste from clipboard
    action = new EditPasteAsNew();
    m_ActionEditPasteFromClipboard = action;
    m_Actions.add(action);

    // Data/Filter columns
    action = new DataFilterColumns();
    m_ActionDataFilterColumns = action;
    m_Actions.add(action);

    // Data/Filter rows
    action = new DataFilterRows();
    m_ActionDataFilterRows = action;
    m_Actions.add(action);

    // Data/Convert
    action = new DataConvert();
    m_ActionDataConvert = action;
    m_Actions.add(action);

    // Data/Transform
    action = new DataTransform();
    m_ActionDataTransform = action;
    m_Actions.add(action);

    // Data/Sort
    action = new DataSort();
    m_ActionDataSort = action;
    m_Actions.add(action);

    // Data/Chart
    action = new DataChart();
    m_ActionDataChart = action;
    m_Actions.add(action);

    // Data/Compute difference
    action = new DataComputeDifference();
    m_ActionDataComputeDifference = action;
    m_Actions.add(action);

    // View/Apply to all
    action = new ViewApplyToAll();
    m_ActionViewApplyToAll = action;
    m_Actions.add(action);

    // View/Decimals
    action = new ViewDecimals();
    m_ActionViewDisplayedDecimals = action;
    m_Actions.add(action);

    // View/Rendering
    action = new ViewCellRenderingCustomizer();
    m_ActionViewCellRenderingCustomizer = action;
    m_Actions.add(action);

    // View/Show formulas
    action = new ViewShowFormulas();
    m_ActionViewShowFormulas = action;
    m_Actions.add(action);

    // View/Show cellt types
    action = new ViewShowCellTypes();
    m_ActionViewShowCellTypes = action;
    m_Actions.add(action);

    // Help/Formulas
    action = new HelpFormulas();
    m_ActionHelpFormulas = action;
    m_Actions.add(action);

    // Help/Query
    action = new HelpQuery();
    m_MenuItemHelpQuery = action;
    m_Actions.add(action);
  }

  /**
   * Sets up the toolbar, using the actions.
   *
   * @see		#initActions()
   */
  @Override
  protected void initToolBar() {
    addToToolBar(m_ActionFileOpen);
    addToToolBar(m_ActionFileSave);
    addSeparator();
    addToToolBar(m_ActionDataFilterColumns);
    addToToolBar(m_ActionDataFilterRows);
    addToToolBar(m_ActionDataConvert);
    addToToolBar(m_ActionDataTransform);
    addToToolBar(m_ActionDataSort);
    addToToolBar(m_ActionDataChart);
    addSeparator();
    addToToolBar(m_ActionViewDisplayedDecimals);
  }

  /**
   * Creates a menu bar (singleton per panel object). Can be used in frames.
   *
   * @return		the menu bar
   */
  @Override
  public JMenuBar getMenuBar() {
    JMenuBar	result;
    JMenu	menu;
    JMenu	submenu;
    JMenuItem	menuitem;
    String[]	classes;

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
      menu.addChangeListener(e -> updateMenu());

      menu.add(m_ActionFileOpen);

      // File/Recent files
      submenu = new JMenu("Open recent");
      menu.add(submenu);
      m_RecentFilesHandler = new RecentFilesHandlerWithCommandline<>(
	SESSION_FILE, getProperties().getInteger("MaxRecentFiles", 5), submenu);
      m_RecentFilesHandler.addRecentItemListener(new RecentItemListener<JMenu,Setup>() {
	@Override
	public void recentItemAdded(RecentItemEvent<JMenu,Setup> e) {
	  // ignored
	}
	@Override
	public void recentItemSelected(RecentItemEvent<JMenu,Setup> e) {
	  load((SpreadSheetReader) e.getItem().getHandler(), e.getItem().getFile());
	  updateMenu();
	}
      });
      m_MenuFileOpenRecent = submenu;

      menu.add(m_ActionFileSave);
      menu.add(m_ActionFileSaveAs);
      menu.add(m_ActionFileClosePage);
      menu.add(m_ActionFileGarbageCollectionOnClose.getMenuItem());

      // File/Send to
      menu.addSeparator();
      if (SendToActionUtils.addSendToSubmenu(this, menu))
	menu.addSeparator();

      menu.add(m_ActionFileExit);

      // Edit
      menu = new JMenu("Edit");
      result.add(menu);
      menu.setMnemonic('E');
      menu.addChangeListener(e -> updateMenu());

      menu.add(m_ActionEditClearClipboard);
      menu.add(m_ActionEditPasteFromClipboard);

      // Data
      menu = new JMenu("Data");
      result.add(menu);
      menu.setMnemonic('D');
      menu.addChangeListener(e -> updateMenu());

      menu.add(m_ActionDataFilterColumns);
      menu.add(m_ActionDataFilterRows);
      menu.add(m_ActionDataConvert);
      menu.add(m_ActionDataTransform);
      menu.add(m_ActionDataSort);
      menu.add(m_ActionDataChart);
      menu.add(m_ActionDataComputeDifference);

      // Data/Plugin
      classes = AbstractDataPlugin.getPlugins();
      if (classes.length > 0) {
	menu.addSeparator();
	m_MenuItemDataPlugins = new ArrayList<>();
	m_DataPlugins         = new ArrayList<>();
	for (String cls: classes) {
	  try {
	    final AbstractDataPlugin data = (AbstractDataPlugin) ClassManager.getSingleton().forName(cls).getDeclaredConstructor().newInstance();
	    m_DataPlugins.add(data);
	    if (data.getMenuIcon() == null)
	      menuitem = new JMenuItem(data.getMenuText(), ImageManager.getEmptyIcon());
	    else
	      menuitem = new JMenuItem(data.getMenuText(), ImageManager.getIcon(data.getMenuIcon()));
	    menuitem.addActionListener(e -> process(data));
	    m_MenuItemDataPlugins.add(menuitem);
	    menu.add(menuitem);
	  }
	  catch (Exception e) {
	    System.err.println("Failed to generate menu item for data plugin: " + cls);
	    e.printStackTrace();
	  }
	}
      }

      // View
      menu = new JMenu("View");
      result.add(menu);
      menu.setMnemonic('V');
      menu.addChangeListener(e -> updateMenu());

      menu.add(m_ActionViewApplyToAll.getMenuItem());
      menu.add(m_ActionViewDisplayedDecimals);
      menu.add(m_ActionViewCellRenderingCustomizer);
      menu.add(m_ActionViewShowFormulas.getMenuItem());
      menu.add(m_ActionViewShowCellTypes.getMenuItem());

      // View/Tabs
      m_ViewerTabs.addTabsSubmenu(menu);

      // View/Plugins
      classes = AbstractViewPlugin.getPlugins();
      if (classes.length > 0) {
	menu.addSeparator();
	m_MenuItemViewPlugins = new ArrayList<>();
	m_ViewPlugins         = new ArrayList<>();
	for (String cls: classes) {
	  try {
	    final AbstractViewPlugin view = (AbstractViewPlugin) ClassManager.getSingleton().forName(cls).getDeclaredConstructor().newInstance();
	    m_ViewPlugins.add(view);
	    if (view.getMenuIcon() == null)
	      menuitem = new JMenuItem(view.getMenuText(), ImageManager.getEmptyIcon());
	    else
	      menuitem = new JMenuItem(view.getMenuText(), ImageManager.getIcon(view.getMenuIcon()));
	    menuitem.addActionListener(e -> view(view));
	    m_MenuItemViewPlugins.add(menuitem);
	    menu.add(menuitem);
	  }
	  catch (Exception e) {
	    System.err.println("Failed to generate menu item for view plugin: " + cls);
	    e.printStackTrace();
	  }
	}
      }

      // Window
      menu = new JMenu("Window");
      menu.setMnemonic('W');
      menu.addChangeListener((ChangeEvent e) -> updateMenu());
      result.add(menu);

      // Window/New window
      menuitem = new JMenuItem("New window");
      menu.add(menuitem);
      menuitem.setMnemonic('N');
      menuitem.addActionListener((ActionEvent e) -> newWindow());

      menu.addSeparator();

      // Window/Half width
      menuitem = new JMenuItem("Half width");
      menu.add(menuitem);
      menuitem.setMnemonic('i');
      menuitem.addActionListener((ActionEvent e) -> GUIHelper.makeHalfScreenWidth(SpreadSheetViewerPanel.this));

      // Window/Half height
      menuitem = new JMenuItem("Half height");
      menu.add(menuitem);
      menuitem.setMnemonic('g');
      menuitem.addActionListener((ActionEvent e) -> GUIHelper.makeHalfScreenHeight(SpreadSheetViewerPanel.this));

      // Help
      menu = new JMenu("Help");
      result.add(menu);
      menu.setMnemonic('H');
      menu.addChangeListener(e -> updateMenu());

      menu.add(m_ActionHelpFormulas);
      menu.add(m_MenuItemHelpQuery);

      // update menu
      m_MenuBar = result;
      updateMenu();
    }
    else {
      result = m_MenuBar;
    }

    return result;
  }

  /**
   * Returns the spreadsheet tabbed pane.
   *
   * @return		the tabbed pane
   */
  public MultiPagePane getMultiPagePane() {
    return m_MultiPagePane;
  }

  /**
   * Returns the viewer tabs.
   *
   * @return		the tabs
   */
  public ViewerTabManager getViewerTabs() {
    return m_ViewerTabs;
  }

  /**
   * Alows the user to enter the number of decimals to display.
   *
   * @param applyAll	whether to apply the setting to all open tabs
   */
  public void enterNumDecimals(boolean applyAll) {
    String 	valueStr;
    int 	decimals;

    if (applyAll)
      decimals = -1;
    else
      decimals = m_MultiPagePane.getNumDecimalsAt(m_MultiPagePane.getSelectedIndex());
    valueStr = GUIHelper.showInputDialog(
      this, "Please enter the number of decimals to display (-1 to display all):", "" + decimals);
    if (valueStr == null)
      return;

    decimals = Integer.parseInt(valueStr);
    if (applyAll)
      m_MultiPagePane.setNumDecimals(decimals);
    else
      m_MultiPagePane.setNumDecimalsAt(m_MultiPagePane.getSelectedIndex(), decimals);
  }

  /**
   * Allows the user to select a different cell rendering customizer.
   *
   * @param applyAll	whether to apply background to all open tabs
   */
  public void selectRendering(boolean applyAll) {
    CellRenderingCustomizer 	renderer;
    GenericObjectEditorDialog 	dialog;

    if (applyAll)
      renderer = m_MultiPagePane.getCellRenderingCustomizer();
    else
      renderer = m_MultiPagePane.getCellRenderingCustomizerAt(m_MultiPagePane.getSelectedIndex());

    if (getParentDialog() != null)
      dialog = new GenericObjectEditorDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new GenericObjectEditorDialog(getParentFrame(), true);
    dialog.setTitle("Cell rendering customizer");
    dialog.getGOEEditor().setCanChangeClassInDialog(true);
    dialog.getGOEEditor().setClassType(CellRenderingCustomizer.class);
    dialog.setCurrent(renderer);
    dialog.setLocationRelativeTo(dialog.getParent());
    dialog.setVisible(true);
    if (dialog.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
      return;

    renderer = (CellRenderingCustomizer) dialog.getCurrent();
    if (applyAll)
      m_MultiPagePane.setCellRenderingCustomizer(renderer);
    else
      m_MultiPagePane.setCellRenderingCustomizerAt(m_MultiPagePane.getSelectedIndex(), renderer);
  }

  /**
   * Updates the enabled state of the actions.
   */
  @Override
  public void updateActions() {
    for (SpreadSheetViewerAction action: m_Actions)
      action.update(this);
  }

  /**
   * updates the enabled state of the menu items.
   */
  public void updateMenu() {
    boolean		sheetSelected;
    SpreadSheetPanel	panel;
    int			i;

    if (m_MenuBar == null)
      return;

    sheetSelected = (m_MultiPagePane.getPageCount() > 0) && (m_MultiPagePane.getSelectedIndex() != -1);
    panel         = m_MultiPagePane.getCurrentPanel();

    updateActions();

    // Data
    if (m_MenuItemDataPlugins != null) {
      for (i = 0; i < m_DataPlugins.size(); i++) {
	m_MenuItemDataPlugins.get(i).setEnabled(
	  sheetSelected && m_DataPlugins.get(i).canProcess(panel));
      }
    }

    // View
    if (m_MenuItemViewPlugins != null) {
      for (i = 0; i < m_ViewPlugins.size(); i++) {
	m_MenuItemViewPlugins.get(i).setEnabled(
	  sheetSelected && m_ViewPlugins.get(i).canView(panel));
      }
    }
  }

  /**
   * Returns whether we can proceed with the operation or not, depending on
   * whether the user saved the sheet or discarded the changes.
   *
   * @return		true if safe to proceed
   */
  public boolean checkForModified() {
    if (getCurrentPanel() == null)
      return true;
    else
      return checkForModified(getCurrentPanel());
  }

  /**
   * Returns whether we can proceed with the operation or not, depending on
   * whether the user saved the sheet or discarded the changes.
   *
   * @param panel 	the panel to check
   * @return		true if safe to proceed
   */
  public boolean checkForModified(SpreadSheetPanel panel) {
    boolean 	result;
    int		retVal;
    String	msg;

    if (panel == null)
      return true;

    result = !panel.isModified();

    if (!result) {
      if (panel.getFilename() == null)
	msg = "Spreadsheet not saved - save?";
      else
	msg = "Spreadsheet not saved - save?\n" + panel.getFilename();
      retVal = GUIHelper.showConfirmMessage(this, msg, "Spreadsheet not saved");
      switch (retVal) {
	case GUIHelper.APPROVE_OPTION:
	  if (panel.getFilename() != null)
	    save();
	  else
	    saveAs();
	  result = !panel.isModified();
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
   * Opens one or more CSV files.
   */
  public void open() {
    int			retVal;
    PlaceholderFile[]	files;

    retVal = m_FileChooser.showOpenDialog(this);
    if (retVal != SpreadSheetFileChooser.APPROVE_OPTION)
      return;

    files = m_FileChooser.getSelectedPlaceholderFiles();
    for (File file: files)
      load(m_FileChooser.getReader(), file);
  }

  /**
   * Loads the specified file.
   *
   * @param file	the file to load
   */
  public void load(File file) {
    load(m_FileChooser.getReaderForFile(file), file);
  }

  /**
   * Loads the specified file.
   *
   * @param reader	the reader to use for reading the file
   * @param file	the file to load
   */
  public void load(final SpreadSheetReader reader, File file) {
    SwingWorker 	worker;

    worker = new SwingWorker() {
      @Override
      protected Object doInBackground() throws Exception {
	MouseUtils.setWaitCursor(SpreadSheetViewerPanel.this);
	// default reader
	SpreadSheetReader sreader = reader;
	if (reader == null)
	  sreader = new CsvSpreadSheetReader();

	SpreadSheet sheet = null;
	List<SpreadSheet> sheets = null;
	if (sreader instanceof MultiSheetSpreadSheetReader)
	  sheets = ((MultiSheetSpreadSheetReader) sreader).readRange(file.getAbsolutePath());
	else
	  sheet = sreader.read(file.getAbsolutePath());

	String msg;
	if ((sheet == null) && (sheets == null)) {
	  if (sreader.hasLastError())
	    msg = "Error loading spreadsheet file:\n" + file + "\n" + sreader.getLastError();
	  else
	    msg = "Error loading spreadsheet file:\n" + file;
	  GUIHelper.showErrorMessage(SpreadSheetViewerPanel.this, msg);
	}
	else {
	  SpreadSheetPanel panel;
	  boolean runGC = SpreadSheetViewerPanel.getProperties().getBoolean("GarbageCollectionOnClose", true);
	  if (sheet != null) {
	    panel = m_MultiPagePane.addPage(file, sheet);
	    panel.setReader(sreader);
	    panel.setGarbageCollectionOnClose(runGC);
	  }
	  else {
	    for (SpreadSheet sh: sheets) {
	      panel = m_MultiPagePane.addPage(file, sh);
	      panel.setReader(sreader);
	      panel.setGarbageCollectionOnClose(runGC);
	    }
	  }
	  m_FileChooser.setCurrentDirectory(file.getParentFile().getAbsoluteFile());
	  if (m_RecentFilesHandler != null)
	    m_RecentFilesHandler.addRecentItem(new Setup(file, sreader));
	}
	return null;
      }

      @Override
      protected void done() {
	super.done();
	MouseUtils.setDefaultCursor(SpreadSheetViewerPanel.this);
	updateMenu();
      }
    };
    worker.execute();
  }

  /**
   * Saves the specified file.
   *
   * @param writer	the writer to use for saving the file
   * @param file	the file to save
   */
  public void write(SpreadSheetWriter writer, File file) {
    SpreadSheetTable	table;
    SpreadSheetPanel	panel;
    int			index;
    SpreadSheet		sheet;

    index = m_MultiPagePane.getSelectedIndex();
    if (index == -1)
      return;
    panel = m_MultiPagePane.getPanelAt(index);
    if (panel == null)
      return;
    table = panel.getTable();
    if (table == null)
      return;

    sheet = table.toSpreadSheet();
    writer.reset();
    if (!writer.write(sheet, file)) {
      GUIHelper.showErrorMessage(this, "Failed to write spreadsheet to '" + file + "'!");
    }
    else {
      panel.setFilename(file);
      panel.setWriter(writer);
      panel.setModified(false);
      m_MultiPagePane.setTitleAt(index, m_MultiPagePane.createPageTitle(file, sheet));
      updateMenu();
    }
  }

  /**
   * Saves the current sheet (uses SwingWorker).
   *
   * @see #save(SpreadSheetWriter, File, boolean)
   */
  public void save() {
    SpreadSheetPanel	panel;

    panel = getCurrentPanel();
    if (panel == null)
      return;

    if (panel.getFilename() == null) {
      saveAs();
      return;
    }
    if (panel.getReader() == null) {
      saveAs();
      return;
    }
    if (panel.getWriter() == null) {
      if (panel.getReader().getCorrespondingWriter() == null) {
	saveAs();
	return;
      }
      save(panel.getReader().getCorrespondingWriter(), panel.getFilename(), false);
    }
    else {
      save(panel.getWriter(), getCurrentPanel().getFilename(), false);
    }
  }

  /**
   * Saves the current sheet under a new name (uses SwingWorker).
   *
   * @see #save(SpreadSheetWriter, File, boolean)
   */
  public void saveAs() {
    int			retVal;

    retVal = m_FileChooser.showSaveDialog(this);
    if (retVal != SpreadSheetFileChooser.APPROVE_OPTION)
      return;

    save(m_FileChooser.getWriter(), m_FileChooser.getSelectedPlaceholderFile(), true);
  }

  /**
   * Writes the file to disk (uses SwingWorker).
   *
   * @param writer	the writer to use for writing the spreadsheet
   * @param file	the file to write to
   * @param recent	whether to add the file to the recent files list
   */
  protected void save(final SpreadSheetWriter writer, final File file, final boolean recent) {
    SwingWorker		worker;

    worker = new SwingWorker() {
      @Override
      protected Object doInBackground() throws Exception {
	MouseUtils.setWaitCursor(SpreadSheetViewerPanel.this);
	write(writer, file);
	return null;
      }

      @Override
      protected void done() {
	super.done();
	MouseUtils.setDefaultCursor(SpreadSheetViewerPanel.this);
	if (recent && (m_RecentFilesHandler != null) && (m_FileChooser.getWriter().getCorrespondingReader() != null))
	  m_RecentFilesHandler.addRecentItem(new Setup(file, m_FileChooser.getWriter().getCorrespondingReader()));
      }
    };
    worker.execute();
  }

  /**
   * Closes the current active tab.
   */
  public void closeFile() {
    int		index;

    index = m_MultiPagePane.getSelectedIndex();
    if (index == -1)
      return;
    if (!checkForModified())
      return;

    m_MultiPagePane.removePageAt(index);
  }

  /**
   * Closes the dialog or frame.
   */
  public void close() {
    if (!checkForModified()) {
      setVisibleAgain();
      return;
    }

    if (getParentFrame() != null)
      ((JFrame) getParentFrame()).setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    closeParent();
  }

  /**
   * Returns all the image panels.
   *
   * @return		the image panels
   */
  public SpreadSheetPanel[] getAllPanels() {
    return m_MultiPagePane.getAllPanels();
  }

  /**
   * Returns the currently selected panel.
   *
   * @return		the current panel, null if not available
   */
  public SpreadSheetPanel getCurrentPanel() {
    return m_MultiPagePane.getCurrentPanel();
  }

  /**
   * Returns the classes that the supporter generates.
   *
   * @return		the classes
   */
  @Override
  public Class[] getSendToClasses() {
    return new Class[]{PlaceholderFile.class, JTable.class};
  }

  /**
   * Checks whether something to send is available.
   *
   * @param cls		the classes to retrieve the item for
   * @return		true if an object is available for sending
   */
  @Override
  public boolean hasSendToItem(Class[] cls) {
    return    (SendToActionUtils.isAvailable(new Class[]{PlaceholderFile.class, JTable.class}, cls))
      && (m_MultiPagePane.getSelectedIndex() != -1);
  }

  /**
   * Returns the object to send.
   *
   * @param cls		the classes to retrieve the item for
   * @return		the item to send
   */
  @Override
  public Object getSendToItem(Class[] cls) {
    Object			result;
    CsvSpreadSheetWriter	writer;
    SpreadSheet			sheet;
    int				index;

    index = m_MultiPagePane.getSelectedIndex();
    if (index == -1)
      return null;

    result = null;

    if (SendToActionUtils.isAvailable(PlaceholderFile.class, cls)) {
      sheet  = m_MultiPagePane.getTableAt(index).toSpreadSheet();
      result = SendToActionUtils.nextTmpFile("spreadsheetviewer", "csv");
      writer = new CsvSpreadSheetWriter();
      if (!writer.write(sheet, (PlaceholderFile) result))
	result = null;
    }
    else if (SendToActionUtils.isAvailable(JTable.class, cls)) {
      result = m_MultiPagePane.getTableAt(index);
    }

    return result;
  }

  /**
   * Filters the data with the transformer and adds the generated
   * output as new tab.
   *
   * @param oldTitle	the title from the old tab
   * @param input	the spreadsheet to process
   * @param filter	the transformer
   */
  public void filterData(String oldTitle, Object input, Actor filter) {
    SwingWorker		worker;

    worker = new SwingWorker() {
      @Override
      protected Object doInBackground() throws Exception {
	MouseUtils.setWaitCursor(SpreadSheetViewerPanel.this);
	try {
	  List processed = ActorUtils.transform(filter, input);
	  for (Object obj: processed) {
	    if (!(obj instanceof SpreadSheet)) {
	      GUIHelper.showErrorMessage(SpreadSheetViewerPanel.this, "Generated non-spreadsheet object??\n" + obj.getClass().getName());
	      return null;
	    }
	    m_MultiPagePane.addPage(oldTitle + "'", (SpreadSheet) obj);
	  }
	}
	catch (Exception e) {
	  GUIHelper.showErrorMessage(SpreadSheetViewerPanel.this, "Failed to filter data:\n" + LoggingHelper.throwableToString(e));
	}
	return null;
      }

      @Override
      protected void done() {
	super.done();
	MouseUtils.setDefaultCursor(SpreadSheetViewerPanel.this);
	updateMenu();
      }
    };
    worker.execute();
  }

  /**
   * Processes the current spreadsheet with the specified plugin.
   *
   * @param plugin	the plugin to use
   */
  protected void process(AbstractDataPlugin plugin) {
    SwingWorker		worker;

    worker = new SwingWorker() {
      @Override
      protected Object doInBackground() throws Exception {
	MouseUtils.setWaitCursor(SpreadSheetViewerPanel.this);
	SpreadSheetPanel panel = m_MultiPagePane.getCurrentPanel();
	if (panel == null)
	  return null;
	SpreadSheet input = panel.getSheet();
	if (input == null)
	  return null;
	plugin.setCurrentPanel(panel);
	SpreadSheet output = plugin.process(input);
	if (plugin.getCanceledByUser() || (output == null))
	  return null;
	if (plugin.isInPlace())
	  m_MultiPagePane.getCurrentTable().setModel(new SpreadSheetTableModel(output));
	else
	  m_MultiPagePane.addPage(m_MultiPagePane.newTitle(), output);
	plugin.setCurrentPanel(null);
	return null;
      }

      @Override
      protected void done() {
	super.done();
	MouseUtils.setDefaultCursor(SpreadSheetViewerPanel.this);
	updateMenu();
      }
    };
    worker.execute();
  }

  /**
   * Displays a dialog with the panel created by the plugin.
   *
   * @param plugin	for generating the view
   */
  protected void view(final AbstractViewPlugin plugin) {
    BasePanel		panel;
    SpreadSheet		sheet;
    ApprovalDialog	dialog;
    String		title;
    SpreadSheetPanel	current;

    current = m_MultiPagePane.getCurrentPanel();
    if ((current == null) || (current.getSheet() == null))
      return;
    sheet = current.getSheet();

    plugin.setCurrentPanel(current);
    panel = plugin.generate(sheet);
    if (plugin.getCanceledByUser() || (panel == null)) {
      plugin.setCurrentPanel(null);
      return;
    }
    if (getParentDialog() != null)
      dialog = new ApprovalDialog(getParentDialog(), ModalityType.MODELESS);
    else
      dialog = new ApprovalDialog(getParentFrame(), false);
    title = plugin.getMenuText();
    if (current.getTabTitle() != null)
      title += " - " + current.getTabTitle();
    dialog.setTitle(title);
    if (plugin.getMenuIcon() != null)
      dialog.setIconImage(ImageManager.getIcon(plugin.getMenuIcon()).getImage());
    dialog.getContentPane().add(panel, BorderLayout.CENTER);
    dialog.setCancelVisible(plugin.requiresButtons());
    dialog.setApproveVisible(plugin.requiresButtons());
    dialog.pack();
    dialog.setLocationRelativeTo(this);
    dialog.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
	super.windowClosing(e);
	plugin.setCurrentPanel(null);
      }
    });
    dialog.setVisible(true);
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
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    if (m_Actions != null) {
      for (SpreadSheetViewerAction action: m_Actions)
	action.cleanUp();
      m_Actions = null;
    }
  }

  /**
   * Sets whether to apply settings to all tabs or just current one.
   *
   * @param value	true if to apply to all
   */
  public void setApplyToAll(boolean value) {
    m_ApplyToAll = value;
  }

  /**
   * Returns whether to apply settings to all tabs or just current one.
   *
   * @return		true if to apply to all
   */
  public boolean getApplyToAll() {
    return m_ApplyToAll;
  }

  /**
   * Displays a new preview window/frame.
   *
   * @return		the new panel
   */
  public SpreadSheetViewerPanel newWindow() {
    SpreadSheetViewerPanel 	result;
    ChildFrame 			oldFrame;
    ChildFrame 			newFrame;

    result   = null;
    oldFrame = (ChildFrame) GUIHelper.getParent(this, ChildFrame.class);
    if (oldFrame != null) {
      newFrame = oldFrame.getNewWindow();
      newFrame.setVisible(true);
      result   = (SpreadSheetViewerPanel) newFrame.getContentPane().getComponent(0);
    }

    return result;
  }

  /**
   * Returns the properties that define the editor.
   *
   * @return		the properties
   */
  public static synchronized Properties getProperties() {
    if (m_Properties == null) {
      try {
	m_Properties = Properties.read("adams/gui/tools/" + FILENAME);
      }
      catch (Exception e) {
	m_Properties = new Properties();
	System.err.println("Failed to load properties: " + FILENAME);
	e.printStackTrace();
      }
    }

    return m_Properties;
  }

  /**
   * Returns the properties that define the menu.
   *
   * @return		the properties
   */
  public static synchronized Properties getPropertiesMenu() {
    if (m_PropertiesMenu == null) {
      try {
	m_PropertiesMenu = Properties.read("adams/gui/tools/" + FILENAME_MENU);
      }
      catch (Exception e) {
	m_PropertiesMenu = new Properties();
	System.err.println("Failed to load properties: " + FILENAME_MENU);
	e.printStackTrace();
      }
    }

    return m_PropertiesMenu;
  }
}
