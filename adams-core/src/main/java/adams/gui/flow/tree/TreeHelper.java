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
 * TreeHelper.java
 * Copyright (C) 2014-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.tree;

import adams.core.option.OptionUtils;
import adams.flow.core.AbstractActor;
import adams.flow.core.ActorHandler;
import adams.flow.core.ActorPath;
import adams.gui.core.ConsolePanel;

import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;
import java.util.List;

/**
 * Helper class for flow tree related stuff.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TreeHelper {

  /**
   * Converts the path to a node (the last component in a path).
   *
   * @param path	the path to the actor
   * @return		the node
   */
  public static Node pathToNode(TreePath path) {
    if (path == null)
      return null;

    return (Node) path.getLastPathComponent();
  }

  /**
   * Converts the paths to actors (the last component in a path).
   *
   * @param paths	the paths to the actors
   * @return		the actors
   */
  public static Node[] pathsToNodes(TreePath[] paths) {
    Node[]	result;
    int		i;

    if (paths == null)
      return null;
    
    result = new Node[paths.length];
    for (i = 0; i < paths.length; i++)
      result[i] = (Node) paths[i].getLastPathComponent();

    return result;
  }

  /**
   * Converts the path to an actor (the last component in a path).
   * Does not return the "full" actor.
   *
   * @param path	the path to the actor
   * @return		the actor
   */
  public static AbstractActor pathToActor(TreePath path) {
    return pathToActor(path, false);
  }

  /**
   * Converts the path to an actor (the last component in a path).
   * "Full" actor means including (potential) sub-tree.
   *
   * @param path	the path to the actor
   * @param full	whether to return the full actor
   * @return		the actor
   */
  public static AbstractActor pathToActor(TreePath path, boolean full) {
    AbstractActor	result;
    Node		node;
    
    if (path == null)
      return null;
    
    node = pathToNode(path);
    if (full)
      result = node.getFullActor();
    else
      result = node.getActor();
    
    return result;
  }

  /**
   * Converts the paths to actors (the last component in a path).
   * Does not return the "full" actor.
   *
   * @param paths	the paths to the actors
   * @return		the actors
   */
  public static AbstractActor[] pathsToActors(TreePath[] paths) {
    return pathsToActors(paths, false);
  }

  /**
   * Converts the paths to actors (the last component in a path).
   * "Full" actor means including (potential) sub-tree.
   *
   * @param paths	the paths to the actors
   * @param full	whether to return the full actor
   * @return		the actors
   */
  public static AbstractActor[] pathsToActors(TreePath[] paths, boolean full) {
    AbstractActor[]	result;
    Node[]		nodes;
    int			i;

    if (paths == null)
      return null;
    
    result = new AbstractActor[paths.length];
    nodes  = pathsToNodes(paths);
    for (i = 0; i < nodes.length; i++) {
      if (full)
	result[i] = nodes[i].getFullActor();
      else
	result[i] = nodes[i].getActor();
    }

    return result;
  }

  /**
   * Turns a {@link TreePath} into a {@link ActorPath}.
   *
   * @param path	the path to convert
   * @return		the generated path
   */
  public static ActorPath treePathToActorPath(TreePath path) {
    Object[]	parts;
    String[]	names;
    int		i;

    parts = path.getPath();
    names = new String[parts.length];
    for (i = 0; i < parts.length; i++)
      names[i] = ((Node) parts[i]).getActor().getName();

    return new ActorPath(names);
  }

  /**
   * Builds the tree from the actor commandlines.
   *
   * @param actors	the commandlines with indentation
   * @param index	the index in the list of commandlines to use
   * @param previous	the previous node
   */
  protected static void buildTree(List<String> actors, int index, Node previous) {
    int			level;
    String		cmdline;
    AbstractActor	actor;
    Node		node;
    Node		parent;

    cmdline = actors.get(index);

    // determine level
    level = 0;
    while (level < cmdline.length() && cmdline.charAt(level) == ' ')
      level++;

    try {
      actor = (AbstractActor) OptionUtils.forCommandLine(AbstractActor.class, actors.get(0).trim());
      node  = new Node(previous.getOwner(), actor);
    }
    catch (Exception e) {
      ConsolePanel.getSingleton().append("Failed to parse actor: " + actors.get(0), e);
      return;
    }

    // sibling
    if (level == previous.getLevel()) {
      ((Node) previous.getParent()).add(node);
    }
    // child of some parent node
    else if (level < previous.getLevel()) {
      parent = previous;
      while (level < parent.getLevel())
	parent = (Node) parent.getParent();
      parent.add(node);
    }
    // child
    else {
      previous.add(node);
    }
  }

  /**
   * Builds the tree from the nested commandlines.
   *
   * @param actors	the nested commandlines
   * @return		the root node, null if failed to build
   */
  public static Node buildTree(List<String> actors) {
    AbstractActor	actor;
    Node		root;

    if (actors.size() == 0)
      return null;

    try {
      actor = (AbstractActor) OptionUtils.forCommandLine(AbstractActor.class, actors.get(0).trim());
      root  = new Node(null, actor);
      buildTree(actors, 1, root);
      return root;
    }
    catch (Exception e) {
      ConsolePanel.getSingleton().append("Failed to parse actor: " + actors.get(0), e);
      return null;
    }
  }

  /**
   * Builds the tree with the given root.
   *
   * @param root	the root actor, can be null
   * @return		the root node
   */
  public static Node buildTree(AbstractActor root) {
    return buildTree(null, root, true);
  }

  /**
   * Builds the tree recursively.
   *
   * @param parent	the parent to add the actor to
   * @param actor	the actor to add
   * @param append	whether to append the sub-tree to the parent or just
   * 			return it (recursive calls always append the sub-tree!)
   * @return		the generated node
   */
  public static Node buildTree(Node parent, AbstractActor actor, boolean append) {
    return buildTree(parent, new AbstractActor[]{actor}, append)[0];
  }

  /**
   * Builds the tree recursively.
   *
   * @param parent	the parent to add the actor to
   * @param actors	the actors to add
   * @param append	whether to append the sub-tree to the parent or just
   * 			return it (recursive calls always append the sub-tree!)
   * @return		the generated nodes
   */
  protected static Node[] buildTree(final Node parent, AbstractActor[] actors, boolean append) {
    final Node[]	result;
    int			n;
    int			i;

    result = new Node[actors.length];
    for (n = 0; n < actors.length; n++) {
      result[n] = new Node((parent != null) ? parent.getOwner() : null, actors[n]);

      if (actors[n] instanceof ActorHandler) {
	for (i = 0; i < ((ActorHandler) actors[n]).size(); i++)
	  buildTree(result[n], ((ActorHandler) actors[n]).get(i), true);
      }
    }

    if ((parent != null) && append) {
      SwingUtilities.invokeLater(() -> {
	for (Node node : result)
	  parent.add(node);
      });
    }

    return result;
  }
}
