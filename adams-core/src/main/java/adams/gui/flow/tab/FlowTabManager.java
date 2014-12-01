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
 * FlowTabManager.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.tab;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.TreePath;

import adams.core.ClassLocator;
import adams.core.Properties;
import adams.env.Environment;
import adams.flow.core.AbstractActor;
import adams.gui.core.BaseTabbedPaneWithTabHiding;
import adams.gui.core.MouseUtils;
import adams.gui.flow.FlowEditorPanel;
import adams.gui.flow.FlowPanel;
import adams.gui.flow.tree.Node;
import adams.gui.flow.tree.Tree;

/**
 * Specialized JTabbedPane for managing tabs in the flow editor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FlowTabManager
  extends BaseTabbedPaneWithTabHiding {

  /** for serialization. */
  private static final long serialVersionUID = 3685631497946681192L;

  /** the file to store the tab setup in. */
  public final static String SESSION_FILE = "FlowTabManagerSession.props";

  /** the default menu item text. */
  public final static String MENUITEM_TABS = "Show tabs";

  /** the sufix for the session file. */
  public final static String SUFFIX_VISIBLE = ".Visible";

  /** the flow editor that the tab manager belongs to. */
  protected FlowEditorPanel m_Owner;
  
  /** the properties. */
  protected static Properties m_Properties;

  /** all the available tabs. */
  protected List<AbstractEditorTab> m_TabList;

  /**
   * Initializes the tab manager.
   * 
   * @param owner	the editor panel tha the manager belongs to
   */
  public FlowTabManager(FlowEditorPanel owner) {
    super();
    
    m_Owner = owner;
    if (m_Owner != null) {
      m_Owner.getFlowPanels().addChangeListener(new ChangeListener() {
	@Override
	public void stateChanged(ChangeEvent e) {
	  notifyTabs(getOwner().getCurrentPanel());
	}
      });
    }
  }
  
  /**
   * Initializes the widget.
   */
  @Override
  protected void initialize() {
    String[]		tabs;
    AbstractEditorTab	tab;
    Properties		props;
    String		key;
    boolean		update;

    super.initialize();

    setCloseTabsWithMiddelMouseButton(false);

    tabs      = AbstractEditorTab.getTabs();
    m_TabList = new ArrayList<AbstractEditorTab>();
    props     = getProperties();
    update    = false;
    for (String tabName: tabs) {
      tab = AbstractEditorTab.forName(tabName);
      if (tab != null) {
	tab.setOwner(this);
	m_TabList.add(tab);
	key = createPropertyKey(tab.getClass());
	if (!props.hasKey(key)) {
	  props.setBoolean(key, !(tab instanceof RuntimeTab));
	  update = true;
	}
	else if (tab instanceof RuntimeTab) {
	  if (props.getBoolean(key)) {
	    props.setBoolean(key, false);
	    update = true;
	  }
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
    AbstractEditorTab	tab;
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
  public FlowEditorPanel getOwner() {
    return m_Owner;
  }

  /**
   * Gets called when the user clicks on a tab.
   */
  @Override
  protected void tabClicked(MouseEvent e) {
    int		index;
    Component	comp;

    super.tabClicked(e);
    
    index = indexAtLocation(e.getX(), e.getY());
    if ((index >= 0) && MouseUtils.isMiddleClick(e) && canCloseTabWithMiddleMouseButton(index)) {
      comp = getComponentAt(index);
      if (comp instanceof RuntimeTab)
	setVisible(comp.getClass(), false);
    }
  }
  
  /**
   * Notifies all the selection aware tabs that the selection of actors has
   * changed.
   *
   * @param paths	the selected paths
   * @param actors	the selected actors
   */
  public void notifyTabs(TreePath[] paths, AbstractActor[] actors) {
    int		i;

    for (i = 0; i < getTabCount(); i++) {
      if (getComponentAt(i) instanceof SelectionAwareEditorTab)
	((SelectionAwareEditorTab) getComponentAt(i)).actorSelectionChanged(paths, actors);
    }
  }
  
  /**
   * Notifies all the tab change aware tabs that a different flow panel was 
   * selected.
   *
   * @param panel	the current panel
   */
  public void notifyTabs(FlowPanel panel) {
    int		i;

    for (i = 0; i < getTabCount(); i++) {
      if (getComponentAt(i) instanceof TabChangeAwareEditorTab)
	((TabChangeAwareEditorTab) getComponentAt(i)).flowPanelChanged(panel);
    }
  }

  /**
   * Notifies all the tabs.
   *
   * @param tree	the tree to use as basis
   */
  public void refresh(Tree tree) {
    TreePath[]		paths;
    AbstractActor[]	actors;
    int			i;

    if (tree == null)
      return;

    paths = tree.getSelectionPaths();
    if (paths == null)
      paths = new TreePath[0];
    actors = new AbstractActor[paths.length];
    for (i = 0; i < paths.length; i++)
      actors[i] = ((Node) paths[i].getLastPathComponent()).getActor();

    notifyTabs(paths, actors);
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
    for (final AbstractEditorTab tab: m_TabList) {
      if (tab instanceof RuntimeTab)
	continue;
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
   * @return		true if the tab is visible by default (if not a {@link RuntimeTab} tab)
   */
  public synchronized boolean isVisible(Class cls) {
    return getProperties().getBoolean(createPropertyKey(cls), !ClassLocator.hasInterface(RuntimeTab.class, cls));
  }

  /**
   * Sets whether a tab should be visible or not.
   *
   * @param cls		the tab class
   * @param value	if true then the tab is made visible by default
   */
  public synchronized void setVisible(Class cls, boolean value) {
    setVisible(cls, value, true);
  }

  /**
   * Sets whether a tab should be visible or not.
   *
   * @param cls		the tab class
   * @param value	if true then the tab is made visible by default
   * @param update	whether to update the properties
   */
  public synchronized void setVisible(Class cls, boolean value, boolean update) {
    getProperties().setBoolean(createPropertyKey(cls), value);
    for (AbstractEditorTab tab: m_TabList) {
      if (tab.getClass() == cls) {
	if (value) {
	  displayTab(tab);
	  setSelectedComponent(tab);
	}
	else {
	  hideTab(tab);
	}
      }
    }
    if (update)
      updateProperties();
  }

  /**
   * Sets whether all tabs should be visible or not.
   *
   * @param value	if true then the tab is made visible by default
   */
  public synchronized void setAllVisible(boolean value) {
    for (AbstractEditorTab tab: m_TabList) {
      getProperties().setBoolean(createPropertyKey(tab.getClass()), value);
      if (value)
        displayTab(tab);
      else
        hideTab(tab);
    }
    updateProperties();
  }

  /**
   * Returns the tab instance of the given class.
   * 
   * @param cls		the class of tab to get the instance for
   * @return		the tab instance
   */
  public synchronized AbstractEditorTab getTab(Class cls) {
    AbstractEditorTab	result;
    
    result = null;
    
    for (AbstractEditorTab tab: m_TabList) {
      if (tab.getClass() == cls) {
	result = tab;
	break;
      }
    }
    
    return result;
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
