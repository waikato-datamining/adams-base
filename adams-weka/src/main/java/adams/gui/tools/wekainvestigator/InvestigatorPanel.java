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
import adams.core.Properties;
import adams.core.StatusMessageHandler;
import adams.core.Utils;
import adams.core.option.OptionUtils;
import adams.env.Environment;
import adams.env.WekaInvestigatorDefinition;
import adams.gui.action.AbstractBaseAction;
import adams.gui.action.BaseAction;
import adams.gui.chooser.WekaFileChooser;
import adams.gui.core.BaseMenu;
import adams.gui.core.BaseStatusBar;
import adams.gui.core.ConsolePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.RecentFilesHandler;
import adams.gui.event.RecentItemEvent;
import adams.gui.event.RecentItemListener;
import adams.gui.goe.GenericObjectEditorDialog;
import adams.data.weka.classattribute.AbstractClassAttributeHeuristic;
import adams.data.weka.classattribute.LastAttribute;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import adams.gui.tools.wekainvestigator.data.FileContainer;
import adams.gui.tools.wekainvestigator.source.AbstractSource;
import adams.gui.tools.wekainvestigator.tab.AbstractInvestigatorTab;
import adams.gui.tools.wekainvestigator.tab.InvestigatorTabbedPane;
import adams.gui.tools.wekainvestigator.tab.LogTab;
import adams.gui.workspace.AbstractWorkspacePanel;
import weka.core.Instances;
import weka.core.converters.AbstractFileLoader;
import weka.core.converters.ConverterUtils;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
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

  /** the properties. */
  protected static Properties m_Properties;

  /** the tabbed pane for the tabs. */
  protected InvestigatorTabbedPane m_TabbedPane;

  /** the status bar. */
  protected BaseStatusBar m_StatusBar;

  /** the menu bar. */
  protected JMenuBar m_MenuBar;

  /** the submenu for a new tab. */
  protected BaseMenu m_MenuFileNewTab;

  /** the submenu for a sources. */
  protected BaseMenu m_MenuFileSources;

  /** the action for closing a tab. */
  protected BaseAction m_ActionFileCloseTab;

  /** the action for closing all tabs. */
  protected BaseAction m_ActionFileCloseAllTabs;

  /** the action for closing the investigator. */
  protected BaseAction m_ActionFileClose;

  /** the action for loading a dataset. */
  protected BaseAction m_ActionFileOpen;

  /** the action for selecting the class attribute heuristic. */
  protected BaseAction m_ActionFileClassAttribute;

  /** the log. */
  protected StringBuilder m_Log;

  /** the data loaded. */
  protected List<DataContainer> m_Data;

  /** the filechooser for datasets. */
  protected WekaFileChooser m_FileChooser;

  /** the recent files handler. */
  protected RecentFilesHandler<JMenu> m_RecentFilesHandler;

  /** the heuristic for selecting the class attribute. */
  protected AbstractClassAttributeHeuristic m_ClassAttribute;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    String	cmdline;

    super.initialize();

    m_Log                = new StringBuilder();
    m_Data               = new ArrayList<>();
    m_FileChooser        = new WekaFileChooser();
    m_RecentFilesHandler = null;

    cmdline = getProperties().getProperty("ClassAttributeHeuristic", OptionUtils.getCommandLine(new LastAttribute()));
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
    String[]			classes;
    AbstractInvestigatorTab	tab;

    super.initGUI();

    setLayout(new BorderLayout());

    m_TabbedPane = new InvestigatorTabbedPane(this);
    add(m_TabbedPane, BorderLayout.CENTER);

    classes = getProperties().getProperty("DefaultTabs", LogTab.class.getName()).split(",");
    for (String cls: classes) {
      try {
	tab = (AbstractInvestigatorTab) Class.forName(cls).newInstance();
	m_TabbedPane.addTab(tab);
      }
      catch (Exception e) {
	ConsolePanel.getSingleton().append(Level.SEVERE, "Failed to instantiate investigator tab: ", e);
      }
    }

    m_StatusBar = new BaseStatusBar();
    add(m_StatusBar, BorderLayout.SOUTH);

    initActions();
  }

  /**
   * Initializes the actions.
   */
  protected void initActions() {
    // tabs
    m_ActionFileCloseTab = new AbstractBaseAction() {
      private static final long serialVersionUID = 1028160012672649573L;
      @Override
      protected void doActionPerformed(ActionEvent e) {
	int index = m_TabbedPane.getSelectedIndex();
	if (index > -1)
	  m_TabbedPane.removeTabAt(index);
	updateMenu();
      }
    };
    m_ActionFileCloseTab.setName("Close tab");
    m_ActionFileCloseTab.setIcon(GUIHelper.getEmptyIcon());

    m_ActionFileCloseAllTabs = new AbstractBaseAction() {
      private static final long serialVersionUID = 2162739410818834253L;
      @Override
      protected void doActionPerformed(ActionEvent e) {
	m_TabbedPane.removeAll();
	updateMenu();
      }
    };
    m_ActionFileCloseAllTabs.setName("Close all tabs");
    m_ActionFileCloseAllTabs.setIcon(GUIHelper.getEmptyIcon());

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
    m_ActionFileCloseTab.setEnabled(m_TabbedPane.getTabCount() > 0);
    m_ActionFileCloseAllTabs.setEnabled(m_TabbedPane.getTabCount() > 0);
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
      menu.addChangeListener((ChangeEvent e) -> updateMenu());
      result.add(menu);

      // File/New tab
      m_MenuFileNewTab = new BaseMenu("New tab");
      m_MenuFileNewTab.setIcon(GUIHelper.getIcon("new.gif"));
      menu.add(m_MenuFileNewTab);
      classes = ClassLister.getSingleton().getClasses(AbstractInvestigatorTab.class);
      for (final Class cls: classes) {
	try {
	  tab      = (AbstractInvestigatorTab) cls.newInstance();
	  menuitem = new JMenuItem(tab.getTitle());
	  if (tab.getTabIcon() == null)
	    menuitem.setIcon(GUIHelper.getEmptyIcon());
	  else
	    menuitem.setIcon(GUIHelper.getIcon(tab.getTabIcon()));
	  menuitem.addActionListener((ActionEvent e) -> {
	    try {
	      AbstractInvestigatorTab tabNew = (AbstractInvestigatorTab) cls.newInstance();
	      m_TabbedPane.addTab(tabNew);
	    }
	    catch (Exception ex) {
	      ConsolePanel.getSingleton().append("Failed to instantiate tab class: " + cls.getName(), ex);
	    }
	  });
	  m_MenuFileNewTab.add(menuitem);
	}
	catch (Exception e) {
	  ConsolePanel.getSingleton().append("Failed to instantiate tab class: " + cls.getName(), e);
	}
      }
      m_MenuFileNewTab.sort();
      menu.add(m_ActionFileCloseTab);
      menu.add(m_ActionFileCloseAllTabs);

      menu.addSeparator();

      // File/Open file
      menu.add(m_ActionFileOpen);

      // File/Recent files
      submenu = new JMenu("Open recent");
      menu.add(submenu);
      m_RecentFilesHandler = new RecentFilesHandler<>(SESSION_FILE, 10, submenu);
      m_RecentFilesHandler.addRecentItemListener(new RecentItemListener<JMenu,File>() {
	public void recentItemAdded(RecentItemEvent<JMenu,File> e) {
	  // ignored
	}
	public void recentItemSelected(RecentItemEvent<JMenu,File> e) {
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
   * Logs the message.
   *
   * @param msg		the log message
   */
  @Override
  public void logMessage(String msg) {
    int		i;

    m_Log.append(msg);
    m_Log.append("\n");

    m_StatusBar.showStatus(msg);

    for (i = 0; i < m_TabbedPane.getTabCount(); i++) {
      if (m_TabbedPane.getComponentAt(i) instanceof LogTab)
	((LogTab) m_TabbedPane.getComponentAt(i)).append(msg);
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
   */
  public void fireDataChange() {
    int		i;

    for (i = 0; i < m_TabbedPane.getTabCount(); i++)
      ((AbstractInvestigatorTab) m_TabbedPane.getComponentAt(i)).dataChanged();

    updateMenu();
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
    int			retVal;
    FileContainer	cont;

    retVal = m_FileChooser.showOpenDialog(this);
    if (retVal != WekaFileChooser.APPROVE_OPTION)
      return;

    logMessage("Loading: " + m_FileChooser.getSelectedFile());
    cont = new FileContainer(m_FileChooser.getReader(), m_FileChooser.getSelectedFile());
    updateClassAttribute(cont.getData());
    m_Data.add(cont);
    if (m_RecentFilesHandler != null)
      m_RecentFilesHandler.addRecentItem(m_FileChooser.getSelectedFile());
    logMessage("Loaded: " + m_FileChooser.getSelectedFile());
    fireDataChange();
  }

  /**
   * Lets user select a dataset.
   */
  public void openFile(File file) {
    FileContainer	cont;
    AbstractFileLoader  loader;

    logMessage("Loading: " + file);
    loader = m_FileChooser.getReaderForFile(file);
    if (loader == null) {
      logError("Failed to determine loader for: " + file, "Error loading");
      return;
    }

    cont = new FileContainer(loader, file);
    updateClassAttribute(cont.getData());
    m_Data.add(cont);
    if (m_RecentFilesHandler != null)
      m_RecentFilesHandler.addRecentItem(file);
    logMessage("Loaded: " + file);
    fireDataChange();
  }

  /**
   * For opening a recently used file.
   *
   * @param e		the event
   */
  public void openRecent(RecentItemEvent<JMenu,File> e) {
    AbstractFileLoader 	loader;
    FileContainer	cont;

    loader = ConverterUtils.getLoaderForFile(e.getItem());
    if (loader == null) {
      logError("Failed to determine file loader for the following file:\n" + e.getItem(), "Error reloading data");
      return;
    }

    try {
      logMessage("Loading: " + e.getItem());
      loader.setFile(e.getItem());
      cont = new FileContainer(loader, e.getItem());
      updateClassAttribute(cont.getData());
      m_Data.add(cont);
      m_FileChooser.setCurrentDirectory(e.getItem().getParentFile());
      logMessage("Loaded: " + e.getItem());
      fireDataChange();
    }
    catch (Exception ex) {
      logError("Failed to load file:\n" + e.getItem() + "\n" + Utils.throwableToString(ex), "Error reloading data");
    }
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
   * Displays a message.
   *
   * @param msg		the message to display
   */
  public void showStatus(String msg) {
    m_StatusBar.showStatus(msg);
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
   * Returns the properties that define the editor.
   *
   * @return		the properties
   */
  public static synchronized Properties getProperties() {
    if (m_Properties == null)
      m_Properties = Environment.getInstance().read(WekaInvestigatorDefinition.KEY);

    return m_Properties;
  }
}
