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
 * EditExternalFlow.java
 * Copyright (C) 2014 University of Waikato, Hamilton, NZ
 */
package adams.gui.flow.tree.menu;

import java.awt.event.ActionEvent;

import adams.core.io.FlowFile;
import adams.flow.core.ExternalActorHandler;
import adams.gui.flow.FlowEditorPanel;

/**
 * For editing an external flow.
 * 
 * @author fracpete
 * @version $Revision$
 */
public class EditExternalFlow
  extends AbstractTreePopupMenuItemAction {

  /** for serialization. */
  private static final long serialVersionUID = 3991575839421394939L;
  
  /**
   * Returns the caption of this action.
   * 
   * @return		the caption, null if not applicable
   */
  @Override
  protected String getTitle() {
    return "Edit...";
  }
  
  /**
   * Returns the name of the icon to use.
   * 
   * @return		the name, null if not applicable
   */
  @Override
  protected String getIconName() {
    return "flow.gif";
  }

  /**
   * Returns the key for the tree shortcut in the properties file.
   * 
   * @return		the key, null if not applicable
   * @see		FlowEditorPanel#getTreeShortcut(String)
   */
  @Override
  protected String getTreeShortCutKey() {
    return "Edit.Flow";
  }

  /**
   * Updates the action using the current state information.
   */
  @Override
  protected void doUpdate() {
    boolean enabled = m_State.editable && m_State.isSingleSel && (m_State.selNode.getActor() instanceof ExternalActorHandler);
    if (enabled) {
      FlowFile file = ((ExternalActorHandler) m_State.selNode.getActor()).getActorFile();
      enabled = file.exists() && !file.isDirectory();
    }
    setEnabled(enabled);
  }

  /**
   * The action to execute.
   *
   * @param e		the event
   */
  @Override
  public void actionPerformed(ActionEvent e) {
    m_State.tree.editFlow(m_State.selPath);
  }
}
