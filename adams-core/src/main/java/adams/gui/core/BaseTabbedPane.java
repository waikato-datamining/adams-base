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
 * BaseTabbedPane.java
 * Copyright (C) 2009-2022 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

import adams.core.CleanUpHandler;
import adams.core.Shortening;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.event.TabClosedEvent;
import adams.gui.event.TabClosedListener;

import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Enhanced JTabbedPane. Offers closing of tabs with middle mouse button.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class BaseTabbedPane
  extends JTabbedPane {

  /** for serialization. */
  private static final long serialVersionUID = 3893515959380608202L;

  /**
   * Interface for classes that can hook into the closing using the middle
   * mouse button and stop it.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public interface MiddleMouseButtonCloseApprover {

    /**
     * Method gets called when having to approve a middle mouse button click.
     *
     * @param source 	the origin of the click
     * @param e		the mouse event
     */
    public boolean approveClosingWithMiddleMouseButton(BaseTabbedPane source, MouseEvent e);
  }

  /**
   * Container for the tab undo list.
   */
  public static class TabUndo {
    /** the component that made up the tab. */
    public Component component;

    /** the title. */
    public String title;

    /** the position. */
    public int index;

    /** tiptext. */
    public String tiptext;

    /** the icon. */
    public Icon icon;

    /** the tab component. */
    public Component tabComponent;

    /** whether the tab was selected. */
    public boolean selected;
  }

  /**
   * For storing the tab undo containers.
   *
   * Clearing or removing cleans up the tabs if the implement {@link CleanUpHandler}.
   */
  public static class TabUndoList
    extends ArrayList<TabUndo> {

    private static final long serialVersionUID = 5710141119325238633L;

    /**
     * Clears the list.
     */
    @Override
    public void clear() {
      int	i;

      for (i = 0; i < size(); i++) {
	if (get(i).component instanceof CleanUpHandler)
	  ((CleanUpHandler) get(i).component).cleanUp();
      }

      super.clear();
    }

    /**
     * Removes the specified range.
     *
     * @param fromIndex	the start (incl)
     * @param toIndex	the end (excl)
     */
    @Override
    protected void removeRange(int fromIndex, int toIndex) {
      int	i;

      for (i = fromIndex; i < toIndex; i++) {
	if (get(i).component instanceof CleanUpHandler)
	  ((CleanUpHandler) get(i).component).cleanUp();
      }

      super.removeRange(fromIndex, toIndex);
    }

    /**
     * Removes the specified index.
     *
     * @param index	the index to remove
     * @return		the removed object
     */
    @Override
    public TabUndo remove(int index) {
      return remove(index, true);
    }

    /**
     * Removes the specified index.
     *
     * @param index	the index to remove
     * @param cleanUp 	whether to clean up the component
     * @return		the removed object
     */
    public TabUndo remove(int index, boolean cleanUp) {
      if (cleanUp) {
	if (get(index).component instanceof CleanUpHandler)
	  ((CleanUpHandler) get(index).component).cleanUp();
      }
      return super.remove(index);
    }

    /**
     * Removes the specified object.
     *
     * @param o		the object to remove
     * @return		true if removed successfully
     */
    @Override
    public boolean remove(Object o) {
      boolean	result;
      TabUndo	undo;

      result = super.remove(o);
      if (result) {
	undo = (TabUndo) o;
	if (undo.component instanceof CleanUpHandler)
	  ((CleanUpHandler) undo.component).cleanUp();
      }

      return result;
    }
  }

  /** Allows the user to close tabs with the middle mouse button. */
  protected boolean m_CloseTabsWithMiddleMouseButton;

  /** the approver for the middle mouse button. */
  protected MiddleMouseButtonCloseApprover m_MiddleMouseButtonCloseApprover;

  /** whether to show a "close tab" button. */
  protected boolean m_ShowCloseTabButton;

  /** whether to prompt user when closing a tab. */
  protected boolean m_PromptUserWhenClosingTab;

  /** whether to make tabs detachable. */
  protected boolean m_DetachableTabs;

  /** the maximum length in chars for titles before getting shortened. */
  protected int m_MaxTitleLength;

  /** the maximum number of tabs to keep for undo. */
  protected int m_MaxTabCloseUndo;

  /** the list of tabs to undo. */
  protected transient TabUndoList m_TabUndoList;

  /** whether to skip tab undo. */
  protected boolean m_SkipTabUndo;

  /** the listeners for tab changes. */
  protected Set<ChangeListener> m_TabChangeListeners;

  /** the listeners for tabs being closed. */
  protected Set<TabClosedListener> m_TabClosedListeners;

  /**
   * Creates an empty <code>TabbedPane</code> with a default
   * tab placement of <code>JTabbedPane.TOP</code>.
   */
  public BaseTabbedPane() {
    super();
    initialize();
    initGUI();
    finishInit();
  }

  /**
   * Creates an empty <code>TabbedPane</code> with the specified tab placement
   * of either: <code>JTabbedPane.TOP</code>, <code>JTabbedPane.BOTTOM</code>,
   * <code>JTabbedPane.LEFT</code>, or <code>JTabbedPane.RIGHT</code>.
   *
   * @param tabPlacement the placement for the tabs relative to the content
   */
  public BaseTabbedPane(int tabPlacement) {
    super(tabPlacement);
    initialize();
    initGUI();
    finishInit();
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
  public BaseTabbedPane(int tabPlacement, int tabLayoutPolicy) {
    super(tabPlacement, tabLayoutPolicy);
    initialize();
    initGUI();
    finishInit();
  }

  /**
   * Performs further initializations of widgets.
   */
  protected void initialize() {
    m_CloseTabsWithMiddleMouseButton = false;
    m_ShowCloseTabButton             = false;
    m_DetachableTabs                 = false;
    m_PromptUserWhenClosingTab       = false;
    m_MaxTitleLength                 = 30;
    m_MaxTabCloseUndo                = 0;
    m_TabUndoList                    = null;
    m_TabChangeListeners             = new HashSet<>();
    m_TabClosedListeners             = new HashSet<>();
  }

  /**
   * Performs further initializations of widgets.
   */
  protected void initGUI() {
    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
	tabClicked(e);
      }
    });
  }

  /**
   * Finishes the initialization.
   */
  protected void finishInit() {
  }

  /**
   * Gets called when the user clicks on a tab.
   * <br><br>
   * Default implementation closes tabs if automatic closing of tabs is
   * enabled.
   *
   * @see		#getCloseTabsWithMiddelMouseButton()
   * @see		#canCloseTabWithMiddleMouseButton(int)
   */
  protected void tabClicked(final MouseEvent e) {
    int		index;
    Component	comp;

    if (m_CloseTabsWithMiddleMouseButton) {
      index = indexAtLocation(e.getX(), e.getY());
      if ((index >= 0) && MouseUtils.isMiddleClick(e) && canCloseTabWithMiddleMouseButton(index)) {
	if ((m_MiddleMouseButtonCloseApprover != null) && !m_MiddleMouseButtonCloseApprover.approveClosingWithMiddleMouseButton(this, e))
	  return;
	comp = getComponentAt(index);
	removeTabAt(index);
	afterTabClosedWithMiddleMouseButton(index, comp);
      }
    }
  }

  /**
   * Prompts the user whether the tab can be closed.
   *
   * @param index	the index of the tab to be closed
   * @return		true if can be closed
   */
  public boolean canCloseTab(int index) {
    int		retVal;

    if (!m_PromptUserWhenClosingTab)
      return true;

    retVal = GUIHelper.showConfirmMessage(this, "Do you want to close the '" + getTitleAt(index) + "' tab?");
    return (retVal == ApprovalDialog.APPROVE_OPTION);
  }

  /**
   * Hook method that checks whether the specified tab can really be closed
   * with a click of the middle mouse button.
   * <br><br>
   * Default implementation always returns true.
   *
   * @param index	the tab index
   * @return		true if tab can be closed
   * @see		#getCloseTabsWithMiddelMouseButton()
   */
  protected boolean canCloseTabWithMiddleMouseButton(int index) {
    return canCloseTab(index);
  }

  /**
   * Hook method that gets executed after a tab was successfully removed with
   * a middle mouse button click.
   * <br><br>
   * Default implementation calls cleanUp() method of {@link CleanUpHandler}
   * instances.
   *
   * @param index	the original index
   * @param comp	the component that was removed
   */
  protected void afterTabClosedWithMiddleMouseButton(int index, Component comp) {
    if (comp instanceof CleanUpHandler)
      ((CleanUpHandler) comp).cleanUp();
  }

  /**
   * Sets whether users can close tabs with the middle mouse button.
   *
   * @param value	if true then users can close tabs with the middle mouse button
   * @see		#canCloseTabWithMiddleMouseButton(int)
   */
  public void setCloseTabsWithMiddleMouseButton(boolean value) {
    m_CloseTabsWithMiddleMouseButton = value;
  }

  /**
   * Returns whether users can close tabs with the middle mouse button.
   *
   * @return		true if users can close tabs with the middle mouse button
   * @see		#canCloseTabWithMiddleMouseButton(int)
   */
  public boolean getCloseTabsWithMiddelMouseButton() {
    return m_CloseTabsWithMiddleMouseButton;
  }

  /**
   * Sets the approver for the middle mouse button.
   *
   * @param value	the approver
   */
  public void setMiddleMouseButtonCloseApprover(MiddleMouseButtonCloseApprover value) {
    m_MiddleMouseButtonCloseApprover = value;
  }

  /**
   * Returns the approver for the middle mouse button.
   *
   * @return		the approver, null if none set
   */
  public MiddleMouseButtonCloseApprover getMiddleMouseButtonCloseApprover() {
    return m_MiddleMouseButtonCloseApprover;
  }

  /**
   * Sets whether to show "close tab" buttons.
   *
   * @param value	true if to show buttons
   */
  public void setShowCloseTabButton(boolean value) {
    m_ShowCloseTabButton = value;
    updateTabComponents();
  }

  /**
   * Returns whether to show "close tab" buttons.
   *
   * @return		true if button displayed
   */
  public boolean getShowCloseTabButton() {
    return m_ShowCloseTabButton;
  }

  /**
   * Sets whether to allow tabs to be detached.
   *
   * @param value	true if detachable
   */
  public void setDetachableTabs(boolean value) {
    m_DetachableTabs = value;
    updateTabComponents();
  }

  /**
   * Returns whether to allow tabs to be detached.
   *
   * @return		true if detachable
   */
  public boolean getDetachableTabs() {
    return m_DetachableTabs;
  }

  /**
   * Updates the tab components.
   */
  protected void updateTabComponents() {
    int		i;

    for (i = 0; i < getTabCount(); i++) {
      if (m_ShowCloseTabButton || m_DetachableTabs)
	setTabComponentAt(i, new ButtonTabComponent(this, !m_DetachableTabs));
      else
	setTabComponentAt(i, null);
    }
  }

  /**
   * Sets whether to prompt the user when closing a tab.
   *
   * @param value	true if to prompt
   */
  public void setPromptUserWhenClosingTab(boolean value) {
    m_PromptUserWhenClosingTab = value;
  }

  /**
   * Returns whether to prompt the user when closing a tab.
   *
   * @return		true if to prompt
   */
  public boolean getPromptUserWhenClosingTab() {
    return m_PromptUserWhenClosingTab;
  }

  /**
   * Removes the currently selected tab.
   *
   * @return		true if a tab was removed
   */
  public boolean removeSelectedTab() {
    if (getSelectedIndex() == -1)
      return false;

    removeTabAt(getSelectedIndex());
    return true;
  }

  /**
   * Inserts a new tab for the given component, at the given index,
   * represented by the given title and/or icon, either of which may
   * be {@code null}.
   *
   * @param title the title to be displayed on the tab
   * @param icon the icon to be displayed on the tab
   * @param component the component to be displayed when this tab is clicked.
   * @param tip the tooltip to be displayed for this tab
   * @param index the position to insert this new tab
   *       ({@code > 0 and <= getTabCount()})
   *
   * @throws IndexOutOfBoundsException if the index is out of range
   *         ({@code < 0 or > getTabCount()})
   */
  public void insertTab(String title, Icon icon, Component component, String tip, int index) {
    ButtonTabComponent 		tabComp;

    super.insertTab(title, icon, component, tip, index);

    if (m_ShowCloseTabButton || m_DetachableTabs) {
      setTabComponentAt(index, new ButtonTabComponent(this, !m_DetachableTabs));

      if (component instanceof PopupMenuProvider) {
	tabComp = (ButtonTabComponent) getTabComponentAt(index);
	tabComp.addMouseListener(new MouseAdapter() {
	  @Override
	  public void mouseClicked(MouseEvent e) {
	    if (MouseUtils.isRightClick(e)) {
	      JPopupMenu menu = ((PopupMenuProvider) component).getPopupMenu();
	      menu.show(tabComp, e.getX(), e.getY());
	    }
	    // for some reason, adding a mouse listener stops left/middle
	    // mouse button clicks from working??
	    else if (MouseUtils.isLeftClick(e)) {
	      setSelectedComponent(component);
	    }
	    else if (MouseUtils.isMiddleClick(e)) {
	      tabClicked(e);
	    }
	  }
	});
      }
    }

    notifyTabChangeListeners();
  }

  /**
   * Sets the title for the tab at the specified position.
   *
   * @param index	the position of the tab
   * @param title	the new title
   */
  @Override
  public void setTitleAt(int index, String title) {
    super.setTitleAt(index, title);
    if ((getTabComponentAt(index) != null) && (m_ShowCloseTabButton || m_DetachableTabs)) {
      getTabComponentAt(index).invalidate();
      getTabComponentAt(index).validate();
      getTabComponentAt(index).repaint();
    }
  }

  /**
   * Sets the title for the tab at the specified position.
   *
   * @param index	the position of the tab
   * @param title	the new title
   */
  public void setShortenedTitleAt(int index, String title) {
    String  	shortTitle;

    shortTitle = Shortening.shortenMiddle(title, m_MaxTitleLength);
    if (!title.equals(shortTitle)) {
      setTitleAt(index, shortTitle);
      setToolTipTextAt(index, title);
    }
    else {
      setTitleAt(index, title);
      setToolTipTextAt(index, null);
    }
  }

  /**
   * Sets the maximum title length to allow before shortening when using
   * {@link #setShortenedTitleAt(int, String)}.
   *
   * @param value	the maximum length in chars
   */
  public void setMaxTitleLength(int value) {
    m_MaxTitleLength = value;
  }

  /**
   * Returns the maximum title length to allow before shortening when using
   * {@link #setShortenedTitleAt(int, String)}.
   *
   * @return		the maximum length in chars
   */
  public int getMaxTitleLength() {
    return m_MaxTitleLength;
  }

  /**
   * Sets the maximum tabs to keep around for undoing closing.
   *
   * @param value	the maximum, <1 turned off
   */
  public void setMaxTabCloseUndo(int value) {
    m_MaxTabCloseUndo = value;
  }

  /**
   * Returns the maximum tabs to keep around for undoing closing.
   *
   * @return		the maximum, <1 turned off
   */
  public int getMaxTabCloseUndo() {
    return m_MaxTabCloseUndo;
  }

  /**
   * Returns the tab undo list.
   *
   * @return		the list
   */
  protected TabUndoList getTabUndoList() {
    if (m_TabUndoList == null)
      m_TabUndoList = new TabUndoList();
    return m_TabUndoList;
  }

  /**
   * Generates a tab undo container.
   *
   * @param index	the index of the tab to back up
   * @return		the undo container
   */
  protected TabUndo generateTabUndo(int index) {
    TabUndo	undo;

    undo              = new TabUndo();
    undo.component    = getComponentAt(index);
    undo.title        = getTitleAt(index);
    undo.index        = index;
    undo.tiptext      = getToolTipTextAt(index);
    undo.icon         = getIconAt(index);
    undo.tabComponent = getTabComponentAt(index);
    undo.selected     = (index == getSelectedIndex());

    return undo;
  }

  /**
   * Adds the tab to its undo list, if enabled.
   *
   * @param index	the position of the tab
   */
  protected void addTabUndo(int index) {
    if ((m_MaxTabCloseUndo < 1) || m_SkipTabUndo)
      return;

    getTabUndoList().add(generateTabUndo(index));

    while (getTabUndoList().size() > m_MaxTabCloseUndo)
      getTabUndoList().remove(0, true);
  }

  /**
   * Returns whether a tab close can be undone.
   *
   * @return		true if possible
   */
  public boolean canUndoTabClose() {
    return (getTabUndoList().size() > 0);
  }

  /**
   * Performs an undo of a tab close.
   *
   * @return		true if successfully restored
   */
  public boolean undoTabClose() {
    TabUndo	undo;
    int		size;

    size = getTabUndoList().size();
    if (size < 1)
      return false;

    undo = getTabUndoList().get(size - 1);
    getTabUndoList().remove(size - 1, false);
    insertTab(undo.title, undo.icon, undo.component, undo.tiptext, undo.index);
    setTabComponentAt(undo.index, undo.tabComponent);
    if (undo.selected)
      setSelectedIndex(undo.index);

    return true;
  }

  /**
   * Removes the tab at <code>index</code>. Automatically handles the tab undo.
   * @param index the index of the tab to be removed
   * @throws IndexOutOfBoundsException if index is out of range
   *            {@code (index < 0 || index >= tab count)}
   */
  @Override
  public void removeTabAt(int index) {
    Component	comp;

    comp = getComponentAt(index);
    addTabUndo(index);
    if (getComponentAt(index) instanceof DetachablePanel)
      ((DetachablePanel) getComponentAt(index)).reattach();
    super.removeTabAt(index);
    notifyTabChangeListeners();
    notifyTabClosedListeners(index, comp);
  }

  /**
   * Removes all the tabs and their corresponding components
   * from the <code>tabbedpane</code>.
   * Bypasses undo for tabs, clears the undo list.
   */
  public void removeAll() {
    m_SkipTabUndo = true;
    super.removeAll();
    m_SkipTabUndo = false;
    getTabUndoList().clear();
    notifyTabChangeListeners();
  }

  /**
   * Returns whether the tab can be moved.
   *
   * @param from	the current tab's index
   * @param to 		the destination index
   * @return		true if can be moved
   */
  protected boolean canMoveTabTo(int from, int to) {
    return (from != to) && (to >= 0) && (to < getTabCount());
  }

  /**
   * Returns whether the tab can be moved to the start.
   *
   * @param from	the current tab's index
   * @return		true if can be moved
   */
  public boolean canMoveTabToStart(int from) {
    return canMoveTabTo(from, 0);
  }

  /**
   * Returns whether the tab can be moved to the left.
   *
   * @param from	the current tab's index
   * @return		true if can be moved
   */
  public boolean canMoveTabToLeft(int from) {
    return canMoveTabTo(from, from - 1);
  }

  /**
   * Returns whether the tab can be moved to the right.
   *
   * @param from	the current tab's index
   * @return		true if can be moved
   */
  public boolean canMoveTabToRight(int from) {
    return canMoveTabTo(from, from + 1);
  }

  /**
   * Returns whether the tab can be moved to the end.
   *
   * @param from	the current tab's index
   * @return		true if can be moved
   */
  public boolean canMoveTabToEnd(int from) {
    return canMoveTabTo(from, getTabCount() - 1);
  }

  /**
   * Moves a tab from A to B.
   *
   * @param from 	the current position
   * @param to		the new position
   */
  protected void moveTabTo(int from, int to) {
    TabUndo	undo;

    undo = generateTabUndo(from);
    removeTabAt(from);
    insertTab(undo.title, undo.icon, undo.component, undo.tiptext, to);
    setTabComponentAt(to, undo.tabComponent);
    if (undo.selected)
      setSelectedIndex(to);
  }

  /**
   * Moves the tab to the left most position.
   *
   * @param index	the tab's current index
   * @return		true if successfully moved
   */
  public boolean moveTabToStart(int index) {
    if (!canMoveTabTo(index, 0))
      return false;
    moveTabTo(index, 0);
    return true;
  }

  /**
   * Moves the tab to the left.
   *
   * @param index	the tab's current index
   * @return		true if successfully moved
   */
  public boolean moveTabToLeft(int index) {
    if (!canMoveTabTo(index, index - 1))
      return false;
    moveTabTo(index, index - 1);
    return true;
  }

  /**
   * Moves the tab to the right.
   *
   * @param index	the tab's current index
   * @return		true if successfully moved
   */
  public boolean moveTabToRight(int index) {
    if (!canMoveTabTo(index, index + 1))
      return false;
    moveTabTo(index, index + 1);
    return true;
  }

  /**
   * Moves the tab to the right most position.
   *
   * @param index	the tab's current index
   * @return		true if successfully moved
   */
  public boolean moveTabToEnd(int index) {
    if (!canMoveTabTo(index, getTabCount() - 1))
      return false;
    moveTabTo(index, getTabCount() - 1);
    return true;
  }

  /**
   * Generates the submenu for moving tabs.
   *
   * @param index	the current tab's index
   * @return		the submenu
   */
  public BaseMenu getTabMoveSubMenu(final int index) {
    BaseMenu	result;
    JMenuItem	menuitem;

    result = new BaseMenu("Move tab to");

    menuitem = new JMenuItem("Start");
    menuitem.setEnabled(canMoveTabToStart(index));
    menuitem.addActionListener((ActionEvent e) -> moveTabToStart(index));
    result.add(menuitem);

    menuitem = new JMenuItem("Left");
    menuitem.setEnabled(canMoveTabToLeft(index));
    menuitem.addActionListener((ActionEvent e) -> moveTabToLeft(index));
    result.add(menuitem);

    menuitem = new JMenuItem("Right");
    menuitem.setEnabled(canMoveTabToRight(index));
    menuitem.addActionListener((ActionEvent e) -> moveTabToRight(index));
    result.add(menuitem);

    menuitem = new JMenuItem("End");
    menuitem.setEnabled(canMoveTabToEnd(index));
    menuitem.addActionListener((ActionEvent e) -> moveTabToEnd(index));
    result.add(menuitem);

    return result;
  }

  /**
   * Adds the listener for changes in tabs.
   *
   * @param l		the listener to add
   */
  public void addTabChangeListener(ChangeListener l) {
    m_TabChangeListeners.add(l);
  }

  /**
   * Removes the listener for changes in tabs.
   *
   * @param l		the listener to remove
   */
  public void removeTabChangeListener(ChangeListener l) {
    m_TabChangeListeners.remove(l);
  }

  /**
   * Removes all tab change listeners.
   */
  public void clearTabChangeListeners() {
    m_TabChangeListeners.clear();
  }

  /**
   * Notifies all the tab change listeners
   */
  protected void notifyTabChangeListeners() {
    ChangeEvent		e;

    e = new ChangeEvent(this);
    for (ChangeListener l: m_TabChangeListeners)
      l.stateChanged(e);
  }

  /**
   * Adds the listener for closing tabs.
   *
   * @param l		the listener to add
   */
  public void addTabClosedListeners(TabClosedListener l) {
    m_TabClosedListeners.add(l);
  }

  /**
   * Removes the listener for closing tabs.
   *
   * @param l		the listener to remove
   */
  public void removeTabClosedListeners(TabClosedListener l) {
    m_TabClosedListeners.remove(l);
  }

  /**
   * Removes all tab closed listeners.
   */
  public void clearTabClosedListeners() {
    m_TabClosedListeners.clear();
  }

  /**
   * Notifies all the tab closed listeners
   *
   * @param tabIndex 	the index of the tab that got closed
   * @param component	the component that got removed
   */
  protected void notifyTabClosedListeners(int tabIndex, Component component) {
    TabClosedEvent e;

    e = new TabClosedEvent(this, tabIndex, component);
    for (TabClosedListener l: m_TabClosedListeners)
      l.tabClosed(e);
  }
}
