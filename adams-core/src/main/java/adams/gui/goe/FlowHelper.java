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
 * FlowHelper.java
 * Copyright (C) 2011-2017 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.goe;

import adams.flow.core.Actor;
import adams.flow.core.ActorHandler;
import adams.flow.core.ActorUtils;
import adams.flow.core.CallableActorHandler;
import adams.flow.core.ExternalActorHandler;
import adams.flow.standalone.Standalones;
import adams.gui.application.Child;
import adams.gui.application.ChildFrame;
import adams.gui.application.ChildWindow;
import adams.gui.flow.FlowEditorPanel;
import adams.gui.flow.FlowTreeHandler;
import adams.gui.flow.tree.Node;
import adams.gui.flow.tree.Tree;
import nz.ac.waikato.cms.locator.ClassLocator;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * A helper class for flow-related queries.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FlowHelper {

  /**
   * Tries to obtain the current flow editor tree.
   *
   * @param cont	the container to start the search from
   * @return		the tree, null if none found
   */
  public static Tree getTree(Container cont) {
    Tree		result;
    Container		parent;
    Component		comp;
    FlowEditorPanel	editor;

    result = null;

    parent = cont;
    while (parent != null) {
      comp = null;
      if (parent instanceof Child) {
	if (parent instanceof ChildFrame)
	  comp = ((ChildFrame) parent).getContentPane().getComponent(0);
	else
	  comp = ((ChildWindow) parent).getContentPane().getComponent(0);
      }
      else if (parent instanceof FlowEditorPanel) {
	comp = parent;
      }
      else if (parent instanceof FlowTreeHandler) {
	comp = parent;
      }
      
      if (comp != null) {
	if (comp instanceof FlowEditorPanel) {
	  editor = (FlowEditorPanel) comp;
	  if (editor.hasCurrentPanel())
	    result = editor.getCurrentPanel().getTree();
	  break;
	}
        else if (comp instanceof FlowTreeHandler) {
          result = ((FlowTreeHandler) comp).getTree();
          break;
        }
      }

      parent = parent.getParent();
    }

    return result;
  }

  /**
   * Checks whether a flow is currently being edited (in the flow editor).
   *
   * @param cont	the container to start the search from
   * @return		true if a flow is being edited
   */
  public static boolean isFlowEdited(Container cont) {
    return (getTree(cont) != null);
  }

  /**
   * Returns the parent of the node that is currently being edited or the
   * node below a node is added.
   *
   * @param cont	the container to start the search from
   * @return		the parent node, null if none being edited
   */
  public static Node getEditedParent(Container cont) {
    Node	result;
    Tree	tree;

    result = null;

    tree = getTree(cont);
    if (tree != null)
      result = tree.getCurrentEditingParent();

    return result;
  }

  /**
   * Returns the node that is currently being edited.
   *
   * @param cont	the container to start the search from
   * @return		the node, null if none being edited
   */
  public static Node getEditedNode(Container cont) {
    Node	result;
    Tree	tree;

    result = null;

    tree = getTree(cont);
    if (tree != null)
      result = tree.getCurrentEditingNode();

    return result;
  }

  /**
   * Returns the database connection that needs to be used in the GOE.
   *
   * @param cont	the container to start the search from
   * @param actorCls	the database connection actor class to look for
   * @param defDbCon	the default database connection if none found in tree
   * @return		the database connection to be used
   */
  public static adams.db.AbstractDatabaseConnection getDatabaseConnection(Container cont, Class actorCls, adams.db.AbstractDatabaseConnection defDbCon) {
    Node	current;
    Node	parent;

    current = getEditedNode(cont);
    if (current != null)
      parent = (Node) current.getParent();
    else
      parent = getEditedParent(cont);

    if (parent == null)
      return null;
    
    return getDatabaseConnection(parent, actorCls, defDbCon);
  }

  /**
   * Returns the database connection that needs to be used in the GOE.
   *
   * @param parent	the node to start the search from
   * @param actorCls	the database connection actor class to look for
   * @param defDbCon	the default database connection if none found in tree
   * @return		the database connection to be used
   */
  public static adams.db.AbstractDatabaseConnection getDatabaseConnection(Node parent, Class actorCls, adams.db.AbstractDatabaseConnection defDbCon) {
    return getDatabaseConnection(parent, actorCls, defDbCon, true);
  }

  /**
   * Returns the database connection that needs to be used in the GOE.
   *
   * @param parent	the node to start the search from
   * @param actorCls	the database connection actor class to look for
   * @param defDbCon	the default database connection if none found in tree
   * @param up		whether to go up in the actor tree
   * @return		the database connection to be used
   */
  protected static adams.db.AbstractDatabaseConnection getDatabaseConnection(Node parent, Class actorCls, adams.db.AbstractDatabaseConnection defDbCon, boolean up) {
    adams.db.AbstractDatabaseConnection	result;
    Node				current;
    Actor				actor;
    Actor 				subactor;
    int					i;
    int					n;
    ActorHandler			handler;

    result = null;

    while ((parent != null) && (result == null)) {
      if (parent.getActor() instanceof ActorHandler) {
	handler = (ActorHandler) parent.getActor();

	if (handler.getActorHandlerInfo().canContainStandalones()) {
	  for (i = 0; i < parent.getChildCount(); i++) {
	    current = (Node) parent.getChildAt(i);
	    actor   = current.getActor();

	    if (ActorUtils.isStandalone(actor)) {
	      if (!actor.getSkip() && (actor.getClass().isAssignableFrom(actorCls))) {
		result = ((adams.flow.standalone.AbstractDatabaseConnection) actor).getConnection();
		break;
	      }
	      else if (actor instanceof Standalones) {
		for (n = 0; n < current.getChildCount(); n++) {
		  subactor = ((Node) current.getChildAt(n)).getActor();
		  if (!subactor.getSkip() && (subactor.getClass().isAssignableFrom(actorCls))) {
		    result = ((adams.flow.standalone.AbstractDatabaseConnection) subactor).getConnection();
		    break;
		  }
		}
	      }
	      else if (actor instanceof ExternalActorHandler) {
		// load in external actor
		current.expand();
		for (n = 0; n < current.getChildCount(); n++) {
		  result = getDatabaseConnection((Node) current.getChildAt(n), actorCls, defDbCon, false);
		  if (result != null)
		    break;
		}
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

    // none found -> use default
    if (result == null)
      result = defDbCon;

    return result;
  }

  /**
   * Checks whether the actor is listed in the restricted classes (specific
   * class or superclass/interface).
   *
   * @param actor	the class to check
   * @param restrict	the restrictions if any
   * @return		true if restricted
   */
  protected static boolean isRestricted(Class actor, HashSet<Class> restrict) {
    boolean	result;

    result = restrict.contains(actor);

    if (!result) {
      for (Class cls: restrict) {
	result = ClassLocator.isSubclass(cls, actor) || ClassLocator.hasInterface(cls, actor);
	if (result)
	  break;
      }
    }

    return result;
  }

  /**
   * Locates all nodes representing {@link CallableActorHandler} actors.
   *
   * @param cont	the container to start the search from
   * @return		the nodes with {@link CallableActorHandler} found
   */
  public static List<Node> findCallableActorsHandler(Container cont) {
    return findNodes(cont, CallableActorHandler.class);
  }

  /**
   * Locates all nodes representing {@link CallableActorHandler} actors.
   *
   * @param parent	the parent node
   * @return		the nodes with {@link CallableActorHandler} found
   */
  public static List<Node> findCallableActorsHandler(Node parent) {
    return findNodes(parent, CallableActorHandler.class);
  }

  /**
   * Locates all nodes representing {@link CallableActorHandler} actors.
   *
   * @param parent	the parent node
   * @param restrict	the classes to restrict the results to
   * @return		the nodes with {@link CallableActorHandler} found
   */
  public static List<Node> findCallableActorsHandler(Node parent, Class[] restrict) {
    return findNodes(parent, restrict, CallableActorHandler.class);
  }

  /**
   * Locates all top nodes representing CallableActors actors.
   *
   * @param cont	the container to get the root node from
   * @return		the nodes with CallableActors found
   */
  public static List<Node> findTopCallableActors(Container cont) {
    return findTopNodes(cont, CallableActorHandler.class);
  }

  /**
   * Locates all top nodes representing CallableActors actors.
   *
   * @param parent	the parent node
   * @return		the nodes with CallableActors found
   */
  public static List<Node> findTopCallableActors(Node parent) {
    return findTopNodes(parent, CallableActorHandler.class);
  }

  /**
   * Locates all nodes representing the specified type of actors.
   *
   * @param cont	the container to start the search from
   * @param type	the type of actors to find
   * @return		the nodes found
   */
  public static List<Node> findNodes(Container cont, Class type) {
    Node	current;
    Node	parent;

    current = getEditedNode(cont);
    if (current != null)
      parent = (Node) current.getParent();
    else
      parent = getEditedParent(cont);

    return findNodes(parent, type);
  }

  /**
   * Locates all nodes representing the specified type of actors.
   *
   * @param parent	the parent node
   * @param type	the type of actors to find
   * @return		the nodes found
   */
  public static List<Node> findNodes(Node parent, Class type) {
    return findNodes(parent, true, null, type);
  }

  /**
   * Locates all nodes representing the specified type of actors.
   *
   * @param parent	the parent node
   * @param restrict	the classes to restrict the results to
   * @return		the nodes found
   */
  public static List<Node> findNodes(Node parent, Class[] restrict, Class type) {
    return findNodes(parent, true, new HashSet<>(Arrays.asList(restrict)), type);
  }

  /**
   * Locates all nodes representing the specified type of actors.
   *
   * @param parent	the parent node
   * @param up		whether to go up in the actor tree
   * @param restrict	the classes to restrict the results to, null if no restrictions
   * @return		the nodes found
   */
  protected static List<Node> findNodes(Node parent, boolean up, HashSet<Class> restrict, Class type) {
    List<Node>		result;
    ActorHandler	handler;
    Actor		actor;
    Actor		subactor;
    int			i;
    int			n;
    Node		current;

    result = new ArrayList<>();

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
	      if (!actor.getSkip() && ClassLocator.matches(type, actor.getClass())) {
		if ((restrict == null) || isRestricted(actor.getClass(), restrict))
		  result.add(current);
	      }
	      else if (actor instanceof Standalones) {
		for (n = 0; n < current.getChildCount(); n++) {
		  subactor = ((Node) current.getChildAt(n)).getActor();
		  if (!subactor.getSkip() && ClassLocator.matches(type, subactor.getClass())) {
		    if ((restrict == null) || isRestricted(subactor.getClass(), restrict))
		      result.add((Node) current.getChildAt(n));
		  }
		}
	      }
	      else if (actor instanceof ExternalActorHandler) {
		// load in external actor
		current.expand();
		for (n = 0; n < current.getChildCount(); n++)
		  result.addAll(findNodes((Node) current.getChildAt(n), false, restrict, type));
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

  /**
   * Locates all top nodes representing CallableActors actors.
   *
   * @param cont	the container to get the root node from
   * @param type	the type of actor to locate
   * @return		the nodes with CallableActors found
   */
  public static List<Node> findTopNodes(Container cont, Class type) {
    Node	current;
    Node	parent;

    current = getEditedNode(cont);
    if (current != null)
      parent = (Node) current.getParent();
    else
      parent = getEditedParent(cont);

    // find root
    if (parent != null)
      parent = (Node) parent.getRoot();

    return findTopNodes(parent, type);
  }

  /**
   * Locates all top nodes representing the specified type.
   *
   * @param parent	the parent node
   * @param type	the type of actor to locate
   * @return		the nodes with actors found
   */
  public static List<Node> findTopNodes(Node parent, Class type) {
    List<Node>		result;
    ActorHandler	handler;
    Actor		actor;
    Actor		subactor;
    int			i;
    int			n;
    Node		current;

    result = new ArrayList<>();

    if (parent == null)
      return result;

    if (parent.getActor() instanceof ActorHandler) {
      handler = (ActorHandler) parent.getActor();

      if (handler.getActorHandlerInfo().canContainStandalones()) {
	for (i = 0; i < parent.getChildCount(); i++) {
	  current = (Node) parent.getChildAt(i);
	  actor   = current.getActor();

	  if (ActorUtils.isStandalone(actor)) {
	    if (!actor.getSkip() && ClassLocator.matches(type, actor.getClass())) {
	      result.add(current);
	    }
	    else if (actor instanceof Standalones) {
	      for (n = 0; n < current.getChildCount(); n++) {
		subactor = ((Node) current.getChildAt(n)).getActor();
		if (!subactor.getSkip() && ClassLocator.matches(type, subactor.getClass())) {
		  result.add((Node) current.getChildAt(n));
		}
	      }
	    }
	    else if (actor instanceof ExternalActorHandler) {
	      // load in external actor
	      current.expand();
	      for (n = 0; n < current.getChildCount(); n++)
		result.addAll(findTopNodes((Node) current.getChildAt(n), type));
	    }
	  }
	  else {
	    // finished inspecting standalone actors
	    break;
	  }
	}
      }
    }

    return result;
  }
}
