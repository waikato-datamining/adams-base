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
 * ApplicationFrame.java
 * Copyright (C) 2008-2013 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.application;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import adams.core.base.BaseString;
import adams.core.io.PlaceholderDirectory;
import adams.core.logging.ConsolePanelHandler;
import adams.core.logging.LoggingHelper;
import adams.core.logging.LoggingLevel;
import adams.core.management.Launcher;
import adams.core.management.ProcessUtils;
import adams.core.management.RestartableApplication;
import adams.core.net.InternetHelper;
import adams.core.option.AbstractOptionConsumer;
import adams.core.option.ArrayConsumer;
import adams.core.option.OptionUtils;
import adams.db.AbstractDatabaseConnection;
import adams.db.AbstractIndexedTable;
import adams.db.DatabaseConnectionHandler;
import adams.env.Environment;
import adams.event.DatabaseConnectionChangeEvent;
import adams.event.DatabaseConnectionChangeEvent.EventType;
import adams.event.DatabaseConnectionChangeListener;
import adams.gui.core.AbstractFrameWithOptionHandling;
import adams.gui.core.BasePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.MenuBarProvider;
import adams.gui.scripting.ScriptingEngine;
import adams.gui.scripting.ScriptingEngineHandler;
import adams.gui.scripting.ScriptingLogPanel;

