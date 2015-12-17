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
 * CreateCallableActor.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */
package adams.gui.flow.tree.menu;

import adams.core.ClassLister;
import adams.flow.core.CallableActorHandler;
import adams.gui.action.AbstractPropertiesAction;
import adams.gui.core.GUIHelper;

import javax.swing.ImageIcon;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Menu for turning actors into callable actors, using the specified
 * callable actor handler.
 * 
 * @author fracpete
 * @version $Revision$
 */
public class CreateCallableActor
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
    return "Create callable actor";
  }

  /**
   * Returns the sub menu actions.
   * 
   * @return		the submenu items
   */
  @Override
  protected AbstractPropertiesAction[] getSubMenuActions() {
    List<AbstractPropertiesAction> 	result;
    String[]				clsnames;
    AbstractPropertiesAction		action;
    ImageIcon				icon;

    result   = new ArrayList<>();
    clsnames = ClassLister.getSingleton().getClassnames(CallableActorHandler.class);
    for (String clsname: clsnames) {
      try {
	final Class cls = Class.forName(clsname);
	action = new AbstractTreePopupMenuItemAction() {
	  private static final long serialVersionUID = -8553715825229272758L;
	  @Override
	  protected String getTitle() {
	    return cls.getSimpleName();
	  }
	  @Override
	  protected void doUpdate() {
	    setEnabled(m_State.editable && m_State.isSingleSel && (m_State.tree.getOwner() != null));
	  }
	  @Override
	  protected void doActionPerformed(ActionEvent e) {
	    m_State.tree.getOperations().createCallableActor(m_State.selPath, cls);
	  }
	};
	icon = GUIHelper.getIcon(clsname);
	if (icon != null)
	  action.setIcon(icon);
	result.add(action);
      }
      catch (Exception e) {
	System.err.println("Failed to instantiate callable actor handler: " + clsname);
	e.printStackTrace();
      }
    }

    return result.toArray(new AbstractPropertiesAction[result.size()]);
  }
}
