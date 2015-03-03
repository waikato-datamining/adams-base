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
 * GlobalDataContainerFilterChangeEvent.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.data.filter.event;

import javax.swing.event.ChangeEvent;

/**
 * Whenever a change in the global data container filter happens,
 * the listeners get notified with this event.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class GlobalDataContainerFilterChangeEvent
  extends ChangeEvent {

  /** for serialization. */
  private static final long serialVersionUID = -4916095762765587184L;

  /**
   * Initializes the event.
   *
   * @param source	the database connection object
   */
  public GlobalDataContainerFilterChangeEvent(Object source) {
    super(source);
  }

  /**
   * Returns the event as string.
   *
   * @return		a string representation of the event
   */
  public String toString() {
    return getClass().getName();
  }
}
