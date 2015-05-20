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
 * BasicTreeNode.java
 * Copyright (C) 2010-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * TreeNode used by the BasicTree component. Implements the "Transferable"
 * interface for simple drag'n'drop support of strings.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseTreeNode
  extends DefaultMutableTreeNode
  implements Transferable {

  /** for serialization. */
  private static final long serialVersionUID = 4553408733581911316L;

  /**
   * Creates a tree node that has no parent and no children, but which
   * allows children.
   */
  public BaseTreeNode() {
    super();
  }

  /**
   * Creates a tree node with no parent, no children, but which allows
   * children, and initializes it with the specified user object.
   *
   * @param userObject an Object provided by the user that constitutes
   *                   the node's data
   */
  public BaseTreeNode(Object userObject) {
    super(userObject);
  }

  /**
   * Creates a tree node with no parent, no children, initialized with
   * the specified user object, and that allows children only if
   * specified.
   *
   * @param userObject an Object provided by the user that constitutes
   *        the node's data
   * @param allowsChildren if true, the node is allowed to have child
   *        nodes -- otherwise, it is always a leaf node
   */
  public BaseTreeNode(Object userObject, boolean allowsChildren) {
    super(userObject, allowsChildren);
  }

  /**
   * Turns the node/userObject into a transferable string.
   *
   * @return		the generated string
   */
  public Transferable toTransferable() {
    return new TransferableString(toString());
  }

  /**
   * Returns an array of DataFlavor objects indicating the flavors the data
   * can be provided in.  The array should be ordered according to preference
   * for providing the data (from most richly descriptive to least descriptive).
   *
   * @return an array of data flavors in which this data can be transferred
   */
  public DataFlavor[] getTransferDataFlavors() {
    return new DataFlavor[]{DataFlavor.stringFlavor};
  }

  /**
   * Returns whether or not the specified data flavor is supported for
   * this object.
   *
   * @param flavor the requested flavor for the data
   * @return boolean indicating whether or not the data flavor is supported
   */
  public boolean isDataFlavorSupported(DataFlavor flavor) {
    return (flavor == DataFlavor.stringFlavor);
  }

  /**
   * Returns an object which represents the data to be transferred.  The class
   * of the object returned is defined by the representation class of the flavor.
   *
   * @param flavor the requested flavor for the data
   * @return the transferable data
   * @throws IOException if the data is no longer available in the requested flavor.
   * @throws UnsupportedFlavorException if the requested data flavor is not supported.
   * @see DataFlavor#getRepresentationClass
   */
  public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
    if (!isDataFlavorSupported(flavor))
      throw new UnsupportedFlavorException(flavor);

    return toTransferable();
  }
  
  /**
   * Returns the plain text representation of this node.
   * <br><br>
   * Default implementation simply returns {@link #toString()}.
   * 
   * @return		the string representation
   */
  public String toPlainText() {
    return toString();
  }
  
  /**
   * Returns the children as list.
   * 
   * @return		the children as list
   */
  public List<BaseTreeNode> getChildren() {
    List<BaseTreeNode>	result;
    int			i;
    
    result = new ArrayList<BaseTreeNode>();
    for (i = 0; i < getChildCount(); i++)
      result.add((BaseTreeNode) getChildAt(i));
    
    return result;
  }
}
