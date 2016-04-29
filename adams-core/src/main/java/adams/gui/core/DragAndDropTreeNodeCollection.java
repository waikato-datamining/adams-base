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
 * DragAndDropTreeNodeCollection.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

import com.github.fracpete.jclipboardhelper.TransferableString;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * Helper class for drag-n-drop in the DragAndDropTree.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <N> the type of node to handle
 */
public class DragAndDropTreeNodeCollection<N extends BaseTreeNode>
  implements Transferable, Serializable, Collection<N> {

  /** for serialization. */
  private static final long serialVersionUID = -7713547721629308103L;

  /** the nodes. */
  protected ArrayList<N> m_Nodes;

  /**
   * Initializes the collection.
   */
  private DragAndDropTreeNodeCollection() {
    super();

    m_Nodes = new ArrayList<N>();
  }

  /**
   * Initializes the collection with all the nodes.
   *
   * @param nodes	the nodes for the collection
   */
  public DragAndDropTreeNodeCollection(N[] nodes) {
    this();
    m_Nodes.addAll(Arrays.asList(nodes));
  }

  /**
   * Returns the size of the collection.
   *
   * @return		the number of nodes stored
   */
  public int size() {
    return m_Nodes.size();
  }

  /**
   * Returns whether the collection is empty.
   *
   * @return		true if no nodes stored
   */
  public boolean isEmpty() {
    return m_Nodes.isEmpty();
  }

  /**
   * Checks whether the object is stored among the nodes.
   *
   * @param o		the object to look for
   * @return		true if the object can be located
   */
  public boolean contains(Object o) {
    return m_Nodes.contains(o);
  }

  /**
   * Returns an iterator over the nodes.
   *
   * @return		the node iterator
   */
  public Iterator<N> iterator() {
    return m_Nodes.iterator();
  }

  /**
   * Returns the nodes as array.
   *
   * @return		the nodes
   */
  public Object[] toArray() {
    return m_Nodes.toArray();
  }

  /**
   * Returns the nodes as array.
   *
   * @param a		the array to use
   * @return		the nodes
   */
  public <N> N[] toArray(N[] a) {
    return m_Nodes.toArray(a);
  }

  /**
   * Adds the node.
   *
   * @param e		the node to add
   * @return		true if the collection was modified
   */
  public boolean add(N e) {
    return m_Nodes.add(e);
  }

  /**
   * Removes the object from the collection.
   *
   * @param o		the object to remove
   * @return		true if the collection was modified
   */
  public boolean remove(Object o) {
    return m_Nodes.remove(o);
  }

  /**
   * Checks whether all the elements of the specified collection are available
   * in this one.
   *
   * @param c		the collection to use for checking
   * @retur		true if all elements are also in this collection
   */
  public boolean containsAll(Collection<?> c) {
    return m_Nodes.containsAll(c);
  }

  /**
   * Adds all the elements of the collection to this one.
   *
   * @param c		the collection to add
   * @return		true if the collection was modified
   */
  public boolean addAll(Collection<? extends N> c) {
    return m_Nodes.addAll(c);
  }

  /**
   * Removes all the elements from the collection that are also in this one.
   *
   * @param c		the collection to remove
   * @return		true if the collection was modified
   */
  public boolean removeAll(Collection<?> c) {
    return m_Nodes.removeAll(c);
  }

  /**
   * Removes all elements but the ones from the specified collection from this
   * one.
   *
   * @param c		the collection to retain
   * @return		true if the collection was modified
   */
  public boolean retainAll(Collection<?> c) {
    return m_Nodes.retainAll(c);
  }

  /**
   * Removes all nodes.
   */
  public void clear() {
    m_Nodes.clear();
  }

  /**
   * Returns the supported flavors.
   *
   * @return		the flavors
   */
  public DataFlavor[] getTransferDataFlavors() {
    return new DataFlavor[]{DataFlavor.stringFlavor};
  }

  /**
   * Returns whether the flavor is supported (only stringFlavor supported).
   *
   * @param flavor	the flavor to check
   * @return		true if supported
   */
  public boolean isDataFlavorSupported(DataFlavor flavor) {
    return (flavor == DataFlavor.stringFlavor);
  }

  /**
   * Returns the nodes as transferable object in the requested flavor.
   *
   * @param flavor	the requested flavor
   * @return		the converted nodes
   * @throws UnsupportedFlavorException	if the flavor is not supported
   * @throws IOException		if an IO operation fails
   */
  public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
    if (!isDataFlavorSupported(flavor))
      throw new UnsupportedFlavorException(flavor);

    return toTransferable();
  }

  /**
   * Turns the node/userObject into a transferable string.
   * One node per line, separated by "\n", string representation obtained via
   * the node's toString() method.
   *
   * @return		the generated string
   */
  public Transferable toTransferable() {
    StringBuilder	result;
    int			i;

    result = new StringBuilder();
    for (i = 0; i < size(); i++) {
      if (i > 0)
	result.append("\n");
      result.append(m_Nodes.get(i).toString());
    }

    return new TransferableString(result.toString());
  }
}
