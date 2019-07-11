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
 * AbstractSplitter.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.splitgenerator.generic.splitter;

import adams.core.option.AbstractOptionHandler;
import adams.data.binning.Binnable;
import com.github.fracpete.javautils.struct.Struct2;

import java.util.List;

/**
 * Ancestor for splitter schemes.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractSplitter
  extends AbstractOptionHandler
  implements Splitter {

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
    if (data.size() < getMinimumSize())
      return "Not enough data for splitting: " + data.size() + " < " + getMinimumSize();
    return null;
  }

  /**
   * Returns the minimum number of items required for splitting.
   *
   * @return		the minimum
   */
  protected int getMinimumSize() {
    return 2;
  }

  /**
   * Splits the data into two.
   *
   * @param data	the data to split
   * @param <T>		the payload type
   * @return		the split data
   */
  protected abstract <T> Struct2<List<Binnable<T>>,List<Binnable<T>>> doSplit(List<Binnable<T>> data);

  /**
   * Splits the data into two.
   *
   * @param data	the data to split
   * @param <T>		the payload type
   * @return		the split data
   */
  public <T> Struct2<List<Binnable<T>>,List<Binnable<T>>> split(List<Binnable<T>> data) {
    String    msg;

    msg = check(data);
    if (msg != null)
      throw new IllegalStateException(msg);

    return doSplit(data);
  }
}
