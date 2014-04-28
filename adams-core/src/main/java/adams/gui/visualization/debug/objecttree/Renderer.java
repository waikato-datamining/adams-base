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
 * Renderer.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.debug.objecttree;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import adams.gui.core.GUIHelper;

/**
 * A specialized renderer for the tree elements.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Renderer
  extends DefaultTreeCellRenderer {

  /** for serialization. */
  private static final long serialVersionUID = 8669721980782126964L;

  /** the collapsed icon. */
  protected ImageIcon m_IconCollapsed;

  /** the expanded icon. */
  protected ImageIcon m_IconExpanded;

  /** the array element icon. */
  protected ImageIcon m_IconArrayElement;

  /** the hash icon. */
  protected ImageIcon m_IconHash;

  /** the empty icon. */
  protected ImageIcon m_IconObject;

  /**
   * Initializes the renderer.
   */
  public Renderer() {
    super();

    m_IconCollapsed    = GUIHelper.getIcon("folder_closed.png");
    m_IconExpanded     = GUIHelper.getIcon("folder_open.png");
    m_IconArrayElement = GUIHelper.getIcon("brackets.gif");
    m_IconHash         = GUIHelper.getIcon("hash.gif");
    m_IconObject       = GUIHelper.getIcon("object.gif");
  }

  /**
   * For rendering the cell.
   *
   * @param tree		the tree
   * @param value		the node
   * @param sel		whether the element is selected
   * @param expanded	whether the node is expanded
   * @param leaf		whether the node is a leaf
   * @param row		the row in the tree
   * @param hasFocus	whether the node is focused
   * @return		the rendering component
   */
  @Override
  public Component getTreeCellRendererComponent(
      JTree tree, Object value, boolean sel, boolean expanded,
      boolean leaf, int row, boolean hasFocus) {

    super.getTreeCellRendererComponent(
        tree, value, sel, expanded, leaf, row, hasFocus);

    ImageIcon icon = m_IconObject;
    Node node = null;
    if (value instanceof Node)
      node = (Node) value;
    if (leaf) {
      if (node != null) {
	switch (node.getNodeType()) {
	  case ARRAY_ELEMENT:
	    icon = m_IconArrayElement;
	    break;
	  case HASHCODE:
	    icon = m_IconHash;
	    break;
	}
      }
    }
    setIcon(icon);

    return this;
  }
}