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
 * AbstractInfoNodeGenerator.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core.dotnotationtree;

import java.io.Serializable;

/**
 * Ancestor for classes that manipulate the DotNotation tree at the leaves,
 * adding info nodes.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see AbstractInfoNode
 */
public abstract class AbstractInfoNodeGenerator
  implements Serializable, Comparable<AbstractInfoNodeGenerator> {

  /** for serialization. */
  private static final long serialVersionUID = 6568199845831230467L;

  /**
   * Processes the leaf, potentially adding one or more info nodes.
   *
   * @param leaf	the node to add the info node(s) to
   * @param label	the full label for the current path to the root
   * @return		true if at least one info node was added
   */
  public abstract boolean process(DotNotationNode leaf, String label);

  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   *
   * Merely uses the classname for comparison.
   *
   * @param   o the object to be compared.
   * @return  a negative integer, zero, or a positive integer as this object
   *		is less than, equal to, or greater than the specified object.
   */
  public int compareTo(AbstractInfoNodeGenerator o) {
    return getClass().getName().compareTo(o.getClass().getName());
  }

  /**
   * Returns whether the two objects are the same.
   * <br><br>
   * Only compares the classnames of the two objects.
   *
   * @param o	the object to be compared
   * @return	true if the object is the same as this one
   */
  public boolean equals(Object o) {
    if (!(o instanceof AbstractInfoNodeGenerator))
      return false;
    else
      return (compareTo((AbstractInfoNodeGenerator) o) == 0);
  }
}
