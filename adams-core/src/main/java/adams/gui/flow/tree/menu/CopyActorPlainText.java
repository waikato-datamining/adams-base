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
 * CopyActorPlainText.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */
package adams.gui.flow.tree.menu;

import adams.data.io.output.DefaultFlowWriter;
import adams.gui.core.GUIHelper;
import adams.gui.flow.tree.ClipboardActorContainer;
import adams.gui.flow.tree.Node;
import adams.gui.flow.tree.TreeHelper;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;

import java.awt.event.ActionEvent;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * For copying the currently selected actor(s) and placing them on the 
 * clipboard.
 * 
 * @author fracpete
 */
public class CopyActorPlainText
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
    return "Copy (plain text)";
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
  protected void doActionPerformed(ActionEvent e) {
    Node[]		nodes;
    List 		data;
    DefaultFlowWriter	fwriter;
    StringWriter	swriter;

    nodes = TreeHelper.pathsToNodes(m_State.selPaths);
    if (nodes.length == 1) {
      data = TreeHelper.getNested(nodes[0]);
    }
    else {
      data = new ArrayList();
      data.add(ClipboardActorContainer.class.getName());
      for (Node node: nodes)
        data.add(TreeHelper.getNested(node));
    }

    swriter = new StringWriter();
    fwriter = new DefaultFlowWriter();
    fwriter.setUseCompact(true);
    if (fwriter.write(data, swriter))
      ClipboardHelper.copyToClipboard(swriter.toString());
    else
      GUIHelper.showErrorMessage(getParentDialog(), "Failed to copy actor(s) to clipboard!");
  }
}
