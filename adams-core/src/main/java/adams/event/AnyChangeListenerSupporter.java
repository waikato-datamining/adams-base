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
 * AnyChangeListenerSupporter.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.event;

import javax.swing.event.ChangeListener;

/**
 * Interface for classes like text components that can send out notifications
 * for any change that occurs (less fine-grained).
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface AnyChangeListenerSupporter {

  /**
   * Adds the listener for listening to any text changes.
   *
   * @param l		the listener to add
   */
  public void addAnyChangeListener(ChangeListener l);

  /**
   * Removes the listener from listening to any text changes.
   *
   * @param l		the listener to remove
   */
  public void removeAnyChangeListener(ChangeListener l);
}
