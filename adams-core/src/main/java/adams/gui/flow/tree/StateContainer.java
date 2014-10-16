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
 * StateContainer.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.tree;

import javax.swing.tree.TreePath;

import adams.flow.execution.FlowExecutionListeningSupporter;
import adams.flow.template.AbstractActorTemplate;
import adams.gui.flow.tree.Tree.InsertPosition;

/**
 * Simple container that just captures the current state of things of the
 * flow tree, which allows menu items to decide whether to be executable or not.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StateContainer {
  
  /** the tree this state is for. */
  public Tree tree;
  
  /** the currently selected paths. */
  public TreePath[] selPaths;
  
  /** the number of selected nodes. */
  public int numSel;
  
  /** the node at the mouse position. */
  public Node nodeAtMouseLoc;
  
  /** whether only a single node is selected. */
  public boolean isSingleSel;
  
  /** the path in case of single selection. */
  public TreePath selPath;

  /** the node in case of single selection. */
  public Node selNode;
  
  /** the parent of the selected node. */
  public Node parent;
  
  /** whether the tree/node is editable. */
  public boolean editable;
  
  /** whether the node can be deleted. */
  public boolean canRemove;

  /** whether clipboard content can be pasted. */
  public boolean canPaste;

  /** whether the node is a mutable actor handler. */
  public boolean isMutable;

  /** whether the parent is a mutable actor handler. */
  public boolean isParentMutable;
  
  /** the last template that was used. */
  public AbstractActorTemplate lastTemplate;

  /** the position of the last template that was added via 'Add from template'. */
  public InsertPosition lastTemplateInsertPosition;
  
  /** the currently running flow. */
  public FlowExecutionListeningSupporter runningFlow;
  
  /**
   * Returns a short description of the container's content.
   */
  @Override
  public String toString() {
    StringBuilder	result;
    
    result = new StringBuilder();
    result.append("tree: @" + tree.hashCode() + "\n");
    result.append("selPaths:\n");
    for (TreePath path: selPaths)
      result.append(" - " + path + "\n");
    result.append("selPaths: " + numSel + "\n");
    result.append("nodeAtMouseLoc: " + nodeAtMouseLoc + "\n");
    result.append("isSingleSel: " + isSingleSel + "\n");
    result.append("selPath: " + selPath + "\n");
    result.append("selPath: " + selPath + "\n");
    result.append("parent: " + parent + "\n");
    result.append("editable: " + editable + "\n");
    result.append("canRemove: " + canRemove + "\n");
    result.append("canPaste: " + canPaste + "\n");
    result.append("isMutable: " + isMutable + "\n");
    result.append("isMutable: " + isMutable + "\n");
    result.append("isParentMutable: " + isParentMutable + "\n");
    result.append("lastTemplate: " + lastTemplate + "\n");
    result.append("lastTemplateInsertPosition: " + lastTemplateInsertPosition + "\n");
    result.append("runningFlow: " + runningFlow + "\n");
        
    return result.toString();
  }
}
