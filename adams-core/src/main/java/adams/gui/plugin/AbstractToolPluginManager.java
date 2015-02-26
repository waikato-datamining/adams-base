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
 * AbstractToolPluginManager.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.gui.plugin;

import adams.gui.core.BaseMenu;
import adams.gui.core.GUIHelper;

import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Ancestor for classes that manage tool plugins.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the owning tool
 * @param <P> the type of plugin
 */
public abstract class AbstractToolPluginManager<T extends ToolPluginSupporter, P extends AbstractToolPlugin> {

  /** the owner. */
  protected T m_Owner;

  /** the plugins. */
  protected List<P> m_Plugins;

  /** the plugin menu items. */
  protected List<JMenuItem> m_MenuItemPlugins;

  /** the menu the plugins were added to. */
  protected BaseMenu m_MenuPlugins;

  /** the change listener to use for triggering menu updates. */
  protected ChangeListener m_MenuUpdateListener;

  /**
   * Initializes the manager.
   *
   * @param owner	the owning tool
   */
  protected AbstractToolPluginManager(T owner) {
    m_Owner           = owner;
    m_Plugins         = new ArrayList<>();
    m_MenuItemPlugins = new ArrayList<>();
    m_MenuUpdateListener = null;
  }

  /**
   * Sets the listener for menu updates.
   *
   * @param l		the listener, null to unset
   */
  public void setMenuUpdateListener(ChangeListener l) {
    m_MenuUpdateListener = l;
  }

  /**
   * Returns the listener for menu updates.
   *
   * @return		the listener, null if not available
   */
  public ChangeListener getMenuUpdateListener() {
    return m_MenuUpdateListener;
  }

  /**
   * Returns a list of plugin classnames.
   *
   * @return		all the available plugins
   */
  public abstract String[] getPlugins();

  /**
   * Adds the plugins to the menu bar.
   *
   * @param menubar	the menu bar
   */
  public void addToMenuBar(JMenuBar menubar) {
    BaseMenu	menu;
    JMenuItem	menuitem;
    int		i;
    String[]	plugins;

    // add menu
    plugins = getPlugins();
    menu = new BaseMenu("Plugins");
    menubar.add(menu);
    menu.setMnemonic('P');
    menu.setVisible(plugins.length > 0);
    menu.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
	if (m_MenuUpdateListener != null)
	  m_MenuUpdateListener.stateChanged(e);
	updateMenu();
      }
    });
    m_MenuPlugins = menu;

    // add items
    m_MenuItemPlugins.clear();
    m_Plugins.clear();
    for (i = 0; i < plugins.length; i++) {
      try {
	final P plugin = (P) Class.forName(plugins[i]).newInstance();
	menuitem = new JMenuItem(plugin.getCaption());
	menuitem.setIcon(plugin.getIcon());
	menu.add(menuitem);
	menuitem.addActionListener(new ActionListener() {
	  public void actionPerformed(ActionEvent e) {
	    String error = plugin.execute(m_Owner.getCurrentPanel());
	    if ((error != null) && !error.isEmpty())
	      GUIHelper.showErrorMessage(
		m_Owner.getCurrentPanel(),
		"Error occurred executing plugin '" + plugin.getCaption() + "':\n" + error);
	    updateMenu();
	  }
	});
	m_Plugins.add(plugin);
	m_MenuItemPlugins.add(menuitem);
      }
      catch (Exception e) {
	System.err.println("Failed to install plugin '" + plugins[i] + "':");
	e.printStackTrace();
      }
    }
    m_MenuPlugins.sort();
  }

  /**
   * updates the enabled state of the menu items.
   */
  public void updateMenu() {
    int		i;
    boolean	enabled;

    for (i = 0; i < m_Plugins.size(); i++) {
      try {
	enabled = m_Plugins.get(i).canExecute(m_Owner.getCurrentPanel());
	m_MenuItemPlugins.get(i).setEnabled(enabled);
      }
      catch (Exception e) {
	System.err.println("Failed to update plugin: " + m_Plugins.get(i).getClass().getName());
	e.printStackTrace();
      }
    }
  }
}
