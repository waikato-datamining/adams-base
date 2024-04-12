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
 * AbstractTreeQuickMenuItemAction.java
 * Copyright (C) 2024 University of Waikato, Hamilton, NZ
 */
package adams.gui.flow.tree.quickaction;

import adams.core.Properties;
import adams.flow.core.Actor;
import adams.gui.action.AbstractPropertiesMenuItemAction;
import adams.gui.event.ActorChangeEvent;
import adams.gui.event.ActorChangeEvent.Type;
import adams.gui.flow.FlowEditorPanel;
import adams.gui.flow.tree.StateContainer;
import adams.gui.goe.GenericObjectEditorDialog;

import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.Dialog;
import java.awt.Frame;
import java.util.List;

/**
 * Ancestor for menu items in the quick action menu of the flow tree.
 * 
 * @author fracpete
 */
public abstract class AbstractTreeQuickMenuItemAction
  extends AbstractPropertiesMenuItemAction<StateContainer, GenericObjectEditorDialog>
  implements TreeQuickAction {

  /** for serialization. */
  private static final long serialVersionUID = -5921557331961517641L;
  
  /**
   * Returns the underlying properties.
   * 
   * @return		the properties
   */
  @Override
  protected Properties getProperties() {
    return FlowEditorPanel.getPropertiesTreeQuickAction();
  }
  
  /**
   * Checks whether the keystroke matches.
   * 
   * @param ks		the keystroke to match
   * @return		true if a match
   */
  public boolean keyStrokeApplies(KeyStroke ks) {
    return hasAccelerator() && ks.equals(getAccelerator());
  }
  
  /**
   * Tries to determine the frame this panel is part of.
   *
   * @return		the parent frame if one exists or null if not
   */
  protected Frame getParentFrame() {
    if (m_State != null)
      return m_State.tree.getParentFrame();
    else
      return null;
  }

  /**
   * Tries to determine the dialog this panel is part of.
   *
   * @return		the parent dialog if one exists or null if not
   */
  protected Dialog getParentDialog() {
    if (m_State != null)
      return m_State.tree.getParentDialog();
    else
      return null;
  }
  
  /**
   * Adds an undo point with the given comment.
   *
   * @param comment	the comment for the undo point
   */
  public void addUndoPoint(String comment) {
    if (m_State != null)
      m_State.tree.addUndoPoint(comment);
  }

  /**
   * Updates the currently selected actor and refreshes the tree.
   *
   * @param newActor	the new actor to set
   */
  protected void updateSelectedActor(Actor newActor) {
    List<TreePath> exp;

    exp = m_State.tree.getExpandedTreePaths();
    m_State.selNode.setActor(newActor);
    ((DefaultTreeModel) m_State.tree.getModel()).nodeChanged(m_State.selNode);
    m_State.tree.setModified(true);
    m_State.tree.notifyActorChangeListeners(new ActorChangeEvent(m_State.tree, m_State.selNode, Type.MODIFY));
    SwingUtilities.invokeLater(() -> m_State.tree.setExpandedTreePaths(exp));
  }
}
