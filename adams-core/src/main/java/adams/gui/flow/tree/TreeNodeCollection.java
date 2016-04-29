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
 * TreeNodeCollection.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.tree;

import adams.gui.core.DragAndDropTreeNodeCollection;
import com.github.fracpete.jclipboardhelper.TransferableString;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

/**
 * Node collection for the flow editor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TreeNodeCollection
  extends DragAndDropTreeNodeCollection<Node> {

  /** for serialization. */
  private static final long serialVersionUID = 8624532382080344377L;

  /**
   * Initializes the collection with all the nodes.
   *
   * @param nodes	the nodes for the collection
   */
  public TreeNodeCollection(Node[] nodes) {
    super(nodes);
  }

  /**
   * Turns the node/userObject into a transferable string.
   * Uses the ClipboardActorContainer class to wrap around the actors from
   * the nodes.
   *
   * @return		the generated string
   * @see		ClipboardActorContainer
   */
  public Transferable toTransferable() {
    ClipboardActorContainer	result;
    int				i;

    result = new ClipboardActorContainer();
    for (i = 0; i < m_Nodes.size(); i++)
      result.add(i, m_Nodes.get(i).getFullActor());

    return new TransferableString(result.toNestedString());
  }

  /**
   * Recreates the node collection from a transferable string.
   *
   * @param tree	the tree the node collection is for
   * @param t		the transferable to use
   * @return		the node collection, null in case of an error
   */
  public static TreeNodeCollection fromTransferable(Tree tree, Transferable t) {
    TreeNodeCollection		result;
    ClipboardActorContainer	cont;
    Node[]			nodes;
    int				i;
    Object			data;

    result = null;
    try {
      data = t.getTransferData(DataFlavor.stringFlavor);
    }
    catch (Exception e) {
      System.err.println("Failed to obtain string data from transferable for TreeNodeCollection:");
      e.printStackTrace();
      data = null;
    }
    if (data != null) {
      cont = ClipboardActorContainer.fromNestedString(data.toString());
      if (cont != null) {
	nodes = new Node[cont.size()];
	for (i = 0; i < cont.size(); i++)
	  nodes[i] = new Node(tree, cont.get(i));
	result = new TreeNodeCollection(nodes);
      }
    }

    return result;
  }
}
