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
 * Node.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.debug.objecttree;

import adams.core.Utils;
import adams.core.option.OptionHandler;
import adams.data.textrenderer.AbstractTextRenderer;
import adams.gui.core.BaseTreeNode;
import adams.gui.visualization.debug.inspectionhandler.AbstractInspectionHandler;
import adams.gui.visualization.debug.propertyextractor.AbstractPropertyExtractor;

import javax.swing.tree.DefaultTreeModel;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Node for displaying a single property of an object.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Node
  extends BaseTreeNode {

  /** for serialization. */
  private static final long serialVersionUID = 39921763469189066L;

  /** the tree this node belongs to. */
  protected Tree m_Owner;

  /** the name of the property. */
  protected String m_Property;

  /** the node type. */
  protected NodeType m_NodeType;

  /** whether the node has been initialized. */
  protected boolean m_Initialized;

  /** caching the class / extractors relation. */
  protected static Map<Class,List<AbstractPropertyExtractor>> m_ExtractorCache;

  /** caching the class / inspection handler relation. */
  protected static Map<Class,List<AbstractInspectionHandler>> m_InspectionHandlerCache;

  /**
   * Initializes the node.
   *
   * @param owner 	the tree this node belongs to
   * @param parent 	the parent of this node, null for root
   * @param property	the name of the property the object belongs to
   * @param obj		the obj to display
   */
  public Node(Tree owner, Node parent, String property, Object obj) {
    this(owner, parent, property, obj, NodeType.NORMAL);
  }

  /**
   * Initializes the node.
   *
   * @param owner 	the tree this node belongs to
   * @param parent 	the parent of this node, null for root
   * @param property	the name of the property the object belongs to
   * @param obj		the obj to display
   * @param type	the type of node
   */
  public Node(Tree owner, Node parent, String property, Object obj, NodeType type) {
    super(obj);

    m_Owner       = owner;
    m_Property    = property;
    m_NodeType    = type;
    m_Initialized = false;

    if (m_ExtractorCache == null) {
      m_ExtractorCache         = new HashMap<>();
      m_InspectionHandlerCache = new HashMap<>();
    }

    if (!Utils.isPrimitive(obj))
      add(new DummyNode());
  }

  /**
   * Returns the tree this node belongs to.
   *
   * @return		the tree
   */
  public Tree getOwner() {
    return m_Owner;
  }

  /**
   * Returns the parent of this node.
   *
   * @return		the parent, null for root
   */
  public Node getParent() {
    return (Node) super.getParent();
  }

  /**
   * Adds the array below the parent.
   *
   * @param obj		the array to add
   */
  protected void addArray(Object obj) {
    int		len;
    int		i;
    Object	value;
    Node	child;

    len = Array.getLength(obj);
    for (i = 0; (i < len) && (i < Tree.MAX_ITEMS); i++) {
      value = Array.get(obj, i);
      if (value != null) {
	child = new Node(m_Owner, this, "[" + (i+1) + "]", value, NodeType.ARRAY_ELEMENT);
	add(child);
      }
      else {
	child = new Node(m_Owner, this, "[" + (i+1) + "]", null, NodeType.ARRAY_ELEMENT);
	add(child);
      }
    }
    if (len > Tree.MAX_ITEMS) {
      child = new Node(m_Owner, this, "[" + Tree.MAX_ITEMS + "-" + len + "]", "skipped", NodeType.ARRAY_ELEMENT);
      add(child);
    }
  }

  /**
   * Adds the properties of this object as children.
   */
  protected void doExpand() {
    List<AbstractPropertyExtractor> 	extractors;
    List<AbstractInspectionHandler> 	handlers;
    Hashtable<String, Object> 		additional;
    Object 				current;
    String 				label;
    HashSet<String> 			labels;
    int 				i;
    boolean 				add;
    Node				child;
    List<String>			keys;

    // Object's hashcode
    if (!Utils.isPrimitive(getUserObject()) && m_Owner.matches(Tree.LABEL_HASHCODE)) {
      child = new Node(m_Owner, this, Tree.LABEL_HASHCODE, getUserObject().hashCode(), NodeType.HASHCODE);
      add(child);
    }

    // array?
    if (getUserObject().getClass().isArray())
      addArray(getUserObject());

    labels = new HashSet<>();

    // child properties
    try {
      if (m_ExtractorCache.containsKey(getUserObject().getClass())) {
	extractors = m_ExtractorCache.get(getUserObject().getClass());
      }
      else {
	extractors = AbstractPropertyExtractor.getExtractors(getUserObject());
	m_ExtractorCache.put(getUserObject().getClass(), extractors);
      }
      for (AbstractPropertyExtractor extractor : extractors) {
	extractor.setCurrent(getUserObject());
	for (i = 0; i < extractor.size(); i++) {
	  current = extractor.getValue(i);
	  if (current != null) {
	    label = extractor.getLabel(i);
	    add = m_Owner.matches(label)
	      || (current instanceof OptionHandler)
	      || (current.getClass().isArray());
	    add = add && !labels.contains(label);
	    if (add) {
	      labels.add(label);
	      child = new Node(m_Owner, this, label, current, NodeType.NORMAL);
	      add(child);
	    }
	  }
	}
      }
    }
    catch (Exception e) {
      System.err.println("Failed to obtain property descriptors for: " + getUserObject());
      e.printStackTrace();
    }

    // additional values obtained through inspection handlers
    if (m_InspectionHandlerCache.containsKey(getUserObject().getClass())) {
      handlers = m_InspectionHandlerCache.get(getUserObject().getClass());
    }
    else {
      handlers = AbstractInspectionHandler.getHandler(getUserObject());
      m_InspectionHandlerCache.put(getUserObject().getClass(), handlers);
    }
    for (AbstractInspectionHandler handler : handlers) {
      additional = handler.inspect(getUserObject());
      keys = new ArrayList<>(additional.keySet());
      Collections.sort(keys);
      for (String key : keys) {
	if (m_Owner.matches(key) && !labels.contains(key)) {
	  labels.add(key);
	  child = new Node(m_Owner, this, key, additional.get(key), NodeType.NORMAL);
	  add(child);
	}
      }
    }
  }

  /**
   * Expands the node (forced).
   */
  public void expand() {
    m_Initialized = true;
    removeAllChildren();

    doExpand();

    // flag as changed to trigger redraw
    if (getOwner().getModel() instanceof DefaultTreeModel)
      ((DefaultTreeModel) getOwner().getModel()).nodeStructureChanged(this);
  }

  /**
   * Expands the node if necessary.
   */
  public void expandIfNecessary() {
    if (m_Initialized)
      return;

    expand();
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
    return AbstractTextRenderer.renderObject(getUserObject());
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
    List<String> 	result;
    Node		current;

    result  = new ArrayList<>();
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
