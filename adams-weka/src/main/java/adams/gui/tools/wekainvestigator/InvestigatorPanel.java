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
 * InvestigatorPanel.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator;

import adams.core.ClassLister;
import adams.core.CleanUpHandler;
import adams.core.DateFormat;
import adams.core.DateUtils;
import adams.core.Properties;
import adams.core.StatusMessageHandler;
import adams.core.Utils;
import adams.core.option.OptionUtils;
import adams.data.weka.classattribute.AbstractClassAttributeHeuristic;
import adams.data.weka.classattribute.LastAttribute;
import adams.env.Environment;
import adams.env.WekaInvestigatorDefinition;
import adams.env.WekaInvestigatorShortcutsDefinition;
import adams.gui.action.AbstractBaseAction;
import adams.gui.action.BaseAction;
import adams.gui.chooser.WekaFileChooser;
import adams.gui.core.BaseMenu;
import adams.gui.core.BaseStatusBar;
import adams.gui.core.ConsolePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.RecentFilesHandlerWithCommandline;
import adams.gui.core.RecentFilesHandlerWithCommandline.Setup;
import adams.gui.event.RecentItemEvent;
import adams.gui.event.RecentItemListener;
import adams.gui.event.WekaInvestigatorDataEvent;
import adams.gui.goe.GenericObjectEditorDialog;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import adams.gui.tools.wekainvestigator.data.FileContainer;
import adams.gui.tools.wekainvestigator.source.AbstractSource;
import adams.gui.tools.wekainvestigator.tab.AbstractInvestigatorTab;
import adams.gui.tools.wekainvestigator.tab.InvestigatorTabbedPane;
import adams.gui.tools.wekainvestigator.tab.LogTab;
import adams.gui.workspace.AbstractWorkspacePanel;
import weka.core.Instances;
import weka.core.converters.AbstractFileLoader;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

