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
 * NodeDroppedEvent.java
 * Copyright (C) 2010-2011 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.event;

import java.util.EventObject;

import adams.gui.core.BaseTree;
import adams.gui.core.BaseTreeNode;

/**
 * Event that gets sent in case of successful drag'n'drop events.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class NodeDroppedEvent
  extends EventObject {

  /** for serialization. */
  private static final long serialVersionUID = -7524242153886240526L;

  /**
   * For a more fine-grained notification of drop events.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum NotificationTime {
    /** before the drop happens. */
    BEFORE,
    /** after the drop has finished. */
    FINISHED
  }

  /** the nodes that got dropped. */
  protected BaseTreeNode[] m_Nodes;

  /** the time of notification. */
  protected NotificationTime m_NotificationTime;

  /**
   * Initializes the event.
   *
   * @param source	the tree that triggered the event
   * @param node	the "dropped" node
   * @param time	the notification time
   */
  public NodeDroppedEvent(BaseTree source, BaseTreeNode node, NotificationTime time) {
    this(source, new BaseTreeNode[]{node}, time);
  }

  /**
   * Initializes the event.
   *
   * @param source	the tree that triggered the event
   * @param nodes	the "dropped" nodes
   * @param time	the notification time
   */
  public NodeDroppedEvent(BaseTree source, BaseTreeNode[] nodes, NotificationTime time) {
    super(source);

    m_Nodes            = nodes;
    m_NotificationTime = time;
  }

  /**
   * Returns the base tree that triggered the event.
   *
   * @return		the tree
   */
  public BaseTree getTree() {
    return (BaseTree) getSource();
  }

  /**
   * Returns the "dropped" nodes.
   *
   * @return		the nodes
   */
  public BaseTreeNode[] getNodes() {
    return m_Nodes;
  }

  /**
   * Returns the notification time of the event.
   *
   * @return		the time
   */
  public NotificationTime getNotificationTime() {
    return m_NotificationTime;
  }
}