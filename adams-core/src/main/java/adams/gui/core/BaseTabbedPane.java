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
 * Copyright (C) 2009-2018 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

import adams.core.CleanUpHandler;
import adams.core.Shortening;
import adams.gui.dialog.ApprovalDialog;

import javax.swing.Icon;
import javax.swing.JTabbedPane;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

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

  /** Allows the user to close tabs with the middle mouse button. */
  protected boolean m_CloseTabsWithMiddleMouseButton;

  /** the approver for the middle mouse button. */
  protected MiddleMouseButtonCloseApprover m_MiddleMouseButtonCloseApprover;

  /** whether to show a "close tab" button. */
  protected boolean m_ShowCloseTabButton;

  /** whether to prompt user when closing a tab. */
  protected boolean m_PromptUserWhenClosingTab;

  /** the maximum length in chars for titles before getting shortened. */
  protected int m_MaxTitleLength;

  /** the maximum number of tabs to keep for undo. */
  protected int m_MaxTabCloseUndo;

  /** the list of tabs to undo. */
  protected transient List<TabUndo> m_TabUndoList;

  /** whether to skip tab undo. */
  protected boolean m_SkipTabUndo;

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
    m_PromptUserWhenClosingTab       = false;
    m_MaxTitleLength                 = 30;
    m_MaxTabCloseUndo                = 0;
    m_TabUndoList                    = null;
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
    int		i;

    m_ShowCloseTabButton = value;

    for (i = 0; i < getTabCount(); i++) {
      if (m_ShowCloseTabButton)
	setTabComponentAt(i, new ButtonTabComponent(this));
      else
	setTabComponentAt(i, null);
    }
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
    super.insertTab(title, icon, component, tip, index);
    if (m_ShowCloseTabButton)
      setTabComponentAt(index, new ButtonTabComponent(this));
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
    if ((getTabComponentAt(index) != null) && m_ShowCloseTabButton) {
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
  protected List<TabUndo> getTabUndoList() {
    if (m_TabUndoList == null)
      m_TabUndoList = new ArrayList<>();
    return m_TabUndoList;
  }

  /**
   * Adds the tab to its undo list, if enabled.
   *
   * @param index	the position of the tab
   */
  protected void addTabUndo(int index) {
    TabUndo	undo;

    if ((m_MaxTabCloseUndo < 1) || m_SkipTabUndo)
      return;

    undo              = new TabUndo();
    undo.component    = getComponentAt(index);
    undo.title        = getTitleAt(index);
    undo.index        = index;
    undo.tiptext      = getToolTipTextAt(index);
    undo.icon         = getIconAt(index);
    undo.tabComponent = getTabComponentAt(index);
    undo.selected     = (index == getSelectedIndex());

    getTabUndoList().add(undo);

    while (getTabUndoList().size() > m_MaxTabCloseUndo)
      getTabUndoList().remove(0);
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
    getTabUndoList().remove(size - 1);
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
    addTabUndo(index);
    super.removeTabAt(index);
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
  }
}
