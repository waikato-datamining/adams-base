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
 * PassThrough.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.splitgenerator.generic.randomization;

import adams.data.binning.Binnable;

import java.util.List;

/**
 * Performs no randomization.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PassThrough
  extends AbstractRandomization {

  private static final long serialVersionUID = 4334977393029180519L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Performs no randomization.";
  }

  /**
   * Does not randomize the data.
   *
   * @param data	the input data
   * @param <T>		the payload type
   * @return		the input data
   */
  @Override
  protected <T> List<Binnable<T>> doRandomize(List<Binnable<T>> data) {
    return data;
  }
}
