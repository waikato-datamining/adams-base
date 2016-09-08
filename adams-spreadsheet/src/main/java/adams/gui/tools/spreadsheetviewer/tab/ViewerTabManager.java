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
 * ViewerTabManager.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.spreadsheetviewer.tab;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import adams.core.Properties;
import adams.env.Environment;
import adams.gui.core.BaseTabbedPaneWithTabHiding;
import adams.gui.tools.SpreadSheetViewerPanel;
import adams.gui.tools.spreadsheetviewer.SpreadSheetPanel;

/**
 * Specialized JTabbedPane for managing tabs in the flow editor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ViewerTabManager
  extends BaseTabbedPaneWithTabHiding {

  /** for serialization. */
  private static final long serialVersionUID = 3685631497946681192L;

  /** the file to store the tab setup in. */
  public final static String SESSION_FILE = "SpreadSheetViewerTabManagerSession.props";

  /** the default menu item text. */
  public final static String MENUITEM_TABS = "Show tabs";

  /** the sufix for the session file. */
  public final static String SUFFIX_VISIBLE = ".Visible";

  /** the flow editor that the tab manager belongs to. */
  protected SpreadSheetViewerPanel m_Owner;
  
  /** the properties. */
  protected static Properties m_Properties;

  /** all the available tabs. */
  protected List<AbstractViewerTab> m_TabList;

  /**
   * Initializes the tab manager.
   * 
   * @param owner	the editor panel tha the manager belongs to
   */
  public ViewerTabManager(SpreadSheetViewerPanel owner) {
    super();
    
    m_Owner = owner;
  }
  
  /**
   * Initializes the widget.
   */
  @Override
  protected void initialize() {
    String[]		tabs;
    AbstractViewerTab	tab;
    Properties		props;
    String		key;
    boolean		update;

    super.initialize();

    setPreferredSize(new Dimension(300, 0));
    
    setCloseTabsWithMiddleMouseButton(false);

    tabs      = AbstractViewerTab.getTabs();
    m_TabList = new ArrayList<AbstractViewerTab>();
    props     = getProperties();
    update    = false;
    for (String tabName: tabs) {
      tab = AbstractViewerTab.forName(tabName);
      if (tab != null) {
	tab.setOwner(this);
	m_TabList.add(tab);
	key = createPropertyKey(tab.getClass());
	if (!props.hasKey(key)) {
	  props.setBoolean(key, true);
	  update = true;
	}
      }
    }
    Collections.sort(m_TabList);

    if (update)
      updateProperties();
  }

  /**
   * Performs further initializations of widgets.
   */
  @Override
  protected void initGUI() {
    AbstractViewerTab	tab;
    int			i;

    super.initGUI();

    for (i = 0; i < m_TabList.size(); i++) {
      tab = m_TabList.get(i);
      addTab(tab.getTitle(), tab);
      if (!isVisible(tab.getClass()))
	hideTab(tab);
    }
  }

  /**
   * Returns the editor panel that owns this tab manager.
   * 
   * @return		the owner
   */
  public SpreadSheetViewerPanel getOwner() {
    return m_Owner;
  }
  
  /**
   * Notifies all the selection aware tabs that the selection of sheet/rows has
   * changed.
   *
   * @param panel	the panel that triggered the notification
   * @param rows	the currently selected rows
   */
  public void notifyTabs(SpreadSheetPanel panel, int[] rows) {
    int		i;

    for (i = 0; i < getTabCount(); i++) {
      if (getComponentAt(i) instanceof SelectionAwareViewerTab)
	((SelectionAwareViewerTab) getComponentAt(i)).sheetSelectionChanged(panel, rows);
    }
  }

  /**
   * Notifies all the tabs.
   *
   * @param panel	the panel
   */
  public void refresh(SpreadSheetPanel panel) {
    int[]	rows;

    if (panel == null)
      return;

    rows = panel.getTable().getSelectedRows();
    notifyTabs(panel, rows);
  }

  /**
   * Adds all the available tabs.
   *
   * @param menu	the menu to add the "Send to" submenu to if available
   * @param cls		the class that the "Send to" actions must support
   */
  public void addTabsSubmenu(JMenu menu) {
    final JMenu	submenu;
    JMenuItem	menuitem;
    boolean	first;

    submenu = new JMenu(MENUITEM_TABS);
    submenu.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
	updateMenu(submenu);
      }
    });
    menu.add(submenu);

    menuitem = new JMenuItem("Enable all");
    menuitem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	setAllVisible(true);
      }
    });
    submenu.add(menuitem);

    menuitem = new JMenuItem("Disable all");
    menuitem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	setAllVisible(false);
      }
    });
    submenu.add(menuitem);

    first = true;
    for (final AbstractViewerTab tab: m_TabList) {
      if (first) {
	first = false;
	submenu.addSeparator();
      }
      menuitem = new JCheckBoxMenuItem(tab.getTitle());
      menuitem.setSelected(isVisible(tab.getClass()));
      menuitem.addActionListener(new ActionListener() {
        @Override
	public void actionPerformed(ActionEvent e) {
          setVisible(tab.getClass(), !isVisible(tab.getClass()));
          if (isVisible(tab.getClass()))
            displayTab(tab);
          else
            hideTab(tab);
        }
      });
      submenu.add(menuitem);
    }
  }

  /**
   * Updates the specified menu with the current enabled/disabled state of
   * the tabs.
   *
   * @param menu	the menu to update
   */
  protected void updateMenu(JMenu menu) {
    int			i;
    int			n;
    JCheckBoxMenuItem	item;

    for (i = 0; i < menu.getMenuComponentCount(); i++) {
      if (menu.getMenuComponent(i) instanceof JCheckBoxMenuItem) {
	item = (JCheckBoxMenuItem) menu.getMenuComponent(i);
	for (n = 0; n < m_TabList.size(); n++) {
	  if (item.getText().equals(m_TabList.get(n).getTitle())) {
	    item.setSelected(isVisible(m_TabList.get(n).getClass()));
	    break;
	  }
	}
      }
    }
  }

  /**
   * Creates the key for the property.
   *
   * @param cls		the tab class
   * @return		the property key
   * @see		#SUFFIX_VISIBLE
   */
  protected String createPropertyKey(Class cls) {
    return cls.getName() + SUFFIX_VISIBLE;
  }

  /**
   * Returns whether a tab should be visible or not.
   *
   * @param cls		the tab class
   * @return		true if the tab is visible by default
   */
  public synchronized boolean isVisible(Class cls) {
    return getProperties().getBoolean(createPropertyKey(cls), true);
  }

  /**
   * Sets whether a tab should be visible or not.
   *
   * @param cls		the tab class
   * @param value	if true then the tab is made visible by default
   */
  public synchronized void setVisible(Class cls, boolean value) {
    getProperties().setBoolean(createPropertyKey(cls), value);
    updateProperties();
  }

  /**
   * Sets whether all tabs should be visible or not.
   *
   * @param value	if true then the tab is made visible by default
   */
  public synchronized void setAllVisible(boolean value) {
    for (AbstractViewerTab tab: m_TabList) {
      getProperties().setBoolean(createPropertyKey(tab.getClass()), value);
      if (value)
        displayTab(tab);
      else
        hideTab(tab);
    }
    updateProperties();
  }

  /**
   * Saves the current properties.
   *
   * @return		true if successfully updated
   */
  protected synchronized boolean updateProperties() {
    String	filename;

    filename = Environment.getInstance().createPropertiesFilename(SESSION_FILE);
    return getProperties().save(filename);
  }

  /**
   * Returns the properties that define the editor.
   *
   * @return		the properties
   */
  public static synchronized Properties getProperties() {
    if (m_Properties == null) {
      try {
	m_Properties = Properties.read(SESSION_FILE);
      }
      catch (Exception e) {
	m_Properties = new Properties();
      }
    }

    return m_Properties;
  }
}
