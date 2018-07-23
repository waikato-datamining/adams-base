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
 * TreeHelper.java
 * Copyright (C) 2014-2017 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.tree;

import adams.core.MessageCollection;
import adams.core.option.ArrayConsumer;
import adams.core.option.NestedFormatHelper.Line;
import adams.flow.core.Actor;
import adams.flow.core.ActorHandler;
import adams.flow.core.ActorPath;
import adams.flow.core.ExternalActorHandler;

import javax.swing.tree.TreePath;
import java.util.ArrayList;
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
  public static Actor pathToActor(TreePath path) {
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
  public static Actor pathToActor(TreePath path, boolean full) {
    Actor	result;
    Node	node;
    
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
  public static Actor[] pathsToActors(TreePath[] paths) {
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
  public static Actor[] pathsToActors(TreePath[] paths, boolean full) {
    Actor[]	result;
    Node[]	nodes;
    int		i;

    if (paths == null)
      return null;
    
    result = new Actor[paths.length];
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
   * Converts the path to nested node format (the last component in a path).
   *
   * @param path	the path to the actor
   * @return		the nested format
   */
  public static List pathToNested(TreePath path) {
    if (path == null)
      return null;

    return getNested(pathToNode(path));
  }

  /**
   * Converts the paths to actors (the last component in a path).
   *
   * @param paths	the paths to the actors
   * @return		the actors
   */
  public static List[] pathsToNested(TreePath[] paths) {
    List[]	result;
    int		i;

    if (paths == null)
      return null;

    result = new List[paths.length];
    for (i = 0; i < paths.length; i++)
      result[i] = getNested(pathToNode(paths[i]));

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
   * Builds the tree from the nested commandlines.
   *
   * @param nested	the nested commandlines
   * @return		the root node, null if failed to build
   */
  public static Node buildTree(List nested) {
    return buildTree(nested, new MessageCollection(), new MessageCollection());
  }

  /**
   * Builds the tree from the nested commandlines.
   *
   * @param nested	the nested commandlines
   * @param warnings	for storing any warnings
   * @param errors	for storing any errors
   * @return		the root node, null if failed to build
   */
  public static Node buildTree(List nested, MessageCollection warnings, MessageCollection errors) {
    return buildTree(null, nested, warnings, errors, new ArrayConsumer());
  }

  /**
   * Builds the tree from the nested commandlines.
   *
   * @param root	the root node to add to
   * @param nested	the nested commandlines
   * @param warnings	for storing any warnings
   * @param errors	for storing any errors
   * @return		the root node, null if failed to build
   */
  protected static Node buildTree(Node root, List nested, MessageCollection warnings, MessageCollection errors, ArrayConsumer consumer) {
    String		cmdline;
    Actor		actor;
    Node 		node;
    int			i;

    if (nested.size() == 0)
      return null;

    i = 0;
    while (i < nested.size()) {
      try {
        cmdline = ((Line) nested.get(i)).getContent();
	actor   = (Actor) consumer.fromString(cmdline);
	if (consumer.hasErrors()) {
	  errors.addAll(consumer.getErrors());
	  return null;
	}
      }
      catch (Exception e) {
	errors.add("Failed to parse actor: " + nested.get(0), e);
	return null;
      }
      node = new Node(null, actor, cmdline);
      if (root != null)
	root.add(node);
      else
        root = node;
      i++;
      if ((i < nested.size()) && (nested.get(i) instanceof List)) {
	buildTree(node, (List) nested.get(i), warnings, errors, consumer);
	i++;
      }
    }

    return root;
  }

  /**
   * Builds the tree with the given root.
   *
   * @param root	the root actor, can be null
   * @return		the root node
   */
  public static Node buildTree(Actor root) {
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
  public static Node buildTree(Node parent, Actor actor, boolean append) {
    return buildTree(parent, new Actor[]{actor}, append)[0];
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
  protected static Node[] buildTree(final Node parent, Actor[] actors, boolean append) {
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
      for (Node node : result)
	parent.add(node);
    }

    return result;
  }

  /**
   * Inserts blanks at the start of the string.
   *
   * @param s		the string to process
   * @param numBlanks	the number of blanks to insert
   * @return		the processed string
   */
  protected static String indent(String s, int numBlanks) {
    StringBuilder 	result;
    int			i;

    result = new StringBuilder();
    for (i = 0; i < numBlanks; i++)
      result.append(" ");

    result.append(s);

    return result.toString();
  }

  /**
   * Adds the node and its children to the list of commandlines.
   *
   * @param node      	the node to add
   * @param cmdlines	the command lines to add to
   */
  protected static void getCommandLines(Node node, List<String> cmdlines, boolean noExtActors) {
    int		i;
    boolean	skipChildren;

    cmdlines.add(indent(node.getCommandLine(), node.getLevel()));
    skipChildren = noExtActors && (node.getActor() instanceof ExternalActorHandler);
    if (!skipChildren) {
      for (i = 0; i < node.getChildCount(); i++)
	getCommandLines((Node) node.getChildAt(i), cmdlines, noExtActors);
    }
  }

  /**
   * Returns the nested commandlines. Indentation in blanks represents
   * nesting level.
   *
   * @param root	the root node
   * @return		the tree as nested commandlines
   */
  public static List<String> getCommandLines(Node root) {
    return getCommandLines(root, false);
  }

  /**
   * Returns the nested commandlines. Indentation in blanks represents
   * nesting level.
   *
   * @param root	the root node
   * @param noExtActors	whether to exclude external actors
   * @return		the tree as nested commandlines
   */
  public static List<String> getCommandLines(Node root, boolean noExtActors) {
    List<String>	result;

    result = new ArrayList<>();
    getCommandLines(root, result, noExtActors);

    return result;
  }

  /**
   * Adds the node and its children to the nested format.
   *
   * @param node      	the node to add
   * @param nested	the nested format to add to
   */
  protected static void getNested(Node node, List nested, boolean noExtActors) {
    int		i;
    boolean	skipChildren;
    List	list;

    nested.add(new Line(node.getCommandLine()));
    skipChildren = noExtActors && (node.getActor() instanceof ExternalActorHandler);
    if (!skipChildren && (node.getChildCount() > 0)) {
      list = new ArrayList();
      nested.add(list);
      for (i = 0; i < node.getChildCount(); i++)
	getNested((Node) node.getChildAt(i), list, noExtActors);
    }
  }

  /**
   * Returns the nested format.
   *
   * @param root	the root node
   * @return		the tree as nested format
   */
  public static List getNested(Node root) {
    return getNested(root, false);
  }

  /**
   * Returns the nested format.
   *
   * @param root	the root node
   * @param noExtActors	whether to exclude external actors
   * @return		the tree as nested format
   */
  public static List getNested(Node root, boolean noExtActors) {
    List	result;

    result = new ArrayList<>();
    getNested(root, result, noExtActors);

    return result;
  }
}
