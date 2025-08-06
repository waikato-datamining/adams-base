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
 * StringFavorites.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;

import adams.core.CloneHandler;
import adams.core.Properties;
import adams.core.classmanager.ClassManager;
import adams.core.logging.LoggingObject;
import adams.env.Environment;
import adams.gui.dialog.ApprovalDialog;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;

/**
 * A helper class for managing simple favorites.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class StringFavorites
  extends LoggingObject
  implements Comparable, CloneHandler<StringFavorites> {

  private static final long serialVersionUID = -6275908166147814829L;

  /**
   * Container class for a favorite setup.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   */
  public static class StringFavorite
    extends LoggingObject
    implements Comparable, CloneHandler<StringFavorite> {

    /** for serialization. */
    private static final long serialVersionUID = 9155308607371430795L;

    /** the name of the favorite. */
    protected String m_Name;

    /** the data of the favorite. */
    protected String m_Data;

    /**
     * Initializes the favorite.
     *
     * @param name	the name of the favorite
     * @param data	the commandline of the favorite
     */
    public StringFavorite(String name, String data) {
      super();

      if (name == null)
	throw new IllegalArgumentException("Name of favorite cannot be null!");
      if (data == null)
	throw new IllegalArgumentException("Data for favorite cannot be null!");

      m_Name = name;
      m_Data = data;

      configureLogger();
    }

    /**
     * Returns the name of the favorite.
     *
     * @return		the name
     */
    public String getName() {
      return m_Name;
    }

    /**
     * Returns the data of the favorite.
     *
     * @return		the data
     */
    public String getData() {
      return m_Data;
    }

    /**
     * Returns a copy of itself.
     *
     * @return		the copy
     */
    public StringFavorite getClone() {
      return new StringFavorite(m_Name, m_Data);
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * @param   o the object to be compared.
     * @return  a negative integer, zero, or a positive integer as this object
     *		is less than, equal to, or greater than the specified object.
     * @throws ClassCastException if the specified object's type prevents it
     *         from being compared to this object.
     */
    public int compareTo(Object o) {
      int	result;
      StringFavorite f;

      if (o == null)
        return 1;

      if (!(o instanceof StringFavorite))
	return -1;

      f = (StringFavorite) o;

      result = getName().toLowerCase().compareTo(f.getName().toLowerCase());
      if (result == 0)
	result = getData().compareTo(f.getData());

      return result;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj	the reference object with which to compare.
     * @return		true if this object is the same as the obj argument;
     * 			false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
      return (compareTo(obj) == 0);
    }

    /**
     * Hashcode so can be used as hashtable key. Returns the hashcode of the
     * name string.
     *
     * @return		the hashcode, -1 if name is null
     */
    @Override
    public int hashCode() {
      if (m_Name == null)
	return -1;
      else
	return m_Name.hashCode();
    }

    /**
     * Returns the name of the favorite.
     *
     * @return		the name
     */
    @Override
    public String toString() {
      return m_Name;
    }
  }

  /**
   * Event that gets sent when a favorite gets selected.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   */
  public static class StringFavoriteSelectionEvent
    extends EventObject {

    /** for serialization. */
    private static final long serialVersionUID = -3355442271698515292L;

    /** the selected favorite. */
    protected StringFavorite m_Favorite;

    /**
     * Initializes the event.
     *
     * @param source	the object that triggered the event
     * @param favorite	the selected favorite
     */
    public StringFavoriteSelectionEvent(Object source, StringFavorite favorite) {
      super(source);

      m_Favorite = favorite;
    }

    /**
     * Returns the selected favorite.
     *
     * @return		the favorite
     */
    public StringFavorite getFavorite() {
      return m_Favorite;
    }
  }

  /**
   * Interface for classes that listen to selections of favorites.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   */
  public static interface StringFavoriteSelectionListener {

    /**
     * Gets called when a favorite gets selected.
     *
     * @param e		the event
     */
    public void favoriteSelected(StringFavoriteSelectionEvent e);
  }

  /** the filename of the props file. */
  public final static String FILENAME = "StringFavorites.props";

  /** the separator between classname and favorite name. */
  public final static String SEPARATOR = "-";

  /** the properties. */
  protected Properties m_Properties;

  /** the temporary favorites. */
  protected Properties m_TempProperties;

  /** whether the favorites were modified. */
  protected boolean m_Modified;

  /** whether to save the properties whenever a change happened. */
  protected boolean m_AutoSave;

  /** the singleton. */
  protected static StringFavorites m_Singleton;

  /**
   * Initializes the favorites with immediate saving enabled.
   */
  public StringFavorites() {
    this(true);
  }

  /**
   * Initializes the favorites.
   *
   * @param autoSave	whether to save the favorites immediately
   * 			whenever modified
   */
  public StringFavorites(boolean autoSave) {
    super();

    m_AutoSave       = autoSave;
    m_Modified       = false;
    m_TempProperties = new Properties();
  }

  /**
   * Sets auto save to on or off.
   *
   * @param value	if true then auto save is on
   */
  public void setAutoSave(boolean value) {
    m_AutoSave = value;
  }

  /**
   * Returns whether the favorites get immediately saved whenever modified.
   *
   * @return		true if auto-save is on
   */
  public boolean isAutoSave() {
    return m_AutoSave;
  }

  /**
   * Sets the modified state.
   *
   * @param value	the modified state
   */
  public void setModified(boolean value) {
    m_Modified = value;
  }

  /**
   * Returns whether the favorites are modified.
   *
   * @return		true if modified
   */
  public boolean isModified() {
    return m_Modified;
  }

  /**
   * Sets the properties containing the favorites to use.
   *
   * @param value	the properties to use
   */
  public void setProperties(Properties value) {
    m_Properties = (Properties) value.clone();
  }

  /**
   * Returns the properties of the favorites.
   *
   * @return		the properties
   */
  public synchronized Properties getProperties() {
    if (m_Properties == null) {
      load();
      if (m_AutoSave)
	updateFavorites();
    }

    return m_Properties;
  }

  /**
   * Returns the properties of the temp favorites.
   *
   * @return		the properties
   */
  protected Properties getTempProperties() {
    return m_TempProperties;
  }

  /**
   * Loads the favorites.
   */
  protected synchronized void load() {
    File	file;

    m_Properties = new Properties();
    file         = new File(Environment.getInstance().getHome() + File.separator + FILENAME);
    if (file.exists())
      m_Properties.load(file.getAbsolutePath());
  }

  /**
   * Removes all favorites.
   */
  public void clear() {
    getProperties().clear();
    m_Modified = true;
    if (m_AutoSave)
      updateFavorites();
  }

  /**
   * Returns the favorites for the specified class.
   *
   * @param props 	the properties to extract the favorites from
   * @param cls		the class to get the favorites for
   * @return		the favorites
   */
  protected List<StringFavorite> getFavorites(Properties props, Class cls) {
    List<StringFavorite>	result;
    Enumeration<String>	enm;
    String		key;
    String		prefix;
    StringFavorite favorite;

    result = new ArrayList<>();
    prefix = createKey(cls);
    enm    = (Enumeration<String>) props.propertyNames();
    while (enm.hasMoreElements()) {
      key = enm.nextElement();
      if (key.startsWith(prefix)) {
	favorite = new StringFavorite(key.substring(prefix.length()), props.getProperty(key));
	result.add(favorite);
      }
    }

    Collections.sort(result);

    return result;
  }

  /**
   * Returns the favorites for the specified class.
   *
   * @param cls		the class to get the favorites for
   * @return		the favorites
   */
  public List<StringFavorite> getFavorites(Class cls) {
    return getFavorites(getProperties(), cls);
  }

  /**
   * Returns the temporary favorites for the specified class.
   *
   * @param cls		the class to get the favorites for
   * @return		the favorites
   */
  public List<StringFavorite> getTempFavorites(Class cls) {
    return getFavorites(getTempProperties(), cls);
  }

  /**
   * Returns all the current superclasses.
   *
   * @return		the classnames of the superclasses
   */
  public List<String> getSuperclasses() {
    List<String>	result;
    HashSet<String>	set;
    Enumeration<String>	enm;
    String		key;
    String		classname;

    set = new HashSet<>();
    enm = (Enumeration<String>) getProperties().propertyNames();
    while (enm.hasMoreElements()) {
      key       = enm.nextElement();
      classname = key.substring(0, key.indexOf(SEPARATOR));
      set.add(classname);
    }

    result = new ArrayList<>(set);
    Collections.sort(result);

    return result;
  }

  /**
   * Fixes the name by replacing all "|" with "/".
   * 
   * @param name	the name to fix
   * @return		the fixed name
   */
  protected String fixName(String name) {
    return name.replace("|", "/");
  }

  /**
   * Generates a properties key prefix for the class.
   *
   * @param cls		the class to generate the key for
   * @return		the key
   */
  protected String createKey(Class cls) {
    return createKey(cls, "");
  }

  /**
   * Generates a properties key for the class/name.
   *
   * @param cls		the class to generate the key for
   * @param name 	the name to generete the key for
   * @return		the key
   */
  protected String createKey(Class cls, String name) {
    if (cls.isArray())
      return cls.getComponentType().getName() + "[]" + SEPARATOR + fixName(name);
    else
      return cls.getName() + SEPARATOR + fixName(name);
  }

  /**
   * Returns the named favorite for the specified class.
   *
   * @param cls		the class to get the favorite for
   * @param name	the name of the favorite
   * @return		the favorite, null if not available
   */
  public StringFavorite getFavorite(Class cls, String name) {
    String	key;

    key = createKey(cls, name);
    if (getProperties().hasKey(key))
      return new StringFavorite(name, getProperties().getProperty(key));
    else
      return null;
  }

  /**
   * Returns the named temporary favorite for the specified class.
   *
   * @param cls		the class to get the favorite for
   * @param name	the name of the favorite
   * @return		the favorite, null if not available
   */
  public StringFavorite getTempFavorite(Class cls, String name) {
    String	key;

    key = createKey(cls, name);
    if (getTempProperties().hasKey(key))
      return new StringFavorite(name, getTempProperties().getProperty(key));
    else
      return null;
  }

  /**
   * Adds a favorite for a class.
   *
   * @param cls		the class to add the favorite for
   * @param data	the favorite
   * @param name	the name of the favorite
   */
  protected void addFavorite(Properties props, Class cls, String data, String name) {
    String	key;

    key = createKey(cls, name);
    props.setProperty(key, data);
    m_Modified = true;
    if (m_AutoSave)
      updateFavorites();
  }

  /**
   * Adds a favorite for a class.
   *
   * @param cls		the class to add the favorite for
   * @param data	the favorite
   * @param name	the name of the favorite
   */
  public void addFavorite(Class cls, String data, String name) {
    addFavorite(getProperties(), cls, data, name);
    m_Modified = true;
    if (m_AutoSave)
      updateFavorites();
  }

  /**
   * Adds a temporary favorite for a class.
   *
   * @param cls		the class to add the favorite for
   * @param data	the favorite
   * @param name	the name of the favorite
   */
  public void addTempFavorite(Class cls, String data, String name) {
    addFavorite(getTempProperties(), cls, data, name);
  }

  /**
   * Removes the favorites for the specified class.
   *
   * @param classname	the class to remove the favorites for
   */
  public void removeFavorites(String classname) {
    Class	cls;

    try {
      if (classname.endsWith("[]"))
	cls = ClassManager.getSingleton().forName(classname.substring(0, classname.length() - 2));
      else
	cls = ClassManager.getSingleton().forName(classname);
      removeFavorites(cls);
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to remove favorites for: " + classname, e);
    }
  }

  /**
   * Removes the favorites for the specified class.
   *
   * @param cls		the class to remove the favorites for
   */
  public void removeFavorites(Class cls) {
    List<StringFavorite>	favorites;
    int				i;
    String			key;

    favorites = getFavorites(cls);
    key       = createKey(cls);
    for (i = 0; i < favorites.size(); i++)
      getProperties().removeKey(key + favorites.get(i).getName());

    m_Modified = true;

    if (m_AutoSave)
      updateFavorites();
  }

  /**
   * Removes a favorite for a class.
   *
   * @param cls		the class to remove the favorite for
   * @param name	the name of the favorite
   */
  public void removeFavorite(Class cls, String name) {
    getProperties().removeKey(createKey(cls, name));
    m_Modified = true;
    if (m_AutoSave)
      updateFavorites();
  }

  /**
   * Removes a temporary favorite for a class.
   *
   * @param cls		the class to remove the favorite for
   * @param name	the name of the favorite
   */
  public void removeTempFavorite(Class cls, String name) {
    getTempProperties().removeKey(createKey(cls, name));
  }

  /**
   * Returns the filename the favorites will be saved as.
   *
   * @return		the filename
   */
  public String getFilename() {
    return Environment.getInstance().getHome() + File.separator + FILENAME;
  }

  /**
   * Updates the favorites, i.e., stores the properties on disk.
   *
   * @return		true if successfully saved
   */
  public synchronized boolean updateFavorites() {
    boolean	result;
    String	filename;

    filename = getFilename();
    if (!m_Properties.save(filename)) {
      result = false;
      System.err.println("Error saving GOE favorites to '" + filename + "'!");
    }
    else {
      result     = true;
      m_Modified = false;
    }

    return result;
  }

  /**
   * Adds the menuitem to the menu.
   *
   * @param menu	the JMenu or JPopupMenu to add to
   * @param menuitem	the item to add
   */
  protected void addMenuItem(Object menu, JMenuItem menuitem) {
    if (menu instanceof JPopupMenu)
      ((JPopupMenu) menu).add(menuitem);
    else if (menu instanceof JMenu)
      ((JMenu) menu).add(menuitem);
    else
      throw new IllegalArgumentException("Only JMenu and JPopupMenu are accepted!");
  }

  /**
   * Adds a separator to the menu.
   *
   * @param menu	the JMenu or JPopupMenu to add to
   */
  protected void addSeparator(Object menu) {
    if (menu instanceof JPopupMenu)
      ((JPopupMenu) menu).addSeparator();
    else if (menu instanceof JMenu)
      ((JMenu) menu).addSeparator();
    else
      throw new IllegalArgumentException("Only JMenu and JPopupMenu are accepted!");
  }

  /**
   * Prompts the user whether to remove the favorite.
   *
   * @param temp	temporary or permanent favorites
   * @param cls		the class of the favorite
   * @param name	the name of the favorite
   */
  protected void promptRemoval(boolean temp, Class cls, String name) {
    int		retVal;

    retVal = GUIHelper.showConfirmMessage(null, "Do you want to remove favorite '" + name + "'?");
    if (retVal != ApprovalDialog.APPROVE_OPTION)
      return;

    if (temp)
      removeTempFavorite(cls, name);
    else
      removeFavorite(cls, name);
  }

  /**
   * Adds menu items with the favorites (permanent or temporary) to the menu.
   *
   * @param menu	the menu (JMenu/JPopupMenu) to add the favorites to
   * @param cls		the class the favorites are for
   * @param current	the current data associated with the favorite
   * @param listener	the listener to attach to the menu items' ActionListener
   */
  protected void addFavoritesMenuItems(final boolean temp, Object menu, final Class cls, final String current, final StringFavoriteSelectionListener listener) {
    JMenu		managemenu;
    JMenu 		updatemenu;
    JMenu 		removemenu;
    JMenuItem 		item;
    List<StringFavorite> 	favorites;
    int 		i;

    // get favorites
    if (temp)
      favorites = getTempFavorites(cls);
    else
      favorites = getFavorites(cls);

    // manage
    if (temp)
      managemenu = new JMenu("Manage temporary...");
    else
      managemenu = new JMenu("Manage...");
    addMenuItem(menu, managemenu);

    // add
    item = new JMenuItem("Add...");
    item.addActionListener((ActionEvent e) -> {
      String name = GUIHelper.showInputDialog(null, "Please enter name for favorite:");
      if (name == null)
	return;
      name = name.trim();
      if (temp)
	addTempFavorite(cls, current, name);
      else
	addFavorite(cls, current, name);
    });
    addMenuItem(managemenu, item);

    // update
    updatemenu = new JMenu("Update");
    updatemenu.setEnabled(false);  // in case we have no favorites yet
    addMenuItem(managemenu, updatemenu);
    for (i = 0; i < favorites.size(); i++) {
      final StringFavorite favorite = favorites.get(i);
      String name = favorite.getName();
      updatemenu.setEnabled(true);
      item = new JMenuItem(name);
      if (temp)
	item.addActionListener((ActionEvent e) -> addTempFavorite(cls, current, favorite.getName()));
      else
	item.addActionListener((ActionEvent e) -> addFavorite(cls, current, favorite.getName()));
      updatemenu.add(item);
    }

    // remove
    removemenu = new JMenu("Remove");
    removemenu.setEnabled(false);  // in case we have no favorites yet
    addMenuItem(managemenu, removemenu);
    for (i = 0; i < favorites.size(); i++) {
      final StringFavorite favorite = favorites.get(i);
      String name = favorite.getName();
      removemenu.setEnabled(true);
      item = new JMenuItem(name);
      item.addActionListener((ActionEvent e) -> promptRemoval(temp, cls, favorite.getName()));
      removemenu.add(item);
    }

    // current favorites
    for (i = 0; i < favorites.size(); i++) {
      if (i == 0)
	addSeparator(menu);
      final StringFavorite favorite = favorites.get(i);
      String name = favorite.getName();
      item = new JMenuItem(name);
      item.addActionListener((ActionEvent e) -> listener.favoriteSelected(new StringFavoriteSelectionEvent(listener, favorite)));
      addMenuItem(menu, item);
    }
  }

  /**
   * Adds a menu item with the favorites to the menu.
   *
   * @param menu	the menu (JMenu/JPopupMenu) to add the favorites to
   * @param cls		the class the favorites are for
   * @param current	the current data associated with the favorite
   * @param listener	the listener to attach to the menu items' ActionListener
   */
  public void addFavoritesMenuItems(Object menu, final Class cls, final String current, StringFavoriteSelectionListener listener) {
    addFavoritesMenuItems(false, menu, cls, current, listener);
    if (menu instanceof JMenu)
      ((JMenu) menu).addSeparator();
    else if (menu instanceof JPopupMenu)
      ((JPopupMenu) menu).addSeparator();
    addFavoritesMenuItems(true, menu, cls, current, listener);
  }

  /**
   * Adds a menu item with the favorites submenu to the popup menu.
   *
   * @param menu	the menu (JMenu/JPopMenu) to add the favorites to
   * @param cls		the class the favorites are for
   * @param current	the current data associated with the favorite
   * @param listener	the listener to attach to the menu items' ActionListener
   */
  public void addFavoritesSubMenu(Object menu, Class cls, String current, StringFavoriteSelectionListener listener) {
    JMenu		submenu;

    submenu = new JMenu("Favorites");
    submenu.setIcon(ImageManager.getIcon("favorite.gif"));
    if (menu instanceof JPopupMenu)
      ((JPopupMenu) menu).add(submenu);
    else if (menu instanceof JMenu)
      ((JMenu) menu).add(submenu);
    else
      throw new IllegalArgumentException("Only JMenu and JPopupMenu are accepted!");

    addFavoritesMenuItems(submenu, cls, current, listener);
  }

  /**
   * Returns a copy of itself.
   *
   * @return		the copy
   */
  public StringFavorites getClone() {
    StringFavorites result;

    result              = new StringFavorites(m_AutoSave);
    result.m_Modified   = m_Modified;
    result.m_Properties = m_Properties.getClone();

    return result;
  }

  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   *
   * @param   o the object to be compared.
   * @return  a negative integer, zero, or a positive integer as this object
   *		is less than, equal to, or greater than the specified object.
   * @throws ClassCastException if the specified object's type prevents it
   *         from being compared to this object.
   */
  public int compareTo(Object o) {
    int		result;
    StringFavorites f;

    if (o == null)
      return 1;

    f = (StringFavorites) o;

    result = m_Properties.compareTo(f.m_Properties);

    return result;
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   *
   * @param obj		the reference object with which to compare.
   * @return		true if this object is the same as the obj argument;
   * 			false otherwise.
   */
  @Override
  public boolean equals(Object obj) {
    return (compareTo(obj) == 0);
  }

  /**
   * Returns the singleton (and initializes it, if necessary).
   *
   * @return		the singleton
   */
  public static synchronized StringFavorites getSingleton() {
    if (m_Singleton == null) {
      m_Singleton = new StringFavorites();
      m_Singleton.getProperties();
    }

    return m_Singleton;
  }

  /**
   * Reloads the system-wide favorites.
   */
  public static synchronized void reload() {
    getSingleton().load();
  }
}
