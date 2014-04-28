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
 * ActorPathNode.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.goe.actorpathtree;

import adams.gui.core.dotnotationtree.DotNotationNode;

/**
 * Specialized tree node for actor paths.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ActorPathNode
  extends DotNotationNode {

  /** for serialization. */
  private static final long serialVersionUID = 7329048118794889190L;

  /** the character for masking escaped dots. */
  public final static String MASK_CHARACTER = "\t";

  /** the classname of the actor. */
  protected String m_Classname;

  /** the classname to use for the icon (if not {@link #m_Classname} available). */
  protected String m_IconClassname;
  
  /**
   * Initializes the class node with the specified label (package or class
   * name).
   *
   * @param label	the label for this node
   */
  public ActorPathNode(String label) {
    super(label.replace(MASK_CHARACTER, "."));
  }

  /**
   * Sets the classname of the actor.
   *
   * @param value	the classname
   */
  public void setClassname(String value) {
    m_Classname = value;
  }

  /**
   * Returns the class name of the actor.
   *
   * @return		the actor's class name
   */
  public String getClassname() {
    return m_Classname;
  }
  
  /**
   * Returns whether a classname is set.
   * 
   * @return		true if a class name is set
   */
  public boolean hasClassname() {
    return (m_Classname != null);
  }

  /**
   * Sets the classname for the icon.
   *
   * @param value	the classname
   */
  public void setIconClassname(String value) {
    m_IconClassname = value;
  }

  /**
   * Returns the classname for the icon.
   *
   * @return		the classname
   */
  public String getIconClassname() {
    return m_IconClassname;
  }
  
  /**
   * Returns whether a classname for the icon is set.
   * 
   * @return		true if a classname is set
   */
  public boolean hasIconClassname() {
    return (m_IconClassname != null);
  }

  /**
   * Returns whether the label matches the specified string.
   *
   * @param s		the string to match against the label
   * @return		true if a match
   */
  @Override
  public boolean isLabelMatch(String s) {
    return getLabel().equals(s.replace(MASK_CHARACTER, "."));
  }

  /**
   * Returns the full label.
   *
   * @return		the full label, null if not a leaf
   */
  @Override
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
          m_Item = node.getLabel().replace(".", "\\.") + "." + m_Item;
        }
      }

      result = m_Item;
    }

    return result;
  }
}