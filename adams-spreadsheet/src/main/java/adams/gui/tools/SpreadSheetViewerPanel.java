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
 * SpreadSheetViewerPanel.java
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import adams.core.CleanUpHandler;
import adams.core.Properties;
import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.data.conversion.AbstractSpreadSheetConversion;
import adams.data.conversion.Conversion;
import adams.data.conversion.TransposeSpreadSheet;
import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.io.input.MultiSheetSpreadSheetReader;
import adams.data.io.input.SpreadSheetReader;
import adams.data.io.output.CsvSpreadSheetWriter;
import adams.data.io.output.SpreadSheetWriter;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.data.spreadsheet.columnfinder.ByName;
import adams.data.spreadsheet.columnfinder.ColumnFinder;
import adams.data.spreadsheet.rowfinder.ByValue;
import adams.data.spreadsheet.rowfinder.RowFinder;
import adams.flow.core.AbstractActor;
import adams.flow.core.ActorUtils;
import adams.flow.transformer.AbstractSpreadSheetTransformer;
import adams.flow.transformer.AbstractTransformer;
import adams.flow.transformer.Convert;
import adams.flow.transformer.SpreadSheetColumnFilter;
import adams.flow.transformer.SpreadSheetDifference;
import adams.flow.transformer.SpreadSheetRowFilter;
import adams.flow.transformer.SpreadSheetSubset;
import adams.gui.chooser.SpreadSheetFileChooser;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.GUIHelper;
import adams.gui.core.MenuBarProvider;
import adams.gui.core.ParameterPanel;
import adams.gui.core.RecentFilesHandlerWithCommandline;
import adams.gui.core.RecentFilesHandlerWithCommandline.Setup;
import adams.gui.core.SpreadSheetTable;
import adams.gui.core.SpreadSheetTableModel;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.dialog.TextDialog;
import adams.gui.event.RecentItemEvent;
import adams.gui.event.RecentItemListener;
import adams.gui.event.SortSetupEvent;
import adams.gui.event.SortSetupListener;
import adams.gui.event.TabVisibilityChangeEvent;
import adams.gui.event.TabVisibilityChangeListener;
import adams.gui.goe.GenericObjectEditorDialog;
import adams.gui.sendto.SendToActionSupporter;
import adams.gui.sendto.SendToActionUtils;
import adams.gui.tools.spreadsheetviewer.AbstractDataPlugin;
import adams.gui.tools.spreadsheetviewer.AbstractViewPlugin;
import adams.gui.tools.spreadsheetviewer.SortPanel;
import adams.gui.tools.spreadsheetviewer.SpreadSheetPanel;
import adams.gui.tools.spreadsheetviewer.TabbedPane;
import adams.gui.tools.spreadsheetviewer.chart.AbstractChartGenerator;
import adams.gui.tools.spreadsheetviewer.chart.ScatterPlot;
import adams.gui.tools.spreadsheetviewer.menu.DataChart;
import adams.gui.tools.spreadsheetviewer.menu.DataComputeDifference;
import adams.gui.tools.spreadsheetviewer.menu.DataConvert;
import adams.gui.tools.spreadsheetviewer.menu.DataFilterColumns;
import adams.gui.tools.spreadsheetviewer.menu.DataFilterRows;
import adams.gui.tools.spreadsheetviewer.menu.DataSort;
import adams.gui.tools.spreadsheetviewer.menu.DataTransform;
import adams.gui.tools.spreadsheetviewer.menu.FileCloseTab;
import adams.gui.tools.spreadsheetviewer.menu.FileExit;
import adams.gui.tools.spreadsheetviewer.menu.FileOpen;
import adams.gui.tools.spreadsheetviewer.menu.FileSaveAs;
import adams.gui.tools.spreadsheetviewer.menu.HelpFormulas;
import adams.gui.tools.spreadsheetviewer.menu.HelpQuery;
import adams.gui.tools.spreadsheetviewer.menu.SpreadSheetViewerAction;
import adams.gui.tools.spreadsheetviewer.menu.ViewApplyToAll;
import adams.gui.tools.spreadsheetviewer.menu.ViewDecimals;
import adams.gui.tools.spreadsheetviewer.menu.ViewNegativeBackground;
import adams.gui.tools.spreadsheetviewer.menu.ViewPositiveBackground;
import adams.gui.tools.spreadsheetviewer.menu.ViewShowFormulas;
import adams.gui.tools.spreadsheetviewer.tab.ViewerTabManager;

