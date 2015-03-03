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
 * ActorPathTree.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.goe.actorpathtree;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import adams.gui.core.BaseTreeNode;
import adams.gui.core.dotnotationtree.DotNotationTree;
import adams.gui.flow.tree.Node;
import adams.gui.flow.tree.Tree;

/**
 * Displays actor paths in a tree structure.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of node to use
 */
public abstract class ActorPathTree<T extends ActorPathNode>
  extends DotNotationTree<T> {

  /** for serialization. */
  private static final long serialVersionUID = 6343911349519910301L;

  /** the underlying flow. */
  protected Tree m_FlowTree;

  /**
   * Initializes the tree with no classes.
   */
  public ActorPathTree() {
    super();
    setSorted(true);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_FlowTree = null;
  }

  /**
   * Sets the underlying flow.
   *
   * @param value	the flow
   */
  public void setFlowTree(Tree value) {
    m_FlowTree = value;
  }

  /**
   * Returns the underlying flow.
   *
   * @return		the flow
   */
  public Tree getFlowTree() {
    return m_FlowTree;
  }

  /**
   * Returns the default renderer to use.
   *
   * @return		the renderer
   */
  @Override
  protected TreeCellRenderer getDefaultRenderer() {
    return new ActorPathTreeRenderer();
  }

  /**
   * Masks the escaped dots to avoid splitting on the dot.
   *
   * @param s		the string to process
   * @return		the processed string
   */
  protected String maskEscapedDots(String s) {
    return s.replace("\\.", ActorPathNode.MASK_CHARACTER);
  }

  /**
   * Un-masks the masked escaped dots.
   *
   * @param s		the string to process
   * @return		the processed string
   */
  protected String unmaskEscapedDots(String s) {
    return s.replace(ActorPathNode.MASK_CHARACTER, "\\.");
  }

  /**
   * Displays the specified items.
   *
   * @param items	the items to display
   */
  @Override
  public void setItems(List<String> items) {
    ArrayList<String>	masked;

    masked = new ArrayList<String>();
    for (String item: items)
      masked.add(maskEscapedDots(item));

    super.setItems(masked);
  }

  /**
   * Checks whether the item is among the stored ones.
   *
   * @param item	the item to look for
   * @return		true if already present
   */
  @Override
  public boolean hasItem(String item) {
    return super.hasItem(maskEscapedDots(item));
  }

  /**
   * Adds the item, if necessary.
   *
   * @param item	the item to add
   */
  @Override
  public void addItem(String item) {
    super.addItem(maskEscapedDots(item));
  }

  /**
   * Returns the first stored item.
   *
   * @return		the item or null if no items stored
   */
  @Override
  public String getFirstItem() {
    return unmaskEscapedDots(super.getFirstItem());
  }

  /**
   * The item to select initially.
   *
   * @param item	the item to select
   */
  @Override
  public void setSelectedItem(String item) {
    super.setSelectedItem(maskEscapedDots(item));
  }

  /**
   * Returns the label used for the root node.
   *
   * @return		the label
   */
  @Override
  protected String getRootNodeLabel() {
    return null;
  }

  /**
   * Returns a new instance of a node.
   *
   * @param label	the label to use
   * @return		the new node
   * @see		#newNode(String)
   */
  protected T newNodeInstance(String label) {
    return (T) new ActorPathNode(label);
  }

  /**
   * Creates a new node with the specified label.
   *
   * @param label	the label to use for the node
   * @return		the new node
   * @see		#newNodeInstance(String)
   */
  @Override
  protected T newNode(String label) {
    T		result;
    String	classname;
    Node	node;

    classname = null;
    if (m_FlowTree != null) {
      node = m_FlowTree.locate(label);
      if (node != null)
	classname = node.getActor().getClass().getName();
    }

    result = newNodeInstance(label);
    if (classname != null) {
      result.setClassname(checkClassname(result, classname));
      result.setIconClassname(checkIconClassname(result, classname));
    }

    return result;
  }

  /**
   * Processes the classname for the icon, returns null if not suitable to be 
   * added to the node.
   * <p/>
   * Default implementation just returns the provided classname.
   * 
   * @param node	the node currently processed
   * @param classname	the classname to process
   * @return		null if not acceptable, otherwise the classname
   */
  protected String checkIconClassname(T node, String classname) {
    return classname;
  }

  /**
   * Processes the classname, returns null if not suitable to be added to the
   * node.
   * <p/>
   * Default implementation just returns the provided classname.
   * 
   * @param node	the node to process
   * @param classname	the classname to process
   * @return		null if not acceptable, otherwise the classname
   */
  protected String checkClassname(T node, String classname) {
    return classname;
  }
  
  /**
   * Post-processes a leaf after being added, i.e., info node generators
   * are applied.
   *
   * @param node	the node to process
   * @param item	the full item string
   * @see		#processClassname(String)
   */
  @Override
  protected void postAddLeaf(T node, String item) {
    String		classname;
    Node		located;

    classname = null;
    if (m_FlowTree != null) {
      located = m_FlowTree.locate(item);
      if (located != null)
	classname = located.getActor().getClass().getName();
      if (classname != null) {
	node.setIconClassname(checkIconClassname(node, classname));
	node.setClassname(checkClassname(node, classname));
      }
    }

    super.postAddLeaf(node, item);
  }

  /**
   * Selects the node (containing a classname) identified by the label
   * of the node.
   *
   * @param name	the label of the node
   * @return		the node, null if not found
   */
  public ActorPathNode findNodeByName(String name) {
    ActorPathNode	result;
    BaseTreeNode	node;
    Enumeration		enm;
    ActorPathNode	child;

    result = null;

    if ((name != null) && (name.length() > 0)) {
      node = (BaseTreeNode) getTreeModel().getRoot();
      if (node != null) {
	enm = node.breadthFirstEnumeration();
	while (enm.hasMoreElements()) {
	  node = (BaseTreeNode) enm.nextElement();
	  if (node instanceof ActorPathNode) {
	    child = (ActorPathNode) node;
	    if (child.hasClassname() && child.isLabelMatch(name)) {
	      result = child;
	      break;
	    }
	  }
	}
      }
    }

    return result;
  }

  /**
   * Selects the node (containing a classname) identified by the name.
   *
   * @param name	the label of the node
   */
  public void selectNodeByName(String name) {
    ActorPathNode	node;
    TreePath		path;

    node = findNodeByName(name);
    if (node != null) {
      path = new TreePath(node.getPath());
      setSelectionPath(path);
    }
  }
}
