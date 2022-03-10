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
 * AddMostCommonActor.java
 * Copyright (C) 2022 University of Waikato, Hamilton, NZ
 */
package adams.gui.flow.tree.menu;

import adams.gui.action.AbstractPropertiesAction;

/**
 * Menu for adding most common actors.
 * 
 * @author fracpete
 */
public class AddMostCommonActor
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
    return "Most common";
  }

  /**
   * Returns the sub menu actions.
   * 
   * @return		the submenu items
   */
  @Override
  protected AbstractPropertiesAction[] getSubMenuActions() {
    AbstractPropertiesAction[]	result;
    
    result = new AbstractPropertiesAction[]{
	new AddMostCommonActorBeneath(),
	null,
	new AddMostCommonActorHere(),
	new AddMostCommonActorAfter(),
    };
    
    return result;
  }
}
