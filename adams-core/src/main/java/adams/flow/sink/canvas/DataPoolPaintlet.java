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
 * AbstractDataPoolPaintlet.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink.canvas;

import adams.gui.visualization.core.Paintlet;

/**
 * Interface for paintlets for the Canvas actor.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface DataPoolPaintlet
  extends Paintlet {

  /**
   * Returns the panel the paintlet belongs to.
   * 
   * @return		the panel
   */
  public DataPoolPanel getDataPoolPanel();

  /**
   * Returns the data pool to paint.
   * 
   * @return		the pool
   */
  public DataPool getDataPool();

  /**
   * Returns the classes that the paintlet accepts.
   * 
   * @return		the classes of objects that can be processed
   */
  public abstract Class[] accepts();

  /**
   * Prepares the painting, e.g., setting min/max of axes.
   * 
   * @param panel	the panel to prepare
   */
  public void prepareUpdate();
}
