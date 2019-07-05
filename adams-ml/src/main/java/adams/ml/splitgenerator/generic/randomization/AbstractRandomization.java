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
 * AbstractRandomization.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.ml.splitgenerator.generic.randomization;

import adams.core.option.AbstractOptionHandler;
import adams.data.binning.Binnable;

import java.util.List;

/**
 * Ancestor for randomization schemes.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractRandomization
  extends AbstractOptionHandler
  implements Randomization {

  private static final long serialVersionUID = -5619683133352175979L;

  /**
   * Resets the scheme.
   */
  public void reset() {
    super.reset();
  }

  /**
   * Check method before performing randomization.
   *
   * @param data	the data to check
   * @return		null if checks passed, otherwise error message
   */
  protected <T> String check(List<Binnable<T>> data) {
    if ((data == null) || (data.size() == 0))
      return "No data provided!";
    return null;
  }

  /**
   * Randomizes the data.
   *
   * @param data	the data to randomize
   * @param <T>		the payload type
   * @return		the randomized data
   */
  protected abstract <T> List<Binnable<T>> doRandomize(List<Binnable<T>> data);

  /**
   * Randomizes the data.
   *
   * @param data	the data to randomize
   * @param <T>		the payload type
   * @return		the randomized data
   * @throws IllegalStateException  if checks fail
   */
  public <T> List<Binnable<T>> randomize(List<Binnable<T>> data) {
    String    msg;

    msg = check(data);
    if (msg != null)
      throw new IllegalStateException(msg);

    return doRandomize(data);
  }
}
