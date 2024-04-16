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
 * AbstractGOEQuickAction.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.flow.tree.quickaction;

import adams.core.MessageCollection;
import adams.core.discovery.IntrospectionHelper;
import adams.core.discovery.PropertyPath;
import adams.core.option.OptionUtils;
import adams.core.option.UserMode;
import adams.flow.core.Actor;
import adams.gui.core.GUIHelper;
import adams.gui.core.UserModeUtils;
import adams.gui.goe.GenericObjectEditorDialog;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;

/**
 * Ancestor for GOE-based quick actions.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractGOEQuickAction
  extends AbstractTreeQuickMenuItemAction {

  private static final long serialVersionUID = -6455846796708144253L;

  /**
   * Returns the caption of this action.
   *
   * @return		the caption, null if not applicable
   */
  @Override
  protected String getTitle() {
    return "Edit " + getClassDescription() + "...";
  }

  /**
   * Returns the property name to look for.
   *
   * @return		the name
   */
  protected abstract String getPropertyName();

  /**
   * The abstract superclass to use.
   *
   * @return		the superclass
   */
  protected abstract Class getSuperclass();

  /**
   * Returns the default object to use.
   *
   * @return		the default object
   */
  protected abstract Object getDefaultObject();

  /**
   * Returns the description of the class used in errors/undo points.
   *
   * @return		the description
   */
  protected abstract String getClassDescription();

  /**
   * Performs the actual update of the state of the action.
   */
  @Override
  protected void doUpdate() {
    UserMode	userMode;
    boolean	hasMethod;

    userMode  = UserModeUtils.getUserMode(m_State.tree);
    hasMethod = IntrospectionHelper.hasProperty(
      m_State.selNode.getActor().getClass(), getPropertyName(), getSuperclass(), userMode);
    setEnabled(m_State.editable && m_State.isSingleSel && hasMethod);
  }

  /**
   * Invoked when an action occurs.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    Component			parent;
    Actor			actorOld;
    Actor 			actorNew;
    Object 			objOld;
    Object 			objNew;
    GenericObjectEditorDialog	dialog;
    MessageCollection		errors;
    boolean			updated;

    parent = GUIHelper.getParentComponent(m_State.tree);

    // get old classifier
    actorOld = m_State.selNode.getActor();
    errors   = new MessageCollection();
    objOld = PropertyPath.getValue(actorOld, getPropertyName(), errors);
    if (objOld == null)
      objOld = getDefaultObject();

    // enter new name
    dialog = GenericObjectEditorDialog.createDialog((Container) parent);
    dialog.getGOEEditor().setClassType(getSuperclass());
    dialog.getGOEEditor().setCanChangeClassInDialog(true);
    dialog.setCurrent(objOld);
    dialog.pack();
    dialog.setLocationRelativeTo(parent);
    dialog.setVisible(true);
    if (dialog.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
      return;

    objNew = dialog.getCurrent();
    if (OptionUtils.getCommandLine(objOld).equals(OptionUtils.getCommandLine(objNew)))
      return;

    // update actor
    actorNew = actorOld.shallowCopy();
    updated  = PropertyPath.setValue(actorNew, getPropertyName(), objNew, errors);
    if (!updated) {
      GUIHelper.showErrorMessage(parent, "Failed to update " + getClassDescription() + "!");
      return;
    }
    addUndoPoint("Updated " + getClassDescription());
    updateSelectedActor(actorNew);
  }
}
