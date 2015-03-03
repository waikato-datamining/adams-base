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
 * PopupMenuHandler.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core.dotnotationtree;

import javax.swing.JPopupMenu;

/**
 * Interface for classes that generate a popup menu in the class tree.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface PopupMenuHandler {

  /**
   * Returns the popup menu for an item node.
   *
   * @param node	the item node
   * @param isLeaf	whether the node is the last node in this branch,
   * 			excluding any info nodes
   * @return		the popup or null if no popup available
   */
  public JPopupMenu getItemNodePopup(DotNotationNode node, boolean isLeaf);

  /**
   * Returns the popup menu for an info node.
   *
   * @param node	the info node
   * @return		the popup or null if no popup available
   */
  public JPopupMenu getInfoNodePopup(AbstractInfoNode node);
}
