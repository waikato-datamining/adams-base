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
 * HistorySelectionListener.java
 * Copyright (C) 2009-2012 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.event;

/**
 * Interface for classes that listen to selections of history items.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface HistorySelectionListener {

  /**
   * Gets called when a history item gets selected.
   *
   * @param e		the event
   */
  public void historySelected(HistorySelectionEvent e);
}