/**
 * A panel for viewing SpreadSheet files.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetViewerPanel
  extends BasePanel
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
  
  /** the tabbed pane for displaying the CSV files. */
  protected TabbedPane m_TabbedPane;
  
  /** the viewer tabs. */
  protected ViewerTabManager m_ViewerTabs;

  /** the menu bar, if used. */
  protected JMenuBar m_MenuBar;

  /** the "open" menu item. */
  protected SpreadSheetViewerAction m_MenuItemFileOpen;

  /** the "load recent" submenu. */
  protected JMenu m_MenuItemFileOpenRecent;

  /** the "save as" menu item. */
  protected SpreadSheetViewerAction m_MenuItemFileSaveAs;

  /** the "close" menu item. */
  protected SpreadSheetViewerAction m_MenuItemFileClose;

  /** the "exit" menu item. */
  protected SpreadSheetViewerAction m_MenuItemFileExit;

  /** the "filter columns" menu item. */
  protected SpreadSheetViewerAction m_MenuItemDataFilterColumns;

  /** the "filter rows" menu item. */
  protected SpreadSheetViewerAction m_MenuItemDataFilterRows;

  /** the "compute difference" menu item. */
  protected SpreadSheetViewerAction m_MenuItemDataComputeDifference;

  /** the "Convert" menu item. */
  protected SpreadSheetViewerAction m_MenuItemDataConvert;

  /** the "Transform" menu item. */
  protected SpreadSheetViewerAction m_MenuItemDataTransform;

  /** the "Sort" menu item. */
  protected SpreadSheetViewerAction m_MenuItemDataSort;

  /** the "Chart" menu item. */
  protected SpreadSheetViewerAction m_MenuItemDataChart;

  /** the "apply to all" menu item. */
  protected SpreadSheetViewerAction m_MenuItemViewApplyToAll;

  /** the "displayed decimals" menu item. */
  protected SpreadSheetViewerAction m_MenuItemViewDisplayedDecimals;

  /** the "negative background" menu item. */
  protected SpreadSheetViewerAction m_MenuItemViewNegativeBackground;

  /** the "positive background" menu item. */
  protected SpreadSheetViewerAction m_MenuItemViewPositiveBackground;

  /** the "show formulas" menu item. */
  protected SpreadSheetViewerAction m_MenuItemViewShowFormulas;

  /** the "formulas" help menu item. */
  protected SpreadSheetViewerAction m_MenuItemHelpFormulas;

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

  /** the dialog for column finders. */
  protected GenericObjectEditorDialog m_GOEColumnFinder;

  /** the dialog for row finders. */
  protected GenericObjectEditorDialog m_GOERowFinder;

  /** the dialog for spreadsheet conversions. */
  protected GenericObjectEditorDialog m_GOEConversion;

  /** the dialog for spreadsheet transformers. */
  protected GenericObjectEditorDialog m_GOETransformer;

  /** the dialog for spreadsheet chart generators. */
  protected GenericObjectEditorDialog m_GOEChart;
  
  /** the sort panel. */
  protected SortPanel m_SortPanel;

  /** whether to apply settings to all tabs or just current one. */
  protected boolean m_ApplyToAll;

  /** menu items. */
  protected List<SpreadSheetViewerAction> m_MenuItems;

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
    m_MenuItems          = new ArrayList<SpreadSheetViewerAction>();
    
    // TODO make it derived from ToolBarPane
    initActions();
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_SplitPane = new BaseSplitPane(BaseSplitPane.HORIZONTAL_SPLIT, true);
    m_SplitPane.setOneTouchExpandable(true);
    m_SplitPane.setResizeWeight(1.0);
    add(m_SplitPane, BorderLayout.CENTER);
    
    m_TabbedPane = new TabbedPane(this);
    m_TabbedPane.setCloseTabsWithMiddelMouseButton(true);
    m_SplitPane.setLeftComponent(m_TabbedPane);
    
    m_ViewerTabs = new ViewerTabManager(this);
    m_ViewerTabs.addTabVisibilityChangeListener(new TabVisibilityChangeListener() {
      @Override
      public void tabVisibilityChanged(TabVisibilityChangeEvent e) {
	m_SplitPane.setRightComponentHidden(m_ViewerTabs.getTabCount() == 0);
      }
    });
    m_SplitPane.setRightComponent(m_ViewerTabs);
    m_SplitPane.setRightComponentHidden(m_ViewerTabs.getTabCount() == 0);
    m_SplitPane.setDividerLocation(0.8);
  }

  /**
   * Initializes the actions.
   */
  protected void initActions() {
    SpreadSheetViewerAction	action;

    // File/Open
    action = new FileOpen();
    m_MenuItemFileOpen = action;
    m_MenuItems.add(action);

    // File/Save as
    action = new FileSaveAs();
    m_MenuItemFileSaveAs = action;
    m_MenuItems.add(action);

    // File/Close tab
    action = new FileCloseTab();
    m_MenuItemFileClose = action;
    m_MenuItems.add(action);

    // File/Exit
    action = new FileExit();
    m_MenuItemFileExit = action;
    m_MenuItems.add(action);

    // Data/Filter columns
    action = new DataFilterColumns();
    m_MenuItemDataFilterColumns = action;
    m_MenuItems.add(action);

    // Data/Filter rows
    action = new DataFilterRows();
    m_MenuItemDataFilterRows = action;
    m_MenuItems.add(action);

    // Data/Convert
    action = new DataConvert();
    m_MenuItemDataConvert = action;
    m_MenuItems.add(action);

    // Data/Transform
    action = new DataTransform();
    m_MenuItemDataTransform = action;
    m_MenuItems.add(action);

    // Data/Sort
    action = new DataSort();
    m_MenuItemDataSort = action;
    m_MenuItems.add(action);

    // Data/Chart
    action = new DataChart();
    m_MenuItemDataChart = action;
    m_MenuItems.add(action);

    // Data/Compute difference
    action = new DataComputeDifference();
    m_MenuItemDataComputeDifference = action;
    m_MenuItems.add(action);

    // View/Apply to all
    action = new ViewApplyToAll();
    m_MenuItemViewApplyToAll = action;
    m_MenuItems.add(action);

    // View/Decimals
    action = new ViewDecimals();
    m_MenuItemViewDisplayedDecimals = action;
    m_MenuItems.add(action);

    // View/Negative background
    action = new ViewNegativeBackground();
    m_MenuItemViewNegativeBackground = action;
    m_MenuItems.add(action);

    // View/Positive background
    action = new ViewPositiveBackground();
    m_MenuItemViewPositiveBackground = action;
    m_MenuItems.add(action);

    // View/Show formulas
    action = new ViewShowFormulas();
    m_MenuItemViewShowFormulas = action;
    m_MenuItems.add(action);

    // Help/Formulas
    action = new HelpFormulas();
    m_MenuItemHelpFormulas = action;
    m_MenuItems.add(action);

    // Help/Query
    action = new HelpQuery();
    m_MenuItemHelpQuery = action;
    m_MenuItems.add(action);
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
      result = new JMenuBar();

      // File
      menu = new JMenu("File");
      result.add(menu);
      menu.setMnemonic('F');
      menu.addChangeListener(new ChangeListener() {
	@Override
	public void stateChanged(ChangeEvent e) {
	  updateMenu();
	}
      });

      menu.add(m_MenuItemFileOpen);
      
      // File/Recent files
      submenu = new JMenu("Open recent");
      menu.add(submenu);
      m_RecentFilesHandler = new RecentFilesHandlerWithCommandline<JMenu>(
	  SESSION_FILE, getProperties().getInteger("MaxRecentFiles", 5), submenu);
      m_RecentFilesHandler.addRecentItemListener(new RecentItemListener<JMenu,Setup>() {
	@Override
	public void recentItemAdded(RecentItemEvent<JMenu,Setup> e) {
	  // ignored
	}
	@Override
	public void recentItemSelected(RecentItemEvent<JMenu,Setup> e) {
	  load((SpreadSheetReader) e.getItem().getHandler(), e.getItem().getFile());
	}
      });
      m_MenuItemFileOpenRecent = submenu;

      menu.add(m_MenuItemFileSaveAs);
      menu.add(m_MenuItemFileClose);

      // File/Send to
      menu.addSeparator();
      if (SendToActionUtils.addSendToSubmenu(this, menu))
	menu.addSeparator();

      menu.add(m_MenuItemFileExit);

      // Data
      menu = new JMenu("Data");
      result.add(menu);
      menu.setMnemonic('D');
      menu.addChangeListener(new ChangeListener() {
	@Override
	public void stateChanged(ChangeEvent e) {
	  updateMenu();
	}
      });

      menu.add(m_MenuItemDataFilterColumns);
      menu.add(m_MenuItemDataFilterRows);
      menu.add(m_MenuItemDataConvert);
      menu.add(m_MenuItemDataTransform);
      menu.add(m_MenuItemDataSort);
      menu.add(m_MenuItemDataChart);
      menu.add(m_MenuItemDataComputeDifference);

      // Data/Plugin
      classes = AbstractDataPlugin.getPlugins();
      if (classes.length > 0) {
	menu.addSeparator();
	m_MenuItemDataPlugins = new ArrayList<JMenuItem>();
	m_DataPlugins         = new ArrayList<AbstractDataPlugin>();
	for (String cls: classes) {
	  try {
	    final AbstractDataPlugin data = (AbstractDataPlugin) Class.forName(cls).newInstance();
	    m_DataPlugins.add(data);
	    if (data.getMenuIcon() == null)
	      menuitem = new JMenuItem(data.getMenuText(), GUIHelper.getEmptyIcon());
	    else
	      menuitem = new JMenuItem(data.getMenuText(), GUIHelper.getIcon(data.getMenuIcon()));
	    menuitem.addActionListener(new ActionListener() {
	      @Override
	      public void actionPerformed(ActionEvent e) {
		process(data);
	      }
	    });
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
      menu.addChangeListener(new ChangeListener() {
	@Override
	public void stateChanged(ChangeEvent e) {
	  updateMenu();
	}
      });

      menu.add(m_MenuItemViewApplyToAll.getMenuItem());
      menu.add(m_MenuItemViewDisplayedDecimals);
      menu.add(m_MenuItemViewNegativeBackground);
      menu.add(m_MenuItemViewPositiveBackground);
      menu.add(m_MenuItemViewShowFormulas.getMenuItem());

      // View/Tabs
      m_ViewerTabs.addTabsSubmenu(menu);
      
      // View/Plugins
      classes = AbstractViewPlugin.getPlugins();
      if (classes.length > 0) {
	menu.addSeparator();
	m_MenuItemViewPlugins = new ArrayList<JMenuItem>();
	m_ViewPlugins         = new ArrayList<AbstractViewPlugin>();
	for (String cls: classes) {
	  try {
	    final AbstractViewPlugin view = (AbstractViewPlugin) Class.forName(cls).newInstance();
	    m_ViewPlugins.add(view);
	    if (view.getMenuIcon() == null)
	      menuitem = new JMenuItem(view.getMenuText(), GUIHelper.getEmptyIcon());
	    else
	      menuitem = new JMenuItem(view.getMenuText(), GUIHelper.getIcon(view.getMenuIcon()));
	    menuitem.addActionListener(new ActionListener() {
	      @Override
	      public void actionPerformed(ActionEvent e) {
		view(view);
	      }
	    });
	    m_MenuItemViewPlugins.add(menuitem);
	    menu.add(menuitem);
	  }
	  catch (Exception e) {
	    System.err.println("Failed to generate menu item for view plugin: " + cls);
	    e.printStackTrace();
	  }
	}
      }

      // Help
      menu = new JMenu("Help");
      result.add(menu);
      menu.setMnemonic('H');
      menu.addChangeListener(new ChangeListener() {
	@Override
	public void stateChanged(ChangeEvent e) {
	  updateMenu();
	}
      });

      menu.add(m_MenuItemHelpFormulas);
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
  public TabbedPane getTabbedPane() {
    return m_TabbedPane;
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
   * Displays a help text in a dialog.
   * 
   * @param title	the title for the help
   * @param content	the text to display
   */
  public void showHelpText(String title, String content) {
    TextDialog 	dialog;
    
    if (getParentDialog() != null)
      dialog = new TextDialog(getParentDialog());
    else
      dialog = new TextDialog(getParentFrame());
    dialog.setDialogTitle(title);
    dialog.setContent(content);
    dialog.setEditable(false);
    dialog.setSize(800, 600);
    GUIHelper.setSizeAndLocation(dialog);
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
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
      decimals = m_TabbedPane.getNumDecimalsAt(m_TabbedPane.getSelectedIndex());
    valueStr = JOptionPane.showInputDialog(
	"Please enter the number of decimals to display (-1 to display all):", decimals);
    if (valueStr == null)
      return;

    decimals = Integer.parseInt(valueStr);
    if (applyAll)
      m_TabbedPane.setNumDecimals(decimals);
    else
      m_TabbedPane.setNumDecimalsAt(m_TabbedPane.getSelectedIndex(), decimals);
  }

  /**
   * Allows the user to select a background color for negative/positive values.
   *
   * @param negative	whether to select negative or positive background
   * @param applyAll	whether to apply background to all open tabs
   */
  public void selectBackground(boolean negative, boolean applyAll) {
    Color	color;

    if (applyAll)
      color = Color.WHITE;
    else {
      if (negative)
	color = m_TabbedPane.getNegativeBackgroundAt(m_TabbedPane.getSelectedIndex());
      else
	color = m_TabbedPane.getPositiveBackgroundAt(m_TabbedPane.getSelectedIndex());
    }

    if (negative)
      color = JColorChooser.showDialog(this, "Background for negative values", color);
    else
      color = JColorChooser.showDialog(this, "Background for positive values", color);
    if (color == null)
      return;

    if (negative) {
      if (applyAll)
	m_TabbedPane.setNegativeBackground(color);
      else
	m_TabbedPane.setNegativeBackgroundAt(m_TabbedPane.getSelectedIndex(), color);
    }
    else {
      if (applyAll)
	m_TabbedPane.setPositiveBackground(color);
      else
	m_TabbedPane.setPositiveBackgroundAt(m_TabbedPane.getSelectedIndex(), color);
    }
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

    sheetSelected = (m_TabbedPane.getTabCount() > 0) && (m_TabbedPane.getSelectedIndex() != -1);
    panel         = m_TabbedPane.getCurrentPanel();

    for (SpreadSheetViewerAction action: m_MenuItems)
      action.update(this);

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
  public void load(SpreadSheetReader reader, File file) {
    SpreadSheet		sheet;
    List<SpreadSheet>	sheets;
    String		msg;

    // default reader
    if (reader == null)
      reader = new CsvSpreadSheetReader();

    sheet  = null;
    sheets = null;
    if (reader instanceof MultiSheetSpreadSheetReader)
      sheets = ((MultiSheetSpreadSheetReader) reader).readRange(file.getAbsolutePath());
    else
      sheet = reader.read(file.getAbsolutePath());
    
    if ((sheet == null) && (sheets == null)) {
      if (reader.hasLastError())
	msg = "Error loading spreadsheet file:\n" + file + "\n" + reader.getLastError();
      else
	msg = "Error loading spreadsheet file:\n" + file;
      GUIHelper.showErrorMessage(this, msg);
    }
    else {
      if (sheet != null) {
	m_TabbedPane.addTab(file, sheet);
      }
      else {
	for (SpreadSheet sh: sheets)
	  m_TabbedPane.addTab(file, sh);
      }
      m_FileChooser.setCurrentDirectory(file.getParentFile());
      if (m_RecentFilesHandler != null)
	m_RecentFilesHandler.addRecentItem(new Setup(file, reader));
    }
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

    index = m_TabbedPane.getSelectedIndex();
    if (index == -1)
      return;
    panel = m_TabbedPane.getPanelAt(index);
    if (panel == null)
      return;
    table = panel.getTable();
    if (table == null)
      return;

    sheet = table.toSpreadSheet();
    if (!writer.write(sheet, file))
      GUIHelper.showErrorMessage(this, "Failed to write spreadsheet to '" + file + "'!");
    else
      m_TabbedPane.setTitleAt(index, m_TabbedPane.createTabTitle(file, sheet));
  }

  /**
   * Saves the current sheet.
   */
  public void saveAs() {
    int			retVal;
    PlaceholderFile	file;

    retVal = m_FileChooser.showSaveDialog(this);
    if (retVal != SpreadSheetFileChooser.APPROVE_OPTION)
      return;

    file = m_FileChooser.getSelectedPlaceholderFile();
    write(m_FileChooser.getWriter(), file);
    if ((m_RecentFilesHandler != null) && (m_FileChooser.getWriter().getCorrespondingReader() != null))
      m_RecentFilesHandler.addRecentItem(new Setup(file, m_FileChooser.getWriter().getCorrespondingReader()));
  }

  /**
   * Closes the current active tab.
   */
  public void closeFile() {
    int		index;

    index = m_TabbedPane.getSelectedIndex();
    if (index == -1)
      return;

    m_TabbedPane.remove(index);
  }

  /**
   * Closes the dialog or frame.
   */
  public void close() {
    closeParent();
  }

  /**
   * Returns all the image panels.
   *
   * @return		the image panels
   */
  public SpreadSheetPanel[] getAllPanels() {
    return m_TabbedPane.getAllPanels();
  }

  /**
   * Returns the currently selected panel.
   *
   * @return		the current panel, null if not available
   */
  public SpreadSheetPanel getCurrentPanel() {
    return m_TabbedPane.getCurrentPanel();
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
           && (m_TabbedPane.getSelectedIndex() != -1);
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

    index = m_TabbedPane.getSelectedIndex();
    if (index == -1)
      return null;

    result = null;

    if (SendToActionUtils.isAvailable(PlaceholderFile.class, cls)) {
      sheet  = m_TabbedPane.getTableAt(index).toSpreadSheet();
      result = SendToActionUtils.nextTmpFile("spreadsheetviewer", "csv");
      writer = new CsvSpreadSheetWriter();
      if (!writer.write(sheet, (PlaceholderFile) result))
	result = null;
    }
    else if (SendToActionUtils.isAvailable(JTable.class, cls)) {
      result = m_TabbedPane.getTableAt(index);
    }

    return result;
  }

  /**
   * Returns the dialog for column finders.
   *
   * @return		the dialog
   */
  protected GenericObjectEditorDialog getColumnFinderDialog() {
    if (m_GOEColumnFinder == null) {
      if (getParentDialog() != null)
	m_GOEColumnFinder = new GenericObjectEditorDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
      else
	m_GOEColumnFinder = new GenericObjectEditorDialog(getParentFrame(), true);
      m_GOEColumnFinder.setTitle("Column finder");
      m_GOEColumnFinder.getGOEEditor().setClassType(ColumnFinder.class);
      m_GOEColumnFinder.getGOEEditor().setCanChangeClassInDialog(true);
      m_GOEColumnFinder.getGOEEditor().setValue(new ByName());
      m_GOEColumnFinder.setLocationRelativeTo(this);
    }

    return m_GOEColumnFinder;
  }

  /**
   * Returns the dialog for row finders.
   *
   * @return		the dialog
   */
  protected GenericObjectEditorDialog getRowFinderDialog() {
    if (m_GOERowFinder == null) {
      if (getParentDialog() != null)
	m_GOERowFinder = new GenericObjectEditorDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
      else
	m_GOERowFinder = new GenericObjectEditorDialog(getParentFrame(), true);
      m_GOERowFinder.setTitle("Row finder");
      m_GOERowFinder.getGOEEditor().setClassType(RowFinder.class);
      m_GOERowFinder.getGOEEditor().setCanChangeClassInDialog(true);
      m_GOERowFinder.getGOEEditor().setValue(new ByValue());
      m_GOERowFinder.setLocationRelativeTo(this);
    }

    return m_GOERowFinder;
  }

  /**
   * Returns the dialog for conversion schemes.
   *
   * @return		the dialog
   */
  protected GenericObjectEditorDialog getConversionDialog() {
    if (m_GOEConversion == null) {
      if (getParentDialog() != null)
	m_GOEConversion = new GenericObjectEditorDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
      else
	m_GOEConversion = new GenericObjectEditorDialog(getParentFrame(), true);
      m_GOEConversion.setTitle("Conversion");
      m_GOEConversion.getGOEEditor().setClassType(AbstractSpreadSheetConversion.class);
      m_GOEConversion.getGOEEditor().setCanChangeClassInDialog(true);
      m_GOEConversion.getGOEEditor().setValue(new TransposeSpreadSheet());
      m_GOEConversion.setLocationRelativeTo(this);
    }

    return m_GOEConversion;
  }

  /**
   * Returns the dialog for transformers.
   *
   * @return		the dialog
   */
  protected GenericObjectEditorDialog getTransformerDialog() {
    if (m_GOETransformer == null) {
      if (getParentDialog() != null)
	m_GOETransformer = new GenericObjectEditorDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
      else
	m_GOETransformer = new GenericObjectEditorDialog(getParentFrame(), true);
      m_GOETransformer.setTitle("Transformer");
      m_GOETransformer.getGOEEditor().setClassType(AbstractSpreadSheetTransformer.class);
      m_GOETransformer.getGOEEditor().setCanChangeClassInDialog(true);
      m_GOETransformer.getGOEEditor().setValue(new SpreadSheetSubset());
      m_GOETransformer.setLocationRelativeTo(this);
    }

    return m_GOETransformer;
  }

  /**
   * Returns the dialog for chart generators.
   *
   * @return		the dialog
   */
  protected GenericObjectEditorDialog getChartGeneratorDialog() {
    if (m_GOEChart == null) {
      if (getParentDialog() != null)
	m_GOEChart = new GenericObjectEditorDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
      else
	m_GOEChart = new GenericObjectEditorDialog(getParentFrame(), true);
      m_GOEChart.setTitle("Chart");
      m_GOEChart.getGOEEditor().setClassType(AbstractChartGenerator.class);
      m_GOEChart.getGOEEditor().setCanChangeClassInDialog(true);
      m_GOEChart.getGOEEditor().setValue(new ScatterPlot());
      m_GOEChart.setLocationRelativeTo(this);
    }

    return m_GOEChart;
  }

  /**
   * Filters the data with the transformer and adds the generated
   * output as new tab.
   *
   * @param oldTitle	the title from the old tab
   * @param input	the spreadsheet to process
   * @param filter	the transformer
   */
  protected void filterData(String oldTitle, Object input, AbstractActor filter) {
    List	processed;

    try {
      processed = ActorUtils.transform(filter, input);
      for (Object obj: processed) {
	if (!(obj instanceof SpreadSheet)) {
	  GUIHelper.showErrorMessage(this, "Generated non-spreadsheet object??\n" + obj.getClass().getName());
	  return;
	}
	m_TabbedPane.addTab(oldTitle + "'", (SpreadSheet) obj);
      }
    }
    catch (Exception e) {
      GUIHelper.showErrorMessage(this, "Failed to filter data:\n" + Utils.throwableToString(e));
    }
  }

  /**
   * Filters the spreadsheet using a column finder.
   */
  public void findColumns() {
    SpreadSheet			sheet;
    ColumnFinder		finder;
    SpreadSheetColumnFilter	filter;

    sheet = m_TabbedPane.getCurrentSheet();
    if (sheet == null)
      return;

    getColumnFinderDialog().setVisible(true);
    if (getColumnFinderDialog().getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
      return;

    finder = (ColumnFinder) getColumnFinderDialog().getGOEEditor().getValue();
    filter = new SpreadSheetColumnFilter();
    filter.setFinder(finder);

    filterData(m_TabbedPane.getTitleAt(m_TabbedPane.getSelectedIndex()), sheet, filter);
  }

  /**
   * Filters the spreadsheet using a row finder.
   */
  public void findRows() {
    SpreadSheet			sheet;
    RowFinder			finder;
    SpreadSheetRowFilter	filter;

    sheet = m_TabbedPane.getCurrentSheet();
    if (sheet == null)
      return;

    getRowFinderDialog().setVisible(true);
    if (getRowFinderDialog().getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
      return;

    finder = (RowFinder) getRowFinderDialog().getGOEEditor().getValue();
    filter = new SpreadSheetRowFilter();
    filter.setFinder(finder);

    filterData(m_TabbedPane.getTitleAt(m_TabbedPane.getSelectedIndex()), sheet, filter);
  }

  /**
   * Filters the spreadsheet using a conversion.
   */
  public void convert() {
    SpreadSheet		sheet;
    Conversion		conversion;
    Convert		filter;

    sheet = m_TabbedPane.getCurrentSheet();
    if (sheet == null)
      return;

    getConversionDialog().setVisible(true);
    if (getConversionDialog().getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
      return;

    conversion = (Conversion) getConversionDialog().getGOEEditor().getValue();
    filter = new Convert();
    filter.setConversion(conversion);

    filterData(m_TabbedPane.getTitleAt(m_TabbedPane.getSelectedIndex()), sheet, filter);
  }

  /**
   * Filters the spreadsheet using the selected transformer.
   */
  public void transform() {
    SpreadSheet		sheet;
    AbstractTransformer	transformer;

    sheet = m_TabbedPane.getCurrentSheet();
    if (sheet == null)
      return;

    getTransformerDialog().setVisible(true);
    if (getTransformerDialog().getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
      return;

    transformer = (AbstractTransformer) getTransformerDialog().getGOEEditor().getValue();

    filterData(m_TabbedPane.getTitleAt(m_TabbedPane.getSelectedIndex()), sheet, transformer);
  }

  /**
   * Computes the difference between the two sheets and inserts it as new tab.
   */
  protected void computeDifference(SpreadSheet sheet1, SpreadSheet sheet2, SpreadSheetColumnRange keyCols) {
    SpreadSheetDifference	filter;

    if ((sheet1 == null) || (sheet2 == null))
      return;

    filter = new SpreadSheetDifference();
    filter.setKeyColumns(keyCols);
    filterData(m_TabbedPane.newTitle(), new SpreadSheet[]{sheet1, sheet2}, filter);
  }

  /**
   * Shows a short dialog.
   */
  public void sort() {
    final ApprovalDialog	dialog;

    if (getParentDialog() != null)
      dialog = new ApprovalDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new ApprovalDialog(getParentFrame(), true);
    dialog.setDefaultCloseOperation(ApprovalDialog.DISPOSE_ON_CLOSE);
    dialog.setTitle("Sort");
    dialog.getApproveButton().setEnabled(false);
    if (m_SortPanel == null) {
      m_SortPanel = new SortPanel();
      m_SortPanel.addSortSetupListener(new SortSetupListener() {
	@Override
	public void sortSetupChanged(SortSetupEvent e) {
	  dialog.getApproveButton().setEnabled(e.getSortPanel().isValidSetup());
	}
      });
    }
    if (m_SortPanel.setSpreadSheet(m_TabbedPane.getCurrentSheet()))
      m_SortPanel.addDefinition();
    dialog.getApproveButton().setEnabled(m_SortPanel.isValidSetup());
    dialog.getContentPane().add(m_SortPanel, BorderLayout.CENTER);
    dialog.pack();
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
    if (dialog.getOption() != ApprovalDialog.APPROVE_OPTION)
      return;
    m_TabbedPane.getCurrentTable().sort(m_SortPanel.getComparator());
  }

  /**
   * Pops up a dialog allowing the user to generate a chart from the current
   * spreadsheet.
   */
  public void generateChart() {
    SpreadSheetPanel		panel;
    AbstractChartGenerator	generator;

    panel = m_TabbedPane.getCurrentPanel();
    if (panel == null)
      return;

    getChartGeneratorDialog().setVisible(true);
    if (getChartGeneratorDialog().getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
      return;

    generator = (AbstractChartGenerator) getChartGeneratorDialog().getGOEEditor().getValue();
    panel.generateChart(generator);
  }

  /**
   * Computes the difference between two sheets that the user selects and
   * inserts it as new tab.
   */
  public void computeDifference() {
    ApprovalDialog	dialog;
    ParameterPanel	params;
    final JComboBox	sheet1;
    final JComboBox	sheet2;
    List<String>	titles;
    final JTextField	range;

    if (getParentDialog() != null)
      dialog = new ApprovalDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new ApprovalDialog(getParentFrame(), true);
    dialog.setTitle("Compute difference");
    params = new ParameterPanel();
    dialog.getContentPane().add(params, BorderLayout.CENTER);
    titles = m_TabbedPane.getTabTitles();
    sheet1 = new JComboBox(titles.toArray(new String[titles.size()]));
    params.addParameter("First sheet", sheet1);
    params.addParameter("", new JLabel("minus"));
    sheet2 = new JComboBox(titles.toArray(new String[titles.size()]));
    params.addParameter("Second sheet", sheet2);
    params.addParameter("", new JLabel("using"));
    range = new JTextField(10);
    range.setText("");
    range.setToolTipText(new SpreadSheetColumnRange().getExample());
    params.addParameter("Key columns", range);
    dialog.pack();
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);

    if (dialog.getOption() != ApprovalDialog.APPROVE_OPTION)
      return;

    if (sheet1.getSelectedIndex() == sheet2.getSelectedIndex()) {
      GUIHelper.showErrorMessage(this, "You must select two different spreadsheets!");
      return;
    }

    computeDifference(
	m_TabbedPane.getSheetAt(sheet1.getSelectedIndex()),
	m_TabbedPane.getSheetAt(sheet2.getSelectedIndex()),
	new SpreadSheetColumnRange(range.getText()));
  }

  /**
   * Processes the current spreadsheet with the specified plugin.
   *
   * @param plugin	the plugin to use
   */
  protected void process(AbstractDataPlugin plugin) {
    SpreadSheetPanel	panel;
    SpreadSheet		input;
    SpreadSheet		output;

    panel = m_TabbedPane.getCurrentPanel();
    if (panel == null)
      return;
    input = panel.getSheet();
    if (input == null)
      return;
    plugin.setCurrentPanel(panel);
    output = plugin.process(input);
    if (plugin.getCanceledByUser() || (output == null))
      return;
    if (plugin.isInPlace())
      m_TabbedPane.getCurrentTable().setModel(new SpreadSheetTableModel(output));
    else
      m_TabbedPane.addTab(m_TabbedPane.newTitle(), output);
    plugin.setCurrentPanel(null);
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

    current = m_TabbedPane.getCurrentPanel();
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
      dialog.setIconImage(GUIHelper.getIcon(plugin.getMenuIcon()).getImage());
    dialog.getContentPane().add(panel, BorderLayout.CENTER);
    dialog.setCancelVisible(plugin.requiresButtons());
    dialog.setApproveVisible(plugin.requiresButtons());
    dialog.pack();
    dialog.setLocationRelativeTo(this);
    dialog.addWindowListener(new WindowAdapter() {
      /* (non-Javadoc)
       * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
       */
      @Override
      public void windowClosing(WindowEvent e) {
        super.windowClosing(e);
        plugin.setCurrentPanel(null);
      }
    });
    dialog.setVisible(true);
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    if (m_GOEChart != null) {
      m_GOEChart.dispose();
      m_GOEChart = null;
    }
    if (m_GOEColumnFinder != null) {
      m_GOEColumnFinder.dispose();
      m_GOEColumnFinder = null;
    }
    if (m_GOEConversion != null) {
      m_GOEConversion.dispose();
      m_GOEConversion = null;
    }
    if (m_GOERowFinder != null) {
      m_GOERowFinder.dispose();
      m_GOERowFinder = null;
    }
    if (m_GOETransformer != null) {
      m_GOETransformer.dispose();
      m_GOETransformer = null;
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
