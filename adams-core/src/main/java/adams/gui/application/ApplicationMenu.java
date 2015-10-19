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
 * ApplicationMenu.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.application;

import adams.core.Properties;
import adams.core.logging.LoggingObject;
import adams.gui.core.GUIHelper;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;

/**
 * Generates the menu for the application frame.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ApplicationMenu
  extends LoggingObject
  implements Serializable {

  /** for serialization. */
  private static final long serialVersionUID = 7821913342345552227L;

  /** the props file key for the menu bar. */
  public final static String LAYOUT_MENUBAR = "MenuBar";

  /** the props file key for the automatic menu item discovery. */
  public final static String LAYOUT_AUTOMATICDISCOVERY = "AutomaticMenuItemDiscovery";

  /** the props file key prefix for the menus. */
  public final static String LAYOUT_MENU_PREFIX = "Menu-";

  /** the props file key prefix for the shortcuts. */
  public final static String LAYOUT_SHORTCUT_PREFIX = "Shortcut-";

  /** the props file key prefix for the blacklisted menu items. */
  public final static String LAYOUT_BLACKLISTED_PREFIX = "Blacklisted-";

  /** the props file key for the windows menu. */
  public final static String LAYOUT_MENU_WINDOWS = "Windows";

  /** the props file key for the tools menu. */
  public final static String LAYOUT_MENU_TOOLS = "Tools";

  /** the separator between classname and alternative title. */
  public final static String SEPARATOR = "#";

  /** stores classnames that aren't available. */
  protected static HashSet<String> m_UnavailableMenuItems;
  
  /** the parent application frame. */
  protected AbstractApplicationFrame m_Owner;

  /** the file with the layout, shortcuts and blacklisted menu items. */
  protected String m_Setup;

  /** the properties read from the setup file. */
  protected Properties m_Properties;

  /** the user mode. */
  protected UserMode m_UserMode;

  /** the "windows" menu. */
  protected JMenu m_MenuWindows;

  /**
   * Initializes menu generator.
   *
   * @param owner	the owning application frame
   */
  public ApplicationMenu(AbstractApplicationFrame owner) {
    super();

    if (m_UnavailableMenuItems == null)
      m_UnavailableMenuItems = new HashSet<String>();
    
    m_Owner       = owner;
    m_Setup       = null;
    m_UserMode    = UserMode.BASIC;
    m_MenuWindows = null;
    m_Properties  = null;
  }

  /**
   * Returns the owning frame.
   *
   * @return		the owner
   */
  public AbstractApplicationFrame getOwner() {
    return m_Owner;
  }

  /**
   * Sets the setup file with the layout, etc.
   *
   * @param value	the setup file
   */
  public void setSetup(String value) {
    m_Setup = value;
  }

  /**
   * Returns the setup file with the layout, etc.
   *
   * @return		the setup file
   */
  public String getSetup() {
    return m_Setup;
  }

  /**
   * Sets the user mode to use, i.e., the visibility level of menu items.
   *
   * @param value	the user mode
   */
  public void setUserMode(UserMode value) {
    m_UserMode = value;
  }

  /**
   * Returns the current user mode, i.e., the visibility level of menu items.
   *
   * @return		the user mode
   */
  public UserMode getUserMode() {
    return m_UserMode;
  }

  /**
   * Returns the properties.
   *
   * @return		the properties
   */
  protected synchronized Properties getProperties() {
    if (m_Properties == null) {
      try {
	m_Properties = Properties.read(m_Setup);
      }
      catch (Exception e) {
	throw new IllegalStateException("Failed to load menu setup file!", e);
      }
    }

    return m_Properties;
  }

  /**
   * Determines the lowest index of the menu items in the given menu.
   *
   * @param items	the items to look for in the menu (case-insensitive)
   * @param menu	the menu to look through
   * @return		the lowest index, -1 if none was found
   */
  protected int determineItemIndex(String[] items, JMenu menu) {
    int		result;
    int		i;
    String	text;

    result = -1;

    for (i = 0; i < items.length; i++)
      items[i] = items[i].toLowerCase();

    for (i = 0; i < menu.getMenuComponentCount(); i++) {
      if (menu.getMenuComponent(i) instanceof JMenuItem) {
	text = ((JMenuItem) menu.getMenuComponent(i)).getText().toLowerCase();
	for (String item: items) {
	  if (text.matches(item)) {
	    result = i;
	    break;
	  }
	}
      }
      if (result != -1)
	break;
    }

    return result;
  }

  /**
   * Generates the menu and returns it.
   *
   * @return		the menu bar
   */
  public JMenuBar getMenuBar() {
    JMenuBar		result;
    JMenu[]		menus;
    JMenuItem		menuitem;
    AbstractMenuItemDefinition	definition;
    int			i;
    int			n;
    Properties		props;
    String[]		items;
    char[]		mnemonics;
    Class		cls;
    Constructor		constr;
    String		shortcut;
    String		classname;
    boolean		addSeparator;
    boolean		autoDiscovery;
    boolean[]		separators;
    boolean		added;
    JMenu		menu;
    HashSet<String>	listAdded;
    HashSet<String>	additional;
    List<String>	additionalList;
    Enumeration<String>	keys;
    String		key;
    int			indexToolsMenu;
    int			indexLast;
    String		title;

    if (m_Setup == null)
      throw new IllegalStateException("No menu setup file provided!");

    result = new JMenuBar();

    // create the menus
    props            = getProperties();
    autoDiscovery    = props.getBoolean(LAYOUT_AUTOMATICDISCOVERY, false);
    additional       = new HashSet<String>(Arrays.asList(AbstractMenuItemDefinition.getMenuItemDefinitions()));
    items            = props.getProperty(LAYOUT_MENUBAR, "").split(",");
    mnemonics        = GUIHelper.getMnemonics(items);
    menus            = new JMenu[items.length];
    separators       = new boolean[items.length];
    indexToolsMenu   = -1;
    listAdded        = new HashSet<>();
    for (i = 0; i < items.length; i++) {
      if (items[i].length() == 0)
	continue;
      menus[i] = new JMenu(items[i]);
      menus[i].setMnemonic(mnemonics[i]);
      menus[i].setVisible(false);
      if (items[i].equals(LAYOUT_MENU_WINDOWS)) {
	m_MenuWindows = menus[i];
	m_MenuWindows.setVisible(false);  // initially, no windows visible
      }
      if (items[i].equals(LAYOUT_MENU_TOOLS))
	indexToolsMenu = i;
      result.add(menus[i]);
    }

    // populate menu with menu items
    for (i = 0; i < menus.length; i++) {
      items        = props.getProperty(LAYOUT_MENU_PREFIX + menus[i].getText(), "").split(",");
      addSeparator = false;
      for (n = 0; n < items.length; n++) {
	if (items[n].length() == 0)
	  continue;
	if (isBlacklisted(items[n]))
	  continue;
	if (isUnavailable(items[n]))
	  continue;
	if (items[n].equals("-")) {
	  addSeparator = true;
	}
	else {
	  try {
	    classname = items[n];
	    title     = null;
	    if (listAdded.contains(classname))
	      continue;
	    listAdded.add(classname);
	    if (classname.contains(SEPARATOR)) {
	      title     = classname.substring(classname.indexOf(SEPARATOR) + 1);
	      classname = classname.substring(0, classname.indexOf(SEPARATOR));
	    }
	    additional.remove(classname);
	    if (isUnavailable(classname))
	      continue;
	    shortcut  = null;
	    if (props.hasKey(LAYOUT_SHORTCUT_PREFIX + items[n]))
	      shortcut = props.getProperty(LAYOUT_SHORTCUT_PREFIX + items[n]);
	    try {
	      cls = Class.forName(classname);
	    }
	    catch (ClassNotFoundException e) {
	      getLogger().severe("Menu item not found, skipping: " + classname);
	      setUnavailable(classname);
	      continue;
	    }
	    constr     = cls.getConstructor(AbstractApplicationFrame.class);
	    definition = (AbstractMenuItemDefinition) constr.newInstance(getOwner());
	    if (m_UserMode.compareTo(definition.getUserMode()) < 0)
	      continue;
	    if (definition.requiresRestartableApplication() && !m_Owner.getEnableRestart())
	      continue;
	    menus[i].setVisible(true);
	    menuitem = definition.getMenuItem();
	    if (menuitem == null)
	      continue;
	    if (!(menuitem instanceof JMenu)) {
	      if (shortcut != null)
		menuitem.setAccelerator(GUIHelper.getKeyStroke(shortcut));
	      else
		menuitem.setAccelerator(null);
	    }
	    if (title != null)
	      menuitem.setText(title);
	    if (addSeparator)
	      menus[i].addSeparator();
	    menus[i].add(menuitem);
	    addSeparator = false;
	  }
	  catch (Exception e) {
	    getLogger().log(Level.SEVERE,
		"Error processing menu item '" + items[n] + "' "
		+ "for menu '" + menus[i].getText() + "':", e);
	  }
	}
      }
      // set mnemonics
      items = new String[menus[i].getItemCount()];
      for (n = 0; n < menus[i].getItemCount(); n++) {
	if (menus[i].getItem(n) instanceof JMenuItem)
	  items[n] = menus[i].getItem(n).getText();
	else
	  items[n] = "";
      }
      mnemonics = GUIHelper.getMnemonics(items);
      for (n = 0; n < menus[i].getItemCount(); n++) {
	if (menus[i].getItem(n) instanceof JMenuItem)
	  menus[i].getItem(n).setMnemonic(mnemonics[n]);
      }
    }

    // additional menu items found?
    if (autoDiscovery) {
      keys = props.propertyNames(LAYOUT_BLACKLISTED_PREFIX + ".*");

      // remove blacklisted menu items
      while (keys.hasMoreElements()) {
	key = keys.nextElement();
	if (props.getBoolean(key, false)) {
	  classname = key.substring(LAYOUT_BLACKLISTED_PREFIX.length());
	  additional.remove(classname);
	}
      }

      // add items according to their category
      additionalList = new ArrayList<String>(additional);
      Collections.sort(additionalList);
      for (String add: additionalList) {
	try {
	  cls      = Class.forName(add);
	  constr   = cls.getConstructor(AbstractApplicationFrame.class);
	  definition  = (AbstractMenuItemDefinition) constr.newInstance(getOwner());
	  if (m_UserMode.compareTo(definition.getUserMode()) < 0)
	    continue;
	  menuitem = definition.getMenuItem();
	  added    = false;
	  for (i = 0; i < menus.length; i++) {
	    if (menus[i].getText().equals(definition.getCategory())) {
	      added = true;
	      menus[i].setVisible(true);
	      // check for "Exit", "Restart" or "Close" (these are always last!)
	      if (menus[i].getMenuComponentCount() > 0) {
		indexLast = determineItemIndex(new String[]{"restart.*", "exit", "close"}, menus[i]);
		if (indexLast != -1) {
		  menus[i].insertSeparator(indexLast);
		  menus[i].insert(menuitem, indexLast);
		  continue;
		}
	      }
	      if (!separators[i] && menus[i].getMenuComponentCount() > 0) {
		menus[i].addSeparator();
		separators[i] = true;
	      }
	      menus[i].add(menuitem);
	    }
	  }
	  // add new menu
	  if (!added) {
	    for (i = 0; i < result.getMenuCount(); i++) {
	      if (result.getMenu(i).getText().equals(definition.getCategory())) {
		added = true;
		result.getMenu(i).add(menuitem);
	      }
	    }
	    // no menu/category added yet?
	    if (!added) {
	      menu = new JMenu(definition.getCategory());
	      menu.add(menuitem);
	      if (indexToolsMenu == -1) {
		result.add(menu);
	      }
	      else {
		result.add(menu, indexToolsMenu);
		indexToolsMenu++;
	      }
	      added = true;
	    }
	  }
	}
	catch (Exception e) {
	    getLogger().log(Level.SEVERE,
		"Error processing additional menu item '" + add + "':", e);
	}
      }
    }

    return result;
  }

  /**
   * Sets whether the class is blacklisted or not.
   *
   * @param cls		the class to blacklist/whitelist
   * @param value	true if to blacklist
   */
  public void setBlacklisted(Class cls, boolean value) {
    setBlacklisted(cls.getName(), value);
  }

  /**
   * Sets whether the class is blacklisted or not.
   *
   * @param classname	the class to blacklist/whitelist
   * @param value	true if to blacklist
   */
  public void setBlacklisted(String classname, boolean value) {
    String	key;
    Properties	props;

    props  = getProperties();
    key    = LAYOUT_BLACKLISTED_PREFIX + classname;
    props.setBoolean(key, value);
  }

  /**
   * Checks whether the class is blacklisted.
   *
   * @param cls		the class to check
   * @return		true if blacklisted
   */
  public boolean isBlacklisted(Class cls) {
    return isBlacklisted(cls.getName());
  }

  /**
   * Checks whether the class is blacklisted.
   *
   * @param classname	the class to check
   * @return		true if blacklisted
   */
  public boolean isBlacklisted(String classname) {
    boolean	result;
    String	key;
    Properties	props;

    props  = getProperties();
    key    = LAYOUT_BLACKLISTED_PREFIX + classname;
    result = props.getBoolean(key, false);

    return result;
  }
  
  /**
   * Checks whether the class is unavailable (ie not found in classpath),
   * based on previous menu generation.
   * 
   * @param cls		the class to check
   * @return		true if unavailable
   */
  public boolean isUnavailable(Class cls) {
    return isUnavailable(cls.getName());
  }
  
  /**
   * Checks whether the class is unavailable (ie not found in classpath),
   * based on previous menu generation.
   * 
   * @param classname	the name to check
   * @return		true if unavailable
   */
  public boolean isUnavailable(String classname) {
    return m_UnavailableMenuItems.contains(classname);
  }
  
  /**
   * Marks this class as unavailable.
   * 
   * @param cls		the class to mark as unavailable
   */
  public void setUnavailable(Class cls) {
    setUnavailable(cls.getName());
  }
  
  /**
   * Marks this class as unavailable.
   * 
   * @param classname	the name to mark as unavailable
   */
  public void setUnavailable(String classname) {
    m_UnavailableMenuItems.add(classname);
  }

  /**
   * Returns the "Windows" menu from the last "getMenuBar" call.
   *
   * @return		the "Windows" menu
   * @see		#getMenuBar()
   */
  public JMenu getWindowsMenu() {
    return m_MenuWindows;
  }
}
