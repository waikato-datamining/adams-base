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
 * Node.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.debug.objecttree;

import java.util.ArrayList;
import java.util.List;

import adams.gui.core.BaseTreeNode;

/**
 * Specialized tree node.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Node
  extends BaseTreeNode {

  /** for serialization. */
  private static final long serialVersionUID = 39921763469189066L;

  /**
   * The types of nodes.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum NodeType {
    /** normal. */
    NORMAL,
    /** array element. */
    ARRAY_ELEMENT,
    /** hashcode. */
    HASHCODE
  }
  
  /** the name of the property. */
  protected String m_Property;

  /** the node type. */
  protected NodeType m_NodeType;

  /**
   * Initializes the node.
   *
   * @param property	the name of the property the object belongs to
   * @param obj		the obj to display
   */
  public Node(String property, Object obj) {
    this(property, obj, NodeType.NORMAL);
  }

  /**
   * Initializes the node.
   *
   * @param property	the name of the property the object belongs to
   * @param obj		the obj to display
   * @param type	the type of node
   */
  public Node(String property, Object obj, NodeType type) {
    super(obj);

    m_Property = property;
    m_NodeType = type;
  }

  /**
   * Returns the property.
   * 
   * @return		the property
   */
  public String getProperty() {
    return m_Property;
  }
  
  /**
   * Returns the node type.
   *
   * @return		the node type
   */
  public NodeType getNodeType() {
    return m_NodeType;
  }

  /**
   * Returns whether the stored object is an array.
   *
   * @return		true if the stored object is an array
   */
  public boolean isArray() {
    return getUserObject().getClass().isArray();
  }

  /**
   * Returns a string representation of the stored object.
   *
   * @return		the string representation
   */
  public String toRepresentation() {
    AbstractObjectPlainTextRenderer	renderer;
    
    if (getUserObject() == null)
      return "null";
    
    renderer = AbstractObjectPlainTextRenderer.getRenderer(getUserObject()).get(0);
    return renderer.render(getUserObject());
  }

  /**
   * Returns the property name of the user object.
   *
   * @return		the property name
   */
  @Override
  public String toString() {
    StringBuilder	result;
    Class		cls;
    String		levels;

    result = new StringBuilder();
    result.append("<html>");

    if (m_Property == null)
      result.append("this");
    else
      result.append(m_Property);

    result.append(" <font color=\"gray\">[");
    if (getUserObject() != null) {
      levels = "";
      cls    = getUserObject().getClass();
      while (cls.isArray()) {
	levels += "[]";
	cls = cls.getComponentType();
      }
      result.append(cls.getName());
      result.append(levels);
    }
    else {
      result.append("null");
    }
    result.append("]</font>");
    result.append("</html>");

    return result.toString();
  }

  /**
   * Returns the property name of the user object.
   *
   * @return		the property name
   */
  @Override
  public String toPlainText() {
    StringBuilder	result;
    Class		cls;
    String		levels;

    result = new StringBuilder();

    if (m_Property == null)
      result.append("this");
    else
      result.append(m_Property);

    result.append(" [");
    levels = "";
    cls = getUserObject().getClass();
    while (cls.isArray()) {
      levels += "[]";
      cls = cls.getComponentType();
    }
    result.append(cls.getName());
    result.append(levels);
    result.append("]");

    return result.toString();
  }
  
  /**
   * Returns the property path from the root to this node.
   * 
   * @return		the individual property names
   */
  public String[] getPropertyPath() {
    List<String>	result;
    Node		current;
    
    result  = new ArrayList<String>();
    current = this;
    do {
      if (current.getProperty() != null)
	result.add(0, current.getProperty());
      current = (Node) current.getParent();
    }
    while (current != null);
    
    return result.toArray(new String[result.size()]);
  }
}