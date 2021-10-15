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
 * HandlerFavorites.java
 * Copyright (C) 2021 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools.previewbrowser;

import adams.core.Properties;
import adams.core.logging.LoggingObject;
import adams.core.option.OptionUtils;
import adams.env.Environment;
import adams.gui.core.BaseButtonWithDropDownMenu;
import adams.gui.core.GUIHelper;
import adams.gui.dialog.ApprovalDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

/**
 * Manages the favorites.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class ContentHandlerFavorites
    extends LoggingObject {

  private static final long serialVersionUID = 3953765628612115247L;

  /**
   * Encapsulates a single favorite.
   */
  public static class ContentHandlerFavorite
      extends LoggingObject {

    private static final long serialVersionUID = -7917753097321799489L;

    /** the extension. */
    protected String m_Extension;

    /** the name of the favorite. */
    protected String m_Name;

    /** the handler commandline. */
    protected String m_Commandline;

    /**
     * Initializes the favorite.
     *
     * @param ext	the extension
     * @param name	the name
     * @param handler	the handler
     */
    public ContentHandlerFavorite(String ext, String name, AbstractContentHandler handler) {
      this(ext, name, OptionUtils.getCommandLine(handler));
    }

    /**
     * Initializes the favorite.
     *
     * @param ext	the extension
     * @param name	the name
     * @param cmdline	the handler commandline
     */
    public ContentHandlerFavorite(String ext, String name, String cmdline) {
      m_Extension   = ext;
      m_Name        = name;
      m_Commandline = cmdline;
    }

    /**
     * Returns the extension of the favorite.
     *
     * @return		the extension
     */
    public String getExtension() {
      return m_Extension;
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
     * Returns a new handler instance.
     *
     * @return		the instance, null if failed to instantiate
     */
    public AbstractContentHandler getHandler() {
      if (m_Commandline.trim().isEmpty())
	return null;

      try {
	return (AbstractContentHandler) OptionUtils.forCommandLine(AbstractContentHandler.class, m_Commandline);
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to parse command-line: " + m_Commandline, e);
	return null;
      }
    }

    /**
     * Returns a string representation of the object.
     *
     * @return		the string representation
     */
    public String toString() {
      return "ext=" + m_Extension + ", name=" + m_Name + ", cmdline=" + m_Commandline;
    }
  }

  /** the filename of the props file. */
  public final static String FILENAME = "PreviewBrowserFavorites.props";

  /** the separator between extension and name. */
  public final static String SEPARATOR = "|";

  /** whether the favorites were modified. */
  protected boolean m_Modified;

  /** whether to save the properties whenever a change happened. */
  protected boolean m_AutoSave;

  /** the favorites. */
  protected List<ContentHandlerFavorite> m_Favorites;

  /** the singleton. */
  protected static ContentHandlerFavorites m_Singleton;

  /**
   * Initializes the favorites with immediate saving enabled.
   */
  public ContentHandlerFavorites() {
    this(true);
  }

  /**
   * Initializes the favorites.
   *
   * @param autosave	whether to save the favorites immediately
   * 				whenever modified
   */
  public ContentHandlerFavorites(boolean autosave) {
    super();

    m_AutoSave  = autosave;
    m_Modified  = false;
    m_Favorites = new ArrayList<>();
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
   * Loads the favorites.
   */
  protected synchronized void load() {
    File				file;
    Properties  			props;
    List<ContentHandlerFavorite>	favorites;
    ContentHandlerFavorite		favorite;
    String[]				names;
    String				cmdline;

    props = new Properties();
    file  = new File(Environment.getInstance().getHome() + File.separator + FILENAME);
    if (file.exists())
      props.load(file.getAbsolutePath());

    favorites = new ArrayList<>();
    for (String ext : props.keySetAll()) {
      if (ext.contains("|"))
	continue;
      try {
	names = OptionUtils.splitOptions(props.getProperty(ext));
	for (String name: names) {
	  cmdline = props.getProperty(ext + SEPARATOR + name);
	  if (cmdline == null) {
	    getLogger().warning("Missing entry: " + ext + SEPARATOR + name);
	    continue;
	  }
	  try {
	    favorite = new ContentHandlerFavorite(ext, name, (AbstractContentHandler) OptionUtils.forCommandLine(AbstractContentHandler.class, cmdline));
	    favorites.add(favorite);
	  }
	  catch (Exception e) {
	    getLogger().log(Level.SEVERE, "Failed to parse command-line: " + cmdline, e);
	  }
	}
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to parse names for extension '" + ext + "': " + props.getProperty(ext), e);
      }
    }

    m_Favorites = favorites;
  }

  /**
   * Removes all favorites.
   */
  public void clear() {
    m_Favorites.clear();
    m_Modified = true;
    if (m_AutoSave)
      updateFavorites();
  }

  /**
   * Returns the list of extensions we have favorites for.
   *
   * @return		the list
   */
  public List<String> getExtensions() {
    List<String>	result;
    Set<String> 	exts;

    exts = new HashSet<>();
    for (ContentHandlerFavorite f: m_Favorites)
      exts.add(f.getExtension());
    result = new ArrayList<>(exts);
    Collections.sort(result);

    return result;
  }

  /**
   * Returns all the favorites for the specified extension.
   *
   * @param ext		the extension to get the favorites for
   * @return		the favorites
   */
  public List<ContentHandlerFavorite> getFavorites(String ext) {
    List<ContentHandlerFavorite>	result;

    result = new ArrayList<>();
    for (ContentHandlerFavorite f: m_Favorites) {
      if (f.getExtension().equalsIgnoreCase(ext))
	result.add(f);
    }

    return result;
  }

  /**
   * Adds the favorite.
   *
   * @param ext		the extension this favorite is for
   * @param name	the name of the favorite
   * @param handler	the handler
   * @return		the generated favorite
   */
  public synchronized ContentHandlerFavorite addFavorite(String ext, String name, AbstractContentHandler handler) {
    ContentHandlerFavorite result;

    result = new ContentHandlerFavorite(ext, name, handler);
    m_Favorites.add(result);
    if (m_AutoSave)
      updateFavorites();

    return result;
  }

  /**
   * Removes the favorite with the specified name.
   *
   * @param name	the name of the favorite to remove
   * @return		the removed favorite, if any
   */
  public synchronized ContentHandlerFavorite removeFavorite(String name) {
    ContentHandlerFavorite 	result;
    int				i;

    result = null;

    for (i = 0; i < m_Favorites.size(); i++) {
      if (m_Favorites.get(i).getName().equals(name)) {
        result = m_Favorites.remove(i);
        break;
      }
    }

    if (m_AutoSave)
      updateFavorites();

    return result;
  }

  /**
   * Removes all the favorites for this extension.
   *
   * @param ext		the extension to remove the favorites for
   */
  public synchronized void removeFavorites(String ext) {
    int		i;

    i = 0;
    while (i < m_Favorites.size()) {
      if (m_Favorites.get(i).getExtension().equalsIgnoreCase(ext))
        m_Favorites.remove(i);
      else
        i++;
    }

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
    boolean				result;
    String				filename;
    Properties				props;
    List<ContentHandlerFavorite>	favorites;
    List<String>			names;

    // generate properties
    props = new Properties();
    for (String ext: getExtensions()) {
      favorites = getFavorites(ext);
      names = new ArrayList<>();
      for (ContentHandlerFavorite f: favorites) {
	names.add(f.getName());
	props.setProperty(ext + SEPARATOR + f.getName(), f.getCommandline());
      }
      props.setProperty(ext, OptionUtils.joinOptions(names.toArray(new String[0])));
    }

    // save properties
    filename = getFilename();
    if (!props.save(filename)) {
      result = false;
      System.err.println("Error saving preview browser favorites to '" + filename + "'!");
    }
    else {
      result     = true;
      m_Modified = false;
    }

    return result;
  }

  /**
   * Adds menu items for favorites to the dropdown button.
   *
   * @param button	the button to add the favorites to
   * @param ext		the extension of the current file, disables the button if null
   */
  public void customizeDropDownButton(BaseButtonWithDropDownMenu button, final String ext, final PreviewDisplay display) {
    JMenuItem 				item;
    JMenu				submenu;
    List<ContentHandlerFavorite> 	favorites;
    int 				i;

    button.clearMenu();

    if (ext == null) {
      button.setEnabled(false);
      return;
    }

    button.setEnabled(true);
    favorites = getFavorites(ext);

    // for adding a favorite
    item = new JMenuItem("Add to favorites...");
    item.addActionListener((ActionEvent e) -> {
      AbstractContentHandler current = display.getContentHandler();
      String name = GUIHelper.showInputDialog(null, "Please enter name for favorite:");
      if (name == null)
	return;
      name = name.trim();
      ContentHandlerFavorite favorite = addFavorite(ext, name, current);
      display.selectFavorite(favorite);
      customizeDropDownButton(button, ext, display);
    });
    button.addToMenu(item);

    // for removing favorites
    if (favorites.size() > 0) {
      button.addSeparatorToMenu();
      submenu = new JMenu("Remove");
      button.addToMenu(submenu);
      for (i = 0; i < favorites.size(); i++) {
	final ContentHandlerFavorite f = favorites.get(i);
	item = new JMenuItem(f.getName());
	item.addActionListener((ActionEvent e) -> {
	  int retVal = GUIHelper.showConfirmMessage(display.getParent(), "Do you want to delete favorite '" + f.getName() + "'?");
	  if (retVal != ApprovalDialog.APPROVE_OPTION)
	    return;
	  ContentHandlerFavorites.getSingleton().removeFavorite(f.getName());
	  customizeDropDownButton(button, ext, display);
	});
	submenu.add(item);
      }
    }

    // for removing favorites for extension
    if (favorites.size() > 0) {
      item = new JMenuItem("Remove all for ." + ext);
      item.addActionListener((ActionEvent e) -> {
        int retVal = GUIHelper.showConfirmMessage(display.getParent(), "Do you want to delete all favorites for extension '." + ext + "'?");
        if (retVal != ApprovalDialog.APPROVE_OPTION)
          return;
	ContentHandlerFavorites.getSingleton().removeFavorites(ext);
	customizeDropDownButton(button, ext, display);
      });
      button.addToMenu(item);
    }

    // current favorites
    if (favorites.size() > 0) {
      button.addSeparatorToMenu();
      for (i = 0; i < favorites.size(); i++) {
	final ContentHandlerFavorite f = favorites.get(i);
	item = new JMenuItem(f.getName());
	item.addActionListener((ActionEvent e) -> display.selectFavorite(f));
	button.addToMenu(item);
      }
    }
  }

  /**
   * Returns the singleton (and initializes it, if necessary).
   *
   * @return		the singleton
   */
  public static synchronized ContentHandlerFavorites getSingleton() {
    if (m_Singleton == null) {
      m_Singleton = new ContentHandlerFavorites();
      m_Singleton.load();
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
