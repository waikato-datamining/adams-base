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
 * TransferableNode.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.tree;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serializable;

/**
 * A container for nodes. Used in drag'n'drop.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class TransferableNode
  implements Serializable, Transferable {

  /** for serialization. */
  private static final long serialVersionUID = -4291529156857201031L;

  public static DataFlavor FlowNodeFlavour = new DataFlavor(Node.class, "Flow node");

  /** the nodes to transfer. */
  protected Node[] m_Data;

  /**
   * Initializes the container.
   *
   * @param data	the node to transfer
   */
  public TransferableNode(Node data) {
    super();

    m_Data = new Node[]{data};
  }

  /**
   * Initializes the container.
   *
   * @param data	the nodes to transfer
   */
  public TransferableNode(Node[] data) {
    super();

    m_Data = data.clone();
  }

  /**
   * Returns an array of DataFlavor objects indicating the flavors the data
   * can be provided in.  The array should be ordered according to preference
   * for providing the data (from most richly descriptive to least descriptive).
   *
   * @return 		an array of data flavors in which this data can be transferred
   */
  public DataFlavor[] getTransferDataFlavors() {
    return new DataFlavor[]{FlowNodeFlavour};
  }

  /**
   * Returns whether or not the specified data flavor is supported for
   * this object.
   *
   * @param flavor 	the requested flavor for the data
   * @return 		boolean indicating whether or not the data flavor is supported
   */
  public boolean isDataFlavorSupported(DataFlavor flavor) {
    return (flavor.equals(FlowNodeFlavour));
  }

  /**
   * Returns an object which represents the data to be transferred.  The class
   * of the object returned is defined by the representation class of the flavor.
   *
   * @param flavor 		the requested flavor for the data
   * @return			the transferred string
   * @throws IOException	if the data is no longer available
   *              		in the requested flavor.
   * @throws UnsupportedFlavorException 	if the requested data flavor is
   *              				not supported.
   * @see DataFlavor#getRepresentationClass
   */
  public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
    if (flavor.equals(FlowNodeFlavour))
      return m_Data;
    else
      throw new UnsupportedFlavorException(flavor);
  }

  /**
   * Returns the underlying nodes.
   *
   * @return		the nodes
   */
  public Node[] getData() {
    return m_Data;
  }

  /**
   * Returns the underlying commandline of the full actor.
   *
   * @return		the commandline
   */
  public String toString() {
    return m_Data.length + " nodes";
  }
}
