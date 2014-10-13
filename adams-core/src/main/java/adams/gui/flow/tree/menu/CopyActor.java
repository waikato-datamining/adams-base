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
 * CopyActor.java
 * Copyright (C) 2014 University of Waikato, Hamilton, NZ
 */
package adams.gui.flow.tree.menu;

import java.awt.event.ActionEvent;

import adams.gui.flow.FlowEditorPanel;

/**
 * For copying the currently selected actor(s) and placing them on the 
 * clipboard.
 * 
 * @author fracpete
 * @version $Revision$
 */
public class CopyActor
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
    return "Copy";
  }
  
  /**
   * Returns the name of the icon to use.
   * 
   * @return		the name, null if not applicable
   */
  @Override
  protected String getIconName() {
    return "copy.gif";
  }

  /**
   * Returns the key for the tree shortcut in the properties file.
   * 
   * @return		the key, null if not applicable
   * @see		FlowEditorPanel#getTreeShortcut(String)
   */
  @Override
  protected String getTreeShortCutKey() {
    return "Copy";
  }

  /**
   * Updates the action using the current state information.
   */
  @Override
  protected void doUpdate() {
    setEnabled((m_State.numSel > 0));
  }

  /**
   * The action to execute.
   *
   * @param e		the event
   */
  @Override
  public void actionPerformed(ActionEvent e) {
    m_State.tree.copyActors(m_State.selPaths);
  }
}
