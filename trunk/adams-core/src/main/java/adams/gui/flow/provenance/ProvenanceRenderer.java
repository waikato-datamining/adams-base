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
 * ProvenanceRenderer.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.flow.provenance;

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
public class ProvenanceRenderer
  extends DefaultTreeCellRenderer {

  /** for serialization. */
  private static final long serialVersionUID = -4642238450035078932L;

  /** the collapsed icon. */
  protected ImageIcon m_IconCollapsed;

  /** the expanded icon. */
  protected ImageIcon m_IconExpanded;

  /**
   * Initializes the renderer.
   */
  public ProvenanceRenderer() {
    super();

    m_IconCollapsed = GUIHelper.getIcon("folder_closed.png");
    m_IconExpanded  = GUIHelper.getIcon("folder_open.png");
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
  public Component getTreeCellRendererComponent(
      JTree tree, Object value, boolean sel, boolean expanded,
      boolean leaf, int row, boolean hasFocus) {

    super.getTreeCellRendererComponent(
        tree, value, sel, expanded, leaf, row, hasFocus);

    ImageIcon icon = null;
    if (!leaf) {
      if (expanded)
	icon = m_IconExpanded;
      else
	icon = m_IconCollapsed;
    }
    setIcon(icon);

    return this;
  }
}