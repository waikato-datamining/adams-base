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
 * SearchableContainerManager.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.container;

/**
 * Interface for container managers that can be searched.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> tye type of container
 */
public interface SearchableContainerManager<T extends AbstractContainer> {

  /**
   * Triggers the search.
   * 
   * @param search	the search string
   * @param regExp	whether to perform regexp matching
   */
  public void search(String search, boolean regExp);
  
  /**
   * Clears any previous search settings.
   */
  public void clearSearch();

  /**
   * Returns whether a search filter has been appplied.
   *
   * @return		true if search filter applied
   */
  public boolean isFiltered();

  /**
   * Returns the indices of all filtered containers.
   *
   * @return		all containers
   */
  public int[] getFilteredIndices();

  /**
   * Returns whether the container at the specified position is filtered (= visibile).
   *
   * @param index	the container's position
   * @return		true if the container is filtered
   */
  public boolean isFiltered(int index);

  /**
   * Returns the nth filtered container.
   *
   * @param index	the index (relates only to the filtered containers!)
   * @return		the container, null if index out of range
   */
  public T getFiltered(int index);

  /**
   * Determines the index of the container.
   *
   * @param c		the container to look for
   * @return		the index of the container or -1 if not found
   */
  public int indexOfFiltered(T c);

  /**
   * Returns the number of filtered containers.
   *
   * @return		the number of filtered containers
   */
  public int countFiltered();
}