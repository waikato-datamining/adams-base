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
 * AbstractGlobalDataContainerFilter.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.data.filter;

import java.util.HashSet;
import java.util.Iterator;

import adams.data.container.DataContainer;
import adams.data.filter.event.GlobalDataContainerFilterChangeEvent;
import adams.data.filter.event.GlobalDataContainerFilterChangeListener;

/**
 * Ancestor for global filters that are used to filter data containers coming
 * from the database or from files.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of container to filter
 */
public abstract class AbstractGlobalDataContainerFilter<T extends DataContainer> {

  /** the filter to apply to the data before caching. */
  protected AbstractFilter m_DefaultFilter;

  /** the filter being used in this table object. */
  protected AbstractFilter m_Filter;

  /** the listeners in case of changes to the filter. */
  protected HashSet<GlobalDataContainerFilterChangeListener> m_ChangeListeners;

  /**
   * Default constructor.
   */
  protected AbstractGlobalDataContainerFilter() {
    super();

    m_ChangeListeners = new HashSet<GlobalDataContainerFilterChangeListener>();
  }

  /**
   * Sets the filter to run over the data before it is cached.
   *
   * @param value 	the filter
   */
  public synchronized void setFilter(AbstractFilter value) {
    if ((m_DefaultFilter == null) || (!m_DefaultFilter.equals(value))) {
      m_DefaultFilter = value;
      setupFilter();

      // notify all listeners
      notifyChangeListeners(new GlobalDataContainerFilterChangeEvent(this));
    }
  }

  /**
   * Returns the filter used to pre-process the data before it is cached.
   *
   * @return 		the filter
   */
  public final AbstractFilter getFilter() {
    if (m_DefaultFilter == null)
      return new PassThrough();
    else
      return m_DefaultFilter;
  }

  /**
   * Sets up the filter to use for filtering the containers before they're
   * put in the cache.
   */
  protected synchronized void setupFilter() {
    if (m_DefaultFilter != null) {
      if (!(m_DefaultFilter instanceof PassThrough))
	m_Filter = m_DefaultFilter.shallowCopy(true);
      else
	m_Filter = null;
    }
  }

  /**
   * Filters the data with the currently set filter.
   *
   * @param c		the container to filter
   * @return		the filtered container
   * @see		#m_Filter
   */
  public synchronized T filter(T c) {
    AbstractFilter	filter;

    // filter data
    if (m_Filter != null) {
      synchronized(m_Filter) {
	filter = m_Filter.shallowCopy(true);
      }
      c = (T) filter.filter(c);
      filter.destroy();
    }

    return c;
  }

  /**
   * Adds a listener for connect/disconnect events to the internal list.
   *
   * @param l		the listener to add
   */
  public void addChangeListener(GlobalDataContainerFilterChangeListener l) {
    m_ChangeListeners.add(l);
  }

  /**
   * Removes a listener for connect/disconnect events from the internal list.
   *
   * @param l		the listener to remove
   */
  public void removeChangeListener(GlobalDataContainerFilterChangeListener l) {
    m_ChangeListeners.remove(l);
  }

  /**
   * Notifies all listeners with the given event.
   *
   * @param e		the event to send to the listeners
   */
  protected void notifyChangeListeners(GlobalDataContainerFilterChangeEvent e) {
    Iterator<GlobalDataContainerFilterChangeListener>	iter;

    iter = m_ChangeListeners.iterator();
    while (iter.hasNext())
      iter.next().filterStateChanged(e);
  }
}
