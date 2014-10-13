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
 * Listeners.java
 * Copyright (C) 2014 University of Waikato, Hamilton, NZ
 */
package adams.gui.flow.tree.menu;

/**
 * Manages listeners (add/remove).
 * 
 * @author fracpete
 * @version $Revision$
 */
public class Listeners
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
    return "Listeners";
  }

  /**
   * Returns the name of the icon to use.
   * 
   * @return		the name, null if not applicable
   */
  @Override
  protected String getIconName() {
    return "listen.png";
  }
  
  /**
   * Returns the sub menu actions.
   * 
   * @return		the submenu items
   */
  @Override
  protected AbstractTreePopupAction[] getSubMenuActions() {
    AbstractTreePopupAction[]	result;
    
    result = new AbstractTreePopupAction[]{
	new AddActorBeneath(),
	null,
	new AddActorHere(),
	new AddActorAfter(),
    };
    
    return result;
  }
}
