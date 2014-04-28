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
 * HistorySelectionEvent.java
 * Copyright (C) 2009-2012 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.event;

import java.util.EventObject;

/**
 * Event that gets sent when a history item gets selected.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class HistorySelectionEvent
  extends EventObject {

  /** for serialization. */
  private static final long serialVersionUID = 45824542929908105L;

  /** the selected favorite. */
  protected Object m_HistoryItem;

  /**
   * Initializes the event.
   *
   * @param source		the object that triggered the event
   * @param historyItem	the selected history item
   */
  public HistorySelectionEvent(Object source, Object historyItem) {
    super(source);

    m_HistoryItem = historyItem;
  }

  /**
   * Returns the selected history item.
   *
   * @return		the history item
   */
  public Object getHistoryItem() {
    return m_HistoryItem;
  }
}