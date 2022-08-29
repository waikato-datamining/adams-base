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
 * CopyActorCommandline.java
 * Copyright (C) 2022 University of Waikato, Hamilton, NZ
 */
package adams.gui.flow.tree.menu;

import adams.gui.flow.tree.Node;
import adams.gui.flow.tree.TreeHelper;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;

import java.awt.event.ActionEvent;

/**
 * For copying the currently selected actor and placing it on the
 * clipboard.
 * 
 * @author fracpete
 */
public class CopyActorCommandline
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
    return "Copy (command-line)";
  }

  /**
   * Updates the action using the current state information.
   */
  @Override
  protected void doUpdate() {
    setEnabled((m_State.numSel == 1));
  }

  /**
   * The action to execute.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    Node[]		nodes;

    nodes = TreeHelper.pathsToNodes(m_State.selPaths);
    if (nodes.length != 1)
      return;

    ClipboardHelper.copyToClipboard(nodes[0].getFullActor().toCommandLine());
  }
}
