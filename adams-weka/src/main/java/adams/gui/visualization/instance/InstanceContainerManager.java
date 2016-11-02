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
 * InstanceContainerManager.java
 * Copyright (C) 2009-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.instance;

import adams.data.instance.Instance;
import adams.gui.event.DataChangeEvent;
import adams.gui.event.DataChangeEvent.Type;
import adams.gui.event.DataChangeListener;
import adams.gui.visualization.container.AbstractContainerManager;
import adams.gui.visualization.container.ColorContainerManager;
import adams.gui.visualization.container.ContainerListManager;
import adams.gui.visualization.container.NamedContainerManager;
import adams.gui.visualization.container.VisibilityContainerManager;
import adams.gui.visualization.core.AbstractColorProvider;
import adams.gui.visualization.core.DefaultColorProvider;
import gnu.trove.list.array.TIntArrayList;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * A handler for the Instance containers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class InstanceContainerManager
  extends AbstractContainerManager<InstanceContainer>
  implements VisibilityContainerManager<InstanceContainer>, NamedContainerManager,
             ColorContainerManager {

  /** for serialization. */
  private static final long serialVersionUID = -4325235760470150191L;

  /** the owning panel. */
  protected ContainerListManager<InstanceContainerManager> m_Owner;

  /** the color provider for managing the colors. */
  protected AbstractColorProvider m_ColorProvider;

  /** the current search term. */
  protected String m_SearchString;

  /** whether the current search is using regular expressions. */
  protected boolean m_SearchRegexp;

  /** the filtered containers. */
  protected TIntArrayList m_FilteredList;

  /**
   * Initializes the manager.
   *
   * @param owner	the owning panel
   */
  public InstanceContainerManager(ContainerListManager<InstanceContainerManager> owner) {
    super();

    m_Owner         = owner;
    m_ColorProvider = new DefaultColorProvider();
    m_FilteredList  = null;

    if (owner instanceof DataChangeListener)
      addDataChangeListener((DataChangeListener) owner);
  }

  /**
   * Returns the owning panel.
   *
   * @return		the owner
   */
  public ContainerListManager getOwner() {
    return m_Owner;
  }

  /**
   * Sets the color provider to use.
   *
   * @param value	the color provider
   */
  public synchronized void setColorProvider(AbstractColorProvider value) {
    int		i;
    
    m_ColorProvider = value;
    for (i = 0; i < count(); i++)
      get(i).setColor(getNextColor());
  }

  /**
   * Returns the color provider to use.
   *
   * @return		the color provider in use
   */
  public AbstractColorProvider getColorProvider() {
    return m_ColorProvider;
  }

  /**
   * Returns the next color in line.
   *
   * @return		the next color
   */
  public Color getNextColor() {
    return m_ColorProvider.next();
  }

  /**
   * Clears the container list.
   */
  @Override
  public void clear() {
    m_FilteredList = null;

    super.clear();

    m_ColorProvider.resetColors();
  }

  /**
   * Returns a new container containing the given payload.
   *
   * @param o		the payload to encapsulate
   * @return		the new container
   */
  @Override
  public InstanceContainer newContainer(Comparable o) {
    return new InstanceContainer(this, (Instance) o);
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
   * Adds the given container to the list. Duplicates are ignored.
   *
   * @param c		the container to add
   */
  @Override
  public void add(InstanceContainer c) {
    c.setColor(getNextColor());

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
  public InstanceContainer set(int index, InstanceContainer c) {
    InstanceContainer	result;

    result = super.set(index, c);

    if (!m_Updating && updateSearchOnUpdate())
      updateSearch();

    return result;
  }

  /**
   * Removes the container at the specified position.
   *
   * @param index	the index of the container to remove
   * @return		the container that got removed
   */
  @Override
  public InstanceContainer remove(int index) {
    InstanceContainer		result;

    if (!m_AllowRemoval)
      return null;
    
    result = super.remove(index);

    m_ColorProvider.recycle(result.getColor());

    if (!m_Updating && updateSearchOnUpdate())
      updateSearch();

    return result;
  }

  /**
   * Determines the index of the sequence with the specified ID.
   *
   * @param id	the ID of the sequence
   * @return		the index of the sequence or -1 if not found
   */
  public int indexOf(String id) {
    int	result;
    int	i;

    result = -1;

    for (i = 0; i < count(); i++) {
      if (get(i).getID().equals(id)) {
        result = i;
        break;
      }
    }

    return result;
  }

  /**
   * Returns the indices of all visible containers.
   *
   * @return		all containers
   */
  public int[] getVisibleIndices() {
    TIntArrayList	result;
    int			i;

    result = new TIntArrayList();

    for (i = 0; i < count(); i++) {
      if (!isVisible(i))
        continue;
      result.add(i);
    }

    return result.toArray();
  }

  /**
   * Returns (a copy of) all currently stored containers. Those containers
   * have no manager.
   *
   * @return		all containers
   */
  public List<InstanceContainer> getAllVisible() {
    List<InstanceContainer>	result;
    InstanceContainer		cont;
    int				i;

    result = new ArrayList<InstanceContainer>();

    for (i = 0; i < count(); i++) {
      if (!isVisible(i))
        continue;
      cont = (InstanceContainer) get(i).copy();
      cont.setManager(null);
      result.add(cont);
    }

    return result;
  }

  /**
   * Returns whether the container at the specified position is visible.
   *
   * @param index	the container's position
   * @return		true if the container is visible
   */
  public boolean isVisible(int index) {
    return get(index).isVisible();
  }

  /**
   * Sets the specified container's visibility. Uses the scripting engine
   * if the owner is derived from SpectrumPanel.
   *
   * @param index	the index of the container
   * @param visible	if true then the container will be made visible
   */
  public void setVisible(int index, boolean visible) {
    get(index).setVisible(visible);

    notifyDataChangeListeners(new DataChangeEvent(this, Type.VISIBILITY, index));
  }

  /**
   * Returns the nth visible container.
   *
   * @param index	the index (relates only to the visible containers!)
   * @return		the container, null if index out of range
   */
  public InstanceContainer getVisible(int index) {
    InstanceContainer	result;
    int			i;
    int			count;

    result = null;
    count  = -1;

    for (i = 0; i < count(); i++) {
      if (isVisible(i))
	count++;
      if (count == index) {
	result = get(i);
	break;
      }
    }

    return result;
  }

  /**
   * Returns the number of visible containers.
   *
   * @return		the number of visible containers
   */
  public int countVisible() {
    int	result;
    int	i;

    result = 0;

    for (i = 0; i < count(); i++) {
      if (isVisible(i))
        result++;
    }

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
  protected boolean isMatch(InstanceContainer cont, String search, boolean regExp) {
    if (regExp)
      return cont.getID().matches(search);
    else
      return cont.getID().toLowerCase().contains(search);
  }

  /**
   * Updates the search.
   */
  protected void updateSearch() {
    TIntArrayList	filtered;
    int			i;

    if (m_SearchString == null) {
      m_FilteredList = null;
      notifyDataChangeListeners(new DataChangeEvent(this, Type.SEARCH));
      return;
    }

    filtered = new TIntArrayList();
    for (i = 0; i < m_List.size(); i++) {
      if (isMatch(m_List.get(i), m_SearchString, m_SearchRegexp))
	filtered.add(i);
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
  public InstanceContainer getFiltered(int index) {
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
  public int indexOfFiltered(InstanceContainer c) {
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