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
 * BaseTree.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;

import javax.swing.JInternalFrame;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.Dialog;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * A JTree ehanced with a few useful methods.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @author  Addison-Wesley
 * @version $Revision$
 */
public class BaseTree
  extends JTree {

  /** for serialization. */
  private static final long serialVersionUID = 7574481276254738172L;

  /**
   * Initializes the tree.
   */
  public BaseTree() {
    super();
    initialize();
  }

  /**
   * Initializes the tree with the given model.
   *
   * @param model	the tree model to use
   */
  public BaseTree(TreeModel model) {
    super(model);
    initialize();
  }

  /**
   * Initializes the tree with the given root node.
   *
   * @param root	the root node to use
   */
  public BaseTree(TreeNode root) {
    super(root);
    initialize();
  }

  /**
   * Further initialization of the tree.
   */
  protected void initialize() {
    setRowHeight(0);
  }

  /**
   * Expands the specified node.
   *
   * @param node	the node to expand
   */
  public void expand(DefaultMutableTreeNode node) {
    expandPath(new TreePath(node.getPath()));
  }

  /**
   * Expands only the root node.
   */
  public void expandRoot() {
    TreeNode root = (TreeNode) getModel().getRoot();
    if ((root != null) && (root instanceof DefaultMutableTreeNode))
      expand((DefaultMutableTreeNode) root);
  }

  /**
   * Expands all nodes in the tree.
   */
  public void expandAll() {
    TreeNode root = (TreeNode) getModel().getRoot();
    if (root != null)
      toggleAll(new TreePath(root), true);
  }

  /**
   * Expands the sub-tree below the specified node.
   *
   * @param node	the node to expand
   */
  public void expandAll(DefaultMutableTreeNode node) {
    expandAll(new TreePath(node.getPath()));
  }

  /**
   * Expands the sub-tree below the specified node.
   *
   * @param path	the path to the sub-tree
   */
  public void expandAll(TreePath path) {
    toggleAll(path, true);
  }

  /**
   * Collapses the specified node.
   *
   * @param node	the node to collapse
   */
  public void collapse(DefaultMutableTreeNode node) {
    collapsePath(new TreePath(node.getPath()));
  }

  /**
   * Collapses only the root node.
   */
  public void collapseRoot() {
    TreeNode root = (TreeNode) getModel().getRoot();
    if ((root != null) && (root instanceof DefaultMutableTreeNode))
      collapse((DefaultMutableTreeNode) root);
  }

  /**
   * Collapses all nodes in the tree.
   */
  public void collapseAll() {
    TreeNode root = (TreeNode) getModel().getRoot();
    if (root != null)
      toggleAll(new TreePath(root), false);
  }

  /**
   * Collapses the sub-tree below the specified node.
   *
   * @param node	the node to collapse
   */
  public void collapseAll(DefaultMutableTreeNode node) {
    collapseAll(new TreePath(node.getPath()));
  }

  /**
   * Collapses the sub-tree below the specified node.
   *
   * @param path	the path to the sub-tree
   */
  public void collapseAll(TreePath path) {
    toggleAll(path, false);
  }

  /**
   * Performs the expand/collapse recursively.
   *
   * @param parent	the parent path
   * @param expand	whether to expand or collapse
   */
  protected void toggleAll(TreePath parent, boolean expand) {
    TreeNode 	node;
    int		i;
    TreeNode	child;

    node = (TreeNode) parent.getLastPathComponent();
    for (i = 0; i < node.getChildCount(); i++) {
      child = node.getChildAt(i);
      toggleAll(parent.pathByAddingChild(child), expand);
    }

    if (expand)
      expandPath(parent);
    else
      collapsePath(parent);
  }
  
  /**
   * Returns all currently expanded nodes.
   * 
   * @return		the expanded nodes
   */
  public List<TreePath> getExpandedTreePaths() {
    return getExpandedTreePaths(null);
  }
  
  /**
   * Returns all currently expanded nodes, starting from the specified node.
   * 
   * @param node	the node to start from, use null for root
   * @return		the expanded nodes
   */
  public List<TreePath> getExpandedTreePaths(DefaultMutableTreeNode node) {
    ArrayList<TreePath>		result;
    Enumeration<TreePath>	enm;
    
    result = new ArrayList<TreePath>();
    if (getModel().getRoot() == null)
      return result;
    
    if (node == null)
      node = (DefaultMutableTreeNode) getModel().getRoot();
    enm = getExpandedDescendants(new TreePath(node));
    if (enm != null) {
      while (enm.hasMoreElements())
	result.add(enm.nextElement());
    }
    
    return result;
  }
  
  /**
   * Expands the specified nodes, all others get collapsed.
   * 
   * @param nodes	the nodes to have expanded
   */
  public void setExpandedTreePaths(List<TreePath> nodes) {
    setExpandedTreePaths(null, nodes);
  }
  
  /**
   * Expands the specified nodes, all others get collapsed.
   * 
   * @param node	the starting node, use null for root
   * @param nodes	the nodes to have expanded
   */
  public void setExpandedTreePaths(DefaultMutableTreeNode node, List<TreePath> nodes) {
    List<TreePath>	current;

    // same?
    current = getExpandedTreePaths(node);
    if (current.equals(nodes))
      return;
    
    if (node == null)
      collapseAll();
    else
      collapseAll(node);
    
    for (TreePath n: nodes)
      setExpandedState(n, true);
  }
  
  /**
   * Returns whether the root node is selected.
   * 
   * @return		true if selected
   */
  public boolean isRootSelected() {
    boolean	result;
    TreePath	path;

    if (getModel().getRoot() == null)
      return false;
    
    path   = new TreePath(getModel().getRoot());
    result = isPathSelected(path);
    
    return result;
  }

  /**
   * Redraws the complete tree. Model must be derived from 
   * {@link DefaultTreeModel}.
   */
  public void redraw() {
    if (getModel().getRoot() != null)
      redraw((DefaultMutableTreeNode) getModel().getRoot());
  }
  
  /**
   * Redraws the node and its subtree. Model must be derived from 
   * {@link DefaultTreeModel}.
   * 
   * @param node	the node (and its subtree) to redraw
   */
  public void redraw(DefaultMutableTreeNode node) {
    List<TreePath> 	nodes;
    int[] 		selected;

    if (getModel() instanceof DefaultTreeModel) {
      nodes    = getExpandedTreePaths(node);
      selected = getSelectionRows();
      ((DefaultTreeModel) getModel()).nodeStructureChanged(node);
      setExpandedTreePaths(node, nodes);
      setSelectionRows(selected);
    }
  }
  
  /**
   * Tries to determine the frame this panel is part of.
   *
   * @return		the parent frame if one exists or null if not
   */
  public Frame getParentFrame() {
    return GUIHelper.getParentFrame(this);
  }

  /**
   * Tries to determine the dialog this panel is part of.
   *
   * @return		the parent dialog if one exists or null if not
   */
  public Dialog getParentDialog() {
    return GUIHelper.getParentDialog(this);
  }

  /**
   * Tries to determine the internal frame this panel is part of.
   *
   * @return		the parent internal frame if one exists or null if not
   */
  public JInternalFrame getParentInternalFrame() {
    return GUIHelper.getParentInternalFrame(this);
  }

  /**
   * Sets the expanded state of the rows currently being displayed.
   *
   * @param value	the expanded state of the rows
   */
  public void setExpandedState(boolean[] value) {
    int		i;

    for (i = 0; i < value.length; i++) {
      if (value[i])
        expandRow(i);
    }
  }

  /**
   * Returns the expanded (or not) state of all the rows currently being displayed.
   *
   * @return		the expanded state for the rows
   */
  public boolean[] getExpandedState() {
    boolean[]	result;
    int		i;

    result = new boolean[getRowCount()];

    for (i = 0; i < result.length; i++)
      result[i] = isExpanded(i);

    return result;
  }

  /**
   * Sets the expanded state of the rows currently being displayed.
   *
   * @param value	the expanded state of the rows
   */
  public void setExpandedStateList(List<Boolean> value) {
    int		i;

    for (i = 0; i < value.size(); i++) {
      if (value.get(i))
	expandRow(i);
    }
  }

  /**
   * Returns the expanded (or not) state of all the rows currently being displayed.
   *
   * @return		the expanded state for the rows
   */
  public List<Boolean> getExpandedStateList() {
    ArrayList<Boolean>	result;
    int			i;

    result = new ArrayList<Boolean>();

    for (i = 0; i < getRowCount(); i++)
      result.add(isExpanded(i));

    return result;
  }

  /**
   * Returns the closest common ancestor for the two nodes.
   *
   * @param node1	the first node
   * @param node2	the second node
   * @return		the ancestor or null if no common ancestor, no even the
   * 			root node
   */
  public BaseTreeNode getCommonAncestor(BaseTreeNode node1, BaseTreeNode node2) {
    BaseTreeNode	result;
    Object[]		path1;
    Object[]		path2;
    int			i;

    result = null;
    path1  = node1.getPath();
    path2  = node2.getPath();

    for (i = 0; (i < path1.length) && (i < path2.length); i++) {
      if (path1[i].equals(path2[i]))
	result = (BaseTreeNode) path1[i];
      else
	break;
    }

    return result;
  }

  /**
   * Adds the node (and its potentional children) to the StringBuilder.
   *
   * @param builder	for adding the tree structure to
   * @param level	the current level (for indentation)
   * @param node	the node to process
   * @param more	for keeping track whether more siblings come after the
   * 			node
   */
  protected void toString(StringBuilder builder, int level, TreeNode node, List<Boolean> more) {
    StringBuilder	indentStr;
    int			i;
    TreeNode		child;

    // generate indentation string
    indentStr = new StringBuilder();
    for (i = 0; i < level; i++) {
      if (more.get(i))
	indentStr.append("| ");
      else
	indentStr.append("  ");
    }

    // add node
    if (level > 0) {
      builder.append(indentStr);
      builder.append("|\n");
    }
    builder.append(indentStr);
    builder.append("+ " + node.toString() + "\n");

    // add children
    for (i = 0; i < node.getChildCount(); i++) {
      child = node.getChildAt(i);
      more.add(i < node.getChildCount() - 1);
      toString(builder, level + 1, child, more);
      more.remove(more.size() - 1);
    }
  }

  /**
   * Generates a string representation of the tree.
   *
   * @return		the string representation
   */
  @Override
  public String toString() {
    StringBuilder	result;
    Object		root;
    List<Boolean>	more;

    result = new StringBuilder();

    root = getModel().getRoot();
    if (root instanceof TreeNode) {
      more = new ArrayList<Boolean>();
      more.add(false);
      toString(result, 0, (TreeNode) root, more);
    }
    else {
      result.append("Cannot generate tree!");
    }

    return result.toString();
  }
  
  /**
   * Generates a string representation of the tree in plain text.
   * 
   * @return		the string representation
   */
  public String toPlainText() {
    StringBuilder	result;
    Object		root;
    List<Boolean>	more;

    result = new StringBuilder();

    root = getModel().getRoot();
    if (root instanceof BaseTreeNode) {
      more = new ArrayList<Boolean>();
      more.add(false);
      toPlainText(result, 0, (BaseTreeNode) root, more);
    }
    else {
      result.append("Cannot generate tree!");
    }

    return result.toString();
  }

  /**
   * Adds the node (and its potentional children) to the StringBuilder.
   *
   * @param builder	for adding the tree structure to
   * @param level	the current level (for indentation)
   * @param node	the node to process
   * @param more	for keeping track whether more siblings come after the
   * 			node
   */
  protected void toPlainText(StringBuilder builder, int level, BaseTreeNode node, List<Boolean> more) {
    StringBuilder	indentStr;
    int			i;
    TreeNode		child;
    String[]		lines;

    // generate indentation string
    indentStr = new StringBuilder();
    for (i = 0; i < level; i++) {
      if (more.get(i))
	indentStr.append("| ");
      else
	indentStr.append("  ");
    }

    // add node
    if (level > 0) {
      builder.append(indentStr);
      builder.append("|\n");
    }
    lines = node.toPlainText().split("\n");
    for (i = 0; i < lines.length; i++) {
      builder.append(indentStr);
      if (i == 0)
	builder.append("+ ");
      else
	builder.append("  ");
      builder.append(lines[i] + "\n");
    }

    // add children
    for (i = 0; i < node.getChildCount(); i++) {
      child = node.getChildAt(i);
      more.add(i < node.getChildCount() - 1);
      toPlainText(builder, level + 1, (BaseTreeNode) child, more);
      more.remove(more.size() - 1);
    }
  }
}
