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
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.container;

import adams.gui.event.DataChangeEvent;
import adams.gui.event.DataChangeEvent.Type;

import java.util.ArrayList;
import java.util.List;

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
  protected List<T> m_FilteredList;
  
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
   * Checks whether the container is already in the list. Filling in the
   * preAdd-hook, one can avoid clashes.
   *
   * @param o		the container to look for
   * @return		true if the container is already stored
   */
  @Override
  public boolean contains(T o) {
    boolean	result;
    int		i;

    if (!isFiltered())
      return super.contains(o);
    
    result = false;

    for (i = 0; i < m_FilteredList.size(); i++) {
      if (m_FilteredList.get(i).equals(o)) {
        result = true;
        break;
      }
    }

    return result;
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
   * Returns the container at the specified location.
   *
   * @param index	the index of the container
   * @return		the container
   */
  @Override
  public T get(int index) {
    if (isFiltered())
      return m_FilteredList.get(index);
    else
      return m_List.get(index);
  }

  /**
   * Removes the container at the specified position.
   *
   * @param index	the index of the container to remove
   * @return		the container that got removed, null if nothing removed, e.g. if no remove allowed
   */
  @Override
  public T remove(int index) {
    T		result;
    
    if (!m_AllowRemoval)
      return null;

    if (!isFiltered())
      return super.remove(index);
    
    result = m_FilteredList.remove(index);
    if (result != null)
      m_List.remove(result);

    notifyDataChangeListeners(new DataChangeEvent(this, Type.REMOVAL, index, result));

    return result;
  }

  /**
   * Returns the number of containers currently stored.
   *
   * @return		the number of containers
   */
  @Override
  public int count() {
    if (isFiltered())
      return m_FilteredList.size();
    else
      return m_List.size();
  }

  /**
   * Determines the index of the container.
   *
   * @param c		the container to look for
   * @return		the index of the container or -1 if not found
   */
  @Override
  public int indexOf(T c) {
    if (isFiltered())
      return m_FilteredList.indexOf(c);
    else
      return m_List.indexOf(c);
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
    List<T>	filtered;
    
    if (m_SearchString == null) {
      m_FilteredList = null;
      notifyDataChangeListeners(new DataChangeEvent(this, Type.SEARCH));
      return;
    }
    
    filtered = new ArrayList<T>();
    for (T cont: m_List) {
      if (isMatch(cont, m_SearchString, m_SearchRegexp))
	filtered.add(cont);
    }
    
    m_FilteredList = filtered;
    notifyDataChangeListeners(new DataChangeEvent(this, Type.SEARCH));
  }
}
