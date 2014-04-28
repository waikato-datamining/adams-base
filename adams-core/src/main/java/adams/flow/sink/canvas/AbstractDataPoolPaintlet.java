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

import adams.gui.event.PaintEvent.PaintMoment;
import adams.gui.visualization.core.AbstractPaintlet;

/**
 * Ancestor for paintlets for the Canvas actor.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractDataPoolPaintlet
  extends AbstractPaintlet 
  implements DataPoolPaintlet {

  /** for serialization. */
  private static final long serialVersionUID = 3468008070725471553L;

  /**
   * Returns the panel the paintlet belongs to.
   * 
   * @return		the panel
   */
  public DataPoolPanel getDataPoolPanel() {
    return (DataPoolPanel) getPanel();
  }

  /**
   * Returns the data pool to paint.
   * 
   * @return		the pool
   */
  public DataPool getDataPool() {
    return getDataPoolPanel().getPool();
  }
  
  /**
   * Returns when this paintlet is to be executed.
   *
   * @return		when this paintlet is to be executed
   */
  @Override
  public PaintMoment getPaintMoment() {
    return PaintMoment.PAINT;
  }

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
  public abstract void prepareUpdate();
}
