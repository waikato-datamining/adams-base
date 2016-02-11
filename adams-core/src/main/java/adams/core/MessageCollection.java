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
 * MessageCollection.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Container for collecting messages.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MessageCollection
  implements Serializable {

  private static final long serialVersionUID = 9025206792308781497L;

  /** the collected messages. */
  protected List<String> m_Messages;

  /**
   * Initializes the container.
   */
  public MessageCollection() {
    super();
    m_Messages = new ArrayList<>();
  }

  /**
   * Clears all messages.
   */
  public void clear() {
    m_Messages.clear();
  }

  /**
   * Checks whether any messages were collected.
   *
   * @return		true if at least one message collected
   */
  public boolean isEmpty() {
    return m_Messages.isEmpty();
  }

  /**
   * Returns the number of messages collected.
   *
   * @return		the number of messages
   */
  public int size() {
    return m_Messages.size();
  }

  /**
   * Adds the message.
   *
   * @param msg		the message to collect
   */
  public void add(String msg) {
    m_Messages.add(msg);
  }

  /**
   * Adds the message at the specified index.
   *
   * @param index	the index where to insert the message
   * @param msg		the message to collect
   */
  public void add(int index, String msg) {
    m_Messages.add(index, msg);
  }

  /**
   * Returns the message at the specified index.
   *
   * @param index	the index of the message to retrieve
   * @return		the message at the index
   */
  public String get(int index) {
    return m_Messages.get(index);
  }

  /**
   * Deletes the message at the specified index.
   *
   * @param index	the index of the message to delete
   * @return		the removed message
   */
  public String remove(int index) {
    return m_Messages.remove(index);
  }

  /**
   * Returns the messages collated as single string.
   *
   * @return		the collected messages
   */
  public String toString() {
    StringBuilder	result;

    result = new StringBuilder();
    for (String msg: m_Messages) {
      if (result.length() > 0)
	result.append("\n");
      result.append(msg);
    }

    return result.toString();
  }

  /**
   * Returns the collected messages as list.
   *
   * @return		the collected messages
   */
  public List<String> toList() {
    return new ArrayList<>(m_Messages);
  }
}
