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
 * SplitPair.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.ml.splitgenerator.generic.randomsplit;

import adams.ml.splitgenerator.generic.core.Subset;

import java.io.Serializable;

/**
 * Combines train and test data.
 *
 * @param <T> the type of wrapped data
 */
public class SplitPair<T>
  implements Serializable {

  private static final long serialVersionUID = -7911202345550167880L;

  /** the training data. */
  protected Subset<T> m_Train;

  /** the test data. */
  protected Subset<T> m_Test;

  /**
   * Initializes the split pair.
   *
   * @param train	the training data
   * @param test	the test data
   */
  public SplitPair(Subset<T> train, Subset<T> test) {
    m_Train = train;
    m_Test  = test;
  }

  /**
   * Returns the training data.
   *
   * @return		the data
   */
  public Subset<T> getTrain() {
    return m_Train;
  }

  /**
   * Returns the test data.
   *
   * @return		the data
   */
  public Subset<T> getTest() {
    return m_Test;
  }

  /**
   * Returns a short string representation.
   *
   * @return		the representation
   */
  @Override
  public String toString() {
    return "train=" + getTrain().getData().size() + ", test=" + getTest().getData().size();
  }
}
