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
 * TreeModel.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.tree;

import javax.swing.tree.DefaultTreeModel;

import adams.core.Destroyable;

/**
 * Specialized tree model for the flow editor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TreeModel
  extends DefaultTreeModel
  implements Destroyable {

  /** for serialization. */
  private static final long serialVersionUID = 5319049932782128289L;

  /**
   * Initializes the model with no nodes.
   */
  public TreeModel() {
    this(null);
  }

  /**
   * Initializes the tree with the specified root node.
   *
   * @param root	the root node, can be null
   */
  public TreeModel(Node root) {
    super(root);
  }

  /**
   * Recursively destroys all nodes (starting at the leaves).
   *
   * @param parent	the parent to go down from in the hierarchy
   */
  protected void destroy(Node parent) {
    int		i;

    for (i = 0; i < parent.getChildCount(); i++)
      destroy((Node) parent.getChildAt(i));

    parent.destroy();
  }

  /**
   * Frees up memory in a "destructive" non-reversible way.
   */
  public void destroy() {
    destroy((Node) getRoot());
  }
}
