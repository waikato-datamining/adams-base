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
 * DotNotationNode.java
 * Copyright (C) 2009-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core.dotnotationtree;

import adams.gui.core.BaseTreeNode;
import com.github.fracpete.jclipboardhelper.TransferableString;

import java.awt.datatransfer.Transferable;

/**
 * Specialized tree node.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DotNotationNode
  extends BaseTreeNode {

  /** for serialization. */
  private static final long serialVersionUID = 9062259637831548370L;

  /** the label for the root node in case of multiple hierarchies. */
  public final static String MULTIPLE_ROOT = "root";

  /** the item. */
  protected String m_Item;

  /**
   * Initializes the node with the specified label.
   *
   * @param label	the label for this node
   */
  public DotNotationNode(String label) {
    super(label);

    m_Item = null;
  }

  /**
   * Returns the (partial) label for this node.
   *
   * @return		the partial label
   */
  public String getLabel() {
    return (String) getUserObject();
  }

  /**
   * Returns whether the label matches the specified string.
   *
   * @param s		the string to match against the label
   * @return		true if a match
   */
  public boolean isLabelMatch(String s) {
    return getLabel().equals(s);
  }

  /**
   * Returns whether this node represents a leaf node in the sense of the
   * end-point of the dot notation of the full label. Custom trees might
   * have additional information below this actual leaf node.
   * <br><br>
   * Default implementation just returns isLeaf() value.
   *
   * @return		true if leaf
   */
  public boolean isItemLeaf() {
    return isLeaf();
  }
  
  /**
   * Returns the separator in use.
   * 
   * @return		the separator
   */
  public String getSeparator() {
    return ".";
  }

  /**
   * Returns the full label.
   *
   * @return		the full label, null if not a leaf
   */
  public String getItem() {
    String		result;
    DotNotationNode	node;

    result = null;

    if (isItemLeaf()) {
      if (m_Item == null) {
        node   = this;
        m_Item = node.getLabel();
        while (node.getParent() != null) {
          node = (DotNotationNode) node.getParent();
          if (node.isRoot() && node.getLabel().equals(MULTIPLE_ROOT))
            continue;
          m_Item = node.getLabel() + getSeparator() + m_Item;
        }
      }

      result = m_Item;
    }

    return result;
  }

  /**
   * Turns the full label into a transferable string.
   *
   * @return		the generated string
   * @see		#getItem()
   */
  @Override
  public Transferable toTransferable() {
    return new TransferableString(getItem());
  }
}