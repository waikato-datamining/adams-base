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
 * GraphHelper.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.output;

import weka.core.Drawable;

/**
 * Helper class for graphs.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @see Drawable
 */
public class GraphHelper {

  /**
   * Checks whether an actual graph is available.
   *
   * @param item	the item to check
   * @return		true if graph available
   */
  public static boolean hasGraph(Drawable d) {
    try {
      if (d.graph() == null)
        return false;
    }
    catch (Exception e) {
      return false;
    }
    return true;
  }

  /**
   * Simple check whether the drawble generates a dotty tree.
   *
   * @param s		the drawble to check
   * @return		true if dotty tree
   */
  public static boolean isDottyTree(Drawable d) {
    try {
      return (isDottyTree(d.graph()));
    }
    catch (Exception e) {
      return false;
    }
  }

  /**
   * Simple check whether the string represents a dotty tree.
   *
   * @param s		the string to check
   * @return		true if dotty tree
   */
  public static boolean isDottyTree(String s) {
    return (s != null) && !s.isEmpty() && s.contains("digraph");
  }
}
