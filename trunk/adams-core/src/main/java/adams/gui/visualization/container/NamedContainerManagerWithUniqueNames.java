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
 * NamedContainerManagerWithUniqueNames.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.container;

import java.util.HashSet;

/**
 * Interface for container managers that ensure that the names of the containers
 * are unique.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface NamedContainerManagerWithUniqueNames<C extends AbstractContainer>
  extends NamedContainerManager {

  /**
   * Creates a unique name from of the given one, if necessary, testing against
   * the specified names.
   *
   * @param names		the names to test uniqueness against
   * @param name		the name to make unique
   * @return		the unique name
   */
  public String getUniqueName(HashSet<String> names, String name);

  /**
   * Updates the name of the container, i.e., gives it a unique name.
   *
   * @param c		the container to process
   * @param old	the old container this one is replacing, can be null
   * @return		the updated container (for convenience)
   */
  public C updateName(C c, C old);
}
