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
 * AbstractFlowEditorMenuItem.java
 * Copyright (C) 2012-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.menu;

import adams.core.ClassLister;
import adams.core.logging.LoggingObject;
import adams.gui.action.AbstractBaseAction;
import adams.gui.flow.FlowEditorPanel;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * Ancestor for additional menu items in the flow editor.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractFlowEditorMenuItem
  extends LoggingObject 
  implements Comparable<AbstractFlowEditorMenuItem> {

  /** for serialization. */
  private static final long serialVersionUID = -6136183940974324177L;
  
  /** the owner. */
  protected FlowEditorPanel m_Owner;
  
  /** the underlying action. */
  protected AbstractBaseAction m_Action;

  /** the underlying menuitem. */
  protected JMenuItem m_MenuItem;

  /** the underlying submenu. */
  protected JMenu m_SubMenu;

  /**
   * Initializes the menu item.
   */
  protected AbstractFlowEditorMenuItem() {
    super();
    initialize();
  }

  /**
   * Initializes the menu item.
   */
  protected void initialize() {
    if (hasAction())
      m_Action = newAction();
    else if (hasMenuItem())
      m_MenuItem = newMenuItem();
    else if (hasSubMenu())
      m_SubMenu = newSubMenu();
  }

  /**
   * Returns whether the menu item is based on an action.
   * <br/>
   * Default implementation returns true.
   *
   * @return		true if action-based
   */
  public boolean hasAction() {
    return true;
  }

  /**
   * Creates the action to use.
   *
   * @return		the action
   */
  protected abstract AbstractBaseAction newAction();

  /**
   * Returns the action to add to the flow editor menu.
   *
   * @return		the action to add
   * @see		#hasAction()
   */
  public AbstractBaseAction getAction() {
    return m_Action;
  }

  /**
   * Returns whether the menu item is based on a menuitem.
   * <br/>
   * Default implementation returns false.
   *
   * @return		true if menuitem-based
   */
  public boolean hasMenuItem() {
    return false;
  }

  /**
   * Creates the menuitem to use.
   * <br/>
   * Default implementation returns null.
   *
   * @return		the menuitem
   */
  protected JMenuItem newMenuItem() {
    return null;
  }

  /**
   * Returns the menuitem to add to the flow editor menu.
   *
   * @return		the menuitem to add
   * @see		#hasMenuItem()
   */
  public JMenuItem getMenuItem() {
    return m_MenuItem;
  }

  /**
   * Returns whether the menu item is based on a submenu.
   * <br/>
   * Default implementation returns false.
   *
   * @return		true if submenu-based
   */
  public boolean hasSubMenu() {
    return false;
  }

  /**
   * Creates the submenu to use.
   * <br/>
   * Default implementation returns null.
   *
   * @return		the submenu
   */
  protected JMenu newSubMenu() {
    return null;
  }

  /**
   * Returns the submenu to add to the flow editor menu.
   *
   * @return		the submenu to add
   * @see		#hasSubMenu()
   */
  public JMenu getSubMenu() {
    return m_SubMenu;
  }

  /**
   * Sets the owning flow editor.
   * 
   * @param value	the owner
   */
  public void setOwner(FlowEditorPanel value) {
    m_Owner = value;
  }
  
  /**
   * Returns the owner.
   * 
   * @return		the owner, null if none set
   */
  public FlowEditorPanel getOwner() {
    return m_Owner;
  }
  
  /**
   * Returns the name of the menu to list this item under.
   * 
   * @return		the name of the menu
   * @see		FlowEditorPanel#MENU_FILE
   * @see		FlowEditorPanel#MENU_EDIT
   * @see		FlowEditorPanel#MENU_RUN
   * @see		FlowEditorPanel#MENU_ACTIVE
   * @see		FlowEditorPanel#MENU_VIEW
   * @see		FlowEditorPanel#MENU_WINDOW
   * @see		FlowEditorPanel#MENU_HELP
   */
  public abstract String getMenu();

  /**
   * Updating the action/menuitem/submenu, based on the current status of the owner.
   */
  public abstract void update();

  /**
   * Determines the caption of the menu item.
   *
   * @param item	the item
   * @return		the caption, empty string if unable to find one
   */
  protected String determineCaption(AbstractFlowEditorMenuItem item) {
    if (item.hasAction())
      return item.getAction().getName();
    else if (item.hasMenuItem())
      return item.getMenuItem().getText();
    else if (item.hasSubMenu())
      return item.getSubMenu().getText();
    else
      return "";
  }

  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   * <br><br>
   * Uses the name of the menu item text for comparison.
   *
   * @param   o the object to be compared.
   * @return  a negative integer, zero, or a positive integer as this object
   *		is less than, equal to, or greater than the specified object.
   */
  @Override
  public int compareTo(AbstractFlowEditorMenuItem o) {
    String 	captionThis;
    String 	captionOther;

    if (o == null)
      return 1;

    captionThis  = determineCaption(this);
    captionOther = determineCaption(o);

    return captionThis.compareTo(captionOther);
  }

  /**
   * Returns whether the two objects are the same.
   * <br><br>
   * Only compares the menu item text of the two objects.
   *
   * @param o	the object to be compared
   * @return	true if the object is the same as this one
   */
  @Override
  public boolean equals(Object o) {
    if (o instanceof AbstractFlowEditorMenuItem)
      return (compareTo((AbstractFlowEditorMenuItem) o) == 0);
    else
      return false;
  }

  /**
   * Returns a list with classnames of menu items.
   *
   * @return		the menu item classnames
   */
  public static Class[] getMenuItems() {
    return ClassLister.getSingleton().getClasses(AbstractFlowEditorMenuItem.class);
  }
}
