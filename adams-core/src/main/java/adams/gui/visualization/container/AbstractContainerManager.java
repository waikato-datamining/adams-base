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
 * ContainerManager.java
 * Copyright (C) 2009-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.container;

import adams.gui.event.DataChangeEvent;
import adams.gui.event.DataChangeEvent.Type;
import adams.gui.event.DataChangeListener;
import gnu.trove.list.array.TIntArrayList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * A handler for containers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of container to use
 */
public abstract class AbstractContainerManager<T extends AbstractContainer>
  implements Serializable {

  /** for serialization. */
  private static final long serialVersionUID = -2758522365930747099L;

  /** the containers. */
  protected List<T> m_List;

  /** the containers while updating. */
  protected List<T> m_UpdateList;

  /** the listeners for data changes. */
  protected HashSet<DataChangeListener> m_DataChangeListeners;

  /** whether to allow deletes. */
  protected boolean m_AllowRemoval;
  
  /** whether an update is currently in progress and notifications are
   * suppressed. */
  protected boolean m_Updating;

  /**
   * Initializes the manager.
   */
  protected AbstractContainerManager() {
    super();

    m_List                = new ArrayList<>();
    m_UpdateList          = new ArrayList<>();
    m_DataChangeListeners = new HashSet<>();
    m_Updating            = false;
    m_AllowRemoval        = true;
  }

  /**
   * Sets whether containers can be removed.
   * 
   * @param value	true if to allow removal
   */
  public void setAllowRemoval(boolean value) {
    m_AllowRemoval = value;
  }
  
  /**
   * Returns whether containers can be removed.
   * 
   * @return		true if removal allowed
   */
  public boolean getAllowRemoval() {
    return m_AllowRemoval;
  }
  
  /**
   * Initiates the start of a larger update.
   *
   * @see		#isUpdating()
   */
  public void startUpdate() {
    m_UpdateList = new ArrayList<>(m_List);
    m_Updating = true;
  }

  /**
   * Finishes the update.
   *
   * @see		#isUpdating()
   */
  public void finishUpdate() {
    finishUpdate(true);
  }

  /**
   * Finishes the update.
   *
   * @param notify	whether to notify the listeners about the update
   * @see		#isUpdating()
   */
  public void finishUpdate(boolean notify) {
    m_Updating = false;

    m_List = new ArrayList<>(m_UpdateList);
    m_UpdateList.clear();

    if (notify)
      notifyDataChangeListeners(new DataChangeEvent(this, Type.BULK_UPDATE));
  }

  /**
   * Returns whether an update is currently in progress.
   *
   * @return		true if an update is currently happening
   */
  public boolean isUpdating() {
    return m_Updating;
  }

  /**
   * Clears the container list.
   */
  public void clear() {
    if (m_Updating)
      return;

    m_List.clear();

    notifyDataChangeListeners(new DataChangeEvent(this, Type.CLEAR));
  }

  /**
   * Returns a new container containing the given payload.
   *
   * @param o		the payload to encapsulate
   * @return		the new container
   */
  public abstract T newContainer(Comparable o);

  /**
   * Checks whether the container is already in the list. Filling in the
   * preAdd-hook, one can avoid clashes.
   *
   * @param o		the container to look for
   * @return		true if the container is already stored
   */
  public boolean contains(T o) {
    boolean	result;
    int		i;

    result = false;

    for (i = 0; i < m_List.size(); i++) {
      if (m_List.get(i).equals(o)) {
        result = true;
        break;
      }
    }

    return result;
  }

  /**
   * A pre-hook for the add method, before a container gets added to the
   * internal list.
   *
   * @param  c	the container to process
   * @return		the processed container
   */
  protected T preAdd(T c) {
    return c;
  }

  /**
   * Adds the given container to the list.
   *
   * @param c		the container to add
   */
  public void add(T c) {
    int		index;

    c.setManager(this);
    c = preAdd(c);
    if (m_Updating) {
      m_UpdateList.add(c);
      index = -1;
    }
    else {
      m_List.add(c);
      index = indexOf(c);
    }

    if (index > -1)
      notifyDataChangeListeners(new DataChangeEvent(this, Type.ADDITION, index));

    postAdd(c);
  }

  /**
   * A post-hook for the add-method, after the container got added to the internal
   * list and the notifications got sent.
   * <br><br>
   * Default implementation does nothing.
   *
   * @param c		the container that got added
   */
  public void postAdd(T c) {
  }

  /**
   * Adds all containers from the given collection.
   *
   * @param c		the collection to add
   */
  public void addAll(Collection<T> c) {
    Iterator<T>		iter;
    TIntArrayList 	indices;
    int			index;

    // add containers
    startUpdate();
    iter = c.iterator();
    while (iter.hasNext())
      add(iter.next());
    finishUpdate(false);

    // determine indices of containers
    indices = new TIntArrayList();
    iter    = c.iterator();
    while (iter.hasNext()) {
      index = indexOf(iter.next());
      if (index > -1)
	indices.add(index);
    }

    if (indices.size() > 0)
      notifyDataChangeListeners(new DataChangeEvent(this, Type.ADDITION, indices.toArray()));
  }

  /**
   * Returns the container at the specified location.
   *
   * @param index	the index of the container
   * @return		the container
   */
  public T get(int index) {
    return m_List.get(index);
  }

  /**
   * Returns (a copy of) all currently stored containers. Those containers
   * have no manager.
   *
   * @return		all containers
   */
  public List<T> getAll() {
    List<T>	result;
    T		cont;
    int		i;

    result = new ArrayList<>();

    for (i = 0; i < count(); i++) {
      cont = (T) get(i).copy();
      cont.setManager(null);
      result.add(cont);
    }

    return result;
  }

  /**
   * A pre-hook for the set method, before the container replaces the item
   * currently occupying the position.
   *
   * @param index	the position to place the container
   * @param c		the container to set
   * @return		the processed container
   */
  protected T preSet(int index, T c) {
    return c;
  }

  /**
   * Replaces the container at the given position.
   *
   * @param index	the position to replace
   * @param c		the replacement
   * @return		the old container
   */
  public T set(int index, T c) {
    T		result;
    boolean	localUpdating;

    localUpdating = !m_Updating;
    if (localUpdating)
      m_Updating = true;

    if (localUpdating)
      result = m_List.set(index, preSet(index, c));
    else
      result = m_UpdateList.set(index, preSet(index, c));

    if (localUpdating)
      m_Updating = false;

    notifyDataChangeListeners(new DataChangeEvent(this, Type.REPLACEMENT, index, result));

    postSet(index, c, result);
    
    return result;
  }

  /**
   * A post-hook for the set method, after the container replaced the item
   * previously occupying the position.
   * <br><br>
   * Default implementation does nothing.
   *
   * @param index	the position to place the container
   * @param c		the container that was set set
   * @param old		the previous container
   */
  protected void postSet(int index, T c, T old) {
  }

  /**
   * Removes the container at the specified position.
   *
   * @param index	the index of the container to remove
   * @return		the container that got removed, null if nothing removed,
   * 			e.g. if no remove allowed or currently updating
   */
  public T remove(int index) {
    T		result;

    if (!m_AllowRemoval)
      return null;

    if (isUpdating())
      result = m_UpdateList.remove(index);
    else
      result = m_List.remove(index);

    notifyDataChangeListeners(new DataChangeEvent(this, Type.REMOVAL, index, result));

    return result;
  }

  /**
   * Returns the number of containers currently stored.
   *
   * @return		the number of containers
   */
  public int count() {
    return m_List.size();
  }

  /**
   * Determines the index of the container.
   *
   * @param c		the container to look for
   * @return		the index of the container or -1 if not found
   */
  public int indexOf(T c) {
    return m_List.indexOf(c);
  }

  /**
   * Adds the listener to the internal list.
   *
   * @param l		the listener to add
   */
  public void addDataChangeListener(DataChangeListener l) {
    m_DataChangeListeners.add(l);
  }

  /**
   * Removes the listener from the internal list.
   *
   * @param l		the listener to remove
   */
  public void removeDataChangeListener(DataChangeListener l) {
    m_DataChangeListeners.remove(l);
  }

  /**
   * Sends all listeners the specified event. Ignored if an update is
   * currently in progress.
   *
   * @param e		the event to send
   * @see		#isUpdating()
   */
  public void notifyDataChangeListeners(DataChangeEvent e) {
    Iterator<DataChangeListener>	iter;

    if (isUpdating())
      return;

    iter = m_DataChangeListeners.iterator();
    while (iter.hasNext())
      iter.next().dataChanged(e);
  }

  /**
   * Returns a string representation of the handler, i.e., all currently
   * stored containers.
   *
   * @return		 a string representation
   */
  @Override
  public String toString() {
    StringBuilder	result;
    int		i;

    result = new StringBuilder();

    result.append("[");
    for (i = 0; i < count(); i++)
      result.append(get(i).toString());
    result.append("]");

    return result.toString();
  }
}