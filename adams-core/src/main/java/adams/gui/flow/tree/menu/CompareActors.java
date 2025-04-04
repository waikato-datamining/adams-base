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
 * CompareActors.java
 * Copyright (C) 2016-2019 University of Waikato, Hamilton, NZ
 */
package adams.gui.flow.tree.menu;

import adams.gui.action.AbstractPropertiesAction;

/**
 * Performs a diff on two actors.
 * 
 * @author fracpete
 */
public class CompareActors
  extends AbstractTreePopupSubMenuAction {

  /** for serialization. */
  private static final long serialVersionUID = 3991575839421394939L;
  
  /**
   * Returns the caption of this action.
   * 
   * @return		the caption, null if not applicable
   */
  @Override
  protected String getTitle() {
    return "Compare actors";
  }

  /**
   * Returns any sub menu actions. By default, this method returns null.
   * Override this method when creating a submenu, use "null" in an array
   * element to create a separator.
   *
   * @return		the submenu actions
   */
  @Override
  protected AbstractPropertiesAction[] getSubMenuActions() {
    return new AbstractPropertiesAction[]{
      new CompareJustActors(),
      new CompareSubFlows(),
    };
  }

  /**
   * Updates the action using the current state information.
   */
  @Override
  protected void doUpdate() {
    setEnabled(	     
	   m_State.editable 
	&& (m_State.numSel == 2)
	&& (m_State.tree.getOwner() != null));
  }
}
