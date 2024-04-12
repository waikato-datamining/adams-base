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
 * ChangeVariableValue.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.flow.tree.quickaction;

import adams.core.MessageCollection;
import adams.core.base.BaseText;
import adams.core.discovery.IntrospectionHelper;
import adams.core.discovery.PropertyPath;
import adams.core.option.UserMode;
import adams.flow.core.Actor;
import adams.gui.core.GUIHelper;
import adams.gui.core.UserModeUtils;

import java.awt.Component;
import java.awt.event.ActionEvent;

/**
 * Changes the value of the variable.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class ChangeVariableValue
  extends AbstractTreeQuickMenuItemAction {

  private static final long serialVersionUID = -6455846796708144253L;

  public static final String VARIABLE_VALUE = "variableValue";

  /**
   * Returns the caption of this action.
   *
   * @return		the caption, null if not applicable
   */
  @Override
  protected String getTitle() {
    return "Change variable value...";
  }

  /**
   * Performs the actual update of the state of the action.
   */
  @Override
  protected void doUpdate() {
    UserMode	userMode;
    boolean	hasMethod;

    userMode  = UserModeUtils.getUserMode(m_State.tree);
    hasMethod = IntrospectionHelper.hasProperty(
      m_State.selNode.getActor().getClass(), VARIABLE_VALUE, BaseText.class, userMode);
    setEnabled(m_State.editable && m_State.isSingleSel && hasMethod);
  }

  /**
   * Invoked when an action occurs.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    Component		parent;
    Actor		actorOld;
    Actor 		actorNew;
    BaseText 		valueOld;
    BaseText 		valueNew;
    String 		value;
    MessageCollection	errors;
    boolean		updated;

    parent = GUIHelper.getParentComponent(m_State.tree);

    // get old value
    actorOld = m_State.selNode.getActor();
    errors   = new MessageCollection();
    valueOld = (BaseText) PropertyPath.getValue(actorOld, VARIABLE_VALUE, errors);
    if (valueOld == null)
      valueOld = new BaseText();

    // enter new value
    value = GUIHelper.showInputDialog(parent, "Please enter the new variable value:", valueOld.getValue());
    if (value == null)
      return;
    valueNew = new BaseText(value);
    if (valueNew.getValue().equals(valueOld.getValue()))
      return;

    // update actor
    actorNew = actorOld.shallowCopy();
    updated  = PropertyPath.setValue(actorNew, VARIABLE_VALUE, valueNew, errors);
    if (!updated) {
      GUIHelper.showErrorMessage(parent, "Failed to update variable value!");
      return;
    }
    addUndoPoint("Changed variable value to: " + valueNew);
    updateSelectedActor(actorNew);
  }
}
