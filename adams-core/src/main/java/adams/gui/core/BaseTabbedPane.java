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
 * BaseTabbedPane.java
 * Copyright (C) 2009-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

import adams.core.CleanUpHandler;
import adams.core.Shortening;

import javax.swing.Icon;
import javax.swing.JTabbedPane;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Enhanced JTabbedPane. Offers closing of tabs with middle mouse button.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
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
     * Method gets called
     */
    public boolean approveClosingWithMiddleMouseButton(BaseTabbedPane source);
  }

  /** Allows the user to close tabs with the middle mouse button. */
  protected boolean m_CloseTabsWithMiddleMouseButton;

  /** the approver for the middle mouse button. */
  protected MiddleMouseButtonCloseApprover m_MiddleMouseButtonCloseApprover;

  /** whether to show a "close tab" button. */
  protected boolean m_ShowCloseTabButton;

  /** the maximum length in chars for titles before getting shortened. */
  protected int m_MaxTitleLength;

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
    m_MaxTitleLength                 = 30;
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
  protected void tabClicked(MouseEvent e) {
    int		index;
    Component	comp;

    if (m_CloseTabsWithMiddleMouseButton) {
      index = indexAtLocation(e.getX(), e.getY());
      if ((index >= 0) && MouseUtils.isMiddleClick(e) && canCloseTabWithMiddleMouseButton(index)) {
	if ((m_MiddleMouseButtonCloseApprover != null) && !m_MiddleMouseButtonCloseApprover.approveClosingWithMiddleMouseButton(this))
	  return;
	comp = getComponentAt(index);
	removeTabAt(index);
	afterTabClosedWithMiddleMouseButton(index, comp);
      }
    }
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
    return true;
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
   * @return
   */
  public boolean getShowCloseTabButton() {
    return m_ShowCloseTabButton;
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
}
