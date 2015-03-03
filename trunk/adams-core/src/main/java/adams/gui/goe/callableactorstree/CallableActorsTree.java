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
 * CallableActorsTree.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.goe.callableactorstree;

import adams.flow.standalone.CallableActors;
import adams.gui.goe.actorpathtree.ActorPathNode;
import adams.gui.goe.actorpathtree.ActorPathTree;

/**
 * Displays classes in a tree structure.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CallableActorsTree
  extends ActorPathTree<ActorPathNode> {

  /** for serialization. */
  private static final long serialVersionUID = 1489354474021395304L;

  /**
   * Initializes the tree with no classes.
   */
  public CallableActorsTree() {
    super();
  }

  /**
   * Processes the classname, returns null if not suitable to be added to the
   * node.
   * 
   * @param node	the node currently processed
   * @param classname	the classname to process
   * @return		null if not acceptable, otherwise the classname
   */
  @Override
  protected String checkClassname(ActorPathNode node, String classname) {
    String	result;
    Object	obj;
    
    result = classname;
    try {
      obj = Class.forName(result).newInstance();
      if (obj instanceof CallableActors)
	result = null;
      if (node.getParent() == null)
	result = null;
    }
    catch (Exception e) {
      System.err.println("Failed to check classname:");
      e.printStackTrace();
    }
    
    return result;
  }
}
