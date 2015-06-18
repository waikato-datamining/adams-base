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
 * AbstractFlowEditorMenuItem.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.menu;

import adams.core.ClassLister;
import adams.core.logging.LoggingObject;
import adams.gui.action.AbstractBaseAction;
import adams.gui.flow.FlowEditorPanel;

/**
 * Ancestor for additional menu items in the flow editor.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
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
    m_Action = newAction();
  }
  
  /**
   * Creates the action to use.
   * 
   * @return		the action
   */
  protected abstract AbstractBaseAction newAction();
  
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
   * @see		FlowEditorPanel#MENU_DEBUG
   * @see		FlowEditorPanel#MENU_RUN
   * @see		FlowEditorPanel#MENU_VIEW
   * @see		FlowEditorPanel#MENU_WINDOW
   */
  public abstract String getMenu();
  
  /**
   * Returns the action to add to the flow editor menu.
   * 
   * @return		the action to add
   */
  public AbstractBaseAction getAction() {
    return m_Action;
  }
  
  /**
   * Updating the action, based on the current status of the owner.
   */
  public abstract void updateAction();
  
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
    if (o == null)
      return 1;
    return getAction().getName().compareTo(o.getAction().getName());
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
  public static String[] getMenuItems() {
    return ClassLister.getSingleton().getClassnames(AbstractFlowEditorMenuItem.class);
  }
}
