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
 * RandomNumberGenerator.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.random;

import adams.core.ShallowCopySupporter;
import adams.core.option.OptionHandler;

/**
 * Interface for random number generators.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 4584 $
 * @param <T> the type of random number to return
 */
public interface RandomNumberGenerator<T extends Number>
  extends OptionHandler, ShallowCopySupporter<RandomNumberGenerator> {

  /**
   * Resets the generator.
   */
  public void reset();
  
  /**
   * Returns the nexct random number.
   *
   * @return		the next number
   */
  public T next();
}
