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
 * VisibilityContainerManager.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.container;

import java.util.List;

/**
 * Indicator interface for container managers that manage containers
 * encapsulating visbility information as well.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> tye type of container
 */
public interface VisibilityContainerManager<T extends AbstractContainer> {

  /**
   * Returns (a copy of) all currently stored (visible) containers. Those containers
   * have no manager.
   *
   * @return		all containers
   */
  public List<T> getAllVisible();

  /**
   * Returns the indices of all visible containers.
   *
   * @return		all containers
   */
  public int[] getVisibleIndices();

  /**
   * Returns whether the container at the specified position is visible.
   *
   * @param index	the container's position
   * @return		true if the container is visible
   */
  public boolean isVisible(int index);

  /**
   * Sets the specified container's visibility.
   *
   * @param index	the index of the container
   * @param visible	if true then the container will be made visible
   */
  public void setVisible(int index, boolean visible);

  /**
   * Returns the nth visible container.
   *
   * @param index	the index (relates only to the visible containers!)
   * @return		the container, null if index out of range
   */
  public T getVisible(int index);

  /**
   * Returns the number of visible containers.
   *
   * @return		the number of visible containers
   */
  public int countVisible();
}