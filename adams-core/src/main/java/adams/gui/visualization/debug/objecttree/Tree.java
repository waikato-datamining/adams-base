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
 * Tree.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.debug.objecttree;

import java.lang.reflect.Array;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import adams.core.option.OptionHandler;
import adams.gui.core.BaseTree;
import adams.gui.visualization.debug.inspectionhandler.AbstractInspectionHandler;
import adams.gui.visualization.debug.objecttree.Node.NodeType;
import adams.gui.visualization.debug.propertyextractor.AbstractPropertyExtractor;

/**
 * Specialized tree that displays the properties of an object.
 * <br><br>
 * In order to avoid loops, a HashSet is used for keeping track of the processed
 * objects. Of course, custom equals(Object)/compareTo(Object) methods will
 * interfere with this mechanism.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Tree
  extends BaseTree {

  /** for serialization. */
  private static final long serialVersionUID = -127345486742553561L;

  /** the label for the hashcode. */
  public final static String LABEL_HASHCODE = "hashCode";

  /** the current object. */
  protected transient Object m_Object;

  /** the search string. */
  protected String m_SearchString;
  
  /** the search pattern. */
  protected Pattern m_SearchPattern;
  
  /** whether the search is using a regular expression. */
  protected boolean m_IsRegExp;
  
  /**
   * Initializes the tree.
   */
  public Tree() {
    super();

    m_SearchString  = null;
    m_SearchPattern = null;
    m_IsRegExp      = false;
    setShowsRootHandles(true);
    setRootVisible(true);
    setCellRenderer(new Renderer());
    buildTree(null);
  }

  /**
   * Builds the tree from the given object.
   *
   * @param root	the object to build the tree from, null for empty tree
   */
  protected void buildTree(Object root) {
    DefaultTreeModel		model;
    Node		rootNode;

    if (root == null) {
      model = new DefaultTreeModel(null);
    }
    else {
      rootNode = buildTree(null, null, root, NodeType.NORMAL);
      model    = new DefaultTreeModel(rootNode);
    }

    setModel(model);
  }

  /**
   * Adds the array below the parent.
   *
   * @param parent	the parent to add the array to
   * @param obj		the array to add
   */
  protected void addArray(Node parent, Object obj) {
    int		len;
    int		i;
    Object	value;
    Node	child;

    len = Array.getLength(obj);
    for (i = 0; i < len; i++) {
      value = Array.get(obj, i);
      if (value != null) {
	buildTree(parent, "[" + (i+1) + "]", value, NodeType.ARRAY_ELEMENT);
      }
      else {
	child = new Node("[" + (i+1) + "]", null, NodeType.ARRAY_ELEMENT);
	parent.add(child);
      }
    }
  }

  /**
   * Checks whether object represents a primitive class.
   * 
   * @param obj		the object to check
   */
  protected boolean isPrimitive(Object obj)  {
    if (obj instanceof Boolean)
      return true;
    else if (obj instanceof Byte)
      return true;
    else if (obj instanceof Short)
      return true;
    else if (obj instanceof Integer)
      return true;
    else if (obj instanceof Long)
      return true;
    else if (obj instanceof Float)
      return true;
    else if (obj instanceof Double)
      return true;
    else if (obj instanceof Character)
      return true;
    else if (obj instanceof String)
      return true;
    else
      return false;
  }
  
  /**
   * Checks the label against the current search setup.
   * 
   * @param label	the label to check
   * @return		true if a match and should be added
   */
  protected boolean matches(String label) {
    boolean	result;

    result = true;
    
    if (m_SearchString != null) {
      result = false;
      if (m_SearchPattern != null)
	result = m_SearchPattern.matcher(label).matches();
      else
	result = (label.indexOf(m_SearchString) > -1);
    }

    return result;
  }
  
  /**
   * Builds the tree recursively.
   *
   * @param parent	the parent to add the object to (null == root)
   * @param property	the name of the property the object belongs to (null == root)
   * @param obj		the object to add
   * @param type	the type of node
   * @return		the generated node
   */
  protected Node buildTree(Node parent, String property, Object obj, NodeType type) {
    Node				result;
    AbstractPropertyExtractor		extractor;
    List<AbstractInspectionHandler>	handlers;
    Hashtable<String,Object>		additional;
    Object				current;
    String				label;
    int					i;
    boolean				add;

    result = new Node(property, obj, type);
    if (parent != null)
      parent.add(result);

    // Object's hashcode
    if (!isPrimitive(obj) && matches(LABEL_HASHCODE))
      result.add(new Node(LABEL_HASHCODE, obj.hashCode(), NodeType.HASHCODE));

    // array?
    if (obj.getClass().isArray())
      addArray(result, obj);

    // child properties
    try {
      extractor = AbstractPropertyExtractor.getExtractor(obj);
      extractor.setCurrent(obj);
      for (i = 0; i < extractor.size(); i++) {
	current = extractor.getValue(i);
	if (current != null) {
	  label = extractor.getLabel(i);
	  add   =    matches(label) 
	          || (current instanceof OptionHandler) 
	          || (current.getClass().isArray());
	  if (add)
	    buildTree(result, label, current, NodeType.NORMAL);
	}
      }
    }
    catch (Exception e) {
      System.err.println("Failed to obtain property descriptors for: " + obj);
      e.printStackTrace();
    }

    // additional values obtained through inspection handlers
    handlers = AbstractInspectionHandler.getHandler(obj);
    for (AbstractInspectionHandler handler: handlers) {
      additional = handler.inspect(obj);
      for (String key: additional.keySet()) {
	if (matches(key))
	  buildTree(result, key, additional.get(key), NodeType.NORMAL);
      }
    }

    return result;
  }

  /**
   * Sets the object to display.
   *
   * @param value	the object
   */
  public void setObject(Object value) {
    m_Object = value;
    buildTree(m_Object);
  }

  /**
   * Returns the object currently displayed.
   *
   * @return		the object
   */
  public Object getObject() {
    return m_Object;
  }
  
  /**
   * Attempts to select the specified property path.
   * 
   * @param parent	the parent node
   * @param path	the path to select (and open in the tree)
   * @param index	the index in the path array
   * @return		true if successfully selected
   */
  protected boolean selectPropertyPath(Node parent, String[] path, int index) {
    boolean	result;
    Node	child;
    int		i;
    
    result = false;
    
    for (i = 0; i < parent.getChildCount(); i++) {
      child = (Node) parent.getChildAt(i);
      if (child.getProperty().equals(path[index])) {
	if (index < path.length - 1) {
	  result = selectPropertyPath(child, path, index + 1);
	}
	else {
	  result = true;
	  setSelectionPath(new TreePath(child.getPath()));
	}
      }
    }
    
    return result;
  }
  
  /**
   * Attempts to select the specified property path.
   * 
   * @param path	the path to select (and open in the tree)
   * @return		true if successfully selected
   */
  public boolean selectPropertyPath(String[] path) {
    return selectPropertyPath((Node) getModel().getRoot(), path, 0);
  }
  
  /**
   * Initiates the search.
   * 
   * @param search	the search string
   * @param isRegExp	whether the search is using a regular expression
   */
  public void search(String search, boolean isRegExp) {
    if (search.trim().length() == 0) {
      search   = null;
      isRegExp = false;
    }
    
    m_SearchString = search;
    m_IsRegExp     = isRegExp;
    
    if ((m_SearchString != null) && m_IsRegExp) {
      try {
	m_SearchPattern = Pattern.compile(m_SearchString);
      }
      catch (Exception e) {
	m_SearchPattern = null;
      }
    }
    else {
      m_SearchPattern = null;
    }
    
    buildTree(m_Object);
  }
}