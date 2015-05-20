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
 * SelectionModel.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.tree;

import java.util.ArrayList;

import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;

/**
 * The selection model for the flow editor tree.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SelectionModel
  extends DefaultTreeSelectionModel {

  /** for serialization. */
  private static final long serialVersionUID = 5457981531733561691L;

  /**
   * Initializes the selection model.
   */
  public SelectionModel() {
    super();
    setSelectionMode(DISCONTIGUOUS_TREE_SELECTION);
  }

  /**
   * Checks whether two paths have the (immediate) parent.
   * E.g., a.b.c.1 and a.b.c.2 have a.b.c as immediate parent (= true).
   * But a.b.c.1 and a.b.d.2 have only a.b. as parent (= false).
   *
   * @param path1	the first path
   * @param path2	the second path
   * @return		true if paths have the same immediate parent
   */
  protected boolean isSameParent(TreePath path1, TreePath path2) {
    boolean	result;

    result = (path1.getPathCount() == path2.getPathCount()) && (path1.getPathCount() > 1);
    if (result) {
      path1  = path1.getParentPath();
      path2  = path2.getParentPath();
      result = (path1.getLastPathComponent() == path2.getLastPathComponent());
    }

    return result;
  }

  /**
   * Returns the paths in the selection. This will return null (or an
   * empty array) if nothing is currently selected.
   * <br><br>
   * This implemention removes all selection paths that are not on the same
   * level as the first one.
   *
   * @return		the selected paths
   */
  public TreePath[] getSelectionPaths() {
    TreePath[]		paths;
    ArrayList<TreePath>	result;
    int			i;

    paths = super.getSelectionPaths();
    if (paths == null)
      return null;
    if (paths.length == 0)
      return paths;

    result = new ArrayList<TreePath>();
    for (i = 0; i < paths.length; i++) {
      // do leaves have same parent?
      if (result.size() > 0) {
	if (!isSameParent(paths[i], result.get(result.size() - 1)))
	  return new TreePath[0];
      }
      result.add(paths[i]);
    }

    return result.toArray(new TreePath[result.size()]);
  }
}
