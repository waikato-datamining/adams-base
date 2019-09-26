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
 * DOMNode.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core.dom;

import adams.data.conversion.DOMNodeToString;
import adams.gui.core.BaseTreeNode;
import com.github.fracpete.jclipboardhelper.TransferableString;
import org.w3c.dom.Node;

import java.awt.datatransfer.Transferable;

/**
 * Specialized tree node.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DOMNode
  extends BaseTreeNode {

  /** for serialization. */
  private static final long serialVersionUID = 9062259637831548370L;

  /** the label for the root node in case of multiple hierarchies. */
  public final static String ROOT = "root";

  /** the value. */
  protected Object m_Value;

  /**
   * Initializes the node with the specified label.
   *
   * @param label	the label for this node
   * @param value	the DOM object to attach, can be null
   */
  public DOMNode(String label, Object value) {
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
   * Checks whether there is any DOM object attached.
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
   * @return		the generated string, null if not available
   * @see		#getValue()
   */
  @Override
  public Transferable toTransferable() {
    TransferableString 		result;
    DOMNodeToString		conv;
    String			msg;
    
    result = null;
    
    if (!(getValue() instanceof Node))
      return result;
    
    try {
      conv = new DOMNodeToString();
      conv.setInput(getValue());
      msg = conv.convert();
      if (msg == null)
	result = new TransferableString((String) conv.getOutput());
      else
	System.err.println("Failed to convert value to string: " + msg);
      conv.cleanUp();
    }
    catch (Exception e) {
      System.err.println("Failed to convert value to string:");
      e.printStackTrace();
      result = null;
    }

    return result;
  }
}