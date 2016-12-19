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
 * AbstractMoment.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.data.image.moments;

import adams.core.option.AbstractOptionHandler;

/**
 * Top level interface for moment classes
 *
 * @author sjb90
 * @version $Revision$
 */
public abstract class AbstractMoment<T>
  extends AbstractOptionHandler {

  private static final long serialVersionUID = 6361679938889787199L;

  /**
   * Takes an image of type T and returns a boolean matrix that can be used for moments
   * @param img the image to convert
   * @return the boolean matrix representing the image
   */
  protected abstract boolean[][] imageToMatrix(T img);

  /**
   * Hook method for performing checks on the image.
   *
   * @param img		the image to check
   * @return		true if successful
   */
  protected abstract boolean check(T img);

  /**
   * Performs the actual calculation.
   *
   * @param img		the image to process
   * @return		the calculated moment
   */
  protected abstract double doCalculate(boolean[][] img);

  /**
   * Calculates the moment.
   *
   * @param img		the image to process
   * @return		the calculated moment
   */
  public double calculate(T img) {
    if (!check(img))
      throw new IllegalStateException("Image is invalid");
    return doCalculate(imageToMatrix(img));
  }
}
