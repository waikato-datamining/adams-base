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
 * DOMRenderer.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core.dom;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import adams.gui.core.GUIHelper;

/**
 * A specialized renderer for the DOMTree elements.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DOMRenderer
  extends DefaultTreeCellRenderer {

  /** for serialization. */
  private static final long serialVersionUID = -3242391430220560720L;

  /** the general icon for elements. */
  protected Icon m_Icon;

  /**
   * Initializes the renderer.
   */
  public DOMRenderer() {
    super();
    initialize();
  }

  /**
   * Initializes the members.
   */
  protected void initialize() {
    m_Icon = GUIHelper.getIcon("brackets2.gif");
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

    // icon available?
    if (value instanceof DOMNode) {
      setIcon(m_Icon);
      setBorder(BorderFactory.createEmptyBorder(1, 0, 1, 0));
    }

    return this;
  }
}