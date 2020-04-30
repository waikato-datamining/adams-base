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
 * InteractionEvent.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.image.interactionlogging;

import adams.core.DateUtils;
import adams.gui.visualization.image.ImagePanel;

import java.util.Date;
import java.util.EventObject;
import java.util.Map;

/**
 * Interaction event.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class InteractionEvent
  extends EventObject {

  private static final long serialVersionUID = 6112718226480864054L;

  /** the timestamp. */
  protected Date m_Timestamp;

  /** the ID. */
  protected String m_ID;

  /** the data. */
  protected Map<String,Object> m_Data;

  /**
   * Constructs the event.
   *
   * @param source 	the ImagePanel that generated the event
   * @param timestamp 	the timestamp of the event
   * @param id 		the ID/type of the event
   * @throws IllegalArgumentException if source is null
   */
  public InteractionEvent(ImagePanel source, Date timestamp, String id) {
    this(source, timestamp, id, null);
  }

  /**
   * Constructs the event.
   *
   * @param source 	the ImagePanel that generated the event
   * @param timestamp 	the timestamp of the event
   * @param id 		the ID/type of the event
   * @param data 	optional data associated with the event, can be null
   * @throws IllegalArgumentException if source is null
   */
  public InteractionEvent(ImagePanel source, Date timestamp, String id, Map<String,Object> data) {
    super(source);
    m_Timestamp = timestamp;
    m_ID        = id;
    m_Data      = data;
  }

  /**
   * Returns the ImagePanel that generated the source.
   *
   * @return		the panel
   */
  public ImagePanel getImagePanel() {
    return (ImagePanel) getSource();
  }

  /**
   * Returns the timestamp of the event.
   *
   * @return		the timestamp
   */
  public Date getTimestamp() {
    return m_Timestamp;
  }

  /**
   * Returns the ID/type of the event.
   *
   * @return		the ID/type
   */
  public String getID() {
    return m_ID;
  }

  /**
   * Returns the associated data.
   *
   * @return		the data, null if none available
   */
  public Map<String, Object> getData() {
    return m_Data;
  }

  /**
   * Short representation of the event.
   *
   * @return		the representation
   */
  @Override
  public String toString() {
    return DateUtils.getTimestampFormatterMsecs().format(m_Timestamp) + ": " + m_ID;
  }
}
