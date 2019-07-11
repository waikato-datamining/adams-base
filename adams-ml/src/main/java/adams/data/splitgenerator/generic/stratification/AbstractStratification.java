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
 * AbstractStratification.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.splitgenerator.generic.stratification;

import adams.core.option.AbstractOptionHandler;
import adams.data.binning.Binnable;

import java.util.List;

/**
 * Ancestor for stratification schemes.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractStratification
  extends AbstractOptionHandler
  implements Stratification {

  private static final long serialVersionUID = -5619683133352175979L;

  /**
   * Resets the scheme.
   */
  public void reset() {
    super.reset();
  }

  /**
   * Check method before performing stratification.
   *
   * @param data	the data to check
   * @return		null if checks passed, otherwise error message
   */
  protected <T> String check(List<Binnable<T>> data, int folds) {
    if ((data == null) || (data.size() == 0))
      return "No data provided!";
    if (data.size() < folds)
      return "You cannot have more folds than data: " + data.size() + " < " + folds;
    return null;
  }

  /**
   * Stratifies the data.
   *
   * @param data	the data to stratify
   * @param folds	the number of folds
   * @param <T>		the payload type
   * @return		the stratified data
   */
  protected abstract <T> List<Binnable<T>> doStratify(List<Binnable<T>> data, int folds);

  /**
   * Stratifies the data.
   *
   * @param data	the data to stratify
   * @param folds	the number of folds
   * @return 		the stratified data
   * @param <T>		the payload type
   * @throws IllegalStateException  if checks fail
   */
  public <T> List<Binnable<T>> stratify(List<Binnable<T>> data, int folds) {
    String	msg;

    msg = check(data, folds);
    if (msg != null)
      throw new IllegalStateException(msg);

    return doStratify(data, folds);
  }
}
