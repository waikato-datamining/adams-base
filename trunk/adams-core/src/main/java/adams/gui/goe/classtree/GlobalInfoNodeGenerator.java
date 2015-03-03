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
 * GlobalInfoNodeGenerator.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.goe.classtree;

import adams.gui.core.dotnotationtree.AbstractInfoNodeGenerator;
import adams.gui.core.dotnotationtree.DotNotationNode;
import adams.gui.goe.GlobalInfoCache;

/**
 * Adds a global info node (if global info is available).
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class GlobalInfoNodeGenerator
  extends AbstractInfoNodeGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -8530798109543087462L;

  /**
   * Processes the ClassNode leaf, potentially adding one or more info nodes.
   *
   * @param leaf	the ClassNode to add the info node(s) to
   * @param classname	the full classname for the current path to the root
   * @return		true if at least one info node was added
   */
  public boolean process(DotNotationNode leaf, String classname) {
    boolean 	result;

    result = false;

    if (GlobalInfoCache.getSingleton().has(classname)) {
      leaf.add(new GlobalInfoNode(classname, GlobalInfoCache.getSingleton().get(classname)));
      result = true;
    }

    return result;
  }
}
