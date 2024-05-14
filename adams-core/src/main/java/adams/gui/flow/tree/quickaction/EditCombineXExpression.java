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
 * EditCombineXExpression.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.flow.tree.quickaction;

import adams.core.Utils;
import adams.core.base.BaseText;
import adams.flow.core.Actor;
import adams.flow.source.CombineStorage;
import adams.flow.source.CombineVariables;
import adams.gui.core.GUIHelper;

import java.awt.Component;
import java.awt.event.ActionEvent;

/**
 * Lets the user edit the expression of a CombineVariables/Storage source.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class EditCombineXExpression
  extends AbstractTreeQuickMenuItemAction {

  private static final long serialVersionUID = -6455846796708144253L;

  /**
   * Returns the caption of this action.
   *
   * @return		the caption, null if not applicable
   */
  @Override
  protected String getTitle() {
    return "Edit expression...";
  }

  /**
   * Performs the actual update of the state of the action.
   */
  @Override
  protected void doUpdate() {
    boolean	validActor;

    validActor = (m_State.selNode.getActor() instanceof CombineVariables)
		   || (m_State.selNode.getActor() instanceof CombineStorage);
    setEnabled(m_State.editable && m_State.isSingleSel && validActor);
  }

  /**
   * Invoked when an action occurs.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    Component		parent;
    Actor 		actor;
    BaseText 		valueOld;
    String		valueOldStr;
    BaseText 		valueNew;
    String 		valueNewStr;

    parent = GUIHelper.getParentComponent(m_State.tree);

    // get old value
    actor = m_State.selNode.getActor();
    if (actor instanceof CombineVariables)
      valueOld = ((CombineVariables) actor).getExpression();
    else if (actor instanceof CombineStorage)
      valueOld = ((CombineStorage) actor).getExpression();
    else
      throw new IllegalStateException("Unhandled actor: " + Utils.classToString(actor));
    valueOldStr = valueOld.getValue();

    // enter new value
    valueNewStr = GUIHelper.showInputDialog(parent, "Enter the expression (Ctrl+Enter for new line):", valueOldStr, "Edit expression", null, 40, 5);
    if (valueNewStr == null)
      return;
    if (valueNewStr.equals(valueOldStr))
      return;
    valueNew = new BaseText(valueNewStr);

    // update actor
    addUndoPoint("Updated expression");
    if (actor instanceof CombineVariables)
      ((CombineVariables) actor).setExpression(valueNew);
    else if (actor instanceof CombineStorage)
      ((CombineStorage) actor).setExpression(valueNew);
    else
      throw new IllegalStateException("Unhandled actor: " + Utils.classToString(actor));
    updateSelectedActor(actor);
  }
}
