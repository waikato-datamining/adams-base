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
 * BaseTabbedPaneWithTabHiding.java
 * Copyright (C) 2009-2011 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

import java.awt.Component;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.Icon;

import adams.gui.event.TabVisibilityChangeEvent;
import adams.gui.event.TabVisibilityChangeEvent.Type;
import adams.gui.event.TabVisibilityChangeListener;

/**
 * Enhanced JTabbedPane. Allows one to "hide" tabs as well.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseTabbedPaneWithTabHiding
  extends BaseTabbedPane {

  /** for serialization. */
  private static final long serialVersionUID = 3893515959380608202L;

  /**
   * Container for backing up hidden tabs.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   * @param <T> the type of object to store
   */
  public static class PageBackup<T>
    implements Serializable {

    /** for serialization. */
    private static final long serialVersionUID = -8446679315630104192L;

    /** the title of the tab. */
    protected String m_Title;

    /** the icon of the tab. */
    protected Icon m_Icon;

    /** the payload of the tab. */
    protected T m_Payload;

    /** the tool tip of the component. */
    protected String m_Tip;

    /**
     * Initializes the backup container.
     *
     * @param title	the title of the tab
     * @param icon	the icon of the tab
     * @param payload	the payload of the tab, e.g., the component
     * @param tip	the tool tip of the tab
     */
    public PageBackup(String title, Icon icon, T payload, String tip) {
      super();

      m_Title   = title;
      m_Icon    = icon;
      m_Payload = payload;
      m_Tip     = tip;
    }

    /**
     * Returns the icon of the tab.
     *
     * @return		the icon
     */
    public Icon getIcon() {
      return m_Icon;
    }

    /**
     * Returns the payload of the tab.
     *
     * @return		the payload
     */
    public T getPayload() {
      return m_Payload;
    }

    /**
     * Returns the tip of the tab.
     *
     * @return		the tip
     */
    public String getTip() {
      return m_Tip;
    }

    /**
     * Returns the title of the tab.
     *
     * @return		the title
     */
    public String getTitle() {
      return m_Title;
    }
  }

  /** for storing the hidden tabs. */
  protected Hashtable<Component,PageBackup<Component>> m_HiddenPages;

  /** the listeners for tab visibility changes. */
  protected HashSet<TabVisibilityChangeListener> m_TabVisibilityChangeListeners;

  /**
   * Creates an empty <code>TabbedPane</code> with a default
   * tab placement of <code>JTabbedPane.TOP</code>.
   */
  public BaseTabbedPaneWithTabHiding() {
    super();
  }

  /**
   * Creates an empty <code>TabbedPane</code> with the specified tab placement
   * of either: <code>JTabbedPane.TOP</code>, <code>JTabbedPane.BOTTOM</code>,
   * <code>JTabbedPane.LEFT</code>, or <code>JTabbedPane.RIGHT</code>.
   *
   * @param tabPlacement the placement for the tabs relative to the content
   */
  public BaseTabbedPaneWithTabHiding(int tabPlacement) {
    super(tabPlacement);
  }

  /**
   * Creates an empty <code>TabbedPane</code> with the specified tab placement
   * and tab layout policy.  Tab placement may be either:
   * <code>JTabbedPane.TOP</code>, <code>JTabbedPane.BOTTOM</code>,
   * <code>JTabbedPane.LEFT</code>, or <code>JTabbedPane.RIGHT</code>.
   * Tab layout policy may be either: <code>JTabbedPane.WRAP_TAB_LAYOUT</code>
   * or <code>JTabbedPane.SCROLL_TAB_LAYOUT</code>.
   *
   * @param tabPlacement the placement for the tabs relative to the content
   * @param tabLayoutPolicy the policy for laying out tabs when all tabs will not fit on one run
   */
  public BaseTabbedPaneWithTabHiding(int tabPlacement, int tabLayoutPolicy) {
    super(tabPlacement, tabLayoutPolicy);
  }

  /**
   * Performs further initializations.
   */
  protected void initialize() {
    super.initialize();

    m_HiddenPages               = new Hashtable<Component,PageBackup<Component>>();
    m_TabVisibilityChangeListeners = new HashSet<TabVisibilityChangeListener>();
  }

  /**
   * Hides the tab containing the specified component.
   *
   * @param component	the component which tab to hide
   * @return		the component that was hidden
   */
  public Component hideTab(Component component) {
    return hideTab(indexOfComponent(component));
  }

  /**
   * Hides the tab at the specified position.
   *
   * @param index	the index of the tab to hide
   * @return		the component that was hidden, null in case of an error
   */
  public Component hideTab(int index) {
    Component	result;

    if ((index < 0) || (index >= getTabCount()))
      return null;

    result = getComponentAt(index);

    m_HiddenPages.put(
	result,
	new PageBackup(
	    getTitleAt(index),
	    getIconAt(index),
	    result,
	    getToolTipTextAt(index)));
    removeTabAt(index);

    notifyTabVisibiltiyChangeListeners(new TabVisibilityChangeEvent(this, Type.HIDE, result));

    return result;
  }

  /**
   * Displays a hidden tab again.
   *
   * @param component	the component to display again
   */
  public void displayTab(Component component) {
    PageBackup	page;

    page = m_HiddenPages.get(component);
    if (page != null) {
      addTab(page.getTitle(), page.getIcon(), (Component) page.getPayload(), page.getTip());
      m_HiddenPages.remove(component);
    }

    notifyTabVisibiltiyChangeListeners(new TabVisibilityChangeEvent(this, Type.DISPLAY, component));
  }

  /**
   * Removes all the tabs and their corresponding components
   * from the <code>tabbedpane</code>. Removes the hidden tabs as well.
   */
  public void removeAll() {
    m_HiddenPages.clear();
    super.removeAll();
  }

  /**
   * Adds a hidden <code>component</code> and <code>tip</code>
   * represented by a <code>title</code> and/or <code>icon</code>,
   * either of which can be <code>null</code>.
   *
   * @param title the title to be displayed in this tab
   * @param icon the icon to be displayed in this tab
   * @param component the component to be displayed when this tab is clicked
   * @param tip the tooltip to be displayed for this tab
   */
  public void addHiddenTab(String title, Icon icon, Component component, String tip) {
    m_HiddenPages.put(
	component,
	new PageBackup<Component>(title, icon, component, tip));
  }

  /**
   * Adds a hidden <code>component</code> represented by a <code>title</code>
   * and/or <code>icon</code>, either of which can be <code>null</code>.
   *
   * @param title the title to be displayed in this tab
   * @param icon the icon to be displayed in this tab
   * @param component the component to be displayed when this tab is clicked
   */
  public void addHiddenTab(String title, Icon icon, Component component) {
    addHiddenTab(title, icon, component, null);
  }

  /**
   * Adds a hidden <code>component</code> represented by a <code>title</code>
   * and no icon.
   *
   * @param title the title to be displayed in this tab
   * @param component the component to be displayed when this tab is clicked
   */
  public void addHiddenTab(String title, Component component) {
    addHiddenTab(title, null, component, null);
  }

  /**
   * Checks whether the component is currently hidden.
   *
   * @param comp	the component to check
   * @return		true if the component is hidden
   */
  public boolean isHidden(Component comp) {
    return m_HiddenPages.containsKey(comp);
  }

  /**
   * Removes the hidden component.
   *
   * @param comp	the hidden component to remove
   * @return		true if successfully remove
   */
  public boolean removeHidden(Component comp) {
    return (m_HiddenPages.remove(comp) != null);
  }

  /**
   * Adds a listener for tab visibility.
   *
   * @param l		the listener to add
   */
  public void addTabVisibilityChangeListener(TabVisibilityChangeListener l) {
    m_TabVisibilityChangeListeners.add(l);
  }

  /**
   * Removes a listener for tab visibility.
   *
   * @param l		the listener to remove
   */
  public void remvoeTabVisibilityChangeListener(TabVisibilityChangeListener l) {
    m_TabVisibilityChangeListeners.remove(l);
  }

  /**
   * Notifies all tab visibility listeners about the specified event.
   *
   * @param e		the event to send
   */
  protected void notifyTabVisibiltiyChangeListeners(TabVisibilityChangeEvent e) {
    Iterator<TabVisibilityChangeListener> iter;

    iter = m_TabVisibilityChangeListeners.iterator();
    while (iter.hasNext())
      iter.next().tabVisibilityChanged(e);
  }
}
