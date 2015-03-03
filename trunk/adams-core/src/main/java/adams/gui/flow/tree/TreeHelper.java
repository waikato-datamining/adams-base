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
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.tree;

import javax.swing.tree.TreePath;

import adams.flow.core.AbstractActor;
import adams.flow.core.ActorPath;

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

}
