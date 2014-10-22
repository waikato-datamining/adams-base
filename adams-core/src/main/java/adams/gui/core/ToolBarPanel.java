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
 * ToolBarPanel.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JToolBar;

import adams.gui.action.AbstractBaseAction;

/**
 * Specialized panel with a toolbar. The actual content panel is accessible
 * using <code>getContentPanel()</code>.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class ToolBarPanel
  extends BasePanel {

  /** for serialization. */
  private static final long serialVersionUID = -5948851044915629484L;

  /**
   * The location of the toolbar.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum ToolBarLocation {
    /** hides the toolbar. */
    HIDDEN,
    /** top. */
    NORTH,
    /** bottom. */
    SOUTH,
    /** left. */
    WEST,
    /** right. */
    EAST
  }

  /** the location of the toolbar. */
  protected ToolBarLocation m_Location;

  /** the toolbar. */
  protected JToolBar m_ToolBar;

  /** the actual content panel. */
  protected BasePanel m_ContentPanel;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Location = ToolBarLocation.NORTH;
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_ContentPanel = new BasePanel(new BorderLayout());
    add(m_ContentPanel, BorderLayout.CENTER);

    m_ToolBar = new JToolBar();
    m_ToolBar.setFloatable(false);
    add(m_ToolBar, BorderLayout.NORTH);
  }

  /**
   * Sets up all the actions.
   *
   * @see		AbstractBaseAction
   */
  protected abstract void initActions();

  /**
   * Sets up the toolbar, using the actions.
   *
   * @see		#initActions()
   */
  protected abstract void initToolBar();

  /**
   * Finishes the initialization, setting up actions and toolbar.
   *
   * @see		#initActions()
   * @see		#initToolBar()
   * @see		#updateActions()
   */
  @Override
  protected void finishInit() {
    super.finishInit();

    initActions();
    initToolBar();
    updateActions();
  }

  /**
   * Sets the location of the toolbar.
   *
   * @param value	the location of the toolbar
   */
  public void setToolBarLocation(ToolBarLocation value) {
    m_Location = value;
    switch (m_Location) {
      case HIDDEN:
	m_ToolBar.setVisible(false);
	break;
      case NORTH:
	add(m_ToolBar, BorderLayout.NORTH);
	m_ToolBar.setOrientation(JToolBar.HORIZONTAL);
	m_ToolBar.setVisible(true);
	break;
      case SOUTH:
	add(m_ToolBar, BorderLayout.SOUTH);
	m_ToolBar.setOrientation(JToolBar.HORIZONTAL);
	m_ToolBar.setVisible(true);
	break;
      case WEST:
	add(m_ToolBar, BorderLayout.WEST);
	m_ToolBar.setOrientation(JToolBar.VERTICAL);
	m_ToolBar.setVisible(true);
	break;
      case EAST:
	add(m_ToolBar, BorderLayout.EAST);
	m_ToolBar.setOrientation(JToolBar.VERTICAL);
	m_ToolBar.setVisible(true);
	break;
      default:
	throw new IllegalArgumentException("Unhandled toolbar location: " + value);
    }
  }

  /**
   * Returns the location of the toolbar.
   *
   * @return		the location
   */
  public ToolBarLocation getToolBarLocation() {
    return m_Location;
  }

  /**
   * Adds the specified action to the toolbar.
   *
   * @param action	the action to add
   */
  public void addToToolBar(Action action) {
    m_ToolBar.add(action);
  }

  /**
   * Adds the specified component to the toolbar.
   *
   * @param comp	the component to add
   */
  public void addToToolBar(JComponent comp) {
    m_ToolBar.add(comp);
  }

  /**
   * Removes the specified component from the toolbar.
   *
   * @param comp	the component to remove
   */
  public void removeFromToolBar(JComponent comp) {
    m_ToolBar.remove(comp);
  }

  /**
   * Adds a separator to the toolbar.
   */
  public void addSeparator() {
    m_ToolBar.addSeparator();
  }

  /**
   * Returns the number of components in the toolbar.
   *
   * @return		the number of components
   */
  public int getToolBarComponentCount() {
    return m_ToolBar.getComponentCount();
  }

  /**
   * Returns the component at the specified index.
   *
   * @param index	the index of the component to retrieve
   * @return		the component at the index
   */
  public Component getToolBarComponentAtIndex(int index) {
    return m_ToolBar.getComponentAtIndex(index);
  }

  /**
   * Returns the underlying toolbar.
   *
   * @return		the toolbar
   */
  public JToolBar getToolBar() {
    return m_ToolBar;
  }

  /**
   * Returns the content panel.
   *
   * @return		the content panel
   */
  public BasePanel getContentPanel() {
    return m_ContentPanel;
  }

  /**
   * Sets whether the toolbar is to be floatable or not, i.e., whether the user
   * can move the toolbar around.
   *
   * @param value	if true the user will be able to move the toolbar
   */
  public void setFloatable(boolean value) {
    m_ToolBar.setFloatable(value);
  }

  /**
   * Returns whether the toolbar is floatable or not, i.e., whether the user
   * can move the toolbar around.
   *
   * @return		true if the user can move the toolbar
   */
  public boolean isFloatable() {
    return m_ToolBar.isFloatable();
  }

  /**
   * Updates the enabled state of the actions.
   */
  protected abstract void updateActions();
}
