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
 * AbstractHistory.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */

package adams.core;

import java.util.ArrayList;
import java.util.List;

import adams.core.logging.LoggingObject;

/**
 * Ancestor for classes maintaining a history of objects.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of object to handler
 */
public abstract class AbstractHistory<T>
  extends LoggingObject {

  /** for serialization. */
  private static final long serialVersionUID = 6838638921199269531L;

  /** the maximum entries in the history. */
  public final static int MAX_HISTORY_COUNT = 10;

  /** the maximum length of a caption in the history. */
  public final static int MAX_HISTORY_LENGTH = 200;

  /** the history of objects. */
  protected List<T> m_History;

  /**
   * Initializes the history.
   */
  public AbstractHistory() {
    super();

    initialize();
  }

  /**
   * Initializes members.
   */
  protected void initialize() {
    m_History = new ArrayList();
  }

  /**
   * Creates a copy of the object.
   *
   * @param obj		the object to copy
   */
  protected abstract T copy(T obj);

  /**
   * Clears the history.
   */
  public synchronized void clear() {
    m_History.clear();
  }

  /**
   * Adds the object to the history.
   *
   * @param obj		the object to add
   */
  public synchronized void add(T obj) {
    obj = copy(obj);

    if (m_History.contains(obj))
      m_History.remove(obj);
    m_History.add(0, obj);

    while (m_History.size() > MAX_HISTORY_COUNT)
      m_History.remove(m_History.size() - 1);
  }

  /**
   * Returns the number of entries in the history.
   *
   * @return		the size of the history
   */
  public synchronized int size() {
    return m_History.size();
  }

  /**
   * Returns the current history.
   *
   * @return		the history
   */
  public synchronized List<T> getHistory() {
    return m_History;
  }
}