/**
 * Abstract frame class for applications.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractApplicationFrame
  extends AbstractFrameWithOptionHandling
  implements DatabaseConnectionHandler, DatabaseConnectionChangeListener,
             RestartableApplication {

  /** for serialization. */
  private static final long serialVersionUID = -5800519559483605870L;

  /** the props file key for the menu bar. */
  public final static String LAYOUT_MENUBAR = "MenuBar";

  /** the props file key prefix for the menus. */
  public final static String LAYOUT_MENU_PREFIX = "Menu.";

  /** the props file key for the windows menu. */
  public final static String LAYOUT_MENU_WINDOWS = "Windows";

  /** the separator between classname and shortcut. */
  public final static String LAYOUT_SHORTCUT_SEPARATOR = "#";

  /** the frame itself. */
  protected AbstractApplicationFrame m_Self;

  /** contains the child frames/windows (title &lt;-&gt; object). */
  protected HashSet<Child> m_Children;

  /** the "windows" menu. */
  protected JMenu m_MenuWindows;

  /** the scripting log panel. */
  protected ScriptingLogPanel m_ScriptingLogPanel;

  /** the global database connection. */
  protected AbstractDatabaseConnection m_DbConn;

  /** the title of the application. */
  protected String m_ApplicationTitle;

  /** the logging level. */
  protected LoggingLevel m_LoggingLevel;

  /** the user mode - determines what menu entries to display. */
  protected UserMode m_UserMode;

  /** whether the application can be restarted (through Launcher class). */
  protected boolean m_EnableRestart;

  /** the menu items (classnames with further parameters) to start up immediately. */
  protected BaseString[] m_StartUps;

  /** the directories containing PDF documentation. */
  protected PlaceholderDirectory[] m_DocumentationDirectories;
  
  /** the application menu in use. */
  protected ApplicationMenu m_AppMenu;

  /** whether initialization has finished. */
  protected boolean m_InitFinished;

  /** the logger to use. */
  protected Logger m_Logger;
  
  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"logging-level", "loggingLevel",
	LoggingLevel.INFO);

    m_OptionManager.add(
	"title", "applicationTitle",
	getDefaultApplicationTitle());

    m_OptionManager.add(
	"user-mode", "userMode",
	UserMode.EXPERT);

    m_OptionManager.add(
	"start-up", "startUps",
	new BaseString[0]);

    m_OptionManager.add(
	"doc-dir", "documentationDirectories",
	new PlaceholderDirectory[0]);

    m_OptionManager.add(
	"enable-restart", "enableRestart",
	false);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    m_InitFinished = false;

    super.initialize();

    initializeLogger();
    
    m_DbConn            = getDefaultDatabaseConnection();
    m_DbConn.addChangeListener(this);

    m_ScriptingLogPanel = new ScriptingLogPanel();
    m_Children          = new HashSet<Child>();

    m_AppMenu           = null;
  }
  
  /**
   * Initializes the logger.
   * <p/>
   * Default implementation uses the class name.
   */
  @Override
  protected void initializeLogger() {
    m_Logger = LoggingHelper.getLogger(getClass());
  }
  
  /**
   * Returns the logger in use.
   * 
   * @return		the logger
   */
  @Override
  public Logger getLogger() {
    return m_Logger;
  }

  /**
   * Returns the default database connection.
   *
   * @return		the default database connection
   */
  protected abstract AbstractDatabaseConnection getDefaultDatabaseConnection();

  /**
   * Returns the default title of the application.
   *
   * @return		the default title
   */
  protected abstract String getDefaultApplicationTitle();

  /**
   * Sets the logging level.
   *
   * @param value 	the level
   */
  public void setLoggingLevel(LoggingLevel value) {
    m_LoggingLevel = value;
    getDatabaseConnection().setLoggingLevel(value);
    getLogger().setLevel(value.getLevel());
  }

  /**
   * Returns the logging level.
   *
   * @return 		the level
   */
  public LoggingLevel getLoggingLevel() {
    return m_LoggingLevel;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String loggingLevelTipText() {
    return "The logging level to use.";
  }

  /**
   * Returns the currently set application title.
   *
   * @return		the current title
   */
  public String getApplicationTitle() {
    return m_ApplicationTitle;
  }

  /**
   * Sets the application title to use.
   *
   * @param value	the title to use
   */
  public void setApplicationTitle(String value) {
    m_ApplicationTitle = value;
    createTitle("");
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String applicationTitleTipText() {
    return "The title for the application.";
  }

  /**
   * Sets the user mode - determines what menu entries are being displayed.
   *
   * @param value 	the user mode
   */
  public void setUserMode(UserMode value) {
    if (m_UserMode != value) {
      m_UserMode = value;
      if (m_InitFinished) {
	setJMenuBar(createMenuBar());
	setVisible(true);  // necessary, otherwise menu is blocked for some reason
	windowListChanged();
      }
    }
  }

  /**
   * Returns the current user mode - determines what menu entries are being
   * displayed.
   *
   * @return 		the user mode
   */
  public UserMode getUserMode() {
    return m_UserMode;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String userModeTipText() {
    return "The user mode, which determines the visibility of the menu items.";
  }

  /**
   * Returns the currently set menu items to immediately start up.
   *
   * @return		the menu items (incl. further parameters)
   */
  public BaseString[] getStartUps() {
    return m_StartUps;
  }

  /**
   * Sets the menu items to start up immediately.
   *
   * @param value	the menu items (incl. further parameters)
   */
  public void setStartUps(BaseString[] value) {
    m_StartUps = value;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String startUpsTipText() {
    return
        "The menu items to start up immediately; each consists of classname "
      + "and optional parameters (in case the menu definition implements "
      + AdditionalParameterHandler.class.getName() + ").";
  }

  /**
   * Returns the currently set directories with PDF documentation.
   *
   * @return		the directories
   */
  public PlaceholderDirectory[] getDocumentationDirectories() {
    return m_DocumentationDirectories;
  }

  /**
   * Sets the currently set directories with PDF documentation.
   *
   * @param value	the menu items (incl. further parameters)
   */
  public void setDocumentationDirectories(PlaceholderDirectory[] value) {
    m_DocumentationDirectories = value;
    if (m_InitFinished) {
      setJMenuBar(createMenuBar());
      setVisible(true);  // necessary, otherwise menu is blocked for some reason
      windowListChanged();
    }
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String documentationDirectoriesTipText() {
    return "The directories containing PDF documentation (may get listed in the Help menu).";
  }

  /**
   * Sets whether to enable the restart through the Launcher.
   *
   * @param value	true if to enable restart via Launcher class
   */
  @Override
  public void setEnableRestart(boolean value) {
    if (value != m_EnableRestart)  {
      m_EnableRestart = value;
      reset();
      setJMenuBar(createMenuBar());
    }
  }

  /**
   * Returns whether to enable the restart through the Launcher.
   *
   * @return		true if restart is enabled
   */
  @Override
  public boolean getEnableRestart() {
    return m_EnableRestart;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String enableRestartTipText() {
    return
        "If enabled and started through the " + Launcher.class.getName()
      + " class, the application can be restarted through the menu.";
  }

  /**
   * Sets the look'n'feel.
   */
  protected void setLookAndFeel() {
    GUIHelper.setLookAndFeel(GUIHelper.getLookAndFeel());
  }

  /**
   * initializes the GUI.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    m_Self = this;

    setLookAndFeel();

    createTitle("");
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    setJMenuBar(createMenuBar());

    // size + position
    pack();
    setSize(getGraphicsConfiguration().getBounds().width, getHeight());
    setLocation(0, 0);
  }

  /**
   * finishes the initialization, by setting size/location.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    AbstractInitialization.initAll(this);
    createTitle("");
    m_InitFinished = true;
    setUserMode(m_UserMode);
    LoggingHelper.setDefaultHandler(new ConsolePanelHandler());
  }

  /**
   * Closes all children frames.
   */
  protected void closeChildren() {
    Iterator<Child> 	iter;
    List<Child> 	list;
    int 		i;
    Child 		c;

    // close all children
    iter = getWindowList();
    list = new ArrayList<Child>();
    while (iter.hasNext())
      list.add(iter.next());
    for (i = 0; i < list.size(); i++) {
      c = list.get(i);
      c.dispose();
    }
  }

  /**
   * Closes the application.
   */
  protected void closeApplication() {
    m_Self.dispose();
  }

  /**
   * Closes the application down.
   */
  public void close() {
    // close all children
    closeChildren();

    // close main window
    closeApplication();

    // make sure we stop
    System.exit(0);
  }

  /**
   * Returns the filename that stores the menu layout.
   *
   * @return		the filename
   */
  protected abstract String getMenuLayoutFile();

  /**
   * Returns the menu generator.
   *
   * @return		the menu generator
   */
  protected synchronized ApplicationMenu getAppMenu() {
    if (m_AppMenu == null) {
      m_AppMenu = new ApplicationMenu(this);
      m_AppMenu.setSetup(getMenuLayoutFile());
      m_AppMenu.setUserMode(getUserMode());
    }

    return m_AppMenu;
  }

  /**
   * Creates the menu bar.
   *
   * @return		the generated menu bar
   */
  protected JMenuBar createMenuBar() {
    JMenuBar		result;

    m_AppMenu     = null;
    result        = getAppMenu().getMenuBar();
    m_MenuWindows = getAppMenu().getWindowsMenu();

    return result;
  }

  /**
   * creates a frame and returns it.
   *
   * @param title		the title of the frame
   * @param c			the component to place, can be null
   * @param width		the width of the frame, ignored if -1
   * @param height		the height of the frame, ignored if -1
   * @param icon		the icon to use, null for default
   * @return			the generated frame
   */
  protected ChildFrame createChildFrame(String title, Component c, int width, int height, String icon) {
    return createChildFrame(this, title, c, width, height, icon);
  }

  /**
   * creates a frame and returns it.
   *
   * @param owner		the owner
   * @param title		the title of the frame
   * @param c			the component to place, can be null
   * @param width		the width of the frame, ignored if -1
   * @param height		the height of the frame, ignored if -1
   * @param icon		the icon to use, null for default
   * @return			the generated frame
   */
  public static ChildFrame createChildFrame(AbstractApplicationFrame owner, String title, Component c, int width, int height, String icon) {
    ChildFrame 			result;
    int 			screenHeight;
    int 			screenWidth;
    ScriptingEngineHandler 	handler;

    result = new ChildFrame(owner, title, icon);

    // layout
    result.setLayout(new BorderLayout());
    if (c != null)
      result.getContentPane().add(c, BorderLayout.CENTER);

    // size
    result.pack();
    if ((width > -1) && (height > -1))
      result.setSize(width, height);
    result.validate();

    // location
    if (owner != null) {
      screenHeight = owner.getGraphicsConfiguration().getBounds().height;
      screenWidth  = owner.getGraphicsConfiguration().getBounds().width;
      result.setLocation(
	  (screenWidth - result.getBounds().width) / 2,
	  (screenHeight - result.getBounds().height) / 2);
    }

    // custom size and location
    if (c != null)
      GUIHelper.setSizeAndLocation(result, c);

    // add listener
    result.addDisposeWindowListener();

    // menu bar?
    if ((c != null) && (c instanceof MenuBarProvider))
      result.setJMenuBar(((MenuBarProvider) c).getMenuBar());

    // startup script?
    if ((c != null) && (c instanceof ScriptingEngineHandler) && (c instanceof BasePanel)) {
      handler = (ScriptingEngineHandler) c;
      if (GUIHelper.getStartupScript(c) != null)
	handler.getScriptingEngine().add((BasePanel) c, GUIHelper.getStartupScript(c));
    }

    // display frame
    result.setVisible(true);

    return result;
  }

  /**
   * creates a window and returns it.
   *
   * @param title		the title of the frame
   * @param c			the component to place, can be null
   * @param width		the width of the frame, ignored if -1
   * @param height		the height of the frame, ignored if -1
   * @param icon		the icon to use, null for default
   * @return			the generated frame
   */
  protected ChildWindow createChildWindow(String title, Component c, int width, int height, String icon) {
    return createChildWindow(this, title, c, width, height, icon);
  }

  /**
   * creates a window and returns it.
   *
   * @param owner		the owner
   * @param title		the title of the frame
   * @param c			the component to place, can be null
   * @param width		the width of the frame, ignored if -1
   * @param height		the height of the frame, ignored if -1
   * @param icon		the icon to use, null for default
   * @return			the generated frame
   */
  public static ChildWindow createChildWindow(AbstractApplicationFrame owner, String title, Component c, int width, int height, String icon) {
    ChildWindow 		result;
    int 			screenHeight;
    int 			screenWidth;
    ScriptingEngineHandler 	handler;

    result = new ChildWindow(owner, title, icon);

    // layout
    result.setLayout(new BorderLayout());
    if (c != null)
      result.getContentPane().add(c, BorderLayout.CENTER);

    // size
    result.pack();
    if ((width > -1) && (height > -1))
      result.setSize(width, height);
    result.validate();

    // location
    if (owner != null) {
      screenHeight = owner.getGraphicsConfiguration().getBounds().height;
      screenWidth  = owner.getGraphicsConfiguration().getBounds().width;
      result.setLocation(
	  (screenWidth - result.getBounds().width) / 2,
	  (screenHeight - result.getBounds().height) / 2);
    }

    // custom size and location
    if (c != null)
      GUIHelper.setSizeAndLocation(result, c);

    // add listener
    result.addDisposeWindowListener();

    // startup script?
    if ((c != null) && (c instanceof ScriptingEngineHandler) && (c instanceof BasePanel)) {
      handler = (ScriptingEngineHandler) c;
      if (GUIHelper.getStartupScript(c) != null)
	handler.getScriptingEngine().add((BasePanel) c, GUIHelper.getStartupScript(c));
    }

    // display frame
    result.setVisible(true);

    return result;
  }

  /**
   * insert the menu item in a sorted fashion.
   *
   * @param menu	the menu to add the item to
   * @param menuitem	the menu item to add
   */
  protected void insertMenuItem(JMenu menu, JMenuItem menuitem) {
    insertMenuItem(menu, menuitem, 0);
  }

  /**
   * insert the menu item in a sorted fashion.
   *
   * @param menu	the menu to add the item to
   * @param menuitem	the menu item to add
   * @param startIndex	the index in the menu to start with (0-based)
   */
  protected void insertMenuItem(JMenu menu, JMenuItem menuitem, int startIndex) {
    boolean	inserted;
    int		i;
    JMenuItem	current;
    String	currentStr;
    String	newStr;

    inserted = false;
    newStr   = menuitem.getText().toLowerCase();

    // try to find a spot inbetween
    for (i = startIndex; i < menu.getMenuComponentCount(); i++) {
      if (!(menu.getMenuComponent(i) instanceof JMenuItem))
	continue;

      current    = (JMenuItem) menu.getMenuComponent(i);
      currentStr = current.getText().toLowerCase();
      if (currentStr.compareTo(newStr) > 0) {
	inserted = true;
	menu.insert(menuitem, i);
	break;
      }
    }

    // add it at the end if not yet inserted
    if (!inserted)
      menu.add(menuitem);
  }

  /**
   * creates and displays the title.
   *
   * @param title 	the additional part of the title
   */
  public void createTitle(String title) {
    String				newTitle;
    String				name;
    HashSet<AbstractDatabaseConnection>	conns;
    HashSet<String>			connsStr;
    List<String>			connsList;

    newTitle = getApplicationTitle();
    name     = InternetHelper.getLocalHostName();
    if (name != null)
      newTitle += "@" + name;

    conns = AbstractDatabaseConnection.getActiveConnectionObjects();
    if (conns.size() > 0) {
      connsStr = new HashSet<String>();
      for (AbstractDatabaseConnection conn: conns)
	connsStr.add(conn.toStringShort());
      connsList = new ArrayList<String>(connsStr);
      Collections.sort(connsList);
      newTitle += " " + connsList;
    }
    else {
      newTitle += " [-not connected-]";
    }

    if (title.length() != 0) {
      if (title.length() > 50)
	newTitle += " - " + title.substring(0, 50) + "...";
      else
	newTitle += " - " + title;
    }

    setTitle(newTitle);
  }

  /**
   * adds the given child frame to the list of frames.
   *
   * @param c 		the child frame to add
   */
  public void addChildFrame(ChildFrame c) {
    m_Children.add(c);
    windowListChanged();
  }

  /**
   * adds the given child frame to the list of frames.
   *
   * @param c 		the child frame to add
   */
  public void addChildWindow(ChildWindow c) {
    m_Children.add(c);
    windowListChanged();
  }

  /**
   * tries to remove the child frame, it returns true if it could do such.
   *
   * @param c 		the child frame to remove
   * @return 		true if the child frame could be removed
   */
  public boolean removeChildFrame(Container c) {
    boolean result = m_Children.remove(c);
    windowListChanged();
    return result;
  }

  /**
   * brings child frame to the top.
   *
   * @param c 		the frame to activate
   * @return 		true if frame was activated
   */
  public boolean showWindow(Child c) {
    boolean	result;

    result = false;

    if (c != null) {
      createTitle(c.getTitle());
      if (c instanceof ChildFrame)
	((ChildFrame) c).setExtendedState(JFrame.NORMAL);
      c.toFront();
      c.requestFocus();
      result = true;
    }

    return result;
  }

  /**
   * brings the first frame to the top that is of the specified
   * window class.
   *
   * @param windowClass	the class to display the first child for
   * @return		true, if a child was found and brought to front
   */
  public boolean showWindow(Class windowClass) {
    return showWindow(getWindow(windowClass));
  }

  /**
   * returns all currently open frames.
   *
   * @return 		an iterator over all currently open frame
   */
  public Iterator<Child> getWindowList() {
    return m_Children.iterator();
  }

  /**
   * returns the first instance of the given window class, null if none can be
   * found.
   *
   * @param windowClass	the class to retrieve the first instance for
   * @return		null, if no instance can be found
   */
  public Child getWindow(Class windowClass) {
    Child		result;
    Iterator<Child>	iter;
    Child		current;

    result = null;
    iter   = getWindowList();
    while (iter.hasNext()) {
      current = iter.next();
      if (current.getClass() == windowClass) {
        result = current;
        break;
      }
    }

    return result;
  }

  /**
   * returns the first window with the given title, null if none can be
   * found.
   *
   * @param title	the title to look for
   * @return		null, if no instance can be found
   */
  public Child getWindow(String title) {
    Child		result;
    Iterator<Child>	iter;
    Child		current;
    boolean		found;

    result = null;
    iter   = getWindowList();
    while (iter.hasNext()) {
      current = iter.next();
      found   = current.getTitle().equals(title);

      if (found) {
        result = current;
        break;
      }
    }

    return result;
  }

  /**
   * checks, whether an instance of the given window class is already in
   * the Window list.
   *
   * @param windowClass	the class to check for an instance in the current
   * 			window list
   * @return		true if the class is already listed in the Window list
   */
  public boolean containsWindow(Class windowClass) {
    return (getWindow(windowClass) != null);
  }

  /**
   * checks, whether a window with the given title is already in
   * the Window list.
   *
   * @param title	the title to check for in the current window list
   * @return		true if a window with the given title is already
   * 			listed in the Window list
   */
  public boolean containsWindow(String title) {
    return (getWindow(title) != null);
  }

  /**
   * minimizes all windows.
   */
  public void minimizeWindows() {
    Iterator<Child>	iter;
    Child		child;

    iter = getWindowList();
    while (iter.hasNext()) {
      child = iter.next();
      try {
	if (child instanceof ChildFrame)
	  ((ChildFrame) child).setExtendedState(JFrame.ICONIFIED);
      }
      catch (Exception e) {
	e.printStackTrace();
      }
    }
  }

  /**
   * restores all windows.
   */
  public void restoreWindows() {
    Iterator<Child>	iter;
    Child		child;

    iter = getWindowList();
    while (iter.hasNext()) {
      child = iter.next();
      try {
	if (child instanceof ChildFrame)
	  ((ChildFrame) child).setExtendedState(JFrame.NORMAL);
    }
      catch (Exception e) {
	e.printStackTrace();
      }
    }
  }

  /**
   * is called when window list changed somehow (add or remove).
   */
  public void windowListChanged() {
    Runnable run = new Runnable() {
      @Override
      public void run() {
	buildWindowsMenu();
      }
    };
    SwingUtilities.invokeLater(run);
  }

  /**
   * creates the menu of currently open windows.
   */
  protected void buildWindowsMenu() {
    Iterator<Child>	iter;
    JMenuItem		menuitem;
    int			startIndex;
    List<Image>		images;
    boolean		useEmpty;
    JMenu		submenu;

    // remove all existing entries
    m_MenuWindows.removeAll();

    // minimize + restore + separator
    menuitem = new JMenuItem("Minimize");
    menuitem.setIcon(GUIHelper.getIcon("minimize.png"));
    menuitem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent evt) {
        minimizeWindows();
      }
    });
    m_MenuWindows.add(menuitem);

    menuitem = new JMenuItem("Restore");
    menuitem.setIcon(GUIHelper.getIcon("maximize.png"));
    menuitem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent evt) {
        restoreWindows();
      }
    });
    m_MenuWindows.add(menuitem);

    submenu = new JMenu("User mode");
    for (final UserMode um: UserMode.values()) {
      menuitem = new JMenuItem(um.toString());
      menuitem.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
	  setUserMode(um);
	}
      });
      submenu.add(menuitem);
    }

    m_MenuWindows.addSeparator();

    // windows
    startIndex = m_MenuWindows.getMenuComponentCount() - 1;
    iter       = getWindowList();
    m_MenuWindows.setVisible(iter.hasNext());
    while (iter.hasNext()) {
      Child child = iter.next();
      menuitem = new JMenuItem(child.getTitle());
      useEmpty = true;
      if (child instanceof Window) {
	images = ((Window) child).getIconImages();
	if (images.size() > 0) {
	  useEmpty = false;
	  menuitem.setIcon(new ImageIcon(images.get(0)));
	}
      }
      if (useEmpty)
	menuitem.setIcon(GUIHelper.getEmptyIcon());
      insertMenuItem(m_MenuWindows, menuitem, startIndex);
      menuitem.setActionCommand(Integer.toString(child.hashCode()));
      menuitem.addActionListener(new ActionListener() {
        @Override
	public void actionPerformed(ActionEvent evt) {
          Iterator<Child> iter = getWindowList();
          while (iter.hasNext()) {
            Child child = iter.next();
            String hashFrame = Integer.toString(child.hashCode());
            if (hashFrame.equals(evt.getActionCommand())) {
              showWindow(child);
              break;
            }
          }
        }
      });
    }
  }

  /**
   * Shows or hides this component depending on the value of parameter b.
   *
   * @param b		if true, shows this component; otherwise, hides this
   * 			component
   */
  @Override
  public void setVisible(boolean b) {
    super.setVisible(b);

    if (b)
      repaint();
  }

  /**
   * Returns the currently used database connection object, can be null.
   *
   * @return		the current object
   */
  @Override
  public AbstractDatabaseConnection getDatabaseConnection() {
    return m_DbConn;
  }

  /**
   * Sets the database connection object to use.
   *
   * @param value	the object to use
   */
  @Override
  public void setDatabaseConnection(AbstractDatabaseConnection value) {
    m_DbConn = value;
  }

  /**
   * A change in the database connection occurred.
   *
   * @param e		the event
   */
  @Override
  public void databaseConnectionStateChanged(DatabaseConnectionChangeEvent e) {
    createTitle("");
    if (e.getType() == EventType.CONNECT)
      m_DbConn = e.getDatabaseConnection();
  }

  /**
   * Starts up any menu items that were defined.
   *
   * @see		#m_StartUps
   */
  protected void startUpMenuItems() {
    int						i;
    AbstractMenuItemDefinition			item;
    final List<AbstractMenuItemDefinition>	items;
    Runnable					runnable;

    // collect menu items
    items = new ArrayList<AbstractMenuItemDefinition>();
    for (i = 0; i < m_StartUps.length; i++) {
      item = AbstractMenuItemDefinition.forCommandLine(this, m_StartUps[i].toString());
      if (getAppMenu().isBlacklisted(item.getClass())) {
	getLogger().severe(
	    item.getClass() + " is blacklisted and cannot be displayed!");
	continue;
      }
      if (getUserMode().compareTo(item.getUserMode()) < 0) {
	getLogger().severe(
	    item.getClass() + " requires at least user mode '"
	    + getAppMenu().getUserMode() + "' (current: '" + getUserMode() + "')!");
	continue;
      }
      items.add(item);
    }

    // start up menu items
    if (items.size() > 0) {
      runnable = new Runnable() {
	@Override
	public void run() {
	  for (int i = 0; i < items.size(); i++)
	    items.get(i).launch();
	}
      };
      SwingUtilities.invokeLater(runnable);
    }
  }

  /**
   * Returns the scripting log panel instance.
   *
   * @return		the panel
   */
  public ScriptingLogPanel getScriptingLogPanel() {
    return m_ScriptingLogPanel;
  }

  /**
   * Runs the application from the commandline.
   *
   * @param env		the environment class to use
   * @param app		the application frame class
   * @param options	the commandline options
   * @return		the instantiated frame, null in case of an error or
   * 			invocation of help
   */
  public static AbstractApplicationFrame runApplication(Class env, Class app, String[] options) {
    AbstractApplicationFrame	result;

    Environment.setEnvironmentClass(env);
    LoggingHelper.useHandlerFromOptions(options);

    try {
      if (OptionUtils.helpRequested(options)) {
	System.out.println("Help requested...\n");
	result = forName(app.getName(), new String[0]);
	System.out.print("\n" + OptionUtils.list(result));
	LoggingHelper.outputHandlerOption();
	ScriptingEngine.stopAllEngines();
	result.dispose();
	result = null;
      }
      else {
	result = forName(app.getName(), options);
	Environment.getInstance().setApplicationFrame(result);
	if (result.getDatabaseConnection().isConnected())
	  AbstractIndexedTable.initTables(result.getDatabaseConnection());
	result.getLogger().info("PID: " + ProcessUtils.getVirtualMachinePID());
	result.setVisible(true);
	result.startUpMenuItems();
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Instantiates the application frame with the given options.
   *
   * @param classname	the classname of the application frame to instantiate
   * @param options	the options for the application frame
   * @return		the instantiated application frame or null if an error occurred
   */
  public static AbstractApplicationFrame forName(String classname, String[] options) {
    AbstractApplicationFrame	result;

    try {
      result = (AbstractApplicationFrame) OptionUtils.forName(AbstractApplicationFrame.class, classname, options);
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Instantiates the application frame from the given commandline
   * (i.e., classname and optional options).
   *
   * @param cmdline	the classname (and optional options) of the
   * 			application frame to instantiate
   * @return		the instantiated application frame
   * 			or null if an error occurred
   */
  public static AbstractApplicationFrame forCommandLine(String cmdline) {
    return (AbstractApplicationFrame) AbstractOptionConsumer.fromString(ArrayConsumer.class, cmdline);
  }
}
