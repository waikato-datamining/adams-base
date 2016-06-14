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
 * AbstractSearchableContainerManager.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.container;

import adams.gui.event.DataChangeEvent;
import adams.gui.event.DataChangeEvent.Type;
import gnu.trove.list.array.TIntArrayList;

/**
 * Ancestor for container managers that can be searched.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see SearchableContainerManager
 */
public abstract class AbstractSearchableContainerManager<T extends AbstractContainer>
  extends AbstractContainerManager<T> 
  implements SearchableContainerManager<T> {
  
  /** for serialization. */
  private static final long serialVersionUID = -3626257922847866204L;

  /** the current search term. */
  protected String m_SearchString;
  
  /** whether the current search is using regular expressions. */
  protected boolean m_SearchRegexp;

  /** the filtered containers. */
  protected TIntArrayList m_FilteredList;
  
  /**
   * Initializes the manager.
   */
  protected AbstractSearchableContainerManager() {
    super();
    
    m_FilteredList = null;
  }

  /**
   * Returns whether a search filter has been appplied.
   * 
   * @return		true if search filter applied
   */
  @Override
  public boolean isFiltered() {
    return (m_FilteredList != null);
  }
  
  /**
   * Whether to update the search whenever the content changes.
   * 
   * @return		true if to update whenever data changes
   */
  protected boolean updateSearchOnUpdate() {
    return true;
  }
  
  /**
   * Finishes the update.
   *
   * @param notify	whether to notify the listeners about the update
   * @see		#isUpdating()
   */
  @Override
  public void finishUpdate(boolean notify) {
    super.finishUpdate(notify);
    if (updateSearchOnUpdate() && notify)
      updateSearch();
  }

  /**
   * Clears the container list.
   */
  @Override
  public void clear() {
    m_FilteredList = null;
    super.clear();
  }

  /**
   * Adds the given container to the list.
   *
   * @param c		the container to add
   */
  @Override
  public void add(T c) {
    super.add(c);
    if (!m_Updating && updateSearchOnUpdate())
      updateSearch();
  }

  /**
   * Replaces the container at the given position.
   *
   * @param index	the position to replace
   * @param c		the replacement
   * @return		the old container
   */
  @Override
  public T set(int index, T c) {
    T	result;
    
    result = super.set(index, c);
    
    if (!m_Updating && updateSearchOnUpdate())
      updateSearch();
    
    return result;
  }

  /**
   * Removes the container at the specified position.
   *
   * @param index	the index of the container to remove
   * @return		the container that got removed, null if nothing removed, e.g. if no remove allowed
   */
  @Override
  public T remove(int index) {
    int		i;
    T		result;
    
    if (!m_AllowRemoval)
      return null;

    result = super.remove(index);

    if (!m_Updating && updateSearchOnUpdate())
      updateSearch();

    return result;
  }

  /**
   * Triggers the search.
   * 
   * @param search	the search string
   * @param regExp	whether to perform regexp matching
   */
  public void search(String search, boolean regExp) {
    m_SearchString = search;
    m_SearchRegexp = regExp;
    
    updateSearch();
  }
  
  /**
   * Clears any previous search settings.
   */
  public void clearSearch() {
    search(null, false);
  }
  
  /**
   * Returns whether the container matches the current search.
   * 
   * @param cont	the container to check
   * @param search	the search string
   * @param regExp	whether to perform regular expression matching
   */
  protected abstract boolean isMatch(T cont, String search, boolean regExp);
  
  /**
   * Updates the search.
   */
  protected void updateSearch() {
    TIntArrayList	filtered;
    int			i;
    
    if ((m_SearchString == null) || m_SearchString.isEmpty()) {
      m_FilteredList = null;
      notifyDataChangeListeners(new DataChangeEvent(this, Type.SEARCH));
      return;
    }
    
    filtered = new TIntArrayList();
    for (i = 0; i < m_List.size(); i++) {
      if (isMatch(m_List.get(i), m_SearchString, m_SearchRegexp))
	filtered.add(i);
    }

    if ((filtered.size() == m_List.size()) && (m_FilteredList != null)) {
      m_FilteredList = null;
      notifyDataChangeListeners(new DataChangeEvent(this, Type.SEARCH));
      return;
    }
    
    m_FilteredList = filtered;
    notifyDataChangeListeners(new DataChangeEvent(this, Type.SEARCH));
  }

  /**
   * Returns the indices of all filtered containers.
   *
   * @return		all containers
   */
  public int[] getFilteredIndices() {
    if (m_FilteredList != null)
      return m_FilteredList.toArray();
    else
      return new int[0];
  }

  /**
   * Returns whether the container at the specified position is filtered (= visibile).
   *
   * @param index	the container's position
   * @return		true if the container is filtered
   */
  public boolean isFiltered(int index) {
    return (m_FilteredList != null) && (m_FilteredList.contains(index));
  }

  /**
   * Returns the nth filtered container.
   *
   * @param index	the index (relates only to the filtered containers!)
   * @return		the container, null if index out of range
   */
  public T getFiltered(int index) {
    if (m_FilteredList == null)
      return null;
    else
      return m_List.get(m_FilteredList.get(index));
  }

  /**
   * Determines the index of the filtered container.
   *
   * @param c		the container to look for
   * @return		the index of the container or -1 if not found
   */
  public int indexOfFiltered(T c) {
    int		result;
    int		i;

    result = -1;

    if (m_FilteredList != null) {
      for (i = 0; i < m_FilteredList.size(); i++) {
	if (m_List.get(m_FilteredList.get(i)).equals(c)) {
	  result = i;
	  break;
	}
      }
    }

    return result;
  }

  /**
   * Returns the number of filtered containers.
   *
   * @return		the number of filtered containers
   */
  public int countFiltered() {
    if (m_FilteredList == null)
      return 0;
    else
      return m_FilteredList.size();
  }
}
