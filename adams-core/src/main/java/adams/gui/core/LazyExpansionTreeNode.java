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
 * LazyExpansionTreeNode.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

/**
 * Allows for lazy expansion of a node's sub-tree.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class LazyExpansionTreeNode
  extends BaseTreeNode {

  /** for serialization. */
  private static final long serialVersionUID = -3931549349098523711L;
  
  /** whether the tree node has been expanded before. */
  protected boolean m_ExpansionOccurred;
  
  /**
   * Creates a tree node that has no parent and no children, but which
   * allows children.
   */
  public LazyExpansionTreeNode() {
    super();
  }

  /**
   * Creates a tree node with no parent, no children, but which allows
   * children, and initializes it with the specified user object.
   *
   * @param userObject an Object provided by the user that constitutes
   *                   the node's data
   */
  public LazyExpansionTreeNode(Object userObject) {
    super(userObject);
  }

  /**
   * Creates a tree node with no parent, no children, initialized with
   * the specified user object, and that allows children only if
   * specified.
   *
   * @param userObject an Object provided by the user that constitutes
   *        the node's data
   * @param allowsChildren if true, the node is allowed to have child
   *        nodes -- otherwise, it is always a leaf node
   */
  public LazyExpansionTreeNode(Object userObject, boolean allowsChildren) {
    super(userObject, allowsChildren);
  }
  
  /**
   * Sets whether the expansion has already occurred.
   * 
   * @param value	true if the expansion has already occurred.
   */
  public void setExpansionOccurred(boolean value) {
    m_ExpansionOccurred = value;
  }
  
  /**
   * Returns whether the expansion has already occurred.
   * 
   * @return		true if the expansion has already occurred
   */
  public boolean getExpansionOccurred() {
    return m_ExpansionOccurred;
  }
  
  /**
   * Returns whether the node can be expanded at all.
   * <br><br>
   * Default implementation returns false.
   * 
   * @return		true if it can be expanded
   */
  public boolean canExpand() {
    return false;
  }
  
  /**
   * Expands this node.
   * 
   * @return		true if structure below this node was changed
   */
  protected abstract boolean doExpand();
  
  /**
   * Expands the node, if not yet occurred.
   * 
   * @return		true if structure below node was changed
   * @see		#doExpand()
   */
  public synchronized boolean expand() {
    boolean	result;
    
    result = false;
    
    if (!m_ExpansionOccurred) {
      result              = doExpand();
      m_ExpansionOccurred = true;
    }

    return result;
  }

  /**
   * Resets the node.
   */
  protected abstract void doReset();
  
  /**
   * Collapses the node and removes all children, resetting the node.
   * 
   * @return		true if sub-tree was changed
   */
  public synchronized boolean collapse() {
    boolean	result;
    
    result = false;
    
    if (!m_ExpansionOccurred)
      return result;
    
    if (getChildCount() > 0) {
      removeAllChildren();
      result = true;
    }

    doReset();
    
    m_ExpansionOccurred = false;
    
    return result;
  }
}
