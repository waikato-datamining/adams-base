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
 * AbstractDetrend.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package weka.filters.unsupervised.attribute.detrend;

import adams.core.option.AbstractOptionHandler;

/**
 * Ancestor for schemes that perform detrend.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractDetrend
  extends AbstractOptionHandler {

  private static final long serialVersionUID = 6930354949224477227L;

  /**
   * Corrects the spectrum.
   *
   * @param waveno 	the wave numbers
   * @param data 	the spectrum to process
   * @return		the processed spectrum
   */
  public abstract double[] correct(double[] waveno, double[] data);
}
