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
 * ActorChangeEvent.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.event;

import java.util.EventObject;

import adams.core.Utils;
import adams.gui.flow.tree.Node;
import adams.gui.flow.tree.Tree;

/**
 * Event that gets sent when the tree's actors got modified.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ActorChangeEvent
  extends EventObject {

  /** for serialization. */
  private static final long serialVersionUID = -3412999977330314107L;

  /**
   * The type of event.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum Type {
    /** an actor got modified. */
    MODIFY,
    /** a range of actors got modified. */
    MODIFY_RANGE,
    /** a bulk of actors got modified (no actors attached). */
    MODIFY_BULK,
    /** an actor got deleted. */
    REMOVE,
    /** a range of actors got deleted. */
    REMOVE_RANGE
  }

  /** the nodes that triggered the event. */
  protected Node[] m_Nodes;

  /** the type of event. */
  protected ActorChangeEvent.Type m_Type;

  /**
   * Initializes the event.
   *
   * @param tree	the tree
   * @param node	the node
   * @param type	the type of event
   */
  public ActorChangeEvent(Tree tree, Node node, ActorChangeEvent.Type type) {
    this(tree, new Node[]{node}, type);
  }

  /**
   * Initializes the event.
   *
   * @param tree	the tree
   * @param nodes	the nodes
   * @param type	the type of event
   */
  public ActorChangeEvent(Tree tree, Node[] nodes, ActorChangeEvent.Type type) {
    super(tree);

    m_Nodes = nodes;
    m_Type  = type;
  }

  /**
   * Returns the tree that triggered the event.
   *
   * @return		the tree
   */
  public Tree getTree() {
    return (Tree) getSource();
  }

  /**
   * Returns the node(s) that triggered the event.
   *
   * @return		the node(s)
   */
  public Node[] getNodes() {
    return m_Nodes;
  }

  /**
   * Returns the type of event.
   *
   * @return		the type of event
   */
  public ActorChangeEvent.Type getType() {
    return m_Type;
  }

  /**
   * Returns a string representation of the event.
   *
   * @return		the string representation
   */
  public String toString() {
    return "Tree=" + getTree() + ", Nodes=" + Utils.arrayToString(getNodes()) + ", Type=" + getType();
  }
}