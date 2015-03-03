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
 * DoubleClickEvent.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.event;

import java.util.EventObject;

/**
 * Event that gets sent in case of double-clicks (left mouse-button).
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DoubleClickEvent
  extends EventObject {

  /** for serialization. */
  private static final long serialVersionUID = 6504052809968807029L;

  /**
   * Initializes the object.
   * 
   * @param src		the source that triggered the event
   */
  public DoubleClickEvent(Object src) {
    super(src);
  }
}
