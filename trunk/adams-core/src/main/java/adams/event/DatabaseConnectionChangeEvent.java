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
 * DatabaseConnectionChangeEvent.java
 * Copyright (C) 2008 University of Waikato, Hamilton, New Zealand
 */

package adams.event;

import javax.swing.event.ChangeEvent;

import adams.db.AbstractDatabaseConnection;


/**
 * Whenever a change in the database connection (connect/disconnect) happens,
 * the listeners get notified with this event.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DatabaseConnectionChangeEvent
  extends ChangeEvent {

  /** for serialization. */
  private static final long serialVersionUID = -4819016430256498493L;

  /**
   * Enum for the type of event.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum EventType {
    /** a general notification. */
    GENERAL,
    /** a connect happened. */
    CONNECT,
    /** a disconnect happened. */
    DISCONNECT;
  }

  /** the type of event. */
  protected EventType m_Type;

  /**
   * Initializes the event.
   *
   * @param source	the database connection object
   * @param type	the type of event
   */
  public DatabaseConnectionChangeEvent(Object source, EventType type) {
    super(source);

    m_Type = type;
  }

  /**
   * Returns the database connection object that triggered the event.
   *
   * @return		the responsible object
   */
  public AbstractDatabaseConnection getDatabaseConnection() {
    return (AbstractDatabaseConnection) getSource();
  }

  /**
   * The type of event.
   *
   * @return		the type
   */
  public EventType getType() {
    return m_Type;
  }

  /**
   * Returns the event as string.
   *
   * @return		a string representation of the event
   */
  public String toString() {
    return getClass().getName() + ": type=" + getType() + ", dbc=" + getSource();
  }
}
