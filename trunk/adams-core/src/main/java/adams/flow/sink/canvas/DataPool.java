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
 * DataPool.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink.canvas;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents the data to be painted on the canvas.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DataPool
  extends ArrayList {

  /** for serialization. */
  private static final long serialVersionUID = -6250810486879623266L;

  /**
   * Default constructor.
   */
  public DataPool() {
    super();
  }
  
  /**
   * Initializes the pool with an initial capacity.
   * 
   * @param initialCapacity	the initial capacity for the pool
   */
  public DataPool(int initialCapacity) {
    super(initialCapacity);
  }
  
  /**
   * Initializes the pool with the content of the specified pool.
   * 
   * @param pool	the pool to get the data from
   */
  public DataPool(DataPool pool) {
    super(pool);
  }
  
  /**
   * Initializes the pool with the content of the collection.
   * 
   * @param c		the collection to get the data from
   */
  public DataPool(Collection c) {
    super(c);
  }
}
