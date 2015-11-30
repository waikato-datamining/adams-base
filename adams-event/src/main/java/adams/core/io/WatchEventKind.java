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
 * WatchEventKind.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.core.io;

import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;

/**
 * The events to watch.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public enum WatchEventKind {
  CREATE(StandardWatchEventKinds.ENTRY_CREATE),
  MODIFY(StandardWatchEventKinds.ENTRY_MODIFY),
  DELETE(StandardWatchEventKinds.ENTRY_DELETE);

  /** the associated kind. */
  private WatchEvent.Kind<Path> m_EventKind;

  /**
   * Initializes the event kind enum.
   *
   * @param eventKind	the event kind
   */
  private WatchEventKind(WatchEvent.Kind<Path> eventKind) {
    m_EventKind = eventKind;
  }

  /**
   * Returns the event kind.
   *
   * @return		the event kind
   */
  public WatchEvent.Kind<Path> getEventKind() {
    return m_EventKind;
  }
}
