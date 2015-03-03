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
 * ClassNode.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.goe.classtree;

import adams.gui.core.dotnotationtree.DotNotationNode;

/**
 * Specialized tree node.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ClassNode
  extends DotNotationNode {

  /** for serialization. */
  private static final long serialVersionUID = 9062259637831548370L;

  /** whether the class is deprecated. */
  protected Boolean m_Deprecated;
  
  /**
   * Initializes the class node with the specified label (package or class
   * name).
   *
   * @param label	the label for this node
   */
  public ClassNode(String label) {
    super(label);
  }

  /**
   * Returns whether this node represents a leaf node in the sense of the
   * end-point of the dot notation of the full label. Custom trees might
   * have additional information below this actual leaf node.
   *
   * @return		true if leaf
   */
  @Override
  public boolean isItemLeaf() {
    boolean	result;
    int		i;

    result = true;

    for (i = 0; i < getChildCount(); i++) {
      if (getChildAt(i) instanceof ClassNode) {
	result = false;
	break;
      }
    }

    return result;
  }

  /**
   * Checks whether the associated class is deprecated (only for leaves).
   * 
   * @return		true if deprecated
   */
  public synchronized boolean isDeprecated() {
    Class	cls;
    
    if (m_Deprecated == null) {
      if (isItemLeaf()) {
	try {
	  cls          = Class.forName(getItem());
	  m_Deprecated = (cls.getAnnotation(Deprecated.class) != null);
	}
	catch (Exception e) {
	  m_Deprecated = false;
	  // ignored
	}
      }
      else {
	m_Deprecated = false;
      }
    }
    
    return m_Deprecated;
  }
  
  /**
   * Returns a string to be displayed in the tree.
   * 
   * @return		the string for the tree
   */
  @Override
  public String toString() {
    if (isDeprecated())
      return "<html><strike>" + super.toString() + "</strike></html>";
    else
      return super.toString();
  }
}