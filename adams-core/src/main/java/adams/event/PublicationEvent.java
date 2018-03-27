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
 * PublicationEvent.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.event;

import adams.core.PublishSubscribeHandler;

import java.util.EventObject;

/**
 * Event when data gets published through {@link PublishSubscribeHandler}.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PublicationEvent
  extends EventObject {

  private static final long serialVersionUID = 1087485560915594611L;

  /** the data source. */
  protected Object m_DataSource;

  /** the published data. */
  protected Object m_PublishedData;

  /**
   * Initializes the event.
   *
   * @param handler	the handler of the subscriptions
   * @param dataSource	the source of the data
   * @param publishedData	the published data
   */
  public PublicationEvent(PublishSubscribeHandler handler, Object dataSource, Object publishedData) {
    super(handler);
    m_DataSource    = dataSource;
    m_PublishedData = publishedData;
  }

  /**
   * Returns the handler.
   *
   * @return		the handler
   */
  public PublishSubscribeHandler getHandler() {
    return (PublishSubscribeHandler) getSource();
  }

  /**
   * Returns the object that generated the data.
   *
   * @return		the data source
   */
  public Object getDataSource() {
    return m_DataSource;
  }

  /**
   * Returns the published the data.
   *
   * @return		the data
   */
  public Object getPublishedData() {
    return m_PublishedData;
  }
}
