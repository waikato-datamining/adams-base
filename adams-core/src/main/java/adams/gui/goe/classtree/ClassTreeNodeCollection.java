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
 * ClassTreeNodeCollection.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.goe.classtree;

import adams.gui.core.DragAndDropTreeNodeCollection;
import com.github.fracpete.jclipboardhelper.TransferableString;

import java.awt.datatransfer.Transferable;

/**
 * Node collection for the class tree.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ClassTreeNodeCollection
  extends DragAndDropTreeNodeCollection<ClassNode> {

  /** for serialization. */
  private static final long serialVersionUID = 8624532382080344377L;

  /**
   * Initializes the collection with all the nodes.
   *
   * @param nodes	the nodes for the collection
   */
  public ClassTreeNodeCollection(ClassNode[] nodes) {
    super(nodes);
  }

  /**
   * Turns the node/userObject into a transferable string.
   * Uses only the first node.
   *
   * @return		the generated string
   */
  public Transferable toTransferable() {
    return new TransferableString(m_Nodes.get(0).getItem());
  }
}
