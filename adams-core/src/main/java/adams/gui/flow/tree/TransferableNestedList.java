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
 * TransferableNestedList.java
 * Copyright (C) 2018-2023 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.tree;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/**
 * A container for nested list. Used in drag'n'drop.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class TransferableNestedList
  implements Serializable, Transferable {

  /** for serialization. */
  private static final long serialVersionUID = -4291529156857201031L;

  public static DataFlavor FlavorNestedList = new DataFlavor(List.class, "Nested list");

  /** the nodes to transfer. */
  protected List[] m_Data;

  /**
   * Initializes the container.
   *
   * @param data	the node to transfer
   */
  public TransferableNestedList(Node data) {
    super();

    m_Data = new List[]{TreeHelper.getNested(data)};
  }

  /**
   * Initializes the container.
   *
   * @param data	the nodes to transfer
   */
  public TransferableNestedList(Node[] data) {
    super();

    m_Data = new List[data.length];
    for (int i = 0; i < data.length; i++)
      m_Data[i] = TreeHelper.getNested(data[i]);
  }

  /**
   * Returns an array of DataFlavor objects indicating the flavors the data
   * can be provided in.  The array should be ordered according to preference
   * for providing the data (from most richly descriptive to least descriptive).
   *
   * @return 		an array of data flavors in which this data can be transferred
   */
  public DataFlavor[] getTransferDataFlavors() {
    if (m_Data.length != 1)
      return new DataFlavor[]{FlavorNestedList};
    else
      return new DataFlavor[]{FlavorNestedList, DataFlavor.stringFlavor};
  }

  /**
   * Returns whether or not the specified data flavor is supported for
   * this object.
   *
   * @param flavor 	the requested flavor for the data
   * @return 		boolean indicating whether or not the data flavor is supported
   */
  public boolean isDataFlavorSupported(DataFlavor flavor) {
    return ((m_Data.length == 1) && (flavor.equals(DataFlavor.stringFlavor)))
      || flavor.equals(FlavorNestedList);
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
    if (flavor.equals(FlavorNestedList))
      return m_Data;
    else if (flavor.equals(DataFlavor.stringFlavor))
      return TreeHelper.buildTree(m_Data[0]).getFullActor().toCommandLine();
    else
      throw new UnsupportedFlavorException(flavor);
  }

  /**
   * Returns the underlying nodes.
   *
   * @return		the nodes
   */
  public List[] getData() {
    return m_Data;
  }

  /**
   * Returns the underlying commandline of the full actor.
   *
   * @return		the commandline
   */
  public String toString() {
    return m_Data.length + " list(s)";
  }
}
