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
 * PublishSubscribeHandler.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.core;

import adams.core.logging.LoggingObject;
import adams.event.PublicationEvent;
import adams.event.PublicationListener;

import java.util.HashSet;
import java.util.Set;

/**
 * Manages publishing of data to subscribed listeners.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PublishSubscribeHandler
  extends LoggingObject {

  private static final long serialVersionUID = -681259318445702646L;

  /** the subscribers. */
  public Set<PublicationListener> m_Subscribers;

  /**
   * Initializes the handler.
   */
  public PublishSubscribeHandler() {
    super();
    initialize();
  }

  /**
   * Initializes the members.
   */
  protected void initialize() {
    m_Subscribers = new HashSet<>();
  }

  /**
   * Adds the subscriber.
   *
   * @param s		the subscriber to add
   */
  public synchronized void addSubscriber(PublicationListener s) {
    m_Subscribers.add(s);
  }

  /**
   * Removes the subscriber.
   *
   * @param s		the subscriber to remove
   */
  public synchronized void removeSubscriber(PublicationListener s) {
    m_Subscribers.add(s);
  }

  /**
   * Removes all subscribers.
   */
  public synchronized void clear() {
    m_Subscribers.clear();
  }

  /**
   * Publishes the data.
   *
   * @param source	the source that generated the data
   * @param data	the data to publish
   */
  public synchronized void publish(Object source, Object data) {
    PublicationEvent	e;

    e = new PublicationEvent(this, source, data);
    for (PublicationListener s: m_Subscribers)
      s.dataPublished(e);
  }
}
