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
 * TypeColorProvider.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.image;

import javax.swing.event.ChangeListener;
import java.awt.Color;

/**
 * Interface for image overlays that give access to colors per type (aka label).
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface TypeColorProvider
  extends ImageOverlay {

  /**
   * Checks whether a color has been stored for the given object type.
   *
   * @param type	the type to check
   * @return		true if custom color available
   */
  public boolean hasTypeColor(String type);

  /**
   * Returns the color for the object type.
   *
   * @param type	the type to get the color for
   * @return		the color, null if none available
   */
  public Color getTypeColor(String type);

  /**
   * Adds the listener for location updates.
   *
   * @param l		the listener to add
   */
  public void addLocationsUpdatedListeners(ChangeListener l);

  /**
   * Removes the listener for location updates.
   *
   * @param l		the listener to remove
   */
  public void removeLocationsUpdatedListeners(ChangeListener l);
}
