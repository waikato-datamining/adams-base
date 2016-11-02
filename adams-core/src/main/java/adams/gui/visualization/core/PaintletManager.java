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
 * PaintletManager.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.core;

import java.util.Iterator;

/**
 * Interface for classes that manage paintlets.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface PaintletManager {

  /**
   * Adds the paintlet to the internal list.
   *
   * @param p		the paintlet to add.
   */
  public void addPaintlet(Paintlet p);

  /**
   * Removes this paintlet from its internal list.
   *
   * @param p		the paintlet to remove
   */
  public void removePaintlet(Paintlet p);

  /**
   * Returns an iterator over all currently stored paintlets.
   *
   * @return		the paintlets
   */
  public Iterator<Paintlet> paintlets();
}
