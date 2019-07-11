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
 * StratifiableSplitGenerator.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.splitgenerator;

/**
 * Interface for split generators that can stratify the randomized data.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface StratifiableSplitGenerator<I,O>
  extends SplitGenerator<I,O> {

  /**
   * Sets whether to stratify the data (nominal class).
   *
   * @param value	whether to stratify the data (nominal class)
   */
  public void setStratify(boolean value);

  /**
   * Returns whether to stratify the data (in case of nominal class).
   *
   * @return		true if to stratify
   */
  public boolean getStratify();
}