/**
 * The main panel for the Investigator.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class InvestigatorPanel
  extends AbstractWorkspacePanel
  implements StatusMessageHandler, CleanUpHandler {

  private static final long serialVersionUID = 7442747356297265526L;

  /** the file to store the recent files in. */
  public final static String SESSION_FILE = "WekaInvestigatorSession.props";

  /** the name of the props file. */
  public final static String FILENAME = "WekaInvestigator.props";

  /** the name of the shortcut props file. */
  public final static String FILENAME_SHORTCUTS = "WekaInvestigatorShortcuts.props";

  /** the properties. */
  protected static Properties m_Properties;

  /** the shortcut properties. */
  protected static Properties m_ShortcutProperties;

  /** the tabbed pane for the tabs. */
  protected InvestigatorTabbedPane m_TabbedPane;

  /** the status bar. */
  protected BaseStatusBar m_StatusBar;

  /** the menu bar. */
  protected JMenuBar m_MenuBar;

  /** the submenu for a sources. */
  protected BaseMenu m_MenuFileSources;

  /** the action for closing the investigator. */
  protected BaseAction m_ActionFileClose;

  /** the action for enabling/disabling undo. */
  protected JCheckBoxMenuItem m_MenuItemEditUndoEnabled;

  /** the action for loading a dataset. */
  protected BaseAction m_ActionFileOpen;

  /** the action for selecting the class attribute heuristic. */
  protected BaseAction m_ActionFileClassAttribute;

  /** the submenu for a new tab. */
  protected BaseMenu m_MenuTabNewTab;

  /** the action for closing a tab. */
  protected BaseAction m_ActionTabCloseTab;

  /** the action for closing all tabs. */
  protected BaseAction m_ActionTabCloseAllTabs;

  /** the log. */
  protected StringBuilder m_Log;

  /** the data loaded. */
  protected List<DataContainer> m_Data;

  /** the filechooser for datasets. */
  protected WekaFileChooser m_FileChooser;

  /** the recent files handler. */
  protected RecentFilesHandlerWithCommandline<JMenu> m_RecentFilesHandler;

  /** the heuristic for selecting the class attribute. */
  protected AbstractClassAttributeHeuristic m_ClassAttribute;

  /** for timestamps in the statusbar. */
  protected DateFormat m_StatusBarDateFormat;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    String	cmdline;

    super.initialize();

    m_Log                 = new StringBuilder();
    m_Data                = new ArrayList<>();
    m_RecentFilesHandler  = null;
    m_StatusBarDateFormat = DateUtils.getTimeFormatter();
    m_FileChooser         = new WekaFileChooser();
    m_FileChooser.setMultiSelectionEnabled(true);

    cmdline = getProperties().getProperty("General.ClassAttributeHeuristic", OptionUtils.getCommandLine(new LastAttribute()));
    try {
      m_ClassAttribute = (AbstractClassAttributeHeuristic) OptionUtils.forAnyCommandLine(
        AbstractClassAttributeHeuristic.class, cmdline);
    }
    catch (Exception e) {
      ConsolePanel.getSingleton().append(Level.SEVERE, "Failed to instantiate class attribute heuristic: " + cmdline, e);
      m_ClassAttribute = new LastAttribute();
    }
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_TabbedPane = new InvestigatorTabbedPane(this);
    add(m_TabbedPane, BorderLayout.CENTER);

    m_StatusBar = new BaseStatusBar();
    m_StatusBar.setMouseListenerActive(true);
    add(m_StatusBar, BorderLayout.SOUTH);

    initActions();
  }

  /**
   * Adds the default tabs.
   */
  public void addDefaultTabs() {
    String[]			classes;
    AbstractInvestigatorTab	tab;

    classes = getProperties().getProperty("General.DefaultTabs", LogTab.class.getName()).split(",");
    for (String cls: classes) {
      try {
	tab = (AbstractInvestigatorTab) Class.forName(cls).newInstance();
	m_TabbedPane.addTab(tab);
      }
      catch (Exception e) {
	ConsolePanel.getSingleton().append(Level.SEVERE, "Failed to instantiate investigator tab: ", e);
      }
    }
  }

  /**
   * Initializes the actions.
   */
  protected void initActions() {
    // tabs
    m_ActionTabCloseTab = new AbstractBaseAction() {
      private static final long serialVersionUID = 1028160012672649573L;
      @Override
      protected void doActionPerformed(ActionEvent e) {
	int index = m_TabbedPane.getSelectedIndex();
	if (index > -1)
	  m_TabbedPane.removeTabAt(index);
	updateMenu();
      }
    };
    m_ActionTabCloseTab.setName("Close tab");
    m_ActionTabCloseTab.setIcon(GUIHelper.getIcon("close_tab_focused.gif"));

    m_ActionTabCloseAllTabs = new AbstractBaseAction() {
      private static final long serialVersionUID = 2162739410818834253L;
      @Override
      protected void doActionPerformed(ActionEvent e) {
	m_TabbedPane.removeAll();
	updateMenu();
      }
    };
    m_ActionTabCloseAllTabs.setName("Close all tabs");
    m_ActionTabCloseAllTabs.setIcon(GUIHelper.getEmptyIcon());

    m_ActionFileClose = new AbstractBaseAction() {
      private static final long serialVersionUID = -1104246458353845500L;
      @Override
      protected void doActionPerformed(ActionEvent e) {
	closeParent();
      }
    };
    m_ActionFileClose.setName("Close");
    m_ActionFileClose.setIcon("exit.png");
    m_ActionFileClose.setAccelerator("ctrl pressed Q");

    m_ActionFileOpen = new AbstractBaseAction() {
      private static final long serialVersionUID = -1104246458353845500L;
      @Override
      protected void doActionPerformed(ActionEvent e) {
	openFile();
      }
    };
    m_ActionFileOpen.setName("Open...");
    m_ActionFileOpen.setIcon("open.gif");
    m_ActionFileOpen.setAccelerator("ctrl pressed O");

    m_ActionFileClassAttribute = new AbstractBaseAction() {
      private static final long serialVersionUID = -1104246458353845500L;
      @Override
      protected void doActionPerformed(ActionEvent e) {
	chooseClassAttributeHeuristic();
      }
    };
    m_ActionFileClassAttribute.setName("Class attribute...");
    m_ActionFileClassAttribute.setIcon(GUIHelper.getEmptyIcon());
  }

  /**
   * Updates the actions.
   */
  protected void updateActions() {
    m_ActionTabCloseTab.setEnabled(m_TabbedPane.getTabCount() > 0);
    m_ActionTabCloseAllTabs.setEnabled(m_TabbedPane.getTabCount() > 0);
  }

  /**
   * Creates a menu bar (singleton per panel object). Can be used in frames.
   *
   * @return		the menu bar
   */
  @Override
  public JMenuBar getMenuBar() {
    JMenuBar			result;
    JMenu			menu;
    JMenu			submenu;
    JMenuItem			menuitem;
    Class[]			classes;
    AbstractInvestigatorTab	tab;
    AbstractSource		source;

    if (m_MenuBar == null) {
      result = new JMenuBar();

      // File
      menu = new JMenu("File");
      menu.setMnemonic('F');
      menu.addChangeListener((ChangeEvent e) -> updateMenu());
      result.add(menu);

      // File/Open file
      menu.add(m_ActionFileOpen);

      // File/Recent files
      submenu = new JMenu("Open recent");
      menu.add(submenu);
      m_RecentFilesHandler = new RecentFilesHandlerWithCommandline<>(SESSION_FILE, 10, submenu);
      m_RecentFilesHandler.addRecentItemListener(new RecentItemListener<JMenu,Setup>() {
	public void recentItemAdded(RecentItemEvent<JMenu,Setup> e) {
	  // ignored
	}
	public void recentItemSelected(RecentItemEvent<JMenu,Setup> e) {
	  openRecent(e);
	}
      });

      // File/Sources
      m_MenuFileSources = new BaseMenu("Other sources");
      m_MenuFileSources.setIcon(GUIHelper.getEmptyIcon());
      menu.add(m_MenuFileSources);
      classes = ClassLister.getSingleton().getClasses(AbstractSource.class);
      for (final Class cls: classes) {
	try {
	  source   = (AbstractSource) cls.newInstance();
	  source.setOwner(this);
	  menuitem = new JMenuItem(source);
	  m_MenuFileSources.add(menuitem);
	}
	catch (Exception e) {
	  ConsolePanel.getSingleton().append("Failed to instantiate source class: " + cls.getName(), e);
	}
      }
      m_MenuFileSources.sort();

      // File/Class attribute
      menu.add(m_ActionFileClassAttribute);

      menu.addSeparator();

      // File/Close
      menu.add(m_ActionFileClose);

      // Edit
      menu = new JMenu("Edit");
      menu.setMnemonic('E');
      menu.addChangeListener((ChangeEvent e) -> updateMenu());
      result.add(menu);

      // Edit/Undo enabled
      m_MenuItemEditUndoEnabled = new JCheckBoxMenuItem("Undo enabled");
      m_MenuItemEditUndoEnabled.setIcon(GUIHelper.getIcon("undo.gif"));
      m_MenuItemEditUndoEnabled.setSelected(getProperties().getBoolean("General.UndoEnabled", true));
      m_MenuItemEditUndoEnabled.addActionListener((ActionEvent e) -> toggleUndo());
      menu.add(m_MenuItemEditUndoEnabled);

      // Tab
      menu = new JMenu("Tab");
      menu.setMnemonic('T');
      menu.addChangeListener((ChangeEvent e) -> updateMenu());
      result.add(menu);

      // Tab/New tab
      m_MenuTabNewTab = new BaseMenu("New tab");
      m_MenuTabNewTab.setIcon(GUIHelper.getIcon("new.gif"));
      menu.add(m_MenuTabNewTab);
      classes = ClassLister.getSingleton().getClasses(AbstractInvestigatorTab.class);
      for (final Class cls: classes) {
	try {
	  tab      = (AbstractInvestigatorTab) cls.newInstance();
	  menuitem = new JMenuItem(tab.getTitle());
	  if (tab.getTabIcon() == null)
	    menuitem.setIcon(GUIHelper.getEmptyIcon());
	  else
	    menuitem.setIcon(GUIHelper.getIcon(tab.getTabIcon()));
          // shortcut?
          if (getShortcutProperties().hasKey("Tab-" + cls.getName()))
	    menuitem.setAccelerator(GUIHelper.getKeyStroke(getShortcutProperties().getProperty("Tab-" + cls.getName())));
	  menuitem.addActionListener((ActionEvent e) -> {
	    try {
	      AbstractInvestigatorTab tabNew = (AbstractInvestigatorTab) cls.newInstance();
	      m_TabbedPane.addTab(tabNew, true);
	    }
	    catch (Exception ex) {
	      ConsolePanel.getSingleton().append("Failed to instantiate tab class: " + cls.getName(), ex);
	    }
	  });
	  m_MenuTabNewTab.add(menuitem);
	}
	catch (Exception e) {
	  ConsolePanel.getSingleton().append("Failed to instantiate tab class: " + cls.getName(), e);
	}
      }
      m_MenuTabNewTab.sort();
      menu.addSeparator();
      menu.add(m_ActionTabCloseTab);
      menu.add(m_ActionTabCloseAllTabs);

      m_MenuBar = result;
    }
    else {
      result = m_MenuBar;
    }

    return result;
  }

  /**
   * Updates the title of the dialog.
   */
  @Override
  protected void updateTitle() {
    setParentTitle(m_TitleGenerator.generate(getDefaultTitle()));
  }

  /**
   * updates the enabled state of the menu items.
   */
  @Override
  protected void updateMenu() {
    updateActions();
    // TODO
  }

  /**
   * Returns the default title.
   *
   * @return		the default title
   */
  @Override
  protected String getDefaultTitle() {
    return "Investigator";
  }

  /**
   * Return the timestamp prefix for logs.
   *
   * @return		the prefix
   */
  protected String getTimestampPrefix() {
    return "[" + m_StatusBarDateFormat.format(new Date()) + "] ";
  }

  /**
   * Logs the message.
   *
   * @param msg		the log message
   */
  @Override
  public synchronized void logMessage(String msg) {
    int		i;
    String	prefix;

    prefix = getTimestampPrefix();

    m_Log.append(prefix + msg);
    m_Log.append("\n");

    for (i = 0; i < m_TabbedPane.getTabCount(); i++) {
      if (m_TabbedPane.getComponentAt(i) instanceof LogTab)
	((LogTab) m_TabbedPane.getComponentAt(i)).append(prefix + msg);
    }
  }

  /**
   * Logs the error message and also displays an error dialog.
   *
   * @param msg		the error message
   * @param title	the title for the dialog
   */
  @Override
  public void logError(String msg, String title) {
    logMessage(msg);
    showStatus(msg);
    GUIHelper.showErrorMessage(this, msg, title);
  }

  /**
   * Returns the internal log buffer.
   *
   * @return		the buffer
   */
  public StringBuilder getLog() {
    return m_Log;
  }

  /**
   * Empties the log.
   */
  public void clearLog() {
    int		i;

    m_Log.setLength(0);
    for (i = 0; i < m_TabbedPane.getTabCount(); i++) {
      if (m_TabbedPane.getComponentAt(i) instanceof LogTab)
	((LogTab) m_TabbedPane.getComponentAt(i)).clearLog();
    }
  }

  /**
   * Returns the currently loaded data.
   *
   * @return		the data
   */
  public List<DataContainer> getData() {
    return m_Data;
  }

  /**
   * Notifies all the tabs that the data has changed.
   *
   * @param e		the event to send
   */
  public void fireDataChange(WekaInvestigatorDataEvent e) {
    int		i;

    for (i = 0; i < m_TabbedPane.getTabCount(); i++)
      ((AbstractInvestigatorTab) m_TabbedPane.getComponentAt(i)).dataChanged(e);

    updateMenu();
  }

  /**
   * Returns the underlying tabbed pane.
   *
   * @return		the tabbed pane
   */
  public InvestigatorTabbedPane getTabbedPane() {
    return m_TabbedPane;
  }

  /**
   * Updates the class attribute, if not set.
   *
   * @param data	the data to update
   * @return		the (potentially) updated data
   */
  public Instances updateClassAttribute(Instances data) {
    if (data.classIndex() == -1)
      data.setClassIndex(m_ClassAttribute.determineClassAttribute(data));
    return data;
  }

  /**
   * Lets user select a dataset.
   */
  public void openFile() {
    int				retVal;
    final AbstractFileLoader	loader;

    retVal = m_FileChooser.showOpenDialog(this);
    if (retVal != WekaFileChooser.APPROVE_OPTION)
      return;

    loader = m_FileChooser.getReader();
    for (final File file: m_FileChooser.getSelectedFiles()) {
      logMessage("Loading: " + file);
      final FileContainer cont = new FileContainer(loader, file);
      cont.getUndo().setEnabled(isUndoEnabled());
      updateClassAttribute(cont.getData());
      SwingUtilities.invokeLater(() -> {
	m_Data.add(cont);
	if (m_RecentFilesHandler != null)
	  m_RecentFilesHandler.addRecentItem(new Setup(file, loader));
	logMessage("Loaded: " + file);
	fireDataChange(new WekaInvestigatorDataEvent(this, WekaInvestigatorDataEvent.ROWS_ADDED, m_Data.size() - 1));
      });
    }
  }

  /**
   * Lets user select a dataset.
   */
  public void openFile(File file) {
    AbstractFileLoader  loader;
    SwingWorker 	worker;

    logMessage("Loading: " + file);
    loader = m_FileChooser.getReaderForFile(file);
    if (loader == null) {
      logError("Failed to determine loader for: " + file, "Error loading");
      return;
    }

    worker = new SwingWorker() {
      @Override
      protected Object doInBackground() throws Exception {
	final FileContainer cont = new FileContainer(loader, file);
	cont.getUndo().setEnabled(isUndoEnabled());
	updateClassAttribute(cont.getData());
	SwingUtilities.invokeLater(() -> {
	  m_Data.add(cont);
	  if (m_RecentFilesHandler != null)
	    m_RecentFilesHandler.addRecentItem(new Setup(file, loader));
	  logMessage("Loaded: " + file);
	  fireDataChange(new WekaInvestigatorDataEvent(
	    InvestigatorPanel.this, WekaInvestigatorDataEvent.ROWS_ADDED, m_Data.size() - 1));
	});
	return null;
      }
    };
    worker.execute();
  }

  /**
   * For opening a recently used file.
   *
   * @param e		the event
   */
  public void openRecent(RecentItemEvent<JMenu,Setup> e) {
    AbstractFileLoader 	loader;
    SwingWorker 	worker;

    loader = (AbstractFileLoader) e.getItem().getHandler();
    if (loader == null) {
      logError("Failed to determine file loader for the following file:\n" + e.getItem(), "Error reloading data");
      return;
    }

    worker = new SwingWorker() {
      @Override
      protected Object doInBackground() throws Exception {
	try {
	  logMessage("Loading: " + e.getItem());
	  loader.setFile(e.getItem().getFile());
	  final FileContainer cont = new FileContainer(loader, e.getItem().getFile());
	  updateClassAttribute(cont.getData());
	  SwingUtilities.invokeLater(() -> {
	    m_Data.add(cont);
	    m_FileChooser.setCurrentDirectory(e.getItem().getFile().getParentFile());
	    logMessage("Loaded: " + e.getItem());
	    fireDataChange(new WekaInvestigatorDataEvent(
	      InvestigatorPanel.this, WekaInvestigatorDataEvent.ROWS_ADDED, m_Data.size() - 1));
	  });
	}
	catch (Exception ex) {
	  logError("Failed to load file:\n" + e.getItem() + "\n" + Utils.throwableToString(ex), "Error reloading data");
	}
	return null;
      }
    };
    worker.execute();
  }

  /**
   * Lets the user choose the class attribute heuristic.
   */
  public void chooseClassAttributeHeuristic() {
    GenericObjectEditorDialog	dialog;

    if (getParentDialog() != null)
      dialog = new GenericObjectEditorDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new GenericObjectEditorDialog(getParentFrame(), true);
    dialog.setTitle("Select class attribute heuristic");
    dialog.getGOEEditor().setClassType(AbstractClassAttributeHeuristic.class);
    dialog.getGOEEditor().setCanChangeClassInDialog(true);
    dialog.setCurrent(m_ClassAttribute);
    dialog.pack();
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
    if (dialog.getResult() == GenericObjectEditorDialog.APPROVE_OPTION)
      m_ClassAttribute = (AbstractClassAttributeHeuristic) dialog.getCurrent();
    dialog.dispose();
  }

  /**
   * Returns whether undo is enabled.
   *
   * @return		true if enabled
   */
  public boolean isUndoEnabled() {
    return m_MenuItemEditUndoEnabled.isSelected();
  }

  /**
   * Toggles the undo state.
   */
  protected void toggleUndo() {
    WekaInvestigatorDataEvent 	event;

    for (DataContainer cont: getData())
      cont.getUndo().setEnabled(isUndoEnabled());

    event = new WekaInvestigatorDataEvent(
      this,
      isUndoEnabled() ? WekaInvestigatorDataEvent.UNDO_ENABLED : WekaInvestigatorDataEvent.UNDO_DISABLED);
    fireDataChange(event);
  }

  /**
   * Displays a message.
   *
   * @param msg		the message to display
   */
  public void showStatus(String msg) {
    m_StatusBar.showStatus(getTimestampPrefix() + msg);
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    super.cleanUp();
    m_Data.clear();
    m_TabbedPane.cleanUp();
  }

  /**
   * Returns the properties that define the investigator.
   *
   * @return		the properties
   */
  public static synchronized Properties getProperties() {
    if (m_Properties == null)
      m_Properties = Environment.getInstance().read(WekaInvestigatorDefinition.KEY);

    return m_Properties;
  }

  /**
   * Returns the properties that define the shortcuts.
   *
   * @return		the properties
   */
  public static synchronized Properties getShortcutProperties() {
    if (m_ShortcutProperties == null)
      m_ShortcutProperties = Environment.getInstance().read(WekaInvestigatorShortcutsDefinition.KEY);

    return m_ShortcutProperties;
  }
}
