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
 * JsonNode.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core.json;

import java.awt.datatransfer.Transferable;

import net.minidev.json.JSONValue;
import adams.gui.core.BaseTreeNode;
import adams.gui.core.TransferableString;

/**
 * Specialized tree node.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class JsonNode
  extends BaseTreeNode {

  /** for serialization. */
  private static final long serialVersionUID = 9062259637831548370L;

  /** the label for the root node in case of multiple hierarchies. */
  public final static String ROOT = "json";

  /** the value. */
  protected Object m_Value;

  /**
   * Initializes the node with the specified label.
   *
   * @param label	the label for this node
   * @param value	the JSON object to attach, can be null
   */
  public JsonNode(String label, Object value) {
    super(label);

    m_Value = value;
  }

  /**
   * Returns the label for this node.
   *
   * @return		the label
   */
  public String getLabel() {
    return (String) getUserObject();
  }

  /**
   * Checks whether there is any JSON object attached.
   * 
   * @return		true if a value is attached
   */
  public boolean hasValue() {
    return (m_Value != null);
  }
  
  /**
   * Returns the value for this node.
   *
   * @return		the value
   */
  public Object getValue() {
    return m_Value;
  }

  /**
   * Turns the value into a transferable string.
   *
   * @return		the generated string
   * @see		#getValue()
   */
  @Override
  public Transferable toTransferable() {
    return new TransferableString(JSONValue.toJSONString(m_Value));
  }
}