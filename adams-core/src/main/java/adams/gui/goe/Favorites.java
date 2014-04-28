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
 * Favorites.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.goe;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Collections;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.HashSet;
import java.util.Vector;
import java.util.logging.Level;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import adams.core.CloneHandler;
import adams.core.Properties;
import adams.core.logging.LoggingObject;
import adams.core.option.OptionUtils;
import adams.env.Environment;
import adams.gui.core.GUIHelper;

/**
 * A helper class for managing the GOE favorites.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Favorites
  implements Comparable, CloneHandler<Favorites> {

  /**
   * Container class for a favorite setup.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class Favorite
    extends LoggingObject
    implements Comparable, CloneHandler<Favorite> {

    /** for serialization. */
    private static final long serialVersionUID = 9155308607371430795L;

    /** the name of the favorite. */
    protected String m_Name;

    /** the commandline of the favorite. */
    protected String m_Commandline;

    /**
     * Initializes the favorite.
     *
     * @param name	the name of the favorite
     * @param cmd	the commandline of the favorite
     */
    public Favorite(String name, String cmd) {
      super();

      if (name == null)
	throw new IllegalArgumentException("Name of favorite cannot be null!");
      if (cmd == null)
	throw new IllegalArgumentException("Command for favorite cannot be null!");
      
      m_Name        = name;
      m_Commandline = cmd;

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
     * Returns the commandline of the favorite.
     *
     * @return		the commandline
     */
    public String getCommandline() {
      return m_Commandline;
    }

    /**
     * Turns the commandline into an object.
     *
     * @return		the generated object, or null in case of an error
     */
    public Object getObject() {
      Object	result;

      try {
	result = OptionUtils.forAnyCommandLine(Object.class, m_Commandline);
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to turn command-line into object: " + m_Commandline, e);
	result = null;
      }

      return result;
    }

    /**
     * Returns a copy of itself.
     *
     * @return		the copy
     */
    public Favorite getClone() {
      return new Favorite(m_Name, m_Commandline);
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
      Favorite	f;

      if (o == null)
        return 1;

      if (!(o instanceof Favorite))
	return -1;

      f = (Favorite) o;

      result = getName().toLowerCase().compareTo(f.getName().toLowerCase());
      if (result == 0)
	result = getCommandline().compareTo(f.getCommandline());

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
   * @version $Revision$
   */
  public static class FavoriteSelectionEvent
    extends EventObject {

    /** for serialization. */
    private static final long serialVersionUID = -3355442271698515292L;

    /** the selected favorite. */
    protected Favorite m_Favorite;

    /**
     * Initializes the event.
     *
     * @param source	the object that triggered the event
     * @param favorite	the selected favorite
     */
    public FavoriteSelectionEvent(Object source, Favorite favorite) {
      super(source);

      m_Favorite = favorite;
    }

    /**
     * Returns the selected favorite.
     *
     * @return		the favorite
     */
    public Favorite getFavorite() {
      return m_Favorite;
    }
  }

  /**
   * Interface for classes that listen to selections of favorites.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static interface FavoriteSelectionListener {

    /**
     * Gets called when a favorite gets selected.
     *
     * @param e		the event
     */
    public void favoriteSelected(FavoriteSelectionEvent e);
  }

  /** the filename of the props file. */
  public final static String FILENAME = "GenericObjectEditorFavorites.props";

  /** the separator between classname and favorite name. */
  public final static String SEPARATOR = "-";

  /** for storing a temporary favorite. */
  public final static String TEMPORARY = "$TMP$";

  /** the properties. */
  protected Properties m_Properties;

  /** whether the favorites were modified. */
  protected boolean m_Modified;

  /** whether to save the properties whenever a change happened. */
  protected boolean m_AutoSave;

  /** the singleton. */
  protected static Favorites m_Singleton;

  /**
   * Initializes the favorites with immediate saving enabled.
   */
  public Favorites() {
    this(true);
  }

  /**
   * Initializes the favorites.
   *
   * @param autosave	whether to save the favorites immediately
   * 				whenever modified
   */
  public Favorites(boolean autosave) {
    super();

    m_AutoSave = autosave;
    m_Modified = false;
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
   * @return		true if autosave is on
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
      if (removeTemporaryFavorites() && m_AutoSave)
	updateFavorites();
    }

    return m_Properties;
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
   * @param classname	the class to get the favorites for
   * @return		the favorites
   */
  public Vector<Favorite> getFavorites(String classname) {
    try {
      return getFavorites(Class.forName(classname));
    }
    catch (Exception e) {
      e.printStackTrace();
      return new Vector<Favorite>();
    }
  }

  /**
   * Returns the favorites for the specified class.
   *
   * @param cls		the class to get the favorites for
   * @return		the favorites
   */
  public Vector<Favorite> getFavorites(Class cls) {
    Vector<Favorite>	result;
    Enumeration<String>	enm;
    String		key;
    String		prefix;
    Favorite		favorite;

    result = new Vector<Favorite>();

    prefix = cls.getName() + SEPARATOR;
    enm    = (Enumeration<String>) getProperties().propertyNames();
    while (enm.hasMoreElements()) {
      key = enm.nextElement();
      if (key.startsWith(prefix)) {
	favorite = new Favorite(key.substring(prefix.length()), getProperties().getProperty(key));
	result.add(favorite);
      }
    }

    Collections.sort(result);

    return result;
  }

  /**
   * Returns all the current superclasses.
   *
   * @return		the classnames of the superclasses
   */
  public Vector<String> getSuperclasses() {
    Vector<String>	result;
    HashSet<String>	set;
    Enumeration<String>	enm;
    String		key;
    String		classname;

    set = new HashSet<String>();
    enm = (Enumeration<String>) getProperties().propertyNames();
    while (enm.hasMoreElements()) {
      key       = enm.nextElement();
      classname = key.substring(0, key.indexOf(SEPARATOR));
      set.add(classname);
    }

    result = new Vector<String>(set);
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
   * Returns the named favorite for the specified class.
   *
   * @param cls		the class to get the favorite for
   * @param name	the name of the favorite
   * @return		the favorite, null if not available
   */
  public Favorite getFavorite(Class cls, String name) {
    String	key;

    key = cls.getName() + SEPARATOR + fixName(name);
    if (getProperties().hasKey(key))
      return new Favorite(name, getProperties().getProperty(key));
    else
      return null;
  }

  /**
   * Adds a favorite for a class.
   *
   * @param cls		the class to add the favorite for
   * @param obj		the favorite
   * @param name	the name of the favorite
   */
  public void addFavorite(Class cls, Object obj, String name) {
    getProperties().setProperty(cls.getName() + SEPARATOR + fixName(name), OptionUtils.getCommandLine(obj));
    m_Modified = true;
    if (m_AutoSave)
      updateFavorites();
  }

  /**
   * Removes the favorites for the specified class.
   *
   * @param classname	the class to remove the favorites for
   */
  public void removeFavorites(String classname) {
    try {
      removeFavorites(Class.forName(classname));
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Removes the favorites for the specified class.
   *
   * @param cls		the class to remove the favorites for
   */
  public void removeFavorites(Class cls) {
    Vector<Favorite>	favorites;
    int			i;

    favorites = getFavorites(cls);
    for (i = 0; i < favorites.size(); i++)
      getProperties().removeKey(cls.getName() + SEPARATOR + favorites.get(i).getName());

    m_Modified = true;

    if (m_AutoSave)
      updateFavorites();
  }

  /**
   * Removes a favorite for a class.
   *
   * @param classname	the class to remove the favorite for
   * @param name	the name of the favorite
   */
  public void removeFavorite(String classname, String name) {
    try {
      removeFavorite(Class.forName(classname), fixName(name));
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Removes a favorite for a class.
   *
   * @param cls		the class to remove the favorite for
   * @param name	the name of the favorite
   */
  public void removeFavorite(Class cls, String name) {
    getProperties().removeKey(cls.getName() + SEPARATOR + fixName(name));
    m_Modified = true;
    if (m_AutoSave)
      updateFavorites();
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
   * Adds a menu item with the favorites to the popup menu.
   *
   * @param menu	the menu to add the favorites to
   * @param cls		the class the favorites are for
   * @param current	the current object
   * @param listener	the listener to attach to the menu items' ActionListener
   */
  public void customizePopupMenu(JPopupMenu menu, Class cls, Object current, FavoriteSelectionListener listener) {
    JMenu		submenu;
    JMenu		updatemenu;
    JMenuItem		item;
    Vector<Favorite>	favorites;
    int			i;

    favorites = getFavorites(cls);

    submenu = new JMenu("Favorites");
    submenu.setIcon(GUIHelper.getIcon("favorite.gif"));
    menu.add(submenu);

    // for adding a favorite
    final Class fCls = cls;
    final Object fCurrent = current;
    item = new JMenuItem("Add to favorites...");
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	String name = null;
	do {
	  name = JOptionPane.showInputDialog("Please enter name for favorite:");
	  if (name == null)
	    return;
	  name = name.trim();
	  if (name.startsWith("$")) {
	    GUIHelper.showErrorMessage(
		null, "Name cannot start with '$'!");
	    name = null;
	  }
	}
	while (name == null);
	addFavorite(fCls, fCurrent, name);
      }
    });
    submenu.add(item);
    
    updatemenu = new JMenu("Update favorite");
    updatemenu.setEnabled(false);  // in case we have no favorites yet
    submenu.add(updatemenu);
    for (i = 0; i < favorites.size(); i++) {
      final Favorite favorite = favorites.get(i);
      String name = favorite.getName();
      if (name.equals(TEMPORARY))
	continue;
      updatemenu.setEnabled(true);
      item = new JMenuItem(name);
      item.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  addFavorite(fCls, fCurrent, favorite.getName());
	}
      });
      updatemenu.add(item);
    }

    item = new JMenuItem("Add as temporary favorite");
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	addFavorite(fCls, fCurrent, TEMPORARY);
      }
    });
    submenu.add(item);

    // current favorites
    final FavoriteSelectionListener fListener = listener;
    for (i = 0; i < favorites.size(); i++) {
      if (i == 0)
	submenu.addSeparator();
      final Favorite favorite = favorites.get(i);
      String name = favorite.getName();
      if (name.equals(TEMPORARY))
	name = "-Temp-";
      item = new JMenuItem(name);
      item.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  fListener.favoriteSelected(new FavoriteSelectionEvent(fListener, favorite));
	}
      });
      submenu.add(item);
    }
  }

  /**
   * Returns a copy of itself.
   *
   * @return		the copy
   */
  public Favorites getClone() {
    Favorites	result;

    result              = new Favorites(m_AutoSave);
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
    Favorites	f;

    if (o == null)
      return 1;

    f = (Favorites) o;

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
   * Removes all the temporary favorites from the properties.
   *
   * @return		true if modified
   */
  protected boolean removeTemporaryFavorites() {
    boolean		result;
    Enumeration<String>	enm;
    Vector<String>	tmp;
    String		name;
    int			i;

    result = false;

    tmp = new Vector<String>();
    enm = (Enumeration<String>) m_Properties.propertyNames();
    while (enm.hasMoreElements()) {
      name = enm.nextElement();
      if (name.endsWith(SEPARATOR + TEMPORARY))
	tmp.add(name);
    }
    if (tmp.size() > 0) {
      result = true;
      for (i = 0; i < tmp.size(); i++)
	m_Properties.removeKey(tmp.get(i));
    }

    return result;
  }

  /**
   * Returns the singleton (and initializes it, if necessary).
   *
   * @return		the singleton
   */
  public static synchronized Favorites getSingleton() {
    if (m_Singleton == null) {
      m_Singleton = new Favorites();
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
