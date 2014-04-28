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
 * CellEditor.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.tree;

import java.util.EventObject;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellEditor;

/**
 * Simply avoids the editing of the HTML of the tree nodes.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CellEditor
  extends DefaultTreeCellEditor {

  /**
   * Constructs a <code>DefaultTreeCellEditor</code>
   * object for a JTree using the specified renderer and
   * a default editor. (Use this constructor for normal editing.)
   *
   * @param tree      a <code>JTree</code> object
   * @param renderer  a <code>DefaultTreeCellRenderer</code> object
   */
  public CellEditor(JTree tree, DefaultTreeCellRenderer renderer) {
    super(tree, renderer);
  }

  /**
   * Constructs a <code>DefaultTreeCellEditor</code>
   * object for a <code>JTree</code> using the
   * specified renderer and the specified editor. (Use this constructor
   * for specialized editing.)
   *
   * @param tree      a <code>JTree</code> object
   * @param renderer  a <code>DefaultTreeCellRenderer</code> object
   * @param editor    a <code>TreeCellEditor</code> object
   */
  public CellEditor(JTree tree, DefaultTreeCellRenderer renderer, TreeCellEditor editor) {
    super(tree, renderer, editor);
  }

  /**
   * If the <code>realEditor</code> returns true to this
   * message, <code>prepareForEditing</code>
   * is messaged and true is returned.
   *
   * @return		always false
   */
  public boolean isCellEditable(EventObject event) {
    return false;
  }
}
