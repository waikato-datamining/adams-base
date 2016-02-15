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
 * EventFlowHelper.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.goe;

import adams.flow.core.Actor;
import adams.flow.core.ActorHandler;
import adams.flow.core.ActorUtils;
import adams.flow.core.ExternalActorHandler;
import adams.flow.standalone.Events;
import adams.flow.standalone.Standalones;
import adams.gui.flow.tree.Node;

import java.awt.Container;
import java.util.ArrayList;
import java.util.List;

/**
 * A helper class for flow-related queries.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class EventFlowHelper
  extends FlowHelper {

  /**
   * Locates all nodes representing Events actors.
   *
   * @param cont	the container to start the search from
   * @return		the nodes with Events found
   */
  public static List<Node> findEvents(Container cont) {
    Node	current;
    Node	parent;

    current = getEditedNode(cont);
    if (current != null)
      parent = (Node) current.getParent();
    else
      parent = getEditedParent(cont);

    return findEvents(current, parent);
  }

  /**
   * Locates all nodes representing Events actors.
   *
   * @param current	the node to start the search from
   * @param parent	the parent node
   * @return		the nodes with Events found
   */
  public static List<Node> findEvents(Node current, Node parent) {
    return findEvents(current, parent, true);
  }

  /**
   * Locates all nodes representing Events actors.
   *
   * @param current	the node to start the search from
   * @param parent	the parent node
   * @param up		whether to go up in the actor tree
   * @return		the nodes with Events found
   */
  protected static List<Node> findEvents(Node current, Node parent, boolean up) {
    List<Node>		result;
    ActorHandler	handler;
    Actor		actor;
    Actor 		subactor;
    int			i;
    int			n;

    result = new ArrayList<Node>();

    if (parent == null)
      return result;

    while (parent != null) {
      if (parent.getActor() instanceof ActorHandler) {
	handler = (ActorHandler) parent.getActor();

	if (handler.getActorHandlerInfo().canContainStandalones()) {
	  for (i = 0; i < parent.getChildCount(); i++) {
	    current = (Node) parent.getChildAt(i);
	    actor   = current.getActor();

	    if (ActorUtils.isStandalone(actor)) {
	      if (!actor.getSkip() && (actor instanceof Events)) {
		result.add(current);
	      }
	      else if (actor instanceof Standalones) {
		for (n = 0; n < current.getChildCount(); n++) {
		  subactor = ((Node) current.getChildAt(n)).getActor();
		  if (!subactor.getSkip() && (subactor instanceof Events)) {
		    result.add((Node) current.getChildAt(n));
		  }
		}
	      }
	      else if (actor instanceof ExternalActorHandler) {
		// load in external actor
		current.expand();
		for (n = 0; n < current.getChildCount(); n++)
		  result.addAll(findEvents(current, (Node) current.getChildAt(n), false));
	      }
	    }
	    else {
	      // finished inspecting standalone actors
	      break;
	    }
	  }
	}
      }

      if (up)
	parent = (Node) parent.getParent();
      else
	parent = null;
    }

    return result;
  }
}
